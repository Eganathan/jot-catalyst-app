import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

/**
 * Utility class for input validation and sanitization
 * Provides secure methods to validate request parameters and prevent injection attacks
 */
public class ValidationUtils {
    private static final Logger LOGGER = Logger.getLogger(ValidationUtils.class.getName());
    
    // Constants for validation limits
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PER_PAGE = 50;
    private static final int MIN_PER_PAGE = 1;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_NOTE_LENGTH = 10000;
    private static final int MAX_REQUEST_SIZE = 1024 * 1024; // 1MB
    
    /**
     * Validates and sanitizes page parameter from request
     * @param request HTTP request containing page parameter
     * @param defaultValue default value if parameter is missing or invalid
     * @return validated page number
     * @throws IllegalArgumentException if page parameter is invalid
     */
    public static int validatePageParameter(HttpServletRequest request, int defaultValue) 
            throws IllegalArgumentException {
        String pageParam = request.getParameter("page");
        
        if (pageParam == null || pageParam.trim().isEmpty()) {
            return defaultValue;
        }
        
        try {
            int page = Integer.parseInt(pageParam.trim());
            if (page < MIN_PAGE_SIZE || page > MAX_PAGE_SIZE) {
                throw new IllegalArgumentException(
                    String.format("Page must be between %d and %d", MIN_PAGE_SIZE, MAX_PAGE_SIZE));
            }
            return page;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid page parameter: " + pageParam);
            throw new IllegalArgumentException("Page parameter must be a valid integer");
        }
    }
    
    /**
     * Validates and sanitizes per_page parameter from request
     * @param request HTTP request containing per_page parameter
     * @param defaultValue default value if parameter is missing or invalid
     * @return validated per_page number
     * @throws IllegalArgumentException if per_page parameter is invalid
     */
    public static int validatePerPageParameter(HttpServletRequest request, int defaultValue) 
            throws IllegalArgumentException {
        String perPageParam = request.getParameter("per_page");
        
        if (perPageParam == null || perPageParam.trim().isEmpty()) {
            return defaultValue;
        }
        
        try {
            int perPage = Integer.parseInt(perPageParam.trim());
            if (perPage < MIN_PER_PAGE || perPage > MAX_PER_PAGE) {
                throw new IllegalArgumentException(
                    String.format("Per page must be between %d and %d", MIN_PER_PAGE, MAX_PER_PAGE));
            }
            return perPage;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid per_page parameter: " + perPageParam);
            throw new IllegalArgumentException("Per page parameter must be a valid integer");
        }
    }
    
    /**
     * Validates text content for title field
     * @param title title string to validate
     * @return sanitized title
     * @throws IllegalArgumentException if title is invalid
     */
    public static String validateTitle(String title) throws IllegalArgumentException {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        
        String trimmedTitle = title.trim();
        if (trimmedTitle.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Title cannot exceed %d characters", MAX_TITLE_LENGTH));
        }
        
        // Basic sanitization - remove potential harmful characters
        return trimmedTitle.replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Validates text content for note field
     * @param note note string to validate
     * @return sanitized note
     * @throws IllegalArgumentException if note is invalid
     */
    public static String validateNote(String note) throws IllegalArgumentException {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null");
        }
        
        String trimmedNote = note.trim();
        if (trimmedNote.isEmpty()) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        
        if (trimmedNote.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Note cannot exceed %d characters", MAX_NOTE_LENGTH));
        }
        
        // Basic sanitization - remove potential harmful characters
        return trimmedNote.replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Validates ID from URI path
     * @param id ID to validate
     * @return validated ID
     * @throws IllegalArgumentException if ID is invalid
     */
    public static Long validateId(Long id) throws IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be a positive number");
        }
        return id;
    }
    
    /**
     * Validates request content length to prevent oversized requests
     * @param request HTTP request to validate
     * @throws IllegalArgumentException if request is too large
     */
    public static void validateRequestSize(HttpServletRequest request) throws IllegalArgumentException {
        int contentLength = request.getContentLength();
        if (contentLength > MAX_REQUEST_SIZE) {
            throw new IllegalArgumentException(
                String.format("Request size cannot exceed %d bytes", MAX_REQUEST_SIZE));
        }
    }
    
    /**
     * Sanitizes string for safe database storage
     * @param input input string to sanitize
     * @return sanitized string
     */
    public static String sanitizeForDatabase(String input) {
        if (input == null) {
            return null;
        }
        // Remove SQL injection patterns and escape quotes
        return input.replaceAll("['\"\\\\;]", "").trim();
    }
}