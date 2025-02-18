
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class JotBulkServices extends BulkBaseService<JotBulkServices> {
    private static final Logger LOGGER = Logger.getLogger(JotBulkServices.class.getName());
    private HttpServletRequest _request;
    private HttpServletResponse _response;


    @Override
    public JotBulkServices create(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        LOGGER.log(Level.INFO, "create completed");
        return this;
    }

    @Override
    public void process() throws IOException {
        LOGGER.log(Level.INFO, "process initiated");
        METHOD method = METHOD.valueOf(_request.getMethod().toUpperCase());
        LOGGER.log(Level.INFO, "create method: " + method);
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
        LOGGER.log(Level.INFO, "create and process invoked");
        create(request, response).process();
    }

    //Implementation
    private void getBulkJots() throws IOException {
        try {
            LOGGER.log(Level.INFO, "GET BULK Started");
            int totalEntries = Integer.parseInt(ZCQL.getInstance().executeQuery("SELECT COUNT(ROWID) FROM notes").get(0).get("notes", "ROWID").toString());

            if (totalEntries <= 0) {
                LOGGER.log(Level.INFO, "GET BULK :empty");
                initiateErrorMessage(_response, 204, "Empty Content", "There are no Jots available,start creating jots");
                return;
            }

            Integer page = Integer.parseInt(_request.getParameter("page"));
            Integer perPage = Integer.parseInt(_request.getParameter("per_page"));

            Boolean hasMore = totalEntries > page * perPage;


            ArrayList<JSONObject> jotItems = new ArrayList<>();
            ZCQL.getInstance().executeQuery(String.format("SELECT ROWID, title, note, CREATEDTIME, MODIFIEDTIME FROM notes ORDER BY notes.MODIFIEDTIME DESC LIMIT %d, %d",((page - 1) * perPage), perPage)).forEach(
                    row -> {
                        jotItems.add(new JSONObject() {
                            {
                                put("id", row.get("notes", "ROWID").toString());
                                put("created_time", row.get("notes", "CREATEDTIME").toString());
                                put("modified_time", row.get("notes", "MODIFIEDTIME").toString());
                                put("title", row.get("notes", "title").toString());
                                put("note", row.get("notes", "note").toString());
                            }
                        });
                    });

            JSONObject bulkResponseJson = bulkResponseGenerator("jots", jotItems, page, perPage, hasMore);
            initiateSuccessMessage(_response, 200, bulkResponseJson);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "GET BULK :exception" + e.getLocalizedMessage());
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
            row.set("note", jot.get("note"));

            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").insertRow(row);

            JSONObject createdJot = new JSONObject() {
                {
                    put("jot", new JSONObject() {
                        {
                            put("id", jotItem.get("ROWID").toString());
                            put("title", jotItem.get("title").toString());
                            put("note", jotItem.get("note").toString());
                            put("created_time", jotItem.get("CREATEDTIME").toString());
                            put("modified_time", jotItem.get("MODIFIEDTIME").toString());
                        }
                    });
                }
            };

            initiateSuccessMessage(_response, 201, createdJot);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"Jot Creation Exception: "+e.getLocalizedMessage());
            initiateErrorMessage(_response, 500, "Unable to create Jot", "Creation of Jot Failed due to an exception " + e.getLocalizedMessage());
        }
    }
}


