package client;
import my_programm.CustomFileReader;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Client2 {

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Клиент запущен");
        List<String> commands = new ArrayList<>();

        while (true) {
            try {
                clientSocket = new Socket("localhost", 4004); // коннектимся
                System.out.println("Мы подключились к серверу");
                reader = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//            InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());

                boolean run = true;
                while (run) {
                    CompletableFuture<String> waitMessageFromServer = CompletableFuture.supplyAsync(() -> wait_new_message(in));

                    Date c_date = new Date();
                    while (!waitMessageFromServer.isDone()) {
                        do {
                            if (new Date().getTime() - c_date.getTime() > 300) {
                                waitMessageFromServer.cancel(true);
                            }
                            try {
                                if (reader.ready()) {
                                    commands.add(reader.readLine());
                                }
                            } catch (Exception e) {
//                            System.out.println("ConsoleInputReadTask() cancelled");
                            }
                        } while (!waitMessageFromServer.isDone());
                    }

                    if (waitMessageFromServer.isCancelled()) {
                        continue;
                    }

                    String mes = waitMessageFromServer.get();

                    if (mes != null) {
                        if (mes.strip().equals("Готов принимать данные")) {
                            String s = "";
                            for (String l : commands) {
                                if (l.strip().equals("exit")) {
                                    System.exit(0);
                                } else if (l.strip().contains("execute_script ")) { // execute_script smert.txt
                                    s += get_command(l, new ArrayList<>());
//                                    System.out.println(s);
                                } else if (!l.strip().equals("save")) {
                                    s += l + "\n";
                                }
                            }
//                            if (s != "") {
//                                System.out.println(s);
//                            }
                            commands.clear();
                            out.write(s + "end\n");
                            out.flush();
                        } else if (mes.strip().equals("error")) {
                            run = false;
                        } else {
                                System.out.println(mes);
                        }
                    }
                }
//                (input.contains("execute_script ")) {
//                    return this.get_list_of_commands(input.split("\s")[1]);
            } catch (Exception e) {
//            e.printStackTrace();
//                System.out.println("Не работайн");
            } finally {
                System.out.println("попытка подключения");
                Thread.sleep(1500);
            }
        }
    }

    private static String get_command(String command, ArrayList<String> blacklist) {
        String filename = command.split("\s")[1];
        String com = "";
        List<String> arr = CustomFileReader.readFile(filename);
        if (arr == null) {return "";}
        for (String s : arr) {
            if (s.contains("execute_script ") && !blacklist.contains(s.split("\s")[1])) {
                blacklist.add(s.split("\s")[1]);
                com += get_command(s, blacklist);
            } else {
                com += s + "\n";
            }
        }
        return com;
    }

    private static String wait_new_message(BufferedReader in) {
        try {
            return in.readLine();
        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Сервер умер, а в месте с ним и мы...");
//            System.exit(0);
//            throw new RuntimeException();
            System.out.println("Потеря соединения с сервером");
            return "error";
        }
//        return null;
    }
}