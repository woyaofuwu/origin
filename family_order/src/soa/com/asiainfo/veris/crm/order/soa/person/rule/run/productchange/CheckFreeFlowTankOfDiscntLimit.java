
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * Copyright: Copyright 2016 Asiainfo-Linkage
 * 
 * @ClassName: CheckFreeFlowTankOfDiscntLimit.java
 * @Description: REQ201703280014 关于流量自由充流量池成员订购套餐的限制优化
 * @version: v1.0.0
 * @author: fangwz
 */
public class CheckFreeFlowTankOfDiscntLimit extends BreBase implements IBREScript
{
	@Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
	        String errorMsg = "";
	        String xChoiceTag = databus.getString("X_CHOICE_TAG");

	        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
	        {
	            IDataset selectedElements = databus.getDataset("TF_B_TRADE_DISCNT");
	            String strId = databus.getString("USER_ID");
	            //物联网用户
	            String strBrandCode = databus.getString("BRAND_CODE","");
	            //集团成员
	            IDataset uuInfos = RelaUUInfoQry.getRelaByUserIdbAndRelaTypeCode(strId, "9A");
	            if (IDataUtil.isNotEmpty(selectedElements)&&"PWLW".equals(strBrandCode)&&IDataUtil.isNotEmpty(uuInfos))
	            {
	            	String strId_A = uuInfos.getData(0).getString("USER_ID_A","");
	            	IDataset ASvc1 = UserSvcInfoQry.qryUserSvcByUserId(strId_A);
	            	boolean istag=false;
	            	if (IDataUtil.isNotEmpty(ASvc1))
	                {
	                    for (int k = 0 ; k < ASvc1.size(); k++)
	                    {
	                    	if("99010012".equals(ASvc1.getData(k).getString("SERVICE_ID",""))||"99010013".equals(ASvc1.getData(k).getString("SERVICE_ID",""))){
	                    		istag=true;
	                    		break;
	                    	}
	                  }
	               if(istag){
	                   if (IDataUtil.isNotEmpty(selectedElements))
	                   {
	                   for (int i = 0, size = selectedElements.size(); i < size; i++)
	                   {
	                       IData element = selectedElements.getData(i);
	                       String elementId = element.getString("DISCNT_CODE","");
	                       String modifyTag = element.getString("MODIFY_TAG");
	                       //不允许操作配置表优惠  因为可以继承  顾判新增，删除，修改
	                       if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)|| BofConst.MODIFY_TAG_DEL.equals(modifyTag))
	                       {
	                   		   IDataset discntConfig = CommparaInfoQry.getCommparaByParaAttr("CSM","9018","0898");
	                           if (IDataUtil.isNotEmpty(discntConfig))
	                           {
	                               for (int j = 0; j <  discntConfig.size(); j++)
	                               {
	                                   IData attr = discntConfig.getData(j);

	                                   String attrCode = attr.getString("PARAM_CODE");
	                                   String attrname = attr.getString("PARAM_NAME");
	                                   if (elementId.equals(attrCode))
	                                   {
	                                           errorMsg = "您订购了服务99010012共享池产品或 99010013共享池产品,限制变更【"+attrname+"】套餐!";
	                                           BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "110", errorMsg);
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
	        return false;
	    }
}
