package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.pages.PreferencePage;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.CsvUtil;
import com.lombardrisk.utils.fileService.ExcelUtil;
import com.lombardrisk.utils.fileService.XMLUtil;

/**
 * Created by zhijun dai on 11/28/2016.
 */
public class ImportExportFormat extends TestTemplate
{
	String importFolder = System.getProperty("user.dir") + "/data_ar/ImportExportFormat/";

	@Test
	public void test6504() throws Exception
	{
		String caseID = "6504";
		logger.info("Verify the date format of exported CSV file is correct when Regional language is China_Chinese" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String cellID = testData.get(5);
			String cellValue = testData.get(6);
			String comment = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			// setFormat(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			formInstancePage.editCellValue(cellID, cellValue);

			String exportFilePath = formInstancePage.exportToFile(entity, returnName, referenceDate, "csv", null, null);

			logger.info("Export file is: " + exportFilePath);
			List<String> list = CsvUtil.readFile(new File(exportFilePath));
			Assert.assertTrue(list.get(0).contains(comment));
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i).contains(cellID))
				{
					String str = list.get(i).split(",")[2];
					String actualDate = str.substring(1, str.length() - 1);
					Pattern pattern = Pattern.compile("^\\d{4}-[0-1][0-9]-[0-3][0-9]$");
					Assert.assertTrue(pattern.matcher(actualDate).matches());
					break;
				}
			}
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6505() throws Exception
	{
		String caseID = "6505";
		logger.info("Verify the date format of exported CSV file is correct when Regional language is UK_English" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String cellID = testData.get(5);
			String cellValue = testData.get(6);
			String comment = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			formInstancePage.editCellValue(cellID, cellValue);

			String exportFilePath = formInstancePage.exportToFile(entity, returnName, referenceDate, "csv", null, null);

			System.out.println(exportFilePath);
			List<String> list = CsvUtil.readFile(new File(exportFilePath));
			Assert.assertTrue(list.get(0).contains(comment));
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i).contains(cellID))
				{
					String str = list.get(i).split(",")[2];
					String actualDate = str.substring(1, str.length() - 1);
					Pattern pattern = Pattern.compile("^[0-3][0-9]/[0-1][0-9]/\\d{4}$");
					Assert.assertTrue(pattern.matcher(actualDate).matches());
					break;
				}
			}
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6506() throws Exception
	{
		String caseID = "6506";
		logger.info("Verify the date format of exported CSV file is correct when Regional language is US_English" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String cellID = testData.get(5);
			String cellValue = testData.get(6);
			String comment = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			formInstancePage.editCellValue(cellID, cellValue);

			String exportFilePath = formInstancePage.exportToFile(entity, returnName, referenceDate, "csv", null, null);

			System.out.println(exportFilePath);
			List<String> list = CsvUtil.readFile(new File(exportFilePath));
			Assert.assertTrue(list.get(0).contains(comment));
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i).contains(cellID))
				{
					String str = list.get(i).split(",")[2];
					String actualDate = str.substring(1, str.length() - 1);
					Pattern pattern = Pattern.compile("^[0-1][0-9]/[0-3][0-9]/\\d{4}$");
					Assert.assertTrue(pattern.matcher(actualDate).matches());
					break;
				}
			}
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6518() throws Exception
	{
		String caseID = "6518";
		logger.info("Verify CSV file of yyyy-mm-dd date format can be uploaded and imported properly" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String cellID = testData.get(6);
			String value = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			formInstancePage.importAdjustment(importFile, false, false);
			String cellValue = formInstancePage.getCellText(cellID);
			Assert.assertEquals(cellValue, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6514() throws Exception
	{
		String caseID = "6514";
		logger.info("Dashboard EI-01-08 Verify CSV file of dd mm yyyy date format can be uploaded and imported properly" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String cellID = testData.get(6);
			String value = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			formInstancePage.importAdjustment(importFile, false, false);
			String cellValue = formInstancePage.getCellText(cellID);
			Assert.assertEquals(cellValue, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6517() throws Exception
	{
		String caseID = "6517";
		logger.info("Dashboard EI-01-09 Verify CSV file of mm dd yyyy date format can be uploaded and imported properly" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String cellID = testData.get(6);
			String value = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			formInstancePage.importAdjustment(importFile, false, false);
			String cellValue = formInstancePage.getCellText(cellID);
			Assert.assertEquals(cellValue, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6563() throws Exception
	{
		String caseID = "6563";
		logger.info("Dashboard EI-01-14 Verify CSV file can be uploaded and imported properly when date format is not same as Preferences settings" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String cellID = testData.get(6);
			String value = testData.get(7);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			formInstancePage.importAdjustment(importFile, false, false);
			String cellValue = formInstancePage.getCellText(cellID);
			Assert.assertEquals(cellValue, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6521() throws Exception
	{
		String caseID = "6521";
		logger.info("Dashboard EI-02-01 Verify error message will popup when DateFormat commentary does not exist in the CSV file" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String value = testData.get(6);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			String errorMsg = formInstancePage.getImportAdjustmentErrorInfo(importFile);
			Assert.assertEquals(errorMsg, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6564() throws Exception
	{
		String caseID = "6564";
		logger.info("Dashboard EI-02-02 Verify error message will popup when DateFormat commentary is not in the recognized form" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String value = testData.get(6);
			String fileName2 = testData.get(7);
			String value2 = testData.get(8);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			String errorMsg = formInstancePage.getImportAdjustmentErrorInfo(importFile);
			Assert.assertEquals(errorMsg, value);

			File importFile2 = new File(importFolder + "/" + fileName2);
			String errorMsg2 = formInstancePage.getImportAdjustmentErrorInfo(importFile2);
			Assert.assertEquals(errorMsg2, value2);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6534() throws Exception
	{
		String caseID = "6534";
		logger.info("Dashboard EI-02-03 Verify error message will popup when DateFormat commentary in the CSV file NOT supported by AR" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String value = testData.get(6);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			String errorMsg = formInstancePage.getImportAdjustmentErrorInfo(importFile);
			Assert.assertEquals(errorMsg, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6529() throws Exception
	{
		String caseID = "6529";
		logger.info("Dashboard EI-02-04 Verify error message will popup when part of date type value in the CSV file is not same as commentary line" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String language = testData.get(0);
			String regulator = testData.get(1);
			String entity = testData.get(2);
			String referenceDate = testData.get(3);
			String returnName = testData.get(4);
			String fileName = testData.get(5);
			String value = testData.get(6);

			ListPage listPage = super.m.listPage;
			PreferencePage preferencePage = listPage.enterPreferencePage();
			listPage = preferencePage.selectLanguageByValue(language);
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);

			File importFile = new File(importFolder + "/" + fileName);
			String errorMsg = formInstancePage.getImportAdjustmentErrorInfo(importFile).replaceAll("\n|\r", "");
			Assert.assertEquals(errorMsg, value);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6615() throws Exception
	{
		String caseID = "6615";
		logger.info("Verify date and time stamp is added when export return to excel file and date type cell format is specified" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String referenceDate = testData.get(2);
			String returnName = testData.get(3);
			String time = testData.get(4);
			String timeZone_expected = testData.get(5);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			String exportFilePath = formInstancePage.exportToFile(entity, returnName, referenceDate, "excel", null, null);
			String exportTime = ExcelUtil.getCellValueByCellName(new File(exportFilePath), "_ExportTime", null, null);
			String timeZone = ExcelUtil.getCellValueByCellName(new File(exportFilePath), "_TimeZone", null, null);
			String date = new SimpleDateFormat(time).format(new Date());
			Assert.assertTrue(exportTime.contains(date));
			Assert.assertEquals(timeZone, timeZone_expected);
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6627() throws Exception
	{
		String caseID = "6627";
		logger.info("Dashboard ETIT-01-05 Verify date and time stamp is added when export return to vanilla file" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String referenceDate = testData.get(2);
			String returnName = testData.get(3);
			String module = testData.get(4);
			String format = testData.get(5);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			String filePath = formInstancePage.exportToFile(regulator, returnName, referenceDate, "vanilla", module, null);
			String exportTime = XMLUtil.getCellValueFromVanilla(filePath, "exportTime", null);
			String date = new SimpleDateFormat(format).format(new Date());
			Assert.assertTrue(exportTime.contains(date));
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6702() throws Exception
	{
		String caseID = "6702";
		logger.info("Verify date and time stamp is not added when timezone and export time are not defined" + " CaseID is " + caseID);
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String referenceDate = testData.get(2);
			String returnName = testData.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, returnName, null, false, false);
			String exportFilePath = formInstancePage.exportToFile(entity, returnName, referenceDate, "excel", null, null);
			List<String> names = ExcelUtil.getCellNamesFromExcel(new File(exportFilePath));
			Assert.assertTrue(!names.contains("_ExportTime"));
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ExportFormat");
		}
	}

	@Test
	public void test6829() throws Exception
	{
		String caseID = "6829";
		logger.info("Verify date format (From extended grid) from the export excel is same with that in AR" + " CaseID is " + caseID);
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_importExportFormat, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);
			String CellName = testData.get(5);
			String CellName2 = testData.get(6);

			File importFile = new File(new File(testData_importExportFormat).getParent() + "/ImportFile/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false);
			String cellValue = formInstancePage.getCellText(CellName);
			assertThat(cellValue.startsWith("201606"));
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			cellValue = ExcelUtil.getCellValueByCellName(new File(exportFilePath), CellName2);
			assertThat(cellValue.startsWith("201606"));
			testRst = true;
		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID + ",6830", testRst, "ExportFormat");
		}
	}

}
