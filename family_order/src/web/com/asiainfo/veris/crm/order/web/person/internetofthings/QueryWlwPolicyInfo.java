
package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryWlwPolicyInfo extends PersonBasePage
{

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String hintInfo);

    public void queryUserPolicyInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        IDataset dataset = CSViewCall.call(this, "SS.IotQryPolicyInfoSvc.queryUserPolicyInfo", data);
        
        IDataset resultDataSet = new DatasetList();//返回至界面的值

        if(IDataUtil.isEmpty(dataset)){
            setHintInfo("获取该手机号码归属地无信息！");
        }else{
    	  String resultCode = dataset.first().getString("X_RSPCODE");
    	  if(StringUtils.equals("0000", resultCode)){//查询成功
    		  IDataset infoResp = dataset.first().getDataset("INFO_RSP");
    		  if(IDataUtil.isNotEmpty(infoResp)){
    			  String policyValueCode = infoResp.first().getString("USR_EXTENSION_STATUS");
                  if(policyValueCode.length()!=8){
                	  setHintInfo("信息查询失败！");
                  }else{
                	  String cardMachineState = policyValueCode.substring(0, 1);
                	  String districtState = policyValueCode.substring(1, 2);
                	  IData policyData = new DataMap();
                	  policyData.put("POLICY_NAME", "机卡分离状态");
                	  String policyValue="" ;
                	  if(StringUtils.equals("0", cardMachineState)){
                		  policyValue ="机卡未分离";
                	  }else if(StringUtils.equals("1", cardMachineState)){
                		  policyValue = "机卡已分离";
                	  }else{
                		  policyValue = "机卡状态无效";
                	  }
                	  policyData.put("POLICY_VALUE", policyValue);
                	  resultDataSet.add(policyData);
                	  
                	  policyData = new DataMap();
                	  policyData.put("POLICY_NAME", "用户业务签约位置变化状态");
                	  if(StringUtils.equals("0", districtState)){
                		  policyValue ="终端未超出业务限制的位置区";
                	  }else if(StringUtils.equals("1", districtState)){
                		  policyValue = "终端已超出业务限制的位置区";
                	  }else{
                		  policyValue = "终端业务限制区域状态值无效";
                	  }
                	  policyData.put("POLICY_VALUE", policyValue);
                	  resultDataSet.add(policyData);
                  }  
    		  }else{
    			  setHintInfo("查询物联网用户策略信息为空！");
    		  }
    	  }else{
    		  String failInfo = dataset.first().getString("X_RSPDESC");
    		  setHintInfo(failInfo);
    	  }
        }
        setCondition(data);
        setInfos(resultDataSet);
    }

}
