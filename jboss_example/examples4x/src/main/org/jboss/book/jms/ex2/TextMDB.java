package org.jboss.book.jms.ex2;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.ejb.EJBException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

/**
 * An MDB that transforms the TextMessages it receives and send the
 * transformed messages to the Queue found in the incoming message
 * JMSReplyTo header.
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class TextMDB 
    implements MessageDrivenBean, 
               MessageListener
{
    private static Logger log = Logger.getLogger(TextMDB.class);
    
    private MessageDrivenContext ctx = null;
    private QueueConnection conn;
    private QueueSession session;
    
    public TextMDB()
    {
        log.info("TextMDB.ctor, this=" + hashCode());
        log.debug("ctor.StackTrace", new Throwable("ctor"));
    }
    
    public void setMessageDrivenContext(MessageDrivenContext ctx)
    {
        this.ctx = ctx;
        log.info("TextMDB.setMessageDrivenContext, this=" + hashCode());
    }
 
    public void ejbCreate()
    {
        log.info("TextMDB.ejbCreate, this="+hashCode());
        try {
            setupPTP();
        } catch(Exception e) {
            log.error("Failed to init TextMDB", e);
            throw new EJBException("Failed to init TextMDB", e);
        }
    }

    public void ejbRemove()
    {
        log.info("TextMDB.ejbRemove, this=" + hashCode());
        ctx = null;
        try {
            if (session != null) {
                session.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch(JMSException e) {
            log.error("ejbRemove error", e);
        }
    }
    
    public void onMessage(Message msg)
    {
        log.info("TextMDB.onMessage, this="+hashCode());
        try {
            TextMessage tm = (TextMessage) msg;
            String text = tm.getText() + "processed by: " + hashCode();
            Queue dest = (Queue) msg.getJMSReplyTo();
            sendReply(text, dest);
        } catch(Throwable t) {
            log.error("onMessage error", t);
        }
    }

    private void setupPTP()
        throws JMSException, 
               NamingException
    {
        InitialContext iniCtx = new InitialContext();
        Object tmp = iniCtx.lookup("java:comp/env/jms/QCF");
        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
        conn = qcf.createQueueConnection();
        session = conn.createQueueSession(false,
                                          QueueSession.AUTO_ACKNOWLEDGE);
        conn.start();
    }
    
    private void sendReply(String text, Queue dest)
        throws JMSException
    {
        log.info("TextMDB.sendReply, this=" + hashCode() +
                 ", dest=" + dest);

        QueueSender sender = session.createSender(dest);
        TextMessage tm     = session.createTextMessage(text);

        sender.send(tm);
        sender.close();
   }

}
