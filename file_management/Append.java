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
public class Append {
    	
    
    static void append( int curr, byte[] toBeAppended, int offsetOfString, int endOfFile, int ind ) throws Exception
	{
		if( endOfFile<0 )
		{
			int addressData = getDataAddress(curr);
			int noOfBytesToBeWritten = Math.min(clustersize, toBeAppended.length-offsetOfString);
			file.seek(addressData);
			file.write(toBeAppended, offsetOfString, noOfBytesToBeWritten);
			offsetOfString+=noOfBytesToBeWritten;
			if( offsetOfString==toBeAppended.length )
			{
				setEndOfFile( ind, noOfBytesToBeWritten );
			}
			else
			{
				int next = pqfat.poll();
				setSizeOfFile(ind,1,"add");
				setFATEntry(curr,next);
				setFATEntry(next,totalfat+1);
				append( next, toBeAppended, offsetOfString, endOfFile, ind );
			}
		}
		else
		{
			int next = getNextCluster(curr);
			if( next==(totalfat+1) )
			{
				int addressData = getDataAddress(curr);
				file.seek(addressData+endOfFile);
				int noOfBytesToBeWritten = Math.min(clustersize-endOfFile, toBeAppended.length);
				file.write(toBeAppended, 0, noOfBytesToBeWritten);
				offsetOfString+=noOfBytesToBeWritten;
				if( offsetOfString==toBeAppended.length )
				{
					setEndOfFile(ind, noOfBytesToBeWritten+endOfFile);
				}
				else
				{
					next = pqfat.poll();
					setSizeOfFile(ind,1,"add");
					setFATEntry(curr, next);
					setFATEntry(next, totalfat+1); 
					append(next,toBeAppended,noOfBytesToBeWritten,-1,ind);
				}
			}
			else
			{
				append(next,toBeAppended,offsetOfString,endOfFile,ind);
			}
		}
	}
	
	static void appendFile(  String name, byte[] toBeAppended ) throws Exception
	{
		int ind = index.get(name);
		int firstCluster = basetodec(getDataFromFileEntry(ind, offfirstcluster, 3));
		int next = getNextCluster(firstCluster);
		int endOfFile = basetodec(getDataFromFileEntry(ind, offEnd, 1));
		if( next==(totalfat+1) )
		{
			int addressData = getDataAddress(firstCluster);
			file.seek(addressData+endOfFile);
			int noOfBytesToBeWritten = Math.min(clustersize-endOfFile, toBeAppended.length);
			file.write(toBeAppended, 0, noOfBytesToBeWritten);
			
			if( noOfBytesToBeWritten==toBeAppended.length )
			{
				setEndOfFile(ind, endOfFile+noOfBytesToBeWritten);
			}
			else
			{
				next = pqfat.poll();
				
				setSizeOfFile(ind,1,"add");
				
				setFATEntry(firstCluster, next);
				
				setFATEntry(next, totalfat+1);
				
				append( next, toBeAppended, noOfBytesToBeWritten, -1, ind );
			}
		}
		else
		{
			append( next, toBeAppended, 0, endOfFile, ind );
		}
	}
	
}
