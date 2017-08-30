package com.lombardrisk.pages;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

/**
 * Created by zhijun dai on 12/5/2016.
 */
public class RejectionsPage extends AbstractPage
{
	/**
	 * @param webDriverWrapper
	 */
	public RejectionsPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
	}

	/**
	 * export rejections log
	 */
	public String exportRejectionsLog() throws Exception
	{
		waitStatusDlg();
		waitThat("rp.export").toBeClickable();
		String dir = FileUtils.getUserDirectoryPath() + "\\downloads";
		String latestFile = getLatestFile(dir);
		if (httpDownload)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("rp.export").click();
			TestCaseManager.getTestCase().stopTransaction();
			String exportedFile = TestCaseManager.getTestCase().getDownloadFile();
			String oldName = new File(exportedFile).getName();
			String path = new File(exportedFile).getAbsolutePath().replace(oldName, "");
			String fileName = TestCaseManager.getTestCase().getDefaultDownloadFileName();
			String file = null;
			if (fileName == null || fileName.length() == 0)
			{
				file = downloadFile(null, latestFile, null);
			}
			else
			{
				renameFile(path, oldName, fileName);
				file = path + fileName;
			}
			return file;
		}
		else
		{
			element("rp.export").click();
			waitStatusDlg();
			return downloadFile(null, latestFile, null);
		}
	}

	/**
	 * Set the filters
	 */
	public void setFilters(String[] filters) throws Exception
	{
		element("rp.filterBtn").click();
		waitStatusDlg();
		deleteFilters();
		for (int i = 1; i <= filters.length; i++)
		{
			String[] parameters = filters[i - 1].split(",");
			element("rp.addFilterBtn").click();
			element("rp.filterName", i + "").selectByVisibleText(parameters[0]);
			waitStatusDlg();
			element("rp.filterCondition", i + "").selectByVisibleText(parameters[1]);
			waitStatusDlg();
			if (parameters[2].trim() != null && !parameters[2].trim().equalsIgnoreCase(""))
			{
				List<IWebDriverWrapper.IWebElementWrapper> elements = element("rp.filterValue", i + "").getAllMatchedElements();
				for (int j = 0; j < elements.size(); j++)
				{
					elements.get(j).input(parameters[2 + j]);
					if (elements.get(j).getAttribute("id").contains("date"))
					{
						waitStatusDlg();
						element("rp.currDay").click();
						waitStatusDlg();
					}
				}
				waitStatusDlg();
			}
		}
		element("rp.saveFilterBtn").click();
		waitStatusDlg();
	}

	/**
	 * Delete all filter
	 */
	public void deleteFilters() throws Exception
	{
		if (element("rp.deleteFilterBtn").isDisplayed())
		{
			List<IWebDriverWrapper.IWebElementWrapper> elements = element("rp.deleteFilterBtn").getAllMatchedElements();
			for (int i = 0; i < elements.size(); i++)
			{
				elements.get(i).click();
				waitStatusDlg();
			}
		}
	}
}
