package org.jboss.book.security.ex1;

import java.rmi.RemoteException;
import javax.ejb.EJBObject;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface Echo extends EJBObject
{
   public String echo(String arg) throws RemoteException;
}
