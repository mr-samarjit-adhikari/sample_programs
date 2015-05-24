package org.jboss.book.jms.ex3;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/** An MDB that transforms the TextMessages it receives and send the transformed
 messages to the Topic found in the incoming message JMSReplyTo header.

 @author  Scott.Stark@jboss.org
 @version $Revision: 1.1 $
 */
public class TextMDB implements MessageDrivenBean, MessageListener
{
   private static Logger log = Logger.getLogger(TextMDB.class);

   private MessageDrivenContext ctx = null;
   private TopicConnection conn;
   private TopicSession session;

   public TextMDB()
   {
      log.info("TextMDB.ctor, this="+hashCode());
      log.debug("ctor.StackTrace", new Throwable("ctor"));
   }

   public void setMessageDrivenContext(MessageDrivenContext ctx)
   {
      this.ctx = ctx;
      log.info("TextMDB.setMessageDrivenContext, this="+hashCode());
   }

   public void ejbCreate()
   {
      log.info("TextMDB.ejbCreate, this="+hashCode());
      try
      {
         setupPTP();
      }
      catch(Exception e)
      {
         log.error("Failed to init TextMDB", e);
         throw new EJBException("Failed to init TextMDB", e);
      }
   }
   public void ejbRemove()
   {
      log.info("TextMDB.ejbRemove, this="+hashCode());
      ctx = null;
      try
      {
         if( session != null )
            session.close();
         if( conn != null )
            conn.close();
      }
      catch(JMSException e)
      {
         log.error("ejbRemove error", e);
      }
   }

   public void onMessage(Message msg)
   {
      log.info("TextMDB.onMessage, this="+hashCode());
      try
      {
         TextMessage tm = (TextMessage) msg;
         String text = tm.getText() + "processed by: "+hashCode();
         Topic dest = (Topic) msg.getJMSReplyTo();
         sendReply(text, dest);
      }
      catch(Throwable t)
      {
         log.error("onMessage error", t);
      }
   }

   private void setupPTP()
      throws JMSException, NamingException
   {
      InitialContext iniCtx = new InitialContext();
      Object tmp = iniCtx.lookup("java:comp/env/jms/TCF");
      TopicConnectionFactory qcf = (TopicConnectionFactory) tmp;
      conn = qcf.createTopicConnection();
      session = conn.createTopicSession(false,
         TopicSession.AUTO_ACKNOWLEDGE);
      conn.start();
   }
   private void sendReply(String text, Topic dest)
      throws JMSException
   {
      log.info("TextMDB.sendReply, this="+hashCode()
         +", dest="+dest);
      TopicPublisher sender = session.createPublisher(dest);
      TextMessage tm = session.createTextMessage(text);
      sender.publish(tm);
      sender.close();
   }

}
