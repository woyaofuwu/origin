
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.script.productlimit.CheckProductData;
import com.asiainfo.veris.crm.order.soa.script.productlimit.CheckProductDataSet;
import com.asiainfo.veris.crm.order.soa.script.productlimit.CheckProductDecision;
import com.asiainfo.veris.crm.order.soa.script.productlimit.ICheckProductDataSet;
import com.asiainfo.veris.crm.order.soa.script.productlimit.IinitArgments;
import com.asiainfo.veris.crm.order.soa.script.productlimit.InitArgments;
import com.asiainfo.veris.crm.order.soa.script.productlimit.InitCheckTag;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 由于4G自选流量包截止时间是到月底的，框架底层对这个必选包没法判断，所以在这里增加个特殊的4G自选套餐必选流量包校验
 * @author: wukw3
 */
public class CheckNecessarilyChoose extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckNecessarilyChoose.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	String xChoiceTag = databus.getString("X_CHOICE_TAG");
    	boolean bResult = false;
    	boolean bDelFlg = false;
    	boolean bNewFlg = false;
    	boolean bYuyinDelFlg = false;
    	boolean bYuyinNewFlg = false;

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckNecessarilyChoose() >>>>>>>>>>>>>>>>>>");
        	
//        	CheckProductData checkProductData = new CheckProductData();
//            CheckProductDecision checkProductDecision = new CheckProductDecision();
//            InitCheckTag initCheckTag = new InitCheckTag();
//            initCheckTag.initCheckTag(databus, checkProductDecision, checkProductData);
//            
//            ICheckProductDataSet cpdsBus = new CheckProductDataSet(databus, checkProductDecision);
//            IDataset userALLElement = cpdsBus.getUserAllDiscntList();
//        	
//        	IData reqData = databus.getData("REQDATA");// 请求的数据
        	
        	String processTagSet = databus.getString("PROCESS_TAG_SET");//产品
        	if(StringUtils.isNotEmpty(processTagSet)&&"4".equals(processTagSet.substring(8, 9))) return false; //主产品变更就不判断了。
        	
        	String userProductId = databus.getString("PRODUCT_ID");//产品
        	if(!"10004445".equals(userProductId)) return false; //非4G自选产品不判断
        	

        	/* 总线相关信息包括台账信息资料信息获取 区域 */
        	IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        	IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        	for (Iterator iterTradeDiscnt = listTradeDiscnt.iterator(); iterTradeDiscnt.hasNext();)
        	{
        		IData tradeDiscnt = (IData) iterTradeDiscnt.next();
        		//流量
        		if("10004453".equals(tradeDiscnt.getString("PACKAGE_ID",""))&&"1".equals(tradeDiscnt.getString("MODIFY_TAG",""))){
        			bDelFlg = true;
        		}
        		if("10004453".equals(tradeDiscnt.getString("PACKAGE_ID",""))&&"0".equals(tradeDiscnt.getString("MODIFY_TAG",""))){
        			bNewFlg = true;
        		}
        		//语音
        		if("10004450".equals(tradeDiscnt.getString("PACKAGE_ID",""))&&"1".equals(tradeDiscnt.getString("MODIFY_TAG",""))){
        			bYuyinDelFlg = true;
        		}
        		if("10004450".equals(tradeDiscnt.getString("PACKAGE_ID",""))&&"0".equals(tradeDiscnt.getString("MODIFY_TAG",""))){
        			bYuyinNewFlg = true;
        		}
        	}
        	//流量
        	if(bDelFlg&&!bNewFlg){//只删不增报错
        		bResult = true;
        	}
        	//语音
        	if(bYuyinDelFlg&&!bYuyinNewFlg){//只删不增报错
        		bResult = true;
        	}
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckNecessarilyChoose() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
