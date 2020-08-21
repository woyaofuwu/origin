
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分）
 * chenxy3 2016-3-24
 * 校验是否特殊活动需要找用户该类型的积分，不是总积分。
 */
public class  CheckSpeActiveNeedScore extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckSpeActiveNeedScore.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckSpeActiveNeedScore() >>>>>>>>>>>>>>>>>>");
        }
        boolean rtnFlag = false;
        String serialNum=databus.getString("SERIAL_NUMBER");
    	String userId = databus.getString("USER_ID");
    	String productId = databus.getString("PRODUCT_ID");//获取当前预受理的产品ID
        String packageId = databus.getString("PACKAGE_ID");//获取当前预受理的包ID 
        String eparchyCode=databus.getString("TRADE_EPARCHY_CODE","0898");
        //1、先取积分
//        IDataset scores = SaleScoreInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        IDataset scores = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SCORE);
        
        if(scores!=null && scores.size()>0){
        	String needScore=scores.getData(0).getString("SCORE_VALUE","");
        	String scoreTypeCode=scores.getData(0).getString("SCORE_TYPE_CODE","");
        	//2、积分如果是负数，说明需要扣用户积分，则进入规则判断
        	if(Integer.parseInt(needScore)<0){
        		IDataset specElems= CommparaInfoQry.getCommParas("CSM", "2401",packageId, productId,  "0898");  
            	
            	//3、有配置的特殊类活动，需要调账务接口获取该类型的用户积分
            	if (specElems!=null && specElems.size()>0)
                {
        	    	IData callParam=new DataMap();
        	    	callParam.put("USER_ID",userId);
        	    	callParam.put("INTEGRAL_TYPE_CODE",scoreTypeCode);//积分类型
        	    	IDataset callSet= AcctCall.queryUserScoreValue(callParam);//调账务接口取积分
        	    	if(callSet!=null && callSet.size()>0){
        	    		IData callrtn=callSet.getData(0);
        	    		String xResultCode=callrtn.getString("X_RESULTCODE","");
        	    		if("1".equals(xResultCode)){
	        	    		String userScore=callrtn.getString("SCORE_VALUE","");//账务提供   —— 用户积分
	        	    		if(Integer.parseInt(userScore)<-Integer.parseInt(needScore)){
	        	    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 16240101, "用户此类型积分不足。该活动需要积分："+Math.abs(Integer.parseInt(needScore))+"；用户该类型当前积分："+userScore+"。");
	        	    			rtnFlag=true;
	        	    		}
        	    		}else{
        	    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 16240102, "用户此类型积分不足。该活动需要积分："+Math.abs(Integer.parseInt(needScore))+"；用户该类型当前积分：0。");
        	    			rtnFlag=true;
        	    		}
        	    	}            		
                }else{
                	//4、非特殊类活动，走原有的规则：
                	String depositTag = scores.getData(0).getString("DEPOSIT_TAG", "0");
                    UcaData uca = UcaDataFactory.getNormalUca(serialNum);//取用户的总积分
                    int userScore = uca.getUserScore();
                	if (!depositTag.equals("1"))// 不能用预存补积分，海南都是为0，不能预存补积分。
                    {
                        if (Integer.parseInt(needScore) > userScore)
                        {
                        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 16240103, "用户总积分不足。该活动需要积分："+Math.abs(Integer.parseInt(needScore))+"；用户当前积分："+userScore+"。");
                        	rtnFlag=true;
                        } 
                    }
                }
        	}
        } 
        return rtnFlag;
    }  
}
