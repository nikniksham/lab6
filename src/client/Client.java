package client;
import my_programm.CustomFileReader;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Client {

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        System.out.println("Клиент запущен");
        List<String> commands = new ArrayList<>();

        try {
            clientSocket = new Socket("localhost", 4004); // коннектимся
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            boolean run = true;
            while (run) {
                CompletableFuture<String> waitMessageFromServer = CompletableFuture.supplyAsync(() -> wait_new_message(in));

                while (!waitMessageFromServer.isDone()) {
                    do {
                        try {
                            if (reader.ready()) {
                                String ls = reader.readLine();
                                if (ls.contains("execute_script ")) {
                                    commands.add(read_script(ls, new ArrayList<String>()));
                                } else {
                                    commands.add(ls + "\n");
                                }
                            }
                        } catch (Exception e) {
//                            System.out.println("ConsoleInputReadTask() cancelled");
                        }
                    } while (!waitMessageFromServer.isDone());
                }

                String mes = waitMessageFromServer.get();

                if (mes != null) {
//                    System.out.println("Получил письмо ---> " + waitMessageFromServer.get());
                    if (mes.equals("Готов принимать данные")) {
                        String s = "";
                        for (String l : commands) {
                            s += l;
                        }
                        commands.clear();
                        out.write(s + "end\n");
                        out.flush();
                    } else {
                        System.out.println(mes);
                    }
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Не работайн");
        } finally {
            System.out.println("Клиент выключен");
        }
    }

    private static String read_script(String command, List<String> used_filenames) {
        String scriptname = command.split("\s")[1];
        List<String> lcs = CustomFileReader.readFile(scriptname.strip());
        String lcomm = "";
        if (lcs == null) {
            System.out.println("Скрипт не найден --> " + scriptname);
            return "";
        }
        for (String lc : lcs) {
            if (lc.contains("execute_script ")) {
                if (!used_filenames.contains(lc.split("\s")[1])) {
                    used_filenames.add(lc.split("\s")[1]);
                    lcomm += read_script(lc, used_filenames);
                } else {
                    System.out.println("Рекурсия запрещена >_<");
                }
            } else {
                lcomm += lc + "\n";
            }
        }
        return lcomm;
    }
    private static String wait_new_message(BufferedReader in) {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}