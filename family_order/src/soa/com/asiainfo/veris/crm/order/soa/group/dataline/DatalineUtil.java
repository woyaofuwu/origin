package com.asiainfo.veris.crm.order.soa.group.dataline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSnSVC;
import com.asiainfo.veris.crm.order.soa.group.esp.DataLineDiscntConst;

public class DatalineUtil {
    /**
     * 解析专线数据
     * 
     * @param attrInternet
     * @return
     * @throws Exception
     */
    public static IDataset parseDataInfo(IDataset attrInternet) throws Exception {
        IDataset internet = new DatasetList();

        if (null != attrInternet && attrInternet.size() > 0) {
            for (int i = 0; i < attrInternet.size(); i++) {
                IData productParamInfo = attrInternet.getData(0);

                if (null != productParamInfo && productParamInfo.size() > 0) {

                    IDataset productParam = new DatasetList(productParamInfo.getString("PRODUCT_PARAM"));

                    if (null != productParam && productParam.size() > 0) {

                        for (int j = 0; j < productParam.size(); j++) {
                            IData attr = productParam.getData(j);

                            if ("NOTIN_AttrInternet".equals(attr.getString("ATTR_CODE")) && null != attr.getString("ATTR_VALUE") && attr.getString("ATTR_VALUE").length() > 0) {

                                internet = new DatasetList(attr.getString("ATTR_VALUE"));
                            }

                        }
                    }
                }
            }
        }

        return internet;
    }

    /**
     * 解析ESOP专线数据
     * 
     * @param attrInternet
     * @return
     * @throws Exception
     */
    public static IDataset parseDataLineInfo(IDataset attrInternet) throws Exception {
        IDataset internet = new DatasetList();

        if (null != attrInternet && attrInternet.size() > 0) {
            for (int i = 0; i < attrInternet.size(); i++) {
                IData productParamInfo = attrInternet.getData(0);

                if (null != productParamInfo && productParamInfo.size() > 0) {

                    IDataset productParam = new DatasetList(productParamInfo.getString("PRODUCT_PARAM"));

                    if (null != productParam && productParam.size() > 0) {

                        for (int j = 0; j < productParam.size(); j++) {
                            IData attr = productParam.getData(j);

                            if ("NOTIN_DATALINE_DATA".equals(attr.getString("ATTR_CODE")) && null != attr.getString("ATTR_VALUE") && attr.getString("ATTR_VALUE").length() > 0) {

                                internet = new DatasetList(attr.getString("ATTR_VALUE"));
                            }

                        }
                    }
                }
            }
        }

        return internet;
    }

    /**
     * 解析ESOP专线数据
     * 
     * @param attrInternet
     * @return
     * @throws Exception
     */
    public static IDataset parseCommonDataInfo(IDataset attrInternet) throws Exception {
        IDataset internet = new DatasetList();

        if (null != attrInternet && attrInternet.size() > 0) {
            for (int i = 0; i < attrInternet.size(); i++) {
                IData productParamInfo = attrInternet.getData(0);

                if (null != productParamInfo && productParamInfo.size() > 0) {

                    IDataset productParam = new DatasetList(productParamInfo.getString("PRODUCT_PARAM"));

                    if (null != productParam && productParam.size() > 0) {

                        for (int j = 0; j < productParam.size(); j++) {
                            IData attr = productParam.getData(j);

                            if ("NOTIN_COMMON_DATA".equals(attr.getString("ATTR_CODE")) && null != attr.getString("ATTR_VALUE") && attr.getString("ATTR_VALUE").length() > 0) {

                                internet = new DatasetList(attr.getString("ATTR_VALUE"));
                            }

                        }
                    }
                }
            }
        }

        return internet;
    }

    /**
     * 获取serialnumber
     * 
     * @param groupId
     * @param productId
     * @return
     * @throws Exception
     */
    public static String getGrpSerialNumber(String groupId, String productId) throws Exception {
        String serialNumberB = "";
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, "0898");

        GrpGenSnSVC genSv = new GrpGenSnSVC();
        IDataset grpSnData = genSv.genGrpSn(param);

