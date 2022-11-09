package url.shortener;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;
import url.storage.InMemoryStorage;
import url.storage.Storage;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

public class LambdaHandlerTest {
    @Test
    void testHandleRequestShouldReturn302StatusCodeAndHeaderLocation() throws NoSuchAlgorithmException {
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();

        APIGatewayProxyRequestEvent testEvent = createTestRequestEvent("/test", "GET");

        Map<String, String> headers = new HashMap<>();
        headers.put("Location", "https://www.google.com");

        expected.setStatusCode(302);
        expected.setHeaders(headers);
        LambdaHandler handler = new LambdaHandler();
        APIGatewayProxyResponseEvent actual = handler.handleRequest(testEvent, null);

        assertEquals(expected, actual);
    }

    @Test
    void testHandleRequestShouldReturn404NotFoundWhenShortnameNotInStorage() throws NoSuchAlgorithmException {
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();
        APIGatewayProxyResponseEvent actual;
        APIGatewayProxyRequestEvent testEvent = createTestRequestEvent("/not found", "GET");
        expected.setStatusCode(404);
        expected.setBody("Key \'not found\' not found in storage.");

        LambdaHandler handler = new LambdaHandler();
        actual = handler.handleRequest(testEvent, null);

        assertEquals(expected, actual);
    }

    @Test
    void testHandleRequestShouldReturn405BadRequestWhenNotGetOrPost() throws NoSuchAlgorithmException {
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();
        APIGatewayProxyResponseEvent actual;
        APIGatewayProxyRequestEvent testEvent = createTestRequestEvent("not found", "ANY");
        expected.setStatusCode(405);
        expected.setBody("HTTP Method not allowed.");

        LambdaHandler handler = new LambdaHandler();
        actual = handler.handleRequest(testEvent, null);

        assertEquals(expected, actual);
    }

    @Test
    void testHandleRequestShouldReturn400BadRequestWhenPathIsBlank() throws NoSuchAlgorithmException {
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();
        APIGatewayProxyResponseEvent actual;
        APIGatewayProxyRequestEvent testEvent = createTestRequestEvent("/", "GET");
        expected.setStatusCode(400);
        expected.setBody("Bad URL path.");

        LambdaHandler handler = new LambdaHandler();
        actual = handler.handleRequest(testEvent, null);

        assertEquals(expected, actual);
    }

    @Test
    void testHandleRequestShouldReturn200AndShortenedUrlAsWellAsAddingUrlToStorage() throws NoSuchAlgorithmException {
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();
        APIGatewayProxyResponseEvent actual;
        APIGatewayProxyRequestEvent testEvent = createTestRequestEvent("", "POST");
        testEvent.setBody("test");

        expected.setStatusCode(200);

        expected.setBody("kdismnx5moyu6q6pzt8tqdpy");
        Storage storage = new InMemoryStorage();
        Shortener shortener = new MD5Shortener(storage);
        LambdaHandler handler = new LambdaHandler(shortener);

        actual = handler.handleRequest(testEvent, null);
        assertEquals(expected, actual);
        String testKey = actual.getBody();

        assertEquals("test", shortener.getLongName(testKey));
    }

    private APIGatewayProxyRequestEvent createTestRequestEvent(String path, String method) {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setPath(path);
        event.setHttpMethod(method);

        return event;
    }
}
