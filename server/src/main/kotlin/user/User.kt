package user

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class User (id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(UserTable)

    var username by UserTable.username
    var passwordHash by UserTable.passwordHash
    var role by UserTable.role
    var name by UserTable.name
    var emailAddress by UserTable.emailAddress
    var homeAddress by UserTable.homeAddress

}
