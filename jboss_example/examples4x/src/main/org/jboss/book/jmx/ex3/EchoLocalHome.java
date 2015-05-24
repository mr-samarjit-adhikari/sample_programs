package org.jboss.book.jmx.ex3;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface EchoLocalHome extends EJBLocalHome
{
   public EchoLocal create()
      throws CreateException;
}
