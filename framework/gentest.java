//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//Author: 		Ganna Demydova
//Description: 	main class and routing for testing framework
//Version		00.88 18.03.2017
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

package framework;

import library.GenTestAnnotation;
import sun.reflect.ReflectionFactory;
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

	// URL which should be parsed and afterwards reflected
	static URL[] v_url;
	// holder for loading of class for testing
	static Class<?> v_class;
	// variable for instance of testing class
	static Object v_object;
	// list of constructor for class for testing
	static Constructor[] constructors;
	// secondary element for loading of URLs
	static URLClassLoader v_cloader;
	// variable for method
	static Method v_method;
	// variable for setting the time of method duration
	static Long time;
	static String results = "";

	// List of arguments for creating of instance of consturctor and tartet
	// test-functions
	static Object[] v_arg_constr; // list of arguments for consturctor
	static Object[] v_arg_testfunc; // list of arguments for function
	static Object[] v_arg_testfunc_minimal; // list of arguments for function
	static Object[] v_arg_testfunc_minimal_test; // list of arguments for
													// function

	// List of types of arguments for creating of instance of constructor and
	// tartet test-functions

	// list of types of arguments for constructor
	static Class[] v_params_constr;
	// list of types of arguments for function
	static Class[] v_params_testfunc;
	// boolean array indicating from annotations avoiding instancing 0
	static boolean[] not_zero;
	// String array indicating the max and min values for array generation
	static String[][] extremum;
	// String array indicating the length for array generation
	static String[][] array_length;
	// secondary variable
	static int min_array_length;

	// ArrayList for serializing of the results
	static List<Object[]> arc_v_arg_testfunc = new ArrayList<Object[]>();

	// ArrayList for deserialized results
	static List<Object[]> rec_v_arg_testfunc = new ArrayList<Object[]>();
	// list of arguments for function
	static Class[] rec_v_params_testfunc;
	static boolean state = false;

	// constructor
	public gentest(String url, String classname) throws MalformedURLException, ClassNotFoundException {
		// set URL-variable
		v_url = new URL[] { new URL(url) };
		// set URLClassLoader
		v_cloader = new URLClassLoader(v_url);
		// set Class variable
		v_class = v_cloader.loadClass(classname);
		// get array of constructors
		constructors = v_class.getConstructors();

	}

	// method for parsing of annotations from runtime-code of the tested
	// function
	public static boolean getAnnotations() {
		// inctancing of the secondary arrays according to the length of array
		// of input parameters
		not_zero = new boolean[v_arg_testfunc.length];
		extremum = new String[v_arg_testfunc.length][2];
		array_length = new String[v_arg_testfunc.length][2];
		boolean v_exclude = false;

		try {
			// get all annotation of the corresponding method
			Annotation[] annotations = v_method.getAnnotations();
			for (Annotation annotation : annotations) {
				// check if annotation has expected relevant type
				if (annotation.annotationType().getSimpleName().equals("GenTestAnnotation"))
					;
				{
					// read corresponding annotation with the given interface
					// definition
					GenTestAnnotation an_v_method = v_method.getAnnotation(GenTestAnnotation.class);
					// check if annotation contain the skipping instruction: if
					// yes - skip further parsing returning true
					if (an_v_method.instruction().equals("exclude"))
						return true;

					for (int i = 0; i < v_arg_testfunc.length; i++) {
						not_zero[i] = false;
						extremum[i][0] = "";
						extremum[i][1] = "";
						array_length[i][0] = "";
						array_length[i][1] = "";

						// split annotation related to the function by ";" char;
						for (String v_annnotation_part : an_v_method.parameters().split(";")) {
							// if in the splitted part the name of parameters
							// appears then analyse it further
							if (v_annnotation_part.contains(v_method.getParameters()[i].getName())) {
								// if we have identified, that splitted part
								// contain the name of the target parameter and
								// also "not 0" substring, then set
								// corresponding boolean array, which is used
								// for generation of input parameters for the
								// function
								if (v_annnotation_part.contains("not 0")) {
									not_zero[i] = true;
								}

								// splitting the part, related to the
								// corresponding parameter by "," char
								for (String v_annnotation_comma : v_annnotation_part.split(",")) {

									// parsing of further properties of the
									// input parameters
									if (v_annnotation_comma.contains("max")) {
										extremum[i][1] = v_annnotation_comma.substring(
												v_annnotation_comma.indexOf("max=") + 4, v_annnotation_comma.length());
										System.out
												.println("found max: " + extremum[i][1] + " for argument number: " + i);
									}
									if (v_annnotation_comma.contains("min")) {
										extremum[i][0] = v_annnotation_comma.substring(
												v_annnotation_comma.indexOf("min=") + 4, v_annnotation_comma.length());
										System.out
												.println("found min: " + extremum[i][0] + " for argument number: " + i);
									}
									if (v_annnotation_comma.contains("arrayMax")) {
										array_length[i][1] = v_annnotation_comma.substring(
												v_annnotation_comma.indexOf("arrayMax=") + 9,
												v_annnotation_comma.length());
										System.out.println("found array_length_max: " + array_length[i][1]
												+ " for argument number: " + i);
									}
									if (v_annnotation_comma.contains("arrayMin")) {
										array_length[i][0] = v_annnotation_comma.substring(
												v_annnotation_comma.indexOf("arrayMin=") + 9,
												v_annnotation_comma.length());
										min_array_length = Integer.parseInt(array_length[i][0]);
										System.out.println("found array_length_min: " + array_length[i][0]
												+ " for argument number: " + i);
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return v_exclude;
	}

	// the function that compares the number and types of saved parameters in
	// xml file with those in the tested function
	public static void compare_parm() {
		if (rec_v_params_testfunc != null) {
			for (int i = 0; i < rec_v_params_testfunc.length; i++) {
				// if parameters are not identical, the recovered list of data
				// will be
				// cleared
				if (!((rec_v_params_testfunc.length == v_params_testfunc.length)
						&& (rec_v_params_testfunc[i].getSimpleName().equals(v_params_testfunc[i].getSimpleName())))) {
					System.out.println("The parameters have changed, skipping the history of failures");
					rec_v_arg_testfunc.clear();
				}
			}
		}
	}

	// the function that looks for minimal failing case
	public static void minimal_failing_case() throws InstantiationException, IllegalAccessException {
		// iteration number
		int iteration = 0;
		int element = 0;
		// list of arguments, which can be minimized
		List<Integer> v_arg_list = new ArrayList<Integer>();
		// cloning current set of input parameters
		v_arg_testfunc_minimal = v_arg_testfunc.clone();

		// look trough list of arguments and find index of those, which can be
		// optimized (Strings or Arrays)
		for (int i = 0; i < v_params_testfunc.length; i++) {
			if (v_params_testfunc[i].getSimpleName().contains("[]")) {
				if (Array.getLength(v_arg_testfunc_minimal[i]) > 1
						&& Array.getLength(v_arg_testfunc_minimal[i]) > min_array_length)
					v_arg_list.add(i);
			}
			if (v_params_testfunc[i].getSimpleName().contains("String")) {
				if (((String) v_arg_testfunc_minimal[i]).length() > 1
						&& ((String) v_arg_testfunc_minimal[i]).length() > min_array_length)
					v_arg_list.add(i);
			}
		}
		// go trough 100 iterations and look for simplification of input dataset
		while (iteration < 100 && v_arg_list.size() > 0) {
			// by each step try to simplify next parameter
			element = iteration % v_arg_list.size();
			// clone already simplified version for test
			v_arg_testfunc_minimal_test = v_arg_testfunc_minimal.clone();
			// delete one element from the current parameter
			v_arg_testfunc_minimal_test[v_arg_list.get(element)] = Generator.delete_random_element(v_params_testfunc,
					v_arg_testfunc_minimal_test[v_arg_list.get(element)], v_arg_list.get(element));

			try {
				////////////////////////////////////////////////////////////////////////////
				// invoke the function and check for exception
				v_method.invoke(v_object, v_arg_testfunc_minimal_test);
			} catch (Exception ex) {
				// catch found element
				v_arg_testfunc_minimal = v_arg_testfunc_minimal_test.clone();
				// if length==1 exclude from further simplification procedure
				if (v_params_testfunc[v_arg_list.get(element)].getSimpleName().contains("[]")) {
					if (Array.getLength(v_arg_testfunc_minimal[v_arg_list.get(element)]) == 1
							|| Array.getLength(v_arg_testfunc_minimal[v_arg_list.get(element)]) <= min_array_length)
						v_arg_list.remove(element);
				} else {
					if (((String) v_arg_testfunc_minimal[v_arg_list.get(element)]).length() == 1
							|| ((String) v_arg_testfunc_minimal[v_arg_list.get(element)]).length() <= min_array_length)
						v_arg_list.remove(element);
				}
			}
			iteration++;
		}
	}

	// main method
	public static void main(String[] args)
			throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {

		// instance relevant class variables
		Generator v_generator = new Generator();
		gentest test_engine;
		Persist v_persist = new Persist();

		// evaluate the list of classes
		List<String> classes = ClassFinder.find(args[0], args[1]);

		// if quantity of arguments doesnt correspond to expectation - close
		// the program
		if (args.length != 3) {
			System.out.println("INITIALIZING ERROR: You need to give 3 arguments");
			return;
		}

		// go trough all classes
		for (String var_class : classes) {

			System.out.println("++++++++++++++++++++++++++++++START TESTING FOR CLASS " + var_class
					+ "+++++++++++++++++++++++++++++++++++");

			// try to initialize the class variable with given arguments
			try {
				test_engine = new gentest("file://" + args[0], var_class);
				time = Long.parseLong(args[2]);
			} catch (ClassNotFoundException e) {
				System.out.println("INITIALIZING ERROR: Class cannot be found under given URL.");
				return;
			} catch (NumberFormatException e) {
				System.out
						.println("INITIALIZING ERROR: Give testing time doesnt correspond to expected format (LONG).");
				return;
			} catch (Exception e) {
				// Error notification
				System.out.println(e);
				return;
			}

			if (constructors.length > 0) {
				// Take first constructor and parse his parameter list
				v_arg_constr = new Object[constructors[0].getParameterCount()];
				v_params_constr = constructors[0].getParameterTypes();

				// initialize variable of testing class
				v_generator.fill_arguments(v_arg_constr, v_params_constr, null, new String[v_arg_constr.length][2],
						new String[v_arg_constr.length][2]);
				// instanse of testing class
				v_object = constructors[0].newInstance(v_arg_constr);
			} else {
				// if theris no constructor - use the bridge solution for
				// initializing
				v_object = SilentObjectCreator.create(v_class);
			}

			//////////////////////////////////////////
			// looking for instance of tested function
			for (Method v_methods : v_class.getDeclaredMethods()) {
				System.out.println(
						"+++++++++++++++STARTING TESTING FOR FUNCTION " + v_methods.getName() + "+++++++++++++++");
				v_method = v_methods;
				// make accessable in case of restrictions
				v_method.setAccessible(true);

				// Parse parameter list of test function
				v_arg_testfunc = new Object[v_method.getParameterCount()];
				v_params_testfunc = v_method.getParameterTypes();
				rec_v_arg_testfunc = null;
				rec_v_params_testfunc = null;
				arc_v_arg_testfunc.clear();

				// initializing of annotations
				if (getAnnotations() == true) {
					System.out.println("Instruction for skipping of testing identified");
					System.out.println(
							"+++++++++++++++END TESTING FOR FUNCTION " + v_methods.getName() + "++++++++++++++++++++");
					System.out.println("");
					continue;
				}

				/////////////////////////////////////////////////////////////////////
				// TESTING

				// recover already available datasets
				v_persist.deserialize(v_class.getName(), v_method.getName());
				rec_v_arg_testfunc = v_persist.get_args();
				rec_v_params_testfunc = v_persist.get_parms();

				// compare, if something is changed in the structure of input
				// parameters
				compare_parm();

				// timer Variable

				long last = System.currentTimeMillis();
				// further secondary variables
				int i = 0;
				int failed = 0;
				int results_counter = 0;
				int v_rec_count = 0;

				if (rec_v_arg_testfunc != null)
					v_rec_count = rec_v_arg_testfunc.size();

				// go until given time is over
				while (System.currentTimeMillis() < last + time) {

					// start testing with failed results from previous testing
					// or generate the input
					try {

						if (i < v_rec_count) {
							v_arg_testfunc = rec_v_arg_testfunc.get(i);
						} else {
							v_generator.fill_arguments(v_arg_testfunc, v_params_testfunc, not_zero, extremum,
									array_length);
						}

						// increment the number of test
						i++;

						////////////////////////////////////////////////////////////////////////////
						// invoke the function
						v_method.invoke(v_object, v_arg_testfunc);
					}

					catch (Exception ex) {

						results_counter++;
						// counts the number of failed tests
						failed++;

						// storing set of input arguments, which lead to
						// exception
						arc_v_arg_testfunc.add(v_arg_testfunc.clone());

						if (results_counter == 1) {
							System.out.println();
							System.out.println("First 10 failed results");
						}

						// prints the first 10 negative results which brought to
						// error
						if (results_counter <= 10) {
							System.out.println();
							System.out.println("Failure: " + results_counter);
							// runs along the paramehers of the tested function
							for (int k = 0; k < v_params_testfunc.length; k++) {
								// print the type of parameters of the tested
								// function
								System.out.print(
										"Input parameter " + k + ": " + v_params_testfunc[k].getSimpleName() + " - ");
								// if one of the parameters is array, it should
								// be printed in a loop
								if (v_params_testfunc[k].getSimpleName().contains("[]")) {
									// loop for array print: from 0 up to the
									// length of array which is one of the
									// parameters of the tested function
									for (int j = 0; j < Array.getLength(v_arg_testfunc[k]); j++) {
										System.out.print(Array.get(v_arg_testfunc[k], j).toString() + " ");
									}
								} else
									System.out.println(v_arg_testfunc[k]);
							}
							System.out.println();

							// identifying the source of mistake
							System.out.println("+++++++++++++exception++++++++++++");
							System.out.println(ex);
							StackTraceElement[] trace = ex.getCause().getStackTrace();
							for (int k = 0; k < trace.length; k++) {
								if (trace[k].toString().contains(v_method.getName())) {
									System.out.println("Source of exception: " + trace[k].toString());
								}
							}
							

							////////////////////////////////////////////////////////////////////////////
							// look for minimal failing case
							minimal_failing_case();
							System.out.println("+++++++minimal failing case+++++++");

							// runs along the paramehers of the tested function
							for (int k = 0; k < v_params_testfunc.length; k++) {
								// print the type of parameters of the tested
								// function
								System.out.print(
										"Input parameter " + k + ": " + v_params_testfunc[k].getSimpleName() + " - ");
								// if one of the parameters is array, it should
								// be printed in a loop
								if (v_params_testfunc[k].getSimpleName().contains("[]")) {
									// loop for array print: from 0 up to the
									// length of array which is one of the
									// parameters of the tested function
									for (int j = 0; j < Array.getLength(v_arg_testfunc_minimal[k]); j++) {
										System.out.print(Array.get(v_arg_testfunc_minimal[k], j).toString() + " ");
									}
								} else
									System.out.println(v_arg_testfunc_minimal[k]);
							}
							System.out.println("");
						}
					}
				}

				// Print the results of test
				System.out.println();
				System.out.println("*************************************************");
				System.out.println("*************************************************");
				System.out.println("*************************************************");
				System.out.println("The tests were running within: " + time / 1000 + "s");
				System.out.println("Number of all tests: " + i);
				System.out.println("Number of succeedeed tests: " + (i - failed));
				System.out.println("Number of failed tests: " + failed);

				// storing of tests
				// serialize();
				v_persist.serialize(arc_v_arg_testfunc, rec_v_params_testfunc, v_class.getName(), v_method.getName());

				//////////////////////////////////////////////
				System.out.println(
						"+++++++++++++++END TESTIG FOR FUNCTION " + v_methods.getName() + "++++++++++++++++++++");
				System.out.println("");

			}

			System.out.println("++++++++++++++++++++++++++++++END TESTING FOR CLASS " + var_class
					+ "+++++++++++++++++++++++++++++++++++");
		} // cycle for for going trough the packages

	}
}