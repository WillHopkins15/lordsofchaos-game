package org.lordsofchaos.network;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Provides methods for handling the case when a server is run on a different machine. The
 * knownhosts file stores the hostnames of all machines that have run the server. This allows the
 * clients to resolve a connection with a server, no matter what system is running it, without
 * having to know the server IP. Also it avoids sending packets to all systems on the local network,
 * looking for a connection.
 *
 * @author Will Hopkins
 */
public class HostManager {

    private static final String HOSTFILE = "knownhosts";

    /**
     * @return A list of known hostnames
     * @throws IOException If an I/O error occurs opening the source file
     */
    public static ArrayList<String> getHosts() throws IOException {
        ArrayList<String> hosts = new ArrayList<>();
        String line;
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(HOSTFILE)) {
            BufferedReader file = new BufferedReader(new InputStreamReader(stream));
            while ((line = file.readLine()) != null) {
                hosts.add(line);
            }
        }
        return hosts;
    }

    /**
     * Adds the specified hostname to the knownhosts file. If the hostname exists in the file then
     * it is not added again.
     *
     * @param hostname The server to add's hostname
     * @throws IOException If an I/O error occurs opening the source file
     */

    @Deprecated
    public static void addHost(String hostname) throws IOException {
        if (getHosts().contains(hostname)) {
            return;
        }
        try (FileWriter fw = new FileWriter(HOSTFILE, true); PrintWriter file = new PrintWriter(
            fw)) {
            System.out.printf("Host %s not recognised... Adding to known hosts\n", hostname);
            file.println(hostname);
        }
    }

    /**
     * Checks if the host the server is running on is in the hostname file
     *
     * @param hostname hostname of device running the server
     * @return true if hostname is in the knownhosts file
     * @throws Exception if hostname is not in the knownhosts file
     */
    public static boolean hostRecognised(String hostname) throws Exception {
        if (getHosts().contains(hostname)) {
            return true;
        }
        throw new Exception("Add " + hostname + " to the hosts file.");
    }
}
