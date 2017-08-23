package com.wlwl.cube.analysisForGB.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class PropertyResource
{
	private static final PropertyResource instance = new PropertyResource();

	private  HashMap<String, String> properties = new HashMap<String, String>();


	public static PropertyResource getInstance()
	{
		return instance;
	}


	public HashMap<String, String> getProperties()
	{
		return properties;
	}
	
	public void reLoadProperty()
	{
		Properties prop = new Properties();
		String path;
		try
		{
			path = new File( "." ).getCanonicalPath() + "/resource/cfg.properties";
			InputStream in = new BufferedInputStream( new FileInputStream( path ) );
			prop.load( in );
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext())
			{
				String key = it.next();
				properties.put( key, prop.getProperty( key ) );
			}
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	public PropertyResource()
	{
		reLoadProperty();
	}


	public static void main(String[] args)
	{
		PropertyResource.getInstance();
	}
}
