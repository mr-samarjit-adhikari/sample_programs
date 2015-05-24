package org.jboss.book.jms.ex1;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSubscriber;
import javax.jms.TopicSession;
import javax.naming.InitialContext;

/** A client that creates a durable topic subscriber and exits simply to
create the durable topic using the JBossMQ dynamic durable topic capability.


@author  Scott.Stark@jboss.org
@version $Revision: 1.1 $
*/
public class DurableTopicSetup
{

   public static void main(String args[]) throws Exception
   {
      System.out.println("Begin DurableTopicSetup");
      InitialContext iniCtx = new InitialContext();

      Object tmp = iniCtx.lookup("ConnectionFactory");
      TopicConnectionFactory tcf = (TopicConnectionFactory) tmp;
      TopicConnection conn = tcf.createTopicConnection("john", "needle");
      Topic topic = (Topic) iniCtx.lookup("topic/testTopic");
      TopicSession session = conn.createTopicSession(false,
                                                     TopicSession.AUTO_ACKNOWLEDGE);

      TopicSubscriber recv = session.createDurableSubscriber(topic, "jms-ex1dtps");
      session.createSubscriber(topic);
      
      conn.close();
      System.out.println("End DurableTopicSetup");
      System.exit(0);
   }

}
