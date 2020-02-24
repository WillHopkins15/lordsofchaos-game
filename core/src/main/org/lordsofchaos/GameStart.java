package org.lordsofchaos;

import org.lordsofchaos.player.Attacker;
import org.lordsofchaos.player.Defender;

public class GameStart {

    // names will later need to be given
    protected final static String ATTACKERNAME = "blank";
    protected final static String DEFENDERNAME = "blank";

    public static Attacker attacker = new Attacker(ATTACKERNAME);
    public static Defender defender = new Defender(DEFENDERNAME);

    public void start() throws InterruptedException {

        while (defender.getHealth() != 0) {

            // GameController.initialise();

            // countdown for attacker to put troops down
            countdown();

            // countdown for defence to put towers down
            countdown();

            while (!GameController.troops.isEmpty()) {

                moveTroops();
                shootTroops();

            }

            plusWave();
            attacker.addMoney();
            defender.addMoney();

        }

        // end of game

    }

    public void countdown() throws InterruptedException {
        int timer = 30;

        while (timer >= 0) {
            timer = timer - 1;
            Thread.sleep(1000);
        }
    }

    public void plusWave() {
        GameController.wave += 1;
    }

    public void moveTroops() {
        int size = GameController.troops.size();

        for (int i = 0; i < size; i++) {
            (GameController.troops.get(i)).move(0f);

            if (GameController.troops.get(i).getAtEnd()) {
                GameController.troops.remove((GameController.troops.get(i)));
            }
        }
    }

    public void shootTroops() {
        if (!GameController.towers.isEmpty()) {
            for (int j = 0; j < GameController.towers.size(); j++) {
                // GameController.towers.get(j).shoot();
            }
        }
    }

}