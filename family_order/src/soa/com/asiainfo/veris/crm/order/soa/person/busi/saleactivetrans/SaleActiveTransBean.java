package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans.order.requestdata.SaleActiveTransReqData;

public class SaleActiveTransBean extends CSBizBean
{
    public void buildBaseRequestData(SaleActiveTransReqData transReqData, IData param) throws Exception
    {
        transReqData.setSourceSn(param.getString("SOURCE_SN"));
        transReqData.setTargetSn(param.getString("TARGET_SN"));
        transReqData.setProductId(param.getString("PRODUCT_ID"));
        transReqData.setPackageId(param.getString("PACKAGE_ID"));
        transReqData.setRelationTradeId(param.getString("RELATION_TRADE_ID"));

        UcaData uca = transReqData.getUca();
        SaleActiveTradeData saleactiveTradeData = uca.getUserSaleActiveByRelaTradeId(transReqData.getRelationTradeId());

        transReqData.setCampnType(saleactiveTradeData.getCampnType());
        transReqData.setSourceStartDate(saleactiveTradeData.getStartDate());
        transReqData.setSourceEndDate(saleactiveTradeData.getEndDate());
        transReqData.setSourceOnNetStartDate(saleactiveTradeData.getRsrvDate1());
        transReqData.setSourceOnNetEndDate(saleactiveTradeData.getRsrvDate2());
        transReqData.setSourceMonthBetwen(saleactiveTradeData.getMonths());
    }

