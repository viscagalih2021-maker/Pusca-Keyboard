package com.pusca.keyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PuscaKeyboardService.PREFS, MODE_PRIVATE)
        val pad = (24 * resources.displayMetrics.density).toInt()

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad, pad, pad)

        val title = TextView(this)
        title.text = "Pusca Selling Keyboard"
        title.textSize = 22f
        root.addView(title)

        val info = TextView(this)
        info.text = "Keyboard jualan Pusca: template, produk, order, invoice, dan keuangan langsung dari kolom chat WhatsApp, Instagram, atau SMS. Datanya dari aplikasi Pusca yang sama."
        info.textSize = 14f
        info.setPadding(0, pad / 2, 0, pad)
        root.addView(info)

        val label = TextView(this)
        label.text = "Alamat aplikasi keyboard"
        root.addView(label)

        val urlInput = EditText(this)
        urlInput.inputType = InputType.TYPE_TEXT_VARIATION_URI
        urlInput.setText(prefs.getString(PuscaKeyboardService.KEY_URL, PuscaKeyboardService.DEFAULT_URL))
        root.addView(urlInput)

        val save = Button(this)
        save.text = "Simpan alamat"
        save.setOnClickListener {
            prefs.edit()
                .putString(PuscaKeyboardService.KEY_URL, urlInput.text.toString().trim())
                .apply()
        }
        root.addView(save)

        val login = Button(this)
        login.text = "Buka aplikasi untuk login"
        login.setOnClickListener {
            startActivity(Intent(this, WebLoginActivity::class.java))
        }
        root.addView(login)

        val enable = Button(this)
        enable.text = "Aktifkan keyboard Pusca"
        enable.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        root.addView(enable)

        val pick = Button(this)
        pick.text = "Pilih keyboard Pusca"
        pick.setOnClickListener {
            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showInputMethodPicker()
        }
        root.addView(pick)

        val steps = TextView(this)
        steps.text = "Langkah: 1) Login sekali lewat tombol di atas. 2) Aktifkan keyboard Pusca. 3) Pilih keyboard Pusca. 4) Buka WhatsApp, ketuk kolom pesan, keyboard jualan siap dipakai."
        steps.textSize = 13f
        steps.setPadding(0, pad, 0, 0)
        root.addView(steps)

        setContentView(root)
    }
}
