package com.agilogy.db.sql

import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.sql.Blob
import java.sql.Clob
import java.sql.NClob
import java.sql.Ref
import java.sql.RowId
import java.sql.SQLXML
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant

val Boolean?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setBoolean(pos, it) } ?: ps.setNull(pos, Types.BOOLEAN) }
val Byte?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setByte(pos, it) } ?: ps.setNull(pos, Types.TINYINT) }
val Short?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setShort(pos, it) } ?: ps.setNull(pos, Types.SMALLINT) }
val Int?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setInt(pos, it) } ?: ps.setNull(pos, Types.INTEGER) }
val Long?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setLong(pos, it) } ?: ps.setNull(pos, Types.BIGINT) }
val Float?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setFloat(pos, it) } ?: ps.setNull(pos, Types.FLOAT) }
val Double?.param: SqlParameter get() = { ps, pos -> this@param?.let { ps.setDouble(pos, it) } ?: ps.setNull(pos, Types.DOUBLE) }

val BigDecimal?.param: SqlParameter get() = { ps, pos -> ps.setBigDecimal(pos, this@param) }
val String?.param: SqlParameter get() = { ps, pos -> ps.setString(pos, this@param) }
val ByteArray?.param: SqlParameter get() = { ps, pos -> ps.setBytes(pos, this@param) }
val Instant?.param: SqlParameter get() = { ps, pos -> ps.setTimestamp(pos, this@param?.let { Timestamp(it.toEpochMilli()) }) }
val Reader?.param: SqlParameter get() = { ps, pos -> ps.setCharacterStream(pos, this@param) }
val InputStream?.param: SqlParameter get() = { ps, pos -> ps.setBinaryStream(pos, this@param) }
val Ref?.param: SqlParameter get() = { ps, pos -> ps.setRef(pos, this@param) }
val RowId?.param: SqlParameter get() = { ps, pos -> ps.setRowId(pos, this@param) }
val String?.paramNSString: SqlParameter get() = { ps, pos -> ps.setNString(pos, this@paramNSString) }
val Reader?.paramNsString: SqlParameter get() = { ps, pos -> ps.setNCharacterStream(pos, this@paramNsString) }
val Blob?.param: SqlParameter get() = { ps, pos -> ps.setBlob(pos, this@param) }
val Clob?.param: SqlParameter get() = { ps, pos -> ps.setClob(pos, this@param) }
val NClob?.param: SqlParameter get() = { ps, pos -> ps.setNClob(pos, this@param) }
val SQLXML?.param: SqlParameter get() = { ps, pos -> ps.setSQLXML(pos, this@param) }