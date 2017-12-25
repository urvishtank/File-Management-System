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
public class Accesss_Methods {
    
    static char permi() throws IOException
	{
		char temp;
		String p="";
		System.out.println("Read Only Permissions? y/n \t");
		temp=br.readLine().charAt(0);
		while( temp!='y' && temp!='n' )
		{
			System.out.println("Try Again...");
			System.out.println("Read Permissions? y/n \t");
			temp=br.readLine().charAt(0);
		}
		if( temp=='y' ) p+='1';
		else p+='0';
			
		System.out.println("Read/Write Permissions? y/n \t");
		temp=br.readLine().charAt(0);
		while( temp!='y' && temp!='n' )
		{
			System.out.println("Try Again...");
			System.out.println("Read/Write Permissions? y/n \t");
			temp=br.readLine().charAt(0);
		}
		if( temp=='y' ) p+='1';
		else p+='0';
		
		System.out.println("Execute Permissions? y/n\t");
		temp=br.readLine().charAt(0);
		while( temp!='y' && temp!='n' )
		{
			System.out.println("Try Again...");
			System.out.println("Execute Permissions? y/n\t");
			temp=br.readLine().charAt(0);
		}
		if( temp=='y' ) p+='1';
		else p+='0';
		
		int p1 = 4*(p.charAt(0)-'0')+2*(p.charAt(1)-'0')+(p.charAt(2)-'0');
		return (char)(p1+'0');
	}
    
   
    
}
