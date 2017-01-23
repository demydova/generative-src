//V2 is refactoring of the code of V1, which was developed without essential structre from historical perpsective

package framework;
import library.GenTestAnnotation;

import java.util.Random;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
	static Class[] v_params_constr;		//list of types of arguments for consturctor
	static Class[] v_params_testfunc;	//list of types of arguments for function
	static boolean[] not_zero;			//boolean array indicating from annotations avoiding instancing 0
	
	//ArrayList for serializing of the results
	static List<Object[]> arc_v_arg_testfunc = new ArrayList<Object[]>();
	
	//ArrayList for deserialized results
	static List<Object[]> rec_v_arg_testfunc = new ArrayList<Object[]>();
	static Class[] rec_v_params_testfunc;	//list of arguments for function
	static boolean state=false;
	
	
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
	
	//function for parsing of annotations from runtime-code of the tested function
	public static void getAnnotations(){
			//only for testing purpose - please delete this peace of code
			not_zero=new boolean [v_arg_testfunc.length];

			try
		      {
		         Annotation[] annotations = v_method.getAnnotations();
		         //System.out.println(annotations[0].toString());
		         for (Annotation annotation : annotations)
		         {
		        	 //System.out.println(annotation.annotationType().getSimpleName());
		            if (annotation.annotationType().getSimpleName().equals("GenTestAnnotation"));
		            {	 
		            	
		            	
		            	GenTestAnnotation an_v_method = v_method.getAnnotation(GenTestAnnotation.class);
		            	//Array that keeps the parts of splitted string
		            	String[] parts = an_v_method.parameters().split(";");
		            	
		            	
		            	for(int i=0; i<v_arg_testfunc.length; i++){
		            		not_zero[i]=false;
		            		for ( String v_annnotation_part : an_v_method.parameters().split(";") ){
		            			
		            			if (v_annnotation_part.contains(v_method.getParameters()[i].getName())){
		            				if (v_annnotation_part.contains("not 0")){
			            				not_zero[i]=true;
			            			}	            				
		            			}
		            			
		            		}
		            	}
		            }
		         }
		      } catch (Exception e)
		      {
		         e.printStackTrace();
		      }
			
	}
	
	//saving the arguments, which leads to exception
	public static void serialize() {
        try {
            XMLEncoder o = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(v_method.getName()+".xml")));
            o.writeObject(arc_v_arg_testfunc);
            o.writeObject(v_params_testfunc);
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
	//restoring of inputs, which lead to exception - for securing of replicability
    @SuppressWarnings("unchecked")
    private static void deSerialize() {
        try {
            XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(v_method.getName()+".xml")));
            rec_v_arg_testfunc = (List<Object[]>) d.readObject();
            rec_v_params_testfunc = (Class[]) d.readObject();
            d.close();
        } catch (FileNotFoundException ex) {
        }
    }
    
    //the function that compares the number and types of saved parameters in xml file with those in the tested function
    public static void compare_parm(){
		
		for (int i=0;i<v_arg_testfunc.length;i++){
			//if parameters are not identic, the saved List of data will be deleted in xml file
			if (!((rec_v_params_testfunc.length==v_params_testfunc.length)&&(rec_v_params_testfunc[i].getSimpleName().equals(v_params_testfunc[i].getSimpleName())))){
				System.out.println("The parameters have changed");	
				rec_v_arg_testfunc.clear();
									
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
		v_generator.fill_arguments(v_arg_constr, v_params_constr, null);
		//instanse of testing class
		v_object=constructors[0].newInstance(v_arg_constr);
		
		
		//initializing of annotations
		getAnnotations();
		
		
		/////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		//TESTING
		
		//try to check the failed tests, which was already stored
		deSerialize();
		compare_parm();

		
		///check if parameter structure has changed - and if yes - clear up rec_v_arg_testfunc in order, that test engine doesnt use it
		
		//Timer Variable
    	long last = System.currentTimeMillis();
    	int i=0;
    	int failed=0;
    	int results_counter=0;
    	//the time for testing is set
    	while (System.currentTimeMillis() < last + time) {
    		//start testing with failed results from previous testing
    		try{
    			
    			if(i<rec_v_arg_testfunc.size())
    			{
    				v_arg_testfunc=rec_v_arg_testfunc.get(i);
    			} else
    			{	
    				v_generator.fill_arguments(v_arg_testfunc, v_params_testfunc, not_zero); 
    			}
    			
    			//increment the number of test
    			i++;
    			//invoke the function 	  		
    			v_method.invoke(v_object, v_arg_testfunc);
    			
    			
    			//System.out.println("Function results: "+v_method.invoke(v_object, v_arg_testfunc));	
    			//System.out.println(v_arg_testfunc[0]);
    		}
 
    		catch (Exception ex) {
    			results_counter++;
    			//counts the number of failed tests
    			failed++;
    			
    			//storing set of input arguments, which lead to exception
    			arc_v_arg_testfunc.add(v_arg_testfunc.clone());
    			
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
    	
    	//storing of tests
    	serialize();
    }
}