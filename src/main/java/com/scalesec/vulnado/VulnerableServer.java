package com.scalesec.vulnado;

import java.io.*;
import java.net.*;
import java.util.*;

public class VulnerableServer {

    public static void main(String[] args) {
        // Ανοιχτές θύρες για ακούσματα
        openPort(8080);  // HTTP θύρα
        openPort(3306);  // MySQL θύρα
        openPort(21);    // FTP θύρα
    }

    public static void openPort(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);

            // Ο Server παραμένει σε αναμονή για συνδέσεις
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Διαβάζει δεδομένα από τον client
            String clientInput = in.readLine();
            System.out.println("Received: " + clientInput);

            // SQL Injection Vulnerability: υποθετικό παράδειγμα χειρισμού εισόδου χωρίς έλεγχο
            String query = "SELECT * FROM users WHERE username = '" + clientInput + "';";
            System.out.println("Executing query: " + query);

            // Απαντάει στον client
            out.println("Query Executed: " + query);

            // Χωρίς κρυπτογράφηση δεδομένων
            out.println("Plain-text response: No encryption used here!");

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
