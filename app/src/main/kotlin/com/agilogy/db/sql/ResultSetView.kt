package com.agilogy.db.sql

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class ResultSetView(private val resultSet: ResultSet) {

    private fun <A> A.orNull(): A? = if (resultSet.wasNull()) null else this

    fun string(columnIndex: Int): String? = resultSet.getString(columnIndex)

    fun boolean(columnIndex: Int): Boolean? = resultSet.getBoolean(columnIndex).orNull()

    fun byte(columnIndex: Int): Byte? = resultSet.getByte(columnIndex).orNull()

    fun short(columnIndex: Int): Short? = resultSet.getShort(columnIndex).orNull()

    fun int(columnIndex: Int): Int? = resultSet.getInt(columnIndex).orNull()

    fun long(columnIndex: Int): Long? = resultSet.getLong(columnIndex).orNull()

    fun float(columnIndex: Int): Float? = resultSet.getFloat(columnIndex).orNull()

    fun double(columnIndex: Int): Double? = resultSet.getDouble(columnIndex).orNull()

    fun bigDecimal(columnIndex: Int): BigDecimal? = resultSet.getBigDecimal(columnIndex)

    fun bytes(columnIndex: Int): ByteArray? = resultSet.getBytes(columnIndex)

    fun date(columnIndex: Int): LocalDate? = resultSet.getDate(columnIndex)?.toLocalDate()

    fun time(columnIndex: Int): LocalTime? = resultSet.getTime(columnIndex)?.toLocalTime()

    fun timestamp(columnIndex: Int): Instant? = resultSet.getTimestamp(columnIndex)?.toInstant()

    fun characterStream(columnIndex: Int): Reader? = resultSet.getCharacterStream(columnIndex)

    fun binaryStream(columnIndex: Int): InputStream? = resultSet.getBinaryStream(columnIndex)
}