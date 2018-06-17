package lambdahandlers

import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.apache.http.HttpStatus
import org.slf4j.LoggerFactory

abstract class AbstractLambdaHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected val logger = LoggerFactory.getLogger(this::class.java)

    fun badRequestResponse(e: Exception, message: String = "BAD REQUEST"): APIGatewayProxyResponseEvent {
        logger.debug(e.message, e)
        return badRequestResponse(message)
    }

    fun badRequestResponse(message: String): APIGatewayProxyResponseEvent {
        val result = APIGatewayProxyResponseEvent()
        result.statusCode = HttpStatus.SC_BAD_REQUEST
        result.body = "{\"message\" : \"$message\"}"
        return result
    }

    fun okRequestResponse(): APIGatewayProxyResponseEvent {
        val result = APIGatewayProxyResponseEvent()
        result.statusCode = HttpStatus.SC_OK
        return result
    }


    fun Map<String, String>.getValueOrNull(key: String): String? {
        return if (containsKey(key)) get(key) else null
    }
}