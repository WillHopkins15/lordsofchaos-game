package org.lordsofchaos.player;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;
import org.lordsofchaos.player.Player;

public class Defender extends Player {
    protected int Health;
    protected RealWorldCoordinates coordinates;

    public Defender(String Name) {
        super(Name);
        setCurrentMoney(50);
        setHealth(100);
        //change this when the actual coordinate for the game is decided
        RealWorldCoordinates coord = new RealWorldCoordinates(19,19);  
        setCoordinates(coord);
        setMoneyBoost(50);
        
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

        
}