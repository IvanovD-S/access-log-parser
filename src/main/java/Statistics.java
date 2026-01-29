import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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

    private final Map<Long, Integer> visitsPerSecond = new HashMap<>();
    private final Map<String, Integer> userVisitCountByIp = new HashMap<>();
    private final Set<String> referrerDomains = new HashSet<>();

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getContentSizeBytes();

        // Обновление временных границ
        if (minTime == null || entry.getTimestamp().isBefore(minTime)) {
            minTime = entry.getTimestamp();
        }
        if (maxTime == null || entry.getTimestamp().isAfter(maxTime)) {
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
                userVisitCountByIp.merge(ip, 1, Integer::sum);
            }

            long second = entry.getTimestamp().atZone(ZoneOffset.UTC).toEpochSecond();
            visitsPerSecond.merge(second, 1, Integer::sum);

            String os = userAgent.getOsType();
            if (os != null && !os.isEmpty()) {
                osFrequency.merge(os, 1, Integer::sum);
            }

            String browser = userAgent.getBrowserType();
            if (browser != null && !browser.isEmpty()) {
                browserFrequency.merge(browser, 1, Integer::sum);
            }
        }

        // Сбор доменов из Referer (всех, включая ботов)
        String referrer = entry.getReferrer();
        if (referrer != null && !referrer.equals("-") && !referrer.isEmpty()) {
            String domain = extractDomain(referrer);
            if (domain != null) {
                referrerDomains.add(domain);
            }
        }
    }


    private String extractDomain(String referrer) {

        if (referrer == null || referrer.isEmpty() || referrer.equals("-") || referrer.equals("\"-\"")) {
            return null;
        }

        referrer = referrer.strip();

        if (referrer.startsWith("\"")) {
            referrer = referrer.substring(1);
        }

        if (referrer.endsWith("\"")) {
            referrer = referrer.substring(0, referrer.length() - 1);
        }

        referrer = URLDecoder.decode(referrer, StandardCharsets.UTF_8);

        referrer = referrer.replaceAll("https?://", "");

        referrer = referrer.replaceFirst("^www\\.", "");

        int slashIndex = referrer.indexOf('/');
        if (slashIndex != -1) {
            referrer = referrer.substring(0, slashIndex);
        }

        int ampersandIndex = referrer.indexOf('&');
        if (ampersandIndex != -1) {
            referrer = referrer.substring(0, ampersandIndex);
        }

        if (referrer.equals("localhost") || referrer.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
            return null;
        }

        return referrer.trim();
    }

    public int getPeakVisitsPerSecond() {
        if (visitsPerSecond.isEmpty()) {
            return 0;
        }
        return Collections.max(visitsPerSecond.values());
    }

    public Set<String> getReferrerDomains() {
        return new HashSet<>(referrerDomains); // возвращаем копию
    }

    public int getMaxVisitsBySingleUser() {
        if (userVisitCountByIp.isEmpty()) {
            return 0;
        }
        return Collections.max(userVisitCountByIp.values());
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
