package book

import author.Author
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class BookService {
    suspend fun getAllBooks(): Map<Book, Author> {
        var books: MutableMap<Book,Author> = mutableMapOf<Book, Author>()
        suspendTransaction {
            val allBooks = Book.all().forEach { book ->
                books[book] = book.author
            }
        }
        return books
    }
}