package no.difi.meldingsutveksling.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun String?.notNullOrEmpty(f: (String?) -> Unit) {
    if (this != null && this.isNotEmpty()) {
        f(this)
    }
}