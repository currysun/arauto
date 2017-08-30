package com.lombardrisk.utils.fileService;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Leo Tu on 6/17/2015.
 */

public class CsvUtil
{
	private final static Logger logger = LoggerFactory.getLogger(CsvUtil.class);
	private final List<String> list = new ArrayList<String>();
	private BufferedReader bufferedreader = null;

	public CsvUtil()
	{

	}

	public CsvUtil(final String filename) throws IOException
	{
		bufferedreader = new BufferedReader(new FileReader(filename));
		String stemp;
		while ((stemp = bufferedreader.readLine()) != null)
		{
			list.add(stemp);
		}
	}

	public static String getCellValueFromCSV(File csvFile, String cellId, String instance, String rowKey) throws IOException
	{

		String inString, cellValue = null;

		BufferedReader reader = null;
		FileReader fReader = null;

		try
		{
			fReader = new FileReader(csvFile);
			reader = new BufferedReader(fReader);

			while ((inString = reader.readLine()) != null)
			{
				String[] data = inString.replace("\"", "").split(",");
				if (data[0].equals(cellId))
				{
					if (data[2].equals(instance))
					{
						if (rowKey == null || rowKey.equals(""))
						{
							cellValue = data[3];
							break;
						}
						else
						{
							try
							{
								if (data[5].equals(rowKey))
								{
									cellValue = data[3];
									break;
								}
							}
							catch (Exception e)
							{
								continue;
							}
						}
					}
				}
			}
		}
		catch (FileNotFoundException e)
		{
			// e.printStackTrace();
		}
		catch (IOException e)
		{
			// e.printStackTrace();
		}
		finally
		{
			if (reader != null)
				reader.close();
			if (fReader != null)
				fReader.close();
		}

		return cellValue;
	}

	public static List<String> readFile(File file) throws IOException
	{
		BufferedReader br = null;
		FileReader fReader = null;
		List<String> list = new ArrayList<String>();
		try
		{
			fReader = new FileReader(file);
			br = new BufferedReader(fReader);
			String str;
			while ((str = br.readLine()) != null)
			{
				/*
				 * if (str.contains("/") && str.contains(" 0:00")) { for (String
				 * column : str.split(",")) { if ((column.contains("/")) &&
				 * column.contains(" 0:00")) { String init = column; for (String
				 * part : column.split("/")) { if (part.length() == 1) column =
				 * column.replace(part, "0" + part); } column =
				 * column.replace(" 0:00", " 00:00:00"); str = str.replace(init,
				 * column); } }
				 * 
				 * }
				 */
				if (str.length() > 0)
				{
					list.add(str);
				}
			}
			br.close();
		}
		catch (IOException e)
		{
			// e.printStackTrace();
		}
		finally
		{
			if (br != null)
				br.close();
			if (fReader != null)
				fReader.close();
		}
		return list;
	}

	public static boolean compareCSV(File file1, File file2) throws IOException
	{
		boolean rst = true;
		List<String> fileCon1 = readFile(file1);
		List<String> fileCon2 = readFile(file2);
		if (fileCon1.size() != fileCon2.size())
		{
			logger.error("Records amount is different");
			rst = false;
		}
		else
		{
			for (int i = 0; i < fileCon1.size(); i++)
			{
				if (!fileCon1.contains(fileCon2.get(i)))
				{
					rst = false;
					break;
				}
			}
		}
		return rst;
	}

	public static void writeFile(File file, List<String> list) throws IOException
	{
		BufferedWriter bw = null;
		FileWriter fwr = null;
		try
		{

			String line = System.getProperty("line.separator");
			fwr = new FileWriter(file);
			bw = new BufferedWriter(fwr);
			for (String str : list)
			{
				bw.write(str);
				bw.write(line);
			}
			bw.close();
		}
		catch (IOException e)
		{
			// e.printStackTrace();
		}
		finally
		{
			if (bw != null)
				bw.close();
			if (fwr != null)
				fwr.close();
		}
	}

	private static void sort(List<String> list, final int column)
	{
		try
		{
			Collections.sort(list, new Comparator<String>() {
				public int compare(String str1, String str2)
				{
					String[] str1s = str1.split(",");
					String[] str2s = str2.split(",");
					return str1s[column - 1].compareTo(str2s[column - 1]);
				}
			});
		}
		catch (Exception e)
		{

		}
	}

	public static void sortCsv(String sourceFile, String destFile, int colID) throws IOException
	{
		logger.info("Begin sort csv file[" + sourceFile + "] by column " + colID);
		File file1 = new File(sourceFile);
		File file2 = new File(destFile);
		List<String> list = readFile(file1);
		String head = list.get(0);
		int tmp = list.get(0).split(",").length;
		int col = colID;
		if (col < 1 || col > tmp)
		{
			list.clear();
			System.exit(4);
		}
		sort(list, col);
		int key = list.indexOf(head);
		list.remove(key);
		list.add(0, head);
		writeFile(file2, list);

	}

