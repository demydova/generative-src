//V2 is refactoring of the code of V1, which was developed without essential structre from historical perpsective

package framework;
import java.util.Random;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class gentest {
	
	//URL which should be parsed and afterwards reflected
	URL[] v_url;
	//holder for loading of class for testing
	static Class<?> v_class;
	//variable for instance of testing class
	static Object v_object;
	//list of consturctor for class for testing
	static Constructor[] constructors;
	//secondary element for loading of URLs
	URLClassLoader v_cloader;
	//variable for method
	static Method v_method;
	
	//List of arguments for creating of instance of consturctor and tartet test-functions
	static Object[] v_arg_constr;		//list of arguments for consturctor
	static Object[] v_arg_testfunc;	//list of arguments for function
	
	//List of types of arguments for creating of instance of consturctor and tartet test-functions
	static Class[] v_params_constr;		//list of arguments for consturctor
	static Class[] v_params_testfunc;	//list of arguments for function
	
	
	//constructor
	public gentest(String url, String classname, String funcname) throws MalformedURLException, ClassNotFoundException
	{
		v_url=new URL[] {new URL(url)};							//set URL-variable
		v_cloader = new URLClassLoader(v_url);					//set URLClassLoader
		v_class=v_cloader.loadClass(classname);					//set Class variable
		constructors = v_class.getConstructors();				//get array of constructors
		
		//looking for instance of tested function
		for(Method v_methods : v_class.getDeclaredMethods())
		{
			System.out.println("Method " +v_methods.getName() + " Func "+ funcname);
				if(v_methods.getName().equals(funcname))
				{
					v_method=v_methods;
					break;
				}
				else
					System.out.println("The necessary method is not found");
		}	
		
	}

	//main method
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException	
	{
		Generator v_generator = new Generator();
		//initialize framework for target class and function
		gentest test_engine = new gentest("file:///Users/annademydova/Documents/workspace/randoopTestProject/bin/", "core.Util", "edlich");

		//Take first constructor and parse his parameter list
		v_arg_constr=new Object[constructors[0].getParameterCount()];
		v_params_constr = constructors[0].getParameterTypes();
		
		//Parse parameter list of test function
		v_arg_testfunc=new Object[v_method.getParameterCount()];
		v_params_testfunc = v_method.getParameterTypes();
		
		//initialize variable of testing class
		v_generator.fill_arguments(v_arg_constr, v_params_constr);
		v_object=constructors[0].newInstance(v_arg_constr);
		
		try{
			//Timer Variable
    		long last = System.currentTimeMillis();
    		//the time for testing is set
    		while (System.currentTimeMillis() < last + 5000) {
    			//invoke the function 		
    			v_generator.fill_arguments(v_arg_testfunc, v_params_testfunc);
    			System.out.println("Function results: "+v_method.invoke(v_object, v_arg_testfunc));
    		}
		}
		catch (Exception e) {
			// Error notification
	  	    System.out.println("An error has occured");
	  	    }

	}	
}