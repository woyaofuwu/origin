package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants.FamilyTradeType;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveBookTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveTrade extends BaseTrade implements ITrade
{
    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        if (BofConst.PAY_MONEY_CODE_BY_HDFK.equals(saleActiveReqData.getPayMoneyCode()))//货到付款
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            dataBus.setOrderTypeCode("257");
            btd.getMainTradeData().setTradeTypeCode("257");

            createSaleActiveBookTradeData(btd, SaleActiveConst.ACTIVE_BOOK_TYPE_HDFK);
        }
        else if (SaleActiveUtil.isWideNetActiveAccept(btd))//判断如果是配了commpara942即预受理转正式的配置中的预受理包，则判断为预受理营销活动业务类型230
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            //如果业务类型时600 不需要修改订单类型为230
            //450 家庭创建
            if (!FamilyTradeType.ACCEPT.getValue().equals(dataBus.getOrderTypeCode())&&!"600".equals(dataBus.getOrderTypeCode())&&!"606".equals(dataBus.getOrderTypeCode())&&!"6800".equals(dataBus.getOrderTypeCode())
            		&&!"4800".equals(dataBus.getOrderTypeCode()))
            {
                dataBus.setOrderTypeCode(SaleActiveConst.SALE_ACTIVE_TRADE_TYPE_BOOK);
            }
            
            btd.getMainTradeData().setTradeTypeCode(SaleActiveConst.SALE_ACTIVE_TRADE_TYPE_BOOK);

            createSaleActiveBookTradeData(btd, SaleActiveConst.ACTIVE_BOOK_TYPE_WIDENET);
            ProductModuleCreator.createProductModuleTradeData(saleActiveReqData.getPmds(), btd);
        }
        else//正常的营销活动240
        {
            createSaleActiveTradeData(btd);
            ProductModuleCreator.createProductModuleTradeData(saleActiveReqData.getPmds(), btd);
        }
    }

    @SuppressWarnings("unchecked")
    private void createSaleActiveBookTradeData(BusiTradeData btd, String bookType) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();
        SaleActiveTradeData satd = createSaleActiveCommonTradeData(btd);
        SaleActiveBookTradeData sabtd = new SaleActiveBookTradeData(satd.toData());

        sabtd.setBookType(bookType);
        sabtd.setDealStateCode("0");
        String imsserialNumber = "";
        if (SaleActiveConst.ACTIVE_BOOK_TYPE_WIDENET.equals(bookType))
        {
            //宽带融合开户同时办理营销活动会传入此值
            if (StringUtils.isNotBlank(saleActiveReqData.getAcceptTradeId()))
            {
                sabtd.setAcceptTradeId(saleActiveReqData.getAcceptTradeId()); 
            }
            else
            {
            	//先保留原来逻辑
            	sabtd.setAcceptTradeId(SaleActiveUtil.getWidenetUserOpenTradeId(btd.getRD().getUca().getSerialNumber()));
            	if("".equals(SaleActiveUtil.getWidenetUserOpenTradeId(btd.getRD().getUca().getSerialNumber())))
            	{
            		IData useuca = UcaInfoQry.qryUserInfoBySn(btd.getRD().getUca().getSerialNumber());
                    IDataset id2 = TradeInfoQry.getExistUser("MS", useuca.getString("USER_ID"), "1");
                    
                    if(IDataUtil.isNotEmpty(id2))
                    {
                    	imsserialNumber = id2.getData(0).getString("SERIAL_NUMBER","");
                    }
            		sabtd.setAcceptTradeId(SaleActiveUtil.getIMSUserOpenTradeId(imsserialNumber));
            	}
            	
            	//取出该包的td_b_package_ext表的TAG_SET1配置，判断下是否是依赖魔百和开户未完工工单（包括宽带融合开户开魔百和无活动）
//            	IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(saleActiveReqData.getPackageId(), btd.getRD().getUca().getUserEparchyCode());
            	IDataset packageExtInfo = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, saleActiveReqData.getPackageId(), "TD_B_PACKAGE_EXT");
            	if(IDataUtil.isNotEmpty(packageExtInfo))
            	{
            		String tagset1 = SaleActiveUtil.getPackageExtTagSet1(saleActiveReqData.getPackageId(), packageExtInfo);//packageExtData.getString("TAG_SET1", "");
            		if (tagset1.length() > 4)
                    {
            			if ("2".equals(tagset1.substring(4, 5)))//是依赖魔百和开户未完工工单的配置，即TAG_SET1的第五位为2
            			{
            				String acceptTradeId = SaleActiveUtil.getInternetTvUserOpenTradeId(btd.getRD().getUca().getSerialNumber());
            				//此处是为了区别宽带融合开户开魔百和无魔百和活动，已经在上面setAcceptTradeId正确了，这里是没有4800的未完工工单的，所以就不再set了
            				if(StringUtils.isNotBlank(acceptTradeId))
            				{
            					//魔百和开户无魔百和活动，魔百和开户在途中，在营销活动受理中开魔百和预受理活动，需要把魔百和开户未完工工单setAcceptTradeId
            					sabtd.setAcceptTradeId(acceptTradeId);
            				}
            			}
                    }
            	}
            }
            
            BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();
            String eparchyCode = sard.getUca().getUserEparchyCode();
            IDataset commparaConfig = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "942", sard.getProductId(), sard.getPackageId(), eparchyCode);
            IData commparaConfigData = commparaConfig.getData(0);
            sabtd.setProductIdB(commparaConfigData.getString("PARA_CODE2"));
            sabtd.setPackageIdB(commparaConfigData.getString("PARA_CODE3"));
            //存在预约的活动则报错，不给予办理
            IDataset hdfkActives2 = SaleActiveInfoQry.getUserBookSaleActive2(btd.getRD().getUca().getUserId(),sard.getProductId(),"1");
            if (IDataUtil.isNotEmpty(hdfkActives2))
            {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户存在预受理的营销活动，业务办理不能继续！");
            }
        }

        if (SaleActiveConst.ACTIVE_BOOK_TYPE_HDFK.equals(bookType) || StringUtils.isNotBlank(imsserialNumber))
        {
            BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();
            sabtd.setAcceptTradeId("0");
            if(StringUtils.isNotBlank(imsserialNumber))
            {
            	sabtd.setAcceptTradeId(SaleActiveUtil.getIMSUserOpenTradeId(imsserialNumber));
            }
            
            if(StringUtils.isNotBlank(sard.getSaleGoodsImei()))
            {
	            IData intfParam = new DataMap();
	            intfParam.put("RES_NO", sard.getSaleGoodsImei());
	            intfParam.put("SALE_STAFF_ID", sard.getSaleStaffId());
	            intfParam.put("SERIAL_NUMBER", sard.getUca().getSerialNumber());
	            intfParam.put("CAMPN_TYPE", sard.getCampnType());
	            intfParam.put("PRODUCT_ID", sard.getProductId());
	            intfParam.put(Route.ROUTE_EPARCHY_CODE, sard.getUca().getUser().getEparchyCode());
	            IDataset termainlDatas = CSAppCall.call("CS.TerminalQuerySVC.getEnableTerminalByResNo", intfParam);
	            IData termainlData = termainlDatas.getData(0);
	            sabtd.setResCode(sard.getSaleGoodsImei());
	            sabtd.setDeviceBrand(termainlData.getString("DEVICE_BRAND"));
	            sabtd.setDeviceBrandCode(termainlData.getString("DEVICE_BRAND_CODE"));
	            sabtd.setDeviceModel(termainlData.getString("DEVICE_MODEL"));
	            sabtd.setDeviceModelCode(termainlData.getString("DEVICE_MODEL_CODE"));
            }
            
        }

        btd.add(btd.getRD().getUca().getSerialNumber(), sabtd);
    }

    @SuppressWarnings("unchecked")
    private SaleActiveTradeData createSaleActiveCommonTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveTradeData satd = new SaleActiveTradeData();
		SaleActiveReqData saleActiveRD= (SaleActiveReqData) btd.getRD();
        BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();
        satd.setUserId(sard.getUca().getUserId());
        satd.setSerialNumber(sard.getUca().getSerialNumber());
        satd.setCampnCode(sard.getCampnCode());
        satd.setCampnId(sard.getCampnId());
        satd.setCampnType(sard.getCampnType());
        satd.setStartDate(sard.getStartDate());
        satd.setEndDate(sard.getEndDate());
        satd.setSerialNumberB(sard.getGiftSerialNumber());
        satd.setPackageId(sard.getPackageId());
        satd.setProductId(sard.getProductId());
        satd.setProcessTag("0");
        satd.setRelationTradeId(sard.getTradeId());
        satd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(sard.getProductId());
        satd.setProductName(productInfo.getString("PRODUCT_NAME"));
        satd.setProductMode(productInfo.getString("PRODUCT_MODE"));
        String packageId = sard.getPackageId();
        String packageName = UPackageInfoQry.getPackageNameByPackageId(packageId);//PkgInfoQry.getPackageNameByPackageId(packageId);
        satd.setPackageName(packageName);
        satd.setRemark(sard.getRemark());
        satd.setOperFee(btd.getOperFee());
        satd.setForegift(btd.getForeGift());
        satd.setAdvancePay(btd.getAdvanceFee());
        satd.setMonths(String.valueOf(SysDateMgr.monthInterval(sard.getStartDate(), sard.getEndDate())));
        satd.setInstId(SeqMgr.getInstId());
        satd.setRsrvTag1("0");
        IDataset citys = UserInfoQry.qryCityInfoByUserId(sard.getUca().getUserId());
        String cityCode;
        if (IDataUtil.isNotEmpty(citys) && citys.size()>0){
        	cityCode = citys.getData(0).getString("CITY_CODE");
        }else{
        	cityCode = btd.getRD().getUca().getUser().getCityCode();
        }
        satd.setRsrvStr21(cityCode);
        //IPHONE6活动处理 20141022
        satd.setRsrvStr22(sard.getIphone6Imei());
        
        //REQ201505150014 请更改抢4G手机红包营销活动打印发票的货品名称 20150515  by songlm
        satd.setRsrvStr23(sard.getAllMoneyName());
        
        IDataset packageExtInfo = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");//PkgInfoQry.getPackageByPackage(packageId, btd.getRD().getUca().getUserEparchyCode());
        satd.setRsrvNum1(packageExtInfo.getData(0).getString("RSRV_STR25"));
        
		if (StringUtils.isNotBlank(saleActiveRD.getSmsVeriSuccess())
				&& "1".equals(saleActiveRD.getSmsVeriSuccess())) {
			satd.setRsrvStr9("SmsCodeVeri"); // 对于提交后需要动态验证码的验证的营销活动办理，验证成功标记，1-成功
		}
		
		if("1".equals(btd.getRD().getPageRequestData().getString("IS_COMM_ACTIVE",""))){		//移动商城6.57通用活动办理保存操作流水号、活动ID、活动名
			satd.setRsrvStr16(btd.getRD().getPageRequestData().getString("UNI_CHANNEL",""));
			satd.setRsrvStr17(btd.getRD().getPageRequestData().getString("TRANS_IDO",""));
			satd.setRsrvStr18(btd.getRD().getPageRequestData().getString("ACTIVITY_ID",""));
			satd.setRsrvStr19(btd.getRD().getPageRequestData().getString("ACTIVITY_NAME",""));
			satd.setRsrvStr20(btd.getRD().getPageRequestData().getString("IS_COMM_ACTIVE",""));
		}
		
		
        return satd;
    }

    @SuppressWarnings("unchecked")
    private void createSaleActiveTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveTradeData satd = createSaleActiveCommonTradeData(btd);
        btd.add(btd.getRD().getUca().getSerialNumber(), satd);
    }
}
