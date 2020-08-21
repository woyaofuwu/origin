/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay.order.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata.BaseChangeProductReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay.BroadBandProductAndPayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcontinuepay.order.requestdata.CttBroadBandContinuePayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

/**
 */
public class CttBroadBandContinuePayTrade extends ChangeProductTrade implements ITrade
{
    private final static transient Logger logger = Logger.getLogger(CttBroadBandContinuePayTrade.class);

    public void changeMainTradeData(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = new MainTradeData();

        BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();
        CttBroadBandContinuePayReqData reqData = (CttBroadBandContinuePayReqData) btd.getRD();
        List<ProductModuleData> productModuleList = request.getProductElements();
        for (ProductModuleData productModule : productModuleList)
        {
            if ("S".equals(productModule.getElementType()) && ("0".equals(productModule.getModifyTag()) || "U".equals(productModule.getModifyTag())))
            {
                reqData.setSpeedFlag(true);
            }
        }
        // 走PBOSS发送指令 未完成。需修改传递参数
        // td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "APP_TYPE", "300");
        // td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "EXEC_TIME", startDate);//铁通根据ORDER表的执行时间执行工单
        String strPTS = mainTradeData.getProcessTagSet();
        if (reqData.getSpeedFlag() || reqData.getStateFlag())
        {// 只要有速率和服务变化就发指令
            mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);
        }
        if (reqData.getSpeedFlag())
        {// 只有速率变化才置位，否则指令接口有问题
            if (StringUtils.isBlank(strPTS))
            {
                strPTS = "00000000000000000000";
            }
            strPTS = strPTS.substring(0, 3) + "1" + strPTS.substring(4);
        }
        mainTradeData.setProcessTagSet(strPTS);

        // REQ201302040002铁通宽带累计续费率报表需求@add by wukw3 20130716------------------start----------------------------------
        // 1：提前，2当月，3，过期
        String flag = "3";
        String now = request.getAcceptTime();
        List<DiscntTradeData> UserDiscntList = request.getUca().getUserDiscnts();
        if (UserDiscntList != null && UserDiscntList.size() > 0)
        {
            for (int i = 0; i < UserDiscntList.size(); i++)
            {
                DiscntTradeData discnt = UserDiscntList.get(i);
                String end_date = discnt.getEndDate();
                if (now.substring(0, 7).compareTo(end_date.substring(0, 7)) < 0)
                {
                    flag = "1";
                }
                else if (now.substring(0, 7).compareTo(end_date.substring(0, 7)) == 0 && flag != "1")
                {
                    flag = "2";
                }
            }
        }

