package droids;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
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
        this.healAmount = (int) (this.getMaxHP() * 0.10); // Лікує на 20%
        this.regenAmount = (int) (this.getMaxHP() * 0.05); // Регенерує 5%
    }

    /**
     * ПОЛІМОРФІЗМ (Активна дія)
     * Хілер лікує союзника з найнижчим % ХП.
     */
    @Override
    public void performMainAction() {
        // Створюємо список живих союзників + себе
        List<Droid> allAllies = new ArrayList<>();
        for (Droid d : this.team) {
            if (d.isAlive()) allAllies.add(d);
        }
        if (this.isAlive()) allAllies.add(this); // Додаємо себе, якщо живий

        // Знаходимо союзника з найнижчим відсотком HP
        Droid target = allAllies.stream()
                .min(Comparator.comparingDouble(d -> (double) d.getCurrentHP() / d.getMaxHP()))
                .orElse(null);

        if (target != null) {
            double healMultiplier = (target == this) ? 0.5 : 1.0;
            int healAmount = (int) Math.round(this.healAmount * healMultiplier);

            System.out.println("✨ " + this.getName() + " лікує " + target.getName() +
                    " на " + healAmount + " ХП" + (target == this ? " (самолікування)" : "") + "!");
            target.heal(healAmount);
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