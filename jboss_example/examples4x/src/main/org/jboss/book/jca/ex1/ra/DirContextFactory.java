package org.jboss.book.jca.ex1.ra;

import java.io.Serializable;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.resource.Referenceable;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface DirContextFactory 
    extends Referenceable, 
            Serializable
{
    public DirContext getConnection() 
        throws NamingException;
}
