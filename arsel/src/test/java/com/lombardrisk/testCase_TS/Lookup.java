package com.lombardrisk.testCase_TS;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.testng.annotations.Test;

import com.lombardrisk.pages.ComputePage;
import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Created by leo tu on 3/21/2017.
 */
public class Lookup extends TestTemplate
{

	@Test
	public void test6975() throws Exception
	{
		boolean testRst = false;
		String caseID = "6975";
		logger.info("====Verify compute return LookupType can work well when lookup field is null-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "BG v4";
			String ReferenceDate = "30/03/2016";

			ListPage listPage = super.m.listPage;
			listPage.setRegulator(Regulator);
			ComputePage computePage = listPage.enterComputePage();
			FormInstancePage formInstancePage = computePage.computeReturn(Entity, ReferenceDate, Form, false);
			formInstancePage.selectPage("P1");
			String text = formInstancePage.getCellText("TOT1");
			assertThat(text).isEqualTo("10,703");

			text = formInstancePage.getCellText("TOT3");
			assertThat(text).isEqualTo("10,883");

			text = formInstancePage.getCellText("TOT4");
			assertThat(text).isEqualTo("371");

			text = formInstancePage.getCellText("EO13A");
			assertThat(text).isEqualTo("370");

			text = formInstancePage.getCellText("CB13B");
			assertThat(text).isEqualTo("132");

			text = formInstancePage.getCellText("ExtDBGrid1-782859656COUNTRY2");
			assertThat(text).isEqualTo("AI");

			text = formInstancePage.getCellText("ExtDBGrid1-782859656COUNTRY");
			assertThat(text).isEqualTo("ABUD");

			text = formInstancePage.getCellText("ExtDBGrid1-782859656C3");
			assertThat(text).isEqualTo("501");

			formInstancePage.selectPage("P2");
			text = formInstancePage.getCellText("ExtDBGrid1-782859656COUNTRY");
			assertThat(text).isEqualTo("ABUD");

			text = formInstancePage.getCellText("ExtDBGrid1-782859656C5");
			assertThat(text).isEqualTo("2,676");

			formInstancePage.selectPage("P3");
			text = formInstancePage.getCellText("ExtDBGrid1-782859656C11");
			assertThat(text).isEqualTo("-207");

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
	public void test6988() throws Exception
	{
		boolean testRst = false;
		String caseID = "6988";
		logger.info("====Verify import from csv LookupType can work well when lookup field is null BS T and DT S-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "CL v2";
			String ReferenceDate = "30/11/2016";
			String fileName = "HKMA_1_CL_v2_20161130.csv";

			updateDB("T", "S");
			reStartBrowser();

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + fileName);
			ListPage listPage = super.m.listPage;

			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.importAdjustment(importFile, false, false, false);
			formInstancePage.selectPage("P1");
			String text = formInstancePage.getCellText("ExtDBGrid1-782859656COUNTRYNAME");
			assertThat(text).isEqualTo("Abu Dhabi");

			text = formInstancePage.getCellText("ExtDBGrid1-1252611147COUNTRYNAME");
			assertThat(text).isEqualTo("Romania");

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
	public void test6982() throws Exception
	{
		boolean testRst = false;
		String caseID = "6982";
		logger.info("====Verify manual edit return LookupType can work well BS is T and DT is C-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "CL v2";
			String ReferenceDate = "30/12/2015";

			updateDB("T", "C");
			reStartBrowser();

			ListPage listPage = super.m.listPage;

			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.selectPage("P1");
			formInstancePage.insertRowForLookup("ExtDBGrid1COUNTRYNAME", "Uruguay");
			String text = formInstancePage.getCellText("ExtDBGrid11503360766COUNTRY2");
			assertThat(text).isEqualTo("UY");

			formInstancePage.selectPage("P3");
			text = formInstancePage.getCellText("ExtDBGrid11503360766COUNTRY2");
			assertThat(text).isEqualTo("UY");
			formInstancePage.insertRowForLookup("ExtDBGrid11503360766COUNTRYNAME", "Somalia");

			text = formInstancePage.getCellText("ExtDBGrid1-365109388COUNTRYNAME");
			assertThat(text).isEqualTo("Somalia");

			text = formInstancePage.getCellText("ExtDBGrid11503360766COUNTRYNAME");
			assertThat(text).isEqualTo("Uruguay");

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
	public void test7009() throws Exception
	{
		boolean testRst = false;
		String caseID = "7009";
		logger.info("====Verify export to excel lookup fields also can be computed-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "BG v4";
			String ReferenceDate = "30/03/2016";

			updateDB("F", "");
			reStartBrowser();

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);
			FormInstancePage formInstancePage = listPage.openFirstFormInstance();

			String exportedFile = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "excel", null, null);
			String text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___TOT1", null, null);
			assertThat(text).isEqualTo("10703");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___TOT13A", null, null);
			assertThat(text).isEqualTo("883");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___CB13B", null, null);
			assertThat(text).isEqualTo("132");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "COUNTRY2", null, "1");
			assertThat(text).isEqualTo("AI");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "COUNTRY2", null, "2");
			assertThat(text).isEqualTo("AA");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C5", null, "2");
			assertThat(text).isEqualTo("0");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C5", null, "1");
			assertThat(text).isEqualTo("2676");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C9", null, "2");
			assertThat(text).isEqualTo("206");

			text = ExcelUtil.getCellValueByCellName(new File(exportedFile), "___C9", null, "1");
			assertThat(text).isEqualTo("0");

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
	public void test6977() throws Exception
	{
		boolean testRst = false;
		String caseID = "6977";
		logger.info("====Verify more than one selection fileds LookupType also can work well BS is F-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "BG v4";
			String ReferenceDate = "10/10/2016";

			updateDB("F", "D");
			reStartBrowser();

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.selectPage("P1");
			formInstancePage.insertRowForLookup("ExtDBGrid1COUNTRYNAME", "Uruguay");
			String text = formInstancePage.getCellText("ExtDBGrid11503360766COUNTRYNAME");
			assertThat(text).isEqualTo("Uruguay");

			text = formInstancePage.getCellText("ExtDBGrid11503360766COUNTRY2");
			assertThat(text).isEqualTo("UY");

			text = formInstancePage.getCellText("ExtDBGrid11503360766C3");
			assertThat(text).isEqualTo("727");

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
	public void test6992() throws Exception
	{
		boolean testRst = false;
		String caseID = "6992";
		logger.info("====error message should be pop up when manual input duplicate grid key-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "CL v2";
			String ReferenceDate = "30/01/2015";

			updateDB("T", "T");
			reStartBrowser();

			ListPage listPage = super.m.listPage;

			listPage.getProductListPage(Regulator, Entity, Form, null);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			formInstancePage.selectPage("P1");
			formInstancePage.insertRowForLookup("ExtDBGrid1COUNTRYNAME", "Uruguay");
			String message = formInstancePage.insertRowForLookup("ExtDBGrid11503360766COUNTRYNAME", "Uruguay");
			assertThat(message).isEqualTo("row key Uruguay already exist in grid ExtDBGrid1, No duplicate rows are allowed.");
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
	public void test6993() throws Exception
	{
		boolean testRst = false;
		String caseID = "6993";
		logger.info("====Verify compute return error message should be pop up when lookup table not exist-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "CL v2";
			String ReferenceDate = "30/06/2016";

			updateDB("T", "T");
			reStartBrowser();

			String sql = "Drop table T_COUNTRY_301215_REG_HKMA2004";
			DBQuery.queryRecord(2,sql); //drop table T_COUNTRY_301215_REG_HKMA2004 at first in AR_5122HKMA_SYSTEM1
			ListPage listPage = super.m.listPage;
			listPage.setRegulator(Regulator);
			ComputePage computePage = listPage.enterComputePage();
			String message = computePage.getErrorMessage(Entity, ReferenceDate, Form);
			assertThat(message).isEqualTo("Lookup table T_COUNTRY_301215_REG_HKMA2004 not found.");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
			String sql = "create table T_COUNTRY_301215_REG_HKMA2004 as select * from T_COUNTRY_010115_REG_HKMA2004";
			DBQuery.queryRecord(2,sql); //resotre table T_COUNTRY_301215_REG_HKMA2004 at first in AR_5122HKMA_SYSTEM1
		}
	}

	@Test
	public void test7206() throws Exception
	{
		boolean testRst = false;
		String caseID = "7206";
		logger.info("====Verify import from excel error message pops up when field value not exsit in lookup table-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "BG v4";
			String ReferenceDate = "30/10/2016";

			updateDB("F", "");
			reStartBrowser();
			String sql = "Drop table T_COUNTRY_301215_REG_HKMA2004";
			DBQuery.queryRecord(2,sql); //drop table T_COUNTRY_301215_REG_HKMA2004 at first in AR_5122HKMA_SYSTEM1
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			String message = listPage.getCreateNewFormErrorMsg(Entity, ReferenceDate, Form, null, false, false);
			assertThat(message).isEqualTo("Lookup table T_COUNTRY_301215_REG_HKMA2004 not found.");

			testRst = true;
		}
		catch (RuntimeException e)
		{
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "Lookup");
			String sql = "create table T_COUNTRY_301215_REG_HKMA2004 as select * from T_COUNTRY_010115_REG_HKMA2004";
			DBQuery.queryRecord(2,sql); //resotre table T_COUNTRY_301215_REG_HKMA2004 at first in AR_5122HKMA_SYSTEM1
		}
	}

	@Test
	public void test7208() throws Exception
	{
		boolean testRst = false;
		String caseID = "6975";
		logger.info("====Verify import from csv error message pops up should be pop up when duplicate grid key-external[case id=" + caseID + "]====");
		try
		{
			String Regulator = "Hong Kong Monetary Authority";
			String Entity = "1";
			String Form = "CL v2";
			String ReferenceDate = "30/01/2015";
			String FileName = "HKMA_1_CL_v2_20160630_dup.csv";

			updateDB("T", "S");
			reStartBrowser();

			File importFile = new File(System.getProperty("user.dir") + "/" + testDataFolderName + "/Lookup/ImportFile/" + FileName);
			ListPage listPage = super.m.listPage;
			listPage.setRegulator(Regulator);
			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			String errorInfo = formInstancePage.getImportAdjustmentErrorInfo(importFile);
			logger.info(errorInfo);
			assertThat(errorInfo).contains("Detail: Grid row key duplicated.");
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

	private void updateDB(String BS, String DT) throws Exception
	{
		int dbIndex = 2;
		String SQL = "";
		if (BS.equalsIgnoreCase("T") && DT.equalsIgnoreCase("T"))
			SQL = "UPDATE \"HKMAGridKey\" \n" + "SET \n"
					+ "\"SourceTableName\"='COUNTRY',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"=NULL ,\"DestField2\"=NULL,\"SourceField3\"=NULL ,\"DestField3\"=NULL,\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='T',\"BatchSelected\"='T'\n"
					+ "where \"ReturnId\"=70021";
		else if (BS.equalsIgnoreCase("T") && DT.equalsIgnoreCase("S"))
		{
			if (T_DBType.equalsIgnoreCase("sqlserver"))
				SQL = "UPDATE \"HKMAGridKey\" \n" + "SET "
						+ "\"SourceTableName\"='COUNTRY',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"=NULL ,\"DestField2\"=NULL,\"SourceField3\"=NULL ,\"DestField3\"=NULL,\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='S',\"ProcDate\"='2015-01-01',\"BatchSelected\"='T' "
						+ "where \"ReturnId\"=70021";
			else
				SQL = "UPDATE \"HKMAGridKey\" \n" + "SET "
						+ "\"SourceTableName\"='COUNTRY',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"=NULL ,\"DestField2\"=NULL,\"SourceField3\"=NULL ,\"DestField3\"=NULL,\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='S',\"ProcDate\"='01-JAN-15',\"BatchSelected\"='T' "
						+ "where \"ReturnId\"=70021";
		}
		else if (BS.equalsIgnoreCase("T") && DT.equalsIgnoreCase("C"))
			SQL = "UPDATE \"HKMAGridKey\"\n" + "SET \n"
					+ "\"SourceTableName\"='COUNTRY',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"=NULL ,\"DestField2\"=NULL,\"SourceField3\"=NULL ,\"DestField3\"=NULL,\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='C',\"BatchSelected\"='T'\n"
					+ "where \"ReturnId\"=70021";
		else if (BS.equalsIgnoreCase("F") && DT.equalsIgnoreCase("D"))
			SQL = "UPDATE \"HKMAGridKey\"\n" + "SET \n"
					+ "\"SourceTableName\"='T_COUNTRY_301215_REG_HKMA2004',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRY,COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"='HKMATDATE' ,\"DestField2\"='C1',\"SourceField3\"='HKACCAMNT' ,\"DestField3\"='C3',\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='T',\"BatchSelected\"='F'\n"
					+ "where \"ReturnId\"=250045";
		else if (BS.equalsIgnoreCase("F"))
			SQL = "UPDATE \"HKMAGridKey\"\n" + "SET \n"
					+ "\"SourceTableName\"='T_COUNTRY_301215_REG_HKMA2004',\"SourceTableAlias\"='STB System HKMA',\"DisplayText\"='COUNTRYNAME',\"SourceField1\"='COUNTRY2' ,\"DestField1\"='COUNTRY2',\"SourceField2\"='HKMATDATE' ,\"DestField2\"='C1',\"SourceField3\"='HKACCAMNT' ,\"DestField3\"='C3',\"BatchId\"='REG',\"Chapter\"='HKMA2004',\"DateType\"='T',\"BatchSelected\"='F'\n"
					+ "where \"ReturnId\"=250045";
		else
		{
		}
		DBQuery.update(dbIndex, SQL);
	}

}
