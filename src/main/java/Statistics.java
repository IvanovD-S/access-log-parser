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
    private final Map<String, Integer> osFrequency = new HashMap<>();

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

        String os = entry.getUserAgent().getOsType();

        if (os != null && !os.isEmpty()) {
            osFrequency.put(os, osFrequency.getOrDefault(os, 0) + 1);
        }
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
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

    public double getTrafficRate() {
        if (minTime == null || maxTime == null)
            return 0.0;

        Duration duration = Duration.between(minTime, maxTime);

        long hoursDiff = Math.max(duration.toHours(), 1);

        return (double) totalTraffic / hoursDiff / 1024;
    }
}
