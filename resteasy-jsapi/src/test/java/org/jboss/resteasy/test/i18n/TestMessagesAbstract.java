package org.jboss.resteasy.test.i18n;

import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.Assert;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.jsapi.i18n.Messages;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.test.resteasy_jaxrs.i18n.TestMessagesParent;
import org.junit.Test;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 27, 2015
 */
abstract public class TestMessagesAbstract extends TestMessagesParent
{
   protected static final String BASE = String.format("0%5s", Messages.BASE).substring(0, 4);
   protected static ResourceMethodInvoker testMethod;
   
   static
   {
      try
      {
         Class<?> clazz = TestMessagesAbstract.class;
         Method method = TestMessagesAbstract.class.getMethod("testLocale");
         ResourceClass resourceClass = new ResourceClass(TestMessagesAbstract.class, "path");
         ResourceMethod resourceMethod = new ResourceMethod(resourceClass, method, method);
         ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
         InjectorFactory injectorFactory = new InjectorFactoryImpl();
         POJOResourceFactory resourceFactory = new POJOResourceFactory(clazz);
         testMethod = new ResourceMethodInvoker(resourceMethod, injectorFactory, resourceFactory, providerFactory);
      }
      catch (NoSuchMethodException e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testLocale() throws Exception
   {  
      Locale locale = getLocale();
      String filename = "org/jboss/resteasy/jsapi/i18n/Messages.i18n_" + locale.toString() + ".properties";
      if (!before(locale, filename))
      {
         System.out.println(getClass() + ": " + filename + " not found.");
         return;
      }
      
      Assert.assertEquals(getExpected(BASE + "00", "impossibleToGenerateJsapi", "class", "method"), Messages.MESSAGES.impossibleToGenerateJsapi("class", "method"));
      Assert.assertEquals(getExpected(BASE + "05", "invoker", testMethod), Messages.MESSAGES.invoker(testMethod));
      Assert.assertEquals(getExpected(BASE + "35", "restApiUrl", "http"), Messages.MESSAGES.restApiUrl("http"));
      Assert.assertEquals(getExpected(BASE + "60", "thereAreNoResteasyDeployments"), Messages.MESSAGES.thereAreNoResteasyDeployments());     
   }
   
   @Override
   protected int getExpectedNumberOfMethods()
   {
      return Messages.class.getDeclaredMethods().length;  
   }
   
   @Override
   protected String getExpected(String id, String message, Object... args)
   {
      String s = super.getExpected(id, message, args);
      String ss = pruneQuotes(s);
      System.out.println("actual expected: " + ss);
      return ss;
   }
   
   protected String pruneQuotes(String s)
   {
      StringBuffer sb = new StringBuffer();
      boolean sawQuote = false;
      for (int i = 0; i < s.length(); i++)
      {
         char c = s.charAt(i);
         if (sawQuote)
         {
            sawQuote = false;
            sb.append('\'');
            if (c != '\'')
            {
               sb.append(c);
            }
         }
         else if (c == '\'')
         {
            sawQuote = true;
         }
         else
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }
   
   abstract protected Locale getLocale();
}
