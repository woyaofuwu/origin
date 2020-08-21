package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend.PlatSvcTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

public class PlatSvcRelaAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		// TODO Auto-generated method stub
		List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
		UcaData uca = btd.getRD().getUca();
		//记录平台服务绑定的服务关系数大于1条的处理次数,当关系数==处理次数时,需要删除对应的服务
		IData offerRelCounts = new DataMap();

		for (int k = 0; k < pstds.size(); k++)
		{
			PlatSvcTradeData pstd = pstds.get(k);
			PlatSvcData psd = (PlatSvcData) pstd.getPmd();
			PlatOfficeData officeData = null;
			if (psd != null)
			{
				officeData = psd.getOfficeData();
			}
			if (officeData == null)
			{
				officeData = PlatOfficeData.getInstance(pstd.getElementId());
			}

			//modify by chencheng9 start
			//如果是老的咪咕无线音乐特级会员，则不需要连带退订老特会再开通新特会
			if("81".equals(officeData.getBizTypeCode()) && PlatConstants.OPER_ORDER.equals(pstd.getOperCode()) &&
					"698039".equals(officeData.getSpCode()) && "698039018080000003".equals(officeData.getBizCode()))
			{
				if (isLv3Member(uca))
				{
					CSAppException.apperr(PlatException.CRM_PLAT_74, "该用户已开通老特级会员服务，不需要再次开通新的特级会员服务");
				}
			}
			//modify by chencheng9 end

			IDataset relaLimitDatas = PlatInfoQry.queryRelaLimitPlatSvcs(pstd.getElementId(), pstd.getOperCode(), officeData.getBizTypeCode());

			if (relaLimitDatas != null && relaLimitDatas.size() > 0)
			{
				int size = relaLimitDatas.size();
				// 判断用户如果是做彩印业务的升级操作，则不插21的SVC台账记录 modify by zhangbo18 begin
				boolean oper_flag = false;
				if ("46".equals(officeData.getBizTypeCode()) && PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
				{
					for (int si = 0; si < pstds.size(); si++)
					{
						PlatSvcTradeData p = pstds.get(si);
						PlatOfficeData offData = PlatOfficeData.getInstance(p.getElementId());
						if ("46".equals(offData.getBizTypeCode()) && !PlatConstants.OPER_CANCEL_ORDER.equals(p.getOperCode()))
						{
							oper_flag = true;
							break;
						}
					}
				}
				// modify by zhangbo18 end

				for (int i = 0; i < size; i++)
				{
					IData relaConfig = relaLimitDatas.getData(i);
					String relaState = relaConfig.getString("SVC_STATE", "");
					String limitType = relaConfig.getString("LIMIT_TYPE");
					String flag = relaConfig.getString("RSRV_STR1", ""); // 非TRUE时，不管是PACKAGE_ID为多少，都连带退订，如果TURE，则PACKAGE_ID为50000000才退订
					String pfTag = relaConfig.getString("RSRV_STR2", "1"); // 是否发指令，此处可能需要修改配置表TD_B_PLATSVC_LIMIT，表中原配置为01，发指令
					String infoCode = relaConfig.getString("RSRV_STR3", ""); // 需要匹配属性名
					String infoValue = relaConfig.getString("RSRV_STR4", "");// 需要匹配属性值
					String attrCode = relaConfig.getString("RSRV_STR5", ""); // 属性名
					// 连带开服务或者平台服务，需要加的属性名
					String attrValue = relaConfig.getString("RSRV_STR6", ""); // 属性值
					// 连带开服务或者平台服务，需要加的属性值

					if (!"".equals(infoCode) && !"".equals(infoValue))
					{
						List<AttrTradeData> attrTradeDatas = pstd.getAttrTradeDatas();
						if (attrTradeDatas == null || attrTradeDatas.size() < 0)
						{
							continue;
						} else
						{
							int attrSize = attrTradeDatas.size();
							boolean isFind = false;
							for (int j = 0; j < attrSize; j++)
							{
								// 针对特级无线音乐会员需要绑定彩铃
								AttrTradeData attr = attrTradeDatas.get(j);
								if (attr.getAttrCode().equals(infoCode) && attr.getAttrValue().equals(infoValue) && (BofConst.MODIFY_TAG_ADD.equals(attr.getModifyTag())))
								{
									isFind = true;
									break;
								}
							}
							if (!isFind)
							{
								continue;
							}
						}
					}

					if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(relaConfig.getString("SVC_TYPE")))
					{
						// 如果是彩印业务升级操作，则不插21的SVC台账
						if ("46".equals(officeData.getBizTypeCode()) && "21".equals(relaConfig.getString("SERVICE_ID_L")) && oper_flag)
						{
							continue;
						}


						// 连带处理普通服务
						if ("3".equals(limitType))
						{

							// 连带开
							String modifyTag = "";
							List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(relaConfig.getString("SERVICE_ID_L"));
							if (userSvcs.size() <= 0)
							{
								modifyTag = BofConst.MODIFY_TAG_ADD;

								SvcTradeData std = new SvcTradeData();
								String instId = SeqMgr.getInstId();
								std.setInstId(instId);
								std.setElementId(relaConfig.getString("SERVICE_ID_L"));
								std.setModifyTag(modifyTag);
								if (BofConst.MODIFY_TAG_ADD.equals(pstd.getModifyTag()))
								{
									std.setStartDate(pstd.getStartDate());
								} else
								{
									std.setStartDate(btd.getRD().getAcceptTime());
								}
								std.setEndDate(pstd.getEndDate());
								std.setProductId(PlatConstants.PRODUCT_ID);
								std.setPackageId(PlatConstants.PACKAGE_ID);
								//和飞信办理默认开通VOLTE服务  add by tanjl 20180629
								if("79".equals(officeData.getBizTypeCode())){
									boolean lteTag = PersonUtil.isLteCardUser(uca.getUserId());// 4GLTE卡标识
									if(lteTag){
										std.setEndDate(SysDateMgr.getTheLastTime());
										std.setProductId(uca.getProductId());
										std.setPackageId("-1");
									} else {
										CSAppException.apperr(PlatException.CRM_PLAT_74, "非4G用户无法开通VOLTE功能，无法进行和飞信订购操作！");
									}
								}
								std.setUserId(pstd.getUserId());
								std.setMainTag("0");
								std.setUserIdA("-1");
								btd.add(uca.getSerialNumber(), std);

								// 如果配置了属性，则新增属性
								if (StringUtils.isNotBlank(attrValue) && StringUtils.isNotBlank(attrCode))
								{
									String[] attrCodeArray = attrCode.split(":");
									String[] attrValueArray = attrValue.split(":");

									if (attrCodeArray.length != attrValueArray.length)
									{
										CSAppException.apperr(PlatException.CRM_PLAT_74, "属性名和属性值配置有问题，请配置TD_S_COMPARA表的RSRV_STR5(属性名)" + "RSRV_STR6(属性值)，多个属性请用:分割");
									}

									for (int j = 0; j < attrCodeArray.length; j++)
									{
										AttrTradeData attrTrade = new AttrTradeData();
										attrTrade.setAttrCode(attrCodeArray[j]);
										attrTrade.setAttrValue(attrValueArray[j]);
										attrTrade.setElementId(relaConfig.getString("SERVICE_ID_L"));
										attrTrade.setEndDate(pstd.getEndDate());
										attrTrade.setInstId(SeqMgr.getInstId());
										attrTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
										attrTrade.setRelaInstId(instId);
										attrTrade.setStartDate(pstd.getStartDate());
										attrTrade.setUserId(pstd.getUserId());
										attrTrade.setInstType(BofConst.ELEMENT_TYPE_CODE_SVC);
										btd.add(uca.getSerialNumber(), attrTrade);
									}

								}

								// 生成offer_rel记录
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(relaConfig.getString("SERVICE_ID_L"));
								offerRelTradeData.setRelOfferType(relaConfig.getString("SVC_TYPE"));
								offerRelTradeData.setRelOfferInsId(instId);
								offerRelTradeData.setRelUserId(pstd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), std.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), std.getEndDate()));
								offerRelTradeData.setModifyTag(modifyTag);
								offerRelTradeData.setRemark("平台服务关联订购服务");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);
							} else
							{
								// 用户已经订购了该服务，那么只建立OFFER_REL关系
								SvcTradeData userSvc = userSvcs.get(0);

								boolean flagstime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND).compareTo(userSvc.getStartDate()) > 0 ? false : true;
								if (flagstime)
								{
									// 为了防止预约产品变更订购服务导致数据问题，判断时间报错不允许办理（预约生效）
									CSAppException.apperr(PlatException.CRM_PLAT_74, "新增服务不能生效，因为它所依赖的服务" + relaConfig.getString("SERVICE_ID_L") + "于" + userSvc.getStartDate() + "时间开始，业务不能继续办理");
								}

								boolean flagetime = "2030-12".compareTo(userSvc.getEndDate().substring(0, 7)) > 0 ? true : false;
								if (flagetime)
								{
									// 为了防止预约产品变更订购服务导致数据问题，判断时间报错不允许办理（预约取消）
									CSAppException.apperr(PlatException.CRM_PLAT_74, "新增服务不能生效，因为它所依赖的服务" + relaConfig.getString("SERVICE_ID_L") + "于" + userSvc.getEndDate() + "时间失效，业务不能继续办理");
								}

								// 生成offer_rel记录
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(relaConfig.getString("SERVICE_ID_L"));
								offerRelTradeData.setRelOfferType(relaConfig.getString("SVC_TYPE"));
								offerRelTradeData.setRelOfferInsId(userSvc.getInstId());
								offerRelTradeData.setRelUserId(pstd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), userSvc.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), userSvc.getEndDate()));
								offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								offerRelTradeData.setRemark("平台服务关联订购服务（用户已订购该服务）");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);
							}
						} else if ("4".equals(limitType))
						{

							// 彩印业务分为3种，分别是企业彩印，个人彩印，分众彩印，3种彩印之间不互斥，都会绑定21服务，所以退订的时候要做特殊处理，只有不含有其它彩印服务的情况下才会删除彩印服务
							// 不判断了，但是又查询了platsvc_limit表，故注释IDataset
							// relaLimitServiceDatas
							// =PlatInfoQry.queryRelaLimitPlatSvc_L(relaConfig.getString("SERVICE_ID_L"),PlatConstants.OPER_CANCEL_ORDER,officeData.getBizTypeCode(),uca.getUserId());
							// if(relaLimitServiceDatas.size()>1)
							// {
							// continue;
							// }
							// 连带关
							List<SvcTradeData> stds = uca.getUserSvcBySvcId(relaConfig.getString("SERVICE_ID_L"));
							if (stds.size() > 0)
							{
								SvcTradeData std = stds.get(0);

								// if (("TRUE".equals(flag) &&
								// "50000000".equals(std.getProductId())) ||
								// (!"TRUE".equals(flag)))
								if (std.getModifyTag().equals(BofConst.MODIFY_TAG_USER) || BofConst.MODIFY_TAG_UPD.equals(std.getModifyTag()))
								{
									// 删除offer_rel
									String relOfferInsId = std.getInstId();
									IDataset userOfferRels = UserOfferRelQry.getOfferRelByRelOfferInsId(relOfferInsId);

									IData tempUserOfferRel = new DataMap();
									int count = 0;
									for (int ii = 0; ii < userOfferRels.size(); ii++)
									{
										count++;
										IData userOfferRel = userOfferRels.getData(ii);
										if (pstd != null && StringUtils.isNotEmpty(pstd.getElementType()) && StringUtils.equals(pstd.getElementType(), userOfferRel.getString("OFFER_TYPE", "")) && StringUtils.isNotEmpty(pstd.getElementId()) && StringUtils.endsWith(pstd.getElementId(), userOfferRel.getString("OFFER_CODE", "")))
										{
											if (StringUtils.equals(pstd.getInstId(), userOfferRel.getString("OFFER_INS_ID")))
											{
												tempUserOfferRel = userOfferRel;
											}
										}
									}

									SvcTradeData newStd = std.clone();
									newStd.setModifyTag(BofConst.MODIFY_TAG_DEL);
									newStd.setEndDate(pstd.getOperTime());// 立即终止
									// 单条记录，删除svc，删除offer_rel
									if (count == 1)
									{
										btd.add(uca.getSerialNumber(), newStd);

										// 删除offer_rel单条不需要特殊处理，公共已经处理
										/*
										 * if (null != tempUserOfferRel &&
										 * tempUserOfferRel.size() > 0) {
										 * OfferRelTradeData offerRelTradeData =
										 * new
										 * OfferRelTradeData(tempUserOfferRel);
										 * offerRelTradeData
										 * .setModifyTag(BofConst
										 * .MODIFY_TAG_DEL);
										 * offerRelTradeData.setEndDate
										 * (calculateEndDate(pstd.getEndDate(),
										 * newStd.getEndDate()));// 立即终止
										 * offerRelTradeData
										 * .setRemark("平台服务关联退订服务");
										 * btd.add(uca.getSerialNumber(),
										 * offerRelTradeData); }
										 */

									}

									if (count > 1)
									{
										// 只删除对应的这条offer_rel，不删除svc
										if (null != tempUserOfferRel && tempUserOfferRel.size() > 0)
										{
											OfferRelTradeData offerRelTradeData = new OfferRelTradeData(tempUserOfferRel);
											offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
											offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), newStd.getEndDate()));// 立即终止
											offerRelTradeData.setRemark("平台服务关联退订服务");
											btd.add(uca.getSerialNumber(), offerRelTradeData);
											//QR-20191113-03 用户取消平台业务后，没有关联吧彩印服务取消start
											if(offerRelCounts.containsKey(relOfferInsId)){
												offerRelCounts.put(relOfferInsId,offerRelCounts.getInt(relOfferInsId)+1);
											}else{
												offerRelCounts.put(relOfferInsId,1);
											}
											if(count==offerRelCounts.getInt(relOfferInsId)){
												btd.add(uca.getSerialNumber(), newStd);
											}
											//QR-20191113-03 用户取消平台业务后，没有关联吧彩印服务取消end
										}
									}

								}
							}
						}
					} else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(relaConfig.getString("SVC_TYPE")))
					{

						// 连带处理平台服务
						if ("3".equals(limitType))
						{
							// 如果局数据不存在，则不连带开
							// PlatOfficeData.getInstance(relaConfig.getString("SERVICE_ID_L"));
							IDataset platSvcs = BofQuery.getPlatInfoByServiceId(relaConfig.getString("SERVICE_ID_L"));
							if (platSvcs == null || platSvcs.size() == 0)
							{
								continue;
							}

							// 如果是批量处理默认开通，不处理
							if ("SS.PlatBatchIBossRegIntfSVC.tradeReg".equals(CSBizBean.getVisit().getXTransCode()))
							{
								continue;
							}


							List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relaConfig.getString("SERVICE_ID_L"));
							if (userPlatSvcs == null || userPlatSvcs.size() <= 0)
							{
								PlatSvcTradeData newPstd = new PlatSvcTradeData();
								// 订购服务
								newPstd.setElementId(relaConfig.getString("SERVICE_ID_L"));
								newPstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
								newPstd.setUserId(uca.getUserId());
								newPstd.setBizStateCode(PlatConstants.STATE_OK);
								newPstd.setProductId(pstd.getProductId());
								newPstd.setPackageId(pstd.getPackageId());
								newPstd.setStartDate(pstd.getStartDate());
								newPstd.setEndDate(pstd.getEndDate());
								String instId = SeqMgr.getInstId();
								newPstd.setInstId(instId);
								newPstd.setActiveTag(pstd.getActiveTag());// 主被动标记
								newPstd.setOperTime(pstd.getOperTime());
								newPstd.setPkgSeq(pstd.getPkgSeq());// 批次号，批量业务时传值
								newPstd.setUdsum(pstd.getUdsum());// 批次数量
								newPstd.setIntfTradeId(pstd.getIntfTradeId());
								newPstd.setOperCode(PlatConstants.OPER_ORDER);
								newPstd.setOprSource(pstd.getOprSource());
								newPstd.setIsNeedPf(pfTag);
								PlatSvcTrade.dealFirstTime(newPstd, uca); // 处理首次订购时间，连带开的可能首次订购时间为空
								btd.add(uca.getSerialNumber(), newPstd);

								// 如果配置了属性，则新增属性
								// 如果配置了属性，则新增属性
								if (StringUtils.isNotBlank(attrValue) && StringUtils.isNotBlank(attrCode))
								{
									String[] attrCodeArray = attrCode.split(":");
									String[] attrValueArray = attrValue.split(":");

									if (attrCodeArray.length != attrValueArray.length)
									{
										CSAppException.apperr(PlatException.CRM_PLAT_74, "属性名和属性值配置有问题，请配置TD_S_COMPARA表的RSRV_STR5(属性名)" + "RSRV_STR6(属性值)，多个属性请用:分割");
									}

									for (int j = 0; j < attrCodeArray.length; j++)
									{
										AttrTradeData attrTrade = new AttrTradeData();
										attrTrade.setAttrCode(attrCodeArray[j]);
										attrTrade.setAttrValue(attrValueArray[j]);
										attrTrade.setElementId(relaConfig.getString("SERVICE_ID_L"));
										attrTrade.setEndDate(pstd.getEndDate());
										attrTrade.setInstId(SeqMgr.getInstId());
										attrTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
										attrTrade.setRelaInstId(instId);
										attrTrade.setStartDate(pstd.getStartDate());
										attrTrade.setUserId(pstd.getUserId());
										attrTrade.setInstType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
										btd.add(uca.getSerialNumber(), attrTrade);
									}
								}

								// 订购生成offer_rel关系
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(relaConfig.getString("SERVICE_ID_L"));
								offerRelTradeData.setRelOfferType(relaConfig.getString("SVC_TYPE"));
								offerRelTradeData.setRelOfferInsId(instId);
								offerRelTradeData.setRelUserId(pstd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), newPstd.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), newPstd.getEndDate()));
								offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								offerRelTradeData.setRemark("平台服务关联订购平台服务");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);
							} else
							{
								// 如果本来就存在，但是是暂停状态，则恢复
								PlatSvcTradeData userPlatSvc = userPlatSvcs.get(0);
								if ("N".equals(userPlatSvc.getBizStateCode()))
								{
									String modifyTag = userPlatSvc.getModifyTag();
									userPlatSvc.setOperCode(PlatConstants.OPER_RESTORE);
									userPlatSvc.setStartDate(pstd.getStartDate());
									userPlatSvc.setEndDate(pstd.getEndDate());
									userPlatSvc.setBizStateCode(PlatConstants.STATE_OK);
									userPlatSvc.setOperTime(btd.getRD().getAcceptTime());
									userPlatSvc.setModifyTag(BofConst.MODIFY_TAG_UPD);
									userPlatSvc.setOprSource(pstd.getOprSource());
									userPlatSvc.setIsNeedPf(pfTag);

									if (BofConst.MODIFY_TAG_USER.equals(modifyTag))
									{
										btd.add(uca.getSerialNumber(), userPlatSvc);
									}

								}

								// 增加OFFER_REL处理
								OfferRelTradeData offerRelTradeData = new OfferRelTradeData();

								offerRelTradeData.setUserId(pstd.getUserId());
								offerRelTradeData.setOfferCode(pstd.getElementId());
								offerRelTradeData.setOfferType(pstd.getElementType());
								offerRelTradeData.setOfferInsId(pstd.getInstId());
								offerRelTradeData.setGroupId("-1");// 连带关系groupId
								// 设置为-1
								offerRelTradeData.setRelOfferCode(relaConfig.getString("SERVICE_ID_L"));
								offerRelTradeData.setRelOfferType(relaConfig.getString("SVC_TYPE"));
								offerRelTradeData.setRelOfferInsId(userPlatSvc.getInstId());
								offerRelTradeData.setRelUserId(pstd.getUserId());
								offerRelTradeData.setRelType(PlatConstants.OFFER_REL_TYPE);
								offerRelTradeData.setStartDate(calculateStartDate(pstd.getStartDate(), userPlatSvc.getStartDate()));
								offerRelTradeData.setEndDate(calculateEndDate(pstd.getEndDate(), userPlatSvc.getEndDate()));
								offerRelTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
								offerRelTradeData.setRemark("平台服务关联订购平台服务");
								offerRelTradeData.setInstId(SeqMgr.getInstId());

								btd.add(uca.getSerialNumber(), offerRelTradeData);
							}
						} else if ("4".equals(limitType))
						{
							List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relaConfig.getString("SERVICE_ID_L"));

							if (userPlatSvcs != null && userPlatSvcs.size() > 0)
							{
								PlatSvcTradeData userPlatSvc = userPlatSvcs.get(0);
								if (BofConst.MODIFY_TAG_USER.equals(userPlatSvc.getModifyTag()) || BofConst.MODIFY_TAG_UPD.equals(userPlatSvc.getModifyTag()))
								{
									String modifyTag = userPlatSvc.getModifyTag();
									// 彩印业务，如果当月存在多次变更，则已经指定月底失效的服务不再插工单记录
									boolean add_flag = true;
									if ("46".equals(psd.getOfficeData().getBizTypeCode()))
									{
										if (userPlatSvc.getEndDate().compareTo(SysDateMgr.getLastDateThisMonth()) < 1)
										{
											add_flag = false;
										}
									}
									// 订购服务
									userPlatSvc.setModifyTag(BofConst.MODIFY_TAG_DEL);
									userPlatSvc.setBizStateCode(PlatConstants.STATE_CANCEL);
									userPlatSvc.setEndDate(pstd.getOperTime());
									if (psd != null && ("78".equals(psd.getOfficeData().getBizTypeCode()) || "46".equals(psd.getOfficeData().getBizTypeCode())))
									{
										userPlatSvc.setBizStateCode(PlatConstants.STATE_OK);
										userPlatSvc.setEndDate(SysDateMgr.getLastDateThisMonth());
										pstd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
									}
									userPlatSvc.setActiveTag(pstd.getActiveTag());// 主被动标记
									userPlatSvc.setOperTime(pstd.getOperTime());
									userPlatSvc.setPkgSeq(pstd.getPkgSeq());// 批次号，批量业务时传值
									userPlatSvc.setUdsum(pstd.getUdsum());// 批次数量
									userPlatSvc.setIntfTradeId(pstd.getIntfTradeId());
									userPlatSvc.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
									userPlatSvc.setOprSource(pstd.getOprSource());
									userPlatSvc.setIsNeedPf(pfTag);

									if (BofConst.MODIFY_TAG_USER.equals(modifyTag))
									{

										if ("46".equals(psd.getOfficeData().getBizTypeCode()))
										{
											if (add_flag)
											{
												btd.add(uca.getSerialNumber(), userPlatSvc);
											}
										} else
										{
											btd.add(uca.getSerialNumber(), userPlatSvc);
										}
									}

								}
							}
						}
					}
				}
			}
		}
	}

	public static String calculateStartDate(String fromStartDate, String toStartDate) throws Exception
	{
		return fromStartDate.compareTo(toStartDate) > 0 ? fromStartDate : toStartDate;
	}

	public static String calculateEndDate(String fromEndDate, String toEndDate) throws Exception
	{
		return fromEndDate.compareTo(toEndDate) > 0 ? toEndDate : fromEndDate;
	}


	/**
	 * 判断用户是否已开通老的咪咕无线音乐特级会员平台服务
	 * @param uca
	 * @return
	 * @throws Exception
	 */
	private boolean isLv3Member(UcaData uca) throws Exception {
		boolean isLv3Member = false;
		List<PlatSvcTradeData> memberPlatSvcs = uca.getUserPlatSvcByServiceId("98001901");
		if(memberPlatSvcs != null && memberPlatSvcs.size() > 0) {
			PlatSvcTradeData memberPlatSvc = memberPlatSvcs.get(0);
			List<AttrTradeData> memberLevelList = uca.getUserAttrsByRelaInstId(memberPlatSvc.getInstId());
			AttrTradeData memberLevel = memberLevelList.get(0);
			if ("302".equals(memberLevel.getAttrCode()) && "3".equals(memberLevel.getAttrValue())) {
				isLv3Member = true;
			}
		}
		return isLv3Member;
	}

}
