package copy

import book.BookTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val FORMAT_CODE_LENGTH = 30
const val LOCATION_CODE_LENGTH = 30
const val MAX_NOTE_LENGTH = 255

object CopyTable : IntIdTable() {
    var book = reference("bookId", BookTable)
    var formatCode = varchar("formatCode", FORMAT_CODE_LENGTH)
    var locationCode = varchar("locationCode", LOCATION_CODE_LENGTH)
    var notes = varchar("notes", MAX_NOTE_LENGTH)
}