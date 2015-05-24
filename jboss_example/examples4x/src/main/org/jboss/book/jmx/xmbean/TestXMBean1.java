package org.jboss.book.jmx.xmbean;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.management.Attribute;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.naming.InitialContext;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;
import org.jboss.jmx.adaptor.rmi.RMINotificationListener;

/**
 * A client that demonstrates how to connect to the JMX server using
 * the RMI adaptor.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class TestXMBean1
{
   public static class Listener implements RMINotificationListener
   {
      public Listener() throws RemoteException
      {
         UnicastRemoteObject.exportObject(this);
      }
      public void handleNotification(Notification event, Object handback)
      {
         System.out.println("handleNotification, event: "+event);
      }
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws Exception
   {
      InitialContext ic = new InitialContext();
      RMIAdaptor server = (RMIAdaptor) ic.lookup("jmx/rmi/RMIAdaptor");

      // Get the MBeanInfo for the JNDIMap XMBean
      ObjectName name = new ObjectName("chap2.xmbean:service=JNDIMap");
      MBeanInfo info = server.getMBeanInfo(name);
      System.out.println("JNDIMap Class: "+info.getClassName());
      MBeanOperationInfo[] opInfo = info.getOperations();
      System.out.println("JNDIMap Operations: ");
      for(int o = 0; o < opInfo.length; o ++)
      {
         MBeanOperationInfo op = opInfo[o];
         String returnType = op.getReturnType();
         String opName = op.getName();
         System.out.print(" + "+returnType+" "+opName+"(");
         MBeanParameterInfo[] params = op.getSignature();
         for(int p = 0; p < params.length; p ++)
         {
            MBeanParameterInfo paramInfo = params[p];
            String pname = paramInfo.getName();
            String type = paramInfo.getType();
            if( pname.equals(type) )
               System.out.print(type);
            else
               System.out.print(type+" "+name);
            if( p < params.length-1 )
               System.out.print(',');
         }
         System.out.println(")");
      }


      // Register as a notification listener
      Listener listener = new Listener();
      server.addNotificationListener(name, listener, null, null);

      // Get the InitialValues attribute
      String[] initialValues = (String[]) server.getAttribute(name, "InitialValues");
      for(int n = 0; n < initialValues.length; n += 2)
      {
         String key = initialValues[n];
         String value = initialValues[n+1];
         System.out.println("key="+key+", value="+value);
      }

      // Invoke the put(Object, Object) op
      String[] sig = {"java.lang.Object", "java.lang.Object"};
      Object[] opArgs = {"key1", "value1"};
      server.invoke(name, "put", opArgs, sig);
      System.out.println("JNDIMap.put(key1, value1) successful");
      sig = new String[]{"java.lang.Object"};
      opArgs = new Object[]{"key0"};
      Object result0 = server.invoke(name, "get", opArgs, sig);
      System.out.println("JNDIMap.get(key0): "+result0);
      opArgs = new Object[]{"key1"};
      Object result1 = server.invoke(name, "get", opArgs, sig);
      System.out.println("JNDIMap.get(key1): "+result1);

      // Change the InitialValues
      initialValues[0] = "key10";
      initialValues[1] = "value10";
      Attribute ivalues = new Attribute("InitialValues", initialValues);
      server.setAttribute(name, ivalues);

      // Unregister the listener.
      server.removeNotificationListener(name, listener);
      UnicastRemoteObject.unexportObject(listener, true);
   }
}
