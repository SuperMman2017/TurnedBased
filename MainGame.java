import java.text.NumberFormat;
import java.util.LinkedList;

public class MainGame {
    public static final String regex = ",";
    /*potion has 1  */
    public static final int ITEM_ARG = 6;
    public static final int WEAPON_ARG = 7;
    public static final String shopFile = "shopdata.csv";
    public static final char ARMOR = 'A';
    public static final char WEAPON = 'W';
    public static final char POTION = 'P';
    public static final char POTION_HEAL = 'H';
    public static final char POTION_DAMAGE = 'D';
    private static Loader loader = new TextFileLoader(); 
    private static ItemFactory itemFactory = new ItemFactory();
    private static EnemyFactory enemyFactory = new EnemyFactory();

    public static void main(String []args) {
        Shop shop = new Shop();
        Player player = new Player();
        try {
            loadItems(itemFactory, shop, loader.Load(shopFile));
        }
        catch(InvalidFileException e) {
            System.out.println(e.getMessage());
        }
        try{
            loadPlayer(shop, player);
        }
        catch(IllegalArgumentException er) {
            System.out.println(er.getMessage());
        }
        catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Player weapon is " + player.getCurrentWeapon().getName() + 
                            " \nCurrent armor: " + player.getCurrentArmor().getName());
    }

    /*Sets player's weapon and armor to the cheapest items from the shop */
    public static void loadPlayer(Shop shop, Player player) {
        if(shop.getList().size() == 0 ) {
            throw new NullPointerException("Shop is empty, no items have loaded in the shop");
        }
        Item cheapestArmor = shop.cheapestArmor(player);
        Item cheapestWeapon = shop.cheapestWeapon(player);
        if(cheapestArmor != null && cheapestWeapon != null) {
            player.setArmor(cheapestArmor);
            player.setWeapon(cheapestWeapon);
        }
    }

    public static void loadItems(ItemFactory factory, Shop shop,LinkedList<String> fileContents) throws InvalidFileException {
        
        for(String content : fileContents) {
            Item item = null;
            int parseError = 0;
            String[] tokens = content.split(regex);
            
            System.out.println(tokens[2] + " " + tokens[3] + " " +tokens[4]);
            /*7 arguments for weapon */
            if(tokens.length == WEAPON_ARG) {
                parseError = 0;
                
                if(tokens[0].toUpperCase().charAt(0) == WEAPON) {
                    try{
                        Weapon weapon = new Weapon();
                        String damageType = tokens[5].trim();
                        String weaponType = tokens[6].trim();
                        String name = tokens[1].trim();
                        int minDamage = Integer.parseInt(tokens[2].trim());
                        int maxDamage = Integer.parseInt(tokens[3].trim());
                        int cost = Integer.parseInt(tokens[4].trim());
                        /**Validate that the String is nonempty String */
                        if(damageType.equals("") || weaponType.equals("") || name.equals("")) {
                            parseError = 1;
                        }
                        weapon.setStat(name, damageType ,weaponType, minDamage, maxDamage);
                        weapon.setCost(cost);
                        item = weapon;
                    }
                    catch(NumberFormatException e) {
                        parseError = 1;
                    }
                }
                else {
                    throw new InvalidFileException(new String("Contents in file failed to load, fix " + shopFile + " to avoid this error"));
                }
            }
            /*6 arguments for potions or armor*/
            else if(tokens.length == ITEM_ARG) {
                int cost = 0;
                int minEffect = 0;
                int maxEffect = 0;  
                try {
                    cost = Integer.parseInt(tokens[4].trim());
                    minEffect = Integer.parseInt(tokens[2].trim());
                    maxEffect = Integer.parseInt(tokens[3].trim());
                    parseError = 0;       
                }
                catch(NumberFormatException e) {
                    parseError = 1;
                }

                if(tokens[0].toUpperCase().charAt(0) == POTION) {
                    item = ItemFactory.createPotion(tokens[1].trim(),tokens[5].trim().charAt(0),cost,minEffect,maxEffect);
                    item.setCost(cost);
                    item.setName(tokens[1].trim());
                }

                else if (tokens[0].charAt(0) == ARMOR){
                        Armor armor = new Armor();
                        String name = tokens[1].trim();
                        String material = tokens[5].trim();

                        /*Check if name or material is a nonempty String */
                        if(name.equals("") || material.equals("")) {
                            parseError = 1;
                        }
                        armor.setItem(name, cost, material, minEffect, maxEffect);
                        item = armor;
                }
            }
            if(parseError == 0 && item != null) {
                shop.addItem(item);
                System.out.println(item.getName() + " Item added to shop");
            }
            else if (parseError == 1 ) {
                System.out.println("Error loadingFiles");
            }
        }
    }
}