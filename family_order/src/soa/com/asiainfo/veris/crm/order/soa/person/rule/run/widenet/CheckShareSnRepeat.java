package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 校验多终端新增副号是否重复
 *  提交提示
 * @author lizj
 * @date 2019-10-11
 */
public class CheckShareSnRepeat extends BreBase implements IBREScript{

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {

		String tradeId = databus.getString("TRADE_ID");
		String xChoiceTag = databus.getString("X_CHOICE_TAG");
		
	    if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
	      IData inparams = new DataMap();
	      inparams.put("TRADE_ID", tradeId);

	      IDataset tradeShareRela = databus.getDataset("TF_B_TRADE_SHARE_RELA");
		  System.out.println("进入CheckShareSnRepeat"+tradeId+"!!!!"+tradeShareRela);
		  if(IDataUtil.isNotEmpty(tradeShareRela)){
			 for(int i=0; i<tradeShareRela.size();i++){
				 String tag = tradeShareRela.getData(i).getString("MODIFY_TAG");
				 String roleCode = tradeShareRela.getData(i).getString("ROLE_CODE");
				 String sn = tradeShareRela.getData(i).getString("SERIAL_NUMBER");
				 System.out.println("CheckShareSnRepeat输出"+roleCode);
				 int repeatNum =0;
				 if("0".equals(tag)&&"02".equals(roleCode)){
					 for(int j=0; j<tradeShareRela.size();j++){
						 String tag2 = tradeShareRela.getData(j).getString("MODIFY_TAG");
						 String roleCode2 = tradeShareRela.getData(j).getString("ROLE_CODE");
						 String sn2 = tradeShareRela.getData(j).getString("SERIAL_NUMBER");
						 if("0".equals(tag2)&&"02".equals(roleCode2)){
							 if(sn.equals(sn2)){
								 repeatNum++;
							 }
						 }
					 }
					 if(repeatNum>=2){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191012", "多终端共享新增副号:"+sn+"重复！");
						return true;
					 }
				 }
				 
			 }
		  }
        }
		
		 
		return false;
	}

}
