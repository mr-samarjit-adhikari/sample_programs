package org.jboss.book.jmx.ex0;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.jboss.util.ChapterExRepository;
import org.jboss.util.Debug;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExCCEb
{
   public static void main(String[] args) throws Exception
   {
      ChapterExRepository.init(ExCCEb.class);

      String chapDir = System.getProperty("chapter.dir");
      Logger ucl0Log = Logger.getLogger("UCL0");
      File jar0 = new File(chapDir+"/j0.jar");
      ucl0Log.info("jar0 path: "+jar0.toString());
      URL[] cp0 = {jar0.toURL()};
      URLClassLoader ucl0 = new URLClassLoader(cp0);
      Thread.currentThread().setContextClassLoader(ucl0);
      Class ctxClass1 = ucl0.loadClass("org.jboss.book.jmx.ex0.ExCtx");
      StringBuffer buffer = new StringBuffer("ExCtx Info");
      Debug.displayClassInfo(ctxClass1, buffer, false);
      ucl0Log.info(buffer.toString());
      Object ctx0 = ctxClass1.newInstance();
      Object value = null;
      try
      {
         Class[] types = {};
         Method getValue = ctxClass1.getMethod("getValue", types);
         Object[] margs = {};
         value = getValue.invoke(ctx0, margs);
         buffer.setLength(0);
         buffer.append("main.obj.CodeSource: ");
         Debug.displayClassInfo(value.getClass(), buffer, false);
         ucl0Log.info(buffer.toString());
      }
      catch(Exception e)
      {
         ucl0Log.error("Failed to invoke ExCtx.getValue", e);
      }

      File jar1 = new File(chapDir+"/j1.jar");
      Logger ucl1Log = Logger.getLogger("UCL1");
      ucl1Log.info("jar1 path: "+jar1.toString());
      URL[] cp1 = {jar1.toURL()};
      URLClassLoader ucl1 = new URLClassLoader(cp1);
      Class ctxClass2 = ucl1.loadClass("org.jboss.book.jmx.ex0.ExCtx");
      buffer.setLength(0);
      buffer.append("ExCtx Info");
      Debug.displayClassInfo(ctxClass2, buffer, false);
      ucl1Log.info(buffer.toString());
      Object ctx2 = ctxClass2.newInstance();
      Thread.currentThread().setContextClassLoader(ucl1);
      try
      {
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

      try
      {
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
