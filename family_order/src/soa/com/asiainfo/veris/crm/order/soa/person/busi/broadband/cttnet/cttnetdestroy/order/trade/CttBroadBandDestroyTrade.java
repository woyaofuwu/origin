
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccessAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AddrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetActTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetdestroy.order.requestdata.CttBroadBandDestroyReqData;

public class CttBroadBandDestroyTrade extends BaseTrade implements ITrade
{

    /**
     * @param btd
     * @param reqData
     * @param accessAcct
     * @param bindTag
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeAccessAcct(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        IDataset broadbandList = BroadBandInfoQry.getBroadBandAccessAcctByUserId(reqData.getUca().getUserId());
        int size = broadbandList.size();
        for (int i = 0; i < size; i++)
        {
            AccessAcctTradeData accessAcctTD = new AccessAcctTradeData(broadbandList.getData(i)).clone();
            accessAcctTD.setEndDate(reqData.getAcceptTime());
            accessAcctTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            accessAcctTD.setRemark("移动宽带拆机");

            btd.add(btd.getRD().getUca().getSerialNumber(), accessAcctTD);
        }
    }

    /**
     * 用户宽带地址子台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeAddr(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", reqData.getUca().getUserId());
        IDataset addrList = BroadBandInfoQry.queryBroadBandAddressInfo(param);
        int size = addrList.size();
        for (int i = 0; i < size; i++)
        {
            AddrTradeData addrTD = new AddrTradeData(addrList.getData(i)).clone();
            addrTD.setEndDate(reqData.getAcceptTime());
            addrTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            addrTD.setRemark("移动宽带拆机");

            btd.add(btd.getRD().getUca().getSerialNumber(), addrTD);
        }
    }

    private void cancelTradeAttr(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        List<AttrTradeData> attrList = reqData.getUca().getUserAttrs();
        for (int i = 0; i < attrList.size(); i++)
        {
            AttrTradeData attrTradeData = attrList.get(i);
            attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            attrTradeData.setEndDate(reqData.getAcceptTime());

            btd.add(btd.getRD().getUca().getSerialNumber(), attrTradeData);
        }
    }

    /**
     * 用户优惠子台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeDiscnt(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        List<DiscntTradeData> discntList = reqData.getUca().getUserDiscnts();
        int size = discntList.size();
        String modemDiscntCode = BroadBandInfoQry.getModemDiscntCode(); // modom租用优惠编码
        String sendBackDiscntCode = "31910001"; // 宽带补退优惠编码
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTD = discntList.get(i).clone();
            discntTD.setEndDate(reqData.getAcceptTime());// 立即截止，优惠按天计算
            discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            discntTD.setRemark("宽带拆机");

            String discntCode = discntTD.getDiscntCode();
            if (!sendBackDiscntCode.equals(discntCode) && !modemDiscntCode.equals(discntCode))// 优惠编码不是modem租用编码，不是补退优惠编码
            {
                // 设置主优惠套餐优惠编码，保存到主台账，提供给pboss接口
                MainTradeData mainTD = btd.getMainTradeData();
                mainTD.setRsrvStr1(discntCode);
            }

            btd.add(btd.getRD().getUca().getSerialNumber(), discntTD);
        }
    }

    /**
     * 用户付费关系子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradePayRelation(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        IDataset payRelationList = PayRelaInfoQry.getAllValidUserPayByAcctId(reqData.getUca().getAcctId());
        int size = payRelationList.size();
        for (int i = 0; i < size; i++)
        {
            PayRelationTradeData payRelationTD = new PayRelationTradeData(payRelationList.getData(i)).clone();
            payRelationTD.setEndCycleId(SysDateMgr.getNowCycle());
            payRelationTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            payRelationTD.setRemark("宽带拆机");

            btd.add(btd.getRD().getUca().getSerialNumber(), payRelationTD);
        }
    }

    /**
     * 用户产品子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeProduct(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        ProductTradeData productTD = reqData.getUca().getUserMainProduct().clone();

        productTD.setEndDate(reqData.getAcceptTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }

    /**
     * 用户宽带速率子台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeRate(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        IDataset rateList = BroadBandInfoQry.queryUserRateByUserId(reqData.getUca().getUserId());
        int size = rateList.size();
        for (int i = 0; i < size; i++)
        {
            RateTradeData rateTD = new RateTradeData(rateList.getData(i)).clone();
            rateTD.setEndDate(reqData.getAcceptTime());
            rateTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            rateTD.setRemark("移动宽带拆机");

            btd.add(btd.getRD().getUca().getSerialNumber(), rateTD);
        }
    }

    /**
     * 用户服务子台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeSvc(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        List<SvcTradeData> svcList = reqData.getUca().getUserSvcs();
        int size = svcList.size();
        for (int i = 0; i < size; i++)
        {
            SvcTradeData svcTD = svcList.get(i).clone();
            svcTD.setEndDate(reqData.getAcceptTime());
            svcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            svcTD.setRemark("宽带拆机");

            btd.add(btd.getRD().getUca().getSerialNumber(), svcTD);
        }
    }

    /**
     * 用户子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeUser(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        UserTradeData userTD = reqData.getUca().getUser().clone();

        userTD.setRemoveTag("2");
        userTD.setDestroyTime(reqData.getAcceptTime());
        userTD.setRemoveCityCode(CSBizBean.getVisit().getCityCode());
        userTD.setRemoveDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setRemoveEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        userTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        userTD.setRemark(reqData.getRemark());

        btd.add(btd.getRD().getUca().getSerialNumber(), userTD);
    }

    /**
     * 用户资源子台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     *             wangjx 2013-12-26
     */
    private void cancelTradeUserRes(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        List<ResTradeData> resList = reqData.getUca().getUserAllRes();
        int size = resList.size();
        if (!resList.isEmpty())
        {
            for (int i = 0; i < size; i++)
            {
                ResTradeData resTD = resList.get(i).clone();
                resTD.setEndDate(reqData.getAcceptTime());
                resTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                resTD.setRemark("宽带拆机");

                btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
            }
        }
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CttBroadBandDestroyReqData reqData = (CttBroadBandDestroyReqData) btd.getRD();

        cancelTradeUser(btd, reqData);

        // cancelTradeAccessAcct(btd, reqData);
        // cancelTradeAddr(btd, reqData);
        // cancelTradeRate(btd, reqData);
        geneTradeWideNet(btd, reqData); // 设置用户台账宽带资料
        geneTradeWideNetAct(btd, reqData); // 设置用户台账宽带账号资料
        cancelTradePayRelation(btd, reqData);// 设置用户付费关系业务台账
        cancelTradeUserRes(btd, reqData);// 设置用户资源台账
        cancelTradeSvc(btd, reqData);// 设置服务业务台账
        cancelTradeDiscnt(btd, reqData);// 设置用户优惠业务台账
        cancelTradeAttr(btd, reqData);// 设置用户属性业务台账
        cancelTradeProduct(btd, reqData);

        // 修改主台账字段
        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setProductId(reqData.getUca().getProductId());
        mainTD.setBrandCode(reqData.getUca().getBrandCode());
        mainTD.setCityCode(CSBizBean.getVisit().getCityCode());
        mainTD.setSerialNumberB(reqData.getUca().getSerialNumber());
        mainTD.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTD.setPfType(CttConstants.PF_TYPE);
        mainTD.setOlcomTag(CttConstants.OLCOM_TAG);

        // 服开信息
        // mainTD.setRsrvStr2(tag);// 是否回收终端
        mainTD.setRsrvStr4(CSBizBean.getVisit().getStaffName());
        mainTD.setRsrvStr5(CSBizBean.getVisit().getDepartName());
        mainTD.setRsrvStr6(CSBizBean.getVisit().getSerialNumber());// 营业员手机号码
    }

