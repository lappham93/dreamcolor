package com.mit.importData;

import com.mit.dao.distributor.DistributorDAO;
import com.mit.entities.distributor.Distributor;

public class InsertDistributor {
	
	public static void insertDistributor() {
		for (int i = 0; i < 5; i ++) {
			Distributor dis = new Distributor(0, "name " + i, "", "address " + i, "", "Ho chi minh", "Ho chi minh", "", "View nam", "123123", new Long(i), "", "");
			DistributorDAO.getInstance().insert(dis);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		insertDistributor();

	}

}
