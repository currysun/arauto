package com.lombardrisk.utils.fileService;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.csvreader.CsvReader;

import jxl.read.biff.BiffException;

public class CSVFileService implements IFileService
{

	@Override
	public Map<Integer, Map<Integer, FileContent>> parseFile(InputStream is) throws BiffException, IOException
	{
		// TODO Auto-generated method stub
		if (is == null)
		{
			return null;
		}
		Map<Integer, Map<Integer, FileContent>> contents = new HashMap<Integer, Map<Integer, FileContent>>();
		;
		DataInputStream in = new DataInputStream(is);

		CsvReader csvReader = new CsvReader(new InputStreamReader(in, "UTF-8"), ',');
		int rowIndex = 0;
		FileContent fileContent = null;
		if (csvReader != null)
		{
			while (csvReader.readRecord())
			{
				int columnCount = csvReader.getColumnCount();
				Map<Integer, FileContent> rowValues = new HashMap<Integer, FileContent>();
				for (int i = 0; i < columnCount; i++)
				{
					String columnValue = csvReader.get(i);
					if (columnValue == null || columnValue.trim().isEmpty())
					{
						continue;
					}
					fileContent = new FileContent(rowIndex, i, columnValue.trim());
					rowValues.put(i, fileContent);

				}
				if (rowValues.size() > 0)
				{
					contents.put(rowIndex++, rowValues);
				}
			}
		}
		return contents;
	}

}
