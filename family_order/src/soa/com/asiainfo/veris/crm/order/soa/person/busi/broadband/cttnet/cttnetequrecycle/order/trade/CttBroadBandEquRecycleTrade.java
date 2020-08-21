
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetequrecycle.order.requestdata.CttBroadBandEquRecycleReqData;

public class CttBroadBandEquRecycleTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CttBroadBandEquRecycleReqData reqData = (CttBroadBandEquRecycleReqData) btd.getRD();
        // 登记主台账
        geneMainTrade(btd);

        // 登记资源台帐
        geneTradeRes(btd);

        if ("2".equals(reqData.getRES_RENT_TYPE())) // 如果是租用，需要终止特定服务、优惠
        {
            geneTradeSpeSvc(btd);
            geneTradeSpeDiscnt(btd);
        }

    }

    /**
     * 校验账号是否存在
     * 
     * @param pd
     * @param td
     * @throws Exception
     */

    private void geneMainTrade(BusiTradeData btd) throws Exception
    {
        CttBroadBandEquRecycleReqData reqData = (CttBroadBandEquRecycleReqData) btd.getRD();

        MainTradeData mainTradeData = btd.getMainTradeData();
        String resCode = reqData.getRES_CODE();
        if (!"0".equals(resCode) && !"-1".equals(resCode))
        {
            // mainTradeData.setProcessTagSet("1");
        }
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag("0");
        mainTradeData.setNetTypeCode(CttConstants.NET_TYPE_CODE);
        mainTradeData.setRsrvStr1(reqData.getRES_KIND_CODE());

        String rsrvStr2 = "";
        if ("0".equals(resCode))
        {

        }
        else
        {
            rsrvStr2 = resCode;
        }
        mainTradeData.setRsrvStr2(resCode);

    }

    /**
     * 业务台帐资源子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeRes(BusiTradeData btd) throws Exception
    {
        CttBroadBandEquRecycleReqData reqData = (CttBroadBandEquRecycleReqData) btd.getRD();
        // 资源类型，W-固话宽带物品
        String resTypeCode = "W";
        IDataset userResList = UserResInfoQry.qryUserResInfoByUserIdResType(reqData.getUca().getUserId(), "-1", resTypeCode, reqData.getRES_CODE());
        if (userResList.isEmpty())
        {
            CSAppException.apperr(ResException.CRM_RES_6);
        }

        IData resData = userResList.getData(0);
        ResTradeData resTradeData = new ResTradeData(resData);

        resTradeData.setEndDate(reqData.getAcceptTime());
        resTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        resTradeData.setRsrvStr4(reqData.getRES_KIND_CODE());
        resTradeData.setRsrvStr1(reqData.getRES_RENT_TYPE());
        resTradeData.setRemark("因设备回收而终止");

        btd.add(reqData.getUca().getSerialNumber(), resTradeData);
    }

    /**
     * 业务台帐优惠子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSpeDiscnt(BusiTradeData btd) throws Exception
    {
        CttBroadBandEquRecycleReqData reqData = (CttBroadBandEquRecycleReqData) btd.getRD();

        String discntCode = "31910000"; // modem租用特殊优惠
        IDataset dataset = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(reqData.getUca().getUserId(), discntCode);

        if (dataset != null && dataset.size() > 0)
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData discntData = dataset.getData(i);
                DiscntTradeData discntTradeData = new DiscntTradeData(discntData);
                discntTradeData.setEndDate(reqData.getAcceptTime());
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                discntTradeData.setRemark("因设备回收而终止");
                btd.add(reqData.getUca().getSerialNumber(), discntTradeData);
            }

        }
    }

    /**
     * 业务台帐服务子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSpeSvc(BusiTradeData btd) throws Exception
    {
        CttBroadBandEquRecycleReqData reqData = (CttBroadBandEquRecycleReqData) btd.getRD();

        String serviceId = "509"; // modem租用特殊服务
        IDataset dataset = UserSvcInfoQry.getSvcUserIdPf(reqData.getUca().getUserId(), serviceId);

        if (dataset != null && dataset.size() > 0)
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData svcData = dataset.getData(i);
                SvcTradeData svcTradeData = new SvcTradeData(svcData);
                svcTradeData.setEndDate(reqData.getAcceptTime());
                svcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                svcTradeData.setRemark("因设备回收而终止");
                btd.add(reqData.getUca().getSerialNumber(), svcTradeData);

            }
        }
    }

}
