package org.lordsofchaos.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Provides methods for handling the case when a server is run on a different machine.
 * The knownhosts file stores the hostnames of all machines that have run the server. This
 * allows the clients to resolve a connection with a server, no matter what system is running
 * it, without having to know the server IP. Also it avoids sending packets to all systems on the
 * local network, looking for a connection.
 *
 * @author Will Hopkins
 */
public class HostManager
{
    private static final String HOSTFILE = new File("knownhosts").getAbsolutePath();
    
    /**
     * @return A list of known hostnames
     * @throws IOException If an I/O error occurs opening the source file
     */
    public static ArrayList<String> getHosts() throws IOException {
        ArrayList<String> hosts = new ArrayList<>();
        try (Scanner file = new Scanner(Paths.get(HOSTFILE))) {
            while (file.hasNext())
                hosts.add(file.nextLine());
        }
        return hosts;
    }
    
    /**
     * Adds the specified hostname to the knownhosts file. If the hostname exists in the
     * file then it is not added again.
     *
     * @param hostname The server to add's hostname
     * @throws IOException If an I/O error occurs opening the source file
     */
    public static void addHost(String hostname) throws IOException {
        if (getHosts().contains(hostname)) {
            return;
        }
        try (FileWriter fw = new FileWriter(HOSTFILE, true); PrintWriter file = new PrintWriter(fw)) {
            System.out.printf("Host %s not recognised... Adding to known hosts\n", hostname);
            file.println(hostname);
        }
    }
}
