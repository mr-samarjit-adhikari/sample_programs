package org.jboss.book.jca.ex1;

import javax.naming.InitialContext;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExClient
{
   public static void main(String args[]) 
       throws Exception
   {
      InitialContext iniCtx = new InitialContext();
      Object         ref    = iniCtx.lookup("EchoBean");
      EchoHome       home   = (EchoHome) ref;
      Echo           echo   = home.create();

      System.out.println("Created Echo");

      System.out.println("Echo.echo('Hello') = " + echo.echo("Hello"));
   }
   
}
