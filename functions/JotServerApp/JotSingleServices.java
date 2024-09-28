import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class JotSingleServices extends SingleBaseService<JotSingleServices> {

    private HttpServletRequest _request;
    private HttpServletResponse _response;


    @Override
    public JotSingleServices create(HttpServletRequest request, HttpServletResponse response) {
        _request = request;
        _response = response;
        return this;
    }

    @Override
    public void process() throws IOException {

        METHOD method = METHOD.valueOf(_request.getMethod().toUpperCase(Locale.ROOT));

        switch (method) {
            case GET -> getSingleJot();
            case PUT -> updateJot();
            case DELETE -> deleteJotItem();
            default -> {
                // public void initiateErrorMessage(HttpServletResponse response, Integer responseCode, String errorTitle, String errorMessage)
                initiateErrorMessage(_response, 500, "invalid Method", "Invalid Method");
            }
        }
    }

    @Override
    public void createAndProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        create(request,response).process();
    }

    //Implementations

    private void getSingleJot() throws IOException {
        try {
            Long jotId = extractID(_request.getRequestURI());
            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").getRow(jotId);

            JSONObject jotForResponse = new JSONObject() {
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

            initiateSuccessMessage(_response, 200, jotForResponse);
        } catch (Exception e) {
            initiateErrorMessage(_response, 500, "Unable to GET Jot", "GET Jot Failed due to an exception" + e.getLocalizedMessage());
        }
    }

    private void updateJot() throws IOException {
        try {
            Long jotID = extractID(_request.getRequestURI());

            JSONParser jsonParser = new JSONParser();
            ServletInputStream requestBody = _request.getInputStream();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(requestBody, StandardCharsets.UTF_8));
            JSONObject jot = (JSONObject) jsonObject.get("jot");

            List<ZCRowObject> rows = new ArrayList<ZCRowObject>();

            ZCRowObject row = ZCRowObject.getInstance();
            row.set("ROWID", jotID);
            row.set("title", jot.get("title"));
            row.set("message", jot.get("note"));

            rows.add(row);

            ZCObject.getInstance().getTable("notes").updateRows(rows);
            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").getRow(jotID);

            JSONObject updatedJot = new JSONObject() {
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
            initiateSuccessMessage(_response, 200, updatedJot);
        } catch (Exception e) {
            initiateErrorMessage(_response, 500, "Unable to update Jot", "Updation failed due to an exception" + e.getLocalizedMessage());
        }
    }


    public void deleteJotItem() throws IOException {
        try {
            Long jotIdForDeletion = extractID(_request.getRequestURI());
            ZCObject.getInstance().getTable("notes").deleteRow(jotIdForDeletion);

            JSONObject deletedMessage = new JSONObject() {
                {
                    put("message", "deleted successfully");
                }
            };
            initiateSuccessMessage(_response, 200, deletedMessage);
        } catch (Exception e) {
            initiateErrorMessage(_response, 500, "Unable to delete Jot", "Deletion failed due to an exception" + e.getLocalizedMessage());
        }
    }

}


