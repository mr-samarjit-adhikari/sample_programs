package org.jboss.book.misc.ex2;

import java.util.Date;
import org.jboss.varia.scheduler.Schedulable;

import org.apache.log4j.Logger;

/** A simple Schedulable example.
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExSchedulable implements Schedulable
{
   private static final Logger log = Logger.getLogger(ExSchedulable.class);

   private String name;
   private long value;

   public ExSchedulable(String name, long value)
   {
      this.name = name;
      this.value = value;
      log.info("ctor, name: " + name + ", value: " + value);
   }

   public void perform(Date now, long remainingRepetitions)
   {
      log.info("perform, now: " + now +
         ", remainingRepetitions: " + remainingRepetitions +
         ", name: " + name + ", value: " + value);
   }
}
