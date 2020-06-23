package com.anastas.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientSession implements Runnable{

    private final Socket clientSocket;
    private volatile Consumer<String> onMessage;
    private final String name;
    private volatile boolean isOffline = false;

    public ClientSession(Socket clientSocket, String name){
        this.clientSocket = clientSocket;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            System.out.println(name + " is online");
            while (true) {
                String message = in.readUTF();
                //implement consumer
                //this method accepts one value and performs the operation on the given argument
                onMessage.accept(message);
            }
        } catch (EOFException e){
            System.out.println(name + " is offline");
            isOffline = true;
        } catch (IOException ignored) {
        }
    }

    public void sendMessage(String message) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
        outputStream.writeUTF(message);
    }

    public void setOnMessage(Consumer<String> onMessage){
        this.onMessage = onMessage;
    }

    public String getName(){
        return name;
    }

    public boolean isOffline(){ return isOffline; }

    public void closeSocket() throws IOException {
        clientSocket.close();
    }
}
