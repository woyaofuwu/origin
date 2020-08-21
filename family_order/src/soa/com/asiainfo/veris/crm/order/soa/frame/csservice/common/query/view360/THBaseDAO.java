
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THBaseDAO
{
    /**
     * 根据条件查询账户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getAccountInfos(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TF_F_ACCOUNT ");
        parser.addSQL(" where CUST_ID = :CUST_ID");
        return Dao.qryByParse(parser, pagination);

    }

    /**
     * 根据条件查询账户信息 Cg库
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getAccountInfosByCg(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_ACCOUNT_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from "+tableName+" ");
        parser.addSQL(" where CUST_ID = :CUST_ID");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_HIS);

    }

    public IDataset getCustomerInfos(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from tf_f_customer ");
        parser.addSQL(" where CUST_ID = :CUST_ID  ");
        parser.addSQL("   and partition_id = mod(to_number(:CUST_ID),10000) ");
        parser.addSQL("   and rownum < 2 ");
        return Dao.qryByParse(parser, pagination);
    }

    public IDataset getCustomerInfosByCg(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_CUSTOMER_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from "+tableName+" ");
        parser.addSQL(" where CUST_ID = :CUST_ID and rownum < 2 ");
        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_HIS);
    }

    /**
     * 根据条件查询客户信息
     * 
     * @param param
     * @return
     */
    public IDataset getCustPersonInfos(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from  tf_f_cust_person ");
        parser.addSQL(" where CUST_ID = :CUST_ID  ");
        parser.addSQL("   and partition_id = mod(to_number(:CUST_ID), 10000) ");
        parser.addSQL("   and rownum < 2 ");
        return Dao.qryByParse(parser, pagination);
    }

    /**
     * 根据条件查询客户信息 Cg库
     * 
     * @param param
     * @return
     */
    public IDataset getCustPersonInfosByCg(IData param, Pagination pagination) throws Exception
    {
        String cust_id = param.getString("CUST_ID", "");
        if ("".equals(cust_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_CUST_PERSON_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from  "+tableName+"");
        parser.addSQL(" where CUST_ID = :CUST_ID and rownum < 2 ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_HIS);
    }

    /**
     * 查询用户相关信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getUserInfos(IData param, Pagination pagination) throws Exception
    {
        String user_id = param.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from TF_F_USER ");
        parser.addSQL(" where USER_ID = :USER_ID and rownum < 2 ");
        IDataset dataset = Dao.qryByParse(parser, pagination);
        return dataset;

    }

    /**
     * 查询用户相关信息 Cg库
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getUserInfosByCg(IData param, Pagination pagination) throws Exception
    {
        String user_id = param.getString("USER_ID", "");
        if ("".equals(user_id))
        {
            return new DatasetList();
        }
        String year_id = param.getString("QUERY_YEAR","");
        if(StringUtils.isBlank(year_id)){
        	return new DatasetList();
        }
        String tableName = "TF_BHB_TRADE_USER_"+year_id;
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select * from "+tableName+" ");
        parser.addSQL(" where USER_ID = :USER_ID and rownum < 2 ");

        return Dao.qryByParse(parser, pagination, Route.CONN_CRM_HIS);

    }
}
