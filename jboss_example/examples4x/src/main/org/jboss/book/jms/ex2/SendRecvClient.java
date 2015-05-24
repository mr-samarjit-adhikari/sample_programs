package org.jboss.book.jms.ex2;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import EDU.oswego.cs.dl.util.concurrent.CountDown;

/** 
 * A complete JMS client example program that sends N TextMessages to
 * a Queue B and asynchronously receives the messages as modified by
 * TextMDB from Queue A.
 * 
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class SendRecvClient
{
    static final int N = 10;
    static CountDown done = new CountDown(N);
    QueueConnection conn;
    QueueSession session;
    Queue queA;
    Queue queB;
    
    public static class ExListener 
        implements MessageListener
    {
        public void onMessage(Message msg)
        {
            done.release();
            TextMessage tm = (TextMessage) msg;
            try {
                System.out.println("onMessage, recv text=" + 
                                   tm.getText());
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    public void setupPTP()
        throws JMSException, 
               NamingException
    {
        InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("UIL2ConnectionFactory");
        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
        
        conn = qcf.createQueueConnection();
        queA = (Queue) iniCtx.lookup("queue/A");
        queB = (Queue) iniCtx.lookup("queue/B");
        session = conn.createQueueSession(false,
                                          QueueSession.AUTO_ACKNOWLEDGE);
        conn.start();
    }
    
    public void sendRecvAsync(String textBase)
        throws JMSException,
               NamingException,
               InterruptedException
    {
        System.out.println("Begin sendRecvAsync");
        // Setup the PTP connection, session
        setupPTP();

        // Set the async listener for queA
        QueueReceiver recv = session.createReceiver(queA);
        recv.setMessageListener(new ExListener());

        // Send a few text msgs to queB
        QueueSender send = session.createSender(queB);
        for(int m = 0; m < N; m ++) {
            TextMessage tm = session.createTextMessage(textBase + "#" + m);
            tm.setJMSReplyTo(queA);
            send.send(tm);
            System.out.println("sendRecvAsync, sent text=" + tm.getText());
        }
        System.out.println("End sendRecvAsync");
    }
    
    public void stop()
        throws JMSException
    {
        conn.close();
    }
  
    public static void main(String args[]) 
        throws Exception
    {
        System.out.println("Begin SendRecvClient, now="+System.currentTimeMillis());
        SendRecvClient client = new SendRecvClient();
        client.sendRecvAsync("A text msg");
        client.done.acquire();
        client.stop();
        System.exit(0);
        System.out.println("End SendRecvClient");
    }
}
