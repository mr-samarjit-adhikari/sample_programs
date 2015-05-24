package org.jboss.book.jca.ex1.ra;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;

import org.apache.log4j.Category;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class DirContextFactoryImpl implements DirContextFactory
{
   static Category log = Category.getInstance(DirContextFactoryImpl.class);
   private transient ConnectionManager manager;
   private transient ManagedConnectionFactory factory;
   private transient FSRequestInfo fsInfo;
   private Reference reference;

   DirContextFactoryImpl(ConnectionManager manager,
      ManagedConnectionFactory factory, FSRequestInfo fsInfo)
   {
      this.manager = manager;
      this.factory = factory;
      this.fsInfo = fsInfo;
      log.debug("ctor, fsInfo="+fsInfo);
   }

   public DirContext getConnection() throws NamingException
   {
      log.debug("getConnection", new Exception("CalledBy:"));
      DirContext dc = null;
      try
      {
         dc = (DirContext) manager.allocateConnection(factory, fsInfo);
      }
      catch(ResourceException e)
      {
         throw new NamingException("Unable to get Connection: "+e);
      }
      return dc;
   }
   public DirContext getConnection(String user, String password) throws NamingException
   {
      log.debug("getConnection, user="+user);
      DirContext dc = null;
      try
      {
         dc = (DirContext) manager.allocateConnection(factory, fsInfo);
      }
      catch(ResourceException e)
      {
         throw new NamingException("Unable to get Connection: "+e);
      }
      return dc;
   }

   public void setReference(Reference reference)
   {
      log.debug("setReference, reference="+reference, new Exception("CalledBy:"));
      this.reference = reference;
   }

   public Reference getReference() throws NamingException
   {
      log.debug("getReference");
      return reference;
   }
}
