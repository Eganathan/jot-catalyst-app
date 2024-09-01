import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.catalyst.advanced.CatalystAdvancedIOHandler;


public class JotServerApp implements CatalystAdvancedIOHandler {


    @Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            new ServerApp(request,response);
        } catch (Exception e) {
            ResponseGenerator.initiateErrorMessage(response, 500,"Request failed due to an exception","Sorry we cannot handle your data now!, we had a exception. E"+e.getLocalizedMessage());
        }
    }
}