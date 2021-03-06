
package org.jboss.book.jmx.ex2;

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
 * @version $Revision: 1.1 $
 * Run is as : ant -Dchap=jmx -Dex=2 run-example
 */
public class TestJNDIMapEx2
{
   public static void main(String[] args) throws Exception
   {
		// Sample lookup code
		InitialContext ctx = new InitialContext();
		HashMap map = (HashMap) ctx.lookup("inmemory/maps/MapTest2");
   }
}
