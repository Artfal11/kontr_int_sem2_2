package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("v1.png");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        File fileDir = new File("C:\\Users\\user\\IdeaProjects\\kontr2\\src\\main\\java\\org\\example\\cw2\\v1\\");
        String[] filenames = fileDir.list();
        ArrayList<PieceOfImage> threads = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        for (String filename : filenames) {
            PieceOfImage runnable = new PieceOfImage("C:\\Users\\user\\IdeaProjects\\kontr2\\src\\main\\java\\org\\example\\cw2\\v1\\" + filename);
            executor.execute(runnable); // Запуск задачи
            threads.add(runnable);
        }

        executor.shutdown(); // остановка добавления новых задач

        while (!executor.isTerminated()) {
            // ждем завершения всех задач
        }

        Collections.sort(threads, Comparator.comparingInt(thread -> thread.part));

        for (PieceOfImage thread : threads) {
            fileOutputStream.write(thread.data);
            fileOutputStream.flush();
        }
    }

    public static class PieceOfImage implements Runnable {
        String filename;
        public int part;
        byte[] data;

        PieceOfImage(String filename) {
            this.filename = filename;
        }

        @Override
        public void run() {
            try (DataInputStream is = new DataInputStream(new FileInputStream(filename))) {
                int sz = is.readInt();
                data = is.readNBytes(sz);
                int even = is.readInt();
                part = is.readInt();
                int controlNumber = 0;

                for (byte b : data) {
                    for (int i = 0; i < 8; i++) {
                        controlNumber += (b & (1 << i)) >> i;
                    }
                }

                if (controlNumber % 2 != even) {
                    throw new IOException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

