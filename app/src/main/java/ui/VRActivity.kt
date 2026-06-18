package com.hafizhihiman.sipedas.ui.vr

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.hafizhihiman.sipedas.databinding.ActivityVrBinding
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

class VRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVrBinding
    private lateinit var sceneView: SceneView
    private lateinit var modelLoader: ModelLoader

    // Node model saat ini
    private var currentModelNode: ModelNode? = null

    // Status model yang sedang ditampilkan
    private var isHealthyModel = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SceneView
        sceneView = SceneView(this).apply {
            // Atur latar belakang (warna atau skybox)
            backgroundColor = android.graphics.Color.parseColor("#0A1929")
            // Kamera dapat di-rotate oleh user (sentuh)
            camera.isManipulable = true
            // Posisi awal kamera
            camera.position = Position(0f, 0.5f, 2f)
        }

        // Masukkan SceneView ke container di layout
        binding.vrContainer.addView(sceneView)

        modelLoader = ModelLoader(sceneView)

        // Muat model default (sehat) saat pertama kali
        loadModel("models/plant_healthy.glb")

        // Tombol Sehat
        binding.btnHealthy.setOnClickListener {
            if (!isHealthyModel) {
                loadModel("models/plant_healthy.glb")
                isHealthyModel = true
                binding.cardInfo.visibility = View.GONE
            }
        }

        // Tombol Sakit (contoh: antraknosa)
        binding.btnSick.setOnClickListener {
            if (isHealthyModel) {
                loadModel("models/plant_anthracnose.glb")
                isHealthyModel = false
                // Tampilkan card informasi penyakit
                binding.cardInfo.visibility = View.VISIBLE
                binding.tvVRDisease.text = "Penyakit: Antraknosa"
                binding.tvVRSolution.text = "Solusi: Semprot fungisida berbahan aktif mankozeb"
            }
        }

        // Tombol Reset (kembali ke posisi awal)
        binding.btnReset.setOnClickListener {
            currentModelNode?.let {
                it.transform.rotation = com.google.android.filament.gltf.math.Quaternion.identity()
                it.transform.scale = 1f
            }
            sceneView.camera.position = Position(0f, 0.5f, 2f)
            Toast.makeText(this, "Tampilan direset", Toast.LENGTH_SHORT).show()
        }

        // Tombol back di toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadModel(modelPath: String) {
        // Hapus model sebelumnya jika ada
        currentModelNode?.let { sceneView.removeNode(it) }

        // Muat model baru
        modelLoader.loadModel(modelPath) { model ->
            model?.let {
                currentModelNode = ModelNode(model).apply {
                    // Atur posisi, skala (sesuaikan dengan ukuran model)
                    transform.position = Position(0f, -0.3f, 0f)
                    transform.scale = 0.5f
                }
                sceneView.addNode(currentModelNode!!)
                binding.tvPlaceholder.visibility = View.GONE
            } ?: run {
                binding.tvPlaceholder.visibility = View.VISIBLE
                binding.tvPlaceholder.text = "Model 3D tidak tersedia untuk versi demo."
                Toast.makeText(this, "Gagal memuat model: $modelPath", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sceneView.destroy()
    }
}