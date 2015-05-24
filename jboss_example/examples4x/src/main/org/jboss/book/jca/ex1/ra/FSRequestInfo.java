package org.jboss.book.jca.ex1.ra;

import java.io.File;
import javax.resource.spi.ConnectionRequestInfo;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class FSRequestInfo implements ConnectionRequestInfo
{
   private File rootDir;
   
   /** Creates new FSRequestInfo */
   public FSRequestInfo(File rootDir)
   {
      this.rootDir = rootDir;
   }

   public boolean equals(Object obj)
   {
      FSRequestInfo info = (FSRequestInfo) obj;
      return rootDir.equals(info.rootDir);
   }

   public int hashCode()
   {
      return rootDir.hashCode();
   }
   public String toString()
   {
      return "[FSRequestInfo, rootDir="+rootDir.getAbsolutePath()+"]";
   }
}
