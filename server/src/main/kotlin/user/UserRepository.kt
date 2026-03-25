package user

import auth.hashAndSalt
import com.password4j.Password
import io.ktor.server.auth.UserPasswordCredential
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import user.Role
import user.User
import user.UserTable
import kotlin.time.Clock

const val MIN_USERNAME_LENGTH = 3
const val MIN_PASSWORD_LENGTH = 3

fun UserPasswordCredential.nameIsValid() =
    when {
        name.length < MIN_USERNAME_LENGTH -> false
        name.all { it.isLetterOrDigit() || it == '_' } -> true
        else -> false
    }

fun UserPasswordCredential.passwordIsValid() =
    when {
        password.length < MIN_PASSWORD_LENGTH -> false
        password.any { it.isWhitespace() } -> false
        else -> true
    }

class UserRepository {
    suspend fun addUser(
        credentials: UserPasswordCredential,
        roleToApply: Role,
    ): User {
        require(credentials.nameIsValid()) {
            "Invalid username"
        }

        val nameInDb: Boolean =
            suspendTransaction {
                User.find { UserTable.username eq credentials.name }.any()
            }

        require(!nameInDb) { "Username already exists" }

        require(credentials.passwordIsValid()) {
            "Password is invalid"
        }

        val hash = credentials.password.hashAndSalt()

        return suspendTransaction {
            User.new {
                username = credentials.name
                passwordHash = hash.toString()
                role = roleToApply
            }
        }
    }

    suspend fun checkCredentials(credentials: UserPasswordCredential): Boolean {
        return suspendTransaction {
            val storedUser =
                User
                    .find { UserTable.username eq credentials.name }
                    .firstOrNull() ?: return@suspendTransaction false

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            return@suspendTransaction Password.check(credentials.password, storedUser.passwordHash).withScrypt()
        }
    }

    suspend fun getUserRole(username: String): Role =
        suspendTransaction {
            User
                .find { UserTable.username eq username }
                .firstOrNull()
                ?.role ?: throw IllegalArgumentException("User not found")
        }

    suspend fun getUserRole(id: EntityID<Int>): Role =
        suspendTransaction {
            User
                .find { UserTable.id.eq(id) }
                .firstOrNull()
                ?.role ?: throw IllegalArgumentException("User not found")
        }

    suspend fun getUsername(id: EntityID<Int>): String =
        suspendTransaction {
            User.find { UserTable.id eq id }.firstOrNull()?.username ?: throw NoSuchElementException("User not found")
        }
}
