
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.sql.Timestamp;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bre.svc.BreEngine;

public class PlatSvcTrade extends BaseProductModuleTrade implements IProductModuleTrade
{

    public static final void dealFirstTime(PlatSvcTradeData pstd, UcaData uca) throws Exception
    {
        String userId = pstd.getUserId();
        String serviceId = pstd.getElementId();
        String firstDate = "";
        String firstMonthDate = "";

        // 查询首次订购
        IDataset firstTimeDatass = BofQuery.getUserPlatSvcFirstDate(userId, serviceId, uca.getUserEparchyCode());
        if (firstTimeDatass != null && firstTimeDatass.size() > 0)
        {
            IData firstData = firstTimeDatass.getData(0);
            firstDate = firstData.getString("FIRST_DATE", "");
            if (!"".equals(firstDate))
            {
                pstd.setFirstDate(firstDate);
            }
            else
            {
                pstd.setFirstDate(pstd.getStartDate());
            }
        }
        else
        {
            pstd.setFirstDate(pstd.getStartDate());
        }

        // 查询本月首次订购
        IDataset firstMonthDatas = BofQuery.getUserPlatSvcFirstDateMon(userId, serviceId, uca.getUserEparchyCode());
        if (firstMonthDatas != null && firstMonthDatas.size() > 0)
        {
            IData firstData = firstMonthDatas.getData(0);
            firstMonthDate = firstData.getString("FIRST_DATE_MON", "");
            if (!"".equals(firstMonthDate))
            {
                pstd.setFirstDateMon(firstMonthDate);
            }
            else
            {
                pstd.setFirstDateMon(pstd.getStartDate());
            }
        }
        else
        {
            pstd.setFirstDateMon(pstd.getStartDate());
        }
    }

    /**
     * 拼装tf_b_trade_platsvc及tf_b_trade_platsvc_attr表的基本数据
     * 
     * @param pd
     * @param prd
     * @param uca
     * @return
     * @throws Exception
     */
    public final PlatSvcTradeData buildBasicPlatTradeData(PlatSvcData psd, UcaData uca, BaseReqData brd) throws Exception
    {
        PlatSvcTradeData platSvcTradeData = new PlatSvcTradeData();
        if (BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag()))
        {
            // 订购服务
            platSvcTradeData.setModifyTag(psd.getModifyTag());
            platSvcTradeData.setUserId(uca.getUserId());
            String bizStateCode = this.operCodeParserState(psd.getOperCode());
            platSvcTradeData.setBizStateCode(bizStateCode);
            platSvcTradeData.setProductId(psd.getProductId());
            platSvcTradeData.setPackageId(psd.getPackageId());
            if (StringUtils.isNotBlank(psd.getStartDate()))
            {
                platSvcTradeData.setStartDate(this.transferTime(psd.getStartDate()));
            }
            else
            {
                platSvcTradeData.setStartDate(brd.getAcceptTime());
            }

            if (psd.isScoreChange())
            {
                platSvcTradeData.setEndDate(psd.getScoreChangeTime());
            }
            else if (StringUtils.isNotBlank(psd.getEndDate()))
            {
                platSvcTradeData.setEndDate(psd.getEndDate());
            }
            else
            {
                platSvcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            }
            platSvcTradeData.setInCardNo(psd.getInCardNo());
            platSvcTradeData.setEntityCardNo(psd.getEntityCardNo());
            platSvcTradeData.setGiftSerialNumber(psd.getGiftSerialNumber());
            platSvcTradeData.setGiftUserId(psd.getGiftUserId());
            String instId = SeqMgr.getInstId();
            platSvcTradeData.setInstId(instId);
            platSvcTradeData.setRemark(psd.getRemark());
        }
        else if (BofConst.MODIFY_TAG_DEL.equals(psd.getModifyTag()))
        {
            // 退订服务
            PlatSvcTradeData pstd = null;
            if (StringUtils.isBlank(psd.getInstId()))
            {
                List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
                if (pstds.size() > 0)
                {
                    pstd = pstds.get(0);
                }
            }
            else
            {
                pstd = uca.getUserPlatSvcByInstId(psd.getInstId());
            }
            if (pstd == null)
            {
                // 抛错
                CSAppException.apperr(PlatException.CRM_PLAT_0913);
            }
            platSvcTradeData = pstd.clone();
            platSvcTradeData.setBizStateCode(PlatConstants.STATE_CANCEL);
            platSvcTradeData.setModifyTag(psd.getModifyTag());
            platSvcTradeData.setEndDate(brd.getAcceptTime());
        }
        else if (BofConst.MODIFY_TAG_UPD.equals(psd.getModifyTag()))
        {
            // 暂停恢复服务
            PlatSvcTradeData pstd = null;
            if (StringUtils.isBlank(psd.getInstId()))
            {
                List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
                if (pstds.size() > 0)
                {
                    pstd = pstds.get(0);
                }
            }
            else
            {
                pstd = uca.getUserPlatSvcByInstId(psd.getInstId());
            }
            if (pstd == null)
            {
                // 抛错
                CSAppException.apperr(PlatException.CRM_PLAT_0913);
            }
            platSvcTradeData = pstd.clone();
            if (PlatConstants.OPER_PAUSE.equals(psd.getOperCode()))
            {
                platSvcTradeData.setBizStateCode(PlatConstants.STATE_PAUSE);
            }
            else if (PlatConstants.OPER_RESTORE.equals(psd.getOperCode()) || PlatConstants.OPER_UNLOSE.equals(psd.getOperCode()))
            {
                platSvcTradeData.setBizStateCode(PlatConstants.STATE_OK);
            }
            else if (PlatConstants.OPER_LOSE.equals(psd.getOperCode()))
            {
                platSvcTradeData.setBizStateCode(PlatConstants.STATE_LOSE);
            }
            else if (PlatConstants.OPER_CONTINUE_ORDER.equals(psd.getOperCode()))
            {
                platSvcTradeData.setEndDate(psd.getEndDate());
                platSvcTradeData.setEntityCardNo(psd.getEntityCardNo());
            }
            platSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        }

