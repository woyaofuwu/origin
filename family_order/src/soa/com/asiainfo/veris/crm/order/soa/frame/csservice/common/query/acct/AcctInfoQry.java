
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UPayModeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class AcctInfoQry
{

    /**
     * 根据ACCT_ID、CUST_ID查询账户资料
     * 
     * @param acctId
     * @param custId
     * @param removeTag
     * @return
     * @throws Exception
     */
    public static IDataset getAcctInfoByAcctIdCustId(String acctId, String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        param.put("CUST_ID", custId);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_CUSTID_ACCTID", param);
    }

    /**
     * 根据合同号查询帐户信息
     */
    public static IDataset getAcctInfoByContractNo(IData inparams, Pagination page, String routeId) throws Exception
    {

        IDataset resultset = Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_CONTRACT_NO", inparams, page, routeId);

        if (IDataUtil.isEmpty(resultset))
        {
            return null;
        }

        for (int row = 0; row < resultset.size(); row++)
        {
            IData data = (IData) resultset.get(row);

            data.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(data.getString("EPARCHY_CODE")));

            String payModeCode = data.getString("PAY_MODE_CODE");

            data.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(payModeCode));

            if (StringUtils.isNotBlank(payModeCode))
            {
                data.put("BANK", UBankInfoQry.getBankNameByBankCode(data.getString("BANK_CODE")));
            }
        }

        return resultset;
    }

    /**
     * 根据合同号查询帐户信息
     */
    public static IDataset getAcctInfoByContractNoForGrp(IData inparams, Pagination page) throws Exception
    {
        return getAcctInfoByContractNo(inparams, page, Route.CONN_CRM_CG);
    }

    public static IDataset getAcctInfoByCustId(String custId) throws Exception
    {
        return getAcctInfoByCustId(custId, null);
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     */
    /**
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getAcctInfoByCustId(String custId, String routeId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT PARTITION_ID, TO_CHAR(ACCT_ID) ACCT_ID, ");
        sql.append("TO_CHAR(CUST_ID) CUST_ID, PAY_NAME, PAY_MODE_CODE, ");
        sql.append("ACCT_PASSWD, NET_TYPE_CODE, EPARCHY_CODE, CITY_CODE, ");
        sql.append("BANK_CODE, BANK_ACCT_NO, ");
        sql.append("TO_CHAR(SCORE_VALUE) SCORE_VALUE, CREDIT_CLASS_ID, ");
        sql.append("TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE, ");
        sql.append("TO_CHAR(CREDIT_VALUE) CREDIT_VALUE, ");
        sql.append("TO_CHAR(DEBUTY_USER_ID) DEBUTY_USER_ID, DEBUTY_CODE, ");
        sql.append("CONTRACT_NO, DEPOSIT_PRIOR_RULE_ID, ");
        sql.append("ITEM_PRIOR_RULE_ID, ");
        sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, ");
        sql.append("REMOVE_TAG, ");
        sql.append("TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE, ");
        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, ");
        sql.append("RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
        sql.append("RSRV_STR10 ");
        sql.append("FROM TF_F_ACCOUNT ");
        sql.append("WHERE CUST_ID = TO_NUMBER(:CUST_ID) ");
        sql.append("AND REMOVE_TAG = '0' ");
        sql.append("order by ACCT_ID ");

        IDataset accountList = Dao.qryBySql(sql, data, routeId);

        if (IDataUtil.isEmpty(accountList))
        {
            return null;
        }

        for (int row = 0, size = accountList.size(); row < size; row++)
        {
            IData account = accountList.getData(row);

            account.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(account.getString("EPARCHY_CODE")));

            String payModeCode = account.getString("PAY_MODE_CODE");

            account.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(payModeCode));

            if (StringUtils.isNotBlank(payModeCode))
            {
                account.put("BANK", UBankInfoQry.getBankNameByBankCode(account.getString("BANK_CODE")));
            }

        }

        return accountList;

    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     */
    public static IDataset getAcctInfoByCustIdDesc(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_PK_ASC", inparams);
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息 统付代付专用 by 陈强 2011-02-23
     */
    public static IDataset getAcctInfoByCustIDForGroupAccPay(IData inparams) throws Exception
    {

        IDataset resultset = Dao.qryByCode("TF_F_ACCOUNT", "GROUP_ACCOUNT_PAY", inparams);
        if (IDataUtil.isEmpty(resultset))
            return resultset;

        for (int i = 0; i < resultset.size(); i++)
        {
            IData map = resultset.getData(i);
            map.put("PAY_MODE_NAME", StaticUtil.getStaticValue("TD_S_PAYMODE", map.getString("PAY_MODE_CODE")));
            map.put("EPARCHY_NAME", StaticUtil.getStaticValue("AREA_CODE", map.getString("EPARCHY_CODE")));
            map.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(map.getString("PRODUCT_ID")));
        }
        return resultset;
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     */
    public static IDataset getAcctInfoByCustIdForGrp(String custId) throws Exception
    {

        return getAcctInfoByCustId(custId, Route.CONN_CRM_CG);
    }

    /**
     * 根据集团编码查TF_F_CUST_GROUP，得到CUST_ID,然后再根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     * 
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getAcctInfoByGrpId(IData inparams, Pagination page) throws Exception
    {
        String groupId = inparams.getString("GROUP_ID");

        IData GroupCustInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(GroupCustInfo))
        {
            return null;
        }

        String custId = GroupCustInfo.getString("CUST_ID");

        return getAcctInfoByCustIdForGrp(custId);
    }

    public static IDataset getAcctInfosBysn(String serialNumber) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_PAYMODECODE_BY_SELNUM", inparam);

    }

    /**
     * 根据BANK_ACCT_NO获取账户信息
     */
    public static IDataset getAcctPayName(String bankAcctNo, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", eparchyCode);
        inparam.put("BANK_ACCT_NO", bankAcctNo);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_PAY_NAME_BY_BANK_ACCT_NO_HAIN", inparam);
    }

    public static IDataset getAcctUserInfoByCustId(String custId) throws Exception
    {
        IDataset resultset = new DatasetList();
        // 查询集团账户信息
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("REMOVE_TAG", "0");
        IDataset acctInfos = Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_CUSTID", inparam);

        if (IDataUtil.isEmpty(acctInfos))
        {
            return resultset;
        }

        for (int i = 0, iSize = acctInfos.size(); i < iSize; i++)
        {
            IData acctInfo = acctInfos.getData(i);
            String acctId = acctInfo.getString("ACCT_ID", "");
            IData tempAcct = new DataMap();
            tempAcct.put("ACCT_ID", acctId);
            tempAcct.put("PAY_NAME", acctInfo.getString("PAY_NAME", ""));
            tempAcct.put("OPEN_DATE", acctInfo.getString("OPEN_DATE", ""));
            tempAcct.put("PAY_MODE_CODE", acctInfo.getString("PAY_MODE_CODE", ""));
            tempAcct.put("EPARCHY_CODE", acctInfo.getString("EPARCHY_CODE", ""));
            tempAcct.put("RSRV_STR8", acctInfo.getString("RSRV_STR8", ""));
            tempAcct.put("RSRV_STR9", acctInfo.getString("RSRV_STR9", ""));
            tempAcct.put("RSRV_STR10", acctInfo.getString("RSRV_STR10", ""));
            tempAcct.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(acctInfo.getString("PAY_MODE_CODE", "")));
            tempAcct.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(acctInfo.getString("EPARCHY_CODE", "")));
            tempAcct.put("PRINT_NAME", StaticUtil.getStaticValue("PRINT_MODE", acctInfo.getString("RSRV_STR8")));
            tempAcct.put("NODE_NAME", StaticUtil.getStaticValue("NODE_MODE", acctInfo.getString("RSRV_STR9")));
            // 查询付费关系信息
            inparam.put("ACCT_ID", acctId);
            IDataset payRelaInfos = Dao.qryByCode("TF_A_PAYRELATION", "SEL_USER_VALID_DEFAULT", inparam);
            if (IDataUtil.isEmpty(payRelaInfos))
            {

                resultset.add(tempAcct);
                continue;
            }

            for (int k = 0, kSize = payRelaInfos.size(); k < kSize; k++)
            {

                IData payRelaInfo = payRelaInfos.getData(k);
                String userId = payRelaInfo.getString("USER_ID", "");
                IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
                if (IDataUtil.isNotEmpty(userInfo))
                {
                    IData tempAcctUser = new DataMap();
                    tempAcctUser.putAll(tempAcct);
                    tempAcctUser.put("DEFAULT_TAG", payRelaInfo.getString("DEFAULT_TAG", ""));
                    tempAcctUser.put("ACT_TAG", payRelaInfo.getString("ACT_TAG", ""));
                    tempAcctUser.put("START_CYCLE_ID", payRelaInfo.getString("START_CYCLE_ID", ""));
                    tempAcctUser.put("END_CYCLE_ID", payRelaInfo.getString("END_CYCLE_ID", ""));
                    tempAcctUser.put("USER_ID", payRelaInfo.getString("USER_ID", ""));

                    tempAcctUser.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
                    tempAcctUser.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
                    tempAcctUser.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(tempAcctUser.getString("PRODUCT_ID")));
                    resultset.add(tempAcctUser);
                    continue;

                }
            }
        }

        return resultset;
    }

    public static IDataset getALLAcctInfoByAcctID(String acctid, String payrelation_query_type, Pagination page) throws Exception
    {
        return getALLAcctInfoByAcctID(acctid, payrelation_query_type, page, null);
    }

    public static IDataset getALLAcctInfoByAcctID(String acctid, String payrelation_query_type, Pagination page, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctid);
        param.put("PAYRELATION_QUERY_TYPE", payrelation_query_type);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_NORMAL_PAYRELATION", param, page, routeId);
    }

    public static IDataset getALLAcctInfoByAcctIDForCG(String acctid, String payQueryType, Pagination page) throws Exception
    {
        return getALLAcctInfoByAcctID(acctid, payQueryType, page, Route.CONN_CRM_CG);
    }

    /**
     * 根据USER_ID查询用户所有付费帐户信息(个人)
     */
    public static IDataset getALLAcctInfoByUserID(IData inparams) throws Exception
    {
        return getALLAcctInfoByUserID(inparams, null, null);
    }

    public static IDataset getALLAcctInfoByUserID(IData param, Pagination page, String routeId) throws Exception
    {

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_NORMAL_PAYRELATION_BYID", param, page, routeId);
    }

    public static IDataset getALLAcctInfoByUserID(String userId, String payQueryType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PAYRELATION_QUERY_TYPE", payQueryType);

        return getALLAcctInfoByUserID(param, page, null);
    }

    public static IDataset getALLAcctInfoByUserIDForCG(String userId, String payQueryType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PAYRELATION_QUERY_TYPE", payQueryType);

        return getALLAcctInfoByUserID(param, page, Route.CONN_CRM_CG);
    }

    /**
     * @description 根据
     * @author LIUZZ
     * @date 5 25, 2011
     * @param inparams
     * @param conName
     *            ""：默认路由地州查询 非空：指定库查询
     * @return
     * @throws Exception
     */
    public static IDataset getAllRelAAByActIdA(IData inparams, String conName, Pagination page) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_ALLRELA_BY_ACCTIDA", inparams, page, conName);
    }

    /**
     * 根据账户标识 ACCT_ID 获取账户托收信息
     * 
     * @param IData
     *            传入参数 [ACCT_ID]
     * @return 帐户托收信息
     * @throws Exception
     */
    public static IDataset getConsignInfoByAcctId(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_ACCOUNT_CONSIGN", "SEL_CONSIGN_BY_ACCTID", param);
    }

    /**
     * 根据CUST_ID查询客户最近有效帐户信息
     */
    public static IDataset getLastAcctInfoByCustID(IData inparams) throws Exception
    {

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_LAST_ACCOUT", inparams);
    }

    /**
     * 根据CUSTID查询账户信息
     * 
     * @param custId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public static IDataset getOldAcctInfo(String custId) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("CUST_ID", custId);
        inparam.put("REMOVE_TAG", 0);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_CUSTID", inparam);
    }

    public static IDataset getPayforUnite(String acctid) throws Exception
    {
        IData param = new DataMap();
        param.put("ACCT_ID", acctid);
        param.put("DEFAULT_TAG", "1");
        param.put("ACT_TAG", "1");

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_PAYRELATION_FOR_UNITE", param);
    }

    /**
     * 获取PAY_ITEMCODE, 调用账务流程返回PAYITEM_CODE
     * 
     * @param product_id
     * @return
     * @throws Exception
     */
    public static String getPayItemCode(String product_id) throws Exception
    {
        IDataset firstItems = CommparaInfoQry.getPayItemsParam("CGM", "1", product_id, null);

        if (IDataUtil.isEmpty(firstItems))
        {
            return product_id;
        }

        StringBuilder firstSb = new StringBuilder();
        firstSb.append("'");
        for (int i = 0, size = firstItems.size(); i < size; i++)
        {
            firstSb.append(firstItems.getData(i).getString("PARA_CODE1"));
            firstSb.append((i == firstItems.size() - 1) ? "'" : "','");
        }
        String temp = firstSb.toString();

        IData data = new DataMap();
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PARENT_ITEM_CODE in (" + temp + ")");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级明细帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        parser.addSQL(" order by note.NOTE_ITEM_CODE");

        IDataset secondItems = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

        if (IDataUtil.isEmpty(secondItems))
        {
            return product_id;
        }

		if ((secondItems.size()==1 && StringUtils.equals(secondItems.getData(0).getString("NOTE_ITEM_CODE", "-1"), "-1")))
        {
            return "-1";
        }

        StringBuilder secondSb = new StringBuilder();
        secondSb.append("");
        for (int j = 0, sz = secondItems.size(); j < sz; j++)
        {
            secondSb.append(secondItems.getData(j).getString("NOTE_ITEM_CODE"));
            secondSb.append("|");
        }

        IData payItemData = AcctCall.qryPayItemCode(secondSb.toString());

        return payItemData.getString("PAYITEM_CODE", "-1");
    }

    /**
     * 获取PAY_ITEMCODE, 调用账务流程返回PAYITEM_CODE
     * 
     * @param itemcodes
     * @return
     * @throws Exception
     */
    public static String getPayItemCode(String[] itemcodes) throws Exception
    {

        StringBuilder firstSb = new StringBuilder();
        firstSb.append("'");
        for (int i = 0, size = itemcodes.length; i < size; i++)
        {
            firstSb.append(itemcodes[i]);
            firstSb.append((i == size - 1) ? "'" : "','");
        }
        String temp = firstSb.toString();

        IData data = new DataMap();
        SQLParser parser = new SQLParser(data);
        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PARENT_ITEM_CODE in (" + temp + ")");
        parser.addSQL(" and note.PRINT_LEVEL='1'"); // 二级明细帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000001'");
        parser.addSQL(" order by note.NOTE_ITEM_CODE");

        IDataset secondItems = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

        if (IDataUtil.isEmpty(secondItems) || (secondItems.size()==1 && StringUtils.equals(secondItems.getData(0).getString("NOTE_ITEM_CODE", "-1"), "-1")))
        {
            return "-1";
        }

        StringBuilder secondSb = new StringBuilder();
        secondSb.append("");
        for (int j = 0, sz = secondItems.size(); j < sz; j++)
        {
            secondSb.append(secondItems.getData(j).getString("NOTE_ITEM_CODE"));
            secondSb.append("|");
        }

        IData payItemData = AcctCall.qryPayItemCode(secondSb.toString());

        return payItemData.getString("PAYITEM_CODE", "-1");
    }

    public static IData getPayItemCodeForGrp(String itemStr) throws Exception
    {

        IData data = new DataMap();
        data.put("DETAIL_ITEMSET", itemStr);

        IDataOutput output = CSAppCall.callAcct("AM_CRM_QryPayItemCode", data, true);
        // IDataset result = CSAppCall.call("http://10.200.130.83:10000/service", "AM_CRM_QryPayItemCode", data, true);
        IDataset result = output.getData();
        if (IDataUtil.isEmpty(result))
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_10);

        IData itemcode = result.getData(0);

        return itemcode;

    }

    /**
     * 判断手机号码是不是有未完工的付费关系变更工完
     */
    public static Boolean getPayrelaAdvFlag(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);
        parser.addSQL("SELECT * FROM tf_b_trade WHERE 1 = 1 ");
        parser.addSQL(" and serial_number=:SERIAL_NUMBER  ");
        parser.addSQL(" AND trade_type_code=:TRADE_TYPE_CODE  ");
        IDataset results = Dao.qryByParse(parser, new Pagination());
        if (results != null && results.size() > 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static IDataset getPayRelaByUserEffected(IData inparams, String eparchyCode) throws Exception
    {

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_ADVPAYRELA_BY_USER_EFFECTED", inparams, eparchyCode);
    }

    /**
     * @Description 根据acctId查询其对应的付费产品信息
     * @author wusf
     * @date 2009-11-27
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getProductInfoByAcctId(String acctId) throws Exception
    {

        IData inParam = new DataMap();
        inParam.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_PRODUCT_BY_ACCTID", inParam);
    }

    public static IDataset getRelAAByActIdAReltypecode(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_BY_ACCTID_RELTYPECODE", inparams);
    }

    /**
     * @description 根据
     * @author wusf
     * @date Oct 20, 2010
     * @param inparams
     * @param conName
     *            ""：默认路由地州查询 非空：指定库查询
     * @return
     * @throws Exception
     */
    public static IDataset getRelAAByActIdAReltypecode(IData inparams, String conName) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_BY_ACCTID_RELTYPECODE", inparams, conName);
    }

    /**
     * @description:根据acct_id_a在指定数据库查询账户关系
     * @author wusf
     * @date Sep 30, 2010
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdA(IData inparams, String conName) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDA", inparams, conName);
    }

    /**
     * @description 根据acct_id_a、acct_id_b查询账户关系
     * @author wusf
     * @date Oct 15, 2010
     * @param inparams
     * @param conName
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdAB(IData inparams, String conName) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_BY_ACCTIDAB", inparams, conName);
    }

    /**
     * @description:根据acct_id_a在指定数据库查询账户关系
     * @author wusf
     * @date Sep 30, 2010
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdATag(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", inparams);
    }

    /**
     * @description:根据acct_id_a在指定数据库查询账户关系
     * @author wusf
     * @date Sep 30, 2010
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdATag(IData inparams, String conName) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", inparams, conName);
    }

    /**
     * @author luoyong2
     * @description: 别乱改，逻辑就是这样的，跟Dao.qryByCodeAllCrm不同
     */
    public static IDataset getRelationAAByActIdATagAllDb(IData inparams) throws Exception
    {
        IDataset allData = new DatasetList();
        IDataset oneData = null;

        for (String db : Route.getAllCrmDb())
        {
            oneData = Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", inparams, db);

            if (IDataUtil.isNotEmpty(oneData))
            {
                for (int j = 0; j < oneData.size(); j++)
                {
                    IData info = oneData.getData(j);
                    info.put("CONN_NAME", db);
                    info.put("LIMIT_NAME", StaticUtil.getStaticValue("PAYRELAADVCHG_LIMIT_TYPE", info.getString("LIMIT_TYPE")));
                }
                allData.addAll(oneData);
            }
        }

        return allData;
    }

    /**
     * @description:根据acct_id_a在CG数据库查询账户关系
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdATagForCg(IData inparams) throws Exception
    {
        IDataset resultset = Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", inparams, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData reuslt = resultset.getData(i);
            reuslt.put("LIMIT_NAME", StaticUtil.getStaticValue("PAYRELAADVCHG_LIMIT_TYPE", reuslt.getString("LIMIT_TYPE")));
        }
        return resultset;
    }

    /**
     * @description:根据acct_id_b在指定数据库查询账户关系
     * @author wusf
     * @date Sep 30, 2010
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdB(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDB", inparams);
    }

    /**
     * @author luoyong2
     * @description: 别乱改，逻辑就是这样的，跟Dao.qryByCodeAllCrm不同
     */
    public static IDataset getRelationAAByActIdBAllDb(IData inparams) throws Exception
    {
        IDataset allData = new DatasetList();
        IDataset oneData = null;

        for (String db : Route.getAllCrmDb())
        {
            oneData = Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", inparams, db);

            if (IDataUtil.isNotEmpty(oneData))
            {
                for (int j = 0; j < oneData.size(); j++)
                {
                    IData info = oneData.getData(j);
                    info.put("CONN_NAME", db);
                    info.put("LIMIT_NAME", StaticUtil.getStaticValue("PAYRELAADVCHG_LIMIT_TYPE", info.getString("LIMIT_TYPE")));
                }
                allData.addAll(oneData);
            }
        }

        return allData;
    }

    public static IDataset getRelationAAByActIdBTag(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDBTAG", inparams);
    }

    /**
     * @description:根据acct_id_b在指定数据库查询账户关系
     * @author wusf
     * @date Sep 30, 2010
     * @param inparams
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getRelationAAByActIdBTag(IData inparams, String conName) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDBTAG", inparams, conName);
    }

    public static IDataset getRelationAAByActIdBTagForCg(IData inparams) throws Exception
    {
        IDataset resultset = Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDBTAG", inparams, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(resultset))
            return resultset;
        for (int i = 0; i < resultset.size(); i++)
        {
            IData reuslt = resultset.getData(i);
            reuslt.put("LIMIT_NAME", StaticUtil.getStaticValue("PAYRELAADVCHG_LIMIT_TYPE", reuslt.getString("LIMIT_TYPE")));
        }
        return resultset;
    }

    /**
     * 查询需要需要二次短信确认的资费明细项
     * 
     * @param data
     * @return 资费明细项
     * @throws Exception
     */
    public static IDataset getSmsPayItemsByGrp(IData data) throws Exception
    {

        return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ITEM_ID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getUserAccountInfo(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_ACTINFO_BY_SELNUM", param);

    }

    public static IDataset qamIsCanCancel(IData data) throws Exception
    {

        return CSAppCall.call("CANCANCEL", data);
    }

    public static IDataset qryAccount(IData data, Pagination page) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_ACCOUNT", "SEL_ACT_BY_USERID_LINK_RELATION", data, page);
    }

    public static IDataset qryAcctAcctDayInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.acct_day ");
        parser.addSQL(" from tf_f_account_acctday t where 1=1 ");
        parser.addSQL(" AND t.ACCT_ID = to_number(:ACCT_ID) ");
        parser.addSQL(" AND t.PARTITION_ID = MOD(TO_NUMBER(:ACCT_ID), 10000) ");
        parser.addSQL(" AND t.END_DATE > t.START_DATE ");
        parser.addSQL(" AND t.END_DATE > SYSDATE ");
        parser.addSQL(" order by t.START_DATE desc ");
        return Dao.qryByParse(parser);
    }

    /*
     * vpmn账户信息
     */
    public static IDataset qryAcctInfo(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT a.pay_name pay_name,a.pay_mode_code pay_mode_code ");
        parser.addSQL(" FROM   tf_a_payrelation p, tf_f_account a ");
        parser.addSQL(" WHERE  a.acct_id = TO_NUMBER(p.acct_id)  ");
        parser.addSQL("       AND p.act_tag='1' AND p.act_tag='1' ");
        parser.addSQL("       AND p.user_id=to_number(:USER_ID) AND p.partition_id=MOD(to_number(:USER_ID),10000)  ");
        parser.addSQL("       AND p.start_cycle_id <= p.end_cycle_id ");
        parser.addSQL("       AND p.end_cycle_id=  ");
        parser.addSQL("       (SELECT max(a.end_cycle_id) FROM tf_a_payrelation a  ");
        parser.addSQL("       WHERE a.user_id = TO_NUMBER(:USER_ID)  ");
        parser.addSQL("       AND a.partition_id=mod(TO_NUMBER(:USER_ID),10000)  ");
        // parser.addSQL(" AND a.default_tag = '1' AND p.act_tag='1' ");
        parser.addSQL("       AND p.start_cycle_id <= p.end_cycle_id)   ");
        IDataset resIds = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        return resIds;
    }

    /**
     * 根据银行账号查询
     * 
     * @param bankAcctNo
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByBankAcctNo(String bankAcctNo) throws Exception
    {
        IData param = new DataMap();
        param.put("BANK_ACCT_NO", bankAcctNo);
        IDataset infos = Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_BANK_ACCTNO", param);
        return infos;
    }

    public static IDataset qryAcctInfoByCustIdAndPayMode(String custId, String payModeCode, String removeTag) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PAY_MODE_CODE", payModeCode);
        inparam.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_PAYMODE", inparam);
    }

    /**
     * 联表查询USER/ACCT/PAYRELATION表
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     *             wangjx 2013-7-22
     */
    public static IDataset qryAcctInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SN_DEFAULT_NORMAL", param);
    }
    
    /**
     * 联表查询USER/ACCT/PAYRELATION表
     * 同上面的语句qryAcctInfoBySn
     * 只不过上面的语句没有加上账期，有可能会取到多条（包含失败的）
     * CHENXY3 2015-06-24
     */
    public static IDataset qryAcctDefaultIdBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_ACCTID_DEFAULT_BY_SERIALNUM", param);
    }
    
     
    public static IDataset qryAcctInfoBySnDef(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SN_DEFAULT", param);
    }

    public static IDataset qryAcctInfoBySnDefAbn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SN_DEFAULT_ABNORMAL", param);
    }

    public static IDataset qryAcctInfoBySnDefLAbn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SN_DEFAULT_LAST_ABNORMAL", param);
    }

    public static IDataset qryAllRelAAByUserA(String userId, String routeId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_ACCTID_BYUSERID", inparam, routeId);
    }

    // 查询集团账户信息
    public static IDataset qryGrpAcctInfo(IData inParams) throws Exception
    {

        IDataset infos = Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SN_DEFAULT_NORMAL", inParams);
        return infos;
    }

    public static IDataset qryIntegralAcctInfoByUserId(String userId, String status) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("STATUS", status);
        return Dao.qryByCode("TF_F_INTEGRAL_ACCT", "SEL_BY_USER_ID", param);
    }

    public static IDataset queryAcctInfoByContractNo(String CONTRACT_NO) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("CONTRACT_NO", CONTRACT_NO);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_CONTRACTNO", inparams);
    }

    public static IDataset queryAcctInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_ALL_BY_SELNUM", param);
    }

    public static IDataset queryGroupAcctInfo(IData indata) throws Exception
    {
        IDataset result = null;
        SQLParser parser = new SQLParser(indata);
        parser.addSQL("SELECT a.acct_id,a.pay_name,to_char(a.open_date,'yyyy-mm-dd hh24:mi:ss') OPEN_DATE ");
        parser.addSQL("FROM tf_f_cust_group g, tf_f_account a ");
        parser.addSQL("WHERE g.cust_id=a.cust_id ");
        parser.addSQL("AND g.remove_tag='0' ");
        parser.addSQL("AND g.group_id=:GROUP_ID ");
        result = Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
        return result;
    }

    public static IDataset queryPayNameByBankAcctNo(String bankCode, String bankAcctNo) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("BANK_CODE", bankCode);
        inparam.put("BANK_ACCT_NO", bankCode);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_PAY_NAME_BY_BANK_ACCT_NO", inparam);
    }

    public static IDataset queryUsersByBank(IData data, Pagination page) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_ACCOUNT", "SEL_USER_BY_BANK", data, page, Route.getCrmDefaultDb());
    }
    
    public static IDataset queryUsersByBank2(IData data, Pagination page) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_ACCOUNT", "SEL_USER_BY_BANK2", data, page, Route.getCrmDefaultDb());
    }
    
    public static IDataset qryAcctInfoByAcctNo(String bankAcctNo) throws Exception{
        
        IData inparam = new DataMap();
        inparam.put("BANK_ACCT_NO", bankAcctNo);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_ACCTNO", inparam);
    }
    public static IDataset qryAcctInfoByAcctNoNew(String bankAcctNo) throws Exception{
        
        IData inparam = new DataMap();
        inparam.put("BANK_ACCT_NO", bankAcctNo);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_ACCTNO_NEW", inparam);
    }

    /**
     * 付费关系查询
     * 
     * @param data
     * @param dao
     * @return
     * @throws Exception
     */
    public static IDataset selectByAcctid(IData data, String conn) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("SELECT 1 FROM tf_a_payrelation where 1=1 ");
        parser.addSQL(" and acct_id = :ACCT_ID");
        parser.addSQL(" and TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD')) between start_cycle_id and end_cycle_id");
        return Dao.qryByParse(parser, conn);
    }

    public static int updataWidenetAcctInfoDelete(String acctId, String strFinishDate) throws Exception
    {
        IData delParam = new DataMap();
        delParam.put("FINISH_DATE", strFinishDate);
        delParam.put("ACCT_ID", acctId);
        int count = Dao.executeUpdateByCodeCode("TF_F_ACCOUNT", "UPD_DEL_BY_ACCTID_WHIT_FINISH_DATE", delParam);
        return count;
    }
    
    public static IDataset queryAcctInfoBySn2(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_BY_SERIAL_NUMBER", param ,Route.getCrmDefaultDb());
    }
    
    /**
     * 生成付费帐目编码
     * @param itemStr
     * @return
     * @throws Exception
     */
    public static IData getPayItemCodeForGrpMeb(String itemStr) throws Exception
    {
        IData data = new DataMap();
        data.put("DETAIL_ITEMSET", itemStr);

        IDataOutput output = CSAppCall.callAcct("AM_CRM_QryPayItemCode", data, true);
        IDataset result = output.getData();
        if (IDataUtil.isEmpty(result)){
        	CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_144);
        }
        IData itemcode = result.getData(0);
        return itemcode;
    }
    
    /**
     * 获取集团统一付费产品的默认账户信息
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getAcctInfoByUserIdFroUPGP(String userId) throws Exception
    {
    	IDataset resultSet = new DatasetList();
    	
    	//查询集团账户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
    	IDataset acctInfos = Dao.qryByCode("TF_A_PAYRELATION", "SEL_PAYACCOUNT_FOR_UNIFYGRP", inparam);
    	
    	if (IDataUtil.isEmpty(acctInfos))
        {
            return resultSet;
        }

        for (int i = 0, iSize = acctInfos.size(); i < iSize; i++)
        {
            IData acctInfo = acctInfos.getData(i);
            String acctId = acctInfo.getString("ACCT_ID", "");
            
            IData tempAcct = new DataMap();
            tempAcct.put("ACCT_ID", acctId);
            tempAcct.put("USER_ID", userId);
            tempAcct.put("PAY_NAME", acctInfo.getString("PAY_NAME", ""));
            tempAcct.put("OPEN_DATE", acctInfo.getString("OPEN_DATE", ""));
            tempAcct.put("PAY_MODE_CODE", acctInfo.getString("PAY_MODE_CODE", ""));
            tempAcct.put("EPARCHY_CODE", acctInfo.getString("EPARCHY_CODE", ""));
            tempAcct.put("RSRV_STR8", acctInfo.getString("RSRV_STR8", ""));
            tempAcct.put("RSRV_STR9", acctInfo.getString("RSRV_STR9", ""));
            tempAcct.put("RSRV_STR10", acctInfo.getString("RSRV_STR10", ""));
            
            tempAcct.put("PAY_MODE_NAME", UPayModeInfoQry.getPayModeNameByPayModeCode(acctInfo.getString("PAY_MODE_CODE", "")));
            tempAcct.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(acctInfo.getString("EPARCHY_CODE", "")));
            tempAcct.put("PRINT_NAME", StaticUtil.getStaticValue("PRINT_MODE", acctInfo.getString("RSRV_STR8")));
            tempAcct.put("NODE_NAME", StaticUtil.getStaticValue("NODE_MODE", acctInfo.getString("RSRV_STR9")));
            
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(userInfo))
            {
            	tempAcct.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID"));
            	tempAcct.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID")));
            }
            resultSet.add(tempAcct);
        }

        return resultSet;
    }
    
    
    /**
     * 根据acctId获取集团统一付费产品是否有代付关系
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getUnifyCountByUserId(String acctId) throws Exception
    {
	    IData inparams = new DataMap();
	    inparams.put("ACCT_ID", acctId);
        return Dao.qryByCode("TF_F_ACCOUNT", "SEL_UNIFYCOUNT_BY_ACCTID", inparams);
    }
    
    /**
     * duhj
     * @param acctId
     * @param payModeCode
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountChgInfo(String custId,String payModeCode) throws Exception
    {
	    IData inparams = new DataMap();
	    inparams.put("CUST_ID", custId);
	    inparams.put("PAY_MODE_CODE", payModeCode);

        return Dao.qryByCodeParser("TF_F_ACCOUNT", "SEL_BY_ACCTID_PAY_MODE_CODE", inparams);
    }
    /**
     * 根据账户ID查询是否已有付费关系
     * @param acctId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-6
     */
    public static IDataset getPayrelaAcctByAcctId(String acctId) throws Exception
    {
    	IData params = new DataMap();
        params.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.PARTITION_ID,");
        sql.append("       A.USER_ID,");
        sql.append("       A.ACCT_ID,");
        sql.append("       A.PAYITEM_CODE,");
        sql.append("       A.ACCT_PRIORITY,");
        sql.append("       A.USER_PRIORITY,");
        sql.append("       A.ADDUP_MONTHS,");
        sql.append("       A.ADDUP_METHOD,");
        sql.append("       A.BIND_TYPE,");
        sql.append("       A.DEFAULT_TAG,");
        sql.append("       A.ACT_TAG,");
        sql.append("       A.LIMIT_TYPE,");
        sql.append("       A.LIMIT,");
        sql.append("       A.COMPLEMENT_TAG,");
        sql.append("       A.INST_ID,");
        sql.append("       A.START_CYCLE_ID,");
        sql.append("       A.END_CYCLE_ID,");
        sql.append("       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        sql.append("       A.UPDATE_STAFF_ID,");
        sql.append("       A.UPDATE_DEPART_ID,");
        sql.append("       A.REMARK,");
        sql.append("       A.RSRV_STR1,");
        sql.append("       A.RSRV_STR2,");
        sql.append("       A.RSRV_STR3,");
        sql.append("       A.RSRV_STR4,");
        sql.append("       A.RSRV_STR5,");
        sql.append("       A.RSRV_STR6,");
        sql.append("       A.RSRV_STR7,");
        sql.append("       A.RSRV_STR8,");
        sql.append("       A.RSRV_STR9,");
        sql.append("       A.RSRV_STR10");
        sql.append("  FROM TF_A_PAYRELATION A");
        sql.append(" WHERE A.ACCT_ID = :ACCT_ID");
        sql.append("   AND TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) BETWEEN START_CYCLE_ID AND END_CYCLE_ID");

        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }
    /**
     * 查询账户是否给集团海洋通产品用户付费
     * @param acctId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-7
     */
    public static IDataset getGhytPrdPayrelaAcctByAcctId(String acctId) throws Exception
    {
    	IData params = new DataMap();
        params.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID,");
        sql.append("       A.ACCT_ID,");
        sql.append("       B.PRODUCT_ID,");
        sql.append("       A.START_CYCLE_ID,");
        sql.append("       A.END_CYCLE_ID,");
        sql.append("       A.DEFAULT_TAG,");
        sql.append("       A.ACT_TAG,");
        sql.append("       B.START_DATE,");
        sql.append("       B.END_DATE");
        sql.append("  FROM TF_A_PAYRELATION A, TF_F_USER_PRODUCT B");
        sql.append(" WHERE A.PARTITION_ID = B.PARTITION_ID");
        sql.append("   AND A.USER_ID = B.USER_ID");
        sql.append("   AND A.ACCT_ID = :ACCT_ID");
        sql.append("   AND B.PRODUCT_ID = 84011638");
        sql.append("   AND A.DEFAULT_TAG = '1'");
        sql.append("   AND A.ACT_TAG = '1'");
        sql.append("   AND TO_NUMBER(TO_CHAR(SYSDATE, 'yyyymmdd')) BETWEEN A.START_CYCLE_ID AND A.END_CYCLE_ID");
        sql.append("   AND B.END_DATE > SYSDATE");
        sql.append("   AND B.MAIN_TAG = '1'");

        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }
    /**
     * 查询集团产品用户默认的付费账户的集团高级付费关系成员
     * @param userId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-8
     */
    public static IDataset getGrpPrdAdvPayMebByUserId(String userId) throws Exception
    {
    	IData params = new DataMap();
        params.put("USER_ID", userId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.USER_ID,");
        sql.append("       A.USER_ID_A,");
        sql.append("       A.ACCT_ID,");
        sql.append("       A.PAYITEM_CODE,");
        sql.append("       A.START_CYCLE_ID,");
        sql.append("       A.END_CYCLE_ID");
        sql.append("  FROM TF_F_USER_SPECIALEPAY A, TF_A_PAYRELATION B");
        sql.append(" WHERE A.USER_ID_A = B.USER_ID");
        sql.append("   AND A.ACCT_ID = B.ACCT_ID");
        sql.append("   AND B.USER_ID = :USER_ID");
        sql.append("   AND B.DEFAULT_TAG = '1'");
        sql.append("   AND B.ACT_TAG = '1'");
        sql.append("   AND TO_CHAR(SYSDATE, 'yyyymmdd') BETWEEN A.START_CYCLE_ID AND A.END_CYCLE_ID");
        sql.append("   AND TO_CHAR(SYSDATE, 'yyyymmdd') BETWEEN B.START_CYCLE_ID AND B.END_CYCLE_ID");

        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }
    
    /**
     * 
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryAcctInfoByCustId(String custId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT PARTITION_ID,");
        sql.append("       TO_CHAR(ACCT_ID) ACCT_ID,");
        sql.append("       TO_CHAR(CUST_ID) CUST_ID,");
        sql.append("       PAY_NAME,");
        sql.append("       PAY_MODE_CODE,");
        sql.append("       ACCT_DIFF_CODE,");
        sql.append("       ACCT_PASSWD,");
        sql.append("       ACCT_TAG,");
        sql.append("       NET_TYPE_CODE,");
        sql.append("       EPARCHY_CODE,");
        sql.append("       CITY_CODE,");
        sql.append("       BANK_CODE,");
        sql.append("       BANK_ACCT_NO,");
        sql.append("       SCORE_VALUE,");
        sql.append("       CREDIT_CLASS_ID,");
        sql.append("       BASIC_CREDIT_VALUE,");
        sql.append("       CREDIT_VALUE,");
        sql.append("       DEBUTY_USER_ID,");
        sql.append("       DEBUTY_CODE,");
        sql.append("       CONTRACT_NO,");
        sql.append("       DEPOSIT_PRIOR_RULE_ID,");
        sql.append("       ITEM_PRIOR_RULE_ID, ");
        sql.append("       TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE,");
        sql.append("       REMOVE_TAG,");
        sql.append("       TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE,");
        sql.append("       UPDATE_STAFF_ID,");
        sql.append("       UPDATE_DEPART_ID,");
        sql.append("       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,");
        sql.append("       REMARK,");
        sql.append("       RSRV_STR1,");
        sql.append("       RSRV_STR2,");
        sql.append("       RSRV_STR3,");
        sql.append("       RSRV_STR4,");
        sql.append("       RSRV_STR5,");
        sql.append("       RSRV_STR6,");
        sql.append("       RSRV_STR7,");
        sql.append("       RSRV_STR8,");
        sql.append("       RSRV_STR9,");
        sql.append("       RSRV_STR10");
        sql.append("  FROM TF_F_ACCOUNT");
        sql.append(" WHERE CUST_ID = TO_NUMBER(:CUST_ID)");
        sql.append("   AND REMOVE_TAG = '0'");
        return Dao.qryBySql(sql, data);
    }
    
	/**
     * 调用账务接口查询返回
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset checkFee(IData input) throws Exception
    {
    	DatasetList results = new DatasetList();
    	
    	IDataOutput output = CSAppCall.callAcct("AM_CRM_GetGroupAccountOwefee", input, true);//AM_CRM_GetAccountOwefee

        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
        {
            IDataset dataset = output.getData();

            if (IDataUtil.isNotEmpty(dataset))
            {
                return dataset;
            }
        }
        return results;
    }
    
    /**
     * 查询是否是集团客户预缴款(虚拟)产品的账户
     * @param acctId
     * @return
     * @throws Exception
     */
    public static IDataset getPrepayAcctInfoByAcctId(String acctId) throws Exception
    {
    	IData params = new DataMap();
        params.put("ACCT_ID", acctId);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT EPARCHY_CODE,");
        sql.append("       CITY_CODE,");
        sql.append("       PARTITION_ID,");
        sql.append("       TO_CHAR(ACCT_ID) ACCT_ID,");
        sql.append("       TO_CHAR(CUST_ID) CUST_ID,");
        sql.append("       PAY_NAME,");
        sql.append("       PAY_MODE_CODE,");
        sql.append("       BANK_CODE,");
        sql.append("       BANK_ACCT_NO,");
        sql.append("       TO_CHAR(SCORE_VALUE) SCORE_VALUE,");
        sql.append("       TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE,");
        sql.append("       TO_CHAR(CREDIT_VALUE) CREDIT_VALUE,");
        sql.append("       TO_CHAR(DEBUTY_USER_ID) DEBUTY_USER_ID,");
        sql.append("       DEBUTY_CODE,");
        sql.append("       CONTRACT_NO,");
        sql.append("       DEPOSIT_PRIOR_RULE_ID,");
        sql.append("       ITEM_PRIOR_RULE_ID,");
        sql.append("       REMOVE_TAG,");
        sql.append("       TO_CHAR(OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') OPEN_DATE,");
        sql.append("       TO_CHAR(REMOVE_DATE, 'YYYY-MM-DD HH24:MI:SS') REMOVE_DATE,");
        sql.append("       RSRV_STR5");
        sql.append("  FROM TF_F_ACCOUNT");
        sql.append(" WHERE PARTITION_ID = MOD(TO_NUMBER(:ACCT_ID), 10000)");
        sql.append("       AND ACCT_ID = :ACCT_ID");
        sql.append("       AND REMOVE_TAG = '0'");
        sql.append("       AND RSRV_STR5 = 'PrepayProduct'");
        return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
    }
    

    
    
    /**
     * 根据商品订购实例获取账户ID
     * @param serialNumber
     * @return
     * @throws Exception
     * 2020-5-11 11:57:28
     * xuzh5
     */
    public static IDataset qryAcctInfoByProductOrderID(String PRODUCT_ORDER_ID) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ORDER_ID", PRODUCT_ORDER_ID);

        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_PRODUCT_ORDER_ID", param, Route.CONN_CRM_CG);
    }
    
    
    /**
     * 根据集团订购实例id 查询三户资料
     * @return
     * @throws Exception
     * 2020-5-11 11:57:28
     * xuzh5
     */
    public static IDataset qryAcctInfoByProductOrderID(String PRODUCT_ORDER_ID,String PRODUCT_ID) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ORDER_ID", PRODUCT_ORDER_ID);
        param.put("PRODUCT_SPEC_CODE", PRODUCT_ID);
  
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_SH_BY_PRODUCT_ORDER_ID", param, Route.CONN_CRM_CG);
    }

}
