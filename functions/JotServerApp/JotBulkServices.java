
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JotBulkServices extends BulkBaseService<JotBulkServices> {

    private HttpServletRequest _request;
    private HttpServletResponse _response;


    @Override
    public JotBulkServices create(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        return this;
    }

    @Override
    public void process() {
    }
}


