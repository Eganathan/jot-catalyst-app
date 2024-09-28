import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public interface Runner<T> {

    static final Logger LOGGER = Logger.getLogger("Runner");

    public T create(HttpServletRequest request, HttpServletResponse response);

    public void process() throws Exception;

    public void createAndProcess (HttpServletRequest request, HttpServletResponse response) throws Exception;

}