package com.lombardrisk.utils.fileService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import jxl.read.biff.BiffException;

public interface IFileService
{
	Map<Integer, Map<Integer, FileContent>> parseFile(InputStream is) throws BiffException, IOException;
}
