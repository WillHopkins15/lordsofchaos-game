package org.lordsofchaos;

import org.lordsofchaos.network.GameClient;

import java.util.Scanner;

public class Game
{
    public static void main(String[] args) {
        setupClient();
    }
    
    private static void setupClient() {
        GameClient gc = new GameClient();
        Scanner scan = new Scanner(System.in);
        String msg = "";
        while (!msg.equals("end")) {
            System.out.print("Message: ");
            msg = scan.nextLine();
            gc.sendEcho(msg);
        }
        gc.close();
    }
}