        if (StringUtils.isBlank(psd.getActiveTag()))
        {
            platSvcTradeData.setActiveTag("0");// 主动
        }
        else
        {
            platSvcTradeData.setActiveTag(psd.getActiveTag());// 主被动标记
        }
        platSvcTradeData.setAllTag(psd.getAllTag());
        platSvcTradeData.setUserId(uca.getUserId());
        platSvcTradeData.setElementId(psd.getElementId());
        platSvcTradeData.setOperTime(brd.getAcceptTime());
        platSvcTradeData.setPkgSeq(psd.getPkgSeq());// 批次号，批量业务时传值
        platSvcTradeData.setUdsum(psd.getUdSum());// 批次数量
        platSvcTradeData.setIntfTradeId(psd.getIntfTradeId());
        platSvcTradeData.setOperCode(psd.getOperCode());
        platSvcTradeData.setOprSource(psd.getOprSource());
        platSvcTradeData.setRemark(psd.getRemark());
        platSvcTradeData.setRsrvStr8(psd.getRsrvStr8()); 
        platSvcTradeData.setRsrvStr10(psd.getRsrvStr10());
        platSvcTradeData.setRsrvStr9(psd.getRsrvStr9());
        platSvcTradeData.setRsrvStr7(psd.getRsrvStr7());

