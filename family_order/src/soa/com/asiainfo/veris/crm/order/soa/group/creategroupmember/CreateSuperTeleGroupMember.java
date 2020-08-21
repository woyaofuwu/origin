
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateSuperTeleGroupMember extends CreateGroupMember
{
    private IData productParam = new DataMap();

    public CreateSuperTeleGroupMember()
    {

    }

    /**
     * 生成其它台帐数据
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 生成VpnMeb信息
        infoRegDataSuperTeleVpnMeb();

        // 移动总机平台发数指数据
        // infoRegDataSuperTeleOther();
    }

    /**
     * 成员数据指令处理
     * 
     * @throws Exception
     */
    public void infoRegDataSuperTeleOther() throws Exception
    {
        IData otherData = new DataMap();
        otherData.put("USER_ID", reqData.getUca().getUserId());// 成员用户ID
        otherData.put("RSRV_VALUE_CODE", "JTZJ");// 平台为总机平台
        otherData.put("RSRV_VALUE", reqData.getUca().getUserId());// 成员用户ID

        if (productParam.getString("MEB_CUST_NAME").equals(""))
        {
            otherData.put("RSRV_STR1", productParam.getString("MEB_CUST_NAME"));
        }
        else
        {
            otherData.put("RSRV_STR1", "");
        }

        otherData.put("RSRV_STR9", "612");// 服务为移动分机服务
        otherData.put("RSRV_STR11", "0");// 为成员开户操作
        otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 为成员开户操作
        otherData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 开始时间
        otherData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间

        super.addTradeOther(otherData);
    }

    /**
     * 登记VPN成员信息
     * 
     * @throws Exception
     */
    public void infoRegDataSuperTeleVpnMeb() throws Exception
    {
        IData vpnMebData = super.infoRegDataVpnMeb();

        vpnMebData.put("SHORT_CODE", productParam.getString("SHORT_CODE", ""));// 成员短号码;
        vpnMebData.put("PERFEE_PLAY_BACK", productParam.getString("PERFEE_PLAY_BACK", ""));// 个人付费放音标志
        vpnMebData.put("SINWORD_TYPE_CODE", productParam.getString("M_SINWORD_TYPE_CODE", ""));// 语种
        vpnMebData.put("CALL_DISP_MODE", productParam.getString("CALL_DISP_MODE", ""));// 主叫号码显示方式
        vpnMebData.put("CALL_AREA_TYPE", productParam.getString("M_CALL_AREA_TYPE", ""));// 呼叫区域类型

        productParam.put("CALL_NET_TYPE1", productParam.getString("M_CALL_NET_TYPE1", ""));// CALL_NET_TYPE1内网
        productParam.put("CALL_NET_TYPE2", productParam.getString("M_CALL_NET_TYPE2", ""));// CALL_NET_TYPE2网间
        productParam.put("CALL_NET_TYPE3", productParam.getString("M_CALL_NET_TYPE3", ""));// CALL_NET_TYPE3网外
        productParam.put("CALL_NET_TYPE4", productParam.getString("M_CALL_NET_TYPE4", ""));// CALL_NET_TYPE4网外号码组

        vpnMebData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam)); // 呼叫网络类型
        vpnMebData.put("ADMIN_FLAG", VpnUnit.comFlagField(productParam.getString("ADMIN_FLAG", "")));// 个人标志-管理员

        // 如果话务员标志有效，取消服务，插台帐服务子表
        vpnMebData.put("TELPHONIST_TAG", VpnUnit.comFlagField(productParam.getString("TELPHONIST_TAG", "")));// 个人标志-话务员
        vpnMebData.put("LOCK_TAG", VpnUnit.comFlagField(productParam.getString("LOCK_TAG", "")));// 个人标志-锁定

        vpnMebData.put("LIMFEE_TYPE_CODE", VpnUnit.comFeeTypeField(productParam)); // 费用限额标志
        vpnMebData.put("MON_FEE_LIMIT", productParam.getString("MON_FEE_LIMIT", ""));// 月费用限额
        vpnMebData.put("FUNC_TLAGS", "1100000000000000000000000001000000000000");
        vpnMebData.put("PKG_TYPE", productParam.getString("PKG_TYPE", ""));// 资费套餐类型--页面上有、数据库没配

        super.addTradeVpnMeb(vpnMebData);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        productParam = reqData.cd.getProductParamMap(baseMemProduct);

        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_208);
        }
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    @Override
    public void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();
        tradeData.put("RSRV_STR1", reqData.getGrpUca().getUserId());// 集团用户ID
        tradeData.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));// 成员关系
        tradeData.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团服务号码
        tradeData.put("RSRV_STR4", reqData.getUca().getBrandCode()); // 成员品牌
        tradeData.put("RSRV_STR6", reqData.getUca().getCustomer().getCustName()); // 成员名称

        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        IData mofficeInfo = RouteInfoQry.getMofficeInfoBySn(serialNumber);

        if (IDataUtil.isNotEmpty(mofficeInfo))
        {
            tradeData.put("RSRV_STR7", "1");
        }
        else
        {
            tradeData.put("RSRV_STR7", "0");
        }

        tradeData.put("RSRV_STR8", productParam.getString("SHORT_CODE")); // 成员短号码
        tradeData.put("RSRV_STR10", "0"); // 成员付费方式[服务开通特殊参数]
    }

    /**
     * 覆盖父类方法, 设置短号
     */
    @Override
    public void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);

        map.put("SHORT_CODE", productParam.getString("SHORT_CODE"));
    }

}
