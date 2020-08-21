
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 生成定制服务台帐
 * 
 * @author sunxin
 */
public class GeneSubscribeTradeSvcAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        IDataset commparaInfos = CommparaInfoQry.querySubscribeSvc(btd.getRD().getUca().getBrandCode(), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
        {
            return;
        }
        IData commparaInfo = new DataMap();
        for (int i = 0; i < commparaInfos.size(); i++)
        {
            commparaInfo = commparaInfos.getData(i);
            String serviceId = commparaInfo.getString("PARA_CODE1");
            String discntCode = commparaInfo.getString("PARA_CODE2");
            String sMonth = commparaInfo.getString("PARA_CODE3");
            String mode = commparaInfo.getString("PARA_CODE4");
            List<SvcTradeData> svcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            String instId = "";
            String relaInstId = "";
            int count = 0;
            for (SvcTradeData svcTradeData : svcTradeDatas)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()))
                {
                    if (svcTradeData.getElementId().equals(serviceId))
                    {
                        relaInstId = svcTradeData.getInstId();
                        count++;
                        break;
                    }

                }
            }
            if (count == 0)
            {
                instId = SeqMgr.getInstId();
                SvcTradeData SvcTradeData = new SvcTradeData();
                SvcTradeData.setElementId("");
                SvcTradeData.setUserId(btd.getRD().getUca().getUserId());
                SvcTradeData.setUserIdA("-1");
                SvcTradeData.setProductId("-1");
                SvcTradeData.setPackageId("-1");
                SvcTradeData.setElementType("S");
                SvcTradeData.setInstId(instId);
                SvcTradeData.setCampnId("");
                SvcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                SvcTradeData.setStartDate(SysDateMgr.getSysTime());
                SvcTradeData.setEndDate(SysDateMgr.getTheLastTime());
                SvcTradeData.setRemark("增加用户未选择的定制服务");
                btd.add(btd.getRD().getUca().getSerialNumber(), SvcTradeData);

            }
            List<AttrTradeData> attrTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
            int attrCount = 0;
            int attrCountOther = 0;
            for (AttrTradeData attrTradeData : attrTradeDatas)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(attrTradeData.getModifyTag()) && ("serv_para7".equals(attrTradeData.getAttrCode())) && ("S".equals(attrTradeData.getInstType())) && (relaInstId.equals(attrTradeData.getRelaInstId())))
                {
                    attrTradeData.setAttrValue("DZ0");
                    attrCount++;
                }

                // 登记定制服务的定制结束时间通过生效开始时间
                if (BofConst.MODIFY_TAG_ADD.equals(attrTradeData.getModifyTag()) && ("serv_para8".equals(attrTradeData.getAttrCode())) && ("S".equals(attrTradeData.getInstType())) && (relaInstId.equals(attrTradeData.getRelaInstId())))
                {
                    String date = mode == "0" ? attrTradeData.getAttrValue() : SysDateMgr.getAddMonthsLastDay(Integer.parseInt(sMonth), attrTradeData.getStartDate());
                    attrTradeData.setAttrValue(date);
                    attrCountOther++;
                }

            }

            // 将服务参数对应相应的服务台账（因为前面可能重新拼入台账，这里重新取）
            List<SvcTradeData> newSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
            for (SvcTradeData newSvcTradeData : newSvcTradeDatas)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(newSvcTradeData.getModifyTag()))
                {
                    if (newSvcTradeData.getElementId().equals(serviceId))
                    {
                        if (attrCount == 0)
                        {
                            AttrTradeData AttrTradeData = new AttrTradeData();
                            AttrTradeData.setUserId(btd.getRD().getUca().getUserId());
                            AttrTradeData.setInstType("S");
                            AttrTradeData.setInstId(SeqMgr.getInstId());
                            AttrTradeData.setRelaInstId(newSvcTradeData.getInstId());
                            AttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            AttrTradeData.setAttrCode("serv_para7");
                            AttrTradeData.setAttrValue("DZ0");
                            AttrTradeData.setStartDate(newSvcTradeData.getStartDate());
                            AttrTradeData.setEndDate(newSvcTradeData.getEndDate());
                            AttrTradeData.setRsrvNum1(serviceId);
                            AttrTradeData.setRemark(newSvcTradeData.getRemark());
                            btd.add(btd.getRD().getUca().getSerialNumber(), AttrTradeData);

                        }

                        if (attrCountOther == 0 && (!mode.equals("0")))
                        {
                            AttrTradeData AttrTradeData = new AttrTradeData();
                            AttrTradeData.setUserId(btd.getRD().getUca().getUserId());
                            AttrTradeData.setInstType("S");
                            AttrTradeData.setInstId(SeqMgr.getInstId());
                            AttrTradeData.setRelaInstId(newSvcTradeData.getInstId());
                            AttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            AttrTradeData.setAttrCode("serv_para8");
                            AttrTradeData.setAttrValue(SysDateMgr.getAddMonthsLastDay(Integer.parseInt(sMonth), SysDateMgr.getSysTime()));
                            AttrTradeData.setStartDate(newSvcTradeData.getStartDate());
                            AttrTradeData.setEndDate(newSvcTradeData.getEndDate());
                            AttrTradeData.setRsrvNum1(serviceId);
                            AttrTradeData.setRemark(newSvcTradeData.getRemark());
                            btd.add(btd.getRD().getUca().getSerialNumber(), AttrTradeData);
                        }
                    }

                }
            }

            if (!discntCode.equals(""))
            {

                List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                for (DiscntTradeData discntTradeData : discntTradeDatas)
                {
                    if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()) && (!discntCode.equals(discntTradeData.getElementId())))
                    {
                        DiscntTradeData newDiscnt = new DiscntTradeData();
                        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                        newDiscnt.setProductId("-1");
                        newDiscnt.setPackageId("-1");
                        newDiscnt.setUserIdA("-1");
                        newDiscnt.setCampnId("");
                        newDiscnt.setElementId(discntCode);
                        newDiscnt.setInstId(SeqMgr.getInstId());
                        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newDiscnt.setSpecTag("0");
                        newDiscnt.setStartDate(SysDateMgr.getSysTime());
                        newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(Integer.parseInt(sMonth), SysDateMgr.getSysTime()));
                        newDiscnt.setRemark("增加用户定制服务的优惠");
                        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
                    }
                }
            }

        }

    }

}
