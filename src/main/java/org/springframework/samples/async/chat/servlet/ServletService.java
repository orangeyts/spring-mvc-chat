package org.springframework.samples.async.chat.servlet;


/**
 * https://blog.csdn.net/renfufei/article/details/53512110
 * http://strongant.iteye.com/blog/2156766
 *
 * 讲解的不错 https://blog.csdn.net/liuchuanhong1/article/details/78744138
 *
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServletService {


    //异步Servlet上下文队列.
    private final Map<Integer, AsyncContext> ASYNC_CONTEXT_MAP = new ConcurrentHashMap<Integer, AsyncContext>();

    //消息队列.
    private final BlockingQueue<TextMessage> TEXT_MESSAGE_QUEUE = new LinkedBlockingQueue<TextMessage>();

    //单一实例.
    private static ServletService instance = new ServletService();

    //构造函数，创建发送消息的异步线程.
    private ServletService() {
        new Thread(this.notifierRunnable).start();//线程发发消息给多个用户
    }

    //单一实例.
    public static ServletService getInstance() {
        return instance;
    }

    /**
     *
     * 注册异步Servlet上下文.
     *
     * @param asyncContext
     *            异步Servlet上下文.
     */
    public void addAsyncContext(final AsyncContext asyncContext) {
        HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
        User user = (User) req.getSession().getAttribute("loginuser");
        if (null!=user) {
            ASYNC_CONTEXT_MAP.put(user.getId(), asyncContext);
        }
    }

    /**
     *
     * 删除异步Servlet上下文.
     *
     * @param asyncContext
     *            异步Servlet上下文.
     */
    public void removeAsyncContext(final AsyncContext asyncContext) {

        HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
        User user = (User) req.getSession().getAttribute("loginuser");
        if (null!=user) {
            ASYNC_CONTEXT_MAP.remove(user.getId());
        }

    }

    /**
     *
     * 发送消息到异步线程，最终输出到http response 流 .
     *
     * @param text 发送给客户端的消息.
     *
     */
    public void putMessage(final int userId, final String text) {

        try {
            TextMessage tm = new TextMessage(userId, text);
            TEXT_MESSAGE_QUEUE.add(tm);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public void putMessage(final TextMessage tm) {
        try {
            System.out.println("增加了消息  " + tm.getText());
            TEXT_MESSAGE_QUEUE.add(tm);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean pushMessage(final TextMessage tm) {
        boolean result = false;
        AsyncContext ac = ASYNC_CONTEXT_MAP.get(tm.getUserId());
        try {
            if (null != ac) {
                write(ac, tm.getText());
                result = true;
            }
        } catch (Exception e) {
            ASYNC_CONTEXT_MAP.remove(tm.getUserId());
            e.printStackTrace();
        }

        return result;
    }

    /**
     *
     * 异步线程，当消息队列中被放入数据，将释放take方法的阻塞，将数据发送到http response流上.
     * 该方法暂时没用，用于并发测试
     */
    private Runnable notifierRunnable = new Runnable() {

        @Override
        public void run() {

            boolean done = false;
            while (true) {
                try {
                    final TextMessage tm = TEXT_MESSAGE_QUEUE.take();//当消息队列没有数据时候，线程执行到这里就会被阻塞
                    System.out.println("有消息啦了 " + tm.getText());
                    if (tm.getUserId()==0) {//发送给所有人
                        for (Entry<Integer, AsyncContext> entry : ASYNC_CONTEXT_MAP.entrySet()) {
                            try {
                                write(entry.getValue(), tm.getText());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    else {
                        pushMessage(tm);
                    }

                    Thread.sleep(100);//暂停100ms，停止的这段时间让用户有足够时间连接到服务器

                } catch (InterruptedException iex) {
                    done = true;
                    iex.printStackTrace();
                }

            }
        }
    };

    private void write(AsyncContext ac, String text) throws IOException {
        PrintWriter acWriter = ac.getResponse().getWriter();

        acWriter.write(text);

        acWriter.flush();

        acWriter.close();

        ac.complete();

    }

}
