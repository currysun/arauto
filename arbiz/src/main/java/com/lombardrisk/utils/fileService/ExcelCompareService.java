package com.lombardrisk.utils.fileService;

import java.util.*;

public class ExcelCompareService
{
	private List<Integer> keyCols;
	private List<Integer> valCols;
	private Map<Integer, Map<Integer, FileContent>> contents1;
	private Map<Integer, Map<Integer, FileContent>> contents2;
	private Map<Integer, Map<Integer, FileContent>> results;

	public ExcelCompareService(Map<Integer, Map<Integer, FileContent>> contents1, Map<Integer, Map<Integer, FileContent>> contents2, List<Integer> keyCols, List<Integer> valCols)
	{
		this.keyCols = keyCols;
		this.valCols = valCols;
		this.contents1 = contents1;
		this.contents2 = contents2;
	}

	public void startCompare()
	{
		results = new HashMap<Integer, Map<Integer, FileContent>>();
		Map<ExcelCompareObject, ExcelCompareObject> execelComObjMaps1 = changeContent(contents1, keyCols, valCols);
		Map<ExcelCompareObject, ExcelCompareObject> execelComObjMaps2 = changeContent(contents2, keyCols, valCols);

		if (execelComObjMaps1 == null || execelComObjMaps2 == null)
		{
			return;
		}

		// excel1与excel2中不同的记录
		Set<ExcelCompareObject> keyComObjMaps1 = execelComObjMaps1.keySet();
		ExcelCompareObject tmp1;
		ExcelCompareObject tmp2;
		for (ExcelCompareObject keyComObjMap1 : keyComObjMaps1)
		{
			if (keyComObjMap1 == null)
			{
				continue;
			}
			tmp1 = execelComObjMaps1.get(keyComObjMap1);
			if (tmp1 == null)
			{
				continue;
			}
			tmp2 = execelComObjMaps2.get(keyComObjMap1);
			if (tmp2 == null)
			{
				putContentsToReuslt(keyComObjMap1.getContents());
				putContentsToReuslt(tmp1.getContents());
				continue;
			}

			if (!tmp1.equals(tmp2))
			{
				putContentsToReuslt(keyComObjMap1.getContents());
				putContentsToReuslt(tmp1.getContents());
				continue;
			}
		}

		// 找出excel2中有的，但是excel1没有的
		Set<ExcelCompareObject> keyComObjMaps2 = execelComObjMaps2.keySet();

		for (ExcelCompareObject keyComObjMap2 : keyComObjMaps2)
		{
			if (keyComObjMap2 == null)
			{
				continue;
			}
			tmp2 = execelComObjMaps2.get(keyComObjMap2);
			if (tmp2 == null)
			{
				continue;
			}
			tmp1 = execelComObjMaps1.get(keyComObjMap2);
			if (tmp1 == null)
			{
				putContentsToReuslt(keyComObjMap2.getContents());
				putContentsToReuslt(tmp2.getContents());
				continue;
			}

		}
	}

	public Map<Integer, Map<Integer, FileContent>> getResults()
	{
		return results;
	}

	private void putContentsToReuslt(List<FileContent> fileContents)
	{
		if (fileContents == null || fileContents.size() == 0)
		{
			return;
		}

		Integer row = null;
		Integer col;
		Map<Integer, FileContent> rowResult = null;
		for (FileContent fileContent : fileContents)
		{
			if (fileContent == null)
			{
				continue;
			}
			row = fileContent.getRow();
			rowResult = results.get(row);
			if (rowResult == null)
			{
				rowResult = new HashMap<Integer, FileContent>();
				results.put(row, rowResult);
			}
			col = fileContent.getColumn();
			rowResult.put(col, fileContent);
		}
	}

	private Map<ExcelCompareObject, ExcelCompareObject> changeContent(Map<Integer, Map<Integer, FileContent>> contents, List<Integer> keyCols, List<Integer> valCols)
	{
		if (contents == null || keyCols == null || valCols == null)
		{
			return null;
		}
		Map<ExcelCompareObject, ExcelCompareObject> map = new HashMap<ExcelCompareObject, ExcelCompareObject>();
		ExcelCompareObject keyObj;
		ExcelCompareObject valObj;
		Collection<Map<Integer, FileContent>> valContents = contents.values();
		for (Map<Integer, FileContent> valContent : valContents)
		{
			List<FileContent> keys = getFileContents(valContent, keyCols);
			List<FileContent> values = getFileContents(valContent, valCols);
			keyObj = new ExcelCompareObject();
			keyObj.setContents(keys);
			valObj = new ExcelCompareObject();
			valObj.setContents(values);
			map.put(keyObj, valObj);
		}

		return map;
	}

	private List<FileContent> getFileContents(Map<Integer, FileContent> content, List<Integer> cols)
	{
		if (content == null || cols == null)
		{
			return null;
		}

		List<FileContent> results = new ArrayList<FileContent>();
		for (Integer col : cols)
		{
			FileContent fContent = content.get(col);
			results.add(fContent);
		}

		return results;
	}

	public List<Integer> getKeyCols()
	{
		return keyCols;
	}

	public void setKeyCols(List<Integer> keyCols)
	{
		this.keyCols = keyCols;
	}

	public List<Integer> getValCols()
	{
		return valCols;
	}

	public void setValCols(List<Integer> valCols)
	{
		this.valCols = valCols;
	}

}
