package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.MD5Util;
import com.asiainfo.veris.crm.order.pub.util.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
/**
 * 产品变更调用能开同步权益中心，
 * @author tz
 *
 */
public class SynInterestsAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(SynInterestsAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		String tradeId = mainTrade.getString("TRADE_ID");
		logger.debug("SynInterestsAction tradeId："+tradeId);
		String userId = mainTrade.getString("USER_ID");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		String execTime = mainTrade.getString("ACCEPT_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)).replaceAll("-","").replaceAll(":","").replaceAll(" ","");
		List<String> addDiscnt = new ArrayList<String>();
		IDataset discntList = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		if(IDataUtil.isNotEmpty(discntList)){
			for(Object temp : discntList){
				IData data = (IData)temp;
				if(BofConst.MODIFY_TAG_ADD.equals(data.getString("MODIFY_TAG", ""))){
					addDiscnt.add(data.getString("DISCNT_CODE"));
				}
			}
		}
		int interval = 0;
		//新增优惠列表不为空
		for(String discntCode : addDiscnt){
			interval++;
			IData data = new DataMap();
			data.put("SUBSYS_CODE", "CSM");
			data.put("PARAM_ATTR", "7878");
			data.put("PARAM_CODE", "SYN_INTERESTS");
			data.put("PARA_CODE1", discntCode);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(data);
			logger.debug("SynInterestsAction dataset参数："+dataset.toString());
			String uid = UUID.randomUUID().toString();
			String chanelNo =tradeId +"_"+uid.replaceAll("-","");
			String chanelNo2 = chanelNo.substring(0,30);
			//如果配置不为空，就同步权益中心
			if(IDataUtil.isNotEmpty(dataset)){
				IData comm = dataset.first();
				//是否月底终止平台服务，如果是平台服务就不调用权益中心
				String serviceId = comm.getString("PARA_CODE6");

				IData param = new DataMap();
				param.put("channelNo",chanelNo2);//渠道订单号
				param.put("phone",serialNumber);//受理设备号码
				param.put("salesId",comm.getString("PARA_CODE2"));//销售品ID
				param.put("salesName",comm.getString("PARA_CODE3"));//销售品名称
				param.put("dealType","0");//交易类型
				param.put("acceptTime",execTime);//受理时间YYYYMMDDHH24MISS
				param.put("clientIp",mainTrade.getString("TERM_IP"));//客户端IP
				param.put("isPay","0");//是否需要支付
				param.put("marketingCode","0");//营销活动编码
				IData prodInfo = new DataMap();
				prodInfo.put("prodId",comm.getString("PARA_CODE4"));//组合产品ID
				prodInfo.put("prodName",comm.getString("PARA_CODE5"));//组合产品名称
				prodInfo.put("serverNum",serialNumber);//设备号码
				prodInfo.put("quantity","1");//数量
				prodInfo.put("extField","0");//产品扩展字段
				param.put("prodInfo",prodInfo);//组合产品信息
				//TL_B_SALES_DISCNT_RECORD  
				
				
				
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
			    String timestamp = fmt.format(new Date()); // 时间戳，自动生成
				String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
				String privKey = "test1234";
				//获取私钥
				data.clear();
				data.put("SUBSYS_CODE", "CSM");
				data.put("PARAM_ATTR", "1919");
				data.put("PARAM_CODE", "SYN_PRIV_KEY");
				data.put("PARA_CODE1", "addorder");
				IDataset priKeyDataset = CommparaInfoQry.getCommparaInfoByPara(data);
				if(IDataUtil.isNotEmpty(priKeyDataset)){
					privKey = priKeyDataset.first().getString("PARA_CODE20");
				}
				//
				
				//transactionId + reqTime + PRIVATEKEY+【body内容】
				String signBeforStr = messageId + timestamp + privKey + param.toString();
				
				logger.debug("SynInterestsAction signBeforStr>>>"+signBeforStr.toString());
				String sign = MD5Util.getMD5Str(signBeforStr); 
				logger.debug("SynInterestsAction sign>>>"+sign.toString());
				IData head = new DataMap(); 
				head.put("apiId", priKeyDataset.first().getString("PARA_CODE2"));
				head.put("channelCode", priKeyDataset.first().getString("PARA_CODE3"));
				head.put("transactionId", messageId);
				head.put("reqTime", timestamp);
				head.put("sign", sign);
				head.put("version", "1.0");
				
				IData finalParam = new DataMap(); 
				IData lastFinalParam = new DataMap(); 
				finalParam.put("head", head);
				finalParam.put("body", param);
				lastFinalParam.put("contractRoot", finalParam);
				
				IData insertData = StringUtils.HumpToUnderline(param);
				insertData.put("STATE", "1");
				insertData.put("TRADE_ID", Long.valueOf(tradeId)+1);
				insertData.put("REQUESTINFO", lastFinalParam.toString());
				
				//如果不是平台服务才调用权益中心下单
				if(com.ailk.org.apache.commons.lang3.StringUtils.isBlank(serviceId)){
					String Abilityurl = "";
					IData param1 = new DataMap();
					param1.put("PARAM_NAME", "crm.ABILITY.INTERESTS");
					StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
					IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
					if (Abilityurls != null && Abilityurls.size() > 0)
					{
						Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
					}
					else
					{
						CSAppException.appError("-1", "crm.ABILITY.CIP85接口地址未在TD_S_BIZENV表中配置");
					}
					String apiAddress = Abilityurl;
					
					logger.debug("SynInterestsAction 调用能开参数："+lastFinalParam.toString());
					IData stopResult = AbilityEncrypting.callAbilityPlatCommon(apiAddress,lastFinalParam);
					String resCode=stopResult.getString("resCode");
	        		IData out=stopResult.getData("result");
	        		IData out1= out.getData("contractRoot");
	        		IData out2= out1.getData("body");
	        		
	            	String resultCode="";
	            	String resultMsg="";
	            	resultCode=out2.getString("resultCode");
	            	resultMsg=out2.getString("resultMsg");
	    			logger.debug("调用能开返回结果："+stopResult.toString());
	    			if("0000".equals(resultCode) && "00000".equals(resCode))
	    			{
	    				
	    			}else{
	    				logger.error("SynInterestsAction 调用能开参数："+lastFinalParam.toString());
	    				logger.error("调用能开返回结果："+stopResult.toString());
	    				CSAppException.appError("-1", "同步能力开发平台出错" + resultCode+"|"+resultMsg);
	    			}
	    			insertData.put("RESULTINFO", stopResult.toString());
	    			insertData.put("RESULTCODE", resCode+"|"+resultCode);//能开返回编码+权益中心编码
	    			
	    			insertData.put("TRADE_ID", Long.valueOf(tradeId)+interval);
	    			
	    			Dao.insert("TL_B_SALES_DISCNT_RECORD", insertData);
				}
        		//如果是平台服务，就生成一条到期处理数据
				if(com.ailk.org.apache.commons.lang3.StringUtils.isNotBlank(serviceId)){
					
					
					String paramValue = "";
		        	int addTime = 60;
		        	paramValue = StaticUtil.getStaticValue("DEAL_PLATSVC_INTEREST_EXPIRE", "BEFOER_TIME");
		            if (com.ailk.org.apache.commons.lang3.StringUtils.isNotBlank(paramValue))
		            {
		            	addTime = Integer.parseInt(paramValue);
		            	addTime = addTime * 60;//配置为分钟
		            }
		            //SERIAL_NUMBER=13700408481,SP_CODE=123131,BIZ_CODE=13131,OPER_CODE=1313,BIZ_TYPE_CODE=23
		            IData interParam = new DataMap();
		            interParam.put("SERIAL_NUMBER", serialNumber);
		            interParam.put("SP_CODE", comm.getString("PARA_CODE7"));
		            interParam.put("BIZ_CODE", comm.getString("PARA_CODE8"));
		            interParam.put("OPER_CODE", "02");
		            interParam.put("BIZ_TYPE_CODE", comm.getString("PARA_CODE9"));
		            
		            String monthLastTime = SysDateMgr.getAddMonthsLastDayNoEnv(0,SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
		            IData param11 = new DataMap();
					param11.put("DEAL_ID", SeqMgr.getTradeId());
					param11.put("DEAL_COND", interParam);
					param11.put("USER_ID", userId);
					param11.put("PARTITION_ID", userId.substring(userId.length() - 4));
					param11.put("SERIAL_NUMBER", serialNumber);
					param11.put("EPARCHY_CODE", eparchyCode);
					param11.put("IN_TIME", SysDateMgr.getSysTime());
					param11.put("DEAL_STATE", "0");
					param11.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_END_PLATSVC);
					param11.put("EXEC_TIME", SysDateMgr.addSecond(monthLastTime,-addTime));
					param11.put("EXEC_MONTH", SysDateMgr.getMonthForDate(SysDateMgr.addSecond(execTime,addTime)));
					param11.put("TRADE_ID", tradeId);

		            Dao.insert("TF_F_EXPIRE_DEAL", param11);
				}
				
				
			}
			
			
		}

		
	}
}
