package com.asiainfo.veris.crm.iorder.web.igroup.minorec.esp;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class EspProductDetailShow extends EopBasePage {

	/**
	 * 初始化方法
	 * 
	 * @param cycle
	 * @throws Exception
	 */

	public void initPage(IRequestCycle cycle) throws Exception {
		IData baseDate = getData();
		String ibsysId = baseDate.getString("IBSYSID");
		String productId = baseDate.getString("PRODUCT_ID");
		super.initPage(cycle);
		IDataset memberDataset = new DatasetList();
		IDataset offerDataset = new DatasetList();
        // 获取成员 预受理最新数据
        IData param = new DataMap();
        memberDataset = getMemInfoList(baseDate, param);
 		
		// 获取offer信息
		offerDataset = CSViewCall.call(this, "SS.QuickOrderDataSVC.getNewQuickorderData", param);
		//重新赋值PRODUCT_ID
		productId = param.getString("PRODUCT_ID");
		IData info = new DataMap();
		//管理员id 地州编码
		info.put("staffId", baseDate.getString("staffId"));
		info.put("eparchyCode", baseDate.getString("eparchyCode"));
		
		//推动流程所需信息
		info.put("EOS_COMMON_DATA", buildEosCommonData(baseDate));
		
		info.put("PRODUCT_ID", productId);
		info.put("IBSYSID", ibsysId);
		buildEcOffer(offerDataset, memberDataset, baseDate, info);
		
		IData ecOffer = info.getData("ecOffer");
		
		String operType = baseDate.getString("BUSIFORM_OPER_TYPE");
		info.put("operType", operType);
		//td_s_static表APPLY_REQUIREMENT_OPER_TYPE定义
		if (!"21".equals(operType))
		{
			if ("921015".equals(productId))
	        {
	        	initPriceOfferCha(productId, ecOffer);
	        }else if ("380700".equals(productId)) {
	        	// 和商务TV处理
	    		initPriceOfferCha(productId, ecOffer);
	        }else if ("380300".equals(productId)) {
	        	 
	    		initPriceOfferCha(productId, ecOffer);
	        }
	        else {
	        	
	        }
		} else if ("DstUser".equals(ecOffer.getString("OPER_TYPE"))) {
			espProductProps(productId, info);
		}
		querySynchrostateESP(info);
        // 合同电子协议附件信息
        queryElectronicArchives(info);
		setInfo(info);
	}

	private void espProductProps(String productId, IData info) throws Exception {
		IData inparams = new DataMap();
		//获取userId
		inparams.put("SERIAL_NUMBER", info.getData("ecOffer").getString("SERIAL_NUMBER"));
		IDataset userList = CSViewCall.call(this, "SS.QuickOrderDataSVC.qryUserInfoUserIdBySerialNumber", inparams);
		if (DataUtils.isEmpty(userList)) {
			return;
		}
		//获取user attr
		inparams.put("USER_ID", userList.first().getString("USER_ID"));
		IDataset userAttrInfoList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByUserId", inparams);
		if (DataUtils.isEmpty(userAttrInfoList)) {
			return;
		}
		inparams.clear();
		//获取bboss attr
		inparams.put("PRODUCTSPECNUMBER", productId);
		inparams.put("BIZ_TYPE", "1");// 业务类型：1-集团业务，2-成员业务
		IDataset bbossAttrInfoList = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", inparams);
		if (DataUtils.isEmpty(bbossAttrInfoList)) {
			return;
		}
		IData userAttrMap = new DataMap();
		for (int i = 0, len =bbossAttrInfoList.size(); i < len; i++) {
			IData bbossAttr = (IData)bbossAttrInfoList.get(i);
			userAttrMap.put(bbossAttr.getString("ATTR_CODE"), bbossAttr);
		}
		IData resultData = new DataMap();
		for (int i = 0, len =userAttrInfoList.size(); i < len; i++) {
			IData userAttr = (IData)userAttrInfoList.get(i);
			IData bbossAttr = userAttrMap.getData(userAttr.getString("ATTR_CODE"));
			if ("TEXT".equals(bbossAttr.getString("EDIT_TYPE"))) {
				resultData.put("ATTR_NAME", bbossAttr.getString("ATTR_NAME"));
				resultData.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
			} else if ("SELECTION".equals(bbossAttr.getString("EDIT_TYPE"))) {
				inparams.clear();
				inparams.put("PARAMCODE", userAttr.getString("ATTR_CODE"));
				IDataset bbossBoundDataList = CSViewCall.call(this, "CS.BoundDataQrySVC.qryBoundDataByParamcode", inparams);
				if (DataUtils.isEmpty(bbossBoundDataList)) {
					continue;
				}
				for (int j = 0, size =bbossBoundDataList.size(); j < size; j++) {
					IData boundData = (IData)bbossBoundDataList.get(i);
					if (userAttr.getString("ATTR_VALUE").equals(boundData.getString("OPTION_VALUE"))) {
						resultData.put("ATTR_NAME", bbossAttr.getString("ATTR_NAME"));
						resultData.put("ATTR_VALUE", boundData.getString("OPTION_NAME"));
						break;
					}
				}
			}

		}
	}

	/**
	 ** 获取esp产品最新成员数据
	 * @param baseDate
	 * @param param
	 * @throws Exception
	 * @Date 2019年10月28日
	 * @author xieqj 
	 * @return 
	 */
	private IDataset getMemInfoList(IData baseDate, IData param) throws Exception {
		param.put("IBSYSID", baseDate.getString("IBSYSID"));
		String productId = baseDate.getString("PRODUCT_ID");
		//一单清产品包含esp产品，需找到对应的PRODUCT_ID
		if("VP66666".equals(productId))
		{
			IDataset list = null;
			IData paramData = new DataMap();
			paramData.put("IBSYSID", baseDate.getString("IBSYSID"));
			//获取父子流程关系，根据子流程的BUSIFORM_ID找到子流程的RECORD_NUM
			paramData.put("SUB_BUSIFORM_ID", baseDate.getString("BUSIFORM_ID"));
			list = CSViewCall.call(this,"SS.WorkformReleBeanSVC.qryBySubBusiformId", paramData);
			if (DataUtils.isEmpty(list)) {
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID" + baseDate.getString("BUSIFORM_ID") + "没有查询到父子流程关系信息！");
			}
			//获取tf_b_eop_product 中的productId
			String recordNum = list.first().getString("RELE_VALUE");
			paramData.put("RECORD_NUM", recordNum);
			list = CSViewCall.call(this,"SS.WorkformProductSVC.qryEopProductByIbsysId", paramData);
			if (DataUtils.isEmpty(list)) {
				CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID" + baseDate.getString("IBSYSID") + "没有查询到定单产品信息！");
			}
			productId = list.first().getString("PRODUCT_ID");;
  		}
		param.put("PRODUCT_ID", productId);
		IDataset memberDataset = CSViewCall.call(this, "SS.QuickOrderMemberSVC.getEspNewsMemberInfoList", param);
		return memberDataset;
	}

	/**
	 ** 查询电子协议附件信息
	 * @param info
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj 
	 */
	private void queryElectronicArchives(IData info) throws Exception {
		IData ecCommonInfo = info.getData("EC_COMMON_INFO");
		IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
		IData param = new DataMap();
		param.put("AGREEMENT_ID", contractInfo.getString("CONTRACT_ID"));
		//获取正文电子协议的 档案编码
		IData eleAgreement = CSViewCall.callone(this, "SS.ElectronicAgreementBeanSVC.getElectronicAgreementData", param);
		String archivesId = eleAgreement.getString("ARCHIVES_ID");
		
		//获取附加电子协议的 档案编码
		IDataset eleAgreAttachList = CSViewCall.call(this, "SS.ElectronicAgreAttachBeanSVC.getElectronicAgreAttachData", param);
		if (DataUtils.isNotEmpty(eleAgreAttachList)) {
			StringBuilder ids = new StringBuilder(archivesId);
			for (int i = 0, len = eleAgreAttachList.size(); i < len; i++) {
				IData eleAgreAttach = eleAgreAttachList.getData(i);
				ids.append(",").append(eleAgreAttach.getString("ARCHIVES_ID"));
			}
			archivesId = ids.toString();
		}
		
		param.clear();
		param.put("ARCHIVES_IDS", archivesId);
		
		IDataset eleArchiveslist = CSViewCall.call(this, "SS.ElectronicArchivesBeanSVC.getElectronicArchivesData", param);
		
 		DatasetList archivesList = new DatasetList();
		for (int i = 0, len = eleArchiveslist.size(); i < len; i++) {
			IData data2 = eleArchiveslist.getData(i);
			IData archives = new DataMap();
			archives.put("ARCHIVES_ID", data2.getString("ARCHIVES_ID"));
			archives.put("ARCHIVES_NAME", data2.getString("ARCHIVES_NAME"));
			archives.put("ARCHIVES_ATTACH", data2.getString("ARCHIVES_ATTACH"));
			archivesList.add(archives);
		}
		
 		info.put("ELECTRONIC_ARCHIVES", archivesList);
	}

	/**
	 ** 转义 资费子商品中特征
	 * @param productId 主商品id
	 * @param ecOffer   主商品信息
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj 
	 */
	private void initPriceOfferCha(String productId, IData ecOffer) throws Exception {
		IDataset dataset = ecOffer.getDataset("SUBOFFERS");
		if (DataUtils.isEmpty(dataset))
		{
			 return;
		}
		for (Object object : dataset) {
			IData  priceOffer = (IData)object;
			String offerId = priceOffer.getString("OFFER_ID");
			
			IDataset priceOfferChaSpecs = priceOffer.getDataset("OFFER_CHA_SPECS");
			
			IData map = new DataMap();
			for  (int i = 0, size = priceOfferChaSpecs.size(); i < size; i++)
			{
				IData priceOfferChaSpecData = priceOfferChaSpecs.getData(i);
				map.put(priceOfferChaSpecData.getString("ATTR_CODE"), priceOfferChaSpecData.getString("ATTR_VALUE"));
			}
			//清空重新设值
			priceOfferChaSpecs.clear();
			//查询配置 获取资费商品特征
		    IDataset priceOfferChaList = IUpcViewCall.queryChaByOfferId(offerId);
		    if (DataUtils.isNotEmpty(priceOfferChaList))
		    {
				for (int i = 0, size = priceOfferChaList.size(); i < size; i++) {
					IData priceOfferChaData = priceOfferChaList.getData(i);
					String attrCode = priceOfferChaData.getString("FIELD_NAME");
					if (map.containsKey(attrCode)) {
						IData newPriceOfferChaSpecData = new DataMap();
						newPriceOfferChaSpecData.put("ATTR_CODE", attrCode);
						newPriceOfferChaSpecData.put("ATTR_VALUE", map.getString(attrCode));
						if ("380700".equals(productId) || "380300".equals(productId))
						{
							newPriceOfferChaSpecData.put("ATTR_NAME", priceOfferChaData.getString("DESCRIPTION"));
						}else {
							newPriceOfferChaSpecData.put("ATTR_NAME", priceOfferChaData.getString("CHA_SPEC_NAME"));
							newPriceOfferChaSpecData.put("ATTR_FLAG", true);
						}
						priceOfferChaSpecs.add(newPriceOfferChaSpecData);
					}
				}
		         
		    }	
		}
	}
	
	@Override
	public void submit(IRequestCycle cycle) throws Exception
    {
		IData submitData = new DataMap(getData().getString("SUBMIT_PARAM"));
		IData flowData = submitData.getData("COMMON_DATA");
        IData param = new DataMap();
        param.put("IBSYSID", flowData.getString("IBSYSID"));
        param.put("BUSIFORM_ID", flowData.getString("BUSIFORM_ID"));
        param.put("NODE_ID", flowData.getString("NODE_ID"));
        // 推动流程驱动
        IDataset result = CSViewCall.call(this, "SS.WorkformDriveSVC.execute", param);
        setAjax(result.first());
    }

	/**
	 * @Title:buildEcOffer
	 * @Description: 组装ECoffer数据
	 * @param @param offerDataset  offer信息
	 * @param @param memberDataset 成员信息
	 * @param @param baseDate     请求参数
	 * @param @param info         页面返回数据
	 */
	private void buildEcOffer(IDataset offerDataset, IDataset memberDataset, IData baseDate, IData info) {
		
		StringBuilder offerInitStr = new StringBuilder();
		buildOfferStr(offerDataset.first(), offerInitStr);
		IData offerInitData = new DataMap(offerInitStr.toString());
		
		// 所有成员产品的成员号码数据
		IDataset membersList = new DatasetList();

		for (Object object : memberDataset) {
			IData memberData = (IData) object;
			// 转换获取原始数据
			StringBuilder memberStr = new StringBuilder();
			buildOfferStr(memberData, memberStr);
			DataMap memberOffer = new DataMap(memberStr.toString());
			membersList.add(memberOffer);
		}
		
		info.put("ecOffer", offerInitData.getData("EC_OFFER"));
		info.put("mebList", membersList);
		//上传的成员导入文件，是否需要，待定。。。
		info.put("mebFile", offerInitData.getData("MEB_FILE"));
		
		info.put("CUST_ID", offerInitData.getString("CUST_ID"));
		info.put("EC_COMMON_INFO", offerInitData.getData("EC_COMMON_INFO"));
		info.put("DEAL_TYPE", offerInitData.getString("DEAL_TYPE"));
		info.put("OPER_CODE", offerInitData.getString("OPER_CODE"));
		info.put("SERIAL_NUMBER", offerInitData.getString("SERIAL_NUMBER"));
//		info.put("ORDER_STAFF_ID", offerInitData.getString("ORDER_STAFF_ID"));
//		info.put("ORDER_STAFF_PHONE", offerInitData.getString("ORDER_STAFF_PHONE"));
		
	}

	private void buildOfferStr(IData quickorderCond, StringBuilder offerInitStr) {
		for (int i = 1; i <= 10; i++) {
			if (StringUtils.isNotBlank(quickorderCond.getString("CODING_STR" + i))) {
				offerInitStr.append(quickorderCond.getString("CODING_STR" + i));
			}
		}
	}
	
	/**
	 * @Title:buildEosCommonData
	 * @Description:组装推动流程所需信息
	 * @param @param baseDate
	 * @param @return
	 * @return IData
	 * @throws
	 */
	private IData buildEosCommonData(IData baseDate) {
		IData eosCommonData = new DataMap();
        eosCommonData.put("IBSYSID", baseDate.getString("IBSYSID"));
        eosCommonData.put("BUSIFORM_ID", baseDate.getString("BUSIFORM_ID"));
        eosCommonData.put("NODE_ID", baseDate.getString("NODE_ID"));
        return eosCommonData;
	}
	
	 /**
     ** 查询客户是否已经同步ESP平台
     * @param info
     * @throws Exception
     * @Date 2019年12月8日
     * @author xieqj 
     */
    public void querySynchrostateESP(IData info) throws Exception {
    	IData groupInfo = getGroupInfo();
        IData cond = new DataMap();
        cond.put("CUST_ID", groupInfo.getString("CUST_ID"));
        IData result = CSViewCall.callone(this, "CC.group.IGroupQuerySV.queryGroupSynchrostateESP", cond);
        IDataset resultEsp = result.getDataset("DATAS");

        IData espSnyInfo = new DataMap();
        if (resultEsp != null && resultEsp.size() != 0) {
            IData synchroinfo = (IData) resultEsp.get(0);
            espSnyInfo.putAll(synchroinfo);
        } else {
            espSnyInfo.put("SYN_ESP", "0");
            espSnyInfo.put("SYN_ESPSTATE", "未同步");
        }
        info.put("espSny", espSnyInfo);
    }
    
    /**
     ** 刷新ESP同步状态
     * @param cycle
     * @throws Exception
     * @Date 2019年12月13日
     * @author xieqj 
     */
    public void refreshESPStatus(IRequestCycle cycle) throws Exception {
        IData cond = new DataMap();
        cond.put("CUST_ID", getData().getString("CUST_ID"));
        IData result = CSViewCall.callone(this, "CC.group.IGroupQuerySV.queryGroupSynchrostateESP", cond);
        IDataset resultEsp = result.getDataset("DATAS");

        IData espSnyInfo = new DataMap();
        if (resultEsp != null && resultEsp.size() != 0) {
            IData synchroinfo = (IData) resultEsp.get(0);
            espSnyInfo.putAll(synchroinfo);
        } else {
            espSnyInfo.put("SYN_ESP", "0");
            espSnyInfo.put("SYN_ESPSTATE", "未同步");
        }
        setAjax(espSnyInfo);
    }
    
    public abstract IData getGroupInfo() throws Exception;
    
	public abstract void setInfo(IData info) throws Exception;

}
