package org.jboss.book.jmx.ex3;

import javax.ejb.EJBLocalObject;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface EchoInfoLocal extends EJBLocalObject
{
   public String getInfo();
}
