package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class CheckUserProductAndDiscntChangeDate extends BreBase implements IBREScript 
{

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		
		String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
        	IDataset listTradeDiscnts = databus.getDataset("TF_B_TRADE_DISCNT");
    		
    		IDataset listTradeproducts = databus.getDataset("TF_B_TRADE_PRODUCT");
    		//如果有主产品变更
    		if(IDataUtil.isNotEmpty(listTradeproducts)){

    				for(int i=0; i<listTradeproducts.size(); i++){
    					IData listTradeproduct = listTradeproducts.getData(i);
    					if(BofConst.MODIFY_TAG_ADD.equals(listTradeproduct.getString("MODIFY_TAG"))){
    						 String proStartDate = listTradeproduct.getString("START_DATE");
    						 String productId = listTradeproduct.getString("PRODUCT_ID");
    						 if(SysDateMgr.compareTo(proStartDate,SysDateMgr.getSysTime()) > 0){//预约生效的主产品
    						 //主产品下的必选优惠包
    						 IDataset discntSets = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
    						 for (int j = 0; j < discntSets.size(); j++) {
    							 String discntCodeNeed = discntSets.getData(j).getString("DISCNT_CODE");
    							 if(IDataUtil.isNotEmpty(listTradeDiscnts)){
    									for(int k=0; k<listTradeDiscnts.size();k++){
    										IData listTradeDiscnt = listTradeDiscnts.getData(k);
    										String strModifyTag = listTradeDiscnt.getString("MODIFY_TAG");
    										if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag)){
    											String discntCode = listTradeDiscnt.getString("DISCNT_CODE");
    											if(discntCodeNeed.equals(discntCode)){
    												String disStartDate = listTradeDiscnt.getString("START_DATE");
    												if(SysDateMgr.compareTo(proStartDate,disStartDate) != 0){
    													BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200414, "预约的主产品变更时，主产品下的默认必须优惠套餐开始时间，必须与主产品信息表的开始时间一致！");
    													return true;
    												}
    											}
    										}
    									}
    							}
    						 }
    					} 
    				}
    			}

    		}  
        }
		return false;
    }
}