package org.jboss.book.security.ex1;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;

import org.apache.log4j.Category;

public class EchoBean implements SessionBean
{
   private static final Category log = Category.getInstance(EchoBean.class);
   private SessionContext sessionCtx;

   public void ejbCreate()
   {
      log.debug("ejbCreate: ");
   }

   public void ejbLoad()
   {
      log.debug("ejbLoad");
   }

   public void ejbRemove()
   {
      log.debug("ejbRemove");
   }

   public void ejbStore()
   {
      log.debug("ejbStore");
   }

   public void setSessionContext(SessionContext context)
   {
      sessionCtx = context;
      log.debug("setSessionContext");
   }

   public void unsetSessionContext()
   {
      sessionCtx = null;
      log.debug("unsetSessionContext");
   }

   public void ejbActivate()
   {
      log.debug("ejbActivate");
   }
   public void ejbPassivate()
   {
      log.debug("ejbPassivate");
   }

   public String echo(String arg)
   {
      log.debug("echo, arg="+arg);
      return arg;
   }
}
