package org.jboss.book.jmx.ex1;

import javax.naming.NamingException;

/** An example MBean that implicitly implements the Service interface methods.
 This is version 1 as shown in Listing 2.7.
 */
public interface JNDIMapMBean
{
   public String getJndiName();
   public void setJndiName(String jndiName) throws NamingException;

   public String[] getInitialValues();
   public void setInitialValues(String[] keyValuePairs);
   
   public int getState();
   public void setState(int state);

   public void start() throws Exception;
   public void stop() throws Exception;
}

