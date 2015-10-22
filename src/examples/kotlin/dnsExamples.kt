package examples

import io.vertx.core.*
import io.vertx.core.dns.*
import io.vertx.kotlin.lang.*

/**
 * @author [Julien Viet](mailto:julien@julienviet.com)
 */
public class DNSExamples {

    public fun example1(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
    }

    public fun example2(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.lookup("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> println("Resolved to ${result.result}")
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example3(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.lookup4("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> println("Resolved to ${result.result}")
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example4(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.lookup6("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> println("Resolved to ${result.result}")
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example5(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveA("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example6(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveAAAA("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example7(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveCNAME("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example8(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveMX("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example9(record: MxRecord) {
        record.priority()
        record.name()
    }

    public fun example10(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveTXT("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example11(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveNS("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example12(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolveSRV("vertx.io") { result ->
            when (result) {
                is AsyncSuccessResult -> result.result.forEach { println(it) }
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example13(record: SrvRecord) {
        record.priority()
        record.name()
        record.weight()
        record.port()
        record.protocol()
        record.service()
        record.target()
    }

    public fun example14(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.resolvePTR("1.0.0.10.in-addr.arpa") { result ->
            when (result) {
                is AsyncSuccessResult -> println(result.result)
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }

    public fun example15(vertx: Vertx) {
        val client = vertx.createDnsClient(53, "10.0.0.1")
        client.reverseLookup("10.0.0.1") { result ->
            when (result) {
                is AsyncSuccessResult -> println(result.result)
                is AsyncErrorResult -> println("Failed to resolve due to ${result.error}")
            }
        }
    }
}