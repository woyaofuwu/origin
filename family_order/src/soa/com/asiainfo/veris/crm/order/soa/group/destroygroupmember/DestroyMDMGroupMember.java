
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyMDMGroupMember extends DestroyGroupMember
{
    protected DestroyMDMGroupMemberReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyMDMGroupMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyMDMGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setJION_IN(map.getString("JOIN_IN"));
        reqData.setNeedSms(map.getBoolean("IF_SMS", true)); // 是否发短信
    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        if ("1".equals(reqData.getJION_IN()))
        {
            validchkCustManagerId();
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
    }

    /**
     * 判断集团关键人的操作是否属于分管客户经理
     * 
     * @throws Exception
     * @author lixiuyu@20100505
     */
    public void validchkCustManagerId() throws Exception
    {
        String staffId = CSBizBean.getVisit().getStaffId();
        String memUserId = reqData.getUca().getUserId();
        String memEparchCode = reqData.getUca().getUserEparchyCode();
        IDataset mebInfos = GrpMebInfoQry.getGroupInfoByMember(memUserId, memEparchCode, null); // TF_F_CUST_GROUPMEMBER查出集团成员的客户经理
        if (IDataUtil.isNotEmpty(mebInfos))
        {
            IData mebInfo = (IData) mebInfos.get(0);
            String memberKind = mebInfo.getString("MEMBER_KIND", ""); // 成员类型：见参数表TD_S_STATIC/GMB_MEMBERKIND
            String custManagerId = mebInfo.getString("CUST_MANAGER_ID", "");
            if ("1".equals(memberKind) || "2".equals(memberKind)) // 1-联系人,2-集团领导人,0-一般成员
            {
                if (!custManagerId.equals(staffId))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_673);
                }
            }
        }
    }

    protected void prepareSucSmsData(IData smsData) throws Exception
    {
        super.prepareSucSmsData(smsData);

        boolean isCustManager = smsData.getBoolean("IS_CUST_MANAGER");

        if (!isCustManager)
            return;

        String custManagerId = reqData.getGrpUca().getCustGroup().getCustManagerId();
        IData custmanagerinfos = UStaffInfoQry.qryCustManagerInfoByCustManagerId(custManagerId);
        if (IDataUtil.isNotEmpty(custmanagerinfos) && reqData.isNeedSms())
        {
            String serialNumber = custmanagerinfos.getString("SERIAL_NUMBER");
            if (StringUtils.isBlank(serialNumber))
            {
                return; // 客户经理号码为空就不发短信
            }
            IData userInfo = UserInfoQry.getMebUserInfoBySN(serialNumber);
            smsData.put("RECV_OBJECT", serialNumber); // 客户经理号码
            if (IDataUtil.isEmpty(userInfo))
            {
                smsData.put("RECV_ID", "-1");
            }
            else
            {
                smsData.put("RECV_ID", userInfo.getString("USER_ID"));
            }

        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData map = bizData.getTrade();
        map.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团用户
        map.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId()));// 关系类型编码
        map.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// A服务号码
        map.put("RSRV_STR4", SysDateMgr.getNowCyc());// 当前帐期
        map.put("RSRV_STR8", reqData.getUca().getCustomer().getCustName());// 成员客户名称
        map.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团编号

    }

}
