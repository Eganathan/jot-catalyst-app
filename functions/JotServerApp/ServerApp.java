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
    private static final Pattern JOTS_PATTERN = Pattern.compile("/jots");
    private static final Pattern TESTING_PATTERN = Pattern.compile("/testing");

    private Map<Pattern, Runner> routesMap;

    public ServerApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.request = request;
        this.response = response;

        routesMap = new HashMap<>();
        routesMap.put(JOTS_PATTERN, new JotServices().create(request, response));
        routesMap.put(TESTING_PATTERN, new TestingServices().create(request, response));

        navigate();
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