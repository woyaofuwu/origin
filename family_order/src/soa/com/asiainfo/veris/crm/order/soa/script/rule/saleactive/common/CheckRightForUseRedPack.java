
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 新增
 * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
 * chenxy3 2016-08-25
 * 调平台，判断是否能办理红包使用
 */
public class  CheckRightForUseRedPack extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckSpeActiveNeedScore.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckRightForUseRedPack() >>>>>>>>>>>>>>>>>>");
        }
        boolean rtnFlag = false;
        String serialNum=databus.getString("SERIAL_NUMBER");
    	String userId = databus.getString("USER_ID");
    	String productId = databus.getString("PRODUCT_ID");//获取当前预受理的产品ID
        String packageId = databus.getString("PACKAGE_ID");//获取当前预受理的包ID 
        String eparchyCode=databus.getString("TRADE_EPARCHY_CODE","0898");
        String xChoiceTag = databus.getString("SUBMIT_FLAG");//0-查询时校验 1=提交时候校验
        String PAGE_RULE=databus.getString("PAGE_RULE","");//只在查询时校验，不在提交时候校验
        //1、先根据活动取发送红包的活动
        if("CHECK_BY_PACKAGE".equals(PAGE_RULE)){ 
	        IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, eparchyCode);
	
	        if(comms!=null && comms.size()>0){
	        	String useRedPackProdId=comms.getData(0).getString("PARAM_CODE","");
	        	String activeRedPackLimit=comms.getData(0).getString("PARA_CODE2","0");//使用红包上限
	        	String activeRedPackLimitType=comms.getData(0).getString("PARA_CODE3","");//类型A=按code2值扣，B=大于0最高code2（最长8位）
	        	
	        	if(Integer.parseInt(activeRedPackLimit)<=0 || activeRedPackLimit.length()>8||"".equals(activeRedPackLimit)){
	        		//如果余额小于下限，则报错
	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6898000, "配置错误：TD_S_COMMPARA PARAM_ATTR=6895的PARA_CODE2或PARA_CODE3不允许为空，金额不允许为0或大于8位长度。");
                	rtnFlag=true; 

	        	}else{
		        	//调接口：
					IData inparam=new DataMap(); 
					//生成14位数字
					String mid = SysDateMgr.getSysDateYYYYMMDD()+String.valueOf(RandomStringUtils.randomNumeric(6));
					String requestDate = SysDateMgr.getSysDateYYYYMMDD(); //YYYYMMDD
			        String requestTime = SysDateMgr.getSysTime();//2016-08-24 18:19:51
			        requestTime=requestTime.substring(requestTime.indexOf(":")-2).replaceAll(":", "");//格式：HHMISS
			        String merid="888002115000004";
			        IDataset userMerIds=CommparaInfoQry.getCommparaAllColByParser("CSM", "6896", "1","0898");
			        if(IDataUtil.isNotEmpty(userMerIds)){
			        	merid=userMerIds.getData(0).getString("PARA_CODE1","");
			        }
					String signString="MCODE=101760&MID="+mid+"&DATE="+requestDate+"&TIME="+requestTime+"&MERID="+merid+"&MOBILEID="+serialNum;
			        String requestXML ="<MESSAGE><MCODE>101760</MCODE><MID>"+mid+"</MID><DATE>"+requestDate+"</DATE><TIME>"+requestTime+"</TIME><MERID>"+merid+"</MERID><MOBILEID>"+serialNum+"</MOBILEID>";
			        
					inparam.put("SIGN_STRING",signString);
					inparam.put("REQUEST_XML",requestXML);
					inparam.put("CALL_TYPE","CHECK_VALUE");//查询余额
					
					IDataset callResults=CSAppCall.call("SS.SaleActiveSVC.redPackPlatCall", inparam); 
					if(callResults!=null && callResults.size()>0){
						String x_resultcode=callResults.getData(0).getString("X_RESULTCODE","");
						String x_resultinfo=callResults.getData(0).getString("X_RESULTINFO","");
						if("1".equals(x_resultcode)){
							String allBalance=callResults.getData(0).getString("ALL_BALANCE", "");
				        	String elecQuan=callResults.getData(0).getString("ELEC_QUAN", "");//查余额根据沟通使用这个比对
				        	
				        	if("A".equals(activeRedPackLimitType)){
				        		if(Integer.parseInt(elecQuan)<Integer.parseInt(activeRedPackLimit)){
					        		//A情况，必须按设置的值来扣
					        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6898001, "用户当前红包余额："+Integer.parseInt(elecQuan)/100+"。必须达到配置金额"+Integer.parseInt(activeRedPackLimit)/100+"才允许办理");
			                    	rtnFlag=true; 
					        	}
				        	}else{//B只要有红包>0就不会报错。
				        		if(Integer.parseInt(elecQuan)<=0){
					        		//A情况，必须按设置的值来扣
					        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6898001, "用户当前红包余额为0，必须有红包金额才允许办理。");
			                    	rtnFlag=true; 
					        	}
				        	} 
						}else{
							//如果调接口失败，也报错
							BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6898002, "红包余额查询接口失败！失败信息："+x_resultinfo);
		                	rtnFlag=true; 
						}
					} 
	        	}
		    }  
        }
        return rtnFlag;
    }  
}
