package com.asiainfo.veris.crm.iorder.web.igroup.esop.datalineInformation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class DatalineInformation extends EopBasePage {

    public abstract void setInfos(IDataset infos);

    public abstract void setCondition(IData info);

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initial(IRequestCycle cycle) throws Exception {

        IData inputData = this.getData();
        IDataset infos = new DatasetList();
        String ibsysId = inputData.getString("IBSYSID");
        // 查询EOMS_BUSI_STATE状态编码
        // IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.getWorkfromEoms", inputData);
        // IDataset archiveWayInfos = CSViewCall.call(this, "SS.WorkFormSVC.qryArchiveWay", inputData);
        // if (IDataUtil.isNotEmpty(archiveWayInfos)) {
        // String newValue = archiveWayInfos.getData(0).getString("ATTR_NEW_VALUE");
        // inputData.put("ATTR_NEW_VALUE", newValue);
        //
        // }
        // if (IDataUtil.isNotEmpty(eomsInfos)) {
        // String eomsBusiState = "EOMS_BUSI_STATE_" + eomsInfos.getData(0).getString("SHEETTYPE");
        // inputData.put("CONFIGNAME", eomsBusiState);
        // }
        inputData.put("CONFIGNAME", "EOMS_BUSI_STATE");

        IDataset result = CSViewCall.call(this, "SS.WorkFormSVC.getRenewWorkSheet", inputData);
        int datalineLengths = 0;
        if (IDataUtil.isNotEmpty(result)) {
            for (int i = 0; i < result.size(); i++) {
                IData infoss = new DataMap();
                IData resultInfo = result.getData(i);
                String recordNum = resultInfo.getString("RECORD_NUM");
                String nodeId = resultInfo.getString("NODE_ID");
                String tradeId = resultInfo.getString("TRADE_ID");
                String productNo = resultInfo.getString("PRODUCT_NO");
                String productId = resultInfo.getString("PRODUCT_ID");
                String insertTime = resultInfo.getString("CREATE_DATE");
                String valueDesc = resultInfo.getString("VALUEDESC");
                String paramValue = resultInfo.getString("PARAMVALUE");
                String dealType = resultInfo.getString("SHEETTYPE");
                String eomsOpdesc = resultInfo.getString("EOMS_OPDESC");
                if ("7010".equals(productId)) {
                    infoss.put("BUSI_SIGN", "VOIP专线");
                } else if ("7011".equals(productId)) {
                    infoss.put("BUSI_SIGN", "互联网专线");
                } else if ("70111".equals(productId)) {
                    infoss.put("BUSI_SIGN", "云互联（互联网）");
                } else if ("70112".equals(productId)) {
                    infoss.put("BUSI_SIGN", "云专线（互联网）");
                }else if ("7012".equals(productId)) {
                    infoss.put("BUSI_SIGN", "数据专线");
                }else if ("70121".equals(productId)) {
                    infoss.put("BUSI_SIGN", "云互联（数据传输）");
                }else if ("70122".equals(productId)) {
                    infoss.put("BUSI_SIGN", "云专线（数据传输）");
                } else if ("7016".equals(productId)) {
                    infoss.put("BUSI_SIGN", "IMS专线");
                }
                if ("P".equals(paramValue)) {
                    datalineLengths++;
                }
                infoss.put("PARAMVALUE", paramValue);
                infoss.put("VALUEDESC", valueDesc);
                infoss.put("TRADE_ID", tradeId);
                infoss.put("IBSYSID", ibsysId);
                infoss.put("PRODUCT_NO", productNo);
                infoss.put("PRODUCT_ID", productId);
                infoss.put("SERIALNO", resultInfo.getString("SERIALNO"));
                infoss.put("EOMS_ACCEPTTIME", insertTime);
                infoss.put("NODE_ID", nodeId);
                infoss.put("DEAL_TYPE", dealType);
                infoss.put("RECORD_NUM", recordNum);
                infoss.put("EOMS_OPDESC", eomsOpdesc);
                infoss.put("EOMS_TITLE", resultInfo.getString("RSRV_STR4"));
                infoss.put("EOMS_CUSTOMNO", resultInfo.getString("GROUP_ID"));
                inputData.put("DEAL_TYPE", dealType);

                infos.add(infoss);
                // if ("34".equals(dealType)) {
                // break;
                // }

            }
        }

        int lengths = infos.size();
        if (lengths > 1) {
            inputData.put("LENGTH", "true");
        } else {
            inputData.put("LENGTH", "false");
        }
        inputData.put("LENGTHS_SUM", datalineLengths);
        if (datalineLengths != lengths) {
            inputData.put("LENGTHS_FALSE", "false");
        } else {
            inputData.put("LENGTHS_FALSE", "true");
        }
        // 把参数放到缓存，重派时取缓存的数据
        SharedCache.set("DATALINE_ATTRINFOS", infos);
        this.setInfos(infos);
        this.setCondition(inputData);
    }

}
