package org.lordsofchaos;

import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;


public class GameStart{
    
    protected int wave = 1;
    //names will later need to be given
    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";

    public static Attacker attacker = new Attacker(ATTACKERNAME);
    public static Defender defender = new Defender(DEFENDERNAME);

    
    public void start() throws InterruptedException{

        
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