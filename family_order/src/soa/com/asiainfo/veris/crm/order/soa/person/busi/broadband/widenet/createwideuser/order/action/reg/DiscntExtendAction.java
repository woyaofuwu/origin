
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class DiscntExtendAction implements ITradeAction
{

    /**
     * 资费顺延
     * 
     * @author chenzm
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {

        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            String discntCode = discntTradeData.getDiscntCode();
            String productId = discntTradeData.getProductId();
            String packageId = discntTradeData.getPackageId();
            String endDate = discntTradeData.getEndDate();
            String instId = "";
            if ("-1".equals(packageId))
            {
                continue;
            }
//            IDataset pkgElemInfos = BofQuery.getServElementByPk(packageId, discntCode, "D");
            
            IDataset enableModeInfo = UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_DISCNT, discntCode);
            
            // 根据新增优惠绑定默认优惠
            IDataset commparaInfo1s = CommparaInfoQry.getCommparaInfoByCode3("CSM", "602", discntCode, productId, btd.getRD().getUca().getUserEparchyCode());
            if (IDataUtil.isNotEmpty(commparaInfo1s))
            {
                DiscntTradeData newDiscnt = new DiscntTradeData();
                instId = SeqMgr.getInstId();
                IData commparaInfo = commparaInfo1s.getData(0);
                newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                newDiscnt.setUserIdA("-1");
                newDiscnt.setProductId(productId);
                newDiscnt.setPackageId(commparaInfo.getString("PARA_CODE2"));
                newDiscnt.setElementId(commparaInfo.getString("PARA_CODE1"));
                newDiscnt.setInstId(instId);
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                newDiscnt.setSpecTag("0");
                newDiscnt.setStartDate(SysDateMgr.getDateNextMonthFirstDay(endDate));
                newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
                newDiscnt.setRemark("宽带装机顺延收费优惠");
                btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
            }
            else
            {
                // 根据新增产品绑定默认优惠
                IDataset commparaInfo2s = CommparaInfoQry.getCommpara("CSM", "601", productId, btd.getRD().getUca().getUserEparchyCode());
                if (IDataUtil.isNotEmpty(commparaInfo2s))
                {
                    DiscntTradeData newDiscnt = new DiscntTradeData();
                    instId = SeqMgr.getInstId();
                    IData commparaInfo = commparaInfo2s.getData(0);
                    newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                    newDiscnt.setUserIdA("-1");
                    newDiscnt.setProductId(productId);
                    newDiscnt.setPackageId(commparaInfo.getString("PARA_CODE2"));
                    newDiscnt.setElementId(commparaInfo.getString("PARA_CODE1"));
                    newDiscnt.setInstId(instId);
                    newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    newDiscnt.setSpecTag("0");
                    newDiscnt.setStartDate(SysDateMgr.getDateNextMonthFirstDay(endDate));
                    newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
                    newDiscnt.setRemark("宽带装机顺延收费优惠");
                    btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);

                }

            }
            discntTradeData.setRsrvStr2(instId);
            discntTradeData.setRsrvStr1(enableModeInfo.getData(0).getString("DISABLE_OFFSET"));
        }

    }

}
