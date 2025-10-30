package droids;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Хіллер
 * - Дія: Лікує союзника з найнижчим % ХП (performMainAction).
 * - Пасивка: Регенерує 5% свого макс. ХП в кінці раунду (onRoundEnd).
 */
@Getter
@Setter
public class HealerDroid extends Droid {

    private final int healAmount;
    private final int regenAmount;

    public HealerDroid(String name) {
        super(
                name,               // name
                "Healer",           // type
                250,                // maxHP
                10,                 // minATC
                15,                 // maxATC
                5,                  // critChance
                20,                 // maxCritAttack
                100,                // abilityChance (не використовується)
                0.1                 // shieldMultiplier (10%)
        );
        this.healAmount = (int) (this.getMaxHP() * 0.20); // Лікує на 20%
        this.regenAmount = (int) (this.getMaxHP() * 0.05); // Регенерує 5%
    }

    /**
     * ПОЛІМОРФІЗМ (Активна дія)
     * Хілер лікує союзника з найнижчим % ХП.
     */
    @Override
    public void performMainAction() {
        // Створюємо список живих союзників + себе
        List<Droid> allAllies = this.team.stream()
                .filter(Droid::isAlive)
                .collect(Collectors.toList());
        allAllies.add(this); // Додаємо себе в список для можливого хіла

        // Знаходимо союзника з найнижчим % ХП
        Optional<Droid> targetToHeal = allAllies.stream()
                .filter(Droid::isAlive) // Додаткова перевірка
                .min(Comparator.comparingDouble(d -> (double) d.getCurrentHP() / d.getMaxHP()));

        if (targetToHeal.isPresent()) {
            Droid target = targetToHeal.get();
            System.out.println("✨ " + this.getName() + " лікує " + target.getName() +
                    " на " + this.healAmount + " ХП!");
            target.heal(this.healAmount);
        } else {
            System.out.println(this.getName() + " не має кого лікувати.");
        }
    }

    /**
     * ПОЛІМОРФІЗМ (Пасивна дія)
     * В кінці раунду Хілер регенерує ХП.
     */
    @Override
    public void onRoundEnd() {
        super.onRoundEnd();


        if (this.isAlive() && this.getCurrentHP() < this.getMaxHP()) {
            System.out.println("✚ " + this.getName() + " регенерує " + this.regenAmount + " ХП.");
            this.heal(this.regenAmount);
        }
    }
    public Droid createHealer(Scanner scanner){
        System.out.println("Введіть назву хіллера");
        String name = scanner.nextLine();
        Droid newDroid=new HealerDroid(name);
        return newDroid;
    }
}