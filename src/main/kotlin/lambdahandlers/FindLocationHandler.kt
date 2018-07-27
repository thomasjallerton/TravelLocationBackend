package lambdahandlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import service.FindTopLocationService
import util.ResponseConverter

class FindLocationHandler: AbstractLambdaHandler() {

    val findTopLocationService = FindTopLocationService()

    override fun handleRequest(request: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        return if (request != null) {
            try {
                val queryParams = request.queryStringParameters
                val count = queryParams.getOrDefault("count", "1")
                val body = jacksonObjectMapper().readValue(request.body, FindTopLocationRequest::class.java)
                if (body.url != null) {
                    logger.debug("Handing off to service")
                    ResponseConverter.responseToLambda(findTopLocationService.findTopLocation(body.url, count.toInt()))
                } else {
                    badRequestResponse("Missing url param")
                }
            } catch (e: Exception) {
                badRequestResponse(e)
            }
        } else {
            badRequestResponse("Missing request")
        }
    }

    data class FindTopLocationRequest(val url: String?)
}

fun main(args: Array<String>) {
    val request = APIGatewayProxyRequestEvent()
    val queryStringParams = HashMap<String, String>()
    queryStringParams["count"] = "3"
    request.queryStringParameters = queryStringParams

    val findTopLocationRequest = FindLocationHandler.FindTopLocationRequest("https://medium.com/@thinksustainabilityblog/natural-singapore-3d7e789c408f")
    val body = jacksonObjectMapper().writeValueAsString(findTopLocationRequest)

    request.body = body

    val response = FindLocationHandler().handleRequest(request, null)

    println(response.body)
}