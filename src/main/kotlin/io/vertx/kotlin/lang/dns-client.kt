package io.vertx.kotlin.lang

import io.vertx.core.Handler
import io.vertx.core.dns.DnsClient
import io.vertx.core.dns.MxRecord
import io.vertx.core.dns.SrvRecord

public fun DnsClient.lookup(a: String, handler: (AsyncResult<String>) -> Unit): DnsClient =
        lookup(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.lookup4(a: String, handler: (AsyncResult<String>) -> Unit): DnsClient =
        lookup4(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.lookup6(a: String, handler: (AsyncResult<String>) -> Unit): DnsClient =
        lookup6(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveA(a: String, handler: (AsyncResult<List<String>>) -> Unit): DnsClient =
        resolveA(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveAAAA(a: String, handler: (AsyncResult<List<String>>) -> Unit): DnsClient =
        resolveAAAA(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveCNAME(a: String, handler: (AsyncResult<List<String>>) -> Unit): DnsClient =
        resolveCNAME(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveTXT(a: String, handler: (AsyncResult<List<String>>) -> Unit): DnsClient =
        resolveTXT(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolvePTR(a: String, handler: (AsyncResult<String>) -> Unit): DnsClient =
        resolvePTR(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveNS(a: String, handler: (AsyncResult<List<String>>) -> Unit): DnsClient =
        resolveNS(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.reverseLookup(a: String, handler: (AsyncResult<String>) -> Unit): DnsClient =
        reverseLookup(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveSRV(a: String, handler: (AsyncResult<List<SrvRecord>>) -> Unit): DnsClient =
        resolveSRV(a, Handler { handler(it.toAsyncResultK()) })

public fun DnsClient.resolveMX(a: String, handler: (AsyncResult<List<MxRecord>>) -> Unit): DnsClient =
        resolveMX(a, Handler { handler(it.toAsyncResultK()) })


