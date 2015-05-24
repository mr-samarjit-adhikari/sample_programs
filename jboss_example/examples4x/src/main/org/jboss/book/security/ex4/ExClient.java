package org.jboss.book.security.ex4;

import java.rmi.RemoteException;
import java.security.Security;
import javax.naming.InitialContext;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import org.jboss.logging.XLevel;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExClient
{
   public static void main(String args[]) throws Exception
   {
      // Install the Sun JSSE provider since we may not have JSSE installed
      Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
      // Configure log4j
      BasicConfigurator.configure();
      Category root = Category.getRoot();
      root.setLevel(XLevel.TRACE);
      InitialContext iniCtx = new InitialContext();
      Object ref = iniCtx.lookup("EchoBean4");
      EchoHome home = (EchoHome) ref;
      Echo echo = home.create();
      System.out.println("Created Echo");
      System.out.println("Echo.echo()#1 = "+echo.echo("This is call 1"));
   }
}
