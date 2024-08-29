import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;


// import org.json.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.catalyst.advanced.CatalystAdvancedIOHandler;
import com.zc.component.object.ZCObject;
import com.zc.component.object.ZCRowObject;
import com.zc.component.zcql.ZCQL;


public class JotServerApp implements CatalystAdvancedIOHandler {
    private static final Logger LOGGER = Logger.getLogger(JotServerApp.class.getName());
    private JSONObject responseData = new JSONObject();

    @Override
    public void runner(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {

            String method = request.getMethod();
            LOGGER.log(Level.INFO, "Jots Request Recived");

            Pattern jotPattern = Pattern.compile("^/jots/\\d{17}$");
            Matcher matcher = jotPattern.matcher(request.getRequestURI());

//            responseData.put("requestedUrl", request.getRequestURI());

            if (matcher.matches()) {
                getJotNote(request, response);
            } else
                switch (request.getRequestURI()) {
                    case "/jots/test": {
                        LOGGER.log(Level.INFO, "Jots Request routed to /jots");
                        responseData.put("data", new JSONObject() {
                            {
                                put("success", "jots/test is working");
                                put("error_message", "Successfully test completed, jots/test is working as expected");
                            }
                        });
                        response.setContentType("application/json");
                        response.setStatus(200);
                        response.getWriter().write(responseData.toString());
                        break;
                    }
                    case "/jots": {
                        LOGGER.log(Level.INFO, "Jots Request routed to /jots");

                        switch (method) {
                            case "GET": {
                                getBulkKeeps(request, response);
                                break;
                            }
                            case "POST": {
                                LOGGER.log(Level.INFO, "Jots POST request");
                                createKeepNote(request, response);
                                break;
                            }
                            case "PUT": {
                                LOGGER.log(Level.INFO, "Jots PUT request");
                                updateJotNote(request, response);
                                break;
                            }
                            case "DELETE": {
                                LOGGER.log(Level.INFO, "Jots DELETE request");
                                deleteJotItem(request, response);
                                break;
                            }
                            default: {
                                LOGGER.log(Level.INFO, "Jots UNKNOWN request");
                                errorMessage(response, null, "invalid request method", "You seem to have used invalid method,we only support GET/POST/PUT/DELETE", 404);
                                break;
                            }
                        }
                        break;
                    }
                    default: {
                        LOGGER.log(Level.SEVERE, "Jots Request routed to Default as invalid URL");
                        response.setContentType("application/json");
                        response.setStatus(404);
                        response.getWriter().write(responseData.toString());
                        errorMessage(response, null, "invalud URL requested", "You seem to have entered invalid url please try again with valid url", 404);
                    }
                }
        } catch (Exception e) {
            errorMessage(response, e, "Exception at server", "Sorry we cannot handle your data now!, we had a exception.", 500);
        }
    }


    @SuppressWarnings("unchecked")
    public void createKeepNote(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.log(Level.INFO, "Inside Jot Creation Block");
        try {

            JSONParser jsonParser = new JSONParser();
            ServletInputStream requestBody = request.getInputStream();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(requestBody, "UTF-8"));
            JSONObject jot = (JSONObject) jsonObject.get("jot");

            ZCRowObject row = ZCRowObject.getInstance();
            row.set("title", jot.get("title"));
            row.set("message", jot.get("message"));

            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").insertRow(row);


            responseData.put("status", "success");
            responseData.put("data", new JSONObject() {
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
            });
            response.setContentType("application/json");
            response.setStatus(201);
            response.getWriter().write(responseData.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in JotServerApp", e);
            errorMessage(response, e, "Unable to create Jot", "Creation of Jot Failed due to an exception" + e.getLocalizedMessage(), 500);
        }
    }

