package org.jboss.book.security.ex4;

import java.net.*;
import java.security.Security;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;

public class JSSE_install_check
{
   public static void main(String[] args) throws Exception
   {
      Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

      ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
      SSLServerSocket sslSocket = (SSLServerSocket) factory.createServerSocket(0);

      String [] cipherSuites = sslSocket.getEnabledCipherSuites();
      for(int i = 0; i < cipherSuites.length; i++)
      {
         System.out.println("Cipher Suite " + i +
            " = " + cipherSuites[i]);
      }
   }
}
