为了实现你所描述的功能，我们需要引入一种加密机制来保护 `accessKey`。考虑到链接的长度和易用性，我们可以使用对称加密算法，如AES（Advanced Encryption Standard）。AES是一种广泛使用的加密标准，它提供了良好的安全性和性能。

我们将使用Java Cryptography Extension (JCE) 来实现AES加密解密。此外，我们还需要一个校验码来确保数据的完整性。这里，我将展示如何创建一个工具类来处理加密和解密操作，并在你的文件操作类中集成这些功能。

首先，我们需要添加一个工具类 `EncryptionUtil` 来处理加密和解密逻辑：

```kotlin
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

/**
 * 加密解密工具类
 *
 * @author Qwen
 * @createTime 2024/12/3
 * @description 提供基于AES的加解密功能
 */
object EncryptionUtil {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

    // 获取或设置校验码
    var checkCode: String by lazy {
        // 这里可以是从Redis获取checkCode的逻辑
        // 例如：redisTemplate.opsForValue().get("filehub_check_code") ?: "filehub"
        "filehub" // 默认值，实际应用中应该从Redis读取
    }

    /**
     * 对字符串进行AES加密
     * @param value 待加密的字符串
     * @return 加密后的字符串
     */
    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(checkCode.toByteArray(), ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.toByteArray()))
    }

    /**
     * 对字符串进行AES解密
     * @param encryptedValue 加密后的字符串
     * @return 解密后的字符串
     */
    fun decrypt(encryptedValue: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(checkCode.toByteArray(), ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedValue)))
    }
}
```

接下来，我们需要修改 `FileManipulationUtil` 类，以便在保存文件时返回加密后的 `accessKey`，并在获取文件时先解密 `accessKey`。

```kotlin
// 修改 saveFile 方法
fun saveFile(accessKey: String, file: MultipartFile): String {
    // ... 省略其他代码
    val fileName = targetFile.name
    val encipheredAccessKey = EncryptionUtil.encrypt(accessKey)
    return "$encipheredAccessKey/$fileName"
}

// 修改 getFile 方法
fun getFile(encipheredAccessKey: String, fileName: String): File? {
    val accessKey = EncryptionUtil.decrypt(encipheredAccessKey)
    return File(rootLocation, "$accessKey/$fileName").takeIf { it.exists() }
}

// 修改 deleteFile 方法
fun deleteFile(encipheredAccessKey: String, fileName: String) {
    val accessKey = EncryptionUtil.decrypt(encipheredAccessKey)
    File(rootLocation, "$accessKey/$fileName").delete()
}

// 修改 getFileDirectorySize 方法
fun getFileDirectorySize(encipheredAccessKey: String): Long {
    val accessKey = EncryptionUtil.decrypt(encipheredAccessKey)
    val directory = File(rootLocation, accessKey)
    return if (directory.isDirectory) directory.walk().sumOf { it.length() } else 0L
}

// 修改 getAccessKeyFileNameList 方法
fun getAccessKeyFileNameList(encipheredAccessKey: String): List<String>? {
    val accessKey = EncryptionUtil.decrypt(encipheredAccessKey)
    val directory = File(rootLocation, accessKey)
    return if (directory.isDirectory) directory.list()?.toList() else null
}
```

这样我们就完成了加密 `accessKey` 的功能。请注意，这个示例中的 `checkCode` 是硬编码的，你需要根据实际情况从Redis中动态获取它。此外，由于 AES-ECB 模式存在一些安全问题，建议在生产环境中使用更安全的模式，如 AES-GCM 或者结合使用随机的初始化向量（IV）来增强安全性。