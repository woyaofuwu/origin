package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct; 

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.VideoFlowConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChangeProductVideoFlowDiscntBean extends CSBizBean {
 
	private static final Logger log = Logger.getLogger(ChangeProductVideoFlowDiscntBean.class);
	 
	/**
	 * @description 
	 * 	针对对视频流量业务资费包特殊校验
	 * @author chenmw3
	 * @date 2017-4-21
	 * @param tableData
	 * @param btd
	 * @return
	 * @throws Exception
	 */
	public void checkVideoFlowDiscnt(IData tableData, BusiTradeData<?> btd) throws Exception{
		//1、取出资费信息
		//资费台账
		IDataset discntTradeInfos = tableData.getDataset(TradeTableEnum.TRADE_DISCNT.getValue());
		//属性台账
		IDataset attrTradeInfos = tableData.getDataset(TradeTableEnum.TRADE_ATTR.getValue());
		if(IDataUtil.isEmpty(discntTradeInfos)){
			return;
		}
		//用户地州
		String userId = btd.getRD().getUca().getUser().getUserId();
		String eparchyCode = btd.getRD().getUca().getUser().getEparchyCode();
		//深度克隆资费台账、属性台账
		IDataset discntTradeInfosClone = (IDataset)Clone.deepClone(discntTradeInfos);
		IDataset attrTradeInfosClone = (IDataset)Clone.deepClone(attrTradeInfos);
		
		//2、取出视频流量资费信息
		IDataset discntConfigInfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "2017" ,"IS_VIDEO_PKG", eparchyCode, null);
		if(IDataUtil.isEmpty(discntConfigInfos)){
			return;
		}
		IDataset videoFlowDiscntInfos = new DatasetList();
		for(int i=0,sizeI=discntTradeInfosClone.size();i<sizeI;i++){
			IData discntTradeInfoClone = discntTradeInfosClone.getData(i);
			String instId = discntTradeInfoClone.getString("INST_ID");
			IData discntConfigInfo = this.getDiscntConfigInfo(discntTradeInfoClone, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo)){
				continue;
			}
			String discntName = discntConfigInfo.getString("PARAM_NAME");
			String ctrmProductId = discntConfigInfo.getString("PARA_CODE2");
			String derateDincntCode = discntConfigInfo.getString("PARA_CODE3","");
			String appServiceIds = discntConfigInfo.getString("PARA_CODE20","");
			IDataset videoFlowDiscntAttrInfos = new DatasetList();
			discntTradeInfoClone.put("TARGET_INDEX", i);
			discntTradeInfoClone.put("DISCNT_NAME", discntName);
			discntTradeInfoClone.put("CTRM_PRODUCT_ID", ctrmProductId);
			discntTradeInfoClone.put("DERATE_DINCNT_CODE", derateDincntCode);
			discntTradeInfoClone.put("APP_SERVICE_IDS", appServiceIds);
			discntTradeInfoClone.put("videoFlowDiscntAttrInfos", videoFlowDiscntAttrInfos);
			if(StringUtils.isNotBlank(appServiceIds)){
				videoFlowDiscntInfos.add(discntTradeInfoClone);
				continue;
			}
			if(IDataUtil.isEmpty(attrTradeInfosClone)){
				continue;
			}
			for(int j=0,sizeJ=attrTradeInfosClone.size();j<sizeJ;j++){
				IData attrTradeInfoClone = attrTradeInfosClone.getData(j);
				String relaInstId = attrTradeInfoClone.getString("RELA_INST_ID");
				String attrCode = attrTradeInfoClone.getString("ATTR_CODE");
				if(!StringUtils.equals(instId, relaInstId)){
					continue;
				}
				if(StringUtils.equals(VideoFlowConst.discntAttrCodeService1, attrCode)||StringUtils.equals(VideoFlowConst.discntAttrCodeService2, attrCode)||StringUtils.equals(VideoFlowConst.discntAttrCodeService3, attrCode)){
					attrTradeInfoClone.put("TARGET_INDEX", j);
					videoFlowDiscntAttrInfos.add(attrTradeInfoClone);
				}
			}
			if(IDataUtil.isNotEmpty(videoFlowDiscntAttrInfos)){
				videoFlowDiscntInfos.add(discntTradeInfoClone);
				continue;
			}
		}
		if(IDataUtil.isEmpty(videoFlowDiscntInfos)){
			return;
		}
		
		//3、对视频流量资费进行批量校验
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("EPARCHY_CODE", eparchyCode);
		inparam.put("videoFlowDiscntInfos", videoFlowDiscntInfos);
		inparam.put("discntConfigInfos", discntConfigInfos);
		IData batCheckResult = this.batCheckVideoFlowDiscntIntf(inparam);
		String batCheckResultCode = batCheckResult.getString("BAT_CHECK_RESULT_CODE");
		String batCheckResultInfo = batCheckResult.getString("BAT_CHECK_RESULT_INFO");
		if(!StringUtils.equals("0", batCheckResultCode)){
			Utility.error("-1", null, batCheckResultInfo);
			return;
		}
		
		//4、组装视频流量资费和属性
		videoFlowDiscntInfos = batCheckResult.getDataset("videoFlowDiscntInfos");
		for(int i=0,sizeI=videoFlowDiscntInfos.size();i<sizeI;i++){
			IData newDiscntInfo = videoFlowDiscntInfos.getData(i);
			int discntTargetIndex = newDiscntInfo.getInt("TARGET_INDEX");
			discntTradeInfos.getData(discntTargetIndex).put("START_DATE", newDiscntInfo.getString("START_DATE"));
			discntTradeInfos.getData(discntTargetIndex).put("END_DATE", newDiscntInfo.getString("END_DATE"));
			discntTradeInfos.getData(discntTargetIndex).put("MODIFY_TAG", newDiscntInfo.getString("MODIFY_TAG"));
			IDataset newDiscntAttrInfos = newDiscntInfo.getDataset("videoFlowDiscntAttrInfos", new DatasetList());
			for(int j=0,sizeJ=newDiscntAttrInfos.size();j<sizeJ;j++){
				IData newDiscntAttrInfo = newDiscntAttrInfos.getData(j);
				int discntAttrTargetIndex = newDiscntAttrInfo.getInt("TARGET_INDEX");
				attrTradeInfos.getData(discntAttrTargetIndex).put("START_DATE", newDiscntAttrInfo.getString("START_DATE"));
				attrTradeInfos.getData(discntAttrTargetIndex).put("END_DATE", newDiscntAttrInfo.getString("END_DATE"));
				attrTradeInfos.getData(discntAttrTargetIndex).put("MODIFY_TAG", newDiscntAttrInfo.getString("MODIFY_TAG"));
			}
		}
		//新资费、新资费属性操作
		IDataset newTradeDiscntInfos = batCheckResult.getDataset("newTradeDiscntInfos",new DatasetList());
		IDataset newTradeDiscntAttrInfos = batCheckResult.getDataset("newTradeDiscntAttrInfos",new DatasetList());
		discntTradeInfos.addAll(newTradeDiscntInfos);
		if(IDataUtil.isNotEmpty(attrTradeInfos)){
			attrTradeInfos.addAll(newTradeDiscntAttrInfos);
		}
		
		this.dealDiscntStartDate(discntTradeInfos,btd.getRD().getUca());
		
		//目前修改的生效时间是tabledata里面的数据，需处理btd里面的数据修改成一致，否则校验会报错
		List<DiscntTradeData> tradeDiscnts = (List<DiscntTradeData>) btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if(tradeDiscnts.size()>0 && tradeDiscnts != null){
			for(int i = 0; i < discntTradeInfos.size(); i++){
				IData discntTradeInfo = discntTradeInfos.getData(i);
				String modifyTag = discntTradeInfo.getString("MODIFY_TAG");
				String elementId = discntTradeInfo.getString("DISCNT_CODE");
				String startDate = discntTradeInfo.getString("START_DATE");
				String endDate = discntTradeInfo.getString("END_DATE");

				for(int j = 0; j < tradeDiscnts.size(); j++){
					DiscntTradeData tradeDiscnt = tradeDiscnts.get(j);
					String modifyTag1 = tradeDiscnt.getModifyTag();
					String elementId1 = tradeDiscnt.getElementId();
					String startDate1 = tradeDiscnt.getStartDate();
					String endDate1 = tradeDiscnt.getEndDate();

					if(modifyTag.equals(modifyTag1) && elementId.equals(elementId1) && startDate.equals(startDate1) && endDate.equals(endDate1)){
						continue ;
					}
					if (modifyTag.equals(modifyTag1) && elementId.equals(elementId1) && endDate.equals(endDate1) 
							&&((startDate.compareTo(startDate1) <= 0) || (startDate.compareTo(startDate1) >= 0))){
						tradeDiscnt.setStartDate(startDate);
					}
				}
			}
		}
		
	}
	
	/**
	 * @description 
	 * 	对视频流量业务资费包的批量校验
	 * 		1、资费订购校验:
	 * 			(1) 9元资费属性：必传为1个，可传为1个；支持3次订购
	 *			(2) 18元资费属性：必传为1个，可传为2个；支持1次订购
	 *			(3) 24元资费属性：必传为1个；支持1次订购
	 *		2、资费变更校验
	 *
	 * @author chenmw3
	 * @date 2017-4-21
	 * @param inparams
	 * @return BAT_CHECK_RESULT_CODE 
	 * 		0-全部校验通过
	 * 		1-存在校验失败
	 * @throws Exception
	 */
	public IData batCheckVideoFlowDiscntIntf(IData inparam) throws Exception{
		IData returnInfo = new DataMap();
		IDataset newTradeDiscntInfos = new DatasetList();
		IDataset newTradeDiscntAttrInfos = new DatasetList();
		returnInfo.put("newTradeDiscntInfos", newTradeDiscntInfos);
		returnInfo.put("newTradeDiscntAttrInfos", newTradeDiscntAttrInfos);
		
		//1、接口必传参数校验
		String userId = inparam.getString("USER_ID");
		String eparchyCode = inparam.getString("EPARCHY_CODE");
		IDataset videoFlowDiscntInfos = inparam.getDataset("videoFlowDiscntInfos");
		IDataset discntConfigInfos = inparam.getDataset("discntConfigInfos");
		returnInfo.put("USER_ID", userId);
		returnInfo.put("EPARCHY_CODE", eparchyCode);
		returnInfo.put("videoFlowDiscntInfos", videoFlowDiscntInfos);
		returnInfo.put("discntConfigInfos", discntConfigInfos);
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(eparchyCode) || IDataUtil.isEmpty(videoFlowDiscntInfos) || IDataUtil.isEmpty(discntConfigInfos)){
			returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
			returnInfo.put("BAT_CHECK_RESULT_INFO", "必传参数校验失败！USER_ID,EPARCHY_CODE,videoFlowDiscntInfos,discntConfigInfos都是必传参数！");
			return returnInfo;
		}
		
		//2、查询用户全部资费，筛选出当前生效的视频流量的资费信息和属性信息
		IDataset userVideoFlowDiscntInfos = this.getUserVideoFlowDiscntInfos(inparam);
		
		//3、批量视频资费自身互斥判断
		IData batCheckResult = this.batCheckVideoFlowDiscntBetweenIntf(inparam, newTradeDiscntInfos, userVideoFlowDiscntInfos);
		String batCheckResultCode = batCheckResult.getString("BAT_CHECK_RESULT_CODE");
		if(!StringUtils.equals("0", batCheckResultCode)){
			return batCheckResult;
		}
		
		//4、批量视频资费校验
		for(int i=0,sizeI=videoFlowDiscntInfos.size();i<sizeI;i++){
			IData discntInfo = videoFlowDiscntInfos.getData(i);
			discntInfo.put("EPARCHY_CODE", eparchyCode);
			discntInfo.put("userVideoFlowDiscntInfos", userVideoFlowDiscntInfos);
			discntInfo.put("discntConfigInfos", discntConfigInfos);
			IData checkResult = this.checkVideoFlowDiscntIntf(discntInfo);
			String checkResultCode = checkResult.getString("CHECK_RESULT_CODE");
			String checkResultInfo = checkResult.getString("CHECK_RESULT_INFO");
			if(!StringUtils.equals("0", checkResultCode)){
				returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
				returnInfo.put("BAT_CHECK_RESULT_INFO", checkResultInfo);
				return returnInfo;
			}
			
			//新增资费
			IDataset newTradeDiscntInfosSon = checkResult.getDataset("newTradeDiscntInfos",new DatasetList());
			if(IDataUtil.isNotEmpty(newTradeDiscntInfosSon)){
				for(int j=0,sizeJ=newTradeDiscntInfosSon.size();j<sizeJ;j++){
					IData newTradeDiscntInfo = newTradeDiscntInfosSon.getData(j);
					newTradeDiscntInfo.put("TRADE_ID", discntInfo.getString("TRADE_ID"));
					newTradeDiscntInfo.put("ACCEPT_MONTH", discntInfo.getString("ACCEPT_MONTH"));
					newTradeDiscntInfo.put("UPDATE_TIME", discntInfo.getString("UPDATE_TIME"));
					newTradeDiscntInfo.put("UPDATE_STAFF_ID", discntInfo.getString("UPDATE_STAFF_ID"));
					newTradeDiscntInfo.put("UPDATE_DEPART_ID", discntInfo.getString("UPDATE_DEPART_ID"));
					newTradeDiscntInfo.put("REMARK", discntInfo.getString("REMARK"));
					newTradeDiscntInfos.add(newTradeDiscntInfo);
				}
			}
			
			//新增资费属性
			IDataset newTradeDiscntAttrInfosSon = checkResult.getDataset("newTradeDiscntAttrInfos",new DatasetList());
			if(IDataUtil.isNotEmpty(newTradeDiscntAttrInfosSon)){
				for(int j=0,sizeJ=newTradeDiscntAttrInfosSon.size();j<sizeJ;j++){
					IData newTradeDiscntAttrInfo = newTradeDiscntAttrInfosSon.getData(j);
					newTradeDiscntAttrInfo.put("TRADE_ID", discntInfo.getString("TRADE_ID"));
					newTradeDiscntAttrInfo.put("ACCEPT_MONTH", discntInfo.getString("ACCEPT_MONTH"));
					newTradeDiscntAttrInfo.put("UPDATE_TIME", discntInfo.getString("UPDATE_TIME"));
					newTradeDiscntAttrInfo.put("UPDATE_STAFF_ID", discntInfo.getString("UPDATE_STAFF_ID"));
					newTradeDiscntAttrInfo.put("UPDATE_DEPART_ID", discntInfo.getString("UPDATE_DEPART_ID"));
					newTradeDiscntAttrInfo.put("REMARK", discntInfo.getString("REMARK"));
					newTradeDiscntAttrInfos.add(newTradeDiscntAttrInfo);
				}
			}
		}
		
		returnInfo.put("BAT_CHECK_RESULT_CODE", "0");
		returnInfo.put("BAT_CHECK_RESULT_INFO", "全部产品校验通过!");
		return returnInfo;
	}

	/**
	 * @description 
	 * 	对视频流量资费间的互斥关系校验
	 * @author chenmw3
	 * @date 2017-4-21
	 * @param inparams
	 * @return BAT_CHECK_RESULT_CODE 
	 * 		0-全部校验通过
	 * 		1-全部校验不通过
	 * @throws Exception
	 */
	private IData batCheckVideoFlowDiscntBetweenIntf(IData inparam, IDataset newTradeDiscntInfos, IDataset userVideoFlowDiscntInfos) throws Exception{
		IData returnInfo = new DataMap();
		//1、校验接口必传参数
		String userId = inparam.getString("USER_ID");
		String eparchyCode = inparam.getString("EPARCHY_CODE");
		IDataset videoFlowDiscntInfos = inparam.getDataset("videoFlowDiscntInfos");
		IDataset discntConfigInfos = inparam.getDataset("discntConfigInfos");
		returnInfo.put("USER_ID", userId);
		returnInfo.put("EPARCHY_CODE", eparchyCode);
		returnInfo.put("videoFlowDiscntInfos", videoFlowDiscntInfos);
		returnInfo.put("discntConfigInfos", discntConfigInfos);
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(eparchyCode) || IDataUtil.isEmpty(videoFlowDiscntInfos) || IDataUtil.isEmpty(discntConfigInfos)){
			returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
			returnInfo.put("BAT_CHECK_RESULT_INFO", "必传参数校验失败！USER_ID,EPARCHY_CODE,videoFlowDiscntInfos,discntConfigInfos都是必传参数！");
			return returnInfo;
		}
		
		//2、校验订购次数
		int orderCount_9 = this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_9, videoFlowDiscntInfos) + this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_9, userVideoFlowDiscntInfos);