        return platSvcTradeData;
    }

    /**
     * 拼装出平台服务的元素台帐数据
     */
    @Override
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData pmd, List<ProductModuleData> pmds, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        PlatSvcData psd = (PlatSvcData) pmd;

        IData databus = new DataMap();
        databus.put("ACTION_TYPE", "PlatCheckBefore");
        // databus.put("ACTION_TYPE", "TradeCheckAfter");
        databus.put("TRADE_TYPE_CODE", brd.getTradeType().getTradeTypeCode());
        databus.put("ORDER_TYPE_CODE", brd.getOrderTypeCode());
        databus.put("BIZ_TYPE_CODE", psd.getOfficeData().getBizTypeCode());
        databus.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        databus.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        databus.put("PRODUCT_ID", "-1");
        databus.put("BRAND_CODE", "ZZZZ");
        databus.put("EPARCHY_CODE", "ZZZZ");
        databus.put("SERVICE_ID", psd.getElementId());
        databus.put("SP_CODE", psd.getOfficeData().getSpCode());
        databus.put("BIZ_CODE", psd.getOfficeData().getBizCode());
        databus.put(PlatConstants.TF_B_TRADE_PLATSVC, PlatConstants.TF_B_TRADE_PLATSVC);// 标识为平台业务
        databus.put(PlatConstants.RULE_UCA, uca);
        databus.put(PlatConstants.RULE_PLATSVC, psd);
        databus.put("RULE_PLATSVC_All", pmds);

        IData result = BreEngine.bre4SuperLimit(databus);
        if (IDataUtil.isNotEmpty(result))
        {
            CSAppException.breerr(result);
        }

        PlatSvcTradeData pstd = this.buildBasicPlatTradeData(psd, uca, brd);
        dealFirstTime(pstd, uca);
        this.dealIsNeedPf(pstd, psd, uca, brd);
        List<AttrTradeData> attrs = pstd.getAttrTradeDatas();
        if (psd.getAttrs() != null && psd.getAttrs().size() > 0 && (attrs == null || attrs.size() <= 0))
        {
            AttrTrade.createAttrTradeData(pstd, psd.getAttrs(), uca);
        }
        return pstd;
    }

    public void dealIsNeedPf(PlatSvcTradeData pstd, PlatSvcData psd, UcaData uca, BaseReqData req) throws Exception
    {
        // 某些特殊业务，通过KIND_ID,或者入参判断是否需要走服开
        if (StringUtils.isNotBlank(psd.getIsNeedPf()))
        {
            pstd.setIsNeedPf(psd.getIsNeedPf());
            return;
        }

        IDataset result = BofQuery.getPlatSvcPfRule(pstd.getElementId(), psd.getOfficeData().getOrgDomain(), psd.getOfficeData().getBizTypeCode(), psd.getOperCode(), psd.getOprSource());
        if (result != null && result.size() > 0)
        {
            int size = result.size();
            if (size > 1)
            {
                pstd.setIsNeedPf("1");// 如果查出来两条，则默认走服务开通
            }
            else
            {
                // 只查出一条
                String isNeedPf = result.getData(0).getString("X_RESULTOBJ");
                String ruleCode1 = result.getData(0).getString("RSRV_STR1", "");

                // 从一级boss过来的，先发二次确认短信，再订购的需要发服开
                if ("".equals(ruleCode1) || (!"".equals(ruleCode1) && "true".equals(req.getIsConfirm())))
                {
                    pstd.setIsNeedPf(isNeedPf);
                }
                else
                {
                    pstd.setIsNeedPf("0");// 不走服务开通
                }
            }
        }
        else
        {
            // 没有查到配置数据，则默认走服务开通
            pstd.setIsNeedPf("1");
        }
    }

    /**
     * 根据操作码转换成业务状态
     * 
     * @param operCode
     *            操作码
     * @return
     */
    private String operCodeParserState(String operCode)
    {

        String stateCode = "";
        if (operCode.equals(PlatConstants.OPER_ORDER) || operCode.equals(PlatConstants.OPER_RESTORE) || operCode.equals(PlatConstants.OPER_UNLOSE))
        {
            stateCode = PlatConstants.STATE_OK;// 正常使用
        }
        else if (operCode.equals(PlatConstants.OPER_PAUSE))
        {
            stateCode = PlatConstants.STATE_PAUSE;// 暂停
        }
        else if (operCode.equals(PlatConstants.OPER_CANCEL_ORDER))
        {
            stateCode = PlatConstants.STATE_CANCEL;// 终止
        }
        else if (operCode.equals(PlatConstants.OPER_LOSE))
        {
            stateCode = PlatConstants.STATE_LOSE;// 挂失
        }
        else
        {
            stateCode = PlatConstants.STATE_OK;
        }
        return stateCode;
    }

    private String transferTime(String timeStr) throws Exception
    {
        // 针对一级boss时间的处理
        String startDate = null;
        Timestamp timestamp = null;

        // 对电渠的特殊处理
        if (timeStr.length() == 10)
        {
            try
            {
                // 开始时间小于当前时间，用当前时间
                if (timeStr.compareTo(SysDateMgr.getSysTime()) <= 0)
                {
                    return SysDateMgr.getSysTime();
                }
                else
                {
                    return timeStr;
                }

            }
            catch (Exception e)
            {
                return SysDateMgr.getSysTime();
            }

        }
        else if (timeStr.length() == 14)
        {
            try
            {
                timestamp = SysDateMgr.encodeTimestamp("yyyyMMddHHmmss", timeStr);
                startDate = DateFormatUtils.format(timestamp.getTime(), "yyyy-MM-dd HH:mm:ss");
            }
            catch (Exception e)
            {

            }
            if (startDate != null)
            {
                return startDate;
            }
            else
            {
                return SysDateMgr.getSysDate();
            }

        }
        else
        {
            try
            {
                timestamp = SysDateMgr.encodeTimestamp(timeStr);
                startDate = DateFormatUtils.format(timestamp.getTime(), "yyyy-MM-dd HH:mm:ss");
            }
            catch (Exception e)
            {

            }

            if (startDate != null)
            {
                return startDate;
            }
            else
            {
                return SysDateMgr.getSysDate();
            }
        }
    }

}
