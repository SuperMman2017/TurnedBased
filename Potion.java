
public class Potion extends Item {

    /*potionType determines what type of potion it is e.g D = damage, H  = heal */
    private char potionType;
    private int effect;

    public static final String HEAL_DESC = "Single use; User recovers health.";
    /*potion constructor requires a name, cost and character representing the type of potion it is */
    public Potion(String name,int cost, char potionType) {
        this.name = name;
        this.cost = cost;
        this.potionType = potionType;
    }
    
    public void setEffect(int effect) {
        this.effect = effect;
    }

    public int getEffect() {
        return effect;
    }

    public void setPotionType(char potionType) {
        this.potionType = potionType;
    }
    public char getPotionType() {
        return this.potionType;
    }

    @Override public void doEffect(Character character) {

    }
}