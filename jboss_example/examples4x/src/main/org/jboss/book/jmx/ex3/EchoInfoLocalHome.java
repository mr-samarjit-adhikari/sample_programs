package org.jboss.book.jmx.ex3;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface EchoInfoLocalHome extends EJBLocalHome
{
   public EchoInfoLocal create(String key) throws CreateException;
   public EchoInfoLocal findByPrimaryKey(String key) throws FinderException;
}
