package org.jboss.book.security.ex3b;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import org.jboss.logging.XLevel;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class TstReadOnly
{
   public static void main(String args[]) throws Exception
   {
      // Set up a simple configuration that logs on the console.
      FileAppender fa = new FileAppender(new PatternLayout("%r[%c{1}], %m%n"), "ex3bro-trace.log");
      fa.setAppend(false);
      Logger cat = Logger.getLogger("org.jboss.security");
      cat.setLevel(XLevel.TRACE);
      cat.setAdditivity(false);
      cat.addAppender(fa);

      cat = Logger.getLogger("org.jboss.invocation");
      cat.setLevel(XLevel.TRACE);
      cat.setAdditivity(false);
      cat.addAppender(fa);

      Properties env = new Properties();
      env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.HttpNamingContextFactory");
      env.setProperty(Context.PROVIDER_URL, "http://localhost:8080/invoker/ReadOnlyJNDIFactoryHA");
      System.out.println("JNDI env: "+env);
      InitialContext iniCtx = new InitialContext(env);
      // Do a lookup on the srp-readonly context
      Object ref = iniCtx.lookup("srp-readonly/SRPServerInterfaceHA");
      cat.info("srp-readonly/SRPServerInterfaceHA: "+ref);
      // Do a lookup on another context that should fail
      try
      {
         ref = iniCtx.lookup("jmx");
         throw new IllegalStateException("Was able to lookup jmx: "+ref);
      }
      catch(NamingException e)
      {
         cat.info("lookup of jmx failed as expected", e);
      }
   }
}