        if (null != grpSnData && grpSnData.size() > 0) {
            IData serial = grpSnData.getData(0);
            serialNumberB = serial.getString("SERIAL_NUMBER", "");
        }

        return serialNumberB;
    }

    public static int getDatalineOrderPfWait(IDataset attrInternet) throws Exception {
        int pfWait = 1;
        IDataset commonData = parseCommonDataInfo(attrInternet);

        if (null != commonData && commonData.size() > 0) {

            for (int i = 0; i < commonData.size(); i++) {
                IData data = commonData.getData(i);

                if ("PF_WAIT".equals(data.getString("ATTR_CODE")) && StringUtils.isNotBlank(data.getString("ATTR_CODE"))) {

                    pfWait = data.getInt("ATTR_VALUE");
                }
            }

        }

        return pfWait;
    }

    /**
     * 建立部分依赖
     * 
     * @param mainTradeId
     * @param limitTradeId
     * @throws Exception
     */

    public static void createLimit(String mainTradeId, String limitTradeId) throws Exception {
        IData limit = new DataMap();
        limit.put("TRADE_ID", mainTradeId);
        limit.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        limit.put("LIMIT_TRADE_ID", limitTradeId);
        limit.put("LIMIT_TYPE", "1");
        limit.put("ROUTE_ID", "0898");
        limit.put("STATE", "0");
        Dao.insert("TF_B_TRADE_LIMIT", limit, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /** 解析中继信息 */
    public static IDataset parseVoipZjInfo(IDataset attrInternetzj) throws Exception {
        IDataset internetzj = new DatasetList();

        if (null != attrInternetzj && attrInternetzj.size() > 0) {

            for (int i = 0; i < attrInternetzj.size(); i++) {
                IData productParamInfozj = attrInternetzj.getData(0);

                if (null != productParamInfozj && productParamInfozj.size() > 0) {

                    IDataset productParamzj = new DatasetList(productParamInfozj.getString("PRODUCT_PARAM"));

                    if (null != productParamzj && productParamzj.size() > 0) {

                        for (int j = 0; j < productParamzj.size(); j++) {
                            IData attrzj = productParamzj.getData(j);

                            if ("NOTIN_ZJ_ATTR".equals(attrzj.getString("ATTR_CODE"))) {

                                internetzj = new DatasetList(attrzj.getString("ATTR_VALUE"));
                            }

                        }
                    }
                }
            }
        }

        return internetzj;
    }

    // 添加交互信息
    public static IDataset addTradeUserDataLineAttr(IDataset attrDataList, IData dataline, IData userData) throws Exception {
        IDataset dataset = new DatasetList();
        // 公共信息
        if (null != attrDataList && attrDataList.size() > 0) {
            for (int i = 0; i < attrDataList.size(); i++) {
                IData attrDataValue = attrDataList.getData(i);
                IData attrData = new DataMap();

                attrData.put("USER_ID", userData.getString("USER_ID"));
                attrData.put("INST_TYPE", "R");
                attrData.put("SHEET_TYPE", userData.getString("SHEET_TYPE"));
                if (null != dataline && dataline.size() > 0) {
                    attrData.put("PRODUCT_NO", dataline.getString("PRODUCTNO"));
                } else if (null != userData && userData.size() > 0) {
                    attrData.put("PRODUCT_NO", userData.getString("PRODUCT_NO"));
                }
                attrData.put("INST_ID", SeqMgr.getInstId());
                attrData.put("ATTR_CODE", attrDataValue.getString("ATTR_CODE"));
                attrData.put("ATTR_VALUE", attrDataValue.getString("ATTR_VALUE"));
                attrData.put("START_DATE", userData.getString("START_DATE"));
                attrData.put("END_DATE", SysDateMgr.getTheLastTime());
                attrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                attrData.put("RELA_INST_ID", "-1");
                attrData.put("IS_NEED_PF", "1");

                dataset.add(attrData);
            }
        }

        // 专线信息
        if (null != dataline && dataline.size() > 0) {
            for (int j = 0; j < dataline.size(); j++) {
                String attr[] = dataline.getNames();
                if (!attr[j].startsWith("pam")) {
                    IData dataAttr = new DataMap();
                    dataAttr.put("USER_ID", userData.getString("USER_ID"));
                    dataAttr.put("INST_TYPE", "R");
                    dataAttr.put("SHEET_TYPE", userData.getString("SHEET_TYPE"));
                    dataAttr.put("PRODUCT_NO", dataline.getString("PRODUCTNO"));
                    dataAttr.put("INST_ID", SeqMgr.getInstId());
                    dataAttr.put("ATTR_CODE", attr[j]);

                    // 带宽从页面上获取
                    if ("BANDWIDTH".equals(dataline.getString("BANDWIDTH"))) {
                        dataAttr.put("ATTR_VALUE", userData.getString("BANDWIDTH"));
                    } else {
                        dataAttr.put("ATTR_VALUE", dataline.getString(attr[j]));
                    }
                    dataAttr.put("START_DATE", userData.getString("START_DATE"));
                    dataAttr.put("END_DATE", SysDateMgr.getTheLastTime());
                    dataAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    dataAttr.put("RELA_INST_ID", "-1");
                    dataAttr.put("IS_NEED_PF", "1");

                    dataset.add(dataAttr);
                }
            }
        }

        return dataset;
    }

    // 添加资料信息
    public static IDataset addTradeUserDataLine(IData dataline, IData internet, IData user) throws Exception {
        IDataset dataset = new DatasetList();

        if (null != dataline && dataline.size() > 0) {
            /* String routeMode = dataline.getString("ROUTEMODE", ""); if(StringUtils.isNotEmpty(routeMode)){ routeMode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMVALUE" }, "PARAMNAME", new
             * String[] { "ROUTEMODE", routeMode }); } */
            IData userData = new DataMap();
            userData.put("USER_ID", user.getString("USER_ID"));
            userData.put("INST_ID", SeqMgr.getInstId());
            userData.put("SHEET_TYPE", user.getString("SHEET_TYPE"));
            userData.put("PRODUCT_NO", dataline.getString("PRODUCTNO", ""));
            userData.put("INSTANCE_NUMBER", dataline.getString("PRODUCTNO", ""));
            userData.put("LINE_NUMBER", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
            userData.put("LINE_NAME", internet.getString("pam_NOTIN_LINE_NUMBER", ""));
            userData.put("BIZ_SECURITY_LV", dataline.getString("BIZSECURITYLV", ""));
            userData.put("BAND_WIDTH", internet.getString("pam_NOTIN_LINE_BROADBAND", ""));
            userData.put("ROUTE_MODE", dataline.getString("ROUTEMODE", ""));

            userData.put("LINE_PRICE", internet.getString("pam_NOTIN_LINE_PRICE"));
            userData.put("LINE_STLBUG", internet.getString("pam_NOTIN_INSTALLATION_COST"));
            userData.put("LINE_STLCM", internet.getString("pam_NOTIN_ONE_COST"));
            userData.put("LINE_IPUSR", internet.getString("pam_NOTIN_IP_PRICE"));

            userData.put("PROVINCE_A", dataline.getString("PROVINCEA", ""));
            userData.put("CITY_A", dataline.getString("CITYA", ""));
            userData.put("AREA_A", dataline.getString("AREAA", ""));
            userData.put("COUNTY_A", dataline.getString("COUNTYA", ""));
            userData.put("VILLAGE_A", dataline.getString("VILLAGEA", ""));
            userData.put("PORT_TYPE_A", dataline.getString("PORTARATE", ""));
            userData.put("PORT_INTERFACE_TYPE_A", dataline.getString("PORTAINTERFACETYPE", ""));
            userData.put("PORT_CUSTOME_A", dataline.getString("PORTACUSTOM", ""));
            userData.put("PORT_CONTACT_A", dataline.getString("PORTACONTACT", ""));
            userData.put("PORT_CONTACT_PHONE_A", dataline.getString("PORTACONTACTPHONE", ""));

            userData.put("PROVINCE_Z", dataline.getString("PROVINCEZ", ""));
            userData.put("CITY_Z", dataline.getString("CITYZ", ""));
            userData.put("AREA_Z", dataline.getString("AREAZ", ""));
            userData.put("COUNTY_Z", dataline.getString("COUNTYZ", ""));
            userData.put("VILLAGE_Z", dataline.getString("VILLAGEZ", ""));
            userData.put("PORT_TYPE_Z", dataline.getString("PORTZRATE", ""));
            userData.put("PORT_INTERFACE_TYPE_Z", dataline.getString("PORTZINTERFACETYPE", ""));
            userData.put("PORT_CUSTOME_Z", dataline.getString("PORTZCUSTOM", ""));
            userData.put("PORT_CONTACT_Z", dataline.getString("PORTZCONTACT", ""));
            userData.put("PORT_CONTACT_PHONE_Z", dataline.getString("PORTZCONTACTPHONE", ""));

            userData.put("CUST_APPSERV_IPADDNUM", dataline.getString("CUSAPPSERVIPADDNUM", ""));
            userData.put("DO_MAINNAME", dataline.getString("DOMAINNAME", ""));
            userData.put("MAIN_DO_MAINADD", dataline.getString("MAINDOMAINADD", ""));
            userData.put("SUPPORT_MODE", dataline.getString("SUPPORTMODE", ""));
            userData.put("AMOUNT", dataline.getString("AMOUNT", ""));
            userData.put("IS_CUSTOMER_PROVIDE_EQUIPMENT", dataline.getString("ISCUSTOMERPE", ""));
            userData.put("CUSTOMER_DEVICE_MODE", dataline.getString("CUSTOMERDEVICEMODE", ""));
            userData.put("CUSTOMER_DEVICE_TYPE", dataline.getString("CUSTOMERDEVICETYPE", ""));
            userData.put("CUSTOMER_DEVICE_VENDOR", dataline.getString("CUSTOMERDEVICEVENDOR", ""));
            userData.put("PHONE_PERMISSION", dataline.getString("PHONEPERMISSION", ""));
            userData.put("PHONE_LIST", dataline.getString("PHONELIST", ""));

            userData.put("START_DATE", user.getString("START_DATE"));
            userData.put("END_DATE", SysDateMgr.getTheLastTime());
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            userData.put("REMARK", dataline.getString("REMARK", ""));

            userData.put("RSRV_NUM1", dataline.getString("RSRV_NUM1", ""));
            userData.put("RSRV_NUM2", dataline.getString("RSRV_NUM2", ""));
            userData.put("RSRV_NUM3", dataline.getString("RSRV_NUM3", ""));
            userData.put("RSRV_STR1", dataline.getString("IPTYPE", ""));// IP地址类型
            userData.put("RSRV_STR2", dataline.getString("RSRV_STR2", ""));
            userData.put("RSRV_STR3", dataline.getString("RSRV_STR3", ""));
            userData.put("RSRV_STR4", dataline.getString("RSRV_STR4", ""));
            userData.put("RSRV_STR5", dataline.getString("TRADENAME", ""));
            userData.put("RSRV_DATE1", dataline.getString("RSRV_DATE1", ""));
            userData.put("RSRV_DATE2", dataline.getString("RSRV_DATE2", ""));

            dataset.add(userData);
        }
        return dataset;
    }

    /**
     * 修改专线资料
     * 
     * @param dataline
     * @param internet
     * @param user
     * @return
     * @throws Exception
     */
    public static IDataset updateTradeUserDataline(IData dataline, IData lineInfo, IData internet, IData user) throws Exception {
        IDataset dataset = new DatasetList();
        if (lineInfo.getString("PRODUCT_NO").equals(dataline.getString("PRODUCTNO")) && lineInfo.getString("USER_ID").equals(user.getString("USER_ID"))) {

            lineInfo.put("USER_ID", user.getString("USER_ID"));
            lineInfo.put("INST_ID", lineInfo.getString("INST_ID", ""));
            // lineInfo.put("INST_ID", SeqMgr.getInstId());
            lineInfo.put("SHEET_TYPE", user.getString("SHEET_TYPE"));
            lineInfo.put("PRODUCT_NO", dataline.getString("PRODUCTNO", ""));
            lineInfo.put("INSTANCE_NUMBER", dataline.getString("PRODUCTNO", ""));
            lineInfo.put("LINE_NUMBER", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
            lineInfo.put("LINE_NAME", internet.getString("pam_NOTIN_LINE_NUMBER", ""));
            lineInfo.put("BIZ_SECURITY_LV", dataline.getString("BIZSECURITYLV", ""));
            // modify by fufn BUG20180116174029 esop扩容开通单台账
            if (dataline != null && dataline.getString("BANDWIDTH") != null) {
                lineInfo.put("BAND_WIDTH", dataline.getString("BANDWIDTH"));
            } else {
                lineInfo.put("BAND_WIDTH", internet.getString("pam_NOTIN_LINE_BROADBAND", ""));
            }
            lineInfo.put("ROUTE_MODE", dataline.getString("ROUTEMODE", ""));

            lineInfo.put("LINE_PRICE", internet.getString("pam_NOTIN_LINE_PRICE"));
            lineInfo.put("LINE_STLBUG", internet.getString("pam_NOTIN_INSTALLATION_COST"));
            lineInfo.put("LINE_STLCM", internet.getString("pam_NOTIN_ONE_COST"));
            lineInfo.put("LINE_IPUSR", internet.getString("pam_NOTIN_IP_PRICE"));

            lineInfo.put("PROVINCE_A", dataline.getString("PROVINCEA", ""));
            lineInfo.put("CITY_A", dataline.getString("CITYA", ""));
            lineInfo.put("AREA_A", dataline.getString("AREAA", ""));
            lineInfo.put("COUNTY_A", dataline.getString("COUNTYA", ""));
            lineInfo.put("VILLAGE_A", dataline.getString("VILLAGEA", ""));
            lineInfo.put("PORT_TYPE_A", dataline.getString("PORTARATE", ""));
            lineInfo.put("PORT_INTERFACE_TYPE_A", dataline.getString("PORTAINTERFACETYPE", ""));
            lineInfo.put("PORT_CUSTOME_A", dataline.getString("PORTACUSTOM", ""));
            lineInfo.put("PORT_CONTACT_A", dataline.getString("PORTACONTACT", ""));
            lineInfo.put("PORT_CONTACT_PHONE_A", dataline.getString("PORTACONTACTPHONE", ""));

            lineInfo.put("PROVINCE_Z", dataline.getString("PROVINCEZ", ""));
            lineInfo.put("CITY_Z", dataline.getString("CITYZ", ""));
            lineInfo.put("AREA_Z", dataline.getString("AREAZ", ""));
            lineInfo.put("COUNTY_Z", dataline.getString("COUNTYZ", ""));
            lineInfo.put("VILLAGE_Z", dataline.getString("VILLAGEZ", ""));
            lineInfo.put("PORT_TYPE_Z", dataline.getString("PORTZRATE", ""));
            lineInfo.put("PORT_INTERFACE_TYPE_Z", dataline.getString("PORTZINTERFACETYPE", ""));
            lineInfo.put("PORT_CUSTOME_Z", dataline.getString("PORTZCUSTOM", ""));
            lineInfo.put("PORT_CONTACT_Z", dataline.getString("PORTZCONTACT", ""));
            lineInfo.put("PORT_CONTACT_PHONE_Z", dataline.getString("PORTZCONTACTPHONE", ""));

            lineInfo.put("CUST_APPSERV_IPADDNUM", dataline.getString("CUSAPPSERVIPADDNUM", ""));
            lineInfo.put("DO_MAINNAME", dataline.getString("DOMAINNAME", ""));
            lineInfo.put("MAIN_DO_MAINADD", dataline.getString("MAINDOMAINADD", ""));
            lineInfo.put("SUPPORT_MODE", dataline.getString("SUPPORTMODE", ""));
            lineInfo.put("AMOUNT", dataline.getString("AMOUNT", ""));
            lineInfo.put("IS_CUSTOMER_PROVIDE_EQUIPMENT", dataline.getString("ISCUSTOMERPE", ""));
            lineInfo.put("CUSTOMER_DEVICE_MODE", dataline.getString("CUSTOMERDEVICEMODE", ""));
            lineInfo.put("CUSTOMER_DEVICE_TYPE", dataline.getString("CUSTOMERDEVICETYPE", ""));
            lineInfo.put("CUSTOMER_DEVICE_VENDOR", dataline.getString("CUSTOMERDEVICEVENDOR", ""));
            lineInfo.put("PHONE_PERMISSION", dataline.getString("PHONEPERMISSION", ""));
            lineInfo.put("PHONE_LIST", dataline.getString("PHONELIST", ""));

            if (null != dataline && dataline.size() > 0) {
                String changeMode = dataline.getString("CHANGEMODE");
                if (StringUtils.isNotBlank(changeMode)) {
                    if ("停机".equals(changeMode)) {
                        lineInfo.put("END_DATE", user.getString("UPDATE_TIME"));
                    } else if ("复机".equals(changeMode)) {
                        lineInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                    }
                }
            }

            lineInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            lineInfo.put("UPDATE_TIME", user.getString("UPDATE_TIME", ""));
            lineInfo.put("REMARK", dataline.getString("REMARK", ""));

            lineInfo.put("RSRV_NUM1", dataline.getString("RSRV_NUM1", ""));
            lineInfo.put("RSRV_NUM2", dataline.getString("RSRV_NUM2", ""));
            lineInfo.put("RSRV_NUM3", dataline.getString("RSRV_NUM3", ""));
            lineInfo.put("RSRV_STR1", dataline.getString("RSRV_STR1", ""));
            lineInfo.put("RSRV_STR2", dataline.getString("RSRV_STR2", ""));
            lineInfo.put("RSRV_STR3", dataline.getString("RSRV_STR3", ""));
            lineInfo.put("RSRV_STR4", dataline.getString("RSRV_STR4", ""));
            lineInfo.put("RSRV_STR5", dataline.getString("TRADENAME", ""));
            lineInfo.put("RSRV_DATE1", dataline.getString("RSRV_DATE1", ""));
            lineInfo.put("RSRV_DATE2", dataline.getString("RSRV_DATE2", ""));

            dataset.add(lineInfo);
        }

        return dataset;
    }

    /**
     * 解析ESOP返回值
     * 
     * @param httpResult
     * @return
     * @throws Exception
     */
    public static IData mergeData(IDataset httpResult) throws Exception {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0) {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0) {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++) {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++) {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++) {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }

    /**
     * 建立全部依赖
     * 
     * @param mainTradeId
     * @param limitTradeId
     * @throws Exception
     */

    public static void createAllLimit(String mainTradeId, String limitTradeId) throws Exception {
        IData limit = new DataMap();
        limit.put("TRADE_ID", mainTradeId);
        limit.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        limit.put("LIMIT_TRADE_ID", limitTradeId);
        limit.put("LIMIT_TYPE", "0");
        limit.put("ROUTE_ID", "0898");
        limit.put("STATE", "0");
        Dao.insert("TF_B_TRADE_LIMIT", limit, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 拼装专线优惠
     * 
     * @param mainTradeId
     * @param limitTradeId
     * @throws Exception
     */

    public static IDataset getElementInfo(IDataset selectElements, IData datalineData, String productId) throws Exception {
        if (IDataUtil.isNotEmpty(selectElements)) {
            for (int i = 0; i < selectElements.size(); i++) {
                IData elelment = selectElements.getData(i);
                if (DataLineDiscntConst.viopElementId.equals(elelment.getString("ELEMENT_ID")) && "701001".equals(productId)) { // 如果是必选优惠
                    IDataset attrParams = elelment.getDataset("ATTR_PARAM");
                    for (int j = 0; j < attrParams.size(); j++) {
                        IData attrParam = attrParams.getData(j);
                        String attrCode = attrParam.getString("ATTR_CODE");
                        if (DataLineDiscntConst.productNO.equals(attrCode)) {// 专线实例号
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.bandWidth.equals(attrCode)) {// 带宽
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_BROADBAND"));
                        } else if (DataLineDiscntConst.productPrice.equals(attrCode)) {// 价格
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_PRICE"));
                        } else if (DataLineDiscntConst.cost.equals(attrCode)) {// 安装调试费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_INSTALLATION_COST"));
                        } else if (DataLineDiscntConst.oneCost.equals(attrCode)) {// 一次性通信服务费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_ONE_COST"));
                        } else if (DataLineDiscntConst.tradeId.equals(attrCode)) {// 业务标识
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        }
                    }
                } else if (
                		(DataLineDiscntConst.internetElementId.equals(elelment.getString("ELEMENT_ID"))
        				||DataLineDiscntConst.internet1ElementId.equals(elelment.getString("ELEMENT_ID"))
						||DataLineDiscntConst.internet2ElementId.equals(elelment.getString("ELEMENT_ID"))
                				) 
                		&& (DataLineDiscntConst.internetProductIdMember.equals(productId)
                				||DataLineDiscntConst.internet1ProductIdMember.equals(productId)
                				||DataLineDiscntConst.internet2ProductIdMember.equals(productId))) { // 如果是必选优惠
                    IDataset attrParams = elelment.getDataset("ATTR_PARAM");
                    for (int j = 0; j < attrParams.size(); j++) {
                        IData attrParam = attrParams.getData(j);
                        String attrCode = attrParam.getString("ATTR_CODE");
                        if (DataLineDiscntConst.productNO.equals(attrCode)) {// 专线实例号
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.bandWidth.equals(attrCode)) {// 带宽
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_BROADBAND"));
                        } else if (DataLineDiscntConst.productPrice.equals(attrCode)) {// 价格
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_PRICE"));
                        } else if (DataLineDiscntConst.cost.equals(attrCode)) {// 安装调试费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_INSTALLATION_COST"));
                        } else if (DataLineDiscntConst.oneCost.equals(attrCode)) {// 一次性通信服务费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_ONE_COST"));
                        } else if (DataLineDiscntConst.tradeId.equals(attrCode)) {// 业务标识
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.ipPrice.equals(attrCode)) {// IP地址使用费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_IP_PRICE"));
                        } else if (DataLineDiscntConst.softwarePrice.equals(attrCode)) {// 软件应用服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_SOFTWARE_PRICE"));
                        } else if (DataLineDiscntConst.netPrice.equals(attrCode)) {// 技术支持服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_NET_PRICE"));
                        }
                    }
                } else if ((
                		DataLineDiscntConst.datalineElementId.equals(elelment.getString("ELEMENT_ID"))
                		||DataLineDiscntConst.dataline1ElementId.equals(elelment.getString("ELEMENT_ID"))
						||DataLineDiscntConst.dataline2ElementId.equals(elelment.getString("ELEMENT_ID"))
                		)
                		
                		&& (DataLineDiscntConst.datalineProductIdMember.equals(productId)
                				||DataLineDiscntConst.dataline1ProductIdMember.equals(productId)
                				||DataLineDiscntConst.dataline2ProductIdMember.equals(productId))) { // 如果是必选优惠
                    IDataset attrParams = elelment.getDataset("ATTR_PARAM");
                    for (int j = 0; j < attrParams.size(); j++) {
                        IData attrParam = attrParams.getData(j);
                        String attrCode = attrParam.getString("ATTR_CODE");
                        if (DataLineDiscntConst.productNO.equals(attrCode)) {// 专线实例号
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.bandWidth.equals(attrCode)) {// 带宽
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_BROADBAND"));
                        } else if (DataLineDiscntConst.productPrice.equals(attrCode)) {// 价格
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_PRICE"));
                        } else if (DataLineDiscntConst.cost.equals(attrCode)) {// 安装调试费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_INSTALLATION_COST"));
                        } else if (DataLineDiscntConst.oneCost.equals(attrCode)) {// 一次性通信服务费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_ONE_COST"));
                        } else if (DataLineDiscntConst.tradeId.equals(attrCode)) {// 业务标识
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.softwarePrice.equals(attrCode)) {// 软件应用服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_SOFTWARE_PRICE"));
                        } else if (DataLineDiscntConst.netPrice.equals(attrCode)) {// 技术支持服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_NET_PRICE"));
                        } else if (DataLineDiscntConst.groupPercent.equals(attrCode)) {// 集团所在市县分成比例
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_GROUP_PERCENT"));
                        } else if (DataLineDiscntConst.aPercent.equals(attrCode)) {// A端所在市县分成比例
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_A_PERCENT"));
                        } else if (DataLineDiscntConst.zPercent.equals(attrCode)) {// Z端所在市县分成比例
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_Z_PERCENT"));
                        } else if (DataLineDiscntConst.SLA.equals(attrCode)) {// SLA服务费（元/月）
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_SLA"));
                        }
                    }
                } else if (DataLineDiscntConst.imsElementId.equals(elelment.getString("ELEMENT_ID")) && "97016".equals(productId)) { // 如果是必选优惠
                    IDataset attrParams = elelment.getDataset("ATTR_PARAM");
                    for (int j = 0; j < attrParams.size(); j++) {
                        IData attrParam = attrParams.getData(j);
                        String attrCode = attrParam.getString("ATTR_CODE");
                        if (DataLineDiscntConst.productNO.equals(attrCode)) {// 专线实例号
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.bandWidth.equals(attrCode)) {// 带宽
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_BROADBAND"));
                        } else if (DataLineDiscntConst.productPrice.equals(attrCode)) {// 价格
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_LINE_PRICE"));
                        } else if (DataLineDiscntConst.cost.equals(attrCode)) {// 安装调试费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_INSTALLATION_COST"));
                        } else if (DataLineDiscntConst.oneCost.equals(attrCode)) {// 一次性通信服务费(元)
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_ONE_COST"));
                        } else if (DataLineDiscntConst.tradeId.equals(attrCode)) {// 业务标识
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_PRODUCT_NUMBER"));
                        } else if (DataLineDiscntConst.ipPrice.equals(attrCode)) {// IP地址使用费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_IP_PRICE"));
                        } else if (DataLineDiscntConst.softwarePrice.equals(attrCode)) {// 软件应用服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_SOFTWARE_PRICE"));
                        } else if (DataLineDiscntConst.netPrice.equals(attrCode)) {// 技术支持服务费
                            attrParam.put("ATTR_VALUE", datalineData.getString("pam_NOTIN_NET_PRICE"));
                        }
                    }
                }
            }
        }
        return selectElements;
    }

    /**
     * 拼装专线一次性优惠
     * 
     * @param mainTradeId
     * @param limitTradeId
     * @throws Exception
     */

    public static IDataset getEndDateInfo(IDataset selectElements, IData enddate) throws Exception {
        IDataset newSelectElements = new DatasetList();
        if (IDataUtil.isNotEmpty(selectElements)) {
            for (int i = 0; i < selectElements.size(); i++) {
                IData elelment = selectElements.getData(i);
                if (DataLineDiscntConst.disposableElementId.equals(elelment.getString("ELEMENT_ID"))) {
                    if ("false".equals(enddate.getString("ONCE_DISCNT"))) {
                        elelment.clear();
                    } else {
                        elelment.put("END_DATE", enddate.getString("INSERT_TIME"));
                    }
                }
                if (DataLineDiscntConst.internetElementId.equals(elelment.getString("ELEMENT_ID"))
                		|| DataLineDiscntConst.internet1ElementId.equals(elelment.getString("ELEMENT_ID"))
                		|| DataLineDiscntConst.internet2ElementId.equals(elelment.getString("ELEMENT_ID"))
                		|| DataLineDiscntConst.datalineElementId.equals(elelment.getString("ELEMENT_ID"))
                		|| DataLineDiscntConst.dataline1ElementId.equals(elelment.getString("ELEMENT_ID"))
                		|| DataLineDiscntConst.dataline2ElementId.equals(elelment.getString("ELEMENT_ID"))) {
                    if ("false".equals(enddate.getString("ONCE_DISCNT"))) {
                        elelment.put("START_DATE", enddate.getString("INSERT_TIME"));
                    }
                }
                if (DataLineDiscntConst.imsElementId.equals(elelment.getString("ELEMENT_ID")) ) {
                    if ("false".equals(enddate.getString("ONCE_DISCNT"))) {
                        elelment.put("START_DATE", enddate.getString("INSERT_TIME"));
                    }
                }
                if (IDataUtil.isNotEmpty(elelment)) {
                    newSelectElements.add(elelment);
                }
            }
        }
        return newSelectElements;
    }

}
