import org.json.simple.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class ResponseGenerator {

    public static JSONObject responseGenerator(String key, JSONObject object) {
        JSONObject responseData = new JSONObject();

        responseData.put("data", new JSONObject() {{
            put(key, object);
        }});

        responseData.put("status", "Success");

        return responseData;
    }

    public static JSONObject responseBulkGenerator(String key, ArrayList<JSONObject> bulkData, Integer currentPage, Integer perPage, Boolean hasMore) {
        JSONObject responseData = new JSONObject();

        responseData.put("status", "Success");
        responseData.put("data", new JSONObject() {{
            put(key, bulkData);
            put("paging_info", getPagingInfo(currentPage, perPage, hasMore));
        }});
        return responseData;
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