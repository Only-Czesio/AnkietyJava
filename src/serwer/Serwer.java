package serwer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serwer {
    private static final int PORT = 6000;

    public static void main(String[] args) {
        BazaDanych baza = new BazaDanych();
        System.out.println("SERWER: Start na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowe połączenie: " + clientSocket.getInetAddress());

                new Thread(new ObslugaKlienta(clientSocket, baza)).start();
            }
        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        }
    }
}