/*
 * 0: Fail
 * 1: OK
 * 2: New user
 * 3: Active client list
 */

import com.sun.jdi.InternalException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

public class ConsoleServer {
    final static int PORT = 1402;
    static HashMap<String, ClientHandler> clientHandlerMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Server is started on port " + PORT);
        try {
            Socket client;
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                client = serverSocket.accept();
                if (client != null) {
                    DataInputStream dis = new DataInputStream(client.getInputStream());
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());

                    String request = dis.readUTF();
                    String[] tmp = request.split("#");
                    if (tmp[0].equals("2")) {
                        String userName = tmp[1];
                        if (clientHandlerMap.containsKey(userName)) {
                            dos.writeUTF("0");
                            System.out.println("[FAIL] New client login with name [" + userName + "]");

                        } else {
                            dos.writeUTF("1#" + generateActiveList(""));
                            ClientHandler clientHandler = new ClientHandler(client, userName, dos, dis);
                            Thread t = new Thread(clientHandler);
                            clientHandlerMap.put(userName, clientHandler);
                            updateActiveClient();
                            t.start();
                            System.out.println("[OK] New client login with name [" + userName + "]");
                        }
                    }
                    client = null;
                    dis = null;
                    dos = null;
                }
                Thread.sleep(1000);
            }
        } catch (InternalException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static String generateActiveList(String currentUser) {
        Set<String> activeClient = clientHandlerMap.keySet();
        StringBuilder message = new StringBuilder();
        for (String client : activeClient) {
            if (!client.equals(currentUser)) {
                message.append(message.length() > 0 ? ("#" + client) : client);
            }
        }
        return message.toString();
    }

    static void removeClient(String userName) {
        clientHandlerMap.remove(userName);
        updateActiveClient();
    }

    static void updateActiveClient() {
        for (ClientHandler clientHandler : clientHandlerMap.values()) {
            String msg = generateActiveList(clientHandler.getName());
            try {
                clientHandler.dos.writeUTF("3#" + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
