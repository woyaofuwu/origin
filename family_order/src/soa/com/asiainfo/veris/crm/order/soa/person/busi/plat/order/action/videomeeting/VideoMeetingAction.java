
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.videomeeting;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * 视频会议的预约和取消预约时， 数据保存到TF_B_TRADE_OTHER表
 * 
 * @author xiekl
 */
public class VideoMeetingAction implements IProductModuleAction
{

    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();

        if (PlatConstants.OPER_BOOKING.equals(pstd.getOperCode()) || PlatConstants.OPER_CANCEL_BOOKING.equals(pstd.getOperCode()))
        {
            // 拼Trade Other台账
            stringOtherTrade(btd, pstd, uca);
            // 将属性都删除
            pstd.setAttrTradeDatas(new ArrayList<AttrTradeData>());
            psd.setAttrs(new ArrayList<AttrData>());
        }
    }

    /**
     * 获取属性值
     * 
     * @param attrCode
     * @param attrs
     * @return
     */
    private String getAttrValue(String attrCode, List<AttrData> attrs)
    {

        for (int i = 0; i < attrs.size(); i++)
        {
            AttrData attr = attrs.get(i);
            String attrValue = attr.getAttrValue();
            if (attrCode.equals(attr.getAttrCode()) && (attrValue != null && !"".equals(attrValue)))
            {
                return attrValue;
            }
        }
        return "";
    }

    private void stringOtherTrade(BusiTradeData btd, PlatSvcTradeData pstd, UcaData uca) throws Exception
    {
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();
        List<AttrData> attrList = psd.getAttrs();

        if (PlatConstants.OPER_BOOKING.equals(pstd.getOperCode()))
        {
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setRsrvValueCode("V2CP");
            otherTradeData.setRsrvValue(PlatConstants.PLAT_VIDEO_MEETING);
            otherTradeData.setRsrvStr1(this.getAttrValue("CONF_MODE", attrList));
            otherTradeData.setRsrvStr2(this.getAttrValue("CONF_TYPE", attrList));
            otherTradeData.setRsrvStr3(this.getAttrValue("MAXNUM", attrList));
            otherTradeData.setRsrvStr4(this.getAttrValue("MAXSPEAKERNUM", attrList));
            otherTradeData.setRsrvStr5(this.getAttrValue("START_TIME", attrList));
            otherTradeData.setRsrvStr6(this.getAttrValue("END_TIME", attrList));
            otherTradeData.setRsrvStr7(this.getAttrValue("CONF_PWD", attrList));
            otherTradeData.setRsrvStr8(this.getAttrValue("COMPERE_PWD", attrList));
            otherTradeData.setRsrvStr9(psd.getElementId());

            // 非IBOSS渠道预约
            if (!CSBizBean.getVisit().getInModeCode().equals("6"))
            {
                otherTradeData.setRsrvStr10(SeqMgr.getTradeId());

            }
            else
            // 一级boss渠道预约
            {
                otherTradeData.setRsrvStr10(this.getAttrValue("CONF_ID", attrList));
            }
            otherTradeData.setRsrvStr11(pstd.getOperCode());
            otherTradeData.setRsrvStr30(this.getAttrValue("CONF_CONT", attrList));
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setRemark("视频会议预约");
            otherTradeData.setStartDate(pstd.getStartDate());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);

            btd.add(btd.getRD().getUca().getUser().getSerialNumber(), otherTradeData);
        }
        else if (PlatConstants.OPER_CANCEL_BOOKING.equals(pstd.getOperCode()))
        {
            String confId = this.getAttrValue("CONF_ID", attrList);
            if ("".equals(confId))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "视频会议的CONF_ID为空！");
            }

            IDataset meetings = UserOtherInfoQry.queryUserBookingMeeting(uca.getUserId(), confId, PlatConstants.OPER_BOOKING);
            IData cancelMeeting = null;

            if (IDataUtil.isEmpty(meetings))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "没有查到改视频会议预约资料！");
            }

            cancelMeeting = meetings.getData(0);

            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(cancelMeeting.getString("INST_ID", ""));
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setRsrvValueCode("V2CP");
            otherTradeData.setRsrvValue(PlatConstants.PLAT_VIDEO_MEETING);
            otherTradeData.setRsrvStr1(cancelMeeting.getString("RSRV_STR1", ""));
            otherTradeData.setRsrvStr2(cancelMeeting.getString("RSRV_STR2", ""));
            otherTradeData.setRsrvStr3(cancelMeeting.getString("RSRV_STR3", ""));
            otherTradeData.setRsrvStr4(cancelMeeting.getString("RSRV_STR4", ""));
            otherTradeData.setRsrvStr5(cancelMeeting.getString("RSRV_STR5", ""));
            otherTradeData.setRsrvStr6(cancelMeeting.getString("RSRV_STR6", ""));
            otherTradeData.setRsrvStr7(cancelMeeting.getString("RSRV_STR7", ""));
            otherTradeData.setRsrvStr8(cancelMeeting.getString("RSRV_STR8", ""));
            otherTradeData.setRsrvStr9(cancelMeeting.getString("RSRV_STR9", ""));
            otherTradeData.setRsrvStr10(confId);
            otherTradeData.setRsrvStr11(pstd.getOperCode());
            otherTradeData.setRsrvStr30(cancelMeeting.getString("RSRV_STR30", ""));
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            otherTradeData.setRemark("视频会议预约取消");
            otherTradeData.setStartDate(cancelMeeting.getString("START_DATE"));
            otherTradeData.setEndDate(SysDateMgr.getSysTime());

            btd.add(btd.getRD().getUca().getUser().getSerialNumber(), otherTradeData);
        }

    }

}
