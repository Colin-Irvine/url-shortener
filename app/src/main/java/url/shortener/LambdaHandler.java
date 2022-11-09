package url.shortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import url.storage.InMemoryStorage;
import url.storage.Storage;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private APIGatewayProxyRequestEvent request;
    private APIGatewayProxyResponseEvent response;
    private Shortener shortener;
    private String shortUrl;

    public LambdaHandler() throws NoSuchAlgorithmException {
        this.response = new APIGatewayProxyResponseEvent();
        Storage storage = new InMemoryStorage();
        this.shortener = new MD5Shortener(storage);
    }

    public LambdaHandler(Shortener shortener) {
        this.response = new APIGatewayProxyResponseEvent();
        this.shortener = shortener;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // TODO: This can be encapsulated at least inside a function, return createResponse for example
        try {
            request = input;
            checkRequest();

            if (isShortenRequest()) {
                handleShortenRequest();
            }
            if (isLengthenRequest()) {
                handleLengthenRequest();
            }

            return response;

        }
        catch (HttpException exc) {
            createErrorResponse(exc);
            return response;
        }
        catch (Exception exc) {
            createErrorResponse(exc);
            return response;
        }
    }

    private void checkRequest() throws HttpException {
        if (isNotAcceptedHttpMethod()) {
            throw new HttpException("HTTP Method not allowed.", 405);
        }

        if (isEventBodyBad()) {
            throw new HttpException("Bad request.", 400);
        }

        if (isEventPathBad()) {
            throw new HttpException("Bad URL path.", 400);
        }
    }

    private boolean isNotAcceptedHttpMethod() {
        return !(isShortenRequest() ||
                isLengthenRequest());
    }

    private boolean isShortenRequest() {
        return request.getHttpMethod().equals("POST");
    }

    private boolean isLengthenRequest() {
        return request.getHttpMethod().equals("GET");
    }

    private void handleLengthenRequest() throws HttpException {
        // TODO: Clean up and abstract/extract if possible
        String errorMessage = "Key \'%s\' not found in storage.";
        String path = extractShortNameFromPath();
        shortUrl = shortener.getLongName(path);

        if (isKeyNotInStorage()) {
            errorMessage = String.format(errorMessage, path);
            throw new HttpException(errorMessage,404);
        }

        createKeyFoundResponse(shortUrl);
    }

    private void handleShortenRequest() {
        // TODO: Clean up and abstract/extract if possible
        //  -    This also only manipulates response
        String url = request.getBody();
        shortUrl = shortener.getShortName(url);
        response.setBody(shortUrl);
        response.setStatusCode(200);
    }

    private boolean isKeyNotInStorage() {
        return shortUrl == null;
    }

    private void createKeyFoundResponse(String location) {
        // TODO: Clean up and abstract/extract if possible
        //  -    This only manipulates 'response'
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        response.setStatusCode(302);
        response.setHeaders(headers);
    }

    private boolean isEventBodyBad() {
        if (isLengthenRequest()) {
            return false;
        }
        if (request.getBody().isEmpty()) {
            return true;
        }

        return false;
    }

    private boolean isEventPathBad() {
        if (isShortenRequest()){
            return false;
        }
        return extractShortNameFromPath().isEmpty();
    }

    private String extractShortNameFromPath() {
        String path = request.getPath();

        if (path.lastIndexOf('/') != -1)
            return path.substring(path.lastIndexOf('/') + 1);

        return path;
    }

    private class HttpException extends Exception {
        private String errorMessage;
        private int statusCode;

        public HttpException(String errorMessage, int statusCode) {
            super(errorMessage);
            this.errorMessage = errorMessage;
            this.statusCode = statusCode;
        }
    }

    private APIGatewayProxyResponseEvent createErrorResponse(HttpException exc) {
        // TODO: Consider this as a class, this only changes response
        response.setStatusCode(exc.statusCode);
        response.setBody(exc.errorMessage);

        return response;
    }

    private APIGatewayProxyResponseEvent createErrorResponse(Exception exc) {
        // TODO: Consider this as a class, this only changes response
        response.setBody(exc.getMessage());
        response.setStatusCode(500);

        return response;
    }

}
