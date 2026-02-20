package copy

import book.Book
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class Copy (id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Copy>(CopyTable)

    var book by Book referencedOn CopyTable.book
    var formatCode by CopyTable.formatCode
    var locationCode by CopyTable.locationCode
    var notes by CopyTable.notes
}
