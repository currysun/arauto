package com.lombardrisk.testCase_TS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.CsvUtil;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Created by zhijun dai on 12/5/2016.
 */
public class AdjustmentsTest extends TestTemplate
{

	@Test
	public void test6511() throws Exception
	{
		String caseID = "6511";
		logger.info("====DR-01-06 Verify filter centre works well on Rejections Report. [case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String[] filters = testdata.get(6).split("#");
			String fileName = testdata.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			AdjustLogPage adjustLogPage = formInstancePage.getDrillDown();
			RejectionsPage rejectionsPage = adjustLogPage.enterRejectionsPage();
			rejectionsPage.setFilters(filters);
			String filePath = rejectionsPage.exportRejectionsLog();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);
		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6540() throws Exception
	{
		String caseID = "6540";
		logger.info("====Verify filter centre works well with Date. [case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String[] filters = testdata.get(6).split("#");
			String fileName = testdata.get(7);
			String[] filters2 = testdata.get(8).split("#");
			String fileName2 = testdata.get(9);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			AdjustLogPage adjustLogPage = formInstancePage.getDrillDown();
			RejectionsPage rejectionsPage = adjustLogPage.enterRejectionsPage();
			rejectionsPage.setFilters(filters);
			String filePath = rejectionsPage.exportRejectionsLog();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);

			rejectionsPage.setFilters(filters2);
			String filePath2 = rejectionsPage.exportRejectionsLog();
			boolean flag2 = CsvUtil.compareCSV(new File(testFolder + fileName2), new File(filePath2));
			Assert.assertTrue(flag2);

		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6515() throws Exception
	{
		String caseID = "6515";
		logger.info("====Verify exported report contain filter message.[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String pageName = testdata.get(6);
			String cellName = testdata.get(7);
			String[] filters = "STBIMPDATE,=,15/07/2015#STBIMPINDEX,<,605".split("#");
			String fileName = testdata.get(8);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			allocationPage.setFilters(filters, null);
			String filePath = allocationPage.exportAllocation();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);
		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6539() throws Exception
	{
		String caseID = "6539";
		logger.info("====Verify filter centre works well with varchar. [case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String[] filters = testdata.get(6).split("#");
			String fileName = testdata.get(7);
			String[] filters2 = testdata.get(8).split("#");
			String fileName2 = testdata.get(9);
			String[] filters3 = testdata.get(10).split("#");
			String fileName3 = testdata.get(11);
			String[] filters4 = testdata.get(12).split("#");
			String fileName4 = testdata.get(13);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			AdjustLogPage adjustLogPage = formInstancePage.getDrillDown();
			AllocationPage allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters, "true");
			String filePath = allocationPage.exportAllocation();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters2, "true");
			String filePath2 = allocationPage.exportAllocation();
			boolean flag2 = CsvUtil.compareCSV(new File(testFolder + fileName2), new File(filePath2));
			Assert.assertTrue(flag2);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters3, "true");
			String filePath3 = allocationPage.exportAllocation();
			boolean flag3 = CsvUtil.compareCSV(new File(testFolder + fileName3), new File(filePath3));
			Assert.assertTrue(flag3);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters4, "true");
			String filePath4 = allocationPage.exportAllocation();
			boolean flag4 = CsvUtil.compareCSV(new File(testFolder + fileName4), new File(filePath4));
			Assert.assertTrue(flag4);
		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6538() throws Exception
	{
		String caseID = "6538";
		logger.info("====Verify filter centre works well with Number.[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String[] filters = "STBDrillRef,=,524".split("#");
			String fileName = testdata.get(6);
			String[] filters2 = "STBDrillRef,<,524".split("#");
			String fileName2 = testdata.get(7);
			String[] filters3 = "STBDrillRef,>,524".split("#");
			String fileName3 = testdata.get(8);
			String[] filters4 = "STBDrillRef,>=,524".split("#");
			String fileName4 = testdata.get(9);
			String[] filters5 = "STBDrillRef,<=,524".split("#");
			String fileName5 = testdata.get(10);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			AdjustLogPage adjustLogPage = formInstancePage.getDrillDown();
			AllocationPage allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters, "true");
			String filePath = allocationPage.exportAllocation();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters2, "true");
			String filePath2 = allocationPage.exportAllocation();
			boolean flag2 = CsvUtil.compareCSV(new File(testFolder + fileName2), new File(filePath2));
			Assert.assertTrue(flag2);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters3, "true");
			String filePath3 = allocationPage.exportAllocation();
			boolean flag3 = CsvUtil.compareCSV(new File(testFolder + fileName3), new File(filePath3));
			Assert.assertTrue(flag3);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters4, "true");
			String filePath4 = allocationPage.exportAllocation();
			boolean flag4 = CsvUtil.compareCSV(new File(testFolder + fileName4), new File(filePath4));
			Assert.assertTrue(flag4);

			adjustLogPage = formInstancePage.getDrillDown();
			allocationPage = adjustLogPage.enterAllocationPage();
			allocationPage.setFilters(filters5, "true");
			String filePath5 = allocationPage.exportAllocation();
			boolean flag5 = CsvUtil.compareCSV(new File(testFolder + fileName5), new File(filePath5));
			Assert.assertTrue(flag5);
		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6513() throws Exception
	{
		String caseID = "6513";
		logger.info("====Verify cell drilldown filter works well on Allocations Report.[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String pageName = testdata.get(6);
			String cellName = testdata.get(7);
			String[] filters = testdata.get(8).split("#");
			String fileName = testdata.get(9);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			formInstancePage.selectPage(pageName);
			AllocationPage allocationPage = formInstancePage.cellDoubleClick(cellName);
			allocationPage.setFilters(filters, null);
			String filePath = allocationPage.exportAllocation();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);

		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6787() throws Exception
	{
		String caseID = "6787";
		logger.info("====Verify Rejections Report can be viewed and exported when table is REJECTION and drill down.[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String fileName = testdata.get(6);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			AdjustLogPage adjustLogPage = formInstancePage.getDrillDown();
			RejectionsPage rejectionsPage = adjustLogPage.enterRejectionsPage();
			String filePath = rejectionsPage.exportRejectionsLog();
			String testFolder = System.getProperty("user.dir") + "\\" + testDataFolderName + "\\AdjustmentsTest\\";
			boolean flag = CsvUtil.compareCSV(new File(testFolder + fileName), new File(filePath));
			Assert.assertTrue(flag);

		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}

	@Test
	public void test6825() throws Exception
	{
		String caseID = "6825";
		logger.info("==== Verify the X-validation rule works well when cross return exists.[case id=" + caseID + "]====");
		boolean testRst = true;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testdata = getElementValueFromXML(testData_adjustmentsTest, nodeName);
			String regulator = testdata.get(0);
			String entity = testdata.get(1);
			String form = testdata.get(2);
			String formCode = testdata.get(3);
			String version = testdata.get(4);
			String referenceDate = testdata.get(5);
			String value = testdata.get(6);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(regulator, entity, form, null);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			ValidationPage validationPage = formInstancePage.enterValidation(true);
			String filePath = validationPage.exportValidationLog();
			ArrayList<String> list = ExcelUtil.getRowValueFromExcel(new File(filePath), "validation", 274);
			Assert.assertTrue(list.contains(value));
		}
		catch (Throwable e)
		{
			testRst = false;
			e.printStackTrace();
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "AdjustmentsTest");
		}
	}
}
