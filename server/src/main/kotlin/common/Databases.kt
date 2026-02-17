package common

import author.Author
import author.AuthorTable
import book.Book
import book.BookTable
import copy.Copy
import copy.CopyTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.io.read
import user.UserTable

object LibraryDatabase {
    const val URL = "jdbc:h2:./libraryData"
    const val DRIVER = "org.h2.Driver"

    val db by lazy { Database.connect(URL, DRIVER) }
}

@DataSchema
interface DataFrameType {
    val author: String
    @ColumnName("format_code")
    val formatCode: String
    @ColumnName("isbn_13")
    val isbn13: Double?
    @ColumnName("location_code")
    val locationCode: String?
    val notes: String?
    val title: String
}

fun Application.configureDatabases() {
    TransactionManager.defaultDatabase = LibraryDatabase.db
    seedDatabase()
}

fun Application.seedDatabase() {
    val dir = "/Users/muhammadrauf/Documents/Dev Projects/library-project-2850/server/src/"
    val df = DataFrame.read("${dir}main/resources/booklist.csv").cast<DataFrameType>()

    transaction {
        SchemaUtils.create(BookTable, UserTable, AuthorTable, CopyTable)

        df.select("author").distinct().values().forEach {
            Author.new {
                name = it.toString()
                bio = ""
            }
        }

        df.select("author", "isbn_13", "author").distinct().forEach { row ->
            val book = Book.new {
                title = row["title"].toString()
                isbn = row["isbn_13"].toString()
                authorId = Author.find {
                    AuthorTable.name eq row["author"].toString()
                }.first()
            }
        }

        df.forEach { row ->
            val isbn = row["isbn_13"].toString()
            val matchingBook = Book.find { BookTable.isbn eq isbn }.first()

            Copy.new {
                book = matchingBook
                formatCode = row["format_code"].toString()
                locationCode = row["location_code"]?.toString() ?: ""
                notes = row["notes"]?.toString() ?: ""
            }
        }
    }
}