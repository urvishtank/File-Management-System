/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file_management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
//import file_management.Accesss_Methods;
import static file_management.Accesss_Methods.*;
import static file_management.delete.*;
import static file_management.Rename.*;
import static file_management.Create.*;
import static file_management.Append.*;
import static file_management.Read.*;
import static file_management.time.*;

public class File_management 
{
	static int totalFileEntry=13268,clustersize=128,totalfat=20480000/clustersize;
	static RandomAccessFile file;
	static int sizeOfFileEntry,offFAT,sizeOfFATentry=3;
    static int offcreated=10,offmod=22,offsize=34,offpermi=37,offfirstcluster=38,offEnd=41,diskDataStartingPoint=1024000;
	static TreeMap<String, Integer> index = new TreeMap();
    static Queue<Integer> pqfE = new LinkedList();
    static Queue<Integer> pqfat = new LinkedList();
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static String getName( int in ) throws Exception
    {
    	int address = getAddressFromFileEntry(in);
    	byte[] b = new byte[10];
    	file.seek(address);
    	file.read(b);
    	String a = byteToString(b);
    	StringBuilder name=new StringBuilder();
    	for( int i=0 ; i<10 ; i++ ) 
    	{
    		if( a.charAt(i)==(char)0 ) break;
    		name.append(a.charAt(i));
    	}
    	return name.toString();
    }
    
    static int getFirstCluster( int in ) throws Exception
    {
    	int address = getAddressFromFileEntry(in);
    	byte[] b = new byte[3];
    	file.seek(address+offfirstcluster);
    	file.read(b);
    	String a = byteToString(b);
    	return basetodec(a);
    }
    
    static String getDataFromFileEntry( int in, int offset, int len ) throws Exception
    {
    	int address = getAddressFromFileEntry(in);
    	byte[] b = new byte[len];
    	file.seek(address+offset);
    	file.read(b);
    	String a = byteToString(b);
    	return a;
    }
    
