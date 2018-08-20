package org.springframework.samples.async.chat.servlet;

import javax.servlet.AsyncContext;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncContextQueue {

    public static final Queue<AsyncContext> ASYNC_QUEUE = new ConcurrentLinkedQueue<AsyncContext>();

    public static void printQueue(){
        System.out.println("size : " + ASYNC_QUEUE.size() + " join chat");
    }
}
