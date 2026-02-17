package author

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Author(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<Author>(AuthorTable)

    var name by AuthorTable.name
    var bio by AuthorTable.bio
}
