
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.requestdata.WidenetProductRequestData;

public class WidenetDiscntBindDiscntAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        WidenetProductRequestData request = (WidenetProductRequestData) btd.getRD();
        String productId = "";
        if (request.getNewMainProduct() != null)
        {
            request.getNewMainProduct().getProductId();
        }
        else
        {
            productId = btd.getRD().getUca().getProductId();
        }
        IDataset commInfo2s = CommparaInfoQry.getCommpara("CSM", "601", productId, CSBizBean.getTradeEparchyCode());
        int size = discntTradeDatas.size();
        for (int i = 0; i < size; i++)
        {
            DiscntTradeData discntTradeData = discntTradeDatas.get(i);
            if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
            {
                IDataset commInfo1s = CommparaInfoQry.getCommparaInfoByCode3("CSM", "602", discntTradeData.getElementId(), productId, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(commInfo1s))
                {
                    DiscntTradeData newDiscnt = new DiscntTradeData();
                    newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                    newDiscnt.setUserIdA("-1");
                    newDiscnt.setProductId(productId);
                    newDiscnt.setPackageId(commInfo1s.getData(0).getString("PARA_CODE2"));
                    newDiscnt.setElementId(commInfo1s.getData(0).getString("PARA_CODE1"));
                    newDiscnt.setInstId(SeqMgr.getInstId());
                    newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    newDiscnt.setSpecTag("0");
                    newDiscnt.setStartDate(SysDateMgr.getNextSecond(discntTradeData.getEndDate()));
                    newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
                    newDiscnt.setRemark("宽带产品变更");
                    btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);

                }
                else
                {
                    if (IDataUtil.isNotEmpty(commInfo2s))
                    {
                        DiscntTradeData newDiscnt = new DiscntTradeData();
                        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                        newDiscnt.setUserIdA("-1");
                        newDiscnt.setProductId(productId);
                        newDiscnt.setPackageId(commInfo2s.getData(0).getString("PARA_CODE2"));
                        newDiscnt.setElementId(commInfo2s.getData(0).getString("PARA_CODE1"));
                        newDiscnt.setInstId(SeqMgr.getInstId());
                        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        newDiscnt.setSpecTag("0");
                        newDiscnt.setStartDate(SysDateMgr.getNextSecond(discntTradeData.getEndDate()));
                        newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
                        newDiscnt.setRemark("宽带产品变更");
                        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
                    }
                }

            }

        }

    }

}
