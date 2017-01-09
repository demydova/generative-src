package framework;

import java.security.SecureRandom;
import java.util.Random;

public class Generator {
	
	//a variable that hold all the possible chars of the English language
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
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
    public void fill_arguments(Object[] v_arg, Class[] v_params)
    {
    	String typName; //a variable for switch-case
    	
    	
    			for (int i=0;i<v_arg.length; i++)
    			{
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
    					case "byte": v_arg[i]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));	
    					break;
			
    					//Random generation of short values
    					case "short": v_arg[i]=(short) (new Random().nextInt(Short.MAX_VALUE + 1)); 
    					break;
			
    					//Random generation of int values
    					case "int": v_arg[i]=new Random().nextInt(12);
    					break;
			
    					//Random generation of long values
    					case "long": v_arg[i]=new Random().nextLong();
    					break;
			
    					//Random generation of float values
    					case "float": v_arg[i]=new Random().nextFloat();
    					break;
			
    					//Random generation of double values
    					case "double" : v_arg[i]=new Random().nextDouble();
    					break;
			
    					//Random generation of String values
    					case "String":v_arg[i]=randomString (new Random().nextInt(11));
    					break;
					
    					//Generation of values for arrays
    					//Random generation of int arrays
    					case "int[]":
    						int[] ar1 = new int[new Random().nextInt(11)];
    						for (int j=0; j<ar1.length; j++){
    							ar1[j]=new Random().nextInt(12);
    						}
    						v_arg[i]=ar1;
    						break;
    					
    					//Random generation of boolean arrays
    					case "boolean[]":
    						boolean[] ar2 = new boolean[new Random().nextInt(11)];
        					for (int j=0; j<ar2.length; j++){
        						ar2[j]=new Random().nextBoolean();
        					}
        					v_arg[i]=ar2;
        					break;
        					
        				//Random generation of char arrays
    					case "char[]":
    						char[] ar3 = new char[new Random().nextInt(11)];
        					for (int j=0; j<ar3.length; j++){
        						ar3[j]=(char)(new Random().nextInt(26) + 'a');
        					}
        					v_arg[i]=ar3;
        					break;
        				
        				//Random generation of byte arrays
    					case "byte[]":
    						byte[] ar4 = new byte[new Random().nextInt(11)];
        					for (int j=0; j<ar4.length; j++){
        						ar4[j]=(byte) (new Random().nextInt(Byte.MAX_VALUE + 1));
        					}
        					v_arg[i]=ar4;
        					break;
        					
        				//Random generation of short arrays
    					case "short[]":
    						short[] ar5 = new short[new Random().nextInt(11)];
        					for (int j=0; j<ar5.length; j++){
        						ar5[j]=(short) (new Random().nextInt(Short.MAX_VALUE + 1));
        					}
        					v_arg[i]=ar5;
        					break;
        				
        				//Random generation of long arrays	
    					case "long[]":
    						long[] ar6 = new long[new Random().nextInt(11)];
        					for (int j=0; j<ar6.length; j++){
        						ar6[j]=new Random().nextLong();
        					}
        					v_arg[i]=ar6;
        					break;
        				
        				//Random generation of float arrays
    					case "float[]":
    						float[] ar7 = new float[new Random().nextInt(11)];
        					for (int j=0; j<ar7.length; j++){
        						ar7[j]=new Random().nextFloat();
        					}
        					v_arg[i]=ar7;
        					break;
        					
        				//Random generation of double arrays
    					case "double[]":
    						double[] ar8 = new double[new Random().nextInt(11)];
        					for (int j=0; j<ar8.length; j++){
        						ar8[j]=new Random().nextDouble();
        					}
        					v_arg[i]=ar8;
        					break;
        					
        				//Random generation of String arrays
    					case "String[]":
    						String[] ar9 = new String[new Random().nextInt(11)];
        					for (int j=0; j<ar9.length; j++){
        						ar9[j]=randomString (new Random().nextInt(11));
        					}
        					v_arg[i]=ar9;
        					break;
			
    					default : v_arg[i]="x";
    					break;
    					}	
    			}
    
  	  
    }   
}
