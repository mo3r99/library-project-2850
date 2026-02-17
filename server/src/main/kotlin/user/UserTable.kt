package user

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

enum class Role {
    ADMIN, USER
}

const val MAX_USERNAME_LENGTH = 50
const val MAX_PASSWORD_HASH_LENGTH = 256
const val MAX_NAME_LENGTH = 100
const val MAX_EMAIL_LENGTH = 100
const val MAX_ADDRESS_LENGTH = 100

object UserTable : IntIdTable("users") {
    var username = varchar("username", MAX_USERNAME_LENGTH)
    var passwordHash = varchar("password_hash", MAX_PASSWORD_HASH_LENGTH)
    var role = enumeration<Role>("role", Role::class)
    var name = varchar("name", MAX_NAME_LENGTH)
    var emailAddress = varchar("email_address", MAX_EMAIL_LENGTH)
    var homeAddress = varchar("home_address", MAX_ADDRESS_LENGTH)
}
