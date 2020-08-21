package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.NoPhoneWidenetMoveSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove.order.requestdata.NoPhoneWidenetMoveRequestData;

public class NoPhoneWidenetMoveTrade extends BaseTrade implements ITrade
{

    @SuppressWarnings("unchecked")
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	NoPhoneWidenetMoveRequestData reqData = (NoPhoneWidenetMoveRequestData) btd.getRD();
    	
        IDataset WidenetInfos = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());//获取旧宽带widenet表信息
        
        createTradeWidenet(btd, reqData, WidenetInfos.getData(0));//widenet台帐
        appendTradeMainData(btd, reqData);//主台帐
        
        String mainSvcId = "";
        
        genProductChgTrade(btd, reqData, mainSvcId);//未梳理，产品变更相关
        genModelChgTrade(btd, reqData);//未梳理，光猫相关
    }
    
    /**
     * widenet台帐
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, NoPhoneWidenetMoveRequestData reqData, IData widenetInfo) throws Exception
    {
    	//移机前旧地址widenet台帐
    	WideNetTradeData old_wtd = new WideNetTradeData(widenetInfo);
    	old_wtd.setModifyTag(BofConst.MODIFY_TAG_DEL);//置为1删除
    	old_wtd.setEndDate(SysDateMgr.getSysTime());//结束时间为当前
        btd.add(btd.getRD().getUca().getSerialNumber(), old_wtd);
        
    	//移机后新地址widenet台帐
        WideNetTradeData wtd = new WideNetTradeData(widenetInfo);
        wtd.setRsrvTag1(widenetInfo.getString("RSRV_TAG1"));//沿用widenetInfo中宽带的无手机宽带标记
        wtd.setOldStandAddress(widenetInfo.getString("STAND_ADDRESS"));//记录widenetInfo中宽带的原标准地址
        wtd.setOldStandAddressCode(widenetInfo.getString("STAND_ADDRESS_CODE"));//记录widenetInfo中宽带的原标准地址编码
        wtd.setOldDetailAddress(widenetInfo.getString("DETAIL_ADDRESS"));//记录widenetInfo中宽带的原详细地址
        wtd.setRsrvStr1(widenetInfo.getString("RSRV_STR1"));//记录widenetInfo中宽带密码
        
        wtd.setStandAddress(reqData.getNewStandAddress());//记录请求过来的，移机后新标准地址
        wtd.setStandAddressCode(reqData.getNewStandAddressCode());//记录请求过来的，移机后新标准地址编码
        wtd.setDetailAddress(reqData.getNewDetailAddress());//记录请求过来的，移机后新详细地址
        wtd.setContact(reqData.getNewContact());//记录请求过来的，移机后联系人
        wtd.setContactPhone(reqData.getNewContactPhone());//记录请求过来的，移机后联系人电话
        wtd.setPhone(reqData.getNewPhone());//记录请求过来的，移机后联系电话
        wtd.setRsrvNum1(reqData.getDeviceId());//记录请求过来的，移机后设备编码
        wtd.setRsrvStr4(reqData.getNewAreaCode());//记录请求过来的，移机后地区，AREA_CODE
        
        wtd.setStartDate(SysDateMgr.getSysTime());//开始时间
        wtd.setEndDate(SysDateMgr.getTheLastTime());//结束时间
        wtd.setInstId(SeqMgr.getInstId());//实例编码
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);//新增
        
        //如果存在产品变更，则记录RsrvNum5为1
        if(reqData.isChgProd()){
        	wtd.setRsrvNum5("1");
        }else{
        	wtd.setRsrvNum5("0");
        }
        
        wtd.setRsrvNum3("");
        wtd.setRsrvNum4("");
        
        //记录移机后新宽带类型 1-移动FTTB 2-铁通ADSL 3-移动FTTH 5-铁通FTTH 6-铁通FTTB
        if (!"".equals(reqData.getNewWideType()))
        {
            wtd.setRsrvStr2(reqData.getNewWideType());
        }else{
        	wtd.setRsrvStr2(widenetInfo.getString("RSRV_STR2"));
        }
        
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }
    
    /**
     * 修改主台帐字段
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, NoPhoneWidenetMoveRequestData reqData) throws Exception
    {
        btd.getMainTradeData().setRsrvStr5(UProductInfoQry.getProductNameByProductId(btd.getRD().getUca().getProductId()));//记录新宽带产品名称在主台帐RsrvStr5
        btd.getMainTradeData().setSubscribeType("300");
        
        //如果存在产品变更，则记录主台帐RsrvStr6为1
        if(reqData.isChgProd()){
        	btd.getMainTradeData().setRsrvStr6("1");
        }
    }
    
    /**
     * 产品变更台账处理
     */
    private void genProductChgTrade(BusiTradeData<BaseTradeData> btd, NoPhoneWidenetMoveRequestData reqData, String mainSvcId) throws Exception
    {
    	//如果不存在产品变更，则返回
    	if(!reqData.isChgProd()){
    		return;
    	}

		//取得用户选择开通关闭或者修改的元素
		List<ProductModuleData> userSelected = reqData.getProductElements();
		if (userSelected == null)
		{
			userSelected = new ArrayList<ProductModuleData>();
		}
		
		List<ProductModuleData> operElements = new ArrayList<ProductModuleData>();
		operElements.addAll(userSelected);
        
		UcaData uca = reqData.getUca();
		btd.getMainTradeData().setRsrvStr1(uca.getBrandCode());//主台帐的RsrvStr1，记录老品牌
		btd.getMainTradeData().setRsrvStr4(uca.getProductId());//主台帐的RsrvStr4，记录老产品ID
		btd.getMainTradeData().setRsrvStr3(reqData.getAcceptTime());//主台帐的RsrvStr3，记录受理时间（虽然采用预约方式，但还是记录受理时间）
		
		boolean isProductChange = false;
		String productChangeDate = null;
		String oldProductEndDate = null;
		//如果存在主产品变更
		if (reqData.getNewMainProduct() != null && !uca.getProductId().equals(reqData.getNewMainProduct().getProductId()))
		{
			//主台帐的RsrvStr5，旧主产品名称
			btd.getMainTradeData().setRsrvStr5(UProductInfoQry.getProductNameByProductId(reqData.getNewMainProduct().getProductId()));
			
			//不能有预约的产品变更
			if (uca.getUserNextMainProduct() != null)
			{
				CSAppException.apperr(ProductException.CRM_PRODUCT_195);
			}
			
			//主产品变更
			isProductChange = true;
			btd.getMainTradeData().setProductId(reqData.getNewMainProduct().getProductId());//主台帐的ProductId，记录新宽带产品的ID
			btd.getMainTradeData().setBrandCode(reqData.getNewMainProduct().getBrandCode());//主台帐的BrandCode，记录新宽带产品的品牌
			
			//计算新的主产品的生效时间和老主产品的失效时间
			String newProductId = reqData.getNewMainProduct().getProductId();
			String oldProductId = reqData.getUca().getProductId();
			
			if (reqData.isEffectNow())
			{
				productChangeDate = reqData.getAcceptTime();
			}
			else if (reqData.isBookingTag())
			{
				productChangeDate = reqData.getBookingDate();
			}
			else
			{
				productChangeDate = this.getProductChangeDate(oldProductId, newProductId, reqData);
			}
			oldProductEndDate = SysDateMgr.getLastSecond(productChangeDate);//老产品结束时间
			
			//生成新老产品的相关台帐
			this.createProductTrade(newProductId, oldProductId, productChangeDate, oldProductEndDate, reqData, btd);
			
			//拼装需要继承的元素
			List<ProductTradeData> userProducts = uca.getUserProducts();
			IDataset newProductElements = ProductInfoQry.getProductElements(reqData.getNewMainProduct().getProductId(), uca.getUserEparchyCode());
			
			//服务
			List<SvcTradeData> userSvcs = uca.getUserSvcs();
			int svcSize = userSvcs.size();
			for (int i = 0; i < svcSize; i++)
			{
				SvcTradeData userSvc = userSvcs.get(i);
				
				//先判断元素对应的产品是否是无手机宽带产品
				if (!this.isNeedDeal(userProducts, userSvc.getProductId()))
				{
					continue;//如果不是，则跳过
				}
				
				if (userSvc.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_SVC, userSvc.getElementId(), userSvc.getInstId(), userSelected))
				{
					//是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userSvc.getElementId(), userSvc.getElementType());
					if (transElement != null)
					{
						if (!userSvc.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userSvc.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							SvcTradeData svcTradeData = userSvc.clone();
							svcTradeData.setRsrvStr3(svcTradeData.getProductId());
							svcTradeData.setRsrvStr4(svcTradeData.getPackageId());
							svcTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							svcTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							svcTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), svcTradeData);
						}
					}
					else
					{
						//不能继承,则删除
						SvcData delSvcData = new SvcData(userSvc.toData());
						delSvcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
						operElements.add(delSvcData);
					}
				}
			}
			
			//优惠
			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
			int discntSize = userDiscnts.size();
			for (int i = 0; i < discntSize; i++)
			{
				DiscntTradeData userDiscnt = userDiscnts.get(i);
				
				//先判断元素对应的产品是否是无手机宽带产品
				if (!this.isNeedDeal(userProducts, userDiscnt.getProductId()))
				{
					continue;//如果不是，则跳过
				}
				
				if (userDiscnt.getModifyTag().equals(BofConst.MODIFY_TAG_USER) && !this.isExistInUserSelected(BofConst.ELEMENT_TYPE_CODE_DISCNT, userDiscnt.getElementId(), userDiscnt.getInstId(), userSelected))
				{
					// 是否能转换到新产品，如果能，则继承，不能，则删除
					IData transElement = this.getTransElement(newProductElements, userDiscnt.getElementId(), userDiscnt.getElementType());
					if (transElement != null)
					{
						if (!userDiscnt.getProductId().equals(transElement.getString("PRODUCT_ID")) || !userDiscnt.getPackageId().equals(transElement.getString("PACKAGE_ID")))
						{
							DiscntTradeData discntTradeData = userDiscnt.clone();
							discntTradeData.setRsrvStr3(userDiscnt.getProductId());
							discntTradeData.setRsrvStr4(userDiscnt.getPackageId());
							discntTradeData.setModifyTag(BofConst.MODIFY_TAG_INHERIT);
							discntTradeData.setProductId(transElement.getString("PRODUCT_ID"));
							discntTradeData.setPackageId(transElement.getString("PACKAGE_ID"));
							btd.add(uca.getSerialNumber(), discntTradeData);
						}
					}
					else
					{
						// 不能继承,则删除
						DiscntData delDiscnt = new DiscntData(userDiscnt.toData());
						delDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
						operElements.add(delDiscnt);
					}
				}
			}
			
			//针对元素修改时的处理，需要转换产品ID和包ID
			int selectSize = userSelected.size();
			for (int i = 0; i < selectSize; i++)
			{
				ProductModuleData pmd = userSelected.get(i);
				if (BofConst.MODIFY_TAG_UPD.equals(pmd.getModifyTag()))
				{
					IData transElement = this.getTransElement(newProductElements, pmd.getElementId(), pmd.getElementType());
					if (transElement != null)
					{
						if (!transElement.getString("PRODUCT_ID", "").equals(pmd.getProductId()) || !transElement.getString("PACKAGE_ID", "").equals(pmd.getPackageId()))
						{
							pmd.setProductId(transElement.getString("PRODUCT_ID"));
							pmd.setPackageId(transElement.getString("PACKAGE_ID"));
						}
					}
				}
			}

			//转换主产品时，处理必选元素
			IDataset forceElements = this.getProductForceElement(newProductElements);
			if (IDataUtil.isNotEmpty(forceElements))
			{
				int forceSize = forceElements.size();
				for (int i = 0; i < forceSize; i++)
				{
					IData forceElement = forceElements.getData(i);
					if (!this.isExistInUserSelected(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), null, userSelected))
					{
						//不在用户的选择列表里面
						IDataset attrs = AttrItemInfoQry.getelementItemaByPk(forceElement.getString("ELEMENT_ID"), forceElement.getString("ELEMENT_TYPE_CODE"), uca.getUserEparchyCode(), null);
						List<AttrData> attrDatas = new ArrayList<AttrData>();
						if (IDataUtil.isNotEmpty(attrs))
						{
							int length = attrs.size();
							for (int j = 0; j < length; j++)
							{
								IData attr = attrs.getData(j);
								AttrData attrData = new AttrData();
								attrData.setAttrCode(attr.getString("ATTR_CODE"));
								attrData.setAttrValue(attr.getString("ATTR_INIT_VALUE"));
								attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								attrDatas.add(attrData);
							}
						}
						if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							if (uca.getUserSvcBySvcId(forceElement.getString("ELEMENT_ID")).size() > 0)
							{
								continue;
							}

							SvcData svcData = new SvcData(forceElement);
							svcData.setStartDate(SysDateMgr.getSysDate());
							svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
							if (attrDatas.size() > 0)
							{
								svcData.setAttrs(attrDatas);
							}
							operElements.add(svcData);
						}
						else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(forceElement.getString("ELEMENT_TYPE_CODE")))
						{
							DiscntData discntData = null;

							String maxEndDate = reqData.getAcceptTime();

							List<DiscntTradeData> existUserDiscnts = uca.getUserDiscntByDiscntId(forceElement.getString("ELEMENT_ID"));

							if (existUserDiscnts != null && existUserDiscnts.size() > 0)// 必选元素是否已经存在
							{
								for (DiscntTradeData existUserDiscnt : existUserDiscnts)
								{
									maxEndDate = SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND);

									if (maxEndDate.compareTo(SysDateMgr.decodeTimestamp(existUserDiscnt.getEndDate(), SysDateMgr.PATTERN_STAND)) < 0)
									{
										maxEndDate = existUserDiscnt.getEndDate();
									}
								}

								if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(SysDateMgr.END_TIME_FOREVER, SysDateMgr.PATTERN_STAND)) < 0)
								{
									if (SysDateMgr.decodeTimestamp(maxEndDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(productChangeDate, SysDateMgr.PATTERN_STAND)) > 0)// 已经存在的必选元素大于本次产品生效时间
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(SysDateMgr.addSecond(maxEndDate, 1));
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, SysDateMgr.addSecond(maxEndDate, 1)));
									}
									else
									{
										discntData = new DiscntData(forceElement);
										discntData.setStartDate(productChangeDate);
										discntData.setEndDate(ProductModuleCalDate.calEndDate(discntData, productChangeDate));
									}
								}
							}
							else
							{
								discntData = new DiscntData(forceElement);
							}

							if (discntData != null)
							{
								discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								if (attrDatas.size() > 0)
								{
									discntData.setAttrs(attrDatas);
								}
								operElements.add(discntData);
							}
						}
					}
				}
			}
		}
		else
		{
			//应该不会存在该条件下的情况，即主产品未变，仅元素变化
		}

		ProductTradeData nextProduct = uca.getUserNextMainProduct();

		ProductTimeEnv env = new ProductTimeEnv();
		if (reqData.isEffectNow())
		{
			env.setBasicAbsoluteStartDate(reqData.getAcceptTime());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(reqData.getAcceptTime()));
		}
		// 仅元素预约变更且没有预约产品情况
		else if (!isProductChange && reqData.isBookingTag() && nextProduct == null)
		{
			env.setBasicAbsoluteStartDate(reqData.getBookingDate());
			env.setBasicAbsoluteCancelDate(SysDateMgr.getLastSecond(reqData.getBookingDate()));
		}
		else if (isProductChange)
		{
			env.setBasicAbsoluteStartDate(productChangeDate);
			env.setBasicAbsoluteCancelDate(oldProductEndDate);
		}
		// 存在预约产品变更 且 本次业务又预约 报错
		else if (reqData.isBookingTag() && nextProduct != null)
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_243);
		}
		
		// 元素拼串处理
		ProductModuleCreator.createProductModuleTradeData(operElements, btd, env);
		
		//修改服务生效时间为立即生效
		List<BaseTradeData> svcTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		String oldRate = "",newRate = "";
		if(svcTradeData!=null&&svcTradeData.size()>0){
			for(int i=0;i<svcTradeData.size();i++){
				SvcTradeData svc = (SvcTradeData)svcTradeData.get(i);
				IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "4000", svc.getElementId(), btd.getRD().getUca().getUserEparchyCode());
	            if (IDataUtil.isNotEmpty(commparaInfos) && commparaInfos.size() == 1)
	            {
	            	if("0".equals(svc.getModifyTag())) newRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            	if("1".equals(svc.getModifyTag())) oldRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            	if("2".equals(svc.getModifyTag())||"U".equals(svc.getModifyTag())){
	            		newRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            		oldRate = commparaInfos.getData(0).getString("PARA_CODE1");
	            	}
	            }
				
				//移机时产品变更， 与服开约定不同步svc和svcstate表。
				svc.setIsNeedPf("0");
			}
		}
		
		List<BaseTradeData> wideTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_WIDENET);
		if(wideTradeData!=null&&wideTradeData.size()>0){
			for(int i=0;i<wideTradeData.size();i++){
				WideNetTradeData wntd = (WideNetTradeData)wideTradeData.get(i);
				if(BofConst.MODIFY_TAG_ADD.equals(wntd.getModifyTag())){
					wntd.setRsrvNum3(oldRate);
					wntd.setRsrvNum4(newRate);
				}
			}
		}
		
		List<BaseTradeData> svcStateData = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
		if(svcStateData!=null&&svcStateData.size()>0){
			for(int i=0;i<svcStateData.size();i++){
				SvcStateTradeData svcState = (SvcStateTradeData)svcStateData.get(i);
				//移机时产品变更， 与服开约定不同步svc和svcstate表。
				svcState.setIsNeedPf("0");
			}
		}
    }
    
    //生效时间处理
    public String getProductChangeDate(String oldProductId, String newProductId, NoPhoneWidenetMoveRequestData request) throws Exception
	{
		String productChangeDate = null;
		IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
		if (IDataUtil.isNotEmpty(productTrans))
		{
			IData productTran = productTrans.getData(0);
			String enableTag = productTran.getString("ENABLE_TAG");

			if (enableTag.equals("0"))
			{// 立即生效
				productChangeDate = request.getAcceptTime();
			}
			else if ((enableTag.equals("1")) || (enableTag.equals("2")))
			{// 下帐期生效
				productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
			}
			else if (enableTag.equals("3"))
			{// 按原产品的生效方式
				ProductData oldProductData = new ProductData(request.getUca().getProductId());
				String enableTagOld = oldProductData.getEnableTag();

				if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagOld.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
			else if (enableTag.equals("4"))
			{// 按新产品的生效方式
				String enableTagNew = request.getNewMainProduct().getEnableTag();

				if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
				{// 立即生效
					productChangeDate = request.getAcceptTime();
				}
				else if (enableTagNew.equals("1"))
				{// 下帐期生效
					productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
				}
			}
		}
		else
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_4);
		}
		return productChangeDate;
	}
    
    //生成新老产品台帐
    @SuppressWarnings("unchecked")
	public void createProductTrade(String newProductId, String oldProductId, String productChangeDate, String oldProductEndDate, NoPhoneWidenetMoveRequestData request, BusiTradeData btd) throws Exception
	{
		UcaData uca = request.getUca();

		//新主产品新增台帐
		ProductTradeData newProductTradeData = new ProductTradeData();
		newProductTradeData.setProductId(newProductId);
		newProductTradeData.setBrandCode(request.getNewMainProduct().getBrandCode());
		newProductTradeData.setProductMode(request.getNewMainProduct().getProductMode());
		newProductTradeData.setStartDate(productChangeDate);
		newProductTradeData.setEndDate(SysDateMgr.getTheLastTime());
		newProductTradeData.setInstId(SeqMgr.getInstId());
		newProductTradeData.setUserId(uca.getUserId());
		newProductTradeData.setUserIdA("-1");
		newProductTradeData.setMainTag("1");
		newProductTradeData.setOldProductId(uca.getProductId());
		newProductTradeData.setOldBrandCode(uca.getBrandCode());
		newProductTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
		btd.add(uca.getSerialNumber(), newProductTradeData);

		//老主产品结束台帐
		ProductTradeData oldProduct = uca.getUserProduct(oldProductId).get(0);
		ProductTradeData oldProductTrade = oldProduct.clone();
		oldProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
		oldProductTrade.setEndDate(oldProductEndDate);
		btd.add(uca.getSerialNumber(), oldProductTrade);
	}
    
    //元素对应产品的产品类型是否在无手机宽带中
    private boolean isNeedDeal(List<ProductTradeData> userProducts, String elementProductId) throws Exception
	{
		int size = userProducts.size();
		for (int i = 0; i < size; i++)
		{
			ProductTradeData ptd = userProducts.get(i);
			
			//18-铁通ADSL，21-移动FTTH，22-移动FTTB，23-铁通FTTH，24-铁通FTTB
			if (ptd.getProductId().equals(elementProductId)
					&& ("18".equals(ptd.getProductMode()) || "21".equals(ptd.getProductMode()) || "22".equals(ptd.getProductMode()) || "23".equals(ptd.getProductMode()) || "24".equals(ptd.getProductMode())))
			{
				return true;
			}
		}
		return false;
	}
    
    //是否存在于已选元素区内
    private boolean isExistInUserSelected(String elementTypeCode, String elementId, String instId, List<ProductModuleData> userSelected)
	{
		if (userSelected == null || userSelected.size() <= 0)
		{
			return false;
		}
		else
		{
			int size = userSelected.size();
			for (int i = 0; i < size; i++)
			{
				ProductModuleData selected = userSelected.get(i);
				if (selected.getElementType().equals(elementTypeCode) && selected.getElementId().equals(elementId))
				{
					if (!StringUtils.isBlank(selected.getInstId()))
					{
						if (selected.getInstId().equals(instId))
						{
							return true;
						}
						else
						{
							continue;
						}
					}
					return true;
				}
			}
			return false;
		}
	}
    
    //是否继承
    public IData getTransElement(IDataset productElements, String elementId, String elementType)
	{
		if (productElements == null || productElements.size() <= 0)
		{
			return null;
		}
		int size = productElements.size();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if (element.getString("ELEMENT_ID").equals(elementId) && element.getString("ELEMENT_TYPE_CODE").equals(elementType))
			{
				return element;
			}
		}
		return null;
	}
    
    //默认必选元素
    private IDataset getProductForceElement(IDataset productElements) throws Exception
	{
		int size = productElements.size();
		IDataset result = new DatasetList();
		for (int i = 0; i < size; i++)
		{
			IData element = productElements.getData(i);
			if ("1".equals(element.getString("PACKAGE_FORCE_TAG")) && "1".equals(element.getString("ELEMENT_FORCE_TAG")))
			{
				result.add(element);
			}
		}
		//默认元素
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if ("1".equals(element.getString("ELEMENT_DEFAULT_TAG")) && !"1".equals(element.getString("ELEMENT_FORCE_TAG")))
            {
                result.add(element);
            }
        }
		return result;
	}

    
    /**
     * 光猫处理
     */
    private void genModelChgTrade(BusiTradeData<BaseTradeData> btd, NoPhoneWidenetMoveRequestData reqData) throws Exception
    {
    	//如果存在光猫变更
    	if(reqData.isChgModel()){
    		//如果存在租赁的光猫且不满3年，则先将RsrvStr9置为1.移机未退光猫
    		IData param = new DataMap();
    		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    		param.put("SERIAL_NUMBER", serialNumber);
    		NoPhoneWidenetMoveSVC ws = new NoPhoneWidenetMoveSVC();
            IDataset infos = ws.getModelInfo(param);//获取宽带用户other表中光猫信息  RSRV_VALUE_CODE='FTTH'
            if(infos!=null&&infos.size()>0){
            	for(int i=0;i<infos.size();i++){
            		//rsrv_tag1--申领模式  0租赁，1购买，2赠送，3自备  rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫  2.拆机未退光猫
            		if(!"1".equals(infos.getData(i).getString("RSRV_STR9",""))&&!"2".equals(infos.getData(i).getString("RSRV_STR9",""))&&("FTTH".equals(infos.getData(i).getString("RSRV_VALUE_CODE"))||"FTTH_GROUP".equals(infos.getData(i).getString("RSRV_VALUE_CODE")))) {
            			String startDate = infos.getData(i).getString("START_DATE");
            			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);
            			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
            			//如果存在租赁的光猫且不满3年，则先将RsrvStr9置为1.移机未退光猫
            			if(midMonths>0){
            				OtherTradeData otherT = new OtherTradeData(infos.getData(i));
            				otherT.setModifyTag(BofConst.MODIFY_TAG_UPD);
            				otherT.setRsrvStr9("1");
            				otherT.setRsrvDate1(SysDateMgr.getSysTime());
            				btd.add(serialNumber, otherT);
            				
            				//“提醒：您仍有未退还光猫，请90天内到移动营业厅办理光猫退还业务，如逾期未办理，光猫冻结预存款将自动沉淀。”
            				btd.getMainTradeData().setRsrvStr10("1");//记录主台帐的RsrvStr10为1，后面打印免填单配置中读到，用于打印提示信息
            			}
            		}
            	}
            }
    		
    		//如果未选择光猫模式，则返回
    		String modelMode = reqData.getModelMode();//测试环境只有3-租赁
        	if("".equals(modelMode)){
        		return;
    		}
    		
        	String deposit="",strModelMode="0";        	
    		if("3".equals(modelMode)){
    			deposit = "0";
    			strModelMode="3";
    		}else if("2".equals(modelMode)){
    			deposit = "0";
    			strModelMode="2";
    		}else{
    			deposit = reqData.getModelDeposit();
    			strModelMode="0";
    		}

    		//拼other台帐
	        createOtherTradeInfo(btd, deposit, strModelMode,reqData); 
    	}
    }
    
    /**
     * 拼other台帐
     */
    @SuppressWarnings("unchecked")
	private void createOtherTradeInfo(BusiTradeData btd,String deposit,String strModelMode, NoPhoneWidenetMoveRequestData reqData) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setUserId(btd.getRD().getUca().getUser().getUserId());
        otherTradeData.setRsrvValueCode("FTTH");
        otherTradeData.setRsrvValue("FTTH光猫申领");
        otherTradeData.setRsrvStr2(deposit);//押金
        otherTradeData.setRsrvStr7("0");//押金状态
        otherTradeData.setRsrvStr8(btd.getTradeId());//tradeId
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
        otherTradeData.setRsrvTag1(strModelMode);//申领模式  0租赁，1购买，2赠送，3自备
        otherTradeData.setRsrvTag2("1");//光猫状态  1:申领，2:更改，3:退还，4:丢失
        
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(reqData.getRemark()); 
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 

        //作用未知
        IDataset depositInfo = CommparaInfoQry.getCommpara("CSM", "6131", "2", reqData.getUca().getUserEparchyCode());
        String isExchangeModel = reqData.getExchangeModel();
        if(depositInfo!=null 
        		&& depositInfo.size()>0 
        		&& "0".equals(strModelMode) 
        		&& Integer.parseInt(deposit)<depositInfo.getData(0).getInt("PARA_CODE1") 
        		&& "3".equals(isExchangeModel)){
        	otherTradeData.setRsrvTag3("1");//rsrv_tag3--业务类型  1:开户，2:移机     手机宽带移机就用的1，照搬过来了
        }
        
        btd.add(serialNumber, otherTradeData);
        
        if("0".equals(strModelMode)||"1".equals(strModelMode) ){
        	btd.getMainTradeData().setRsrvStr2("1");
        }else if("2".equals(strModelMode)){
        	btd.getMainTradeData().setRsrvStr2("2");
        }
        else if("3".equals(strModelMode)){
        	btd.getMainTradeData().setRsrvStr2("3");
        }
    }
}
