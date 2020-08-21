package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class DataLineExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData inParam, Pagination arg1) throws Exception {

        IDataset infos = new DatasetList();
        // 对专线实列号进行排组查询
        String recordNumAll = inParam.getString("RECORD_NUM_ALL");
        String NodeId = inParam.getString("NODE_ID");
        String[] recordNumAllInfo = recordNumAll.split(",");
        for (int i = 0; i < recordNumAllInfo.length; i++) {
            IData datalineInfo = new DataMap();
            IData productNoData = new DataMap();
            String recordNum = recordNumAllInfo[i];
            productNoData.put("RECORD_NUM", recordNum);
            productNoData.put("NODE_ID", NodeId);
            productNoData.put("IBSYSID", inParam.getString("IBSYSID"));
            productNoData.put("FLAG", "1");
            // 调接口，查询eomsSub专线数据
            IDataset result = CSAppCall.call("SS.WorkFormSVC.querydataLineAttrInfoList", productNoData);
            if (IDataUtil.isNotEmpty(result)) {
                for (Object object : result) {
                    IData moseSubInfo = (IData) object;
                    String attrValue = moseSubInfo.getString("ATTR_VALUE");
                    if (StringUtils.isNotEmpty(attrValue) && attrValue.endsWith("%")) {
                        attrValue = attrValue.substring(0, attrValue.length() - 1);
                        moseSubInfo.put("ATTR_VALUE", attrValue);
                    }
                    if ("ISPREOCCUPY".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                        String staticDataName = StaticUtil.getStaticValue("IF_CHOOSE_CONFCRM", moseSubInfo.getString("ATTR_VALUE", ""));
                        moseSubInfo.put("ATTR_VALUE", staticDataName);
                    }
                    if ("ROUTEMODE".equals(moseSubInfo.getString("ATTR_CODE", ""))) {
                        String staticDataName = StaticUtil.getStaticValue("ROUTEMODE", moseSubInfo.getString("ATTR_VALUE", ""));
                        moseSubInfo.put("ATTR_VALUE", staticDataName);
                    }
                    datalineInfo.put(moseSubInfo.getString("ATTR_CODE"), moseSubInfo.getString("ATTR_VALUE"));
                }
            }
            infos.add(datalineInfo);
        }

        return infos;
    }

}
