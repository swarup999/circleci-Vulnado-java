package com.scalesec.vulnado;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class VulnerableServer {
    public static void main(String[] args) throws Exception {
        // Open a server on port 8080
        ServerSocket serverSocket = new ServerSocket(8082);
        System.out.println("Server started on port 8082");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Vulnerability 1: Command Injection
            out.println("Enter a command to execute: ");
            String command = in.readLine();

            // Unsafe execution of system commands
            Process process = Runtime.getRuntime().exec(command);  // Command Injection vulnerability
            BufferedReader processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            out.println("Command output:");
            while ((line = processOutput.readLine()) != null) {
                out.println(line);
            }

            clientSocket.close();
        }
    }
}
