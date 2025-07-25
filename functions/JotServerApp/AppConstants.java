/**
 * Application constants for the Jot Server App
 * Contains all magic numbers, strings, and configuration values used throughout the application
 */
public class AppConstants {

    // Database table names
    public static final String NOTES_TABLE = "notes";

    // Database column names
    public static final String COL_ROWID = "ROWID";
    public static final String COL_TITLE = "title";
    public static final String COL_NOTE = "note";
    public static final String COL_CREATED_TIME = "CREATEDTIME";
    public static final String COL_MODIFIED_TIME = "MODIFIEDTIME";

    // Regular expression patterns
    public static final String ID_PATTERN = "\\d{17}$";
    public static final String SINGLE_JOT_PATH_PATTERN = "^/jots/\\d{17}$";
    public static final String BULK_JOTS_PATH = "/jots";
    public static final String TESTING_PATH = "/testing";

    // Default pagination values
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PER_PAGE = 10;

    // HTTP status codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_ERROR = 500;

    // Response status strings
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";

    // JSON response keys
    public static final String KEY_DATA = "data";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_JOT = "jot";
    public static final String KEY_JOTS = "jots";
    public static final String KEY_ID = "id";
    public static final String KEY_CREATED_TIME = "created_time";
    public static final String KEY_MODIFIED_TIME = "modified_time";
    public static final String KEY_PAGING_INFO = "paging_info";
    public static final String KEY_PAGE = "page";
    public static final String KEY_PER_PAGE = "per_page";
    public static final String KEY_HAS_MORE = "has_more";

    // Error messages
    public static final String ERROR_INVALID_METHOD = "Invalid HTTP method";
    public static final String ERROR_INVALID_URL = "Invalid URL";
    public static final String ERROR_EMPTY_CONTENT = "No content available";
    public static final String ERROR_UNABLE_TO_FETCH = "Unable to fetch jots";
    public static final String ERROR_UNABLE_TO_CREATE = "Unable to create jot";
    public static final String ERROR_UNABLE_TO_UPDATE = "Unable to update jot";
    public static final String ERROR_UNABLE_TO_DELETE = "Unable to delete jot";
    public static final String ERROR_UNABLE_TO_GET = "Unable to get jot";
    public static final String ERROR_REQUEST_FAILED = "Request failed due to an exception";
    public static final String ERROR_VALIDATION_FAILED = "Validation failed";
    public static final String ERROR_JOT_NOT_FOUND = "Jot not found";
    public static final String ERROR_INVALID_JOT_ID = "Invalid Jot ID";

    // Success messages
    public static final String SUCCESS_DELETED = "Jot deleted successfully";
    public static final String SUCCESS_CREATED = "Jot created successfully";
    public static final String SUCCESS_UPDATED = "Jot updated successfully";

    // Content type
    public static final String CONTENT_TYPE_JSON = "application/json";

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }
}