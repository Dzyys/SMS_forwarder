package com.example.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pdus = intent.extras?.get("pdus") as? Array<*> ?: return

        // 读取用户设置的目标号码
        val prefs = context.getSharedPreferences("sms_config", Context.MODE_PRIVATE)
        val forwardNumber = prefs.getString("forward_number", null)

        if (forwardNumber.isNullOrEmpty()) {
            Log.d("SmsReceiver", "未设置转发号码，跳过转发")
            return
        }

        for (pdu in pdus) {
            val msg = SmsMessage.createFromPdu(pdu as ByteArray, "3gpp")
            val content = msg.messageBody
            val smsManager = SmsManager.getDefault()

            try {
                smsManager.sendTextMessage(forwardNumber, null, content, null, null)
                Toast.makeText(context, "短信已转发：$content", Toast.LENGTH_SHORT).show()
                Log.d("SmsReceiver", "短信转发到 $forwardNumber")
            } catch (e: Exception) {
                Toast.makeText(context, "短信转发失败：${e.message}", Toast.LENGTH_LONG).show()
                Log.e("SmsReceiver", "转发失败", e)
            }
        }
    }
}
