/*
 * FSManagedConnectionMetaData.java
 *
 * Created on October 31, 2001, 8:04 PM
 */

package org.jboss.book.jca.ex1.ra;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version 
 */
public class FSManagedConnectionMetaData implements ManagedConnectionMetaData
{
   /** Creates new FSManagedConnectionMetaData */
    public FSManagedConnectionMetaData()
    {
    }

    public String getEISProductName() throws ResourceException
    {
       return "Local File System Adaptor";
    }

    public String getEISProductVersion() throws ResourceException
    {
       return "JBoss 2.4.x JCA";
    }

    public int getMaxConnections() throws ResourceException
    {
       return 100;
    }

    public String getUserName() throws ResourceException
    {
       return "nobody";
    }

}
