import com.sun.jdi.InternalException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

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
                    System.out.println("New client login...");
                    DataInputStream dis = new DataInputStream(client.getInputStream());
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());

                    String request = dis.readUTF();
                    String[] tmp = request.split("#");
                    if (tmp[0].equals("2")) {
                        String userName = tmp[1];
                        if (clientHandlerMap.keySet().contains(userName)) {
                            dos.writeUTF("0");
                            dos.flush();
                        } else {
                            dos.writeUTF("1");
                            dos.flush();
                            clientHandlerMap.put(userName, new ClientHandler(client, userName, dos, dis));
                        }
                    }
                    client = null;
                }
            }
        } catch (InternalException | IOException e) {
            e.printStackTrace();
        }
    }
}
