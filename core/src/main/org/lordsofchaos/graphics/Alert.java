package org.lordsofchaos.graphics;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Alert {
    private float currentTime;
    private float targetTime;
    private String text;
    private int x;
    private int y;
    private BitmapFont font;
    private boolean readyToDelete;
    private Screen currentScreen;
    public  Alert(float targetTime, BitmapFont font, String text, int x, int y, Screen currentScreen){
        this.targetTime = targetTime;
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.currentScreen = currentScreen;
        readyToDelete = false;
    }
    public void update(float deltaTime, SpriteBatch batch, Screen screen){
        currentTime += deltaTime;
        if(currentTime < targetTime){
            if(screen == currentScreen || currentScreen == null)
                font.draw(batch,text,x,y);
        }
        else{
            readyToDelete = true;
        }
    }
    public void dispose(){
        font.dispose();
    }
    public boolean getDeleteStatus(){
        return readyToDelete;
    }
    public Screen getCurrentScreen(){
        return currentScreen;
    }
    public String getText(){
        return text;
    }
}
