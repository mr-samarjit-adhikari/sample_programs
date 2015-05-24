package org.jboss.book.jmx.ex3;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.FinderException;
import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

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
      String info = "echo, arg=";
      try
      {
         InitialContext ctx = new InitialContext();
         EchoInfoLocalHome home = (EchoInfoLocalHome) ctx.lookup("java:comp/env/ejb/EchoInfoLocalHome");
         EchoInfoLocal bean = null;
         try
         {
            bean = home.findByPrimaryKey("echo");
         }
         catch(FinderException e)
         {
            bean = home.create("echo");
         }
         info = bean.getInfo();
      }
      catch(Exception e)
      {
         throw new EJBException("Failed to access EchoInfo", e);
      }
      log.info("echo, info="+info+", arg="+arg);
      return arg;
   }
}
