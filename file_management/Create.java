/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_management;

import static file_management.File_management.*;
import static file_management.time.*;

/**
 *
 * @author urvish
 */
public class Create {
    
     static int createFile( String name, char permissions ) throws Exception
    {
    	
    	if( name.length()>10 || (int)permissions>55 || pqfE.size()==0 || pqfat.size()==0 ) return -1;
    	
    	int first = pqfE.poll();
    	index.put(name, first);
    	int firstfat=pqfat.poll();
    	int address=getAddressFromFileEntry(first);
    	
    	file.seek(address);
    	file.write(name.getBytes());
    	
    	file.seek(address+offcreated);
    	file.write(getCurrDateAndTime().getBytes());
    	
    	file.seek(address+offmod);
    	file.write(getCurrDateAndTime().getBytes());
    	
    	file.seek(address+offsize);
    	file.write(regulate(decTo128(1),3).getBytes());
    	
    	file.seek(address+offpermi);
    	file.write((permissions+"").getBytes());
    	
    	file.seek(address+offfirstcluster);
    	file.write(regulate(decTo128(firstfat),3).getBytes());
    	
    	setFATEntry(firstfat, totalfat+1);
    	
    	file.seek(address+offEnd);
    	file.write(decTo128(0).getBytes());
    	return 1;
    }
    
}
