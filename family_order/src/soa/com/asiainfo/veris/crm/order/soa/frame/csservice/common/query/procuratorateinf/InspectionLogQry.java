
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class InspectionLogQry
{

    /**
     *查询接口数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getInspectionLog(IData data,Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_INSPECTION_LOG", data,pagination);
    }

    /**
     * 按地址查询信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoByAddress(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_USERS_BY_ADDRESS", data);

        return result;

    }

    /**
     * 按名字查询信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoByCustName(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_USERS_BY_CUSTNAME", data);

        return result;

    }

    /**
     * 按证件查用户信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoByLincese(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_USERS_BY_LINCESE", data);

        return result;

    }

    /**
     * 按手机号码查询信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoByServialNumber(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_USERS_BY_SERNUMBER", data);

        return result;

    }

    /**
     * 05: 证件号码查询关联信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationInfoByLincese(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_RELATION_BY_LINCESE", data);

        return result;

    }

    /**
     * 05:手机号码查询关联信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationInfoBySerNumber(IData data) throws Exception
    {

        IDataset result = new DatasetList();
        result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_RELATION_BY_NUMBER", data);

        return result;

    }
    
    /**
     * 按IMEI查询信息
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryInfoByIMEI(IData data) throws Exception
    {

    	IDataset result = new DatasetList();
    	result = Dao.qryByCode("TL_O_INSPECTIONLOG", "SEL_USERS_BY_IMEI", data);

    	return result;

    }

    /**
     * 添加接口数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static int saveInspectionLog(IData data) throws Exception
    {

        int i = Dao.executeUpdateByCodeCode("TL_O_INSPECTIONLOG", "INS_INSPECTION_LOG", data);

        return i;

    }

    /**
     * 修改接口数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static int upInspectionLog(IData data) throws Exception
    {

        int i = Dao.executeUpdateByCodeCode("TL_O_INSPECTIONLOG", "UPD_INSPECTION_LOG", data);

        return i;

    }

}
