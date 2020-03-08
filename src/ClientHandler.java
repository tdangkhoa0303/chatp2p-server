import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    final DataOutputStream dos;
    final DataInputStream dis;
    Socket s;
    private String name;

    public ClientHandler(Socket s, String name, DataOutputStream dos, DataInputStream dis) {
        this.name = name;
        this.dos = dos;
        this.dis = dis;
        this.s = s;
    }

    @Override

    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                if (received.equals("logout")) {
                    System.out.println("Logout");
                    // Remove this client:


                    this.s.close();
                    break;
                }
                StringTokenizer stk = new StringTokenizer("#");
                String recipient = stk.nextToken();
                String content = stk.nextToken();
                ConsoleServer.clientHandlerMap.get(recipient).dos.writeUTF("3#" + name + "#" + content);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.dis.close();
            this.dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
