package com.anastas.chat.client;

import com.anastas.chat.protocol.*;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String nick = args[2];

        try {
            Socket socket = new Socket(host, port);

            DataOutputStream out =
                    new DataOutputStream(socket.getOutputStream());
            out.writeUTF(nick);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            Thread networkReceiverThread = new Thread(() -> {
                try {
                    while (true) {
                        //the method blocks and waits for next input
                        String serializedPacket = in.readUTF();
                        Gson gson = new Gson();
                        Packet packet = gson.fromJson(serializedPacket, Packet.class);
                        PacketType packetType = packet.getType();
                        String payloadJson = packet.getPayloadJson();
                        if(packetType.equals(PacketType.MESSAGE)) {
                            Message message = gson.fromJson(payloadJson, Message.class);
                            System.out.println(message.getSender() + ": " + message.getBody());
                        } else {
                            Presence presence = gson.fromJson(payloadJson, Presence.class);
                            List<SingleUserPresence> list = presence.getPresence();
                            for (SingleUserPresence singleUserPresence : list) {
                                System.out.print(singleUserPresence.getName() + ": ");
                                if (singleUserPresence.isOffline()) {
                                    System.out.println(" is offline");
                                } else {
                                    System.out.println(" is online");
                                }
                            }
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("Lost connection with the server");
                } catch (IOException ignored) {
                }
            });
            networkReceiverThread.start();

            Thread consoleReaderThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                try {
                    while (true) {
                        String message = scanner.nextLine().trim();
                        if(message.startsWith(":")) {
                            String[] array = message.split("\\s+", 2);
                            if(array[0].trim().equalsIgnoreCase(":bye")) {
                                if(array.length >= 2) {
                                    out.writeUTF(array[0].substring(1) + " " + array[1]);
                                } else {
                                    out.writeUTF("bye");
                                }
                                socket.close();
                                break;
                            } else {
                                out.writeUTF(message);
                            }
                        } else {
                            if(!message.isEmpty()) {
                                out.writeUTF(message);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            consoleReaderThread.setDaemon(true);
            consoleReaderThread.start();
        } catch (IOException ex) {
            System.out.println("Can't connect with the server: " + ex.getMessage());
        }
    }
}
