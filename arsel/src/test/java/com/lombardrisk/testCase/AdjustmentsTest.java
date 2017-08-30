package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;

/**
 * Created by zhijun dai on 12/29/2016.
 */
public class AdjustmentsTest extends TestTemplate
{

	@Test
	public void test6767() throws Exception
	{
		String caseID = "6767";
		logger.info("==== Verify Summary report count can display hundred and thousand[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String format = testData.get(4);
			String[] color = testData.get(5).split("#");
			String[] count = testData.get(6).split("#");

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);

			FormInstancePage formInstancePage = listPage.createNewForm(entity, referenceDate, form, null, false, false);
			formInstancePage.validationNowClick();
			String datetime = formInstancePage.getDatetimeAfterLiveValidate();
			Assert.assertTrue(formInstancePage.checkDateFormat(datetime, format));
			Assert.assertEquals(formInstancePage.getFirstRowValidateType(color[0]), count[0]);
			Assert.assertEquals(formInstancePage.getFirstRowValidateType(color[1]), count[1]);
			Assert.assertEquals(formInstancePage.getFirstRowValidateType(color[2]), count[2]);
			Assert.assertEquals(formInstancePage.getFirstRowValidateType(color[3]), count[3]);

		}
		catch (Throwable e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4006() throws Exception
	{
		/**
		 * OFSAA OFSA-01-19 Verify user can drilldown plain cell after
		 * modifictaion properly
		 */
		String caseID = "4006";
		logger.info("====Verify user can drilldown plain cell after modifictaion properly [" + caseID + "]====");
		boolean testRst = true;

		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String formCode = testData.get(4);
			String version = testData.get(5);
			String pageName = testData.get(6);
			String instanceId = testData.get(7);
			String cellName = testData.get(8);
			String cellValue = testData.get(9);

			// List the page filter by regulator and entity
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			listPage.deleteFormInstance(form, referenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(entity);
			retrievePage.setReferenceDate(referenceDate);
			retrievePage.setForm(form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();

			// Open the retrieved return
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			formInstancePage.editCellValue(cellName, cellValue);
			Assert.assertEquals(formInstancePage.getCellText(cellName), cellValue);

			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			List<String> values = allocationPage.getColumnValueOfAllRows("2");
			Assert.assertTrue(values.contains("203"));
			Assert.assertTrue(values.contains("204"));
			Assert.assertTrue(values.contains("241"));
			Assert.assertTrue(values.contains("242"));
		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4437() throws Exception
	{
		/**
		 * OFSAA OFSA-01-27 Verify plain cell of Numbe type can be drilldown
		 * properly after user retrieve and PRESERVE Adjustment - return viewer
		 * page
		 */
		String caseID = "4437";
		logger.info("====Verify plain cell of Numbe type can be drilldown properly after user retrieve and PRESERVE Adjustment - return viewer page [" + caseID + "]====");
		boolean testRst = true;
		boolean executeSQL = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String formCode = testData.get(4);
			String version = testData.get(5);
			String pageName = testData.get(6);
			String instanceId = testData.get(7);
			String cellName = testData.get(8);
			String cellValue = testData.get(9);
			String type = testData.get(10);

			// List the page filter by regulator and entity
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			listPage.deleteFormInstance(form, referenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(entity);
			retrievePage.setReferenceDate(referenceDate);
			retrievePage.setForm(form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();

			// Open the retrieved return
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			formInstancePage.editCellValue(cellName, cellValue);
			Assert.assertEquals(formInstancePage.getCellText(cellName), cellValue);

			// Update the source data
			String updateRunSkeyValue = "UPDATE MV_FRY9C_HI1_0318 SET N_RUN_SKEY = \"N_RUN_SKEY\"+1";
			String updateSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY+1000000000 where DRILL_REF IN (75,460)";
			DBQuery.updateSourceVew(updateRunSkeyValue);
			DBQuery.updateSourceVew(updateSource);
			executeSQL = true;

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.retrieveNew(type);
			if (isJobSuccessed())
			{
				logger.info("Update form sucessed");
			}
			else
			{
				logger.error("Update form failed");
			}
			formInstancePage.closeRetrieveDialog();
			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			Assert.assertEquals(formInstancePage.getCellText(cellName), cellValue);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			List<String> values = allocationPage.getColumnValueOfAllRows("6");

			String querySql = "SELECT N_EOP_BAL_RCY FROM MV_FRY9C_HI1_0318 where DRILL_REF IN (75,460)";
			List<String> initValues = DBQuery.queryRecordsFromSourceData(querySql);
			Assert.assertEquals(values.get(0), initValues.get(0));
			Assert.assertEquals(values.get(1), initValues.get(1));

		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String revertSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY-1000000000 where DRILL_REF IN (75,460)";
				DBQuery.updateSourceVew(revertSource);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4438() throws Exception
	{
		/**
		 * OFSAA OFSA-01-28 Verify plain cell of Numbe type can be drilldown
		 * properly after user retrieve and DISCARD Adjustment - return viewer
		 * page
		 */
		String caseID = "4438";
		logger.info("====Verify plain cell of Numbe type can be drilldown properly after user retrieve and DISCARD Adjustment - return viewer page [" + caseID + "]====");
		boolean testRst = true;
		boolean executeSQL = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String formCode = testData.get(4);
			String version = testData.get(5);
			String pageName = testData.get(6);
			String instanceId = testData.get(7);
			String cellName = testData.get(8);
			String cellValue = testData.get(9);
			String type = testData.get(10);

			// List the page filter by regulator and entity
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			listPage.deleteFormInstance(form, referenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(entity);
			retrievePage.setReferenceDate(referenceDate);
			retrievePage.setForm(form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();

			// Open the retrieved return
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			formInstancePage.editCellValue(cellName, cellValue);
			Assert.assertEquals(formInstancePage.getCellText(cellName), cellValue);

			// Update the source data
			String updateRunSkeyValue = "UPDATE MV_FRY9C_HI1_0318 SET N_RUN_SKEY = \"N_RUN_SKEY\"+1";
			String updateSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY+1000000000 where DRILL_REF IN (75,460)";
			DBQuery.updateSourceVew(updateRunSkeyValue);
			DBQuery.updateSourceVew(updateSource);
			executeSQL = true;

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.retrieveNew(type);
			if (isJobSuccessed())
			{
				logger.info("Update form succeeded");
			}
			else
			{
				logger.error("Update form failed");
			}
			formInstancePage.closeRetrieveDialog();
			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			Assert.assertNotEquals(formInstancePage.getCellText(cellName), cellValue);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			List<String> values = allocationPage.getColumnValueOfAllRows("6");

			String querySql = "SELECT N_EOP_BAL_RCY FROM MV_FRY9C_HI1_0318 where DRILL_REF IN (75,460)";
			List<String> initValues = DBQuery.queryRecordsFromSourceData(querySql);
			Assert.assertEquals(values.get(0), initValues.get(0));
			Assert.assertEquals(values.get(1), initValues.get(1));

		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String revertSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY-1000000000 where DRILL_REF IN (75,460)";
				DBQuery.updateSourceVew(revertSource);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4459() throws Exception
	{
		/**
		 * OFSAA OFSA-01-31 Verify plain cell of Numbe type can be drilldown
		 * properly after user UPDATE return - return viewer page
		 */
		String caseID = "4459";
		logger.info("====Verify plain cell of Numbe type can be drilldown properly after user UPDATE return - return viewer page [" + caseID + "]====");
		boolean testRst = true;
		boolean executeSQL = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String formCode = testData.get(4);
			String version = testData.get(5);
			String pageName = testData.get(6);
			String instanceId = testData.get(7);
			String cellName = testData.get(8);
			String cellValue = testData.get(9);

			// List the page filter by regulator and entity
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			listPage.deleteFormInstance(form, referenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(entity);
			retrievePage.setReferenceDate(referenceDate);
			retrievePage.setForm(form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();

			// Open the retrieved return
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			formInstancePage.editCellValue(cellName, cellValue);
			Assert.assertEquals(formInstancePage.getCellText(cellName), cellValue);

			// Update the source data
			String updateRunSkeyValue = "UPDATE MV_FRY9C_HI1_0318 SET N_RUN_SKEY=N_RUN_SKEY+1";
			String updateSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY+1000000000 where DRILL_REF IN (75,460)";
			DBQuery.updateSourceVew(updateRunSkeyValue);
			DBQuery.updateSourceVew(updateSource);
			executeSQL = true;

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.update();
			if (isJobSuccessed())
			{
				logger.info("Update form stressed");
			}
			else
			{
				logger.error("Update form failed");
			}
			formInstancePage.closeRetrieveDialog();
			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			Assert.assertNotEquals(formInstancePage.getCellText(cellName), cellValue);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			List<String> values = allocationPage.getColumnValueOfAllRows("6");

			String querySql = "SELECT N_EOP_BAL_RCY FROM MV_FRY9C_HI1_0318 where DRILL_REF IN (75,460)";
			List<String> initValues = DBQuery.queryRecordsFromSourceData(querySql);
			Assert.assertEquals(values.get(0), initValues.get(0));
			Assert.assertEquals(values.get(1), initValues.get(1));

		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String revertSource = "UPDATE MV_FRY9C_HI1_0318 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY-1000000000 where DRILL_REF IN (75,460)";
				DBQuery.updateSourceVew(revertSource);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4439() throws Exception
	{
		/**
		 * OFSAA OFSA-01-29 Verify extended grid of Numbe type can be drilldown
		 * properly after user retrieve and PRESERVE Adjustment - dashboard
		 */
		String caseID = "4439";
		logger.info("====Verify extended grid of Numbe type can be drilldown properly after user retrieve and PRESERVE Adjustment - dashboard [" + caseID + "]====");
		boolean testRst = true;
		boolean executeSQL = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testData.get(0);
			String entity = testData.get(1);
			String form = testData.get(2);
			String referenceDate = testData.get(3);
			String formCode = testData.get(4);
			String version = testData.get(5);
			String pageName = testData.get(6);
			String instanceId = testData.get(7);
			String cellName = testData.get(8);
			String cellValue = testData.get(9);
			String type = testData.get(10);
			String actualValue = testData.get(11);

			// List the page filter by regulator and entity
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			listPage.deleteFormInstance(form, referenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(entity);
			retrievePage.setReferenceDate(referenceDate);
			retrievePage.setForm(form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();

			// Open the retrieved return
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			formInstancePage.editCellValue(cellName, cellValue);
			Assert.assertEquals(formInstancePage.getCellText(cellName), actualValue);

			// Update the source data
			String updateRunSkeyValue = "UPDATE MV_FRY14ASUMM_PPME6 SET N_RUN_SKEY=N_RUN_SKEY+1";
			String updateSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY*2 where DRILL_REF IN (281,282)";
			DBQuery.updateSourceVew(updateRunSkeyValue);
			DBQuery.updateSourceVew(updateSource);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.retrieveNew(type);
			if (isJobSuccessed())
			{
				logger.info("Update form succeeded");
			}
			else
			{
				logger.error("Update form failed");
			}
			formInstancePage.closeRetrieveDialog();
			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

			formInstancePage.selectPage(pageName);
			formInstancePage.selectInstance(instanceId);
			Assert.assertEquals(formInstancePage.getCellText(cellName), actualValue);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			List<String> values = allocationPage.getColumnValueOfAllRows("5");

			String querySql = "SELECT N_EOP_BAL_RCY FROM MV_FRY14ASUMM_PPME6 where DRILL_REF IN (281,282)";
			List<String> initValues = DBQuery.queryRecordsFromSourceData(querySql);
			Assert.assertEquals(values.get(0), initValues.get(0));
			Assert.assertEquals(values.get(1), initValues.get(1));

		}
		catch (Exception e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String revertSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY/2 where DRILL_REF IN (281,282)";
				DBQuery.updateSourceVew(revertSource);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4440() throws Exception
	{
		String caseID = "4440";
		logger.info("====Verify extended grid of Number type can be drilldown properly after user retrieve and DISCARD Adjustment - dashboard[case id=" + caseID + "]====");
		boolean testRst = false;
		boolean executeSQL = false;
		String nodeName = "C" + caseID;
		List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
		String Regulator = testData.get(0);
		String Entity = testData.get(1);
		String Form = testData.get(2);
		String ReferenceDate = testData.get(3);
		String PageName = testData.get(4);
		String Instance = testData.get(5);
		String CellName = testData.get(6);
		String CellValue = testData.get(7);
		String Type = testData.get(8);
		String ExpectedValue1 = testData.get(9);
		String ExpectedValue2 = testData.get(10);

		String formCode = splitReturn(Form).get(0);
		String formVersion = splitReturn(Form).get(1);

		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(Entity);
			retrievePage.setReferenceDate(ReferenceDate);
			retrievePage.setForm(Form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();
			refreshPage();

			// Open the retrieved return
			listPage.setProcessDate(ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.selectPage(PageName);
			formInstancePage.selectInstance(Instance);
			formInstancePage.editCellValue(CellName, CellValue);
			formInstancePage.closeFormInstance();

			String updateSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY*2 where DRILL_REF IN (281,282)";
			DBQuery.updateSourceVew(updateSource);

			updateSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_RUN_SKEY=N_RUN_SKEY+1";
			DBQuery.updateSourceVew(updateSource);
			executeSQL = true;

			ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, formVersion, ReferenceDate);
			returnSourcePage.retrieveNew(Type);
			if (isJobSuccessed())
			{
				logger.info("Retrieve new form succeed");
			}
			else
			{
				logger.error("Retrieve new form failed");
				testRst = false;
			}
			listPage.closeJobDialog();

			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.selectPage(PageName);
			formInstancePage.selectInstance(Instance);
			String actualValue = formInstancePage.getCellText(CellName);
			assertThat(actualValue).isEqualTo(ExpectedValue1);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(CellName);
			String currentValue = allocationPage.getCurrentValue();

			assertThat(currentValue).isEqualTo(ExpectedValue2);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String dropTable = "drop table MV_FRY14ASUMM_PPME6";
				String createTable="create table MV_FRY14ASUMM_PPME6 as select * from MV_FRY14ASUMM_PPME6_BK";
				DBQuery.queryRecords(1,dropTable);
				DBQuery.queryRecords(1, createTable);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test4461() throws Exception
	{
		String caseID = "4461";
		logger.info("====Verify extended grid of Numbe type can be drilldown properly after user UPDATE return - dashboard[case id=" + caseID + "]====");
		boolean testRst = false;
		boolean executeSQL = false;
		String nodeName = "C" + caseID;
		List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
		String Regulator = testData.get(0);
		String Entity = testData.get(1);
		String Form = testData.get(2);
		String ReferenceDate = testData.get(3);
		String PageName = testData.get(4);
		String Instance = testData.get(5);
		String CellName = testData.get(6);
		String CellValue = testData.get(7);
		String ExpectedValue1 = testData.get(8);
		String ExpectedValue2 = testData.get(9);

		String formCode = splitReturn(Form).get(0);
		String formVersion = splitReturn(Form).get(1);

		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(Entity);
			retrievePage.setReferenceDate(ReferenceDate);
			retrievePage.setForm(Form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();
			refreshPage();

			// Open the retrieved return
			listPage.setProcessDate(ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.selectPage(PageName);
			formInstancePage.selectInstance(Instance);
			formInstancePage.editCellValue(CellName, CellValue);
			formInstancePage.closeFormInstance();

			String updateSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_EOP_BAL_RCY=N_EOP_BAL_RCY*2 where DRILL_REF IN (281,282)";
			DBQuery.updateSourceVew(updateSource);

			updateSource = "UPDATE MV_FRY14ASUMM_PPME6 SET N_RUN_SKEY=N_RUN_SKEY+1";
			DBQuery.updateSourceVew(updateSource);
			executeSQL = true;

			ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, formVersion, ReferenceDate);
			returnSourcePage.update();
			if (isJobSuccessed())
			{
				logger.info("Retrieve new form succeed");
			}
			else
			{
				logger.error("Retrieve new form failed");
				testRst = false;
			}
			listPage.closeJobDialog();

			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.selectPage(PageName);
			formInstancePage.selectInstance(Instance);
			String actualValue = formInstancePage.getCellText(CellName);
			assertThat(actualValue).isEqualTo(ExpectedValue1);
			// AllocationPage allocationPage =
			// formInstancePage.cellDoubleClick(CellName);
			// String currentValue = allocationPage.getCurrentValue();
			//
			// assertThat(currentValue).isEqualTo(ExpectedValue2);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			if (executeSQL)
			{
				String dropTable = "drop table MV_FRY14ASUMM_PPME6";
				String createTable="create table MV_FRY14ASUMM_PPME6 as select * from MV_FRY14ASUMM_PPME6_BK";
				DBQuery.queryRecords(1,dropTable);
				DBQuery.queryRecords(1, createTable);
			}
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6951() throws Exception
	{
		String caseID = "6951";
		logger.info("====Verify user can drilldown extended grid after modifictaion properly[case id=" + caseID + "]====");
		boolean testRst = false;
		String nodeName = "C" + caseID;
		List<String> testData = getElementValueFromXML(testData_adjustmentsTest, nodeName);
		String Regulator = testData.get(0);
		String Entity = testData.get(1);
		String Form = testData.get(2);
		String ReferenceDate = testData.get(3);
		String PageName = testData.get(4);
		String Instance = testData.get(5);
		String CellName = testData.get(6);
		String CellValue = testData.get(7);

		try
		{
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);

			// Retrieve the special return
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(Entity);
			retrievePage.setReferenceDate(ReferenceDate);
			retrievePage.setForm(Form);
			retrievePage.clickOK();
			if (isJobSuccessed())
			{
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();
			refreshPage();

			// Open the retrieved return
			listPage.setProcessDate(ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.selectPage(PageName);
			formInstancePage.selectInstance(Instance);
			formInstancePage.clickNextPage("ExtDBGrid44");
			formInstancePage.editCellValue(CellName, CellValue);

			AllocationPage allocationPage = formInstancePage.cellDoubleClick(CellName);
			List<String> drillRef = allocationPage.getColumnValueOfAllRows("2");
			assertThat(drillRef).contains("273");
			assertThat(drillRef).contains("274");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}
}
