package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;

public class CheckDiscntsLimit extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		 String xChoiceTag = databus.getString("X_CHOICE_TAG");
		 System.out.println("进入CheckDiscntsLimit");

	        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
	        {
	            //String serialNumber = databus.getString("SERIAL_NUMBER");
	            String userId = databus.getString("USER_ID");
                System.out.println("CheckDiscntsLimit参数"+userId);
                IDataset listTradeproduct = databus.getDataset("TF_B_TRADE_PRODUCT");
        		//如果有主产品变更
        		if(IDataUtil.isNotEmpty(listTradeproduct)){
        			IData param = new DataMap();
	    	    	param.put("USER_ID",userId);
	    	        param.put("SUBSYS_CODE","CSM");
	    	        param.put("PARAM_ATTR", "9931");
	    	        Date now =new Date();
    				String date = SysDateMgr.date2String(now,SysDateMgr.PATTERN_STAND);//获取当前时间
	            	IDataset commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara(param);
	            	System.out.println("CheckDiscntsLimit9931"+commparaInfos9931);
	        		if(IDataUtil.isNotEmpty(commparaInfos9931)){
	        			
	                    param.put("DISCNT_CODE", commparaInfos9931.getData(0).getString("PARA_CODE1"));
	                    IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_XQWDISCNTINFO_BYUSERID", param);
	                    System.out.println("CheckDiscntsLimitDiscnt"+userDiscnts);
	                    if(IDataUtil.isNotEmpty(userDiscnts)){
	                    	String endDate = userDiscnts.getData(0).getString("END_DATE");
	        				String offerName = commparaInfos9931.getData(0).getString("PARA_CODE3");
	        				
	        				if(SysDateMgr.monthsBetween(date,endDate)>0){
			        			String tips="尊敬的客户：您已参加"+offerName+"（不含港澳台主叫）活动。如需变更基础套餐，请先发送短信QXYYFF到10086取消语音翻番包，" +
			        					"再变更基础套餐。取消语音翻番包后，次月起将不再享受语音翻番包优惠。更多活动详请请到中国移动海南公司营业厅或拨打10086热线咨询。【中国移动】";
			                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190516, tips);
			                	
			        			return true;
	        				}
	                    }
	        			
	        		}else{
	        			//查生效优惠
	        			commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara2(param);
	        			if(IDataUtil.isNotEmpty(commparaInfos9931)){
	        				
		                    param.put("DISCNT_CODE", commparaInfos9931.getData(0).getString("PARA_CODE1"));
		                    IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_XQWDISCNTINFO_BYUSERID", param);
		                    System.out.println("CheckDiscntsLimitDiscnt"+userDiscnts);
		                    if(IDataUtil.isNotEmpty(userDiscnts)){
		                    	String endDate = userDiscnts.getData(0).getString("END_DATE");
		        				String offerName = commparaInfos9931.getData(0).getString("PARA_CODE3");
		        				if(SysDateMgr.monthsBetween(date,endDate)>0){
		        					String tips="尊敬的客户：您已参加"+offerName+"（不含港澳台主叫）活动。如需变更基础套餐，请先发送短信QXYYFF到10086取消语音翻番包，" +
		        					"再变更基础套餐。取消语音翻番包后，次月起将不再享受语音翻番包优惠。更多活动详请请到中国移动海南公司营业厅或拨打10086热线咨询。【中国移动】";
		        		        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190517, tips);
		        					return true;
		        				}
		                    }
	        			}
	        			
	        		}
        		}
	                	
	        }
	        
	        
		return false;
	}
  public static void main(String[] args) throws Exception {
	 // System.out.println(SysDateMgr.getLastDateThisMonth());
	  String a="2019-0-14 19:22:22";
	  String b="2019-05-30 23:59:59";
	  Date now =new Date();
	  String date = SysDateMgr.date2String(now,SysDateMgr.PATTERN_STAND);
	  System.out.println(date);
	 System.out.println(SysDateMgr.monthsBetween(b,date)==0); 
	  
}
}
