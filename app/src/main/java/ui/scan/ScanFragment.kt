package com.hafizhihiman.sipedas.ui.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hafizhihiman.sipedas.data.AppDatabase
import com.hafizhihiman.sipedas.data.DiagnosisRecord
import com.hafizhihiman.sipedas.databinding.FragmentScanBinding
import com.hafizhihiman.sipedas.ml.TensorFlowHelper
import com.hafizhihiman.sipedas.ui.DetailReasoningActivity
import com.hafizhihiman.sipedas.ui.SymptomCheckActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var tensorFlowHelper: TensorFlowHelper
    private var imageCapture: ImageCapture? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var currentBitmap: Bitmap? = null
    private var currentImagePath: String? = null

    // Variabel untuk menyimpan data hasil akhir dari SymptomCheckActivity
    private var lastRecordId: Long? = null
    private var lastDisease = ""
    private var lastCF = 0f
    private var lastUsedRules = ""

    // Launcher izin kamera
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
            else Toast.makeText(requireContext(), "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
        }

    // Launcher ambil foto dari galeri
    private val pickFromGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bmp = uriToBitmap(it)
            bmp?.let { bitmap ->
                currentBitmap = bitmap
                lifecycleScope.launch { processImage(bitmap) }
            }
        }
    }

    // Launcher tanya jawab gejala
    private val symptomCheckLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data ?: return@registerForActivityResult
            val disease = data.getStringExtra("DISEASE") ?: ""
            val confidence = data.getFloatExtra("CONFIDENCE", 0f)
            val finalCF = data.getFloatExtra("FINAL_CF", 0f)
            val recommendation = data.getStringExtra("FINAL_RECOMMENDATION") ?: ""
            val facts = data.getStringExtra("FACTS") ?: ""
            val usedRules = data.getStringExtra("USED_RULES") ?: ""

            // Update UI Card Result
            binding.cardResult.visibility = View.VISIBLE
            binding.tvDisease.text = disease
            binding.tvConfidenceCNN.text = "${"%.1f".format(confidence * 100)}%"
            binding.tvCF.text = "%.2f".format(finalCF)
            binding.tvRecommendation.text = recommendation

            // Tampilkan gambar jika ada
            currentBitmap?.let { 
                binding.imagePreview.setImageBitmap(it)
                binding.imagePreview.visibility = View.VISIBLE
            }

            // Sembunyikan loading
            binding.layoutLoading.visibility = View.GONE

            // Simpan ke database dengan integrasi CBR (Case-Based Reasoning)
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
                val dao = db.diagnosisDao()

                // CBR: 1. Retrieve (Cari riwayat kasus serupa yang berhasil)
                val pastCase = dao.getSimilarHelpfulCase(disease, facts)
                var finalCbrCF = finalCF
                var finalRec = recommendation
                var cbrUsed = false

                if (pastCase != null) {
                    // CBR: 2. Reuse (Gunakan solusi masa lalu untuk memperkuat kasus saat ini)
                    finalCbrCF = (finalCF + 0.05f).coerceAtMost(1f)
                    finalRec = "$recommendation\n\n[CBR - Kasus Serupa Ditemukan]: Kasus ini sangat mirip dengan riwayat diagnosis Anda sebelumnya yang berhasil ditangani. Confidence CF ditingkatkan."
                    cbrUsed = true
                }

                val record = DiagnosisRecord(
                    imagePath = currentImagePath,
                    disease = disease,
                    confidence = confidence,
                    finalCF = finalCbrCF,
                    recommendation = finalRec,
                    additionalFacts = facts,
                    usedRules = if (cbrUsed) "$usedRules;CBR_Boost: +0.05" else usedRules
                )
                
                lastRecordId = dao.insert(record)
                
                lastDisease = disease
                lastCF = finalCbrCF
                lastUsedRules = if (cbrUsed) "$usedRules;CBR_Boost: +0.05" else usedRules

                // Update UI di Main Thread
                withContext(Dispatchers.Main) {
                    binding.tvCF.text = "%.2f".format(finalCbrCF)
                    binding.tvRecommendation.text = finalRec
                }
            }
        } else {
            binding.layoutLoading.visibility = View.GONE
            Toast.makeText(requireContext(), "Cek gejala dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tensorFlowHelper = TensorFlowHelper(requireContext(), "cabe_model.tflite")

        binding.btnTakePhoto.setOnClickListener {
            if (allPermissionsGranted()) {
                takePhoto()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnGallery.setOnClickListener {
            pickFromGallery.launch("image/*")
        }

        // Tombol feedback Rule Learner
        binding.btnHelpful.setOnClickListener { updateFeedback("helpful") }
        binding.btnNotHelpful.setOnClickListener { updateFeedback("not_helpful") }

        binding.btnDetailReasoning.setOnClickListener {
            val intent = Intent(requireContext(), DetailReasoningActivity::class.java).apply {
                putExtra("DISEASE", lastDisease)
                putExtra("FINAL_CF", lastCF)
                putExtra("USED_RULES", lastUsedRules)
            }
            startActivity(intent)
        }

        // Mulai kamera otomatis
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.viewFinder.display?.rotation ?: 0)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal membuka kamera", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(requireContext().externalCacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.layoutLoading.visibility = View.VISIBLE
        binding.cardResult.visibility = View.GONE

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    currentBitmap = bitmap
                    currentImagePath = photoFile.absolutePath
                    lifecycleScope.launch { processImage(bitmap) }
                }
                override fun onError(exception: ImageCaptureException) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.layoutLoading.visibility = View.GONE
                        Toast.makeText(requireContext(), "Gagal mengambil foto", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private suspend fun processImage(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            binding.layoutLoading.visibility = View.VISIBLE
            binding.cardResult.visibility = View.GONE
        }
        
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val result = tensorFlowHelper.classify(resized)

        withContext(Dispatchers.Main) {
            if (result.isEmpty()) {
                binding.layoutLoading.visibility = View.GONE
                Toast.makeText(requireContext(), "Gagal mengklasifikasi gambar", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            val (label, confidence) = result[0]

            // Buka kuesioner gejala
            val intent = Intent(requireContext(), SymptomCheckActivity::class.java).apply {
                putExtra("DISEASE", label)
                putExtra("CONFIDENCE", confidence)
                putExtra("IMAGE_PATH", currentImagePath)
            }
            symptomCheckLauncher.launch(intent)
        }
    }

    private fun updateFeedback(feedback: String) {
        lastRecordId?.let { id ->
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(requireContext())
                val dao = db.diagnosisDao()
                val record = dao.getRecordById(id)
                record?.let {
                    it.userFeedback = feedback
                    dao.update(it)

                    // Rule Learner
                    val prefs = requireContext().getSharedPreferences("RuleLearner", Context.MODE_PRIVATE)
                    val currentBoost = prefs.getFloat(lastDisease, 0f)
                    val newBoost = if (feedback == "helpful") currentBoost + 0.01f else currentBoost - 0.01f
                    prefs.edit().putFloat(lastDisease, newBoost.coerceIn(-0.2f, 0.2f)).apply()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Terima kasih atas masukan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            requireContext().contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) { null }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        tensorFlowHelper.close()
        cameraExecutor.shutdown()
        _binding = null
    }
}
