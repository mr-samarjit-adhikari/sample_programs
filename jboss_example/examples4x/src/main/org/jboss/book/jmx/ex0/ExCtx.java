package org.jboss.book.jmx.ex0;

import java.io.IOException;

import org.apache.log4j.Logger;

import org.jboss.util.Debug;

/** A classes used to demonstrate various class loading issues
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExCtx
{
   ExObj value;

   public ExCtx() throws IOException
   {
      value = new ExObj();
      Logger log = Logger.getLogger(ExCtx.class);
      StringBuffer buffer = new StringBuffer("ctor.ExObj");
      Debug.displayClassInfo(value.getClass(), buffer, false);
      log.info(buffer.toString());
      ExObj2 obj2 = value.ivar;
      buffer.setLength(0);
      buffer = new StringBuffer("ctor.ExObj.ivar");
      Debug.displayClassInfo(obj2.getClass(), buffer, false);
      log.info(buffer.toString());
   }
   public Object getValue()
   {
      return value;
   }
   public void useValue(Object obj) throws Exception
   {
      Logger log = Logger.getLogger(ExCtx.class);
      StringBuffer buffer = new StringBuffer("useValue2.arg class");
      Debug.displayClassInfo(obj.getClass(), buffer, false);
      log.info(buffer.toString());
      buffer.setLength(0);
      buffer.append("useValue2.ExObj class");
      Debug.displayClassInfo(ExObj.class, buffer, false);
      log.info(buffer.toString());
      ExObj ex = (ExObj) obj;
   }
   void pkgUseValue(Object obj) throws Exception
   {
      Logger log = Logger.getLogger(ExCtx.class);
      log.info("In pkgUseValue");
   }
}
