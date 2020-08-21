
package com.asiainfo.veris.crm.order.soa.group.saleactive;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPtypeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MFileInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class SaleActiveBean extends GroupBean
{
    protected SaleActiveBeanReqData reqData = null;
    private String acctId = "";
    private String saleActiveInsId = "";
    
    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liuzz
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        childCheckBeforeTrade();
        childCheckBeforeTrade2();
        grpSaleActiveStockCheck();
        dealElements();
        decodeActive();
        regSaleActiveFiles();
    }

    protected void dealElements() throws Exception
    {
        IDataset elements = reqData.getSelectElements();

        for (int ielem = 0; ielem < elements.size(); ielem++)
        {
            // 元素类型 [S:服务, D:资费, R:资源, A:预存赠送, G:实物, J:积分, C:信誉度, Z:平台业务]
            IData element = elements.getData(ielem);

            String elemType = element.getString("ELEMENT_TYPE_CODE", "");
            String elementId = element.getString("ELEMENT_ID");

            // 集团规则校验
            IData inData = new DataMap();
            inData.put("PRODUCT_ID", reqData.getSaleProductId());
            inData.put("PACKAGE_ID", reqData.getPackageId());
            inData.put("GRP_PRODUCT_ID", reqData.getGrpProductId());
            inData.put("USER_ID", reqData.getUca().getUserId());
            inData.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
            inData.put("ELEMENT_ID", elementId);
            inData.put("ELEMENT_TYPE_CODE", elemType);
            inData.put("EVENT_TYPE", "CHKELEM");

            CheckSaleElement(inData);

            if (element.containsKey("ATTR_PARAM"))
            {
                IDataset attrs = element.getDataset("ATTR_PARAM");

                for (int i = 0; i < attrs.size(); i++)
                {
                    IData attr = attrs.getData(i);
                    String attrCode = attr.getString("ATTR_CODE");
                    if ("DISCNT_CODE".equals(attrCode) || "START_DATE".equals(attrCode) || "END_DATE".equals(attrCode) || "END_OFFSET".equals(attrCode))
                    {
                        attrs.remove(i);
                        i--;
                        continue;
                    }
                    String attrValue = attr.getString("ATTR_VALUE");
                    inData.put("ELEMENT_ID", attrCode);
                    inData.put("ELEMENT_TYPE_CODE", "M");
                    inData.put("ELEMENT_VALUE", attrValue);
                    CheckSaleElement(inData);
                }
            }

            // 集团只存在 A、D、Z，所以现在暂时只处理这几种类型
            if ("D".equals(elemType))
            {
                decodeSaleDiscnt(element);
                decodeSaleAcctDiscnt(element);
            }
            else if ("A".equals(elemType))
            {
                decodeDeposit(element);
            }
            else if ("G".equals(elemType))
            {
                decodeGoods(element);
            }
        }
    }

    protected IData CheckSaleElement(IData inData) throws Exception
    {
        String[] paramName =
        { "v_event_type", "v_eparchy_code", "v_city_code", "v_depart_id", "v_staff_id", "v_user_id", "v_deposit_gift_id", "v_target", "v_group_id", "v_user_id_a", "v_group_prod_id", "v_product_id", "v_package_id", "v_trade_id", "v_checkInfo",
                "v_resultcode", "v_resultinfo", "v_elem_id", "v_elem_type", "v_elem_value" };

        IData paramValue = new DataMap();
        paramValue.put("v_event_type", inData.getString("EVENT_TYPE", ""));
        paramValue.put("v_eparchy_code", CSBizBean.getVisit().getStaffEparchyCode());
        paramValue.put("v_depart_id", CSBizBean.getVisit().getDepartId());
        paramValue.put("v_staff_id", CSBizBean.getVisit().getStaffId());
        paramValue.put("v_city_code", CSBizBean.getVisit().getCityCode());
        paramValue.put("v_user_id", inData.getString("USER_ID", "-1"));
        paramValue.put("v_deposit_gift_id", "");
        paramValue.put("v_target", inData.getString("TARGET_TAG", "0"));
        paramValue.put("v_group_id", inData.getString("GROUP_ID", "-1"));
        paramValue.put("v_user_id_a", inData.getString("USER_ID", "-1"));
        paramValue.put("v_group_prod_id", inData.getString("GRP_PRODUCT_ID", "-1"));
        paramValue.put("v_product_id", inData.getString("PRODUCT_ID", "-1"));
        paramValue.put("v_package_id", inData.getString("PACKAGE_ID", "-1"));
        paramValue.put("v_trade_id", getTradeId());
        paramValue.put("v_checkInfo", "");
        paramValue.put("v_resultcode", "");
        paramValue.put("v_resultinfo", "");
        paramValue.put("v_elem_id", inData.getString("ELEMENT_ID", "-1"));
        paramValue.put("v_elem_type", inData.getString("ELEMENT_TYPE_CODE", "-1"));
        paramValue.put("v_elem_value", inData.getString("ELEMENT_VALUE", "-1"));
        Dao.callProc("p_csm_CheckForGrpSaleActive", paramName, paramValue);

        if (!"0".equals(paramValue.getString("v_resultcode", "0")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, paramValue.getString("v_resultinfo", ""));
        }
        return paramValue;

    }

    /**
     * checkBefore
     * 
     * @author liuzz
     * @date 2014-8-12
     */
    public void childCheckBeforeTrade() throws Exception
    {

        String salePrdId = this.reqData.getSaleProductId();
        String pacakgeId = reqData.getPackageId();
        String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
        
        //add by chenzg@20170821 提交业务时检验至少要交预存10万元
        if("69901004".equals(salePrdId)){
        	List<FeeData> feeList = this.reqData.getFeeList();
        	if(feeList!=null && feeList.size()>0){
        		for (FeeData feeData : feeList) {
					if("2".equals(feeData.getFeeMode()) && "9030".equals(feeData.getFeeTypeCode())){
						//实缴的预存款是否小于10万（单位：分）
						if(Long.valueOf(feeData.getFee())<10000000){
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "后向流量预存有礼营销活动预存款不能小于10万");
						}
					}
				}
        	}
        }

        IDataset bindPackageIds = ParamInfoQry.getCommparaByCode("CSM", "783", pacakgeId, eparchyCode);
        if (IDataUtil.isEmpty(bindPackageIds))
        {
            return;
        }

        IDataset results = PkgExtInfoQry.queryPackageExtInfo(pacakgeId, eparchyCode);
        if (IDataUtil.isEmpty(results))
        {
            return;
        }
        String rsrvStr25 = results.getData(0).getString("RSRV_STR25", "0");
        int rsrvInt25 = 0;
        if (rsrvStr25 != null)
        {
            rsrvInt25 = Integer.parseInt(rsrvStr25);
        }

        String endcycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-1, SysDateMgr.getSysTime()), "yyyyMM");
        String startcycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(-3, SysDateMgr.getSysTime()), "yyyyMM");
        // 查询用户前三个月消费情况
        IDataset chkAcctInfos = AcctCall.querySomeMonthFee(reqData.getUca().getUserId(), startcycle, endcycle);
        if (IDataUtil.isEmpty(chkAcctInfos))
        {
            return;
        }
        int retInt = chkAcctInfos.getData(0).getInt("ALL_RETURN_FEE", 0); // 单位：分
        if (retInt > 0)
        {
            int averageInt = retInt / 3;
            if (averageInt < rsrvInt25)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103); // 该集团产品前三个月的消费平均值低于约定消费的金额，不能办理该活动！
            }
        }
        
    }

    /**
     * checkBefore
     * 
     * @author liuzz
     * @date 2014-8-12
     */
    public void childCheckBeforeTrade2() throws Exception
    {

        String pacakgeId = reqData.getPackageId();
        String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();

        IDataset bindPackageIds = ParamInfoQry.getCommparaByCode("CSM", "784", pacakgeId, eparchyCode);
        if (IDataUtil.isEmpty(bindPackageIds))
        {
            return;
        }

        IDataset results = PkgExtInfoQry.queryPackageExtInfo(pacakgeId, eparchyCode);
        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1163);
        }
        
        String rsrvStr25 = results.getData(0).getString("RSRV_STR25", "0");
        int rsrvInt25 = 0;
        if (rsrvStr25 != null)
        {
            rsrvInt25 = Integer.parseInt(rsrvStr25);
        }
        
        // 查询用户当月消费情况
        IDataset chkAcctInfos = AcctCall.getCalcOweFeeByUserAcctId(reqData.getUca().getUserId(), reqData.getUca().getAcctId(), "0");
        IData acctResults = null;
        int retInt = 0;
        if (IDataUtil.isNotEmpty(chkAcctInfos))
        {
            acctResults = chkAcctInfos.getData(0);
        }
        if (acctResults != null && acctResults.size() > 0)
        {
            retInt = acctResults.getInt("ALLROWE_FEE", 0); // 单位：分
        }
        else
        {
            retInt = 0;
        }
        
        if (retInt >= 0)
        {
            if (retInt < rsrvInt25)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1164); // 该集团产品当月的月消费额值低于约定消费的金额，不能办理该活动！
            }
        }
    }
    
    protected IData setStartEndDate(IData info) throws Exception
    {

        if ("".equals(info.getString("ACCT_DAY", "")))
        {
            // 没有获取到用户的结账日信息，请与系统管理员联系！
        }
        String acctDay = info.getString("ACCT_DAY");
        String firstDate = info.getString("FIRST_DATE");
        String nextFirstDate = info.getString("NEXT_FIRST_DATE", info.getString("FIRST_DATE"));
        String nextAcctDay = info.getString("NEXT_ACCT_DAY", info.getString("ACCT_DAY"));
        String bookDate = info.getString("BOOK_DATE", "");
//        String enableTag = info.getString("ENABLE_TAG", "");
//        String startAbsoluteDate = info.getString("START_ABSOLUTE_DATE", "");
//        String startOffset = info.getString("START_OFFSET", "");
//        String startUnit = info.getString("START_UNIT", "");
//        String endEnableTag = info.getString("END_ENABLE_TAG");
//        String endAbsoluteDate = info.getString("END_ABSOLUTE_DATE", "");
//        String endOffset = info.getString("END_OFFSET", "");
//        String endUnit = info.getString("END_UNIT", "");
        
        String enableTag = info.getString("ENABLE_MODE", "");
        String startAbsoluteDate = info.getString("ABSOLUTE_ENABLE_DATE", "");
        String startOffset = info.getString("ENABLE_OFFSET", "");
        String startUnit = info.getString("ENABLE_UNIT", "");
        String endEnableTag = info.getString("DISABLE_MODE");
        String endAbsoluteDate = info.getString("ABSOLUTE_DISABLE_DATE", "");
        String endOffset = info.getString("DISABLE_OFFSET", "");
        String endUnit = info.getString("DISABLE_UNIT", "");
        
        IDataset startDates = new DatasetList();
        IDataset endDates = new DatasetList();
        IData dates = new DataMap();
        if (enableTag.equals("3"))
        { // 可选立即生效或下帐期生效
            IData now = new DataMap();

            String nowStartDate = SysDateMgr.getSysTime();
            now.put("START_DATE", nowStartDate);
            now.put("START_DATE_TEXT", "立即生效[" + nowStartDate + "]");
            startDates.add(now);

            IData next = new DataMap();
            String nextStartDate = AcctDateUtils.getFirstDayNextAcct(nowStartDate, acctDay, firstDate);
            next.put("START_DATE", nextStartDate);
            next.put("START_DATE_TEXT", "下帐期生效[" + nextStartDate + "]");
            startDates.add(next);

            String nowEndDate = AcctDateUtils.endDate(nowStartDate, endEnableTag, endAbsoluteDate, endOffset, endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);
            String nextEndDate = AcctDateUtils.endDate(nextStartDate, endEnableTag, endAbsoluteDate, endOffset, endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);

            IData nowEnd = new DataMap();
            nowEnd.put("END_DATE", nowEndDate);
            endDates.add(nowEnd);
            IData nextEnd = new DataMap();
            nextEnd.put("END_DATE", nextEndDate);
            endDates.add(nextEnd);

        }
        else
        {
            String startDate = AcctDateUtils.startDateByDate(bookDate, enableTag, startAbsoluteDate, startOffset, startUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);

            String endDate = AcctDateUtils.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);

            info.put("START_DATE", startDate);
            info.put("END_DATE", endDate);
            IData now = new DataMap();
            now.put("START_DATE", startDate);
            now.put("START_DATE_TEXT", startDate);
            startDates.add(now);
            IData endData = new DataMap();
            endData.put("END_DATE", endDate);
            endDates.add(endData);
        }

        dates.put("END_DATES", endDates);
        dates.put("START_DATES", startDates);
        // 集团营销利用此字段标识协议期可选
        if ("3".equals(endEnableTag))
        {

            IDataset endoffsets = new DatasetList();
            IDataset enddates = new DatasetList();

            int offset = Integer.parseInt(endOffset);
            for (int i = 1; i <= offset; i++)
            {
                IData endoffset = new DataMap();
                endoffset.put("END_OFFSET", new Integer(i).toString());
                endoffsets.add(endoffset);

                if (enableTag.equals("3"))
                {

                    String nowStartDate = startDates.getData(0).getString("START_DATE");
                    String nextStartDate = startDates.getData(1).getString("START_DATE");
                    String nowEndDate = AcctDateUtils.endDate(nowStartDate, "1", endAbsoluteDate, new Integer(i).toString(), endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);
                    String nextEndDate = AcctDateUtils.endDate(nextStartDate, "1", endAbsoluteDate, new Integer(i).toString(), endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);
                    IData nowEnd = new DataMap();
                    nowEnd.put("END_DATE", nowEndDate);
                    enddates.add(nowEnd);
                    IData nextEnd = new DataMap();
                    nextEnd.put("END_DATE", nextEndDate);
                    enddates.add(nextEnd);

                }
                else
                {
                    String startDate = info.getString("START_DATE");
                    String endDate = AcctDateUtils.endDate(startDate, "1", endAbsoluteDate, new Integer(i).toString(), endUnit, acctDay, firstDate, nextAcctDay, nextFirstDate);
                    IData nextEnd = new DataMap();
                    nextEnd.put("END_DATE", endDate);
                    enddates.add(nextEnd);
                }
            }
            if (endoffsets.size() > 0)
            {
                dates.put("END_OFFSETS", endoffsets);
            }
            else
            {
                IData endoffset = new DataMap();
                endoffset.put("END_OFFSET", 1);
                endoffsets.add(endoffset);
                dates.put("END_OFFSETS", endoffsets);
            }
            if (enddates.size() > 0)
            {
                dates.put("END_DATES", enddates);
            }
            info.put("END_OFFSET", "1");
        }
        return dates;
    }

    /** 营销活动(每笔业务有且只有一条活动台帐数据) */
    protected void decodeActive() throws Exception
    {
        IData param = new DataMap();
        decodePubSaleActiveParam(param);
        saleActiveInsId = SeqMgr.getInstId();
        param.put("INST_ID", saleActiveInsId); // 实例标识
        param.put("USER_ID", reqData.getUca().getUserId());
        param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        param.put("START_DATE", reqData.getStartDate()); // 合同起始时间
        param.put("END_DATE", reqData.getEndDate()); // 合同终止时间

        param.put("RSRV_DATE1", reqData.getOnNetStartDate()); // 约定在网起始时间
        param.put("RSRV_DATE2", reqData.getOnNetEndDate()); // 约定在网终止时间

        param.put("MONTHS", SysDateMgr.monthInterval(reqData.getStartDate(), reqData.getEndDate())); // 合约期
        param.put("REMARK", ""); // 备注
        param.put("RSRV_TAG1", "0"); // 办理人
        String userCity = reqData.getUca().getUser().getCityCode();
        param.put("RSRV_STR21", userCity);

        String saleProductId = reqData.getSaleProductId();
        //畅享流量集团营销活动不登记该表
        if(StringUtils.isNotBlank(saleProductId) && ("69901002".equals(saleProductId) || "69901007".equals(saleProductId)) ){
            param.put("RSRV_STR25", acctId);
        }
        super.addTradeSaleActive(param);
    }

    /**
     * 拼活动公用串
     * 
     * @param pd
     * @param td
     * @param active
     * @throws Exception
     * @author liuzz
     * @date 2014-8-8
     */
    private void decodePubSaleActiveParam(IData active) throws Exception
    {
        // 如果是赠送他人，还需要给被赠送人拼一条活动串，防止出现同一个月有2个返还款
        // 获取产品及产品包名称
        active.put("PRODUCT_MODE", "02"); // 产品模式
        active.put("PRODUCT_ID", reqData.getSaleProductId());
        active.put("PRODUCT_NAME", UPtypeProductInfoQry.getProductTypeNameByProductTypeCode(reqData.getSaleProductId()));
        active.put("PACKAGE_ID", reqData.getPackageId());
        active.put("PACKAGE_NAME", OfferCfg.getInstance(reqData.getPackageId(), BofConst.ELEMENT_TYPE_CODE_PACKAGE).getOfferName());
        active.put("CAMPN_ID", "-1"); // 活动标识
        active.put("CAMPN_CODE", "-1"); // 活动编码
        active.put("CAMPN_NAME", "-1"); // 活动名
        active.put("CAMPN_TYPE", reqData.getCampnType()); // 活动类型
        active.put("OPER_FEE", "0");// 营业费用
        active.put("FOREGIFT", "0");// 押金金额
        active.put("ADVANCE_PAY", "0");// 预付话费
        active.put("SCORE_CHANGED", "0");// 积分异动值
        active.put("PROCESS_TAG", "0"); // 处理标记
        active.put("ACCEPT_DATE", reqData.getAcceptTime()); // 受理时间
        active.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 受理员工
        active.put("RELATION_TRADE_ID", getTradeId()); // 关联业务流水号
        active.put("CANCEL_DATE", ""); // 返销时间
        active.put("CANCEL_STAFF_ID", ""); // 返销员工
        active.put("CANCEL_CAUSE", ""); // 返销原因
        active.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 修改标记
    }

    protected void decodeSaleDiscnt(IData element) throws Exception
    {
        String saleProductId = reqData.getSaleProductId();
        //畅享流量集团营销活动不登记该表
        if(StringUtils.isNotBlank(saleProductId) && "69901002".equals(saleProductId)){
            
        } else {
            
        // 开始拼子台帐
        IData param = new DataMap();
        param.put("USER_ID", reqData.getUca().getUserId()); // 
        param.put("START_DATE", element.getString("START_DATE", ""));
        param.put("END_DATE", element.getString("END_DATE", ""));

        String instId = SeqMgr.getInstId();
        param.put("USER_ID_A", "-1");
        param.put("PACKAGE_ID", reqData.getPackageId());
        param.put("PRODUCT_ID", reqData.getSaleProductId());
        param.put("DISCNT_CODE", element.getString("ELEMENT_ID", ""));
        param.put("SPEC_TAG", "0"); // 特殊优惠标记
        param.put("RELATION_TYPE_CODE", "");
        param.put("INST_ID", instId);
        param.put("CAMPN_ID", "-1");
        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 新增
        param.put("REMARK", element.getString("REMARK", ""));
        param.put("RSRV_STR1", getTradeId());
        super.addTradeDiscnt(param);

        element.put("INST_ID", instId);
        // 拼优惠子台帐属性参数
        if (element.containsKey("ATTR_PARAM"))
        {
            decodeSaleAttr(element);
            }
        }
    }

    /** 子台帐属性参数(最新实现) */
    private void decodeSaleAttr(IData element) throws Exception
    {
        // 准备参数
        IDataset attrs = element.getDataset("ATTR_PARAM");
        if (IDataUtil.isEmpty(attrs))
        {
            return;
        }
        for (int i = 0, size = attrs.size(); i < size; i++)
        {
            IData attr = attrs.getData(i);
            String attrCode = attr.getString("ATTR_CODE");
            String attrValue = attr.getString("ATTR_VALUE");

            IData param = new DataMap();
            param.put("USER_ID", reqData.getUca().getUserId());
            param.put("INST_TYPE", element.getString("ELEMENT_TYPE_CODE", ""));
            param.put("INST_ID", SeqMgr.getInstId());
            param.put("RELA_INST_ID", element.getString("INST_ID", ""));
            param.put("ATTR_CODE", attrCode);
            param.put("ATTR_VALUE", attrValue);
            param.put("START_DATE", element.getString("START_DATE", ""));
            param.put("END_DATE", element.getString("END_DATE", ""));
            param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 新增
            param.put("ELEMENT_ID", element.getString("ELEMENT_ID", ""));
            super.addTradeAttr(param);
        }
    }

    protected void decodeSaleAcctDiscnt(IData element) throws Exception
    {
        String saleProductId = reqData.getSaleProductId();
        //畅享流量集团营销活动、暂时
        if(StringUtils.isNotBlank(saleProductId) && ("69901002".equals(saleProductId) || "69901007".equals(saleProductId)) ){
            
            // 开始拼子台帐
            IData param = new DataMap();
            //String tradeId = getTradeId();
            //param.put("TRADE_ID", tradeId);
            //param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
            param.put("ACCT_ID", acctId);
            param.put("DISCNT_CODE", element.getString("ELEMENT_ID", ""));
            param.put("START_DATE", element.getString("START_DATE", ""));
            param.put("END_DATE", element.getString("END_DATE", ""));
            param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 新增
            if( "69901007".equals(saleProductId) ){
            	param.put( "INST_ID",element.getString("INST_ID", SeqMgr.getInstId()) );
            }else{
            	param.put("INST_ID", SeqMgr.getInstId());
            }
            
            
             
            param.put("UPDATE_TIME", reqData.getAcceptTime()); // 受理时间
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            
            //boolean resultInsert = Dao.insert("TF_B_TRADE_ACCT_DISCNT", param);
            super.addTradeAcctDiscnt(param);
            
        }
        
    }
    
    protected void decodeDeposit(IData element) throws Exception
    {

        IDataset saleDeposits = UpcCall.qryOfferGiftByExtGiftId(element.getString("ELEMENT_ID"));//SaleDepositInfoQry.querySaleDepositById(element.getString("ELEMENT_ID"), reqData.getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(saleDeposits))
        {
            // 查询预存无数据!
            return;
        }
        IData deposit = saleDeposits.getData(0);

        if (StringUtils.isBlank(element.getString("START_DATE")))
        {
            // 预存开始时间为空!
        }
        if (StringUtils.isBlank(element.getString("END_DATE")))
        {
            // 预存结束时间为空!
        }
        IData param = new DataMap();
        param.put("USER_ID", reqData.getUca().getUserId()); // 如果赠送，填被赠送人USER_ID

        param.put("USER_ID_A", reqData.getUca().getUserId()); // 统一填办理人的USER_ID
        param.put("PRODUCT_ID", reqData.getSaleProductId()); // 产品标识
        param.put("PACKAGE_ID", reqData.getPackageId()); // 包标识
        param.put("DISCNT_GIFT_ID", element.getString("ELEMENT_ID", "")); // 赠送优惠标识
        param.put("A_DISCNT_CODE", deposit.getString("GIFT_OBJ_ID", "")); // 帐务活动优惠编码
        param.put("DEPOSIT_TYPE", deposit.getString("GIFT_TYPE", "")); // 预存类型编码
        param.put("INST_ID", SeqMgr.getInstId()); // 实例标识
        param.put("CAMPN_ID", "-1"); // 活动标识
        param.put("MONTHS", deposit.getString("GIFT_CYCLE", "")); // 作用月份数
        param.put("LIMIT_MONEY", ""); // 限额

        if ("1".equals(deposit.getString("GIFT_TYPE")))
        {
        	deposit.put("DISCNT_GIFT_ID", element.getString("ELEMENT_ID", ""));
            param.put("PAYMENT_ID", deposit.getString("PAYMENT_ID", "0"));
            param.put("FEE", deposit.getString("MONEY", "0"));
            decodeGiftFee(deposit);
        }
        else
        {
            param.put("PAYMENT_ID", element.getString("FEE_TYPE_CODE", "0"));
            param.put("FEE", element.getString("FEE", "0"));
        }        
        
        param.put("PAY_MODE", element.getString("PAY_MODE", "")); // 付款模式
        param.put("IN_DEPOSIT_CODE", element.getString("IN_DEPOSIT_CODE", "")); // 转入存折
        param.put("OUT_DEPOSIT_CODE", element.getString("OUT_DEPOSIT_CODE", "")); // 转出存折
        param.put("RELATION_TRADE_ID", getTradeId()); // 关联业务流水号
        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 修改标志

        param.put("START_DATE", reqData.getStartDate()); // 
        param.put("END_DATE", reqData.getEndDate()); // 

        param.put("REMARK", "");
        param.remove("DEPOSIT_USER_ID");

        super.addTradeSaleDeposit(param);
    }

    /**
     * TF_B_TRADE_GIFTFEE
     * @param pd
     * @param td
     * @param data
     * @throws Exception
     * @author liuzz
     * @date 2014-10-30
     */
    public void decodeGiftFee(IData data) throws Exception {
        IData tradefeeSub = new DataMap();
        tradefeeSub.put("USER_ID", reqData.getUca().getUserId());
        tradefeeSub.put("USER_ID_A", reqData.getUca().getUserId());
        tradefeeSub.put("FEE_MODE", "2");
        tradefeeSub.put("FEE_TYPE_CODE", data.getString("PAYMENT_ID","0"));
        tradefeeSub.put("FEE", data.getString("MONEY","0"));
        tradefeeSub.put("DISCNT_GIFT_ID", data.getString("DISCNT_GIFT_ID"));
        tradefeeSub.put("MONTHS", data.getString("GIFT_CYCLE", "0"));
        tradefeeSub.put("LIMIT_MONEY", "0");
        tradefeeSub.put("REMARK", "单独赠送优惠，将帐务管理优惠编码传入帐务'");
        super.addTradefeeGiftfee(tradefeeSub);

    }
    
    protected void decodeGoods(IData element) throws Exception
    {

        //IDataset saleGoods = SaleGoodsInfoQry.querySaleGoodsByGoodsId(element.getString("ELEMENT_ID"));
        IData goods = UpcCall.qryOfferComChaTempChaByCond(element.getString("ELEMENT_ID"), BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        IData offerInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_SALEGOODS, element.getString("ELEMENT_ID"));
        if (IDataUtil.isEmpty(goods))
        {
            // 查询实物无数据!
            return;
        }
        //IData goods = saleGoods.getData(0);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_B", ""); // 赠送号码
        param.put("PRODUCT_ID", reqData.getSaleProductId()); // 产品标识
        param.put("PACKAGE_ID", reqData.getPackageId()); // 包标识
        param.put("INST_ID", SeqMgr.getInstId()); // 实例标识
        param.put("CAMPN_ID", "-1"); // 活动标识
        param.put("GOODS_ID", element.getString("ELEMENT_ID", "")); // 实物标识
        param.put("GOODS_NAME", offerInfo.getString("OFFER_NAME", "")); // 实物名
        param.put("GOODS_NUM", "1"); // 实物数量
        param.put("GOODS_VALUE", goods.getString("GOODS_VALUE", "")); // 赠送物品的价值
        param.put("GOODS_STATE", "0"); // 实物状态

        String resTag = goods.getString("RES_TAG", "");

        param.put("RES_TAG", resTag);
        param.put("RES_TYPE_CODE", goods.getString("GOODS_TYPE_CODE", ""));
        param.put("RES_ID", goods.getString("RES_ID", ""));

        // 看该手机是赠送还是购买的手机
        String tag_set = goods.getString("TAG_SET", "0000000000000000");
        String tag = tag_set.substring(1, 2);
        param.put("RSRV_TAG1", tag);// 购买
        param.put("USER_ID", reqData.getUca().getUserId());

        param.put("ACCEPT_DATE", getAcceptTime());
        param.put("DESTROY_FLAG", goods.getString("DESTROY_FLAG", "")); // 是否可拆机
        param.put("GIFT_MODE", goods.getString("GIFT_MODE", "")); // 赠送方式
        param.put("POST_NAME", element.getString("POST_NAME", "")); // 邮寄姓名
        param.put("POST_ADDRESS", element.getString("POST_ADDRESS", "")); // 邮寄地址
        param.put("POST_CODE", element.getString("POST_CODE", "")); // 邮政编码
        param.put("RELATION_TRADE_ID", getTradeId()); // 关联业务流水号
        param.put("CANCEL_DATE", SysDateMgr.getTheLastTime()); // 取消日期
        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 修改标记
        param.put("REMARK", "");

        param.put("RSRV_STR10", reqData.getCampnType()); // 活动类型

        super.addTradeSaleGoods(param);
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new SaleActiveBeanReqData();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (SaleActiveBeanReqData) getBaseReqData();
    }

    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        String productId = map.getString("PRODUCT_ID");
        reqData.setSaleProductId(productId);
        reqData.setPackageId(map.getString("PACKAGE_ID"));
        reqData.setSelectElements(map.getDataset("SELECTED_ELEMENTS"));
        reqData.setNeedAcct("");
        if ("1".equals(map.getString("NEED_ACCT", "")))
        {
            reqData.setNeedAcct("acct");
            acctId = map.getString("ACCT_ID", "0");
        }
        reqData.setStartDate(map.getString("START_DATE", ""));
        reqData.setEndDate(map.getString("END_DATE", ""));
        reqData.setOnNetStartDate(map.getString("ONNET_START_DATE", ""));
        reqData.setOnNetEndDate(map.getString("ONNET_END_DATE", ""));

        reqData.setCampnType(map.getString("CAMPN_TYPE"));
        
        if(StringUtils.isNotBlank(productId) && "69901002".equals(productId)){
            reqData.setSaleActiveFiles(map.getDataset("SALE_FILE_LIST"));
        }
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getSaleProductId());
        data.put("RSRV_STR2", reqData.getPackageId());
        data.put("RSRV_STR3", UPtypeProductInfoQry.getProductTypeNameByProductTypeCode(reqData.getSaleProductId()));
        data.put("RSRV_STR4", OfferCfg.getInstance(reqData.getPackageId(), BofConst.ELEMENT_TYPE_CODE_PACKAGE).getOfferName());
        data.put("RSRV_STR5", reqData.getStartDate());
        data.put("RSRV_STR6", reqData.getEndDate());
        //data.put("RSRV_STR7", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PACKAGE", "PACKAGE_ID", "PACKAGE_KIND_CODE", reqData.getPackageId()));
        data.put("RSRV_STR8", reqData.getNeedAcct());
        data.put("RSRV_STR9", reqData.getOnNetStartDate());
        data.put("RSRV_STR10", reqData.getOnNetEndDate());

        data.put("CAMPN_ID", "-1");

    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3606"; // 集团营销活动
    }

    
    /**
     * 
     * @throws Exception
     */
    private void regSaleActiveFiles() throws Exception
    {
        String saleProductId = reqData.getSaleProductId();
        //畅享流量集团营销活动、暂时
        if(StringUtils.isNotBlank(saleProductId) 
                && "69901002".equals(saleProductId)){
            
            String userId = reqData.getUca().getUserId();
            String serialNumber = reqData.getUca().getSerialNumber();
            String groupId = reqData.getUca().getCustGroup().getGroupId();
            String custName = reqData.getUca().getCustGroup().getCustName();
            String staffId =  CSBizBean.getVisit().getStaffId();
            String createTime = SysDateMgr.getSysTime();
            String tradeTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", "3606");
            String partitionId = StrUtil.getPartition4ById(userId);
            
            IDataset fileList = reqData.getSaleActiveFiles();
            if(IDataUtil.isNotEmpty(fileList)){
                for (int i = 0; i < fileList.size(); i++) {
                    IData fileData = fileList.getData(i);
                    String fileId = fileData.getString("FILE_ID");
                    String fileName = "";
                    IDataset fileDatas = MFileInfoQry.qryFileInfoListByFileID(fileId);
                    if(IDataUtil.isNotEmpty(fileDatas)){
                        fileName = fileDatas.getData(0).getString("FILE_NAME","");
                    }
                    
                    fileData.put("PARTITION_ID",  partitionId);
                    fileData.put("USER_ID",  userId);
                    fileData.put("SERIAL_NUMBER_A",  serialNumber);
                    fileData.put("GROUP_ID",  groupId);
                    fileData.put("CUST_NAME",  custName);
                    fileData.put("PRODUCT_ID",  saleProductId);
                    fileData.put("CREATE_STAFF", staffId);
                    fileData.put("CREATE_TIME",  createTime);
                    fileData.put("TRADE_TYPE_CODE",  "3606");
                    fileData.put("TRADE_TYPE",  tradeTypeName);
                    fileData.put("TRADE_TAG",  "1");
                    fileData.put("TRADE_ID",  getTradeId());
                    fileData.put("FILE_NAME", fileName);
                    
                    boolean resultFlag = Dao.insert("TF_F_GROUP_FTPFILE", fileData, Route.getCrmDefaultDb());
                  
                }
            }
        }
    }
    
    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception
    {
    	IData productInfo = new DataMap();
    	productInfo.put("SALEACTIVE_TAG", "1");
    	productInfo.put("INST_ID", saleActiveInsId);
    	productInfo.put("USER_ID", reqData.getUca().getUserId());
    	productInfo.put("PRODUCT_ID", reqData.getPackageId());
    	productInfo.put("START_DATE", reqData.getStartDate());
    	productInfo.put("END_DATE", reqData.getEndDate());
    	return productInfo;
    }
    
    /**
     * 企业宽带感恩大派送的营销活动礼包校验
     * @throws Exception
     */
    private void grpSaleActiveStockCheck() throws Exception
    {
    	String saleProductId = this.reqData.getSaleProductId();
        String packageId = reqData.getPackageId();
        
        if("69902002".equals(saleProductId)){//企业宽带感恩大派送
        	if(StringUtils.isNotBlank(packageId))
        	{
        		String staffId = CSBizBean.getVisit().getStaffId();
        	    String eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
        	    String cityCode = CSBizBean.getVisit().getCityCode();
        	        
        		IDataset offerResult = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
            	if(IDataUtil.isEmpty(offerResult))
            	{
            		String message = "据" +  packageId + "获取活动包信息无数据!";
            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
            	}
            	
            	IData pkgExtInfo = offerResult.getData(0);
                String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");
                
                IDataset stockInfo = ActiveStockInfoQry.queryByResKind(condFactor3, staffId,cityCode, eparchyCode);
                if (IDataUtil.isEmpty(stockInfo))
                {
                	String message = "您未分配该活动礼包,不可办理该营销活动!";
            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
                }
                
                IData activeStockInfo = stockInfo.getData(0);
                int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
                int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
                if (warnningValueU >= warnningValueD)
                {
                	String message = "您已经达到办理限额!";
            		CSAppException.apperr(GrpException.CRM_GRP_713, message);
                }
                
                IData cond = new DataMap();
                cond.put("RES_KIND_CODE", condFactor3);
                cond.put("STAFF_ID", staffId);
                cond.put("EPARCHY_CODE", eparchyCode);
                cond.put("CITY_CODE", cityCode);
                StringBuilder sql = new StringBuilder(200);
                sql.append(" UPDATE TF_F_ACTIVE_STOCK");
                sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 1");
                sql.append(" WHERE STAFF_ID = :STAFF_ID");
                sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
                sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
                sql.append(" AND CITY_CODE = :CITY_CODE");
                Dao.executeUpdate(sql, cond);
        	}
        }
    }
    
}
