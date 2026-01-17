public class UserAgent {
    private final String osType;
    private final String browserType;

    private static final String WINDOWS = "Windows";
    private static final String MAC_OS = "macOS";
    private static final String LINUX = "Linux";

    private static final String EDGE = "Edge";
    private static final String FIREFOX = "Firefox";
    private static final String CHROME = "Chrome";
    private static final String OPERA = "Opera";


    public UserAgent(String agentString) {

        if (agentString.contains("Windows"))
            this.osType = WINDOWS;

        else if (agentString.contains("macOS"))
            this.osType = MAC_OS;

        else if (agentString.contains("Linux"))
            this.osType = LINUX;

        else this.osType = "Other";

        if (agentString.contains("Edge"))
            this.browserType = EDGE;

        else if (agentString.contains("Firefox"))
            this.browserType = FIREFOX;

        else if (agentString.contains("Chrome"))
            this.browserType = CHROME;

        else if (agentString.contains("Opera"))
            this.browserType = OPERA;

        else
            this.browserType = "Other";
    }

    public String getBrowserType() {
        return browserType;
    }

    public String getOsType() {
        return osType;
    }
}