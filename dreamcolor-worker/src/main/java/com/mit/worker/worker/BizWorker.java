package com.mit.worker.worker;
//package com.dkmobility.worker.worker;
//
//import java.util.List;
//
//import com.dkmobility.dao.biz.BizInfoDAO;
//import com.dkmobility.dao.service.ServiceCategoryCommonDAO;
//import com.dkmobility.dao.service.ServiceCommonDAO;
//import com.dkmobility.entities.biz.BizInfo;
//import com.dkmobility.models.ServiceModel;
//import com.dkmobility.utils.StringUtils;
//
//public class BizWorker {
//	public static void initializeBiz(int bizId) {
//		BizInfo biz = BizInfoDAO.getInstance().getById(bizId);
//		
//		if (biz != null) {
//			List<Integer> typeIds = StringUtils.stringToList(biz.getBusinessTypes(), true);
//			
//			if (typeIds != null && typeIds.size() > 0) {
//				List<Integer> categoryIds = ServiceCategoryCommonDAO.getInstance().listIdByTypeList(typeIds);
//				
//				if (categoryIds != null && categoryIds.size() > 0) {
//					List<Integer> serviceIds = ServiceCommonDAO.getInstance().listIdByCategoryList(categoryIds);
//					
//					if (serviceIds != null && serviceIds.size() > 0) {
//						ServiceModel.Instance.addCommonServices(bizId, serviceIds);
//					}
//				}
//			}
//		}
//	}
//}
