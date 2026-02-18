package book

import author.Author
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Book (id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Book>(BookTable)

    var title by BookTable.title
    var author by Author referencedOn BookTable.author
    var isbn by BookTable.isbn
}
