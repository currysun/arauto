package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.pages.ValidationPage;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;
import com.lombardrisk.utils.fileService.TxtUtil;
import com.lombardrisk.utils.fileService.XMLUtil;

/**
 * Create by Leo Tu on Jul 2, 2015
 */
public class CheckRule extends TestTemplate
{

	static List<String> Files = new ArrayList<>();
	static String testDataFolder = null;
	static String checkRuleFileFolder = null;
	static File testRstFile = null;

	@Parameters(
	{ "fileName" })
	@BeforeClass
	private void getFilePath(@Optional String fileName) throws Exception
	{
		logger.info("File is: " + fileName);
		if (fileName == null || fileName.equals(""))
			fileName = "CheckRule.xls";
		Files = createFolderAndCopyFile("CheckRule", fileName);
		testDataFolder = Files.get(0);
		checkRuleFileFolder = Files.get(1);
		testRstFile = new File(Files.get(2));
	}

	protected void verifySumRule(int ID, String Regulator, String Group, String Form, String formCode, String version, String ProcessDate, String EditCell, String CheckRules) throws Exception
	{
		boolean testResult = false;
		try
		{
			logger.info("============Test Sum Rule[" + Form + "_" + ProcessDate + "_" + Group + "]=============");

			File expectedRstFile = new File(checkRuleFileFolder + CheckRules);
			if (expectedRstFile.exists())
			{
				expectedRstFile.delete();
			}
			logger.info("Copy file " + testDataFolder + CheckRules + " to " + expectedRstFile);
			FileUtils.copyFile(new File(testDataFolder + CheckRules), expectedRstFile);

			String cellName;
			String rowNO = null;
			if (EditCell.contains(","))
			{
				logger.info(EditCell + " is extendgrid cell");
				cellName = EditCell.split(",")[0].trim();
				rowNO = EditCell.split(",")[1].trim();
				rowNO = String.valueOf(Integer.parseInt(rowNO) + 48);
			}
			else
			{
				cellName = EditCell;
			}
			String gridName = getExtendCellName(Regulator, formCode, version, cellName);
			String ExtCellName = null;
			if (gridName != null)
			{
				ExtCellName = gridName + rowNO + cellName;
			}
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);

			String curValue = formInstancePage.getCellText(Regulator, formCode, version, null, cellName, ExtCellName);
			logger.info("Current value is: " + curValue);
			String editValue;
			try
			{
				if (curValue.equalsIgnoreCase("N"))
					curValue = "1";
				editValue = String.valueOf(Integer.parseInt(curValue.trim()) + 1);
			}
			catch (Exception e)
			{
				editValue = curValue.subSequence(0, 1) + getRandomString(2);
				if (editValue.equalsIgnoreCase(curValue))
				{
					editValue = curValue.subSequence(0, 1) + getRandomString(2);
				}
			}

			formInstancePage.editCellValue(null, cellName, ExtCellName, editValue);

			File exportProblem = new File(formInstancePage.exportProblem());

			long startTime = System.currentTimeMillis();
			String commons[] =
			{ parentPath + "/public/extension/GetRuleProblem/GetRuleProblem.exe", exportProblem.getAbsolutePath(), expectedRstFile.getAbsolutePath(), targetLogFolder };
			logger.info("cmd args are:" + commons[1] + " " + commons[2]);
			Process process = Runtime.getRuntime().exec(commons);
			process.waitFor();
			long cur = System.currentTimeMillis();
			logger.info("Take " + (cur - startTime) / 1000 + " seconds");

			boolean caseRst = true;
			int rowAmt = ExcelUtil.getRowNums(expectedRstFile, null);
			for (int i = 1; i <= rowAmt; i++)
			{
				ArrayList<String> rowValueList = ExcelUtil.getRowValueFromExcel(expectedRstFile, null, i);
				String caseID = rowValueList.get(0).trim();
				String check = rowValueList.get(1).trim();
				String ruleType = rowValueList.get(2);
				int ruleID = Integer.parseInt(rowValueList.get(3).trim());
				String instance = rowValueList.get(4).trim();
				if (instance.equals(""))
					instance = null;
				String rowID = rowValueList.get(5).trim();
				String id = rowID;
				if (rowID.length() > 0)
					rowID = String.valueOf(Integer.parseInt(rowValueList.get(5).trim()) + 48);
				String expectedValue = rowValueList.get(6);
				String expectedLevel = rowValueList.get(8).trim();
				String expectedError = rowValueList.get(10).trim();
				boolean levelRst = false;
				if (expectedLevel.equals(rowValueList.get(9).trim()))
					levelRst = true;
				boolean errorMsgRst = false;
				if (expectedError.equals(rowValueList.get(11).trim()))
					errorMsgRst = true;
				boolean valueRst = true;

				if (check.equalsIgnoreCase("Y"))
					if (ruleType.equalsIgnoreCase("Sum"))
					{
						if (instance != null)
						{
							logger.info("Begin verify sum rule " + ruleID + " instance: " + instance + " row number: " + id);
						}
						else
						{
							logger.info("Begin verify sum rule " + ruleID + " row number: " + id);
						}

						String Destfld = getDestFldFromSumRule(Regulator, formCode, version, ruleID);

						if (Destfld.contains("@"))
						{
							String temp = Destfld.replace("@", "~");
							Destfld = temp.split("~")[0];

							if (Destfld.contains("["))
							{

								String tmp = Destfld.replace("[", "~").replace("]", "");
								Destfld = tmp.split("~")[0].trim();
								instance = tmp.split("~")[1].trim();
							}
						}
						if (Destfld.contains("["))
						{
							String tmp = Destfld.replace("[", "~").replace("]", "");
							Destfld = tmp.split("~")[0].trim();
							instance = tmp.split("~")[1].trim();
						}
						gridName = getExtendCellName(Regulator, formCode, version, Destfld);
						ExtCellName = null;
						if (gridName != null)
							ExtCellName = gridName + rowID + Destfld;

						if (!expectedLevel.equalsIgnoreCase("Error"))
						{
							logger.info("Get cell " + Destfld + " instance: " + instance + " row number: " + id + " in form");
							String actualValue = formInstancePage.getCellText(Regulator, formCode, version, instance, Destfld, ExtCellName);
							if (actualValue == null)
							{
								valueRst = false;
								ExcelUtil.writeToExcel(expectedRstFile, i, 7, "Cannot find the cell: " + Destfld);
							}
							else
							{
								ExcelUtil.writeToExcel(expectedRstFile, i, 7, actualValue);
								if (!actualValue.equals(expectedValue))
									valueRst = false;
							}

						}

						if (valueRst && levelRst && errorMsgRst)
						{
							logger.info("Same with expected value");
							ExcelUtil.writeToExcel(expectedRstFile, i, 13, "Pass");
							testResult = true;
						}
						else
						{
							logger.warn("Different with expected value");
							ExcelUtil.writeToExcel(expectedRstFile, i, 13, "Fail");
						}
						writeTestResultToFile(caseID, caseRst, "CheckRule");
					}
			}

		}
		catch (NumberFormatException e)
		{
		}
		catch (Exception e)
		{
			testResult = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			closeFormInstance();
			ExcelUtil.writeTestRstToFile(testRstFile, ID, 9, testResult);
		}
	}

	protected void verifySumRuleValue(int ID, String Regulator, String Group, String Form, String formCode, String version, String ProcessDate, String CheckRules) throws Exception
	{
		boolean testResult = false;
		try
		{
			logger.info("============Test Sum Rule[" + Form + "_" + ProcessDate + "_" + Group + "]=============");

			File expectedRstFile = new File(checkRuleFileFolder + CheckRules);
			if (expectedRstFile.exists())
			{
				expectedRstFile.delete();
			}
			logger.info("Copy file " + testDataFolder + CheckRules + " to " + expectedRstFile);
			FileUtils.copyFile(new File(testDataFolder + CheckRules), expectedRstFile);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);

			testResult = getCellValueInForm2(formInstancePage, Regulator, formCode, version, expectedRstFile);

		}
		catch (NumberFormatException e)
		{
		}
		catch (Exception e)
		{
			testResult = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			closeFormInstance();
			ExcelUtil.writeTestRstToFile(testRstFile, ID, 9, testResult);
		}
	}

	@SuppressWarnings("static-access")
	protected void
	verifyValRuleForPro(int ID, String Regulator, String Group, String Form, String formCode, String version, String ProcessDate, String CheckRules) throws Exception
	{
		boolean testRst = false;
		try
		{
			logger.info("============Test Validation Rule[" + Form + "_" + ProcessDate + "_" + Group + "]=============");
			File expectedRstFile = new File(checkRuleFileFolder + CheckRules);
			if (expectedRstFile.exists())
			{
				expectedRstFile.delete();
			}
			logger.info("Copy file " + testDataFolder + CheckRules + " to " + expectedRstFile);
			FileUtils.copyFile(new File(testDataFolder + CheckRules), expectedRstFile);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, Form, ProcessDate);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);

			logger.info("Begin get rule result");
			File compareRstFile = new File(targetLogFolder + "/rule_compareRst.txt");
			if (compareRstFile.exists())
				compareRstFile.delete();
			String exportFile = formInstancePage.exportValidationResult();
			long startTime = System.currentTimeMillis();
			String commons[] =
			{ parentPath + "/public/extension/GetRuleResult/GetRuleResult.exe", exportFile, expectedRstFile.getAbsolutePath(), targetLogFolder, "Y" };
			logger.info("cmd args are:" + commons[0] + " " + commons[1] + " " + commons[2]);
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logger.info("Current time is:  " + dateFormat.format(now));
			Process process = Runtime.getRuntime().exec(commons);
			process.waitFor();
			long cur = System.currentTimeMillis();
			logger.info("Take " + (cur - startTime) / 1000 + " seconds");

			String rst = TxtUtil.getAllContent(compareRstFile).trim();
			if (rst.valueOf(0).equalsIgnoreCase("0"))
				rst = rst.substring(1);
			if (rst.equalsIgnoreCase("pass"))
				testRst = true;
			logger.info("Test result is:" + testRst);
		}
		catch (NumberFormatException e)
		{
			logger.error(e.getMessage(), e);
		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			closeFormInstance();
			ExcelUtil.writeTestRstToFile(testRstFile, ID, 9, testRst);

		}

	}

	@Parameters(
	{ "fileName" })
	@Test
	public void testRule(@Optional String fileName) throws Exception
	{
		logger.info("File is: " + fileName);
		if (fileName == null || fileName.equals(""))
			fileName = "CheckRule.xls";
		if (!testRstFile.getName().equalsIgnoreCase(fileName))
			testRstFile = new File(testRstFile.getParent() + fileName);
		File testDataFile = new File(testDataFolderName + "/CheckRule/" + fileName);
		for (int index = 1; index <= ExcelUtil.getRowNums(testDataFile, null); index++)
		{
			ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, index);
			int ID = Integer.parseInt(rowValue.get(0).trim());
			String Regulator = rowValue.get(1).trim();
			String Group = rowValue.get(2).trim();
			String Form = rowValue.get(3).trim();
			String ProcessDate = rowValue.get(4).trim();
			String Run = rowValue.get(5).trim();
			String Type = rowValue.get(6).trim();
			String EditCell = rowValue.get(7).trim();
			String CheckRules = rowValue.get(8).trim();

			EditCell = EditCell.trim();
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);
			Form = splitReturn(Form).get(2);

			if (CheckRules.endsWith(".xlsx") && Run.equalsIgnoreCase("Y"))
			{
				if (Type.equalsIgnoreCase("sum"))
				{
					if (EditCell.length() > 1)
						verifySumRule(ID, Regulator, Group, Form, formCode, version, ProcessDate, EditCell, CheckRules);
					else
						verifySumRuleValue(ID, Regulator, Group, Form, formCode, version, ProcessDate, CheckRules);
				}
				else
				{
					verifyValRuleForPro(ID, Regulator, Group, Form, formCode, version, ProcessDate, CheckRules);
				}
			}
		}
	}

	@Test
	public void test6747() throws Exception
	{
		String caseID = "6747";
		logger.info("==== Verify Summary Error report work well with grouping by type -Plain cell[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Validation, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form1 = testData.get(2);
			String Form2 = testData.get(3);
			String ReferenceDate = testData.get(4);
			String ImportFile = testData.get(5);
			String Module = testData.get(6);

			File importFile = new File(new File(testData_Validation).getParent() + "/ImportFile/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form1, null, false, false);
			formInstancePage.closeFormInstance();

			formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form2, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false);
			formInstancePage.validationNowClick();

			String timeStamp = formInstancePage.getValidationTimeStamp();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String today = sdf.format(new Date());
			assertThat(timeStamp.startsWith(today)).isEqualTo(true);

			List<String> rst = formInstancePage.getValidationResult_IntraSeries();
			assertThat(rst.get(0)).isEqualTo("5");
			assertThat(rst.get(1)).isEqualTo("4");
			assertThat(rst.get(2)).isEqualTo("4");
			assertThat(rst.get(3)).isEqualTo("6");

			rst = formInstancePage.getValidationResult_VSchedule();
			assertThat(rst.get(0)).isEqualTo("0");
			assertThat(rst.get(1)).isEqualTo("4");
			assertThat(rst.get(2)).isEqualTo("2");
			assertThat(rst.get(3)).isEqualTo("0");

			String exportFile = formInstancePage.exportToFile(Entity, Form2, ReferenceDate, "vanilla", Module, null);

			List<Map> attributeValue = XMLUtil.getElementAttributeFromXML(exportFile, "validationAggregate");
			for (Map map : attributeValue)
			{
				String firstAttr = map.get("ruleType").toString();
				if ("Intra-series".equals(firstAttr))
				{
					assertThat(map.get("failCount").toString()).isEqualTo("5");
					assertThat(map.get("passCount").toString()).isEqualTo("4");
					assertThat(map.get("ignoredCount").toString()).isEqualTo("4");
					assertThat(map.get("errorCount").toString()).isEqualTo("6");
				}
				if ("v-schedule".equals(firstAttr))
				{
					assertThat(map.get("failCount").toString()).isEqualTo("0");
					assertThat(map.get("passCount").toString()).isEqualTo("4");
					assertThat(map.get("ignoredCount").toString()).isEqualTo("2");
					assertThat(map.get("errorCount").toString()).isEqualTo("0");
				}
			}

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Validation");
		}
	}

	@Test
	public void test6749() throws Exception
	{
		String caseID = "6749";
		logger.info("==== Verify Summary Error report work well without grouping by type -Extend Grid[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Validation, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);
			String Module = testData.get(5);

			File importFile = new File(new File(testData_Validation).getParent() + "/ImportFile/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false);
			formInstancePage.validationNowClick();

			String timeStamp = formInstancePage.getValidationTimeStamp();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String today = sdf.format(new Date());
			assertThat(true).isEqualTo(timeStamp.startsWith(today));

			List<String> rst = formInstancePage.getValidationResult_IntraSeries();
			assertThat(rst.get(0)).isEqualTo("1");
			assertThat(rst.get(1)).isEqualTo("6");
			assertThat(rst.get(2)).isEqualTo("1");
			assertThat(rst.get(3)).isEqualTo("0");

			String exportFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "vanilla", Module, null);

			List<Map> attributeValue = XMLUtil.getElementAttributeFromXML(exportFile, "validationAggregate");
			for (Map map : attributeValue)
			{
				String firstAttr = map.get("ruleType").toString();
				if ("Quality-grid".equals(firstAttr))
				{
					assertThat(map.get("failCount").toString()).isEqualTo("1");
					assertThat(map.get("passCount").toString()).isEqualTo("6");
					assertThat(map.get("ignoredCount").toString()).isEqualTo("1");
					assertThat(map.get("errorCount").toString()).isEqualTo("0");
				}
			}

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Validation");
		}
	}

	@Test
	public void
	test6735() throws Exception
	{
		String caseID = "6735";
		logger.info("==== Verify 3 new filters can work well in validation panel-Plain cell[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Validation, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			ValidationPage validationPage = formInstancePage.enterValidation(true);
			validationPage.filterInReportLine("71");
			String ruleId = validationPage.getRuleNo(1, "val");
			assertThat(ruleId).isEqualTo("71");
			ruleId = validationPage.getRuleNo(2, "val");
			assertThat(ruleId).isEqualTo("1332");

			validationPage.clearFilter();

			validationPage.filterInType("v-schedule");
			ruleId = validationPage.getRuleNo(1, "val");
			assertThat(ruleId).isEqualTo("219");
			validationPage.enterLastPage();
			ruleId = validationPage.getRuleNo(2, "val");
			assertThat(ruleId).isEqualTo("224");
			validationPage.clearFilter();
			validationPage.filterInRegRuleID("v");
			validationPage.enterFirstPage();
			ruleId = validationPage.getRuleNo(1, "val");
			assertThat(ruleId).isEqualTo("2");
			validationPage.enterLastPage();
			ruleId = validationPage.getRuleNo(1, "val");
			assertThat(ruleId).isEqualTo("6");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Validation");
		}
	}

	@Test
	public void test6754() throws Exception
	{
		String caseID = "6754";
		logger.info("==== Verify Support export validation rule for level 2 content[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Validation, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);

			File importFile = new File(new File(testData_Validation).getParent() + "/ImportFile/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false);
			ValidationPage validationPage = formInstancePage.enterValidation(true);
			validationPage.filterInCell("RCFDG584");
			String exportFile = formInstancePage.exportValidationResult();

			ArrayList<String> columns = ExcelUtil.getRowValueFromExcel(new File(exportFile), "validation", 0);
			assertThat(columns).contains("Status");
			assertThat(columns).contains("Type");
			assertThat(columns).contains("Reg Rule Id");

			columns = ExcelUtil.getRowValueFromExcel(new File(exportFile), "validation", 2);
			assertThat(columns).contains("Cell Description");
			assertThat(columns).contains("Report Line");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Validation");
		}
	}

	@Test
	public void test6096() throws Exception
	{
		String caseID = "6096";
		logger.info("==== Verify Live Validation works fine[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Validation, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String CheckFile1 = testData.get(4);
			String CheckFile2 = testData.get(5);

			Files = createFolderAndCopyFile("CheckRule", null);
			testDataFolder = Files.get(0);

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			File expectedRstFile1 = new File(checkRuleFileFolder + CheckFile1);
			if (expectedRstFile1.exists())
				expectedRstFile1.delete();
			logger.info("Copy file " + testDataFolder + CheckFile1 + " to " + expectedRstFile1);
			FileUtils.copyFile(new File(testDataFolder + CheckFile1), expectedRstFile1);

			File expectedRstFile2 = new File(checkRuleFileFolder + CheckFile1);
			if (expectedRstFile2.exists())
				expectedRstFile2.delete();
			logger.info("Copy file " + testDataFolder + CheckFile1 + " to " + expectedRstFile2);
			FileUtils.copyFile(new File(testDataFolder + CheckFile1), expectedRstFile2);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, "31/10/2015", false, false);
			formInstancePage.liveValidationClick();
			formInstancePage.enterValidation(false);

			String color = formInstancePage.getCellColor(Regulator, formCode, formVersion, "MKSER010C010");
			assertThat(color).isEqualTo("Yellow");

			logger.info("Begin get rule result");
			File compareRstFile = new File(targetLogFolder + "/rule_compareRst.txt");
			if (compareRstFile.exists())
				compareRstFile.delete();
			String exportFile = formInstancePage.exportValidationResult();
			long startTime = System.currentTimeMillis();
			String commons[] =
			{ parentPath + "/public/extension/GetRuleResult/GetRuleResult.exe", exportFile, expectedRstFile1.getAbsolutePath(), targetLogFolder, "Y" };
			logger.info("cmd args are:" + commons[0] + " " + commons[1] + " " + commons[2]);
			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logger.info("Current time is:  " + dateFormat.format(now));
			Process process = Runtime.getRuntime().exec(commons);
			process.waitFor();
			long cur = System.currentTimeMillis();
			logger.info("Take " + (cur - startTime) / 1000 + " seconds");

			String rst = TxtUtil.getAllContent(compareRstFile).trim();
			if (rst.valueOf(0).equalsIgnoreCase("0"))
				rst = rst.substring(1);
			assertThat(rst).isEqualTo("Pass");

			formInstancePage.enterValidation(false);

			logger.info("Begin get rule result");
			compareRstFile = new File(targetLogFolder + "/rule_compareRst.txt");
			if (compareRstFile.exists())
				compareRstFile.delete();
			exportFile = formInstancePage.exportValidationResult();
			startTime = System.currentTimeMillis();
			String commons2[] =
			{ parentPath + "/public/extension/GetRuleResult/GetRuleResult.exe", exportFile, expectedRstFile2.getAbsolutePath(), targetLogFolder, "Y" };
			logger.info("cmd args are:" + commons2[0] + " " + commons2[1] + " " + commons2[2]);
			now = new Date();
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			logger.info("Current time is:  " + dateFormat.format(now));
			process = Runtime.getRuntime().exec(commons2);
			process.waitFor();
			cur = System.currentTimeMillis();
			logger.info("Take " + (cur - startTime) / 1000 + " seconds");

			rst = TxtUtil.getAllContent(compareRstFile).trim();
			if (rst.valueOf(0).equalsIgnoreCase("0"))
				rst = rst.substring(1);
			assertThat(rst).isEqualTo("Pass");

			// formInstancePage.liveValidationClick();
			color = formInstancePage.getCellColor(Regulator, formCode, formVersion, "MKSER010C010");
			assertThat(color).isNotEqualTo("Yellow");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Validation");
		}
	}

	private boolean getCellValueInForm2(FormInstancePage formInstancePage, String Regulator, String formCode, String version, File testData) throws Exception
	{
		boolean rst = true;
		int amt = ExcelUtil.getRowNums(testData, null);
		for (int index = 1; index <= amt; index++)
		{
			boolean testRst = false;
			boolean valueCorrect = true;

			ArrayList<String> expectedValueValueList = ExcelUtil.getRowValueFromExcel(testData, null, index);
			String caseId = expectedValueValueList.get(0).trim();
			String cellName = expectedValueValueList.get(1).trim();
			String rowID = expectedValueValueList.get(2).trim();
			String instance = (expectedValueValueList.get(3).trim().equals("")) ? null : expectedValueValueList.get(3).trim();
			String expectedValue = expectedValueValueList.get(4).trim();

			String extendCell = null;
			logger.info("Verify if " + cellName + "(instance=" + instance + " rowID=" + rowID + ")=" + expectedValue);
			if (rowID.length() > 0)
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

			boolean findCell = true;
			String accValue = formInstancePage.getCellText(Regulator, formCode, version, instance, cellName, extendCell);

			if (accValue != null)
				ExcelUtil.writeToExcel(testData, index, 5, accValue);
			else
			{
				ExcelUtil.writeToExcel(testData, index, 5, "Cannot find cell");
				ExcelUtil.writeToExcel(testData, index, 6, "Fail");
				findCell = false;
				valueCorrect = false;
			}

			if (findCell)
			{
				if (!compareCellValue(accValue, expectedValue, true))
				{
					logger.error("Expected value(" + expectedValue + ") is not equal accrual value(" + accValue + ")");
					valueCorrect = false;
					ExcelUtil.writeToExcel(testData, index, 6, "Fail");
				}
				else
				{
					ExcelUtil.writeToExcel(testData, index, 6, "Pass");
				}
			}
			logger.info(cellName + "(instance=" + instance + " rowID=" + rowID + ") expected value=" + expectedValue + " ,acctual value=" + accValue);
			if (valueCorrect)
				testRst = true;
			if (!testRst)
				rst = false;

			writeTestResultToFile(caseId, testRst, "CheckRule");
		}

		return rst;
	}

}
