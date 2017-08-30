package com.lombardrisk.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.yiwan.webcore.test.TestCaseManager;
import org.yiwan.webcore.web.IWebDriverWrapper;

/**
 * Created by Leo Tu on 1/25/16
 */
public class AllocationPage extends AbstractPage
{

	public AllocationPage(IWebDriverWrapper webDriverWrapper)
	{
		super(webDriverWrapper);
	}

	/**
	 * get
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getSumCellValue() throws Exception
	{
		return element("ac.sumValue").getInnerText();
	}

	/**
	 * get sum rule expression
	 * 
	 * @return rule expression
	 * @throws Exception
	 */
	public String getSumRule() throws Exception
	{
		return element("ac.sumRule").getInnerText();
	}

	/**
	 * verify if exits sub item value
	 * 
	 * @param cellName
	 * @param cellValue
	 * @return true or false
	 * @throws Exception
	 */
	public boolean isSubItemValueExist(String cellName, String cellValue) throws Exception
	{
		boolean rst = false;
		int amt = (int) element("ac.allocation").getRowCount();
		for (int i = 0; i <= amt; i++)
		{
			if (element("ac.subCellName", String.valueOf(i)).getInnerText().equalsIgnoreCase(cellName))
			{
				if (element("ac.subCellValue", String.valueOf(i)).getInnerText().equalsIgnoreCase(cellValue))
				{
					rst = true;
					break;
				}
			}

		}
		return rst;
	}

	/**
	 * verify if exist item
	 * 
	 * @param CALCULATED
	 * @param CUSTOMER_CODE
	 * @param DRILL_REF
	 * @return true or false
	 * @throws Exception
	 */
	public boolean isAllocationItemValueExist(String CALCULATED, String CUSTOMER_CODE, String DRILL_REF) throws Exception
	{
		boolean find = false;
		int amt = (int) element("ac.allocationLeft").getRowCount();
		if (!element("ac.firstPage").getAttribute("class").contains("ui-state-disabled"))
		{
			element("ac.firstPage").click();
			waitStatusDlg();
		}
		boolean flag = true;
		while (flag)
		{

			for (int i = 1; i <= amt; i++)
			{
				if (element("ac.calculated", String.valueOf(i)).getInnerText().equals(CALCULATED))
				{
					if (element("ac.customerCode", String.valueOf(i)).getInnerText().equals(CUSTOMER_CODE))
					{
						if (element("ac.drillRef", String.valueOf(i)).getInnerText().equals(DRILL_REF))
						{
							find = true;
							flag = false;
							break;
						}
					}
				}
			}

			if (!find)
			{
				if (!element("ac.nextPage").getAttribute("class").contains("ui-state-disabled"))
				{
					element("ac.nextPage").click();
					waitStatusDlg();
					amt = (int) element("ac.allocationLeft").getRowCount();
				}
				else
				{
					flag = false;
				}
			}

		}
		return find;
	}

	/**
	 * export allocation
	 * 
	 * @return exported file
	 * @throws Exception
	 */
	public String exportAllocation() throws Exception
	{
		Thread.sleep(1000 * 10);
		waitStatusDlg();
		String dir = FileUtils.getUserDirectoryPath() + "/downloads";
		String latestFile = getLatestFile(dir);
		if (httpDownload)
		{
			TestCaseManager.getTestCase().startTransaction("");
			TestCaseManager.getTestCase().setPrepareToDownload(true);
			element("ac.export").click();
			TestCaseManager.getTestCase().stopTransaction();
			// element("fp.hidDrillDownTable").click();
			waitStatusDlg();
			String exportedFile = System.getProperty("user.dir") + "/" + TestCaseManager.getTestCase().getDownloadFile();
			return getOriginalFile(exportedFile, latestFile, setOriginalName);
		}
		else
		{
			element("ac.export").click();
			waitStatusDlg();
			// element("fp.hidDrillDownTable").click();
			waitStatusDlg();
			return downloadFile(null, latestFile, null);
		}
	}

	/**
	 * click cell link
	 * 
	 * @param cellName
	 * @throws Exception
	 */
	public void clickCellLink(String cellName) throws Exception
	{
		waitStatusDlg();
		element("ac.cellLink", cellName).click();
		waitStatusDlg();
		Thread.sleep(1000);
	}

	/**
	 * Get the sum table header
	 */
	public List<String> getSumTabHeader() throws Exception
	{
		waitStatusDlg();
		return element("ac.sumHeader").getAllInnerTexts();
	}

