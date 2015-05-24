package org.jboss.book.security.ex1;

import javax.naming.InitialContext;
import org.apache.log4j.Logger;

/**
; * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExClient
{
    public static void main(String args[]) 
        throws Exception
    {
        Logger log = Logger.getLogger("ExClient");
        log.info("Looking up EchoBean");

        InitialContext iniCtx = new InitialContext();
        Object         ref    = iniCtx.lookup("security.EchoBean");
        EchoHome       home   = (EchoHome) ref;
        Echo           echo   = home.create();

        log.info("Created Echo");
        log.info("Echo.echo('Hello') = " + echo.echo("Hello"));
        log.info("Echo.echo('Four') = "  + echo.echo("Four"));
    }
}
