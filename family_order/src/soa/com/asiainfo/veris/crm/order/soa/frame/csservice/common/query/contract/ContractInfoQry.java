
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.contract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ContractInfoQry
{
    /**
     * 根据合同ID获取合同产品
     * 
     * @author xiajj
     * @param params
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param pagination
     * @return IDataset 合同产品列表
     * @throws Exception
     */
    public static IDataset getConProuctByConid(IData params, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_CONTRACT_PRODUCT", "SEL_CP_BY_CONID", params, pagination, Route.CONN_CRM_CG);
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
     * 根据合约取gprs套餐
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGPRSDiscntByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_GPRS_DISCNT_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约取数据业务
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getPlatSvcByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("ELEMENT_TYPE_CODE", "Z");
        data.put("PACKAGE_TYPE_CODE", "D");
        data.put("PARAM_VALUE", "105");
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_INFO_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约取最低优惠元素
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getSaleDiscntInfo(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PACKAGE_TYPE_CODE", "4");
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("PARAM_VALUE", "107");
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_SALELEMENT_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset getSalegoodsInfo(String deviceModeCode, String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("DEVICE_MODE_CODE", deviceModeCode);
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PACKAGE_TYPE_CODE", "E");
        data.put("ELEMENT_TYPE_CODE", "G");
        return Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_SALE_BY_GOODS", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getUserSaleInfo(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TD_B_CONTRACT", "SEL_CHECKSALE", param);
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

    /**
     * 根据合约取wlan包流量套餐
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getWlanFlowByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("PACKAGE_TYPE_CODE", "C");
        data.put("PARAM_VALUE", "104");
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_INFO_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 根据合约取wlan包时长套餐
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getWlanTimeByContract(String contractId, String eparchyCode) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PACKAGE_TYPE_CODE", "B");
        data.put("ELEMENT_TYPE_CODE", "D");
        data.put("PARAM_VALUE", "103");
        IDataset dataset = Dao.qryByCode("TD_B_CONTRACT", "SEL_CONTRACT_INFO_BY_CONTRACT", data, Route.CONN_CRM_CEN);
        return dataset;
    }

    /*三代无用代码删除*/
    /*public static IDataset queryContractByID(String contractId) throws Exception
    {
        IData data = new DataMap();
        data.put("CONTRACT_ID", contractId);
        return Dao.qryByCode("TD_B_CONTRACT", "SEL_BY_CONTRACTID", data, Route.CONN_CRM_CEN);
    }*/
    
    public static IDataset queryContractMaterialByMaterialCode(String materialCode) throws Exception
    {
        IData data = new DataMap();
        data.put("MATERIAL_CODE", materialCode);
        return Dao.qryByCode("TD_B_CONTRACT", "SEL_BY_MATERIAL_CODE", data, Route.CONN_CRM_CEN);
    }

    public ContractInfoQry()
    {

    }
}
