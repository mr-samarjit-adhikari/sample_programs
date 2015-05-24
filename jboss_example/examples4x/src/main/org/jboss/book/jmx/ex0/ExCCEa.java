package org.jboss.book.jmx.ex0;

import java.net.URL;
import java.util.ArrayList;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class ExCCEa
{
   public static void main(String[] args) throws Exception
   {
      ArrayList array = new ArrayList();
      array.add(new URL("file:/tmp"));
      String url = (String) array.get(0);
      System.out.println("URL: "+url);
   }
}
