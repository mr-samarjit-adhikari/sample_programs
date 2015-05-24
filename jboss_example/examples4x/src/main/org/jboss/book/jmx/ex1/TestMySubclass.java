package org.jboss.book.jmx.ex1;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.HashMap;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.2 $
 * Run is as : ant -Dchap=jmx -Dex=1 run-example
 */
public class TestMySubclass
{
   public static void main(String[] args) throws Exception
   {
      MBeanServer server = MBeanServerFactory.createMBeanServer("chap2.ex1.test");
      MySubclass mbean = new MySubclass();
      ObjectName name = new ObjectName("chap2.ex1.test:name=TestMysubclass");
      System.out.println("Registering: "+name);
      server.registerMBean(mbean, name);
      MBeanInfo info = server.getMBeanInfo(name);
      MBeanAttributeInfo[] attrInfo = info.getAttributes();
      System.out.println("+++ Attributes("+attrInfo.length+"):");
      for(int a = 0; a < attrInfo.length; a ++)
         System.out.println("  "+attrInfo[a].getName());
      MBeanOperationInfo[] opInfo = info.getOperations();
      System.out.println("+++ Operations("+opInfo.length+"):");
      for(int n= 0; n < opInfo.length; n ++)
         System.out.println("  "+opInfo[n].getName());
   }
}
