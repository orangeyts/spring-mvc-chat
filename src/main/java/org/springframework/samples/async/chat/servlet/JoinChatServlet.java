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
import java.util.Random;

/**
 * http://localhost:8081/spring-mvc-chat/servletChat
 */
@WebServlet(urlPatterns = "/joinServletChat",asyncSupported = true)
public class JoinChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        res.setHeader("Cache-Control","private");
        res.setHeader("Pragma","no-cache");
        req.setCharacterEncoding("UTF-8");
        PrintWriter writer = res.getWriter();
        Random random = new Random();
        int userId = random.nextInt(100000);
        req.getSession().setAttribute("loginuser",new User(userId));
        final AsyncContext ac = req.startAsync();
        ac.setTimeout(30 * 1000);
        ac.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                ServletService.getInstance().removeAsyncContext(ac);
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                ServletService.getInstance().removeAsyncContext(ac);
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                ServletService.getInstance().removeAsyncContext(ac);
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {

            }
        });
//        AsyncContextQueue.ASYNC_QUEUE.add(ac);
        ServletService.getInstance().addAsyncContext(ac);
        System.out.println(Thread.currentThread().getName() + " add to 异步队列");
//        AsyncContextQueue.printQueue();
    }
}
