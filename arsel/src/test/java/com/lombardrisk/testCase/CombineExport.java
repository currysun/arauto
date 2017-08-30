package com.lombardrisk.testCase;

import com.lombardrisk.pages.*;
import com.lombardrisk.utils.Business;
import com.lombardrisk.utils.DBQuery;
import com.lombardrisk.utils.TestTemplate;
import com.lombardrisk.utils.fileService.ExcelUtil;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Grant Sun on 23/05/2017.
 * CombineExport include 2 parts,one is DScombined and the other is XSLTCombine.
 */
public class CombineExport extends TestTemplate{
    @Parameters(
            { "fileName" })
    @Test
    public void DsReturnCombine_Export(@Optional String fileName) throws Exception
    {
        if (fileName == null || fileName.equals(""))
            fileName = "DSExportCombine.xls";
        createFolderAndCopyFile("CombineExport", fileName);
        List<String> Files = createFolderAndCopyFile("CombineExport", fileName);
        String testDataFolder = Files.get(0);
        // String checkCellFileFolder = Files.get(1);

        ListPage listPage = super.m.listPage;
        PhysicalLocationPage physicalLocationPage = listPage.enterPhysicalLoaction();
        if (!physicalLocationPage.getPhysicalLocation().contains("0001"))
            physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
        physicalLocationPage.backToDashboard();

        if (!testRstFile.getName().equalsIgnoreCase(fileName))
            testRstFile = new File(testRstFile.getParent() + fileName);

        File testDataFile = new File(testDataFolderName + "/CombineExport/" + fileName);
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
            String subGroup=rowValue.get(19).trim();
            String [] subGrpArray =subGroup.split(",");
            boolean testRst = true;
            boolean updateComplete=false;

            if (Run)
            {
                listPage = super.m.listPage;
                logger.info("==========Test ds return Combined export==========");
                logger.info("Case id is:" + caseId);
                try
                {

                    if(caseId.equals("7399"))  //test csae:7399
                    {
                        physicalLocationPage = listPage.enterPhysicalLoaction();
                        physicalLocationPage.deleteExistLocation("0001");
                        physicalLocationPage.backToDashboard();
                    }

                    listPage.getProductListPageSwtichEntity(Regulator, Group, Form, referenceDate);
                    String formCode = splitReturn(Form).get(0);
                    String version = splitReturn(Form).get(1);
                    List<String> formDetail = listPage.getFormDetailInfo(1);
                    //boolean atested=listPage.isFormLockedInList(Form,referenceDate);//define return whether is attested or not.
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
                        HomePage homePage = listPage.approveReturnCombine(listPage, Regulator, Group, Form, referenceDate);
                        homePage.logon();
                        listPage.getProductListPage(Regulator, Group, Form, referenceDate);
                    }
                    else
                    {
                        if (formDetail.get(6).equalsIgnoreCase("lock"))//&& (!locked)
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
                        SQL = "UPDATE \"" + physicalViewName + "\" SET \"" + updateSource + "\"= \"" + updateSource + "\"+1 where ENTITYCODE="+"'"+subGroup+"'";
                        DBQuery.update(2, SQL);
                        updateComplete=true;
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
                        formInstancePage.closeFormInstance();
                    }
                    else if (location.length() > 1)
                    {
                        String dateString = referenceDate.replace("/", "");
                        dateString = dateString.substring(4, 8) + dateString.substring(2, 4) + dateString.substring(0, 2);
                        String sublocation=location; //Initialize sublocation from location.
                        location = location + "/Submission/" + regulatorPrefix + "/" + Group + "/" + dateString + "/";
                        File folderFile = new File(location);
                        FileUtils.cleanDirectory(folderFile);

                        if (errorLog.length() > 0)
                        {
                            logger.info("File location is:" + location);
                            location = location + "ValidationErrors/";
                            testRst = formInstancePage.isExportCombineSucceed(fileType, module, compressType, "test export", location);
                        }
                        else
                        {
                            logger.info("File location is:" + location);
                            boolean exportRst = formInstancePage.isExportCombineSucceed(fileType, module, compressType, "test export", location);
                            if (!exportRst)
                                testRst = false;
                            if (baseline.length() > 1 && testRst)
                            {
                                String baselineName=baseline;
                                baseline = testDataFolder + baseline;
                                String exportFilePath = formInstancePage.getDownloadedDSReturn(location);
                                if (fileType.equalsIgnoreCase("dscb"))
                                    fileType = "xml";
                                ArrayList<String> listFileName = new ArrayList<String>();
                                AbstractPage.getAllFileName(location,listFileName);
                                testRst=false;
                                for (String name : listFileName)
                                {
                                    if(name.equals(baselineName)){
                                        testRst=true;
                                        logger.info("Baseline file "+name+" is not same with "+baselineName);
                                    }
                                    String exportAllFilePath = location+name;
                                    if(!name.equalsIgnoreCase("combine"))
                                    {
                                        copyFailedFileToTestRst(exportAllFilePath, "CombineExport");
                                    }
                                }

                                //copy submission file in sub-entity,like 0005,0006,0007.
                                    for(String subGrpArrayList:subGrpArray){
                                        String subloc=sublocation + "/Submission/" + regulatorPrefix + "/" + subGrpArrayList + "/" + dateString + "/";
                                        String exportSubFilePath=formInstancePage.getDownloadedDSReturn(subloc);
                                        copyFailedFileToTestRst(exportSubFilePath, "CombineExport");
                                    }
                                formInstancePage.closeFormInstance();
                            }
                        }

                    }
                    else
                    /*
                     if location content is null then do not export.Only for sub-return lock
                     */
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
                    writeTestResultToFile(testRstFile, ID, 17, caseId, testRst, "CombineExport");
                    if(caseId.equals("7399"))  //restore 0001 in networklocation.
                    {
                        physicalLocationPage = listPage.enterPhysicalLoaction();
                        physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
                        physicalLocationPage.backToDashboard();
                    }
                    if(caseId.equals("7397") && updateComplete )
                    {
                        String SQL="UPDATE \"ASSETINFLOW\" SET \"RUNEXECUTIONID\"= 1 where ENTITYCODE='0007'";
                        DBQuery.update(2, SQL);
                    }
                    reStartBrowser();
                }
            }

        }
    }

    @Test
    public void test7394() throws Exception
    {
        String caseID = "7394";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {

            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="30/06/2016";
            String fileType="DSCB";
            String module="FR2052A-DATA";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;
            logger.info("Begin to remove all privilege in 0005");
            String TempGroup="0005";
            EntityPage entityManagePage = null;
            entityManagePage = listPage.EnterEntityPage();
            entityManagePage.removeAssignPrivilege(TempGroup,Form);//remove all assign privilege in entity 0005.
            entityManagePage.backToDashboard();
            listPage.getProductListPageSwtichEntity(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            List<String> formDetail = listPage.getFormDetailInfo(1);
            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);

            boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, null, "test export", location);
            if(!exportRst)
            {
                exportRst=true;//expected job status is failed,so set exportresult to true.
            }
            formInstancePage.closeFormInstance();
            formDetail = listPage.getFormDetailInfo(1);
            JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
            List<String> jobInfo = jobManagerPage.getLatestJobInfo();
            String parentJobStatus=jobInfo.get(8);
            boolean compare=parentJobStatus.equals("IN PROGRESS");
            while(compare)
            {
                refreshPage();  //when parent status is in progress,we need to wait for it to Failure.
                jobInfo = jobManagerPage.getLatestJobInfo();
                parentJobStatus=jobInfo.get(8);
                compare=parentJobStatus.equals("IN PROGRESS");
            }
            assertThat(jobInfo.get(8)).isEqualTo("SUCCESS");
            JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
            jobDetailsPage.showCombineExport(1);
            JobResultPage jobResultPage=jobDetailsPage.enterCombineJobResultPage("FED|0005|FR2052A|2","ExportSubJob",1);
            List<String> logs = jobResultPage.getLog();
            assertThat(logs.get(1)).isEqualTo("No \"Export File Format\" access to return FR2052A v2 of entity 0005.");
            testRst = true;
        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
            ListPage listPage = m.listPage;
            String Form="FR2052A v2";
            String TempGroup="0005";
            String[] userGPNames={"rpadmin_grp"};
            boolean addPermission=true;
            String[] permissionNames={"Return Maker","Return Viewer","Return Submitter","Return Approver"};
            EntityPage entityManagePage = null;
            entityManagePage = listPage.EnterEntityPage();
            entityManagePage.addUserGPPri(Form, userGPNames, addPermission, permissionNames,TempGroup);
        }
    }

    @Test
    public void test7408() throws Exception
    {
        String caseID = "7408,7400";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {
            String SQL2 = "Update CFG_EXPORT_FORMAT set SCHEMA_LOCATION='5GSchema.xsd' where SCHEMA_LOCATION='5GSchema-Nagative.xsd' and EXPORT_FORMAT_TYPE='DataSchedule-Combine'";
            DBQuery.update(SQL2);
            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="30/06/2016";
            String fileType="DSCB";
            String module="FR2052A-DATA";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;
            String SQL = "Update CFG_EXPORT_FORMAT set SCHEMA_LOCATION='5GSchema-Nagative.xsd' where SCHEMA_LOCATION='5GSchema.xsd' and EXPORT_FORMAT_TYPE='DataSchedule-Combine'";
            DBQuery.update(SQL);
            listPage.getProductListPage(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            List<String> formDetail = listPage.getFormDetailInfo(1);
            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
            boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, null, "test export", location);
                if(!exportRst)
                {
                    exportRst=true;//expected job status is failed,so set exportresult to true.
                }
            formInstancePage.closeFormInstance();
            formDetail = listPage.getFormDetailInfo(1);

                    JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
                    List<String> jobInfo = jobManagerPage.getLatestJobInfo();
                    String parentJobStatus=jobInfo.get(8);
                    boolean compare=parentJobStatus.equals("IN PROGRESS");
                    while(compare)
                    {
                        refreshPage();  //when parent status is in progress,we need to wait for it to Failure.
                        jobInfo = jobManagerPage.getLatestJobInfo();
                        parentJobStatus=jobInfo.get(8);
                        compare=parentJobStatus.equals("IN PROGRESS");
                    }
                    assertThat(jobInfo.get(8)).isEqualTo("FAILURE");
                    JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
                    Map<Integer, String> combineStatus = jobDetailsPage.getOrderComineExportStatus(1);
                    assertThat(combineStatus.get(0)).isEqualTo("NO RUN");
                    assertThat(combineStatus.get(1)).isEqualTo("NO RUN");
                    assertThat(combineStatus.get(2)).isEqualTo("SUCCESS");
                    assertThat(combineStatus.get(3)).isEqualTo("SUCCESS");
                    assertThat(combineStatus.get(4)).isEqualTo("SUCCESS");
                    assertThat(combineStatus.get(5)).isEqualTo("SUCCESS");
                    assertThat(combineStatus.get(6)).isEqualTo("SUCCESS");
                    assertThat(combineStatus.get(7)).isEqualTo("FAILURE");
                    JobResultPage jobResultPage=jobDetailsPage.enterCombineJobResultPage("FED|0001|FR2052A|2","CombineExecution",1);
                    List<String> logs = jobResultPage.getLog();
                    if ((logs.get(2)).equals("XML Job Validation error.") || (logs.get(3)).equals("XML Job Validation error."))
                    {
                        testRst = true;
                    }
                    jobDetailsPage.backToDashboard();
                    listPage.refreshPage();
            if (!formDetail.get(5).equalsIgnoreCase("Fail"))
            {
                testRst = false;
            }

        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
            String SQL = "Update CFG_EXPORT_FORMAT set SCHEMA_LOCATION='5GSchema.xsd' where SCHEMA_LOCATION='5GSchema-Nagative.xsd' and EXPORT_FORMAT_TYPE='DataSchedule-Combine'";
            DBQuery.update(SQL);
        }
    }

    @Test
    public void test7398() throws Exception
    {
        String caseID = "7398";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {
            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="30/06/2016";
            String fileType="DSCB";
            String module="FR2052A-DATA";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;

            String SQL = "Update ASSETINFLOW set Currency='CAD' where RECORDID=1362 and  ENTITYCODE ='0004' and REFERENCEDATE='30-JUN-16'";
            DBQuery.update(2,SQL);
            listPage.getProductListPage(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            List<String> formDetail = listPage.getFormDetailInfo(1);
            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
            boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, null, "test export", location);
            if(!exportRst)
            {
                exportRst=true;//expected job status is failed,so set exportresult to true.
            }
            formInstancePage.closeFormInstance();

            JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
            List<String> jobInfo = jobManagerPage.getLatestJobInfo();
            String parentJobStatus=jobInfo.get(8);
            boolean compare=parentJobStatus.equals("IN PROGRESS");
            while(compare)
            {
                refreshPage();  //when parent status is in progress,we just wait for it to Failure.
                jobInfo = jobManagerPage.getLatestJobInfo();
                parentJobStatus=jobInfo.get(8);
                compare=parentJobStatus.equals("IN PROGRESS");
            }
            assertThat(jobInfo.get(8)).isEqualTo("FAILURE");
            JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
            Map<Integer, String> combineStatus = jobDetailsPage.getOrderComineExportStatus(1);
            assertThat(combineStatus.get(0)).isEqualTo("NO RUN");
            assertThat(combineStatus.get(1)).isEqualTo("NO RUN");
            assertThat(combineStatus.get(2)).isEqualTo("SUCCESS");
            assertThat(combineStatus.get(3)).isEqualTo("SUCCESS");
            assertThat(combineStatus.get(4)).isEqualTo("SUCCESS");
            assertThat(combineStatus.get(5)).isEqualTo("FAILURE");
            assertThat(combineStatus.get(6)).isEqualTo("SUCCESS");
            assertThat(combineStatus.get(7)).isEqualTo("NO RUN");
            List<String> combineStatusMessage = jobDetailsPage.getComineExportStatusMessage(1);
            assertThat(combineStatusMessage.get(1)).isEqualTo("Export checksum not equal to Retrieve checksum.");
            testRst = true;

        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
            String SQL = "Update ASSETINFLOW set Currency='USD' where RECORDID=1362 and  ENTITYCODE ='0004' and REFERENCEDATE='30-JUN-16'";
            DBQuery.update(2, SQL);
        }
    }

    /*
    @Test
    public void test0810() throws Exception
    {
        String caseID = "0810";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {

            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="30/06/2016";
            String fileType="DSCB";
            String module="FR2052A-DATA";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;
            listPage.getProductListPage(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            for(int i=0;i<50;i++) {
                List<String> formDetail = listPage.getFormDetailInfo(1);
                FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
                boolean exportRst = formInstancePage.isExportDataSchduleSucceed(fileType, module, null, "test export", location);
                if (!exportRst) {
                    exportRst = true;//expected job status is failed,so set exportresult to true.
                }
                formInstancePage.closeFormInstance();

                JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
                List<String> jobInfo = jobManagerPage.getLatestJobInfo();
                String parentJobStatus = jobInfo.get(8);
                boolean compare = parentJobStatus.equals("IN PROGRESS");
                while (compare) {
                    refreshPage();  //when parent status is in progress,we just wait for it to Failure.
                    jobInfo = jobManagerPage.getLatestJobInfo();
                    parentJobStatus = jobInfo.get(8);
                    compare = parentJobStatus.equals("IN PROGRESS");
                }
                assertThat(jobInfo.get(8)).isEqualTo("SUCCESS");
                JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
                Map<Integer, String> combineStatus = jobDetailsPage.getOrderComineExportStatus(1);
                assertThat(combineStatus.get(0)).isEqualTo("NO RUN");
                assertThat(combineStatus.get(1)).isEqualTo("NO RUN");
                assertThat(combineStatus.get(2)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(3)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(4)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(5)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(6)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(7)).isEqualTo("SUCCESS");
                testRst = true;
                jobDetailsPage.backToDashboard();
            }
        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
        }
    }
    */
    /*
    XSLT combined
     */
    @Parameters(
            { "fileName" })
    @Test
    public void XSLTCombinedExport(@Optional String fileName) throws Exception {
        if (fileName == null || fileName.equals(""))
            fileName = "XsltCombine.xls";
        createFolderAndCopyFile("CombineExport", fileName);
        List<String> Files = createFolderAndCopyFile("CombineExport", fileName);
        String testDataFolder = Files.get(0);
        // String checkCellFileFolder = Files.get(1);

        ListPage listPage = super.m.listPage;
        PhysicalLocationPage physicalLocationPage = listPage.enterPhysicalLoaction();
        if (!physicalLocationPage.getPhysicalLocation().contains("0001"))
            physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
        physicalLocationPage.backToDashboard();

        if (!testRstFile.getName().equalsIgnoreCase(fileName))
            testRstFile = new File(testRstFile.getParent() + fileName);

        File testDataFile = new File(testDataFolderName + "/CombineExport/" + fileName);
        for (int i = 1; i <= ExcelUtil.getRowNums(testDataFile, null); i++) {
            ArrayList<String> rowValue = ExcelUtil.getRowValueFromExcel(testDataFile, null, i);
            int ID = Integer.parseInt(rowValue.get(0).trim());
            String Regulator = rowValue.get(1).trim();
            String Group = rowValue.get(2).trim();
            String Form = rowValue.get(3).trim();
            String referenceDate = rowValue.get(4).trim();
            boolean Run = rowValue.get(5).trim().equalsIgnoreCase("Y") ? true : false;
            String fileType = rowValue.get(6).trim();
            String module = rowValue.get(7).trim();
            String ImportFile = rowValue.get(8).trim();
            String message = rowValue.get(9).trim();
            String jobStatus = rowValue.get(10).trim();
            String errorMessage = rowValue.get(11).trim();
            String errorLog = rowValue.get(12).trim();
            String location = rowValue.get(13).trim().replace("//", "\"");
            String baseline = rowValue.get(14).trim();
            String caseId = rowValue.get(16).trim();
            String subGroup = rowValue.get(17).trim();
            String[] subGrpArray = subGroup.split(",");
            boolean approve = rowValue.get(18).trim().equalsIgnoreCase("Y") ? true : false;
            boolean testRst = true;

            if (Run) {
                File importFile = new File(testDataFolderName + "/CombineExport/ImportFile/" + ImportFile);

                listPage = super.m.listPage;
                logger.info("==========Test xslt return Combined export==========");
                logger.info("Case id is:" + caseId);
                boolean AllowNull=false;
                boolean InitialiseToZeros=false;
                try {
                    String compressType=null;
                    if (caseId.equals("7431"))  //test csae:7431 delete sub networklocation
                    {
                        physicalLocationPage = listPage.enterPhysicalLoaction();
                        physicalLocationPage.deleteExistLocation("0001");
                        physicalLocationPage.backToDashboard();
                    }

                    String formCode = splitReturn(Form).get(0);
                    String version = splitReturn(Form).get(1);
                    List<String> formDetail = listPage.getFormDetailInfo(1);
                    String regulatorPrefix = listPage.getSelectRegulatorPrefix();
                    logger.info("Prefix of regulator[" + Regulator + "] is:" + regulatorPrefix);
                    listPage.getProductListPageSwtichEntity(Regulator, Group, Form, null);
                    listPage.deleteFormInstance(Form, referenceDate);
                    if(!listPage.isFormExist(Form,referenceDate)) //if return exist,then do not create from excel again.
                    {
                        boolean openForm=false;
                        FormInstancePage formInstancePage = listPage.createFormFromExcel(importFile, AllowNull, InitialiseToZeros, openForm);
                    }
                    listPage.getProductListPageSwtichEntity(Regulator, Group, Form, null);
                    //boolean atested=listPage.isFormLockedInList(Form,referenceDate);//define return whether is attested or not.
                    if (approve)
                    {
                        if (formDetail.get(6).equalsIgnoreCase("lock"))
                        {
                            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
                            formInstancePage.unlockClick();
                            formInstancePage.closeFormInstance();
                        }

                        FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
                        formInstancePage.clickReadyForApprove();
                        listPage = formInstancePage.closeFormInstance();
                        HomePage homePage = listPage.approveReturnCombine(listPage, Regulator, Group, Form, referenceDate);
                        homePage.logon();
                        listPage.getProductListPage(Regulator, Group, Form, referenceDate);
                    }
                    else
                    {
                        if (formDetail.get(6).equalsIgnoreCase("lock"))//&& (!locked)
                        {
                            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
                            formInstancePage.unlockClick();
                            formInstancePage.closeFormInstance();
                        }
                    }
                    listPage.getProductListPageSwtichEntity(Regulator, Group, Form, referenceDate);
                    FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
                    if (message.length() > 1)
                    {
                        boolean lock = false;
                        if (caseId.equalsIgnoreCase("6122"))
                            lock = true;
                        String actualMessage = formInstancePage.getExportDataScheduleMessage(fileType, module, null, lock);
                        if (!actualMessage.equalsIgnoreCase(message)) {
                            testRst = false;
                            logger.error("Expected message is[" + message + "], but actual message is[" + actualMessage + "]");
                        }
                        formInstancePage.closeFormInstance();
                    }
                    else if (location.length() > 1)
                    {
                        String dateString = referenceDate.replace("/", "");
                        dateString = dateString.substring(4, 8) + dateString.substring(2, 4) + dateString.substring(0, 2);
                        String sublocation = location; //Initialize sublocation from location.
                        location = location + "/Submission/" + regulatorPrefix + "/" + Group + "/" + dateString + "/";
                        File folderFile = new File(location);
                        FileUtils.cleanDirectory(folderFile);

                        if (errorLog.length() > 0)
                        {
                            logger.info("File location is:" + location);
                            location = location + "ValidationErrors/";
                            testRst = formInstancePage.isExportCombineSucceed(fileType, module, null, "test export", location);
                        }
                        else
                        {
                            logger.info("File location is:" + location);
                            boolean exportRst = formInstancePage.isExportCombineSucceed(fileType, module, null, "test export", location);
                            if (!exportRst)
                                testRst = false;
                            if (baseline.length() > 1 && testRst) {
                                String baselineName = baseline;
                                baseline = testDataFolder + baseline;
                                String exportFilePath = formInstancePage.getDownloadedDSReturn(location);
                                if (fileType.equalsIgnoreCase("dscb"))
                                    fileType = "xml";
                                ArrayList<String> listFileName = new ArrayList<String>();
                                AbstractPage.getAllFileName(location,listFileName);
                                testRst = false;
                                for (String name : listFileName)
                                {
                                    if (name.equals(baselineName))
                                    {
                                        testRst = true;
                                        logger.info("Baseline file "+name+" is not same with "+baselineName);
                                    }
                                    String exportAllFilePath = location + name;
                                    if(!name.equalsIgnoreCase("combine"))
                                    {
                                        copyFailedFileToTestRst(exportAllFilePath, "CombineExport");
                                    }
                                }

                                //copy submission file in sub-entity,like 0005,0006,0007.
                                for (String subGrpArrayList : subGrpArray) {
                                    String subloc = sublocation + "/Submission/" + regulatorPrefix + "/" + subGrpArrayList + "/" + dateString + "/";
                                    String exportSubFilePath = formInstancePage.getDownloadedDSReturn(subloc);
                                    copyFailedFileToTestRst(exportSubFilePath, "CombineExport");
                                }
                                formInstancePage.closeFormInstance();
                            }
                        }

                    } else
                        formInstancePage.closeFormInstance();

                    if (errorMessage.length() > 1)
                    {
                        if (jobStatus.length() > 1)
                        {
                            JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
                            List<String> jobDetailsList = jobManagerPage.getLatestJobInfo();
                            if (!jobDetailsList.get(8).equals(jobStatus)) {
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
                } catch (RuntimeException e) {
                    testRst = false;
                    // e.printStackTrace();
                    logger.error(e.getMessage(), e);
                } finally
                {
                    // loseFormInstance();
                    writeTestResultToFile(testRstFile, ID, 15, caseId, testRst, "CombineExport");
                    if (caseId.equals("7431"))  //restore 0001 in networklocation.
                    {
                        physicalLocationPage = listPage.enterPhysicalLoaction();
                        physicalLocationPage.addFileLocation("0001", "C:\\Documents\\DSExport", null);
                        physicalLocationPage.backToDashboard();
                    }
                    reStartBrowser();
                }
            }
        }
    }
    @Test
    public void test7432() throws Exception
    {
        String caseID = "7432";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {

            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="29/06/2016";
            String fileType="XSLTCB";
            String module="FR2052A-5GCOMMENT";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;
            logger.info("Begin to remove all privilege in 0005");
            String TempGroup="0005";
            String Framework="FED";
            String Taxonomy="FED-1.0.0";
            String Module="FR2052A-5GCOMMENT";
            EntityPage entityManagePage = null;
            entityManagePage = listPage.EnterEntityPage();
            entityManagePage.removeAssignPrivilege(TempGroup,Form);//remove all assign privilege in entity 0005.
            entityManagePage.backToDashboard();
            listPage.getProductListPage(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            List<String> formDetail = listPage.getFormDetailInfo(1);
            String formInstancePage = listPage.ExportToRegulatorFormatJob(Group,Form,referenceDate,fileType,Framework,Taxonomy,Module,null);
            formDetail = listPage.getFormDetailInfo(1);
            JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
            List<String> jobInfo = jobManagerPage.getLatestJobInfo();
            String parentJobStatus=jobInfo.get(8);
            boolean compare=parentJobStatus.equals("IN PROGRESS");
            while(compare)
            {
                refreshPage();  //when parent status is in progress,we need to wait for it to Failure.
                jobInfo = jobManagerPage.getLatestJobInfo();
                parentJobStatus=jobInfo.get(8);
                compare=parentJobStatus.equals("IN PROGRESS");
            }
            assertThat(jobInfo.get(8)).isEqualTo("FAILURE");
            JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
            jobDetailsPage.showCombineExport(1);
            JobResultPage jobResultPage=jobDetailsPage.enterCombineJobResultPage("FED|0005|FR2052A|2", "ExportSubJob",1);
            List<String> logs = jobResultPage.getLog();
            assertThat(logs.get(1)).isEqualTo("No \"Export File Format\" access to return FR2052A v2 of entity 0005.");
            testRst = true;

        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
            ListPage listPage = m.listPage;
            String Form="FR2052A v2";
            String TempGroup="0005";
            String[] userGPNames={"rpadmin_grp"};
            boolean addPermission=true;
            String[] permissionNames={"Return Maker","Return Viewer","Return Submitter","Return Approver"};
            EntityPage entityManagePage = null;
            entityManagePage = listPage.EnterEntityPage();
            entityManagePage.addUserGPPri(Form, userGPNames, addPermission, permissionNames,TempGroup);
        }
    }

    @Test
    public void test7435() throws Exception
    {
        String caseID = "7435,7427";
        boolean testRst = false;
        logger.info("====Test...[case id=" + caseID + "]====");
        try
        {
            String Group="0001";
            String Regulator="US1 FED Reserve";
            String Form="FR2052A v2";
            String referenceDate="29/06/2016";
            String fileType="XSLTCB";
            String module="FR2052A-5GCOMMENT";
            String location="C:\\Documents\\DSExport\\";
            String nodeName = "C" + caseID;
            ListPage listPage = m.listPage;
            String SQL = "Update CFG_EXPORT_FORMAT set SCHEMA_LOCATION='5GCommentSchema-Nagative.xsd' where SCHEMA_LOCATION='5GCommentSchema.xsd' and EXPORT_FORMAT_TYPE='XSLT-Combine'";
            DBQuery.update(SQL);
            listPage.getProductListPage(Regulator, Group, Form, referenceDate);
            String formCode = splitReturn(Form).get(0);
            String version = splitReturn(Form).get(1);
            List<String> formDetail = listPage.getFormDetailInfo(1);
            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
            boolean exportRst = formInstancePage.isExportCombineSucceed(fileType, module, null, "test export", location);
            if(!exportRst)
            {
                exportRst=true;//expected job status is failed,so set exportresult to true.
            }
            formInstancePage.closeFormInstance();
            formDetail = listPage.getFormDetailInfo(1);
            if (formDetail.get(10).equalsIgnoreCase("Failure"))
            {
                JobManagerPage jobManagerPage = listPage.enterJobManagerPage();
                List<String> jobInfo = jobManagerPage.getLatestJobInfo();
                String parentJobStatus=jobInfo.get(8);
                boolean compare=parentJobStatus.equals("IN PROGRESS");
                while(compare)
                {
                    refreshPage();  //when parent status is in progress,we need to wait for it to Failure.
                    jobInfo = jobManagerPage.getLatestJobInfo();
                    parentJobStatus=jobInfo.get(8);
                    compare=parentJobStatus.equals("IN PROGRESS");
                }
                assertThat(jobInfo.get(8)).isEqualTo("FAILURE");
                JobDetailsPage jobDetailsPage = jobManagerPage.enterJobDetailsPage(1);
                Map<Integer, String> combineStatus = jobDetailsPage.getOrderComineExportStatus(1);
                assertThat(combineStatus.get(0)).isEqualTo("NO RUN");
                assertThat(combineStatus.get(1)).isEqualTo("NO RUN");
                assertThat(combineStatus.get(2)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(3)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(4)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(5)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(6)).isEqualTo("SUCCESS");
                assertThat(combineStatus.get(7)).isEqualTo("FAILURE");
                JobResultPage jobResultPage=jobDetailsPage.enterCombineJobResultPage("FED|0001|FR2052A|2", "CombineExecution",1);
                List<String> logs = jobResultPage.getLog();
                if ((logs.get(2)).startsWith("XML Job Validation validation error"))
                {
                    testRst = true;
                }
            }

        }
        catch (Exception e)
        {
            testRst = false;
            // e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        finally
        {
            writeTestResultToFile(caseID, testRst, "CombineExport");
            String SQL = "Update CFG_EXPORT_FORMAT set SCHEMA_LOCATION='5GCommentSchema.xsd' where SCHEMA_LOCATION='5GCommentSchema-Nagative.xsd' and EXPORT_FORMAT_TYPE='XSLT-Combine'";
            DBQuery.update(SQL);
        }
    }
}
