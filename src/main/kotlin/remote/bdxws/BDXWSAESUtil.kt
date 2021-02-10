package com.poicraft.bot.v4.plugin.remote.bdxws

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class BDXWSAESUtil(key: String) {
    private val secretKey: String
    private val iv: String

    init {
        genMD5Key(key).let {
            secretKey = it.substring(0, 16)
            iv = it.substring(16)
        }
    }

    private fun genMD5Key(originalString: String): String {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(originalString.toByteArray())
        val sb = StringBuilder()
        for (b in digest) {
            val i: Int = b.toInt() and 0xff
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                hexString = "0$hexString"
            }
            sb.append(hexString)
        }
        return sb.toString().toUpperCase()
    }

    private fun genCipher(mode: Int): Cipher? {
        val cipher = try {
            Cipher.getInstance("AES/CBC/PKCS5Padding")
        } catch (exp: NoSuchAlgorithmException) {
            null
        } catch (exp: NoSuchPaddingException) {
            null
        }

        try {
            cipher?.init(
                mode, SecretKeySpec(secretKey.toByteArray(), "AES"),
                IvParameterSpec(iv.toByteArray())
            )
        } catch (exp: InvalidAlgorithmParameterException) {
            return null
        } catch (exp: InvalidKeyException) {
            return null
        }

        return cipher
    }

    fun decrypt(originalString: String): String {
        val cipher = genCipher(Cipher.DECRYPT_MODE)
        return String(
            cipher!!.doFinal(
                Base64.getDecoder().decode(originalString)
            )
        )
    }

    fun encrypt(originalString: String): String {
        val cipher = genCipher(Cipher.ENCRYPT_MODE)
        val encrypted = cipher!!.doFinal(originalString.toByteArray(charset("UTF-8")))
        return Base64.getEncoder().encodeToString(encrypted)
    }
}