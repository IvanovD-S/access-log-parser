import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int totalTraffic = 0;

    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    private final Set<String> existingPages = new HashSet<>();
    private final Set<String> unexistingPages = new HashSet<>();

    private final Map<String, Integer> osFrequency = new HashMap<>();
    private final Map<String, Integer> browserFrequency = new HashMap<>();

    private int userVisitsCount = 0;
    private int errorRequestCount = 0;
    private final Set<String> uniqueUserIps = new HashSet<>();

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getContentSizeBytes();

        if (minTime == null) {
            minTime = entry.getTimestamp();

        } else if (entry.getTimestamp().isBefore(minTime)) {
            minTime = entry.getTimestamp();
        }

        if (maxTime == null) {
            maxTime = entry.getTimestamp();

        } else if (entry.getTimestamp().isAfter(maxTime)) {
            maxTime = entry.getTimestamp();
        }

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getRequestPath());
        }

        if (entry.getResponseCode() == 404) {
            unexistingPages.add(entry.getRequestPath());
        }

        if (entry.getResponseCode() > 399 && entry.getResponseCode() < 600) {
            errorRequestCount++;
        }

        UserAgent userAgent = entry.getUserAgent();
        boolean isBot = userAgent.isBot();

        if (!isBot) {
            userVisitsCount++;
            String ip = entry.getIpAddress();
            if (ip != null && !ip.isEmpty()) {
                uniqueUserIps.add(ip);
            }
        }

        String os = entry.getUserAgent().getOsType();

        if (os != null && !os.isEmpty()) {
            osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);
        }

        String browser = entry.getUserAgent().getBrowserType();

        if (browser != null && !browser.isEmpty()) {
            browserFrequency.put(browser, browserFrequency.getOrDefault(browser, 0) + 1);
        }
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Set<String> getUnexistingPages() {
        return new HashSet<>(unexistingPages);
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStats = new HashMap<>();
        int totalCount = osFrequency.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("Всего записей с ОС: " + totalCount);
        System.out.println("Распределение по ОС: " + osFrequency);

        if (totalCount == 0) {
            return osStats;
        }

        for (Map.Entry<String, Integer> entry : osFrequency.entrySet()) {
            String os = entry.getKey();
            int count = entry.getValue();
            double proportion = (double) count / totalCount;
            osStats.put(os, proportion);
        }

        return osStats;
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStats = new HashMap<>();
        int totalCount = browserFrequency.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("Всего записей с браузером: " + totalCount);
        System.out.println("Распределение по браузерам: " + browserFrequency);

        if (totalCount == 0) {
            return browserStats;
        }

        for (Map.Entry<String, Integer> entry : browserFrequency.entrySet()) {
            String browser = entry.getKey();
            int count = entry.getValue();
            double proportion = (double) count / totalCount;
            browserStats.put(browser, proportion);
        }

        return browserStats;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null)
            return 0;

        Duration duration = Duration.between(minTime, maxTime);
        long hoursDiff = Math.max(duration.toHours(), 1);

        return
                (double) totalTraffic / hoursDiff / 1024;
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null) {
            return 0;
        } else if (uniqueUserIps.isEmpty() || userVisitsCount == 0) {
            return 0;
        }

        Duration duration = Duration.between(minTime, maxTime);
        long hoursDiff = Math.max(duration.toHours(), 1);
        return
                (double) userVisitsCount / hoursDiff;
    }

    public double getAverageErrorRequestPerHour() {
        if (minTime == null || maxTime == null || errorRequestCount == 0) {
            return 0;
        }

        Duration duration = Duration.between(minTime, maxTime);
        long hoursDiff = Math.max(duration.toHours(), 1);

        return
                (double) errorRequestCount / hoursDiff;
    }

    public double getAverageVisitsPerUser() {
        if (uniqueUserIps.isEmpty() || userVisitsCount == 0) {
            return 0;
        }

        return
                (double) userVisitsCount / uniqueUserIps.size();
    }
}
