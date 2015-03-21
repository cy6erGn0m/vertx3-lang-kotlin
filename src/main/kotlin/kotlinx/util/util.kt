package kotlinx.util

public inline fun <T> T.with(body : T.() -> Unit) : T {
    body()
    return this
}
