package org.jboss.book.jmx.ex0;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jboss.util.ChapterExRepository;
import org.jboss.util.Debug;

/** An example of a LinkageError due to classes being defined by more than
 * one class loader in a non-standard class loading environment.
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExLE
{
   public static void main(String[] args) throws Exception
   {
      ChapterExRepository.init(ExLE.class);

      String chapDir = System.getProperty("chapter.dir");
      Logger ucl0Log = Logger.getLogger("UCL0");
      File jar0 = new File(chapDir+"/j0.jar");
      ucl0Log.info("jar0 path: "+jar0.toString());
      URL[] cp0 = {jar0.toURL()};
      Ex0URLClassLoader ucl0 = new Ex0URLClassLoader(cp0);
      Thread.currentThread().setContextClassLoader(ucl0);
      Class ctxClass1 = ucl0.loadClass("org.jboss.book.jmx.ex0.ExCtx");
      Class obj2Class1 = ucl0.loadClass("org.jboss.book.jmx.ex0.ExObj2");
      StringBuffer buffer = new StringBuffer("ExCtx Info");
      Debug.displayClassInfo(ctxClass1, buffer, false);
      ucl0Log.info(buffer.toString());
      buffer.setLength(0);
      buffer.append("ExObj2 Info, UCL0");
      Debug.displayClassInfo(obj2Class1, buffer, false);
      ucl0Log.info(buffer.toString());

      File jar1 = new File(chapDir+"/j1.jar");
      Logger ucl1Log = Logger.getLogger("UCL1");
      ucl1Log.info("jar1 path: "+jar1.toString());
      URL[] cp1 = {jar1.toURL()};
      Ex0URLClassLoader ucl1 = new Ex0URLClassLoader(cp1);
      Class obj2Class2 = ucl1.loadClass("org.jboss.book.jmx.ex0.ExObj2");
      buffer.setLength(0);
      buffer.append("ExObj2 Info, UCL1");
      Debug.displayClassInfo(obj2Class2, buffer, false);
      ucl1Log.info(buffer.toString());

      ucl0.setDelegate(ucl1);
      try
      {
         ucl0Log.info("Try ExCtx.newInstance()");
         Object ctx0 = ctxClass1.newInstance();
         ucl0Log.info("ExCtx.ctor succeeded, ctx0: "+ctx0);
      }
      catch(Throwable e)
      {
         ucl0Log.error("ExCtx.ctor failed", e);
      }
   }
}
