package org.jboss.book.jmx.ex3;

import java.lang.reflect.Method;
import javax.ejb.CreateException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.system.ServiceMBeanSupport;

/** An example of a DynamicMBean that exposes select attributes and operations
 of an EJB as an MBean.
 
 @author Scott.Stark@jboss.org
 @version $Revision: 1.1 $
 */
public class EjbMBeanAdaptor extends ServiceMBeanSupport
   implements DynamicMBean
{
   private String helloPrefix;
   private String ejbJndiName;
   private EchoLocalHome home;

   /** These are the mbean attributes we expose
    */
   private MBeanAttributeInfo[] attributes = {
      new MBeanAttributeInfo("HelloPrefix", "java.lang.String",
         "The prefix message to append to the session echo reply",
         true, // isReadable
         true, // isWritable
         false), // isIs
      new MBeanAttributeInfo("EjbJndiName", "java.lang.String",
         "The JNDI name of the session bean local home",
         true, // isReadable
         true, // isWritable
         false) // isIs
   };
   /** These are the mbean operations we expose
    */
   private MBeanOperationInfo[] operations;


   /** We override this method to setup our echo operation info. It could
    also be done in a ctor.
    */
   public ObjectName preRegister(MBeanServer server, ObjectName name)
      throws Exception
   {
      log.info("preRegister notification seen");

      operations = new MBeanOperationInfo[5];

      Class thisClass = getClass();
      Class[] parameterTypes = {String.class};
      Method echoMethod = thisClass.getMethod("echo", parameterTypes);
      String desc = "The echo op invokes the session bean echo method and"
         + " returns its value prefixed with the helloPrefix attribute value";
      operations[0] = new MBeanOperationInfo(desc, echoMethod);

      // Add the Service interface operations from our super class
      parameterTypes = new Class[0];
      Method createMethod = thisClass.getMethod("create", parameterTypes);
      operations[1] = new MBeanOperationInfo("The JBoss Service.create", createMethod);
      Method startMethod = thisClass.getMethod("start", parameterTypes);
      operations[2] = new MBeanOperationInfo("The JBoss Service.start", startMethod);
      Method stopMethod = thisClass.getMethod("stop", parameterTypes);
      operations[3] = new MBeanOperationInfo("The JBoss Service.stop", startMethod);
      Method destroyMethod = thisClass.getMethod("destroy", parameterTypes);
      operations[4] = new MBeanOperationInfo("The JBoss Service.destroy", startMethod);

      return name;
   }

// --- Begin ServiceMBeanSupport overides
   protected void createService() throws Exception
   {
      log.info("Notified of create state");
   }
   protected void startService() throws Exception
   {
      log.info("Notified of start state");
      InitialContext ctx = new InitialContext();
      home = (EchoLocalHome) ctx.lookup(ejbJndiName);
      log.info("Testing Echo");
      EchoLocal bean = home.create();
      String echo = bean.echo("startService");
      log.info("echo(startService) = "+echo);
   }
   protected void stopService()
   {
      log.info("Notified of stop state");
   }
// --- End ServiceMBeanSupport overides

   public String getHelloPrefix()
   {
      return helloPrefix;
   }
   public void setHelloPrefix(String helloPrefix)
   {
      this.helloPrefix = helloPrefix;
   }

   public String getEjbJndiName()
   {
      return ejbJndiName;
   }
   public void setEjbJndiName(String ejbJndiName)
   {
      this.ejbJndiName = ejbJndiName;
   }

   public String echo(String arg)
      throws CreateException, NamingException
   {
      log.info("Lookup EchoLocalHome@"+ejbJndiName);
      EchoLocal bean = home.create();
      String echo = helloPrefix + bean.echo(arg);
      return echo;
   }

// --- Begin DynamicMBean interface methods
   /** Returns the management interface that describes this dynamic resource.
    * It is the responsibility of the implementation to make sure the
    * description is accurate.
    *
    * @return the management interface descriptor.
    */
   public MBeanInfo getMBeanInfo()
   {
      String classname = getClass().getName();
      String description = "This is an MBean that uses a session bean in the"
         + " implementation of its echo operation.";
      MBeanConstructorInfo[] constructors = null;
      MBeanNotificationInfo[] notifications = null;
      MBeanInfo mbeanInfo = new MBeanInfo(classname, description, attributes,
         constructors, operations, notifications);
      // Log when this is called so we know when in the lifecycle this is used
      Throwable trace = new Throwable("getMBeanInfo trace");
      log.info("Don't panic, just a stack trace", trace);
      return mbeanInfo;
   }

   /** Returns the value of the attribute with the name matching the
    * passed string.
    *
    * @param attribute the name of the attribute.
    * @return the value of the attribute.
    * @exception AttributeNotFoundException when there is no such attribute.
    * @exception MBeanException wraps any error thrown by the resource when
    * getting the attribute.
    * @exception ReflectionException wraps any error invoking the resource.
    */
   public Object getAttribute(String attribute)
      throws AttributeNotFoundException, MBeanException, ReflectionException
   {
      Object value = null;
      if( attribute.equals("HelloPrefix") )
         value = getHelloPrefix();
      else if( attribute.equals("EjbJndiName") )
         value = getEjbJndiName();
      else
         throw new AttributeNotFoundException("Unknown attribute("+attribute+") requested");
      return value;
   }

   /** Returns the values of the attributes with names matching the
    * passed string array.
    *
    * @param attributes the names of the attribute.
    * @return an {@link AttributeList AttributeList} of name and value pairs.
    */
   public AttributeList getAttributes(String[] attributes)
   {
      AttributeList values = new AttributeList();
      for(int a = 0; a < attributes.length; a ++)
      {
         String name = attributes[a];
         try
         {
            Object value = getAttribute(name);
            Attribute attr = new Attribute(name, value);
            values.add(attr);
         }
         catch(Exception e)
         {
            log.error("Failed to find attribute: "+name, e);
         }
      }
      return values;
   }

   /** Sets the value of an attribute. The attribute and new value are
    * passed in the name value pair {@link Attribute Attribute}.
    *
    * @see javax.management.Attribute
    *
    * @param attribute the name and new value of the attribute.
    * @exception AttributeNotFoundException when there is no such attribute.
    * @exception InvalidAttributeValueException when the new value cannot be
    * converted to the type of the attribute.
    * @exception MBeanException wraps any error thrown by the resource when
    * setting the new value.
    * @exception ReflectionException wraps any error invoking the resource.
    */
   public void setAttribute(Attribute attribute)
      throws AttributeNotFoundException, InvalidAttributeValueException,
      MBeanException, ReflectionException
   {
      String name = attribute.getName();
      if( name.equals("HelloPrefix") )
      {
         String value = attribute.getValue().toString();
         setHelloPrefix(value);
      }
      else if( name.equals("EjbJndiName") )
      {
         String value = attribute.getValue().toString();
         setEjbJndiName(value);
      }
      else
         throw new AttributeNotFoundException("Unknown attribute("+name+") requested");
   }

   /** Sets the values of the attributes passed as an
    * {@link AttributeList AttributeList} of name and new value pairs.
    *
    * @param attributes the name an new value pairs.
    * @return an {@link AttributeList AttributeList} of name and value pairs
    * that were actually set.
    */
   public AttributeList setAttributes(AttributeList attributes)
   {
      AttributeList setAttributes = new AttributeList();
      for(int a = 0; a < attributes.size(); a ++)
      {
         Attribute attr = (Attribute) attributes.get(a);
         try
         {
            setAttribute(attr);
            setAttributes.add(attr);
         }
         catch(Exception ignore)
         {
         }
      }
      return setAttributes;
   }

   /** Invokes a resource operation.
    *
    * @param actionName the name of the operation to perform.
    * @param params the parameters to pass to the operation.
    * @param signature the signartures of the parameters.
    * @return the result of the operation.
    * @exception MBeanException wraps any error thrown by the resource when
    * performing the operation.
    * @exception ReflectionException wraps any error invoking the resource.
    */
   public Object invoke(String actionName, Object[] params, String[] signature)
      throws MBeanException, ReflectionException
   {
      Object rtnValue = null;
      log.info("Begin invoke, actionName="+actionName);
      try
      {
         if( actionName.equals("echo") )
         {
            String arg = (String) params[0];
            rtnValue = echo(arg);
            log.info("Result: "+rtnValue);
         }
         else if( actionName.equals("create") )
         {
            super.create();
         }
         else if( actionName.equals("start") )
         {
            super.start();
         }
         else if( actionName.equals("stop") )
         {
            super.stop();
         }
         else if( actionName.equals("destroy") )
         {
            super.destroy();
         }
         else
         {
            throw new JMRuntimeException("Invalid state, don't know about op="+actionName);
         }
      }
      catch(Exception e)
      {
         throw new ReflectionException(e, "echo failed");
      }
      log.info("End invoke, actionName="+actionName);
      return rtnValue;
   }

// --- End DynamicMBean interface methods

}
