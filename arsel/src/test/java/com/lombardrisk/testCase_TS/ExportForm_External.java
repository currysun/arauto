package com.lombardrisk.testCase_TS;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.HomePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.Business;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.FileUtil;
import com.lombardrisk.utils.fileService.XMLUtil;

public class ExportForm_External extends TestTemplate
{

	@Test
	public void test6586() throws Exception
	{
		String caseID = "6586";
		logger.info("====Verify entity variables values from EXTERNAL metadata can be transmitted to Vanilla successfully[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String Framework = testData.get(4);
			String Taxonomy = testData.get(5);
			String Module = testData.get(6);
			String BaseLine = testData.get(7);
			int DBIndex = Integer.parseInt(testData.get(8));

			logger.info("Set type= Consolidated");
			String FileType = "Vanilla";
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			String prefix = getToolsetRegPrefix(Regulator);
			String tableName = prefix + "uFormVars";
			String SQL = "UPDATE \"" + tableName + "\" SET \"CCValue\"='P' WHERE \"EntityName\"=" + Entity + "  and \"CCName\"='Group_Consolidation_Type'";
			DBQuery.update(DBIndex, SQL);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			String exportFilePath = listPage.ExportToRegulatorFormat(Entity, Form, ReferenceDate, FileType, Framework, Taxonomy, Module, null);
			String BaselineFile = System.getProperty("user.dir") + "/" + testDataFolderName + "/ExportForm_External/CheckCellValue/" + BaseLine;
			Boolean rst = Business.verifyExportedFile(BaselineFile, exportFilePath, FileType);
			assertThat(rst).as("Exist difference between exported file and baseline").isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID + ",6587,", testRst, "Export_External");
		}
	}

	@Test
	public void test6591() throws Exception
	{
		String caseID = "6591";
		logger.info("====Verify entity variables values from EXTERNAL metadata can be transmitted to Vanilla successfully[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			// String Framework = testData.get(4);
			// String Taxonomy = testData.get(5);
			String Module = testData.get(6);
			String BaseLine = testData.get(7);

			String FileType = "Vanilla";
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);
			int DBIndex = Integer.parseInt(testData.get(8));
			String prefix = getToolsetRegPrefix(Regulator);
			String tableName = prefix + "uFormVars";
			String SQL = "UPDATE \"" + tableName + "\" SET \"CCValue\"='P' WHERE \"EntityName\"=" + Entity + "  and \"CCName\"='Group_Consolidation_Type'";
			DBQuery.update(DBIndex, SQL);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, FileType, Module, null);
			String BaselineFile = System.getProperty("user.dir") + "/" + testDataFolderName + "/ExportForm_External/CheckCellValue/" + BaseLine;
			Boolean rst = Business.verifyExportedFile(BaselineFile, exportFilePath, FileType);
			assertThat(rst).as("Exist difference between exported file and baseline").isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID + ",6592,", testRst, "Export_External");
		}
	}

	@Test
	public void test6697() throws Exception
	{
		String caseID = "6697";
		logger.info("====Verify User can not do transmit Vanilla without privilege in toolset envrionment[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String User = testData.get(4);
			ListPage listPage = super.m.listPage;
			HomePage homePage = listPage.logout();
			homePage.loginAs(User, "password");
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);
			boolean rst = listPage.isExportToVanillaDisplayed();
			assertThat(rst).as("Should not display Export To Vanilla option").isEqualTo(false);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			rst = formInstancePage.isExistExportToVanilla();
			assertThat(rst).as("Should not display Export To Vanilla option").isEqualTo(false);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Export_External");
		}
	}

	@Test
	public void test6695() throws Exception
	{
		String caseID = "6695";
		logger.info("==== Verify Toolset(external metadata) Support transmit  Arbitrary successfully - Dashboard entry[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			List<String> Files = createFolderAndCopyFile("ExportForm_External", null);
			String testDataFolder = Files.get(0);
			String checkRstFolder = Files.get(1);
			String importFolder = Files.get(3);

			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFileName = testData.get(4);
			String Module = testData.get(5);
			String CheckCellValueFile = testData.get(6);
			int DBIndex = Integer.parseInt(testData.get(7));

			File importFile = new File(importFolder + ImportFileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			listPage.createFormFromExcel(importFile, false, false, false);
			String SQL;
			if ("oracle".equalsIgnoreCase(AR_DBType))
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=310002 and \"EntityId\"=1 and \"Process_Date\"='31-DEC-15'";
			else
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=310002 and \"EntityId\"=1 and \"Process_Date\"='2015-12-31'";
			DBQuery.update(DBIndex, SQL);
			String exportFilePath = listPage.ExportToRegulatorFormat(Entity, Form, ReferenceDate, "other", null, null, Module, null);
			if (exportFilePath.endsWith(".zip"))
			{
				String saveFolderString = new File(exportFilePath).getParent();
				exportFilePath = saveFolderString + "/" + FileUtil.unCompress(exportFilePath, saveFolderString).get(0);
			}
			File expectedValueFile = new File(checkRstFolder + CheckCellValueFile);
			if (expectedValueFile.exists())
			{
				expectedValueFile.delete();
			}
			logger.info("Begin check cell value in exported file");
			FileUtils.copyFile(new File(testDataFolder + CheckCellValueFile), expectedValueFile);
			boolean rst = Business.verifyExportedFile(expectedValueFile.getAbsolutePath(), exportFilePath, "iFile");

			assertThat(rst).as("Exported value should same with expected value").isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Export_External");
		}
	}

	@Test
	public void test6691() throws Exception
	{
		String caseID = "6691";
		logger.info("==== Verify Toolset(external metadata) Support transmit  Arbitrary successfully - Form page entry[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			List<String> Files = createFolderAndCopyFile("ExportForm_External", null);
			String testDataFolder = Files.get(0);
			String checkRstFolder = Files.get(1);
			String importFolder = Files.get(3);

			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFileName = testData.get(4);
			String Module = testData.get(5);
			String CheckCellValueFile = testData.get(6);
			int DBIndex = Integer.parseInt(testData.get(7));

			File importFile = new File(importFolder + ImportFileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String SQL;
			if ("oracle".equalsIgnoreCase(AR_DBType))
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=310030 and \"EntityId\"=1 and \"Process_Date\"='31-DEC-15'";
			else
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=310030 and \"EntityId\"=1 and \"Process_Date\"='2015-12-31'";
			DBQuery.update(DBIndex, SQL);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "other", Module, null);
			if (exportFilePath.endsWith(".zip"))
			{
				String saveFolderString = new File(exportFilePath).getParent();
				exportFilePath = saveFolderString + "/" + FileUtil.unCompress(exportFilePath, saveFolderString).get(0);
			}
			File expectedValueFile = new File(checkRstFolder + CheckCellValueFile);
			if (expectedValueFile.exists())
				expectedValueFile.delete();
			logger.info("Begin check cell value in exported file");
			FileUtils.copyFile(new File(testDataFolder + CheckCellValueFile), expectedValueFile);
			boolean rst = Business.verifyExportedFile(expectedValueFile.getAbsolutePath(), exportFilePath, "iFile");
			assertThat(rst).as("Exported value should same with expected value").isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Export_External");
		}
	}

	@Test
	public void test6696() throws Exception
	{
		String caseID = "6696";
		logger.info("==== Verify User can not do transmit XSLT or Arbitrary without privilege in toolset environment[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String User = testData.get(4);

			ListPage listPage = super.m.listPage;
			HomePage homePage = listPage.logout();
			homePage.loginAs(User, "password");
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);
			List<String> options = listPage.getExportToRegOptions();
			assertThat(options.size()).isEqualTo(1);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			options = formInstancePage.getExportToRegOptions();
			assertThat(options.size()).isEqualTo(1);
			assertThat(options.get(0)).isEqualTo("Export To Vanilla");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Export_External");
		}
	}

	@Test
	public void test6688() throws Exception
	{
		String caseID = "6688";
		logger.info("==== Verify Toolset(external metadata) Support transmit  XSLT successfully - Dashboard entry[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			List<String> Files = createFolderAndCopyFile("ExportForm_External", null);
			String testDataFolder = Files.get(0);
			// String checkRstFolder = Files.get(1);
			String importFolder = Files.get(3);

			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFileName = testData.get(4);
			String ExportType = testData.get(5);
			String Module = testData.get(6);
			String BaselineName = testData.get(7);

			File importFile = new File(importFolder + ImportFileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, ExportType, Module, null);
			if (exportFilePath.endsWith(".zip"))
			{
				String saveFolderString = new File(exportFilePath).getParent();
				exportFilePath = saveFolderString + "/" + FileUtil.unCompress(exportFilePath, saveFolderString).get(0);
			}
			String baseline = testDataFolder + BaselineName;
			boolean rst = Business.verifyExportedFile(baseline, exportFilePath, ExportType);
			assertThat(rst).as("Exported value should same with expected value").isEqualTo(true);
			formInstancePage.closeFormInstance();
			exportFilePath = listPage.ExportToRegulatorFormat(Entity, Form, ReferenceDate, ExportType, null, null, Module, null);
			if (exportFilePath.endsWith(".zip"))
			{
				String saveFolderString = new File(exportFilePath).getParent();
				exportFilePath = saveFolderString + "/" + FileUtil.unCompress(exportFilePath, saveFolderString).get(0);
			}
			rst = Business.verifyExportedFile(baseline, exportFilePath, ExportType);
			assertThat(rst).as("Exported value should same with expected value").isEqualTo(true);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID + ",6694", testRst, "Export_External");
		}
	}

	@Test
	public void test5557() throws Exception
	{
		String caseID = "5557";
		logger.info("==== Verify Vanilla precision of Monetary_Scale for numeric type is right when Monetary_Scale is 1/3/null[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_External, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String CellName = testData.get(4);
			String EditValue = testData.get(5);
			String Module = testData.get(6);
			String ExpectValue1 = testData.get(7);
			String ExpectValue2 = testData.get(8);
			String ExpectValue3 = testData.get(9);
			int DBIndex = Integer.parseInt(testData.get(10));

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.editCellValue(null, CellName, null, EditValue);
			String SQL;
			if ("oracle".equalsIgnoreCase(AR_DBType))
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=1 where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='04-JAN-17'";
			else
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=1 where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='2017-01-04'";
			DBQuery.update(DBIndex, SQL);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "Vanilla", Module, null);
			String text = XMLUtil.getCellValueFromVanilla(exportFilePath, null, CellName + ",1");
			assertThat(ExpectValue1).isEqualTo(text);

			if ("oracle".equalsIgnoreCase(AR_DBType))
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='04-JAN-17'";
			else
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=3 where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='2017-01-04'";
			DBQuery.update(DBIndex, SQL);
			exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "Vanilla", Module, null);
			text = XMLUtil.getCellValueFromVanilla(exportFilePath, null, CellName + ",1");
			assertThat(ExpectValue2).isEqualTo(text);

			if ("oracle".equalsIgnoreCase(AR_DBType))
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=Null where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='04-JAN-17'";
			else
				SQL = "update \"MASStat\" set \"Monetary_Scale\"=Null where \"ReturnId\"=3108 and \"EntityId\"=1 and \"Process_Date\"='2017-01-04'";
			DBQuery.update(DBIndex, SQL);
			exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "Vanilla", Module, null);
			text = XMLUtil.getCellValueFromVanilla(exportFilePath, null, CellName + ",1");
			assertThat(ExpectValue3).isEqualTo(text);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID + ",5560,5561", testRst, "Export_External");
		}
	}
}
