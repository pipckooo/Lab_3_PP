package droids;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/*
* Дефендер Буде зменшувати урон по дроїдам команди та з 33% шансом
*  заблокує атаку на себе,матиме щит в 10% свого хп
*
* Атакер матиме підвищений  кріт шанс,абілка його буде відповідь ударом з 25%
* хіллер зможе відхілити 20 % хп  дроїдам (в 3 на 3 це буде лише одному а в 3 на 4 це буде 2 )
* також хіллер матиме підвищене хп та особисту регенерацію 5% що раунду
* бафф дроїд матиме два бафи ,зокрема
* 1.баф 1 атаки дроїду на наступний раунд
* 2.баф Щит,дроїд зможе витримати урон,,рівний 30% його хп
* 3. пасивка,з шансом 7% він зможе воскресити союзника,якщо 3 на 4 шанс 14%
*
* зроби код ,це гра дроїдів ,організуй битви 3 на 3 та 3 на 4 проти компютера*/
@Getter
@Setter
public  abstract class Droid {
    private String name;
    private int maxHP,minHP;
    @Setter int currentHP;
    private int maxATC,minATC;
    private int critChance;
    @Setter  private boolean isAlive;
    private double shieldMultiplier;
    @Setter  private boolean shieldDown;
    private int attackInCombat ;
    @Setter private int shield;
    @Setter  private int attackBuffTurns;
    private int maxCritAttack;
    private int abilityChance;
    private String type;
    @Setter  private int damageTaken;

    protected List<Droid> team;
    /**
     * -- GETTER --
     *  Допоміжний метод, щоб DroidBattle міг запитати,
     *  кого цей дроїд вважає ворогом.
     */
    protected List<Droid> enemies;
    protected Droid (String name, String type, int maxHP, int minATC, int maxATC,
                     int critChance, int maxCritAttack, int abilityChance, double shieldMultiplier){
        this.name = name; // A-Value: Встановлюємо ім'я
        this.type = type;
        this.maxHP = maxHP;
        this.minATC = minATC;
        this.maxATC = maxATC;
        this.critChance = critChance;
        this.maxCritAttack = maxCritAttack;
        this.abilityChance = abilityChance;
        this.shieldMultiplier = shieldMultiplier;

        // A-Value: Встановлюємо початкові бойові значення
        this.currentHP = this.maxHP;
        this.isAlive = true;
        this.shieldDown = false;
        this.shield = this.calculateInitialShield();
        this.damageTaken = 0;
        this.attackBuffTurns = 0;
    }
    public void setTeams(List<Droid> team, List<Droid> enemies) {
        this.team = team.stream()
                .filter(d -> d != this)
                .collect(Collectors.toList());
        this.enemies = enemies;
    }
    public static int randomInt (int min, int max){
        return  ThreadLocalRandom.current().nextInt(min,max+1);
    }
    public static boolean ifChanceTrue(int chance){
        return randomInt(1,100)<chance;
    }
    public int getRandomisedATC(){
        return randomInt(this.getMinATC(), this.getMaxATC());
    }
    public  void sheildDamageAbsorbshion(int damage,int damageAfterShield){
        if(this.shield>0 && !this.shieldDown){

            if(this.shield==damage){
                this.shield-=-damage;
                damageAfterShield=0;
                if(this.shield==0){
                    this.shieldDown=true;
                }
            }
            else {damageAfterShield-=this.shield;
                this.shield=0;
                this.shieldDown=true;
            }

        }
    }


    public  boolean checkIfDroidIsAlive(){
        return this.isAlive;
    }

    public final void takeDamage(int damage,Droid attacker){
        if (!this.isAlive) return;

        int damageAfterShield = damage;
        if (!this.shieldDown && this.shield > 0) {
            if (this.shield >= damage) {
                this.shield -= damage;
                damageAfterShield = 0; // Щит поглинув усю атаку
                System.out.println("Щит " + this.getName() + " поглинув " + damage + " пошкодження.");
            } else {
                damageAfterShield = damage - this.shield;
                this.shield = 0;
                this.shieldDown = true;
                System.out.println("Щит " + this.getName() + " був знищений!");
            }
        }

        // Поліморфний виклик. Кожен дроїд сам вирішить, як реагувати.
        onDamageTaken(damageAfterShield, attacker);
    }
    protected void attackSumary(int damage,Droid attacker){
        if(damage<0){
            return;
        }
        this.currentHP-=damage;
        this.damageTaken+=damage;
        if(this.currentHP<=0){
            this.isAlive=false;
            this.currentHP=0;
            System.out.println( attacker.name + " знищив " +this.name);
        }

    }
    public int calculateInitialShield(){
        return (int) (this.maxHP*this.shieldMultiplier);
    }

    public static boolean isChanceTrue(int chance){
        return randomInt(1,100)<=chance;
    }
    public boolean checkAbilityChance(){
        return isChanceTrue(this.abilityChance);
    }
    public static int CheckForType(List<Droid> droids,String type){
        int count = 0;
        for(Droid droid : droids){
            if(droid.type.equals(type)){
                count++;
            }

        }
        return count;
    };
    public abstract void performMainAction()

    ;
    public void attack(Droid target){
        if(!this.checkIfDroidIsAlive()){return;}
        if(!target.checkIfDroidIsAlive()){
            System.out.println(target.name+"вже загинув");
            return;
        }

        int damage = this.getRandomisedATC();

        if(this.getAttackBuffTurns()>0){
            damage=(int)(damage*1.5);
            System.out.println(this.name+" атакує з бафом " +target.name);

        }
        System.out.println(this.name+" атакує " + target.name);
        target.takeDamage(damage,this);
    }
    public void onRoundEnd() {
        // Зменшуємо лічильник бафу атаки
        if (this.attackBuffTurns > 0) {
            this.attackBuffTurns--;
            if (this.attackBuffTurns == 0) {
                System.out.println("Баф атаки " + this.getName() + " закінчився.");
            }
        }
    }

    public void heal(int amount) {
        this.currentHP += amount;
        if (this.currentHP > this.maxHP) {
            this.currentHP = this.maxHP;
        }
    }
    protected void onDamageTaken(int damage, Droid attacker) {
        if (damage <= 0) return;

        this.currentHP -= damage;
        this.damageTaken += damage;
        System.out.println(this.name + " отримав " + damage + " пошкодження.");

        if (this.currentHP <= 0) {
            this.currentHP = 0;
            if (this.isAlive) { // Перевіряємо, чи він помер *саме зараз*
                this.isAlive = false;
                System.out.println("☠️ " + this.name + " знищений!");

                // Повідомляємо всіх живих союзників про нашу смерть
                if (this.team != null) {
                    for (Droid ally : this.team) {
                        if (ally.isAlive()) {
                            ally.onAllyDied(this); // 'this' - це дроїд, який щойно помер
                        }
                    }
                }
            }
        }
    }
    public void onAllyDied(Droid fallenAlly) {

    }



    public List<Droid> getEnemies() {
        return this.enemies;
    }


}



