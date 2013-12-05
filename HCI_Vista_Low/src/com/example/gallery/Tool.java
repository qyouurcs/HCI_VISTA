package com.example.gallery;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

public class Tool implements Comparator<File> {

	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static void sortFile(File[] currentFiles) {
		if (currentFiles != null && currentFiles.length != 0)
			Arrays.sort(currentFiles, new Tool());
	}

	public int compare(File lhs, File rhs) {
		// TODO Auto-generated method stub
		Comparator<Object> cmp = Collator.getInstance(java.util.Locale.ENGLISH);
		return cmp.compare(lhs.getName(), rhs.getName());
	}
}
