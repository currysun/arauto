package com.lombardrisk.utils.fileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.read.biff.BiffException;

public class FileServiceHelper
{
	private static FileServiceHelper fileServiceHelper;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private FileServiceHelper()
	{

	}

	public static FileServiceHelper getInstance()
	{
		if (fileServiceHelper == null)
		{
			fileServiceHelper = new FileServiceHelper();
		}

		return fileServiceHelper;
	}

	public Map<Integer, Map<Integer, FileContent>> parseFile(String fileName, InputStream is) throws IOException
	{
		IFileService fileService;
		fileService = getFileService(fileName);
		if (fileService != null)
		{
			try
			{
				return fileService.parseFile(is);
			}
			catch (BiffException e)
			{
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
			finally
			{
				if (is != null)
				{
					is.close();
				}
			}
		}

		return null;
	}

	private IFileService getFileService(String fileName)
	{
		if (fileName.endsWith(".xls"))
		{
			return new ExcelFileService();
		}
		else if (fileName.endsWith(".csv"))
		{
			return new CSVFileService();
		}

		return null;
	}
}
