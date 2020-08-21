package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after;

 
import org.apache.commons.lang.RandomStringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;  

/***
 * 新增 2016-08-25 chenxy3
 * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求 
 * B活动，扣积分，调接口发红包
 */
public class RedPackActiveFinishAction implements ITradeFinishAction
{ 
    
    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
        String tradeTypeCode =  mainTrade.getString("TRADE_TYPE_CODE","240");
        String userId=mainTrade.getString("USER_ID","");
        //String orderId=mainTrade.getString("ORDER_ID","");
        String tradeId=mainTrade.getString("TRADE_ID",""); 
        String serialNum=mainTrade.getString("SERIAL_NUMBER","");
        String productId=mainTrade.getString("RSRV_STR1","");//发送红包的活动
        String packageId=mainTrade.getString("RSRV_STR2","");//发送红包的活动
        String staffId=mainTrade.getString("TRADE_STAFF_ID","");
        String departId=mainTrade.getString("TRADE_DEPART_ID","");
        
        if ("240".equals(tradeTypeCode))
        {  
        	//1、对于发红包的活动
        	
				IDataset actives=CommparaInfoQry.getCommparaInfoByCode("CSM", "6898", productId, packageId, "0898");
				for(int k=0;k<actives.size();k++){
					
					String sendRedPackVal=actives.getData(k).getString("PARA_CODE2","");//发送红包金额 
					String activeNo=actives.getData(k).getString("PARA_CODE3","");//平台活动号
					String prdId=actives.getData(k).getString("PARA_CODE4","");//"160722001901";//券别编码
					String redPakLimitHour=actives.getData(k).getString("PARA_CODE5","");//红包有效期
					//String instId=SeqMgr.getInstId();
					//调接口
					IData inparam=new DataMap();
					String mlogNo=tradeId;//SysDateMgr.getSysDateYYYYMMDD()+String.valueOf(RandomStringUtils.randomNumeric(8));
					String mid = SysDateMgr.getSysDateYYYYMMDD()+String.valueOf(RandomStringUtils.randomNumeric(6));
					String requestDate = SysDateMgr.getSysDateYYYYMMDD(); //YYYYMMDD
			        String requestTime = SysDateMgr.getSysTime();//2016-08-24 18:19:51
			        requestTime=requestTime.substring(requestTime.indexOf(":")-2).replaceAll(":", "");//格式：HHMISS
			        String merid="888002115000004";//发红包时候固定这个商户
			        
				    //测试商户号码，生产可不配置这条
				    IDataset userMerIds=CommparaInfoQry.getCommparaAllColByParser("CSM", "6896", "1","0898");
				    if(IDataUtil.isNotEmpty(userMerIds)){
				       merid=userMerIds.getData(0).getString("PARA_CODE1","");
				    }
					String signString="PRD_ID="+prdId+"&MCODE=101810&MID="+mid+"&DATE="+requestDate+"&TIME="+requestTime+"&ACT_ID="+activeNo+"&MBL_NO="+serialNum+"&BON_AMT="+sendRedPackVal+"&MLOG_NO="+mlogNo+"&TCNL_TYP=21&OPTYP=1&TTXN_DT="+requestDate+"&MERID="+merid;
			        String requestXML ="<MESSAGE><PRD_ID>"+prdId+"</PRD_ID><MCODE>101810</MCODE><MID>"+mid+"</MID><DATE>"+requestDate+"</DATE><TIME>"+requestTime+"</TIME>" +
										"<ACT_ID>"+activeNo+"</ACT_ID><MBL_NO>"+serialNum+"</MBL_NO><BON_AMT>"+sendRedPackVal+"</BON_AMT><MLOG_NO>"+mlogNo+"</MLOG_NO>" +
										"<TCNL_TYP>21</TCNL_TYP><OPTYP>1</OPTYP>" +
										"<TTXN_DT>"+requestDate+"</TTXN_DT><MERID>"+merid+"</MERID>";
			        
					inparam.put("SIGN_STRING",signString);
					inparam.put("REQUEST_XML",requestXML);
					inparam.put("CALL_TYPE","SEND_PAK");
					
					IDataset callResults=CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam); 
					if(callResults!=null && callResults.size()>0){
						String x_resultcode=callResults.getData(0).getString("X_RESULTCODE","");
						String x_resultinfo=callResults.getData(0).getString("X_RESULTINFO","");
						 
						if("1".equals(x_resultcode)){
							IData otherData = new DataMap();  
					        otherData.put("USER_ID", userId);
					        otherData.put("RSRV_VALUE_CODE", "RED_PAK");
					        otherData.put("RSRV_VALUE", "1");//状态：1=发红包  2=扣款成功（未完工） 3=扣款完工  4=红包发放冲正   5=扣款完工冲正 6=红包发放返销
					        otherData.put("RSRV_STR1", serialNum);
					        //1、发送红包记录字段
					        otherData.put("RSRV_STR2", packageId);//发送红包的活动PACKAGE_ID
					        otherData.put("RSRV_STR3", mid);//发送红包的 MID值
					        otherData.put("RSRV_STR4", mlogNo);//发送红包订单号
					        otherData.put("RSRV_STR5", prdId);//发送红包券别
					        otherData.put("RSRV_STR6", sendRedPackVal);//发送红包金额
					        otherData.put("RSRV_STR7", merid);//商户号
					        otherData.put("RSRV_STR8", activeNo);//平台活动号
					        otherData.put("RSRV_STR9", SysDateMgr.getSysDateYYYYMMDDHHMMSS());//发送红包时间
					        //2、使用红包记录字段
					        otherData.put("RSRV_STR10", "");//使用红包的活动PACKAGE_ID
					        otherData.put("RSRV_STR11", "");//使用红包MID值
					        otherData.put("RSRV_STR12", "");//支付的商户号
					        otherData.put("RSRV_STR13", "");//订单确认（扣款成功）的商户订单号ORDERID
					        otherData.put("RSRV_STR14", "");//使用红包金额
					        otherData.put("RSRV_STR15", "");//扣款成功时间YYYYMMDDHH24MISS
					        otherData.put("RSRV_STR16", "");//扣完成功后营业工单完工时间 
					        //3、返销记录字段
					        otherData.put("RSRV_STR17", "0");//是否返销（0正常 1返销） //如果是活动C返销，要恢复成B活动的原始状态
					        otherData.put("RSRV_STR18", "");//返销时间
					        otherData.put("RSRV_STR19", "");//返销操作员 
					        otherData.put("RSRV_STR20", "");//返销部门
					        otherData.put("RSRV_STR21", "");//活动C返销标记  1=返销
					        otherData.put("RSRV_STR22", "");//活动C返销说明
					        String sysTime=SysDateMgr.getSysTime();
					        otherData.put("RSRV_DATE1", SysDateMgr.addDays(Integer.parseInt(redPakLimitHour)));//SysDateMgr.addDays(Integer.parseInt(redPakLimitHour)/24)+" "+sysTime.substring(sysTime.indexOf(":")-2));//红包使用的有效期（在此之前有效）
					        otherData.put("RSRV_DATE2", null);//红包发放冲正时间
					        otherData.put("RSRV_DATE3", null);//扣款完成冲正时间
					        
					        otherData.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
					        otherData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
					        otherData.put("TRADE_ID", tradeId);//发送红包的TRADE_ID
					        otherData.put("STAFF_ID", staffId);
					        otherData.put("DEPART_ID", departId);
					        otherData.put("UPDATE_STAFF_ID", staffId);
					        otherData.put("UPDATE_DEPART_ID", departId);
					        otherData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
					        otherData.put("REMARK", "和包平台-红包营销活动"); 

					        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "INS_USER_OTHER", otherData); 
						}else{
							//如果红包接口返回错误，则不允许办理
		            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "完工失败!调用红包发送接口错误，错误信息:"+x_resultinfo);
						}
					} 
				} 
				//2、对于发红包活动，完工时候更新OTHER表状态。
				IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, "0898");
				if(IDataUtil.isNotEmpty(comms)){
					IData inpara=new DataMap();
					inpara.put("RSRV_VALUE", "3");//工单完工。
					inpara.put("USER_ID", userId);
					inpara.put("PACKAGE_ID", packageId);
					inpara.put("RSRV_STR25", tradeId);//记录TRADE_ID。
					Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_OTHER_VALUE_RSRV14", inpara);
				}
        }
    }
}
