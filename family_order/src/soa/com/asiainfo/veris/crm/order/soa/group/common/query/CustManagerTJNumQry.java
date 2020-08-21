
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class CustManagerTJNumQry
{
    /**
     * 当月同一号码入库数
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset countCustManagerTjNum(IData param) throws Exception
    {
        param.put("IN_DATE", SysDateMgr.getSysDate("yyyy-MM"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT T.* FROM TF_F_CUSTMANAGER_TJNUM T WHERE T.Tjnumber=:TJNUMBER AND to_char(T.In_Date,'yyyy-MM')=:IN_DATE");
        return Dao.qryByParse(parser);
    }

    public static boolean createInfosByParam(IData param) throws Exception
    {
        boolean num = true;

        num = Dao.insert("TF_F_CUSTMANAGER_TJNUM", param);
        if (num)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 根据类型查询营销活动产品
     */

    public static IDataset getProductInfos(IData param) throws Exception
    {
    	if (ProvinceUtil.isProvince(ProvinceUtil.HAIN)){
    		IDataset poDatas = UpcCall.qrySaleActiveCatalogs();
    		return poDatas;
    	}else{
    		return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_PRODUCTMODE_NOPRIV", param);
    	}
        
    }

    /**
     * 执行查询
     * 
     * @param param
     * @param pageinfo
     * @return
     */
    public static IDataset queryCustManagertjNums(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT LOG_ID,MANGER_STAFF_ID,MANGER_NAME,TJNUMBER,ACTIVE_ID,ACTIVE_NAME,REMARK,IN_MODE, ");
        parser.addSQL(" to_char(IN_DATE,'yyyy-mm-dd') as IN_DATE, IN_STAFF_ID,IN_DEPART_ID ");
        parser.addSQL(" FROM TF_F_CUSTMANAGER_TJNUM WHERE 1=1 ");
        String staffId = param.getString("MANAGER_STAFF_ID", "");
        if (staffId.trim().length() > 0)
        {
            parser.addSQL(" AND MANGER_STAFF_ID=:MANAGER_STAFF_ID ");
        }
        String inDateStart = param.getString("IN_DATE_START", "");
        if (inDateStart.trim().length() > 0)
        {
            parser.addSQL(" AND to_char(IN_DATE,'yyyy-mm-dd') >= :IN_DATE_START ");
        }
        String inDateEnd = param.getString("IN_DATE_END", "");
        if (inDateEnd.trim().length() > 0)
        {
            parser.addSQL(" AND to_char(IN_DATE,'yyyy-mm-dd') <=:IN_DATE_END ");
        }
        String activeId = param.getString("ACTIVE_ID", "");
        if (activeId.trim().length() > 0)
        {
            parser.addSQL(" AND ACTIVE_ID=:ACTIVE_ID ");
        }
        String tjNumber = param.getString("TJNUMBER", "");
        if (tjNumber.trim().length() > 0)
        {
            parser.addSQL(" AND TJNUMBER=:TJNUMBER ");
        }
        parser.addSQL(" ORDER BY LOG_ID DESC ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    /**
     * 根据手机号查询是否是集团成员
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGbmBySerialNumber(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT M.* FROM tf_f_cust_groupmember M JOIN TF_F_CUST_GROUP G ON M.GROUP_ID=G.GROUP_ID ");
        parser.addSQL(" WHERE M.REMOVE_TAG='0' ");
        parser.addSQL(" AND G.EPARCHY_CODE='0898' ");
        parser.addSQL(" AND M.SERIAL_NUMBER=:TJNUMBER");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据工号查询集团客户经理
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int queryGroupCustManagerByStaffId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select count(*) as C from tf_f_cust_group where cust_manager_id=:STAFF_ID and remove_tag='0' ");
        IDataset list = Dao.qryByParse(parser);
        int result = 0;
        if (IDataUtil.isNotEmpty(list))
        {
            result = Integer.parseInt(list.getData(0).getString("C", "0"));
        }
        return result;
    }

    public static IDataset queryInfosByParam(String log_id) throws Exception
    {
        IData param = new DataMap();
        param.put("LOG_ID", log_id);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * FROM TF_F_CUSTMANAGER_TJNUM ");
        parser.addSQL(" WHERE 1= 1 AND LOG_ID = :LOG_ID ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据手机号查询是否是移动客户
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int queryUserBySerialNumber(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select count(*) C from tf_f_user U ");
        parser.addSQL(" WHERE U.REMOVE_TAG='0' ");
        parser.addSQL(" AND u.SERIAL_NUMBER=:TJNUMBER");
        IDataset list = Dao.qryByParse(parser);
        int result = 0;
        if (list != null && list.size() > 0)
        {
            result = Integer.parseInt(list.getData(0).getString("C", "0"));
        }
        return result;
    }

    /**
     * 通过手机号码提取客户经理信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getCustManagerBySerialNumber(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select CUST_MANAGER_ID, CUST_MANAGER_NAME,DEPART_ID FROM tf_f_cust_manager_staff WHERE 1=1 ");
        parser.addSQL(" and valid_tag='1' ");
        parser.addSQL(" and serial_number=:CUST_SERIAL_NUMBER");
        parser.addSQL(" and rownum<2");// 只取一条记录
        return Dao.qryByParse(parser);
    }
    //    
    // /**
    // * 根据手机号查询是否是移动客户
    // *
    // * @param param
    // * @return
    // * @throws Exception
    // */
    // public static IDataset queryUserBySerialNumber(IData param) throws Exception {
    // SQLParser parser = new SQLParser(param);
    // parser.addSQL("select count(*) C from tf_f_user U ");
    // parser.addSQL(" WHERE U.REMOVE_TAG='0' ");
    // parser.addSQL(" AND u.SERIAL_NUMBER=:TJNUMBER ");
    // return Dao.qryByParse(parser);
    // }
}