	public static void main(String[] args) throws Exception
    {
		sizeOfFileEntry=42;
		totalFileEntry = (1024000-480000)/sizeOfFileEntry;
		offFAT = sizeOfFileEntry*totalFileEntry;
    	file = new RandomAccessFile("src/disk.txt","rw");
    	for( int i=0 ; i<totalFileEntry ; i++ )
    	{
    		file.seek(getAddressFromFileEntry(i+1));
    		byte[] b = new byte[1];
    		file.read(b);
    		if( (char)(b[0])==(char)0 )
    			pqfE.add(i+1);
    		else
    		{
    			String name = getName(i+1);
    			index.put(name, i+1);
    		}
    	}
    	
    	for( int i=0 ; i<totalfat ; i++ )
    	{
    		file.seek(getAddressFromfat(i+1));
    		byte[] b = new byte[3];
    		file.read(b);
    		String s = ""+(char)b[0]+(char)b[1]+(char)b[2];
    		if( basetodec(s)==0 )
    			pqfat.add(i+1);
    	}
    	//Menu
    	String instructions = 
    			"Enter\n'createf <filename>' to create new file\n"
    			 +"'delf <filename>' to delete this file"
    			+"'readf <filename>' to read this whole file"
    			+"'writef append <filename>', press ENTER and start writing whatever you want to append into the file\n"
    			+"'writef overwrite <filename>', press ENTER and start writing whatever you want to write into the file\n"
    			+"'list' to display the list the names of all files in system\n"
    			+"'list -l' to display the detailed list of all files in system\n"
    			+"'list <filename>' to display the detailed list of this file\n"
    			+"'renamef <oldFileName> <newFileName>' to rename the file\n'readf <filename>' to read the whole file\n"
    			;
    	System.out.println(instructions);
    	while( true )
    	{
    		System.out.print(">>>   ");
    		String[] s = br.readLine().split(" ");   // taking Input 
    		if( s[0].equals("createf") )  // option 1  Create 
    		{
    			if( index.containsKey(s[1]) )  // check if given name file exist or not......
    			{
    				System.out.println("File with name \""+s[1]+"\" already exists. Try again with different name...");
    	    		continue;
    			}
    			char p=permi();   // Set the permission at the time of Creation
    			int is = createFile(s[1], p);   //Create file and update the values of Date and time and Fat32 Offsets
    			if( is==1 )      //SuccessFull
    				System.out.println("New File created successfully...");
    			else if( is==-1 )  //Not SuccessFull
    				System.out.println("Error...Sytem was unable to create new File. Try Again...  ");
    		}
    		else if( s[0].equals("delf") )      //Option  2 Delete
    		{
    			if( index.containsKey(s[1]) )     
    			{
    				delete(s[1]);
    				System.out.println("File with name \""+s[1]+"\" deleted successfully!");
    			}
    			else
    			{
    				System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
    			}
    		}
    		else if( s[0].equals("readf") )
    		{
    			if( index.containsKey(s[1]) )
    			{
    				char pp = getDataFromFileEntry(index.get(s[1]), offpermi, 1).charAt(0);
					int[] per = getPer(pp);
					if( per[0]==1 || per[1]==1 )
					{
						int in = index.get(s[1]);
	    				int permi = basetodec(getDataFromFileEntry(in, offpermi, 1));
	    				StringBuilder con = new StringBuilder();
	    				readFile(s[1],con);
	    				System.out.println(con);
					}
					else
					{
						System.out.println("Sorry! This file has no read permissions.");
					}
    			}
    			else
    			{
    				System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
    			}
    		}
    		else if( s[0].equals("writef") )
    		{
    			if( s[1].equals("append") )
    			{
    				if( index.containsKey(s[2]) )
    				{
    					char pp = getDataFromFileEntry(index.get(s[2]), offpermi, 1).charAt(0);
    					int[] per = getPer(pp);
    					if( per[1]==1 )   /* Check permission*/
    					{
    						int address = getAddressFromFileEntry(index.get(s[2]));
    						file.seek(address+offmod);
    				    	file.write(getCurrDateAndTime().getBytes());
    						
    						System.out.println("To end writing in this file, please enter \"*****\" in new line");
                			StringBuilder sb = new StringBuilder();
                			String tem = br.readLine();
                			while( !tem.equals("*****") )
                			{
                				sb.append(tem+"\n");
                				tem = br.readLine();
                			}
                			if( sb.toString().length()==0 ) continue;
                			String sbb = sb.toString().substring(0, sb.length()-1);
                                        /*Fat 32 implementation*/
                			int totalFreeBytes = clustersize*pqfat.size() + clustersize-basetodec(getDataFromFileEntry(index.get(s[2]),offEnd,3));// Check whether file exists?
                			if( totalFreeBytes>=sb.toString().length() )
                			{
                				appendFile(s[2],sbb.getBytes());
                				System.out.println();
                			}
                			else
                				System.out.println("Error! Not Enough free space left in the disk! Delete some data and try again . . .");
    					}
    					else
    					{
    						System.out.println("Sorry! This file has no write permissions.");
    					}
    				}
    				else
    				{
    					System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
    				}
    			}
    			/*else if( s[1].equals("overwrite") )
    			{
    				if( index.containsKey(s[2]) )
    				{
    					char pp = getDataFromFileEntry(index.get(s[2]), offpermi, 1).charAt(0);
    					int[] per = getPer(pp);
    					if( per[1]==1 )
    					{
    						System.out.println("To end writing in this file, please enter \"*****\" in new line");
                			StringBuilder sb = new StringBuilder();
                			String tem = br.readLine();
                			while( !tem.equals("*****") )
                			{
                				sb.append(tem+"\n");
                				tem = br.readLine();
                			}
                			if( sb.toString().length()==0 ) continue;
                			String sbb = sb.toString().substring(0, sb.length()-1);
                			int totalFreeBytes = clustersize*(pqfat.size()+basetodec(getDataFromFileEntry(index.get(s[2]), offsize, 3)));
                			if( totalFreeBytes>=sbb.length() )
                			{
                				char permissions = getDataFromFileEntry(index.get(s[2]),offpermi,1).charAt(0);
                				String created="";
                				created = getDataFromFileEntry(index.get(s[2]),offcreated,12);
                				
                				delete(s[2]);
                				createFile(s[2], permissions);
                				int address = index.get(s[2]);
                				file.seek(address+offcreated);
                				file.write(created.getBytes());
                				appendFile(s[2],sbb.getBytes());
                				System.out.println();
                			}
                			else
                				System.out.println("Error! Not Enough free space left in the disk! Delete some data and try again . . .");
    					}
    					else
    					{
    						System.out.println("Sorry! This file has no write permissions.");
    					}
    				}
    				else
    				{
    					System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
    				}
    			}*/
    			else
    			{
    				System.out.println("Sorry! Incorrect input parameters. Try Again . . .");
    			}
    		}
    	
    		else if( s[0].equals("renamef") )
    		{
    			if( s[1].length()<=10 )
    			{
    				if( index.containsKey(s[1]) )
        			{
        				rename(s[1],s[2]);
        			}
        			else
        			{
        				System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
        			}
    			}
    			else
    			{
    				System.out.println("Name of file should be less than or equal to 10 characters");
    			}
    		}
    		else if( s[0].equals("list") )
    		{
    			if( s.length>1 )
    			{
    				String header="Name Of File        File created Date/Time        Last Modified Date/Time        Size        Permissions";
        			System.out.println(header);
        			System.out.println();
    				if( s[1].equals("-l") )
    				{
    					displayfileList(0,null);
    				}
    				else
    				{
    					if( index.containsKey(s[1]) )
    						displayfileList(2,s[1]);
    					else
    						System.out.println("Sorry! File with this name doesn't exist! Try Again . . .");
    				}
    			}
    			else
    			{
    				displayfileList(1,null);
    			}
    		}
    		else
    		{
    			System.out.println("Invalid command");
    		}
    	}
    }
	
