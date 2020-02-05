package org.lordsofchaos.player;


public class Player {
     protected String Name;
     protected int currentMoney;
     protected int moneyBoost;

     public Player(String Name) {
        setName(Name);
     }

     public void setName(String Name) {
         this.Name = Name;
     }

     public String getName() {
         return Name;
     }

     public void setCurrentMoney (int currentMoney) {
         this.currentMoney =  currentMoney;
     }

     public int getCurrentMoney() {
         return currentMoney;
     }

     public void setMoneyBoost(int moneyboost) {
         this.moneyboost = moneyboost;
     }

     public int getMoneyBoost() {
         return moneyboost;
     }

     //not sure about method name can be changed later
     //method may become redundant later
     public void spendMoney(int money) {
        currentMoney = currentMoney - money;
     }

     public void addMoney(int money) {
        currentMoney = currentMoney + money;
    }

     public void addMoney() {
         currentMoney = currentMoney + moneyBoost;
     }


}