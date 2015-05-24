package org.jboss.book.jca.ex1.ra;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;

import org.apache.log4j.Category;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class FSManagedConnectionFactory 
    implements ManagedConnectionFactory, 
               Serializable
{
    static Category log = Category.getInstance(FSManagedConnectionFactory.class);
    private String rootDirPath;
    private transient File rootDir;
    
    /** Creates new FSManagedConnectionFactory */
    public FSManagedConnectionFactory()
    {
    }
    
    public Object createConnectionFactory()
        throws ResourceException
    {
        log.debug("createConnectionFactory");
        throw new UnsupportedOperationException("Cannot be used in unmanaged env");
    }

    public Object createConnectionFactory(ConnectionManager cm)
        throws ResourceException
    {
        log.debug("createConnectionFactory, cm=" + cm, new Exception("CalledBy:"));
        FSRequestInfo fsInfo = new FSRequestInfo(rootDir);
        return new DirContextFactoryImpl(cm, this, fsInfo);
    }

    public ManagedConnection createManagedConnection(Subject subject,
                                                     ConnectionRequestInfo info)
        throws ResourceException
    {
        log.debug("createManagedConnection, subject=" + subject + ", info=" + info,
                  new Exception("CalledBy:"));
        FSRequestInfo fsInfo = (FSRequestInfo) info;
        return new FSManagedConnection(subject, fsInfo);
    }

    public ManagedConnection matchManagedConnections(Set connectionSet, 
                                                     Subject subject,
                                                     ConnectionRequestInfo info)
        throws ResourceException
    {
        log.debug("matchManagedConnections, connectionSet=" + 
                  connectionSet + ", subject=" + subject  +", info="  +info);
        return (ManagedConnection) connectionSet.iterator().next();
    }
    
    public PrintWriter getLogWriter() 
        throws ResourceException
    {
        return null;
    }
    

    public void setLogWriter(PrintWriter out) 
        throws ResourceException
    {
    }

    public boolean equals(Object other)
    {
        return super.equals(other);
    }

    public int hashCode()
    {
        return super.hashCode();
    }
    
    public void setFileSystemRootDir(String rootDirPath)
    {
        this.rootDirPath = rootDirPath;
        rootDir = new File(rootDirPath);

        if (rootDir.exists() == false) {
            rootDir.mkdirs();
        }
        log.debug("setFileSystemRootDir, rootDir=" + rootDir.getAbsolutePath(),
                  new Exception("CalledBy:"));
   }
}
