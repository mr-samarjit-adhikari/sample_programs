package org.jboss.book.jmx.ex0;

import java.net.URLClassLoader;
import java.net.URL;

import org.apache.log4j.Logger;

/** A custom class loader that overrides the standard parent delegation model
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class Ex0URLClassLoader extends URLClassLoader
{

   private static Logger log = Logger.getLogger(Ex0URLClassLoader.class);
   private Ex0URLClassLoader delegate;

   public Ex0URLClassLoader(URL[] urls)
   {
      super(urls);
   }

   void setDelegate(Ex0URLClassLoader delegate)
   {
      this.delegate = delegate;
   }

   protected synchronized Class loadClass(String name, boolean resolve)
	   throws ClassNotFoundException
   {
      Class clazz = null;
      if( delegate != null )
      {
         log.debug(Integer.toHexString(hashCode())+"; Asking delegate to loadClass: "+name);
         clazz = delegate.loadClass(name, resolve);
         log.debug(Integer.toHexString(hashCode())+"; Delegate returned: "+clazz);
      }
      else
      {
         log.debug(Integer.toHexString(hashCode())+"; Asking super to loadClass: "+name);
         clazz = super.loadClass(name, resolve);
         log.debug(Integer.toHexString(hashCode())+"; Super returned: "+clazz);
      }
      return clazz;
   }

   protected Class findClass(String name)
         throws ClassNotFoundException
   {
      Class clazz = null;
      log.debug(Integer.toHexString(hashCode())+"; Asking super to findClass: "+name);
      clazz = super.findClass(name);
      log.debug(Integer.toHexString(hashCode())+"; Super returned: "+clazz);
      return clazz;
   }
}
