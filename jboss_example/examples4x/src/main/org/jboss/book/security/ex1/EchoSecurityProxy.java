package org.jboss.book.security.ex1;

import java.lang.reflect.Method;
import javax.ejb.EJBContext;

import org.apache.log4j.Category;

import org.jboss.security.SecurityProxy;

/** 
 * A simple example of a custom SecurityProxy implementation that
 * demonstrates method argument based security checks.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class EchoSecurityProxy 
    implements SecurityProxy
{
    Category log = Category.getInstance(EchoSecurityProxy.class);
    Method echo;
    
    public void init(Class beanHome, Class beanRemote,
                     Object securityMgr)
        throws InstantiationException
    {
        init(beanHome, beanRemote, null, null, securityMgr);
    }

    public void init(Class beanHome, Class beanRemote,
                     Class beanLocalHome, Class beanLocal, Object securityMgr)
        throws InstantiationException
    {
        log.debug("init, beanHome=" + beanHome + ", beanRemote=" + beanRemote +
                  ", beanLocalhome=" + beanLocalHome + ", beanLocal=" + beanLocal +
                  ", securityMgr=" + securityMgr);
        // Get the echo method for equality testing in invoke
        try {
            Class[] params = {String.class};
            echo = beanRemote.getDeclaredMethod("echo", params);
        } catch(Exception e) {
            String msg = "Failed to find an echo(String) method";
            log.error(msg, e);
            throw new InstantiationException(msg);
        }
    }

    public void setEJBContext(EJBContext ctx)
    {
        log.debug("setEJBContext, ctx=" + ctx);
    }

    public void invokeHome(Method m, Object[] args)
        throws SecurityException
    {
        // We don't validate access to home methods
    }

    public void invoke(Method m, Object[] args, Object bean)
        throws SecurityException
    {
        log.debug("invoke, m=" + m);
        // Check for the echo method
        if (m.equals(echo)) {
            // Validate that the msg arg is not 4 letter word
            String arg = (String) args[0];
            if (arg == null || arg.length() == 4) {
                throw new SecurityException("No 4 letter words");
            }
            // We are not responsible for doing the invoke
        }
    }
}
