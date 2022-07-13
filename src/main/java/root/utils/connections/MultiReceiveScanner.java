package root.utils.connections;

import root.Host;
import root.utils.Pair;

import java.io.Closeable;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MultiReceiveScanner implements Closeable, Callable<Void> {
    public MultiReceiveScanner(InputStream inputStream) {
        receive = new Scanner(inputStream);
        Host.executor.submit(this);
    }

    private Scanner receive;
    private LinkedList<Pair<String, CompletableFuture<String>>> labeledReceivers = new LinkedList<>();

    private LinkedList<Pair<Type, CompletableFuture<String>>> normalReceivers = new LinkedList<>();

    private Object Waiter = new Object();
    public Void call() throws InterruptedException {
        while (true){
            String msg = receive.next();
            Optional<Pair<String, CompletableFuture<String>>> optPair = labeledReceivers.stream().filter((pair -> pair.getKey().equals(msg))).findFirst();

            if (optPair.isPresent()){
                optPair.get().getVal().complete(msg);
                labeledReceivers.remove(optPair.get());
                continue;
            }

            synchronized (Waiter){
                if (normalReceivers.size() == 0)
                    Waiter.wait();
            }

            Pair<Type, CompletableFuture<String>> curReceiver = normalReceivers.removeLast();

            if (curReceiver.getKey().equals(Type.NEXT_LINE))
                curReceiver.getVal().complete(msg + receive.nextLine());
            else
                curReceiver.getVal().complete(msg);
        }
    }

    public String waitForNext(String msg) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        labeledReceivers.addFirst(new Pair<>(msg, future));
        return future.get();
    }

    public String next() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        normalReceivers.addFirst(new Pair<>(Type.NEXT, future));
        synchronized (Waiter){
            Waiter.notify();
        }
        return future.get();
    }

    public int nextInt() throws ExecutionException, InterruptedException {
        return Integer.parseInt(next());
    }

    public String nextLine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        normalReceivers.addFirst(new Pair<>(Type.NEXT_LINE, future));
        synchronized (Waiter){
            Waiter.notify();
        }
        return future.get();
    }

    @Override
    public void close() {
        receive.close();
    }
    enum Type {
        NEXT, NEXT_LINE;
    }
}


