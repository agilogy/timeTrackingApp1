package com.agilogy.db.hikari

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.resource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object HikariCp {

    fun dataSource(
        username: String,
        password: String,
        host: String,
        port: Int = 5432,
        database: String? = null
    ): Resource<DataSource> =
        resource(
            autoCloseable {
                val config = HikariConfig()
                config.jdbcUrl = "jdbc:postgresql://$host:$port/${database ?: ""}"
                config.username = username
                config.password = password
                config.addDataSourceProperty("cachePrepStmts", "true")
                config.addDataSourceProperty("prepStmtCacheSize", "250")
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
                HikariDataSource(config)
            }
        )
}