    @SuppressWarnings("unchecked")
    public void getBulkKeeps(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.log(Level.INFO, "Inside GET BULK JOTS Block");
        try {

            Integer totalEntries = Integer.parseInt(ZCQL.getInstance().executeQuery("SELECT COUNT(ROWID) FROM notes").get(0).get("notes", "ROWID").toString());

            if (totalEntries <= 0) {
                errorMessage(response, null, "Empty Comntent", "There are no Jots available,start creating jots", 204);
                return;
            }


            Integer page = Integer.parseInt(request.getParameter("page"));
            Integer perPage = Integer.parseInt(request.getParameter("per_page"));
            Boolean hasMore = totalEntries > page * perPage;


            LOGGER.log(Level.INFO, "Inside GET BULK: request: page" + page + " per_page=" + perPage + " availableEntries=" + totalEntries + "hasMore=" + hasMore);

            ArrayList<Object> jotItems = new ArrayList<>();

            //need to add pager but will do it later ;)
            ZCQL.getInstance().executeQuery(String.format("SELECT ROWID, title, message, CREATEDTIME, MODIFIEDTIME FROM notes")).forEach(
                    row -> {
                        jotItems.add(new HashMap() {
                            {
                                put("id", row.get("notes", "ROWID").toString());
                                put("created_time", row.get("notes", "CREATEDTIME").toString());
                                put("modified_time", row.get("notes", "MODIFIEDTIME").toString());
                                put("title", row.get("notes", "title").toString());
                                put("note", row.get("notes", "message").toString());
                            }
                        });
                    });

            responseData.put("paging_info", new JSONObject() {
                {
                    put("page", page);
                    put("has_more", hasMore);
                }
            });
            responseData.put("jots", jotItems);
            responseData.put("status", "success");


            response.setContentType("application/json");
            response.setStatus(200);
            response.getWriter().write(responseData.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in JotServerApp", e);
            errorMessage(response, e, "Unable to Fetch Jots", "Fetching Jot Failed (did you provide page and per_page as a query parameter) with an exception :" + e.getLocalizedMessage(), 500);
        }
    }


    @SuppressWarnings("unchecked")
    public void updateJotNote(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.log(Level.INFO, "Inside Jot Update Block");
        try {

            JSONParser jsonParser = new JSONParser();
            ServletInputStream requestBody = request.getInputStream();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(requestBody, "UTF-8"));
            JSONObject jot = (JSONObject) jsonObject.get("jot");

            Long jotId = Long.parseLong(jot.get("id").toString());

            List<ZCRowObject> rows = new ArrayList<ZCRowObject>();

            ZCRowObject row = ZCRowObject.getInstance();
            // row.setRowObject(jot);
            // row.setRowObject(new JSONObject(){
            // 	{
            // 	put("ROWID",jot.get("id"));
            // 	put("title",jot.get("title"));
            // 	put("message",jot.get("message"));
            // 	}
            // });

            row.set("ROWID", jot.get("id"));
            row.set("title", jot.get("title"));
            row.set("message", jot.get("note"));

            rows.add(row);

            ZCObject.getInstance().getTable("notes").updateRows(rows);
            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").getRow(jotId);

            responseData.put("status", "success");
            responseData.put("data", new JSONObject() {
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
            });
            response.setContentType("application/json");
            response.setStatus(202);
            response.getWriter().write(responseData.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in JotServerApp", e);
            errorMessage(response, e, "Unable to update Jot", "Updation of Jot Failed due to an exception" + e.getLocalizedMessage(), 500);
        }
    }

    @SuppressWarnings("unchecked")
    public void getJotNote(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.log(Level.INFO, "Inside Jot Update Block");
        try {

            Pattern pattern = Pattern.compile("\\d{17}$");
            Matcher matcher = pattern.matcher(request.getRequestURI());
            if (!matcher.find())
                throw new IllegalArgumentException("No ID Found ");

            String extractedNumber = matcher.group();

            Long jotId = Long.parseLong(extractedNumber);
            ZCRowObject jotItem = ZCObject.getInstance().getTable("notes").getRow(jotId);

            responseData.put("status", "success");
            responseData.put("data", new JSONObject() {
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
            });
            response.setContentType("application/json");
            response.setStatus(200);
            response.getWriter().write(responseData.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in JotServerApp", e);
            errorMessage(response, e, "Unable to GET Jot", "GET Jot Failed due to an exception" + e.getLocalizedMessage(), 500);
        }
    }


    public void deleteJotItem(HttpServletRequest request, HttpServletResponse response) {
        try {
            Long jotIdForDeletion = Long.parseLong(request.getParameter("id"));
            ZCObject.getInstance().getTable("notes").deleteRow(jotIdForDeletion);

            responseData.put("status", "success");

            responseData.put("data", new JSONObject() {
                {
                    put("title", "Jot Deleted Successfully");
                    put("message", "Jot Deleted Successfully");
                }
            });

            response.setContentType("application/json");
            response.setStatus(200);
            response.getWriter().write(responseData.toJSONString());
        } catch (Exception e) {
            errorMessage(response, e, "Deletion Failed", "The Jot deletion failed please check your input", 500);
        }
    }


    private void errorMessage(HttpServletResponse response, Exception e, String errorTitle, String errorMessage, int errorCode) {
        try {
            responseData.put("data", new JSONObject() {
                {
                    put("code", errorCode);
                    put("error_title", errorTitle);
                    put("error_message", errorMessage);
                }
            });

            LOGGER.log(Level.SEVERE, "Exception in JotServerApp", e);
            response.setContentType("application/json");
            response.setStatus(errorCode);
            response.getWriter().write(responseData.toJSONString());
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Exception throwing error in JotServerApp", e);
        }
    }
}