package org.jboss.book.security.ex3;

import java.net.InetAddress;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.LoginContext;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;

/** A program that creates an account for username=jduke, password=theduke
 using the SRPVerifierStoreService MBean addUser operation.

@author  Scott.Stark@jboss.org
@version $Revision: 1.1 $
*/
public class ExClientSetup
{
   public static void main(String args[]) throws Exception
   {
      String username = args[0];
      String password = args[1];
      System.out.println("Accessing the Security:service=SRPVerifierStore MBean server");
      String serverName = InetAddress.getLocalHost().getHostName();
      String connectorName = "jmx:" +serverName+ ":rmi";
      RMIAdaptor server = (RMIAdaptor) new InitialContext().lookup(connectorName);
      ObjectName srpStore = new ObjectName("Security:service=SRPVerifierStore");
      System.out.println("Creating username="+username+", password="+password);
      Object[] params = {"jduke", "theduke"};
      String[] signature = {"java.lang.String", "java.lang.String"};
      server.invoke(srpStore, "addUser", params, signature);
      System.out.println("User jduke added");
   }
}
