package org.jboss.book.jmx.ex0;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import org.jboss.util.ChapterExRepository;
import org.jboss.util.Debug;

/** An example of a ClassCastException that results from classes loaded through
 * different class loaders.
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExCCEc
{
   public static void main(String[] args) throws Exception
   {
      ChapterExRepository.init(ExCCEc.class);

      String chapDir = System.getProperty("chapter.dir");
      Logger ucl0Log = Logger.getLogger("UCL0");
      // Load ExObj from j0.jar using URLClassLoader instance 0
      File jar0 = new File(chapDir+"/j0.jar");
      ucl0Log.info("jar0 path: "+jar0.toString());
      URL[] cp0 = {jar0.toURL()};
      URLClassLoader ucl0 = new URLClassLoader(cp0);
      Thread.currentThread().setContextClassLoader(ucl0);
      Class objClass = ucl0.loadClass("org.jboss.book.jmx.ex0.ExObj");
      StringBuffer buffer = new StringBuffer("ExObj Info");
      Debug.displayClassInfo(objClass, buffer, false);
      ucl0Log.info(buffer.toString());
      Object value = objClass.newInstance();

      // Load ExCtx from j0.jar using URLClassLoader instance 1
      File jar1 = new File(chapDir+"/j0.jar");
      Logger ucl1Log = Logger.getLogger("UCL1");
      ucl1Log.info("jar1 path: "+jar1.toString());
      URL[] cp1 = {jar1.toURL()};
      URLClassLoader ucl1 = new URLClassLoader(cp1);
      Thread.currentThread().setContextClassLoader(ucl1);
      Class ctxClass2 = ucl1.loadClass("org.jboss.book.jmx.ex0.ExCtx");
      buffer.setLength(0);
      buffer.append("ExCtx Info");
      Debug.displayClassInfo(ctxClass2, buffer, false);
      ucl1Log.info(buffer.toString());
      Object ctx2 = ctxClass2.newInstance();

      try
      {
         // Invoke ExCtx[UCL1].useValue(value=ExObj[UCL0]) via reflection
         Class[] types = {Object.class};
         Method useValue = ctxClass2.getMethod("useValue", types);
         Object[] margs = {value};
         useValue.invoke(ctx2, margs);
      }
      catch(Exception e)
      {
         ucl1Log.error("Failed to invoke ExCtx.useValue", e);
         throw e;
      }
   }
}
