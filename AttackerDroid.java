package droids;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/**
 * Атакер
 * - Підвищений кріт шанс (30%)
 * - 25% шанс на контрудар
 */
@Getter
@Setter
public class AttackerDroid extends Droid {
        private final int AttackerAbilityATC;
    public AttackerDroid(String name) {
        super(
                name,
                "Attacker",
                170,
                27,
                32,
                33,
                37,
                33,
                0.1
        );
        this.AttackerAbilityATC=42;
    }


    public  void performMainAction(){}


    public  void onDamageTaken(int damage,Droid attacker){
        super.onDamageTaken(damage, attacker);
        if(this.checkIfDroidIsAlive()&& attacker.checkIfDroidIsAlive() &&checkAbilityChance()){
            System.out.println(this.getName()+" контратакує " +attacker.getName());
            attacker.takeDamage(this.AttackerAbilityATC,this);
        }
    }
    @Override
    public void onRoundEnd(){}

}