	static int[] getPer( char c )
	{
		int p = (int)c-'0';
		String permissions = new String(); 
    	while( p!=0 )
    	{
    		permissions+=(p%2);
    		p/=2;
    	}
    	for( int i=0 ; permissions.length()<3 ; i++ ) permissions+=0;
    	return new int[]{(int)permissions.charAt(2)-'0',(int)permissions.charAt(1)-'0',(int)permissions.charAt(0)-'0'};
	}
	
	static void displayfileList(int flag, String name ) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		for( int i=1 ; i<=totalFileEntry ; i++ )
    	{
			int address=getAddressFromFileEntry(i);
    		file.seek(address);
    		byte[] b = new byte[1];
    		file.read(b);
    		if( (char)(b[0])!=(char)0 )
    		{
    			String interpretation = interprete(address,i);
    			if( flag==0 )
    			{
    				sb.append(interpretation+"\n");
    			}
    			else if( flag==1 )
    			{
    				String[] sp = interpretation.split("        ");
    				sb.append(sp[0]+"\n");
    			}
    			else
    			{
    				String[] sp = interpretation.split("        ");
    				if( sp[0].equals(name) )
    				{
    					sb.append(interpretation+"\n"); break;
    				}
    			}
    		}
    	}
		System.out.println(sb);
	}
	
	static String interprete( int address, int ind ) throws Exception
	{
		byte[] b = new byte[sizeOfFileEntry];
		file.seek(address);
		file.read(b);
		String a = byteToString(b);
		StringBuilder name=new StringBuilder();
    	for( int i=0 ; i<10 ; i++ ) 
    	{
    		if( a.charAt(i)==(char)0 ) break;
    		name.append(a.charAt(i));
    	}
    	String Name = name.toString(); 
    	while( Name.length()<20 ) Name+=" ";
    	String created = getFormatted(a.substring(10,22));
    	while( created.length()<30 ) created+=" ";
    	String modified = getFormatted(a.substring(22,34));
    	while( modified.length()<31 ) modified+=" ";
    	String size = String.valueOf(128*basetodec(getDataFromFileEntry(ind, offsize, 3)));
    	while( size.length()<12 ) size+=" ";
    	int p = (int)getDataFromFileEntry(ind, offpermi, 1).charAt(0)-'0';
    	StringBuilder permissions = new StringBuilder(); 
    	while( p!=0 )
    	{
    		permissions.append(p%2);
    		p/=2;
    	}
    	for( int i=0 ; permissions.length()<3 ; i++ ) permissions.append('0');
    	return Name+created+modified+size+permissions.reverse();
	}
	
	static String getFormatted( String date1 )
	{
    	String res="";
    	String[] month={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    	String mon = month[Integer.parseInt(date1.substring(2, 4))];
    	res+=mon+" ";
    	res+=date1.substring(0,2)+" "+date1.substring(4,8)+" ";
    	res+=date1.substring(8,10)+":"+date1.substring(10,12);
    	return res;
	}
	
	
	
	
	
	
	
	static void setSizeOfFile( int ind, int size, String op ) throws Exception
	{
		int address = getAddressFromFileEntry(ind);
		if( op.equals("add") )
		{
			int oldsize = basetodec(getDataFromFileEntry(ind, offsize, 3));
			file.seek(address+offsize);
			file.write(regulate(decTo128(oldsize+size), 3).getBytes());
		}
		else
		{
			file.write(regulate(decTo128(size), 3).getBytes());
		}
	}
	
	static void setFATEntry( int curr, int next ) throws Exception
	{
		int addressFAT = getAddressFromfat(curr);
		file.seek(addressFAT);
		file.write(regulate(decTo128(next),3).getBytes());
		return;
	}
	
	static void setEndOfFile( int ind, int endOfFile ) throws Exception
	{
		int address = getAddressFromFileEntry(ind);
		file.seek(address+offEnd);
		file.write(regulate(decTo128(endOfFile), 1).getBytes());
		return;
	}
	
	static void readFile( String name, StringBuilder con ) throws Exception
	{
		int ind = index.get(name);
		int firstCluster = basetodec(getDataFromFileEntry(ind,offfirstcluster,3));
		int endOfFile = basetodec(getDataFromFileEntry(ind, offEnd, 1));
		read(firstCluster, con, endOfFile );
	}
	
	static int getDataAddress( int clusterNo )
	{
		return diskDataStartingPoint+(clusterNo-1)*clustersize;
	}
	
	
	
	static int getNextCluster( int n ) throws Exception
	{
		int address1 = getAddressFromfat(n);
		byte[] b = new byte[3];
		file.seek(address1);
		file.read(b);
		String a = byteToString(b);
		return basetodec(a);
	}
	
   
    
    static String byteToString( byte[] b )
	{
		String s="";
		for( int i=0 ; i<b.length ; i++ )
			s+=(char)b[i];
		return s;
	}
    
    static int getAddressFromFileEntry( int i )
    {
    	return sizeOfFileEntry*(i-1);
    }
    
    static int getAddressFromfat( int i )
    {
    	return offFAT+sizeOfFATentry*(i-1);
    }
    
    static String decTo128( int n )
    {
    	StringBuilder sb = new StringBuilder();
    	while( n>0 )
    	{
    		int rem = n%128;
    		sb.append((char)rem);
    		n/=128;
    	}
    	return sb.reverse().toString();
    }
    
    static int basetodec( String s128 )
    {
    	int p=1;int res=0;
    	for( int i=s128.length()-1 ; i>=0 ; i-- )
    	{
    		res += p*(int)(s128.charAt(i));
    		p*=128;
    	}
    	return res;
    }
    
    static String regulate( String s, int n )
    {
    	String res="";
    	for( int i=0 ; i+s.length()<n ; i++ )
    		res+=(char)0;
    	res+=s;
    	return res;
    }
    
    

   
}