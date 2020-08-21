
package com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.order.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GPRSDiscntChangeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.gprsdiscntchange.order.requestdata.GPRSDiscntChangeReqData;

public class GPRSDiscntChangeTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        createDiscntTradeData(bd);
        createSVCTradeData(bd);
    }

    private void createDiscntTradeData(BusiTradeData bd) throws Exception
    {
        GPRSDiscntChangeReqData gdcrd = (GPRSDiscntChangeReqData) bd.getRD();
        String startDate = SysDateMgr.getSysDate() + SysDateMgr.getFirstTime00000();
        String endDate = SysDateMgr.getYesterdayDate() + SysDateMgr.getEndTime235959();
        String strInstId = SeqMgr.getInstId();
        UcaData uca = gdcrd.getUca();
        String serialNumber = gdcrd.getUca().getSerialNumber();

        // 获取用户原有GPRS优惠,做删除
        List<DiscntTradeData> list = uca.getUserDiscnts();
        DiscntTradeData dtdNew = new DiscntTradeData();
        for (int i = 0, size = list.size(); i < size; i++)
        {
            DiscntTradeData dtd = list.get(i);
//            IDataset gprsDiscnts = DiscntInfoQry.queryDiscntsByDtype("5");
            IDataset gprsDiscnts= UPackageElementInfoQry.queryPackageElementsByProductIdDisctypeCode("","5");
            for (int j = 0, gsize = gprsDiscnts.size(); j < gsize; j++)
            {
                if (dtd.getElementId().equals(gprsDiscnts.get(j, "DISCNT_CODE")))
                {
                    DiscntTradeData dtdOld = dtd.clone();
                    dtdOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    dtdOld.setEndDate(endDate);
                    dtdNew = dtd.clone();
                    bd.add(serialNumber, dtdOld);
                }
            }
        }
        // 新增优惠
        if (StringUtils.isBlank(gdcrd.getNewDiscntCode()))
            CSAppException.apperr(GPRSDiscntChangeException.CRM_GPRSDISCNTCHANGE_5);
        dtdNew.setElementId(gdcrd.getNewDiscntCode());
        dtdNew.setInstId(strInstId);
        dtdNew.setStartDate(startDate);
        dtdNew.setEndDate(SysDateMgr.getTheLastTime());
        dtdNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(serialNumber, dtdNew);
    }

    private void createSVCTradeData(BusiTradeData bd) throws Exception
    {
        GPRSDiscntChangeReqData gdcrd = (GPRSDiscntChangeReqData) bd.getRD();
        String serialNumber = gdcrd.getUca().getSerialNumber();
        // 获取用户GPRS服务,将新资费写入RSRV_STR1
        List<SvcTradeData> gprsSvcInfos = gdcrd.getUca().getUserSvcBySvcId("22");
        for (int i = 0, size = gprsSvcInfos.size(); i < size; i++)
        {
            SvcTradeData svcTD = gprsSvcInfos.get(i).clone();
            svcTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
            svcTD.setRsrvStr1(gdcrd.getNewDiscntCode());
            bd.add(serialNumber, svcTD);
        }
    }

}
