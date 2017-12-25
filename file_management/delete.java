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
public class delete {
   
    static void delete( String name ) throws Exception
	{
		int ind=index.get(name);
		index.remove(name);
		pqfE.add(ind);
		int address=getAddressFromFileEntry(ind);
		byte[] b = new byte[3];
		file.seek(address+offfirstcluster);
		file.read(b, 0, 3);
		String a = byteToString(b);
		int curr=basetodec(a);
		
		int next = getNextCluster(curr);
		file.seek(getAddressFromfat(curr));
		file.write(((char)(0)+""+(char)(0)+""+(char)(0)).getBytes());
		pqfat.add(curr);
		while( next!=(totalfat+1) )
		{
			curr=next;
			next = getNextCluster(next);
			file.seek(getAddressFromfat(curr));
			file.write(((char)(0)+""+(char)(0)+""+(char)(0)).getBytes());
			pqfat.add(curr);
		}
		file.seek(address);
		StringBuilder sb=new StringBuilder();
		for( int i=0 ; i<sizeOfFileEntry ; i++ )
			sb.append((char)0);
		file.write(sb.toString().getBytes());
	}
 
}
