
package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class QueryInfoUtil
{
    /**
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryAgentUserBackListByCond(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TS_S_USER_BACK", "SEL_BY_DEPARTCODE_DEPARTKINDCODE", param, pagination);
        
    }
    
    /**
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryAgentUserBackListByCond_1(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TS_S_USER_BACK", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_1", param, pagination);
        
    }
    
    public static IDataset qryAgentUserBackListByCond_2(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TS_S_USER_BACK", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_2", param, pagination);
        
    }
    
    
    public static IDataset qryAgentUserBackListByCond_3(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TS_S_USER_BACK", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_3", param, pagination);
        
    }
    
    /**
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryAgentUserListByCond_1(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TM_S_USER_BACK_CURRETIME", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_1", param, pagination);        
    }
//    
//    public static IDataset qryAgentUserListByCond_2(IData param, Pagination pagination) throws Exception
//    {
//        return Dao.qryByCode("TM_S_USER_BACK_CURRETIME", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_2", param, pagination);
//        
//    }
//    
//    
//    public static IDataset qryAgentUserListByCond_3(IData param, Pagination pagination) throws Exception
//    {
//        return Dao.qryByCode("TM_S_USER_BACK_CURRETIME", "SEL_BY_DEPARTCODE_DEPARTKINDCODE_3", param, pagination);
//        
//    }

    /**
     * add by ouyk查询部门类别
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryDepartKinds(String eparchyCode) throws Exception
    {
        IData params = new DataMap();
        params.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_M_DEPARTKIND", "SEL_DEPART_KIND", params);
    }

    public static IDataset queryTfBTwoCheckByTimeout(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TWO_CHECK", "SEL_BY_TIMEOUT", param);
    }

    /**
     * add by ouyk 销售订单查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryB2COrder(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_ORDER_BY_CONDITION", param, pagination);
    }

    /**
     * 销售子订单查询
     * 
     * @param TId
     * @return
     * @throws Exception
     */
    public static IDataset querySubOrderByTId(String TId) throws Exception
    {
        if (StringUtils.isEmpty(TId))
            CSAppException.apperr(CrmCommException.CRM_COMM_683);
        IData params = new DataMap();
        params.put("TID", TId);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_SUBORDER_BY_TID", params);
    }

    /**
     * 查询销售子订单产品信息
     * 
     * @param OId
     * @return
     * @throws Exception
     */
    public static IDataset queryProductInfoByOID(String OId) throws Exception
    {
        IData params = new DataMap();
        if (StringUtils.isEmpty(OId))
            CSAppException.apperr(CrmCommException.CRM_COMM_683);
        params.put("OID", OId);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PRODUCT_BY_OID", params);
    }

    /**
     * 退款订单查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryReturnOrder(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_RETURN_ORDER", param, pagination);
    }

    /**
     * 查询退款子订单
     * 
     * @param returnId
     * @return
     * @throws Exception
     */
    public static IDataset queryReturnSubOrderByRefundId(String refundId) throws Exception
    {
        IData param = new DataMap();
        param.put("REFUND_ID", refundId);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_SUB_RETURN_ORDER_BYID", param);
    }

    /**
     * OID,TID,CTRM_PRODUCT_TYPE
     */
    public static IDataset queryProOrderByCtrmType(String oId, String tId, String crmProductType) throws Exception
    {
        IData param = new DataMap();
        param.put("OID", oId);
        param.put("TID", tId);
        param.put("CTRM_PRODUCT_TYPE", crmProductType);
        return Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PROORDER_BY_CTRMTYPE", param);
    }

    /**
     * 查询销售订单信息
     */
    public static IDataset queryListByTid(IData data) throws Exception
    {
        IDataset infos = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_NEED_DISTRIBUTION_ORDER", data);

        return infos;
    }
    
    /**
     * 更新订单子表
     * @param inputs
     * @return
     * @throws Exception
     */
    public static IDataset updateOrderInfo(IData inputs) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_BYID", inputs);
        return null;
    }
    
    public static void updateUserIMEI(IData inputs,String route) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_USERIMEI_ENDDATE", inputs,route);
    }
    
    public static void insertUserIMEI(IData inputs,String route) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "INS_ALL", inputs,route);
    }
    
    public static void updUserImei(IData inputs,String route) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_BY_USER_ID_IMEI", inputs,route);
    }
    
    //下发短信
    public static void sendSMS(IData input) throws Exception
    {
    	IDataset smsInfo = CommparaInfoQry.getCommparaInfoByCode("CSM", "5566", input.getString("PARAM_CODE"), input.getString("PARA_CODE1","0"), input.getString("EPARCHY_CODE","0898"));
      	
  		if (smsInfo != null && smsInfo.size() > 0) {
  			String content = smsInfo.getData(0).getString("PARAM_NAME","");
  			IData channelInfo = StaticInfoQry.getStaticInfoByTypeIdDataId("ABILITY_CHANNEL_ID", input.getString("%101!"));
  			
  			String channelId = (channelInfo == null || channelInfo.isEmpty()) ?input.getString("%101!",""):channelInfo.getString("DATA_NAME","");
  			
  			content = content.replaceAll("%101!", channelId); 	//业务订购渠道
  			content = parseSmsData(input,"%102!",content);  //本地套餐编码
  			content = parseSmsData(input,"%103!",content);  //本地合约编码
  			content = parseSmsData(input,"%104!",content);  //套餐订购时间
  			content = parseSmsData(input,"%105!",content);  //合约订购时间
  			content = parseSmsData(input,"%106!",content);  //集团的产品编码
  			content = parseSmsData(input,"%107!",content);  //集团的合约编码
  			content = parseSmsData(input,"%108!",content);  //新的IMEI号
  			content = parseSmsData(input,"%109!",content);  //老的IMEI号
  			content = parseSmsData(input,"%110!",content);  //
  			
	        // 拼短信表参数
	        IData param = new DataMap();
	        param.put("NOTICE_CONTENT", content);
	        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
	        param.put("IN_MODE_CODE", "0");
	        param.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
	        param.put("RECV_ID", "99999999");
	        param.put("REFER_STAFF_ID", input.getString("TRADE_STAFF_ID",""));
	        param.put("REFER_DEPART_ID", input.getString("TRADE_DEPART_ID",""));
	        param.put("REMARK", "电商能力开发平台短信");
	        String seq = SeqMgr.getSmsSendId();
	        long seq_id = Long.parseLong(seq);
	        param.put("SMS_NOTICE_ID", seq_id);
	        param.put("PARTITION_ID", seq_id % 1000);
	        param.put("SEND_COUNT_CODE", "1");
	        param.put("REFERED_COUNT", "0");
	        param.put("CHAN_ID", "C009");
	        param.put("SMS_NET_TAG", "0");
	        param.put("RECV_OBJECT_TYPE", "00");
	        param.put("SMS_TYPE_CODE", "20");
	        param.put("SMS_KIND_CODE", "02");
	        param.put("NOTICE_CONTENT_TYPE", "0");
	        param.put("FORCE_REFER_COUNT", "1");
	        param.put("FORCE_OBJECT", "10086");
	        param.put("SMS_PRIORITY", "3000");
	        param.put("DEAL_STATE", "15");
	        param.put("SEND_TIME_CODE", "1");
	        param.put("SEND_OBJECT_CODE", "6");
	        param.put("REFER_TIME", SysDateMgr.getSysTime());
	        param.put("DEAL_TIME", SysDateMgr.getSysTime());
	        param.put("MONTH", SysDateMgr.getCurMonth());
	        param.put("DAY", SysDateMgr.getCurDay());
	        param.put("ISSTAT", "0");
	        param.put("TIMEOUT", "0");
	
	        Dao.insert("ti_o_sms", param,Route.getCrmDefaultDb());
  		}
    }
    
    /**
     * 传入的内容进行短信的转换操作
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public static String parseSmsData(IData data, String str, String content) throws Exception{
    	
    	String inputStr = data.getString(str,"");
    	
    	if(!"".equals(inputStr) && !"null".equals(inputStr)){
    		String strContent = inputStr;      //默认取初始进来的值信息
    		String tradeId = data.getString("TRADE_ID");
    		String vasDate = "";         //套餐生效时间
    		String contractDate = "";	 //合约生效时间
    		
    		if("%102!".equals(str)){    //本地套餐编码
    			String localVasId = inputStr;
    			
    			//IData productInfo =  ProductInfoQry.getProductInfo(localVasId, data.getString("EPARCHY_CODE","0898"));
    			IData productInfo =  UProductInfoQry.qryProductByPK(localVasId);//modify by duhj 2017/6/9

    			if(IDataUtil.isNotEmpty(productInfo)){
    				strContent = productInfo.getString("PRODUCT_NAME")+"套餐，内容："+productInfo.getString("PRODUCT_EXPLAIN");
    				content = content.replaceAll(str, strContent);
    			}
    			//获取对应的生效时间
    			vasDate = queryVasInfo(tradeId);
    		}
    		if("%103!".equals(str)){    //本地合约编码
    			String localContractId = inputStr;
    			IData productInfo=new DataMap();
    			try{
        			//IData productInfo = qryProductPackageInfoNoPriv(localContractId, data.getString("PARAM_CODE1"), data.getString("EPARCHY_CODE","0898"));
        			 productInfo = UPackageInfoQry.getPackageByPK(data.getString("PARAM_CODE1"));  
    			}catch (Exception e) {
    			 productInfo=new DataMap();
				}
      			if(IDataUtil.isNotEmpty(productInfo)){
    				strContent = productInfo.getString("PRODUCT_NAME")+"合约，内容："+productInfo.getString("PACKAGE_DESC");
    				content = content.replaceAll(str, strContent);
    			}
    			//查询合约的生效时间
    			contractDate = queryContractInfo(tradeId);

    		}

    		if("%106!".equals(str)){    //套餐编码
    			String vasId = inputStr;
    			//根据套餐的ID查询对应的产品信息
    			IData ctrmVas = new DataMap();
    			ctrmVas.put("VAS_ID", vasId);
    			IData vasData = qryVasInfo(ctrmVas);
    			if(IDataUtil.isNotEmpty(vasData)){
    				strContent = vasData.getString("VAS_NAME")+"套餐，内容："+vasData.getString("VAS_DESC");
    				content = content.replaceAll(str, strContent);
    			}
    			//获取对应的生效时间
    			vasDate = queryVasInfo(tradeId);
    		}
    		if("%107!".equals(str)){    //合约编码
    			String contractId = inputStr;
    			//根据合约的ID查询对应的产品信息
    			IData ctrmContract = new DataMap();
    			ctrmContract.put("CONTRACT_ID", contractId);
    			IData contractData = qryContractInfo(ctrmContract);
    			if(IDataUtil.isNotEmpty(contractData)){
    				strContent = contractData.getString("CONTRACT_NAME")+"合约，内容："+contractData.getString("CONTRACT_CONTENT");
    				content = content.replaceAll(str, strContent);
    			}

    			//查询合约的生效时间
    			contractDate = queryContractInfo(tradeId);
    		}
  			
    		if(!"".equals(vasDate) && vasDate != null ){
    			content = content.replaceAll("%998!", 		vasDate); //套餐生效时间
    		}
    		if(!"".equals(contractDate) && contractDate != null){
    			content = content.replaceAll("%999!", 	contractDate); //合约生效时间
    		}
    		content = content.replaceAll(str, strContent); //业务生效时间
    	}
    	return content;
    }
    
    /**
     * 获取对应套餐的生效时间
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static String queryVasInfo(String tradeId) throws Exception{
    	//根据套餐的ID查询对应的产品信息
    	String vasDate = "";
		IDataset tradeProductInfo = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
		if(IDataUtil.isNotEmpty(tradeProductInfo)){
			vasDate = tradeProductInfo.getData(0).getString("START_DATE");
		}
    	return vasDate;
    }
    
    /**
     * 获取对应合约的生效时间
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static String queryContractInfo(String tradeId) throws Exception{
    	//根据套餐的ID查询对应的产品信息
    	String contractDate = "";
    	//根据合约的ID查询对应的产品信息
		IDataset tradeContractInfo = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
		if(IDataUtil.isNotEmpty(tradeContractInfo)){
			contractDate = tradeContractInfo.getData(0).getString("START_DATE");
		}
    	return contractDate;
    }
    
    /**
	 * 查询合约数据
	 * @param input
	 * @throws Exception
	 */
	public static IData qryContractInfo(IData input) throws Exception{
		return Dao.qryByPK("TD_B_CTRM_CONTRACT", input, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 查询套餐数据
	 * 
	 * @param input
	 * @throws Exception
	 */
	public static IData qryVasInfo(IData input) throws Exception{
		return Dao.qryByPK("TD_B_CTRM_VAS", input, Route.CONN_CRM_CEN);
	}
	
	 /**
     * 根据PRODUCT_ID PACKAGE_ID EPARCHY_CODE查询产品类型信息 不判断权限
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData qryProductPackageInfoNoPriv(String productId, String packageId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PACKAGE_ID", packageId);
        data.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = Dao.qryByCode("TD_B_PRODUCT_PACKAGE", "SEL_INFO_BY_PRODUCT_PACKAGE_NO_PRIV", data, Route.CONN_CRM_CEN);
        return dataset.size() > 0 ? (IData) dataset.get(0) : null;
    }

}
