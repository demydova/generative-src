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
	static URL[] v_url;
	//holder for loading of class for testing
	static Class<?> v_class;
	//variable for instance of testing class
	static Object v_object;
	//list of consturctor for class for testing
	static Constructor[] constructors;
	//secondary element for loading of URLs
	static URLClassLoader v_cloader;
	//variable for method
	static Method v_method;
	//variable for setting the time of method duration
	static Long time;
	static String results="";
	
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
			//System.out.println("Method " +v_methods.getName() + " Func "+ funcname);
			if(v_methods.getName().equals(funcname))
			{
				//instanse of tested function
				v_method=v_methods;
				break;
			}
			
		}		
	}

	//main method
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException	
	{
		Generator v_generator = new Generator();		
		gentest test_engine;
		//initialize framework for target class and function
		//gentest test_engine = new gentest("file:///Users/annademydova/Documents/workspace/randoopTestProject/bin/", "core.Util", "edlich");
		
		//if quantitiy of arguments doesnt correspond to expectation - close the programm
		if (args.length !=4) {
			System.out.println("INITIALIZING ERROR: You need to give 4 arguments");
			return;
		}	
		
		//try to initialize the class variable with given arguments
		try{
			test_engine = new gentest(args[0], args[1], args[2]);
			time=Long.parseLong(args[3]);	
		}
		catch(ClassNotFoundException e){
	        System.out.println("INITIALIZING ERROR: Class cannot be found under given URL.");
	        return;
	     }
		catch(NumberFormatException e){
	        System.out.println("INITIALIZING ERROR: Give testing time doesnt correspond to expected format (LONG).");
	        return;
	     }
		catch (Exception e) {
				// Error notification
				System.out.println(e);
				return;
	    	}
			
		//if the method for testing is not found in the given class
		if (v_method==null){
			System.out.println("INITIALIZING ERROR: The method "+ args[2]+" is not found in class " +args[1]+".java!");
			return;
		}	

		
		//Take first constructor and parse his parameter list
		v_arg_constr=new Object[constructors[0].getParameterCount()];
		v_params_constr = constructors[0].getParameterTypes();
		
		//Parse parameter list of test function
		v_arg_testfunc=new Object[v_method.getParameterCount()];
		v_params_testfunc = v_method.getParameterTypes();
		
		//initialize variable of testing class
		v_generator.fill_arguments(v_arg_constr, v_params_constr);
		//instanse of testing class
		v_object=constructors[0].newInstance(v_arg_constr);
		
		
		//Timer Variable
    	long last = System.currentTimeMillis();
    	int i=0;
    	int failed=0;
    	int results_counter=0;
    	//the time for testing is set
    	while (System.currentTimeMillis() < last + time) {
    		try{
    			//invoke the function 	
    			i++;
    			v_generator.fill_arguments(v_arg_testfunc, v_params_testfunc);    		
    			v_method.invoke(v_object, v_arg_testfunc);
    			//System.out.println("Function results: "+v_method.invoke(v_object, v_arg_testfunc));	
    			//System.out.println("Number of test: " + i);
    		}
 
    		catch (Exception ex) {
    			results_counter++;
    			//counts the number of failed tests
    			failed++;
    			if(results_counter==1){
    				System.out.println("First 10 failed results");
    			}
    			//prints the first 10 negative results which brought to error
    			if(results_counter<10){
    				System.out.println();
    				System.out.println("Failure: " +results_counter);
    				//runs along the paramehers of the tested function
    				for(int k=0; k<v_params_testfunc.length; k++){	
    					//print the type of parameters of the tested function
    					System.out.print("Tested parameter: "+v_params_testfunc[k].getSimpleName() + " - ");
    					//if one of the parameters is array, it should be printed in a loop
    					if(v_params_testfunc[k].getSimpleName().contains("[]")){
    						//loop for array print: from 0 up to the length of array which is one of the parameters of the tested function
    						for(int j=0; j<Array.getLength(v_arg_testfunc[k]); j++){
    							System.out.print(Array.get(v_arg_testfunc[k], j).toString() + " "); 
    						}
    					}
    					else System.out.println(v_arg_testfunc[k]);
    				}
    				System.out.println();
			
    			}

	  	    }

    	}
    	
    	//Print the results of test
    	System.out.println();
    	System.out.println("*************************************************");
    	System.out.println("The tests were running within: " + time/1000 +"s");
    	System.out.println("Number of all tests: " + i);
    	System.out.println("Number of succeedeed tests: " + (i-failed));
    	System.out.println("Number of failed tests: " + failed);
    }
}