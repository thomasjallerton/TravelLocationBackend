package service

import javax.ws.rs.core.Response

interface FindTopLocationApi {
    fun findTopLocation(url: String, numberOfResultsParam: Int = 1): Response
}