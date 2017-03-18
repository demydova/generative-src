//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//Author: 		Ganna Demydova
//Description: 	Saving and recovering of datasets, which was identified
//				in the testing phase
//Version		00.14 12.02.2017
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

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

	// saving the arguments, which was identified in the testing phase
	public static void serialize(List<Object[]> arc_v_arg_testfunc, Class[] v_params_testfunc, String classname,
			String funcname) {
		try {
			// instance of XMLEncoder with the file name, consisting of class
			// name and method name
			XMLEncoder o = new XMLEncoder(
					new BufferedOutputStream(new FileOutputStream(classname + funcname + ".xml")));
			o.writeObject(arc_v_arg_testfunc);
			o.writeObject(v_params_testfunc);
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// restoring of inputs, which lead to exception - for securing of
	// replicability
	// any warnings referring to generics switched off, as we are sure, that
	// everything is correct
	@SuppressWarnings("unchecked")
	public void deserialize(String classname, String funcname) {
		try {
			// de-initializing of vaiables, in case they was used before
			rec_v_arg_testfunc = null;
			rec_v_params_testfunc = null;
			// reading of file with corresponding name
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(classname + funcname + ".xml")));
			rec_v_arg_testfunc = (List<Object[]>) d.readObject();
			rec_v_params_testfunc = (Class[]) d.readObject();
			d.close();
		} catch (FileNotFoundException ex) {
		}
	}

	// get deserialized list of arrays with arguments
	public List<Object[]> get_args() {
		return rec_v_arg_testfunc;
	}

	// get deserialized array of type of parameters
	public Class[] get_parms() {
		return rec_v_params_testfunc;
	}

}
