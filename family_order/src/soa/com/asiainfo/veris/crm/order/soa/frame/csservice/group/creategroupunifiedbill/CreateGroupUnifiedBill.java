
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupunifiedbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class CreateGroupUnifiedBill extends MemberBean
{
    protected CreateGroupUnifiedBillReqData reqData = null;

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理UU信息
        infoRegDataRelation();

        // 处理Other信息
        infoRegDataOther();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateGroupUnifiedBillReqData();
    }

    /**
     * 处理Other信息
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IData otherData = new DataMap();
        otherData.put("USER_ID", reqData.getUca().getUserId());
        otherData.put("RSRV_VALUE_CODE", "MASO");
        otherData.put("RSRV_VALUE", "集团融合计费");
        otherData.put("INST_ID", SeqMgr.getInstId());
        otherData.put("RSRV_STR1", reqData.getUca().getSerialNumber());
        otherData.put("RSRV_STR2", reqData.getGrpUca().getUserId());
        otherData.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        otherData.put("RSRV_STR4", reqData.getUca().getCustId());
        // 分散账期修改
        otherData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        // end
        otherData.put("END_DATE", SysDateMgr.getTheLastTime());
        otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeOther(otherData);
    }

    /**
     * 处理UU信息 ADCMAS产品之前插UU表的，J2EE项目之后则改成插BB表
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IData relaData = new DataMap();

        relaData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        relaData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
        relaData.put("USER_ID_B", reqData.getUca().getUserId());
        relaData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        relaData.put("RELATION_TYPE_CODE", "UB");
        relaData.put("ROLE_TYPE_CODE", "");
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("SHORT_CODE", "");
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("REMARK", reqData.getUca().getUserId());

        // 分散账期修改
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        // end
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        if (relationTypeCode.equals("89") || relationTypeCode.equals("86"))
        {
            this.addTradeRelationBb(relaData);
        }
        else
        {
            this.addTradeRelation(relaData);
        }
    }

    @Override
    protected void initProductCtrlInfo() throws Exception
    {
        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.CreateUnifiedBill);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateGroupUnifiedBillReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setMemRoleB(map.getString("MEM_ROLE_B", "1"));
        reqData.setImmeffect(map.getBoolean("IMMEFFECT", true));
        reqData.setImmediate(map.getString("IMMEDIATE", "false"));
        reqData.setImsPassword(map.getString("IMS_PASSWORD", ""));

        if (StringUtils.isNotEmpty(map.getString("PRODUCT_PRE_DATE")))
        {
            reqData.setImmeffect(false);
            diversifyBooking = true;

            // 下账期开始时间
            String firstTimeNextAcct = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
            reqData.setFirstTimeNextAcct(firstTimeNextAcct);
        }

        makReqDataElement();
    }

    public void makReqDataElement() throws Exception
    {
        // 查询成员是否已经有减免优惠
        String userId = reqData.getUca().getUserId();
        String discntCode = "50009032";
        String eparchyCode = reqData.getUca().getUserEparchyCode();
        IDataset discntSet = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntCode, eparchyCode);

        if (IDataUtil.isEmpty(discntSet))
        {
            IData discntData = new DataMap();
            discntData.put("USER_ID", reqData.getUca().getUserId());
            discntData.put("USER_ID_A", "-1");
            discntData.put("PRODUCT_ID", "-1");
            discntData.put("PACKAGE_ID", "-1");
            discntData.put("DISCNT_CODE", "50009032");// 固定值
            discntData.put("SPEC_TAG", "1");
            discntData.put("RELATION_TYPE_CODE", "UB");
            discntData.put("INST_ID", SeqMgr.getInstId());
            discntData.put("CAMPN_ID", "");
            discntData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            discntData.put("END_DATE", SysDateMgr.getTheLastTime());
            discntData.put("REMARK", "办理集团融合计费业务，减免无线商话商信通套餐费用");
            discntData.put("RSRV_STR1", reqData.getGrpUca().getUserId());
            discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            reqData.cd.putDiscnt(IDataUtil.idToIds(discntData));

        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }

    @Override
    protected void setTradefeePaymoney(IData map) throws Exception
    {
        super.setTradefeePaymoney(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

}
