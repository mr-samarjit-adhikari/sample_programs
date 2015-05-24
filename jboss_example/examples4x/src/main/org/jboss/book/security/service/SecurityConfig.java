/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.book.security.service;

import java.net.URL;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.mx.util.MBeanProxy;
import org.jboss.security.auth.login.XMLLoginConfigMBean;
import org.jboss.system.ServiceMBeanSupport;

/** A security config mbean that loads an xml login configuration and
 pushes a XMLLoginConfig instance onto the the config stack managed by
 the SecurityConfigName mbean(default=jboss.security:service=XMLLoginConfig).
 
 @author Scott.Stark@jboss.org
 @version $Revision: 1.1 $
 */
public class SecurityConfig extends ServiceMBeanSupport
   implements SecurityConfigMBean
{
   // Constants -----------------------------------------------------
   
   // Attributes ----------------------------------------------------
   private String authConf = "login-config.xml";
   private String[] loadedDomains;
   private ObjectName mainSecurityConfig;
   private XMLLoginConfigMBean config;

   // Static --------------------------------------------------------

   // Constructors --------------------------------------------------
   public SecurityConfig()
   {
      setSecurityConfigName("jboss.security:service=XMLLoginConfig");
   }

   public String getName()
   {
      return "JAAS Login Config";
   }
   public String getSecurityConfigName()
   {
      return mainSecurityConfig.toString();
   }
   public void setSecurityConfigName(String objectName)
   {
      try
      {
         mainSecurityConfig = new ObjectName(objectName);
      }
      catch(Exception e)
      {
         log.error("Failed to create ObjectName", e);
      }
   }

   /** Get the resource path to the JAAS login configuration file to use.
    */
   public String getAuthConfig()
   {
      return authConf;
   }

   /** Set the resource path to the JAAS login configuration file to use.
    The default is "login-config.xml".
    */
   public void setAuthConfig(String authConf)
   {
      this.authConf = authConf;
   }

   /** Start the service. This entails locating the AuthConfig resource and
    * then loading this into the XMLLoginConfigMBean given by the
    * SecurityConfigName mbean.
    */
   protected void startService() throws Exception
   {
      // Look for the authConf as resource
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL loginConfig = loader.getResource(authConf);
      if( loginConfig != null )
      {
         log.info("Using JAAS AuthConfig: "+loginConfig.toExternalForm());
         config = (XMLLoginConfigMBean) MBeanProxy.get(XMLLoginConfigMBean.class,
            mainSecurityConfig, server);
         loadedDomains = config.loadConfig(loginConfig);
      }
      else
      {
         log.warn("No AuthConfig resource found");
      }
   }

   protected void stopService() throws Exception
   {
      if( loadedDomains != null )
         config.removeConfigs(loadedDomains);
   }
}
