package org.springframework.samples.async.chat.servlet;

import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageQueue {

    public static final BlockingDeque<String> messages = new LinkedBlockingDeque<String>();

    public static void sendMsg(String msg) {
        try {
            messages.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
