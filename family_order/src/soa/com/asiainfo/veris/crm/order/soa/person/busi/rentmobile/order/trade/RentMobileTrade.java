
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RentTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.requestdata.RentMobileReqData;

public class RentMobileTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        RentMobileReqData reqData = (RentMobileReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String rentTag = reqData.getRentTag();
        IDataset results = UserResInfoQry.queryUserSimInfo(uca.getUserId(), "1");
        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(ResException.CRM_RES_42);
        }

        if ("0".equals(rentTag))// 退机
        {
            IDataset userRent = UserRentInfoQry.queryUserRentInfo(uca.getUserId(), reqData.getRentSerialNumber());
            RentTradeData rentTradeData = new RentTradeData(userRent.getData(0));

            rentTradeData.setRsrvStr2(reqData.getMoney());
            rentTradeData.setRentTag(rentTag);
            rentTradeData.setEndDate(reqData.getAcceptTime());
            rentTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(uca.getSerialNumber(), rentTradeData);

            // 拼装主台账
            MainTradeData mainTradeData = bd.getMainTradeData();
            mainTradeData.setRsrvStr4(rentTag);
        }
        else if ("1".equals(rentTag))// 租机
        {
            RentTradeData rentTradeData = new RentTradeData();
            rentTradeData.setUserId(uca.getUserId());
            rentTradeData.setRentSerialNumber(reqData.getRentSerialNumber());// 租机号码
            rentTradeData.setRsrvStr1(reqData.getFeeSerialNumber());// 国际计费号码
            rentTradeData.setNationalityAreacode(reqData.getAreacode());// 国际区号
//            rentTradeData.setRsrvStr2(reqData.getMoney());
            rentTradeData.setRentImei(reqData.getRentImei());// 租机IMEI
            rentTradeData.setSerialNumber(uca.getSerialNumber());
            rentTradeData.setRentDeviceNo("");
            rentTradeData.setSimCardNo(results.getData(0).getString("RES_CODE"));
            rentTradeData.setImsi(results.getData(0).getString("IMSI"));
            rentTradeData.setRentTag(rentTag);
            rentTradeData.setRsrvStr2(reqData.getRentModeCode());
            rentTradeData.setRsrvDate1(reqData.getStartDate() + StringUtils.substring(reqData.getAcceptTime(), 10, 19));// 结算时间
            rentTradeData.setInstId(SeqMgr.getInstId());

            rentTradeData.setStartDate(reqData.getStartDate() + StringUtils.substring(reqData.getAcceptTime(), 10, 19));
            rentTradeData.setRentTypeCode(reqData.getRentTypeCode());
            rentTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            rentTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            bd.add(uca.getSerialNumber(), rentTradeData);

            // 拼装主台账
            MainTradeData mainTradeData = bd.getMainTradeData();
            mainTradeData.setInvoiceNo(reqData.getInvoiceNo());
            mainTradeData.setRsrvStr1(reqData.getRentModeCode());
            mainTradeData.setRsrvStr2(reqData.getStartDate() + StringUtils.substring(reqData.getAcceptTime(), 10, 19));
            mainTradeData.setRsrvStr4(rentTag);
            mainTradeData.setRsrvStr8(results.getData(0).getString("RES_CODE"));
            mainTradeData.setRsrvStr9(results.getData(0).getString("IMSI"));
        }
    }
}
