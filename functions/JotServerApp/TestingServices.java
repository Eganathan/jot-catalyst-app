import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestingServices implements Runner<TestingServices> {

    private HttpServletRequest _request;
    private HttpServletResponse _response;


    @Override
    public TestingServices create(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        return this;
    }

    @Override
    public void process() throws IOException {
        JSONObject responseData = new JSONObject();

        responseData.put("status","Success");
        responseData.put("testing","Working as expexted");

        _response.setContentType("application/json");
        _response.setStatus(404);
        _response.getWriter().write(responseData.toString());
    }

    @Override
    public void createAndProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        create(request,response).process();
    }
}
