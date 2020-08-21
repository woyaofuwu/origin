
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateJWTVpnGroupMember extends CreateGroupMember
{

    private static final Logger logger = Logger.getLogger(CreateJWTVpnGroupMember.class);

    public CreateJWTVpnGroupMember()
    {

    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // VPN_MEB表
        infoRegDataVPMNVpnMeb();

        // 海南处理
        infoRegSpecInfo();
    }

    public void infoRegSpecInfo() throws Exception
    {
        String sn = reqData.getUca().getSerialNumber();
        if (!"V0HN001010".equals(sn) && !"V0SJ004001".equals(sn))
            return;

        IData pyrl = new DataMap();
        // 获取集团默认帐户

        IData payrelations = UcaInfoQry.qryPayRelaByUserId(reqData.getGrpUca().getUserId());

        if (IDataUtil.isEmpty(payrelations))
            CSAppException.apperr(GrpException.CRM_GRP_118);

        pyrl.put("ACCT_ID", payrelations.getString("ACCT_ID"));

        pyrl.put("PAYITEM_CODE", "1664");
        pyrl.put("LIMIT_TYPE", "0");
        pyrl.put("LIMIT", "10000000");
        pyrl.put("COMPLEMENT_TAG", "0");
        pyrl.put("USER_PRIORITY", "0");
        pyrl.put("BIND_TYPE", "1");
        pyrl.put("START_ACYC_ID", "0");
        pyrl.put("END_ACYC_ID", "0");
        pyrl.put("ACT_TAG", "0");
        pyrl.put("DEFAULT_TAG", "0");
        pyrl.put("START_CYCLE_ID", diversifyBooking ? SysDateMgr.getNextCycle() : SysDateMgr.getNowCycle());
        pyrl.put("END_CYCLE_ID", SysDateMgr.getEndCycle205012());
        pyrl.put("STATE", "ADD");
        pyrl.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        pyrl.put("INST_ID", SeqMgr.getInstId());

        // ACCT_PRIORITY
        IDataset datas = TagInfoQry.getTagInfo(CSBizBean.getUserEparchyCode(), "CS_CHR_SPECIAL_PAYITEM_" + CSBizBean.getTradeEparchyCode(), "0", null);
        String sAcctSPayItem = "";
        String iAcctPriority = "";
        if (IDataUtil.isNotEmpty(datas))
        {
            sAcctSPayItem = datas.getData(0).getString("TAG_NUMBER", "");
            iAcctPriority = datas.getData(0).getString("TAG_CHAR", "0");
        }

        if ("1664".equals(sAcctSPayItem))
        {
            pyrl.put("ACCT_PRIORITY", iAcctPriority);
        }
        else
        {
            pyrl.put("ACCT_PRIORITY", "0");
        }

        this.addTradePayrelation(IDataUtil.idToIds(pyrl));

        if ("V0HN001010".equals(sn))
        {
            IData othe = new DataMap();
            othe.put("USER_ID", reqData.getUca().getUserId());
            othe.put("RSRV_VALUE_CODE", "30");
            othe.put("RSRV_VALUE", "50");
            othe.put("STATE", "ADD");
            othe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            // 分散账期修改
            othe.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            othe.put("END_DATE", SysDateMgr.getTheLastTime());
            othe.put("INST_ID", SeqMgr.getInstId());

            this.addTradeOther(IDataUtil.idToIds(othe));
        }

    }

    public IData getParamData() throws Exception
    {
        String baseMebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMebProductId);
        if (IDataUtil.isEmpty(paramData))
            CSAppException.apperr(ParamException.CRM_PARAM_345);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行CreateJWTVpnMember类 getParamData() 得到产品页面传过来的参数<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    public void infoRegDataVPMNVpnMeb() throws Exception
    {
        // VPN数据
        IData vpnData = new DataMap();

        IData productParam = getParamData();

        vpnData.put("USER_ID", reqData.getUca().getUserId());
        vpnData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        vpnData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        vpnData.put("MEMBER_KIND", "1");
        vpnData.put("SHORT_CODE", productParam.getString("SHORT_CODE", ""));
        String funcTlags = TagInfoQry.getSysTagInfo("CS_INF_DEFTAGSET_VPN1", "TAG_INFO", "444444442211111110001000000000000000", CSBizBean.getUserEparchyCode());
        vpnData.put("FUNC_TLAGS", funcTlags);
        vpnData.put("OPEN_DATE", this.getAcceptTime());
        vpnData.put("STATE", "ADD");
        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        vpnData.put("REMOVE_TAG", "0");
        vpnData.put("INST_ID", SeqMgr.getInstId());

        IDataset vpndatas = IDataUtil.idToIds(vpnData);
        this.addTradeVpnMeb(vpndatas);
    }

    /**
     * 重写主台账表,补充预留字段
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        IData param = getParamData();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        data.put("RSRV_STR2", relationTypeCode);
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR4", reqData.getUca().getBrandCode());// 成员品牌
        data.put("RSRV_STR6", reqData.getGrpUca().getCustId());// 集团custId
        data.put("RSRV_STR7", param.getString("SHORT_CODE", ""));
        data.put("RSRV_STR10", param.getString("OUT_PROV_DISCNT", ""));
    }
}
