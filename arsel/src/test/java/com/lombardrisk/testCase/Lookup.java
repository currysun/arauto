package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Created by leo tu on 3/21/2017.
 */
public class Lookup extends TestTemplate
{

	@Test
	public void test7038() throws Exception
	{
		boolean testRst = false;
		String caseID = "7038";
		logger.info("====Verify Manual edit lookup return for extended grid BGv4[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "BG v4";
			String ReferenceDate = "01/01/2017";
			String Page1 = "Outward";
			String Page2 = "Inward";
			String Page3 = "Fees and transfer pricing";
			String Insert1 = "ExtDBGrid1COUNTRYNAME1";

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.selectPage(Page1);
			int num = formInstancePage.getLookupAmt(Insert1);
			assertThat(num).isEqualTo(239);

			String text = formInstancePage.getLookupOption(Insert1, 2);
			formInstancePage.insertRowForLookup(Insert1, 2);

			String cellValue = formInstancePage.getCellText("COUNTRYNAME1");
			assertThat(cellValue).isEqualTo(text.split("-")[0].trim());

			cellValue = formInstancePage.getCellText("COUNTRY2");
			assertThat(cellValue).isEqualTo(text.split("-")[1].trim());

			cellValue = formInstancePage.getCellText("COUNTRY3");
			assertThat(cellValue).isEqualTo(text.split("-")[2].trim());

			cellValue = formInstancePage.getCellText("C13B");
			assertThat(cellValue).isEqualTo("101.55");

			boolean rst = formInstancePage.isCellEditable("COUNTRYNAME1");
			refreshPage();
			formInstancePage.selectPage(Page1);
			assertThat(rst).isEqualTo(false);

			rst = formInstancePage.isCellEditable("COUNTRY2");
			refreshPage();
			formInstancePage.selectPage(Page1);
			assertThat(rst).isEqualTo(false);

			rst = formInstancePage.isCellEditable("C1");
			refreshPage();
			formInstancePage.selectPage(Page1);
			assertThat(rst).isEqualTo(true);

			formInstancePage.selectPage(Page2);
			cellValue = formInstancePage.getCellText("ExtDBGrid2COUNTRYNAME2");
			assertThat(cellValue).isEqualTo("NULL");

			formInstancePage.selectPage(Page3);
			cellValue = formInstancePage.getCellText("ExtDBGrid3COUNTRYNAME3");
			assertThat(cellValue).isEqualTo("NULL");
			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

	@Test
	public void test7022() throws Exception
	{
		boolean testRst = false;
		String caseID = "7022";
		logger.info("====Verify Import lookup return for extended grid_Key type is N[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "BG v4";
			String ReferenceDate = "01/01/2017";
			String fileName = "BG_V4_1000_20160301_File1.xlsx";
			String Page1 = "Inward";

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + fileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false, false);
			formInstancePage.selectPage(Page1);

			// formInstancePage.editCellValue("ExtDBGrid249C7","123.12");
			formInstancePage.editCellValue("ExtDBGrid249C8", "124.12");
			formInstancePage.editCellValue("ExtDBGrid249C14", "-2009.56");

			// formInstancePage.editCellValue("ExtDBGrid250C7","-2010.56");
			formInstancePage.editCellValue("ExtDBGrid250C8", "-2009.56");
			formInstancePage.editCellValue("ExtDBGrid250C14", "124.12");

			// formInstancePage.editCellValue("ExtDBGrid251C7","30000");
			formInstancePage.editCellValue("ExtDBGrid251C8", "99999");
			formInstancePage.editCellValue("ExtDBGrid251C14", "1111");

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C5", null, "1");
			assertThat(text).isEqualTo(
					"testforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlentestforlen0123456789abcde");
			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C7", null, "1");
			assertThat(text).isEqualTo("123.12");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C8", null, "2");
			assertThat(text).isEqualTo("-2009.56");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C14", null, "3");
			assertThat(text).isEqualTo("1111");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

	@Test
	public void test7023() throws Exception
	{
		boolean testRst = false;
		String caseID = "7023";
		logger.info("====Verify Retrieve lookup return for extended grid BGv4[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "BG v4";
			String ReferenceDate = "31/12/2017";
			String Page = "Fees and transfer pricing";

			String formCode = "BG";
			String version = "4";

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			retrieveForm(listPage, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ReferenceDate);
			formInstancePage.selectPage(Page);

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "1");
			assertThat(text).isEqualTo("1/3/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "2");
			assertThat(text).isEqualTo("1/12/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "3");
			assertThat(text).isEqualTo("2/8/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "4");
			assertThat(text).isEqualTo("2/25/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "5");
			assertThat(text).isEqualTo("4/20/2017");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

	@Test
	public void test7126() throws Exception
	{
		boolean testRst = false;
		String caseID = "7126";
		logger.info("====Verify Export lookup return for extended grid CL v2[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "CL v2";
			String ReferenceDate = "10/10/2016";
			String fileName = "CL_v2_20171002.csv";
			String fileName2 = "Step8.xlsx";
			String Page1 = "P1";
			String Delete1 = "ExtDBGrid1-1357076128COUNTRYNAME";
			String Delete2 = "ExtDBGrid1-1691889586COUNTRYNAME";

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + fileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false, false);
			formInstancePage.selectPage(Page1);
			formInstancePage.deleteRow(Delete1);
			formInstancePage.deleteRow(Delete2);

			importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + fileName2);
			formInstancePage.importAdjustment(importFile, false, false, false);

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "COUNTRY2", null, "1");
			assertThat(text).isEqualTo("CR");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "COUNTRY2", null, "2");
			assertThat(text).isEqualTo("UK");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "COUNTRY2", null, "3");
			assertThat(text).isEqualTo("USA");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "1");
			assertThat(text).isEqualTo("-998.65");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "2");
			assertThat(text).isEqualTo("-999.65");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "3");
			assertThat(text).isEqualTo("-1000.65");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1BAF", null, "1");
			assertThat(text).isEqualTo("1010");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1BAF", null, "2");
			assertThat(text).isEqualTo("1010");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1BAF", null, "3");
			assertThat(text).isEqualTo("-100");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AAA", null, "1");
			assertThat(text).isEqualTo("100");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AAA", null, "2");
			assertThat(text).isEqualTo("10000");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AAA", null, "3");
			assertThat(text).isEqualTo("-300");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

	@Test
	public void test7200() throws Exception
	{
		boolean testRst = false;
		String caseID = "7200";
		logger.info("====Verify Import lookup return for extended grid_Key type is C[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "CL v2";
			String ReferenceDate = "06/01/2016";
			String fileName = "CL_V2_1000_20160106_File1.xlsx";
			String Page1 = "P1";
			String Page2 = "P2";
			String Page3 = "P6";

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + fileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false, false);
			formInstancePage.selectPage(Page2);

			formInstancePage.selectPage(Page3);
			formInstancePage.editCellValue("ExtDBGrid1-771733562CL1ALA", "0");
			formInstancePage.editCellValue("ExtDBGrid1-771733562CL1BLF", "0");

			formInstancePage.editCellValue("ExtDBGrid1-1691889586CL1ALA", "12");
			formInstancePage.editCellValue("ExtDBGrid1-1691889586CL1BLF", "10");

			formInstancePage.importAdjustment(importFile, true, false, false);

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1BLF", null, "3");
			assertThat(text).isEqualTo("1010");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1ALA", null, "3");
			assertThat(text).isEqualTo("1200");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1BLF", null, "2");
			assertThat(text).isEqualTo("1020");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1ALA", null, "2");
			assertThat(text).isEqualTo("1212");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "1");
			assertThat(text).isEqualTo("-998.65");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "2");
			assertThat(text).isEqualTo("-999.65");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "CL1AA", null, "3");
			assertThat(text).isEqualTo("-1000.65");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

	@Test
	public void test7223() throws Exception
	{
		boolean testRst = false;
		String caseID = "7223";
		logger.info("====Verify Import lookup return for extended grid_Key numbering is I[case id=" + caseID + "]====");
		try
		{
			String Regulator = "US1 FED Reserve DS";
			String Entity = "0001";
			String Form = "BG v4";
			String ReferenceDate = "31/12/2017";
			String Page = "Fees and transfer pricing";
			String CreateFileName = "BG_V4_1000_20160301_File1.xlsx";

			String formCode = "BG";
			String version = "4";

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + CreateFileName);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false, false);
			formInstancePage.selectPage(Page);

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "1");
			assertThat(text).isEqualTo("1/3/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "2");
			assertThat(text).isEqualTo("1/12/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "3");
			assertThat(text).isEqualTo("2/8/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "4");
			assertThat(text).isEqualTo("2/25/2017");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C12", null, "5");
			assertThat(text).isEqualTo("4/20/2017");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
		}
	}

}
