package org.lordsofchoas.player;

public class Attacker extends Player{

    public Attacker(String Name) {
        super(Name);
        setMoneyBoost(100);
        setCurrentMoney(100);
    }

}