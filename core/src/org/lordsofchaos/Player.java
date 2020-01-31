package org.lordsofchaos;


public class Player {
     protected String Name;
     protected PlayerType playerType;
     protected int currentMoney;
     protected int Health;
     protected RealWorldCoordinates coordinates;

     public Player(String Name, PlayerType PT, int CurrentMoney, int Health) {
        setName(Name);
        setPlayerType(playerType);
        setCurrentMoney(currentMoney);
        setHealth(Health);
     }

     public void setName(String Name) {
         this.Name = Name;
     }

     public String getName() {
         return Name;
     }

     public void setPlayerType(PlayerType playerType) {
         this.playerType = playerType;
     }

     public PlayerType getPlayerType() {
         return playerType;
     }

     public void setCurrentMoney (int currentMoney) {
         this.currentMoney =  currentMoney;
     }

     public int getCurrentMoney() {
         return currentMoney;
     }

     public void addMoney(int money) {
         currentMoney = currentMoney + money;
     }

     public void setHealth(int Health) {
         this.Health = Health;
     }

     public int getHealth() {
         return Health;
     }

     public void setCoordinates(RealWorldCoordinates coordinates) {
         this.coordinates = coordinates;
     }

     public RealWorldCoordinates getCoordinates() {
         return coordinates;
     }

     //not sure about method name can be changed later
     //method may become redundant later
     public void loseMoney(int money) {
        currentMoney = currentMoney - money;
     }


}