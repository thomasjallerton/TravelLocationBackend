package util

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import javax.ws.rs.core.Response

class ResponseConverter {
    companion object {
        fun responseToLambda(response: Response): APIGatewayProxyResponseEvent {
            val result = APIGatewayProxyResponseEvent()
            result.statusCode = response.status
            result.body = response.entity.toString()
            return result
        }
    }
}
