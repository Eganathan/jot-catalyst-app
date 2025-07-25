/**
 * Custom exception class for Jot application specific errors
 * Provides structured error handling with HTTP status codes and user-friendly messages
 */
public class JotException extends Exception {
    
    private final int httpStatusCode;
    private final String userMessage;
    private final String developerMessage;
    
    /**
     * Constructor for JotException with all details
     * @param httpStatusCode HTTP status code for the error
     * @param userMessage User-friendly error message
     * @param developerMessage Technical error message for developers
     * @param cause Root cause exception
     */
    public JotException(int httpStatusCode, String userMessage, String developerMessage, Throwable cause) {
        super(developerMessage, cause);
        this.httpStatusCode = httpStatusCode;
        this.userMessage = userMessage;
        this.developerMessage = developerMessage;
    }
    
    /**
     * Constructor for JotException without root cause
     * @param httpStatusCode HTTP status code for the error
     * @param userMessage User-friendly error message
     * @param developerMessage Technical error message for developers
     */
    public JotException(int httpStatusCode, String userMessage, String developerMessage) {
        this(httpStatusCode, userMessage, developerMessage, null);
    }
    
    /**
     * Constructor for validation errors
     * @param userMessage User-friendly validation error message
     */
    public JotException(String userMessage) {
        this(AppConstants.HTTP_BAD_REQUEST, userMessage, userMessage, null);
    }
    
    /**
     * Gets the HTTP status code for this exception
     * @return HTTP status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
    
    /**
     * Gets the user-friendly error message
     * @return User-friendly error message
     */
    public String getUserMessage() {
        return userMessage;
    }
    
    /**
     * Gets the technical error message for developers
     * @return Technical error message
     */
    public String getDeveloperMessage() {
        return developerMessage;
    }
    
    /**
     * Factory method for creating validation exceptions
     * @param message Validation error message
     * @return JotException for validation errors
     */
    public static JotException validationError(String message) {
        return new JotException(AppConstants.HTTP_BAD_REQUEST, message, "Validation failed: " + message);
    }
    
    /**
     * Factory method for creating not found exceptions
     * @param resource Name of the resource not found
     * @return JotException for not found errors
     */
    public static JotException notFound(String resource) {
        String message = resource + " not found";
        return new JotException(AppConstants.HTTP_NOT_FOUND, message, message);
    }
    
    /**
     * Factory method for creating internal server exceptions
     * @param message Error message
     * @param cause Root cause exception
     * @return JotException for internal server errors
     */
    public static JotException internalServerError(String message, Throwable cause) {
        return new JotException(
            AppConstants.HTTP_INTERNAL_ERROR,
            "An internal error occurred. Please try again later.",
            message,
            cause
        );
    }
    
    /**
     * Factory method for creating database exceptions
     * @param operation Database operation that failed
     * @param cause Root cause exception
     * @return JotException for database errors
     */
    public static JotException databaseError(String operation, Throwable cause) {
        return new JotException(
            AppConstants.HTTP_INTERNAL_ERROR,
            "Database operation failed. Please try again later.",
            "Database error during " + operation + ": " + cause.getMessage(),
            cause
        );
    }
}