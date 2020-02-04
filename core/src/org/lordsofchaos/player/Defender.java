package org.lordsofchoas.player;

import org.lordsofchaos.coordinatesystems.RealWorldCoordinates;

public class Defender extends Player{
    protected int Health;
    protected RealWorldCoordinates coordinates;

    public Defender(String Name, int CurrentMoney, int Health, RealWorldCoordinates coordinates, int moneyboost) {
        super(Name, PT, CurrentMoney);
        setHealth(100);
        setCoordinates(coordinates);
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