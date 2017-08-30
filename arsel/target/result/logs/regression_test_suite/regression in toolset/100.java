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
            List<String> formDetail = listPage.getFormDetailInfo(1);
            FormInstancePage formInstancePage = listPage.openFormInstance(formCode, version, referenceDate);
			for(int i=0;i<100;i++) {
                export
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