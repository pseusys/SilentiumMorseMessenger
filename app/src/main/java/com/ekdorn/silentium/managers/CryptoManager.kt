@file:Suppress("DEPRECATION")
package com.ekdorn.silentium.managers

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64.*
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher


object CryptoManager {
    private const val CRYPTO_METHOD = "RSA"
    private const val PROVIDER = "AndroidKeyStore"
    private const val CIPHER_TRANSFORM = "RSA/CBC/OAEPWithSHA1AndMGF1Padding"
    private const val ALIAS = "SILENT_KEY"
    private const val CRYPTO_BITS = 2048

    private fun generateKeyPair(context: Context): PublicKey {
        val keyGenerator = KeyPairGenerator.getInstance(CRYPTO_METHOD, PROVIDER).apply {
            val spec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_DECRYPT).setKeySize(CRYPTO_BITS).build()
            else KeyPairGeneratorSpec.Builder(context).setAlias(ALIAS).setKeySize(CRYPTO_BITS).build()
            initialize(spec)
        }
        return keyGenerator.generateKeyPair().public
    }


    private fun getKey(): KeyStore.PrivateKeyEntry {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        return keyStore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
    }

    private fun getPrivateKey() = getKey().privateKey

    fun getPublicKey(): PublicKey = getKey().certificate.publicKey

    /**
     * Encrypt plain text to RSA encrypted and Base64 encoded string
     *
     * @param args
     * @return a encrypted string that Base64 encoded
     */
    fun encrypt(message: String, key: PublicKey): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encoded = cipher.doFinal(message.toByteArray(StandardCharsets.UTF_8))
        return encodeToString(encoded, DEFAULT)
    }

    fun decrypt(message: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORM)
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey())
        val decoded = cipher.doFinal(decode(message, DEFAULT))
        return String(decoded)
    }


    fun publicKeyToString(key: Key): String = encodeToString(key.encoded, DEFAULT)

    fun stringToPublicKey(string: String): PublicKey {
        val keyBytes = decode(string, DEFAULT)
        val keyFactory = KeyFactory.getInstance(CRYPTO_METHOD)
        return keyFactory.generatePublic(X509EncodedKeySpec(keyBytes))
    }


    fun saveKey(context: Context) = generateKeyPair(context)
}
