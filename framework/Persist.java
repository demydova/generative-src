package framework;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Persist {
	public List<Object[]> rec_v_arg_testfunc;
	public Class[] rec_v_params_testfunc;
	//saving the arguments, which leads to exception
	public static void serialize(List<Object[]> arc_v_arg_testfunc, Class[] v_params_testfunc, String classname, String funcname) {
        try {
            XMLEncoder o = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(classname+funcname+".xml")));
            o.writeObject(arc_v_arg_testfunc);
            o.writeObject(v_params_testfunc);
            o.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
	//restoring of inputs, which lead to exception - for securing of replicability
    @SuppressWarnings("unchecked")
    public void deserialize(String classname, String funcname) {
        try {
        	
        	rec_v_arg_testfunc=null;
            rec_v_params_testfunc=null;
            XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(classname+funcname+".xml")));
            rec_v_arg_testfunc = (List<Object[]>) d.readObject();
            rec_v_params_testfunc = (Class[]) d.readObject();
            d.close();
        } catch (FileNotFoundException ex) {
        }
    }
    

}
