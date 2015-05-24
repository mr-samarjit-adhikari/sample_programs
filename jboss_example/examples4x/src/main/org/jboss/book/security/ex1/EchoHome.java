package org.jboss.book.security.ex1;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface EchoHome extends EJBHome
{
   public Echo create()
      throws RemoteException, CreateException;
}
