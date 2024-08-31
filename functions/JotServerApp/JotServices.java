import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

public class JotServices implements Runner<JotServices> {

    private HttpServletRequest _request;
    private HttpServletResponse _response;


    @Override
    public JotServices create(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        return this;
    }

    @Override
    public void process() {

    }
}


