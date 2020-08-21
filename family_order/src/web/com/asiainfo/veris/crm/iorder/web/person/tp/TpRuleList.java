package com.asiainfo.veris.crm.iorder.web.person.tp;

import java.net.URLDecoder;

import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TpRuleList extends PersonBasePage {

    public abstract void setTpRuleList(IDataset tpRuleList);
    public abstract void setUnTpRuleList(IDataset unTpRuleList);
    public abstract void setRefuseRuleList(IDataset refuseRuleList);
    public abstract void setInfo(IData info);

    public void init(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String accessNumber = param.getString("ACCESS_NUMBER");
        String custName = param.getString("CUST_NAME");
        if(StringUtils.isNotEmpty(custName)){
            custName = URLDecoder.decode(URLDecoder.decode(custName,"UTF-8"),"UTF-8");
        }
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        String errorset = URLDecoder.decode(param.getString("ERROR_SET"),"UTF-8");
        IDataset errorSet = new DatasetList(errorset);

        //错误规则提示分类
        IData data = new DataMap();
        data.put("ERROR_SET",errorSet);
        data.put("SERIAL_NUMBER",accessNumber);
        log.debug("======init=============" + data);
        IData result = CSViewCall.callone(this,"SS.TpOrderSVC.checkRuleExist",data);

        //数据展示
        setTpRuleList(result.getDataset("TP_RULE_LIST"));
        setUnTpRuleList(result.getDataset("UNTP_RULE_LIST"));
        setRefuseRuleList(result.getDataset("REFUSE_RULE_LIST"));

        log.debug(accessNumber + "=accessNumber=====TP_RULE_LIST===" + result.getDataset("TP_RULE_LIST"));
        log.debug("====UNTP_RULE_LIST=====" + result.getDataset("UNTP_RULE_LIST"));
        log.debug("======REFUSE_RULE_LIST===" + result.getDataset("REFUSE_RULE_LIST"));

        IData info = new DataMap();
        info.put("ACCESS_NUMBER",accessNumber);
        info.put("CUST_NAME",custName);
        info.put("TRADE_TYPE_CODE",tradeTypeCode);
        setInfo(info);
    }

    /**
     * 甩单提交
     * @param cycle
     * @throws Exception
     */
    public void submit(IRequestCycle cycle)throws Exception{
        IData data = getData();
        String tpRuleList = data.getString("TP_RULE_LIST");
        IDataset ruleList = dealTpRuleList(tpRuleList,data);
        data.put("OBJ_LIST",ruleList);
        IDataset result = CSViewCall.call(this, "SS.TpOrderSVC.toRuleListSubmit", data);
        if(DataUtils.isNotEmpty(ruleList)){
            StringBuilder orderIds = new StringBuilder();
            for (int i = 0; i < result.size();i++){
                orderIds.append(result.get(i));
            }
            setAjax("ORDER_IDS",orderIds.toString());
        }

    }

    /**
     * 规则组装
     * @param tpRuleList
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset dealTpRuleList(String tpRuleList,IData data)throws Exception{
        String accessNumber = data.getString("ACCESS_NUMBER");
        String custName = data.getString("CUST_NAME");
        if(StringUtils.isNotEmpty(custName)){
            custName = URLDecoder.decode(URLDecoder.decode(custName,"UTF-8"),"UTF-8");
        }
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IDataset ruleList = null;
        if(StringUtils.isNotEmpty(tpRuleList)){
            ruleList = new DatasetList(URLDecoder.decode(URLDecoder.decode(tpRuleList,"UTF-8"),"UTF-8"));
            for (int i = 0; i < ruleList.size();i++){
                IData rule = ruleList.getData(i);
                String tipsCode = rule.getString("TIPS_CODE");
                rule.put("WITH_OBJ",tipsCode);
                rule.put("ACCESS_NUMBER",accessNumber);
                rule.put("SERIAL_NUMBER",accessNumber);
                rule.put("CUST_NAME",custName);
                rule.put("TRADE_TYPE_CODE",tradeTypeCode);
            }
        }
        return ruleList;
    }


}
