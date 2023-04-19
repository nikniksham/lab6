package server;

import my_programm.CustomFileReader;
import my_programm.Manager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Server {
    private ServerSocket server; // серверсокет
    private Map<Socket, Map<BufferedReader, BufferedWriter>> clients;
    private Map<BufferedReader, BufferedWriter> loc;
    private Socket clientSocket;
    private Date c_date;
    private boolean run = true;
    private Manager manager;

    public Server() throws IOException {
        clients = new HashMap<>();
        server = new ServerSocket(4004);
        server.setSoTimeout(1000);

    }

    public void start() throws IOException, NullPointerException {
        System.out.println("Сервер запущен");
        manager = new Manager();
        File f = new File("localsave.json");
        if (f.exists() && !f.isDirectory()) {
            manager.setFile("localsave.json", false);
        }

        try {
            while (run) {
                try {
                    CompletableFuture<Socket> waitNewClient = CompletableFuture.supplyAsync(() -> wait_new_client(server));
                    while (!waitNewClient.isDone()) {
                        for (Socket client : clients.keySet()) {
                            Map<BufferedReader, BufferedWriter> buff = clients.get(client);
                            BufferedReader in = buff.keySet().iterator().next();
                            BufferedWriter out = buff.values().iterator().next();
                            CompletableFuture<ArrayList<String>> waitMessage = CompletableFuture.supplyAsync(() -> wait_new_message(in));

                            c_date = new Date();
                            out.write("Готов принимать данные\n");
                            out.flush();
                            while (!waitMessage.isDone()) {
                                if (new Date().getTime() - c_date.getTime() > 1000) {
                                    waitMessage.cancel(true);
                                }
                            }

                            if (!waitMessage.isCancelled()) {
//                            System.out.println("!!!");
                                ArrayList<String> commands = waitMessage.get();
                                String ret = "";
                                if (commands != null && commands.size() > 0) {
//                                System.out.println("Получены сообщения:");
                                    boolean jsonsend = false;
                                    for (String com : commands) {
                                        System.out.println(client + " --> " + com);
                                        for (String s : manager.commandHandler(com)) {
                                            if (s.strip().equals("отправить json")) {
                                                jsonsend = true;
                                            } else {
                                                ret += s + "\n";
                                            }
                                        }
                                    }
                                    out.write(ret);
                                    out.flush();
                                    if (jsonsend) {
//                                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
                                        String jsonString = "";
                                        try {
                                            List<String> arr = CustomFileReader.readFile("sendData.json");
                                            if (arr == null) {
                                                throw new NullPointerException();
                                            }
                                            for (String s : arr) {
                                                jsonString += s + "\n";
                                            }
                                            Object obj = new JSONParser().parse(jsonString);
                                            JSONObject jo = (JSONObject) obj;
//                                            out.write("Готов отпралять json\n");
//                                            out.flush();
                                            out.write(jo.toString() + "\n");
                                            out.flush();
//                                            outputStreamWriter.write(obj.toString() + "\n");
//                                            outputStreamWriter.flush();
                                        } catch (Exception e) {
//                                            e.printStackTrace();
                                        } finally {
//                                            outputStreamWriter.close();
//                                            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
//                                            System.out.println("!2344");
                                        }
                                    }
//                                run = false;
//                                break;
                                }
                            } else {
//                                System.out.println("не поймали");
                            }
                        }

                        if (!run) {
                            break;
                        }

                    }

                    clientSocket = waitNewClient.get();
                    if (clientSocket != null) {
                        System.out.println("Кого-то поймали в свои сети...");
                        System.out.println(clientSocket);
                        loc = new HashMap<>();
                        loc.put(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())), new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                        clients.put(clientSocket, loc);
                    }
                } catch (Exception e) {
                    Socket gay = null;
//                    e.printStackTrace();
                    for (Socket client : clients.keySet()) {
                        try {
                            Map<BufferedReader, BufferedWriter> buff = clients.get(client);
                            BufferedWriter out = buff.values().iterator().next();
                            out.write("проверка на гея\n");
                            out.flush();
                        } catch (Exception e2) {
                            gay = client;
                            System.out.println("гей детектед");
                            break;
                        }
                    }
                    if (gay != null) {
                        Map<BufferedReader, BufferedWriter> buff = clients.get(gay);
                        BufferedWriter out = buff.values().iterator().next();
                        BufferedReader in = buff.keySet().iterator().next();
                        out.close();
                        in.close();
                        clients.remove(gay);
                        gay.close();
                        System.out.println(clients.size() + " " + gay);
                    }
//                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (manager.isChange_something()) {
                manager.save("localsave.json");
            }
            System.out.println("Сервер выключен");
        }
    }

    private static Socket wait_new_client(ServerSocket server) {
        try {
            return server.accept();
        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Тоже выключаемся");
        }
        return null;
    }

    private static ArrayList<String> wait_new_message(BufferedReader in) {
        try {
            ArrayList<String> commands = new ArrayList();
            for (Iterator<String> it = in.lines().iterator(); it.hasNext(); ) {
//                System.out.println("!!!!!!!!!!!!!!");
                String s = it.next();
                if (s.equals("end")) {break;}
                commands.add(s);
            }
//            System.out.println(commands);
            return commands;
        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Выключаемся без лишних вопросов и поломок");
        }
        return null;
    }
}
