package org.jboss.book.jmx.ex2;

// The JNDIMap MBean implementation
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import org.jboss.naming.NonSerializableFactory;

/** An example MBean that extends the JBoss ServiceMBeanSupport class.
This is version 2 as shown in Listing 2.5.
*/
public class JNDIMap extends org.jboss.system.ServiceMBeanSupport
  implements JNDIMapMBean
{
  private String jndiName;
  private HashMap contextMap = new HashMap();

  public String getJndiName()
  {
     return jndiName;
  }
  public void setJndiName(String jndiName) throws NamingException
  {
    String oldName = this.jndiName;
    this.jndiName = jndiName;
    if( super.getState() == STARTED )
    {
      unbind(oldName);
      try
      {
        rebind();
      }
      catch(Exception e)
      {
        NamingException ne = new
          NamingException("Failed to update jndiName");
        ne.setRootCause(e);
        throw ne;
      }
    }
  }

  public void startService() throws Exception
  {
    rebind();
  }
  public void stopService()
  {
    unbind(jndiName);
  }

  private void rebind() throws NamingException
  {
    InitialContext rootCtx = new InitialContext();
    Name fullName = rootCtx.getNameParser("").parse(jndiName);
    log.info("fullName="+fullName);
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
    catch(NamingException e)
    {
      log.error("Failed to unbind map", e);
    }
  }
}
