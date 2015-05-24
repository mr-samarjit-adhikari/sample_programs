package org.jboss.book.jmx.ex1;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface MyClassMBean
{
   public Integer getState();
   public void setState(Integer s);
   public void reset();
}
