import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

abstract class BulkBaseService<T> extends BaseService<T> {


    public static JSONObject bulkResponseGenerator(String key, ArrayList<JSONObject> bulkData, Integer currentPage, Integer perPage, Boolean hasMore) {
        return new JSONObject() {{
            put(key, bulkData);
            put("paging_info", getPagingInfo(currentPage, perPage, hasMore));
        }};
    }

    private static JSONObject getPagingInfo(Integer currentPage, Integer perPage, Boolean hasMore) {
        JSONObject responseData = new JSONObject();
        responseData.put("paging_info", new JSONObject() {
            {
                put("page", currentPage);
                put("has_more", hasMore);
                put("per_page", perPage);
            }
        });
        return responseData;
    }

}