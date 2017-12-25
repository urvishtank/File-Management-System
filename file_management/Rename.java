/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_management;

import static file_management.File_management.*;
import java.io.IOException;

/**
 *
 * @author urvish
 */
public class Rename {
  
    
    static void rename( String oldName, String newName ) throws IOException
	{
		int ind = index.get(oldName);
		index.remove(oldName);
		index.put(newName, ind);
		StringBuilder sb = new StringBuilder();
		for( int i=0 ; i<offcreated ; i++ )
			sb.append((char)0);
		int address = getAddressFromFileEntry(index.get(newName));
		file.seek(address);
		file.write(sb.toString().getBytes());
		file.seek(address);
		file.write(newName.getBytes());
	}
	
}
