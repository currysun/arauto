package com.lombardrisk.testCase_TS;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;
import com.lombardrisk.utils.fileService.FileUtil;
import com.lombardrisk.utils.fileService.TxtUtil;
import com.lombardrisk.utils.fileService.XMLUtil;

/**
 * Created by leo tu on 12/29/2016.
 */
public class Export_Format extends TestTemplate
{

	@Test
	public void test6628() throws Exception
	{
		String caseID = "6628";
		logger.info("====Verify date and time stamp is added when export toolset return to excel file and date type cell format is specified[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_Format, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, ReferenceDate);

			FormInstancePage formInstancePage = listPage.openFirstFormInstance();
			String exportFilePath = formInstancePage.exportToFile(null, null, null, "excel", null, null);
			String exportTime = ExcelUtil.getCellValueByCellName(new File(exportFilePath), "_ExportTime");
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			String currentDate = df.format(new Date());
			if (currentDate.startsWith("0"))
				currentDate = currentDate.substring(1);
			if (currentDate.contains("/0"))
				currentDate = currentDate.replace("/0", "/");
			assertThat(exportTime).contains(currentDate);
			String timeZone = ExcelUtil.getCellValueByCellName(new File(exportFilePath), "_TimeZone");
			assertThat(timeZone).isEqualTo("UTC+08:00");

			testRst = true;
		}
		catch (RuntimeException e)
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
	public void test6634() throws Exception
	{
		String caseID = "6634";
		logger.info("==== Verify date and time stamp is added when export toolset return to vanilla file[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_Format, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String Module = testData.get(4);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "vanilla", Module, null);
			String exportTime = XMLUtil.getCellValueFromVanilla(exportFilePath, "exportTime", null);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String currentDate = df.format(new Date());
			String pattern = "^" + currentDate + "T\\d{2}:\\d{2}:\\d{2}\\+0800";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(exportTime);
			assertThat(true).isEqualTo(m.matches());
			testRst = true;
		}
		catch (RuntimeException e)
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
	public void test6703() throws Exception
	{
		String caseID = "6703";
		logger.info("==== Verify date and time stamp is not added when timezone and exporttime are not defined with toolset[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_Format, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);

			ListPage listPage = m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);

			FormInstancePage formInstancePage = listPage.createNewForm(Entity, ReferenceDate, Form, null, false, false);
			String exportFilePath = formInstancePage.exportToFile(null, null, null, "Excel", null, null);
			List<String> names = ExcelUtil.getCellNamesFromExcel(new File(exportFilePath));
			assertThat("_ExportTime").isNotIn(names);
			testRst = true;
		}
		catch (RuntimeException e)
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
	public void test6704() throws Exception
	{
		String caseID = "6704";
		logger.info("====Test...[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_Export_Format, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);
			String Module = testData.get(5);

			File importFile = new File(testData_Export_Format.replace("ExportFormat.xml", "ImportFile") + "/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			listPage.deleteFormInstance(Form,ReferenceDate);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			formInstancePage.lockClick();
			formInstancePage.validationNowClick();
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "text(MAS_MAS1111IntCod)", Module, null);
			assertThat(true).isEqualTo(exportFilePath.endsWith(".zip"));
			String saveFolderString = new File(exportFilePath).getParent();
			List<String> files = FileUtil.unCompress(exportFilePath, saveFolderString);
			assertThat(files).contains("report_USD_EXPORT.xml");
			assertThat(files).contains("report_IDR_EXPORT.xml");
			for (String file : files)
			{
				if (file.endsWith("report_USD_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("12345____");
					assertThat(content).contains("123456____");
					assertThat(content).contains("123____");
					assertThat(content).contains("12____");
					assertThat(content).contains("1____");
				}
				if (file.endsWith("report_IDR_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("1234567____");
					assertThat(content).contains("123____");
					assertThat(content).contains("12____");
					assertThat(content).contains("1____");
				}

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
			writeTestResultToFile(caseID + ",6707", testRst, "Export");
		}
	}

}
