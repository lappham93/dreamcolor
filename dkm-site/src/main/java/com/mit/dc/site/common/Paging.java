/*
 * Copyright 2016 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.dc.site.common;

/**
 *
 * @author nghiatc
 * @since Jan 6, 2016
 */
public class Paging {
    public int currPage = 1;
	public int totalRecords = 0;
	public int pageSize = 10;
    public int numDisplay = 5;
	
	public int getTotalPages() {
		if (pageSize == 0) return 0;
		
		int totalPages = (totalRecords - 1) / pageSize + 1;
		
		return totalPages;
	}
    
    public static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
