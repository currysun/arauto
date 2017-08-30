package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.lombardrisk.pages.FormInstancePage;
import com.lombardrisk.pages.ListPage;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.FileUtil;
import com.lombardrisk.utils.fileService.TxtUtil;

/**
 * Created by leo tu on 12/29/2016.
 */
public class ExportForm2 extends TestTemplate
{

	@Test
	public void test6682() throws Exception
	{
		String caseID = "6682";
		logger.info("====Test...[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_ExportForm2, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);
			String Module = testData.get(5);

			File importFile = new File(testData_ExportForm2.replace("ExportForm2.xml", "ImportFile") + "/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "text(RC-FRY9CIntCod)", Module, null);
			assertThat(true).isEqualTo(exportFilePath.endsWith(".zip"));
			String saveFolderString = new File(exportFilePath).getParent();
			List<String> files = FileUtil.unCompress(exportFilePath, saveFolderString);
			assertThat(files).contains("report_AFS_EXPORT.xml");
			assertThat(files).contains("report_HTM_EXPORT.xml");
			assertThat(files).contains("report_JPY_EXPORT.xml");
			assertThat(files).contains("report_RMB_EXPORT.xml");
			assertThat(files).contains("report_USD_EXPORT.xml");
			for (String file : files)
			{
				if (file.endsWith("AFS_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("AFS(Sch HI(3 of 4))");
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
				}
				if (file.endsWith("HTM_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("HTM(Sch HI(3 of 4))");
					assertThat(content).contains("HTM(Sch HI(4 of 4))");
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
				}
				if (file.endsWith("JPY_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("test123");
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
					assertThat(content).contains("234000");
				}
				if (file.endsWith("RMB_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("test123");
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
					assertThat(content).contains("345000");
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
			writeTestResultToFile(caseID + ",6686", testRst, "Export");
		}
	}

	@Test
	public void test6683() throws Exception
	{
		String caseID = "6683";
		logger.info("====Test...[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_ExportForm2, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate1 = testData.get(3);
			String ReferenceDate2 = testData.get(4);
			String ImportFile1 = testData.get(5);
			String ImportFile2 = testData.get(6);
			String Module = testData.get(7);

			ListPage listPage = super.m.listPage;
			File importFile = new File(testData_ExportForm2.replace("ExportForm2.xml", "ImportFile") + "/" + ImportFile1);
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate1, "text(RC-FRY9CIntCod)", Module, null);
			assertThat(true).isEqualTo(exportFilePath.endsWith(".zip"));
			String saveFolderString = new File(exportFilePath).getParent();
			for (String file : FileUtil.unCompress(exportFilePath, saveFolderString))
			{
				if (file.endsWith("AFS_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("1235000");
					assertThat(content).contains("12346000");
					assertThat(content).contains("123000");
				}
				if (file.endsWith("RMB_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("1235000");
					assertThat(content).contains("12346000");
					assertThat(content).contains("123000");
				}
			}

			formInstancePage.closeFormInstance();

			importFile = new File(testData_ExportForm2.replace("ExportForm2.xml", "ImportFile") + "/" + ImportFile2);
			listPage.getProductListPage(Regulator, Entity, null, null);
			formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate2, "text(RC-FRY9CIntCod)", Module, null);
			assertThat(true).isEqualTo(exportFilePath.endsWith(".zip"));
			saveFolderString = new File(exportFilePath).getParent();
			for (String file : FileUtil.unCompress(exportFilePath, saveFolderString))
			{
				if (file.endsWith("AFS_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("AFS(Sch HI(3of4))");
					assertThat(content).contains("123000");
				}
				if (file.endsWith("HTM_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("1235000");
				}
				if (file.endsWith("RMB_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat("12").isNotIn(content);
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
			writeTestResultToFile(caseID, testRst, "Export");
		}
	}

	@Test
	public void test6687() throws Exception
	{
		String caseID = "6687";
		logger.info("====Test...[case id=" + caseID + "]====");
		boolean testRst = false;
		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_ExportForm2, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String ImportFile = testData.get(4);
			String Module = testData.get(5);

			File importFile = new File(testData_ExportForm2.replace("ExportForm2.xml", "ImportFile") + "/" + ImportFile);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, null, null);
			FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, false, false, true);
			String exportFilePath = formInstancePage.exportToFile(Entity, Form, ReferenceDate, "text(RC-FRY9CIntCod)", Module, null);
			assertThat(true).isEqualTo(exportFilePath.endsWith(".zip"));
			String saveFolderString = new File(exportFilePath).getParent();
			for (String file : FileUtil.unCompress(exportFilePath, saveFolderString))
			{
				if (file.endsWith("JPY_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
				}
				if (file.endsWith("RMB_EXPORT.xml"))
				{
					String exportedFile = saveFolderString + "/" + file;
					String content = TxtUtil.getAllContent(new File(exportedFile));
					assertThat(content).contains("123000");
					assertThat(content).contains("1235000");
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
			writeTestResultToFile(caseID, testRst, "Export");
		}
	}

}
