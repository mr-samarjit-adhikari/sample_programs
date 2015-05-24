package org.jboss.book.jmx.ex2;

// The JNDIMap MBean interface
import javax.naming.NamingException;

/** An example MBean that extends the JBoss ServiceMBeanSupport class.
This is version 2 as shown in Listing 2.8.
*/
public interface JNDIMapMBean extends org.jboss.system.ServiceMBean
{
  public String getJndiName();
  public void setJndiName(String jndiName) throws NamingException;
}
