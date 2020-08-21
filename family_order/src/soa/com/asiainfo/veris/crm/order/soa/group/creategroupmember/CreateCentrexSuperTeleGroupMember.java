
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateCentrexSuperTeleGroupMember extends CreateGroupMember
{
    private IData centrexProductParam = new DataMap();

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记VPNMEB信息
        infoRegDataCentrexVpnMeb();

        // 融合总机成员新增报文
        infoRegDataCentrexOther("23");

        if (centrexProductParam.getString("IS_SUPERTELOPER", "").equals("on"))
        {
            // 创建话务员报文
            infoRegDataCentrexOther("21");
        }
    }

    /**
     * 写Other表,用来发报文用
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexOther(String operCode) throws Exception
    {
        IData centrexData = new DataMap();
        centrexData.put("USER_ID", reqData.getUca().getUserId());// 成员用户ID
        centrexData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
        centrexData.put("RSRV_VALUE", "Centrex成员总机业务");
        centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 成员产品ID
        centrexData.put("RSRV_STR9", "6301"); // 服务id
        centrexData.put("RSRV_STR11", operCode); // 操作类型
        centrexData.put("OPER_CODE", operCode); // 操作类型
        centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        centrexData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        centrexData.put("END_DATE", getAcceptTime());
        centrexData.put("INST_ID", SeqMgr.getInstId());

        super.addTradeOther(centrexData);
    }

    /**
     * 处理台帐VpnMeb数据 1.RSRV_STR2存放话务员对应的总机号码 2.RSRV_STR3存放话务员的优先级 3.是普通成员RSRV_STR2和RSRV_STR3为空
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexVpnMeb() throws Exception
    {
        IData vpnMebData = super.infoRegDataVpnMeb();

        vpnMebData.put("SHORT_CODE", centrexProductParam.getString("SHORT_CODE", ""));// 短号码
        vpnMebData.put("RSRV_STR2", centrexProductParam.getString("SUPERTELNUMBER", "")); // 总机号码
        vpnMebData.put("RSRV_STR3", centrexProductParam.getString("OPERATORPRIONTY", "")); // 优先级
        vpnMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeVpnMeb(vpnMebData);
    }

    /**
     * @description 处理主台账表数据
     * @author yish
     * @date 2013-10-14
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        String eparchy_code = reqData.getUca().getUser().getEparchyCode(); // 成员地州
        String netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别
        String cntrxMembPoer = "5";
        // 查impu信息
        String userType = "";
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(user_id_b, eparchy_code);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IData datatmp = impuInfo.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }
        if ("00".equals(netTypeCode))
        {
            userType = "1"; // 1: 传统移动用户
        }
        String tmp = GroupImsUtil.getImpuStr4(user_id_b, userType, 0);
        if ("2".equals(tmp))
        {
            cntrxMembPoer = "1"; // 规范建议选1-企业管理员
        }
        data.put("RSRV_STR2", cntrxMembPoer);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        centrexProductParam = reqData.cd.getProductParamMap(mebProductId);

    }

    /**
     * 覆盖父类方法,设置UU关系信息
     */
    @Override
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);
        if (centrexProductParam.getString("IS_SUPERTELOPER", "").equals("on"))
        {
            map.put("ROLE_CODE_B", "3");
        }
        map.put("SHORT_CODE", centrexProductParam.getString("SHORT_CODE"));
        map.put("RSRV_STR2", centrexProductParam.getString("SUPERTELNUMBER", "")); // 总机号码
        map.put("RSRV_STR3", centrexProductParam.getString("OPERATORPRIONTY", "")); // 优先级
    }

}
