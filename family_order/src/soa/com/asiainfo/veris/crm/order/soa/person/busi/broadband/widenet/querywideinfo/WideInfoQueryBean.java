package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.querywideinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WideInfoQueryBean extends CSBizBean {
	private static final long serialVersionUID = 1L;

	public IData qryAlreadyDealtWideInfo(IData data) throws Exception {

		/**
		 * 请求参数
		 */
		String serviceNoType = data.getString("SERVICE_NO_TYPE");// 业务号码类型
		String serviceNo = data.getString("SERVICE_NO");// 业务号码
		String orderId = data.getString("ORDER_ID", "");// 订单编号
		String subOrderId = data.getString("SUB_ORDER_ID","");// 子订单编号
		String cityCode = data.getString("CITY_CODE","");// 地市编码，参见地市编码表，若入参有该参数，返回的是该地市的宽带办理情况

		/**
		 * 响应参数
		 */
		IData result = new DataMap();
		result.put("SERVICE_NO", serviceNo);// 业务账号
		if (StringUtils.isBlank(serviceNo)) {
			return errorRerurn("业务号码为空");
		}
		
		if(!"".equals(orderId)&&!"".equals(subOrderId)){
			IDataset dadaset = queryGerlSubOrder(orderId,subOrderId);
			if (IDataUtil.isEmpty(dadaset)) {
				return errorRerurn("无订单信息");
			}
		}
		
		// 01：手机号码,02：宽带号码
		if ("01".equals(serviceNoType)) {
			serviceNo = "KD_"+serviceNo;
		}
		result.put("BOSS_SERVIE_WB_NO", serviceNo);// 省BOSS侧记录宽带账号
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serviceNo);
		if (IDataUtil.isEmpty(userInfo)) {
			result.put("WB_STATUS", "99");
			return errorRerurn("该业务号码,无用户信息");
		}
		
		// 获取三户信息
		UcaData uca = UcaDataFactory.getNormalUca(serviceNo);
		String custId = userInfo.getString("CUST_ID", "");
		IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);

		//帐号状态 --00：正常 --01：单向停机 02：停机 --03：预销户 04：销户
		result.put("WB_STATUS", "0"+userInfo.getString("REMOVE_TAG"));

		//客户姓名模糊化处理
		String customerName = custNameBlurConvert(custInfo.getString(
				"CUST_NAME", ""));
		result.put("CUSTOMER_NAME", customerName);
		
		String userId = userInfo.getString("USER_ID","");
		IDataset widenetInstallInfos = WidenetInfoQry.getUserWidenetInfo(userId);// 用户宽带安装信息
		if (IDataUtil.isEmpty(widenetInstallInfos)) {
			return errorRerurn("该业务号码，无宽带安装信息");
		}
		IData widenetInstallInfo = widenetInstallInfos.getData(0);
		result.put("ADDRESS", widenetInstallInfo.getString("DETAIL_ADDRESS"));// 宽带安装地址
		
		String city = userInfo.getString("EPARCHY_CODE");
		result.put("CITY", city.substring(1));// 归属地市
		if ("721".equals(result.getString("CITY"))) {
			result.put("CITY_NAME", "三亚");// 归属地市
		} else {
			result.put("CITY_NAME", "海口");// 归属地市
		}

		// 宽带业务类型 01：单宽带02：融合宽带
		IData relevanceInfo = queryVSerialNumberId(userId);
		if (IDataUtil.isEmpty(relevanceInfo)) {
			result.put("WB_TYPE", "01");
		}else{
			result.put("WB_TYPE", "02");
		}

		String planId = uca.getProductId();
		result.put("PLAN_ID", planId);// 主套餐产品编码.

		// 产品信息
		IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId,
				BizRoute.getRouteId());
		if (IDataUtil.isEmpty(productInfo)) {
			return errorRerurn("该业务号码，无产品信息");
		}

		result.put("PLAN_NAME", productInfo.getString("PRODUCT_NAME"));// 主套餐名称

		String startDate = productInfo.getString("START_DATE", "");
		String endData = productInfo.getString("END_DATE", "");
		String planCycle = productCycle(SysDateMgr.monthIntervalYYYYMM(
				startDate, endData));
		result.put("PLAN_CYCLE", planCycle);// 产品周期
		result.put("VALID_DATE", changeTimePattern(startDate));// 生效时间.主套餐生效的时间
		result.put("END_TIME", changeTimePattern(endData));// 失效时间.主套餐失效的时间

		String rate = WideNetUtil.getWidenetUserRate(serviceNo);
		if(!"0".equals(rate)){
			int widenetRate = Integer.valueOf(rate)/1024;
			result.put("MAX_RATE", widenetRate+"");// 宽带最高速率
		}else{
			result.put("MAX_RATE", "0");
		}

		// 当前套餐可抵扣金额 否, 当前套餐专项余额，在续约立即生效或次月生效做差价处理时可扣减的金额。单位：分, 返回非负整数
		result.put("SPECIAL_FEE", "0");
		result.put("OWE_FEE", uca.getLastOweFee());// 宽带欠费金额
		result.put("RENEW_TYPE", "05");// 可续约主套餐类型
		
		String netType = userInfo.getString("NET_TYPE_CODE", "");
		if(!("01".equals(netType)||"02".equals(netType)||"03".equals(netType))){
			netType="04";
		}
		result.put("NET_TYPE", netType);// 网络类型

		IDataset dataset = new DatasetList();
		if (StringUtils.isBlank(cityCode) || cityCode.equals(result.getString("CITY"))) {
			dataset.add(result);
		}
		
		IData result2 = new DataMap();
		result2.put("BIZ_CODE", "0000");
		result2.put("BIZ_DESC", "查询成功");
		result2.put("RESULT_TIME",
				SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		result2.put("RESULT", dataset);

		return result2;
	}

	/**
	 * 查询有没有订单信息
	 * @param orderId
	 * @param subOrderId
	 * @return
	 * @throws Exception
	 */
	public IDataset queryGerlSubOrder(String orderId,String subOrderId) throws Exception {
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("SUBORDER_ID", subOrderId);
		IDataset infos = Dao.qryByCode("TF_B_CTRM_GERLSUBORDER", "SEL_BY_ORDER_ID_SUB", param,Route.CONN_CRM_CEN);
		return infos;
	}
	
	/**
	 * 根据user_id_b查询虚拟号码关联信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public IData queryVSerialNumberId(String userId) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", "49");
		IDataset infos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_RELA_USERIDB", param);
		return infos.size()==0 ? new DataMap() : infos.getData(0);
	}
	
	
	/**
	 * 客户姓名模糊化处理。如 “王小二”返回 “xx二”（小写字母x）
	 * 
	 * @param customerName
	 * @return
	 */
	private String custNameBlurConvert(String customerName) {
		// 客户姓名 --需要模糊化处理。如 “王小二”返回 “xx二”（小写字母x）
		if (customerName == null || customerName.length() == 0) {
			return "";
		}
		customerName =customerName.replaceAll("[^x00-xff]|\\w","x")+customerName.charAt(customerName.length()-1);
		customerName = customerName.substring(1);
		return customerName;
	}

	/**
	 * 根据月份，返回对应周期编码。 01：包三个月02：包半年03：包一年04：包两年05：包三年06：其他
	 * 
	 * @param month
	 * @return
	 */
	private String productCycle(int month) {
		if (month == 3)
			return "01";
		if (month == 6)
			return "02";
		if (month == 12)
			return "03";
		if (month == 24)
			return "04";
		if (month == 36)
			return "05";
		return "06";
	}

	/**
	 * yyyy-MM-dd HH:mm:ss转成yyyyMMddhhmmss格式
	 * @param strDate
	 * @return
	 * @throws Exception
	 */
	private String changeTimePattern(String strDate)throws Exception{
    	DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
    	Date date = (Date) sdf1.parse(strDate);  
    	DateFormat sdf2 = new SimpleDateFormat("yyyyMMddhhmmss"); 
    	return sdf2.format(date);
    }
	  
	private IData errorRerurn(String bizDesc) throws Exception {
		IData result = new DataMap();
		result.put("BIZ_CODE", "2999");
		result.put("BIZ_DESC", "查询失败，"+bizDesc);
		result.put("RESULT_TIME",
				SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
		return result;
	}
	
}
