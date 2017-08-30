package com.lombardrisk.utils.fileService;

public class FileContent
{
	private int row;
	private int column;
	private String content;

	public FileContent(int row, int column, String content)
	{
		this.row = row;
		this.column = column;
		this.content = content;
	}

	public int getRow()
	{
		return row;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof FileContent))
		{
			return false;
		}

		FileContent fObj = (FileContent) obj;
		if (content == null)
		{
			if (fObj.content != null)
			{
				return false;
			}
		}
		return content.equals(fObj.content);
	}

	@Override
	public int hashCode()
	{
		if (content == null || content.trim().isEmpty())
		{
			return 3;
		}

		return content.hashCode();
	}
}
