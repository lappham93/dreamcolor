package com.mit.file.utils;

import java.util.Arrays;
import java.util.List;

public class SizeUtils {
	private static List<Integer> templateSize = Arrays.asList(120, 240, 480);

	public static int calculateSize(int size) {
		int returnSize = 0;
		for (int i = 0; i < templateSize.size() - 1; i++) {
			if(i == 0 && size > 0 && size < templateSize.get(i)) {
				returnSize = templateSize.get(i);
				break;
			}
			if (size >= templateSize.get(i) && size < templateSize.get(i+1)) {
				returnSize = templateSize.get(i);
				break;
			}

			if(i == templateSize.size() - 2 && size >= templateSize.get(i+1)) {
				returnSize = templateSize.get(i);
				break;
			}
		}

		return returnSize;
	}

	public static int calculateSize(int size, List<Integer> templateSize) {
		int returnSize = size;
		int i = 0;
		for (int ds : templateSize) {
			if (size < ds) {
				if (i > 0) {
					returnSize = templateSize.get(i - 1);
				} else {
					returnSize = templateSize.get(0);
				}

				break;
			} else if (i == templateSize.size() - 1) {
				returnSize = templateSize.get(i);
				break;
			}
			i++;
		}

		return returnSize;
	}
}