        mainTradeData.setRsrvStr1(flag);
    }

    private void contiuePayFee(CttBroadBandContinuePayReqData bReqData, BusiTradeData btd) throws Exception
    {
        BroadBandProductAndPayBean bean = BeanManager.createBean(BroadBandProductAndPayBean.class);
        UcaData uca = bReqData.getUca();

        // 存在未生效的预约产品，则提示业务不能继续
        if (uca.getUserNextMainProduct() != null)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_195);
        }

        String newDiscntStartDate = bReqData.getAcceptTime().substring(0, 10);
        String newDiscntEndDate = "";

        List<DiscntTradeData> userDiscnts = bean.getUserDiscnts(uca);

        for (int i = 0; i < userDiscnts.size(); i++)
        {
            DiscntTradeData userDiscnt = userDiscnts.get(i);

            IDataset pkgElemInfos = BofQuery.getServElementByPk(userDiscnt.getPackageId(), userDiscnt.getElementId(), userDiscnt.getElementType());

            if (IDataUtil.isNotEmpty(pkgElemInfos))
            {
                IData pkgElement = pkgElemInfos.getData(0);
                if ("1".equals((pkgElement.getString("MAIN_TAG"))))
                {
                    String oldDiscntEndDate = userDiscnt.getEndDate().substring(0, 10);
                    if (newDiscntStartDate.compareTo(oldDiscntEndDate) < 0)
                    {
                        newDiscntStartDate = SysDateMgr.addDays(oldDiscntEndDate.substring(0, 10), 1) + SysDateMgr.getFirstTime00000();
                    }
                    else
                    {
                        newDiscntStartDate = SysDateMgr.addDays(newDiscntStartDate, 1) + SysDateMgr.getFirstTime00000();
                    }
                    DiscntData tempData = new DiscntData(userDiscnt.toData());
                    tempData.setStartDate(newDiscntStartDate);
                    tempData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    newDiscntEndDate = bean.calEndDate(tempData);

                    if (("exist").equals(userDiscnt.getModifyTag()))
                    {

                        ProductModuleData pmd = new DiscntData(userDiscnt.toData());
                        userDiscnt.setUserId(uca.getUserId());
                        userDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        userDiscnt.setStartDate(newDiscntStartDate);
                        userDiscnt.setEndDate(newDiscntEndDate);
                        userDiscnt.setInstId(SeqMgr.getInstId());
                        btd.add(uca.getSerialNumber(), userDiscnt);
                    }
                }
            }
        }

        // 预约工单
        MainTradeData mainTD = btd.getMainTradeData();
        // mainTD.setExecTime(productChangeDate);
        mainTD.setRsrvStr5(newDiscntStartDate + "--" + newDiscntEndDate);
        mainTD.setRsrvStr6(SysDateMgr.END_DATE_FOREVER);

    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        super.createBusiTradeData(btd);
        // 改变服务状态
        geneSvcStateTradeDatas(btd);

        // getTradeAttrData(btd);

        changeMainTradeData(btd);

    }

    /**
     * 根据用户服务状态看是否为老状态，根据参数表中的配置，终止老状态的结束时间，新插入新状态 td_s_trade_svcstate表中仅支持一种服务一条记录，即仅支持一种不支持连续两次以上次状态变化，否则此函数会有问题
     */
    public void geneSvcStateTradeDatas(BusiTradeData btd) throws Exception
    {
        CttBroadBandContinuePayReqData request = (CttBroadBandContinuePayReqData) btd.getRD();
        String brandCode = request.getUca().getBrandCode();
        String productId = request.getUca().getProductId();
        String eparchyCode = CSBizBean.getTradeEparchyCode();
        IDataset stateparams = TradeSvcStateInfoQry.querySvcStateParamByKey(btd.getTradeTypeCode(), brandCode, productId, eparchyCode);

        if (!stateparams.isEmpty() && stateparams.size() > 0)
        {
            for (int i = 0; i < stateparams.size(); i++)
            {

                String serviceId = (String) stateparams.get(i, "SERVICE_ID");
                String stateCode = (String) stateparams.get(i, "OLD_STATE_CODE");
                // 根据service_id查找用户的服务状态信息
                IDataset userSvcStates = UserSvcStateInfoQry.getUserSvcStateBySvcId(request.getUca().getUserId(), serviceId, stateCode);

                if (!userSvcStates.isEmpty() && userSvcStates.size() > 0)
                {
                    // 删除老的状态
                    SvcStateTradeData svcStateDelData = new SvcStateTradeData(stateparams.getData(i)).clone();
                    svcStateDelData.setMainTag((String) userSvcStates.get(0, "MAIN_TAG"));
                    svcStateDelData.setStateCode((String) userSvcStates.get(0, "STATE_CODE"));
                    svcStateDelData.setStartDate((String) userSvcStates.get(0, "START_DATE"));
                    svcStateDelData.setEndDate(request.getAcceptTime());
                    svcStateDelData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(request.getUca().getSerialNumber(), svcStateDelData);

                    // 新增新的状态
                    SvcStateTradeData svcStateAddData = new SvcStateTradeData(stateparams.getData(i)).clone();
                    svcStateAddData.setMainTag((String) userSvcStates.get(0, "MAIN_TAG"));
                    svcStateAddData.setStateCode((String) stateparams.get(0, "NEW_STATE_CODE"));
                    svcStateAddData.setStartDate(request.getAcceptTime());
                    svcStateAddData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    svcStateAddData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    btd.add(request.getUca().getSerialNumber(), svcStateAddData);

                    request.setStateFlag(true);
                }
            }
        }
    }

    /**
     * 拼装Attr表属性
     * 
     * @param btd
     * @throws Exception
     */
    public void getTradeAttrData(BusiTradeData btd) throws Exception
    {
        BaseChangeProductReqData request = (BaseChangeProductReqData) btd.getRD();

        List<ProductModuleData> selectedElementList = request.getProductElements();
        for (int i = 0; i < selectedElementList.size(); i++)
        {
            ProductModuleData selectElements = selectedElementList.get(i);
            String elementTypeCode = selectElements.getElementType();
            if ("D".equals(elementTypeCode))
            {
                String state = selectElements.getModifyTag();
                String strDiscntCode = selectElements.getElementId();
                String strSD = "";
                String strED = "";
                if (state.equals("0"))
                {
                    strSD = selectElements.getStartDate();
                    if (strSD == null || "".equals(strSD))
                    {
                        strSD = request.getAcceptTime();
                    }
                    strED = selectElements.getEndDate();
                    if (strSD == null || "".equals(strSD))
                    {
                        strSD = SysDateMgr.END_DATE_FOREVER;
                    }

                    // by zengzb 往台帐表插入该优惠的时长(天)
                    // String format = "yyyy-MM-dd";
                    // Date f = SysDateMgr.string2Date(strSD, format);
                    // Date s = SysDateMgr.string2Date(strED,format);

                    AttrTradeData inData = new AttrTradeData();
                    inData.setUserId(request.getUca().getUserId());
                    inData.setInstId(SeqMgr.getInstId());
                    inData.setInstType("D");
                    inData.setAttrCode("35000001");
                    inData.setAttrValue(SysDateMgr.decodeTimestamp("yyyyMMddHHmmss", strSD));
                    inData.setRsrvNum1(strDiscntCode);// 当element_type_code为S时
                    inData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    inData.setStartDate(strSD);
                    inData.setEndDate(strED);
                }
            }

        }
    }

}
