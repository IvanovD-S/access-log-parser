import java.io.*;
import java.sql.SQLOutput;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        // Задание #1. Обработка исключений
        fileName();
    }

    public static void fileName() throws IOException {
        Scanner sc = new Scanner(System.in);
        int correctPathsCount = 0;
        int countLine = 0;
        int minLine = 0;
        int maxLine = 0;

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
            FileReader fileReader = null;
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
                    }
                    int length = line.length();
                    countLine++;
                    if (maxLine < length) {
                        maxLine = length;
                    }
                    if (minLine == 0 || minLine > length) {
                        minLine = length;
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при работе с файлом: " + e.getMessage());
            }
            System.out.println("Количество строк в файле: " + countLine);
            System.out.println("Самая длинная строка: " + maxLine);
            System.out.println("Самая короткая строка: " + minLine);
        }


    }

    private static boolean large1024(String line) {
        if (line.length() > 1024) {
            return true;
        }
        return false;
    }
}

