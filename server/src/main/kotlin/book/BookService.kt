package book

import author.Author
import author.AuthorTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class BookService {
    suspend fun getAllBooksWithAuthors(): Map<Book, String> {
        var books: MutableMap<Book, String> = mutableMapOf<Book, String>()
        suspendTransaction {
            val allBooks = Book.all().forEach { book ->
                books[book] = book.author.name
            }
        }
        return books
    }
}