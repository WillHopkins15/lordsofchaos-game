package org.lordsofchaos;

import java.util.Timer;
import java.util.TaskTimer;


public class GameStart{
    
    protected int wave = 1;


    
    public void start() throws InterruptedException{

        String attackername;
        String defendername;

        Attacker attacker = new Attacker(attackername); 
        Defender defender = new Defender(defendername);

        
        while (defender.getHealth() != 0) {

            GameController.initalise();

            //countdown for attacker to put troops down
            countdown();

            //countdown for defence to put twoers down
            countdown();

            plusWave();
            attacker.addMoney();
            defender.addMoney();


        }

        //end of game

    }

    public void countdown() throws InterruptedException {
        int timer = 30;

        while (timer >= 0) {
            timer = timer - 1;
            Thread.sleep(1000);
        }
    }

    public void plusWave() {
        wave += 1;
    }





}