import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
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
            System.out.println("Указанный путь найден. Указано правильных путей: " + correctPathsCount);

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 1024) {
                        System.out.println("Строка длиннее 1024 символов № " + (countLine + 1));
                        isLineMoreLengthOf = true;
                        break;
                    }

                    countLine++;
                    if (line.contains("Googlebot")) {
                        googlebotCount++;
                    }
                    if (line.contains("YandexBot")) {
                        yandexBotCount++;
                    }

                    try {
                        LogEntry entry = new LogEntry(line);
                        stats.addEntry(entry);
                    } catch (Exception e) {
                        System.err.println("Ошибка обработки строки №" + countLine + ": " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + e.getMessage());
                continue;
            }
        }


            if (countLine > 0) {
                double trafficRate = stats.getTrafficRate();

                Set<String> pages = stats.getExistingPages();
                System.out.println("Существующие страницы: " + pages);

                Set<String> unexistingPages = stats.getUnexistingPages();
                System.out.println("Несуществующие страницы: " + unexistingPages);

                Map<String, Double> osStats = stats.getOsStatistics();
                for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                    System.out.printf("ОС: %s, Доля: %.4f%n", entry.getKey(), entry.getValue());
                }

                Map<String, Double> browserStats = stats.getBrowserStatistics();
                for (Map.Entry<String, Double> entry : browserStats.entrySet()) {
                    System.out.printf("Браузер: %s, Доля: %.4f%n", entry.getKey(), entry.getValue());
                }

                double averageVisitsPerHour = stats.getAverageVisitsPerHour();
                double averageErrorRequestPerHour = stats.getAverageErrorRequestPerHour();
                double averageVisitsPerUser = stats.getAverageVisitsPerUser();

                System.out.println("Количество строк в файле: " + countLine);

                System.out.printf("Среднее количество посетителей сайта за час: %.4f%n", averageVisitsPerHour);
                System.out.printf("Среднее количество ошибочных запросов в час: %.4f%n", averageErrorRequestPerHour);
                System.out.printf("Средняя посещаемость одним пользователем: %.4f%n", averageVisitsPerUser);

                double googlebotCountRequest = (Math.floor((googlebotCount / countLine) * 10000)) / 100;
                double yandexBotCountRequest = (Math.floor((yandexBotCount / countLine) * 10000)) / 100;
                System.out.println("Количество запросов Googlebot: " + googlebotCountRequest + "% от общего числа запросов");
                System.out.println("Количество запросов YandexBot: " + yandexBotCountRequest + "% от общего числа запросов");
                System.out.println("Интенсивность трафика: " + Math.floor(trafficRate * 100) / 100 + " Кб/ч");
            }

            if (isLineMoreLengthOf) {
                System.out.println("Работа с файлом прекращена из-за наличия строки длиннее 1024 символов");
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

}


