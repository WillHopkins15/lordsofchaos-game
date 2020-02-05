package org.lordsofchoas.player;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public class Defender extends Player{
    protected int Health;
    protected RealWorldCoordinates coordinates;

    public Defender(String Name) {
        super(Name);
        setCurrentMoney(50);
        setHealth(100);
        //change tbis when the actual coordinate for the game is decided
        RealWorldCoordinates coord = new RealWorldCoordinates(8,4);  
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