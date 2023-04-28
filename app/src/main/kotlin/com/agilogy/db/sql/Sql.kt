package com.agilogy.db.sql

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

typealias SqlParameter = (PreparedStatement, Int) -> Unit

enum class TransactionIsolationLevel(val value: Int) {
    None(Connection.TRANSACTION_NONE),
    ReadUncommitted(Connection.TRANSACTION_READ_UNCOMMITTED),
    ReadCommitted(Connection.TRANSACTION_READ_COMMITTED),
    RepeatableRead(Connection.TRANSACTION_REPEATABLE_READ),
    Serializable(Connection.TRANSACTION_SERIALIZABLE),
}

object Sql {

    context(Connection)
    private suspend fun <A> preparedStatement(sql: String, vararg params: SqlParameter, f: (PreparedStatement) -> A): A =
        this@Connection.prepareStatement(sql).use { statement ->
            setParameters(statement, *params)
            f(statement)
        }

    suspend fun <A> DataSource.sql(f: suspend context(Connection) () -> A): A = withContext(Dispatchers.IO) {
        with(connection) {
            f(this@with)
        }
    }

    suspend fun <A> DataSource.transaction(isolationLevel: TransactionIsolationLevel, f: context(Connection) () -> A): A =
        withContext(Dispatchers.IO) {
            connection.use {
                val previousAutoCommit = it.autoCommit
                try {
                    with(it) {
                        autoCommit = false
                        transactionIsolation = isolationLevel.value
                        f(this).also { commit() }
                    }
                }finally{
                    it.autoCommit = previousAutoCommit
                }
            }
        }

    context(Connection)
    suspend fun <A> select(sql: String, vararg params: SqlParameter, reader: (ResultSetView) -> A): List<A> =
        preparedStatement(sql, *params) { ps ->
            ps.executeQuery().use { resultSet ->
                val res = mutableListOf<A>()
                val resultSetView = ResultSetView(resultSet)
                while (resultSet.next()) {
                    res.add(reader(resultSetView))
                }
                res
            }
        }

    context(Connection)
    suspend fun <A> selectOne(sql: String, vararg params: SqlParameter, reader: (ResultSetView) -> A): A? =
        preparedStatement(sql, *params) { ps ->
            ps.executeQuery().use { resultSet ->
                val resultSetView = ResultSetView(resultSet)
                if (resultSet.next()) {
                    reader(resultSetView).also { if (resultSet.next()) throw IllegalStateException("More than one row found!") }
                } else null
            }
        }

    private fun setParameters(preparedStatement: PreparedStatement, vararg params: SqlParameter) {
        params.forEachIndexed { pos, param -> param(preparedStatement, pos + 1) }
    }

    context(Connection)
    suspend fun update(sql: String, vararg params: SqlParameter): Int = preparedStatement(sql, *params) { it.executeUpdate() }

    class BatchUpdate internal constructor(private val preparedStatement: PreparedStatement) {
        fun addBatch(vararg params: SqlParameter) {
            with(preparedStatement) {
                setParameters(preparedStatement, *params)
                addBatch()
            }
        }
    }

    context(Connection)
    suspend fun batchUpdate(sql: String, f: context(BatchUpdate)() -> Unit): List<Int> = preparedStatement(sql) { ps ->
        with(BatchUpdate(ps)) { f(this) }
        ps.executeBatch().toList()
    }
}