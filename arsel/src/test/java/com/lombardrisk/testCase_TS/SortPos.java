package com.lombardrisk.testCase_TS;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;

/**
 * Created by leo tu on 3/2/2017.
 */
public class SortPos extends TestTemplate
{

	@Test
	public void test7245() throws Exception
	{
		boolean testRst = false;
		String caseID = "7245";
		logger.info("==== Verify the sorting of grid row honours SortPos & SortStrategy when create from excel[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			// String ReferenceDate = elementValues.get(3);
			String ImportFileName = elementValues.get(4);
			// String PageName=elementValues.get(5);
			String CellVlaueFile = elementValues.get(6);
			int DBIndex = Integer.parseInt(elementValues.get(7));

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String testDataFolder = Files.get(0);
			String checkCellFileFolder = Files.get(1);
			String importFolder = Files.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createFormFromExcel(new File(importFolder + "/" + ImportFileName), false, true, false, true);
			String source = testDataFolder + CellVlaueFile;
			String dest = checkCellFileFolder + CellVlaueFile;

			File expectedValueFile = new File(dest);
			if (!expectedValueFile.isDirectory())
			{
				if (expectedValueFile.exists())
					expectedValueFile.delete();
				FileUtils.copyFile(new File(source), expectedValueFile);
			}

			testRst = getCellValueInForm(formInstancePage, Regulator, formCode, formVersion, expectedValueFile, DBIndex);
			assertThat(testRst).isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7246() throws Exception
	{
		boolean testRst = false;
		String caseID = "7246";
		logger.info("==== Verify the sorting of grid row honours SortPos & SortStrategy when manually insert row[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			// String ReferenceDate = elementValues.get(3);
			String ImportFileName = elementValues.get(4);
			String PageName = elementValues.get(5);
			String Insert = elementValues.get(6);
			String Edit1 = elementValues.get(7);
			String Edit2 = elementValues.get(8);
			String Expect1 = elementValues.get(9);
			String Expect2 = elementValues.get(10);

			// String formCode=splitReturn(Form).get(0);
			// String formVersion=splitReturn(Form).get(1);

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String importFolder = Files.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createFormFromExcel(new File(importFolder + "/" + ImportFileName), false, true, false, true);

			formInstancePage.selectPage(PageName);
			formInstancePage.insertRow(Insert);
			for (String item : Edit1.split("#"))
			{
				String cellId = item.split("~")[0];
				String cellValue = item.split("~")[1];
				formInstancePage.editCellValue(cellId, cellValue);
			}

			formInstancePage.insertRow(Insert);
			for (String item : Edit2.split("#"))
			{
				String cellId = item.split("~")[0];
				String cellValue = item.split("~")[1];
				formInstancePage.editCellValue(cellId, cellValue);
			}

			for (String item : Expect1.split("#"))
			{
				String cellId = item.split("~")[0];
				String cellValue = item.split("~")[1];
				String actValue = formInstancePage.getCellText(cellId);
				assertThat(actValue).isEqualTo(cellValue);
			}
			formInstancePage.clickNextPage();
			for (String item : Expect2.split("#"))
			{
				String cellId = item.split("~")[0];
				String cellValue = item.split("~")[1];
				String actValue = formInstancePage.getCellText(cellId);
				assertThat(actValue).isEqualTo(cellValue);
			}

			testRst = true;

		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7247() throws Exception
	{
		boolean testRst = false;
		String caseID = "7247";
		logger.info("==== Verify the sorting of grid row honours SortPos & SortStrategy when import adjustments from excel[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			// String ReferenceDate = elementValues.get(3);
			String ImportFileName = elementValues.get(4);
			String AdjustmentFileName = elementValues.get(5);
			// String PageName=elementValues.get(6);
			String CellValueFile = elementValues.get(7);
			int DBIndex = Integer.parseInt(elementValues.get(8));

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String testDataFolder = Files.get(0);
			String checkCellFileFolder = Files.get(1);
			String importFolder = Files.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createFormFromExcel(new File(importFolder + "/" + ImportFileName), false, true, false, true);
			formInstancePage.importAdjustment(new File(importFolder + "/" + AdjustmentFileName), true, true, false);
			String source = testDataFolder + CellValueFile;
			String dest = checkCellFileFolder + CellValueFile;

			File expectedValueFile = new File(dest);
			if (!expectedValueFile.isDirectory())
			{
				if (expectedValueFile.exists())
					expectedValueFile.delete();
				FileUtils.copyFile(new File(source), expectedValueFile);
			}

			testRst = getCellValueInForm(formInstancePage, Regulator, formCode, formVersion, expectedValueFile, DBIndex);
			assertThat(testRst).isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7252() throws Exception
	{
		boolean testRst = false;
		String caseID = "7252";
		logger.info("==== Verify the sorting of grid row honours SortPos & SortStrategy when compute return[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			String ReferenceDate = elementValues.get(3);
			String CellValueFile = elementValues.get(4);
			int DBIndex = Integer.parseInt(elementValues.get(5));

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String testDataFolder = Files.get(0);
			String checkCellFileFolder = Files.get(1);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			ComputePage computePage = listPage.enterComputePage();
			FormInstancePage formInstancePage = computePage.computeReturn(Entity, ReferenceDate, Form, false);

			String source = testDataFolder + CellValueFile;
			String dest = checkCellFileFolder + CellValueFile;

			File expectedValueFile = new File(dest);
			if (!expectedValueFile.isDirectory())
			{
				if (expectedValueFile.exists())
					expectedValueFile.delete();
				FileUtils.copyFile(new File(source), expectedValueFile);
			}

			testRst = getCellValueInForm(formInstancePage, Regulator, formCode, formVersion, expectedValueFile, DBIndex);
			assertThat(testRst).isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7250() throws Exception
	{
		boolean testRst = false;
		String caseID = "7250";
		logger.info("==== Verify the sorting of grid row honours SortPos for REPORTER when SortStrategy is absent[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			String ReferenceDate = elementValues.get(3);
			String CellValueFile = elementValues.get(4);
			int DBIndex = Integer.parseInt(elementValues.get(5));

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String testDataFolder = Files.get(0);
			String checkCellFileFolder = Files.get(1);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			ComputePage computePage = listPage.enterComputePage();
			FormInstancePage formInstancePage = computePage.computeReturn(Entity, ReferenceDate, Form, false);

			String source = testDataFolder + CellValueFile;
			String dest = checkCellFileFolder + CellValueFile;

			File expectedValueFile = new File(dest);
			if (!expectedValueFile.isDirectory())
			{
				if (expectedValueFile.exists())
					expectedValueFile.delete();
				FileUtils.copyFile(new File(source), expectedValueFile);
			}

			testRst = getCellValueInForm(formInstancePage, Regulator, formCode, formVersion, expectedValueFile, DBIndex);
			assertThat(testRst).isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7248() throws Exception
	{
		boolean testRst = false;
		String caseID = "7248";
		logger.info("==== Verify error will pop up when create from excel or create new for an extend grid return with duplicated SortPos[case id=" + caseID + "]====");
		int index = 0;
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			// String Form = elementValues.get(2);
			// String ReferenceDate = elementValues.get(3);
			String ImportFileName = elementValues.get(4);
			String Message = elementValues.get(5);
			int DBIndex = Integer.parseInt(elementValues.get(6));

			index = DBIndex;

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String importFolder = Files.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			String SQL = "update \"ECRGridRef\" set \"SortPos\"=3 where \"ReturnId\"=360136 and \"Item\"='LEE4C040'";
			DBQuery.update(DBIndex, SQL);
			HomePage homePage = listPage.logout();
			listPage = homePage.logon();
			String actualMessage = listPage.getCreateFromExcelErrorMsg(new File(importFolder + "/" + ImportFileName), false);
			assertThat(actualMessage).isEqualTo(Message);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			String SQL = "update \"ECRGridRef\" set \"SortPos\"=4 where \"ReturnId\"=360136 and \"Item\"='LEE4C040'";
			DBQuery.update(index, SQL);
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7253() throws Exception
	{
		boolean testRst = false;
		String caseID = "7253";
		logger.info("==== Verify error will pop up when compute return with duplicated SortPos[case id=" + caseID + "]====");
		int index = 0;
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			String ReferenceDate = elementValues.get(3);
			String Message = elementValues.get(4);
			int DBIndex = Integer.parseInt(elementValues.get(5));

			index = DBIndex;

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			String SQL = "update \"ECRGridRef\" set \"SortPos\"=7 where \"ReturnId\"=360180 and \"Item\"='CRSDC080'";
			DBQuery.update(DBIndex, SQL);
			ComputePage computePage = listPage.enterComputePage();
			String actulMessage = computePage.getErrorMessage(Entity, ReferenceDate, Form);
			assertThat(actulMessage).isEqualTo(Message);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			String SQL = "update \"ECRGridRef\" set \"SortPos\"=8 where \"ReturnId\"=360180 and \"Item\"='CRSDC080'";
			DBQuery.update(index, SQL);
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7249() throws Exception
	{
		boolean testRst = false;
		String caseID = "7249";
		logger.info("==== Verify error will pop up when create from excel or create new for an extend grid return with un-recognized SortStrategy[case id=" + caseID + "]====");
		int index = 0;
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			// String Form = elementValues.get(2);
			// String ReferenceDate = elementValues.get(3);
			String ImportFileName = elementValues.get(4);
			String Message = elementValues.get(5);
			int DBIndex = Integer.parseInt(elementValues.get(6));

			index = DBIndex;

			List<String> Files = createFolderAndCopyFile("SortPos", null);
			String importFolder = Files.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			String SQL = "update \"ECRGridRef\" set \"SortStrategy\"='DSC' where \"ReturnId\"=360136 and \"Item\"='LEE2C020'";
			DBQuery.update(DBIndex, SQL);

			HomePage homePage = listPage.logout();
			listPage = homePage.logon();

			String actualMessage = listPage.getCreateFromExcelErrorMsg(new File(importFolder + "/" + ImportFileName), false);
			assertThat(actualMessage).isEqualTo(Message);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			String SQL = "update \"ECRGridRef\" set \"SortStrategy\"='DESC' where \"ReturnId\"=360136 and \"Item\"='LEE2C020'";
			DBQuery.update(index, SQL);
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7256() throws Exception
	{
		boolean testRst = false;
		String caseID = "7256";
		logger.info("==== Verify error will pop up when compute return with un-recognized SortStrategy[case id=" + caseID + "]====");
		int index = 0;
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			String ReferenceDate = elementValues.get(3);
			String Message = elementValues.get(4);
			int DBIndex = Integer.parseInt(elementValues.get(5));
			String Zone = elementValues.get(6);

			index = DBIndex;

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			String SQL = "update \"ECRGridRef\" set \"SortStrategy\"='AESC' where \"ReturnId\"=360180 and \"Item\"='CRSDC080'";
			DBQuery.update(DBIndex, SQL);

			for (int i = 0; i < Zone.split("#").length; i++)
			{
				PreferencePage preferencePage = listPage.enterPreferencePage();
				preferencePage.selectLanguageByValue(Zone.split("#")[i]);
				ComputePage computePage = listPage.enterComputePage();
				String actulMessage = computePage.getErrorMessage(Entity, ReferenceDate.split("#")[i], Form);
				assertThat(actulMessage).isEqualTo(Message.split("#")[i]);
			}
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			String SQL = "update \"ECRGridRef\" set \"SortStrategy\"='ASC' where \"ReturnId\"=360180 and \"Item\"='CRSDC080'";
			DBQuery.update(index, SQL);
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}

	@Test
	public void test7255() throws Exception
	{
		boolean testRst = false;
		String caseID = "7255";
		logger.info("==== Verify error will pop up if duplicated SortPos exists for REPORTER when SortStrategy is absent[case id=" + caseID + "]====");
		int index = 0;
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_SortPos, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String Form = elementValues.get(2);
			String ReferenceDate = elementValues.get(3);
			String Message = elementValues.get(4);
			int DBIndex = Integer.parseInt(elementValues.get(5));
			String Zone = elementValues.get(6);

			index = DBIndex;

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			String SQL = "update \"HKMAGridRef\" set \"SortPos\"=3 where \"ReturnId\"=70014 and \"Item\"='KMP3AC44'";
			DBQuery.update(DBIndex, SQL);

			for (int i = 0; i < Zone.split("#").length; i++)
			{
				PreferencePage preferencePage = listPage.enterPreferencePage();
				preferencePage.selectLanguageByValue(Zone.split("#")[i]);
				ComputePage computePage = listPage.enterComputePage();
				String actulMessage = computePage.getErrorMessage(Entity, ReferenceDate.split("#")[i], Form);
				assertThat(actulMessage).isEqualTo(Message.split("#")[i]);
			}
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			String SQL = "update \"HKMAGridRef\" set \"SortPos\"=4 where \"ReturnId\"=70014 and \"Item\"='KMP3AC44'";
			DBQuery.update(index, SQL);
			writeTestResultToFile(caseID, testRst, "SortPos");
		}
	}
}
