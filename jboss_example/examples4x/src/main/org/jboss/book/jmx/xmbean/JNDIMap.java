package org.jboss.book.jmx.xmbean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.management.Notification;
import org.jboss.naming.NonSerializableFactory;
import org.jboss.system.ServiceMBeanSupport;

/**
 * An XMBean variation of the JNDIMap standard MBean.
 */

public class JNDIMap 
    extends    ServiceMBeanSupport
{
    private static final String GET_EVENT_TYPE = "org.jboss.book.jmx.xmbean.JNDIMap.get";
    private static final String PUT_EVENT_TYPE = "org.jboss.book.jmx.xmbean.JNDIMap.put";
    
    private String   jndiName;
    private String[] keyValuePairs = {"key0", "value0"};

    private Map     proxyMap   = null;
    
    public String getJndiName()
    {
        return jndiName;
    }
    
    public void setJndiName(String jndiName) 
        throws NamingException
    {
        String oldName = this.jndiName;
        this.jndiName  = jndiName;

        // we only need to act if the service has been started
        if (super.getState() == STARTED)  {
            unbind(oldName);
            try {
                rebind();
            } catch (Exception e) {
                NamingException ne = new
                    NamingException("Failed to update jndiName");
                ne.setRootCause(e);
                throw ne;
            }
        }
    }
    
    public String[] getInitialValues()
    {
        return keyValuePairs;
    }
    
    public void setInitialValues(String[] keyValuePairs)
    {
        if (keyValuePairs == null) {
            keyValuePairs = new String[0];
        }

        this.keyValuePairs = keyValuePairs;

        for(int n = 0; n < keyValuePairs.length; n += 2) {
            String key   = keyValuePairs[n];
            String value = keyValuePairs[n+1];
            put(key, value);
        }
    }
    
    public void startService() 
        throws Exception
    {
        ClassLoader loader     = Thread.currentThread().getContextClassLoader();
        Class[]     interfaces = {Map.class};

        proxyMap = (Map) Proxy.newProxyInstance(loader, interfaces, new ProxyMap());

        log.info("Created Map proxy to handle notifications");

        rebind();
    }
    
    public void stopService()
    {
        unbind(jndiName);
    }
    
    public Object get(Object key)
    {
        System.out.println("GET: " + key);
        System.out.println("PM: " + proxyMap);
        Object value = null;
        if (proxyMap != null) {
            value = proxyMap.get(key);
        }
        return value;
    }

    public void put(Object key, Object value)
    {
        System.out.println("PUT: " + key + "," + value);
        if (proxyMap != null) {
            proxyMap.put(key, value);
        }
   }


    private void rebind() 
        throws NamingException
    {
        InitialContext rootCtx = new InitialContext();
        Name fullName = rootCtx.getNameParser("").parse(jndiName);
        log.info("fullName=" + fullName);
        NonSerializableFactory.rebind(fullName, proxyMap, true);
    }

    
    private void unbind(String jndiName)
    {
        try {
            InitialContext rootCtx = new InitialContext();
            rootCtx.unbind(jndiName);
            NonSerializableFactory.unbind(jndiName);
        } catch (NamingException e) {
            log.error("Failed to unbind map", e);
        }
    }


    private void generateNotification(String type) {
        long         eventID = super.getNextNotificationSequenceNumber();
        Notification event   = new Notification(type, this, eventID);

        log.info("sendNotification(event): " + event);

        super.sendNotification(event);
    }


    public class ProxyMap 
        implements InvocationHandler
    {
        /** The in memory map we manage and expose through JNDI */
        private HashMap contextMap = new HashMap();
        
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
        {
            Object value = method.invoke(this.contextMap, args);
            
            if (method.getName().equals("get")) {
                generateNotification(GET_EVENT_TYPE);
            } else if (method.getName().equals("put")) {
                generateNotification(PUT_EVENT_TYPE);
            }
            
            return value;
        }
    }
    
}
