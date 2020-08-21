package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * 产品变更接口专用类
 * @author: wuwangfeng
 * @date: 2020/2/29 22:49
 */
public class ChangeProductInterface extends CSBizService {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ChangeProductInterface.class);
	
	private static final String USER_5G_YX_SVC = "84072842"; //5G优享服务
    private static final String USER_5G_JS_SVC = "84072843"; //5G极速服务

	/**
	 * 根据全球通等级绑定对应服务
	 * @author: wuwangfeng
	 * @date: 2020/2/29 22:49
	 */
	public IDataset bindGlobalGradeInfos(IData input) throws Exception{
		log.debug("bindGlobalGradeInfos()---------------------入参-->input= " + input);
		String serviceNumber = input.getString("SERIAL_NUMBER", "");
		IDataset result = new DatasetList();
		
		// 根据SN查询用户全球通等级信息
		IData userClassInfo = UserClassInfoQry.queryUserClassBySN(input);
		if (userClassInfo != null) {
			String userClass = userClassInfo.getString("USER_CLASS");
			
			// 获取正常用户的三户资料
			UcaData ucaData = UcaDataFactory.getNormalUca(serviceNumber);
			if (null != ucaData) {
				String userId = ucaData.getUser().getUserId();
				
				// 全球通银卡客户
				if ("1".equals(userClass)) {
					result = ChangeProductYX(userId, serviceNumber);
			    
			    // 全球通金卡客户
				} else if ("2".equals(userClass)) {
					Boolean discntFlag = false;
					
					// 根据userId查询用户正在生效的优惠信息
					IDataset discntList = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
					if (IDataUtil.isNotEmpty(discntList) && discntList.size()>0) {
						for (int i = 0; i < discntList.size(); i++) {
							IData data = discntList.getData(i);
							String discntCode = data.getString("DISCNT_CODE", "");
							// 5G智享298套餐(个人版) 和 5G智享369套餐(家庭版)
							if ("84018488".equals(discntCode) || "84018521".equals(discntCode)) {
								discntFlag = true;
							}
						}
						
						//绑定5G极速服务
						if (discntFlag) {
							result = ChangeProductJS(userId, serviceNumber);
							
						//绑定5G优享服务
						} else {
							result = ChangeProductYX(userId, serviceNumber);
						}
					}					
			        			        
				// 全球通白金卡、钻卡客户	
				} else if ("3".equals(userClass) || "4".equals(userClass)) {
					result = ChangeProductJS(userId, serviceNumber);													        			        
				}
			}			
		}
					
		return result;
	}
	
	/**
	 * 绑定5G优享服务(84072842)
	 * @author: wuwangfeng
	 * @throws Exception 
	 * @date: 2020/4/13 17:46
	 */
	private IDataset ChangeProductYX(String userId, String serviceNumber) throws Exception {
		log.debug("ChangeProductYX()----------------wwf------>userId= "+userId+", serviceNumber= "+serviceNumber);
		IData param = new DataMap();
		IDataset elements = new DatasetList();
		
		// 已绑定服务不匹配则删除
		IDataset userSvcs_JS = UserSvcInfoQry.getSvcUserId(userId, USER_5G_JS_SVC);
        if (IDataUtil.isNotEmpty(userSvcs_JS) && userSvcs_JS.size()>0) {
        	IData data = new DataMap();
        	data.put("ELEMENT_ID", USER_5G_JS_SVC);
        	data.put("ELEMENT_TYPE_CODE", "S");
        	data.put("MODIFY_TAG", "1");// 删除
        	elements.add(data);			        	
		}
        
        // 没有绑定相应服务就新增
        IDataset userSvcs_YX = UserSvcInfoQry.getSvcUserId(userId, USER_5G_YX_SVC);
        if (IDataUtil.isEmpty(userSvcs_YX)) {
        	IData data = new DataMap();
        	data.put("ELEMENT_ID", USER_5G_YX_SVC);
        	data.put("ELEMENT_TYPE_CODE", "S");
        	data.put("MODIFY_TAG", "0");// 新增
        	elements.add(data);			        	
		}
        
        // 产品变更接口（多元素）
        if (IDataUtil.isNotEmpty(elements) && elements.size()>0) {
        	param.put("ELEMENTS", elements);
        	param.put("SERIAL_NUMBER", serviceNumber);
        	param.put("IS_NEED_SMS", false); //不发送短信
        	return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", param);
		}
        
        return null;
	}
	
	/**
	 * 绑定5G极速服务(84072843)
	 * @author: wuwangfeng
	 * @date: 2020/4/13 17:46
	 */
	private IDataset ChangeProductJS(String userId, String serviceNumber) throws Exception {
		log.debug("ChangeProductJS()----------------wwf------>userId= "+userId+", serviceNumber= "+serviceNumber);
		IData param = new DataMap();
		IDataset elements = new DatasetList();
		
		// 已绑定服务不匹配则删除
        IDataset userSvcs_yx = UserSvcInfoQry.getSvcUserId(userId, USER_5G_YX_SVC);
        if (IDataUtil.isNotEmpty(userSvcs_yx) && userSvcs_yx.size()>0) {
        	IData data = new DataMap();
        	data.put("ELEMENT_ID", USER_5G_YX_SVC);
        	data.put("ELEMENT_TYPE_CODE", "S");
        	data.put("MODIFY_TAG", "1");// 删除
        	elements.add(data);
		}
        
        // 没有绑定相应服务就新增
        IDataset userSvcs_JS = UserSvcInfoQry.getSvcUserId(userId, USER_5G_JS_SVC);;
        if (IDataUtil.isEmpty(userSvcs_JS)) {
        	IData data = new DataMap();
        	data.put("ELEMENT_ID", USER_5G_JS_SVC);
        	data.put("ELEMENT_TYPE_CODE", "S");
        	data.put("MODIFY_TAG", "0");// 新增
        	elements.add(data);
		}
        
        // 产品变更接口（多元素）
        if (IDataUtil.isNotEmpty(elements) && elements.size()>0) {
        	param.put("ELEMENTS", elements);
        	param.put("SERIAL_NUMBER", serviceNumber);
        	param.put("IS_NEED_SMS", false); //不发送短信
        	return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", param);
		}
		
		return null;
	}
	
	
	/**
	 * 根据全球通等级绑定对应服务
	 * @author: wuwangfeng
	 * @date: 2020/2/29 22:49
	 */
	public IDataset bindGlobalGradeInfo(IData input) throws Exception{
		log.debug("bindGlobalGradeInfo()---------------------入参-->input= " + input);
		String serviceNumber = input.getString("SERIAL_NUMBER", "");
		IDataset result = null;
		
		// 根据SN查询用户全球通等级信息
		IData userClassInfo = UserClassInfoQry.queryUserClassBySN(input);
		if (userClassInfo != null) {
			String userClass = userClassInfo.getString("USER_CLASS");
			
			// 获取正常用户的三户资料
			UcaData ucaData = UcaDataFactory.getNormalUca(serviceNumber);
			if (null != ucaData) {
				String userId = ucaData.getUser().getUserId();
				
				// 全球通银卡、金卡客户绑定5G优享服务(84072842)
				if ("1".equals(userClass) || "2".equals(userClass)) {
					// 等级已服务不匹配则删除不匹配服务
					IDataset userSvcs_JS = UserSvcInfoQry.getSvcUserId(userId, USER_5G_JS_SVC);
			        if (IDataUtil.isNotEmpty(userSvcs_JS) && userSvcs_JS.size()>0) {
			        	IData data = new DataMap();
			        	data.put("SERIAL_NUMBER", serviceNumber);
			        	data.put("ELEMENT_ID", USER_5G_JS_SVC);
			        	data.put("ELEMENT_TYPE_CODE", "S");
			        	data.put("MODIFY_TAG", "1");// 删除
			        	data.put("BOOKING_TAG", "0");			        	
			        	result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);			        	
					}
			        
			        //新增匹配服务
			        IDataset userSvcs_YX = UserSvcInfoQry.getSvcUserId(userId, USER_5G_YX_SVC);
			        if (IDataUtil.isEmpty(userSvcs_YX)) {
			        	IData data = new DataMap();
			        	data.put("SERIAL_NUMBER", serviceNumber);
			        	data.put("ELEMENT_ID", USER_5G_YX_SVC);
			        	data.put("ELEMENT_TYPE_CODE", "S");
			        	data.put("MODIFY_TAG", "0");// 新增
			        	data.put("BOOKING_TAG", "0");			        	
			        	result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);			        	
					}
			        
				// 全球通白金卡、钻卡客户绑定5G极速服务(84072843)	
				} else if ("3".equals(userClass) || "4".equals(userClass)) {
					//等级已服务不匹配则删除不匹配服务
			        IDataset userSvcs_yx = UserSvcInfoQry.getSvcUserId(userId, USER_5G_YX_SVC);
			        if (IDataUtil.isNotEmpty(userSvcs_yx) && userSvcs_yx.size()>0) {
			        	IData data = new DataMap();
			        	data.put("SERIAL_NUMBER", serviceNumber);
			        	data.put("ELEMENT_ID", USER_5G_YX_SVC);
			        	data.put("ELEMENT_TYPE_CODE", "S");
			        	data.put("MODIFY_TAG", "1");// 删除
			        	data.put("BOOKING_TAG", "0");			        	
			        	result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);			        	
					}
			        
			        //新增匹配服务
			        IDataset userSvcs_JS = UserSvcInfoQry.getSvcUserId(userId, USER_5G_JS_SVC);;
			        if (IDataUtil.isEmpty(userSvcs_JS)) {
			        	IData data = new DataMap();
			        	data.put("SERIAL_NUMBER", serviceNumber);
			        	data.put("ELEMENT_ID", USER_5G_JS_SVC);
			        	data.put("ELEMENT_TYPE_CODE", "S");
			        	data.put("MODIFY_TAG", "0");// 新增
			        	data.put("BOOKING_TAG", "0");			        	
			        	result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", data);			        	
					}
					
				}
			}			
		}
					
		return result;
	}
	
}