    public void geneTradeWideNet(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        UcaData ucaData = reqData.getUca();
        IDataset datanet = WidenetInfoQry.getUserWidenetInfo(ucaData.getUserId()); // 获取用户宽带装机信息
        if (IDataUtil.isEmpty(datanet))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_4);
        }
        WideNetTradeData widenetTradeData = new WideNetTradeData(datanet.getData(0));

        widenetTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        widenetTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
        widenetTradeData.setRemark("用户宽带拆机操作");

        IDataset dataset = UserConnQry.getConnInfosByIdA(ucaData.getUserId(), "BK");
        if (IDataUtil.isNotEmpty(dataset))
        {// 如果存在宽带与固话共线信息,记录固话用户ID
            widenetTradeData.setRsrvStr5(dataset.getData(0).getString("USER_ID_B", ""));
        }

        btd.add(ucaData.getSerialNumber(), widenetTradeData);
    }

    /**
     * 设置业务台账用户宽带账号资料表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeWideNetAct(BusiTradeData<BaseTradeData> btd, CttBroadBandDestroyReqData reqData) throws Exception
    {
        UcaData ucaData = reqData.getUca();

        IDataset dataNetAct = WidenetInfoQry.getWidenetActInfosByUserid(ucaData.getUserId());// 获取用户宽带账号信息

        if (IDataUtil.isNotEmpty(dataNetAct))
        {
            WideNetActTradeData actTradeData = new WideNetActTradeData(dataNetAct.getData(0));
            actTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            actTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
            actTradeData.setRemark("用户宽带拆机操作");

            btd.add(ucaData.getSerialNumber(), actTradeData);
        }
    }

}
