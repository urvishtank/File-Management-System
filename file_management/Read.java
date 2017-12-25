/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_management;

import static file_management.File_management.*;

/**
 *
 * @author urvish
 */
public class Read {


static void read(int in, StringBuilder con, int endOfFile ) throws Exception
	{
		int next = getNextCluster(in);
		if( next!=(totalfat+1) )
		{
			byte[] b = new byte[clustersize];
			file.seek(getDataAddress(in));
			file.read(b);
			String a = byteToString(b);
			con.append(a);
			read( next, con, endOfFile );
		}
		else
		{
			if( endOfFile==0 ) return;
			byte[] b = new byte[endOfFile];
			file.seek(getDataAddress(in));
			file.read(b);
			String a = byteToString(b);
			con.append(a);
		}
	}
	
	    
}
