import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class ServerApp {

    private HttpServletRequest request;
    private HttpServletResponse response;

    // Pre-compile patterns
    private static final Pattern JOT_BULK_PATTERN = Pattern.compile("/jots");
    private static final Pattern JOT_SINGLE_PATTERN = Pattern.compile("^/jots/\\d{15}$");
    private static final Pattern TESTING_PATTERN = Pattern.compile("/testing");

    private Map<Pattern, Runner> routesMap;

    public ServerApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.request = request;
        this.response = response;

        addRoutes();
        navigate();
    }

    private void addRoutes() {
        routesMap = new HashMap<>();
        routesMap.put(JOT_BULK_PATTERN, new JotBulkServices().create(request, response));
        routesMap.put(JOT_SINGLE_PATTERN, new JotSingleServices().create(request, response));
        routesMap.put(TESTING_PATTERN, new TestingServices().create(request, response));
    }

    private void navigate() throws Exception {
        String uri = request.getRequestURI();

        boolean matchFound = false;
        for (Pattern pattern : routesMap.keySet()) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                routesMap.get(pattern).process();
                matchFound = true;
                break;
            }
        }

        if (!matchFound) {
            // Handle 404 or other error scenario
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // Example: send 404
        }
    }
}