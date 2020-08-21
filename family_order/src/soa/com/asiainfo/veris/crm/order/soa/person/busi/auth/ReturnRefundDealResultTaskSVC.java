package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;



public class ReturnRefundDealResultTaskSVC extends CSBizService{
    
    private static transient Logger logger = Logger.getLogger(ReturnRefundDealResultTaskSVC.class);


	/**
	 * 第三方电商退款反馈
	 */
	private static final long serialVersionUID = 1L;

	public void returnRefundDealResult(IData data)throws Exception{
		String staffId = data.getString("STAFF_ID","SUPPUSR");
		String departId = data.getString("DEPART_ID","00000");
		
		try{
			IDataset resultInfo =qryRefundDealInfo();
			if(IDataUtil.isNotEmpty(resultInfo)){
				for(int i =0;i<resultInfo.size();i++){
					IData result = new DataMap();
					result.put("UPDATE_STAFF_ID", staffId);
					result.put("UPDATE_DEPART_ID", departId);
					result.put("TID", resultInfo.getData(i).getString("TID"));
					result.put("OID", resultInfo.getData(i).getString("OID"));
//						String routeEparchy = moffices.getData(0).getString("EPARCHY_CODE");
			        IDataset isAbilityTransSet = CommparaInfoQry.getCommparaInfoByCode("CSM", "2016", "IS_ABILITY_TRANS", "1", "ZZZZ");
			        if(IDataUtil.isNotEmpty(isAbilityTransSet)){  //走能力开放平台
			           try{
			              IData paramurl = new DataMap();
			              paramurl.put("PARAM_NAME", "crm.feedback");
			              IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
			              String url = "";
			              if (urls != null && urls.size() > 0)
			              {
			                 url = urls.getData(0).getString("PARAM_VALUE", "");
			               }
			               else
			               {
			                   CSAppException.appError("-1", "crm.feedback接口地址未在TD_S_BIZENV表中配置");
			               }
	                        //反馈订单订购结果
	                        IData abilityData = new  DataMap();
//	                        abilityData.put("KIND_ID", "BIP3B511_T3000514_0_0");
	                        abilityData.put("OprNumb", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	                        abilityData.put("ChannelID", resultInfo.getData(i).getString("CHANNEL_ID"));
	                        abilityData.put("RefundType", resultInfo.getData(i).getString("REFUND_TYPE"));
	                        abilityData.put("RefundID",  resultInfo.getData(i).getString("REFUND_ID"));
	                        abilityData.put("TID", resultInfo.getData(i).getString("TID"));
	                        abilityData.put("OID", resultInfo.getData(i).getString("OID"));
	                        abilityData.put("RspTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	                        abilityData.put("RspResult", resultInfo.getData(i).getString("RSP_RESULT","00"));
	                        abilityData.put("RefundFee", Integer.valueOf(resultInfo.getData(i).getString("REFUND_FEE","0")));
	                        abilityData.put("Memo", resultInfo.getData(i).getString("MEMO","成功"));
	                        String X_RSPCODE="";
	                        String X_RSPDESC="";
	                        IData stopResult = null;
	                        String apiAddress = url;
	                        stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,abilityData);
	                        String resCode=stopResult.getString("resCode");
	                        if(!"00000".equals(resCode)){
	                          X_RSPCODE=stopResult.getString("resCode");
			                  X_RSPDESC=stopResult.getString("resMsg");	
	                         }else{
	                           IData out=stopResult.getData("result");
	                           X_RSPCODE=out.getString("BizCode");
			                   X_RSPDESC=out.getString("BizDesc");	
	                          }
	                            
		                        if(X_RSPDESC!=null&&X_RSPDESC.length()>=100){
		                            X_RSPDESC=X_RSPDESC.substring(0, 100);
		                        }
	                            result.put("RESULT_CODE", X_RSPCODE);
	                            result.put("RESULT_INFO", X_RSPDESC);
	                            logger.debug("-----result:----- "+result);
	                        }catch(Exception e){
	                            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
	                            e.printStackTrace();
	                            result.put("RESULT_CODE", "-1");
	                            result.put("RESULT_INFO", "调iboss异常导致反馈失败");
	                        }
			            }					
					if(StringUtils.isBlank(result.getString("TID")) && StringUtils.isBlank(result.getString("OID"))){
						result.put("RESULT_CODE", "-1");
                        result.put("RESULT_INFO", "查出数据中 TID,OID不能为空");
					}
					updateRefundDealInfo(result);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
    /**
	 * 第三方电商退款反馈
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRefundDealInfo()throws Exception{
		return Dao.qryByCode("TF_B_CTRM_ORDER_PRODUCT", "SEL_REFUND_DEAL_INFO", null,Route.CONN_CRM_CEN);
	}
	public static int updateRefundDealInfo(IData result)throws Exception{
		SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("  update TF_B_CTRM_REFUND_SUB  set  RSRV_STR1 = '1' ");
		sqlParser.addSQL(", RSRV_STR4 = :RESULT_CODE ,RSRV_STR5 = :RESULT_INFO ,UPDATE_TIME = sysdate ,UPDATE_STAFF_ID  = :UPDATE_STAFF_ID , ");
		sqlParser.addSQL(" UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
		sqlParser.addSQL(" WHERE OID = :OID  AND TID = :TID ");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
	}
}
