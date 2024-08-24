import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.Level;

import org.json.JSONException;
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
			LOGGER.log(Level.INFO,"Jots Request Recived");
			switch(request.getRequestURI()) {
				case "/jots/test": {
					LOGGER.log(Level.INFO,"Jots Request routed to /jots");
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
					LOGGER.log(Level.INFO,"Jots Request routed to /jots");

					switch (method) {
						case "GET":{
							LOGGER.log(Level.INFO,"Jots GET request");
						
							break;
						}
						case "POST":{
							LOGGER.log(Level.INFO,"Jots POST request");
							createKeepNote(request,response);
							break;
						}
						case "UPDATE":{
							LOGGER.log(Level.INFO,"Jots UPDATE request");
						
							break;
						}
						case "DELETE":{
							LOGGER.log(Level.INFO,"Jots DELETE request");
						
							break;
						}
						default:{
							LOGGER.log(Level.INFO,"Jots UNKNOWN request");
							errorMessage(response,null, "invalid request method","You seem to have used invalid method,we only support GET/POST/UPDATE/DELETE", 404);
							break;
						}
					}	
					break;
				}
				default: {
					LOGGER.log(Level.SEVERE,"Jots Request routed to Default as invalid URL");
					response.setContentType("application/json");
					response.setStatus(404);
					response.getWriter().write(responseData.toString());
					errorMessage(response,null, "invalud URL requested","You seem to have entered invalid url please try again with valid url", 404);
				}
			}
		}
		catch(Exception e) {
			errorMessage(response, e, "Exception at server","Sorry we cannot handle your data now!, we had a exception.", 500);
		}
	}


	public void createKeepNote(HttpServletRequest request, HttpServletResponse response){
		LOGGER.log(Level.INFO,"Inside Jot Creation Block");
			try {
				
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE,"Exception in JotServerApp",e);
				errorMessage(response, e,"Unable to create Jot","Creation of Jot Failed due to an exception", 500);
			}
	}


	private void errorMessage(HttpServletResponse response, Exception e, String errorTitle, String errorMessage, int errorCode) {
		try{
		responseData.put("data", new JSONObject() {
			{
			put("code",errorCode);
			put("error_title",errorTitle);
			put("error_message", errorMessage);
		}
		});

		LOGGER.log(Level.SEVERE,"Exception in JotServerApp",e);
		response.setContentType("application/json");
		response.setStatus(errorCode);
		response.getWriter().write(responseData.toString());
	}catch(Throwable t){
		LOGGER.log(Level.SEVERE,"Exception throwing error in JotServerApp",e);
	}
	}

}