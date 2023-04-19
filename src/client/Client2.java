package client;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Client2 {

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
                                commands.add(reader.readLine());
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
                            s += l + "\n";
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

    private static String wait_new_message(BufferedReader in) {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}