
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ModifyPhoneCodeReqData;

public class ModifyPhoneCodeTrade extends BaseTrade implements ITrade // extends BaseTrade implements ITrade
{
    protected static Logger log = Logger.getLogger(ModifyPhoneCodeTrade.class);

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
//        wideUserInfoTrade(bd);
        createMainTable(bd);
        resPhoneInfoTrade(bd);
        userSvcTrade(bd);
        dealSpecDiscnt(bd);
        preSimCard(bd);
        dealUserDisInfo(bd);
    }

    public void createMainTable(BusiTradeData bd) throws Exception
    {
        ModifyPhoneCodeReqData phoneReq = (ModifyPhoneCodeReqData) bd.getRD();
        List<MainTradeData> mainList = bd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        mainList.get(0).setRsrvStr2(phoneReq.getNewSerialNumber());
        mainList.get(0).setOlcomTag("1");
        mainList.get(0).setRsrvStr6(phoneReq.getOldSimCardInfo().getImsi());// old imsi
        mainList.get(0).setRsrvStr7(phoneReq.getNewSimCardInfo().getImsi());// new imsi
        mainList.get(0).setRsrvStr9(phoneReq.getOldSimCardInfo().getSimCardNo());
        mainList.get(0).setRsrvStr10(phoneReq.getNewSimCardInfo().getSimCardNo());
        /*
         * if(IDataUtil.isNotEmpty(set)){ if(!"0".equals(set.getData(0).getString("RSRV_TAG3"))){ List<MainTradeData>
         * mainList = bd.getTradeDatas(TradeTableEnum.TRADE_MAIN); mainList.get(0).setRsrvStr1("1");
         * mainList.get(0).setProcessTagSet("000000002"); } }
         */
    }

    /**
     * 处理特殊优惠，目前要求统一积分平台兑换的自由产品（短信、彩信）在改号时立即终止
     * 
     * @param bd
     * @throws Exception
     */
    public void dealSpecDiscnt(BusiTradeData bd) throws Exception
    {
        IDataset paramDiscnt = ParamInfoQry.getCommparaByAttrParaCode("CSM", "4502", "1", "02");
        if (IDataUtil.isNotEmpty(paramDiscnt))
        {
            for (int i = 0; i < paramDiscnt.size(); i++)
            {
                List<DiscntTradeData> disList = bd.getRD().getUca().getUserDiscntByDiscntId(paramDiscnt.getData(i).getString("PARA_CODE2", ""));
                if (disList != null && disList.size() > 0)
                {
                    for (DiscntTradeData disData : disList)
                    {
                        DiscntTradeData dis = disData.clone();
                        dis.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        dis.setRemark("改号时终止积分平台兑换自有产品优惠");
                        dis.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                        bd.add(bd.getRD().getUca().getSerialNumber(), dis);
                    }
                }
            }
        }
    }

    // 状态变更优惠处理：1：先直接删除下月初生效的优惠 2：再插台帐终止当前生效的优惠
    public void dealUserDisInfo(BusiTradeData bd) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", bd.getRD().getUca().getUserId());
        Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "DEL_USER_DISCNT_NOT19", param);

        IDataset set = CommparaInfoQry.getCommByParaAttr("CSM", "3700", "0898");
        List<DiscntTradeData> disList = bd.getRD().getUca().getUserDiscnts();
        if (IDataUtil.isNotEmpty(set))
        {
            for (int i = 0; i < set.size(); i++)
            {
                if ("98001901".equals(set.getData(i).getString("PARAM_CODE")))
                {
                    continue;
                }
                for (DiscntTradeData disData : disList)
                {
                    if (set.getData(i).getString("PARA_CODE2", "").equals(disData.getDiscntCode()))
                    {
                        DiscntTradeData dis = disData.clone();
                        dis.setEndDate(bd.getRD().getAcceptTime());
                        dis.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        dis.setRemark("改号终止平台除无线音乐会员外的优惠");
                        bd.add(bd.getRD().getUca().getSerialNumber(), dis);
                    }
                }
            }
        }
    }

    // 新号码预占SIM卡信息
    public void preSimCard(BusiTradeData bd) throws Exception
    {
        ModifyPhoneCodeReqData phoneReq = (ModifyPhoneCodeReqData) bd.getRD();
        // sim卡预占
        ResCall.resEngrossForSim("0", phoneReq.getNewSimCardInfo().getSimCardNo(), phoneReq.getNewSerialNumber());
        // 号码预占
        ResCall.resEngrossForMphone(phoneReq.getNewSerialNumber());
    }

    /**
     * 资源台帐
     * 
     * @param bd
     * @throws Exception
     */
    public void resPhoneInfoTrade(BusiTradeData bd) throws Exception
    {
        ModifyPhoneCodeReqData phoneReq = (ModifyPhoneCodeReqData) bd.getRD();
        // 号码资源
        List<ResTradeData> oldUserRes = phoneReq.getUca().getUserAllRes();
        for (ResTradeData resInfo : oldUserRes)
        {
            if ("0".equals(resInfo.getResTypeCode()) || "1".equals(resInfo.getResTypeCode()))
            {
                ResTradeData delInfoData = resInfo.clone();
                delInfoData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delInfoData.setEndDate(SysDateMgr.getLastSecond(phoneReq.getAcceptTime()));
                bd.add(phoneReq.getUca().getSerialNumber(), delInfoData);
            }
        }

        String instId = SeqMgr.getInstId();
        ResTradeData resTradeDataNew = new ResTradeData();
        resTradeDataNew.setResTypeCode("0");
        resTradeDataNew.setResCode(phoneReq.getNewSerialNumber());
        resTradeDataNew.setImsi("0");
        resTradeDataNew.setUserId(phoneReq.getUca().getUserId());
        resTradeDataNew.setUserIdA("-1");
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeDataNew.setStartDate(phoneReq.getAcceptTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_TIME_FOREVER);
        bd.add(phoneReq.getUca().getSerialNumber(), resTradeDataNew);

        instId = SeqMgr.getInstId();
        ResTradeData resSimTradeNew = new ResTradeData();
        resSimTradeNew.setResTypeCode("1");
        resSimTradeNew.setResCode(phoneReq.getNewSimCardInfo().getSimCardNo());
        resSimTradeNew.setUserId(phoneReq.getUca().getUserId());
        resSimTradeNew.setUserIdA("-1");
        resSimTradeNew.setImsi(phoneReq.getNewSimCardInfo().getImsi());
        resSimTradeNew.setKi(phoneReq.getNewSimCardInfo().getKi());
        resSimTradeNew.setRsrvStr1(phoneReq.getNewSimCardInfo().getResKindCode());
        resSimTradeNew.setRsrvStr2(phoneReq.getNewSimCardInfo().getResTypeCode());
        resSimTradeNew.setRsrvStr3(phoneReq.getNewSimCardInfo().getAgentSaleTag());
        resSimTradeNew.setRsrvStr4(phoneReq.getNewSimCardInfo().getResKindCode());
        resSimTradeNew.setRsrvStr5(phoneReq.getNewSimCardInfo().getEmptyCardId());
        resSimTradeNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resSimTradeNew.setStartDate(phoneReq.getAcceptTime());
        resSimTradeNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resSimTradeNew.setInstId(instId);
        
        resSimTradeNew.setRsrvTag2(phoneReq.getNewSimCardInfo().getAgentSaleTag().equals("1") ? "A" : "B");// 新处理C为买断卡
        if (phoneReq.getNewSimCardInfo().getAgentSaleTag().equals("1"))
        {
        	resSimTradeNew.setRsrvNum5(phoneReq.getNewSimCardInfo().getSaleMoney());// 卡价
        }
        resSimTradeNew.setRsrvTag3(phoneReq.getNewSimCardInfo().getFlag4G());// 是否为4G卡
        
        bd.add(phoneReq.getUca().getSerialNumber(), resSimTradeNew);

    }

    /**
     * 存在浦发银行签约,存在，删除 如果存在有效的无条件呼转服务，则终止该服务
     * 
     * @param bd
     * @throws Exception
     */
    public void userSvcTrade(BusiTradeData bd) throws Exception
    {
        List<SvcTradeData> svcList = bd.getRD().getUca().getUserSvcBySvcId("3013");// 浦发银行签约服务
        if (svcList != null && svcList.size() > 0)
        {
            for (SvcTradeData svcData : svcList)
            {
                SvcTradeData svc = svcData.clone();
                svc.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                svc.setModifyTag(BofConst.MODIFY_TAG_DEL);
                bd.add(bd.getRD().getUca().getSerialNumber(), svc);

                SvcStateTradeData svcState = bd.getRD().getUca().getUserSvcsStateByServiceId(svcData.getElementId()).clone();
                svcState.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                svcState.setModifyTag(BofConst.MODIFY_TAG_DEL);
                bd.add(bd.getRD().getUca().getSerialNumber(), svcState);
            }
        }
        svcList = bd.getRD().getUca().getUserSvcBySvcId("12"); // //无条件呼转服务
        if (svcList != null && svcList.size() > 0)
        {
            for (SvcTradeData svcData : svcList)
            {
                SvcTradeData svc = svcData.clone();
                svc.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                svc.setModifyTag(BofConst.MODIFY_TAG_DEL);
                svc.setRemark("改号前有无条件呼转，终止该服务");
                bd.add(bd.getRD().getUca().getSerialNumber(), svc);

                SvcStateTradeData svcState = bd.getRD().getUca().getUserSvcsStateByServiceId(svcData.getElementId()).clone();
                svcState.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                svcState.setModifyTag(BofConst.MODIFY_TAG_DEL);
                bd.add(bd.getRD().getUca().getSerialNumber(), svcState);
            }
        }
    }

    /**
     * 宽带台帐
     * 
     * @param bd
     * @throws Exception
     */
//    public void wideUserInfoTrade(BusiTradeData bd) throws Exception
//    {
//        ModifyPhoneCodeReqData phoneReq = (ModifyPhoneCodeReqData) bd.getRD();
//        
//        String serialNumber=bd.getRD().getUca().getSerialNumber();
//        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+serialNumber);
//        if (IDataUtil.isNotEmpty(widenetInfos))
//        {
//            String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
//            if (!"4".equals(wideType))
//            {
//                IData data = new DataMap();
//                data.put("SERIAL_NUMBER", bd.getRD().getUca().getSerialNumber());
//                data.put("SERIAL_NUMBER_PRE", phoneReq.getNewSerialNumber());
//                data.put("TRADE_TYPE_CODE", bd.getTradeTypeCode());
//                CSAppCall.call("SS.WideChangeUserIntfSVC.tradeReg", data);
//            }
//        }
//    }
}
