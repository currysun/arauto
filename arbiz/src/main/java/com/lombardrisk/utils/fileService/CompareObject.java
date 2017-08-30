package com.lombardrisk.utils.fileService;

import java.util.List;

public class CompareObject
{
	private List<FileContent> keys;
	private List<FileContent> values;

	public CompareObject()
	{

	}

	public List<FileContent> getKeys()
	{
		return keys;
	}

	public void setKeys(List<FileContent> keys)
	{
		this.keys = keys;
	}

	public List<FileContent> getValues()
	{
		return values;
	}

	public void setValues(List<FileContent> values)
	{
		this.values = values;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof CompareObject))
		{
			return false;
		}

		CompareObject cObj = (CompareObject) obj;
		return true;
	}
}
