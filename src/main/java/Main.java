import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Курсовой проект. Задание по теме "Циклы"
        fileName();
    }

    public static void fileName() {
        Scanner sc = new Scanner(System.in);
        int correctPathsCount = 0;

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
        }
    }
}