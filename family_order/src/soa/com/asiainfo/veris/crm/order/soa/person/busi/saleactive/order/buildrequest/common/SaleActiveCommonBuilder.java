
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleCreditData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleDepositData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleGoodsData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleScoreData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public abstract class SaleActiveCommonBuilder extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveReqData saleActiveRequestData = (SaleActiveReqData) brd;
        
        String returnYearDiscntRemainFee = param.getString("RETURN_YEAR_DISCNT_REMAIN_FEE","");
        String returnYearDiscntRemainFeeFlag = param.getString("RETURN_YEAR_DISCNT_REMAIN_FEEFLAG","");
        
        if(StringUtils.isNotBlank(returnYearDiscntRemainFeeFlag) && returnYearDiscntRemainFee.compareTo("0") > 0 && StringUtils.isNotBlank(returnYearDiscntRemainFee) && "JZF".equals(returnYearDiscntRemainFeeFlag))
        {
        	saleActiveRequestData.setRsrvStr9(returnYearDiscntRemainFee);
        	saleActiveRequestData.setRsrvStr10(returnYearDiscntRemainFeeFlag);
        }
        
        String productId = param.getString("PRODUCT_ID", "");
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(productId);
        if (IDataUtil.isEmpty(productInfo) && !StringUtils.equals(SaleActiveConst.CALL_TYPE_ACTIVE_TRANS, param.getString("CALL_TYPE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_22, productId);
        }
        String packageId = param.getString("PACKAGE_ID", "");
        IData packageInfo = UPackageInfoQry.getPackageByPK(packageId);
        if (IDataUtil.isEmpty(packageInfo) && !StringUtils.equals(SaleActiveConst.CALL_TYPE_ACTIVE_TRANS, param.getString("CALL_TYPE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }
        saleActiveRequestData.setProductId(productId);
        saleActiveRequestData.setPackageId(packageId);
        String schoolName = param.getString("SCHOOL_NAME");
        if(StringUtils.isNotEmpty(schoolName)){
        	 saleActiveRequestData.setSchoolName(schoolName);
        }
        if(param.getString("ACCEPT_TRADE_ID","")!=null&&!"".equals(param.getString("ACCEPT_TRADE_ID",""))){
            saleActiveRequestData.setAcceptTradeId(param.getString("ACCEPT_TRADE_ID",""));
        }else{
        	saleActiveRequestData.setAcceptTradeId(param.getString("WIDE_MOVE_ACTIVE_TRADEID"));
        }
        
        String studentName = param.getString("STUDENT_NAME");
        if(StringUtils.isNotEmpty(schoolName)){
       	 saleActiveRequestData.setStudentName(studentName);
       }
        String saleGoodsIMSI = param.getString("SALEGOODS_IMEI");
        if(StringUtils.isNotEmpty(saleGoodsIMSI)){
        	saleGoodsIMSI = saleGoodsIMSI.trim();
        }
        saleActiveRequestData.setSaleGoodsImei(saleGoodsIMSI);
        saleActiveRequestData.setSaleStaffId(param.getString("SALE_STAFF_ID"));
        //IPHONE6活动处理 20141022
        saleActiveRequestData.setIphone6Imei(param.getString("IPHONE6_IMEI"));
        
        //REQ201505150014 请更改抢4G手机红包营销活动打印发票的货品名称 20150515  by songlm
        saleActiveRequestData.setAllMoneyName(param.getString("ALL_MONEY_NAME"));
        
        //宽带开户支付模式：P：立即支付  A：先装后付
        saleActiveRequestData.setWidePayMode(param.getString("WIDE_PAY_MODE"));
        
        //REQ201512070014 关于4G终端社会化销售模式系统开发需求 by songlm 20151210
        String giftCode = param.getString("GIFT_CODE");
        
        //信用购机
        saleActiveRequestData.setCreditPurchases(param.getString("CREDIT_PURCHASES",""));
        
        if(StringUtils.isNotEmpty(giftCode))
        {
        	saleActiveRequestData.setGiftCode(giftCode);
        }
        String imeiCode = param.getString("IMEI_CODE");
        if(StringUtils.isNotEmpty(imeiCode))
        {
        	saleActiveRequestData.setImeiCode(imeiCode);
        }
        //end
        
        this.setCampnInfo(param, saleActiveRequestData);

        IData activeDates = getActiveDates(param, saleActiveRequestData);

        saleActiveRequestData.setStartDate(activeDates.getString("START_DATE"));
        saleActiveRequestData.setEndDate(activeDates.getString("END_DATE"));
        saleActiveRequestData.setOnNetStartDate(activeDates.getString("ONNET_START_DATE"));
        saleActiveRequestData.setOnNetEndDate(activeDates.getString("ONNET_END_DATE"));
        
        String bookDate = activeDates.getString("BOOK_DATE");

        IDataset selectedElements = getSelectedElems(param, saleActiveRequestData, bookDate);

        String eparchyCode = saleActiveRequestData.getUca().getUserEparchyCode();

        IDataset xTradeFeeSub = buildSelectedElementsFee(selectedElements, param, productId, packageId, eparchyCode);

        buildSelectedElems(selectedElements, saleActiveRequestData, bookDate);

        String preType = param.getString("PRE_TYPE");
        if (StringUtils.isNotBlank(preType) && !BofConst.PRE_TYPE_CHECK.equals(preType))
        {
            SaleActiveBean saleActiveBean = new SaleActiveBean();
            saleActiveBean.insertPreOrderData(param);
        }

        if (IDataUtil.isNotEmpty(xTradeFeeSub) && !"".equals(param.getString("BATCH_ID")))
        {
            IDataset xPayMoneyList = getPayMoneyList(param, xTradeFeeSub);
            param.put("X_TRADE_FEESUB", xTradeFeeSub);
            param.put("X_TRADE_PAYMONEY", xPayMoneyList);
        }

        if (StringUtils.isNotBlank(param.getString("X_TRADE_PAYMONEY")))
        {
            IDataset payMoneyList = new DatasetList(param.getString("X_TRADE_PAYMONEY"));

            for (int i = 0, size = payMoneyList.size(); i < size; i++)
            {
                IData payMoneyData = payMoneyList.getData(i);

                if (BofConst.PAY_MONEY_CODE_BY_HDFK.equals(payMoneyData.getString("PAY_MONEY_CODE")))
                {
                    saleActiveRequestData.setPayMoneyCode(payMoneyData.getString("PAY_MONEY_CODE"));
                    break;
                }
            }
        }

		// 对于提交后需要动态验证码的验证的营销活动办理，验证成功标记，1-成功
		String smsVeriSuccess = param.getString("SMS_VERI_SUCCESS", "");
		if (StringUtils.isNotBlank(smsVeriSuccess) && "1".equals(smsVeriSuccess)) {
			saleActiveRequestData.setSmsVeriSuccess(smsVeriSuccess);
		}
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-9-2 上午11:13:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-9-2 chengxf2 v1.0.0 修改原因
     */
    private void buildPlatSvcAttrs(SaleActiveReqData brd, IData elem) throws Exception
    {
        IDataset attrDatas = new DatasetList();
        String platSvcId = elem.getString("ELEMENT_ID");
        String productId = elem.getString("PRODUCT_ID");
        String packageId = elem.getString("PACKAGE_ID");
        IDataset platAttrs = CommparaInfoQry.getPlatAttrs(platSvcId, productId, packageId);
        for (int i = 0; i < platAttrs.size(); i++)
        {
            IData platAttr = platAttrs.getData(i);
            IData attrData = new DataMap();
            attrData.put("ATTR_CODE", platAttr.getString("PARA_CODE1", ""));
            attrData.put("ATTR_VALUE", platAttr.getString("PARA_CODE2", ""));
            attrDatas.add(attrData);
        }
        elem.put("ATTR_PARAM", attrDatas);
    }

    private IDataset buildSaleCombineElements(IDataset allElements, BaseSaleActiveReqData brd, String bookDate) throws Exception
    {
        IDataset returnDataset = new DatasetList();
        for (int index = 0, size = allElements.size(); index < size; index++)
        {
            IData element = allElements.getData(index);
            if ("K".equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                String groupId = element.getString("ELEMENT_ID");
                IDataset pkgElements = UPackageElementInfoQry.getCombineElementByGroupId(groupId);
                
//                IDataset pkgElements = PkgElemInfoQry.getCombineElementByPkId(brd.getPackageId(), element.getString("ELEMENT_ID"));
                for (int j = 0, s = pkgElements.size(); j < s; j++)
                {
                    IData combine = pkgElements.getData(j);
                    String offerCode = combine.getString("OFFER_CODE");
                    String offerType = combine.getString("OFFER_TYPE");
                    
                    combine.put("COMBINE_ID", groupId);
                    combine.put("ELEMENT_ID", offerCode);
                    combine.put("ELEMENT_TYPE_CODE", offerType);
                    combine.put("MODIFY_TAG", "0");
                    combine.put("PACKAGE_ID", brd.getPackageId());
                    
                    IData elementInfo = UPackageElementInfoQry.getElementInfo(brd.getPackageId(), BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, offerType);
                    combine.put("RSRV_TAG3", elementInfo.getString("RSRV_TAG3"));
                    
                    IDataset offerModeInfo = UPackageElementInfoQry.qryComJoinElementEnableMode(brd.getPackageId(), BofConst.ELEMENT_TYPE_CODE_PACKAGE, offerCode, offerType);
                    if(IDataUtil.isNotEmpty(offerModeInfo))
                    {
                        combine.putAll(offerModeInfo.getData(0));
                    }
                    
                    combine.put("CAMPN_TYPE", brd.getCampnType());
                    combine.put("SERIAL_NUMBER", brd.getUca().getSerialNumber());
                    combine.put("PRODUCT_ID", brd.getProductId());
                    combine.put("BASIC_CAL_START_DATE", bookDate);
                    combine.put("CUSTOM_ABSOLUTE_START_DATE", bookDate);
                    combine.put(Route.ROUTE_EPARCHY_CODE, brd.getUca().getUserEparchyCode());
                    IDataset combineDateSet = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", combine);
                    IData combineDate = combineDateSet.getData(0);
                    combine.put("START_DATE", combineDate.getString("START_DATE"));
                    combine.put("END_DATE", combineDate.getString("END_DATE"));
                    SaleActiveUtil.filterPackageElementPropertys(combine);
                }
                returnDataset.addAll(pkgElements);
            }
        }
        return returnDataset;
    }

    protected abstract IDataset buildSelectedElementsFee(IDataset selectedElements, IData input, String productId, String packageId, String eparchyCode) throws Exception;

    public void buildSelectedElems(IDataset selectedElements, SaleActiveReqData brd, String bookDate) throws Exception
    {
        IDataset combineElements = buildSaleCombineElements(selectedElements, brd, bookDate);
        selectedElements.addAll(combineElements);

        for (int i = 0, size = selectedElements.size(); i < size; i++)
        {
            IData elem = selectedElements.getData(i);
            String elementId = elem.getString("ELEMENT_ID", "");
            elem.put("PRODUCT_ID", brd.getProductId());
            elem.put("PACKAGE_ID", brd.getPackageId());

            String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

            switch (elemTypeCode.charAt(0))
            {

                case 'A':
                    //新产商品默选A元素没有生失效方式，这里保持和SALE_ACTIVE时间一致
                    elem.put("START_DATE", brd.getStartDate());
                    elem.put("END_DATE", brd.getEndDate());
                    
                    brd.addPmd(new BaseSaleDepositData(elem));
                    if (StringUtils.isBlank(elem.getString("SERIAL_NUMBER_B")))
                    {
                        break;
                    }
                    brd.setGiftSerialNumber(elem.getString("SERIAL_NUMBER_B"));
                    break;

                case 'D':

                    DiscntData discntData = new DiscntData(elem);
                    brd.addPmd(discntData);
                    break;

                case 'S':

                    if (SaleActiveUtil.isExistSvc(brd, elementId))
                    {
                        break;
                    }
                    SvcData svcData = new SvcData(elem);
                    brd.addPmd(svcData);
                    break;

                case 'G':

                    brd.addPmd(new BaseSaleGoodsData(elem));
                    break;

                case 'J':

                    brd.addPmd(new BaseSaleScoreData(elem));
                    break;

                case 'C':

                    brd.addPmd(new BaseSaleCreditData(elem));
                    break;

                case 'Z':

                    if (SaleActiveUtil.isExistPlatSvc(brd, elementId))
                    {
                        break;
                    }
                    elem.put("OPER_CODE", PlatConstants.OPER_ORDER);
                    buildPlatSvcAttrs(brd, elem);
                    PlatSvcData platSvcData = new PlatSvcData(elem);
                    brd.addPmd(platSvcData);
                    break;
            }
        }
    }

    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        IData breParam = new DataMap();

        breParam.putAll(input);
        breParam.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        breParam.put(Route.ROUTE_EPARCHY_CODE, reqData.getUca().getUserEparchyCode());

        CSAppCall.call("CS.SaleActiveCheckSVC.checkBeforeTrade", breParam);
    }

    protected abstract IData getActiveDates(IData param, BaseReqData brd) throws Exception;

    public BaseReqData getBlankRequestDataInstance()
    {
        return new SaleActiveReqData();
    }

    protected abstract IDataset getPayMoneyList(IData input, IDataset tradeFeeSub);

    protected abstract IDataset getSelectedElems(IData param, BaseReqData brd, String bookDate) throws Exception;

    protected abstract void setCampnInfo(IData param, BaseReqData brd) throws Exception;

}
