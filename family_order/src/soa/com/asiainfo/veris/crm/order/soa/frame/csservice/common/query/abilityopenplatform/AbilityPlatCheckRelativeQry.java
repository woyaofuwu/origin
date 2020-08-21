package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AbilityPlatCheckRelativeQry {
	
	private final static Logger logger = Logger.getLogger(Dao.class);
	/**
	 * 电商平台获取限制用户数据
	 * 
	 * @param tagCode
	 * @param userTag
	 * @return
	 * @throws Exception
	 */
	public static IData getTagInfo() throws Exception {
		IDataset tagList = new DatasetList();
		IData cond = new DataMap();
		cond.put("TAG_CODE", "CS_NUM_OPENLIMITONABILITY");
		cond.put("USE_TAG", "0");
		SQLParser parser = new SQLParser(cond);
		parser
				.addSQL("  SELECT tag_number FROM td_s_tag WHERE tag_code =:TAG_CODE AND use_tag=:USE_TAG");
		tagList = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		return tagList.size() > 0 ? tagList.getData(0) : new DataMap();
	}

	/**
	 * 得到能力平台证件类型
	 * 
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static IDataset getAbilityPsptTypeCode() throws Exception {
		IData cond = new DataMap();
		cond.put("TYPE_ID", "ABILITY_PSPT_TYPE_CODE");
		return Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", cond,Route.CONN_CRM_CEN);
	}

	/**
	 * 由集团身份证件编码得到TD_S_PASSPORTTYPE配置相同 的类型编码
	 * 
	 * @param PnetTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRealyPsptTypeCodeByPnet(IData data)
			throws Exception {
		SQLParser parser = new SQLParser(data);
		parser
				.addSQL("SELECT  DATA_ID PSPT_TYPE_CODE FROM   TD_S_STATIC WHERE TYPE_ID=:PNET_TYPE_CODE");
		return Dao.qryByParse(parser);
	}

	/***************************************************************************
	 * 是否办理了宽带, 且在有效期内
	 * 
	 * @param pd
	 * @param td
	 * @return true 是办理了宽带, 且在有效期内
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static boolean getWidenetAcctList(IData data) throws Exception {
		IData inparam = new DataMap();
		boolean bool = false;
		inparam.put("USER_ID", data.getString("USER_ID"));
		IDataset dataset = Dao.qryByCode("TF_F_USER_WIDENET_ACT",
				"SEL_BY_USERID_WIDENET_ACT", inparam);
		if (dataset != null && dataset.size() > 0) {
			bool = true;
		}
		return bool;
	}

	/**
	 * 从活动表获取合约的信息
	 * 
	 * @param pd
	 * @param userId
	 *            用户id
	 * @return
	 * @throws Exception
	 */
	public static IData queryContractInfo(String userId) throws Exception {

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PROCESS_TAG", "0");
		param.put("CAMPN_TYPE", "ct001");
		IDataset result = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_BY_PK",
				param);
		return (result.size() > 0) ? result.getData(0) : new DataMap();
	}

	/**
	 * 根据商品id找出crm测的信息
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCtrmProduct(String goodsId) throws Exception {

		IData ctrmData = new DataMap();
		ctrmData.put("GOODS_ID", goodsId);
		IDataset result = Dao.qryByCode("TD_B_CTRM_RELATION","SEL_BAT_BY_CTRM_PROID", ctrmData,Route.CONN_CRM_CEN);
		return result;
	}

	/**
	 * 根据产品id找出crm测的信息
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCtrmProductByBossId(String checkID,String eparchy_code)
			throws Exception {
		IData ctrmId = new DataMap();
		ctrmId.put("CTRM_PRODUCT_ID", checkID);
		ctrmId.put("EPARCHY_CODE", eparchy_code);
		IDataset result = Dao.qryByCode("TD_B_CTRM_RELATION",
				"SEL_BY_CTRM_PRODUCT_ID", ctrmId,Route.CONN_CRM_CEN);
		return result;
	}

	/**
	 * 得到用户的所有元素
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserElementByUserId(IData td) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID", td.getString("USER_ID"));
		params.put("PRODUCT_ID", td.getString("PRODUCT_ID"));
		IDataset tmp1 = Dao.qryByCode("TF_F_USER_PRODUCT",
				"SEL_PRODUCT_ELEMENT_SVC", params);
		IDataset tmp2 = Dao.qryByCode("TF_F_USER_PRODUCT",
				"SEL_PRODUCT_ELEMENT_DISCNT", params);

		IDataset tmp = new DatasetList();

		for (int i = 0; i < tmp1.size(); i++) {
			IData dataa = new DataMap();
			dataa = tmp1.getData(i);
			tmp.add(dataa);
		}

		for (int i = 0; i < tmp2.size(); i++) {
			IData datab = new DataMap();
			datab = tmp2.getData(i);
			tmp.add(datab);
		}

		return tmp;
	}

	/**
	 * 查看产品的转换关系
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static boolean checkProductLimit(IData data) throws Exception {

		IDataset ppResult = Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_BY_PK",
				data);
		;
		IDataset pResult = Dao.qryByCode("TD_S_PRODUCT_TRANS", "SEL_BY_PK_NEW",
				data);
		;
		if (ppResult != null && ppResult.size() == 0 && pResult != null
				&& ppResult.size() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 查看服务
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkServiceLimit(IData data) throws Exception {
		IDataset ppResult = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERSVC_ID",
				data);
		return ppResult;
	}

	/**
	 * 互斥信息
	 * 
	 * @param pd
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkElementLimit( IData inParam)
			throws Exception {
		IDataset dataLimitInfo = Dao.qryByCode("TD_B_ELEMENT_LIMIT","SEL_ELEMENT_BY_IDA", inParam);
		return dataLimitInfo;
	}

    /**
     * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE,EPARCHY_CODE查询用户信息
     */
    public static IDataset getUserInfoBySn(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_USER", "SEL_BY_SNO", inparams);
    }
    

	/**
	 * 通过中间映射表, 找到对应关系
	 * 
	 * @param pd
	 * @param goodsID
	 * @param checkID
	 * @return
	 * @throws Exception
	 */
	public static IDataset getBossProByCtrmId(String goodsID, String checkID,String eparchyCode) throws Exception {
	    if (!"".equals(checkID)) {
			return AbilityPlatCheckRelativeQry.getCtrmProductByBossId(checkID,eparchyCode);
			// 有商品 和 产品
		}
		
		/*if (!"".equals(goodsID) && "".equals(checkID)) { // 有商品的
			return AbilityPlatCheckRelativeQry.getCtrmProduct(goodsID);
		}
		// 只有产品
		if ("".equals(goodsID) && !"".equals(checkID)) {
			return AbilityPlatCheckRelativeQry.getCtrmProductByBossId(checkID,eparchyCode);
			// 有商品 和 产品
		}
		if (!"".equals(goodsID) && !"".equals(checkID)) {
			IDataset dataSet = new DatasetList();
			dataSet = AbilityPlatCheckRelativeQry.getCtrmProduct(goodsID);
			IDataset rsProduct = AbilityPlatCheckRelativeQry
					.getCtrmProductByBossId(checkID,eparchyCode);

			if (!IDataUtil.isNotEmpty(rsProduct)) {
				for (int i = 0; i < rsProduct.size(); i++) {
					dataSet.add(rsProduct.getData(i));
				}
			}
			dataSet = DataHelper.distinct(dataSet, "PRODUCT_ID", ",");
			return dataSet;
		}*/
		return null;
	}
	
	 /**
	  * 得到各个crm库的dao的执行
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	private  static CrmDAO getCrmDAO(String routeId) throws Exception
	    {
	        if (StringUtils.isBlank(routeId))
	        {
	            routeId = BizRoute.getRouteId();

	            if (logger.isDebugEnabled())
	            {
	                logger.debug("getCrmDAO路由连接到[" + routeId + "]");
	            }
	        }
	        else
	        {
	            if (logger.isDebugEnabled())
	            {
	                logger.debug("getCrmDAO指定连接到[" + routeId + "]");
	            }
	        }

	        return CrmDAO.createDAO(CrmDAO.class, routeId);
	    }
	 /**
     * 查询是否有预约业务
     */
	   
		public static IDataset getUserChangeInfo(String  userId) throws Exception {
		   IData   inparam=new  DataMap();
		   inparam.put("USER_ID", userId);
		   for (String routeId : Route.getAllCrmDb())
	        { 
	            CrmDAO dao = getCrmDAO(routeId);
	            IDataset changeInfo = dao.queryListByCodeCode("TF_F_USER_INFOCHANGE","SEL_BY_USERID_NXTVALID", inparam);
	            if(changeInfo!=null&&changeInfo.size()>0){
	            	return changeInfo;
		        }
	        }
		   return null;
	   }
	    //根据优惠ID，查看用户是否存在该优惠。
		public  static  IDataset  getUserProductById(String  userId) throws Exception{
			   IData   inparam=new  DataMap();
			   inparam.put("USER_ID", userId);
			  // inparam.put("DISCNT_CODE", discntCode);
			   for (String routeId : Route.getAllCrmDb())
		        { 
		            CrmDAO dao = getCrmDAO(routeId);
		            IDataset userDiscnt = dao.queryListByCodeCode("TF_F_USER_DISCNT","SEL_BY_USERID_ALL", inparam);
		            if(userDiscnt!=null&&userDiscnt.size()>0){
		            	return userDiscnt;
			        }
		        }
			   return null;
		}
		
}
