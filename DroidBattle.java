
package droids;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Цей клас керує всім процесом бою.
 * Він містить головний ігровий цикл (game loop), логіку запису в файл та статичні
 * методи для налаштування команд.
 */
public class DroidBattle {

    private final List<Droid> team1;
    private final List<Droid> team2;
    private final Scanner scanner;
    private PrintWriter fileLogger;


    public DroidBattle(List<Droid> team1, List<Droid> team2, String logFileName) {
        this.team1 = team1;
        this.team2 = team2;
        this.scanner = new Scanner(System.in);

        try {
            this.fileLogger = new PrintWriter(new FileWriter(logFileName, true));
            log("--- ЖУРНАЛ НОВОГО БОЮ ---");
            log("КОМАНДА 1: " + team1.stream().map(Droid::getName).collect(Collectors.joining(", ")));
            log("КОМАНДА 2: " + team2.stream().map(Droid::getName).collect(Collectors.joining(", ")));
        } catch (IOException e) {
            System.err.println("Помилка при створенні лог-файлу: " + e.getMessage());
            this.fileLogger = null;
        }
    }


    private void log(String message) {
        System.out.println(message);
        if (fileLogger != null) {
            fileLogger.println(message);
            fileLogger.flush();
        }
    }




    public void startBattle() {

        initializeBattle();

        int round = 1;
        while (checkWinCondition() == null) {
            log("\n==================================");
            log("--- ⚔️  Раунд " + round + " ⚔️ ---");
            log("==================================");

            List<Droid> turnOrder = new ArrayList<>(team1);
            turnOrder.addAll(team2);

            for (Droid currentDroid : turnOrder) {
                if (!currentDroid.isAlive()) continue;
                executeTurn(currentDroid);
                if (checkWinCondition() != null) {
                    break;
                }
            }

            if (checkWinCondition() == null) {
                runRoundEndPhase();
                printBattleStatus();
            }

            round++;
        }

        announceWinner();

        if (fileLogger != null) {
            fileLogger.close();
            System.out.println("\n✅ Журнал бою збережено у файл.");
        }
    }

    private void initializeBattle() {
        for (Droid d : team1) {
            d.setTeams(team1, team2);
        }
        for (Droid d : team2) {
            d.setTeams(team2, team1);
        }
        log("Битва починається!");
        printBattleStatus();
    }

