import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalyst.advanced.CatalystAdvancedIOHandler;

import java.util.logging.Level;
import java.util.logging.Logger;


public class JotServerApp implements CatalystAdvancedIOHandler {
    private static final Logger LOGGER = Logger.getLogger(JotServerApp.class.getName());

    @Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            new RESTAppService(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage());
            ResponseGenerator.initiateErrorMessage(response, 500, "Request failed due to an exception", "Sorry we cannot handle your data now!, we had a exception. E" + e.getLocalizedMessage());
        }
    }
}