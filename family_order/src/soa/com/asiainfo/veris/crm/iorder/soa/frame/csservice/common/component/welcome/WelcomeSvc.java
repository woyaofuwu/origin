package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.welcome;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.PackagePrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class WelcomeSvc extends CSBizService {
    public IData getHotAndRecInfo(IData param) throws Exception{
        IData result = new DataMap();
        IDataset allHotsAndRecs =  new WelcomeBean().getHotAndRecInfo(param);
        //对每种类型进行分类，1.热门 2.推荐
        IDataset hotPopuTypes = new DatasetList(); 
        IDataset recPopuTypes = new DatasetList();
        for (int i=0;i<allHotsAndRecs.size();i++) {
            IData iData = (IData) allHotsAndRecs.get(i);
            if(iData.getString("POPULARITY_TYPE").equals("1")) {
                iData.putAll(param);
                hotPopuTypes.add(iData);
            }
            if(iData.getString("POPULARITY_TYPE").equals("2")) {
                iData.putAll(param);
                recPopuTypes.add(iData);
            }
        }
        result.put("HOTINFOS",getPopuTradeType(hotPopuTypes));
        result.put("RECINFOS",getPopuTradeType(recPopuTypes));
        return  result;
    }
    public IData getPopuTradeType(IDataset catalogs) throws Exception{
        //业务类型 1-宽带，2-营销活动，3-套餐，4-积分，5-平台业务，9-其它
        IData welcomeTradeType = new DataMap();
        IDataset widenets = new DatasetList();
        IDataset sales = new DatasetList();
        IDataset products = new DatasetList();
        IDataset scores = new DatasetList();
        IDataset platsvcs = new DatasetList();
        IDataset others = new DatasetList();
        for(int i=0;i<catalogs.size();i++) {
            IData iData = (IData) catalogs.get(i);
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("1")) {
                if (normal(widenets, iData)) continue;
            }
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("2")) {
                //权限过滤
                if (filterPakage(iData)) continue;
                iData.put("randImage",getRandImage());
                IDataset saleProductNameList = WelcomeBean.getProductInfo(iData.getString("SALE_PRODUCT_ID"));
                if(DataUtils.isNotEmpty(saleProductNameList)) {
                    iData.put("saleProductName", saleProductNameList.getData(0).getString("CATALOG_NAME"));
                }
                iData.putAll(WelcomeBean.qrySystemGuiMenu(iData.getString("MENU_ID")));
                 if(WelcomeBean.setDescriptionAndName(iData, "K")) continue;
                sales.add(iData);
            }
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("3")) {
                //权限过滤
                if (normal(products, iData)) continue;
            }
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("4")) {
                //根据offerCode 查询所需积分数
                IDataset offerScores = new WelcomeBean().getScoreInfo(iData.getString("OFFER_CODE"));
                if(DataUtils.isNotEmpty(offerScores)) {
                    iData.put("SCORE",offerScores.getData(0).getString("SCORE"));
                    iData.put("OFFER_NAME",offerScores.getData(0).getString("RULE_NAME"));
                    iData.put("OFFER_DESCRIPTION",offerScores.getData(0).getString("RULE_NAME"));
                }
                iData.put("randImage",getRandImage());
                iData.putAll(WelcomeBean.qrySystemGuiMenu(iData.getString("MENU_ID")));
                scores.add(iData);
            }
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("5")) {
//                if (filterProduct(iData)) continue;
                //根据serviceId 查询价格
                IDataset offerPrices = new WelcomeBean().getPlatSvcPrice(iData.getString("OFFER_CODE"));
                if(DataUtils.isNotEmpty(offerPrices)) {
                    iData.put("PRICE",offerPrices.getData(0).getString("PRICE"));
                }
                if(WelcomeBean.setDescriptionAndName(iData,"Z")) continue;
                iData.put("randImage",getRandImage());
                iData.putAll(WelcomeBean.qrySystemGuiMenu(iData.getString("MENU_ID")));
                platsvcs.add(iData);
            }
            if(iData.getString("POPULARITY_TRADE_TYPE").equals("9")) {
                others.add(iData);
            }
        }
        welcomeTradeType.put("WIDENETS",widenets);
        welcomeTradeType.put("SALES",sales);
        welcomeTradeType.put("PRODUCTS",products);
        welcomeTradeType.put("SCORES",scores);
        welcomeTradeType.put("PLATSVCS",platsvcs);
        welcomeTradeType.put("OTHERS",others);
        return welcomeTradeType;
    }

    private boolean normal(IDataset widenets, IData iData) throws Exception {
        //权限过滤
        if (filterProduct(iData)) return true;
        iData.put("randImage",getRandImage());
        iData.putAll(WelcomeBean.qrySystemGuiMenu(iData.getString("MENU_ID")));
        if(WelcomeBean.setDescriptionAndName(iData,"P")) return true;
        widenets.add(iData);
        return false;
    }

    private boolean filterProduct(IData iData) throws Exception {
        //权限过滤
        iData.put("PRODUCT_ID",iData.getString("OFFER_CODE"));
        IDataset priIDataset = new DatasetList(iData);
        ProductPrivUtil.filterProductListByPriv(iData.getString("STAFF_ID"), priIDataset);
        if(DataUtils.isEmpty(priIDataset)) {
            return true;
        }
        return false;
    }
    private boolean filterPakage(IData iData) throws Exception {
        //权限过滤
        iData.put("PACKAGE_ID",iData.getString("OFFER_CODE"));
        IDataset priIDataset = new DatasetList(iData);
        PackagePrivUtil.filterPackageListByPriv(iData.getString("STAFF_ID"), priIDataset);
        if(DataUtils.isEmpty(priIDataset)) {
            return true;
        }
        return false;
    }


    public String getRandImage() throws Exception{
        //添加随机图片库数据
        IDataset iDataset = new DatasetList();
        iDataset.add("frame/img/temp/commodityEn/dataActivity.png");
        iDataset.add("frame/img/temp/commodityEn/spCallActivity.png");
        iDataset.add("frame/img/temp/commodityEn/competitionResponseTariff.png");
        iDataset.add("frame/img/temp/commodityEn/school.png");
        iDataset.add("frame/img/temp/commodityEn/rechargeCard.png");
        iDataset.add("frame/img/temp/commodityEn/net.png");
        iDataset.add("frame/img/temp/commodityEn/iotActivity.png");
        iDataset.add("frame/img/temp/commodityEn/unconditionalTransfer.png");
        iDataset.add("frame/img/temp/commodityEn/groupPhoneActivity.png");
        iDataset.add("frame/img/temp/commodityEn/4gLimiter.png");
        iDataset.add("frame/img/temp/commodityEn/4gShareContract.png");
        iDataset.add("frame/img/temp/commodityEn/callHold.png");
        iDataset.add("frame/img/temp/commodityEn/colorfulNumber.png");
        iDataset.add("frame/img/temp/commodityEn/dataUsageReminder.png");
        iDataset.add("frame/img/temp/commodityEn/featurePhoneActivity.png");
        iDataset.add("frame/img/temp/commodityEn/featurePhone.png");
        iDataset.add("frame/img/temp/commodityEn/easyOwn.png");
        iDataset.add("frame/img/temp/commodityEn/cfnRy.png");
        iDataset.add("frame/img/temp/commodityEn/callTransfer.png");
        iDataset.add("frame/img/temp/commodityEn/3gMove.png");
        iDataset.add("frame/img/temp/commodityEn/groupPhoneActivity.png");
        iDataset.add("frame/img/temp/commodityEn/localCall.png");
        iDataset.add("frame/img/temp/commodityEn/4gLimiter.png");
        iDataset.add("frame/img/temp/commodityEn/prettyNumberAdd.png");
        iDataset.add("frame/img/temp/commodityEn/scoreActivity.png");
        iDataset.add("frame/img/temp/commodityEn/4gCpe.png");
        iDataset.add("frame/img/temp/commodityEn/4gDataPackage.png");
        iDataset.add("frame/img/temp/commodityEn/4gShareLocal.png");
        iDataset.add("frame/img/temp/commodityEn/easyOwn.png");
        iDataset.add("frame/img/temp/commodityEn/4gShareInternalPackage.png");
        iDataset.add("frame/img/temp/commodityEn/groupBillPackage.png");
        iDataset.add("frame/img/temp/commodityEn/dataPackageActivity.png");
        iDataset.add("frame/img/temp/commodityEn/groupPhoneActivity.png");
        iDataset.add("frame/img/temp/commodityEn/smsBill.png");
        iDataset.add("frame/img/temp/commodityEn/telephoneActivity.png");
        iDataset.add("frame/img/temp/commodityEn/groupPayment.png");
        iDataset.add("frame/img/temp/commodityEn/industryInfo.png");
        iDataset.add("frame/img/temp/commodityEn/localRoaming.png");
        iDataset.add("frame/img/temp/commodityEn/holiday.png");
        iDataset.add("frame/img/temp/commodityEn/ifBusy.png");
        iDataset.add("frame/img/temp/commodityEn/industryActivity.png");
        iDataset.add("frame/img/temp/commodityEn/net.png");
        iDataset.add("frame/img/temp/commodityEn/iotActivity.png");
        iDataset.add("frame/img/temp/commodityEn/colorfulNumber.png");
        iDataset.add("frame/img/temp/commodityEn/cloud.png");
        iDataset.add("frame/img/temp/commodityEn/childPhone.png");
        iDataset.add("frame/img/temp/commodityEn/callActivity.png");
        iDataset.add("frame/img/temp/commodityEn/activity.png");
        iDataset.add("frame/img/temp/commodityEn/4gShareContract.png");
        iDataset.add("frame/img/temp/commodityEn/4gForMagicBox.png");
        iDataset.add("frame/img/temp/commodityEn/4gDataPackage.png");
        iDataset.add("frame/img/temp/commodityEn/groupDataPackage.png");
        iDataset.add("frame/img/temp/commodityEn/netPlay.png");
        iDataset.add("frame/img/temp/commodityEn/newClientBillPackage.png");
        int num =(int)(Math.random()*(iDataset.size()-1));
        return iDataset.get(num).toString();
    }

}
