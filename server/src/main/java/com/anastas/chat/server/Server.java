package com.anastas.chat.server;

import com.anastas.chat.protocol.Message;
import com.anastas.chat.protocol.Packet;
import com.anastas.chat.protocol.PacketType;
import com.anastas.chat.protocol.Presence;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Server {
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        int portNumber = Integer.parseInt(args[0]);

        Map<String, ClientSession> clients = new ConcurrentHashMap<>();
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                    String name = in.readUTF();
                    final ClientSession clientSession = new ClientSession(clientSocket, name);
                    Consumer<String> onMessage = message -> {
                        try {
                            String[] array = message.split("\\s+", 3);
                            if (array[0].trim().equalsIgnoreCase(":list")) {
                                Presence presence = new Presence();
                                for (ClientSession session : clients.values()) {
                                    presence.add(session.getName(), session.isOffline());
                                }
                                String json = gson.toJson(presence);
                                serializeAndSend(clientSession, json, PacketType.PRESENCE);
                            } else if (array[0].equalsIgnoreCase(":kick")) {
                                String nameToKick = array[1];
                                for (ClientSession session : clients.values()) {
                                    if (session.getName().equals(nameToKick)) {
                                        Message messageKick = new Message(clientSession.getName(), array[2]);
                                        String json = gson.toJson(messageKick);
                                        serializeAndSend(session, json, PacketType.MESSAGE);
                                        session.closeSocket();
                                    }
                                }
                            } else {
                                for (ClientSession session : clients.values()) {
                                    if (session != clientSession) {
                                        Message messageKick = new Message(clientSession.getName(), message);
                                        String json = gson.toJson(messageKick);
                                        serializeAndSend(session, json, PacketType.MESSAGE);
                                    }
                                }
                            }
                        } catch (IOException ignored) {
                        }
                    };

                    clientSession.setOnMessage(onMessage);
                    new Thread(clientSession).start();
                    clients.put(clientSession.getName(), clientSession);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void serializeAndSend(ClientSession session, String json, PacketType packetType) throws IOException {
        Packet packet = new Packet(packetType, json);
        String userJsonPacket = gson.toJson(packet);
        session.sendMessage(userJsonPacket);
    }
}
