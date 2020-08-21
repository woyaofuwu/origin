
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 *
 * @ClassName: CheckProductDiscntInmodeCode.java
 * @Description: REQ201612220012_关于省内流量交易平台个人市场的开发需求【TradeCheckBefore】
 * @version: v1.0.0
 * @author: zhuoyingzhi
 * @date: Sep 3, 2014 4:26:03 PM Modification History: Date Author Version Description
 *    
 */
public class CheckProductDiscntInmodeCode extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            UcaData uca = (UcaData) databus.get("UCADATA");


            if (IDataUtil.isNotEmpty(reqData))
            {

                // 校验优惠
                IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
                if (IDataUtil.isNotEmpty(selectedElements))
                {
                    for (int i = 0, size = selectedElements.size(); i < size; i++)
                    {
                        IData element = selectedElements.getData(i);

                        String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
                        //优惠套餐编码
                        String elementId = element.getString("ELEMENT_ID");
                        
                        String modifyTag = element.getString("MODIFY_TAG");

                        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                        {
                        	
                        	IDataset dataset=CommparaInfoQry.getCommparaAllCol("CSM", "1101", elementId, "0898");
                        	if(IDataUtil.isNotEmpty(dataset)){
                        		 //存在需要拦截的套餐
                        		
                        		 //获取套餐价格
                        	     String discntAcct=dataset.getData(0).getString("PARA_CODE1", "");
                        	     
                        	     //获取  实时结余(单位是分)
                        	     String acctBlance=uca.getAcctBlance();
                        	     
                        	     if(!"".equals(acctBlance)&&acctBlance!=null){
	                        	 		double discntAcctParse=Double.parseDouble(discntAcct);
	                        			double acctBlanceParse=Double.parseDouble(acctBlance);
	                        		  if((discntAcctParse*100) > acctBlanceParse){
	                        			  //拦截，提示信息
	                        			  return true;
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
