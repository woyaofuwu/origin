
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * @author Administrator
 */
public class ContractInfoQry extends CSBizBean
{
    /**
     * 获取合约的可选最低消费
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-5-28
     * @param contractId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getConsumeLimitInfo(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONSUMELIMIT_BY_CONTRACT_PACKAGE", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset getContractByDevice(String deviceModelCode, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("DEVICE_MODEL_CODE", deviceModelCode);
        cond.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_BY_DEVICE", cond, Route.CONN_CRM_CEN);

        return dataset;
    }

    /**
     * 根据ID获取合约信息
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-5-29
     * @param contractId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getContractInfoById(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_BY_ID", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 获取合约的可选合约时长列表
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-5-28
     * @param contractId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getContractMonthsInfo(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("ID_TYPE", "C");
        data.put("ATTR_CODE", "CONTRACT_MONTHS");
        IDataset dataset = Dao.qryByCode("TD_B_ATTR_ITEMB", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约及包编码取营销元素
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getSaleElementsByContractPackage(String contractId, String packageId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PACKAGE_ID", packageId);
        data.put("PARAM_VALUE", "105");
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_ELEMENT_BY_CONTRACT_PACKAGE", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约取营销组
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getSaleGroupsByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_PACKAGE_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约取语音产品
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getVoiceProductByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_YYPRODUCT_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }
}
