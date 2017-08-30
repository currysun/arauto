package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.FormInstanceRetrievePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.Business;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.CsvUtil;
import com.lombardrisk.utils.fileService.ExcelUtil;

public class Precision extends TestTemplate
{
	static String testdataSource = null;
	static String testdataDest = null;
	static File testRstFile = null;
	static String importPath = null;

	protected void testCellValue_DBValue(int ID, String Regulator, String Group, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);
			ListPage listPage = super.m.listPage;

			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			for (int i = 1; i <= amt; i++)
			{
				boolean s1 = true;
				boolean s2 = true;

				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String editValue = (expectedValueValueList.get(3).trim().equals("")) ? null : expectedValueValueList.get(3).trim();
				String expectedValueRP = expectedValueValueList.get(4).trim();
				String expectedValueDB = expectedValueValueList.get(6).trim();

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ")=" + expectedValueRP);
				String extendCell = null;

				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				else
				{
					rowID = "0";
				}
				if (editValue != null)
				{
					logger.info("Begin edit cell to " + editValue);
					formInstancePage.editCellValue(instance, cellName, extendCell, editValue);
				}

				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Cannot find cell");
				}

				String DBValue = "";
				if (!expectedValueDB.equals(""))
				{
					logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ") in DB=" + expectedValueRP);
					boolean isExtendCell = false;
					if (extendCell != null)
						isExtendCell = true;
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ProcessDate, Group, instance, cellName, isExtendCell, Integer.parseInt(rowID));
					if (DBValue != null)
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 7, DBValue);
					}
					else
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 7, "Cannot find cell");
					}
				}
				if (!expectedValueRP.equals(accValue))
					s1 = false;
				try
				{
					if (Double.parseDouble(expectedValueDB) - Double.parseDouble(DBValue) != 0)
						s2 = false;
				}
				catch (Exception e)
				{
					if (!expectedValueDB.equals(DBValue))
						s2 = false;
				}
				if (!s1 || !s2)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Fail");
					testRst = false;
				}
				else
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Pass");
			}
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	protected void testCellValue_ExcelValue(int ID, String Regulator, String Group, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String editValue = (expectedValueValueList.get(3).trim().equals("")) ? null : expectedValueValueList.get(3).trim();
				String extendCell = null;
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				if (editValue != null)
				{
					logger.info("Begin edit cell to " + editValue);
					formInstancePage.editCellValue(instance, cellName, extendCell, editValue);
				}
			}
			String exportedFile = formInstancePage.exportToFile(Group, Form, ProcessDate, "excel", null, null);
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueRP = expectedValueValueList.get(4).trim();
				String expectedValueExcel = expectedValueValueList.get(6).trim();

				String initRowId = rowID;
				String extendCell = null;
				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ")=" + expectedValueRP);
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Cannot find cell");
				}

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ") in excel=" + expectedValueExcel);
				String value_excel = ExcelUtil.getCellValueByCellName(new File(exportedFile), cellName, instance, initRowId);
				ExcelUtil.writeToExcel(cellValueFile, i, 7, value_excel);

				if (!expectedValueRP.equals(accValue) || !expectedValueExcel.equals(value_excel))
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Fail");
					testRst = false;
				}
				else
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Pass");

			}
		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			// closeFormInstance();
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	protected void testCellValue_CSVValue(int ID, String Regulator, String Group, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
			testRst = true;
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String editValue = (expectedValueValueList.get(3).trim().equals("")) ? null : expectedValueValueList.get(3).trim();
				String extendCell = null;
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				if (editValue != null)
				{
					logger.info("Begin edit cell to " + editValue);
					formInstancePage.editCellValue(instance, cellName, extendCell, editValue);
				}

			}
			String exportedFile = formInstancePage.exportToFile(null, null, null, "csv", null, null);
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueRP = expectedValueValueList.get(4).trim();
				String expectedValueCSV = expectedValueValueList.get(6).trim();

				String initRowId = rowID;
				String extendCell = null;
				logger.info("Verify if " + cellName + "(instance=" + instance + " initRowId=" + rowID + ")=" + expectedValueRP);
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Cannot find cell");
				}

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ") in csv=" + expectedValueRP);
				String value_csv = CsvUtil.getCellValueFromCSV(new File(exportedFile), cellName, instance, initRowId);
				ExcelUtil.writeToExcel(cellValueFile, i, 7, value_csv);

				try
				{
					if (!expectedValueRP.endsWith(accValue) || Double.parseDouble(expectedValueCSV) - Double.parseDouble(value_csv) != 0)
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 8, "Fail");
						testRst = false;
					}
					else
						ExcelUtil.writeToExcel(cellValueFile, i, 8, "Pass");
				}
				catch (Exception e)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Fail");
					testRst = false;
				}

			}
		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			// closeFormInstance();
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	protected void testImportedCellValue_DBValue(int ID, String Regulator, String Group, String Form, String ProcessDate, File importFile, File cellValueFile, File testDataFile, String caseID)
			throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
			formInstancePage.importAdjustment(importFile, false, false);
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			for (int i = 1; i <= amt; i++)
			{
				boolean s1 = true;
				boolean s2 = true;

				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueRP = expectedValueValueList.get(4).trim();
				String expectedValueDB = expectedValueValueList.get(6).trim();

				String extendCell = null;
				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ")=" + expectedValueRP);
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				else
				{
					rowID = "0";
				}
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Cannot find cell");
				}

				String DBValue = "";
				if (!expectedValueDB.equals(""))
				{
					logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ") in DB=" + expectedValueRP);
					boolean isExtendCell = false;
					if (extendCell != null)
						isExtendCell = true;
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ProcessDate, Group, instance, cellName, isExtendCell, Integer.parseInt(rowID));
					if (DBValue != null)
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 7, DBValue);
					}
					else
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 7, "Cannot find cell");
					}
				}
				if (!expectedValueRP.equals(accValue))
					s1 = false;
				try
				{
					if (Double.parseDouble(expectedValueDB) - Double.parseDouble(DBValue) != 0)
						s2 = false;
				}
				catch (Exception e)
				{
					if (!expectedValueDB.equals(DBValue))
						s2 = false;
				}
				if (!s1 || !s2)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Fail");
					testRst = false;
				}
				else
					ExcelUtil.writeToExcel(cellValueFile, i, 8, "Pass");
			}
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	protected void testExportExcel_Scaled(int ID, String Regulator, String Group, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = m.formInstancePage;
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			String exportedFile = formInstancePage.exportToFile(Group, Form, ProcessDate, "excel(Scale)", null, null);
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValue = expectedValueValueList.get(3).trim();

				String initRowId = rowID;
				String extendCell = null;
				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ")=" + expectedValue);
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 4, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 4, "Cannot find cell");
				}

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ") in excel=" + expectedValue);
				String value_excel = ExcelUtil.getCellValueByCellName(new File(exportedFile), cellName, instance, initRowId);
				ExcelUtil.writeToExcel(cellValueFile, i, 4, value_excel);

				if (!expectedValue.equals(value_excel))
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Fail");
					testRst = false;
				}
				else
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Pass");

			}
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	protected void testExportExcel_Noscale(int ID, String Regulator, String Group, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = m.formInstancePage;
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			String exportedFile = formInstancePage.exportToFile(Group, Form, ProcessDate, "excel", null, null);
			for (int i = 1; i <= amt; i++)
			{
				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValue = expectedValueValueList.get(3).trim();

				String initRowId = rowID;
				String extendCell = null;
				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ")=" + expectedValue);
				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 4, accValue);
				}
				else
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 4, "Cannot find cell");
				}

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + initRowId + ") in excel=" + expectedValue);
				String value_excel = ExcelUtil.getCellValueByCellName(new File(exportedFile), cellName, instance, initRowId);
				ExcelUtil.writeToExcel(cellValueFile, i, 4, value_excel);

				if (!expectedValue.equals(value_excel))
				{
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Fail");
					testRst = false;
				}
				else
					ExcelUtil.writeToExcel(cellValueFile, i, 5, "Pass");

			}
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(testDataFile, ID, 13, caseID, testRst, "Precision");
		}
	}

	@Parameters(
	{ "fileName" })
	@Test
	public void testPrecision(@Optional String fileName) throws Exception
	{
		logger.info("File is: " + fileName);
		if (fileName == null || fileName.equals(""))
			fileName = "Precision.xls";
		testdataSource = createFolderAndCopyFile("Precision", fileName).get(0);
		testdataDest = createFolderAndCopyFile("Precision", fileName).get(1);
		testRstFile = new File(createFolderAndCopyFile("Precision", fileName).get(2));
		importPath = createFolderAndCopyFile("Precision", fileName).get(3);

		if (!testRstFile.getName().equalsIgnoreCase(fileName))
			testRstFile = new File(testRstFile.getParent() + fileName);
		File testDataFile = new File(testDataFolderName + "/Precision/" + fileName);
		int rouNums = ExcelUtil.getRowNums(testDataFile, null);
		for (int index = 1; index <= rouNums; index++)
		{
			ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, index);
			int ID = Integer.parseInt(rowValue.get(0).trim());
			String Regulator = rowValue.get(1).trim();
			String Group = rowValue.get(2).trim();
			String Form = rowValue.get(3).trim();
			String ProcessDate = rowValue.get(4).trim();
			String Run = rowValue.get(5).trim();
			String cellFormDBFile = rowValue.get(6).trim();
			String cellFormExcelFile = rowValue.get(7).trim();
			String cellFormCSVFile = rowValue.get(8).trim();
			String importFile = rowValue.get(9).trim();
			String cellFormDBFile2 = rowValue.get(10).trim();
			String ExportExcel_Scaled = rowValue.get(11).trim();
			String ExportExcel_NoScale = rowValue.get(12).trim();
			String caseID = rowValue.get(14).trim();

			if (Run.equalsIgnoreCase("Y"))
			{
				Form = splitReturn(Form).get(2);

				if (!cellFormDBFile.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + cellFormDBFile);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + cellFormDBFile), checkCellValueFile);
					testCellValue_DBValue(ID, Regulator, Group, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}
				if (!cellFormExcelFile.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + cellFormExcelFile);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + cellFormExcelFile), checkCellValueFile);
					testCellValue_ExcelValue(ID, Regulator, Group, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}
				if (!cellFormCSVFile.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + cellFormCSVFile);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + cellFormCSVFile), checkCellValueFile);
					testCellValue_CSVValue(ID, Regulator, Group, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}
				if (!cellFormDBFile2.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + cellFormDBFile2);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + cellFormDBFile2), checkCellValueFile);
					testImportedCellValue_DBValue(ID, Regulator, Group, Form, ProcessDate, new File(importPath + importFile), checkCellValueFile, testRstFile, caseID);
				}

				if (!ExportExcel_Scaled.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + ExportExcel_Scaled);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + ExportExcel_Scaled), checkCellValueFile);
					testExportExcel_Scaled(ID, Regulator, Group, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}

				if (!ExportExcel_NoScale.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + ExportExcel_NoScale);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + ExportExcel_NoScale), checkCellValueFile);
					testExportExcel_Noscale(ID, Regulator, Group, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}

				reStartBrowser();
			}
		}
	}

	@Test
	public void testC4838() throws Exception
	{
		boolean testRst = false;
		String caseID = "4838";
		String Regulator = "US1 FED Reserve SP0";
		String Entity = "0001";
		String Form = "FRY9C v1";
		String ReferenceDate = "30/09/2014";
		String cellFormDBFile = "FRY9C_v1_ExpectedValue_20140930.xlsx";

		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);

			logger.info("Begin set retrieve properties");
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(Entity);
			retrievePage.setReferenceDate(ReferenceDate);
			retrievePage.setForm(Form);
			retrievePage.clickOK();
			listPage.closeJobDialog();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
				refreshPage();
			}

			testdataSource = createFolderAndCopyFile("Precision", null).get(0);
			testdataDest = createFolderAndCopyFile("Precision", null).get(1);

			File checkCellValueFile = new File(testdataDest + cellFormDBFile);
			if (checkCellValueFile.exists())
				checkCellValueFile.delete();
			FileUtils.copyFile(new File(testdataSource + cellFormDBFile), checkCellValueFile);

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			int amt = ExcelUtil.getRowAmts(checkCellValueFile, null);

			boolean step1 = true;
			for (int i = 1; i <= amt; i++)
			{
				boolean s1 = true;
				boolean s2 = true;

				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(checkCellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueRP = expectedValueValueList.get(3).trim();
				String expectedValueDB = expectedValueValueList.get(5).trim();

				logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ")=" + expectedValueRP);
				String extendCell = null;

				if (!rowID.equals(""))
				{
					if (rowID.equals("0"))
					{
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + cellName;
					}
					else
					{
						rowID = String.valueOf(Integer.parseInt(rowID) + 48);
						String gridName = getExtendCellName(Regulator, formCode, version, cellName);
						extendCell = gridName + rowID + cellName;
					}
				}
				else
					rowID = "0";
				String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);
				if (accValue != null)
					ExcelUtil.writeToExcel(checkCellValueFile, i, 4, accValue);
				else
					ExcelUtil.writeToExcel(checkCellValueFile, i, 4, "Cannot find cell");

				String DBValue = "";
				if (!expectedValueDB.equals(""))
				{
					logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ") in DB=" + expectedValueRP);
					boolean isExtendCell = false;
					if (extendCell != null)
						isExtendCell = true;
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ReferenceDate, Entity, instance, cellName, isExtendCell, Integer.parseInt(rowID));
					if (DBValue != null)
						ExcelUtil.writeToExcel(checkCellValueFile, i, 6, DBValue);
					else
						ExcelUtil.writeToExcel(checkCellValueFile, i, 6, "Cannot find cell");
				}
				if (!expectedValueRP.equals(accValue))
					s1 = false;
				try
				{
					if (Double.parseDouble(expectedValueDB) - Double.parseDouble(DBValue) != 0)
						s2 = false;
				}
				catch (Exception e)
				{
					if (!expectedValueDB.equals(DBValue))
						s2 = false;
				}
				if (!s1 || !s2)
				{
					ExcelUtil.writeToExcel(checkCellValueFile, i, 7, "Fail");
					step1 = false;
				}
				else
					ExcelUtil.writeToExcel(checkCellValueFile, i, 7, "Pass");
			}

			assertThat(true).as("Check cell value in Form and DB failed").isEqualTo(step1);

			// Blow steps to verify edit log

			// Below step to verify exported excel(no scale)
			String CheckCellValueFile = "FRYPC_v1_NoScale.xlsx";
			String exportFilePath = formInstancePage.exportToFile(null, null, null, "excel", null, null);
			String source = testdataSource + CheckCellValueFile;
			String dest = testdataDest + CheckCellValueFile;
			logger.info("Copy file " + source + " to " + dest);

			File expectedValueFile = new File(dest);
			if (expectedValueFile.exists())
				expectedValueFile.delete();
			FileUtils.copyFile(new File(source), expectedValueFile);
			boolean step2 = Business.verifyExportedFile(dest, exportFilePath, "excel");
			assertThat(true).isEqualTo(step2);

			CheckCellValueFile = "FRYPC_v1_WithScale.xlsx";
			exportFilePath = formInstancePage.exportToFile(null, null, null, "excel(Scale)", null, null);
			source = testdataSource + CheckCellValueFile;
			dest = testdataDest + CheckCellValueFile;
			logger.info("Copy file " + source + " to " + dest);

			expectedValueFile = new File(dest);
			if (expectedValueFile.exists())
				expectedValueFile.delete();
			FileUtils.copyFile(new File(source), expectedValueFile);
			boolean step3 = Business.verifyExportedFile(dest, exportFilePath, "excel");
			assertThat(true).isEqualTo(step3);

			formInstancePage.editCellValue("BHCK4435", "302.1");
			String cellText = formInstancePage.getCellText("BHCK4435");
			assertThat(cellText).isEqualTo("303");

			formInstancePage.editCellValue("BHCKB488", "1.666");
			cellText = formInstancePage.getCellText("BHCKB488");
			assertThat(cellText).isEqualTo("1.67");

			formInstancePage.editCellValue("BHCK4020", "975.5");
			cellText = formInstancePage.getCellText("BHCK4020");
			assertThat(cellText).isEqualTo("976");

			formInstancePage.editCellValue("BHCK4518", "9.75612");
			cellText = formInstancePage.getCellText("BHCK4518");
			assertThat(cellText).isEqualTo("9.7562");

			testRst = true;

		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Precision");
		}
	}
}
