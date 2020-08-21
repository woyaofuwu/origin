package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * REQ202004230007_关于动感地带客户标识的需求 by mengqx 20200508
 * 开户、携入开户、实名制登记、产品变更、营销活动办理和终止，动感地带标识处理ACTION
 */
public class DealMoveAreaTagAction implements ITradeAction {
	private static final Logger logger = Logger.getLogger(DealMoveAreaTagAction.class);

	public static final String PRODUCTS = "PRODUCTS";//配置类型：产品
	public static final String DISCNTS = "DISCNTS";//配置类型：优惠
	public static final String SALEACTIVES = "SALEACTIVES";//配置类型：营销活动

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		logger.debug("-----DealMoveAreaTagFinishAction_begin-----btd：" + btd);

		String userId = btd.getRD().getUca().getUserId();
		String serialNum = btd.getRD().getUca().getSerialNumber();
		String tradeTypeCode = btd.getTradeTypeCode();

		if("10".equals(tradeTypeCode) || "40".equals(tradeTypeCode) || "110".equals(tradeTypeCode)) {//开户、携入开户、产品变更
			//产品
			List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
			logger.debug("-----DealMoveAreaTagFinishAction-----productTrades：" + productTrades);
			if(productTrades != null && productTrades.size() > 0) {
				for (int i = 0; i < productTrades.size(); i++) {
					ProductTradeData product = productTrades.get(i);

					String productId = product.getProductId();
					String modifyTag = product.getModifyTag();
					String startDate = product.getStartDate();
					String endDate = product.getEndDate();

					if (StringUtils.isNotBlank(productId) && "0".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----insertMoveAreaTag_product：" + product);
						insertMoveAreaTag(btd, productId, startDate, endDate, PRODUCTS, userId, serialNum);
					}

					if (StringUtils.isNotBlank(productId) && "1".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----deleteMoveAreaTag_product：" + product);
						deleteMoveAreaTag(btd, productId, endDate, PRODUCTS, userId, serialNum);
					}
				}
			}

			//优惠
			List<DiscntTradeData> discntTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			logger.debug("-----DealMoveAreaTagFinishAction-----discntTradeData：" + discntTradeData);
			if(discntTradeData != null && discntTradeData.size() > 0) {
				for (int i = 0; i < discntTradeData.size(); i++) {
					DiscntTradeData discnt = discntTradeData.get(i);

					String discntCode = discnt.getDiscntCode();
					String modifyTag = discnt.getModifyTag();
					String startDate = discnt.getStartDate();
					String endDate = discnt.getEndDate();

					if (StringUtils.isNotBlank(discntCode) && "0".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----insertMoveAreaTag_discnt：" + discnt);
						insertMoveAreaTag(btd, discntCode, startDate, endDate, DISCNTS, userId, serialNum);
					}

					if (StringUtils.isNotBlank(discntCode) && "1".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----deleteMoveAreaTag_discnt：" + discnt);
						deleteMoveAreaTag(btd, discntCode, endDate, DISCNTS, userId, serialNum);
					}
				}
			}
		}

		if ("60".equals(tradeTypeCode)) {//实名制登记
			List<OtherTradeData> tradeOthers = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
			if(tradeOthers != null && tradeOthers.size() > 0) {
				for (int i = 0; i < tradeOthers.size(); i++) {
					OtherTradeData tradeOther = tradeOthers.get(i);
					// 如果是实名制激活
					logger.debug("-----DealMoveAreaTagFinishAction-----tradeOther：" + tradeOther);
					if ("CHRN".equals(tradeOther.getRsrvValueCode()) && "实名制办理".equals(tradeOther.getRsrvValue())) {
						//产品
						IDataset products = UserProductInfoQry.queryMainProduct(userId);
						if (IDataUtil.isNotEmpty(products)) {
							for (int j = 0; j < products.size(); j++) {
								IData product = products.getData(j);
								if (IDataUtil.isNotEmpty(product)) {
									ProductTradeData productData = new ProductTradeData(product);
									String productId = productData.getProductId();
									String startDate = productData.getStartDate();
									String endDate = productData.getEndDate();
									insertMoveAreaTag(btd, productId, startDate, endDate, PRODUCTS, userId, serialNum);
								}
							}
						}

						//优惠
						IDataset discnts = UserDiscntInfoQry.getUserValidDiscnt(userId);
						if (IDataUtil.isNotEmpty(discnts)) {
							for (int j = 0; j < discnts.size(); j++) {
								IData discnt = discnts.getData(j);
								if (IDataUtil.isNotEmpty(discnt)) {
									DiscntTradeData discntTradeData = new DiscntTradeData(discnt);
									String discntCode = discntTradeData.getDiscntCode();
									String startDate = discntTradeData.getStartDate();
									String endDate = discntTradeData.getEndDate();
									insertMoveAreaTag(btd, discntCode, startDate, endDate, DISCNTS, userId, serialNum);
								}
							}
						}
					}
				}
			}
		}

