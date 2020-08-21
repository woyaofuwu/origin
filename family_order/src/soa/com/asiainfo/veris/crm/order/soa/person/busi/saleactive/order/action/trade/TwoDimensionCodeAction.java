
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class TwoDimensionCodeAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
        {
            return;
        }

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
        {
            return;
        }

        if (saleActiveReqData.getPmds() == null || saleActiveReqData.getPmds().size() <= 0)
        {
            return;
        }

        UcaData uca = saleActiveReqData.getUca();
        String productId = saleActiveReqData.getProductId();
        String packageId = saleActiveReqData.getPackageId();

        // 手机支付电子券
        IDataset mobilePayConfigs = CommparaInfoQry.getCommparaInfoBy5("CSM", "386", productId, packageId, uca.getUserEparchyCode(), null);
        if (IDataUtil.isNotEmpty(mobilePayConfigs))
        {
            IData mobilePayConfig = mobilePayConfigs.getData(0);
//海南第三代订单中心改造 未用到 屏蔽            boolean hasPlatSvc = has54PlatSvc(uca);
            OtherTradeData other = new OtherTradeData();
            other.setUserId(uca.getUserId());
            other.setInstId(SeqMgr.getInstId());
            other.setRsrvValueCode("ACTIVE_DMS_SMS");
            other.setRsrvValue("10203414");
            other.setRsrvStr1("10203414");
            other.setOperCode("06");
            other.setRsrvStr2(mobilePayConfig.getString("PARA_CODE2", ""));//活动编号，建立电子卷活动的活动号，读commpara 386的配置，other插RSRV_STR2，IBOSS对应MD10_SYSTEMID，规范中对应actId
            other.setRsrvStr3(mobilePayConfig.getString("PARA_CODE3", ""));//卷别类型，电子券的类型，读commpara 386的配置，other插RSRV_STR3，IBOSS对应MD10_ISSPID，规范中对应prdId
            other.setRsrvStr4(uca.getSerialNumber());//手机号码，other插RSRV_STR4，规范中对应mobileId
            other.setRsrvStr5(mobilePayConfig.getString("PARA_CODE5", ""));//电子卷金额，单位：分，读commpara 386的配置，other插RSRV_STR5，IBOSS对应MD10_SUBJECT，规范中对应amount
            other.setRsrvStr7(btd.getTradeId());//by songlm@20141011 电子券编码，相当于一个订单编码，不重复唯一的14位数字编码。所以直接插入了tradeId。  REQ201402190004 新增手机支付电子券联机发放接口需求。other插RSRV_STR7，IBOSS对应MD10_ACTIVITYID，规范中对应ttNum
            other.setRsrvStr8(mobilePayConfig.getString("PARA_CODE8", ""));//貌似没有用到
            other.setRsrvStr9("5001");//接口功能码，other插RSRV_STR9，IBOSS对应MD10_ORGTIMES，规范中对应funCode
            other.setRsrvStr10(mobilePayConfig.getString("PARA_CODE10", ""));//商户号码，读commpara 386的配置，other插RSRV_STR10，IBOSS对应MD10_ORGAMT，规范中对应merId
            other.setRsrvStr11(SysDateMgr.getSysDateYYYYMMDD());//商户日期，other插RSRV_STR11，到IBOSS对应MD10_BMPFLAG，规范中对应merDate
            other.setRsrvStr12(SysDateMgr.getSysDateYYYYMMDD());//发放日期，other插RSRV_STR12，到IBOSS对应MD10_PRINTTEXT，规范中股对应ttDate
            //other.setRsrvStr14(hasPlatSvc ? "1" : "0");
            other.setRsrvStr14("1");//by songlm@20141024 麦通邮件并抄送给局方刘海云、亚信李贤敏、宋立明，提出修改该值为固定值1。  other插RSRV_STR14，到IBOSS对应MD10_PINMD5，规范中对应opType
            other.setStartDate(btd.getRD().getAcceptTime());
            other.setEndDate(SysDateMgr.getTheLastTime());
            other.setStaffId(CSBizBean.getVisit().getStaffId());
            other.setDepartId(CSBizBean.getVisit().getDepartId());
            other.setModifyTag(BofConst.MODIFY_TAG_ADD);
            other.setRemark("活动发送手机支付电子券信息");
            btd.add(uca.getSerialNumber(), other);
        }

        // E拇指平台发送二维码的处理
        IDataset ePollexDemensionConfigs = CommparaInfoQry.getCommparaInfoBy5("CSM", "186", productId, packageId, uca.getUserEparchyCode(), null);
        if (IDataUtil.isNotEmpty(ePollexDemensionConfigs))
        {
            IData ePollexDemensionConfig = ePollexDemensionConfigs.getData(0);
            OtherTradeData other = new OtherTradeData();
            other.setUserId(uca.getUserId());
            other.setInstId(SeqMgr.getInstId());
            other.setRsrvValueCode("ACTIVE_DMS_SMS");
            other.setRsrvValue("10203412");
            other.setRsrvStr1("10203412");
            other.setOperCode("06");
            other.setRsrvStr2(ePollexDemensionConfig.getString("PARA_CODE2", ""));
            other.setRsrvStr3(ePollexDemensionConfig.getString("PARA_CODE3", ""));
            other.setRsrvStr4(ePollexDemensionConfig.getString("PARA_CODE4", ""));
            other.setRsrvStr5(ePollexDemensionConfig.getString("PARA_CODE5", ""));
            other.setRsrvStr6(ePollexDemensionConfig.getString("PARA_CODE6", ""));
            other.setRsrvStr7(ePollexDemensionConfig.getString("PARA_CODE7", ""));
            other.setRsrvStr8(ePollexDemensionConfig.getString("PARA_CODE8", ""));
            other.setRsrvStr9(ePollexDemensionConfig.getString("PARA_CODE9", ""));
            other.setRsrvStr10(ePollexDemensionConfig.getString("PARA_CODE10", ""));
            other.setRsrvStr11(ePollexDemensionConfig.getString("PARA_CODE11", ""));
            other.setStartDate(btd.getRD().getAcceptTime());
            other.setEndDate(SysDateMgr.getTheLastTime());
            other.setStaffId(CSBizBean.getVisit().getStaffId());
            other.setDepartId(CSBizBean.getVisit().getDepartId());
            other.setModifyTag(BofConst.MODIFY_TAG_ADD);
            other.setRemark("活动发送E拇指平台二维码信息");
            btd.add(uca.getSerialNumber(), other);
        }
        
        // REQ201408150009 关于开发大客户生日积分欢乐送服务的需求
        List<SaleGoodsTradeData> SaleGoodsTdList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
        
        IDataset VipBirthdayConfigs = CommparaInfoQry.getCommparaInfoBy5("CSM", "187", productId, packageId, uca.getUserEparchyCode(), null);
        if (IDataUtil.isNotEmpty(VipBirthdayConfigs))
        {
            OtherTradeData other = new OtherTradeData();
            other.setUserId(uca.getUserId());
            other.setInstId(SeqMgr.getInstId());
            other.setRsrvValueCode("ACTIVE_DMS_SMS");
            other.setRsrvValue("10203416");
            other.setRsrvStr1("10203416");
            other.setOperCode("06");
            if(SaleGoodsTdList != null && SaleGoodsTdList.size() > 0){
            	other.setRsrvStr7(SaleGoodsTdList.get(0).getResCode());
        	}
            other.setStartDate(btd.getRD().getAcceptTime());
            other.setEndDate(SysDateMgr.getTheLastTime());
            other.setStaffId(CSBizBean.getVisit().getStaffId());
            other.setDepartId(CSBizBean.getVisit().getDepartId());
            other.setModifyTag(BofConst.MODIFY_TAG_ADD);
            other.setRemark("活动发送E拇指平台二维码信息");
            btd.add(uca.getSerialNumber(), other);
        }
    }

    private boolean has54PlatSvc(UcaData uca) throws Exception
    {
        List<PlatSvcTradeData> platSvcTradeDataList = uca.getUserPlatSvcs();
        if (CollectionUtils.isEmpty(platSvcTradeDataList))
        {
            return false;
        }

        for (PlatSvcTradeData platSvcTradeData : platSvcTradeDataList)
        {
            String serviceId = platSvcTradeData.getElementId();
            IData svcData = PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
            String _bizTypeCode = svcData.getString("BIZ_TYPE_CODE");
            if ("54".equals(_bizTypeCode))
            {
                return true;
            }
        }
        return false;
    }

}
