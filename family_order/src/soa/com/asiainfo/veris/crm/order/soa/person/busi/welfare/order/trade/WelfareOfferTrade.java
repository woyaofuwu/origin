
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfareDiscntData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfarePlatSvcData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfareSvcData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.requestdata.WelfareOfferRequestData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 权益自有商品受理类
 * @Auther: zhenggang
 * @Date: 2020/7/3 10:57
 * @version: V1.0
 */
public class WelfareOfferTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        WelfareOfferRequestData reqData = (WelfareOfferRequestData) btd.getRD();

        List<ProductModuleData> pmds = reqData.getPmds();

        // 元素拼串处理
        ProductModuleCreator.createProductModuleTradeData(pmds, btd, null);

        // 登记权益商品和自有商品的关系
        dealWelfareOfferRel(btd, reqData);

        // 传入DEL_WELFARE_OFFER_CODE处理
        if (StringUtils.isNotEmpty(reqData.getDelWelfareOfferCode()))
        {
            this.delWelfareOfferAndRel(btd, reqData);
        }

        MainTradeData mtd = btd.getMainTradeData();
        // 记录前置订单流水（返销使用）
        mtd.setRsrvStr1(reqData.getAdvanceOrderId());
        mtd.setRsrvStr2(reqData.getAdvanceTradeId());
        // 记录权益订单流水
        mtd.setRsrvStr3(reqData.getWelfareTradeId());
        // 记录权益订购打印内容
        mtd.setRsrvStr8(reqData.getPrintContent());


    }

    /**
     * @Description: 针对传入权益ID删除的处理
     * @Param: [btd, reqData]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/14 16:16
     */
    private void delWelfareOfferAndRel(BusiTradeData btd, WelfareOfferRequestData reqData) throws Exception
    {
        String delWelfareOfferCode = reqData.getDelWelfareOfferCode();

        UcaData uca = reqData.getUca();

        // 根据Q找到所有有关系的自有权益商品
        List<OfferRelTradeData> offerRels = uca.getOfferRelsByRelUserId();

        List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

        for (OfferRelTradeData offerRel : offerRels)
        {
            String relOfferCode = offerRel.getRelOfferCode();
            String relOfferType = offerRel.getRelOfferType();

            if (!WelfareConstants.OfferType.WEFFARE.getValue().equals(relOfferType) || !relOfferCode.equals(delWelfareOfferCode))
            {
                continue;
            }

            // 终止商品
            String offerCode = offerRel.getOfferCode();
            String offerType = offerRel.getOfferType();
            String offerInsId = offerRel.getOfferInsId();
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
            {
                DiscntTradeData dtd = uca.getUserDiscntByInstId(offerInsId);
                if (null != dtd)
                {
                    DiscntData dd = new DiscntData();
                    dd.setElementId(dtd.getElementId());
                    dd.setInstId(offerInsId);
                    dd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    dd.setEndDate(reqData.getSelfDefEndDate());
                    pmds.add(dd);
                }
            }
            else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(offerType))
            {
                SvcTradeData std = uca.getUserSvcByInstId(offerInsId);
                if (null != std)
                {
                    SvcData sd = new SvcData();
                    sd.setElementId(std.getElementId());
                    sd.setInstId(offerInsId);
                    sd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    sd.setEndDate(reqData.getSelfDefEndDate());
                    pmds.add(sd);
                }
            }
            else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(offerType))
            {
                PlatSvcTradeData pstd = uca.getUserPlatSvcByInstId(offerInsId);
                if (null != pstd)
                {
                    PlatSvcData psd = new PlatSvcData();
                    psd.setElementId(psd.getElementId());
                    psd.setInstId(offerInsId);
                    psd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                    psd.setEndDate(reqData.getSelfDefEndDate());
                    pmds.add(psd);
                }
            }
            // 终止关系
            offerRel.setModifyTag(BofConst.MODIFY_TAG_DEL);
            offerRel.setEndDate(reqData.getSelfDefEndDate());
            btd.add(uca.getSerialNumber(), offerRel);
        }
        if (pmds.size() > 0)
        {
            ProductModuleCreator.createProductModuleTradeData(pmds, btd, null);
        }
    }

    /**
     * @Description: 权益商品和自有商品关系台账
     * @Param: [btd, reqData]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/6 17:33
     */
    private void dealWelfareOfferRel(BusiTradeData btd, WelfareOfferRequestData reqData) throws Exception
    {
        List<SvcTradeData> stds = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        List<DiscntTradeData> dtds = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);

        for (PlatSvcTradeData pstd : pstds)
        {
            pstd.setRsrvStr1(WelfareConstants.OfferType.WEFFARE.getValue());
        }
        for (SvcTradeData std : stds)
        {
            std.setRsrvStr1(WelfareConstants.OfferType.WEFFARE.getValue());
        }
        for (DiscntTradeData dtd : dtds)
        {
            dtd.setRsrvStr1(WelfareConstants.OfferType.WEFFARE.getValue());
        }
        List<ProductModuleTradeData> pmtds = new ArrayList<ProductModuleTradeData>();
        if (ArrayUtil.isNotEmpty(stds))
        {
            pmtds.addAll(stds);
        }
        if (ArrayUtil.isNotEmpty(dtds))
        {
            pmtds.addAll(dtds);
        }
        if (ArrayUtil.isNotEmpty(pstds))
        {
            pmtds.addAll(pstds);
        }

        for (ProductModuleTradeData pmtd : pmtds)
        {
            String modifyTag = pmtd.getModifyTag();

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                createWelfareOfferRel(btd, pmtd, reqData);
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                deleteWelfareOfferRel(btd, pmtd, reqData);
            }
            else if (BofConst.MODIFY_TAG_INHERIT.equals(modifyTag))
            {
                // 继承的场景
            }
            else if (BofConst.MODIFY_TAG_UPD.equals(modifyTag))
            {
                // 修改的场景
                for (PlatSvcTradeData pstd : pstds)
                {
                    pstd.setEndDate(reqData.getSelfDefEndDate());
                }
                for (SvcTradeData std : stds)
                {
                    std.setEndDate(reqData.getSelfDefEndDate());
                }
                for (DiscntTradeData dtd : dtds)
                {
                    dtd.setEndDate(reqData.getSelfDefEndDate());
                }
                updateWelfareOfferRel(btd, pmtd, reqData);
            }
        }
    }

    /**
     * @Description: 创建关系
     * @Param: [btd, pmtd, reqData]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/6 19:11
     */
    private void createWelfareOfferRel(BusiTradeData btd, ProductModuleTradeData pmtd, WelfareOfferRequestData reqData) throws Exception
    {
        UcaData uca = reqData.getUca();
        OfferRelTradeData offerRel = new OfferRelTradeData();
        offerRel.setInstId(SeqMgr.getInstId());
        offerRel.setUserId(uca.getUserId());

        // 这里反着记录一下，以免OFFER_REL被现网逻辑处理
        offerRel.setOfferCode(pmtd.getElementId());
        offerRel.setOfferType(pmtd.getElementType());
        offerRel.setOfferInsId(pmtd.getInstId());

        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(pmtd.getElementType()))
        {
            WelfareDiscntData wdd = (WelfareDiscntData) pmtd.getPmd();
            offerRel.setRelOfferCode(wdd.getWelfareOfferCode());
            offerRel.setRelOfferType(wdd.getWelfareOfferType());
        }
        else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(pmtd.getElementType()))
        {
            WelfareSvcData wsd = (WelfareSvcData) pmtd.getPmd();
            offerRel.setRelOfferCode(wsd.getWelfareOfferCode());
            offerRel.setRelOfferType(wsd.getWelfareOfferType());
        }
        else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(pmtd.getElementType()))
        {
            WelfarePlatSvcData wpsd = (WelfarePlatSvcData) pmtd.getPmd();
            offerRel.setRelOfferCode(wpsd.getWelfareOfferCode());
            offerRel.setRelOfferType(wpsd.getWelfareOfferType());
        }

        // ------------------------------------------------------------------------
        // 这里分为订单中心触发权益订购和权益中心自己订购
        // 订单中心触发的话这里放主产品的实例ID
        // 权益中心自己订购没有前置交易流水也没有产品变化字段填-1
        // -------------------------------------------------------------------------
        offerRel.setRelOfferInsId(WelfareConstants.RelType.FAKE_ID.getValue());
        //
        if (StringUtils.isNotEmpty(reqData.getAddMainOfferInsId()))
        {
            offerRel.setRelOfferInsId(reqData.getAddMainOfferInsId());
        }

        offerRel.setRelUserId(pmtd.getUserId());
        offerRel.setGroupId(WelfareConstants.RelType.FAKE_ID.getValue());
        if (StringUtils.isNotBlank(reqData.getAdvanceTradeId()))
        {
            // 通过订单中心发起的记录为0
            offerRel.setGroupId(WelfareConstants.RelType.ZERO_ID.getValue());
        }
        offerRel.setRelType(WelfareConstants.RelType.WEFFARE.getValue());
        offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
        offerRel.setStartDate(pmtd.getStartDate());
        offerRel.setEndDate(pmtd.getEndDate());
        btd.add(uca.getSerialNumber(), offerRel);
    }

    /**
     * @Description: 删除关系
     * @Param: [btd, pmtd, reqData]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/6 19:45
     */
    private void deleteWelfareOfferRel(BusiTradeData btd, ProductModuleTradeData pmtd, WelfareOfferRequestData reqData) throws Exception
    {
        UcaData uca = reqData.getUca();

        // 订购时记录OFFER_REL反着记的，这里反着找
        List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndOfferInsId(pmtd.getInstId());

        if (ArrayUtil.isEmpty(offerRels))
        {
            return;
        }

        for (OfferRelTradeData offerRel : offerRels)
        {
            String relOfferCode = offerRel.getRelOfferCode();// 权益编码
            String relOfferType = offerRel.getRelOfferType();// 权益类型
            String relOfferInsId = offerRel.getRelOfferInsId(); // 主商品实例ID 可能为-1表示不是订单中心发起的订购

            if (!WelfareConstants.RelType.WEFFARE.getValue().equals(relOfferType))
            {
                continue;
            }

            if (StringUtils.isNotEmpty(reqData.getDelMainOfferInsId()))
            {
                // 如果退订为订单中心用户产品变更发起，则要找到对应产品的权益
                if (!reqData.getDelMainOfferInsId().equals(relOfferInsId))
                {
                    continue;
                }
            }

            offerRel.setModifyTag(BofConst.MODIFY_TAG_DEL);
            offerRel.setEndDate(pmtd.getEndDate());
            btd.add(uca.getSerialNumber(), offerRel);
        }
    }

    /**
     * @Description: 修改关系
     * @Param: [btd, pmtd, reqData]
     * @return: void
     * @Author: liwei
     * @Date: 2020/7/27 19:45
     */
    private void updateWelfareOfferRel(BusiTradeData btd, ProductModuleTradeData pmtd, WelfareOfferRequestData reqData) throws Exception
    {
        UcaData uca = reqData.getUca();

        // 订购时记录OFFER_REL反着记的，这里反着找
        List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndOfferInsId(pmtd.getInstId());

        if (ArrayUtil.isEmpty(offerRels))
        {
            return;
        }

        for (OfferRelTradeData offerRel : offerRels)
        {
            String relOfferCode = offerRel.getRelOfferCode();// 权益编码
            String relOfferType = offerRel.getRelOfferType();// 权益类型
            String relOfferInsId = offerRel.getRelOfferInsId(); // 主商品实例ID 可能为-1表示不是订单中心发起的订购

            offerRel.setModifyTag(BofConst.MODIFY_TAG_UPD);
            offerRel.setEndDate(reqData.getSelfDefEndDate());
            btd.add(uca.getSerialNumber(), offerRel);
        }
    }

}
