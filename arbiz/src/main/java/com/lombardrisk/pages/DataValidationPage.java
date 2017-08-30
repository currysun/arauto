package com.lombardrisk.pages;

import org.apache.commons.io.FileUtils;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

/**
 * Created by leo tu on 3/1/2017.
 */
public class DataValidationPage extends AbstractPage
{
	public DataValidationPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
	}

	public String exportDataValidation() throws Exception
	{
		String dir = FileUtils.getUserDirectoryPath() + "/downloads";
		String latestFile = getLatestFile(dir);
		if (httpDownload)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("dvp.export").click();
			waitStatusDlg();
			TestCaseManager.getTestCase().stopTransaction();
			String exportedFile = System.getProperty("user.dir") + "/" + TestCaseManager.getTestCase().getDownloadFile();
			return getOriginalFile(exportedFile, latestFile, setOriginalName);
		}
		else
		{
			element("dvp.export").click();
			waitStatusDlg();
			return downloadFile(null, latestFile, null);
		}
	}

	public boolean isFilterExist() throws Exception
	{
		if (element("dvp.filter.ruleId").isDisplayed() && element("dvp.filter.ruleType").isDisplayed() && element("dvp.filter.dataSchedule").isDisplayed()
				&& element("dvp.filter.targetColumn").isDisplayed() && element("dvp.filter.statusLabel").isDisplayed())
			return true;
		else
			return false;
	}

	public void filter(String type, String keyText) throws Exception
	{
		if (type.equalsIgnoreCase("id"))
			element("dvp.filter.ruleId").input(keyText);
		else if (type.equalsIgnoreCase("Type"))
			element("dvp.filter.ruleType").input(keyText);
		else if (type.equalsIgnoreCase("dataSchedule"))
			element("dvp.filter.dataSchedule").input(keyText);
		else if (type.equalsIgnoreCase("targetColumn"))
			element("dvp.filter.targetColumn").input(keyText);
		else if (type.equalsIgnoreCase("status"))
		{
			waitStatusDlg();
			element("dvp.filter.statusLabel").click();
			waitStatusDlg();
			element("dvp.filter.statusItem", keyText).click();
		}
		waitStatusDlg();
		Thread.sleep(5000);
	}

	public int getRowNum() throws Exception
	{
		return element("dvp.rowCount").getNumberOfMatches();
	}
}
