package url.shortener;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LambdaHandlerTest {
    @Test
    void testHandleRequestShouldReturn302StatusCodeAndGoogleLocation() {
        APIGatewayProxyRequestEvent testEvent = new APIGatewayProxyRequestEvent();
        APIGatewayProxyResponseEvent expected = new APIGatewayProxyResponseEvent();
        APIGatewayProxyResponseEvent actual;
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", "https://www.bing.com");
        expected.setStatusCode(302);
        expected.setHeaders(headers);
        LambdaHandler handler = new LambdaHandler();
        actual = handler.handleRequest(testEvent, null);
        assertEquals(actual, expected);
    }
}
