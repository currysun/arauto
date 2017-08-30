package com.lombardrisk.utils.fileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelFileService implements IFileService
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

		Workbook workbook = Workbook.getWorkbook(is);
		// 获取第一个sheet
		Sheet sheet = workbook.getSheet(0);
		if (sheet == null)
		{
			return null;
		}
		int rows = sheet.getRows();
		FileContent fileContent;
		for (int i = 0; i < rows; i++)
		{
			int cols = sheet.getColumns();
			Map<Integer, FileContent> rowContents = new HashMap<Integer, FileContent>();
			for (int j = 0; j < cols; j++)
			{
				Cell cell = sheet.getCell(j, i);
				String content = cell.getContents();
				if (content == null || content.trim().isEmpty())
				{
					continue;
				}
				fileContent = new FileContent(i, j, content.trim());
				rowContents.put(j, fileContent);
			}
			if (rowContents.size() > 0)
			{
				contents.put(i, rowContents);
			}
		}
		return contents;
	}

}
