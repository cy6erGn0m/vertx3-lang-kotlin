package io.vertx.kotlin.lang.jdbc

import io.vertx.kotlin.lang.*
import io.vertx.core
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLConnection

public fun JDBCClient.getConnection(block: (AsyncResult<SQLConnection>) -> Unit) {
    getConnection(Handler { event ->
        block(event.toAsyncResultK())
    })
}


public fun SQLConnection.queryWithParams(sql: String, params: Iterable<*>, onResult: (AsyncResult<ResultSet>) -> Unit) {
    queryWithParams(sql, JsonArray(params.toList()), Handler { event ->
        onResult(event.toAsyncResultK())
    })
}

public val AsyncSuccessResult<SQLConnection>.connection: SQLConnection
    get() = result

public val AsyncSuccessResult<ResultSet>.resultSet: ResultSet
    get() = result

