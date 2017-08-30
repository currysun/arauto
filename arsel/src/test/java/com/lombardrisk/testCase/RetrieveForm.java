package com.lombardrisk.testCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.CsvUtil;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Create by Leo Tu on Jul 16, 2015
 */
public class RetrieveForm extends TestTemplate
{
	static List<String> Files = new ArrayList<>();
	static String testDataFolder = null;;
	static String checkRstFolder = null;
	static File testRstFile = null;

	@Parameters(
	{ "fileName" })
	@Test
	public void testRetrieve(@Optional String fileName) throws Exception
	{
		logger.info("File is: " + fileName);
		if (fileName == null || fileName.equals(""))
			fileName = "RetrieveForm.xls";
		Files = createFolderAndCopyFile("RetrieveForm", fileName);
		testDataFolder = Files.get(0);
		checkRstFolder = Files.get(1);
		testRstFile = new File(Files.get(2));

		if (!testRstFile.getName().equalsIgnoreCase(fileName))
			testRstFile = new File(testRstFile.getParent() + fileName);
		File testDataFile = new File(testDataFolderName + "/RetrieveForm/" + fileName);
		if (testDataFile.exists())
		{
			for (int i = 1; i <= ExcelUtil.getRowNums(testDataFile, null); i++)
			{

				ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, i);
				int ID = Integer.parseInt(rowValue.get(0).trim());
				String Regulator = rowValue.get(1).trim();
				String Group = rowValue.get(2).trim();
				String Form = rowValue.get(3).trim();
				String ProcessDate = rowValue.get(4).trim();
				boolean Run = rowValue.get(5).trim().equalsIgnoreCase("Y") ? true : false;
				boolean deleteReturn = rowValue.get(6).trim().equals("Y") ? true : false;
				String CheckCellValueFile = rowValue.get(7).trim();
				String AllocationFile = rowValue.get(8).trim();
				String RetrieveLog = rowValue.get(9).trim();
				String CaseID = rowValue.get(11).trim();

				if (Run)
				{
					retrieveForm(ID, Regulator, Group, Form, ProcessDate, deleteReturn, CheckCellValueFile, AllocationFile, RetrieveLog, CaseID);
					reStartBrowser();
				}
			}
		}
	}

	protected void retrieveForm(int ID, String Regulator, String Group, String Form, String ProcessDate, Boolean deleteReturn, String CheckCellValueFile, String AllocationFile, String RetrieveLog,
			String CaseID) throws Exception
	{
		logger.info("============testRetrieve=============");
		boolean testRst = false;
		boolean step1 = true, step2 = true, step3 = true, step4 = true, step5 = true;
		try
		{
			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);
			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Group, null, null);
			if (deleteReturn)
			{
				if (listPage.getFormOptions().contains(Form))
				{
					listPage.setForm(Form);
					if (listPage.getProcessDateOptions().contains(ProcessDate))
						listPage.setProcessDate(ProcessDate);
				}
				listPage.deleteFormInstance(Form, ProcessDate);
			}
			if (AllocationFile.length() > 0 && !"MyFirstForm v1".equals(Form))
			{
				String SQL = "SELECT \"PHYSICAL_VIEW_NAME\" FROM \"CFG_DW_VIEW_BINDING\" WHERE \"VIEW_CODE\" IN" + "(SELECT  \"SOURCE_VIEW_CODE\" FROM \"CFG_DT_FORM_IMPORT\" WHERE \"FORM_CODE\"='"
						+ formCode + "' AND \"FORM_VERSION\"=" + version + ")";
				List<String> physicalName = DBQuery.queryRecords(SQL);
				for (String tableName : physicalName)
				{
					String updateSQL = "UPDATE  \"" + tableName + "\"   SET \"N_RUN_SKEY\"=100";
					DBQuery.updateSourceVew(updateSQL);
				}
			}

			logger.info("Begin set retrieve properties");
			int init = listPage.getNotificationNums();
			logger.info("There are " + init + " notification(s)");
			FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
			retrievePage.setGroup(Group);
			retrievePage.setReferenceDate(ProcessDate);
			retrievePage.setForm(Form);
			retrievePage.clickOK();
			// if (!listPage.isJobResultCorrect(formCode))
			// testRst = false;
			boolean openForm = false;
			// listPage.waitJobCompleted(init);
			if (isJobSuccessed())
			{
				openForm = true;
				logger.info("Retrieve form succeeded");
			}
			else
				logger.error("Retrieve form failed");
			listPage.closeJobDialog();
			boolean jobStatus = false;
			if (openForm)
			{
				JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
				if (jobManagerPage.getJobInfo(1).get(8).equals("SUCCESS"))
					jobStatus = true;
				jobManagerPage.backToDashboard();
			}

			if (openForm && jobStatus)
			{

				FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
				if (CheckCellValueFile.endsWith(".xlsx"))
				{
					logger.info("Copy file " + CheckCellValueFile + " to " + checkRstFolder);
					String source = testDataFolder + CheckCellValueFile;
					String dest = checkRstFolder + CheckCellValueFile;

					File expectedValueFile = new File(dest);
					if (expectedValueFile.exists())
					{
						expectedValueFile.delete();
					}
					FileUtils.copyFile(new File(source), expectedValueFile);

					step1 = getCellValueInForm(formInstancePage, Regulator, formCode, version, expectedValueFile);
				}

				// check allocation
				if (AllocationFile.endsWith(".xlsx"))
				{
					logger.info("Check allocation table");
					String source = testDataFolder + AllocationFile;
					String dest = checkRstFolder + AllocationFile;

					File checkAllocationFile = new File(dest);
					if (checkAllocationFile.exists())
						checkAllocationFile.delete();
					logger.info("Copy file " + AllocationFile + " to " + checkRstFolder);
					FileUtils.copyFile(new File(source), checkAllocationFile);

					for (int index = 1; index <= ExcelUtil.getRowNums(checkAllocationFile, null); index++)
					{
						ArrayList<String> allRowValue = ExcelUtil.getRowValueFromExcel(checkAllocationFile, null, index);
						String cellId = allRowValue.get(0).trim();
						String rowKey = allRowValue.get(1).trim();
						String instance = allRowValue.get(2).trim();
						String linkCell = allRowValue.get(3).trim();
						File expAllocFile = new File(testDataFolder + allRowValue.get(4).trim());

						String extCellName = null;

						if (!rowKey.equals(""))
						{
							rowKey = String.valueOf(Integer.parseInt(rowKey) + 48);
							String gridName = getExtendCellName(Regulator, formCode, version, cellId);
							extCellName = gridName + rowKey + cellId;
						}
						AllocationPage allocationPage = formInstancePage.cellDoubleClick(Regulator, formCode, version, instance, cellId, extCellName);
						if (linkCell.contains("->"))
						{
							linkCell = linkCell.replace("->", "#");
							for (String name : linkCell.split("#"))
							{
								allocationPage.clickCellLink(name);
							}

						}
						File exportedFile = new File(allocationPage.exportAllocation());
						if (linkCell.contains("->"))
						{
							allocationPage.clickCellLink(cellId);
						}
						if (exportedFile.getName().endsWith(".csv"))
						{
							String caseStatus = "Pass";
							List<String> export_csv = CsvUtil.readFile(exportedFile);
							List<String> expect_csv = CsvUtil.readFile(expAllocFile);
							int rowAmt = export_csv.size() - 1;
							for (int id = 2; id <= rowAmt; id++)
							{
								String exported = export_csv.get(id);
								if (!expect_csv.contains(exported))
								{
									logger.error("Exported record[" + exported + "] not in expected records");
									step2 = false;
									caseStatus = "Fail";
									break;
								}
							}
							ExcelUtil.writeToExcel(checkAllocationFile, index, 5, caseStatus);
						}
						else
						{
							step2 = false;
							logger.error("Export file is:" + exportedFile.getName());
						}
						if (!step2)
							copyFailedFileToTestRst(exportedFile.getAbsolutePath(), "RetrieveForm");
					}
				}
				// check retrieve log
				if (RetrieveLog.length() > 1)
				{
					RetrieveLog = testDataFolder + RetrieveLog;
					if (RetrieveLog != null)
					{
						File logFile = new File(RetrieveLog);
						String exportedLog = formInstancePage.exportRetrieveLog();

						File exportedLogFile = new File(exportedLog);
						int actRowAmt = ExcelUtil.getRowAmts(exportedLogFile, null);
						int expectRowAmt = ExcelUtil.getRowAmts(logFile, null);
						if (actRowAmt - 4 != expectRowAmt)
							step3 = false;

						ArrayList<List<String>> expectedLog = ExcelUtil.getExcelContent(logFile, null, 1, 2);
						ArrayList<List<String>> actualLog = ExcelUtil.getExcelContent(exportedLogFile, null, 2, 3);
						for (int i = 0; i < expectedLog.size(); i++)
						{
							if (!actualLog.contains(expectedLog.get(i)))
							{
								step3 = false;
								logger.error("Log error!");
							}
						}
						if (!step3)
							copyFailedFileToTestRst(exportedLogFile.getAbsolutePath(), "RetrieveForm");
					}
					formInstancePage.closeRetrieveLog();
				}
			}
			else
			{
				if (RetrieveLog.length() > 1)
				{
					JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
					JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(formCode, version, ProcessDate);
					JobResultPage jobResultPage = jobDetailsPage.enterJobResultPage();
					File exportedLogFile = new File(jobResultPage.exportRetrieveLog());
					jobDetailsPage.backToDashboard();
					File expectedLogFile = new File(testDataFolder + RetrieveLog);
					// int actRowAmt = ExcelUtil.getRowAmts(exportedLogFile,
					// null);
					// int expectRowAmt = ExcelUtil.getRowAmts(expectedLogFile,
					// null);
					// if (actRowAmt - 2 != expectRowAmt)
					// step4 = false;

					ArrayList<List<String>> expectedLog = ExcelUtil.getExcelContent(expectedLogFile, null, 1, 2);
					ArrayList<List<String>> actualLog = ExcelUtil.getExcelContent(exportedLogFile, null, 2, 3);
					for (int i = 0; i < expectedLog.size(); i++)
					{
						if (!actualLog.contains(expectedLog.get(i)))
						{
							step5 = false;
							logger.error("Log error!");
						}
					}
					if (!step5)
						copyFailedFileToTestRst(exportedLogFile.getAbsolutePath(), "RetrieveForm");
				}
			}
			if (step1 && step2 && step3 && step4 && step5)
				testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(testRstFile, ID, 10, CaseID, testRst, "RetrieveForm");
		}

	}

}
