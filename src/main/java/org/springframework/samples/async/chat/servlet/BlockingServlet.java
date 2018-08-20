package org.springframework.samples.async.chat.servlet;


import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

@WebServlet(urlPatterns = "/BlockingServlet",asyncSupported = true)
public class BlockingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String create_time = request.getParameter("create_time");
        System.out.println(create_time);
        addToWaitingList(request.startAsync());
    }

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    static {
        executorService.scheduleAtFixedRate(BlockingServlet::newEvent,0,2, TimeUnit.MILLISECONDS);
    }

    private static void newEvent() {
        System.out.println("pub event ");
        ArrayList clients = new ArrayList(queue.size());
        queue.drainTo(clients);
        try {
            for(Object object : clients){
                AsyncContext ac = (AsyncContext)object;
                ac.getResponse().getWriter().print("ok");
                ac.complete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*clients.parallelStream().forEach((AsyncContext ac) -> {
            ac.getResponse().getWriter().write("OK");
//            ServletUtil.writeResponse(, "OK");
            ac.complete();
        });*/
    }

    private static final BlockingQueue queue = new ArrayBlockingQueue<>(20000);

    public static void addToWaitingList(AsyncContext c) {
        queue.add(c);
    }
}
