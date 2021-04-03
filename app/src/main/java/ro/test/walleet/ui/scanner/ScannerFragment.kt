package ro.test.walleet.ui.scanner

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import ro.test.walleet.common.observe
import ro.test.walleet.databinding.FragmentScannerBinding
import java.lang.Exception
import java.lang.Math.*
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {
    lateinit var binding: FragmentScannerBinding
    val cameraViewModel: CameraXViewModel by viewModels()


    companion object {
        const val CODE_BAR = "CODE-BAR"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScannerBinding.inflate(inflater)
        setupCamera()
        return binding.root
    }

    private fun setupCamera() {
        val lensFacing = CameraSelector.LENS_FACING_BACK
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        observe(cameraViewModel.processCameraProvider) {
            bindUseCases(it, cameraSelector)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun bindUseCases(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector) {
        bindPreviewUseCase(cameraProvider, cameraSelector)
        bindAnalyseUseCase(cameraProvider, cameraSelector)
    }


    private fun bindPreviewUseCase(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector) {


        val metrics = DisplayMetrics().also { binding.previewView.display?.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val previewView = binding.previewView

        val previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView.display.rotation)
            .build()
        previewUseCase.setSurfaceProvider(previewView.surfaceProvider)

        try {
            cameraProvider.bindToLifecycle(/* lifecycleOwner= */this,
                cameraSelector,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e("CameraX", illegalStateException.message ?: "")
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e("CameraX", illegalArgumentException.message ?: "")
        }
    }

    private fun bindAnalyseUseCase(cameraProvider: ProcessCameraProvider, cameraSelector: CameraSelector) {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        // BarcodeScannerOptions.Builder()
        //     .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        //     .build();

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)

        val metrics = DisplayMetrics().also { binding.previewView.display?.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val previewView = binding.previewView

        val analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(previewView.display.rotation)
            .build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase.setAnalyzer(
            cameraExecutor,
            ImageAnalysis.Analyzer { imageProxy ->
                processImageProxy(barcodeScanner, imageProxy)
            }
        )

        try {
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                analysisUseCase
            )
        } catch (illegalStateException: Exception) {
            Log.i(this::class.java.simpleName, illegalStateException.message ?: "")
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                try {
                barcodes.forEach { barcode ->
                    findNavController().previousBackStackEntry?.savedStateHandle?.let{
                        it.set(
                            CODE_BAR,
                            barcode.rawValue
                        )
                   findNavController().navigateUp()
                }
                }
                }catch (exc: Exception) {

                }
            }
            .addOnFailureListener {
                Log.e("DBG CODE", it.message ?: "")
            }.addOnCompleteListener {
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                imageProxy.close()
            }
    }
}