package auth

import com.password4j.Hash
import com.password4j.Password
import java.security.MessageDigest

fun String.sha256(text: String): ByteArray {
    val hasher = MessageDigest.getInstance("SHA-256")
    return hasher.digest(text.toByteArray())
}

const val RANDOM_SALT_LENGTH = 8

fun String.hashAndSalt(): Hash = Password.hash(this).addRandomSalt(RANDOM_SALT_LENGTH).withScrypt()

fun check(
    password: String,
    expectedHash: Hash,
) = Password.check(password, expectedHash)
