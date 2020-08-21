package com.asiainfo.veris.crm.order.soa.group.task.exp;

import java.util.Iterator;

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

public class SimpleChangeExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData data, Pagination pg) throws Exception {
        String lineNos = data.getString("LINE_NOS");
        if(StringUtils.isEmpty(lineNos) || !lineNos.contains(",")) {
            return new DatasetList();
        }
        String[] lineNo = lineNos.split(",");
        IDataset lineList = new DatasetList();
        for (int i = 0; i < lineNo.length; i++) {
            String productNo = lineNo[i];
            IData busi = new DataMap();
            IData param = new DataMap();
            param.put("PRODUCT_NO", productNo);
            IDataset dataLines = CSAppCall.call("SS.TradeDataLineAttrInfoQrySVC.qryAllUserDatalineByProductNO", param);
            if(IDataUtil.isNotEmpty(dataLines)) {
                IData dataline = dataLines.first();

                String userIdB = dataline.getString("USER_ID");
                IData input = new DataMap();
                input.put("USER_ID", userIdB);
                IDataset userInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", input);
                if(IDataUtil.isEmpty(userInfos) || !"0".equals(userInfos.first().getString("REMOVE_TAG"))) {
                    continue;
                }

                IDataset esopParam = StaticUtil.getList(getVisit(), "TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                Iterator<String> itr = dataline.keySet().iterator();
                while (itr.hasNext()) {
                    String attrCode = itr.next();
                    for (int j = 0; j < esopParam.size(); j++) {
                        String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                        if(attrCode.equals(paramValue)) {
                            busi.put(esopParam.getData(j).getString("PARAMNAME"), dataline.getString(attrCode));
                            break;
                        }
                    }
                }

                input.put("INST_TYPE", "D");
                input.put("PRODUCT_ID", userInfos.first().getString("PRODUCT_ID"));
                IDataset discounts = CSAppCall.call("CS.UserAttrInfoQrySVC.getDiscountByUserId", input);
                IDataset disCountParam = StaticUtil.getList(getVisit(),"TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "DISCOUNTPARAM_CRM_ESOP");
                IData discount = discounts.first();
                Iterator<String> itr2 = discount.keySet().iterator();
                while (itr2.hasNext()) {
                    String attrCode = itr2.next();
                    for (int j = 0; j < disCountParam.size(); j++) {
                        String paramValue = disCountParam.getData(j).getString("PARAMVALUE");
                        String key = disCountParam.getData(j).getString("PARAMNAME");
                        if(attrCode.equals(paramValue)) {
                            String attrValue = discount.getString(attrCode);
                            // 处理百分号无法提交问题（这里直接去掉，后面提交会加上）
                            if("NOTIN_RSRV_STR6".equals(key) || "NOTIN_RSRV_STR7".equals(key) || "NOTIN_RSRV_STR8".equals(key)) {
                                if(attrValue != null && attrValue.endsWith("%")) {
                                    attrValue = attrValue.substring(0, attrValue.length() - 1);
                                }
                            }
                            busi.put(key, attrValue);
                            break;
                        }
                    }
                }
            }

            lineList.add(busi);
        }
        return lineList;
    }

}
