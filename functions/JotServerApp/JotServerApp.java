import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.catalyst.advanced.CatalystAdvancedIOHandler;


public class JotServerApp implements CatalystAdvancedIOHandler {

    TempJotServiceApp tempApp = new TempJotServiceApp();

    @Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            new ServerApp(request,response);
            tempApp.runner(request,response);
        } catch (Exception e) {
            tempApp.errorMessage(response, e, "Exception at server", "Sorry we cannot handle your data now!, we had a exception.", 500);
        }
    }
}