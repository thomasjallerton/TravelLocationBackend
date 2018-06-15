package lambdahandlers

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import service.FindTopLocationService
import util.ResponseConverter

class FindLocationHandler: AbstractLambdaHandler() {

    val findTopLocationService = FindTopLocationService()

    override fun handleRequest(request: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        return if (request != null) {
            val queryParams = request.queryStringParameters
            val url = queryParams.getValueOrNull("url")
            if (url != null) {
                ResponseConverter.responseToLambda(findTopLocationService.findTopLocation(url))
            } else {
                badRequestResponse("Missing url param")
            }
        } else {
            badRequestResponse("Missing request")
        }
    }
}