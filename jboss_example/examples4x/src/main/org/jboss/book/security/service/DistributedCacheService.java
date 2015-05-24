/*
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
* See terms of license at gnu.org.
*/
package org.jboss.book.security.service;

import java.lang.reflect.Method;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Vector;
import javax.naming.InitialContext;
import javax.naming.Name;

import org.jboss.ha.framework.interfaces.DistributedState;
import org.jboss.ha.framework.interfaces.DistributedState.DSListenerEx;
import org.jboss.ha.framework.interfaces.HAPartition;
import org.jboss.ha.framework.interfaces.HAPartition.HAMembershipListener;
import org.jboss.ha.framework.server.util.DistributedTimedCachePolicy;
import org.jboss.naming.NonSerializableFactory;
import org.jboss.system.ServiceMBeanSupport;

/** The cluster parition membership monitor and distributed cache
 test mbean implementation

 @author Scott.Stark@jboss.org
 @version $Revision: 1.1 $
 */
public class DistributedCacheService extends ServiceMBeanSupport
   implements DistributedCacheServiceMBean, HAMembershipListener, DSListenerEx
{
   private String jndiName;
   private String partitionName = "Default";
   private HAPartition partition;
   private DistributedState map;
   private DistributedTimedCachePolicy cache;
   private int cacheTimeout = 1800;

// --- Begin ServiceMBeanSupport overriden methods
   protected void startService() throws Exception
   {
      InitialContext ctx = new InitialContext();
      String partitionJndiName = "/HAPartition/" + partitionName;
      partition = (HAPartition) ctx.lookup(partitionJndiName);
      map = partition.getDistributedStateService();
      cache = new DistributedTimedCachePolicy(jndiName, partitionName, cacheTimeout);
      cache.create();
      cache.start();
      // Bind the cache into JNDI
      Name name = ctx.getNameParser("").parse(jndiName);
      NonSerializableFactory.rebind(name, cache, true);

      // Register as a listener of cache changes
      map.registerDSListenerEx(jndiName, this);
      log.info("Registered as DMListener for category="+jndiName);
      // Register as a listener of cluster membership changes
      partition.registerMembershipListener(this);
      log.info("Registered as MembershipListener");
   }

   protected void stopService() throws Exception
   {
      cache.stop();
      map.unregisterDSListenerEx(jndiName, this);
      partition.unregisterMembershipListener(this);
   }
// --- End ServiceMBeanSupport overriden methods

// --- Begin DistributedCacheServiceMBean interface methods
   public String getPartitionName()
   {
      return partitionName;
   }
   public void setPartitionName(String name)
   {
      this.partitionName = name;
   }

   public String getCacheJndiName()
   {
      return jndiName;
   }
   public void setCacheJndiName(String name)
   {
      this.jndiName = name;
   }

   public int getCacheTimeout()
   {
      return cacheTimeout;
   }
   public void setCacheTimeout(int timeoutSecs)
   {
      this.cacheTimeout = timeoutSecs;
   }

   public Vector getClusterNodes()
   {
      Vector view = null;
      try
      {
         InitialContext ctx = new InitialContext();
         String jndiName = "/HAPartition/" + partitionName;
         HAPartition partition = (HAPartition) ctx.lookup(jndiName);
         view = partition.getCurrentView();
      }
      catch(Exception e)
      {
         log.error("Failed to access HAPartition state", e);
      }
      return view;
   }

   public Object get(String key)
   {
      return cache.get(key);
   }
   public void set(String key, String value)
   {
      cache.insert(key, value);
   }
// --- End DistributedCacheServiceMBean interface methods

// --- Begin HAMembershipListener interface methods
   /** Called when a new partition topology occurs.
    * @param deadMembers A list of nodes that have died since the previous view
    * @param newMembers A list of nodes that have joined the partition since
    * the previous view
    * @param allMembers A list of nodes that built the current view
    */
   public void membershipChanged(Vector deadMembers, Vector newMembers, Vector allMembers)
   {
      log.info("DeadMembers: size="+deadMembers.size());
      for(int m = 0; m < deadMembers.size(); m ++)
      {
         AddressPort addrInfo = getMemberAddress(deadMembers.get(m));
         log.info(addrInfo);
      }
      log.info("NewMembers: size="+newMembers.size());
      for(int m = 0; m < newMembers.size(); m ++)
      {
         AddressPort addrInfo = getMemberAddress(newMembers.get(m));
         log.info(addrInfo);
      }
      log.info("AllMembers: size="+allMembers.size());
      for(int m = 0; m < allMembers.size(); m ++)
      {
         AddressPort addrInfo = getMemberAddress(allMembers.get(m));
         log.info(addrInfo);
      }
   }
// --- End HAMembershipListener interface methods

// --- Begin DMListener interface methods
   public void valueHasChanged(String category, Serializable key,
      Serializable value, boolean locallyModified)
   {
      log.info("valueHasChanged, category="+category+", key="+key
         +", value="+value+", locallyModified="+locallyModified);
   }
   public void keyHasBeenRemoved (String category, Serializable key,
      Serializable value, boolean locallyModified)
   {
      log.info("keyHasBeenRemoved, category="+category+", key="+key
         +", value="+value+", locallyModified="+locallyModified);
   }
// --- End DMListener interface methods

   /** Use reflection to access the address InetAddress and port if they exist
    * in the Address implementation
    */
   private AddressPort getMemberAddress(Object addr)
   {
      AddressPort info = null;
      try
      {
         Class[] parameterTypes = {};
         Object[] args = {};
         Method getIpAddress = addr.getClass().getMethod("getIpAddress", parameterTypes);
         InetAddress inetAddr = (InetAddress) getIpAddress.invoke(addr, args);
         Method getPort = addr.getClass().getMethod("getPort", parameterTypes);
         Integer port = (Integer) getPort.invoke(addr, args);
         info = new AddressPort(inetAddr, port);
      }
      catch(Exception e)
      {
         // Try to access the address info from the getName -> addr:port string
         try
         {
            Class[] parameterTypes = {};
            Object[] args = {};
            Method getName = addr.getClass().getMethod("getName", parameterTypes);
            String id = (String) getName.invoke(addr, args);
            String addrString = id;
            Integer port = null;
            int colon = id.indexOf(':');
            if( colon > 0 )
            {
               addrString = id.substring(0, colon);
               port = Integer.valueOf(id.substring(colon+1));
            }
            InetAddress inetAddr = InetAddress.getByName(addrString);
            info = new AddressPort(inetAddr, port);
         }
         catch(Exception e2)
         {
            log.warn("Failed to obtain InetAddress/port from addr: "+addr, e);            
         }
      }
      return info;
   }

   static class AddressPort
   {
      InetAddress addr;
      Integer port;
      AddressPort(InetAddress addr, Integer port)
      {
         this.addr = addr;
         this.port = port;
      }
      public String toString()
      {
         return "{host("+addr+"), port("+port+")}";
      }
   }
}
