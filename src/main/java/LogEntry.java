import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime timestamp;
    private final HttpMethod method;
    private final String requestPath;
    private final int responseCode;
    private final int contentSizeBytes;
    private final String referrer;
    private final UserAgent userAgent;


    public LogEntry(String logString) {
        String[] parts = logString.split("\\s+"); // Разделим строку по пробелам

        this.ipAddress = parts[0]; // IP адрес
        this.timestamp = parseTimestamp(parts[3]); // Время без квадратной скобки слева
        this.method = HttpMethod.valueOf(parts[5].replaceAll("\"", "")); // Метод запроса без кавычек
        this.requestPath = parts[6]; // Путь запроса
        this.responseCode = Integer.parseInt(parts[8]); // Код статуса
        this.contentSizeBytes = Integer.parseInt(parts[9]); // Размер отправленных данных
        this.referrer = parts[10].equals("-") ? "" : parts[10]; // Реферер
        this.userAgent = new UserAgent(parts[11].replaceAll("\"", "")); // User-Agent без кавычек
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

}
