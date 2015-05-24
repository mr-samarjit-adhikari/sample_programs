
package org.jboss.book.jmx.xmbean;

import javax.management.Attribute;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/** A client that demonstrates the persistence of the xmbean attributes. Every
 time it it run it looks up the InitialValues attribute, prints it out
 and then adds a new key/value to the list.

 @author Scott.Stark@jboss.org
 @version $Revision: 1.1 $
 */
public class TestXMBeanRestart
{
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception
   {
      InitialContext ic = new InitialContext();
      RMIAdaptor server = (RMIAdaptor) ic.lookup("jmx/invoker/RMIAdaptor");

      // Get the InitialValues attribute
      ObjectName name = new ObjectName("chap2.xmbean:service=JNDIMap");
      String[] initialValues = (String[]) server.getAttribute(name, "InitialValues");
      System.out.println("InitialValues.length="+initialValues.length);
      int length = initialValues.length;
      for(int n = 0; n < length; n += 2)
      {
         String key = initialValues[n];
         String value = initialValues[n+1];
         System.out.println("key="+key+", value="+value);
      }
      // Add a new key/value pair
      String[] newInitialValues = new String[length+2];
      System.arraycopy(initialValues, 0, newInitialValues, 0, length);
      newInitialValues[length] = "key"+(length/2+1);
      newInitialValues[length+1] = "value"+(length/2+1);

      Attribute ivalues = new Attribute("InitialValues", newInitialValues);
      server.setAttribute(name, ivalues);
   }
}