	/**
	 * Check the sum row of form variable
	 */
	public boolean checkSumRowOfFormVariable(String itemName, String value, String instance, String description, String expression) throws Exception
	{
		waitStatusDlg();
		boolean isIta = element("ac.sumRowOfVar", itemName, "1").getAttribute("style").contains("italic");
		boolean isValueEquals = element("ac.sumRowOfVar", itemName, "2").getInnerText().equals(value);
		boolean isInstanceEquals = element("ac.sumRowOfVar", itemName, "3").getInnerText().equals(instance);
		boolean isDescEquals = element("ac.sumRowOfVar", itemName, "4").getInnerText().equals(description);
		boolean isExpEquals = element("ac.sumRowOfVar", itemName, "5").getInnerText().equals(expression);
		return isIta && isValueEquals && isInstanceEquals && isDescEquals && isExpEquals;
	}

	/**
	 * get all column name
	 *
	 * @return
	 * @throws Exception
	 */
	public List<String> getAllFields() throws Exception
	{
		List<String> fields = new ArrayList<>();
		int i = 1;
		while (element("ac.columnField", String.valueOf(i)).isPresent())
		{
			fields.add(element("ac.columnField", String.valueOf(i)).getInnerText());
			i++;
		}
		return fields;
	}

	/**
	 * Set the filters
	 */
	public void setFilters(String[] filters, String needClearFilter) throws Exception
	{
		Thread.sleep(3000);
		waitStatusDlg();
		element("ac.filterBtn").click();
		waitStatusDlg();
		if ("true".equalsIgnoreCase(needClearFilter))
		{
			deleteFilters();
		}
		int existFilters = getExistFilterCount();
		for (int i = existFilters + 1; i <= filters.length + existFilters; i++)
		{
			String[] parameters = filters[i - 1 - existFilters].split(",");
			element("ac.addFilterBtn").click();
			element("ac.filterName", i + "").selectByVisibleText(parameters[0]);
			waitStatusDlg();
			element("ac.filterCondition", i + "").selectByVisibleText(parameters[1]);
			waitStatusDlg();
			if (parameters.length > 2 && parameters[2].trim() != null && !parameters[2].trim().equalsIgnoreCase(""))
			{
				List<IWebDriverWrapper.IWebElementWrapper> elements = element("ac.filterValue", i + "").getAllMatchedElements();
				for (int j = 0; j < elements.size(); j++)
				{
					elements.get(j).input(parameters[2 + j]);
					if (elements.get(j).getAttribute("id").contains("date"))
					{
						waitStatusDlg();
						element("ac.currDay").click();
						waitStatusDlg();
					}
				}
				waitStatusDlg();
			}
		}
		element("ac.saveFilterBtn").click();
		waitStatusDlg();
	}

	/**
	 * Delete all filter
	 */
	public void deleteFilters() throws Exception
	{
		if (element("ac.deleteFilterBtn").isDisplayed())
		{
			List<IWebDriverWrapper.IWebElementWrapper> elements = element("ac.deleteFilterBtn").getAllMatchedElements();
			for (int i = 0; i < elements.size(); i++)
			{
				elements.get(i).click();
				waitStatusDlg();
			}
		}
	}

	/**
	 * Get exist filter count
	 */
	public int getExistFilterCount() throws Exception
	{
		int existFilters = 0;
		if (element("ac.deleteFilterBtn").isDisplayed())
		{
			existFilters = element("ac.deleteFilterBtn").getAllMatchedElements().size();

		}
		return existFilters;
	}

	/**
	 * Get special column value of all rows
	 */
	public List<String> getColumnValueOfAllRows(String columnNumber) throws Exception
	{
		List<IWebDriverWrapper.IWebElementWrapper> elements;
		List<String> values = new ArrayList<>();
		while (true)
		{
			elements = element("ac.columnValue", columnNumber).getAllMatchedElements();
			for (IWebDriverWrapper.IWebElementWrapper element : elements)
			{
				values.add(element.getInnerText());
			}
			if (element("ac.nextPage").getAttribute("class").contains("disabled"))
			{
				break;
			}
			element("ac.nextPage").click();
		}
		return values;
	}

	/**
	 * get original value from drilldown
	 * 
	 * @return original value
	 * @throws Exception
	 */
	public String getOriginalValue() throws Exception
	{
		String text = element("ac.headValue").getInnerText().replace("Current Value:", "~").replace("Original Value:", "~");
		return text.split("~")[1];
	}

	/**
	 * get current value from drilldown
	 * 
	 * @return current value
	 * @throws Exception
	 */
	public String getCurrentValue() throws Exception
	{
		String text = element("ac.headValue").getInnerText();
		String text2 = text.replace("Current Value:", "~").replace("Original Value:", "~");
		return text2.split("~")[2];
	}

}
