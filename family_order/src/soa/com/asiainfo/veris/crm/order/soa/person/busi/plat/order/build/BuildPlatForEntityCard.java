
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatForEntityCard extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        PlatReqData req = (PlatReqData) brd;
        UcaData uca = req.getUca();
        String cardNo = param.getString("CARD_NO", "");
        String spCode = param.getString("SP_CODE");
        String bizCode = param.getString("BIZ_CODE");
        String startDate = param.getString("START_DATE");
        if (startDate.length() == 8)
        {
            startDate = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            if (startDate.equals(brd.getAcceptTime().substring(0, 10)))
            {
                startDate = startDate + brd.getAcceptTime().substring(10);
            }
        }
        else
        {
            startDate = SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND);
        }
        String operCode = param.getString("OPER_CODE");
        String months = "1";// 默认有效期一个月，需要调用资源接口进行查询
        IDataset entityList = ResCall.queryEntityCardInfo(cardNo, cardNo);
        if (entityList != null && !entityList.isEmpty())
        {
            IData entityCard = entityList.getData(0);
            String packageCode = entityCard.getString("PACKAGE_CODE");
            months = PlatInfoQry.getEntityCardLimitCount(packageCode);
        }

        if (Integer.parseInt(startDate.substring(8, 10)) > 20)
        {
            if (!PlatConstants.OPER_CONTINUE_ORDER.equals(operCode))
            {
                months = (Integer.parseInt(months) + 1) + "";
            }
        }
        String endDate = SysDateMgr.endDate(startDate, "1", "", months, "2");
        PlatSvcData psd = new PlatSvcData();
        PlatOfficeData officeData = PlatOfficeData.getInstance(null, spCode, bizCode);
        psd.setOfficeData(officeData);
        psd.setElementId(officeData.getServiceId());
        psd.setOperCode(operCode);
        psd.setOprSource("64");// 实体卡渠道
        psd.setActiveTag("0");
        if (PlatConstants.OPER_CONTINUE_ORDER.equals(operCode))
        {
            List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(officeData.getServiceId());
            if (pstds == null || pstds.size() <= 0)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0913_3);
            }
            else
            {
                PlatSvcTradeData pstd = pstds.get(0);
                if (StringUtils.isBlank(pstd.getEntityCardNo()))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_0913_3);
                }
                // 只能续订一次的规则还是否需要？
                psd.setModifyTag(BofConst.MODIFY_TAG_UPD);
                psd.setInstId(pstd.getInstId());
                psd.setEndDate(endDate);
                psd.setEntityCardNo(cardNo);
            }
        }
        else if (PlatConstants.OPER_ORDER.equals(operCode))
        {
            List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(officeData.getServiceId());
            if (pstds != null && pstds.size() > 0)
            {
                PlatSvcTradeData pstd = pstds.get(0);
                if (!"50000000".equals(pstd.getProductId()))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_0912_9);
                }
                else if (StringUtils.isNotBlank(pstd.getEntityCardNo()))
                {
                    CSAppException.apperr(PlatException.CRM_PLAT_0912_10);
                }
                else
                {
                    // 发二次确认短信
                    // 保存请求对象
                    this.saveTwoCheckRequest(officeData.getServiceId(), officeData.getServiceId(), req, param);
                    req.setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);
                }
            }
            else
            {
                IDataset excludes = PlatInfoQry.getExcludeEntityCardService(officeData.getServiceId());
                if (IDataUtil.isNotEmpty(excludes))
                {
                    int size = excludes.size();
                    for (int i = 0; i < size; i++)
                    {
                        IData exclude = excludes.getData(i);
                        pstds = uca.getUserPlatSvcByServiceId(exclude.getString("EX_SERVICE_ID"));
                        if (pstds != null && pstds.size() > 0)
                        {
                            PlatSvcTradeData pstd = pstds.get(0);
                            if (!"50000000".equals(pstd.getProductId()))
                            {
                                CSAppException.apperr(PlatException.CRM_PLAT_0953_1);
                            }
                            else if (StringUtils.isNotBlank(pstd.getEntityCardNo()))
                            {
                                CSAppException.apperr(PlatException.CRM_PLAT_0953_2);
                            }
                            else
                            {
                                // 发二次确认短信
                                // 保存请求对象
                                this.saveTwoCheckRequest(exclude.getString("EX_SERVICE_ID"), officeData.getServiceId(), req, param);
                                req.setPreType(BofConst.PRE_TYPE_SMS_CONFIRM);
                            }
                        }
                    }
                }
            }
            psd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            psd.setStartDate(startDate);
            psd.setEndDate(endDate);
            psd.setEntityCardNo(cardNo);
        }
        else if (PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
        {
            psd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        }
        List<PlatSvcData> psds = new ArrayList<PlatSvcData>();
        psds.add(psd);
        req.setPlatSvcDatas(psds);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatReqData();
    }

    private void saveTwoCheckRequest(String cancelServiceId, String orderServiceId, BaseReqData req, IData inparam) throws Exception
    {
        IData inputData = new DataMap();

        // 特殊处理
        inputData.put("SERVICE_ID", "@_@" + cancelServiceId + "@_@" + orderServiceId);
        inputData.put("OPER_CODE", "@_@" + PlatConstants.OPER_CANCEL_ORDER + "@_@" + PlatConstants.OPER_ORDER);
        inputData.put("OPR_SOURCE", "64");

        inputData.put("PRE_TYPE", BofConst.ENTITY_CARD);
        inputData.put("SERIAL_NUMBER", req.getUca().getSerialNumber());
        inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inputData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inputData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inputData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inputData.put("SVC_NAME", "SS.PlatBatchRegIntfSVC.tradeReg");
        inputData.put("OPR_SOURCE", "64");
        inputData.put("ENTITY_CARD_NO", inparam.getString("CARD_NO"));

        IData twoCheckSms = new DataMap();
        twoCheckSms.put("TEMPLATE_ID", "");// 海南需要传TEMPLATE_ID
        twoCheckSms.put("SERIAL_NUMBER", req.getUca().getSerialNumber());
        twoCheckSms.put("SMS_CONTENT", "提醒服务：尊敬的客户，您好！您已普通订购该业务。回复“Y”则退订原业务，通过实体卡重新订购该业务；否则不处理。如有疑问，请咨询10086。中国移动");
        twoCheckSms.put("SMS_TYPE", BofConst.ENTITY_CARD);
        twoCheckSms.put("OPR_SOURCE", "1");
        TwoCheckSms.twoCheck("3700", 0, inputData, twoCheckSms);
    }

}
