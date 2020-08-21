
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class LimitBlackwhiteBean
{

    /**
     * 成员主动销户时 删除用户受限表数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset deleteLimitBlackWhite(String serialNumber) throws Exception
    {
        IData temp = new DataMap();
        temp.put("SERIAL_NUMBER", serialNumber);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL(" delete from  TF_F_LIMIT_BLACKWHITE where serial_number=:SERIAL_NUMBER");
        Dao.executeUpdates(sp);
        return new DatasetList();
    }

    /**
     * 查询黑白名单记录
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static void deleteLimitBlackWhite(String serialNumber, String bizInCode, String custName) throws Exception
    {
        IData temp = new DataMap();
        temp.put("SERIAL_NUMBER", serialNumber);
        temp.put("BIZ_IN_CODE", bizInCode);
        temp.put("CUST_NAME", custName);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL(" delete from  TF_F_LIMIT_BLACKWHITE where serial_number=:SERIAL_NUMBER AND BIZ_IN_CODE=:BIZ_IN_CODE");
        Dao.executeUpdates(sp);
    }

    /**
     *添加黑白名单记录
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public static void insertLimitBlackWhite(String serialNumber, String bizInCode, String custName) throws Exception
    {
        IData temp = new DataMap();
        temp.put("SERIAL_NUMBER", serialNumber);
        temp.put("BIZ_IN_CODE", bizInCode);
        temp.put("CUST_NAME", custName);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL(" insert into TF_F_LIMIT_BLACKWHITE(SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE) values(:SERIAL_NUMBER," + ":BIZ_IN_CODE,:CUST_NAME,sysdate)");
        Dao.executeUpdates(sp);
    }

    /**
     * 根据集团名称，获取受限表信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryByCustName(String custName, Pagination pagination) throws Exception
    {
        IData temp = new DataMap();
        temp.put("CUST_NAME", custName);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL("select  SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE from TF_F_LIMIT_BLACKWHITE  where  cust_name=:CUST_NAME ");
        return Dao.qryByParse(sp, pagination);
    }

    /**
     * 根据ec接入号，获取用户受限表信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryByEcBizInCode(String bizInCode, Pagination pagination) throws Exception
    {
        IData temp = new DataMap();
        temp.put("BIZ_IN_CODE", bizInCode);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL("select  SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE from TF_F_LIMIT_BLACKWHITE  where  BIZ_IN_CODE=:BIZ_IN_CODE ");
        return Dao.qryByParse(sp, pagination);
    }

    /**
     * 根据SERIAL_NUMBER查询受限信息
     */
    public static IDataset queryBySn(String serialNumber, Pagination pagination) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", serialNumber);
        SQLParser sp = new SQLParser(inparams);
        sp.addSQL("select  SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE from TF_F_LIMIT_BLACKWHITE  where  SERIAL_NUMBER=:SERIAL_NUMBER ");
        return Dao.qryByParse(sp, pagination);
        // Dao.qryByCodeParser("TF_F_LIMIT_BLACKWHITE", "SEL_BY_SERALNUM", inparams, pagination);
    }

    /**
     * 查询黑白名单记录
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryLimitBlackWhite(String serialNumber, String bizInCode) throws Exception
    {
        IData temp = new DataMap();
        temp.put("SERIAL_NUMBER", serialNumber);
        temp.put("BIZ_IN_CODE", bizInCode);

        SQLParser sp = new SQLParser(temp);
        sp.addSQL(" select * from  TF_F_LIMIT_BLACKWHITE where serial_number=:SERIAL_NUMBER and BIZ_IN_CODE=:BIZ_IN_CODE");
        return Dao.qryByParse(sp);
    }

}
