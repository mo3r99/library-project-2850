package book

import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class BookService {
    suspend fun getAllBooksWithAuthors(): Map<Book, String> {
        val books = mutableMapOf<Book, String>()
        suspendTransaction {
            Book.all().forEach { book ->
                books[book] = book.author.name
            }
        }
        return books
    }

    suspend fun searchBooks(query: String): Map<Book, String> {
        var books = mapOf<Book, String>()
        suspendTransaction {
            val searchedBooks = Book.find { BookTable.title.lowerCase().like("%$query%") }
            books = searchedBooks.associateWith { book -> book.author.name }
        }

        return books
    }
}