		if ("240".equals(tradeTypeCode) || "237".equals(tradeTypeCode)) {//营销活动受理、营销活动终止
			List<SaleActiveTradeData> saleActiveTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
			logger.debug("-----DealMoveAreaTagFinishAction-----saleActiveTrades：" + saleActiveTrades);
			if(saleActiveTrades != null && saleActiveTrades.size() > 0) {
				for (int i = 0; i < saleActiveTrades.size(); i++) {
					SaleActiveTradeData saleActiveTradeData = saleActiveTrades.get(i);

					String packageId = saleActiveTradeData.getPackageId();
					String productId = saleActiveTradeData.getProductId();
					String modifyTag = saleActiveTradeData.getModifyTag();
					String startDate = saleActiveTradeData.getStartDate();
					String endDate = saleActiveTradeData.getEndDate();

					if (StringUtils.isNotBlank(packageId) && StringUtils.isNotBlank(productId) && "0".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----insertMoveAreaTag_saleActive：" + saleActiveTradeData);
						insertMoveAreaTag(btd, packageId, startDate, endDate, SALEACTIVES, userId, serialNum);
					}

					if (StringUtils.isNotBlank(packageId) && StringUtils.isNotBlank(productId) && "1".equals(modifyTag)) {
						logger.debug("-----DealMoveAreaTagFinishAction-----deleteMoveAreaTag_saleActive：" + saleActiveTradeData);
						deleteMoveAreaTag(btd, packageId, endDate, SALEACTIVES, userId, serialNum);
					}
				}
			}
		}
	}

	private void deleteMoveAreaTag(BusiTradeData btd, String elementId, String endDate, String commparaType, String userId, String serialNum) throws Exception {

		IDataset Commpara424 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "424", commparaType, elementId, "0898");
		if (IDataUtil.isNotEmpty(Commpara424) && Commpara424.size() != 0) {

			String sourceTag = "2";//来源标识（1、直接迁入，2、购买迁入，3、活动迁入，9、其他）
			String remark = "办理变更【"+elementId+"】终止";
			if(PRODUCTS.equals(commparaType)){
				sourceTag = "2";
				remark = "办理产品【"+elementId+"】终止";
			}else if(DISCNTS.equals(commparaType)){
				sourceTag = "2";
				remark = "办理优惠【"+elementId+"】终止";
			}else if(SALEACTIVES.equals(commparaType)){
				sourceTag = "3";
				remark = "办理营销活动【"+elementId+"】终止";
			}

			IDataset userOthers = UserOtherInfoQry.getMoveAreaTagBySourceTagElementID(userId, serialNum, sourceTag, elementId);

			if(IDataUtil.isNotEmpty(userOthers) && IDataUtil.isNotEmpty(userOthers.getData(0))){
				OtherTradeData tradeOther = new OtherTradeData(userOthers.getData(0));
				tradeOther.setEndDate(endDate);
				tradeOther.setModifyTag(BofConst.MODIFY_TAG_DEL); //删除
				tradeOther.setRemark(remark);
				logger.debug("-----DealMoveAreaTagFinishAction_deleteMoveAreaTag-----tradeOther:" + tradeOther);

				btd.add(serialNum, tradeOther);
			}
		}
	}

	private void insertMoveAreaTag(BusiTradeData btd, String elementId, String startDate, String endDate, String commparaType, String userId, String serialNum) throws Exception {

		IDataset Commpara424 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "424", commparaType, elementId, "0898");
		if (IDataUtil.isNotEmpty(Commpara424) && Commpara424.size() != 0) {
			String sourceTag = "2";//来源标识（1、直接迁入，2、购买迁入，3、活动迁入，9、其他）
			String remark = "办理【"+elementId+"】得到";
			if(PRODUCTS.equals(commparaType)){
				sourceTag = "2";
				remark = "办理产品【"+elementId+"】得到";
			}else if(DISCNTS.equals(commparaType)){
				sourceTag = "2";
				remark = "办理优惠【"+elementId+"】得到";
			}else if(SALEACTIVES.equals(commparaType)){
				sourceTag = "3";
				remark = "办理营销活动【"+elementId+"】得到";
			}

			OtherTradeData tradeOther = new OtherTradeData();
			tradeOther.setUserId(userId);
			tradeOther.setRsrvValueCode("MOVE_AREA_TAG");
			tradeOther.setRsrvValue(serialNum);
			tradeOther.setRsrvStr1(sourceTag);
			tradeOther.setRsrvStr2(elementId);
			tradeOther.setStartDate(startDate);
			tradeOther.setEndDate(endDate);
			tradeOther.setStaffId(CSBizBean.getVisit().getStaffId());
			tradeOther.setDepartId(CSBizBean.getVisit().getDepartId());
			tradeOther.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
			tradeOther.setInstId(SeqMgr.getInstId());
			tradeOther.setRemark(remark);
			logger.debug("-----DealMoveAreaTagFinishAction_insertMoveAreaTag-----tradeOther:" + tradeOther);

			btd.add(serialNum, tradeOther);
		}
	}
}
