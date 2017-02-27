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
	static Object[] v_arg_constr;			//list of arguments for consturctor
	static Object[] v_arg_testfunc;			//list of arguments for function
	static Object[] v_arg_testfunc_minimal;	//list of arguments for function
	static Object[] v_arg_testfunc_minimal_test;	//list of arguments for function
	
	//List of types of arguments for creating of instance of consturctor and tartet test-functions
	static Class[] v_params_constr;		//list of types of arguments for consturctor
	static Class[] v_params_testfunc;	//list of types of arguments for function
	static boolean[] not_zero;			//boolean array indicating from annotations avoiding instancing 0
	static String[][] extremum;			//String array indicating the max and min values for array generation
	static String [][]array_length;		//String array indicating the length for array generation
	static int min_array_length;
	
	//ArrayList for serializing of the results
	static List<Object[]> arc_v_arg_testfunc = new ArrayList<Object[]>();
	
	//ArrayList for deserialized results
	static List<Object[]> rec_v_arg_testfunc = new ArrayList<Object[]>();
	static Class[] rec_v_params_testfunc;	//list of arguments for function
	static boolean state=false;
	
	
	//constructor
	public gentest(String url, String classname) throws MalformedURLException, ClassNotFoundException
	{
		v_url=new URL[] {new URL(url)};							//set URL-variable
		v_cloader = new URLClassLoader(v_url);					//set URLClassLoader
		v_class=v_cloader.loadClass(classname);					//set Class variable
		constructors = v_class.getConstructors();				//get array of constructors
				
	}
	
	//function for parsing of annotations from runtime-code of the tested function
	public static boolean getAnnotations(){
			//only for testing purpose - please delete this peace of code
			not_zero=new boolean [v_arg_testfunc.length];
			extremum=new String [v_arg_testfunc.length][2];
			array_length=new String [v_arg_testfunc.length][2];
			boolean v_exclude=false;

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
		            	
		            	if(an_v_method.instruction().equals("exclude")) return true;
		            	
		            	for(int i=0; i<v_arg_testfunc.length; i++){
		            		not_zero[i]=false;
		            		extremum[i][0]="";
		            		extremum[i][1]="";
		            		array_length[i][0]="";
		            		array_length[i][1]="";
		            		//split annotation related to the function by ";" char;
		            		for ( String v_annnotation_part : an_v_method.parameters().split(";") ){
		            			//if in the splitted part the name of parameters appears then analyse it further
		            			if (v_annnotation_part.contains(v_method.getParameters()[i].getName())){
		            				//if we have identified, that splitted part contain the name of the target parameter and also "not 0"
		            				//substring, then set corresponding boolean array, which is used for generation of input parameters
		            				//for the function
		            				if (v_annnotation_part.contains("not 0")){
			            				not_zero[i]=true;
			            				
			            			}	            				
		            			}
		            			//splitting the part, related to the correspnding parameter by "," char
			            		for ( String v_annnotation_comma : v_annnotation_part.split(",") ){
			            			//if relevant part containt the name of the target parameter
			            			if (v_annnotation_part.contains(v_method.getParameters()[i].getName())){
			            				
			            				if (v_annnotation_comma.contains("max")){	
			            					extremum[i][1]=v_annnotation_comma.substring(v_annnotation_comma.indexOf("max=")+4, v_annnotation_comma.length());
			            					System.out.println("found max: "+extremum[i][1]+" for argument number: " + i);
			            				}
			            				if (v_annnotation_comma.contains("min")){	
			            					extremum[i][0]=v_annnotation_comma.substring(v_annnotation_comma.indexOf("min=")+4, v_annnotation_comma.length());
			            					System.out.println("found min: "+extremum[i][0]+" for argument number: " + i);
			            				}
			            				if (v_annnotation_comma.contains("arrayMax")){	
			            					array_length[i][1]=v_annnotation_comma.substring(v_annnotation_comma.indexOf("arrayMax=")+9, v_annnotation_comma.length());
			            					System.out.println("found array_length_max: "+array_length[i][1]+" for argument number: " + i);
			            				}
			            				if (v_annnotation_comma.contains("arrayMin")){	
			            					array_length[i][0]=v_annnotation_comma.substring(v_annnotation_comma.indexOf("arrayMin=")+9, v_annnotation_comma.length());
			            					min_array_length=Integer.parseInt(array_length[i][0]);
			            					System.out.println("found array_length_min: "+array_length[i][0]+" for argument number: " + i);
			            					
			            				}
			            			
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
			
			return v_exclude;
			
	}
    
    //the function that compares the number and types of saved parameters in xml file with those in the tested function
    public static void compare_parm(){
		if(rec_v_params_testfunc!=null)
		{
			for (int i=0;i<rec_v_params_testfunc.length;i++){
				//if parameters are not identic, the saved List of data will be deleted in xml file
				if (!((rec_v_params_testfunc.length==v_params_testfunc.length)&&(rec_v_params_testfunc[i].getSimpleName().equals(v_params_testfunc[i].getSimpleName())))){
					System.out.println("The parameters have changed, skipping the history of failures");	
					rec_v_arg_testfunc.clear();
									
				}	
			}
		}
    }
    
    //the function that look for minimal failing case
    public static void minimal_failing_case() throws InstantiationException, IllegalAccessException{
    	//v_arg_testfunc, v_params_testfunc
    	//iteration number
    	int iteration=0;
    	int element=0;
    	//list of arguments, which can be minimized
    	List<Integer> v_arg_list = new ArrayList<Integer>();
    	
    	v_arg_testfunc_minimal=v_arg_testfunc.clone();
    	
    	//look trough list of arguments and find indext of those, which can be optimized
    	for(int i=0; i<v_params_testfunc.length;i++){
    		if(v_params_testfunc[i].getSimpleName().contains("[]")){
    			if(Array.getLength(v_arg_testfunc_minimal[i])>1) v_arg_list.add(i);
    		}
    		if(v_params_testfunc[i].getSimpleName().contains("String")){
    			if(((String) v_arg_testfunc_minimal[i]).length()>1) v_arg_list.add(i);
    		}
    		
    	}
    			while(iteration<100 && v_arg_list.size()>0){
    				element=iteration%v_arg_list.size();
    				v_arg_testfunc_minimal_test=v_arg_testfunc_minimal.clone();
    				v_arg_testfunc_minimal_test[v_arg_list.get(element)]=Generator.delete_random_element(v_params_testfunc, v_arg_testfunc_minimal_test[v_arg_list.get(element)], v_arg_list.get(element));
    				

    				
    				try{
    	    			
    	    			////////////////////////////////////////////////////////////////////////////
    	    			////////////////////////////////////////////////////////////////////////////
    	    			////////////////////////////////////////////////////////////////////////////
    	    			//invoke the function 	  		
    	    			v_method.invoke(v_object, v_arg_testfunc_minimal_test);

    	    		}
    	 
    	    		catch (Exception ex) {
    	    			//catch found element
    	    			v_arg_testfunc_minimal=v_arg_testfunc_minimal_test.clone();
    	    			//if length==1 delete from minimalizing
    	    			if(v_params_testfunc[v_arg_list.get(element)].getSimpleName().contains("[]"))
            			{
        		    		if(Array.getLength(v_arg_testfunc_minimal[v_arg_list.get(element)])==1 || Array.getLength(v_arg_testfunc_minimal[v_arg_list.get(element)])<=min_array_length) v_arg_list.remove(element);
            			}
        		    	else{
        		    		if(((String) v_arg_testfunc_minimal[v_arg_list.get(element)]).length()==1 || ((String) v_arg_testfunc_minimal[v_arg_list.get(element)]).length()<=min_array_length) v_arg_list.remove(element);
        		    	}
    	    					   	    			
    	    		}    				

    				iteration++;
    			}
    }
    


	//main method
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException	
	{
		Generator v_generator = new Generator();		
		gentest test_engine;
		Persist v_persist=new Persist();
		//initialize framework for target class and function
		//gentest test_engine = new gentest("file:///Users/annademydova/Documents/workspace/randoopTestProject/bin/", "core.Util", "edlich");
		
		//if quantitiy of arguments doesnt correspond to expectation - close the programm
		if (args.length !=3) {
			System.out.println("INITIALIZING ERROR: You need to give 3 arguments");
			return;
		}	
		
		//try to initialize the class variable with given arguments
		try{
			test_engine = new gentest(args[0], args[1]);
			time=Long.parseLong(args[2]);	
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
		

		
		//Take first constructor and parse his parameter list
		v_arg_constr=new Object[constructors[0].getParameterCount()];
		v_params_constr = constructors[0].getParameterTypes();
		
		
		//initialize variable of testing class
		v_generator.fill_arguments(v_arg_constr, v_params_constr, null, new String[v_arg_constr.length][2], new String[v_arg_constr.length][2]);
		//instanse of testing class
		v_object=constructors[0].newInstance(v_arg_constr);
		
		
		//////////////////////////////////////////
		//////////////////////////////////////////
		//////////////////////////////////////////
		//looking for instance of tested function
		for(Method v_methods : v_class.getDeclaredMethods())
		{
			System.out.println("+++++++++++++++STARTING TESTING FOR FUNCTION "+v_methods.getName()+"+++++++++++++++");
			v_method=v_methods;
			//Parse parameter list of test function
			v_arg_testfunc=new Object[v_method.getParameterCount()];
			v_params_testfunc = v_method.getParameterTypes();
			rec_v_arg_testfunc=null;
	        rec_v_params_testfunc = null;
	        arc_v_arg_testfunc.clear();

		
		
		
		//initializing of annotations
		if(getAnnotations()==true)
		{
			System.out.println("Instruction for skipping of testing identified");
			System.out.println("+++++++++++++++END TESTING FOR FUNCTION "+v_methods.getName()+"++++++++++++++++++++");
			System.out.println("");
			continue;
		}
		
		
		/////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////
		//TESTING
		

		v_persist.deserialize(v_class.getName(), v_method.getName());
		rec_v_arg_testfunc=v_persist.rec_v_arg_testfunc;
        rec_v_params_testfunc=v_persist.rec_v_params_testfunc;
        
		compare_parm();
		

		
		///check if parameter structure has changed - and if yes - clear up rec_v_arg_testfunc in order, that test engine doesnt use it
		
		//Timer Variable
    	long last = System.currentTimeMillis();
    	int i=0;
    	int failed=0;
    	int results_counter=0;
    	int v_rec_count=0;
    	
    	
    	if(rec_v_arg_testfunc!=null) v_rec_count=rec_v_arg_testfunc.size();
    	
    	System.out.println("Count stored: "+v_rec_count);
    	
    	//the time for testing is set
    	while (System.currentTimeMillis() < last + time) {
    		//start testing with failed results from previous testing
    		try{
    			
    			if(i<v_rec_count)
    			{
    				v_arg_testfunc=rec_v_arg_testfunc.get(i);
    			} else
    			{	
    				v_generator.fill_arguments(v_arg_testfunc, v_params_testfunc, not_zero, extremum, array_length); 
    			}
    			
    			//increment the number of test
    			i++;
    			
    			//System.out.println("Stored sampes "+rec_v_arg_testfunc.size());
    			
    			////////////////////////////////////////////////////////////////////////////
    			////////////////////////////////////////////////////////////////////////////
    			////////////////////////////////////////////////////////////////////////////
    			//invoke the function 	  		
    			v_method.invoke(v_object, v_arg_testfunc);
    			
    			
    			//System.out.println("Function results: "+v_method.invoke(v_object, v_arg_testfunc));	
    			//System.out.println(v_arg_testfunc[0]);
    		}
 
    		catch (Exception ex) {
    			
    			//ex.printStackTrace();
    			
    			results_counter++;
    			//counts the number of failed tests
    			failed++;
    			
    			//storing set of input arguments, which lead to exception
    			arc_v_arg_testfunc.add(v_arg_testfunc.clone());
    			
    			if(results_counter==1){
    				System.out.println("First 10 failed results");
    			}
    			//prints the first 10 negative results which brought to error
    			
    			
    			////10!!!!!
    			if(results_counter<10){
    				System.out.println();
    				System.out.println("Failure: " +results_counter);
    				//runs along the paramehers of the tested function
    				for(int k=0; k<v_params_testfunc.length; k++){	
    					//print the type of parameters of the tested function
    					System.out.print("Input parameter "+k+": "+v_params_testfunc[k].getSimpleName() + " - ");
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
    				
    				//identifying the source of mistake
    				System.out.println("+++++++source of exception+++++++");
    				StackTraceElement[] trace = ex.getCause().getStackTrace();
    				System.out.println("Message: "+ex.getCause().getMessage());
    				for(int k=0; k<trace.length; k++){	
    					if(trace[k].toString().contains(args[2])) 
    					{
    						System.out.println("Source of exception: "+trace[k].toString());
    						//System.out.println("File: "+trace[k].toString().substring(trace[k].toString().indexOf("(")+1,trace[k].toString().indexOf(")")));
    					}
    					
    				}

    				////////////////////////////////////////////////////////////////////////////
    				////////////////////////////////////////////////////////////////////////////
    				//look for minimal failing case
    				minimal_failing_case(); 
    				System.out.println("+++++++minimal failing case+++++++");
    				
    				//runs along the paramehers of the tested function
    				for(int k=0; k<v_params_testfunc.length; k++){	
    					//print the type of parameters of the tested function
    					System.out.print("Input parameter "+k+": "+v_params_testfunc[k].getSimpleName() + " - ");
    					//if one of the parameters is array, it should be printed in a loop
    					if(v_params_testfunc[k].getSimpleName().contains("[]")){
    						//loop for array print: from 0 up to the length of array which is one of the parameters of the tested function
    						for(int j=0; j<Array.getLength(v_arg_testfunc_minimal[k]); j++){
    							System.out.print(Array.get(v_arg_testfunc_minimal[k], j).toString() + " "); 
    						}
    					}
    					else System.out.println(v_arg_testfunc_minimal[k]);
    				}
    				System.out.println("");
    			}

	  	    }

    	}
    	
    	//Print the results of test
    	System.out.println();
    	System.out.println("*************************************************");
    	System.out.println("*************************************************");
    	System.out.println("*************************************************");
    	System.out.println("The tests were running within: " + time/1000 +"s");
    	System.out.println("Number of all tests: " + i);
    	System.out.println("Number of succeedeed tests: " + (i-failed));
    	System.out.println("Number of failed tests: " + failed);
    	
    	//storing of tests
    	//serialize();
    	v_persist.serialize(arc_v_arg_testfunc, rec_v_params_testfunc, v_class.getName(), v_method.getName());
    	
    	
    	//////////////////////////////////////////////
    	System.out.println("+++++++++++++++END TESTIG FOR FUNCTION "+v_methods.getName()+"++++++++++++++++++++");
    	System.out.println("");
		
	}
    	
    	
    	
    }
}