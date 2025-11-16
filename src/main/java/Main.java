import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Курсовой проект. Задание по теме ""Синтаксис языка" и “Базовые типы данных”"
        System.out.println("Введите первое число и нажмите ENTER: ");
        int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число и нажмите ENTER: ");
        int secondNumber = new Scanner(System.in).nextInt();
        //Сумма чисел
        int sum = firstNumber + secondNumber;
        //Разность чисел
        int difference = firstNumber - secondNumber;
        //Произведение чисел
        int product = firstNumber * secondNumber;
        //Частное чисел
        double quotient = (double) firstNumber / secondNumber;
        //Выводим на экран
        System.out.println("Сумма чисел: "+firstNumber+" + "+secondNumber+" = "+sum);
        System.out.println("Разность чисел: "+firstNumber+" - "+secondNumber+" = "+difference);
        System.out.println("Произведение чисел: "+firstNumber+" * "+secondNumber+" = "+product);
        System.out.println("Частное чисел: "+firstNumber+" / "+secondNumber+" = "+quotient);
    }
}
