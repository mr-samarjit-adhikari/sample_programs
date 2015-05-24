package org.jboss.book.jmx.xmbean;

import java.security.Principal;

import org.jboss.logging.Logger;
import org.jboss.mx.interceptor.AbstractInterceptor;
import org.jboss.mx.server.Invocation;
import org.jboss.security.SimplePrincipal;
import org.jboss.invocation.InvocationException;

/** A simple security interceptor example that restricts access to a single
 * principal
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ServerSecurityInterceptor 
    extends AbstractInterceptor
{
    private static Logger log = Logger.getLogger(ServerSecurityInterceptor.class);
    private SimplePrincipal admin = new SimplePrincipal("admin");
    
    public String getAdminName()
    {
        return admin.getName();
    }
    
    public void setAdminName(String name)
    {
        admin = new SimplePrincipal(name);
    }
    
    public Object invoke(Invocation invocation) throws Throwable
    {
        String opName = invocation.getName();
        log.info("invoke, opName=" + opName);
        
        Object[] a = invocation.getArgs();
        if (a !=null) {
            for (int i=0; i<a.length; i++) {
                System.out.println("arg[" + i + "]=" + a[i]);
            }
        }

        if (opName == null || !opName.equals("invoke")) {
            return invocation.nextInterceptor().invoke(invocation);
        }

        Object[] args = invocation.getArgs();
        org.jboss.invocation.Invocation invokeInfo =
            (org.jboss.invocation.Invocation) args[0];
        Principal caller = invokeInfo.getPrincipal();
        log.info("invoke, opName=" + opName + ", caller=" + caller);

        // Only the admin caller is allowed access
        if (caller == null || caller.equals(admin) == false) {
            throw new SecurityException("Caller=" + 
                                        caller + " is not allowed access");
        }
        
        // Let the invocation go
        return invocation.nextInterceptor().invoke(invocation);
    }
}
