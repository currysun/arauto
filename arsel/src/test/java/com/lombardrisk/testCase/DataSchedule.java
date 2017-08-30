package com.lombardrisk.testCase;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.Business;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;

/**
 * Create by Leo Tu on 5/20/2016
 */

public class DataSchedule extends TestTemplate
{
	@Parameters(
	{ "fileName" })
	@Test
	public void DsReturnRetrieve(@Optional String fileName) throws Exception
	{
		logger.info("File is: " + fileName);
		if (fileName == null || fileName.equals(""))
			fileName = "DSRetrieve.xls";
		List<String> Files = createFolderAndCopyFile("DataSchedule", fileName);
		String testDataFolder = Files.get(0);
		String checkCellFileFolder = Files.get(1);

		if (!testRstFile.getName().equalsIgnoreCase(fileName))
			testRstFile = new File(testRstFile.getParent() + fileName);

		logger.info("============testDSRetrieve=============");

		boolean testRst = true;
		File testDataFile = new File(testDataFolderName + "/DataSchedule/" + fileName);
		int rouNums = ExcelUtil.getRowNums(testDataFile, null);
		for (int index = 1; index <= rouNums; index++)
		{
			ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, index);
			int ID = Integer.parseInt(rowValue.get(0).trim());
			String Regulator = rowValue.get(1).trim();
			String Group = rowValue.get(2).trim();
			String Form = rowValue.get(3).trim();
			String ProcessDate = rowValue.get(4).trim();
			if (ProcessDate.equals(""))
				ProcessDate = null;
			String Run = rowValue.get(5).trim();
			boolean DeleteExistForm = false;
			if (rowValue.get(6).trim().equals("Y"))
				DeleteExistForm = true;

			String jobStatus = rowValue.get(7).trim();
			String CheckCellValueFile = rowValue.get(8).trim();
			String Update_New = rowValue.get(9).trim();
			String formStatus = rowValue.get(10).trim();
			String action = rowValue.get(11).trim();
			String ErrorMessage = rowValue.get(12).trim();
			String CaseID = rowValue.get(14).trim();

			String formCode = splitReturn(Form).get(0);
			String version = splitReturn(Form).get(1);

			if (Run.equalsIgnoreCase("Y"))
			{
				ListPage listPage = super.m.listPage;
				logger.info("Case id is:" + CaseID);
				try
				{
					listPage.getProductListPage(Regulator, Group, Form, null);

					if (CheckCellValueFile.endsWith(".xlsx") && Update_New.length() == 0)
					{
						if (DeleteExistForm)
						{
							// listPage.setProcessDate(ProcessDate);
							listPage.deleteFormInstance(Form, ProcessDate);
						}

						logger.info("Begin set retrieve properties");
						int init = listPage.getNotificationNums();
						logger.info("There are " + init + " notifcation(s)");
						FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
						retrievePage.setGroup(Group);
						retrievePage.setReferenceDate(ProcessDate);
						retrievePage.setForm(Form);
						retrievePage.clickOK();
						boolean openForm = false;
						if (isJobSuccessed())
						{
							logger.info("Retrieve form succeeded");
							openForm = true;
						}
						else
							logger.error("Retrieve form failed");

						listPage.closeJobDialog();

						if (jobStatus.length() > 1)
						{
							JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
							if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
							{
								logger.error("Job status is incorrect,should be " + jobStatus);
								testRst = false;
							}
							jobManagerPage.backToDashboard();
						}

						listPage.clickDashboard();

						if (openForm)
						{

							logger.info("Copy file " + CheckCellValueFile + " to " + checkCellFileFolder);
							String source = testDataFolder + CheckCellValueFile;
							String dest = checkCellFileFolder + CheckCellValueFile;

							FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
							File expectedValueFile = new File(dest);
							if (expectedValueFile.exists())
							{
								expectedValueFile.delete();
							}
							FileUtils.copyFile(new File(source), expectedValueFile);

							testRst = getCellValueInForm(formInstancePage, Regulator, formCode, version, expectedValueFile);
						}
					}
					else if (Update_New.length() > 0)
					{
						String type = null, cellName = null, cellValue = null;
						if (Update_New.length() > 1)
						{
							type = Update_New.split(",")[0];
							cellName = Update_New.split(",")[1];
							cellValue = Update_New.split(",")[2];
							if (type.equalsIgnoreCase("D"))
								type = "Discard";
							else
								type = "Preserve";
						}
						else
						{
							type = Update_New;
						}
						ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
						List<String> views = returnSourcePage.getSourceView();

						String SQL = "SELECT \"PHYSICAL_VIEW_NAME\" FROM \"CFG_DW_VIEW_BINDING\" WHERE \"VIEW_CODE\"='" + views.get(0) + "'";
						String physicalViewName = DBQuery.queryRecord(SQL);
						SQL = "UPDATE \"" + physicalViewName + "\" SET \"N_RUN_SKEY\"= \"N_RUN_SKEY\"+1";
						DBQuery.updateSourceVew(SQL);

						Random rand = new Random();
						int ran = rand.nextInt(2);

						FormInstancePage formInstancePage;

						if (Update_New.length() == 1)
						{
							if (ran == 0)
							{
								returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
								returnSourcePage.update();
								if (isJobSuccessed())
								{
									logger.info("Update form succeed");
								}
								else
								{
									logger.error("Update form failed");
									testRst = false;
								}
								listPage.closeJobDialog();

								if (jobStatus.length() > 1)
								{
									JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
									if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
									{
										logger.error("Job status is incorrect,should be " + jobStatus);
										testRst = false;
									}
									jobManagerPage.backToDashboard();
								}

								listPage.clickDashboard();
							}
							else
							{
								formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
								returnSourcePage = formInstancePage.enterReturnSourcePage();
								returnSourcePage.update();
								if (isJobSuccessed())
								{
									logger.info("Update form succeed");
								}
								else
								{
									logger.error("Update form failed");
									testRst = false;
								}
								formInstancePage.closeRetrieveDialog();
								formInstancePage.closeFormInstance();

								if (jobStatus.length() > 1)
								{
									JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
									if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
									{
										logger.error("Job status is incorrect,should be " + jobStatus);
										testRst = false;
									}
									jobManagerPage.backToDashboard();
								}
							}

						}
						else
						{
							formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
							formInstancePage.editCellValue(null, cellName, null, cellValue);
							if (ran == 0)
							{
								formInstancePage.closeFormInstance();
								returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
								returnSourcePage.retrieveNew(type);
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

								if (jobStatus.length() > 1)
								{
									JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
									if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
									{
										logger.error("Job status is incorrect,should be " + jobStatus);
										testRst = false;
									}
									jobManagerPage.backToDashboard();

								}
								listPage.clickDashboard();
							}
							else
							{
								returnSourcePage = formInstancePage.enterReturnSourcePage();
								returnSourcePage.retrieveNew(type);
							}
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
							formInstancePage.closeFormInstance();
							if (jobStatus.length() > 1)
							{
								JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
								if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
								{
									logger.error("Job status is incorrect,should be " + jobStatus);
									testRst = false;
								}
								jobManagerPage.backToDashboard();
							}
						}

						if (testRst)
						{
							formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
							logger.info("Copy file " + CheckCellValueFile + " to " + checkCellFileFolder);
							String source = testDataFolder + CheckCellValueFile;
							String dest = checkCellFileFolder + CheckCellValueFile;

							File expectedValueFile = new File(dest);
							if (expectedValueFile.exists())
							{
								expectedValueFile.delete();
							}
							FileUtils.copyFile(new File(source), expectedValueFile);

							testRst = getCellValueInForm(formInstancePage, Regulator, formCode, version, expectedValueFile);
						}

					}
					else if (ErrorMessage.length() > 1)
					{
						String errorString = null;

						if (formStatus.equalsIgnoreCase("locked"))
						{
							String formLock = listPage.getFormDetailInfo(1).get(6);
							if (!formLock.equalsIgnoreCase("lock"))
							{
								FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
								formInstancePage.lockClick();
								formInstancePage.closeFormInstance();
							}
						}

						ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
						List<String> views = returnSourcePage.getSourceView();
						String SQL = "SELECT \"PHYSICAL_VIEW_NAME\" FROM \"CFG_DW_VIEW_BINDING\" WHERE \"VIEW_CODE\"='" + views.get(0) + "'";
						String physicalViewName = DBQuery.queryRecord(SQL);
						SQL = "UPDATE \"" + physicalViewName + "\" SET \"N_RUN_SKEY\"= \"N_RUN_SKEY\"+1";
						DBQuery.updateSourceVew(SQL);

						if (action.equalsIgnoreCase("retrieve"))
						{
							FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
							retrievePage.setGroup(Group);
							retrievePage.setReferenceDate(ProcessDate);
							retrievePage.setForm(Form);
							retrievePage.clickOK();

							errorString = returnSourcePage.getWarningMessage();
							returnSourcePage.closeReturnSourcePage();

						}
						else if (action.equalsIgnoreCase("update"))
						{
							returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
							errorString = returnSourcePage.getWarningMessage();
							returnSourcePage.closeReturnSourcePage();
						}
						else if (action.equalsIgnoreCase("lock"))
						{
							returnSourcePage = listPage.enterReturnSourcePage(formCode, version, ProcessDate);
							returnSourcePage.update();

							FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, ProcessDate);
							formInstancePage.lockClick();
						}

						if (!errorString.equalsIgnoreCase(ErrorMessage))
							testRst = false;
					}
					else
					{
						if (DeleteExistForm)
							listPage.deleteFormInstance(Form, ProcessDate);

						logger.info("Begin set retrieve properties");
						int init = listPage.getNotificationNums();
						logger.info("There are " + init + " notifcation(s)");
						FormInstanceRetrievePage retrievePage = listPage.openFormInstanceRetrievePage();
						retrievePage.setGroup(Group);
						retrievePage.setReferenceDate(ProcessDate);
						retrievePage.setForm(Form);
						retrievePage.clickOK();
						if (isJobSuccessed())
						{
							logger.info("Retrieve form succeeded");
						}
						else
							logger.warn("Retrieve form failed");

						listPage.closeJobDialog();

						if (jobStatus.length() > 1)
						{
							JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
							if (!jobManagerPage.getLatestJobInfo().get(8).equals(jobStatus))
							{
								logger.error("Job status is incorrect,should be " + jobStatus);
								testRst = false;
							}
							jobManagerPage.backToDashboard();
						}
					}

				}
				catch (RuntimeException e)
				{
					testRst = false;
					// e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
				finally
				{
					writeTestResultToFile(testRstFile, ID, 13, CaseID, testRst, "DS_RetrieveForm");
					reStartBrowser();
				}
			}
		}

	}

	@Parameters(
	{ "fileName" })
	@Test
	public void DsReturnRetrieve_Export(@Optional String fileName) throws Exception
	{
		if (fileName == null || fileName.equals(""))
			fileName = "DSExport.xls";
		createFolderAndCopyFile("DataSchedule", fileName);
		List<String> Files = createFolderAndCopyFile("DataSchedule", fileName);
		String testDataFolder = Files.get(0);
		// String checkCellFileFolder = Files.get(1);

		ListPage listPage = super.m.listPage;
		PhysicalLocationPage physicalLocationPage = listPage.enterPhysicalLoaction();
		if (!physicalLocationPage.getPhysicalLocation().contains("0001"))
			physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
		physicalLocationPage.backToDashboard();

		if (!testRstFile.getName().equalsIgnoreCase(fileName))
			testRstFile = new File(testRstFile.getParent() + fileName);

		File testDataFile = new File(testDataFolderName + "/DataSchedule/" + fileName);
		for (int i = 1; i <= ExcelUtil.getRowNums(testDataFile, null); i++)
		{
			ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, i);
			int ID = Integer.parseInt(rowValue.get(0).trim());
			String Regulator = rowValue.get(1).trim();
			String Group = rowValue.get(2).trim();
			String Form = rowValue.get(3).trim();
			String referenceDate = rowValue.get(4).trim();
			boolean Run = rowValue.get(5).trim().equalsIgnoreCase("Y") ? true : false;
			boolean approve = rowValue.get(6).trim().equalsIgnoreCase("Y") ? true : false;
			String updateSource = rowValue.get(7).trim();
			String fileType = rowValue.get(8).trim();
			String module = rowValue.get(9).trim();
			String compressType = rowValue.get(10).trim();
			String message = rowValue.get(11).trim();
			String jobStatus = rowValue.get(12).trim();
			String errorMessage = rowValue.get(13).trim();
			String errorLog = rowValue.get(14).trim();
			String location = rowValue.get(15).trim().replace("//", "\"");
			String baseline = rowValue.get(16).trim();
			String caseId = rowValue.get(18).trim();
			boolean testRst = true;

			if (Run)
			{
				listPage = super.m.listPage;
				logger.info("==========Test ds return export==========");
				logger.info("Case id is:" + caseId);
				try
				{
					listPage.getProductListPage(Regulator, Group, Form, referenceDate);

					String formCode = splitReturn(Form).get(0);
					String version = splitReturn(Form).get(1);
					List<String> formDetail = listPage.getFormDetailInfo(1);

					if (approve)
					{
						if (formDetail.get(6).equalsIgnoreCase("lock"))
						{
							FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
							formInstancePage.unlockClick();
							formInstancePage.closeFormInstance();
						}

						FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
						ReturnSourcePage returnSourcePage = formInstancePage.enterReturnSourcePage();
						if (returnSourcePage.verifyRetrieveNewButtonEnabled())
						{
							returnSourcePage.update();
							formInstancePage.closeRetrieveDialog();
							Thread.sleep(1000 * 90);
						}
						else
							returnSourcePage.closeReturnSourcePage();

						formInstancePage.clickReadyForApprove();
						listPage = formInstancePage.closeFormInstance();
						HomePage homePage = listPage.approveReturn(listPage, Regulator, Group, Form, referenceDate);
						homePage.logon();
						listPage.getProductListPage(Regulator, Group, Form, referenceDate);
					}
					else
					{
						if (formDetail.get(6).equalsIgnoreCase("lock"))
						{
							FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
							formInstancePage.unlockClick();
							formInstancePage.closeFormInstance();
						}
					}

					if (updateSource.length() > 1)
					{

						ReturnSourcePage returnSourcePage = listPage.enterReturnSourcePage(formCode, version, referenceDate);
						List<String> views = returnSourcePage.getSourceView();
						String SQL = "SELECT \"PHYSICAL_VIEW_NAME\" FROM \"CFG_DW_VIEW_BINDING\" WHERE \"VIEW_CODE\"='" + views.get(0) + "'";
						String physicalViewName = DBQuery.queryRecord(SQL);
						SQL = "UPDATE \"" + physicalViewName + "\" SET \"" + updateSource + "\"= \"" + updateSource + "\"+1";
						DBQuery.updateSourceVew(SQL);
					}

					String regulatorPrefix = listPage.getSelectRegulatorPrefix();
					logger.info("Prefix of regulator[" + Regulator + "] is:" + regulatorPrefix);
					FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

					if (message.length() > 1)
					{
						boolean lock = false;
						if (caseId.equalsIgnoreCase("6122"))
							lock = true;
						String actualMessage = formInstancePage.getExportDataScheduleMessage(fileType, module, compressType, lock);
						if (!actualMessage.equalsIgnoreCase(message))
						{
							testRst = false;
							logger.error("Expected message is[" + message + "], but actual message is[" + actualMessage + "]");
						}
					}
					else if (location.length() > 1)
					{
						String dateString = referenceDate.replace("/", "");
						dateString = dateString.substring(4, 8) + dateString.substring(2, 4) + dateString.substring(0, 2);
						location = location + "/Submission/" + regulatorPrefix + "/" + Group + "/" + dateString + "/";
						File folderFile = new File(location);
						FileUtils.cleanDirectory(folderFile);
						if (errorLog.length() > 0)
						{
							logger.info("File location is:" + location);
							location = location + "ValidationErrors/";
							testRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, compressType, "test export", location);
						}
						else
						{
							logger.info("File location is:" + location);
							boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, compressType, "test export", location);
							if (!exportRst)
								testRst = false;
							if (baseline.length() > 1 && testRst)
							{
								baseline = testDataFolder + baseline;
								String exportFilePath = formInstancePage.getDownloadedDSReturn(location);
								if (fileType.equalsIgnoreCase("ds"))
									fileType = "xml";
								else if (fileType.equalsIgnoreCase("DS(txt)"))
									fileType = "text";
								else if (fileType.equalsIgnoreCase("DS(csv)"))
									fileType = "csv";
								testRst = Business.verifyExportedFile(baseline, exportFilePath, fileType);
								if (!testRst)
								{
									copyFailedFileToTestRst(exportFilePath, "DataSchedule");
								}
							}
						}

					}
					else
					{
						String dateString = referenceDate.replace("/", "");
						dateString = dateString.substring(4, 8) + dateString.substring(0, 5);
						location = location + "/Submission/" + regulatorPrefix + "/" + Group + "/" + dateString + "/";
						logger.info("File location is:" + location);
						boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, compressType, "test export", location);
						if (!exportRst)
						{
							testRst = false;
							logger.error("Should not generated file");
						}
					}
					formInstancePage.closeFormInstance();

					if (errorMessage.length() > 1)
					{
						if (jobStatus.length() > 1)
						{
							JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
							List<String> jobDetailsList = jobManagerPage.getLatestJobInfo();
							if (!jobDetailsList.get(8).equals(jobStatus))
							{
								logger.error("Job status is incorrect,should be " + jobStatus);
								testRst = false;
							}
							if (!jobDetailsList.get(12).equals(errorLog))
							{
								testRst = false;
								logger.error("Expected log is:" + errorLog + ", but actual log is:" + jobDetailsList.get(7));
							}
							jobManagerPage.backToDashboard();
						}

						MessageCenter messageCenter = listPage.enterMessageCenterPage();
						String msg = messageCenter.getLatestMessage();
						for (String item : errorMessage.split("#"))
						{
							if (!msg.contains(item))
							{
								testRst = false;
								logger.error("Error message is incorrect, actual message is: " + msg);
							}
						}
						messageCenter.closeMessageCenter();
					}
				}
				catch (RuntimeException e)
				{
					testRst = false;
					// e.printStackTrace();
					logger.error(e.getMessage(), e);
				}
				finally
				{
					// loseFormInstance();
					writeTestResultToFile(testRstFile, ID, 17, caseId, testRst, "DS_Export");
					reStartBrowser();
				}
			}

		}
	}

	private void importToDB(String CSVFile_Base, String CSVFile_Export) throws Exception
	{
		String dbmsDriver = "net.sourceforge.jtds.jdbc.Driver";
		String port = "1433";
		String host = "172.20.20.230\\SQL12";
		String db = "AR_AUTOMATION_TOOL";
		String strConn;
		if (host.contains("\\"))
		{
			host = host.replace("\\", "#");
			strConn = String.format("jdbc:jtds:sqlserver://%s:%s/%s;instance=%s", host.split("#")[0], port, db, host.split("#")[1]);
		}
		else
		{
			strConn = String.format("jdbc:jtds:sqlserver://%s:%s/%s", host, port, db);
		}
		DbUtils.loadDriver(dbmsDriver);

		Connection conn = DriverManager.getConnection(strConn, "sa", "password");
		Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		String[] tables =
		{ "BASELINE", "EXPORT" };
		String[] files =
		{ "CSVFile_Base", "CSVFile_Export" };
		for (int i = 0; i < tables.length; i++)
		{
			String delete = "DROP TABLE IF EXISTS [" + tables[i] + "]";
			stmt.execute(delete);
			String sql = "CREATE TABLE " + tables[i] + "(\n" + "\t[RULE ID] [nvarchar](50) NOT NULL,\n" + "\t[RULE TYPE] [nvarchar](50) NOT NULL,\n" + "\t[RULE NAME] [text] NOT NULL,\n"
					+ "\t[RULE DESCRIPTION] [text] NOT NULL,\n" + "\t[DATA SCHEDULE] [nvarchar](50) NOT NULL,\n" + "\t[TARGET COLUMN] [nvarchar](50) NOT NULL,\n" + "\t[STATUS] [nchar](10) NOT NULL,\n"
					+ "\t[FAILED ROWS] [int] NOT NULL) ";
			stmt.execute(sql);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(files[i]))));
			String line;
			int init = 0;
			while ((line = br.readLine()) != null)
			{
				if (init > 0)
				{
					stmt.execute(getSQL(line, tables[i]));
					init++;
				}
			}
		}

		stmt.close();
		conn.close();
	}

	private String getSQL(String line, String tabName) throws Exception
	{
		String[] parts = line.split(",");
		StringBuilder b = new StringBuilder();
		b.append("insert into " + tabName + "(id,name,address,phone) values (");
		for (String s : parts)
		{
			b.append("'" + s + "',");
		}
		b.deleteCharAt(b.length() - 1);
		b.append(")");
		return b.toString();
	}

	@Test
	public void testDSValidation() throws Exception
	{
		String fileName = "DSValidation.xls";

		List<String> Files = createFolderAndCopyFile("DataSchedule", fileName);
		String testDataFolder = Files.get(0);
		// String checkCellFileFolder = Files.get(1);
		testRstFile = new File(Files.get(2));

		File testDataFile = new File(testDataFolderName + "/DataSchedule/" + fileName);
		for (int index = 1; index <= ExcelUtil.getRowNums(testDataFile, null); index++)
		{
			ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, index);
			int ID = Integer.parseInt(rowValue.get(0).trim());
			String CaseID = rowValue.get(8).trim();
			boolean testRst = false;
			try
			{
				String Regulator = rowValue.get(1).trim();
				String Entity = rowValue.get(2).trim();
				String Form = rowValue.get(3).trim();
				String ReferenceDate = rowValue.get(4).trim();
				String Run = rowValue.get(5).trim();
				String DSVBaseline = rowValue.get(6).trim();

				if (Run.equalsIgnoreCase("Y"))
				{
					ListPage listPage = m.listPage;
					listPage.getProductListPage(Regulator, Entity, null, null);
					listPage.deleteFormInstance(Form, ReferenceDate);
					retrieveForm(listPage, Entity, Form, ReferenceDate);

					String formCode = splitReturn(Form).get(0);
					String formVersion = splitReturn(Form).get(1);
					FormInstancePage formInstancePage = listPage.openFormInstance(formCode, formVersion, ReferenceDate);
					DataValidationPage dataValidationPage = formInstancePage.enterDataValidation(true);
					String exportFile = dataValidationPage.exportDataValidation();
					testRst = Business.verifyExportedFile(testDataFolder + "/" + DSVBaseline, exportFile, "csv");
				}
			}
			catch (RuntimeException e)
			{
				testRst = false;
				logger.error(e.getMessage(), e);
			}
			finally
			{
				writeTestResultToFile(testRstFile, ID, 7, CaseID, testRst, "DataScheduleValidation");
				reStartBrowser();
			}
		}
	}

	@Test
	public void test6817() throws Exception
	{
		String caseID = "6817";
		logger.info("====Test case " + caseID + "====");
		boolean testRst = false;

		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_DSVal, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String RuleId = testData.get(4);
			String RuleType = testData.get(5);
			String DataSchedule = testData.get(6);
			String TargetColumn = testData.get(7);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);
			if (!listPage.isFormExist(Form, ReferenceDate))
			{
				retrieveForm(listPage, Entity, Form, ReferenceDate);
			}
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, formVersion, ReferenceDate);
			DataValidationPage dataValidationPage = formInstancePage.enterDataValidation(true);
			String id = RuleId.split("/")[0];
			int ExpNum = Integer.parseInt(RuleId.split("/")[1]);
			dataValidationPage.filter("id", id);
			int num = dataValidationPage.getRowNum();
			assertThat(ExpNum).isEqualTo(num);

			String type = RuleType.split("/")[0];
			ExpNum = Integer.parseInt(RuleType.split("/")[1]);
			dataValidationPage.filter("type", type);
			num = dataValidationPage.getRowNum();
			assertThat(ExpNum).isEqualTo(num);

			String dataSchedule = DataSchedule.split("/")[0];
			ExpNum = Integer.parseInt(DataSchedule.split("/")[1]);
			dataValidationPage.filter("dataSchedule", dataSchedule);
			num = dataValidationPage.getRowNum();
			assertThat(ExpNum).isEqualTo(num);

			String targetColumn = TargetColumn.split("/")[0];
			ExpNum = Integer.parseInt(TargetColumn.split("/")[1]);
			dataValidationPage.filter("targetColumn", targetColumn);
			num = dataValidationPage.getRowNum();
			assertThat(ExpNum).isEqualTo(num);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "DataScheduleValidation");
		}
	}

	@Test
	public void test6818() throws Exception
	{
		String caseID = "6818";
		logger.info("====Test case " + caseID + "====");
		boolean testRst = false;

		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_DSVal, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);
			if (!listPage.isFormExist(Form, ReferenceDate))
			{
				retrieveForm(listPage, Entity, Form, ReferenceDate);
			}
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, formVersion, ReferenceDate);
			DataValidationPage dataValidationPage = formInstancePage.enterDataValidation(true);
			assertThat(dataValidationPage.isFilterExist()).isEqualTo(true);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "DataScheduleValidation");
		}
	}

	@Test
	public void test6821() throws Exception
	{
		String caseID = "6821";
		logger.info("====Test case " + caseID + "====");
		boolean testRst = false;

		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_DSVal, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);
			if (!listPage.isFormExist(Form, ReferenceDate))
			{
				retrieveForm(listPage, Entity, Form, ReferenceDate);
			}
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, formVersion, ReferenceDate);

			assertThat(formInstancePage.isDataValidationTabExist()).isEqualTo(false);

			testRst = true;
		}
		catch (RuntimeException e)
		{
			testRst = false;
			logger.error(e.getMessage(), e);
		}
		finally
		{
			writeTestResultToFile(caseID, testRst, "DataScheduleValidation");
		}
	}

	@Test
	public void test6886() throws Exception
	{
		String caseID = "6886";
		logger.info("====Test case " + caseID + "====");
		boolean testRst = false;

		try
		{
			String nodeName = "C" + caseID;
			List<String> testData = getElementValueFromXML(testData_DSVal, nodeName);
			String Regulator = testData.get(0);
			String Entity = testData.get(1);
			String Form = testData.get(2);
			String ReferenceDate = testData.get(3);
			String Count = testData.get(4);

			ListPage listPage = super.m.listPage;
			listPage.getProductListPage(Regulator, Entity, Form, null);
			String formCode = splitReturn(Form).get(0);
			String formVersion = splitReturn(Form).get(1);
			if (!listPage.isFormExist(Form, ReferenceDate))
			{
				retrieveForm(listPage, Entity, Form, ReferenceDate);
			}
			FormInstancePage formInstancePage = listPage.openFormInstance(formCode, formVersion, ReferenceDate);
			DataValidationPage dataValidationPage = formInstancePage.enterDataValidation(true);
			for (String item : Count.split("#"))
			{
				String status = item.split("/")[0];
				int ExpNum = Integer.parseInt(item.split("/")[1]);
				dataValidationPage.filter("status", status);
				int num = dataValidationPage.getRowNum();
				assertThat(ExpNum).isEqualTo(num);
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
			writeTestResultToFile(caseID, testRst, "DataScheduleValidation");
		}
	}

}
