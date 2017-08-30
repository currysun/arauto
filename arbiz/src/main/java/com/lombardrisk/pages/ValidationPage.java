package com.lombardrisk.pages;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

/**
 * Created by Ray Rui on 3/2/15. Refactored by Leo Tu on 1/29/16
 */
public class ValidationPage extends AbstractDrilldownPage
{
	public ValidationPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
		// TODO Auto-generated constructor stub
	}

	public void setPage(String text) throws Exception
	{
		element("vp.page").selectByVisibleText(text);
		waitStatusDlg();
	}

	public void setCellInput(String text) throws Exception
	{
		element("vp.cell").input(text);
		element("vp.cell").input("\n");
		waitStatusDlg();
	}

	public void setLevel(String text) throws Exception
	{
		element("vp.level").selectByVisibleText(text);
		waitStatusDlg();
	}

	public void setInstancing(String text) throws Exception
	{
		element("vp.instance").selectByVisibleText(text);
	}

	public void setResult(String text) throws Exception
	{
		element("vp.result").selectByVisibleText(text);
	}

	public void clearImageClick() throws Exception
	{
		element("vp.clear").click();
	}

	public String getPageText() throws Exception
	{
		return element("vp.page").getInnerText().trim();
	}

	public String getCellText() throws Exception
	{
		return element("vp.cell").getInnerText().trim();
	}

	public String getLevelText() throws Exception
	{
		return element("vp.level").getInnerText().trim();
	}

	public String getInstanceText() throws Exception
	{
		return element("vp.instance").getInnerText().trim();
	}

	public String getResultText() throws Exception
	{
		return element("vp.result").getInnerText().trim();
	}

	public String getResultText(int index) throws Exception
	{
		return element("vp.ruleRst", String.valueOf(index)).getInnerText().trim();
	}

	public void clearFilter() throws Exception
	{
		element("vp.clear").click();
		waitStatusDlg();
		Thread.sleep(1500);
	}

	public String getStatusText(int index) throws Exception
	{
		return element("vp.ruleStat", String.valueOf(index)).getInnerText().trim();
	}

	public String getRuleNo(int index, String ruleType) throws Exception
	{
		if (ruleType.equalsIgnoreCase("xval"))
			return element("vp.ruleID", String.valueOf(index)).getInnerText().split(" ")[2];
		else
			return element("vp.ruleID", String.valueOf(index)).getInnerText().split(" ")[1];
	}

	public String getValStatus(String ruleType, String ruleID, String cellName, String rowKey, String instance) throws Exception
	{
		String valStatus = null;
		boolean flag = true;
		boolean find = false;
		boolean lastPage = false;
		int count = 0;
		String startPage = null;
		for (int i = 1; i <= 3; i++)
		{
			if (element("vp.curPageNO", String.valueOf(i)).getAttribute("class").contains("ui-state-active"))
			{
				startPage = element("vp.curPageNO", String.valueOf(i)).getInnerText();
				break;
			}
		}

		int rowAmt = (int) element("vp.valTable").getRowCount();
		String curPage = null;
		while (flag)
		{
			for (int i = 1; i <= rowAmt; i++)
			{
				String ID = getRuleNo(i, ruleType);
				if (ID.equals(ruleID))
				{
					if (rowKey.equals(""))
					{
						String result = getResultText(i);
						if (result.startsWith("[PageInstance"))
						{
							String instanceCur = result.substring(result.indexOf(":") + 1, result.indexOf(":") + 2);
							if (instanceCur.equals(instance))
							{
								valStatus = getStatusText(i);
								find = true;
								flag = false;
								break;
							}
						}
						else
						{
							valStatus = getStatusText(i);
							find = true;
							flag = false;
							break;
						}

					}
					else
					{
						String result = getResultText(i);

						if (result.startsWith("[PageInstance"))
						{
							String instanceCur = result.substring(result.indexOf(":") + 1, result.indexOf(":") + 2);
							if (instanceCur.equals(instance))
							{
								String Rst_temp = result.replace("[PageInstance:" + instance + "]", "");
								String rowID = Rst_temp.substring(Rst_temp.indexOf(":") + 1, Rst_temp.indexOf(":") + 2);
								if (rowID.equals(rowKey))
								{
									valStatus = getStatusText(i);
									find = true;
									flag = false;
									break;
								}
							}
						}
						else
						{
							valStatus = getStatusText(i);
							find = true;
							flag = false;
							break;
						}

					}
				}

			}
			if (count > 0)
			{

				for (int i = 1; i <= 3; i++)
				{
					if (element("vp.curPageNO", String.valueOf(i)).getAttribute("class").contains("ui-state-active"))
					{
						curPage = element("vp.curPageNO", String.valueOf(i)).getInnerText();
						break;
					}
				}

				if (Integer.parseInt(curPage) - Integer.parseInt(startPage) >= 0)
				{
					flag = false;
					break;
				}

			}

			if (flag)
			{
				try
				{
					if (element("vp.nextPage").getAttribute("tabindex").equals("0"))
					{
						element("vp.nextPage").click();
						waitThat().timeout(300);
						waitStatusDlg();
					}
					else
					{
						lastPage = true;
					}
				}
				catch (Exception e)
				{
					flag = false;
				}
			}

			if (!find && lastPage && count < 1)
			{
				try
				{
					if (element("vp.nextPage").getAttribute("firtPage").equals("0"))
					{
						element("vp.nextPage").click();
						waitStatusDlg();
						waitThat().timeout(300);
						count++;

						for (int i = 1; i <= 3; i++)
						{
							if (element("vp.curPageNO", String.valueOf(i)).getAttribute("class").contains("ui-state-active"))
							{
								curPage = element("vp.curPageNO", String.valueOf(i)).getInnerText();
								break;
							}
						}

						if (Integer.parseInt(curPage) - Integer.parseInt(startPage) >= 0)
						{
							flag = false;
							break;
						}

					}
				}
				catch (Exception e)
				{

				}
			}

			if (!find && count > 0 && Integer.parseInt(curPage) - Integer.parseInt(startPage) >= 0)
			{
				flag = false;
			}

			rowAmt = (int) element("vp.valTable").getRowCount();
		}

		return valStatus;
	}

	public void clickRuleNO(String ruleNo) throws Exception
	{
		logger.info("Click rule[" + ruleNo + "] in VALIDATIONS");
		if (!element("vp.firtPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("vp.firtPage").click();
			waitStatusDlg();
			waitThat().timeout(300);
		}
		if (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
		{
			while (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
			{
				if (element("vp.ruleNo", ruleNo).isDisplayed())
				{
					element("vp.ruleNo", ruleNo).click();
					waitStatusDlg();
					break;
				}
				else
				{
					element("vp.nextPage").click();
					waitStatusDlg();
					waitThat().timeout(300);
				}
			}
		}
		else
		{
			element("vp.ruleNo", ruleNo).click();
			waitStatusDlg();
		}

	}

	public void clickRuleExpression(String ruleNo) throws Exception
	{
		if (!element("vp.firtPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("vp.firtPage").click();
			waitStatusDlg();
			waitThat().timeout(300);
		}
		if (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
		{
			while (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
			{
				if (element("vp.ruleNo", ruleNo).isDisplayed())
				{
					element("vp.ruleExpression", ruleNo).click();
					waitStatusDlg();
				}
				else
				{
					element("vp.nextPage").click();
					waitStatusDlg();
					waitThat().timeout(300);
				}
			}
		}
		else
		{
			element("vp.ruleExpression", ruleNo).click();
			waitStatusDlg();
		}

	}

	public String getRuleExpression(String ruleNo) throws Exception
	{
		String expression = null;
		if (!element("vp.firtPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("vp.firtPage").click();
			waitStatusDlg();
			waitThat().timeout(300);
		}
		if (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
		{
			while (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
			{
				if (element("vp.ruleNo", ruleNo).isDisplayed())
				{
					expression = element("vp.ruleExpression", ruleNo).getInnerText();
					break;
				}
				else
				{
					element("vp.nextPage").click();
					waitStatusDlg();
					waitThat().timeout(300);
				}
			}
		}
		else
			expression = element("vp.ruleExpression", ruleNo).getInnerText();

		return expression;
	}

	public List<String> splitRuleExpression(String ruleNo) throws Exception
	{
		String expression = getRuleExpression(ruleNo);
		String expressionTmp = expression;
		if (expressionTmp.contains("+"))
			expressionTmp = expressionTmp.replace("+", "~");
		if (expressionTmp.contains("-"))
			expressionTmp = expressionTmp.replace("-", "~");
		if (expressionTmp.contains("*"))
			expressionTmp = expressionTmp.replace("*", "~");
		if (expressionTmp.contains("/"))
			expressionTmp = expressionTmp.replace("/", "~");
		if (expressionTmp.contains("<="))
			expressionTmp = expressionTmp.replace("<=", "~");
		if (expressionTmp.contains(">="))
			expressionTmp = expressionTmp.replace(">=", "~");
		if (expressionTmp.contains("="))
			expressionTmp = expressionTmp.replace("=", "~");
		if (expressionTmp.contains("<"))
			expressionTmp = expressionTmp.replace("<", "~");
		if (expressionTmp.contains(">"))
			expressionTmp = expressionTmp.replace(">", "~");
		if (expressionTmp.contains("Round"))
			expressionTmp = expressionTmp.replace("Round", "").replace("(", "").replace(")", "");

		return Arrays.asList(expressionTmp.split("~"));
	}

	public String getRuleBackgroudColor(String ruleNo) throws Exception
	{
		String color = null;
		if (!element("vp.firtPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("vp.firtPage").click();
			waitStatusDlg();
			waitThat().timeout(300);

		}
		if (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
		{
			while (!element("vp.nextPage").getAttribute("class").contains("ui-state-disabled"))
			{
				if (element("vp.ruleNo", ruleNo).isDisplayed())
				{
					color = element("vp.ruleBackgroud", ruleNo).getCssValue("background-color");
					if (color.equalsIgnoreCase("rgba(181, 217, 245, 1)"))
						color = "Blue";
					break;
				}
				else
				{
					element("vp.nextPage").click();
					waitStatusDlg();
					waitThat().timeout(300);
				}
			}
		}
		else
		{
			color = element("vp.ruleBackgroud", ruleNo).getCssValue("background-color");
			if (color.equalsIgnoreCase("rgba(181, 217, 245, 1)"))
				color = "Blue";
		}

		return color;
	}

	/**
	 * export validation log
	 */
	public String exportValidationLog() throws Exception
	{
		waitStatusDlg();
		waitThat("vp.exportBtn").toBeClickable();
		String dir = FileUtils.getUserDirectoryPath() + "\\downloads";
		String latestFile = getLatestFile(dir);
		if (httpDownload)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("vp.exportBtn").click();
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
			element("vp.exportBtn").click();
			waitStatusDlg();
			return downloadFile(null, latestFile, null);
		}
	}

	/**
	 * input key word in cell
	 *
	 * @param text
	 * @throws Exception
	 */
	public void filterInCell(String text) throws Exception
	{
		element("vp.filter_Cell").input(text);
		clickEnterKey();
		Thread.sleep(500);
	}

	/**
	 * input key word in report line
	 *
	 * @param text
	 * @throws Exception
	 */
	public void filterInReportLine(String text) throws Exception
	{
		element("vp.filter_ReportLine").input(text);
		clickEnterKey();
		Thread.sleep(500);
	}

	/**
	 * input key word in type
	 *
	 * @param text
	 * @throws Exception
	 */
	public void filterInType(String text) throws Exception
	{
		element("vp.filter_Type").input(text);
		clickEnterKey();
		Thread.sleep(1500);
	}

	/**
	 * input key word in reg rule Id
	 *
	 * @param text
	 * @throws Exception
	 */
	public void filterInRegRuleID(String text) throws Exception
	{
		element("vp.filter_RegRuleId").input(text);
		clickEnterKey();
		Thread.sleep(1500);
	}

	/**
	 * enter first page
	 *
	 * @throws Exception
	 */
	public void enterFirstPage() throws Exception
	{
		try
		{
			if ("0".equals(element("vp.firstPage").getAttribute("tabindex")))
			{
				element("vp.firstPage").click();
				waitStatusDlg();
				Thread.sleep(1500);
			}
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}

	}

	/**
	 * enter last page
	 *
	 * @throws Exception
	 */
	public void enterLastPage() throws Exception
	{
		try
		{
			if ("0".equals(element("vp.lastPage").getAttribute("tabindex")))
			{
				element("vp.lastPage").click();
				waitStatusDlg();
				Thread.sleep(1500);
			}
		}
		catch (Exception e)
		{
			logger.warn(e.getMessage(), e);
		}
	}

}