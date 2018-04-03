package main.java;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Client extends Thread {
    private Socket socket;
/*    private BufferedReader in;
    private DataOutputStream out;*/
    private InetAddress address;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int port;
    private MessagePoster messagePoster;
    private MessageWaiter messageWaiter;
    private List<NetworkEventListener> networkEventListeners = new ArrayList<>();
    private List<HandshakeListener> handshakeListeners = new ArrayList<>();

    Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {

            socket = new Socket(address, port);

            socket.setKeepAlive(true);
            /*in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());*/
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());


            /*out.writeBytes("Password\n");//init handshake/send password
            String response = in.readLine();*/
            //while(networkEventListeners.size() == 0);
            /*networkEventListeners.get(0).newMessageArrived(response);*/
            out.writeObject(handshakeListeners.get(0).getResponse());
            out.reset();
            //out.writeObject(handshakeListeners.get(0).getResponse().getTurn());
            Model model = null;
            try {
                model = (Model) in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            handshakeListeners.get(0).getResponse().player[0].setName(model.player[0].getName());
            handshakeListeners.get(0).getResponse().setTurn(model.getTurn());
            messagePoster = new MessagePoster(out, socket);
            messageWaiter = new MessageWaiter(in, socket);
            /*messagePoster.start();
            messageWaiter.start();*/

            for (NetworkEventListener networkEventListener : networkEventListeners)
                networkEventListener.establishedConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandshakeListener(HandshakeListener toAdd) {
        handshakeListeners.add(toAdd);
    }

    public void addNetworkEventListener(NetworkEventListener toAdd) {
        networkEventListeners.add(toAdd);
    }

    MessageWaiter getMessageWaiter() {
        return messageWaiter;
    }

    MessagePoster getMessagePoster() {
        return messagePoster;
    }

    MessagePoster startMessagePoster() {
        messagePoster.start();
        return messagePoster;
    }

    MessageWaiter startMessageWaiter() {
        messageWaiter.start();
        return messageWaiter;
    }
}
