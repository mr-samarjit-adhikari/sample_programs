package org.jboss.book.jca.ex1.ra;

import java.util.ArrayList;
import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.PrintWriter;

import org.apache.log4j.Category;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class FSManagedConnection implements ManagedConnection
{
   static Category log = Category.getInstance(FSManagedConnection.class);
   ArrayList listeners = new ArrayList();
   FSDirContext conn;

   /** Creates new FSManagedConnection */
   public FSManagedConnection(Subject subject,
      FSRequestInfo fsInfo)
   {
      log.debug("ctor, fsInfo="+fsInfo);
   }

   public void addConnectionEventListener(ConnectionEventListener connectionEventListener)
   {
      log.debug("addConnectionEventListener, listener="+connectionEventListener,
         new Exception("CalledBy:"));
      listeners.add(connectionEventListener);
   }
   public void removeConnectionEventListener(ConnectionEventListener connectionEventListener)
   {
      log.debug("removeConnectionEventListener, listener="+connectionEventListener,
         new Exception("CalledBy:"));
      listeners.remove(connectionEventListener);
   }

   public void associateConnection(Object obj) throws ResourceException
   {
      log.debug("associateConnection, obj="+obj, new Exception("CalledBy:"));
      conn = (FSDirContext) obj;
      conn.setManagedConnection(this);
   }

   public void cleanup() throws ResourceException
   {
      log.debug("cleanup");
   }
   
   public void destroy() throws ResourceException
   {
      log.debug("destroy");
   }
   
   public Object getConnection(Subject subject, ConnectionRequestInfo info)
      throws ResourceException
   {
      log.debug("getConnection, subject="+subject+", info="+info,
         new Exception("CalledBy:"));
      if( conn == null )
         conn = new FSDirContext(this);
      return conn;
   }

   public LocalTransaction getLocalTransaction() throws ResourceException
   {
      log.debug("getLocalTransaction");
      return null;
   }
   
   public ManagedConnectionMetaData getMetaData() throws ResourceException
   {
      log.debug("getMetaData");
      return new FSManagedConnectionMetaData();
   }
   
   public XAResource getXAResource() throws ResourceException
   {
      log.debug("getXAResource");
      return null;
   }

   public PrintWriter getLogWriter() throws ResourceException
   {
      return null;
   }
   public void setLogWriter(PrintWriter out) throws ResourceException
   {
   }

   protected void close()
   {
      ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
      ce.setConnectionHandle(conn);
      fireConnectionEvent(ce);
   }

   protected void fireConnectionEvent(ConnectionEvent evt)
   {
      for(int i=listeners.size()-1; i >= 0; i--)
      {
         ConnectionEventListener listener = (ConnectionEventListener) listeners.get(i);
         if(evt.getId() == ConnectionEvent.CONNECTION_CLOSED)
            listener.connectionClosed(evt);
         else if(evt.getId() == ConnectionEvent.CONNECTION_ERROR_OCCURRED)
            listener.connectionErrorOccurred(evt);
      }
   }
}
