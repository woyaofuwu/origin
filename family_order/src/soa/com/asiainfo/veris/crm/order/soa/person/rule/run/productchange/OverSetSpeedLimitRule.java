package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2016 Asiainfo-Linkage
 * 
 * REQ201705020028 超套限速不限量服务开发需求
 * 超套限速不限量 
 * @author: fangwz
 */
public class OverSetSpeedLimitRule extends BreBase implements IBREScript
{
	
	private static final long serialVersionUID = 2953760713086327586L;
	private static Logger logger = Logger.getLogger(OverSetSpeedLimitRule.class);
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
	        String errorMsg = "";
	        String xChoiceTag = databus.getString("X_CHOICE_TAG","");
	        String tradetypecode = databus.getString("TRADE_TYPE_CODE","");
	        if (logger.isDebugEnabled())
            	logger.debug(" >>>>>>>>>进入 OverSetSpeedLimitRule(1)>>>>>>>>> tradetypecode:"+tradetypecode);
	        if (("10".equals(tradetypecode) || "40".equals(tradetypecode) || "310".equals(tradetypecode)) && (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag)))
	        {
	        	IDataset discntTradeslist = databus.getDataset("TF_B_TRADE_DISCNT");
	        	String productId = databus.getDataset("TF_B_TRADE_PRODUCT").getData(0).getString("PRODUCT_ID", "");
	            int sum = 188;
	    		IDataset discntConfig = CommparaInfoQry.getCommparaAllColByParser("CSM","8888","20170524","0898");
	    		if(IDataUtil.isNotEmpty(discntConfig)&&discntConfig.size() > 0 ){
	    			sum = discntConfig.getData(0).getInt("PARA_CODE1",0);	//188！
	    		}
	    		//10004445 4G自选套餐
	    		//是否订购
	    		boolean isadd=false;
	    		//是否退订
	    		boolean isdel=false;
	    		//此次办理的可叠加的优惠
	    		int tradeaddcount = 0;
	    		for (int ilist = 0; ilist <  discntTradeslist.size(); ilist++)
	            {
	                IData tradediscnt = discntTradeslist.getData(ilist);
	                //查是否属于4G自选套餐优惠 查价格
	                 String discntCode = tradediscnt.getString("DISCNT_CODE","");
	                String modifyTag = tradediscnt.getString("MODIFY_TAG","");
	                IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM",discntCode,"20170524","0898");
            		if(IDataUtil.isNotEmpty(discntConfigList)&&discntConfigList.size() > 0 ){
            			if ("0".equals(modifyTag))
    	                {
            				tradeaddcount += discntConfigList.getData(0).getInt("PARA_CODE1",0);
    	                }
            			else if ("1".equals(modifyTag))
    	                {
            				tradeaddcount =tradeaddcount-discntConfigList.getData(0).getInt("PARA_CODE1",0);
    	                }
            		}
	                if ("20170524".equals(discntCode)&&("0".equals(modifyTag)||"U".equals(modifyTag)))
	                {
	                	isadd = true;
	                }
	                if ("20170524".equals(discntCode)&&"1".equals(modifyTag))
	                {
	                	isdel = true;
	                }
	            }
		        if (logger.isDebugEnabled())
	            	logger.debug(" >>>>>>>>>进入 OverSetSpeedLimitRule(2)>>>>>>>>> productId:"+productId+",isadd:"+isadd+",tradeaddcount:"+tradeaddcount);
	    		if ("10004445".equals(productId) && isadd && tradeaddcount < sum )
	    		{
	    			errorMsg = "您不满足订购【20170524】优惠,请选择对应套餐或取消订购!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
                    return true;
	    		}
	    		else
	    			return false;
	    		
	    		
	        }
	        if (!"10".equals(tradetypecode)&& !"40".equals(tradetypecode)&& !"310".equals(tradetypecode) && (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag)))// 提交时校验，依赖请求数据
	        {
	        	String userId =databus.getString("USER_ID");//产品
	            //UcaData uca = (UcaData) databus.get("UCADATA");//UcaDataFactory.getUcaByUserId(userId);
	        	UcaData uca = UcaDataFactory.getUcaByUserId(userId);
	            //List<DiscntTradeData> discntUsers = uca.getUserDiscnts();
	            String ProductId =uca.getProductId();
	            String NewProductId =uca.getUserNewMainProductId();
	            String NextProductId ="";
	            //取出用户现有生效的所有优惠 包含此次新增的
	        	IDataset discntUserslist = databus.getDataset("TF_F_USER_DISCNT_AFTER");
	        	 if (logger.isDebugEnabled())
		            	logger.debug(" >>>>>>>>>进入 OverSetSpeedLimitRule()>>>>>>>>> TF_F_USER_DISCNT_AFTER:"+discntUserslist.toString());
		        IDataset discntTradeslist = databus.getDataset("TF_B_TRADE_DISCNT");
	            if(uca.getUserNextMainProduct()!=null){
	            	NextProductId=uca.getUserNextMainProduct().getProductId();
	            }
	            if (logger.isDebugEnabled())
	            	logger.debug(" >>>>>>>>>进入 OverSetSpeedLimitRule()>>>>>>>>> ProductId:"+ProductId+",>>>>>>>>>NewProductId:"+NewProductId
	            	+",NextProductId:"+NextProductId);
	            int count = 0;
	            int sum = 188;
	    		IDataset discntConfig = CommparaInfoQry.getCommparaAllColByParser("CSM","8888","20170524","0898");
	    		if(IDataUtil.isNotEmpty(discntConfig)&&discntConfig.size() > 0 ){
	    			sum = discntConfig.getData(0).getInt("PARA_CODE1",0);	//188！
	    		}
	            //10004445 4G自选套餐
	    		//是否订购
	    		boolean isadd=false;
	    		//是否退订
	    		boolean isdel=false;
	    		//此次办理的可叠加的优惠
	    		//int tradeaddcount = 0;
	    		for (int ilist = 0; ilist <  discntTradeslist.size(); ilist++)
	            {
	                IData tradediscnt = discntTradeslist.getData(ilist);
	                //查是否属于4G自选套餐优惠 查价格
	                 String discntCode = tradediscnt.getString("DISCNT_CODE","");
	                String modifyTag = tradediscnt.getString("MODIFY_TAG","");
	                /*IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM",discntCode,"20170524","0898");
            		if(IDataUtil.isNotEmpty(discntConfigList)&&discntConfigList.size() > 0 ){
            			if ("0".equals(modifyTag))
    	                {
            				tradeaddcount += discntConfigList.getData(0).getInt("PARA_CODE1",0);
    	                }
            			else if ("1".equals(modifyTag))
    	                {
            				tradeaddcount =tradeaddcount-discntConfigList.getData(0).getInt("PARA_CODE1",0);
    	                }
            		}*/
	                if ("20170524".equals(discntCode)&&("0".equals(modifyTag)||"U".equals(modifyTag)))
	                {
	                	isadd = true;
	                }
	                if ("20170524".equals(discntCode)&&"1".equals(modifyTag))
	                {
	                	isdel = true;
	                }
	            }
	            //变更为10004445 或者保持10004445
	            if ("10004445".equals(ProductId))
	            {  
	                if (discntUserslist != null && discntUserslist.size() > 0)
	                {
	                	 //用户拥有套餐优惠是否满足188
		            	 for (int k = 0; k <  discntUserslist.size(); k++){
		 	                IData discntUsers = discntUserslist.getData(k);
		                        String elementId =discntUsers.getString("DISCNT_CODE","");
		    	                String modifyTag = discntUsers.getString("MODIFY_TAG","");
		                        if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
		                        {
		                        	//查是否属于4G自选套餐优惠 查价格
		                        		IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM",elementId,"20170524","0898");
		                        		if(IDataUtil.isNotEmpty(discntConfigList)&&discntConfigList.size() > 0 ){
		                        			count += discntConfigList.getData(0).getInt("PARA_CODE1",0);	//只要有一个符合，不在做判断！
		                        		}
		                        }
		                        // 如果删除
		                        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
		                        {
		                        	
		                        }
		                     }
	                } 
	                //IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUser(userId,"20170524");
	                IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUD(userId,"20170524");
	                //已订购20170524优惠 但现变更不满足并未退订 请退订
	                if (logger.isDebugEnabled())
		            	logger.debug(" >>>>>>>>>进入 OverSetSpeedLimitRule()3>>>>>>>>> UserDiscnts:"+UserDiscnts.toString()+",count:"+count+",isdel:"+isdel);
                    if(IDataUtil.isNotEmpty(UserDiscnts)&&count<sum&&!isdel){
                   	    errorMsg = "您不满足订购【20170524】优惠,请选择对应套餐或取消订购!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
                        return true;
	            	}
                  //未订购订购20170524优惠 但现变更满足并且没有订购
	                else if(IDataUtil.isEmpty(UserDiscnts)&&count<sum&&isadd){
	                	 errorMsg = "您不满足订购【20170524】优惠,请选择对应套餐或取消订购!";
	                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
	                        return true;
	            	}
	            }
	            //之前是10004445  现变为其他
	            else if("10004445".equals(ProductId)&&!"10004445".equals(NewProductId)){
	            	IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUD(userId,"20170524");
	            	if(IDataUtil.isNotEmpty(UserDiscnts)&&UserDiscnts.size()>0&&!isdel){
	            		    errorMsg = "您不满足订购【20170524】优惠,请取消订购!";
	                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
	                        return true;
	            	}
	            }
	            //有预约产品   之前是10004445  预约为其他
	            else if("10004445".equals(ProductId)&&!"10004445".equals(NextProductId)){
	            	IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUD(userId,"20170524");
	            	if(IDataUtil.isNotEmpty(UserDiscnts)&&UserDiscnts.size()>0&!isdel){
	            		   errorMsg = "您不满足订购【20170524】优惠,请取消订购!";
	                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
	                        return true;
	            	}
	            }
	            else  if ("10004445".equals(NextProductId))
	            {    
	                IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUD(userId,"20170524");
	                if (discntUserslist != null && discntUserslist.size() > 0)
	                {
	                	 //用户拥有套餐优惠是否满足188
		            	 for (int j = 0; j <  discntUserslist.size(); j++){
		 	                IData discntUsers = discntUserslist.getData(j);
		 	               String elementId =discntUsers.getString("DISCNT_CODE","");
	    	                String modifyTag = discntUsers.getString("MODIFY_TAG","");
	                        if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
	                        {
	                        	//查是否属于4G自选套餐优惠 查价格
	                        		IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM","20170524",elementId,"0898");
	                        		if( discntConfigList.size() > 0 ){
	                        			count += discntConfigList.getData(0).getInt("PARA_CODE1",0);	//只要有一个符合，不在做判断！
	                        		}
	                        	}
	                        // 如果删除
	                        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
	                        {
	                        	
	                        }
	                     }
	                }
	                //已订购20170524优惠 但现变更不满足并未退订 请退订
                    if(IDataUtil.isNotEmpty(UserDiscnts)&&count<sum&&!isdel){
                   	    errorMsg = "您不满足订购【20170524】优惠,请选择对应套餐或取消订购!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
                        return true;
	            	}
                  //未订购订购20170524优惠 但现变更满足并且没有订购
	                else if(IDataUtil.isEmpty(UserDiscnts)&&count<sum&&isadd){
	                	 errorMsg = "您不满足订购【20170524】优惠,请选择对应套餐或取消订购!";
	                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
	                        return true;
	            	}
	               
	            }
	        }
	        return false;
	    }

}
