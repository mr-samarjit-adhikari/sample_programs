package org.jboss.book.jca.ex1.ra;

import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NameParser;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;

import org.apache.log4j.Category;

/**
 *
 * @author  Scott.Stark@jboss.org
 * @version $Revision: 1.1 $
 */
public class FSDirContext implements DirContext
{
   static Category log = Category.getInstance(FSDirContext.class);
   FSManagedConnection mc;

   /** Creates new FSDirContext */
   public FSDirContext(FSManagedConnection mc)
   {
      this.mc = mc;
   }

   protected void setManagedConnection(FSManagedConnection mc)
   {
      this.mc = mc;
   }

   public Attributes getAttributes(Name name, String[] str) throws NamingException
   {
      return null;
   }
   
   public void close() throws NamingException
   {
      log.debug("close");
      mc.close();
   }

   public NamingEnumeration list(String str) throws NamingException
   {
      return null;
   }
   
   public void unbind(Name name) throws NamingException
   {
   }
   
   public DirContext getSchemaClassDefinition(Name name) throws NamingException
   {
      return null;
   }
   
   public DirContext createSubcontext(String str, Attributes attributes) throws NamingException
   {
      return null;
   }
   
   public String getNameInNamespace() throws NamingException
   {
      return null;
   }
   
   public Object addToEnvironment(String str, Object obj) throws NamingException
   {
      return null;
   }
   
   public NamingEnumeration listBindings(Name name) throws NamingException
   {
      return null;
   }
   
   public void bind(Name name, Object obj) throws NamingException
   {
   }
   
   public NamingEnumeration search(String str, Attributes attributes, String[] str2) throws NamingException
   {
      return null;
   }
   
   public void modifyAttributes(Name name, int param, Attributes attributes) throws NamingException
   {
   }
   
   public Hashtable getEnvironment() throws NamingException
   {
      return null;
   }
   
   public void bind(String str, Object obj) throws NamingException
   {
   }
   
   public void rebind(String str, Object obj, Attributes attributes) throws NamingException
   {
   }
   
   public DirContext getSchema(Name name) throws NamingException
   {
      return null;
   }
   
   public DirContext getSchemaClassDefinition(String str) throws NamingException
   {
      return null;
   }
   
   public Object lookup(String str) throws NamingException
   {
      return null;
   }
   
   public void destroySubcontext(String str) throws NamingException
   {
   }
   
   public Context createSubcontext(Name name) throws NamingException
   {
      return null;
   }
   
   public Object lookupLink(String str) throws NamingException
   {
      return null;
   }
   
   public DirContext getSchema(String str) throws NamingException
   {
      return null;
   }
   
   public Object lookup(Name name) throws NamingException
   {
      return null;
   }
   
   public void destroySubcontext(Name name) throws NamingException
   {
   }
   
   public NamingEnumeration listBindings(String str) throws NamingException
   {
      return null;
   }
   
   public void rebind(String str, Object obj) throws NamingException
   {
   }
   
   public Object removeFromEnvironment(String str) throws NamingException
   {
      return null;
   }
   
   public void bind(String str, Object obj, Attributes attributes) throws NamingException
   {
   }
   
   public NamingEnumeration search(Name name, Attributes attributes) throws NamingException
   {
      return null;
   }
   
   public NameParser getNameParser(String str) throws NamingException
   {
      return null;
   }
   
   public void bind(Name name, Object obj, Attributes attributes) throws NamingException
   {
   }
   
   public Attributes getAttributes(String str) throws NamingException
   {
      return null;
   }
   
   public void rename(String str, String str1) throws NamingException
   {
   }
   
   public void rename(Name name, Name name1) throws NamingException
   {
   }
   
   public DirContext createSubcontext(Name name, Attributes attributes) throws NamingException
   {
      return null;
   }
   
   public void rebind(Name name, Object obj, Attributes attributes) throws NamingException
   {
   }
   
   public NamingEnumeration list(Name name) throws NamingException
   {
      return null;
   }
   
   public Context createSubcontext(String str) throws NamingException
   {
      return null;
   }
   
   public void modifyAttributes(String str, int param, Attributes attributes) throws NamingException
   {
   }
   
   public NamingEnumeration search(String str, Attributes attributes) throws NamingException
   {
      return null;
   }
   
   public Name composeName(Name name, Name name1) throws NamingException
   {
      return null;
   }
   
   public String composeName(String str, String str1) throws NamingException
   {
      return null;
   }
   
   public NamingEnumeration search(Name name, Attributes attributes, String[] str) throws NamingException
   {
      return null;
   }
   
   public void rebind(Name name, Object obj) throws NamingException
   {
   }
   
   public void modifyAttributes(Name name, ModificationItem[] modificationItem) throws NamingException
   {
   }
   
   public NamingEnumeration search(Name name, String str, SearchControls searchControls) throws NamingException
   {
      return null;
   }
   
   public NamingEnumeration search(Name name, String str, Object[] obj, SearchControls searchControls) throws NamingException
   {
      return null;
   }
   
   public void unbind(String str) throws NamingException
   {
   }
   
   public void modifyAttributes(String str, ModificationItem[] modificationItem) throws NamingException
   {
   }
   
   public Attributes getAttributes(Name name) throws NamingException
   {
      return null;
   }
   
   public Object lookupLink(Name name) throws NamingException
   {
      return null;
   }
   
   public NameParser getNameParser(Name name) throws NamingException
   {
      return null;
   }
   
   public Attributes getAttributes(String str, String[] str1) throws NamingException
   {
      return null;
   }
   
   public NamingEnumeration search(String str, String str1, SearchControls searchControls) throws NamingException
   {
      return null;
   }
   
   public NamingEnumeration search(String str, String str1, Object[] obj, SearchControls searchControls) throws NamingException
   {
      return null;
   }
   
}
