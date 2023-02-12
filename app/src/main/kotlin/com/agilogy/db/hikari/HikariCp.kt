package com.agilogy.db.hikari

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.autoCloseable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object HikariCp {

    fun dataSource(jdbcUrl: String, username: String, password: String): Resource<DataSource> = autoCloseable {
        val config = HikariConfig()
        config.jdbcUrl = jdbcUrl
        config.username = username
        config.password = password
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        HikariDataSource(config)
    }
}
