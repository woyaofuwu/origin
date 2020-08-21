/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.userimei;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SalegoodsException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;

/**
 * @CREATED by gongp@2014-7-30 修改历史 Revision 2014-7-30 下午02:40:12
 */
public class ModifyUserImeiBean extends CSBizBean
{
    public IData modifyUserIMEI(IData data) throws Exception {
    	IData result = new DataMap();
    	
        IDataUtil.chkParam(data, "NEW_IMEI");
        IDataUtil.chkParam(data, "OLD_IMEI");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        /**
         * 光猫变更需求 chenxy3 20160622
         * */
        IDataUtil.chkParam(data,"MOB_CATALOG");//0=手机终端，默认;1=光猫;2=CPE

        UcaData ucaData = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER", ""));
        String strUserId = ucaData.getUserId();

        String strOldImei = data.getString("OLD_IMEI");
        String strNewImei = data.getString("NEW_IMEI");
        String mobCatalog = data.getString("MOB_CATALOG");
        
        //IPHONE6活动变更分支 20141030
        String iphone6_flag = data.getString("IPHONE6_FLAG");
        if(StringUtils.equals(iphone6_flag, "1")){
        	IDataset userSaleActives = UserSaleActiveInfoQry.queryUserSaleActiveByTag(strUserId);
        	String tradeId=null;
        	if (IDataUtil.isNotEmpty(userSaleActives)){
            	for(int i = 0; i < userSaleActives.size(); i++) {
            		IData userSaleActive=userSaleActives.getData(i);
            		String iphone6Imei=userSaleActive.getString("RSRV_STR22","");
            		if(StringUtils.equals(strOldImei, iphone6Imei)){
            			tradeId = userSaleActive.getString("RELATION_TRADE_ID");
            			break;
            		}
    			}	        
            }
        	if(StringUtils.isBlank(tradeId)){
	            CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据OLD_IMEI在TF_F_USER_SALE_ACTIVE未找到记录！");
	        }
        	IData param = new DataMap();
        	param.put("NEW_RES_CODE", strNewImei);
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
            param.put("RELATION_TRADE_ID", tradeId);
            param.put("OLD_RES_CODE", strOldImei);
        	result = CSAppCall.call("SS.ModifyIhone6SaleActiveIMEIRegSVC.tradeReg", param).getData(0);
        }
        //之前变更分支
        else{
        	
        	if("0".equals(mobCatalog)){
        	
		        IDataset dataset = UserSaleGoodsInfoQry.querySaleGoodsInfoByResCode(strUserId, strOldImei);
		
		        IData param = new DataMap();
		       
		
		        if (IDataUtil.isEmpty(dataset))
		        {
		            CSAppException.apperr(SalegoodsException.CRM_SALEGOODS_33);
		        }
		        else
		        {
		            IData temp = dataset.getData(0);
		
		            strUserId = temp.getString("USER_ID", "");
		            String tradeId = temp.getString("RELATION_TRADE_ID", "");
		
		            param.put("NEW_RES_CODE", strNewImei);
		            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
		            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
		            param.put("RELATION_TRADE_ID", tradeId);
		            param.put("OLD_RES_CODE", strOldImei);
		
		            result = CSAppCall.call("SS.ModifySaleActiveIMEIRegSVC.tradeReg", param).getData(0);
		            /*
		             * int UserSaleGoodsCount = Dao.executeUpdateByCodeCode("TF_F_USER_SALE_GOODS", "UPD_IMEI_BY_USERID",
		             * param); if (UserSaleGoodsCount <= 0) { // 换机修改用户IMEI失败
		             * CSAppException.apperr(SalegoodsException.CRM_SALEGOODS_32); } param.clear(); param.put("NEW_IMEI",
		             * strNewImei); param.put("TRADE_ID", tradeId); Dao.executeUpdateByCodeCode("TF_B_TRADE_SALE_GOODS",
		             * "UPD_BY_TRADEID", param); String syncSeqId = data.getString("X_SEQUENCEID", ""); if
		             * (StringUtils.isBlank(syncSeqId)) { syncSeqId = SeqMgr.getBillSynId(); } param.clear();
		             * param.put("SYNC_SEQUENCE", syncSeqId); param.put("X_SEQUENCEID", syncSeqId); param.put("BATCH_ID",
		             * tradeId); param.put("PRIORITY", "0"); param.put("RECV_TIME", SysDateMgr.getSysTime());
		             * param.put("RECV_EPARCHY_CODE", getVisit().getStaffEparchyCode()); param.put("RECV_CITY_CODE",
		             * getVisit().getCityCode()); param.put("RECV_DEPART_ID", getVisit().getDepartId());
		             * param.put("RECV_STAFF_ID", getVisit().getStaffId()); param.put("ACCT_ID", "0"); param.put("USER_ID",
		             * strUserId); param.put("TRADE_TYPE_CODE", 7045); // 营业端受理修改用户IMEI号 param.put("RECV_FEE", "0");
		             * param.put("CHANNEL_ID", "15000"); param.put("PAYMENT_ID", 0); param.put("WRITEOFF_MODE", "1");
		             * param.put("PAY_FEE_MODE_CODE", 0); param.put("PAYMENT_OP", 16000); param.put("OUTER_TRADE_ID", tradeId);
		             * param.put("NET_TYPE_CODE", "00"); param.put("RSRV_INFO1", strNewImei); param.put("RSRV_INFO2",
		             * strOldImei); param.put("RSRV_INFO3", tradeId); param.put("TRADE_ID", tradeId); param.put("CANCEL_TAG",
		             * "0"); param.put("DEAL_TAG", "0"); Dao.executeUpdateByCodeCode("TI_A_SYNC_RECV", "INS_ALL", param); String
		             * serialNumber = data.getString("SERIAL_NUMBER"); String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
		             * if ("240".equals(tradeTypeCode)) { IDataset tradeGoods =
		             * TradeSaleGoodsInfoQry.getTradeSaleGoodsByResTypeCode(tradeId); if (IDataUtil.isNotEmpty(tradeGoods)) {
		             * IData tradeGood = tradeGoods.getData(0); strNewImei = tradeGood.getString("RES_CODE");// /IMEI 号 } } if
		             * (!StringUtils.isBlank(strNewImei)) { String strSysdate = SysDateMgr.getSysTime(); param.clear();
		             * param.put("USER_ID", strUserId); param.put("END_DATE", strSysdate);
		             * Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "UPD_USERIMEI_ENDDATE", param);
		             * Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "DEL_ERR", param); param.put("SERIAL_NUMBER",
		             * serialNumber); param.put("IMEI", strNewImei); param.put("START_DATE", strSysdate); param.put("END_DATE",
		             * SysDateMgr.END_DATE_FOREVER); Dao.executeUpdateByCodeCode("TF_F_USER_IMEI", "INS_ALL", param); }
		             * param.clear(); param.put("X_SEQUENCEID", syncSeqId); param.put("SYNC_SEQUENCE", syncSeqId);
		             * param.put("TRADE_ID", tradeId); param.put("SYNC_DAY", SysDateMgr.getCurDay());
		             * Dao.executeUpdateByCodeCode("TI_B_SYNCHINFO", "INS_SYNCHINFO", param);
		             */
		        }
	        }else if("1".equals(mobCatalog)){
	        	//光猫更换
	        	//1、判断用户是否存在光猫信息
	        	IData inparams=new DataMap();
	        	inparams.put("USER_ID", strUserId);
	        	inparams.put("OLD_RESNO", strOldImei);
	        	IDataset chkResInfos=checkUserModermInfo(inparams);
	        	if(chkResInfos!=null &&chkResInfos.size()>0){
	        		inparams.put("NEW_RESNO", strNewImei);
	        		String callParams=data.getString("SERIAL_NUMBER", "")+","+strNewImei+","+strOldImei+","+mobCatalog;
	        		inparams.put("CALL_PARAMS", callParams);
	        		updUserModermInfo(inparams);
	        	}else{
	        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号及旧终端号无法找到用户光猫相关信息！");
	        	}
	        }else if("2".equals(mobCatalog)){
	        	//CPE更换
	        	//1、判断用户是否存在CPE信息
	        	IData inparams=new DataMap();
	        	inparams.put("USER_ID", strUserId);
	        	inparams.put("OLD_RESNO", strOldImei);
	        	IDataset chkResInfos=checkUserCpeResInfo(inparams);
	        	if(chkResInfos!=null &&chkResInfos.size()>0){
	        		inparams.put("NEW_RESNO", strNewImei);
	        		String callParams=data.getString("SERIAL_NUMBER", "")+","+strNewImei+","+strOldImei+","+mobCatalog;
	        		inparams.put("CALL_PARAMS", callParams);
	        		updUserCpeResInfo(inparams);
	        	}else{
	        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号及旧终端号无法找到用户CPE相关信息！");
	        	}
	        }else if("3".equals(mobCatalog)){
	        	//机顶盒
	        }
        }
        result.put("X_RESULTINFO", "OK");
        result.put("X_RSPDESC", "受理成功");
        return result;
    }
    
    /**
	 * 获取用户是否存在光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset checkUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("OLD_RESNO", inParam.getString("OLD_RESNO"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_FTTH_RES_BY_USER_ID", param);
        return userModerms;
    }
	/**
	 * 更新光猫串号
	 * */
	public static void updUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("NEW_RESNO", inParam.getString("NEW_RESNO"));
        param.put("OLD_RESNO", inParam.getString("OLD_RESNO"));
        param.put("CALL_PARAMS", inParam.getString("CALL_PARAMS"));
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_FTTH_RES_BY_USER_ID", param); 
    }
	
	/**
	 * 获取用户是否存在CPE信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset checkUserCpeResInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("OLD_RESNO", inParam.getString("OLD_RESNO"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_CPE_RESID_BY_USERID", param);
        return userModerms;
    }
	/**
	 * 更新CPE串号
	 * */
	public static void updUserCpeResInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("NEW_RESNO", inParam.getString("NEW_RESNO"));
        param.put("OLD_RESNO", inParam.getString("OLD_RESNO"));
        param.put("CALL_PARAMS", inParam.getString("CALL_PARAMS"));
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_CPE_RESID_BY_USERID", param); 
    }
}