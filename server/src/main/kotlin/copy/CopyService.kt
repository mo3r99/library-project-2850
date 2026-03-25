package copy

import copy.Copy
import copy.Status
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

class CopyService {
    suspend fun getCopyById(id: Int): Copy =
        suspendTransaction {
            Copy.findById(id) ?: throw IllegalArgumentException("Copy with ID $id not found")
        }

    suspend fun reserveCopy(id: Int): Boolean =
        suspendTransaction {
            val copy = Copy.findById(id) ?: throw IllegalArgumentException("Copy with ID $id not found")
            copy.status = Status.RESERVED

            copy.status == Status.RESERVED
        }
}