    private void executeTurn(Droid currentDroid) {
        log("\n--- Хід дроїда: " + currentDroid.getName() +
                " (" + currentDroid.getType() + ") ---");


        currentDroid.performMainAction();


        if (currentDroid.isAlive()) {
            handleAttackerTurn(currentDroid);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
    }

    private void handleAttackerTurn(Droid attacker) {
        List<Droid> enemyTeam = attacker.getEnemies();

        List<Droid> livingEnemies = enemyTeam.stream()
                .filter(Droid::isAlive)
                .collect(Collectors.toList());

        if (livingEnemies.isEmpty()) {
            log(attacker.getName() + " не має кого атакувати.");
            return;
        }


        System.out.println("Оберіть ціль для атаки:");
        for (int i = 0; i < livingEnemies.size(); i++) {
            Droid t = livingEnemies.get(i);
            System.out.println("  " + (i + 1) + ". " + t.getName() +
                    " [" + t.getCurrentHP() + "/" + t.getMaxHP() + " ХП]");
        }
        Droid chosenTarget = getPlayerTargetChoice(livingEnemies);


        log(attacker.getName() + " обрав ціль: " + chosenTarget.getName());

        Droid finalTarget = chosenTarget;

        List<Droid> enemyDefenders = chosenTarget.getTeam().stream()
                .filter(d -> d.isAlive() && d.getType().equals("Defender"))
                .collect(Collectors.toList());

        if (!enemyDefenders.isEmpty()) {

            for (Droid defender : enemyDefenders) {
                if (defender.checkAbilityChance()) {
                    log("🛡️ " + defender.getName() + " ПЕРЕХОПИВ атаку, спрямовану на " + chosenTarget.getName() + "!");
                    finalTarget = defender;
                    break;
                }
            }
        }
        attacker.attack(finalTarget);
    }

    private void runRoundEndPhase() {
        log("\n--- Кінець раунду ---");
        List<Droid> allDroids = new ArrayList<>(team1);
        allDroids.addAll(team2);

        for (Droid d : allDroids) {
            if (d.isAlive()) {
                d.onRoundEnd();
            }
        }
    }

    private String checkWinCondition() {
        boolean team1Alive = team1.stream().anyMatch(Droid::isAlive);
        boolean team2Alive = team2.stream().anyMatch(Droid::isAlive);

        if (!team1Alive) {
            return "Команда 2";
        }
        if (!team2Alive) {
            return "Команда 1";
        }
        return null;
    }

    private void announceWinner() {
        String winner = checkWinCondition();
        log("\n==================================");
        log("Битву завершено! Переможець: " + winner + "!");
        log("==================================");
    }


    private Droid getPlayerTargetChoice(List<Droid> livingEnemies) {
        while (true) {
            try {
                System.out.print("Ваш вибір (1-" + livingEnemies.size() + "): ");
                int choice = Integer.parseInt(scanner.nextLine()) - 1;

                if (choice >= 0 && choice < livingEnemies.size()) {
                    return livingEnemies.get(choice);
                } else {
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть число.");
            }
        }
    }

    private void printBattleStatus() {
        log("\n--- Статус Команд ---");
        log("Команда 1:");
        for (Droid d : team1) {
            String status = String.format("  %s %s: [%d/%d] HP, Щит: %d",
                    d.isAlive() ? "💚" : "☠️",
                    d.getName(), d.getCurrentHP(), d.getMaxHP(), d.getShield());
            log(status);
        }
        log("Команда 2 (Вороги):");
        for (Droid d : team2) {
            String status = String.format("  %s %s: [%d/%d] HP, Щит: %d",
                    d.isAlive() ? "❤️" : "☠️",
                    d.getName(), d.getCurrentHP(), d.getMaxHP(), d.getShield());
            log(status);
        }
    }


    public static Droid chooseDroid(String input, Scanner scanner) {
        Droid newDroid = null;
        String tempName = null;

        switch (input) {
            case "H":
                tempName = getName(scanner, "Healer");
                newDroid = new HealerDroid(tempName); // ✅ РОЗКОМЕНТУВАТИ та ВСТАВИТИ ВАШ КЛАС
                break;
            case "B":
                tempName = getName(scanner, "Buffer");
                newDroid = new BufferDroid(tempName); // ✅ РОЗКОМЕНТУВАТИ та ВСТАВИТИ ВАШ КЛАС
                break;
            case "A":
                tempName = getName(scanner, "Attacker");
                newDroid = new AttackerDroid(tempName); // ✅ РОЗКОМЕНТУВАТИ та ВСТАВИТИ ВАШ КЛАС
                break;
            case "D":
                tempName = getName(scanner, "Defender");
                newDroid = new DefenderDroid(tempName); // ✅ РОЗКОМЕНТУВАТИ та ВСТАВИТИ ВАШ КЛАС
                break;
            default:
                break;
        }


        if (newDroid == null && tempName != null) {
            System.out.println("⚠️ Помилка: Клас для типу '" + input + "' не визначено!");
        }
        return newDroid;
    }

    /** Заповнює команду дроїдами, обраними користувачем. */
    public static void setTeam(int count, List<Droid> team, Scanner scanner) {



        int teamCount = 0;

        do {
            System.out.println("\n🔧 Створення дроїда " + (teamCount + 1) + " з " + count + ":");
            System.out.println("H — хіллер, B — баффер, A — атакер, D — дефендер");
            System.out.print("👉 Ваш вибір: ");

            String choice = scanner.nextLine().replaceAll("\\s+", "").toUpperCase();

            if (choice.length() != 1) {
                System.out.println("❌ Помилка введення: введіть одну літеру без пробілів.");
                continue;
            }

            Droid tempDroid = chooseDroid(choice, scanner);

            if (tempDroid != null) {
                teamCount++;
                team.add(tempDroid);
                System.out.println("✅ Дроїда з ім'ям \"" + tempDroid.getName() + "\" успішно додано до команди!");
            } else {
                System.out.println("⚠️ Дроїда не додано. Спробуйте ще раз.");
            }

        } while (teamCount < count);
    }


    public static String getName(Scanner scanner, String type) {
        System.out.print("Введіть назву дроїда типу " + type + ": ");
        return scanner.nextLine();
    }
}
