package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.TestTemplate;

/**
 * Create by Leo Tu on 10/18/2016
 */

public class ReturnList extends TestTemplate
{

	@Test
	public void test6465() throws Exception
	{
		boolean testRst = false;
		String caseID = "6465";
		logger.info("====Verify the dashboard status is FAILED when there is error result no matter the level is Critical or Warning for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell1 = elementValues.get(5);
			String Status1 = elementValues.get(6);
			String EditCell2 = elementValues.get(7);
			String Status2 = elementValues.get(8);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			for (String fileName : CreateFile.split("#"))
			{
				File importFile = new File(importFolder + fileName);
				listPage.createFormFromExcel(importFile, false, false, false);
			}
			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);

			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			String cellName = EditCell1.split("#")[0];
			String value = EditCell1.split("#")[1].equalsIgnoreCase("null") ? null : EditCell1.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status1.split(",")[0]).as("Validation status should be " + Status1.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status1.split(",")[1]).as("XValidation status should be " + Status1.split(",")[1]).isEqualTo(detailInfo.get(8));

			formInstancePage = listPage.openFirstFormInstance();
			for (String edit : EditCell2.split("~"))
			{
				cellName = edit.split("#")[0];
				value = edit.split("#")[1].equalsIgnoreCase("null") ? null : edit.split("#")[1];
				formInstancePage.editCellValue(cellName, value);
			}
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status2.split(",")[0]).as("Validation status should be " + Status2.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status2.split(",")[1]).as("XValidation status should be " + Status2.split(",")[1]).isEqualTo(detailInfo.get(8));
			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}

	@Test
	public void test6466() throws Exception
	{
		boolean testRst = false;
		String caseID = "6466";
		logger.info("====Verify the dashboard status is PASSED when there ONLY is Warning level Fail result for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell = elementValues.get(5);
			String Status = elementValues.get(6);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			File importFile = new File(importFolder + CreateFile);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String cellName = EditCell.split("#")[0];
			String value = EditCell.split("#")[1].equalsIgnoreCase("null") ? null : EditCell.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);
			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status.split(",")[0]).as("Validation status should be " + Status.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status.split(",")[1]).as("XValidation status should be " + Status.split(",")[1]).isEqualTo(detailInfo.get(8));

			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}

	@Test
	public void test6467() throws Exception
	{
		boolean testRst = false;
		String caseID = "6467";
		logger.info("====Verify the dashboard status is FAILED when there is Critical level Fail result for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell = elementValues.get(5);
			String Status = elementValues.get(6);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			File importFile = new File(importFolder + CreateFile);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String cellName = EditCell.split("#")[0];
			String value = EditCell.split("#")[1].equalsIgnoreCase("null") ? null : EditCell.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);
			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status.split(",")[0]).as("Validation status should be " + Status.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status.split(",")[1]).as("XValidation status should be " + Status.split(",")[1]).isEqualTo(detailInfo.get(8));
			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}

	@Test
	public void test6468() throws Exception
	{
		boolean testRst = false;
		String caseID = "6468";
		logger.info("====Verify the dashboard status is FAILED when there is error result no matter the level is Critical or Warning for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell1 = elementValues.get(5);
			String Status1 = elementValues.get(6);
			String EditCell2 = elementValues.get(7);
			String Status2 = elementValues.get(8);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			File importFile = new File(importFolder + CreateFile);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String cellName = EditCell1.split("#")[0];
			String value = EditCell1.split("#")[1].equalsIgnoreCase("null") ? null : EditCell1.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);
			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status1.split(",")[0]).as("Validation status should be " + Status1.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status1.split(",")[1]).as("XValidation status should be " + Status1.split(",")[1]).isEqualTo(detailInfo.get(8));

			formInstancePage = listPage.openFirstFormInstance();
			for (String edit : EditCell2.split("~"))
			{
				cellName = edit.split("#")[0];
				value = edit.split("#")[1].equalsIgnoreCase("null") ? null : edit.split("#")[1];
				formInstancePage.editCellValue(cellName, value);
			}
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status2.split(",")[0]).as("Validation status should be " + Status2.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status2.split(",")[1]).as("XValidation status should be " + Status2.split(",")[1]).isEqualTo(detailInfo.get(8));
			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}

	@Test
	public void test6469() throws Exception
	{
		boolean testRst = false;
		String caseID = "6469";
		logger.info("====Verify the dashboard status is PASSED when there ONLY is Warning level Fail result for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell = elementValues.get(5);
			String Status = elementValues.get(6);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			File importFile = new File(importFolder + CreateFile);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String cellName = EditCell.split("#")[0];
			String value = EditCell.split("#")[1].equalsIgnoreCase("null") ? null : EditCell.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);
			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status.split(",")[0]).as("Validation status should be " + Status.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status.split(",")[1]).as("XValidation status should be " + Status.split(",")[1]).isEqualTo(detailInfo.get(8));

			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}

	@Test
	public void test6470() throws Exception
	{
		boolean testRst = false;
		String caseID = "6470";
		logger.info("====Verify the dashboard status is FAILED when there is Critical level Fail result for AR package[case id=" + caseID + "]====");
		try
		{
			String nodeName = "C" + caseID;
			List<String> elementValues = getElementValueFromXML(testData_ReturnList, nodeName);
			String Regulator = elementValues.get(0);
			String Entity = elementValues.get(1);
			String ReferenceDate = elementValues.get(2);
			String Form = elementValues.get(3);
			String CreateFile = elementValues.get(4);
			String EditCell = elementValues.get(5);
			String Status = elementValues.get(6);

			List<String> Files = createFolderAndCopyFile("ReturnList", null);
			String importFolder = Files.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			File importFile = new File(importFolder + CreateFile);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String cellName = EditCell.split("#")[0];
			String value = EditCell.split("#")[1].equalsIgnoreCase("null") ? null : EditCell.split("#")[1];
			formInstancePage.editCellValue(cellName, value);
			formInstancePage.validationNowClick();
			formInstancePage.closeFormInstance();

			listPage.setForm(Form);
			listPage.setProcessDate(ReferenceDate);
			List<String> detailInfo = listPage.getFormDetailInfo(1);
			assertThat(Status.split(",")[0]).as("Validation status should be " + Status.split(",")[0]).isEqualTo(detailInfo.get(7));
			assertThat(Status.split(",")[1]).as("XValidation status should be " + Status.split(",")[1]).isEqualTo(detailInfo.get(8));
			testRst = true;

		}
		catch (RuntimeException e)
		{
			// e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "ReturnList");
		}
	}
}
