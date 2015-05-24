package org.jboss.book.security.ex3;

import java.rmi.RemoteException;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;

import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.jboss.util.ChapterExRepository;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExClient
{
   public static void main(String args[]) throws Exception
   {
      // Setup the example log4j repository
      ChapterExRepository.init(ExClient.class);

      // Login using SRP
      System.out.println("Logging in using the 'srp' configuration");
      String username = args[0];
      char[] password = args[1].toCharArray();
      UsernamePasswordHandler handler = new UsernamePasswordHandler(username, password);
      LoginContext lc = new LoginContext("srp", handler);
      lc.login();
      InitialContext iniCtx = new InitialContext();
      Object ref = iniCtx.lookup("EchoBean3");
      EchoHome home = (EchoHome) ref;
      Echo echo = home.create();
      System.out.println("Created Echo");
      System.out.println("Echo.echo()#1 = "+echo.echo("This is call 1"));
      Thread.currentThread().sleep(15*1000);
      /* This will fail due to a SRP cache timeout if the JaasSecurityManager
       cache timeout is also set to the same values.
       */
      try
      {
         System.out.println("Echo.echo()#2 = "+echo.echo("This is call 2"));
      }
      catch(Throwable e)
      {
         while( e instanceof RemoteException )
         {
            RemoteException re = (RemoteException) e;
            e = re.detail;
         }
         System.out.println("Echo.echo()#2 failed with exception: "+e.getMessage());
      }
      lc.logout();
   }
}
