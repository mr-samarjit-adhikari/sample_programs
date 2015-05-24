package org.jboss.book.jmx.ex3;

import javax.ejb.EntityBean;
import javax.ejb.CreateException;
import javax.ejb.EntityContext;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public abstract class EchoInfoBean implements EntityBean
{
   public void setEntityContext(EntityContext ctx) { }
   public void unsetEntityContext() { }
   public void ejbActivate() { }
   public void ejbPassivate() { }
   public void ejbLoad() { }
   public void ejbStore() { }
   public void ejbRemove() { }

   public String ejbCreate(String key) throws CreateException
   {
      setKey(key);
      setInfo("echo info, arg=");
      return null;
   }

   public void ejbPostCreate(String id)
   {
   }

   public abstract String getKey();
   public abstract void setKey(String key);
   public abstract String getInfo();
   public abstract void setInfo(String info);
}
