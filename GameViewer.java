public class GameViewer {

    public GameViewer() {

    }

    public void displayMenu() {
        System.out.println("1. Go to Shop\n2. Choose Character name\n3. Choose Weapon" +
                            "\n4 Choose armour\n5. Start Battle\n6. Exit Game");
    }

    public void displayAvailableWeapons(Player player) {
        for(Item item : player.getBag().getTypeList('W')) {
            System.out.println(item.toString());
        }
    }

    public void displayAvailableArmor(Player player){
        for(Item item : player.getBag().getTypeList('A')) {
            System.out.println(item.toString());
        }
    }
    
    public void finalPromptExit() {
        System.out.println("You are about to leave the game, are you sure you want to quit? Yes(Y/y) No(N/n)");
    }


}