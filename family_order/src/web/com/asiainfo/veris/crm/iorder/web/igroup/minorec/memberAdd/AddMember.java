package com.asiainfo.veris.crm.iorder.web.igroup.minorec.memberAdd;

import com.ailk.bizcommon.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.dataTrans.MinorecIntegrateTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class AddMember extends EopBasePage {

	@Override
	public void initPage(IRequestCycle cycle) throws Exception {
		IData baseDate = getData();
		String ibsysId = baseDate.getString("IBSYSID");
		if (StringUtils.isBlank(ibsysId)) {
			return;
		}
		String nodeId = baseDate.getString("NODE_ID");
		String bpmTempletId = baseDate.getString("BPM_TEMPLET_ID");
		String productId = baseDate.getString("PRODUCT_ID");
		//获取流程信息
        IData info = new DataMap();
        IData nodeTempletedData = new DataMap();
        nodeTempletedData.put("BPM_TEMPLET_ID", bpmTempletId);
        nodeTempletedData.put("NODE_ID", nodeId);
        //流程节点信息
        info.put("NODE_TEMPLETE", nodeTempletedData);
		operTypeByTempletId(bpmTempletId, productId, info);
		//推动流程所需信息
		info.put("EOS_COMMON_DATA", buildEosCommonData(baseDate));
		//获取集团客户信息
		super.initPage(cycle);

		buildOfferInfo(ibsysId, nodeId);
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
        eosCommonData.put("BUSIFORM_NODE_ID", baseDate.getString("BUSIFORM_NODE_ID"));
        eosCommonData.put("BPM_TEMPLET_ID", baseDate.getString("BPM_TEMPLET_ID"));
        eosCommonData.put("BUSI_CODE", baseDate.getString("BUSI_CODE"));
        eosCommonData.put("BUSI_TYPE", baseDate.getString("BUSI_TYPE"));
        return eosCommonData;
	}

	/**
	 * @Title:buildOfferInfo
	 * @Description: 拼接开通时的offer信息+成员信息
	 * @param @param ibsysId  工单id
	 * @param @param nodeId  流程节点id
	 * @param @throws Exception
	 * @return void
	 * @throws
	 */
	private void buildOfferInfo(String ibsysId, String nodeId) throws Exception {
		IData param = new DataMap();
		param.put("IBSYSID", ibsysId);

		IDataset memberDataset = CSViewCall.call(this, "SS.QuickOrderMemberSVC.getMemberInfoListBySysId", param);
		// 所有成员产品的成员号码数据
		IData membersData = new DataMap();
		 
		for (Object object : memberDataset) {
			IData memberData = (IData) object;
			//转换获取原始数据
			StringBuilder memberStr = new StringBuilder();
			buildOfferStr(memberData, memberStr);
			if (DataUtils.isNotEmpty(memberStr.toString()))
			{
				DataMap memberOffer = new DataMap(memberStr.toString());
				
				String offerKey = "offer" + memberData.getString("OFFER_CODE");
				if (!membersData.containsKey(offerKey)) {
					membersData.put(offerKey, new DatasetList());
				}
				membersData.getDataset(offerKey).add(memberOffer);
			}
		
		}
		
		IDataset offerDataset = CSViewCall.call(this, "SS.QuickOrderDataSVC.getQuickorderData", param);

		// 获取offer信息
		IDataset offerInfoList = new DatasetList();
		for (Object object : offerDataset) {
			IData quickorderCond = (IData) object;
			StringBuilder offerStr = new StringBuilder();
			buildOfferStr(quickorderCond, offerStr);
			// 转换
			IData offerInfo = new DataMap(offerStr.toString());
			String offerKey = "offer" + quickorderCond.getString("PRODUCT_ID");
			offerInfo.put("MEB_LIST", membersData.getDataset(offerKey));
			offerInfoList.add(offerInfo);

            IData ecCommonInfo = offerInfo.getData("EC_COMMON_INFO");
            IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
            String contractId = contractInfo.getString("CONTRACT_ID");

            IData agreementElementData = queryContractOfferChaList(contractId, quickorderCond.getString("PRODUCT_ID"));// 电子协议信息回填接口

			//商务宽带需要特殊处理
			if ("7341".equals(quickorderCond.getString("PRODUCT_ID")))
			{
                IDataset ecOfferList = agreementElementData.getDataset("EC_OFFER");
                IDataset ecOfferChaList = ecOfferList.getData(0).getDataset("OFFER_CHA_SPECS");
                IData agreementElement = new DataMap();
                if (IDataUtil.isNotEmpty(ecOfferChaList))
                {
                    if ("EP_WITH".equals(ecOfferChaList.getData(0).getString("ATTR_CODE")))
                    {
                        agreementElement.put("EP_WITH", ecOfferChaList.getData(0).getString("ATTR_VALUE"));
                        setAgreementElement(agreementElement);
                    }
                }

				IDataset offerMember = membersData.getDataset(offerKey);

                //开户方式与支付模式
                mergeWideUserStyleCheck();

				if(IDataUtil.isEmpty(offerMember))
				{
				    IData wideInfo = new DataMap();
				    wideInfo.put("AUTH_SERIAL_NUMBER",offerInfo.getString("EC_SERIAL_NUMBER"));
                    setWideInfo(wideInfo);
				    continue;
                }

				setWidenetInfos(offerMember);
				setWideInfo(offerMember.first());

				String ecSerialNumber = offerMember.first().getString("EC_SERIAL_NUMBER");
				String wideProductType = offerMember.first().getString("WIDE_PRODUCT_TYPE");
				String wideProductName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC",
						new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME",
						new String[] { "WIDE_PRODUCT_TYPE", wideProductType });

				changeWideProductType(wideProductName, ecSerialNumber);// 获取宽带产品

		        IDataset selectedElement = offerMember.first().getDataset("SELECTED_ELEMENTS");// 宽带资费信息
		        IData elementInfo = new DataMap();
		        elementInfo.put("SELECTED_ELEMENTS", selectedElement);// 所有资费放到隐藏域
			    setSaleActiveList(elementInfo);
		        setSaleActiveListAttr(selectedElement);

			}
		}
		
		setOfferInfoList(offerInfoList);
	}
	


	public void importFile(IRequestCycle cycle) throws Exception {
		IData data = this.getData();
		String productId = data.getString("PRODUCT_ID");
		IDataset datalineInfo = (IDataset) SharedCache.get("IMPORT_MEMBER_INFO"+productId);
		setAjax(datalineInfo);

	}

	@Override
	public void buildOtherSvcParam(IData submitData) throws Exception
    {
        MinorecIntegrateTrans.transformAddMemberSubmitData(submitData);
    }
	
	private void buildOfferStr(IData quickorderCond, StringBuilder offerInitStr) {
		for (int i = 1; i <= 10; i++) {
			if (StringUtils.isNotBlank(quickorderCond.getString("CODING_STR" + i))) {
				offerInitStr.append(quickorderCond.getString("CODING_STR" + i));
			}
		}

	}
	
	/**
	   * 查询流程信息
	 * @param info 
	 * 
	 * @param
	 * @throws Exception
	 */
    public void operTypeByTempletId(String templetId, String offerCode, IData info) throws Exception {
    	 if ("FUSECOMMUNICATIONOPEN".equals(templetId) || "2222".equals(offerCode) || "8001".equals(offerCode)) {
             offerCode = "VP998001";
         } else if ("YIDANQINGSHANGPU".equals(templetId) || "8000".equals(offerCode)) {
             offerCode = "VP99999";
         } else if ("YIDANQINGJIUDIAAN".equals(templetId)) {
             offerCode = "VP66666";
         }
    	 
        IData templetInfo = WorkfromViewCall.getOperTypeByTempletId(this, templetId);
        IData input = new DataMap();
        input.put("BUSI_CODE", offerCode);
        input.put("OPER_TYPE", templetInfo.getString("BUSI_OPER_TYPE"));

        info.put("OPER_TYPE",templetInfo.getString("BUSI_OPER_TYPE"));
        IDataset busiSpecReleList = CSViewCall.call(this, "SS.BusiSpecReleInfoSVC.qryInfoByOfferIdOperType", input);
        
        if (IDataUtil.isNotEmpty(busiSpecReleList)) {
            busiSpecReleList.first().put("TEMPLET_ID", info.getString("TEMPLET_ID"));
            info.put("BUSI_SPEC_RELE", busiSpecReleList.first());
            info.put("TEMPLET_BUSI_CODE", busiSpecReleList.first().getString("BUSI_CODE"));
        } 
        setInfo(info);
    }

    /**
     * 获取开户方式 支付模式等
     * 
     * @param
     * @throws Exception
     */
    private void mergeWideUserStyleCheck() throws Exception {

        // 付费模式权限控制
        IDataset widenetPayMode = StaticUtil.getStaticList("WIDENET_PAY_MODE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDENET_PAY_MODE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(widenetPayMode)) {
                for (int k = 0; k < widenetPayMode.size(); k++) {
                    if ("A".equals(widenetPayMode.getData(k).getString("DATA_ID"))) {
                        widenetPayMode.remove(k);
                        break;
                    }
                }
            }
        }

        // 宽带开户方式权限控制
        IDataset mergeWideUserStyleInfos = StaticUtil.getStaticList("HGS_WIDE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HGS_WIDE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(mergeWideUserStyleInfos)) {
                for (int k = 0; k < mergeWideUserStyleInfos.size(); k++) {
                    if ("1".equals(mergeWideUserStyleInfos.getData(k).getString("DATA_ID"))) {
                        mergeWideUserStyleInfos.remove(k);
                        break;
                    }
                }
            }
        }
        setMergeWideUserStyleList(mergeWideUserStyleInfos);
        setWidenetPayModeList(widenetPayMode);
    }
 
    /** 
     **  获取宽带产品
     * @param openType
     * @param serNumber
     * @throws Exception
     * @Date 2019年10月14日
     * @author xieqj 
     */
    public void changeWideProductType(String openType, String serNumber) throws Exception {

        IData data = new DataMap();
        data.put("ROUTE_EPARCHY_CODE", "0898");
        data.put("FLAG", "MINOREC");
        data.put("SERIAL_NUMBER", serNumber);
        if ("铁通ADSL".equals(openType)) {
            data.put("wideProductType", "2");
        } else if ("移动FTTH".equals(openType) || "铁通FTTH".equals(openType)) {
            data.put("wideProductType", "5");
        } else if ("移动FTTB".equals(openType) || "铁通FTTB".equals(openType)) {
            data.put("wideProductType", "6");
        }
        IDataset dataset = CSViewCall.call(this, "SS.MergeWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);

        setProductList(dataset);
    }
    
    /**
     ** 展示地址信息
     * @param cycle
     * @throws Exception
     * @Date 2019年10月14日
     * @author xieqj 
     */
    public void queryWidenetTable(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData map = new DataMap();
        IDataset addressList = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST"));// 地址信息
        IDataset addressList1 = new DatasetList(pageData.getString("DETAIL_ADDRESS_LIST1"));// 表格里面存在的地址信息
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, pageData.getString("GROUP_ID"));
        String serNumber = pageData.getString("SERIAL_NUMBER");
        String productType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "WIDE_PRODUCT_TYPE", pageData.getString("WIDE_PRODUCT_TYPE") });
        String custId = result.getString("CUST_ID", "");
        if (IDataUtil.isEmpty(addressList1)) { // 表格不存在信息，直接返回
            String openType = addressList.first().getString("OPEN_TYPE", "");
            String wideOpenType = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "WIDE_PRODUCT_TYPE", openType });
            map.put("WIDE_PRODUCT_TYPE", wideOpenType);
            for (int i = 0; i < addressList.size(); i++) {
                IData attachInfo = addressList.getData(i);
                attachInfo.put("STAND_ADDRESS", attachInfo.getString("REGION_NAME"));
                attachInfo.put("FLOOR_AND_ROOM_NUM", attachInfo.getString("GIS8"));
            }
            setInfo(map);
            changeWideProductType(openType, serNumber);// 获取宽带产品
            getWideSerialNumber(addressList, serNumber, custId);
        } else {
            IDataset infos = new DatasetList();
            // 过滤表格与新增存在相同的地址信息
            for (int i = 0; i < addressList.size(); i++) {
                IData attachInfo = addressList.getData(i);
                String regionName = attachInfo.getString("REGION_NAME");
                String operType = attachInfo.getString("OPEN_TYPE");
                if (!operType.equals(productType)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "您已选择了【" + productType + "】宽带类型，不能勾选【" + operType + "】的类型，请重新选择!");
                }
                String wideProductId = "";
                boolean flag = false;
                for (int j = 0; j < addressList1.size(); j++) {
                    IData infoData = addressList1.getData(j);
                    String regionNames = infoData.getString("STAND_ADDRESS");
                    wideProductId = infoData.getString("WIDE_PRODUCT_ID");
                    if (regionNames.equals(regionName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    attachInfo.put("WIDE_PRODUCT_ID", wideProductId);
                    attachInfo.put("STAND_ADDRESS", attachInfo.getString("REGION_NAME"));
                    attachInfo.put("FLOOR_AND_ROOM_NUM", attachInfo.getString("GIS8"));
                    infos.add(attachInfo);
                }
            }
            addressList1.addAll(infos);
            getWideSerialNumber(addressList1, serNumber, custId);
        }
    }

    /**
     * 宽带产品资费属性，获取必选的元素
     *
     * @Author taosx
     * @throws Exception
     */
    public void getWidenetUserOpenElement(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData map = new DataMap();
        map.put("NEW_PRODUCT_ID", data.getString("PRODUCT_ID"));
        map.put("ROUTE_EPARCHY_CODE", "0898");

        IDataset dataset = CSViewCall.call(this, "CS.SelectedElementSVC.getWidenetUserOpenElements", map);
        String hint = "";
        if (IDataUtil.isNotEmpty(dataset)) {
            IData elementInfo = new DataMap();
            IDataset selectedElements = new DatasetList(dataset.getData(0).getString("SELECTED_ELEMENTS"));
            for (Object object : selectedElements)
            {
                IData elementsInfo = (IData) object;
                if ("D".equals(elementsInfo.getString("ELEMENT_TYPE_CODE"))) {
                    String productId = elementsInfo.getString("PRODUCT_ID");

                    IData rateData = getData();
                    rateData.put(Route.ROUTE_EPARCHY_CODE, "0898");
                    rateData.put("NEW_PRODUCT_ID", productId);
                    IData resultData = CSViewCall.callone(this, "SS.NoPhoneWideUserCreateSVC.getProductRateByProductId", rateData);

                    if (IDataUtil.isNotEmpty(resultData)) {
                        String epWith = data.getString("EP_WITH");// 协议填写宽带兆数
                        if (StringUtils.isBlank(epWith))
                        {
                            hint = "您未填写电子协议里的宽带兆数！";
                        }
                        else
                        {
                            String wideWith = resultData.getString("NEW_RATE", "");
                            if (StringUtils.isBlank(wideWith))
                            {
                                hint = "TD_S_COMMPARA表未找到产品【" + productId + "】宽带兆数对应关系！";
                            }
                            else
                            {
                                int wideWithi = Integer.parseInt(wideWith);
                                int is = 1024;
                                int widei = wideWithi / is;
                                if (!String.valueOf(widei).equals(epWith))
                                {
                                    hint = "您电子协议输入的宽带兆数为:【" + epWith + "M】,选择的宽带产品是:【" + widei + "M】";
                                }
                                else
                                {
                                    hint = "您电子协议输入的宽带兆数与产品选择的兆数相同，都为【" + epWith + "M】";
                                }
                            }
                        }
                    } else {
                        hint = "TD_S_COMMPARA表未找到产品【" + productId + "】宽带兆数对应关系！";
                    }
                }
            }

            // 写死资费属性
            IDataset saleActiveInfos = new DatasetList();
            IData saleActiveInfo = new DataMap();

            saleActiveInfo.put("ELEMENT_ID", "20001088");
            saleActiveInfo.put("ELEMENT_TYPE_CODE", "D");
            saleActiveInfo.put("PRODUCT_ID", "20150150");
            saleActiveInfo.put("PACKAGE_ID", "20150153");
            saleActiveInfo.put("MODIFY_TAG", "0");
            saleActiveInfo.put("START_DATE", SysDateMgr.getSysDate());
            saleActiveInfo.put("END_DATE", "2050-12-31 00:00:00 ");
            saleActiveInfo.put("INST_ID", "");
            saleActiveInfo.put("ELEMENT_NAME", "宽带套餐(0元包月-集团商务宽带使用)");
            saleActiveInfos.add(saleActiveInfo);
            selectedElements.addAll(saleActiveInfos);
            elementInfo.put("SELECTED_ELEMENTS", selectedElements);
            setSaleActiveListAttr(saleActiveInfos);
            setSaleActiveList(elementInfo);

            IData hintData = new DataMap();
            hintData.put("HINT", hint);
            this.setAjax(hintData);
        }

    }

    /**
     * 电子协议信息回填接口
     *
     * @param
     * @throws Exception
     */
    public IData queryContractOfferChaList(String contractId, String offerCode) throws Exception {

        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);
        param.put("OFFER_CODE", offerCode);
        IData agreementElementData = CSViewCall.callone(this, "SS.ContractOfferChaListSVC.queryContractOfferChaList", param);
        return agreementElementData;
    }


    /**
     ** 获取宽商务宽带开户特殊处理，号码需要在原号码后加0000，并递增
     * @param addressList
     * @param serNumber
     * @param custId
     * @throws Exception
     * @Date 2019年10月14日
     * @author xieqj 
     */
    public void getWideSerialNumber(IDataset addressList, String serNumber, String custId) throws Exception {

        IDataset wideAddressList = new DatasetList();// 把没有宽带号码的放到一个集合里面
        IDataset wideAddressList1 = new DatasetList();// 把已有宽带号码的放到一个集合里面
        IData data = new DataMap();
        String serialNumb = "";// 获取已有的最大宽带号码
        for (int i = 0; i < addressList.size(); i++) {
            IData adderssInfo = addressList.getData(i);
            String serialNumber = adderssInfo.getString("WIDE_SERIAL_NUMBER", "");
            if (StringUtils.isBlank(serialNumber)) {
                wideAddressList.add(adderssInfo);
                continue;
            } else {
                wideAddressList1.add(adderssInfo);
                for (int j = 0; j < addressList.size(); j++) {
                    IData adderssInfoJ = addressList.getData(j);
                    String SerialNumberJ = adderssInfoJ.getString("WIDE_SERIAL_NUMBER", "");
                    if (StringUtils.isNotBlank(SerialNumberJ) && SerialNumberJ.compareTo(serialNumber) > 0) {
                        serialNumb = SerialNumberJ;
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(wideAddressList)) {
            int serNumberSize = wideAddressList.size();
            data.put("WIDE_SERIAL_NUMBER", serialNumb);
            data.put("SERIAL_NUMBER", serNumber);
            data.put("SERNUMBER_SIZE", serNumberSize);
            data.put("OPER_TYPE", "crtUs");
            data.put("CUST_ID", custId);
            // 获取新的宽带号码
            IDataset wideSNdataset = CSViewCall.call(this, "SS.WideUserCreateSVC.getWideSerialNumberMinorec", data);
            // 给新增号码赋值
            if (IDataUtil.isNotEmpty(wideSNdataset)) {
                for (int i = 0; i < wideSNdataset.size(); i++) {
                    IData sideSnInfo = wideSNdataset.getData(i);
                    String sideSerNumber = sideSnInfo.getString("SERIAL_NUMBER");
                    IData wideAddressInfo = wideAddressList.getData(i);
                    wideAddressInfo.put("WIDE_SERIAL_NUMBER", sideSerNumber);
                }
            }
            wideAddressList1.addAll(wideAddressList);// 拼接已有宽带与新增宽带号码
            setWidenetInfos(wideAddressList1);
        } else {
            // 如果都有宽带号码直接返回
            setWidenetInfos(addressList);
        }
    }
    
    public abstract void setWideInfo(IData widenetInfo);
    public abstract void setWidenetInfos(IDataset widenetInfos);
    
    public abstract void setSaleActiveList(IData saleActiveList) throws Exception;

    public abstract void setSaleActiveListAttr(IDataset saleActiveListAttr) throws Exception;
    
    public abstract void setProductList(IDataset productList) throws Exception;
    
    public abstract void setMergeWideUserStyleList(IDataset mergeWideUserStyleInfos) throws Exception;
    public abstract void setWidenetPayModeList(IDataset widenetPayMode) throws Exception;

    public abstract void setAgreementElement(IData pattrInfo) throws Exception;

	public abstract void setOfferInfoList(IDataset groupInfo) throws Exception;
    public abstract void setInfo(IData info) throws Exception;

 }
