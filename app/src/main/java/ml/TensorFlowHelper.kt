package com.hafizhihiman.sipedas.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.Closeable
import java.io.IOException

class TensorFlowHelper(
    context: Context,
    private val modelPath: String = "cabe_model.tflite",
    useGpu: Boolean = false
) : Closeable {

    // ===== Properti Publik =====
    val labels = listOf(
        "Antraknosa", "Bercak Daun", "Keriting Kuning", "Layu Fusarium", "Busuk Buah"
    )
    val inputSize = 224   // ukuran gambar yang diharapkan model

    // ===== Komponen Utama =====
    private var interpreter: Interpreter
    private var gpuDelegate: GpuDelegate? = null

    init {
        val model = try {
            FileUtil.loadMappedFile(context, modelPath)
        } catch (e: IOException) {
            throw IllegalStateException("Gagal memuat model dari assets/$modelPath. Pastikan file ada.", e)
        }

        val options = Interpreter.Options().apply {
            setNumThreads(4)                 // CPU thread
            setUseXNNPACK(true)              // akselerasi CPU modern
        }

        // GPU delegate opsional (bisa gagal di beberapa perangkat)
        if (useGpu) {
            try {
                gpuDelegate = GpuDelegate()
                options.addDelegate(gpuDelegate)
            } catch (e: Exception) {
                // GPU tidak didukung, tetap pakai CPU
                gpuDelegate = null
            }
        }

        interpreter = Interpreter(model, options)

        // Pastikan ukuran input sesuai dengan model yang dimuat
        val inputShape = interpreter.getInputTensor(0).shape() // [1, 224, 224, 3]
        if (inputShape.size != 4 || inputShape[1] != inputSize || inputShape[2] != inputSize) {
            throw IllegalArgumentException(
                "Dimensi input model tidak sesuai. Diharapkan [1, $inputSize, $inputSize, 3], " +
                        "ditemukan [${inputShape.joinToString(",")}]."
            )
        }
    }

    /**
     * Klasifikasi gambar daun menjadi 5 kelas penyakit.
     * @return daftar pasangan (nama penyakit, probabilitas), terurut menurun.
     */
    fun classify(bitmap: Bitmap): List<Pair<String, Float>> {
        // 1. Preprocessing (seperti yang dilakukan saat pelatihan)
        val processedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val tensorImage = TensorImage.fromBitmap(processedBitmap)
        // Jika model memerlukan normalisasi [0,1], tambahkan:
        // val processor = ImageProcessor.Builder()
        //     .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
        //     .add(NormalizeOp(0f, 255f))   // contoh: bagi 255
        //     .build()
        // Simpler: di sini kita langsung gunakan TensorImage yang sudah otomatis float.
        // Untuk normalisasi manual bisa pakai TensorImage(DataType.FLOAT32) lalu load bitmap.

        // 2. Siapkan output buffer
        val output = Array(1) { FloatArray(labels.size) }

        // 3. Inferensi
        interpreter.run(tensorImage.buffer, output)

        // 4. Postprocessing
        val probabilities = output[0]
        return labels.zip(probabilities.toList())
            .sortedByDescending { it.second }
    }

    /**
     * Alternatif: klasifikasi dengan threshold confidence minimum.
     * @return pasangan (penyakit, confidence) jika di atas threshold, null jika tidak ada.
     */
    fun classifyWithThreshold(bitmap: Bitmap, threshold: Float = 0.6f): Pair<String, Float>? {
        val results = classify(bitmap)
        return if (results.isNotEmpty() && results[0].second >= threshold) {
            results[0]
        } else null
    }

    /**
     * Bebaskan resource.
     */
    override fun close() {
        interpreter.close()
        gpuDelegate?.close()
    }
}