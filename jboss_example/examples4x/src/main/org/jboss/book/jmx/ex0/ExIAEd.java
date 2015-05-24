package org.jboss.book.jmx.ex0;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import org.jboss.util.ChapterExRepository;
import org.jboss.util.Debug;

/** An example of IllegalAccessExceptions due to classes loaded by two class
 * loaders.
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExIAEd
{
   public static void main(String[] args) throws Exception
   {
      ChapterExRepository.init(ExIAEd.class);

      String chapDir = System.getProperty("chapter.dir");
      Logger ucl0Log = Logger.getLogger("UCL0");
      File jar0 = new File(chapDir+"/j0.jar");
      ucl0Log.info("jar0 path: "+jar0.toString());
      URL[] cp0 = {jar0.toURL()};
      URLClassLoader ucl0 = new URLClassLoader(cp0);
      Thread.currentThread().setContextClassLoader(ucl0);

      StringBuffer buffer = new StringBuffer("ExIAEd Info");
      Debug.displayClassInfo(ExIAEd.class, buffer, false);
      ucl0Log.info(buffer.toString());

      Class ctxClass1 = ucl0.loadClass("org.jboss.book.jmx.ex0.ExCtx");
      buffer.setLength(0);
      buffer.append("ExCtx Info");
      Debug.displayClassInfo(ctxClass1, buffer, false);
      ucl0Log.info(buffer.toString());
      Object ctx0 = ctxClass1.newInstance();

      try
      {
         Class[] types = {Object.class};
         Method useValue = ctxClass1.getDeclaredMethod("pkgUseValue", types);
         Object[] margs = {null};
         useValue.invoke(ctx0, margs);
      }
      catch(Exception e)
      {
         ucl0Log.error("Failed to invoke ExCtx.pkgUseValue", e);
      }
   }
}
