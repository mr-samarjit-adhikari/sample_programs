package org.jboss.book.jmx.xmbean;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface ClientInterface
{
   public String[] getInitialValues();
   public void setInitialValues(String[] keyValuePairs);
   public Object get(Object key);
   public void put(Object key, Object value);
}
