
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

public class ChangeProductTrade extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade implements ITrade
{
	private static Logger logger = Logger.getLogger(com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.trade.ChangeProductTrade.class);

	/**
	 * 拼VPMN台帐子表
	 * 
	 * @param btd
	 * @param uca
	 * @param oldVpmnDiscnt
	 * @param newVpmnDiscnt
	 * @throws Exception
	 */
	public void createBusiTradeByVmpn(ChangeProductReqData request, BusiTradeData btd, UcaData uca, String oldVpmnDiscnt, String newVpmnDiscnt) throws Exception
	{
		String startDate = "";
		String endDate = "";

		IDataset oldVpmnDiscntDatas = CommparaInfoQry.getCommParas("CSM", "860", "VPMN", oldVpmnDiscnt, CSBizBean.getUserEparchyCode());
		IDataset newVpmnDiscntDatas = CommparaInfoQry.getCommParas("CSM", "860", "VPMN", newVpmnDiscnt, CSBizBean.getUserEparchyCode());

		if (request.isBookingTag())
		{
			String bookingDate = request.getBookingDate();
			int bookingDateDay = SysDateMgr.getIntDayByDate(bookingDate);
			int userAcctDay = Integer.parseInt(uca.getAcctDay());

			if (bookingDateDay != userAcctDay)
			{
				if (IDataUtil.isNotEmpty(oldVpmnDiscntDatas) && IDataUtil.isNotEmpty(newVpmnDiscntDatas))
				{
					startDate = SysDateMgr.getFirstDayOfNextMonth(bookingDate);
					endDate = SysDateMgr.getAddMonthsLastDay(1, bookingDate);
				}
				else
				{
					startDate = bookingDate;
					endDate = SysDateMgr.getLastSecond(startDate);
				}
			}
			else
			{
				startDate = bookingDate;
				endDate = SysDateMgr.getLastSecond(startDate);
			}
		}
		else
		{
			if (IDataUtil.isNotEmpty(oldVpmnDiscntDatas) && IDataUtil.isNotEmpty(newVpmnDiscntDatas))
			{
				startDate = SysDateMgr.getFirstDayOfNextMonth();
				endDate = SysDateMgr.getLastDateThisMonth();
			}
			else
			{
				startDate = request.getAcceptTime();
				endDate = SysDateMgr.getLastSecond(startDate);
			}
		}

		this.createBusiTradeVpmnDiscnt(btd, uca, oldVpmnDiscnt, newVpmnDiscnt, startDate, endDate);

		this.createBusiTradeVpmnSvc(btd, uca, newVpmnDiscnt, startDate);
	}

