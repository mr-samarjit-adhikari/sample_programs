package org.jboss.book.jmx.ex1;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class MyClass implements MyClassMBean
{
   private Integer state = null;
   private String hidden = null;

   public Integer getState()
   {
      return state;
   }

   public void setState(Integer s)
   {
      state = s;
   }

   public String getHidden()
   {
      return hidden;
   }

   public void setHidden(String h)
   {
      hidden = h;
   }

   public void reset()
   {
      state = null;
      hidden = null;
   }
}
