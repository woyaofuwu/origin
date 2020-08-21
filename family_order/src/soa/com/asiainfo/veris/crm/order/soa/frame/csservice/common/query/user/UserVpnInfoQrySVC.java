
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class UserVpnInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 作用：根据user_id查询对应的成员实例化资料信息 对于vpmn业务直接查表TF_F_USER_VPN_MEB 得到用户个性化数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getMemberVpnByUserId(IData input) throws Exception
    {
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE);
        String user_id = input.getString("USER_ID");
        String user_id_a = input.getString("USER_ID_A");
        IData data = UserVpnInfoQry.getMemberVpnByUserId(user_id, user_id_a, eparchyCode);

        return data;
    }

    /**
     * 根据CustId查询用户vpn信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserVPNInfoByCstId(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        IDataset dataset = UserVpnInfoQry.getUserVPNInfoByCstId(cust_id, getPagination());

        IDataset data = DataHelper.distinct(dataset, "VPN_NO", "");

        return data;
    }

    public IDataset qryCriterionVpnInfoByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        return UserVpnInfoQry.qryCriterionVpnInfoByUserId(userId);
    }

    /**
     * 根据USER_ID查询子母VPMN信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryParentUserVpnByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        return UserVpnInfoQry.qryParentUserVpnByUserId(userId);
    }

    /**
     * 查询集团用户VPN信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserVpnByUserId(IData input) throws Exception
    {
        String userId = IDataUtil.getMandaData(input, "USER_ID");

        return UserVpnInfoQry.qryUserVpnByUserId(userId);
    }

    /**
     * 根据vpn编码查询有效的vpn信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryVpnInfoByVpnNo(IData inparam) throws Exception
    {
        String vpnNo = inparam.getString("VPN_NO");
        return UserVpnInfoQry.queryVpnInfoByVpnNo(vpnNo);
    }
    
    /**
     * 根据CustId查询用户多媒体桌面电话的vpn信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserDesktopVPNInfoByCstId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        IDataset dataset = UserVpnInfoQry.getUserDesktopVPNInfoByCstId(custId, getPagination());
        IDataset data = DataHelper.distinct(dataset, "VPN_NO", "");
        return data;
    }
}
