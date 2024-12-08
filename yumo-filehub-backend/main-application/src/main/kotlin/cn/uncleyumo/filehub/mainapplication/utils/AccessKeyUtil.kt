package cn.uncleyumo.filehub.mainapplication.utils

import cn.uncleyumo.utils.ColorPrinter
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author uncle_yumo
 * @fileName AccessKeyUtil
 * @createDate 2024/12/4 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

class AccessKeyUtil {
    companion object {

        private const val ALGORITHM = "AES"  // AES encryption algorithm
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"  // AES encryption transformation
        private const val CHECK_CODE = "这里保密哦！"


        /*
         * Validate the format of access key. Only letters, numbers, underscores and dashes are allowed.
         */
        fun validateFormat(accessKey: String): Boolean {
            // Only letters, numbers, underscores and dashes are allowed
            return accessKey.matches(Regex("^[a-zA-Z0-9_-]+$"))
        }

        fun encrypt(accessKey: String): String {

            val cipher = Cipher.getInstance(TRANSFORMATION)  // Create a cipher instance
            val secretKeySpec = SecretKeySpec(getKey(), ALGORITHM)  // Create a secret key spec
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)  // Initialize the cipher

            val encryptedBytes = cipher.doFinal(accessKey.toByteArray())
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes)
        }

        fun decrypt(encryptedAccessKey: String): String {

            val modifiedBase64 = encryptedAccessKey
                .replace('-', '+')
                .replace('_', '/')

            val padding = 4 - (modifiedBase64.length % 4)
            val paddedBase64 = modifiedBase64 + "=".repeat(padding % 4)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKeySpec = SecretKeySpec(getKey(), ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)

            return String(cipher.doFinal(Base64.getDecoder().decode(paddedBase64)))
        }

        private fun getKey(): ByteArray {
            val sha256 = MessageDigest.getInstance("SHA-256")
            return sha256.digest(CHECK_CODE.toByteArray()).copyOf(16) // 截取为16字节
        }

    }
}
