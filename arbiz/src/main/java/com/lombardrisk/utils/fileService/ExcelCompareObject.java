package com.lombardrisk.utils.fileService;

import java.util.List;

public class ExcelCompareObject
{
	private List<FileContent> contents;

	public List<FileContent> getContents()
	{
		return contents;
	}

	public void setContents(List<FileContent> contents)
	{
		this.contents = contents;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof ExcelCompareObject))
		{
			return false;
		}

		ExcelCompareObject eObj = (ExcelCompareObject) obj;
		List<FileContent> contents2 = eObj.contents;
		if (contents == null)
		{
			if (contents2 != null)
			{
				return false;
			}
		}

		if (contents2 == null)
		{
			return false;
		}

		if (contents.size() != contents2.size())
		{
			return false;
		}

		boolean result = true;

		for (FileContent content : contents)
		{
			if (!contents2.contains(content))
			{
				result = false;
				break;
			}
		}

		return result;
	}

	@Override
	public int hashCode()
	{
		if (contents == null || contents.size() == 0)
		{
			return 2;
		}
		int hCode = 0;
		int length = contents.size();
		for (int i = length - 1; i >= 0; i--)
		{
			FileContent fileContent = contents.get(i);

			if (fileContent == null)
			{
				hCode = hCode + 1;
				continue;
			}
			hCode = hCode + fileContent.hashCode();
		}

		return hCode;
	}
}
