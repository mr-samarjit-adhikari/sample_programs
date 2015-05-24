package org.jboss.book.jmx.ex3;

import java.rmi.RemoteException;
import javax.ejb.EJBLocalObject;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface EchoLocal extends EJBLocalObject
{
   public String echo(String arg);
}
