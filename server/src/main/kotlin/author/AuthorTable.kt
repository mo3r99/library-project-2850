package author

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

const val MAX_LENGTH = 50

object AuthorTable : IntIdTable("authors") {
    var name = varchar("name", MAX_LENGTH)
    var bio = text("bio")
}
