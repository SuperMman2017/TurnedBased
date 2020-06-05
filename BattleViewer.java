import java.util.ConcurrentModificationException;
import java.util.InputMismatchException;
import java.util.LinkedList;
public class BattleViewer {
    //A list of players in the battlefield
    private Player player;
    private Enemy enemy;
    private BattleController battleController;
    private EnemyFactory enemyFactory;
    private UserInterface ui;
    
    public BattleViewer(UserInterface ui, Player player, EnemyFactory enemyFactory) {
        this.player = player;
        this.ui = ui;
        this.enemyFactory = enemyFactory;
        enemy = enemyFactory.getRandomEnemy();
        battleController = new BattleController(player);
    }

    boolean playerTurn = true;
    /*Start the battle*/
    public void battle() {
        changeEnemy();
        if(enemy == null) {
            System.out.println("Enemy null");
        }
        else if(player == null) {
            System.out.println("Player null");
        }
        while(player.isAlive() && enemy.isAlive()) {
            battleController.displayPlayer();
            battleController.displayEnemy(enemy);
            displayPlayerChoice(player);
            int choice = playerBattleChoice();
            
            if(choice == 1) {
                battleController.playerAttacks();
                battleController.displayLastMessage();
                battleController.enemyAttacks();
                battleController.displayLastMessage();
            }

            else if(choice == 2){
                System.out.println("Choose an item by number.");
                System.out.println("Available items are usable as listed: ");
                displayPlayerUsables();
                System.out.println("Enter 0 to cancel your choice");
                Item item = chooseItem(player.getPlayerBag().getTypeList(Potion.POTION).size());
                if(item != null) {
                    /*item is null when player cancels item selection */
                    battleController.playerUsePotion(item);
                }
            }

            if(player.isAlive() && !enemy.isAlive()) {
                enemyDefeated();
                int goldEarned = enemy.getGoldDrop(); 
                String winMessage = new String (player.getName() + " Won!" + "\nYou picked up " + goldEarned + " gold.");
                battleController.logMove(winMessage);
                battleController.displayLog();
            }
            else if(!player.isAlive()){
                String loseMessage = new String (player.getName() + " lost." + "\nGame Over!");
                System.out.println(loseMessage);
                battleController.logMove(loseMessage);
                battleController.displayLog();
            }
        }


    }

    public Item chooseItem(int max) {
        Item itemChosen = null;
        boolean notDone = true;
        int itemChoice = 0;
        while(notDone) {
            itemChoice = ui.inputNumber();
            itemChoice = itemChoice == 0 || (itemChoice >= 1 && itemChoice <= max) ? itemChoice : -1;
            if(itemChoice == -1 ) {
                notDone = false;
            }
            /*Ask for user input again if the player enters incorrect values */
            while(itemChoice > max && itemChoice < 0 ) {
                System.out.println("This item does not exist, try again or Enter 0 to Quit");
                try {
                    itemChoice = ui.inputNumber();
                }
                    catch(InputMismatchException e ) {
                }
            }

            if(itemChoice >= 1 && itemChoice <= max) {
                Item item = player.getPlayerBag().getTypeList(Potion.POTION).get(itemChoice - 1);
                System.out.println("You chose " + item.getName()
                                               + " do you want to go with this?" +  "n\n1. Yes\n2. No");

                int finalChoice = ui.inputNumber();
                if(finalChoice == 1) {
                    notDone = false;
                }
                else {
                    System.out.println("Re-select an item from the following 1-"+max);
                }
            }
            else if(itemChoice == 0) {
                itemChosen = null;
            }
        }
        return itemChosen;
    }

    public void showPlayerInfo() {
        battleController.displayPlayer();
    }

    public int playerBattleChoice() {
        int choice = -1;
        while(choice == -1) {
            try {
                choice = ui.inputNumber();             
            }
            catch(InputMismatchException e){
                choice = -1;
            }
            choice = choice >= 1 && choice <= 2 ? choice : -1; 
            if(choice == -1) {
                System.out.println("Your recent input was not an option. Try again.");
                choice = -1;
            }
        }
        return choice;
    }
    
    public void displayPlayerChoice(Character player) {
        System.out.println("1. Attack\n2. Bag");
    }
    
    /** This method initiates the enemy to attack the player**/
    public void enemyAttacks(){
        int attack = enemy.attack(battleController.log);
        player.setHealth(Math.max(0,(Math.max(0, player.getDefense() - attack) )) );
    }

    /** This method displays the usable items the player has in their bag**/
    public void displayPlayerUsables() {
        LinkedList<Item> list = player.getPlayerBag().getTypeList(Potion.POTION);
        for(Item item : list) {
            System.out.print(item.getName() + " ");
            if(item.getDescription().charAt(0) == PotionOfHealing.POTION_OF_HEALING) {
                System.out.print(   PotionOfHealing.POTION_HEALING_DESCRIPTION 
                                    + " for " + item.getMinEffect() + "-" + item.getMaxEffect());
            }
            else if(item.getDescription().charAt(0) == PotionOfDamage.POTION_OF_DAMAGE) {
                System.out.print(   PotionOfDamage.POTION_DAMAGE_DESCRIPTION
                                    + " for " + item.getMinEffect() + "-" + item.getMaxEffect());
            }
        }
    }

    public void changeEnemy() {
        this.enemy = enemyFactory.getRandomEnemy();
        battleController.setEnemy(enemy);
    }

    public void enemyDefeated() {
        try {
            enemyFactory.decreaseEnemyChances(5);
            enemyFactory.setSpecificEnemy(Dragon.DRAGON, enemyFactory.getChance(Dragon.DRAGON) + 20);
        }
        catch(InvalidEnemyException e ) {
            e.printStackTrace();
        }  
        catch(ConcurrentModificationException e ) {
            System.out.println("What happened?");
            e.printStackTrace();
        }
    }
}