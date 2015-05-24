package org.jboss.book.security.ex2;

import java.security.acl.Group;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

/** An example custom login module that obtains passwords and roles for a user
from a JNDI lookup.
 
@author Scott.Stark@jboss.org
@version $Revision: 1.1 $
*/
public class JndiUserAndPass extends UsernamePasswordLoginModule
{
   /** The JNDI name to the context that handles the password/<username> lookup */
   private String userPathPrefix;
   /** The JNDI name to the context that handles the roles/<username> lookup */
   private String rolesPathPrefix;

   /** Override to obtain the userPathPrefix and rolesPathPrefix options.
    */
   public void initialize(Subject subject, CallbackHandler callbackHandler,
      Map sharedState, Map options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);
      userPathPrefix = (String) options.get("userPathPrefix");
      rolesPathPrefix = (String) options.get("rolesPathPrefix");
   }

   /** Get the roles the current user belongs to by querying the
    rolesPathPrefix + '/' + super.getUsername() JNDI location.
    */
   protected Group[] getRoleSets() throws LoginException
   {
      try
      {
         InitialContext ctx = new InitialContext();
         String rolesPath = rolesPathPrefix + '/' + super.getUsername();
         String[] roles = (String[]) ctx.lookup(rolesPath);
         Group[] groups = {new SimpleGroup("Roles")};
         log.info("Getting roles for user="+super.getUsername());
         for(int r = 0; r < roles.length; r ++)
         {
            SimplePrincipal role = new SimplePrincipal(roles[r]);
            log.info("Found role="+roles[r]);
            groups[0].addMember(role);
         }
         return groups;
      }
      catch(NamingException e)
      {
         log.error("Failed to obtain groups for user="+super.getUsername(), e);
         throw new LoginException(e.toString(true));
      }
   }

   /** Get the password of the current user by querying the
    userPathPrefix + '/' + super.getUsername() JNDI location.
    */
   protected String getUsersPassword() throws LoginException
   {
      try
      {
         InitialContext ctx = new InitialContext();
         String userPath = userPathPrefix + '/' + super.getUsername();
         log.info("Getting password for user="+super.getUsername());
         String passwd = (String) ctx.lookup(userPath);
         log.info("Found password="+passwd);
         return passwd;
      }
      catch(NamingException e)
      {
         log.error("Failed to obtain password for user="+super.getUsername(), e);
         throw new LoginException(e.toString(true));
      }
   }

}
