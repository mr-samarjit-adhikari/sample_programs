package org.jboss.book.security.ex2.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.log4j.Logger;
import org.jboss.naming.Util;

/**
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class JndiStore
   implements JndiStoreMBean, InvocationHandler, ObjectFactory
{
   private static Logger log = Logger.getLogger(JndiStore.class);

   private static String password = "theduke";
   private static String[] roles = {"TheDuke", "Echo"};
   private static NameParser parser;

   /** Creates a new instance of JndiStore */
   public JndiStore()
   {
   }

   public void start() throws Exception
   {
      InitialContext ctx = new InitialContext();
      parser = ctx.getNameParser("");

      /* Create a mapping from the security/store context to a this ObjectFactory
       so that any lookup against security/store/password/username our password
       and security/store/roles/username returns our roles.
      */
      RefAddr refAddr = new StringRefAddr("nns", "JndiStore");
      String factoryName = JndiStore.class.getName();
      Reference ref = new Reference("javax.naming.Context", refAddr, factoryName, null);
      
      Util.rebind(ctx, "security/store", ref);
      log.info("Start, bound security/store");
   }
   public void stop()
   {
      try
      {
         InitialContext ctx = new InitialContext();
         ctx.unbind("security/store");
         log.info("Stop, unbound security/store");
      }
      catch(NamingException e)
      {
      }
   }

   /** Object factory implementation. This method returns a Context proxy
    that is only able to handle a lookup operation for an atomic name of
    a security domain.
   */
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment)
      throws Exception
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class[] interfaces = {Context.class};
      Context ctx = (Context) Proxy.newProxyInstance(loader, interfaces, this);
      return ctx;
   }

   /** This is the InvocationHandler callback for the Context interface that
    was created by out getObjectInstance() method. We handle the security/store
    context lookups here.
    */
   public Object invoke(Object obj, Method method, Object[] args) throws Throwable
   {
      String methodName = method.getName();
      if( methodName.equals("toString") == true )
         return "security/store Context proxy";
      if( methodName.equals("lookup") == false )
         throw new OperationNotSupportedException("Only lookup is supported, op="+method);

      Name name = null;
      if( args[0] instanceof String )
         name = parser.parse((String) args[0]);
      else
         name = (Name) args[0];
      String type = name.toString();
      log.info("lookup, name="+name);
      if( type.startsWith("password") == true )
         return password;
      if( type.startsWith("roles") == true )
         return roles;
      throw new InvalidNameException("The request type must be password or roles, type="+type);
   }
}
