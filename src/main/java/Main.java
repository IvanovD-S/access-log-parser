import java.io.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        // Задание #1. Обработка исключений
        fileName();
    }

    public static void fileName() {
        Scanner sc = new Scanner(System.in);
        int correctPathsCount = 0;
        boolean lineLarge1024 = false;
        int countLine = 0;
        double yandexBot = 0;
        double googlebot = 0;
        Statistics stats = new Statistics();

        while (true) {
            System.out.print("Введите путь к файлу или каталогу ('завершить' для выхода): ");
            String path = sc.nextLine();

            if (path.equals("завершить")) {
                System.out.println("Программа завершена. Всего было указано " + correctPathsCount + " правильных путей.");
                break;
            }

            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (isDirectory) {
                System.out.println("Путь является папкой");
                continue;
            } else if (!fileExists) {
                System.out.println("Файл не найден");
                continue;
            }
            correctPathsCount++;
            System.out.println("Указанный путь найден. Указано правильный путей: " + correctPathsCount);
            FileReader fileReader;
            try {
                fileReader = new FileReader(path);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                BufferedReader reader = new BufferedReader(fileReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    if (large1024(line)) {
                        System.out.println("Строка длиннее 1024 символов № " + (countLine + 1));
                        lineLarge1024 = true;
                        break;
                    }
                    int length = line.length();
                    countLine++;
                    if (line.contains("Googlebot")) {
                        googlebot++;
                    }
                    if (line.contains("YandexBot")) {
                        yandexBot++;
                    }

                    LogEntry entry = new LogEntry(line);
                    stats.addEntry(entry);
                }
            } catch (IOException e) {
                System.out.println("Ошибка при работе с файлом: " + e.getMessage());
            }

            if (!lineLarge1024) {
                double trafficRate = stats.getTrafficRate();
                Set<String> pages = stats.getExistingPages();
                System.out.println("Существующие страницы: " + pages);
                Map<String, Double> osStats = stats.getOsStatistics();
                for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                    System.out.printf("ОС: %s, Доля: %.4f%n", entry.getKey(), entry.getValue());
                }
                System.out.println("Количество строк в файле: " + countLine);
                System.out.println("Количество запросов Googlebot: " + (Math.floor((googlebot / countLine) * 10000)) / 100 + "% от общего числа запросов");
                System.out.println("Количество запросов YandexBot: " + (Math.floor((yandexBot / countLine) * 10000)) / 100 + "% от общего числа запросов");
                System.out.println("Интенсивность трафика: " + Math.floor(trafficRate * 100) / 100 + " Кб/ч");
            } else {
                System.out.println("Работа с файлом прекращена из-за наличия строки длиннее 1024 символов");
            }
        }
    }

    private static boolean large1024(String line) {
        if (line.length() > 1024) {
            return true;
        }
        return false;
    }
}

