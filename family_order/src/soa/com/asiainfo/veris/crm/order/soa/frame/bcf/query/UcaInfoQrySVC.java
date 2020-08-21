
package com.asiainfo.veris.crm.order.soa.frame.bcf.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UcaInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 8806595070869760730L;

    /**
     * 根据ACCT_ID查询帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAcctInfoByAcctId(IData input) throws Exception
    {
        String acctId = input.getString("ACCT_ID");

        IData data = UcaInfoQry.qryAcctInfoByAcctId(acctId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据ACCT_ID查询帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAcctInfoByAcctIdForGrp(IData input) throws Exception
    {
        String acct_id = input.getString("ACCT_ID", "");

        IData data = UcaInfoQry.qryAcctInfoByAcctIdForGrp(acct_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据USER_ID查询用户付费帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAcctInfoByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryAcctInfoByUserId(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据USER_ID查询集团用户默认付费帐户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAcctInfoByUserIdForGrp(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");

        IData data = UcaInfoQry.qryAcctInfoByUserIdForGrp(user_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryCustInfoByCustId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");

        IData data = UcaInfoQry.qryCustInfoByCustId(cust_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryCustomerInfoByCustId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");

        IData data = UcaInfoQry.qryCustomerInfoByCustId(cust_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryCustomerInfoByCustIdForGrp(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");

        IData data = UcaInfoQry.qryCustomerInfoByCustIdForGrp(custId);

        return IDataUtil.idToIds(data);
    }

    public IDataset qryDefaultPayRelaByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryDefaultPayRelaByUserId(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryDefaultPayRelaByUserIdForGrp(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据客户编码查询客户资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryGrpInfoByCustId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");

        IData data = UcaInfoQry.qryGrpInfoByCustId(custId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryGrpInfoByGrpId(IData input) throws Exception
    {
        String groupId = input.getString("GROUP_ID");

        IData data = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryLastPayRelaByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryLastPayRelaByUserId(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryLastPayRelaByUserIdForGrp(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryLastPayRelaByUserIdForGrp(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryPayRelaByUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");

        IData data = UcaInfoQry.qryPayRelaByUserId(user_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 获得用户付费帐户通过UserID
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryPayRelaByUserIdForGrp(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");

        IData data = UcaInfoQry.qryPayRelaByUserIdForGrp(user_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryPerInfoByCustId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");

        IData data = UcaInfoQry.qryPerInfoByCustId(custId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryPerInfoByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData data = UcaInfoQry.qryPerInfoByUserId(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据SERIAL_NUMBER查询用户信息,无产品信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfoBySn(IData idata) throws Exception
    {
        String sn = idata.getString("SERIAL_NUMBER");

        IData data = UcaInfoQry.qryUserInfoBySn(sn);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据userId查询用户信息,无产品信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfoByUserId(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");

        IData data = UcaInfoQry.qryUserInfoByUserId(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据userId查询用户信息,无产品信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfoByUserIdForGrp(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");

        IData data = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryUserMainProdInfoBySn(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        IData data = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryUserMainProdInfoBySnForGrp(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");

        IData data = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serial_number);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据userId查询用户信息
     */
    public IDataset qryUserMainProdInfoByUserId(IData inparams) throws Exception
    {
        String user_id = inparams.getString("USER_ID");

        IData data = UcaInfoQry.qryUserMainProdInfoByUserId(user_id);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    /**
     * 根据userId查询用户信息
     * 
     * @author xunyl
     */
    public IDataset qryUserMainProdInfoByUserIdForGrp(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");

        IData data = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }
    
    public IDataset qryGrpInfoBySn(IData iData) throws Exception
    {
    	String sn = IDataUtil.chkParam(iData, "SERIAL_NUMBER");
    	
    	IData data = UcaInfoQry.qryUserMainProdInfoBySnForGrp(sn);
    	
    	if (IDataUtil.isEmpty(data)) 
    	{
			return new DatasetList();
		}
    	
    	return qryGrpInfoByCustId(data);
    }
}
