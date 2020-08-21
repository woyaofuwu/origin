package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 
 * @author: 
 */
public class DonateScoreLimit extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(DonateScoreLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        boolean bResult = false;
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	IData reqData = databus.getData("REQDATA");// 请求的数据
        	String sn = ruleParam.getString(databus, "SERIAL_NUMBER");
        	if (IDataUtil.isNotEmpty(reqData)){
        		if (logger.isDebugEnabled())
		            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DonateScoreLimit() >>>>>>>>>>>>>>>>>>");
		      int   score  = Integer.parseInt(reqData.getString("TRANSFER_POINT"));
		      IDataset scoreChanged = UserInfoQry.getUserDonateScore(sn);
		      if (IDataUtil.isNotEmpty(scoreChanged)){
		    	  score = Integer.parseInt(scoreChanged.getData(0).getString("SUM(SCORE_ALL)","0"))-score;
		      }else{
		    	  score = 0-score;
		      }
		      logger.debug("=================="+score);
		      if(score <  Integer.parseInt("-20000")){
		    	  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "340", "每月赠送积分不超过20000的用户才能办理该业务！");
                  return true;
		       }
        	}
        }
        return bResult;
    }
}


