import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

abstract class BaseService<T> implements Runner<T> {


    public void initiateErrorMessage(HttpServletResponse response, Integer responseCode, String errorTitle, String errorMessage) throws IOException {
        initiateResponse(response, responseCode, getErrorTemplate(errorTitle, errorMessage), "failure");
    }

    public void initiateSuccessMessage(HttpServletResponse response, Integer responseCode, JSONObject responseJson) throws IOException {
        initiateResponse(response, responseCode, responseJson, "success");
    }


    private void initiateResponse(HttpServletResponse response, Integer responseCode, JSONObject data, String status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(responseCode);
        response.getWriter().write(baseTemplate(data, status).toJSONString());
    }

    private JSONObject getErrorTemplate(String errorTitle, String errorMessage) {
        return new JSONObject() {
            {
                put("error", new JSONObject() {{
                    put("title", errorTitle);
                    put("description", errorMessage);
                }});
            }
        };
    }

    private JSONObject baseTemplate(JSONObject data, String status) {
        return new JSONObject() {
            {
                put("data", data);
                put("status", status);
            }
        };
    }
}