
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 处理融合总机新增和注销总机号码信息
 */
public class ChangeCentrexSuperTeleMemberSimple extends MemberBean
{
    private String mebModifyTag;

    private String roleCodeB;

    private String tradeId;// 集团产品受理TradeId

    public ChangeCentrexSuperTeleMemberSimple()
    {

    }

    protected void dealTradeOfferRel() throws Exception
    {
        // 没有登记产品信息，offer_rel表不登记了
    }
    
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理UU关系信息
        infoRegDataRelation();

        // 处理工单依赖
        infoRegDataTradeLimit();
    }

    /**
     * 处理UU关系信息
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {

        IData relationData = null;

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            relationData = new DataMap();

            relationData.put("USER_ID_A", reqData.getGrpUca().getUserId());
            relationData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
            relationData.put("USER_ID_B", reqData.getUca().getUserId());
            relationData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
            relationData.put("RELATION_TYPE_CODE", "S4"); // 关系类型-固定值
            relationData.put("ROLE_TYPE_CODE", "");
            relationData.put("ROLE_CODE_A", "0");
            relationData.put("ROLE_CODE_B", roleCodeB);
            relationData.put("SHORT_CODE", "");
            relationData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            relationData.put("INST_ID", SeqMgr.getInstId());

            relationData.put("START_DATE", getAcceptTime());
            relationData.put("END_DATE", SysDateMgr.getTheLastTime());
            relationData.put("REMARK", reqData.getGrpUca().getUserId());

        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            IDataset uuInfos = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), "S4", null, null);
            if (IDataUtil.isNotEmpty(uuInfos))
            {
                relationData = uuInfos.getData(0);
                relationData.put("END_DATE", getAcceptTime());
                relationData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }

        super.addTradeRelation(relationData);
    }

    /**
     * 处理工单依赖
     * 
     * @throws Exception
     */
    public void infoRegDataTradeLimit() throws Exception
    {
        IData tradeLimitData = new DataMap();

        // 新增, 先处理集团工单, 再处理成员工单
        if (TRADE_MODIFY_TAG.Add.getValue().equals(mebModifyTag))
        {
            tradeLimitData.put("TRADE_ID", getTradeId());
            tradeLimitData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(getTradeId()));
            tradeLimitData.put("LIMIT_TRADE_ID", tradeId);
            tradeLimitData.put("LIMIT_TYPE", "0");
            tradeLimitData.put("ROUTE_ID", BizRoute.getRouteId());
        }
        // 注销, 先处理成员工单, 再处理集团工单
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(mebModifyTag))
        {
            tradeLimitData.put("TRADE_ID", tradeId);
            tradeLimitData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
            tradeLimitData.put("LIMIT_TRADE_ID", getTradeId());
            tradeLimitData.put("LIMIT_TYPE", "0");
            tradeLimitData.put("ROUTE_ID", Route.CONN_CRM_CG);
        }

        Dao.insert("TF_B_TRADE_LIMIT", tradeLimitData, Route.getJourDb(Route.CONN_CRM_CG));
    }

    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        mebModifyTag = map.getString("MEB_MODIFY_TAG", "");
        roleCodeB = map.getString("MEM_ROLE_B");
        tradeId = map.getString("TRADE_ID");
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            IData svcData = new DataMap();
            svcData.put("USER_ID", reqData.getUca().getUserId());
            svcData.put("USER_ID_A", reqData.getGrpUca().getUserId());
            svcData.put("PRODUCT_ID", "-1");
            svcData.put("PACKAGE_ID", "-1");
            svcData.put("SERVICE_ID", "611"); // 服务标识
            svcData.put("MAIN_TAG", "0"); // 主体服务标志：0-否，1-是
            svcData.put("CAMPN_ID", "");
            svcData.put("INST_ID", SeqMgr.getInstId());
            svcData.put("START_DATE", getAcceptTime());
            svcData.put("END_DATE", SysDateMgr.getTheLastTime());
            svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svcData.put("RSRV_STR8", reqData.getGrpUca().getUserId());
            svcData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志
            reqData.cd.putSvc(IDataUtil.idToIds(svcData));

        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null);
            if (IDataUtil.isNotEmpty(userSvcList))
            {
                IData svcData = userSvcList.getData(0);
                svcData.put("END_DATE", getAcceptTime());
                svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                svcData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志
                svcData.put("PRODUCT_ID", "-1");
                svcData.put("PACKAGE_ID", "-1");
                reqData.cd.putSvc(IDataUtil.idToIds(svcData));
            }
        }
    }

    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        makUcaForMebNormal(map);
    }

    /**
     * 覆盖父类方法,保存预留字段信息
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        tradeData.put("RSRV_STR2", "25");
        tradeData.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        tradeData.put("RSRV_STR4", reqData.getUca().getBrandCode());
        tradeData.put("RSRV_STR5", reqData.getUca().getCustomer().getCustName());
        tradeData.put("RSRV_STR6", reqData.getUca().getCustomer().getCustName());
        tradeData.put("RSRV_STR7", "1");
        tradeData.put("RSRV_STR8", "");
        tradeData.put("RSRV_STR9", tradeId);
        tradeData.put("RSRV_STR10", "0");
    }

    protected String setTradeTypeCode() throws Exception
    {
        return mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()) ? "4624" : "4627";
    }
}
