package org.jboss.book.jmx.xmbean;

import javax.naming.InitialContext;
import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;

/**
 * A client that accesses an XMBean through its RMI interface
 * @author Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class TestXMBean3
{
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        InitialContext  ic     = new InitialContext();
        ClientInterface xmbean = (ClientInterface) ic.lookup("secure-xmbean/ClientInterface");

        // This call should fail because we have not set a security context
        try {
            String[] tmp = xmbean.getInitialValues();
            throw new IllegalStateException("Was able to call getInitialValues");
        } catch (Exception e) {
            System.out.println("Called to getInitialValues failed as expected: "
                               + e.getMessage());
        }

        // Set a security context using the SecurityAssociation
        SecurityAssociation.setPrincipal(new SimplePrincipal("admin"));
        
        // Get the InitialValues attribute
        String[] initialValues = xmbean.getInitialValues();
        for(int n = 0; n < initialValues.length; n += 2) {
            String key   = initialValues[n];
            String value = initialValues[n+1];
            System.out.println("key="+key+", value="+value);
        }

        // Invoke the put(Object, Object) op
        xmbean.put("key1", "value1");
        System.out.println("JNDIMap.put(key1, value1) successful");
        Object result0 = xmbean.get("key0");
        System.out.println("JNDIMap.get(key0): "+result0);
        Object result1 = xmbean.get("key1");
        System.out.println("JNDIMap.get(key1): "+result1);
        
        // Change the InitialValues
        initialValues[0] += ".1";
        initialValues[1] += ".2";
        xmbean.setInitialValues(initialValues);
        
        initialValues = xmbean.getInitialValues();
        for(int n = 0; n < initialValues.length; n += 2) {
            String key   = initialValues[n];
            String value = initialValues[n+1];
            System.out.println("key="+key+", value="+value);
        }
    }
}