	public void createBusiTradeData(BusiTradeData btd) throws Exception
	{
		super.createBusiTradeData(btd);

		ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

		// 海南预约时间不能超过6个月,在此做统一判断处理
		if (request.isBookingTag())
		{
			if (SysDateMgr.monthInterval(SysDateMgr.getSysDate(), request.getBookingDate()) - 1 > 5)
			{
				CSAppException.apperr(TimeException.CRM_TIME_68);
			}
		}

		// 非产品变更 服务不能预约
		if (request.getNewMainProduct() == null && request.isBookingTag())
		{
			List<ProductModuleData> pmds = request.getProductElements();
			if (pmds != null && pmds.size() > 0)
			{
				for (ProductModuleData pmd : pmds)
				{
					if (!BofConst.MODIFY_TAG_USER.equals(pmd.getModifyTag()) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(pmd.getElementType()))
					{
						CSAppException.apperr(ProductException.CRM_PRODUCT_242, USvcInfoQry.getSvcNameBySvcId(pmd.getElementId()));
					}
				}
			}
		}

		UcaData uca = request.getUca();

		String oldVpmnDiscnt = request.getOldVpmnDiscnt();
		String newVpmnDiscnt = request.getNewVpmnDisnct();

		if (StringUtils.isNotBlank(oldVpmnDiscnt) && StringUtils.isNotBlank(newVpmnDiscnt) && !newVpmnDiscnt.equals(oldVpmnDiscnt))
		{
			this.createBusiTradeByVmpn(request, btd, uca, oldVpmnDiscnt, newVpmnDiscnt);
		}

		if (StringUtils.isNotBlank(request.getInvoiceCode()) && "0".equals(this.getVisit().getInModeCode()))
		{
			// 已经存在押金
			boolean existsInvoice = false;
			IDataset foregift = UserOtherInfoQry.getUserOtherservByPK(btd.getRD().getUca().getUserId(), "FG", "0", null);

			if (IDataUtil.isNotEmpty(foregift))
			{
				for (int i = 0, size = foregift.size(); i < size; i++)
				{
					IData gift = foregift.getData(i);
					String rsrvNum1 = gift.getString("RSRV_NUM1", "");
					String rsrvNum2 = gift.getString("RSRV_NUM2", "");

					if ("3".equals(rsrvNum1) && "80000".equals(rsrvNum2))
					{
						existsInvoice = true;
					}
				}
			}
			if (!existsInvoice)
			{
				IDataset invoice = UserOtherInfoQry.getInvoiceInfo(request.getInvoiceCode(), "FG");
				if (IDataUtil.isNotEmpty(invoice))
				{
					CSAppException.apperr(TicketException.CRM_TICKET_10);
				}
				// 主台账设置发票号
				btd.getMainTradeData().setInvoiceNo(request.getInvoiceCode());
			}
		}

		if ("240".equals(btd.getRD().getOrderTypeCode()))// 如果是营销活动过来调用产品变更的
		{
			btd.getMainTradeData().setRsrvStr7("1");
		}

		if ("240".equals(btd.getRD().getOrderTypeCode()) && "ONE_KEY".equals(btd.getRD().getCheckMode())){
			String productId = btd.getRD().getPageRequestData().getString("SALE_ACTIVE_PRODUCT_ID","");
			btd.getMainTradeData().setRsrvStr7(productId);
		}

		// 办理10001139主产品的时候,当预约情况下面 总有零星几笔单子 主优惠是立即生效 产品下月生效情况 先代码做报错处理
		if (btd.isProductChange() && request.getNewMainProduct() != null && "10001139".equals(request.getNewMainProduct().getProductId()))
		{
			List<DiscntTradeData> discntTradeDatas = uca.getUserDiscntByDiscntId("1139");

			if (discntTradeDatas != null && discntTradeDatas.size() > 0)
			{
				for (DiscntTradeData discntTradeData : discntTradeDatas)
				{
					if ("1139".equals(discntTradeData.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
					{
						String productStartDate = btd.getMainTradeData().getRsrvStr3();// 产品生效时间
						String discntStartDate = discntTradeData.getStartDate();

						if (SysDateMgr.decodeTimestamp(discntStartDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.decodeTimestamp(productStartDate, SysDateMgr.PATTERN_STAND)) < 0)
						{
							CSAppException.apperr(ProductException.CRM_PRODUCT_509, discntStartDate, productStartDate);
						}
					}
				}
			}
		}
		
		btd.getMainTradeData().setRsrvStr3(request.getRemark()); //移动商城测1.8流量充值接口，保存操作流水号
		if("UMMP_FLOW".equals(request.getRsrvStr1())){
			//一级自有电渠开展流量直充、流量赠送及积分兑换流量产品一点承载工作 保存订单金额、折扣信息  lihb3 20160907
			btd.getMainTradeData().setRsrvStr4("UMMP_FLOW");
			btd.getMainTradeData().setRsrvStr5(request.getRsrvStr2());   //实付金额
			btd.getMainTradeData().setRsrvStr6(request.getRsrvStr3());   //实付金额+折减金额
		}
		String elecTag=StaticUtil.getStaticValue("PLAT_ELEC_INVOICE", request.getRsrvStr1());
		if(StringUtils.isNotEmpty(elecTag)){
			btd.getMainTradeData().setRsrvStr4(request.getRsrvStr1());
			btd.getMainTradeData().setRsrvStr5(request.getRsrvStr2());   //实付金额
			btd.getMainTradeData().setRsrvStr6(request.getRsrvStr3());   //积分抵扣金额
		}
		/**
		 * REQ201709210005_关于新增一级电渠套餐办理记录入库的需求
		 * @author zhuoyingzhi
		 * @date 20171013
		 */
		IData  pageReq=btd.getRD().getPageRequestData();
		logger.debug("-----ChangeProductTrade-----"+pageReq);
		logger.debug("-----ChangeProductTrade-RSRV_STR9----"+pageReq.getString("RSRV_STR9",""));
		if("BIP3A211_T3000214_1_0".equals(pageReq.getString("RSRV_STR9",""))){
			//移动商城
			//业务类型标识
			btd.getMainTradeData().setRsrvStr9("BIP3A211_T3000214_1_0");
			//渠道标识
			btd.getMainTradeData().setRsrvStr8(pageReq.getString("RSRV_STR8",""));
			//全网资费编码
			btd.getMainTradeData().setRsrvStr10(pageReq.getString("RSRV_STR10",""));
		}
		/***************end*************************/		 

		/**
		 * 商务电话产品变更，要捞取157号码的IMSI放到主台账的RSRV_STR10
		 * @author zhaohj3
		 * @date  2018-2-8 12:24:15 
		 */
		String tradeTypeCode = btd.getTradeTypeCode();
		String brand = btd.getMainTradeData().getBrandCode();
		if ((StringUtils.equals("9733", tradeTypeCode)) && (StringUtils.equals("TT02", brand) || StringUtils.equals("TT04", brand))) {
			String serialNumberA = ""; // 157号码
			String userIdA = ""; // 157号码对应的userId
			String imsiA = ""; // 157号码对应的IMSI
			
			IDataset relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(btd.getRD().getUca().getUserId(), "T2", null);
			if (DataSetUtils.isNotBlank(relaUUInfos)) {
				serialNumberA = relaUUInfos.first().getString("SERIAL_NUMBER_A","");
				userIdA = relaUUInfos.first().getString("USER_ID_A", "");
				
				btd.getMainTradeData().setRsrvStr8("1");
	            btd.getMainTradeData().setRsrvStr9(serialNumberA);
				
				IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userIdA, "1");
				if (DataSetUtils.isNotBlank(userResInfos)) {
					imsiA = userResInfos.first().getString("IMSI", "");
					btd.getMainTradeData().setRsrvStr10(imsiA);
				}
			}
		}
		
		//add by zhangxing3 for 个人业务1.02版本和电子发票接口需求 start
		//authType 鉴权种类: 0 不认证 1 服务密码 2 身份证 3 线上语音 4 声纹
		if ((StringUtils.equals("110", tradeTypeCode)) && !"".equals(pageReq.getString("AUTH_TYPE",""))) {
			btd.getMainTradeData().setRsrvStr10(pageReq.getString("AUTH_TYPE",""));
		}
		//add by zhangxing3 for 个人业务1.02版本和电子发票接口需求 end
		
		/***************end*************************/
	}

	/**
	 * 拼VPMN优惠台帐子表
	 * 
	 * @param btd
	 * @param uca
	 * @param oldVpmnDiscnt
	 * @param newVpmnDiscnt
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public void createBusiTradeVpmnDiscnt(BusiTradeData btd, UcaData uca, String oldVpmnDiscnt, String newVpmnDiscnt, String startDate, String endDate) throws Exception
	{
		List<DiscntTradeData> discntData = uca.getUserDiscntByDiscntId(oldVpmnDiscnt);

		if (ArrayUtil.isNotEmpty(discntData))
		{
			// 删除
			DiscntTradeData delDiscntTD = discntData.get(0).clone();

			delDiscntTD.setEndDate(endDate);
			delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

			btd.add(uca.getSerialNumber(), delDiscntTD);

			// 新增
			DiscntTradeData addDiscntTD = discntData.get(0).clone();

			addDiscntTD.setElementId(newVpmnDiscnt);
			addDiscntTD.setStartDate(startDate);
			addDiscntTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
			addDiscntTD.setInstId(SeqMgr.getInstId());
			addDiscntTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

			btd.add(uca.getSerialNumber(), addDiscntTD);

			// 用户存在跨集团用户优惠资料时候做产品变更需要把新老 优惠都结束
			List<DiscntTradeData> discntOldVpmn = this.findOldVpmnDiscnts(uca, "20", "80000102");  //UserDiscntInfoQry.getDiscntByUserIdOldVpmn(uca.getUserId(), "20", "80000102");

			if (ArrayUtil.isNotEmpty(discntOldVpmn))
			{
				DiscntTradeData delOldVpmnDiscntTD = discntOldVpmn.get(0).clone();

				if (SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND).compareTo(SysDateMgr.getSysTime()) > 0)
				{
					delOldVpmnDiscntTD.setEndDate(SysDateMgr.getLastSecond(startDate));
				}
				else
				{
					delOldVpmnDiscntTD.setEndDate(SysDateMgr.getLastSecond(SysDateMgr.getSysDate()));
				}

				delOldVpmnDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

				btd.add(uca.getSerialNumber(), delOldVpmnDiscntTD);
			}
		}
	}
	
	/**
	 * 找出老的需要终止的VPMN优惠，参看以前的查询方法 UserDiscntInfoQry.getDiscntByUserIdOldVpmn, sql_ref="SEL_BY_USER_ID_OLDVPMN"
	 * @param uca
	 * @param relationType
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	private List<DiscntTradeData> findOldVpmnDiscnts(UcaData uca, String relationType, String groupId) throws Exception{
		List<DiscntTradeData> rst = new ArrayList<DiscntTradeData>();
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		for(DiscntTradeData discnt : userDiscnts){
			if(!"2".equals(discnt.getSpecTag())){
				continue;
			}
			if(!relationType.equals(discnt.getRelationTypeCode())){
				continue;
			}
			List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndRelOfferInsId(discnt.getInstId());
			boolean findGroup = false;
			for(OfferRelTradeData offerRel : offerRels){
				if(groupId.equals(offerRel.getGroupId())){
					findGroup = true;
					break;
				}
			}
			if(!findGroup){
				continue;
			}
			
			if(SysDateMgr.compareTo(SysDateMgr.getLastDateThisMonth(), discnt.getEndDate()) >= 0 && BofConst.MODIFY_TAG_USER.equals(discnt.getModifyTag())){
				rst.add(discnt);
			}
		}
		return rst;
	}

	/**
	 * 拼VPMN服务台帐子表
	 * 
	 * @param btd
	 * @param uca
	 * @param newVpmnDiscnt
	 * @param startDate
	 * @throws Exception
	 */
	public void createBusiTradeVpmnSvc(BusiTradeData btd, UcaData uca, String newVpmnDiscnt, String startDate) throws Exception
	{
		List<SvcTradeData> svcData = uca.getUserSvcBySvcId("860");

		if (ArrayUtil.isNotEmpty(svcData))
		{
			SvcTradeData svcTradeData = svcData.get(0).clone();

			//svcTradeData.setStartDate(startDate);
			svcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
			svcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
			svcTradeData.setRsrvStr1(newVpmnDiscnt);

			btd.add(uca.getSerialNumber(), svcTradeData);
		}
	}
}
