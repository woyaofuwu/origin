
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleDepositData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleGoodsData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.requestdata.SaleActiveEndReqData;

public class SaleActiveEndBean extends CSBizBean
{
    public void buildBusiRequestObj(BaseReqData brd) throws Exception
    {
        SaleActiveEndReqData saleActiveEndReq = (SaleActiveEndReqData) brd;

        buildEndSaleElements(saleActiveEndReq);

        if (SaleActiveUtil.isBackActive(saleActiveEndReq.getProductId(), saleActiveEndReq.getUca().getUserEparchyCode()))
        {
            buildOtherActiveDiscntElements(saleActiveEndReq);
        }
    }

    public void buildEndSaleElements(SaleActiveEndReqData saleActiveEndReqData) throws Exception
    {
        String productId = saleActiveEndReqData.getProductId();
        String packageId = saleActiveEndReqData.getPackageId();
        String relationTradeId = saleActiveEndReqData.getRelationTradeId();
        String forceEndDate = saleActiveEndReqData.getForceEndDate();

        UcaData uca = saleActiveEndReqData.getUca();
        List<DiscntTradeData> discntTradeDataList = uca.getUserDiscntByPidPkid(productId, packageId);
        if (CollectionUtils.isNotEmpty(discntTradeDataList))
        {
            for (int index = 0, size = discntTradeDataList.size(); index < size; index++)
            {
                DiscntTradeData discntTradeData = discntTradeDataList.get(index);
                
                if(isNoCancelDiscnt(packageId, discntTradeData.getElementId(), saleActiveEndReqData.getUca().getUserEparchyCode()))
            	{
            		continue;
            	}
                
                DiscntData discntData = new DiscntData();

                if (StringUtils.isNotBlank(forceEndDate))
                {
                    discntData.setModifyTag(BofConst.MODIFY_TAG_FORCE_END);
                    discntData.setEndDate(forceEndDate);
                }
                else
                {
                    discntData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    // discntData.setCancelTag("3");
                    discntData.setCancelTag(saleActiveEndReqData.getEndDateValue());
                }

                discntData.setInstId(discntTradeData.getInstId());
                discntData.setElementId(discntTradeData.getElementId());

                saleActiveEndReqData.addPmd(discntData);
            }
        }

        List<SvcTradeData> svcTradeDataList = uca.getUserSvcByPidPkId(productId, packageId);
        if (CollectionUtils.isNotEmpty(svcTradeDataList))
        {
            for (int index = 0, size = svcTradeDataList.size(); index < size; index++)
            {
                SvcTradeData serviceTradeData = svcTradeDataList.get(index);
                SvcData svcData = new SvcData();

                if (StringUtils.isNotBlank(forceEndDate))
                {
                    svcData.setModifyTag(BofConst.MODIFY_TAG_FORCE_END);
                    svcData.setEndDate(forceEndDate);
                }
                else
                {
                    svcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    // svcData.setCancelTag("0");
                    svcData.setCancelTag(saleActiveEndReqData.getEndDateValue());
                }

                svcData.setInstId(serviceTradeData.getInstId());
                svcData.setElementId(svcData.getElementId());

                // saleActiveEndReqData.addPmd(svcData);应蔡世泳要求，服务不作处理
            }
        }

        List<SaleDepositTradeData> depositTradeDataList = uca.getUserSaleDepositByRelationTradeId(relationTradeId);
        if (CollectionUtils.isNotEmpty(depositTradeDataList))
        {
            for (int index = 0, size = depositTradeDataList.size(); index < size; index++)
            {
                SaleDepositTradeData depositTradeData = depositTradeDataList.get(index);
                BaseSaleDepositData saleDepositData = new BaseSaleDepositData();
                saleDepositData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                saleDepositData.setInstId(depositTradeData.getInstId());
                saleDepositData.setEndDate(saleActiveEndReqData.getEndDate());
                saleDepositData.setElementId(depositTradeData.getElementId());
                saleActiveEndReqData.addPmd(saleDepositData);
            }
        }

        List<SaleGoodsTradeData> saleGoodsTradeDataList = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);
        if (CollectionUtils.isNotEmpty(saleGoodsTradeDataList))
        {
            for (int index = 0, size = saleGoodsTradeDataList.size(); index < size; index++)
            {
                SaleGoodsTradeData saleGoodsTradeData = saleGoodsTradeDataList.get(index);
                BaseSaleGoodsData goodsData = new BaseSaleGoodsData();
                goodsData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                goodsData.setInstId(saleGoodsTradeData.getInstId());
                goodsData.setEndDate(saleActiveEndReqData.getEndDate());
                goodsData.setResTag(saleActiveEndReqData.getTerminalReturnTag());
                saleActiveEndReqData.addPmd(goodsData);
            }
        }
    }

    public void buildOtherActiveDiscntElements(SaleActiveEndReqData saleActiveEndReqData) throws Exception
    {
        String productId = saleActiveEndReqData.getProductId();
        String packageId = saleActiveEndReqData.getPackageId();

        UcaData uca = saleActiveEndReqData.getUca();
        List<DiscntTradeData> discntTradeDataList = uca.getUserDiscnts();
        List<SaleActiveTradeData> userBackSaleActives = getUserBackSaleActives(saleActiveEndReqData);

        SaleActiveTradeData thisActiveUserData = uca.getUserSaleActiveByRelaTradeId(saleActiveEndReqData.getRelationTradeId());

        if (thisActiveUserData == null)
            return;

        String thisActiveStartDate = thisActiveUserData.getStartDate();
        String thisActiveEndDate = thisActiveUserData.getEndDate();
        String thisActiveMonth = thisActiveUserData.getMonths();

        //原来的算法貌似不对，原来使用的是getIntervalMoths，现在改用为getIntervalMonths   songlm 20150115 QR-20150109-14营销活动终止时间不对BUG
        int intervalMoths = Integer.parseInt(SaleActiveUtil.getIntervalMoths(thisActiveStartDate, thisActiveEndDate, thisActiveMonth));
        intervalMoths = intervalMoths - 1;
        
        for (DiscntTradeData userDiscnt : discntTradeDataList)
        {
            String discntProductId = userDiscnt.getProductId();
            String discntPackageId = userDiscnt.getPackageId();

            if (productId.equals(discntProductId) && packageId.equals(discntPackageId))
                continue;

            for (SaleActiveTradeData userActiveData : userBackSaleActives)
            {
            	//判断是否是后继营销活动，即其他营销活动的开始时间是否大于当前终止营销活动的开始时间
                if (SaleActiveUtil.getDayIntervalNoAbs(userActiveData.getStartDate(), thisActiveStartDate) > 0)
                    continue;

                String activeProductId = userActiveData.getProductId();
                String activePackageId = userActiveData.getPackageId();

                if (!activeProductId.equals(discntProductId) || !activePackageId.equals(discntPackageId))
                    continue;

                DiscntData discntData = new DiscntData();
                discntData.setModifyTag(BofConst.MODIFY_TAG_MOVE);
                discntData.setInstId(userDiscnt.getInstId());
                discntData.setElementId(userDiscnt.getElementId());
                discntData.setStartDate(SysDateMgr.getAddMonthsNowday(-intervalMoths, userActiveData.getStartDate()));
                discntData.setEndDate(SysDateMgr.getAddMonthsNowday(-intervalMoths, userActiveData.getEndDate()));
                discntData.setRemark("营销活动终止前移用户优惠");
                saleActiveEndReqData.addPmd(discntData);

            }
        }
    }

    /**
     * TODO 需要尽快和用户确认活动可终止的口径，妈的，账务活动终止接口有问题，老系统，活动是否可终止，完全是账务接口判断的， 判断的接口是：QAM_DISCNTDEPOSIT_CANBACK
     * 
     * @param serialNumber
     * @param productId
     * @param packageId
     * @param tradeId
     * @return
     * @throws Exception
     */
    public IData checkSaleActiveEnd(String serialNumber, String productId, String packageId, String tradeId) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber, false, false);

        String actionCode = SaleActiveUtil.getEnableActiveActionCode(uca, tradeId);

        IData returnData = new DataMap();

        if (StringUtils.isNotBlank(actionCode))
        {
            IData acctActionInfo = AcctCall.getDiscntLeftFee(serialNumber, tradeId, actionCode);

            int leftMoney = Integer.parseInt(acctActionInfo.getString("LEFT_MONEY", "0"));
            int totalMoney = Integer.parseInt(acctActionInfo.getString("MONEY", "0"));
            int present = totalMoney - leftMoney; // 已使用赠送款（用户补缴）
            returnData.put("PRESENT_MONEY", present / 100);

            if (queryIsCancel(tradeId, productId, packageId))
            {
                if (leftMoney < totalMoney)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该营销包不允许终止！涉及积分，电子卷并且已经开始返还！");
                }
            }
        }

        return returnData;
    }

    private List<SaleActiveTradeData> getUserBackSaleActives(SaleActiveEndReqData saleActiveEndReqData) throws Exception
    {
        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", saleActiveEndReqData.getUca().getUserEparchyCode());

        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();
        UcaData uca = saleActiveEndReqData.getUca();

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);
        List<SaleActiveTradeData> userBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBackSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBackSaleActives);
        userBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBackSaleActives);
        
        userBackSaleActives = SaleActiveUtil.filterUserSaleActivesByModifyTag(userBackSaleActives);

        return userBackSaleActives;
    }

    private IDataset getUserSaleActiveDeposit(UcaData uca, String relationTradeId) throws Exception
    {
        IDataset userDepositDataset = new DatasetList();
        List<SaleDepositTradeData> depositTradeDataList = uca.getUserSaleDepositByRelationTradeId(relationTradeId);

        if (CollectionUtils.isEmpty(depositTradeDataList))
            return null;

        for (int index = 0, size = depositTradeDataList.size(); index < size; index++)
        {
            IData depositData = depositTradeDataList.get(index).toData();
//            IDataset depositDataset = SaleDepositInfoQry.querySaleDepositById(depositTradeDataList.get(index).getElementId(), uca.getUserEparchyCode());
            IDataset depositDataset = UpcCall.qryOfferGiftByExtGiftId(depositTradeDataList.get(index).getElementId());
            depositData.put("DISCNT_GIFT_NAME", depositDataset.getData(0).getString("GIFT_NAME"));
            
            userDepositDataset.add(depositData);
        }

        return userDepositDataset;
    }

    private IDataset getUserSaleActiveDiscnt(UcaData uca, String productId, String packageId) throws Exception
    {
        IDataset userDiscntDataset = new DatasetList();
        List<DiscntTradeData> discntTradeDataList = uca.getUserDiscntByPidPkid(productId, packageId);
        if (CollectionUtils.isEmpty(discntTradeDataList))
            return null;

        for (int index = 0, size = discntTradeDataList.size(); index < size; index++)
        {
            IData discntData = discntTradeDataList.get(index).toData();
            discntData.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discntTradeDataList.get(index).getDiscntCode()));
            userDiscntDataset.add(discntData);
        }

        return userDiscntDataset;
    }

    private IDataset getUserSaleActiveGoods(UcaData uca, String relationTradeId) throws Exception
    {
        IDataset userGoodsDataset = new DatasetList();
        List<SaleGoodsTradeData> saleGoodsTradeDataList = uca.getUserSaleGoodsByRelationTradeId(relationTradeId);

        if (CollectionUtils.isEmpty(saleGoodsTradeDataList))
            return null;

        for (int index = 0, size = saleGoodsTradeDataList.size(); index < size; index++)
        {
            IData saleGoodsData = saleGoodsTradeDataList.get(index).toData();
            IData goodsInfo = UpcCall.queryOfferByOfferId("G", saleGoodsTradeDataList.get(index).getElementId());
            String goodsName = goodsInfo.getString("OFFER_NAME");//SaleGoodsInfoQry.querySaleGoodsByGoodsId(saleGoodsTradeDataList.get(index).getElementId()).getData(0).getString("GOODS_NAME");
            saleGoodsData.put("GOODS_NAME", goodsName);
            userGoodsDataset.add(saleGoodsData);
        }

        return userGoodsDataset;
    }

    private IDataset getUserSaleActiveSvc(UcaData uca, String productId, String packageId) throws Exception
    {
        IDataset userServiceDataset = new DatasetList();
        List<SvcTradeData> svcTradeDataList = uca.getUserSvcByPidPkId(productId, packageId);

        if (CollectionUtils.isEmpty(svcTradeDataList))
            return null;

        for (int index = 0, size = svcTradeDataList.size(); index < size; index++)
        {
            IData serviceData = svcTradeDataList.get(index).toData();
            serviceData.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(svcTradeDataList.get(index).getElementId()));
            userServiceDataset.add(serviceData);
        }

        return userServiceDataset;
    }

    public IDataset queryCanEndSaleActives(String userId, String eparchyCode) throws Exception
    {
        return BofQuery.queryValidSaleActives(userId, eparchyCode);
    }

    public boolean queryIsCancel(String tradeId, String productId, String packageId) throws Exception
    {
        IDataset scoreList = TradeScoreInfoQry.qryTradeScoreInfos(tradeId, "0");

        if (scoreList != null && scoreList.size() > 0)
            return true;

        IDataset paramDs = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "86", productId, packageId, "0898");

        return (paramDs != null && paramDs.size() > 0) ? true : false;
    }

    public IData querySaleActiveDetailInfos(String userId, String productId, String packageId, String relationTradeId, String productMode) throws Exception
    {
        IData returnData = new DataMap();
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);

        returnData.put("SALE_DISCNT", getUserSaleActiveDiscnt(uca, productId, packageId));
        returnData.put("SALE_SERVICE", getUserSaleActiveSvc(uca, productId, packageId));
        returnData.put("SALE_DEPOSIT", getUserSaleActiveDeposit(uca, relationTradeId));
        returnData.put("SALE_GOODS", getUserSaleActiveGoods(uca, relationTradeId));
        returnData.put("ALERT", getPackageAlert(productMode, packageId));

        return returnData;
    }
    
    private boolean isNoCancelDiscnt(String packageId, String discntCode, String eparchyCode)throws Exception
    {
		IDataset commparaDatset = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "520", packageId, discntCode, eparchyCode);
		
		if(IDataUtil.isNotEmpty(commparaDatset))
		{
			return true;
		}
    	
    	return false;
    }
    
    private String getPackageAlert(String productMode, String packageId) throws Exception
    {
        String alertInfo = "";
//        IDataset pkgInfo = PkgInfoQry.queryPackageById(packageId);//获取td_b_package中的配置
        
        IData pkgInfo = null;
        if("02".equals(productMode))
        {
            pkgInfo = UpcCall.qryOfferComChaTempChaByCond(packageId, "K");
        }

        if(IDataUtil.isNotEmpty(pkgInfo))
        {
        	String rsrvStr4 = pkgInfo.getString("RSRV_STR4","");//取RSRV_STR4值
        	//如果包含alert:
        	if(rsrvStr4.startsWith("alert"))
        	{
        		alertInfo = rsrvStr4.replace("alert", "");
        	}
        }

        return alertInfo;
    }

}
