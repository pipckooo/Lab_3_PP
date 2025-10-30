package droids;



import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static droids.DroidBattle.setTeam;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== ⚔️ Налаштування Droid Battle ⚔️ ===");


        int mod = chooseMod(sc);
        int alliesCount = setAlliesCount(mod);
        int enemiesCount = setEnemiesCount(mod);


        List<Droid> playerTeam = new ArrayList<>();
        System.out.println("\n--- Вибір ВАШОЇ команди (" + alliesCount + " дроїди) ---");
        setTeam(alliesCount, playerTeam, sc);

        List<Droid> enemyTeam = new ArrayList<>();
        System.out.println("\n--- Вибір команди СУПЕРНИКА (" + enemiesCount + " дроїди) ---");
        setTeam(enemiesCount, enemyTeam, sc);



        String logFileName = "logForBattles.txt";
        System.out.println("\n✅ Підготовка до бою. Журнал буде збережено у файл: " + logFileName);

        DroidBattle battle = new DroidBattle(playerTeam, enemyTeam, logFileName);

        System.out.println("\n--- БИТВА ПОЧИНАЄТЬСЯ! ---");
        battle.startBattle();

        sc.close();
    }
    public static int chooseMod(Scanner scanner){
        int mod;

        do{
            System.out.println("Вибір режиму,якщо  бажаєте 3 на 3 ,введіть 3,якщо 2 на 2 , введіть 2, якщо 3 на 4 ,введіть 1 ");
            mod=scanner.nextInt();
            if(mod!=3 && mod!=2 && mod!=1 ){
                System.out.println("Помилка вибору режиму");
                continue;
            }

        }while (mod!=3 && mod!=2 && mod!=1);
        return mod;
    }
    public static int setAlliesCount(int mod){
        int temp=0;
        switch (mod) {
            case 1:
                temp=3;

                break;
            case 2:
                temp=2;

                break;

            case  3:
              temp=3;

                break;
            default:
                break;
        }
        return temp;
    }
    public static int setEnemiesCount(int mod){
        int temp=0;
        switch (mod) {
            case 1:
                temp=4;

                break;
            case 2:
                temp=2;

                break;

            case  3:
                temp=3;

                break;
            default:
                break;
        }
return temp;
    }

}