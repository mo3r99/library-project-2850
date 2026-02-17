package book

import author.AuthorTable
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_NAME_LENGTH = 50
const val MAX_ISBN_LENGTH = 13

object BookTable: IntIdTable("book") {
    var title = varchar("title", MAX_NAME_LENGTH)
    var author = reference("author_id", AuthorTable)
    var isbn = varchar("isbn", MAX_ISBN_LENGTH).uniqueIndex()
}
