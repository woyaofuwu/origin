
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;

public class AcctInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset callAccountSvc(IData input) throws Exception
    {
        IDataset iDataset = new DatasetList();

        String[] itemcodes = (String[]) input.get("ITEMCODES");

        String payitem_code = AcctInfoQry.getPayItemCode(itemcodes);

        IData iData = new DataMap();

        iData.put("PAYITEM_CODE", payitem_code);

        iDataset.add(iData);

        return iDataset;
    }

    /**
     * 根据合同号查询帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAcctInfoByContractNoForGrp(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getAcctInfoByContractNoForGrp(input, null);

        return data;
    }

    public IDataset getAcctInfoByCustID(IData input) throws Exception
    {

        String custId = input.getString("CUST_ID");

        IDataset dataset = AcctInfoQry.getAcctInfoByCustId(custId);
        return dataset;
    }

    public IDataset getAcctInfoByCustIDForGroupAccPay(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getAcctInfoByCustIDForGroupAccPay(input);
        return data;
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAcctInfoByCustIDForGrp(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");

        IDataset data = AcctInfoQry.getAcctInfoByCustIdForGrp(custId);

        return data;
    }

    public IDataset getAcctInfoByUserID(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        return IDataUtil.idToIds(UcaInfoQry.qryAcctInfoByUserId(userId));
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息
     * 
     * @author fengsl
     * @date 2013-03-18 *
     * @param input
     * @return
     * @throws Exception
     */

    public IDataset getAcctUserInfoByCustIDForGrp(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID", "");
        return AcctInfoQry.getAcctUserInfoByCustId(custId);
    }

    /**
     * 根据CUST_ID查TF_F_ACCOUNT，得到账户信息 不分页
     * 
     * @author fengsl
     * @date 2013-04-22
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAcctUserInfoByCustIDForGrpNoPage(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID", "");
        return AcctInfoQry.getAcctUserInfoByCustId(custId);
    }

    public IDataset getConsignInfoByAcctId(IData input) throws Exception
    {
        return AcctInfoQry.getConsignInfoByAcctId(input);
    }

    public IDataset getRelAAByActIdAReltypecode(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelAAByActIdAReltypecode(input);
        return data;
    }

    public IDataset getRelationAAByActIdATag(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdATag(input);
        return data;
    }

    public IDataset getRelationAAByActIdATagAllDb(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdATagAllDb(input);
        return data;
    }

    public IDataset getRelationAAByActIdATagForCg(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdATagForCg(input);
        return data;
    }

    public IDataset getRelationAAByActIdB(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdB(input);
        return data;
    }

    public IDataset getRelationAAByActIdBAllDb(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdBAllDb(input);
        return data;
    }

    public IDataset getRelationAAByActIdBTagForCg(IData input) throws Exception
    {
        IDataset data = AcctInfoQry.getRelationAAByActIdBTagForCg(input);
        return data;
    }

    public IDataset qryAcctInfoDay(IData input) throws Exception
    {
        return DiversifyAcctUtil.queryAcctInfoDay();
    }
    
    /**
     * 获取集团统一付费产品的默认账户信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAcctInfoByUserIdFroUPGP(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");
        return AcctInfoQry.getAcctInfoByUserIdFroUPGP(userId);
    }
    /**
     * 查询集团产品用户默认的付费账户的集团高级付费关系成员
     * @param input
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-6-8
     */
    public IDataset getGrpPrdAdvPayMebByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");
        return AcctInfoQry.getGrpPrdAdvPayMebByUserId(userId);
    }
    
    /**
     * 调用账务接口查询返回
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkFee(IData input) throws Exception
    {
        return AcctInfoQry.checkFee(input);
    }
}
