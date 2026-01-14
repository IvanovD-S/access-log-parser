import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private int totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

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
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null)
            return 0.0;

        Duration duration = Duration.between(minTime, maxTime);

        long hoursDiff = Math.max(duration.toHours(), 1);

        return
                (double) totalTraffic / hoursDiff / 1024;
    }
}
