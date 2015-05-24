package org.jboss.book.jms.ex3;

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

/** A complete JMS client example program that sends N
TextMessages to a Topic A and asynchronously receives the
messages as modified by TestMDB from Topic B.

@author  Scott.Stark@jboss.org
@version $Revision: 1.1 $
*/
public class SendRecvClient
{
   static final int N = 10;
   static CountDown done = new CountDown(N);
   TopicConnection conn;
   TopicSession session;
   Topic topicA;
   Topic topicB;

   public static class ExListener implements MessageListener
   {
      public void onMessage(Message msg)
      {
         done.release();
         TextMessage tm = (TextMessage) msg;
         try
         {
            System.out.println("onMessage, recv text="+tm.getText());
         }
         catch(Throwable t)
         {
            t.printStackTrace();
         }
      }
   }

   public void setupPubSub()
      throws JMSException, NamingException
   {
      InitialContext iniCtx = new InitialContext();
      Object tmp = iniCtx.lookup("ConnectionFactory");
      TopicConnectionFactory qcf = (TopicConnectionFactory) tmp;
      conn = qcf.createTopicConnection();
      topicA = (Topic) iniCtx.lookup("topic/jms.ex3.TopicA");
      topicB = (Topic) iniCtx.lookup("topic/jms.ex3.TopicB");

      session = conn.createTopicSession(false,
         TopicSession.AUTO_ACKNOWLEDGE);
      conn.start();
   }

   public void sendRecvAsync(String textBase)
      throws JMSException, NamingException, InterruptedException
   {
      System.out.println("Begin sendRecvAsync");
      // Setup the PTP connection, session
      setupPubSub();
      // Set the async listener for topicA
      TopicSubscriber recv = session.createSubscriber(topicA);
      recv.setMessageListener(new ExListener());
      // Send a few text msgs to topicB
      TopicPublisher send = session.createPublisher(topicB);
      for(int m = 0; m < 10; m ++)
      {
         TextMessage tm = session.createTextMessage(textBase+"#"+m);
         tm.setJMSReplyTo(topicA);
         send.publish(tm);
         System.out.println("sendRecvAsync, sent text="+tm.getText());
      }
      System.out.println("End sendRecvAsync");
   }

   public void stop()  throws JMSException
   {
      conn.stop();
   }

   public static void main(String args[]) throws Exception
   {
      SendRecvClient client = new SendRecvClient();
      client.sendRecvAsync("A text msg");
      client.done.acquire();
      client.stop();
      System.exit(0);
   }

}
