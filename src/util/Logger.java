package util;

import java.io.*;
import java.util.LinkedList;


public class Logger 
{
	private File f;
	private LinkedList<String> dataList;
	
	public Logger(String filename)
	{	    
		f = new File(filename);
	    dataList = new LinkedList<String>();
	}
	
	private void MakeFile()
	{		
	    try 
	    {
	    	if (f.exists())
	    	{
	    		f.renameTo(new File("Copy - "+f.getName()));
	    		System.out.println("File " + f.getName() + " already exists. Renaming...");
	    	}
			f.createNewFile();
			
		} 
	    catch (IOException e) 
	    {
			e.printStackTrace();
			System.out.println("Cannot create file " + f.getName() + ".");
		}
	}
	
	public void Log(double x, double y)
	{
		Integer x_int = Integer.valueOf((int)x);
		Integer y_int = Integer.valueOf((int)y);
		dataList.add(x_int + "|" + y_int);   
	}
	
	public void Log(String s)
	{
		dataList.add(s);
	}
	

	public boolean Commit() 
	{
		MakeFile();
		
		FileWriter fw = null;
		try 
		{
			fw = new FileWriter(f,true);
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
			System.out.println("Cannot open file " + f.getName() + ".");
			return false;
		}
		
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw);
		
		while(!dataList.isEmpty())
		{
			String s = dataList.pollFirst();			
			out.println(s);
		}		
		out.flush();
		out.close();
		return true;
	}

}
