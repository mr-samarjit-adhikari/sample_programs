package org.jboss.book.security.ex2;

import javax.naming.InitialContext;
import javax.security.auth.login.LoginContext;

import org.apache.log4j.Logger;
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
      Logger log = Logger.getLogger("ExClient");
      log.info("Login with username="+args[0]+", password="+args[1]);
      UsernamePasswordHandler handler = new UsernamePasswordHandler(args[0], args[1].toCharArray());
      LoginContext lc = new LoginContext("ExClient", handler);
      lc.login();

      log.info("Looking up EchoBean2");
      InitialContext iniCtx = new InitialContext();
      Object ref = iniCtx.lookup("EchoBean2");
      EchoHome home = (EchoHome) ref;
      Echo echo = home.create();
      log.info("Created Echo");
      log.info("Echo.echo('Hello') = "+echo.echo("Hello"));
      lc.logout();
   }
}
