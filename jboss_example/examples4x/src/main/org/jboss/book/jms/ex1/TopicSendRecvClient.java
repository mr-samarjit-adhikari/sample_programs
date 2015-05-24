package org.jboss.book.jms.ex1;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;
import javax.jms.TopicSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import EDU.oswego.cs.dl.util.concurrent.CountDown;

/** A complete JMS client example program that sends a
TextMessage to a Topic and asynchronously receives the
message from the same Topic.

@author  Scott.Stark@jboss.org
@version $Revision: 1.1 $
*/
public class TopicSendRecvClient
{
    static CountDown done = new CountDown(1);
    TopicConnection  conn = null;
    TopicSession session = null;
    Topic topic = null;
    
    public static class ExListener 
        implements MessageListener
    {
        public void onMessage(Message msg)
        {
            done.release();
            TextMessage tm = (TextMessage) msg;
            try {
                System.out.println("onMessage, recv text=" + tm.getText());
            }  catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }
    
    public void setupPubSub()
        throws JMSException, 
               NamingException
    {
        InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("ConnectionFactory");
        TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
        conn = tcf.createTopicConnection();
        topic = (Topic) iniCtx.lookup("topic/testTopic");
        session = conn.createTopicSession(false,
                                          TopicSession.AUTO_ACKNOWLEDGE);
        conn.start();
    }
    
    public void sendRecvAsync(String text)
        throws JMSException, 
               NamingException
    {
        System.out.println("Begin sendRecvAsync");
        // Setup the PubSub connection, session
        setupPubSub();

        // Set the async listener
        TopicSubscriber recv = session.createSubscriber(topic);
        recv.setMessageListener(new ExListener());
        // Send a text msg
        TopicPublisher send = session.createPublisher(topic);
        TextMessage tm = session.createTextMessage(text);
        send.publish(tm);
        System.out.println("sendRecvAsync, sent text=" + tm.getText());
        send.close();
        System.out.println("End sendRecvAsync");
    }
    
    public void stop()
        throws JMSException
    {
        conn.stop();
        session.close();
        conn.close();
    }

    public static void main(String args[]) throws Exception
    {
        System.out.println("Begin TopicSendRecvClient, now=" + 
                           System.currentTimeMillis());

        TopicSendRecvClient client = new TopicSendRecvClient();
        client.sendRecvAsync("A text msg, now="+System.currentTimeMillis());
        client.done.acquire();
        client.stop();

        System.out.println("End TopicSendRecvClient");
        System.exit(0);
    }

}
