
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MoInfoQry
{
    public static String getSimbyPurview(String context) throws Exception
    {
        if (context == null || context.length() == 0)
            return "";
        String staffId = CSBizBean.getVisit().getStaffEparchyName();// pd.getContext().getStaffId();
        String rightCode = "CSM_SIM_SHOWALL";

        String sql = "select 1 from td_m_dataright where data_code= :DATA_CODE ";
        IData pa = new DataMap();
        pa.put("DATA_CODE", rightCode);
        SQLParser parser = new SQLParser(pa);
        parser.addSQL(sql);
        IDataset ds = new DatasetList();
        ds = Dao.qryByParse(parser, new Pagination());
        if ((ds == null) || (ds.size() == 0)) // 没有定义，不控制权限
            return context;

        String sql_1 = "select data_code,right_class,oper_special " + " from tf_m_staffdataright where data_type<'2' and right_attr='0' " + " and right_tag='1' and (rsvalue1 is null or rsvalue1<>'1') "
                + " and staff_id=:STAFF_ID and data_code=:DATA_CODE " + " union " + " select data_code,right_class,oper_special " + " from tf_m_roledataright where role_code in(" + " select data_code from tf_m_staffdataright "
                + " where data_type<'2' and right_attr='1' and right_tag='1' " + " and staff_id=:STAFF_ID ) and (rsvalue1 is null or rsvalue1<>'1') and data_code= :DATA_CODE " + " minus" + " select data_code,right_class,oper_special "
                + " from tf_m_staffdataright where data_type<'2' and right_attr='0' " + " and right_tag='0' and (rsvalue1 is null or rsvalue1<>'1') " + " and staff_id= :STAFF_ID and data_code= :DATA_CODE ";
        IData pa_1 = new DataMap();
        pa_1.put("STAFF_ID", staffId);
        pa_1.put("DATA_CODE", rightCode);
        SQLParser parser_1 = new SQLParser(pa_1);
        parser_1.addSQL(sql_1);
        IDataset ds_1 = Dao.qryByParse(parser_1);
        if (ds_1.size() > 0)
            return context;
        int clen = context.length();
        return context.substring(0, clen > 6 ? clen - 6 : 0) + "******";
    }

    public static String getSimStateCode(String simStateCode) throws Exception
    {
        String sql = "SELECT RES_STATE FROM RES_STATE_DEF WHERE RES_TYPE_CODE='" + "1" + "' AND STATE_CODE= :SIM_STATE_CODE ";
        IData pa = new DataMap();
        pa.put("SIM_STATE_CODE", simStateCode);
        SQLParser parser = new SQLParser(pa);
        parser.addSQL(sql);
        IDataset simState = Dao.qryByParse(parser,Route.CONN_RES);
        if (simState == null || simState.size() <= 0)
            return simStateCode;
        else
            return simState.getData(0).getString("RES_STATE");
    }

    public static String getSimTypeCode(String simTypeCode) throws Exception
    {
        String sql = "SELECT KIND_NAME FROM TD_S_RESKIND WHERE RES_TYPE_CODE='" + "1" + "' AND RES_KIND_CODE= :SIM_TYPE_CODE ";
        IData pa = new DataMap();
        pa.put("SIM_TYPE_CODE", simTypeCode);
        SQLParser parser = new SQLParser(pa);
        parser.addSQL(sql);
        IDataset simType = Dao.qryByParse(parser);
        if (simType == null || simType.size() <= 0) // today 这个好像有问题，要再调试一下
            return simTypeCode;
        else
            return simType.getData(0).getString("KIND_NAME");
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getSnByEmptyCardId(IData inparam, String ismi) throws Exception
    {
        if (ismi.length() == 0 || ismi == null)
        {
            return "";
        }
        String sql = "SELECT SIM_CARD_NO FROM tf_r_emptycard WHERE empty_card_id = :IMSI ";
        IData pa = new DataMap();
        pa.put("IMSI", ismi);
        SQLParser parser = new SQLParser(pa);
        parser.addSQL(sql);
        IDataset userDataset = Dao.qryByParse(parser);
        if (userDataset == null || userDataset.size() == 0)
            return "";
        else
            return userDataset.getData(0).getString("SIM_CARD_NO", "");
    }

    /**
     * 根据选择的类型和号码查询对应的SIM卡信息 QCS_GetResourceName
     * 
     * @param pd
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getUserSimCardInfoByCodeTypeAndId(IData param) throws Exception
    {
        IDataset simcardDataset = new DatasetList();

        // 0-服务号码， 1-SIM卡号
        String indexs = param.getString("cond_QUERY_TYPE", "");// SIMCARD_QUERY_TYPE", "");
        // today add
        String serialNumber = "";// = param.getString("SERIAL_NUMBER", "");
        if ("0".equals(indexs)) // 手机号码
            serialNumber = param.getString("cond_SERIAL_NUMBER_", "");
        if ("1".equals(indexs)) // SIM卡号
            serialNumber = param.getString("cond_SERIAL_NUMBER1_", "");
        if ("2".equals(indexs)) // 白卡卡号
            serialNumber = param.getString("cond_SERIAL_NUMBER2_", "");
        String sqlRef = "";
        if (indexs.equals("2"))
        {
            serialNumber = getSnByEmptyCardId(param, serialNumber);
        }
        if (indexs.equals("0"))
        {
            // 根据tf_f_user_res表。获取用户的sim_card_no资料
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("RES_TYPE_CODE", "1");

            IDataset bindingDataset = Dao.qryByCode("TF_F_USER_RES", "SEL_BY_SERIALNREMOVE", param);
            if (bindingDataset == null || bindingDataset.size() == 0)
            {
                IDataset dsErr = new DatasetList();
                IData dmErr = new DataMap();

                dsErr.add(dmErr);

                dmErr.put("ERRMSG", "根据服务号码未获取到用户的资源信息错误(USER_RES)[" + serialNumber + "]");
                return dsErr;
            }
            IData bindingSimData = new DataMap();
            bindingSimData = bindingDataset.getData(0);
            if (bindingSimData == null || bindingSimData.getString("RES_CODE") == null)
            {
                IDataset dsErr = new DatasetList();
                IData dmErr = new DataMap();
                dsErr.add(dmErr);
                dmErr.put("ERRMSG", "根据服务号码未获取到用户的SIM资源信息错误(USER_RES)[" + serialNumber + "]");
                return dsErr;
            }
            // 根据SIM卡进行查询
            serialNumber = bindingSimData.getString("RES_CODE");
        }
        param.put("X_GETMODE", "6");
        param.put("RES_NO", serialNumber);

        // add by jingxs 20120814 start
        String route_eparchy = CSBizBean.getTradeEparchyCode();// pd.getContext().getEpachyId();
        if ("HNAN".equals(route_eparchy) || "07XX".equals(route_eparchy))
        {
            IData param1 = new DataMap();
            param1.put("SIM_CARD_NO", serialNumber);
            IDataset eparchy_data_set = null; // Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_SIM", param1,
            // Route.CONN_CRM_1);
            if (eparchy_data_set == null || eparchy_data_set.size() < 1)
            {
                eparchy_data_set = null; // Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_SIM", param1, Route.CONN_CRM_2);
                if (eparchy_data_set == null || eparchy_data_set.size() < 1)
                {
                    eparchy_data_set = null;// Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_SIM", param1,
                    // Route.CONN_CRM_3);
                    if (eparchy_data_set == null || eparchy_data_set.size() < 1)
                    {
                        eparchy_data_set = null;// Dao.qryByCode("TF_R_SIMCARD_USE", "SEL_BY_SIM", param1,
                        // Route.CONN_CRM_4);
                    }
                }
            }
            if (eparchy_data_set != null && eparchy_data_set.size() > 0)
            {
                route_eparchy = eparchy_data_set.getData(0).getString("EPARCHY_CODE", route_eparchy);
            }
        }
        // add by jingxs 20120814 end
        param.put("RES_TRADE_CODE", "IGetSimCardInfo");
        param.put(Route.ROUTE_EPARCHY_CODE, route_eparchy);
        // simcardDataset = dao.queryListByCodeCode("TF_R_SIMCARD_USE", sqlRef, param);
        // simcardDataset = TuxedoHelper.callTuxSvc(pd,"QRM_IGetResInfo",param); today
        simcardDataset = test();
        simcardDataset.getData(0).put("SIM_TYPE_CODE", "C"); // today for test
        simcardDataset.getData(0).put("SIM_STATE_CODE", "4"); // today for test
        String simTypeCode = simcardDataset.getData(0).getString("SIM_TYPE_CODE");
        String simStateCode = simcardDataset.getData(0).getString("SIM_STATE_CODE");
        String simTypeName = getSimTypeCode(simTypeCode);
        String simStateName = getSimStateCode(simStateCode);

        simcardDataset.getData(0).put("SIM_TYPE_NAME", simTypeName);
        simcardDataset.getData(0).put("SIM_STATE_NAME", simStateName);
        simcardDataset.getData(0).put("SIM_CARD_NO", getSimbyPurview(simcardDataset.getData(0).getString("SIM_CARD_NO")));
        simcardDataset.getData(0).put("X_RECORDNUM", "1");// today for test

        return simcardDataset;
    }

    public static IDataset test()
    {
        IDataset set1 = new DatasetList();
        IData d1;
        IData d2;
        IDataset set2 = new DatasetList();
        // {
        d1 = new DataMap();
        d2 = new DataMap();
        d1.put("aa", "11111111111111");
        d2.put("bb", "1");
        d1.put("SERIAL_NUMBER", "SERIAL_NUMBER");
        d1.put("PIN", "PIN");
        d1.put("PIN2", "PIN2");
        d1.put("SIM_CARD_NO", "SIM_CARD_NO");
        d1.put("PUK", "PUK");
        d1.put("PUK2", "PUK2");
        d1.put("SIM_TYPE_NAME", "SIM_TYPE_NAME");
        d1.put("IMSI", "IMSI");
        d1.put("SIM_STATE_NAME", "SIM_STATE_NAME");
        d1.put("ESN", "ESN");
        d1.put("MOFFICE_ID", "MOFFICE_ID");
        d1.put("KI_STATE", "KI_STATE");
        d1.put("STAFF_ID_CODE", "STAFF_ID_CODE");
        d1.put("KI", "KI");
        d1.put("ASSIGN_STAFF_ID", "ASSIGN_STAFF_ID");
        d1.put("OPER_STAFF_ID", "OPER_STAFF_ID");
        d1.put("ASSIGN_TIME", "ASSIGN_TIME");
        d1.put("OPER_TIME", "OPER_TIME");
        d1.put("EPARCHY_CODE", "EPARCHY_CODE");
        d1.put("ERIAL_NUMBER_CODE", "ERIAL_NUMBER_CODE");
        d1.put("CITY_CODE", "CITY_CODE");
        d1.put("STAFF_ID_CODE", "STAFF_ID_CODE");
        d1.put("STAFF_ID", "STAFF_ID");
        d1.put("TIME_CODE", "TIME_CODE");
        d1.put("STOCK_ID", "STOCK_ID");
        d1.put("ACTIVE_STAFF_ID", "ACTIVE_STAFF_ID");
        d1.put("STOCK_ID_O", "STOCK_ID_O");
        d1.put("ACTIVE_TIME", "ACTIVE_TIME");
        d1.put("REMARK", "REMARK");
        d2.put("KI", "KI");
        d2.put("endCardNo", "结束卡号");
        d2.put("simPrice", "卡面值（元）");
        d2.put("singlePrice", "单价（元）");
        d2.put("totalPrice", "总价（元）");
        d2.put("rowCount", "数量");
        d2.put("valueCode", "valueCode");
        d2.put("parvalue", "parvalue");
        d2.put("activateInfo", "activateInfo");
        d2.put("devicePrice", "devicePrice");
        set1.add(d1);
        set2.add(d2);

        return set1;
    }

    /**
     * 手机号码归属地查询(全国)
     * 
     * @author chenhao 2009-6-21
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryMpAreaCode(IData inparam) throws Exception
    {
        // 参考下F:\crm\j2ee_hnan\saleserv\src\com\linkage\saleserv\view\HNAN\person\sundryquery\other\QueryMpAreaCode.java
        IDataset set1 = new DatasetList();
        IData d1 = new DataMap();
        d1.put("AREA_CODE", "07XX");
        d1.put("PROVINCE", "湖南省");
        d1.put("CITY_NAME", "长沙市");
        set1.add(d1);
        return set1; // today QAM_MPAREACODE_QUERY
    }
}
