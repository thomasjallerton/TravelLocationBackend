package util

import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import java.io.InputStream
import java.io.ByteArrayOutputStream



class HttpRequest {
    companion object {
        fun getHtml(url: String): String {
            val httpClient = HttpClients.createDefault()

            val get = HttpGet(url)
            val response= httpClient.execute(get)
            val result = if (response.statusLine.statusCode == HttpStatus.SC_OK) {
                inputStreamToString(response.entity.content)
            } else {
                ""
            }
            response?.close()
            httpClient.close()
            return result
        }

        private fun inputStreamToString(inputStream: InputStream): String {
            val result = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int = inputStream.read(buffer)
            while (length != -1) {
                result.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            return result.toString("UTF-8")
        }
    }
}