/*		if(orderCount_9>3){
			returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
			returnInfo.put("BAT_CHECK_RESULT_INFO", "订购次数校验失败！9元视频流量资费，您已经订购了3个，不能再次订购了！");
			return returnInfo;
		}*/
		int orderCount_18 = getDiscntOrderCount(VideoFlowConst.ctrmProductId_18, videoFlowDiscntInfos) + this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_18, userVideoFlowDiscntInfos);
		if(orderCount_18>1){
			returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
			returnInfo.put("BAT_CHECK_RESULT_INFO", "订购次数校验失败！18元视频流量资费，您已经订购了1个，不能再次订购了！");
			return returnInfo;
		}
		
		if(log.isDebugEnabled()){
			log.debug("ChangeProductVideoFlowDiscntBean batCheckVideoFlowDiscntBetweenIntf videoFlowDiscntInfos = "+videoFlowDiscntInfos);
			log.debug("ChangeProductVideoFlowDiscntBean batCheckVideoFlowDiscntBetweenIntf userVideoFlowDiscntInfos = "+userVideoFlowDiscntInfos);
		}
		
		int orderCount_24_1 = this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_24, videoFlowDiscntInfos);
		int orderCount_24_2 = this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_24, userVideoFlowDiscntInfos);
		
		if(log.isDebugEnabled()){
			log.debug("ChangeProductVideoFlowDiscntBean batCheckVideoFlowDiscntBetweenIntf orderCount_24_1 = "+orderCount_24_1);
			log.debug("ChangeProductVideoFlowDiscntBean batCheckVideoFlowDiscntBetweenIntf orderCount_24_2 = "+orderCount_24_2);
		}
		
		int orderCount_24 = orderCount_24_1 + orderCount_24_2;
