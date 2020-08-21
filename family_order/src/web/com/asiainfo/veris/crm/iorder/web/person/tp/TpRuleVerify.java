package com.asiainfo.veris.crm.iorder.web.person.tp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * T470P
 * 2020/7/30
 **/
public abstract class TpRuleVerify extends PersonBasePage {

    public void callNpVerify(IRequestCycle cycle)throws Exception{
        IData data = this.getData();
        log.debug("=======callNpVerify=======data========" + data);
        IDataset dataset = CSViewCall.call(this,"SS.QueryNpMessageSVC.queryNpOutMessage",data);
        log.debug("===callNpVerify===dataset====================="  + dataset);
        IDataset ruleList = parseNpCheckMsg(dataset);
        if(IDataUtil.isNotEmpty(ruleList)){
            this.setAjax(ruleList);
        }
    }
    public IDataset parseNpCheckMsg(IDataset dataset){
        if(IDataUtil.isNotEmpty(dataset)){
            IData data = dataset.first();
            String errorInfo = data.getString("RESULT_ERRORINFO","");
            String[] errArr = errorInfo.split(";");
            IDataset errList = new DatasetList();
            for (int i = 0;i<errArr.length;i++){
                if(StringUtils.isNotBlank(errArr[i])){
                    IData err = new DataMap();
                    err.put("TIPS_INFO",errArr[i]);
                    err.put("TIPS_CODE",i);
                    errList.add(err);
                }
            }
            return errList;
        }
        return null;
    }

    public static void main(String[] args) {
        String errorInfo = "1、您的号码当前有96.72元费用尚未缴清影响携号转网办理;2、携号转网将影响您办理的主副卡、共享业务，请在申请携号转网前做好相关业务变更;";
        String[] errArr = errorInfo.split(";");
        IDataset errList = new DatasetList();
        for (int i = 0;i<errArr.length;i++){
            if(StringUtils.isNotBlank(errArr[i])){
                IData err = new DataMap();
                err.put("MESSAGE",errArr[i]);
                errList.add(err);
            }
        }
        System.out.println("errList=" +errList);
    }
}
