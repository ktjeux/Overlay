package fr.atelierreferences

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : Activity() {
    private var selectedImage: Uri? = null
    private lateinit var status: TextView
    private lateinit var launchButton: Button

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImage = uri
            status.text = "Image choisie. Tu peux maintenant lancer l'overlay."
            launchButton.isEnabled = true
        }
    }

    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            getSharedPreferences("prefs", MODE_PRIVATE).edit()
                .putString("folder_uri", uri.toString()).apply()
            status.text = "Dossier mémorisé. Cette première version lance une image à la fois : choisis-en une avec le bouton ci-dessous."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(28, 36, 28, 28)
        }
        layout.addView(TextView(this).apply {
            text = "Atelier Références"
            textSize = 28f
        })
        layout.addView(TextView(this).apply {
            text = "Prototype pour afficher une référence au-dessus de Clip Studio Paint."
            textSize = 16f
            setPadding(0, 12, 0, 20)
        })

        fun addButton(label: String, action: () -> Unit): Button {
            val button = Button(this).apply {
                text = label
                setOnClickListener { action() }
            }
            layout.addView(button)
            return button
        }

        addButton("1 — Choisir un dossier (mémorisé)") {
            folderPicker.launch(null)
        }
        addButton("2 — Choisir une image") {
            imagePicker.launch("image/*")
        }
        addButton("Autoriser l'affichage par-dessus les autres applications") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
        launchButton = addButton("3 — Lancer l'overlay") {
            val uri = selectedImage
            if (uri == null) {
                status.text = "Choisis d'abord une image."
            } else if (!Settings.canDrawOverlays(this)) {
                status.text = "Autorise d'abord l'affichage par-dessus les autres applications."
            } else {
                startService(Intent(this, OverlayService::class.java).putExtra("image_uri", uri.toString()))
                status.text = "Overlay lancé. Retourne dans Clip Studio Paint pour tester."
            }
        }
        launchButton.isEnabled = false

        status = TextView(this).apply {
            text = "Commence par choisir une image."
            textSize = 14f
            setPadding(0, 16, 0, 12)
        }
        layout.addView(status)
        layout.addView(TextView(this).apply {
            text = "Limites de ce prototype : une image à la fois, zoom/panoramique et navigation par miniatures à développer. Le mode dessin transmet les gestes vers l'application située dessous."
            textSize = 13f
            setPadding(0, 18, 0, 0)
        })

        setContentView(ScrollView(this).apply { addView(layout) })
    }
}
