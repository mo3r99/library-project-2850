package leeds.compsci.routes

import book.BookService
import copy.Status
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtmlFragment
import leeds.compsci.copy.CopyService

suspend fun ApplicationCall.reserveRoute() {
    val bookId: Int = parameters["bookId"]?.toInt() ?: throw IllegalArgumentException("Invalid bookId parameter")
    val copyId: Int = parameters["copyId"]?.toInt() ?: throw IllegalArgumentException("Invalid copyId parameter")

    val copyService = CopyService()
    val copy = copyService.getCopyById(copyId)

    val bookService = BookService()
    val book = bookService.getBookById(bookId)

    if (copy.status != Status.AVAILABLE) {
        throw IllegalArgumentException("Copy with ID $copyId is not available for reservation")
    }

    if (!copyService.reserveCopy(copyId)) {
        throw IllegalStateException("Failed to reserve copy with ID $copyId")
    }

    respondHtmlFragment {
        +"Copy of ${book.first.title} ${if (copy.locationCode != "") "located at ${copy.locationCode}" else ""} has been reserved successfully"
    }
}