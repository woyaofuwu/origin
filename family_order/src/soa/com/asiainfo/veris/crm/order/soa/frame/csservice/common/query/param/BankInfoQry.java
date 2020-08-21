
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BankInfoQry
{
    // 根据银行名称或编码模糊查询
    public static IDataset getBankByBank(String EPARCHY_CODE, String SUPER_BANK_CODE, String BANK_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        param.put("BANK_CODE", BANK_CODE);
        return Dao.qryByCode("TD_B_BANK", "SEL_BY_BANK_OR_CODE", param, page, Route.CONN_CRM_CEN);
    }

    // 根据银行名称或编码模糊查询
    public static IDataset getBankByBank(String EPARCHY_CODE, String SUPER_BANK_CODE, String BANK_CODE, String BANK) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        param.put("BANK_CODE", BANK_CODE);
        param.put("BANK", BANK);

        return Dao.qryByCodeParser("TD_B_BANK", "SEL_BY_BANK_OR_CODE", param, Route.CONN_CRM_CEN);
    }

    // 根据银行名称或编码模糊查询
    public static IDataset getBankByBank(String EPARCHY_CODE, String SUPER_BANK_CODE, String BANK_CODE, String BANK, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        param.put("BANK_CODE", BANK_CODE);
        param.put("BANK", BANK);

        return Dao.qryByCodeParser("TD_B_BANK", "SEL_BY_BANK_OR_CODE", param, page, Route.CONN_CRM_CEN);
    }

    // 根据银行名称或编码模糊查询
    public static IDataset getBankByBankCtt(String EPARCHY_CODE, String SUPER_BANK_CODE, String BANK_CODE, String BANK) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        param.put("BANK_CODE", BANK_CODE);
        param.put("BANK", BANK);

        return Dao.qryByCodeParser("TD_B_BANK_CTT", "SEL_BY_BANK_OR_CODE", param, Route.CONN_CRM_CEN);
    }

    // 根据上级银行取下级银行,是否需要根据地区来取
    public static IDataset getBankBySuperBank(String SUPER_BANK_CODE, Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUPER_BANK_CODE", StringUtils.isBlank(SUPER_BANK_CODE) ? "" : SUPER_BANK_CODE);
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return Dao.qryByCode("TD_B_BANK", "SEL_BY_SUPBANK", inparam, page, Route.CONN_CRM_CEN);
    }

    /**
     * 根据SUPER_BANK_CODE、EPARCHY_CODE、CITY_CODE，开户界面刷新银行列表逻辑
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IDataset getBankBySuperBank3(String SUPER_BANK_CODE, String city_code, String eparchy_code) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        inparam.put("CITY_CODE", city_code);
        inparam.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_BANK", "SEL_BY_SUPBANK3", inparam, Route.CONN_CRM_CEN);
    }

    // 根据上级银行取下级银行,是否需要根据地区来取
    public static IDataset getBankBySuperBankCtt(String SUPER_BANK_CODE, Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SUPER_BANK_CODE", StringUtils.isBlank(SUPER_BANK_CODE) ? "" : SUPER_BANK_CODE);
        inparam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return Dao.qryByCode("TD_B_BANK_CTT", "SEL_BY_SUPBANK", inparam, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getBankInfo(Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return Dao.qryByCode("TD_B_BANK", "SEL_BY_EPARCHYCODE", inparam, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getBankInfoNotCash(Pagination page) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        return Dao.qryByCode("TD_B_BANK", "SEL_ALL_ORDERBY_BANK", inparam, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getBrandByBank(String EPARCHY_CODE, String BANK_CODE) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("BANK_CODE", BANK_CODE);
        return Dao.qryByCode("TD_B_BANK", "SEL_BY_BANK", param, Route.CONN_CRM_CEN);
    }

    public static String getCttBankNameByBankCode(String bankCode) throws Exception
    {
        String bankName = "";

        if ("0".equals(bankCode))
        {
            bankName = "其他";
        }
        else
        {
            bankName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK_CTT", new String[]
            { "BANK_CODE", "EPARCHY_CODE" }, "BANK", new String[]
            { bankCode, CSBizBean.getTradeEparchyCode() });
        }

        return bankName;
    }

    public static IDataset qryBankInfoByPk(String bankCode, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("BANK_CODE", bankCode);
        param.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_BANK", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qryBankInfoBySuperBankCode() throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData param = new DataMap();
        IDataset list = Dao.qryByCode("TD_S_SUPERBANK", "SEL_BY_ALL", param, Route.CONN_CRM_CEN);
        if (null != list && !list.isEmpty())
        {
            for (int i = 0; i < list.size(); i++)
            {
                IData data = list.getData(i);
                IData bank = new DataMap();
                bank.put("BANK_CODE", data.getString("SUPER_BANK_CODE"));
                bank.put("BANK", data.getString("SUPER_BANK"));
                returnSet.add(bank);
            }
        }
        return returnSet;
    }

    public static IDataset qryBankInfoBySuperBankCode(String superBankCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SUPER_BANK_CODE", superBankCode);
        return Dao.qryByCode("TD_B_BANK", "SEL_ALL_BY_SUPBANK", param, Route.CONN_CRM_CEN);
    }

    public static IDataset qrySuperBankInfo() throws Exception
    {
        IData param = new DataMap();

        SQLParser sql = new SQLParser(param);

        sql.addSQL(" SELECT SUPER_BANK_CODE, SUPER_BANK, REMARK, UPDATE_STAFF_ID, UPDATE_DEPART_ID, TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME");
        sql.addSQL("   FROM TD_S_SUPERBANK");

        return Dao.qryByParse(sql, Route.CONN_CRM_CEN);
    }

    /**
     * 根据上级银行查询银行信息
     * 
     * @author tengg
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryBackCode(String EPARCHY_CODE, String SUPER_BANK_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);
        param.put("SUPER_BANK_CODE", SUPER_BANK_CODE);
        return Dao.qryByCode("TD_B_BANK", "SEL_BY_SUPBANK", param, page, Route.CONN_CRM_CEN);
    }

    public static String qryBankNameByBankCodeOrCCT(String bankCode, String TTGrp) throws Exception
    {

        String bankName = "";

        if ("0".equals(bankCode))
        {
            bankName = "其他";
        }
        else if (StringUtils.equals(TTGrp, "false"))
        {
            bankName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK", new String[]
            { "BANK_CODE", "EPARCHY_CODE" }, "BANK", new String[]
            { bankCode, CSBizBean.getTradeEparchyCode() });
        }
        else if (StringUtils.equals(TTGrp, "true"))
        {
            bankName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_BANK_CTT", new String[]
            { "BANK_CODE", "EPARCHY_CODE" }, "BANK", new String[]
            { bankCode, CSBizBean.getTradeEparchyCode() });
        }

        return bankName;
    }
}
