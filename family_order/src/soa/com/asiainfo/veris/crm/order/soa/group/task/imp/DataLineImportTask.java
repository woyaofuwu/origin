package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class DataLineImportTask extends ImportTaskExecutor {

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception {
        // TODO Auto-generated method stub
        if (IDataUtil.isEmpty(dataset)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到表格数据，或表格未填写完整！");
        }
        IDataset infos = new DatasetList();

        // 对专线实列号进行排组查询
        String recordNumAll = data.getString("RECORD_NUM_ALL");
        String NodeId = data.getString("NODE_ID");
        String productId = data.getString("PRODUCT_ID");
        String changeMode = data.getString("CHANGEMODE");
        String[] recordNumAllInfo = recordNumAll.split(",");
        for (int i = 0; i < recordNumAllInfo.length; i++) {
            IData datalineInfo = new DataMap();
            IData productNoData = new DataMap();
            String recordNum = recordNumAllInfo[i];
            productNoData.put("RECORD_NUM", recordNum);
            productNoData.put("NODE_ID", NodeId);
            productNoData.put("IBSYSID", data.getString("IBSYSID"));
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
                    datalineInfo.put(moseSubInfo.getString("ATTR_CODE"), moseSubInfo.getString("ATTR_VALUE"));
                }
            }
            infos.add(datalineInfo);
        }
        int infosize = infos.size();
        int datasize = dataset.size();

        if (datasize != infosize) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入的条数与导出的条数不一致！");
        }

        for (Object object : dataset) {
            IData dataMap = (IData) object;
            if (StringUtils.isBlank(dataMap.getString("PORTAINTERFACETYPE", ""))) {
                dataMap.put("PORTAINTERFACETYPE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTACUSTOM", ""))) {
                dataMap.put("PORTACUSTOM", "");
            }
            if (StringUtils.isBlank(dataMap.getString("TRADENAME", ""))) {
                dataMap.put("TRADENAME", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PREREASON", ""))) {
                dataMap.put("PREREASON", "");
            }
            if (StringUtils.isBlank(dataMap.getString("TRANSFERMODE", ""))) {
                dataMap.put("TRANSFERMODE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTZINTERFACETYPE", "")) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
                dataMap.put("PORTZINTERFACETYPE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("PORTZCUSTOM", "")) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
                dataMap.put("PORTZCUSTOM", "");
            }
            if (StringUtils.isBlank(dataMap.getString("ROUTEMODE", ""))) {
                dataMap.put("ROUTEMODE", "");
            }
            if (StringUtils.isBlank(dataMap.getString("ISPREOCCUPY", ""))) {
                dataMap.put("ISPREOCCUPY", "");
            }
        }

        // 根据变更场景把变更的值展示到页面
        for (int i = 0; i < infos.size(); i++) {
            IData infoData = infos.getData(i);
            for (int j = 0; j < dataset.size(); j++) {
                IData datasetData = dataset.getData(j);
                if (infoData.getString("PRODUCTNO", "").equals(datasetData.getString("PRODUCTNO", ""))) {
                    if (StringUtils.isNotBlank(changeMode)) {
                        if ("7010".equals(productId) && "业务保障级别调整".equals(changeMode)) {
                            infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                        }
                        if ("7010".equals(productId) && "减容".equals(changeMode)) {
                            int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if ("7010".equals(productId) && "异楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            infoData.putAll(datasetData);
                        }
                        if ("7010".equals(productId) && "同楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                            infoData.putAll(datasetData);
                        }
                        if ("7010".equals(productId) && "扩容".equals(changeMode)) {
                            if (StringUtils.isBlank(infoData.getString("HIDDEN_BANDWIDTH"))) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                            }
                            int bandWidth = Integer.parseInt(infoData.getString("HIDDEN_BANDWIDTH"));// 勘察单带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
                            infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "IP地址调整".equals(changeMode)) {
                            infoData.put("IPTYPE", datasetData.getString("IPTYPE"));
                            infoData.put("CUSAPPSERVIPV6ADDNUM", datasetData.getString("CUSAPPSERVIPV6ADDNUM"));
                            infoData.put("USAPPSERVIPADDNUM", datasetData.getString("USAPPSERVIPADDNUM"));
                            infoData.put("CUSAPPSERVIPV4ADDNUM", datasetData.getString("CUSAPPSERVIPV4ADDNUM"));
                            infoData.put("DOMAINNAME", datasetData.getString("DOMAINNAME"));
                            infoData.put("MAINDOMAINADD", datasetData.getString("MAINDOMAINADD"));
                            infoData.put("IPCHANGE", datasetData.getString("IPCHANGE"));
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "减容".equals(changeMode)) {
                            int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "异楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            infoData.putAll(datasetData);
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "同楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                            infoData.putAll(datasetData);
                        }
                        if (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "扩容".equals(changeMode)) {
                            if (StringUtils.isBlank(infoData.getString("HIDDEN_BANDWIDTH"))) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                            }
                            int bandWidth = Integer.parseInt(infoData.getString("HIDDEN_BANDWIDTH"));// 勘察单带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "业务保障级别调整".equals(changeMode)) {
                            infoData.put("ROUTEMODE", datasetData.getString("ROUTEMODE"));
                            infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                        }
                        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "减容".equals(changeMode)) {
                            int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "异楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("PROVINCEZ", infoData.getString("PROVINCEZ"));
                            infoData.putAll(datasetData);
                        }
                        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "同楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("PROVINCEZ", infoData.getString("PROVINCEZ"));
                            datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                            infoData.putAll(datasetData);
                        }
                        if (("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "扩容".equals(changeMode)) {
                            if (StringUtils.isBlank(infoData.getString("HIDDEN_BANDWIDTH"))) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                            }
                            int bandWidth = Integer.parseInt(infoData.getString("HIDDEN_BANDWIDTH"));// 勘察单带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        
                        
                        if ("7016".equals(productId) && "业务保障级别调整".equals(changeMode)) {
                            infoData.put("BIZSECURITYLV", datasetData.getString("BIZSECURITYLV"));
                        }
                        if ("7016".equals(productId) && "IP地址调整".equals(changeMode)) {
//                            infoData.put("IPTYPE", datasetData.getString("IPTYPE"));
//                            infoData.put("CUSAPPSERVIPV6ADDNUM", datasetData.getString("CUSAPPSERVIPV6ADDNUM"));
                            infoData.put("USAPPSERVIPADDNUM", datasetData.getString("USAPPSERVIPADDNUM"));
//                            infoData.put("CUSAPPSERVIPV4ADDNUM", datasetData.getString("CUSAPPSERVIPV4ADDNUM"));
                            infoData.put("DOMAINNAME", datasetData.getString("DOMAINNAME"));
                            infoData.put("MAINDOMAINADD", datasetData.getString("MAINDOMAINADD"));
                        }
                        if ("7016".equals(productId) && "减容".equals(changeMode)) {
                            int bandWidth = Integer.parseInt(infoData.getString("BANDWIDTH"));// 修改前的带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        if ("7016".equals(productId) && "异楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("CITYA", infoData.getString("CITYA"));
                            datasetData.put("AREAA", infoData.getString("AREAA"));
                            datasetData.put("COUNTYA", infoData.getString("COUNTYA"));
                            datasetData.put("VILLAGEA", infoData.getString("VILLAGEA"));
                            datasetData.put("AREAA", infoData.getString("AREAA"));
                            infoData.putAll(datasetData);
                        }
                        if ("7016".equals(productId) && "同楼搬迁".equals(changeMode)) {
                            datasetData.put("BANDWIDTH", infoData.getString("BANDWIDTH"));
                            datasetData.put("PROVINCEA", infoData.getString("PROVINCEA"));
                            datasetData.put("BIZSECURITYLV", infoData.getString("BIZSECURITYLV"));
                            datasetData.put("CITYA", infoData.getString("CITYA"));
                            datasetData.put("AREAA", infoData.getString("AREAA"));
                            datasetData.put("COUNTYA", infoData.getString("COUNTYA"));
                            datasetData.put("VILLAGEA", infoData.getString("VILLAGEA"));
                            datasetData.put("AREAA", infoData.getString("AREAA"));
                            infoData.putAll(datasetData);
                        }
                        if ("7016".equals(productId) && "扩容".equals(changeMode)) {
                            if (StringUtils.isBlank(infoData.getString("HIDDEN_BANDWIDTH"))) {
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您受理的扩容业务，未获取到勘察单带宽，请核查数据！");
                            }
                            int bandWidth = Integer.parseInt(infoData.getString("HIDDEN_BANDWIDTH"));// 勘察单带宽
                            int bandWidthS = Integer.parseInt(datasetData.getString("BANDWIDTH"));// 修改的带宽
                            if (bandWidth >= bandWidthS) {
                                infoData.put("BANDWIDTH", datasetData.getString("BANDWIDTH"));
                            }
                        }
                        
                    } else {
                        infoData.putAll(datasetData);
                    }

                }
            }

        }

        // SS.WorkFormSVC.insertdataLineInfoList
        SharedCache.set("DATALINE_INFOS", infos);
        return null;
    }

}
