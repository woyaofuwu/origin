package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class CheckVoLTEChangeElementRule extends BreBase implements IBREScript
{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		// TODO Auto-generated method stub
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
			IData reqData = databus.getData("REQDATA");// 请求的数据
			UcaData uca = (UcaData) databus.get("UCADATA");
			IDataset selectedElements = new DatasetList(reqData.getString("SELECTED_ELEMENTS"));
			if (IDataUtil.isNotEmpty(selectedElements))
			{
		         for (int i = 0, size = selectedElements.size(); i < size; i++)
		         {
		             IData element = selectedElements.getData(i);
		
		             String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
		             String elementId = element.getString("ELEMENT_ID");
		             String modifyTag = element.getString("MODIFY_TAG");
		             if("190".equals(elementId) && BofConst.MODIFY_TAG_ADD.equals(modifyTag) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode)){
		            	 //如果是开通VoLTE,需要判断用户上网服务是否暂停
		            	 SvcStateTradeData svcState = uca.getUserSvcsStateByServiceId("22");
		            	 if(svcState!=null&&"2".equals(svcState.getStateCode())){//用户上网服务暂停
		            		 return true;
		            	 }
		             }
		         }
		     }
        }
		return false;
	}
	
}
