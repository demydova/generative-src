package framework;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.Random;

public class Generator {
	
	//a variable that hold all the possible chars of the English language
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	// if no annotation, then default length of array is 10	
	int len_max=10;
	int len_min=0;

	
	//Method for random String generation
	//len is a variable, that  is changed with each test 
	static String randomString (int len)
	{
		// sb-instanse from StringBuilder, convert the given data to a String 
		StringBuilder sb = new StringBuilder( len );
		for( int i = 0; i < len; i++ ) sb.append( AB.charAt( new Random().nextInt(AB.length()) ) );
		return sb.toString();
	}	
	//function for filling of argument list with random values depending on the parameter types
    public void fill_arguments(Object[] v_arg, Class[] v_params, boolean[] not_zero, String[][] extremum, String[][] array_length )
    {
    	String typName; //a variable for switch-case
    	//if not_zero equals null, then initialize it as false
    	if (not_zero==null)
    	{
    		not_zero=new boolean[v_arg.length];
    		for(int k=0; k<not_zero.length;k++)
    		{
    			not_zero[k]=false;
    		}
    	}

    	
    	//setting default conditions
    	
    	for(int i=0; i<v_arg.length; i++){
    		if (not_zero==null) not_zero[i]=false;
    		if (extremum[i][0]==null) extremum[i][0]="0";
    		if (extremum[i][1]==null) extremum[i][1]="100";
    		if (array_length[i][0]==null) array_length[i][0]="1";
    		if (array_length[i][1]==null) array_length[i][1]="20";

    	}
    	
    	for (int i=0;i<v_arg.length; i++){
    		//initialisation of the variable for switch case
    		typName=v_params[i].getSimpleName(); 
    		switch (typName)
    			{
					//Random generation of boolean values
					case "boolean": v_arg[i]=new Random().nextBoolean(); 
					break;
					
					//Random generation of char values
					case "char": v_arg[i]=(char)(new Random().nextInt(26) + 'a'); 
					break;
					
					//Random generation of byte values
					case "byte": 
						if(not_zero[i]!=true){
							v_arg[i]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));
						}else
						{
							do{
								v_arg[i]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));
							   } 
							while((byte)v_arg[i]==0);
						}		
					break;
					
					//Random generation of short values
					case "short": 
						if(not_zero[i]!=true){
							v_arg[i]=(short) (new Random().nextInt(Short.MAX_VALUE + 1)); 
						}else
						{
							do{
								v_arg[i]=(short) (new Random().nextInt(Short.MAX_VALUE + 1));
							   } 
							while((short)v_arg[i]==0);
						}		
					break;
					
					//Random generation of int values
					case "int": 
						if(not_zero[i]){
							v_arg[i]=new Random().nextInt(12);
						}else
						{
							do{
									v_arg[i]=new Random().nextInt(12);
							   } 
							while((int)v_arg[i]==0);
						}		
					break;
					
					//Random generation of long values
					case "long": 
						if(not_zero[i]!=true){
							v_arg[i]=new Random().nextLong();
						}else
						{
							do{
								v_arg[i]=new Random().nextLong();
							   } 
							while((long)v_arg[i]==0);
						}
					break;
					
					//Random generation of float values
					case "float": 
						if(not_zero[i]!=true){
							v_arg[i]=new Random().nextFloat();
						}else
						{
							do{
								v_arg[i]=new Random().nextFloat();
							   } 
							while((float)v_arg[i]==0);
						}
					break;
					
					//Random generation of double values
					case "double" : 
						if(not_zero[i]!=true){
							v_arg[i]=new Random().nextDouble();
						}else
						{
							do{
								v_arg[i]=new Random().nextDouble();
							   } 
							while((double)v_arg[i]==0);
						}
					break;
					
					//Random generation of String values
					case "String":v_arg[i]=null;
					//the length of array is generated according to annotation
					if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
					if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);
					v_arg[i] = randomString(new Random().nextInt(len_max-len_min+1)+len_min);
					}
					break;
					
					//Generation of values for arrays
					//Random generation of int arrays
					case "int[]":
						
						int[] ar1 = null;
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);

						ar1 = new int[new Random().nextInt(len_max-len_min+1)+len_min];}

						
						
						//definine of extremum values
						int v_max=11;
						int v_min=0;
						//parse possible limits, given in the annotations
						if(!extremum[i][0].equals("")) v_min=Integer.parseInt(extremum[i][0]);
						if(!extremum[i][1].equals("")) v_max=Integer.parseInt(extremum[i][1]);
						//generates the values in limits of extremems
						for (int j=0; j<ar1.length; j++){
							//excludes "0" in generation
							if(not_zero[i]!=true){
								ar1[j]=new Random().nextInt(v_max - v_min + 1) + v_min;
							}else
							{
								do{
									ar1[j]=new Random().nextInt(v_max - v_min + 1) + v_min;
								   } 
								while((int)ar1[j]==0);
							}
						}
						//the first and last element of array is set with extremum value
						if(ar1.length>1)
						{
							Array.setInt(ar1, 0, v_min);
							Array.setInt(ar1, ar1.length-1, v_max);
						}
						//if the generated array contains only 1 element, let it be the minimum one
						if(ar1.length==1)
						{
							Array.setInt(ar1, 0, v_min);
						}
						
						v_arg[i]=ar1;
						break;
					
					//Random generation of boolean arrays
					case "boolean[]":
						
						boolean[] ar2 = new boolean[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);

						ar2 = new boolean[new Random().nextInt(len_max-len_min+1)+len_min];}
						for (int j=0; j<ar2.length; j++){
							ar2[j]=new Random().nextBoolean();
						}
						v_arg[i]=ar2;
						break;
						
					//Random generation of char arrays
					case "char[]":
						char[] ar3 = new char[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);

						ar3 = new char[new Random().nextInt(len_max-len_min+1)+len_min];}
						for (int j=0; j<ar3.length; j++){
							ar3[j]=(char)(new Random().nextInt(26) + 'a');
						}
						v_arg[i]=ar3;
						break;
					
					//Random generation of byte arrays
					case "byte[]":
						byte[] ar4 = new byte[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);

						ar4 = new byte[new Random().nextInt(len_max-len_min+1)+len_min];}
						for (int j=0; j<ar4.length; j++){
							if(not_zero[i]!=true){
								ar4[j]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));
							}else
							{
								do{
									ar4[j]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));
								   } 
								while((byte)ar4[j]==0);
							}
						}
						v_arg[i]=ar4;
						break;
						
					//Random generation of short arrays
					case "short[]":
						short[] ar5 = new short[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);
						ar5 = new short[new Random().nextInt(len_max-len_min+1)+len_min];
						}

						for (int j=0; j<ar5.length; j++){
							if(not_zero[i]!=true){
								ar5[j]=(short) (new Random().nextInt(Short.MAX_VALUE + 1));
							}else
							{
								do{
									ar5[j]=(short) (new Random().nextInt(Short.MAX_VALUE + 1));
								   } 
								while((short)ar5[j]==0);
							}
						}
						v_arg[i]=ar5;
						break;
					
					//Random generation of long arrays	
					case "long[]":
						long[] ar6 = new long[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);
						ar6 = new long[new Random().nextInt(len_max-len_min+1)+len_min];
						}
						for (int j=0; j<ar6.length; j++){
							if(not_zero[i]!=true){
								ar6[j]=new Random().nextLong();
							}else
							{
								do{
									ar6[j]=new Random().nextLong();
								   } 
								while((long)ar6[j]==0);
							}
						}
						v_arg[i]=ar6;
						break;
					
					//Random generation of float arrays
					case "float[]":
						float[] ar7 = new float[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);
						ar7 = new float[new Random().nextInt(len_max-len_min+1)+len_min];
						}
						for (int j=0; j<ar7.length; j++){
							if(not_zero[i]!=true){
								ar7[j]=new Random().nextFloat();
							}else
							{
								do{
									ar7[j]=new Random().nextFloat();
								   } 
								while((float)ar7[j]==0);
							}
						}
						v_arg[i]=ar7;
						break;
						
					//Random generation of double arrays
					case "double[]":
						double[] ar8 = new double[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);
						ar8 = new double[new Random().nextInt(len_max-len_min+1)+len_min];
						}
						for (int j=0; j<ar8.length; j++){
							if(not_zero[i]!=true){
								ar8[j]=new Random().nextDouble();
							}else
							{
								do{
									ar8[j]=new Random().nextDouble();
								   } 
								while((double)ar8[j]==0);
							}
						}
						v_arg[i]=ar8;
						break;
						
					//Random generation of String arrays
					case "String[]":
						String[] ar9 = new String[new Random().nextInt(len_max)];
						//the length of array is generated according to annotation
						if(!array_length[i][0].equals("")) len_min=Integer.parseInt(array_length[i][0]);
						if(!array_length[i][1].equals("")){ len_max=Integer.parseInt(array_length[i][1]);}
						for (int j=0; j<ar9.length; j++){
							ar9[j]=randomString (new Random().nextInt(len_max));
						}
						v_arg[i]=ar9;
						break;
					
					default : v_arg[i]="null";
					break;
					}	
    			}
    
  	  
    }   
    
    //the function that look for minimal failing case
    public static Object delete_random_element(Class[] v_params_testfunc, Object v_in_object, int element) throws InstantiationException, IllegalAccessException{
    	Object result=v_in_object;
    	int del;
    	if(v_params_testfunc[element].getSimpleName().contains("[]"))
    			{
    				
    				Object res= Array.newInstance(v_params_testfunc[element].getComponentType(), Array.getLength(v_in_object)-1);
    				del=new Random().nextInt(Array.getLength(v_in_object));
    				int k=0;
    				for(int i=0; i<(Array.getLength(v_in_object)-1);i++)
    				{
    					if(i==del) k++;
    					Array.set(res, i, Array.get(v_in_object, k));
    					k++;	
    				}
    				result=res;
    			}
    	else{
    		result=((String) v_in_object).replaceFirst(""+((String) v_in_object).charAt(new Random().nextInt(((String) v_in_object).length()-1)), "");
    	}
 	return result;
    }
}
