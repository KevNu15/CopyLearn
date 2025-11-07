package Util

import java.util.UUID

object IdUtil {
    fun newId(): String = UUID.randomUUID().toString()
}
