
package com.asiainfo.veris.crm.order.web.group.bat.batdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class BatBatchQuery extends CSBasePage
{

    public void batchDetialErrorToRun(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        IData param = new DataMap();
        param.put("BATCH_ID", condData.getString("BATCH_ID"));
        param.put("DEAL_STATE", "3"); // AEE说改成预处理成功
        param.put("DEAL_DESC", "错单重跑处理");
        param.put("DEAL_STATE_STR", "'6','D'");

        // BBOSS批量错单重跑屏蔽AEE处理
        String batch_oper_type = condData.getString("BATCH_OPER_TYPE", "");
        if (batch_oper_type.equals("BATADDBBOSSMEMBER") || batch_oper_type.equals("BATDELBBOSSMEMBER") || batch_oper_type.equals("BATCONBBOSSMEMBER") || batch_oper_type.equals("BATPASBBOSSMEMBER") || batch_oper_type.equals("BATMODBBOSSMEMBER")
                || batch_oper_type.equals("BATADDYDZFMEM") || batch_oper_type.equals("BATCONFIRMYDZFMEM") || batch_oper_type.equals("BATOPENYDZFMEM") || batch_oper_type.equals("BATADDHYYYKMEM") || batch_oper_type.equals("BATOPENHYYYKMEM"))
        {
            param.put("DEAL_STATE", "0");
        }

        CSViewCall.call(this, "CS.BatDealSVC.updateBatDealStartToRun", param);// 调错单重跑服务

        String vpmnFlag = this.getParameter("vpmnFlag");
        if ("true".equals(vpmnFlag))
        {// VPMN产品页面
            batchDetialQueryVPMN(cycle);
        }
        else
        {
            batchDetialQuery(cycle);
        }
    }

    public void batchDetialQuery(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String batchOperType = condData.getString("BATCH_OPER_TYPE", "");
        Pagination page = getPagination("PageNav");
        IDataOutput outPut = CSViewCall.callPage(this, "CS.BatDealSVC.batchDetialQuery", condData, page);
        IDataset iDataset = outPut.getData();
        IData iData = new DataMap();
        String deal_desc = "";
        String deal_desc_a = "";
        String id = "";
        String id2 = "";
        for (int i = 0, sz = iDataset.size(); i < sz; i++)
        {
            iData = iDataset.getData(i);
            deal_desc = iData.getString("DEAL_DESC", "");
            if (!"".equals(deal_desc) && deal_desc.length() > 200)
            {
                id = "a" + i;
                id2 = "b" + i;
                deal_desc_a = deal_desc.substring(0, 200);
                iData.put("DEAL_DESC_A", deal_desc_a);
                iData.put("DEAL_ID", id);
                iData.put("DEAL_ID2", id2);
            }
            //add by chenzg@20170809 REQ201707240050关于在集团统一付费注销界面增加办理工作手机套餐折扣提醒的需求
            if("PAYRELATIONDESTROY".equals(batchOperType)){
            	String data6 = iData.getString("DATA6", "");
            	iData.put("REMARK", "1".equals(data6) ? "已订购集团工作手机产品" : "");
            }
        }
        setBatchDetials(iDataset);
        setBatchCount(outPut.getDataCount());
        IData condition = getData("cond", false);
        if ("BATOPENHYYYKINTF".equals(batchOperType) || "BATADDHYYYKMEMINTF".equals(batchOperType) || "BATOPENYDZFINTF".equals(batchOperType) || "BATCONFIRMYDZFINTF".equals(batchOperType) || "BATADDYDZFMEMINTF".equals(batchOperType))
        {
            condition.put("TEMPLATE_FORMART_XML", "export/bat/group/" + batchOperType + ".xml");
        }
        else if ("NEWXXTUSERREG".equals(batchOperType) || "NEWXXTUSERREG_SPE".equals(batchOperType) || "NEWXXTUSERCHANGE".equals(batchOperType) )
        {
            condition.put("TEMPLATE_FORMART_XML", "export/bat/group/NEWXXTUSER.xml");
            
        }
        else
        {
            condition.put("TEMPLATE_FORMART_XML", "export/bat/group/GroupBatBaseXML.xml");
        }
        // 汇总统计信息        IDataset hintDataset = CSViewCall.call(this, "CS.BatTradeInfoQrySVC.getBatSumInfo", condData);
        if (hintDataset.size() > 0)
        {
            IData hintData = hintDataset.getData(0);
            setHintInfo(hintData.getString("HINT_MESSAGE"));
            condition.put("ERROR_COUNT", hintData.getString("ERROR_COUNT"));
        }
        setCondition(condition);
    }

    public void batchDetialQueryVPMN(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        Pagination page = getPagination("PageNav");
        IDataOutput outPut = CSViewCall.callPage(this, "CS.BatDealSVC.batchDetialQueryVPMN", condData, page);
        IDataset iDataset = outPut.getData();
        IData iData = new DataMap();
        String deal_desc = "";
        String deal_desc_a = "";
        String id = "";
        String id2 = "";
        for (int i = 0, sz = iDataset.size(); i < sz; i++)
        {
            iData = iDataset.getData(i);
            deal_desc = iData.getString("DEAL_DESC", "");
            if (!"".equals(deal_desc) && deal_desc.length() > 200)
            {
                id = "a" + i;
                id2 = "b" + i;
                deal_desc_a = deal_desc.substring(0, 200);
                iData.put("DEAL_DESC_A", deal_desc_a);
                iData.put("DEAL_ID", id);
                iData.put("DEAL_ID2", id2);
            }
        }
        setBatchDetials(iDataset);
        setBatchCount(outPut.getDataCount());
        IData condition = getData("cond", false);
        condition.put("TEMPLATE_FORMART_XML", "export/bat/group/GroupBatBaseVPMNXML.xml");
        // 汇总统计信息
        IDataset hintDataset = CSViewCall.call(this, "CS.BatTradeInfoQrySVC.getBatSumInfo", condData);
        if (hintDataset.size() > 0)
        {
            IData hintData = hintDataset.getData(0);
            setHintInfo(hintData.getString("HINT_MESSAGE"));
            condition.put("ERROR_COUNT", hintData.getString("ERROR_COUNT"));
        }
        setCondition(condition);

    }

    public abstract void setBatchCount(long batchCount);

    public abstract void setBatchDetials(IDataset detial);

    public abstract void setBatchInfos(IDataset batch);

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);
}
