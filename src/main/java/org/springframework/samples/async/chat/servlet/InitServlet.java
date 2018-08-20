package org.springframework.samples.async.chat.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * http://localhost:8081/spring-mvc-chat/servletChat
 */
@WebServlet(value="/initServlet",loadOnStartup = 1)
public class InitServlet extends HttpServlet {

    /*@Override
    public void init() throws ServletException {
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                boolean done = false;
                while (!done){
                    try {
                        String message = MessageQueue.messages.take();
                        System.out.println("获取到了消息  " + message);
                        for(AsyncContext asyncContext : AsyncContextQueue.ASYNC_QUEUE){
                            try {
                                PrintWriter writer = asyncContext.getResponse().getWriter();
                                writer.println(message);
                                writer.flush();
                                writer.close();
                                asyncContext.complete();
                            } catch (IOException e) {
                                e.printStackTrace();
                                AsyncContextQueue.ASYNC_QUEUE.remove(asyncContext);
                            }
                        }
                    } catch (InterruptedException e) {
                        done = true;
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread notifierThread = new Thread(runnable);
        notifierThread.start();
        System.out.println("消息监听 线程启动完毕");
    }*/
}
