package org.jboss.book.jca.ex1;

import java.rmi.RemoteException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import org.apache.log4j.Category;

import org.jboss.book.jca.ex1.ra.DirContextFactory;

public class EchoBean 
    implements SessionBean
{
    private static final Category log = Category.getInstance(EchoBean.class);
    private SessionContext sessionCtx;
    
    public void ejbCreate()
    {
        log.debug("ejbCreate: ");
    }
    
    public void ejbLoad()
    {
        log.debug("ejbLoad");
    }
    
    public void ejbRemove()
    {
        log.debug("ejbRemove");
    }
    
    public void ejbStore()
    {
        log.debug("ejbStore");
    }
    
    public void setSessionContext(SessionContext context)
    {
        sessionCtx = context;
        log.debug("setSessionContext");
    }
    
    public void unsetSessionContext()
    {
        sessionCtx = null;
        log.debug("unsetSessionContext");
    }
    
    public void ejbActivate()
    {
        log.debug("ejbActivate");
    }
    public void ejbPassivate()
    {
        log.debug("ejbPassivate");
    }
    
    public String echo(String arg)
    {
        log.info("echo, arg="+arg);
        try {
            InitialContext ctx = new InitialContext();
            Object         ref = ctx.lookup("java:comp/env/ra/DirContextFactory");
            log.info("echo, ra/DirContextFactory=" + ref);

            DirContextFactory dcf = (DirContextFactory) ref;
            log.info("echo, found dcf=" + dcf);

            DirContext dc = dcf.getConnection();
            log.info("echo, lookup dc=" + dc);

            dc.close();
        } catch(NamingException e) {
            log.error("Failed during JNDI access", e);
        }
        return arg;
    }
}
