
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class QueryDepositInvoiceNoQry extends CSBizBean
{
    public static IDataset queryUserCustByInvoiceNo(String invoiceNo, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", invoiceNo);
        IDataset dataset = Dao.qryByCode("TD_S_COMMPARA", "SEL_USERCUST_BY_NO_NEW", indata, pagination, routeEparchyCode);
        if(IDataUtil.isNotEmpty(dataset)){
        	dataset.getData(0).put("BRAND", UpcCallIntf.queryBrandNameByChaVal(dataset.getData(0).getString("BRAND_CODE","")));
        }
        return dataset;
    }

    public static IDataset queryUserCustBySerial(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);
        //TODO huanghua 23 与产商品解耦---已解决
        IDataset dataset = Dao.qryByCode("TD_S_COMMPARA", "SEL_USERCUST_BY_SERIAL_NEW2", indata, pagination, routeEparchyCode);
        if(IDataUtil.isNotEmpty(dataset)){
        	dataset.getData(0).put("BRAND", UpcCallIntf.queryBrandNameByChaVal(dataset.getData(0).getString("BRAND_CODE","")));
        }
        return dataset;
    }

    public static IDataset queryUserOtherByInvoiceNo(String invoiceNo, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", invoiceNo);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_NO", indata, pagination, routeEparchyCode);
    }

    public static IDataset queryUserOtherByUser(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_USER", indata, pagination, routeEparchyCode);
    }
    
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserCustBySerial1(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        IDataset dataset = Dao.qryByCode("TD_S_COMMPARA", "SEL_USERCUST_BY_SERIAL_NEW2", indata, pagination, routeEparchyCode);
        if(IDataUtil.isNotEmpty(dataset)){
        	dataset.getData(0).put("BRAND", UpcCallIntf.queryBrandNameByChaVal(dataset.getData(0).getString("BRAND_CODE","")));
        }
        return dataset;
    }
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserCustBySerial11(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);
        IDataset dataset = Dao.qryByCode("TD_S_COMMPARA", "SEL_USERCUST_BY_SERIAL_NEW11", indata, pagination, routeEparchyCode);
        if(IDataUtil.isNotEmpty(dataset)){
        	dataset.getData(0).put("BRAND", UpcCallIntf.queryBrandNameByChaVal(dataset.getData(0).getString("BRAND_CODE","")));
        }
        return dataset;
    }
    

    
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserOtherByUser1(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_USER_1", indata, pagination, routeEparchyCode);
    }
    
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserOtherByUser11(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_USER_11", indata, pagination, routeEparchyCode);
    }
    
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserOtherByUser21(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_USER_21", indata, pagination, routeEparchyCode);
    }
    
    /**
     * REQ201512020036 
     * */
    public static IDataset queryUserOtherByUser2(String serialNumber, String routeEparchyCode, Pagination pagination) throws Exception
    {
        IData indata = new DataMap();
        indata.put("PARA_CODE1", serialNumber);

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_USEROTHER_BY_USER_2", indata, pagination, routeEparchyCode);
    }
}
