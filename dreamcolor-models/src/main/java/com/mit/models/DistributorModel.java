package com.mit.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mit.dao.distributor.DistributorDAO;
import com.mit.entities.distributor.Distributor;

public class DistributorModel {
	
	public static final DistributorModel Instance = new DistributorModel();
	
	private DistributorModel(){};
	
	public Map<String, Object> getListDistributor(int count, int from) {
		Map<String, Object> rs = new HashMap<String, Object>();
		int err = ModelError.SUCCESS;
		boolean hasMore = false;
		List<Distributor> dtors = DistributorDAO.getInstance().getSliceByState("", "", from, count + 1, "name", true);
		if (dtors != null && dtors.size() > count) {
			hasMore = true;
			dtors = dtors.subList(0, count);
		}
		rs.put("hasMore", hasMore);
		rs.put("distributors", dtors);
		rs.put("err", err);
		
		return rs;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
