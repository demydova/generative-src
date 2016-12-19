package framework;
import java.util.Random;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.security.SecureRandom;


public class gentest {
	
	//Methode für random String Generation
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	 static SecureRandom rnd = new SecureRandom();
	 
	  static String randomString (int len){
		   StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
		}
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
		
		Object v_object = null;
		Constructor<?> v_constructor=null;
		
		
		Object[] v_arg2;
		
		//Object[] v_arg=new Object[2];
		//v_arg[0]="String";
		//v_arg[1]=new Random().nextInt(11);
		
		Object[] v_arg1=new Object[1];
		v_arg1[0]=new Random().nextInt(11000000);
		
		Class<?>[] v_param = new Class<?>[2];
		v_param[0]=String.class;
		v_param[1]=Integer.TYPE;
		
		Class<?>[] v_param1 = new Class<?>[1];
		v_param1[0]=Integer.TYPE;
		
		//define URL to class
		URL[] v_url={new URL("file:///Users/annademydova/Documents/workspace/randoopTestProject/bin/")};
		//create ClassLoader object
		URLClassLoader v_cloader = new URLClassLoader(v_url);
		//load class and create the instance of it
		Class<?> v_class=v_cloader.loadClass("core.Util");
		v_constructor=v_class.getConstructor(v_param1);
		
		
		
		//get method and run it
		Method v_method=v_class.getMethod("edlich", v_param);
		
		
	
		// create byte array
		byte[] nbyte = new byte[30];

		//String Vatiable für Switch case
		String typName="";
		
		//Anzahl der Parameter wird berechnet
		v_arg2=new Object[v_method.getParameterCount()];
		Class[] v_paramTypes2 = v_method.getParameterTypes();
		
	      try{
		
		//Timer Variable
		long last = System.currentTimeMillis();
		while (System.currentTimeMillis() < last + 5000) {
			//Typ der Parameter wird bestimmt und in Array definiert
			for (int i=0;i<v_arg2.length; i++){
				
				//Switch Case
				typName=v_paramTypes2[i].getSimpleName(); 
				//System.out.println("slovili "+typName);
				
				
				switch (typName){
				
				
				case "boolean": v_arg2[i]=new Random().nextBoolean();
				break;
				
				case "char": v_arg2[i]=(char)(new Random().nextInt(26) + 'a');
				break;
				
				case "byte": new Random().nextBytes(nbyte);
				v_arg2[i]=nbyte;
				break;
				
				case "short": v_arg2[i]=new Random().nextInt(65536) - 32768;
				break;
				
				case "int": v_arg2[i]=new Random().nextInt(12);
				break;
				
				case "long": v_arg2[i]=new Random().nextLong();
				break;
				
				case "float": v_arg2[i]=new Random().nextFloat();
				break;
				
				case "double" : v_arg2[i]=new Random().nextDouble();
				break;
				
				case "String":v_arg2[i]=randomString (new Random().nextInt(11));
				break;	
				
				default : v_arg2[i]="xxxxxx";
				break;
				}
				
			}
			
			//System.out.println("slovili "+ v_arg2[0]);
			//System.out.println("slovili "+ v_arg2[1]);
			
			
			v_object=v_constructor.newInstance(v_arg1);
			
			
			System.out.println("Function RUN "+v_method.invoke(v_object, v_arg2));
		   
		}
		
		
	      }
	      catch (Exception e) {
	          // gib die Fehlermeldung aus
	          System.out.println("Division by zerro is not allowed");
	      }

		
		//list the methods
		for(Method v_methods : v_class.getDeclaredMethods())
		{
				System.out.println(v_methods.getName());
				
				Class[] v_paramTypes = v_methods.getParameterTypes();
				for(int i=0;i<v_paramTypes.length;i++)
				{
					System.out.println("  "+v_paramTypes[i]);
				}
		}


	}

}
