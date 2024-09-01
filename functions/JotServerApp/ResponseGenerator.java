import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class ResponseGenerator {


    public static void initiateErrorMessage(HttpServletResponse response, Integer errorCode, String errorTitle, String errorMessage) throws IOException {
        JSONObject errorObject = new JSONObject() {
            {
                put("error", new JSONObject() {{
                    put("title", errorTitle);
                    put("description", errorMessage);
                }});
            }
        };

        JSONObject responseData = new JSONObject() {
            {
                put("data", errorObject);
                put("status", "Failure");
            }
        };

        response.setContentType("application/json");
        response.setStatus(errorCode);

        response.getWriter().write(responseData.toJSONString());
    }

}