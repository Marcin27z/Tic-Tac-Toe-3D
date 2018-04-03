package main.java;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class MessagePoster extends Thread {

    //private DataOutputStream out;
    private ObjectOutputStream out;
    private Socket socket;
    BlockingQueue<Model> queue = new ArrayBlockingQueue<>(10);

    MessagePoster(ObjectOutputStream out, Socket socket) {
        this.out = out;
        this.socket = socket;
    }

    /*public void run() {
        while (true) {
            try {
                Model model = queue.take();
                //System.out.println(model.player[0].getName() + " " + model.player[1].getName());
                out.writeObject(model);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    void send(Model model) {
        try {
            System.out.println(model.player[0].getName() + " " + model.player[1].getName());
            out.writeObject(model);
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}