
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.zcql.ZCQL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

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
    public void process() throws IOException {
        METHOD method = METHOD.valueOf(_request.getMethod().toUpperCase(Locale.ROOT));

        switch (method) {
            case GET -> getBulkJots();
            case POST -> createSingleJot();
            default -> {
                initiateErrorMessage(_response, 500, "invalid Method", "Invalid Method");
            }
        }
    }

    @Override
    public void createAndProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        create(request,response).process();
    }

    //Implementation
    private void getBulkJots() throws IOException {
        try {
            int totalEntries = Integer.parseInt(ZCQL.getInstance().executeQuery("SELECT COUNT(ROWID) FROM notes").get(0).get("notes", "ROWID").toString());

            if (totalEntries <= 0) {
                initiateErrorMessage(_response, 500, "Empty Comntent", "There are no Jots available,start creating jots");
                return;
            }

            Integer page = Integer.parseInt(_request.getParameter("page"));
            Integer perPage = Integer.parseInt(_request.getParameter("per_page"));

            Boolean hasMore = totalEntries > page * perPage;


            ArrayList<JSONObject> jotItems = new ArrayList<>();
            //need to add pager but will do it later ;)
            ZCQL.getInstance().executeQuery(String.format("SELECT ROWID, title, message, CREATEDTIME, MODIFIEDTIME FROM notes")).forEach(
                    row -> {
                        jotItems.add(new JSONObject() {
                            {
                                put("id", row.get("notes", "ROWID").toString());
                                put("created_time", row.get("notes", "CREATEDTIME").toString());
                                put("modified_time", row.get("notes", "MODIFIEDTIME").toString());
                                put("title", row.get("notes", "title").toString());
                                put("note", row.get("notes", "message").toString());
                            }
                        });
                    });

            JSONObject bulkResponseJson = bulkResponseGenerator("jots", jotItems, page, perPage, hasMore);
            initiateSuccessMessage(_response, 200, bulkResponseJson);
        } catch (Exception e) {
            initiateErrorMessage(_response, 500, "Unable to Fetch Jots", "Fetching Jot Failed (did you provide page and per_page as a query parameter) with an exception :" + e.getLocalizedMessage());
        }
    }


    private void createSingleJot() throws IOException {
        try {
            JSONParser jsonParser = new JSONParser();
            ServletInputStream requestBody = _request.getInputStream();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(requestBody, StandardCharsets.UTF_8));
            JSONObject jot = (JSONObject) jsonObject.get("jot");

            ZCRowObject row = ZCRowObject.getInstance();
            row.set("title", jot.get("title"));
            row.set("message", jot.get("message"));

            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").insertRow(row);

            JSONObject createdJot = new JSONObject() {
                {
                    put("jot", new JSONObject() {
                        {
                            put("id", jotItem.get("ROWID").toString());
                            put("title", jotItem.get("title").toString());
                            put("note", jotItem.get("message").toString());
                            put("created_time", jotItem.get("CREATEDTIME").toString());
                            put("modified_time", jotItem.get("MODIFIEDTIME").toString());
                        }
                    });
                }
            };

            initiateSuccessMessage(_response, 201, createdJot);
        } catch (Exception e) {
            initiateErrorMessage(_response, 500, "Unable to create Jot", "Creation of Jot Failed due to an exception" + e.getLocalizedMessage());
        }
    }
}


