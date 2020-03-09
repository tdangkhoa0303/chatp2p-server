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

    public String getName() {
        return name;
    }

    @Override

    public void run() {
        String received;
        while (true) {
            try {
                received = dis.readUTF();
                if (received.equals("logout")) {
                    System.out.println("[OK] User [" + this.name + "] logged out.");
                    this.s.close();
                    ConsoleServer.removeClient(this.name);
                    break;
                }
                StringTokenizer stk = new StringTokenizer(received, "#");
                String recipient = stk.nextToken();
                String content = stk.nextToken();
                ConsoleServer.clientHandlerMap.get(recipient).dos.writeUTF("5#" + name + "#" + content);
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
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
