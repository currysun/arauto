package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.testng.annotations.Test;

import com.lombardrisk.pages.EditionManagePage;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.TestTemplate;

/**
 * Create by Leo Tu on 9/18/2015
 */
public class EditionManage extends TestTemplate
{

	@Test
	public void test4042() throws Exception
	{
		boolean testRst = false;
		String caseID = "4042";
		logger.info("====Verify new edition will generate when create return for an existing return,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			int initAmt = editionPage.getEditionAmt();
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			editionPage = listPage.openEditionManage(rowIndex);
			int afterAmt = editionPage.getEditionAmt();
			assertThat(1).as("No edition was generated after create new return").isEqualTo(afterAmt - initAmt);
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4043() throws Exception
	{
		boolean testRst = false;
		String caseID = "4043";
		logger.info("====Verify new edition will generate when create return from EXCEL for an existing return,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");
			String fileName = getElementValueFromXML(testData_edition, nodeName, "ImportFile");
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			int initAmt = editionPage.getEditionAmt();
			File importFile = new File(new File(testData_edition).getParent() + "/ImportFile/" + fileName);
			listPage.createFormFromExcel(importFile, false, false, false);
			rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			editionPage = listPage.openEditionManage(rowIndex);
			int afterAmt = editionPage.getEditionAmt();
			assertThat(1).as("No edition was generated after create return from excel").isEqualTo(afterAmt - initAmt);
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4044() throws Exception
	{
		boolean testRst = false;
		String caseID = "4044";
		logger.info("====Verify REPORT STATUS column of EDITION MANAGER window is editable,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			assertThat(true).as("REPORT STATUS should be editable").isEqualTo(editionPage.isStatusEditable(1));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4045() throws Exception
	{
		boolean testRst = false;
		String caseID = "4045";
		logger.info("====Verify the dormant edition can be deleted in Edition Manager window with warning message,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstDormantEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			editionPage.deleteEdition(creationDate);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			assertThat(false).as("Deleted editions still in editions dropdown list").isEqualTo(formInstancePage.isEditionExist(editionInfo.replace("#", " #")));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4046() throws Exception
	{
		boolean testRst = false;
		String caseID = "4046";
		logger.info("====Verify the dormant edition can be deleted in Edition Manager window with warning message-return page,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			EditionManagePage editionPage = formInstancePage.openEditionManage();
			String editionInfo = editionPage.getFirstDormantEdition();
			String creationDate = editionInfo.split("#")[0];

			formInstancePage.openEditionManage();
			editionPage.deleteEdition(creationDate);
			boolean rst = formInstancePage.isEditionExist(editionInfo.replace("#", " #"));
			assertThat(false).as("Deleted edition still in editions drop list").isEqualTo(rst);
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4303() throws Exception
	{
		boolean testRst = false;
		String caseID = "4303";
		logger.info("====Verify the active edition can be deleted in Edition Manager window with warning message -dashboard,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();

			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstActiveEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			editionPage.deleteEdition(creationDate);

			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			assertThat(false).as("Deleted editions still in editions dropdown list").isEqualTo(formInstancePage.isEditionExist(editionInfo.replace("#", " #")));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4048() throws Exception
	{
		boolean testRst = false;
		String caseID = "4048";
		logger.info("====Verify the active edition can be deleted in Edition Manager window with warning message-return viewer page,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			EditionManagePage editionPage = formInstancePage.openEditionManage();
			String editionInfo = editionPage.getFirstActiveEdition();
			String creationDate = editionInfo.split("#")[0];

			formInstancePage.openEditionManage();
			editionPage.deleteEdition(creationDate);

			assertThat(false).as("Deleted edition still in editions drop list").isEqualTo(formInstancePage.isEditionExist(editionInfo.replace("#", " #")));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4050() throws Exception
	{
		boolean testRst = false;
		String caseID = "4303";
		logger.info("====Verify Verify ACTIVE edition can be set to DORMANT -dashboard,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstActiveEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			editionPage.deactivateForm(creationDate);

			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			assertThat(editionInfo).as("Dormant edition failed").isNotEqualTo(formInstancePage.getCurrentEditionInfo());
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4282() throws Exception
	{
		boolean testRst = false;
		String caseID = "4282";
		logger.info("====Verify DORMANT edition can be set to ACTIVE -return viewer page,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstDormantEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			editionPage.activateForm(creationDate);

			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			assertThat(formInstancePage.getCurrentEditionInfo().trim()).as("Active edition failed").isEqualTo(editionInfo.replace("#", " #"));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4051() throws Exception
	{
		boolean testRst = false;
		String caseID = "4051";
		logger.info("====Verify DORMANT edition is READ-ONLY,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");
			String CellName = getElementValueFromXML(testData_edition, nodeName, "CellName");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstDormantEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			formInstancePage = editionPage.openForm(creationDate);
			boolean rst = formInstancePage.isAddInstanceBtnDisplayed();
			assertThat(false).as("Add/Delete instance button is displayed").isEqualTo(rst);
			rst = formInstancePage.isImportAdjustmentEnabled();
			assertThat(false).as("Import adjustment button is enabled").isEqualTo(rst);
			rst = formInstancePage.isLockUnlockDisabled();
			assertThat(true).as("Lock/Unlock button is enabled").isEqualTo(rst);
			rst = formInstancePage.isCellEditable(CellName);
			assertThat(false).as("Cell value is editable").isEqualTo(rst);
			rst = formInstancePage.isValidationNowEnable();
			assertThat(false).as("ValidationNow button is enabled").isEqualTo(rst);
			rst = formInstancePage.isLiveValidationEnabled();
			assertThat(false).as("LiveValidation button is enabled").isEqualTo(rst);
			rst = formInstancePage.isExportToFileEnabled();
			assertThat(true).as("Export to file button is disabled").isEqualTo(rst);
			rst = formInstancePage.isApproveRejectDisplayed();
			assertThat(false).as("Approve/Reject is enabled").isEqualTo(rst);

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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4052() throws Exception
	{
		boolean testRst = false;
		String caseID = "4052";
		logger.info("====Verify ACTIVE edition that activated from DORMANT edition can work properly,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");
			String CellName = getElementValueFromXML(testData_edition, nodeName, "CellName");
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstDormantEdition();
			String creationDate = editionInfo.split("#")[0];

			listPage.openEditionManage(rowIndex);
			editionPage.activateForm(creationDate);
			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);

			boolean rst = formInstancePage.isAddInstanceBtnDisplayed();
			assertThat(true).as("Add/Delete instance button is not displayed").isEqualTo(rst);
			rst = formInstancePage.isImportAdjustmentEnabled();
			assertThat(true).as("Import adjustment button is disabled").isEqualTo(rst);
			rst = formInstancePage.isCellEditable(CellName);
			assertThat(true).as("Cell value edit is disabled").isEqualTo(rst);
			rst = formInstancePage.isValidationNowEnable();
			assertThat(true).as("ValidationNow button is disabled").isEqualTo(rst);
			rst = formInstancePage.isLiveValidationEnabled();
			assertThat(true).as("LiveValidation button is disabled").isEqualTo(rst);
			rst = formInstancePage.isReadyApproveDisplayed();
			assertThat(true).as("ReadyApprove button is not displayed").isEqualTo(rst);

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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4053() throws Exception
	{
		boolean testRst = false;
		String caseID = "4053";
		logger.info("====Verify user can view different editions via the Editions drop-down list,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, ReferenceDate, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			String editionInfo = editionPage.getFirstDormantEdition();

			formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			formInstancePage.selectEditionFomDropdownList(editionInfo.replace("#", " #"));

			String curEdition = formInstancePage.getCurrentEditionInfo();
			assertThat(curEdition).as(("Select edition from drop list failed")).isEqualTo(editionInfo.replace("#", " #"));
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4287() throws Exception
	{
		boolean testRst = false;
		String caseID = "4287";
		logger.info("====Verify user cannot delete or dormant the edition in Edition Manager window when there is only one edition exists,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			listPage.deleteFormInstance(Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);

			boolean rst = editionPage.isDeleteEditionEnabled();
			assertThat(false).isEqualTo(rst);
			rst = editionPage.isChangeEditionStateEnabled();
			assertThat(false).isEqualTo(rst);
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
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4300() throws Exception
	{
		boolean testRst = false;
		String caseID = "4300";
		logger.info("====Verify new edition will generate when import from EXCEL by replace mode in return list page,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");
			String fileName = getElementValueFromXML(testData_edition, nodeName, "ImportFile");
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			int initAmt = editionPage.getEditionAmt();
			File importFile = new File(new File(testData_edition).getParent() + "/ImportFile/" + fileName);
			formInstancePage = listPage.importAdjustment(importFile, false, false);
			formInstancePage.closeFormInstance();
			rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			editionPage = listPage.openEditionManage(rowIndex);
			int afterAmt = editionPage.getEditionAmt();
			assertThat(1).as("No edition was generated after import adjustment in list").isEqualTo(afterAmt - initAmt);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

	@Test
	public void test4301() throws Exception
	{
		boolean testRst = false;
		String caseID = "4301";
		logger.info("====Verify new edition will generate when import from EXCEL by replace mode in return viewer page,case[" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			String Regulator = getElementValueFromXML(testData_edition, nodeName, "Regulator");
			String Entity = getElementValueFromXML(testData_edition, nodeName, "Entity");
			String Form = getElementValueFromXML(testData_edition, nodeName, "Form");
			String ReferenceDate = getElementValueFromXML(testData_edition, nodeName, "ReferenceDate");
			String fileName = getElementValueFromXML(testData_edition, nodeName, "ImportFile");
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.closeFormInstance();
			int rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			EditionManagePage editionPage = listPage.openEditionManage(rowIndex);
			int initAmt = editionPage.getEditionAmt();
			File importFile = new File(new File(testData_edition).getParent() + "/ImportFile/" + fileName);
			formInstancePage = listPage.openFirstFormInstance();
			formInstancePage.importAdjustment(importFile, false, false);
			formInstancePage.closeFormInstance();
			rowIndex = listPage.getFormInstanceRowPos(formCode, version, ReferenceDate);
			editionPage = listPage.openEditionManage(rowIndex);
			int afterAmt = editionPage.getEditionAmt();
			assertThat(1).as("No edition was generated after import adjustment in list").isEqualTo(afterAmt - initAmt);
			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "FormEdition");
		}
	}

}
