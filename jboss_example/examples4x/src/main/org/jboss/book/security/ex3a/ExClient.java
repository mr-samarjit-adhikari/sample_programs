package org.jboss.book.security.ex3a;

import java.rmi.RemoteException;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.LoginContext;

import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import org.jboss.logging.XLevel;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExClient
{
   public static void main(String args[]) throws Exception
   {
      // Set up a simple configuration that logs on the console.
      FileAppender fa = new FileAppender(new PatternLayout("%r[%c{1}], %m%n"), "ex3a-trace.log");
      fa.setAppend(false);
      Logger cat = Logger.getLogger("org.jboss.security");
      cat.setLevel(XLevel.TRACE);
      cat.setAdditivity(false);
      cat.addAppender(fa);

      cat = Logger.getLogger("org.jboss.invocation");
      cat.setLevel(XLevel.TRACE);
      cat.setAdditivity(false);
      cat.addAppender(fa);

      // Login using SRP
      System.out.println("Logging in using the 'srpHA' configuration");
      String username = args[0];
      char[] password = args[1].toCharArray();
      UsernamePasswordHandler handler = new UsernamePasswordHandler(username, password);
      LoginContext lc = new LoginContext("srpHA", handler);
      lc.login();
      InitialContext iniCtx = new InitialContext();
      Object ref = iniCtx.lookup("EchoBean3a");
      EchoHome home = (EchoHome) ref;
      Echo echo = home.create();
      System.out.println("Created Echo");
      // Make some calls across the cluster
      for(int c = 1; c <= 4; c ++)
      {
         System.out.println("Echo.echo()#"+c+" = "+echo.echo("This is call "+c));
      }
      lc.logout();
   }
}