	public static void writeToSpecCell(String csvFile, int col, String text) throws IOException
	{
		File file = new File(csvFile);
		CsvUtil sourceCsv = new CsvUtil(csvFile);
		int rowAmt = sourceCsv.getRowNum();
		int rowID = 0;

		for (int i = 1; i < rowAmt; i++)
		{

			List<String> rowValue = new ArrayList<String>();
			StringBuilder replaceedTxt = new StringBuilder();
			String value = sourceCsv.getRow(i);

			if (value.endsWith("\""))
			{
				value = value.replace("\"", "");

				value = value + ",";
			}
			else if (!value.endsWith(","))
				value = value + ",";

			for (String txt : value.split(","))
			{
				rowValue.add(txt);
			}

			if (rowValue.size() < col)
				rowValue.add(" ");
			if (rowValue.get(col - 1).equals(" "))
			{
				rowValue.remove(col - 1);
				rowValue.add(col - 1, text);
				rowID = i;
				for (int index = 0; index < rowValue.size(); index++)
				{
					if (!rowValue.get(index).equals(" "))
					{
						replaceedTxt.append(rowValue.get(index));
					}
					replaceedTxt.append(",");

				}
				replaceedTxt = replaceedTxt.deleteCharAt(replaceedTxt.length() - 1);
				List<String> list = readFile(file);
				list.remove(rowID);
				list.add(rowID, replaceedTxt.toString());
				CsvUtil.writeFile(file, list);
			}
		}

	}

	public final static void CsvToExcel(String csv, String excel) throws IOException
	{
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("Sheet1");

		BufferedReader r = null;
		FileReader fReader = null;
		try
		{
			fReader = new FileReader(csv);
			r = new BufferedReader(fReader);
			int i = 0;
			while (true)
			{
				String ln = r.readLine();
				if (ln == null)
					break;

				HSSFRow row = sheet.createRow((short) i++);
				int j = 0;
				for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens();)
				{
					String val = it.nextToken();
					HSSFCell cell = row.createCell((short) j++);
					cell.setCellValue(val);
				}
			}
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		finally
		{
			if (r != null)
				r.close();
			if (wb != null)
				wb.close();
			if (fReader != null)
				fReader.close();
		}

		FileOutputStream fileOut = null;

		try
		{
			fileOut = new FileOutputStream(excel);
			wb.write(fileOut);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		finally
		{
			if (fileOut != null)
				fileOut.close();
		}
	}

	public static boolean compareCSVFiles(String filePath1, String filePath2) throws Exception
	{
		boolean rst = false;
		try
		{
			// 相当于key，比如第一列和第二列作为关键值，搜索记录
			List<Integer> keyCols = new ArrayList<Integer>();
			keyCols.add(0);
			keyCols.add(1);

			// 作为值比对，当找到相同key的记录，比对值是否不一样
			List<Integer> valCols = new ArrayList<Integer>();
			valCols.add(2);
			FileInputStream fis1 = new FileInputStream(new File(filePath1));
			FileInputStream fis2 = new FileInputStream(new File(filePath2));

			// key为行号，Map<Integer, FileContent>为每行的内容以列为key
			Map<Integer, Map<Integer, FileContent>> contents1 = FileServiceHelper.getInstance().parseFile(filePath1, fis1);
			Map<Integer, Map<Integer, FileContent>> contents2 = FileServiceHelper.getInstance().parseFile(filePath2, fis2);
			Map<Integer, Map<Integer, FileContent>> results;
			ExcelCompareService excelCompareService = new ExcelCompareService(contents1, contents2, keyCols, valCols);
			// 开始比对
			excelCompareService.startCompare();
			// 获取差异
			results = excelCompareService.getResults();

			if (results == null || results.size() == 0)
			{
				logger.info("Two files are same");
				rst = true;
			}
			else
			{
				Set<Integer> keys1 = results.keySet();
				Map<Integer, FileContent> rowContent;
				Set<Integer> keys2;

				for (Integer key1 : keys1)
				{
					rowContent = results.get(key1);
					if (rowContent == null)
					{
						continue;
					}

					System.out.print("Row :" + key1 + "\t");
					StringBuilder b = null;
					keys2 = rowContent.keySet();
					for (Integer key2 : keys2)
					{
						if (rowContent.get(key2) == null)
						{
							continue;
						}
						b = new StringBuilder();
						b.append(rowContent.get(key2).getContent() + "\t");
					}
					logger.error(b.toString());
				}
			}

		}
		catch (FileNotFoundException e)
		{
			logger.error("FileNotFoundException", e);
		}
		catch (IOException e)
		{
			logger.error("IOException", e);
		}
		return rst;
	}

	private List<String> getList() throws IOException
	{

		return list;
	}

	private int getRowNum()
	{

		return list.size();
	}

	private int getColNum()
	{
		if (!list.toString().equals("[]"))
		{

			if (list.get(0).toString().contains(","))
			{
				return list.get(0).toString().split(",").length;
			}
			else if (list.get(0).toString().trim().length() != 0)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return 0;
		}
	}

	private String getRow(final int index)
	{

		String rowValue = null;
		if (this.list.size() != 0)
		{
			rowValue = (String) list.get(index);
		}

		return rowValue;

	}

	private String getCol(final int index)
	{

		if (this.getColNum() == 0)
		{
			return null;
		}

		StringBuffer scol = new StringBuffer();
		String temp = null;
		final int colnum = this.getColNum();

		if (colnum > 1)
		{
			for (final Iterator<String> it = list.iterator(); it.hasNext();)
			{
				temp = it.next().toString();
				scol = scol.append(temp.split(",")[index] + ",");
			}
		}
		else
		{
			for (final Iterator<String> it = list.iterator(); it.hasNext();)
			{
				temp = it.next().toString();
				scol = scol.append(temp + ",");
			}
		}
		String str = new String(scol.toString());
		str = str.substring(0, str.length() - 1);
		return str;
	}

	private String getString(final int row, final int col)
	{

		String temp = null;
		final int colnum = this.getColNum();
		if (colnum > 1)
		{
			temp = list.get(row).toString().split(",")[col];
		}
		else if (colnum == 1)
		{
			temp = list.get(row).toString();
		}
		else
		{
			temp = null;
		}
		return temp;
	}

	private void CsvClose() throws IOException
	{
		this.bufferedreader.close();
	}

}
