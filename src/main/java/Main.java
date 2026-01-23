import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Задание #1. Обработка исключений
        try {
            fileName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fileName() throws IOException {
        Scanner sc = new Scanner(System.in);
        int correctPathsCount = 0;
        int countLine = 0;
        double yandexBotCount = 0;
        double googlebotCount = 0;
        boolean isLineMoreLengthOf = false;
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

            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                isLineMoreLengthOf = isLineMoreLengthOf(line, 1024);
                if (isLineMoreLengthOf) {
                    System.out.println("Строка длиннее 1024 символов № " + (countLine + 1));
                    break;
                }
                countLine++;
                if (line.contains("Googlebot")) {
                    googlebotCount++;
                }
                if (line.contains("YandexBot")) {
                    yandexBotCount++;
                }

                LogEntry entry = new LogEntry(line);
                stats.addEntry(entry);
            }
        }

        if (!isLineMoreLengthOf) {
            double trafficRate = stats.getTrafficRate();
            Set<String> pages = stats.getExistingPages();
            System.out.println("Существующие страницы: " + pages);
            Map<String, Double> osStats = stats.getOsStatistics();
            for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                System.out.printf("ОС: %s, Доля: %.4f%n", entry.getKey(), entry.getValue());
            }
            System.out.println("Количество строк в файле: " + countLine);
            double googlebotCountRequest = (Math.floor((googlebotCount / countLine) * 10000)) / 100;
            double yandexBotCountRequest = (Math.floor((yandexBotCount / countLine) * 10000)) / 100;
            System.out.println("Количество запросов Googlebot: " + googlebotCountRequest + "% от общего числа запросов");
            System.out.println("Количество запросов YandexBot: " + yandexBotCountRequest + "% от общего числа запросов");
            System.out.println("Интенсивность трафика: " + Math.floor(trafficRate * 100) / 100 + " Кб/ч");
        } else {
            System.out.println("Работа с файлом прекращена из-за наличия строки длиннее 1024 символов");
        }
    }

    private static boolean isLineMoreLengthOf(String line, int length) {
        if (line.length() > length) {
            return true;
        }
        return false;
    }
}


