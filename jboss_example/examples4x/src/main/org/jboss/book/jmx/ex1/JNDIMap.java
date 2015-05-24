package org.jboss.book.jmx.ex1;

// The JNDIMap MBean implementation

import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import org.jboss.naming.NonSerializableFactory;

/** An example MBean that implicitly implements the Service interface methods.
 This is version 1 as shown in Listing 2.4.
 */
public class JNDIMap implements JNDIMapMBean
{
   private String jndiName;
   private HashMap contextMap = new HashMap();
   private String[] keyValuePairs;
   private boolean started;
   private int state;

   public String getJndiName()
   {
      return jndiName;
   }

   public void setJndiName(String jndiName) throws NamingException
   {
      String oldName = this.jndiName;
      this.jndiName = jndiName;
      if (started)
      {
         unbind(oldName);
         try
         {
            rebind();
         }
         catch (Exception e)
         {
            NamingException ne = new
               NamingException("Failed to update jndiName");
            ne.setRootCause(e);
            throw ne;
         }
      }
   }

   public String[] getInitialValues()
   {
      return keyValuePairs;
   }

   public int getState(){ return state;}
   public void setState(int state){ this.state = state;}

   public void setInitialValues(String[] keyValuePairs)
   {
      if (keyValuePairs == null)
         keyValuePairs = new String[0];
      this.keyValuePairs = keyValuePairs;
      for (int n = 0; n < keyValuePairs.length; n += 2)
      {
         String key = keyValuePairs[n];
         String value = keyValuePairs[n + 1];
         contextMap.put(key, value);
      }
   }

   public void start() throws Exception
   {
      started = true;
      state = 99;
      rebind();
   }

   public void stop()
   {
      started = false;
      unbind(jndiName);
   }

   private void rebind() throws NamingException
   {
      InitialContext rootCtx = new InitialContext();
      Name fullName = rootCtx.getNameParser("").parse(jndiName);
      System.out.println("fullName=" + fullName);
      NonSerializableFactory.rebind(fullName, contextMap, true);
   }

   private void unbind(String jndiName)
   {
      try
      {
         InitialContext rootCtx = new InitialContext();
         rootCtx.unbind(jndiName);
         NonSerializableFactory.unbind(jndiName);
      }
      catch (NamingException e)
      {
         e.printStackTrace();
      }
   }
}
