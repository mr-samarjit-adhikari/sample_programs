package org.jboss.book.jmx.ex0;

import java.io.Serializable;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExObj implements Serializable
{
   public ExObj2 ivar = new ExObj2();
}
