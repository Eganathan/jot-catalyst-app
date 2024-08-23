import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalyst.advanced.CatalystAdvancedIOHandler;

public class JotServerApp implements CatalystAdvancedIOHandler {
	private static final Logger LOGGER = Logger.getLogger(JotServerApp.class.getName());
	private JSONObject responseData = new JSONObject();
	
	@Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			
			String method = request.getMethod();
			LOGGER.log(Level.SEVERE,"Jots Request Recived");
			switch(request.getRequestURI()) {
				case "/jots/test": {
					LOGGER.log(Level.SEVERE,"Jots Request routed to /jots");
					responseData.put("data", new JSONObject() {
						{
							put("success","jots/test is working");
							put("error_message", "Successfully test completed, jots/test is working as expected");
						}
					});
					response.setContentType("application/json");
					response.setStatus(200);
					response.getWriter().write(responseData.toString());
					break;	
				}
				case "/jots": {
					LOGGER.log(Level.SEVERE,"Jots Request routed to /jots");

					switch (method) {
						case "GET":{
							LOGGER.log(Level.SEVERE,"Jots GET request");
						
							break;
						}
						case "POST":{
							LOGGER.log(Level.SEVERE,"Jots POST request");
						
							break;
						}
						case "UPDATE":{
							LOGGER.log(Level.SEVERE,"Jots UPDATE request");
						
							break;
						}
						case "DELETE":{
							LOGGER.log(Level.SEVERE,"Jots DELETE request");
						
							break;
						}
						default:{
							LOGGER.log(Level.SEVERE,"Jots UNKNOWN request");
							responseData.put("data", new JSONObject() {
								{
									put("error_title","invalid request method");
									put("error_message", "You seem to have used invalid method, use only GET/POST/UPDATE/DELETE");
								}
							});
							response.setContentType("application/json");
							response.setStatus(200);
							response.getWriter().write(responseData.toString());
							break;
						}
					}	
					break;
				}
				default: {
					LOGGER.log(Level.SEVERE,"Jots Request routed to Default as invalid URL");
					responseData.put("data", new JSONObject() {
						{
						put("error_title","Invalid URL");
						put("error_message", "You seem to have entered invalid url please try again with valid url");
					}
					});
					response.setContentType("application/json");
					response.setStatus(404);
					response.getWriter().write(responseData.toString());
				}
			}
		}
		catch(Exception e) {
			LOGGER.log(Level.SEVERE,"Exception in JotServerApp",e);
			response.setStatus(500);
			response.getWriter().write("Internal server error");
		}
	}
}