import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Runner<T> {

    public T create(HttpServletRequest request, HttpServletResponse response);

    public void process() throws Exception;

    public void createAndProcess (HttpServletRequest request, HttpServletResponse response) throws Exception;

}