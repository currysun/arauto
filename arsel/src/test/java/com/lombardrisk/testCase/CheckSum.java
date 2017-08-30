package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;

/**
 * Created by leo tu on 1/19/2017.
 */
public class CheckSum extends TestTemplate
{

	@Test
	public void testC6001() throws Exception
	{
		ListPage listPage = super.m.listPage;
		PhysicalLocationPage physicalLocationPage = listPage.enterPhysicalLoaction();
		if (!physicalLocationPage.getPhysicalLocation().contains("0001"))
			physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
	}

	@Test
	public void testC6803() throws Exception
	{
		String caseID = "6803";
		logger.info("====Verify the export only table will display in Return Sources window,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExpectSource = testData.get(4);

			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form,ReferenceDate);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			listPage.setForm(Form);
			ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, formVersion, ReferenceDate);
			List<String> views = returnSourcePage.getSourceView();
			assertThat(views.size()).isEqualTo(ExpectSource.split("#").length);
			for (String view : ExpectSource.split("#"))
			{
				assertThat(views).contains(view);
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
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6804() throws Exception
	{
		String caseID = "6804";
		logger.info("====Verify the checksum works when retrieve new from dashboard and summary only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL;

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			retrieveForm(listPage, Entity, Form, ReferenceDate);

			String s = getRandomString(1);
			if ("oracle".equals(AR_DBType))
				SQL = "update MV_DSFR20_DSP2 SET S_A_1='" + s + "'||substr(S_A_1,0) WHERE DS_ID=1";
			else
				SQL = "update MV_DSFR20_DSP2 SET S_A_1='" + s + "'+substr(S_A_1,0) WHERE DS_ID=1";
			DBQuery.updateSourceVew(SQL);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
			String SQL;
			if ("oracle".equals(AR_DBType))
				SQL = "update MV_DSFR20_DSP2 SET S_A_1=substr(S_A_1,0,3) WHERE DS_ID=1";
			else
				SQL = "update MV_DSFR20_DSP2 SET S_A_1=RTRIM(S_A_1,0,3) WHERE DS_ID=1";
			DBQuery.updateSourceVew(SQL);
		}
	}

	@Test
	public void testC6806() throws Exception
	{
		String caseID = "6806";
		logger.info("====Verify the checksum works when retrieve new from dashboard and summary only table has been inserted or deleted records,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String View = testData.get(6);
			String Table = testData.get(7);

			String SQL;
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			retrieveForm(listPage, Entity, Form, ReferenceDate);

			ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, formVersion, ReferenceDate);

			String runKey = returnSourcePage.getReturnRunKey(View);
			returnSourcePage.closeReturnSourcePage();

			SQL = "insert into " + Table + " (N_RUN_SKEY,PARENT_ENTITY_CODE,PARENT_ENTITY_NAME,N_SCENARIO_CD,D_CALENDAR_DATE,DS_ID,S_N_4_1) VALUES(" + runKey
					+ ",'0001','BANK USA',1,'31-DEC-15',10000,100)";
			DBQuery.updateSourceVew(SQL);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");

			jobManagerPage.backToDashboard();

			retrieveForm(listPage, Entity, Form, ReferenceDate);

			SQL = "delete from " + Table + " where DS_ID=10000";
			DBQuery.updateSourceVew(SQL);

			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			jobManagerPage = listPage.enterJobManagerPage();
			message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6805() throws Exception
	{
		String caseID = "6805";
		logger.info("====Verify the checksum works when retrieve new from return viewer page and summary only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);

			retrieveForm(listPage, Entity, Form, ReferenceDate);
			DBQuery.updateSourceVew(SQL1);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.retrieveNew("discard");
			if (isJobSuccessed())
			{
				logger.info("Retrieve new form succeed");
			}
			else
			{
				logger.error("Retrieve new form failed");
				testRst = false;
			}
			formInstancePage.closeRetrieveDialog();
			DBQuery.updateSourceVew(SQL2);

			formInstancePage.closeFormInstance();
			listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6807() throws Exception
	{
		String caseID = "6807";
		logger.info("====Verify the checksum works when retrieve new from dashboard and export only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form,ReferenceDate);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			DBQuery.updateSourceVew(SQL2);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6809() throws Exception
	{
		String caseID = "6809";
		logger.info("====Verify the checksum works when retrieve new from dashabord and export only table has been inserted or deleted records,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String View = testData.get(6);
			String Table = testData.get(7);

			String SQL;
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);

			retrieveForm(listPage, Entity, Form, ReferenceDate);
			ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, formVersion, ReferenceDate);

			String runKey = returnSourcePage.getReturnRunKey(View);
			returnSourcePage.closeReturnSourcePage();

			SQL = "insert into " + Table + " (N_RUN_SKEY,PARENT_ENTITY_CODE,PARENT_ENTITY_NAME,N_SCENARIO_CD,D_CALENDAR_DATE,DS_ID,S_N_4_1) VALUES(" + runKey
					+ ",'0001','BANK USA',1,'31-DEC-15',10000,100)";
			DBQuery.updateSourceVew(SQL);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6808() throws Exception
	{
		String caseID = "6808";
		logger.info("====Verify the checksum works when retrieve new from return viewer page and export only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			DBQuery.updateSourceVew(SQL1);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.retrieveNew("discard");
			Thread.sleep(10 * 1000);
			formInstancePage.closeRetrieveDialog();

			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			DBQuery.updateSourceVew(SQL2);
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6810() throws Exception
	{
		String caseID = "6810";
		logger.info("====Verify the checksum works when retrieve update from return viewer page and related summary only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			DBQuery.updateSourceVew(SQL1);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.update();
			formInstancePage.closeRetrieveDialog();

			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			DBQuery.updateSourceVew(SQL2);
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6811() throws Exception
	{
		String caseID = "6811";
		logger.info("====Verify the checksum works when retrieve update from return viewer page and related export only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			DBQuery.updateSourceVew(SQL1);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.update();
			formInstancePage.closeRetrieveDialog();

			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			DBQuery.updateSourceVew(SQL2);
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6812() throws Exception
	{
		String caseID = "6812";
		logger.info("====Verify the checksum works when retrieve update from return viewer page and unrelated summary only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			DBQuery.updateSourceVew(SQL1);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.update();
			formInstancePage.closeRetrieveDialog();
			DBQuery.updateSourceVew(SQL2);

			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6813() throws Exception
	{
		String caseID = "6813";
		logger.info("==== Verify the checksum works when retrieve update from return viewer page and unrelated export only table has been updated,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);
			String SQL1 = testData.get(6);
			String SQL2 = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			DBQuery.updateSourceVew(SQL1);

			ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
			returnSourcePage.update();
			formInstancePage.closeRetrieveDialog();
			DBQuery.updateSourceVew(SQL2);

			formInstancePage.closeFormInstance();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String message = jobManagerPage.getJobInfo(1).get(10);
			assertThat(message).isEqualTo("Export checksum not equal to Retrieve checksum.");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}

	@Test
	public void testC6814() throws Exception
	{
		String caseID = "6814";
		logger.info("==== Verify the export job should success without checksum fail info when export only table existing and no data update,case ID[" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_CheckSum, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ExportType = testData.get(4);
			String Module = testData.get(5);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.clickReadyForApprove();
			listPage = formInstancePage.closeFormInstance();
			HomePage homePage = listPage.approveReturn(listPage, Regulator, Entity, Form, ReferenceDate);
			homePage.logon();
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.exportDataSchduleReturn(ExportType, Module, null);
			formInstancePage.closeFormInstance();

			JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
			String status = jobManagerPage.getJobInfo(1).get(8);
			assertThat(status).isEqualTo("SUCCESS");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "CheckSum");
		}
	}
}
