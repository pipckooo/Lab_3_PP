package droids;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

/**
 * Дефендер
 * - Має особистий щит 28% ХП
 * - Пасивки (перехоплення 33%, аура 20%) реалізовані в DroidBattle
 */
@Getter
@Setter
public class DefenderDroid extends Droid {

   public DefenderDroid(String name) {
        super(
                name,
                "Defender",
                240,
                16,
                19,
                15,
                24,
                33,
                0.28
        );
    }

    public  void performMainAction(){}

    @Override
    public void onRoundEnd(){
       super.onRoundEnd();
    }
    public Droid createDefender(Scanner scanner){
       System.out.println("Введіть назву дефендера");
       String name = scanner.nextLine();
     Droid newDroid=new DefenderDroid(name);
     return newDroid;
    }
}