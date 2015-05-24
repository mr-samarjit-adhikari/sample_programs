/*
* JBoss, the OpenSource J2EE webOS
*
* Distributable under LGPL license.
* See terms of license at gnu.org.
*/
package org.jboss.book.security.service;

import java.util.Vector;
import org.jboss.system.Service;

/** A utility mbean that monitors membership of a cluster parition and
 * establishes a DistributedTimedCachePolicy
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public interface DistributedCacheServiceMBean extends Service
{
   /** Get the cluster parition name the mbean is monitoring
    */
   public String getPartitionName();
   /** Set the cluster parition name the mbean is monitoring
    */
   public void setPartitionName(String name);

   /** Get the JNDI name under which the CachePolicy is bound into JNDI
    */
   public String getCacheJndiName();
   /** Set the JNDI name under which the CachePolicy is bound into JNDI
    */
   public void setCacheJndiName(String name);

   /** Get the cache timeout (seconds)
    */
   public int getCacheTimeout();
   /** Set the cache timeout (seconds)
    */
   public void setCacheTimeout(int timeoutSecs);

   /** Get the current cluster parition membership info
    *@return a Vector of org.javagroups.Address implementations, for example,
    *org.javagroups.stack.IpAddress
    */
   public Vector getClusterNodes();

   /** Get a value from the cache policy
    */
   public Object get(String key);
   /** Set a value in the cache policy
    */
   public void set(String key, String value);
}
