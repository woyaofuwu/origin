
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupunifiedbill;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class DestroyGroupUnifiedBill extends MemberBean
{

    protected boolean immeffect = false; // 是否立即生效

    private static final Logger log = Logger.getLogger(DestroyGroupUnifiedBill.class);

    protected DestroyGroupUnifiedBillReqData reqData = null;

    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

        // 产品元素注销
        makRegElementData();
    }

    /**
     * 后处理数据
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        // 关系表
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        if (relationTypeCode.equals("89") || relationTypeCode.equals("86"))
        {
            infoRegDataRelationBb();
        }
        else
        {
            infoRegDataRelation();
        }

        // 付费计划表、付费关系表
        // infoRegDataPayRelation();
        // 集团融合计费接口台账
        infoRegDataOther();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyGroupUnifiedBillReqData();
    }

    /**
     * 集团融合计费接口台账
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        // 查询集团融合计费接口资料表
        IDataset otherInfos = new DatasetList();
        String userId = reqData.getUca().getUserId();
        IDataset dataset = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, "MASO");

        for (int i = 0; i < dataset.size(); i++)
        {
            dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.getData(i).put("END_DATE", getAcceptTime()); // 立即结束
            dataset.getData(i).put("REMARK", "注销集团融合计费取消关系"); // 立即结束
            otherInfos.add(dataset.getData(i));
        }
        this.addTradeOther(otherInfos);
    }

    /**
     * 付费关系表
     * 
     * @throws Exception
     */
    public void infoRegDataPayRelation() throws Exception
    {

        // 查询付费计划表
        IDataset payInfos = new DatasetList();

        IDataset dataset = PayRelaInfoQry.getPayrelationInfo(reqData.getUca().getUserId());

        for (int i = 0; i < dataset.size(); i++)
        {
            if (dataset.getData(i).getString("RSRV_STR1", "").equals(reqData.getGrpUca().getUser().getUserId()))
            {
                dataset.getData(i).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                dataset.getData(i).put("END_CYCLE_ID", SysDateMgr.getLastCycle());// 成员退出集团，付费关系截止到上个月
                // 2010-05修改
                payInfos.add(dataset.getData(i));
            }

        }
        this.addTradePayrelation(payInfos);
    }

    /**
     * 作用:TF_F_RELATION_UU表中的关系
     * 
     * @author admin
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        String relationTypeCode = "UB";
        IDataset uuInfos = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        uuInfo.put("END_DATE", getAcceptTime()); // 立即结束

        this.addTradeRelation(uuInfo);
    }

    /**
     * ADC/MAS 产品处理BB表
     * 
     * @throws Exception
     */
    public void infoRegDataRelationBb() throws Exception
    {
        String uesrIdA = reqData.getGrpUca().getUser().getUserId();
        String userIdB = reqData.getUca().getUserId();
        String relationTypeCode = "UB";

        IDataset uuInfos = RelaBBInfoQry.qryBB(uesrIdA, userIdB, relationTypeCode, null);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        uuInfo.put("END_DATE", getAcceptTime()); // 立即结束

        this.addTradeRelationBb(uuInfo);
    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {
        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.DestroyUnifiedBill);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyGroupUnifiedBillReqData) getBaseReqData();
    }

    /**
     * 产生登记元素的数据
     * 
     * @throws Exception
     */
    public void makRegElementData() throws Exception
    {
        // 查询成员是否已经有减免优惠
        String userId = reqData.getUca().getUserId();
        String discntCode = "50009032";
        String eparchyCode = reqData.getUca().getUserEparchyCode();
        IDataset discntSet = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, discntCode, eparchyCode);

        if (IDataUtil.isNotEmpty(discntSet))
        {
            IData data = discntSet.getData(0);
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            data.put("ELEMENT_ID", data.getString("DISCNT_CODE")); // 优惠编码转换
            // 失效时间
            data.put("END_DATE", getAcceptTime()); // 立即失效
            data.put("REMARK", "注销集团融合计费业务，截止减免优惠");
            reqData.cd.putDiscnt(discntSet);
        }
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        this.makUcaForMebNormal(map);
    }
}
