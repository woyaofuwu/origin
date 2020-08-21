
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据MP_GROUP_CUST_CODE查询集团客户资料 chenyi 13-12-12
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGroupByMpGroup(IData input) throws Exception
    {
        IDataset data = GrpInfoQry.getGroupByMpGroup(input);

        return data;
    }

    public IDataset getGroupCustInfoForPay(IData inparams) throws Exception
    {
        IDataset data = GrpInfoQry.getGroupCustInfoForPay(inparams, this.getPagination());
        return data;
    }

    /**
     * @description 根据黑白名单成员服务号码查询所属集团信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGroupFromBWBySn(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        IDataset dataset = GrpInfoQry.getGroupFromBWBySn(serial_number, null);

        return dataset;
    }

    /**
     * 根据成员的USER_ID和地州编码查询集团信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getGroupInfo(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");
        String relaCode = input.getString("RELATION_CODE", "");
        String priv = input.getString("PRIV_FOR_PRODUCT", "false");
        IDataset data = GrpInfoQry.getGroupInfo(userId, relaCode, priv, null);

        return data;
    }

    /**
     * 根据成员的USER_ID和RelationTypeCode编码查询集团信息
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getGroupInfoByRela(IData input) throws Exception
    {
        IDataset data = GrpInfoQry.getGroupInfoByRela(input, null);

        return data;
    }

    public IDataset getGrpProductinfoByProductId(IData input) throws Exception
    {
        String productId = input.getString("PRODUCT_ID");
        String groupId = input.getString("GROUP_ID");

        IDataset dataset = GrpInfoQry.getGrpProductinfoByProductId(groupId, productId);

        return dataset;
    }

    public IDataset qryGroupProductInfoByGroupId(IData inParam) throws Exception
    {
        return GrpInfoQry.qryGroupProductInfoByGroupId(inParam, getPagination());
    }

    public IDataset qryGroupProductInfoBySn(IData inParam) throws Exception
    {
        return GrpInfoQry.qryGroupProductInfoBySn(inParam, getPagination());
    }

    public IDataset qryGrpInfoByGrpName(IData input) throws Exception
    {
        String custname = input.getString("CUST_NAME");

        IDataset data = GrpInfoQry.qryGrpInfoByGrpName(custname, getPagination());

        return data;
    }

    public IDataset qryGrpInfoByGrpPspt(IData input) throws Exception
    {
        IDataset data = GrpInfoQry.qryGrpInfoByGrpPspt(input, null);

        return data;
    }

    public IDataset qryTTGrpInfoByGrpName(IData input) throws Exception
    {
        String custname = input.getString("CUST_NAME");

        IDataset data = GrpInfoQry.qryTTGrpInfoByGrpName(custname, true, getPagination());

        return data;
    }

    public IDataset qryTTGrpInfoByGrpPspt(IData input) throws Exception
    {
        IDataset data = GrpInfoQry.qryTTGrpInfoByGrpPspt(input, null);

        return data;
    }

    /**
     * @Description: 根据EC_CODE 查询出TF_F_CUST_GROUP表的集团信息，走cg库
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfoByMpCustCode(IData input) throws Exception
    {
        String mp_group_cust_code = input.getString("MP_GROUP_CUST_CODE");
        IDataset data = GrpInfoQry.queryCustGroupInfoByMpCustCode(mp_group_cust_code, getPagination());
        return data;
    }

    /**
     * 获取号码对应的集团信息
     * 
     * @param param
     *            (SERIAL_NUMBER、PRODUCT_ID)
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfosBySerialNum(IData param) throws Exception
    {

        return GrpInfoQry.queryCustGroupInfosBySerialNum(param);
    }

    public IDataset queryGroupCustInfo1(IData input) throws Exception
    {
        return GrpInfoQry.queryGroupCustInfo1(input, getPagination());
    }

    /**
     * 查询集团业务预约信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGroupInfosById(IData input) throws Exception
    {
        String group_id = input.getString("GROUP_ID");

        IDataset data = GrpInfoQry.queryGroupInfosById(group_id, getPagination());

        return data;
    }

    /**
     * 查询成员使用产品信息 --订购产品情况
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGrpProuctByUserId(IData input) throws Exception
    {
        String userID = input.getString("USER_ID");
        IDataset dataset = GrpInfoQry.queryGrpProuctByUserId(userID, getPagination());

        return dataset;
    }

    public IDataset queryUBRelaBbGroupInfo(IData input) throws Exception
    {
        IDataset dataset = GrpInfoQry.getRelaBbGroupInfo(input, null);
        return dataset;
    }

    public IDataset queryUBRelaGroupInfo(IData input) throws Exception
    {
        IDataset dataset = GrpInfoQry.getRelaGroupInfo(input, null);
        return dataset;
    }
    
    /**
     * @Description: 根据CUST_ID 查询出TF_F_CUST_GROUP表的集团信息
     * @author songxw
     * @date
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfoByCustId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        IDataset data = GrpInfoQry.queryGrpInfoByCustId(cust_id);
        return data;
    }
	
	    public IData qryGrpInfoByGIDNUM(IData input) throws Exception
    {
        IData data = GrpInfoQry.qryGrpInfoByGIDNUM(input, null);

        return data;
    }

    /**
     * @Description: 根据CUST_ID 查询出TF_F_CUST_GROUP表的集团信息
     * @author songxw
     * @date
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfoByGroupId(IData input) throws Exception
    {
        String group_id = input.getString("GROUP_ID");
        IDataset data = GrpInfoQry.queryGroupCustInfoByGroupId(group_id);
        return data;
    }
}