/*		if(orderCount_24>1){
			returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
			returnInfo.put("BAT_CHECK_RESULT_INFO", "订购次数校验失败！24元视频流量资费，您已经订购了1个，不能再次订购了！");
			return returnInfo;
		}*/
		
		//3、入参列表中资费+APP的互斥校验
		IDataset videoFlowDiscntInfos_9 = new DatasetList();
		for(int i=0,sizeI=videoFlowDiscntInfos.size();i<sizeI;i++){
			//9元资费信息
			IData videoFlowDiscntInfo_9 = videoFlowDiscntInfos.getData(i);
			String ctrmProductId_9 = videoFlowDiscntInfo_9.getString("CTRM_PRODUCT_ID");
			String discntCode_9 = videoFlowDiscntInfo_9.getString("DISCNT_CODE");
			String discntName_9 = videoFlowDiscntInfo_9.getString("DISCNT_NAME");
			String appServiceId_9 = videoFlowDiscntInfo_9.getString("APP_SERVICE_IDS");
			String modifyTag_9 = videoFlowDiscntInfo_9.getString("MODIFY_TAG");
			IDataset videoFlowDiscntAttrInfos_9 = videoFlowDiscntInfo_9.getDataset("videoFlowDiscntAttrInfos", new DatasetList());
			if(!VideoFlowConst.ctrmProductId_9.equals(ctrmProductId_9)){
				continue;
			}
			if(BofConst.MODIFY_TAG_ADD.equals(modifyTag_9) || BofConst.MODIFY_TAG_UPD.equals(modifyTag_9)){
				videoFlowDiscntInfos_9.add(videoFlowDiscntInfo_9);
			}
			
			//2.1 获取相应的资费属性或者资费编码
			String configDiscntCode_24 = ""; 
			if(IDataUtil.isEmpty(videoFlowDiscntAttrInfos_9)){
				if(StringUtils.isBlank(appServiceId_9)){
					continue;
				}
				//查找9元资费的互斥的24元资费编码
				IData discntConfigInfo_24 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_24, appServiceId_9, discntConfigInfos);
				if(IDataUtil.isEmpty(discntConfigInfo_24)){
					continue;
				}
				configDiscntCode_24 = discntConfigInfo_24.getString("PARAM_CODE");
			}else{
				appServiceId_9 = videoFlowDiscntAttrInfos_9.getData(0).getString("ATTR_VALUE");
			}
			if(StringUtils.isBlank(appServiceId_9)){
				continue;
			}
			
			//2.2 对9元包 存在资费属性 和 不存在属性 情况分别判断 互斥关系
			for(int j=0,sizeJ=videoFlowDiscntInfos.size();j<sizeJ;j++){
				//24元资费信息
				IData videoFlowDiscntInfo_24 = videoFlowDiscntInfos.getData(j);
				String ctrmProductId_24 = videoFlowDiscntInfo_24.getString("CTRM_PRODUCT_ID");
				String discntCode_24 = videoFlowDiscntInfo_24.getString("DISCNT_CODE");
				String discntName_24 = videoFlowDiscntInfo_24.getString("DISCNT_NAME");
				String appServiceId_24 = videoFlowDiscntInfo_24.getString("APP_SERVICE_IDS");
				IDataset videoFlowDiscntAttrInfos_24 = videoFlowDiscntInfo_24.getDataset("videoFlowDiscntAttrInfos", new DatasetList());
				if(!VideoFlowConst.ctrmProductId_24.equals(ctrmProductId_24)){
					continue;
				}
				if(IDataUtil.isNotEmpty(videoFlowDiscntAttrInfos_9)){
					if(IDataUtil.isNotEmpty(videoFlowDiscntAttrInfos_24)){
						appServiceId_24 = videoFlowDiscntAttrInfos_24.getData(0).getString("ATTR_VALUE");
					}
					if(StringUtils.isBlank(appServiceId_24)){
						continue;
					}
					if(appServiceId_9.equals(appServiceId_24)){
						returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
						returnInfo.put("BAT_CHECK_RESULT_INFO", "当前订购的【"+discntName_9+"["+discntCode_9+"]+APP["+appServiceId_9+"]】与当前订购的【"+discntName_24+"["+discntCode_24+"]+APP["+appServiceId_24+"]】存在互斥关系!");
						return returnInfo;
					}
				}else{
					if(configDiscntCode_24.equals(discntCode_24)){
						returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
						returnInfo.put("BAT_CHECK_RESULT_INFO", "当前订购的【"+discntName_9+"["+discntCode_9+"]】与当前订购的【"+discntName_24+"["+discntCode_24+"]】存在互斥关系!");
						return returnInfo;
					}
				}
			}
		}
		//9元叠加包自身互斥校验
		if(IDataUtil.isNotEmpty(videoFlowDiscntInfos_9) && videoFlowDiscntInfos_9.size()>1){
			String[] appServiceIdArr_9 = new String[videoFlowDiscntInfos_9.size()];
			for(int i=0,size=videoFlowDiscntInfos_9.size();i<size;i++){
				IData videoFlowDiscntInfo_9 = videoFlowDiscntInfos_9.getData(i);
				IDataset videoFlowDiscntAttrInfos_9 = videoFlowDiscntInfo_9.getDataset("videoFlowDiscntAttrInfos", new DatasetList());
				String appServiceId_9 = videoFlowDiscntInfo_9.getString("APP_SERVICE_IDS","");
				if(IDataUtil.isNotEmpty(videoFlowDiscntAttrInfos_9)){
					appServiceId_9 = videoFlowDiscntAttrInfos_9.getData(0).getString("ATTR_VALUE","");
				}
				appServiceIdArr_9[i] = appServiceId_9;
			}
			//校验资费属性值：除特殊值之外和空字符串外，资费属性值两两不能相同判断
			boolean isSame = isStrTwoSame(VideoFlowConst.discntAttrSpecialValueService, appServiceIdArr_9);
			if(isSame){
				returnInfo.put("BAT_CHECK_RESULT_CODE", "1");
				returnInfo.put("BAT_CHECK_RESULT_INFO", "资费属性值校验失败！订购9元视频流量资费叠加包时，资费适配的APP重复，请检查填写的资费属性!");
				return returnInfo;
			}
		}
		/*
		//4、校验当前如果是一次性订购了3次9元资费，则需要挂9元的减免资费
		if(orderCount_9 == 3){
			for(int i=0,size=videoFlowDiscntInfos.size();i<size;i++){
				IData discntInfo = videoFlowDiscntInfos.getData(i);
				String ctrmProductId = discntInfo.getString("CTRM_PRODUCT_ID");
				String modifyTag = discntInfo.getString("MODIFY_TAG");
				if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && VideoFlowConst.ctrmProductId_9.equals(ctrmProductId)){
					this.dealDiscntAdd_9_Derate(discntInfo, newTradeDiscntInfos);
					break;
				}
			}
		}
		*/
		returnInfo.put("BAT_CHECK_RESULT_CODE", "0");
		returnInfo.put("BAT_CHECK_RESULT_INFO", "传入的视频资费间不存在互斥关系!");
		return returnInfo;
	}
	
	/**
	 * @description 
	 * 	对视频流量业务资费包的校验
	 * 	1、订购资费属性数、订购次数
	 * 	2、资费包互斥关系
	 * 	3、优先级、生效时间
	 * 
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param inparam
	 * @return CHECK_RESULT_CODE 
	 * 		0-校验通过
	 * 		非0-校验不通过
	 * @throws Exception 
	 */
	public IData checkVideoFlowDiscntIntf(IData discntInfo) throws Exception{
		IData returnInfo = new DataMap();
		IDataset newTradeDiscntInfos = new DatasetList();
		IDataset newTradeDiscntAttrInfos = new DatasetList();
		returnInfo.put("newTradeDiscntInfos", newTradeDiscntInfos);
		returnInfo.put("newTradeDiscntAttrInfos", newTradeDiscntAttrInfos);
		
		//1、必传参数校验
		String userId = discntInfo.getString("USER_ID");
		String eparchyCode = discntInfo.getString("EPARCHY_CODE");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String modifyTag = discntInfo.getString("MODIFY_TAG");
		String ctrmProductId = discntInfo.getString("CTRM_PRODUCT_ID");
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos",new DatasetList());
		IDataset discntConfigInfos = discntInfo.getDataset("discntConfigInfos",new DatasetList());
		if(StringUtils.isBlank(userId)||StringUtils.isBlank(eparchyCode)||StringUtils.isBlank(discntCode)||StringUtils.isBlank(modifyTag)||StringUtils.isBlank(ctrmProductId)||IDataUtil.isEmpty(discntConfigInfos)){
			returnInfo.put("CHECK_RESULT_CODE", "1");
			returnInfo.put("CHECK_RESULT_INFO", "必传参数校验失败！USER_ID,EPARCHY_CODE,DISCNT_CODE,MODIFY_TAG,CTRM_PRODUCT_ID,discntConfigInfos都是必传参数！");
			return returnInfo;
		}

		//2、资费属性数量校验
		String discntAttrValueService1 = "";
		String discntAttrValueService2 = "";
		String discntAttrValueService3 = "";
		for(int j=0;j<videoFlowDiscntAttrInfos.size();j++){
			String attrCode = videoFlowDiscntAttrInfos.getData(j).getString("ATTR_CODE");
			String attrValue = videoFlowDiscntAttrInfos.getData(j).getString("ATTR_VALUE");
			String attrModifyTag = videoFlowDiscntAttrInfos.getData(j).getString("MODIFY_TAG");
			if(BofConst.MODIFY_TAG_ADD.equals(attrModifyTag)){
				if(VideoFlowConst.discntAttrCodeService1.equals(attrCode)){
					discntAttrValueService1 = attrValue;
					continue;
				}
				if(VideoFlowConst.discntAttrCodeService2.equals(attrCode)){
					discntAttrValueService2 = attrValue;
					continue;
				}
				if(VideoFlowConst.discntAttrCodeService3.equals(attrCode)){
					discntAttrValueService3 = attrValue;
					continue;
				}
			}
		}
		discntInfo.put("discntAttrValueService1", discntAttrValueService1);
		discntInfo.put("discntAttrValueService2", discntAttrValueService2);
		discntInfo.put("discntAttrValueService3", discntAttrValueService3);
		
		//3、资费办理校验
		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
			return this.dealDiscntAdd(discntInfo, returnInfo, newTradeDiscntInfos, newTradeDiscntAttrInfos);
		}
		
		//4、资费变更校验
		if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)){
			return this.dealDiscntUpd(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		
		//5、资费退订校验
		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
			return this.dealDiscntDel(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "校验通过!");
		return returnInfo;
	}

	/**
	 * 处理 视频流量资费 订购
	 * @param discntInfo
	 * @param returnInfo
	 * @param newTradeDiscntInfos
	 * @return
	 * @throws Exception
	 */
	private IData dealDiscntAdd(IData discntInfo, IData returnInfo, IDataset newTradeDiscntInfos, IDataset newTradeDiscntAttrInfos) throws Exception {
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String ctrmProductId = discntInfo.getString("CTRM_PRODUCT_ID");
		String discntName = discntInfo.getString("DISCNT_NAME");

		//9元资费属性：第一次订购属性必传为1个；支持3次订购
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_9,ctrmProductId)){
			return dealDiscntAdd_9(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		
		//18元资费属性：第一次订购属性必传为1个，其他2个可为特殊值
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_18,ctrmProductId)){
			return dealDiscntAdd_18(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		
		//24元资费属性：第一次订购属性必传为1个；支持1次订购
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_24,ctrmProductId)){
			return dealDiscntAdd_24(discntInfo, returnInfo, newTradeDiscntInfos, newTradeDiscntAttrInfos);
		}
		
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]校验通过!");
		return returnInfo;
	}

	private IData dealDiscntAdd_9(IData discntInfo, IData returnInfo ,IDataset newTradeDiscntInfos) throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos",new DatasetList());
		IDataset discntConfigInfos = discntInfo.getDataset("discntConfigInfos");
		
		if(IDataUtil.isEmpty(videoFlowDiscntAttrInfos)){
			//一、订购不挂资费属性的9元资费
			String appServiceId = discntInfo.getString("APP_SERVICE_IDS");
			
			//1-校验属性值
			if(StringUtils.isBlank(appServiceId)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费属性值校验失败！该"+discntName+"["+discntCode+"]，在TD_S_COMMPARA表中未配置["+discntCode+"]资费的默认适配的APP值，请检查!");
				return returnInfo;
			}
			
			//2-校验订购次数
			int userDiscntCount = this.getUserEffectVFDCount(userId, discntCode);
			if(userDiscntCount>0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费订购数校验失败！该"+discntName+"["+discntCode+"]，已经订购过，您不能重复订购!");
				return returnInfo;
			}
			
			//3-校验互斥关系(互斥的24元资费:(1)不挂资费属性)
			IData discntConfigInfo_24 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_24, appServiceId, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo_24)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】校验通过!");
				return returnInfo;
			}
			String configDiscntCode_24 = discntConfigInfo_24.getString("PARAM_CODE");
			String configDiscntName_24 = discntConfigInfo_24.getString("PARAM_NAME");
			//互斥的24元资费:(1)不挂资费属性：校验该用户当月生效的24元资费是否存在互斥(24元每月只能办理一次，办理立即生效，所以是不会存在下月资费生效的情况)
			int userDiscntCount_24 = this.getUserEffectVFDCount(userId, configDiscntCode_24);
			if(userDiscntCount_24>0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】与之前订购的【"+configDiscntName_24+"["+configDiscntCode_24+"]】存在互斥关系!");
				return returnInfo;
			}
			
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】校验通过!");
			return returnInfo;
		}else{
			//二、订购挂资费属性的9元资费
			String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");
			
			//1-校验属性值
			if(StringUtils.isBlank(discntAttrValueService1)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费属性值校验失败！该"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]，必传属性值不能为空，请检查!");
				return returnInfo;
			}
			
			//2-校验订购次数
			int userDiscntCount = this.getUserEffectVFDAttrValueCount(userId, discntCode, discntAttrValueService1);
			if(userDiscntCount>0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费订购数校验失败！该"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]，已经订购过，您不能重复订购!");
				return returnInfo;
			}
			
			//3-校验互斥关系(互斥的24元资费:(1)不挂资费属性)
			IData discntConfigInfo_24 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_24, null, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo_24)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】校验通过!");
				return returnInfo;
			}
			String configDiscntCode_24 = discntConfigInfo_24.getString("PARAM_CODE");
			String configDiscntName_24 = discntConfigInfo_24.getString("PARAM_NAME");
			//互斥的24元资费:(2)挂资费属性：判断之前是否有 当前生效、下月开始生效 的24元的视频资费包,如果有，仍可订购9元包，但不能指向同一APP。
			int userDiscntAttrSize_24 = this.getUserEffectVFDAttrValueCount(userId, configDiscntCode_24, discntAttrValueService1);
			int userNextMonthDiscntAttrCount_24 = this.getUserNextMonthStartEffectVFDAttrValueCount(userId, configDiscntCode_24, discntAttrValueService1);
			if(userDiscntAttrSize_24 > 0 || userNextMonthDiscntAttrCount_24 > 0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】与之前订购的【"+configDiscntName_24+"["+configDiscntCode_24+"]+APP["+discntAttrValueService1+"]】存在互斥关系!");
				return returnInfo;
			}
			
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】校验通过!");
			return returnInfo;
		}
	}
	
	/**
	 * 处理9元视频流量包减免资费的订购
	 * @param discntInfo
	 * @param newTradeDiscntInfos
	 * @throws Exception
	 */
	/*
	private void dealDiscntAdd_9_Derate(IData discntInfo,IDataset newTradeDiscntInfos) throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		String derateDincntCode = discntInfo.getString("DERATE_DINCNT_CODE");
		if(StringUtils.isBlank(derateDincntCode)){
			Utility.error("-1", null, "当前订购的【"+discntName+"["+discntCode+"]在TD_S_COMMPARA表中未配置减免资费编码，请检查!");
			return;
		}
		
		//拼接9元减免资费信息
		IData userNewDiscntInfo = new DataMap();
		userNewDiscntInfo.put("TRADE_ID", discntInfo.getString("TRADE_ID"));
		userNewDiscntInfo.put("ACCEPT_MONTH", discntInfo.getString("ACCEPT_MONTH"));
		userNewDiscntInfo.put("INST_ID", SeqMgr.getInstId());
		userNewDiscntInfo.put("UPDATE_TIME", discntInfo.getString("UPDATE_TIME"));
		userNewDiscntInfo.put("UPDATE_STAFF_ID", discntInfo.getString("UPDATE_STAFF_ID"));
		userNewDiscntInfo.put("UPDATE_DEPART_ID", discntInfo.getString("UPDATE_DEPART_ID"));
		userNewDiscntInfo.put("REMARK", discntInfo.getString("REMARK"));
		userNewDiscntInfo.put("USER_ID", userId);
		userNewDiscntInfo.put("USER_ID_A", discntInfo.getString("USER_ID_A"));
		userNewDiscntInfo.put("PRODUCT_ID", "-1");
		userNewDiscntInfo.put("PACKAGE_ID", "-1");
		userNewDiscntInfo.put("DISCNT_CODE", derateDincntCode);
		userNewDiscntInfo.put("SPEC_TAG", discntInfo.getString("SPEC_TAG"));
		userNewDiscntInfo.put("RELATION_TYPE_CODE", discntInfo.getString("RELATION_TYPE_CODE"));
		userNewDiscntInfo.put("CAMPN_ID", discntInfo.getString("CAMPN_ID"));
		//立即生效
		userNewDiscntInfo.put("START_DATE", SysDateMgr.getSysTime());
		userNewDiscntInfo.put("END_DATE", discntInfo.getString("END_DATE"));
		userNewDiscntInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
		userNewDiscntInfo.put("OPER_CODE", discntInfo.getString("OPER_CODE"));
		userNewDiscntInfo.put("IS_NEED_PF", discntInfo.getString("IS_NEED_PF"));
		newTradeDiscntInfos.add(userNewDiscntInfo);
	}
	*/

	private IData dealDiscntAdd_18(IData discntInfo, IData returnInfo, IDataset newTradeDiscntInfos) {
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");
		String discntAttrValueService2 = discntInfo.getString("discntAttrValueService2");
		String discntAttrValueService3 = discntInfo.getString("discntAttrValueService3");
		
		//1-校验资费属性值：不能全为特殊值
		if(VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService1) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService2) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService3)){
			returnInfo.put("CHECK_RESULT_CODE", "1");
			returnInfo.put("CHECK_RESULT_INFO", "资费属性校验失败！当前订购的"+discntName+"["+discntCode+"]，必传属性不能全为特殊值["+VideoFlowConst.discntAttrSpecialValueService+"]，请检查填写的资费属性!");
			return returnInfo;
		}
		
		//2-校验资费属性值：除特殊值之外和空字符串外，资费属性值两两不能相同判断
		boolean isSame = isStrTwoSame(VideoFlowConst.discntAttrSpecialValueService, discntAttrValueService1,discntAttrValueService2,discntAttrValueService3);
		if(isSame){
			returnInfo.put("CHECK_RESULT_CODE", "1");
			returnInfo.put("CHECK_RESULT_INFO", "资费属性值校验失败！订购"+discntName+"["+discntCode+"]时，资费适配的APP重复，请检查填写的资费属性!");
			return returnInfo;
		}
		
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】校验通过!");
		return returnInfo;
	}

	private IData dealDiscntAdd_24(IData discntInfo, IData returnInfo, IDataset newTradeDiscntInfos, IDataset newTradeDiscntAttrInfos)throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String eparchyCode = discntInfo.getString("EPARCHY_CODE");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos",new DatasetList());
		//IDataset userVideoFlowDiscntInfos = discntInfo.getDataset("userVideoFlowDiscntInfos",new DatasetList());
		IDataset discntConfigInfos = discntInfo.getDataset("discntConfigInfos");
		
		if(IDataUtil.isEmpty(videoFlowDiscntAttrInfos)){
			//一、订购不挂资费属性的24元资费
			String appServiceId = discntInfo.getString("APP_SERVICE_IDS");
			
			//1-校验属性值
			if(StringUtils.isBlank(appServiceId)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费属性校验失败！当前订购的"+discntName+"["+discntCode+"]，在TD_S_COMMPARA表中未配置["+discntCode+"]资费的默认适配的APP值，请检查!");
				return returnInfo;
			}
			
			//2-校验互斥关系
			IData discntConfigInfo_9 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_9, appServiceId, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo_9)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】校验通过!");
				return returnInfo;
			}
			String configDiscntCode_9 = discntConfigInfo_9.getString("PARAM_CODE");
			String configDiscntName_9 = discntConfigInfo_9.getString("PARAM_NAME");
			//String derateDincntCode_9 = discntConfigInfo_9.getString("PARA_CODE3");
			//互斥的9元资费:(1)不挂资费属性：校验该用户当月生效的9元资费是否存在互斥(24元每月只能办理一次，办理立即生效，所以是不会存在下月资费生效的情况)
			//台账互斥校验
			IDataset tradeDiscntInfos_9 = TradeDiscntInfoQry.queryCountByUidDiscntCode(userId, configDiscntCode_9, BofConst.MODIFY_TAG_ADD);
			if(IDataUtil.isNotEmpty(tradeDiscntInfos_9)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费互斥校验失败！当前订购的【"+discntName+"["+discntCode+"]】和当前用户未完工工单中的【"+configDiscntName_9+"["+configDiscntCode_9+"]】存在互斥关系!");
				return returnInfo;
			}
			//资料互斥校验
			IDataset userDiscntInfos_9 = UserDiscntInfoQry.getAllDiscntByUserId(userId, configDiscntCode_9);
			if(IDataUtil.isEmpty(userDiscntInfos_9)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】的校验通过!");
				return returnInfo;
			}
			
			//3-资料互斥处理:24元的次月生效,9元包次月失效(如果9元资费当前订购为3个，次月失效1个，则9元减免资费也得次月失效)
			//24元次月生效
			discntInfo.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
			//9元包次月失效
			IData userDiscntInfo_9 = userDiscntInfos_9.getData(0);
			this.dealCommDiscntDel(userDiscntInfo_9);
			newTradeDiscntInfos.add(userDiscntInfo_9);
			//9元减免资费处理
			/*
			int discntOrderCount_9 = getDiscntOrderCount(VideoFlowConst.ctrmProductId_9, userVideoFlowDiscntInfos);
			if(discntOrderCount_9 == 3){
				//退订9元减免资费，次月生效
				this.dealDiscntDel_9_Derate(userId, derateDincntCode_9, newTradeDiscntInfos);
			}
			*/
			
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]】校验通过!");
			return returnInfo;
		}else{
			//二、订购挂资费属性的24元资费
			String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");

			//1-校验资费属性
			if(StringUtils.isBlank(discntAttrValueService1)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费属性校验失败！当前订购的"+discntName+"["+discntCode+"]，必传属性不能为空，请检查!");
				return returnInfo;
			}
			
			//2-校验互斥关系
			IData discntConfigInfo_9 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_9, null, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo_9)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】校验通过!");
				return returnInfo;
			}
			String configDiscntCode_9 = discntConfigInfo_9.getString("PARAM_CODE");
			String configDiscntName_9 = discntConfigInfo_9.getString("PARAM_NAME");
			//String derateDincntCode_9 = discntConfigInfo_9.getString("PARA_CODE3");
			//互斥的24元资费:(2)挂资费属性：判断之前是否有 当前生效、下月开始生效 的9元的视频资费包,如果有，仍可订购24元包，如果两者指向同一APP，则24元的次月生效，9元次月失效。
			//下月开始生效互斥校验：如果存在下月开始生效的9元的视频资费包互斥，则 24元不能办理
			int userNextMonthDiscntAttrCount_9 = this.getUserNextMonthStartEffectVFDAttrValueCount(userId, configDiscntCode_9, discntAttrValueService1);
			if(userNextMonthDiscntAttrCount_9 > 0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】与之前变更的【"+configDiscntName_9+"["+configDiscntCode_9+"]+APP["+discntAttrValueService1+"]】存在互斥关系!");
				return returnInfo;
			}
			//台账互斥校验
			IDataset tradeAttrInfos_9 = TradeAttrInfoQry.getTradeAttrByDiscntCodeAttrValue(userId, configDiscntCode_9, discntAttrValueService1);
			if(IDataUtil.isNotEmpty(tradeAttrInfos_9)){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "资费互斥校验失败！当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】和当前用户未完工工单中的【"+configDiscntName_9+"["+configDiscntCode_9+"]+APP["+discntAttrValueService1+"]】存在互斥关系!");
				return returnInfo;
			}
			//资料互斥校验
			IDataset userAttrInfos_9 = UserAttrInfoQry.getUserAttrByDiscntCodeAttrValue(userId, configDiscntCode_9, discntAttrValueService1);
			if(IDataUtil.isEmpty(userAttrInfos_9)){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】的校验通过!");
				return returnInfo;
			}
			
			//3-资料互斥处理:24元的次月生效,9元包次月失效(如果9元资费当前订购为3个，次月失效1个，则9元减免资费也得次月失效)
			//24元次月生效
			discntInfo.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
			for(int j=0;j<videoFlowDiscntAttrInfos.size();j++){
				videoFlowDiscntAttrInfos.getData(j).put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
			}
			//9元包次月失效(特别注意：这个地方是叠加包形式)
			String relaInstId = userAttrInfos_9.getData(0).getString("RELA_INST_ID");
			IDataset userDiscntInfos_9 = UserDiscntInfoQry.queryDiscntInfosByInstId(userId, relaInstId);
			IData userDiscntInfo_9 = userDiscntInfos_9.getData(0);
			this.dealCommDiscntDel(userDiscntInfo_9);
			newTradeDiscntInfos.add(userDiscntInfo_9);
			//9元包资费属性全部次月失效
			IDataset userDiscntAttrInfos_9 = UserAttrInfoQry.getUserAttrByRelaInstId(userId, relaInstId, eparchyCode);
			for(int i=0,size=userDiscntAttrInfos_9.size();i<size;i++){
				IData userDiscntAttrInfo = userDiscntAttrInfos_9.getData(i);
				userDiscntAttrInfo.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
				userDiscntAttrInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				newTradeDiscntAttrInfos.add(userDiscntAttrInfo);
			}
			/*
			//9元减免资费处理
			int discntOrderCount_9 = getDiscntOrderCount(VideoFlowConst.ctrmProductId_9, userVideoFlowDiscntInfos);
			if(discntOrderCount_9 == 3){
				//退订9元减免资费，次月生效
				this.dealDiscntDel_9_Derate(userId, derateDincntCode_9, newTradeDiscntInfos);
			}
			*/
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"]】校验通过!");
			return returnInfo;
		}
	}

	/**
	 * 处理 视频流量资费 变更
	 * @param discntInfo
	 * @param returnInfo
	 * @param newTradeDiscntInfos
	 * @return
	 * @throws Exception
	 */
	private IData dealDiscntUpd(IData discntInfo, IData returnInfo, IDataset newTradeDiscntInfos) throws Exception {
		String ctrmProductId = discntInfo.getString("CTRM_PRODUCT_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos");
		
		//4.1、当前变更资费是否生效，如果还未生效或本月底截止，则不允许变更
		Date startDate = SysDateMgr.string2Date(discntInfo.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
		Date endDate = SysDateMgr.string2Date(discntInfo.getString("END_DATE"), SysDateMgr.PATTERN_STAND);
		Date sysTime = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
		Date firstDayOfNextMonth = SysDateMgr.string2Date(SysDateMgr.getFirstDayOfNextMonth(),SysDateMgr.PATTERN_STAND_YYYYMMDD);
		if(startDate.getTime()>sysTime.getTime() || endDate.getTime()<firstDayOfNextMonth.getTime()){
			returnInfo.put("CHECK_RESULT_CODE", "1");
			returnInfo.put("CHECK_RESULT_INFO", "当前变更的【"+discntName+"["+discntCode+"]】还未生效或者是本月底截止，不允许变更!");
			return returnInfo;
		}
		
		//4.2、资费属性校验(不带资费属性的变更，直接校验通过)
		if(IDataUtil.isEmpty(videoFlowDiscntAttrInfos)){
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "资费变更校验通过！");
			return returnInfo;
		}
		
		//4.3、资费互斥校验、生效优先级校验
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_9,ctrmProductId)){
			return this.dealDiscntUpd_9(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_18,ctrmProductId)){
			return this.dealDiscntUpd_18(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_24,ctrmProductId)){
			return this.dealDiscntUpd_24(discntInfo, returnInfo, newTradeDiscntInfos);
		}
		
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "资费变更校验通过！");
		return returnInfo;
	}

	private IData dealDiscntUpd_9(IData discntInfo, IData returnInfo,IDataset newTradeDiscntInfos)throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset discntConfigInfos = discntInfo.getDataset("discntConfigInfos");
		String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");
		
		//1-校验资费互斥关系(互斥的24元资费:(2)挂资费属性)
		IData discntConfigInfo_24 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_24, null, discntConfigInfos);
		if(IDataUtil.isNotEmpty(discntConfigInfo_24)){
			String configDiscntCode_24 = discntConfigInfo_24.getString("PARAM_CODE");
			String configDiscntName_24 = discntConfigInfo_24.getString("PARAM_NAME");
			//互斥的24元资费:(2)挂资费属性：判断之前是否有 当前生效、下月开始生效 的24元的视频资费包,如果有，仍可订购9元包，但不能指向同一APP。
			int userDiscntAttrSize_24 = this.getUserEffectVFDAttrValueCount(userId, configDiscntCode_24, discntAttrValueService1);
			int userNextMonthDiscntAttrCount_24 = this.getUserNextMonthStartEffectVFDAttrValueCount(userId, configDiscntCode_24, discntAttrValueService1);
			if(userDiscntAttrSize_24 > 0 || userNextMonthDiscntAttrCount_24 > 0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"】与之前订购的【"+configDiscntName_24+"["+configDiscntCode_24+"]+APP["+discntAttrValueService1+"]】存在互斥关系!");
				return returnInfo;
			}
		}
		
		//2-处理 变更APP
		return this.dealDiscntAttrUpd(discntInfo, returnInfo);
	}

	private IData dealDiscntUpd_18(IData discntInfo, IData returnInfo,IDataset newTradeDiscntInfos)throws Exception {
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");
		String discntAttrValueService2 = discntInfo.getString("discntAttrValueService2");
		String discntAttrValueService3 = discntInfo.getString("discntAttrValueService3");

		//0、校验资费属性值除特殊值之外和空字符串外不能两两相同
		boolean isSame = isStrTwoSame(VideoFlowConst.discntAttrSpecialValueService, discntAttrValueService1,discntAttrValueService2,discntAttrValueService3);
		if(isSame){
			returnInfo.put("CHECK_RESULT_CODE", "1");
			returnInfo.put("CHECK_RESULT_INFO", "资费属性值校验失败！订购【"+discntName+"["+discntCode+"]】时，3个资费属性值除特殊值之外不能重复！");
			return returnInfo;
		}
		
		/* 退订APP导致 资费退订的7情况
		 
		 0	属性值1		属性值2		属性值3
		 	
		 1	特殊值		特殊值		特殊值
		 
		 2	特殊值		特殊值		空
		 3	特殊值		空			特殊值
		 4	空			特殊值		特殊值
		 
		 5	特殊值		空			空
		 6	空			特殊值		空
		 7	空			空			特殊值
		 
		 */
		//1-退订资费(特殊值、特殊值、特殊值)
		if(VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService1) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService2) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService3)){
			this.dealCommDiscntDel(discntInfo);
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
			return returnInfo;
		}
		//2-退订资费(特殊值、特殊值、空)
		if(VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService1) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService2) && StringUtils.isBlank(discntAttrValueService3)){
			//如果 属性值3 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_OneApp(discntInfo, VideoFlowConst.discntAttrCodeService3, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		//3-退订资费(特殊值、空、特殊值)
		if(VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService1) && StringUtils.isBlank(discntAttrValueService2) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService3)){
			//如果 属性值2 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_OneApp(discntInfo, VideoFlowConst.discntAttrCodeService2, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		//4-退订资费(空、特殊值、特殊值)
		if(StringUtils.isBlank(discntAttrValueService1) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService2) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService3)){
			//如果 属性值1 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_OneApp(discntInfo, VideoFlowConst.discntAttrCodeService1, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		//5-退订资费(特殊值、空、空)
		if(VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService1) && StringUtils.isBlank(discntAttrValueService2) && StringUtils.isBlank(discntAttrValueService3)){
			//如果 属性值2、属性值3 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_TwoApp(discntInfo, VideoFlowConst.discntAttrCodeService2, VideoFlowConst.discntAttrCodeService3, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		//6-退订资费(空、特殊值、空)
		if(StringUtils.isBlank(discntAttrValueService1) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService2) && StringUtils.isBlank(discntAttrValueService3)){
			//如果 属性值1、属性值3 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_TwoApp(discntInfo, VideoFlowConst.discntAttrCodeService1, VideoFlowConst.discntAttrCodeService3, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		//7-退订资费(空、空、特殊值)
		if(StringUtils.isBlank(discntAttrValueService1) && StringUtils.isBlank(discntAttrValueService2) && VideoFlowConst.discntAttrSpecialValueService.equals(discntAttrValueService3) ){
			//如果 属性值1、属性值2 下月不生效(这个月底失效),那么该资费就得退订
			boolean isDel = this.dealDiscntDel_18_TwoApp(discntInfo, VideoFlowConst.discntAttrCodeService1, VideoFlowConst.discntAttrCodeService2, VideoFlowConst.discntAttrSpecialValueService);
			if(isDel){
				returnInfo.put("CHECK_RESULT_CODE", "0");
				returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
				return returnInfo;
			}
		}
		
		//8-处理 变更APP 或 退订APP
		return this.dealDiscntAttrUpd(discntInfo, returnInfo);
	}

	private IData dealDiscntUpd_24(IData discntInfo, IData returnInfo,IDataset newTradeDiscntInfos)throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset discntConfigInfos = discntInfo.getDataset("discntConfigInfos");
		String discntAttrValueService1 = discntInfo.getString("discntAttrValueService1");
		
		//1-校验资费互斥关系(互斥的9元资费:(2)挂资费属性)
		IData discntConfigInfo_9 = this.getDiscntConfigInfo(VideoFlowConst.ctrmProductId_9, null, discntConfigInfos);
		if(IDataUtil.isNotEmpty(discntConfigInfo_9)){
			String configDiscntCode_9 = discntConfigInfo_9.getString("PARAM_CODE");
			String configDiscntName_9 = discntConfigInfo_9.getString("PARAM_NAME");
			//互斥的9元资费:(2)挂资费属性：判断之前是否有 当前生效、下月开始生效 的9元的视频资费包,如果有，则不能变更。
			int userDiscntAttrSize_9 = this.getUserEffectVFDAttrValueCount(userId, configDiscntCode_9, discntAttrValueService1);
			int userNextMonthDiscntAttrCount_9 = this.getUserNextMonthStartEffectVFDAttrValueCount(userId, configDiscntCode_9, discntAttrValueService1);
			if(userDiscntAttrSize_9 > 0 || userNextMonthDiscntAttrCount_9 > 0){
				returnInfo.put("CHECK_RESULT_CODE", "1");
				returnInfo.put("CHECK_RESULT_INFO", "当前订购的【"+discntName+"["+discntCode+"]+APP["+discntAttrValueService1+"】与之前订购的【"+configDiscntName_9+"["+configDiscntCode_9+"]+APP["+discntAttrValueService1+"]】存在互斥关系!");
				return returnInfo;
			}
		}
		
		//2-处理 变更APP
		return this.dealDiscntAttrUpd(discntInfo, returnInfo);
	}
	
	/**
	 * 处理 变更APP 或 退订APP
	 * @param discntInfo
	 * @throws Exception
	 */
	private IData dealDiscntAttrUpd(IData discntInfo, IData returnInfo) throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String instId = discntInfo.getString("INST_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		String discntName = discntInfo.getString("DISCNT_NAME");
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos");
		for(int j=0;j<videoFlowDiscntAttrInfos.size();j++){
			IData discntAttrInfo = videoFlowDiscntAttrInfos.getData(j);
			String attrCode = discntAttrInfo.getString("ATTR_CODE");
			String attrValue = discntAttrInfo.getString("ATTR_VALUE");
			String attrModifyTag = videoFlowDiscntAttrInfos.getData(j).getString("MODIFY_TAG");
			if(BofConst.MODIFY_TAG_DEL.equals(attrModifyTag)){
				if(VideoFlowConst.discntAttrCodeService1.equals(attrCode)||VideoFlowConst.discntAttrCodeService2.equals(attrCode)||VideoFlowConst.discntAttrCodeService3.equals(attrCode)){
					//原来的属性值月底截止
					discntAttrInfo.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
				}
				continue;
			}
			if(BofConst.MODIFY_TAG_ADD.equals(attrModifyTag)){
				if(VideoFlowConst.discntAttrCodeService1.equals(attrCode)||VideoFlowConst.discntAttrCodeService2.equals(attrCode)||VideoFlowConst.discntAttrCodeService3.equals(attrCode)){
					//判断资费当前属性是否存在下月开始生效的属性值(每个属性每月只能变更一次)
					//下月1号的日期
			        String qryStartDate = SysDateMgr.getFirstDayOfNextMonth();
					//第 N-1 月月底最后一天的日期
					String qryEndDate = SysDateMgr.getAddMonthsLastDay(2);
					IDataset userNextMonthStartEffectAttrs = UserAttrInfoQry.getUserAttrByDiscntCodeAttrCodeDateScope(userId, instId, discntCode, attrCode, qryStartDate, qryEndDate);
					if(IDataUtil.isNotEmpty(userNextMonthStartEffectAttrs)){
						returnInfo.put("CHECK_RESULT_CODE", "1");
						returnInfo.put("CHECK_RESULT_INFO", "当前变更的【"+discntName+"["+discntCode+"]】的属性本月已经变更过一次，本月不允许变更了!");
						return returnInfo;
					}
					//校验当前变更后APP，是否在资料或者台账中,并且是下月也生效
					if(!StringUtils.equals(VideoFlowConst.discntAttrSpecialValueService,attrValue)){
						int userNextMonthDiscntAttrCount = this.getUserNextMonthEffectVFDAttrValueCount(userId, discntCode, attrValue);
						if(userNextMonthDiscntAttrCount>0){
							returnInfo.put("CHECK_RESULT_CODE", "1");
							returnInfo.put("CHECK_RESULT_INFO", "当前变更的【"+discntName+"["+discntCode+"]+APP["+attrValue+"]】已经存在并且是已经生效、下月生效或者与其他资费互斥，你不能进行变更!");
							return returnInfo;
						}
					}
					//下月初生效
					discntAttrInfo.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
				}
				continue;
			}
		}
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "资费变更校验通过！");
		return returnInfo;
	}

	/**
	 * 处理 视频流量资费 退订
	 * @param discntInfo
	 * @param returnInfo
	 * @param newTradeDiscntInfos
	 * @return
	 * @throws Exception
	 */
	private IData dealDiscntDel(IData discntInfo, IData returnInfo, IDataset newTradeDiscntInfos) throws Exception {
		//String userId = discntInfo.getString("USER_ID");
		String ctrmProductId = discntInfo.getString("CTRM_PRODUCT_ID");
		//String derateDincntCode = discntInfo.getString("DERATE_DINCNT_CODE");
		//IDataset userVideoFlowDiscntInfos = discntInfo.getDataset("userVideoFlowDiscntInfos",new DatasetList());
		
		//9元资费退订，判断是否需要把 9元减免资费退订掉
		if(StringUtils.equals(VideoFlowConst.ctrmProductId_9,ctrmProductId)){
			/*
			//9元减免资费处理
			int discntOrderCount_9 = this.getDiscntOrderCount(VideoFlowConst.ctrmProductId_9, userVideoFlowDiscntInfos);
			if(discntOrderCount_9 == 3){
				//退订9元减免资费，次月生效
				this.dealDiscntDel_9_Derate(userId, derateDincntCode, newTradeDiscntInfos);
			}
			*/
			returnInfo.put("CHECK_RESULT_CODE", "0");
			returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
			return returnInfo;
		}
		
		returnInfo.put("CHECK_RESULT_CODE", "0");
		returnInfo.put("CHECK_RESULT_INFO", "资费退订校验通过！");
		return returnInfo;
	}
	
	/**
	 * 处理 视频流量资费 退订操作
	 * @param discntInfo
	 * @throws Exception
	 */
	private void dealCommDiscntDel(IData discntInfo) throws Exception {
		//月底失效
		discntInfo.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
		discntInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
		//退订资费属性
		IDataset videoFlowDiscntAttrInfos = discntInfo.getDataset("videoFlowDiscntAttrInfos",new DatasetList());
		if(IDataUtil.isEmpty(videoFlowDiscntAttrInfos)){
			return;
		}
		for(int j=0;j<videoFlowDiscntAttrInfos.size();j++){
			IData discntAttrInfo = videoFlowDiscntAttrInfos.getData(j);
			String attrCode = discntAttrInfo.getString("ATTR_CODE");
			String attrModifyTag = videoFlowDiscntAttrInfos.getData(j).getString("MODIFY_TAG");
			if(BofConst.MODIFY_TAG_DEL.equals(attrModifyTag)){
				if(VideoFlowConst.discntAttrCodeService1.equals(attrCode)||VideoFlowConst.discntAttrCodeService2.equals(attrCode)||VideoFlowConst.discntAttrCodeService3.equals(attrCode)){
					//原来的属性值月底截止
					discntAttrInfo.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
				}
				continue;
			}
			if(BofConst.MODIFY_TAG_ADD.equals(attrModifyTag)){
				if(VideoFlowConst.discntAttrCodeService1.equals(attrCode)||VideoFlowConst.discntAttrCodeService2.equals(attrCode)||VideoFlowConst.discntAttrCodeService3.equals(attrCode)){
					//下月初生效
					discntAttrInfo.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
				}
				continue;
			}
		}
	}
	
	/**
	 * 处理 9元视频流量 减免资费退订
	 * @param newTradeDiscntInfos
	 * @param userId
	 * @param derateDincntCode
	 * @throws Exception
	 */
	/*
	private void dealDiscntDel_9_Derate(String userId, String derateDincntCode, IDataset newTradeDiscntInfos) throws Exception {
		IDataset userNewDiscntInfos = UserDiscntInfoQry.getAllDiscntByUserId(userId, derateDincntCode);
		if(IDataUtil.isNotEmpty(userNewDiscntInfos)){
			IData userNewDiscntInfo = userNewDiscntInfos.getData(0);
			this.dealCommDiscntDel(userNewDiscntInfo);
			newTradeDiscntInfos.add(userNewDiscntInfo);
		}
	}
	*/
	
	/**
	 * 退订单个APP导致 18元资费退订的情况
	 * @param discntInfo
	 * @param userId
	 * @param discntCode
	 * @throws Exception
	 */
	private boolean dealDiscntDel_18_OneApp(IData discntInfo, String discntAttrCode, String specialValue) throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		boolean isDel = false;
		IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByDiscntCodeAttrCodeThisDateNoAttrValue(userId, discntCode, discntAttrCode, specialValue, SysDateMgr.firstDayOfMonth(1));
		if(IDataUtil.isEmpty(userAttrInfos)){
			this.dealCommDiscntDel(discntInfo);
			isDel = true;
		}
		return isDel;
	}
	
	/**
	 * 退订两个APP导致 18元资费退订的情况
	 * @param discntInfo
	 * @param userId
	 * @param discntCode
	 * @throws Exception
	 */
	private boolean dealDiscntDel_18_TwoApp(IData discntInfo, String discntAttrCode1, String discntAttrCode2, String specialValue) throws Exception {
		String userId = discntInfo.getString("USER_ID");
		String discntCode = discntInfo.getString("DISCNT_CODE");
		boolean isDel = false;
		IDataset userAttrInfos1 = UserAttrInfoQry.getUserAttrByDiscntCodeAttrCodeThisDateNoAttrValue(userId, discntCode, discntAttrCode1, specialValue, SysDateMgr.firstDayOfMonth(1));
		IDataset userAttrInfos2 = UserAttrInfoQry.getUserAttrByDiscntCodeAttrCodeThisDateNoAttrValue(userId, discntCode, discntAttrCode2, specialValue, SysDateMgr.firstDayOfMonth(1));
		if(IDataUtil.isEmpty(userAttrInfos1) && IDataUtil.isEmpty(userAttrInfos2)){
			this.dealCommDiscntDel(discntInfo);
			isDel = true;
		}
		return isDel;
	}
	
	/**
	 * 获取用户订购的所有视频流量资费信息
	 * @param inparam
	 * @param userAllDiscntInfos
	 * @param discntConfigInfos
	 * @throws Exception
	 */
	private IDataset getUserVideoFlowDiscntInfos(IData inparam)throws Exception {
		String userId = inparam.getString("USER_ID");
		String eparchyCode = inparam.getString("EPARCHY_CODE");
		IDataset discntConfigInfos = inparam.getDataset("discntConfigInfos");
		IDataset userVideoFlowDiscntInfos = new DatasetList();
		//用户当前正常生效的资费
		IDataset userAllDiscntInfos = UserDiscntInfoQry.getUserNormalDiscntByUserId(userId);
		
		//用户下月开始生效的资费
		//下月1号的日期
        String qryStartDate = SysDateMgr.getFirstDayOfNextMonth();
		//第 N-1 月月底最后一天的日期
		String qryEndDate = SysDateMgr.getAddMonthsLastDay(2);
		IDataset userNextMonthAllDiscntInfos = UserDiscntInfoQry.getUserNormalDiscntByUserIdDateScope(userId, qryStartDate, qryEndDate);
		if(IDataUtil.isNotEmpty(userNextMonthAllDiscntInfos)){
			userAllDiscntInfos.addAll(userNextMonthAllDiscntInfos);
		}
		
		if(log.isDebugEnabled()){
			log.debug("ChangeProductVideoFlowDiscntBean getUserVideoFlowDiscntInfos userAllDiscntInfos = "+userAllDiscntInfos);
			log.debug("ChangeProductVideoFlowDiscntBean getUserVideoFlowDiscntInfos userNextMonthAllDiscntInfos = "+userNextMonthAllDiscntInfos);
		}
		
		for(int i=0,sizeI=userAllDiscntInfos.size();i<sizeI;i++){
			IData userAllDiscntInfo = userAllDiscntInfos.getData(i);
			String instId = userAllDiscntInfo.getString("INST_ID");
			//判断是否是视频流量资费
			IData discntConfigInfo = this.getDiscntConfigInfo(userAllDiscntInfo, discntConfigInfos);
			if(IDataUtil.isEmpty(discntConfigInfo)){
				continue;
			}
			String discntName = discntConfigInfo.getString("PARAM_NAME");
			String ctrmProductId = discntConfigInfo.getString("PARA_CODE2");
			String derateDincntCode = discntConfigInfo.getString("PARA_CODE3","");
			String appServiceIds = discntConfigInfo.getString("PARA_CODE20","");
			userAllDiscntInfo.put("DISCNT_NAME", discntName);
			userAllDiscntInfo.put("CTRM_PRODUCT_ID", ctrmProductId);
			userAllDiscntInfo.put("DERATE_DINCNT_CODE", derateDincntCode);
			userAllDiscntInfo.put("APP_SERVICE_IDS", appServiceIds);
			
			//查找视频流量资费关于APP的属性
			IDataset userVideoFlowDiscntAttrInfos = new DatasetList();
			IDataset userDiscntAttrInfos = UserAttrInfoQry.getUserAttrByRelaInstId(userId, instId, eparchyCode);
			for(int j=0,sizeJ=userDiscntAttrInfos.size();j<sizeJ;j++){
				IData userDiscntAttrInfo = userDiscntAttrInfos.getData(j);
				String attrCode = userDiscntAttrInfo.getString("ATTR_CODE");
				if(StringUtils.equals(VideoFlowConst.discntAttrCodeService1, attrCode)||StringUtils.equals(VideoFlowConst.discntAttrCodeService2, attrCode)||StringUtils.equals(VideoFlowConst.discntAttrCodeService3, attrCode)){
					userVideoFlowDiscntAttrInfos.add(userDiscntAttrInfo);
				}
			}
			userAllDiscntInfo.put("userVideoFlowDiscntAttrInfos", userVideoFlowDiscntAttrInfos);
			userVideoFlowDiscntInfos.add(userAllDiscntInfo);
		}
		if(log.isDebugEnabled()){
			log.debug("ChangeProductVideoFlowDiscntBean getUserVideoFlowDiscntInfos userVideoFlowDiscntInfos = "+userVideoFlowDiscntInfos);
		}
		return userVideoFlowDiscntInfos;
	}
	
	/**
	 * 获取某个特定资费的视频流量资费的配置信息
	 * @param discntConfigInfos
	 * @param discntInfo
	 * @return
	 */
	public IData getDiscntConfigInfo(IData discntInfo, IDataset discntConfigInfos) {
		IData discntConfigInfo = new DataMap();
		String discntCode = discntInfo.getString("DISCNT_CODE");
		for(int j=0,sizeJ=discntConfigInfos.size();j<sizeJ;j++){
			String configDiscntCode = discntConfigInfos.getData(j).getString("PARAM_CODE");
			if(discntCode.equals(configDiscntCode)){
				discntConfigInfo = discntConfigInfos.getData(j);
				break;
			}
		}
		return discntConfigInfo;
	}
	
	/**
	 * 获取某个适配的APP视频流量资费的配置信息
	 * @param ctrmProductId
	 * @param eparchyCode
	 * @param appServiceIdA
	 * @return
	 * @throws Exception
	 */
	public IData getDiscntConfigInfo(String ctrmProductIdA, String appServiceIdA, IDataset discntConfigInfos)throws Exception{
		IData discntConfigInfo = new DataMap();
		for(int i=0,size=discntConfigInfos.size();i<size;i++){
			IData configInfo = discntConfigInfos.getData(i);
			String ctrmProductIdB = configInfo.getString("PARA_CODE2");
			String appServiceIdB = configInfo.getString("PARA_CODE20","");
			if(StringUtils.isBlank(appServiceIdA)){
				//获取挂属性资费的配置(配置中没配置默认适配的APP)
				if(StringUtils.equals(ctrmProductIdA, ctrmProductIdB) && StringUtils.isBlank(appServiceIdB)){
					discntConfigInfo = configInfo;
					break;
				}
			}else{
				//获取不挂属性的资费配置(配置中有配置默认适配的APP)
				if(StringUtils.equals(ctrmProductIdA, ctrmProductIdB) && StringUtils.equals(appServiceIdA, appServiceIdB)){
					discntConfigInfo = configInfo;
					break;
				}
			}
		}
		return discntConfigInfo;
	}
	
	/**
	 * 获取用户某种视频流量资费的订购次数
	 * @param ctrmProductIdA
	 * @param videoFlowDiscntInfos
	 * @return
	 */
	public int getDiscntOrderCount(String ctrmProductIdA, IDataset videoFlowDiscntInfos){
		int orderCount = 0;
		for(int i=0,size=videoFlowDiscntInfos.size();i<size;i++){
			IData discntInfo = videoFlowDiscntInfos.getData(i);
			String ctrmProductIdB = discntInfo.getString("CTRM_PRODUCT_ID");
			String modifyTag = discntInfo.getString("MODIFY_TAG","");
			if((StringUtils.isBlank(modifyTag)||BofConst.MODIFY_TAG_ADD.equals(modifyTag)) && StringUtils.equals(ctrmProductIdA, ctrmProductIdB)){
				orderCount++;
			}
		}
		return orderCount;
	}

	/**
	 * @description 获取当前用户生效的指定视频流量资费的数量(包括未完工的)
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param userId
	 * @param ctrmProductId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public int getUserEffectVFDCount(String userId,String discntCode) throws Exception{
		IDataset userDiscntInfos = UserDiscntInfoQry.getAllDiscntByUserId(userId, discntCode);
		IDataset tradeDiscntInfos = TradeDiscntInfoQry.queryCountByUidDiscntCode(userId, discntCode, BofConst.MODIFY_TAG_ADD);
		int userDiscntSize = 0;
		if(IDataUtil.isNotEmpty(userDiscntInfos)){
			userDiscntSize += userDiscntInfos.size();
		}
		if(IDataUtil.isNotEmpty(tradeDiscntInfos)){
			userDiscntSize += tradeDiscntInfos.size();
		}
		return userDiscntSize;
	}
	
	/**
	 * @description 获取当前用户生效的指定视频流量资费的特定属性的数量(包括未完工的)
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param userId
	 * @param ctrmProductId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public int getUserEffectVFDAttrValueCount(String userId,String discntCode,String appServiceId) throws Exception{
		//查询用户是否已经订购互斥的资费包+APP
		IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByDiscntCodeAttrValue(userId, discntCode, appServiceId);
		IDataset tradeAttrInfos = TradeAttrInfoQry.getTradeAttrByDiscntCodeAttrValue(userId, discntCode, appServiceId);
		int userDiscntAttrSize = 0;
		if(IDataUtil.isNotEmpty(userAttrInfos)){
			userDiscntAttrSize += userAttrInfos.size();
		}
		if(IDataUtil.isNotEmpty(tradeAttrInfos)){
			userDiscntAttrSize += tradeAttrInfos.size();
		}
		return userDiscntAttrSize;
	}
	
	/**
	 * @description 获取当前下月 开始生效 的指定视频流量资费的特定属性的数量(包括未完工的)
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param userId
	 * @param ctrmProductId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public int getUserNextMonthStartEffectVFDAttrValueCount(String userId,String discntCode,String appServiceId) throws Exception{
		//下月1号的日期
        String qryStartDate = SysDateMgr.getFirstDayOfNextMonth();
		//第 N-1 月月底最后一天的日期
		String qryEndDate = SysDateMgr.getAddMonthsLastDay(2);
		IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByDiscntCodeAttrValueDateScope(userId, discntCode, appServiceId, qryStartDate, qryEndDate);
		IDataset tradeAttrInfos = TradeAttrInfoQry.getTradeAttrByDiscntCodeAttrValueDateScope(userId, discntCode, appServiceId, qryStartDate, qryEndDate);
		int userDiscntAttrSize = 0;
		if(IDataUtil.isNotEmpty(userAttrInfos)){
			userDiscntAttrSize += userAttrInfos.size();
		}
		if(IDataUtil.isNotEmpty(tradeAttrInfos)){
			userDiscntAttrSize += tradeAttrInfos.size();
		}
		return userDiscntAttrSize;
	}
	
	/**
	 * @description 获取当前下月 生效 的指定视频流量资费的特定属性的数量(包括未完工的)
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param userId
	 * @param ctrmProductId
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public int getUserNextMonthEffectVFDAttrValueCount(String userId,String discntCode,String appServiceId) throws Exception{
		//下月1号的日期
        String thisDate = SysDateMgr.getFirstDayOfNextMonth();
		IDataset userAttrInfos = UserAttrInfoQry.getUserAttrByDiscntCodeAttrValueThisDate(userId, discntCode, appServiceId, thisDate);
		IDataset tradeAttrInfos = TradeAttrInfoQry.getTradeAttrByDiscntCodeAttrValueThisDate(userId, discntCode, appServiceId, thisDate);
		int userDiscntAttrSize = 0;
		if(IDataUtil.isNotEmpty(userAttrInfos)){
			userDiscntAttrSize += userAttrInfos.size();
		}
		if(IDataUtil.isNotEmpty(tradeAttrInfos)){
			userDiscntAttrSize += tradeAttrInfos.size();
		}
		return userDiscntAttrSize;
	}
	
	
	/**
	 * @description 除特殊值之外，判断字符数组中的值两两是否相同
	 * @author chenmw3
	 * @date 2017-04-21
	 * @param specVal
	 * @param strs
	 * @return
	 */
	public boolean isStrTwoSame(String specVal,String ...strs){
		boolean flag = false;
		one:
		for (int i = 0; i < strs.length-1; i++) {
			if (StringUtils.isBlank(strs[i])||specVal.equals(strs[i])) {
				continue;
			}
			for (int j = i+1; j < strs.length; j++) {
				if (strs[i].equals(strs[j])) {
					flag = true;
					break one;
				}
			}
		}
		return flag;
	} 
	
	/**
	 *   在产品变更时（A、B2个优惠都同属1个app），退订原有A优惠，再订购新B优惠，此时B优惠的生效时间应是下个月1号
	 */
	private void dealDiscntStartDate(IDataset discntTradeInfos, UcaData uca) throws Exception{
		System.out.println("--------huping--------------任我看流量包生失效时间处理--start");
		System.out.println("--------huping--------------任我看流量包生失效时间处理--discntTradeInfos："+discntTradeInfos);
		String eparchyCode = uca.getUser().getEparchyCode();
		IDataset discntConfigInfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "2017" ,"IS_VIDEO_PKG", eparchyCode, null);
		if(IDataUtil.isEmpty(discntConfigInfos)){
			return;
		}
		
		for(int i = 0; i < discntTradeInfos.size(); i++){
			IData discntTradeInfo = discntTradeInfos.getData(i);
			System.out.println("--------huping--------------任我看流量包生失效时间处理--discntTradeInfo+"+i+"："+discntTradeInfo);
			String modifyTag = discntTradeInfo.getString("MODIFY_TAG");
			String discntCode = discntTradeInfo.getString("DISCNT_CODE");
			System.out.println("--------huping--------------任我看流量包生失效时间处理--modifyTag："+modifyTag);
			System.out.println("--------huping--------------任我看流量包生失效时间处理--discntCode："+discntCode);

			IDataset discntConfigInfo = DataHelper.filter(discntConfigInfos, "PARAM_CODE=" + discntCode);	
			System.out.println("--------huping--------------任我看流量包生失效时间处理--discntConfigInfo："+discntConfigInfo);
			if(IDataUtil.isEmpty(discntConfigInfo)){
				continue;
			}
			String appId = discntConfigInfo.getData(0).getString("PARA_CODE20");
			log.debug("--------huping--------------任我看流量包生失效时间处理--appId："+appId);
			boolean updateFlag = false;//判断是否是优惠变更（一条新增，一条删除的情况）
			boolean sameAppFlag = false;
			for(int j = i-1; j >= 0; j--){
				IData discntTradeInfo1 = discntTradeInfos.getData(j);
				log.debug("--------huping--------------任我看流量包生失效时间处理--discntTradeInfo1+"+i+"："+discntTradeInfo1);
				String modifyTag1 = discntTradeInfo1.getString("MODIFY_TAG");
				String discntCode1 = discntTradeInfo1.getString("DISCNT_CODE");
				log.debug("--------huping--------------任我看流量包生失效时间处理--modifyTag1："+modifyTag1);
				log.debug("--------huping--------------任我看流量包生失效时间处理--discntCode1："+discntCode1);
				IDataset discntConfigInfo1 = DataHelper.filter(discntConfigInfos, "PARAM_CODE=" + discntCode1 + ",PARA_CODE20=" + appId);	 
				log.debug("--------huping--------------任我看流量包生失效时间处理--discntConfigInfo1："+discntConfigInfo1);
				if(IDataUtil.isEmpty(discntConfigInfo1 )){
					continue;
				}
				if(discntCode.equals(discntCode1)){
					updateFlag = true;
					break;
				}
				sameAppFlag = true;
				if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && BofConst.MODIFY_TAG_ADD.equals(modifyTag1)){
					discntTradeInfos.getData(j).put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
					log.debug("--------huping--------------任我看流量包生失效时间处理--discntTradeInfos.getData(j)："+discntTradeInfos.getData(j));
				}else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && BofConst.MODIFY_TAG_DEL.equals(modifyTag1)){
					discntTradeInfos.getData(i).put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
					log.debug("--------huping--------------任我看流量包生失效时间处理--discntTradeInfos.getData(i)："+discntTradeInfos.getData(i));
				}
			}
			if(updateFlag){
				continue;
			}
			if(!sameAppFlag && BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
				//修改点 start
				IDataset discntConfigInfo2 = DataHelper.filter(discntConfigInfos, "PARAM_CODE=" + discntTradeInfos.getData(i).getString("DISCNT_CODE") + ",PARA_CODE21=1");
//				System.out.println("ChangeProductVideoFlowDiscntBeanxxxxxxxxxxxxxxxx1594 "+discntConfigInfo2);
				if(discntConfigInfo2==null||discntConfigInfo2.size()==0){
					//如果订购时不存在相同APP，则设置为立即生效
					discntTradeInfos.getData(i).put("START_DATE", SysDateMgr.getSysTime());
				}			
				//修改点 start
			}			
		}
		log.debug("--------huping--------------任我看流量包生失效时间处理--discntTradeInfos："+discntTradeInfos);
		System.out.println("--------huping--------------任我看流量包生失效时间处理--end");
	}
}
