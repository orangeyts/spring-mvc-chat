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
@WebServlet(urlPatterns = "/postServletChat")
public class PostChatServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html;charset=UTF-8");
        res.setHeader("Cache-Control","private");
        res.setHeader("Pragma","no-cache");
        req.setCharacterEncoding("UTF-8");
        String message = req.getParameter("message");
//        MessageQueue.sendMsg(message);
        ServletService.getInstance().putMessage(new TextMessage(0,message));
    }
}
