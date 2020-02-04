package org.lordsofchoas.player;

public class Attacker extends Player{

    public Attacker(String Name, int CurrentMoney) {
        super(Name, CurrentMoney);
        setMoneyBoost(100);
    }

}