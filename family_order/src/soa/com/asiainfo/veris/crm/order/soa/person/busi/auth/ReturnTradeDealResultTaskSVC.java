package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;
import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;



public class ReturnTradeDealResultTaskSVC extends CSBizService{
    
    private static transient Logger logger = Logger.getLogger(ReturnTradeDealResultTaskSVC.class);
	private static final long serialVersionUID = 1L;
	
	public void returnTradeDealResult(IData data)throws Exception{
		String staffId = data.getString("STAFF_ID","SUPPUSR");
		String departId = data.getString("DEPART_ID","00000");
//		String cityCode = data.getString("CITY_CODE");
		try{
		IDataset resultInfo =queryTradeDealResultInfo();
		if(IDataUtil.isNotEmpty(resultInfo)){
			for(int i =0;i<resultInfo.size();i++){
				IData result = new DataMap();
				result.put("UPDATE_STAFF_ID", staffId);
				result.put("UPDATE_DEPART_ID", departId);
				result.put("TID", resultInfo.getData(i).getString("TID"));
				result.put("OID", resultInfo.getData(i).getString("OID"));
				
				String resultCode = resultInfo.getData(i).getString("RESULT_CODE");
				String sn = resultInfo.getData(i).getString("PHONE");
				IData param = new DataMap();
				param.put("SERIAL_NUMBER", sn);
				IDataset moffices = UserInfoQry.getMofficeBySN(param);
				if(IDataUtil.isEmpty(moffices)){
					result.put("RESULT_CODE","-1");
					result.put("RESULT_INFO", "moffice表找不到该号码对应的路由信息");
				}
				else{				    
		            IDataset isAbilityTransSet = CommparaInfoQry.getCommparaInfoByCode("CSM", "2016", "IS_ABILITY_TRANS", "1", "ZZZZ");
		            if(IDataUtil.isNotEmpty(isAbilityTransSet)){  //走能力平台同步
		                try{
		                	IData paramurl = new DataMap();
		                    paramurl.put("PARAM_NAME", "crm.ywdg");
		                    IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
		                    String url = "";
		                    if (urls != null && urls.size() > 0)
		                    {
		                        url = urls.getData(0).getString("PARAM_VALUE", "");
		                    }
		                    else
		                    {
		                        CSAppException.appError("-1", "crm.ywdg接口地址未在TD_S_BIZENV表中配置");
		                    }
	                        //反馈订单订购结果
	                        IData abilityData = new  DataMap();
	                        abilityData.put("OprNumb", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	                        abilityData.put("TID", resultInfo.getData(i).getString("TID"));
	                        abilityData.put("OID", resultInfo.getData(i).getString("OID"));
	                        abilityData.put("BookTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	                        abilityData.put("BookResult", resultCode);
	                        if(!"1".equals(resultCode)){
	                            abilityData.put("Memo","2".equals(resultCode)?"订购失败":"订购成功");
	                        }
	                        String memo = "";
	                        if (resultInfo.getData(i).getString("ERROR_RESULT") != null && !"".equals(resultInfo.getData(i).getString("ERROR_RESULT"))) {
	                            memo = memo + ";失败原因【" + resultInfo.getData(i).getString("ERROR_RESULT") + "】";
	                        }
	                        if (resultInfo.getData(i).getString("RESULT_INFO") != null && !"".equals(resultInfo.getData(i).getString("RESULT_INFO"))) {
	                            memo = memo + ";描述【" + resultInfo.getData(i).getString("RESULT_INFO") + "】";
	                        }   
	                        if (memo.startsWith(";")) memo = memo.substring(1);
	                        abilityData.put("Memo","2".equals(resultCode) ? ("订购失败：" + (memo.length() > 200 ? memo.substring(0, 200) : memo)) : "订购成功");
	                        abilityData.put("RspTime",SysDateMgr.getSysDateYYYYMMDDHHMMSS()); 
	                        IData stopResult = null;
	                        String X_RSPCODE="";
	                        String X_RSPDESC="";
	                        String apiAddress = url;
	                        //能力编码
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
	                        result.put("RESULT_INFO", "调能力平台异常导致反馈失败");
	                    }
		            }
				}
				updateTradeDealInfo(result);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
    /**
	 * 查询第三方能力平台处理的信息
	 * @throws Exception
	 */
	public static IDataset queryTradeDealResultInfo()throws Exception{
		return Dao.qryByCode("TF_B_CTRM_ORDER_PRODUCT", "SEL_TRADE_DEAL_INFO", null,Route.CONN_CRM_CEN);
	}
	public static int updateTradeDealInfo(IData result)throws Exception{
		SQLParser sqlParser = new SQLParser(result);
		sqlParser.addSQL("UPDATE TF_B_CTRM_ORDER SET  RSRV_STR4 = :RESULT_CODE  ");
		sqlParser.addSQL(",RSRV_STR5 = :RESULT_INFO");
		sqlParser.addSQL(" ,UPDATE_TIME = sysdate");
		sqlParser.addSQL(",UPDATE_STAFF_ID  = :UPDATE_STAFF_ID ");
		sqlParser.addSQL(",UPDATE_DEPART_ID = :UPDATE_DEPART_ID ");
		sqlParser.addSQL(" WHERE OID = :OID  AND TID = :TID");
		return Dao.executeUpdate(sqlParser,Route.CONN_CRM_CEN);
	}
	
	
}
