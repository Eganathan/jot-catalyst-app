import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SingleBaseService<T> extends BaseService<T> {


    protected Long extractID(String uri) throws IllegalArgumentException, NumberFormatException {
        Pattern pattern = Pattern.compile("\\d{17}$");
        Matcher matcher = pattern.matcher(uri);
        if (!matcher.find())
            throw new IllegalArgumentException("No ID Found ");

        return Long.parseLong(matcher.group());
    }

}
