import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private String ipAddress;
    private LocalDateTime timestamp;
    private HttpMethod method;
    private String requestPath;
    private int responseCode;
    private int contentSizeBytes;
    private String referrer;
    private UserAgent userAgent;

    public LogEntry(String logString) {
        String[] parts = logString.split("\\s+");

        this.ipAddress = "unknown";
        this.timestamp = LocalDateTime.of(1970, 1, 1, 0, 0);
        this.method = HttpMethod.UNKNOWN;
        this.requestPath = "/";
        this.responseCode = 0;
        this.contentSizeBytes = 0;
        this.referrer = "";
        this.userAgent = new UserAgent("Other");

        if (parts.length > 0) {
            this.ipAddress = parts[0];
        }

        if (parts.length >= 3){
            this.timestamp = parseTimestamp(parts[3]);
        }

        if (parts.length >= 5) {
            this.method = HttpMethod.valueOf(parts[5].replaceAll("\"", ""));
        }

        if (parts.length >= 6) {
            this.requestPath = parts[6];
        }

        if (parts.length >= 8) {
            this.responseCode = Integer.parseInt(parts[8]);
        }

        if (parts.length >= 9) {
            this.contentSizeBytes = Integer.parseInt(parts[9]);
        }

        if (parts.length >= 10) {
            this.referrer = parts[10].equals("-") ? "" : parts[10];
        }

        String userAgentStr = extractUserAgent(parts);
        if (parts.length >= 11) {
            this.userAgent = new UserAgent(userAgentStr);
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getContentSizeBytes() {
        return contentSizeBytes;
    }

    public String getReferrer() {
        return referrer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    private LocalDateTime parseTimestamp(String dateStr) {
        if (dateStr.startsWith("[")) {
            dateStr = dateStr.substring(1);
        }
        if (dateStr.endsWith("]")) {
            dateStr = dateStr.substring(0, dateStr.length() - 1);
        }

        boolean hasTimeZone = dateStr.matches(".+\\s[+-]\\d{4}$");

        DateTimeFormatter formatter;
        if (hasTimeZone) {
            formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss ZZZZ", Locale.ENGLISH);
        } else {
            formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
        }

        return LocalDateTime.parse(dateStr, formatter);
    }

    private String extractUserAgent(String[] parts) {
        if (parts == null || parts.length <= 11) {
            return "Other";
        }

        StringBuilder sb = new StringBuilder();
        boolean isOpen = false;

        for (int i = 11; i < Math.min(parts.length, 31); i++) {
            String part = parts[i];

            if (part == null) continue;

            if (part.startsWith("\"")) {
                isOpen = true;
                part = part.substring(1);
            }

            if (part.endsWith("\"")) {
                part = part.substring(0, part.length() - 1);
                isOpen = false;
            }

            sb.append(part).append(" ");

            if (!isOpen) {
                break;
            }
        }

        String result = sb.toString().trim();
        return result.isEmpty() ? "Other" : result;
    }
}