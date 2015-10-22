package core.jdbc

import io.vertx.ext.jdbc.*
import io.vertx.kotlin.lang.*
import io.vertx.kotlin.lang.jdbc.*

// callback hell, could be resolved via RxJava JDBCClient implementation + RxKotlin
fun query(connection: JDBCClient, done: (AsyncResult<Int>) -> Unit) {
    connection.getConnection { connectEvent ->
        when (connectEvent) {
            is AsyncSuccessResult -> {
                connectEvent.connection.queryWithParams("select * from test where id = ?", listOf(2)) { queryEvent ->
                    when (queryEvent) {
                        is AsyncSuccessResult -> {
                            val rowsCount = queryEvent.resultSet.numRows
                            queryEvent.resultSet.rows.forEach { row ->
                                println(row.encode())
                            }
                            connectEvent.connection.close {
                                done(AsyncSuccessResult(rowsCount))
                            }
                        }
                        is AsyncErrorResult -> done(AsyncErrorResult<Int>(queryEvent.error))
                    }
                }
            }
            is AsyncErrorResult -> done(AsyncErrorResult<Int>(connectEvent.error))
        }
    }
}

