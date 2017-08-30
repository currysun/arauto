package com.lombardrisk.testCase_TS;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Created by leo tu on 1/16/2017.
 */
public class Precision extends TestTemplate
{

	static String testdataSource = null;
	static String testdataDest = null;
	static File testRstFile = null;
	static String importPath = null;

	protected void testCellValue_DBValue(int ID, String Regulator, String Entity, String Form, String ReferenceDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			T_DBType = getTestEnvironment().getDatabaseServer(7).getDriver();
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);
			ListPage listPage = super.m.listPage;

			listPage.getProductListPage(Regulator, Entity, Form, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			// if (Scale == null)
			// {
			// String prefix = getToolsetRegPrefix(Regulator);
			// String sql = "select \"ReturnId\" from \"" + prefix + "Rets\"
			// where \"Return\"='" + formCode + "' and \"Version\"=" + version +
			// "";
			// String ReturnID = DBQuery.queryRecord(sql);
			// sql = "select \"EntityId\" from \"" + prefix + "Grps\" where
			// \"Name\"='" + Entity + "'";
			// String EntityID = DBQuery.queryRecord(sql);
			// sql = "update \"" + prefix + "Stat\" set \"Monetary_Scale\"=null
			// where \"EntityId\"=" + EntityID + " and \"ReturnId\"=" + ReturnID
			// + "";
			// DBQuery.update(sql);
			// }
			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
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
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ReferenceDate, Entity, instance, cellName, isExtendCell, Integer.parseInt(rowID));
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
		}
	}

	protected void testExportExcel_Scaled(int ID, String Regulator, String Entity, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = m.formInstancePage;
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			String exportedFile = formInstancePage.exportToFile(Entity, Form, ProcessDate, "excel(Scale)", null, null);
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
		}
	}

	protected void testExportExcel_Noscale(int ID, String Regulator, String Entity, String Form, String ProcessDate, File cellValueFile, File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = m.formInstancePage;
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			String exportedFile = formInstancePage.exportToFile(Entity, Form, ProcessDate, "excel", null, null);
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
		}
	}

	protected void testImportedCellValue_DBValue(int ID, String Regulator, String Entity, String Form, String ProcessDate, File importFile, Boolean ApplyScale, Boolean Add, File cellValueFile,
			File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ProcessDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			// if (Scale == null)
			// {
			// String prefix = getToolsetRegPrefix(Regulator);
			// String sql = "select \"ReturnId\" from \"" + prefix + "Rets\"
			// where \"Return\"='" + formCode + "' and \"Version\"=" + version +
			// "";
			// String ReturnID = DBQuery.queryRecord(sql);
			// sql = "select \"EntityId\" from \"" + prefix + "Grps\" where
			// \"Name\"='" + Entity + "'";
			// String EntityID = DBQuery.queryRecord(sql);
			// sql = "update \"" + prefix + "Stat\" set \"Monetary_Scale\"=null
			// where \"EntityId\"=" + EntityID + " and \"ReturnId\"=" + ReturnID
			// + "";
			// DBQuery.update(sql);
			// }

			formInstancePage = listPage.importAdjustment(importFile, Add, ApplyScale, false);
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
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ProcessDate, Entity, instance, cellName, isExtendCell, Integer.parseInt(rowID));
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
		}
	}

	protected void testImportedDBValue(int ID, String Regulator, String Entity, String Form, String ProcessDate, File importFile, Boolean ApplyScale, Boolean Add, File cellValueFile,
			File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			// if (Scale == null)
			// {
			// String prefix = getToolsetRegPrefix(Regulator);
			// String sql = "select \"ReturnId\" from \"" + prefix + "Rets\"
			// where \"Return\"='" + formCode + "' and \"Version\"=" + version +
			// "";
			// String ReturnID = DBQuery.queryRecord(sql);
			// sql = "select \"EntityId\" from \"" + prefix + "Grps\" where
			// \"Name\"='" + Entity + "'";
			// String EntityID = DBQuery.queryRecord(sql);
			// sql = "update \"" + prefix + "Stat\" set \"Monetary_Scale\"=null
			// where \"EntityId\"=" + EntityID + " and \"ReturnId\"=" + ReturnID
			// + "";
			// DBQuery.update(sql);
			// }

			listPage.importAdjustment(importFile, Add, ApplyScale, false);
			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			for (int i = 1; i <= amt; i++)
			{
				boolean s1 = true;

				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueDB = expectedValueValueList.get(3).trim();

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

				String DBValue = "";
				if (!expectedValueDB.equals(""))
				{
					logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ") in DB=" + expectedValueDB);
					boolean isExtendCell = false;
					if (extendCell != null)
						isExtendCell = true;
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ProcessDate, Entity, instance, cellName, isExtendCell, Integer.parseInt(rowID));
					if (DBValue != null)
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 4, DBValue);
					}
					else
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 4, "Cannot find cell");
					}
				}
				try
				{
					if (Double.parseDouble(expectedValueDB) - Double.parseDouble(DBValue) != 0)
						s1 = false;
				}
				catch (Exception e)
				{
					if (!expectedValueDB.equals(DBValue))
						s1 = false;
				}
				if (!s1)
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
		}
	}

	protected void testCreatedDBValue(int ID, String Regulator, String Entity, String Form, String ProcessDate, File importFile, boolean ApplyScale, boolean AddToExistValue, File cellValueFile,
			File testDataFile, String caseID) throws Exception
	{
		boolean testRst = false;
		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ProcessDate);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, ApplyScale, false, false);
			if (AddToExistValue)
			{
				formInstancePage.closeFormInstance();
				listPage.importAdjustment(importFile, AddToExistValue, ApplyScale, false);
			}

			int amt = ExcelUtil.getRowAmts(cellValueFile, null);
			testRst = true;
			for (int i = 1; i <= amt; i++)
			{
				boolean s1 = true;

				ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(cellValueFile, null, i);
				String cellName = expectedValueValueList.get(0).trim();
				String rowID = expectedValueValueList.get(1).trim();
				String instance = (expectedValueValueList.get(2).trim().equals("")) ? null : expectedValueValueList.get(2).trim();
				String expectedValueDB = expectedValueValueList.get(3).trim();

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

				String DBValue = "";
				if (!expectedValueDB.equals(""))
				{
					logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ") in DB=" + expectedValueDB);
					boolean isExtendCell = false;
					if (extendCell != null)
						isExtendCell = true;
					DBValue = DBQuery.getCellValeFromDB(Regulator, formCode, version, ProcessDate, Entity, instance, cellName, isExtendCell, Integer.parseInt(rowID));
					if (DBValue != null)
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 4, DBValue);
					}
					else
					{
						ExcelUtil.writeToExcel(cellValueFile, i, 4, "Cannot find cell");
					}
				}
				try
				{
					if (Double.parseDouble(expectedValueDB) - Double.parseDouble(DBValue) != 0)
						s1 = false;
				}
				catch (Exception e)
				{
					if (!expectedValueDB.equals(DBValue))
						s1 = false;
				}
				if (!s1)
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
			writeTestResultToFile(testDataFile, ID, 14, caseID, testRst, "Precision");
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
			String Entity = rowValue.get(2).trim();
			String Form = rowValue.get(3).trim();
			String ProcessDate = rowValue.get(4).trim();
			String Run = rowValue.get(5).trim();
			String Scale = rowValue.get(6).trim();
			String FormDB = rowValue.get(7).trim();
			String NoScaleFile = rowValue.get(8).trim();
			String AppleScaleFile = rowValue.get(9).trim();
			String ImportFile = rowValue.get(10).trim();
			String FormDB2 = rowValue.get(11).trim();
			String DB_Import = rowValue.get(12).trim();
			String DB_Create = rowValue.get(13).trim();
			String caseID = rowValue.get(15).trim();

			if (Run.equalsIgnoreCase("Y"))
			{
				boolean ApplyScale = false;
				boolean AddToExistValue = false;
				if (ImportFile.length() > 0)
				{
					String tmp = ImportFile.replace("[", "~").replace("]", "");
					ImportFile = tmp.split("~")[0];
					String tmp2 = tmp.split("~")[1];
					if ("Scaled".equalsIgnoreCase(tmp2.split(",")[0]))
						ApplyScale = true;

					if ("Add".equalsIgnoreCase(tmp2.split(",")[1]))
						AddToExistValue = true;
				}
				Form = splitReturn(Form).get(2);

				Scale = Scale.toLowerCase();
				String sql = " update \"ECRuFormVars\" set \"CCNumber\"=" + Scale + " where \"CCName\"='Monetary_Scale' and \"EntityName\"='" + Entity + "'";
				DBQuery.update(7,sql);

				sql = " update \"ECRFormVars\" set \"CCNumber\"=" + Scale + " where \"CCName\"='Monetary_Scale' and \"ReturnId\"=-1";
				DBQuery.update(7,sql);
				if ("null".equals(Scale)) {
					Scale = null;
				}
				T_DBType = getTestEnvironment().getDatabaseServer(7).getDriver();//Update 1.15.5 so that sqlserver ENV can connect ora DB.
				if (!FormDB.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + FormDB);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + FormDB), checkCellValueFile);
					testCellValue_DBValue(ID, Regulator, Entity, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}
				if (!FormDB2.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + FormDB2);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + FormDB2), checkCellValueFile);
					testImportedCellValue_DBValue(ID, Regulator, Entity, Form, ProcessDate, new File(importPath + ImportFile), ApplyScale, AddToExistValue, checkCellValueFile, testRstFile, caseID);
				}

				if (!DB_Import.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + DB_Import);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + DB_Import), checkCellValueFile);
					testImportedDBValue(ID, Regulator, Entity, Form, ProcessDate, new File(importPath + ImportFile), ApplyScale, AddToExistValue, checkCellValueFile, testRstFile, caseID);
				}

				if (!DB_Create.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + DB_Create);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + DB_Create), checkCellValueFile);
					testCreatedDBValue(ID, Regulator, Entity, Form, ProcessDate, new File(importPath + ImportFile), ApplyScale, AddToExistValue, checkCellValueFile, testRstFile, caseID);
				}

				if (!AppleScaleFile.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + AppleScaleFile);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + AppleScaleFile), checkCellValueFile);
					testExportExcel_Scaled(ID, Regulator, Entity, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}

				if (!NoScaleFile.equals(""))
				{
					File checkCellValueFile = new File(testdataDest + NoScaleFile);
					if (checkCellValueFile.exists())
						checkCellValueFile.delete();
					FileUtils.copyFile(new File(testdataSource + NoScaleFile), checkCellValueFile);
					testExportExcel_Noscale(ID, Regulator, Entity, Form, ProcessDate, checkCellValueFile, testRstFile, caseID);
				}

				reStartBrowser();
			}
		}
	}

}
