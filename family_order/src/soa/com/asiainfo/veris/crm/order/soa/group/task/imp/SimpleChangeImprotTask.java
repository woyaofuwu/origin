package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.Iterator;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.math.NumberUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SimpleChangeImprotTask extends ImportTaskExecutor {

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception {
        String lineNos = data.getString("LINE_NOS");
        String changeMode = data.getString("pattr_CHANGEMODE", "");
        if(StringUtils.isEmpty(lineNos) || !lineNos.contains(",")) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到导出专线列表！");
        }
        if(StringUtils.isEmpty(changeMode)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到专线变更场景！");
        }
        String[] lineNo = lineNos.split(",");

        if(IDataUtil.isEmpty(dataset)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "导入文件为空！");
        }

        IDataset lineList = new DatasetList(); 
        IData busi = new DataMap();
        //检查导入数据
        for (int i = 0, size = dataset.size(); i < size; i++) {
            IData lineData = dataset.getData(i);
            boolean isContain = false;
            for (int j = 0; j < lineNo.length; j++) {
                if(lineData.getString("NOTIN_LINE_NO", "").equals(lineNo[j])) {
                    isContain = true;
                    break;
                }
            }
            if(!isContain) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "专线【" + lineData.getString("NOTIN_LINE_NO", "") + "】未导出,请先导出再导入！");
            }
            //根据变更场景，检查导入数据
            busi = checkLineData(lineData, changeMode);

            lineList.add(busi);
        }

        SharedCache.set("SIMPLE_CHANGE_DATALINE_INFOS", lineList);
        return null;
    }

    private IData checkLineData(IData lineData, String changeMode) throws Exception {
        String productNo = lineData.getString("NOTIN_LINE_NO");

        String productId = "";
        String bizRange = "";

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
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线实例号【" + productNo + "】未获取到有效专线用户信息！");
            }
            productId = userInfos.first().getString("PRODUCT_ID");

            IData commonParam = new DataMap();
            commonParam.put("USER_ID", userIdB);
            commonParam.put("ATTR_CODE", "BIZRANGE");
            commonParam.put("INST_TYPE", "P");
            IDataset commonList = CSAppCall.call("CS.UserAttrInfoQrySVC.getUserProductAttrValue", commonParam);
            if(IDataUtil.isNotEmpty(commonList)){
                bizRange = commonList.first().getString("ATTR_VALUE");
            }
            
            //添加userId,serialNumber
            busi.put("USER_ID", userInfos.first().getString("USER_ID"));
            busi.put("SERIAL_NUMBER", userInfos.first().getString("SERIAL_NUMBER"));

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
            if(IDataUtil.isEmpty(discounts)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线实例号【" + productNo + "】未获取到有效专线资费信息！");
            }
            IDataset disCountParam = StaticUtil.getList(getVisit(), "TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "DISCOUNTPARAM_CRM_ESOP");
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
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线实例号【" + productNo + "】未获取到有效专线信息！");
        }

        if("IP地址调整".equals(changeMode)) {
            busi.put("NOTIN_RSRV_STR10", lineData.getString("NOTIN_RSRV_STR10"));//IP地址使用费
            if(!"97016".equals(productId)&&!"7016".equals(productId)){
            	if(StringUtils.isBlank(lineData.getString("IPCHANGE"))) {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "IP地址调整场景,专线【" + productNo + "】IP地址调整字段不能为空！");
                }
                busi.put("IPCHANGE", lineData.getString("IPCHANGE"));//IP地址调整
            }
            busi.put("IPTYPE", lineData.getString("IPTYPE",""));//IP地址类型
            busi.put("CUSAPPSERVIPV6ADDNUM", lineData.getString("CUSAPPSERVIPV6ADDNUM",""));//申请公网IPV6地址数
            busi.put("CUSAPPSERVIPADDNUM", lineData.getString("CUSAPPSERVIPADDNUM"));//客户申请公网IP地址数
            busi.put("CUSAPPSERVIPV4ADDNUM", lineData.getString("CUSAPPSERVIPV4ADDNUM",""));//申请公网IPV4地址数
            busi.put("DOMAINNAME", lineData.getString("DOMAINNAME"));//域名
            busi.put("MAINDOMAINADD", lineData.getString("MAINDOMAINADD"));//主域名服务器地址
        } else if("减容".equals(changeMode)) {
            busi.put("NOTIN_RSRV_STR2", lineData.getString("NOTIN_RSRV_STR2"));//月租费
            busi.put("NOTIN_RSRV_STR3", lineData.getString("NOTIN_RSRV_STR3"));//一次性费用(安装调试费)(元)
            String bandwidth = lineData.getString("BANDWIDTH");//专线带宽
            String oldbandwidth = busi.getString("BANDWIDTH");
            if(StringUtils.isBlank(bandwidth)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "专线【" + productNo + "】带宽为必填数据请填写后再导入！");
            } else if(!NumberUtils.isNumber(bandwidth)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "专线【" + productNo + "】带宽必须为数字请修改后再导入！");
            } else if(StringUtils.isBlank(bandwidth)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "专线【" + productNo + "】带宽存量数据为空，请检查！");
            } else if(Integer.valueOf(bandwidth) >= Integer.valueOf(oldbandwidth)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "减容场景变更带宽必须小于原来带宽，专线【" + productNo + "】带宽不合法请修改后再导入！");
            }
            busi.put("BANDWIDTH", bandwidth);
            busi.put("NOTIN_RSRV_STR1", bandwidth);
        } else if("同楼搬迁".equals(changeMode)) {
            if("97011".equals(productId)) {
                Iterator<String> it = lineData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if(key.startsWith("NOTIN_") || "BANDWIDTH".equals(key) || "BIZSECURITYLV".equals(key) || "IPTYPE".equals(key) || "CUSAPPSERVIPV6ADDNUM".equals(key) || "CUSAPPSERVIPADDNUM".equals(key) || "CUSAPPSERVIPV4ADDNUM".equals(key)) {
                        continue;
                    }
                    busi.put(key, lineData.getString(key));
                }
            }
            if("97012".equals(productId)) {
                
                //校验本对端地址
                checkBatchDataline(bizRange,lineData);
                
                Iterator<String> it = lineData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if(key.startsWith("NOTIN_") || "BANDWIDTH".equals(key) || "BIZSECURITYLV".equals(key)) {
                        continue;
                    }
                    busi.put(key, lineData.getString(key));
                }
            }
            if("97016".equals(productId)) {
                Iterator<String> it = lineData.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    if(key.startsWith("NOTIN_") || "BANDWIDTH".equals(key) || "BIZSECURITYLV".equals(key) || "IPTYPE".equals(key) || "CUSAPPSERVIPV6ADDNUM".equals(key) || "CUSAPPSERVIPADDNUM".equals(key) || "CUSAPPSERVIPV4ADDNUM".equals(key)) {
                        continue;
                    }
                    busi.put(key, lineData.getString(key));
                }
            }
            busi.put("NOTIN_RSRV_STR3", lineData.getString("NOTIN_RSRV_STR3"));
        }
        if(StringUtils.isBlank(busi.getString("NOTIN_RSRV_STR9"))) {
            busi.put("NOTIN_RSRV_STR9", busi.getString("NOTIN_LINE_NO"));
        }
        return busi;
    }
    
    private void checkBatchDataline(String bizRange,IData dataline)throws Exception{

        String cityA = "";
        String cityZ = "";
        String areaA = "";
        String areaZ = "";
        String countyA = "";
        String countyZ = "";
        String villageA = "";
        String villageZ = "";
        
        Iterator<String> it = dataline.keySet().iterator();
        while(it.hasNext()){
            String attrCode = it.next();
            String attrValue = dataline.getString(attrCode);
            String productNo = dataline.getString("NOTIN_LINE_NO");
            if (attrCode.equals("PROVINCEA")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_PROVINCE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属省份】不符合要求，请修改后再提交！");
                }
            } else if (attrCode.equals("CITYA")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_CITY_NEWCODE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属地市】不符合要求，请修改后再提交！");
                }
                cityA = attrValue;
            } else if (attrCode.equals("AREAA")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_COUNTY_CODE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端所属区县】不符合要求，请修改后再提交！");
                }
                areaA = attrValue;
            } else if (attrCode.equals("PORTAINTERFACETYPE")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "INTERFACE_TYPE", attrValue });
                if (IDataUtil.isEmpty(result) && !attrValue.isEmpty()) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端口类型】不符合要求，请修改后再提交！");
                }
            } else if (attrCode.equals("PROVINCEZ")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_PROVINCE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属省份】不符合要求，请修改后再提交！");
                }
            } else if (attrCode.equals("CITYZ")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_CITY_NEWCODE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属地市】不符合要求，请修改后再提交！");
                }
                cityZ = attrValue;
            } else if (attrCode.equals("AREAZ")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "BELONG_COUNTY_CODE", attrValue });
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端所属区县】不符合要求，请修改后再提交！");
                }
                areaZ = attrValue;
            } else if (attrCode.equals("PORTZINTERFACETYPE")) {
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "INTERFACE_TYPE", attrValue });
                if (IDataUtil.isEmpty(result) && !attrValue.isEmpty()) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端口类型】不符合要求，请修改后再提交！");
                }
            } else if (attrCode.equals("COUNTYA")) {
                countyA = attrValue;
            } else if (attrCode.equals("COUNTYZ")) {
                countyZ = attrValue;
            } else if (attrCode.equals("VILLAGEA")) {
                villageA = attrValue;
            } else if (attrCode.equals("VILLAGEZ")) {
                villageZ = attrValue;
            }
            if (bizRange.equals("本地市") && !cityA.equals(cityZ)) {
                if (!cityA.isEmpty() && !cityZ.isEmpty()) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务范围为【" + bizRange + "】，专线【" + productNo + " 】【A、Z地市】不符合要求，请修改后再提交！");
                }
            } else if (bizRange.equals("本地市") && cityA.equals(cityZ)) {
                String addrA = cityA + areaA + countyA + villageA;
                String addrZ = cityZ + areaZ + countyZ + villageZ;
                if (!addrA.isEmpty() && !addrZ.isEmpty()) {
                    if (addrA.equals(addrZ)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】本对端详细安装地址相同，请修改后再提交！");
                    }
                }
            }
            if (bizRange.equals("跨省") || bizRange.equals("省内跨地市")) {
                if (cityA.equals(cityZ) && !cityA.isEmpty() && !cityZ.isEmpty()) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务范围为【" + bizRange + "】，专线【" + productNo + " 】【A、Z地市】不符合要求，请修改后再提交！");
                }
            } else if (!cityA.isEmpty() && !areaA.isEmpty()) {
                boolean flag = false;
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "CHANGE_AREA_BY_CITY", cityA });
                for (int k = 0; k < result.size(); k++) {
                    String tempResult = result.getData(k).getString("DATA_ID");
                    if (tempResult.contains(areaA)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【A端地市和A端区县】不对应，请修改后再提交！");
                }
            } else if (!cityZ.isEmpty() && !areaZ.isEmpty()) {
                boolean flag = false;
                IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[] { "TYPE_ID", "DATA_NAME" }, new String[] { "CHANGE_AREA_BY_CITY", cityZ });
                for (int k = 0; k < result.size(); k++) {
                    String tempResult = result.getData(k).getString("DATA_ID");
                    if (tempResult.contains(areaZ)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线【" + productNo + " 】【Z端地市和Z端区县】不对应，请修改后再提交！");
                }
            }
        }
    
    }

}
