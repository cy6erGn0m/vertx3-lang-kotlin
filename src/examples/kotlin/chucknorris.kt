package examples.chucknorris

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.kotlin.lang.DefaultVertx

/**
 * -This example connects to the server and retrieves random joke about Chuck Norris-
 * Chuck Norris sending us a random joke and we display it on your screen when he want
 */
fun main(args: Array<String>) {
    DefaultVertx {
        createHttpClient().get("api.icndb.com", "/jokes/random") { response ->
            response.bodyHandler {
                val node = ObjectMapper().readValue(it.bytes, JsonNode::class.java)
                val joke = node.get("value").get("joke").asText()
                println(joke)
                this.close()
            }
        }.end()
    }
}
