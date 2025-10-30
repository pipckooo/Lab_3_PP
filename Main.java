package droids;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import java.util.Scanner;
import java.util.stream.Collectors; // Додано для обробки потоків файлу

import static droids.DroidBattle.setTeam;

public class Main {

    private static final String LOG_FILE_NAME = "logForBattles.txt";
    private static final String BATTLE_SEPARATOR = "--- ЖУРНАЛ НОВОГО БОЮ ---";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ⚔️ Droid Battle System ⚔️ ===");

        // 1. Початковий вибір
        int choice = initialChoice(sc);

        if (choice == 1) {
            startNewBattle(sc);
        } else {
            viewPastBattleLog(sc);
        }

        sc.close();
    }



    public static int initialChoice(Scanner scanner) {
        int choice;
        do {
            System.out.println("\nОберіть дію:");
            System.out.println("1. ⚔️ Почати новий бій");
            System.out.println("2. 📜 Переглянути минулий лог бою");
            System.out.print("Ваш вибір (1 або 2): ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice == 1 || choice == 2) {
                    scanner.nextLine();
                    return choice;
                }
            } else {
                scanner.next();
            }
            System.out.println("❌ Невірний вибір. Введіть 1 або 2.");
        } while (true);
    }



    public static void startNewBattle(Scanner sc) {

        int mod = chooseMod(sc);
        int alliesCount = setAlliesCount(mod);
        int enemiesCount = setEnemiesCount(mod);


        List<Droid> playerTeam = new ArrayList<>();
        System.out.println("\n--- Вибір ВАШОЇ команди (" + alliesCount + " дроїди) ---");
        setTeam(alliesCount, playerTeam, sc);

        List<Droid> enemyTeam = new ArrayList<>();
        System.out.println("\n--- Вибір команди СУПЕРНИКА (" + enemiesCount + " дроїди) ---");
        setTeam(enemiesCount, enemyTeam, sc);


        System.out.println("\n✅ Підготовка до бою. Журнал буде додано до файлу: " + LOG_FILE_NAME);

        DroidBattle battle = new DroidBattle(playerTeam, enemyTeam, LOG_FILE_NAME);

        System.out.println("\n--- БИТВА ПОЧИНАЄТЬСЯ! ---");
        battle.startBattle();
    }




    public static void viewPastBattleLog(Scanner scanner) {
        System.out.print("\nВведіть номер битви, яку бажаєте переглянути (починаючи з 1): ");
        int battleNumber = -1;
        while (true) {
            if (scanner.hasNextInt()) {
                battleNumber = scanner.nextInt();
                scanner.nextLine(); // Очищення буфера
                if (battleNumber > 0) break;
            } else {
                scanner.nextLine();
            }
            System.out.print("Будь ласка, введіть дійсний номер битви (ціле число > 0): ");
        }

        try {
            Path path = Paths.get(LOG_FILE_NAME);
            if (!Files.exists(path)) {
                System.out.println("❌ Файл журналу не знайдено: " + LOG_FILE_NAME);
                return;
            }

            try (Stream<String> lines = Files.lines(path)) {
                List<String> logLines = lines.collect(Collectors.toList());

                int battleStartLine = -1;
                int battleEndLine = logLines.size();
                int currentBattleCount = 0;

                for (int i = 0; i < logLines.size(); i++) {
                    if (logLines.get(i).contains(BATTLE_SEPARATOR)) {
                        currentBattleCount++;
                        if (currentBattleCount == battleNumber) {
                            battleStartLine = i;
                        } else if (currentBattleCount == battleNumber + 1) {
                            battleEndLine = i;
                            break;
                        }
                    }
                }

                if (battleStartLine != -1) {
                    System.out.println("\n======== 📜 ЛОГ БИТВИ №" + battleNumber + " 📜 ========");
                    // Виводимо рядки, включаючи рядок-сепаратор
                    for (int i = battleStartLine; i < battleEndLine; i++) {
                        System.out.println(logLines.get(i));
                    }
                    System.out.println("=============================================");
                } else {
                    System.out.println("❌ Битва №" + battleNumber + " не знайдена у лозі. Знайдено всього " + currentBattleCount + " битв.");
                }

            }
        } catch (IOException e) {
            System.err.println("Помилка при читанні лог-файлу: " + e.getMessage());
        }
    }



    public static int chooseMod(Scanner scanner){
        int mod;

        do{
            System.out.println("\nВибір режиму:");
            System.out.println("1: 3 на 4 (3 союзники, 4 вороги)");
            System.out.println("2: 2 на 2 (2 союзники, 2 вороги)");
            System.out.println("3: 3 на 3 (3 союзники, 3 вороги)");
            System.out.println("4: 1 на 1 (1 союзник, 1 ворог)");
            System.out.print("Ваш вибір (1-4): ");

            if (scanner.hasNextInt()) {
                mod = scanner.nextInt();
                if(mod >= 1 && mod <= 4){
                    scanner.nextLine();
                    return mod;
                }
            } else {
                scanner.next();
            }
            System.out.println("Помилка вибору режиму. Введіть число від 1 до 4.");

        }while (true);
    }

    public static int setAlliesCount(int mod){
        switch (mod) {
            case 1: return 3;
            case 2: return 2;
            case 3: return 3;
            case 4: return 1;
            default: return 0;
        }
    }

    public static int setEnemiesCount(int mod){
        switch (mod) {
            case 1: return 4;
            case 2: return 2;
            case 3: return 3;
            case 4: return 1;
            default: return 0;
        }
    }
}
