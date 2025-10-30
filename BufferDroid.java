package droids;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Бафер
 * - Дія: Накладає баф (атака або щит) на випадкового союзника (performMainAction).
 * - Пасивка: 7% шанс воскресити союзника при його смерті (onAllyDied).
 */
@Getter
@Setter
public class BufferDroid extends Droid {

    public BufferDroid(String name) {
        super(
                name,               // name
                "Buffer",           // type
                190,                // maxHP
                20,                 // minATC
                25,                 // maxATC
                15,                 // critChance
                30,                 // maxCritAttack
                7,                  // abilityChance (для воскресіння)
                0.15                // shieldMultiplier (15%)
        );
    }

    /**
     * ПОЛІМОРФІЗМ (Активна дія)
     * Бафер накладає бафи.
     */
    @Override
    public void performMainAction() {
        List<Droid> livingAllies = this.team.stream()
                .filter(Droid::isAlive)
                .collect(Collectors.toList());
        livingAllies.add(this); // Бафер може бафнути і себе

        if (livingAllies.isEmpty()) {
            System.out.println(this.getName() + " не має кого бафати.");
            return;
        }


        Droid target = livingAllies.get(randomInt(0, livingAllies.size() - 1));


        if (isChanceTrue(50)) {
            // Баф Атаки
            target.setAttackBuffTurns(2); // Баф на 2 ходи
            System.out.println("⚔️ " + this.getName() + " дає баф АТАКИ " + target.getName() + "!");
        } else {
            // Баф Щита (30% від макс. ХП цілі)
            int shieldValue = (int) (target.getMaxHP() * 0.15);
            target.setShield(target.getShield() + shieldValue);
            target.setShieldDown(false);
            System.out.println("🛡️ " + this.getName() + " дає щит ("+ shieldValue +") " + target.getName() + "!");
        }
    }

    /**
     * ПОЛІМОРФІЗМ (Пасивна дія)
     * Реакція на смерть союзника.
     */
    @Override
    public void onAllyDied(Droid fallenAlly) {

        if (this.isAlive()) {
            // Перевіряємо шанс (7%)
            if (isChanceTrue(this.getAbilityChance())) {
                System.out.println("!!! 🌟 " + this.getName() + " воскрешає " + fallenAlly.getName() + " !!!");
                fallenAlly.setAlive(true);
                fallenAlly.setCurrentHP((int) (fallenAlly.getMaxHP() * 0.30)); // 30% ХП
                fallenAlly.setShieldDown(true);
                fallenAlly.setShield(0);
            }
        }
    }

    /**
     * Перевизначаємо для коректної роботи.
     */
    @Override
    public void onRoundEnd() {
        super.onRoundEnd();
    }

}