    private IDataset buildCreditElements(SaleActiveTransReqData transReqData) throws Exception
    {
        String elementStartDate = transReqData.getTargetElementStartDate();
        String elementEndDate = transReqData.getTargetElementEndDate();

//        IDataset packageCreditElements = SaleCreditInfoQry.queryByPkgId(transReqData.getPackageId());
        IDataset packageCreditElements = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(transReqData.getPackageId(), BofConst.ELEMENT_TYPE_CODE_CREDIT);
        
        if (IDataUtil.isEmpty(packageCreditElements))
        {
            return null;
        }
        
        packageCreditElements = SaleActiveUtil.filterElementByCreditClass(packageCreditElements, transReqData.getUca().getUserId());
        IDataset creditDataset = new DatasetList();
        
        for (int j = 0, s = packageCreditElements.size(); j < s; j++)
        {
            IData packageCredit = packageCreditElements.getData(j);

            IData creditElementData = new DataMap();
            creditElementData.put("ELEMENT_ID", packageCredit.getString("CREDIT_GIFT_ID"));
            creditElementData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            creditElementData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_CREDIT);
            creditElementData.put("START_DATE", elementStartDate);
            creditElementData.put("END_DATE", elementEndDate);
            creditDataset.add(creditElementData);
        }
        return creditDataset;
    }

    public void buildSourceActiveDate(SaleActiveTransReqData transReqData) throws Exception
    {
        transReqData.setSourceNewStartDate(transReqData.getSourceStartDate());
        transReqData.setSourceNewEndDate(SysDateMgr.getSysTime());

        if (!SaleActiveUtil.isQyyx(transReqData.getCampnType()))
            return;

        transReqData.setSourceNewOnNetStartDate(transReqData.getSourceOnNetStartDate());

        String sorceOnnetEndDate = transReqData.getSourceOnNetEndDate();
        if (StringUtils.isBlank(sorceOnnetEndDate))
        {
            sorceOnnetEndDate = transReqData.getSourceEndDate();
        }
        String sourceNewOnNetEndDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(transReqData.getTargetMothBetwen()), sorceOnnetEndDate);
        transReqData.setSourceNewOnNetEndDate(sourceNewOnNetEndDate);
    }

    public void buildTargetUserActiveDate(SaleActiveTransReqData transReqData) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(transReqData.getTargetSn());

        String sourceStartDate = transReqData.getSourceStartDate();
        String sourceEndDate = transReqData.getSourceEndDate();

        int monthBetwen = Integer.parseInt(transReqData.getSourceMonthBetwen());

        //如果转移的活动还没有开始，则转移的活动内元素的开始时间为当前活动的开始时间
        String targetElementStartDate = sourceStartDate;
        String firstDayOfNextMonth = SysDateMgr.getFirstDayOfNextMonth();

        int tempMothBetwen = SaleActiveUtil.getMonthIntervalNoAbs(firstDayOfNextMonth, sourceStartDate);

        // 如果活动已经生效，则目标用户的活动开始时间为当前时间的下月1号。活动持续月份为原活动的结束时间的月底-当前时间的月底的差值。
        if (tempMothBetwen < -1)
        {
            String lastDayOfSaleActiveEndDate = SysDateMgr.getDateLastMonthSec(sourceEndDate);
            monthBetwen = SysDateMgr.monthInterval(firstDayOfNextMonth, lastDayOfSaleActiveEndDate);
            targetElementStartDate = SysDateMgr.getFirstDayOfNextMonth();
        }

        // 不在同一个帐期的两个用户是不允许活动转移的，所以这里只需要把老活动的结束帐期拿过来就可以了。
        transReqData.setTargetElementStartDate(targetElementStartDate);
        transReqData.setTargetElementEndDate(transReqData.getSourceEndDate());

        // 对于转移到目标用户的活动，开始、结束时间不再需要重算。
        transReqData.setTargetMothBetwen(String.valueOf(monthBetwen));
        transReqData.setTargetStartDate(transReqData.getSourceStartDate());
        transReqData.setTargetEndDate(transReqData.getSourceEndDate());

        if (!SaleActiveUtil.isQyyx(transReqData.getCampnType()))
        {
            return;
        }

        // 算目标客户签约在网开始、结束时间

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", uca.getUserEparchyCode());

        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, bookConfigs);
        List<SaleActiveTradeData> userBookSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);

        userBookSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBookSaleActives);
        userBookSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBookSaleActives);

        String maxOnNetSatartDate = SaleActiveUtil.getMaxOnNetEndDateFromUserSaleActive(userBookSaleActives);

        String targetOnNetStartDate = firstDayOfNextMonth;

        if (targetOnNetStartDate.compareTo(maxOnNetSatartDate) < 0)
        {
            targetOnNetStartDate = SysDateMgr.getNextSecond(maxOnNetSatartDate);
        }

        transReqData.setTargetOnNetStartDate(targetOnNetStartDate);
        String targetOnNetEndDate = SysDateMgr.getAddMonthsLastDay(monthBetwen, targetOnNetStartDate);
        transReqData.setTargetOnNetEndDate(targetOnNetEndDate);
    }

    public IDataset buildTartgetSelectedElements(SaleActiveTransReqData transReqData) throws Exception
    {
        String relationTradeId = transReqData.getRelationTradeId();
        String productId = transReqData.getProductId();
        String packageId = transReqData.getPackageId();
        String elementStartDate = transReqData.getTargetElementStartDate();
        String elementEndDate = transReqData.getTargetElementEndDate();
        IDataset selectedElements = new DatasetList();

        UcaData uca = transReqData.getUca();
        
        List<DiscntTradeData> userDiscntList = uca.getUserDiscntByPidPkid(productId, packageId);
        if (CollectionUtils.isNotEmpty(userDiscntList))
        {
            IDataset tradeDiscntDataset = new DatasetList();
            for(DiscntTradeData userDiscnt:userDiscntList)
            {
                IData discntData = new DataMap();
                discntData.put("ELEMENT_ID", userDiscnt.getElementId());
                discntData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                discntData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                discntData.put("START_DATE", elementStartDate);
                discntData.put("END_DATE", elementEndDate);
                tradeDiscntDataset.add(discntData);
            }
            
            if (IDataUtil.isNotEmpty(tradeDiscntDataset))
            {
                selectedElements.addAll(tradeDiscntDataset);
            }
        }

        List<SaleGoodsTradeData> userGoodsList = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
        if (CollectionUtils.isNotEmpty(userGoodsList))
        {
            IDataset tradeGoodsDataset = new DatasetList();
            for(SaleGoodsTradeData userGoods:userGoodsList)
            {
                IData goodsData = new DataMap();
                goodsData.put("ELEMENT_ID", userGoods.getElementId());
                goodsData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                goodsData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
                goodsData.put("RES_CODE", "0");
                goodsData.put("START_DATE", elementStartDate);
                goodsData.put("END_DATE", elementEndDate);
                tradeGoodsDataset.add(goodsData);
            }
            
            if (IDataUtil.isNotEmpty(tradeGoodsDataset))
            {
                selectedElements.addAll(tradeGoodsDataset);
            }
        }

        List<SaleDepositTradeData> userDepositList = uca.getUserSaleDepositByRelationTradeId(relationTradeId);
        if (CollectionUtils.isNotEmpty(userDepositList))
        {
            IDataset tradeDepositDataset = new DatasetList();
            for(SaleDepositTradeData userDeposit:userDepositList)
            {
                IData depositData = new DataMap();
                depositData.put("ELEMENT_ID", userDeposit.getElementId());
                depositData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                depositData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
                depositData.put("START_DATE", elementStartDate);
                depositData.put("END_DATE", elementEndDate);
                tradeDepositDataset.add(depositData);
            }
            
            if (IDataUtil.isNotEmpty(tradeDepositDataset))
            {
                selectedElements.addAll(tradeDepositDataset);
            }
        }

        IDataset tradeCreditDataset = buildCreditElements(transReqData);
        if (IDataUtil.isNotEmpty(tradeCreditDataset))
        {
            selectedElements.addAll(tradeCreditDataset);
        }

        return selectedElements;
    }

    public void checkSourceUser(String sourceSn, String activeSate, String endDate, String eparchyCode) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(sourceSn, false, false);

        String changeAcctDay = uca.getChangeAcctDay();
        String acctDay = uca.getAcctDay();

        if (StringUtils.isNotBlank(changeAcctDay) && !acctDay.equals(changeAcctDay))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户存在预约账期,请在预约账期生效后来办理该业务!");
        }

        if (StringUtils.isNotBlank(activeSate) && ("T".equals(activeSate) || "S".equals(activeSate)))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您选择的源用户活动信息已经失效或不允许转移，不允许继续办理!");
        }

        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, bookConfigs);

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", eparchyCode);
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);

        List<SaleActiveTradeData> userBookAndBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBookAndBackSaleActives);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBookAndBackSaleActives);

        if (CollectionUtils.isEmpty(userBookAndBackSaleActives) || userBookAndBackSaleActives.size() <= 1)
            return;

        String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userBookAndBackSaleActives);

        if (StringUtils.isBlank(maxEndDate))
            return;

        int interval = SysDateMgr.dayInterval(maxEndDate, endDate);

        if (interval > 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您选择的源用户活动非最后一个返还类活动，不允许继续办理！");
        }
    }

    public void checkTargetUser(String sourceSn, String targetSn, String porductId, String eparchyCode, IData map) throws Exception
    {
        UcaData sourceUca = UcaDataFactory.getNormalUca(sourceSn, false, false);
        UcaData targetUca = UcaDataFactory.getNormalUca(targetSn, false, false);

        String sourceAcctDay = sourceUca.getAcctDay();
        String targetAcctDay = targetUca.getAcctDay();

        if (!sourceAcctDay.equals(targetAcctDay))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户结账日不一致,无法受理活动转移业务!");
        }

        // 目标用户是否有返还类活动，有则不让办理！
        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, bookConfigs);

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", eparchyCode);
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);

        List<SaleActiveTradeData> userBookAndBackSaleActives = targetUca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBookAndBackSaleActives);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBookAndBackSaleActives);

        if (CollectionUtils.isNotEmpty(userBookAndBackSaleActives))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "目标用户存在有效返还类活动，不允许继续办理！");
        }
        
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        svcParam.put("SOURCE_SN", map.getString("SERIAL_NUMBER"));
        svcParam.put("TARGET_SN", map.getString("TARGET_SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", map.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", map.getString("RELATION_TRADE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        svcParam.put("PRE_TYPE", "1");
        CSAppCall.call("SS.SaleActiveTransRegSVC.tradeReg", svcParam);
        
        /*IData svcParams = new DataMap();

        svcParams.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        //svcParams.put("CAMPN_TYPE", saleActiveTransReqData.getCampnType());
        svcParams.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
        svcParams.put("PACKAGE_ID", map.getString("PACKAGE_ID"));
        //svcParams.put("START_DATE", saleActiveTransReqData.getTargetStartDate());
        //svcParams.put("END_DATE", saleActiveTransReqData.getTargetEndDate());
        //svcParams.put("ONNET_START_DATE", saleActiveTransReqData.getTargetOnNetStartDate());
        //svcParams.put("ONNET_END_DATE", saleActiveTransReqData.getTargetOnNetEndDate());
        //svcParams.put("SELECTED_ELEMENTS", saleActiveTransReqData.getTargetSelectedElements());
        svcParams.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
        svcParams.put("PRE_TYPE", "1");
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg", svcParams);
    	
    	IData activeEndDataParam = new DataMap();

        activeEndDataParam.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));
        activeEndDataParam.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
        activeEndDataParam.put("PACKAGE_ID", map.getString("PACKAGE_ID"));
        //activeEndDataParam.put("CAMPN_TYPE", saleActiveTransReqData.getCampnType());
        activeEndDataParam.put("RELATION_TRADE_ID", map.getString("RELATION_TRADE_ID"));
        activeEndDataParam.put("IS_RETURN", "0");
        activeEndDataParam.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
        activeEndDataParam.put("PRE_TYPE", "1");
        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", activeEndDataParam);*/
    }

    public IDataset queryEndSaleActives(String userId, String eparchyCode) throws Exception
    {
        return BofQuery.queryValidSaleActives(userId, eparchyCode);
    }
    
    public IDataset queryOnNetEndSaleActives(String userId, String eparchyCode) throws Exception
    {
        return BofQuery.queryValidOnNetSaleActives(userId, eparchyCode);
    }

    public IDataset queryTransSaleActives(String serialNumber, String eparchyCode) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber, false, false);

        List<SaleActiveTradeData> userSaleActives = uca.getUserSaleActives();
        userSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userSaleActives);
        userSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userSaleActives);

        if (CollectionUtils.isEmpty(userSaleActives))
        {
            return null;
        }

        IDataset returnDataset = new DatasetList();
        for (SaleActiveTradeData saleActiveTradeData : userSaleActives)
        {
            returnDataset.add(saleActiveTradeData.toData());
        }

        return returnDataset;
    }

    /**
     * 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx
     * @throws Exception
     */
    public IDataset queryCanEndBeautifulNumberSaleActives(String userId, String eparchyCode)  throws Exception{
        //查询号码的吉祥号码开户活动，吉祥号码开户活动是下月生效
        return BofQuery.queryValidSaleActives(userId, eparchyCode);//TODO
    }
}
