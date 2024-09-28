import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RESTAppService {

    private static final Logger LOGGER = Logger.getLogger(RESTAppService.class.getName());

    private HttpServletRequest request;
    private HttpServletResponse response;

    // Route Pattern
    private static final Pattern JOT_BULK_PATTERN = Pattern.compile("/jots");
    private static final Pattern JOT_SINGLE_PATTERN = Pattern.compile("^/jots/\\d{17}$");
    private static final Pattern TESTING_PATTERN = Pattern.compile("/testing");

    private static Map<Pattern, Class<? extends Runner>> routesMap = addRoutes();


    public RESTAppService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.request = request;
        this.response = response;
        navigate(request,response);
        LOGGER.log(Level.INFO,"Init completed");
    }

    private static Map<Pattern, Class<? extends Runner>> addRoutes() {
        routesMap = new HashMap<>();
        routesMap.put(JOT_BULK_PATTERN,  JotBulkServices.class);
        routesMap.put(JOT_SINGLE_PATTERN,  JotSingleServices.class);
        routesMap.put(TESTING_PATTERN,  TestingServices.class);
        return routesMap;
    }

    private void navigate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uri = request.getRequestURI();
        LOGGER.log(Level.INFO,"navigate: uri invoked");
        boolean matchFound = false;
        for (Pattern pattern : routesMap.keySet()) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                //creating the instance for routing
                //handle exceptions
                var classDes = routesMap.get(pattern);
                LOGGER.log(Level.INFO,"navigate: uri matched:"+classDes.getSimpleName());
                classDes.getDeclaredConstructor().newInstance().createAndProcess(request,response);
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            // Handle 404 or other error scenario
            LOGGER.log(Level.INFO,"navigate: uri not matched"+uri);
            ResponseGenerator.initiateErrorMessage(response, 500, "Invalid URL", "Please ensure you are requesting a valid url and providing valid parameters");
        }
    }
}