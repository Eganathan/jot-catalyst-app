import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data Transfer Objects for Jot operations
 * Provides type-safe data structures for request/response handling
 */
public class JotDTO {

    /**
     * DTO for Jot data structure
     * Represents a single jot with all its properties
     */
    public static class Jot {
        private String id;
        private String title;
        private String note;
        private String createdTime;
        private String modifiedTime;

        // Default constructor
        public Jot() {
        }

        /**
         * Constructor with all fields
         *
         * @param id           Unique identifier for the jot
         * @param title        Title of the jot
         * @param note         Content of the jot
         * @param createdTime  Creation timestamp
         * @param modifiedTime Last modification timestamp
         */
        public Jot(String id, String title, String note, String createdTime, String modifiedTime) {
            this.id = id;
            this.title = title;
            this.note = note;
            this.createdTime = createdTime;
            this.modifiedTime = modifiedTime;
        }

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }

        public String getModifiedTime() {
            return modifiedTime;
        }

        public void setModifiedTime(String modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        /**
         * Converts Jot object to JSON representation
         *
         * @return JSONObject representation of the jot
         */
        public JSONObject toJSON() throws JSONException {
            JSONObject jotJson = new JSONObject();
            jotJson.put(AppConstants.KEY_ID, id);
            jotJson.put(AppConstants.KEY_TITLE, title);
            jotJson.put(AppConstants.KEY_DESCRIPTION, note);
            jotJson.put(AppConstants.KEY_CREATED_TIME, createdTime);
            jotJson.put(AppConstants.KEY_MODIFIED_TIME, modifiedTime);
            return jotJson;
        }
    }

    /**
     * DTO for Jot creation/update requests
     * Contains only the editable fields for jot operations
     */
    public static class JotRequest {
        private String title;
        private String note;

        // Default constructor
        public JotRequest() {
        }

        /**
         * Constructor with title and note
         *
         * @param title Title of the jot
         * @param note  Content of the jot
         */
        public JotRequest(String title, String note) {
            this.title = title;
            this.note = note;
        }

        // Getters and setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        /**
         * Validates the request data
         *
         * @throws JotException if validation fails
         */
        public void validate() throws JotException {
            try {
                this.title = ValidationUtils.validateTitle(this.title);
                this.note = ValidationUtils.validateNote(this.note);
            } catch (IllegalArgumentException e) {
                throw JotException.validationError(e.getMessage());
            }
        }
    }

    /**
     * DTO for pagination information
     * Contains pagination metadata for bulk operations
     */
    public static class PagingInfo {
        private int page;
        private int perPage;
        private boolean hasMore;

        /**
         * Constructor with pagination details
         *
         * @param page    Current page number
         * @param perPage Number of items per page
         * @param hasMore Whether there are more pages available
         */
        public PagingInfo(int page, int perPage, boolean hasMore) {
            this.page = page;
            this.perPage = perPage;
            this.hasMore = hasMore;
        }

        // Getters
        public int getPage() {
            return page;
        }

        public int getPerPage() {
            return perPage;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        /**
         * Converts PagingInfo to JSON representation
         *
         * @return JSONObject representation of paging info
         */
        public JSONObject toJSON() throws JSONException {
            JSONObject pagingJson = new JSONObject();
            pagingJson.put(AppConstants.KEY_PAGE, page);
            pagingJson.put(AppConstants.KEY_PER_PAGE, perPage);
            pagingJson.put(AppConstants.KEY_HAS_MORE, hasMore);
            return pagingJson;
        }
    }

    /**
     * DTO for API response structure
     * Standardizes all API responses with consistent format
     */
    public static class ApiResponse {
        private final JSONObject data;
        private final String status;

        /**
         * Constructor for API response
         *
         * @param data   Response data
         * @param status Response status (success/failure)
         */
        public ApiResponse(JSONObject data, String status) {
            this.data = data;
            this.status = status;
        }

        /**
         * Creates success response
         *
         * @param data Response data
         * @return ApiResponse for success
         */
        public static ApiResponse success(JSONObject data) {
            return new ApiResponse(data, AppConstants.STATUS_SUCCESS);
        }

        /**
         * Creates error response
         *
         * @param title       Error title
         * @param description Error description
         * @return ApiResponse for error
         */
        public static ApiResponse error(String title, String description) throws JSONException {
            JSONObject errorData = new JSONObject();
            JSONObject error = new JSONObject();
            error.put(AppConstants.KEY_TITLE, title);
            error.put(AppConstants.KEY_DESCRIPTION, description);
            errorData.put(AppConstants.KEY_ERROR, error);

            return new ApiResponse(errorData, AppConstants.STATUS_FAILURE);
        }

        /**
         * Converts ApiResponse to JSON representation
         *
         * @return JSONObject representation of the response
         */
        public JSONObject toJSON() throws JSONException {
            JSONObject response = new JSONObject();
            response.put(AppConstants.KEY_DATA, data);
            response.put(AppConstants.KEY_STATUS, status);
            return response;
        }
    }
}