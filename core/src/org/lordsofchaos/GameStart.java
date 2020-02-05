package org.lordsofchaos;

import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;


public class GameStart{
    
    protected int wave = 1;


    
    public void start() throws InterruptedException{

        //names will later need to be given
        String attackername = "blank";
        String defendername = "blank";

        Attacker attacker = new Attacker(attackername);
        Defender defender = new Defender(defendername);

        
        while (defender.getHealth() != 0) {

            //GameController.initialise();

            //countdown for attacker to put troops down
            countdown();

            //countdown for defence to put towers down
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