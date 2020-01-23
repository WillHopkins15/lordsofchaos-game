package main.java.org.lordsofchaos;


public class Player {
     protected String Name;
     protected PlayerType playerType;
     protected int currentMoney;

     public Player(String Name, PlayerType PT, int currentMoney) {
        SetName(Name);
        SetPlayerType(playerType);
        SetCurrentMoney(currentMoney);
     }

     public void SetName(String Name) {
         this.Name = Name;
     }

     public String GetName() {
         return Name;
     }

     public void SetPlayerType(PlayerType playerType) {
         this.playerType = playerType;
     }

     public PlayerType GetPlayerType() {
         return playerType;
     }

     public void SetCurrentMoney (int currentMoney) {
         this.currentMoney =  currentMoney;
     }

     public int GetCurrentMoney() {
         return currentMoney;
     }

     public void AddMoney(int money) {
         currentMoney = currentMoney + money;
     }


     //not sure about method name can be changed later
     //method may become redundant later
     public void loseMoney(int money) {
        currentMoney = currentMoney - money;
     }


}