package com.example.smsforwarder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var phoneInput: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)  // 注意必须先设置视图！

        // 获取控件
        phoneInput = findViewById(R.id.editPhone)
        saveButton = findViewById(R.id.btnSave)

        // 申请权限
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS
        ), 1)

        // 提示设置为默认短信应用
        val myPackageName = packageName
        if (Telephony.Sms.getDefaultSmsPackage(this) != myPackageName) {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName)
            startActivity(intent)
        }

        // 加载并显示保存的号码
        val prefs = getSharedPreferences("sms_config", Context.MODE_PRIVATE)
        phoneInput.setText(prefs.getString("forward_number", ""))

        // 保存逻辑
        saveButton.setOnClickListener {
            val phone = phoneInput.text.toString().trim()
            if (phone.isNotEmpty()) {
                prefs.edit().putString("forward_number", phone).apply()
                Toast.makeText(this, "已保存号码：$phone", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "请输入目标手机号", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
