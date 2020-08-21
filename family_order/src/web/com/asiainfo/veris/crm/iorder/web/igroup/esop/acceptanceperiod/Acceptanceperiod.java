package com.asiainfo.veris.crm.iorder.web.igroup.esop.acceptanceperiod;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class Acceptanceperiod extends EopBasePage {

    public abstract void setInAttr(IData inAttr);

    public abstract void setPattr(IData pattr);

    public abstract void setInfo(IData info);

    public abstract void setAuditInfo(IData info);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setAttachInfos(IDataset attachInfos);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setBilingInfo(IData bilingInfo);

    public void initPage(IRequestCycle cycle) throws Exception {
        //super.initPage(cycle);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        String busiformId = param.getString("BUSIFORM_ID");
        String condbpmTempletId = param.getString("BPM_TEMPLET_ID");
        IData info = new DataMap();
        if (StringUtils.isNotBlank(ibsysid)) {
            //计费方式审核，取关联专线流程IBSYSID
            if("ACCEPTANCEPERIOD".equals(condbpmTempletId)) {
                IData input = new DataMap();
                input.put("IBSYSID", ibsysid);
                IDataset modiTraceDatas = CSViewCall.call(this, "SS.WorkformModiTraceSVC.qryModiTraceByIbsysid", input);
                if(IDataUtil.isEmpty(modiTraceDatas)) {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到计费方式审核所关联专线开通流程！");
                }
                ibsysid = modiTraceDatas.first().getString("MAIN_IBSYSID");
            }
            queryInfosByIbsysid(ibsysid);
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到IBSYSID！");
        }
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + param.getString("IBSYSID") + "未查询到流程订单主表数据！");

        }
        subscribeData.put("NODE_ID", nodeId);
        subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
        subscribeData.put("BUSIFORM_ID", busiformId);
        subscribeData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));//子流程bpm_templet_id不一样，改成从入参里取

        queryCommonAttr(subscribeData, info);

        info.put("PRODUCT_ID", subscribeData.getString("BUSI_CODE"));
        //info.put("TEMPLET_ID", subscribeData.getString("RSRV_STR2"));
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        info.put("NODE_ID", nodeId);
        String bpmTempletId = subscribeData.getString("BPM_TEMPLET_ID");
        info.put("BPM_TEMPLET_ID", bpmTempletId);
        String productId = subscribeData.getString("BUSI_CODE");
        if (bpmTempletId.equals("EDIRECTLINEOPENPBOSS") || bpmTempletId.equals("EVIOPDIRECTLINEOPENPBOSS")) {
            if (productId.equals("7011") || productId.equals("7012") || productId.equals("7010")|| productId.equals("7016")
            		||productId.equals("70111") || productId.equals("70112") ||productId.equals("70121") || productId.equals("70122")) {
                info.put("ShowContract", "1");
            }
        }else if(bpmTempletId.equals("EDIRECTLINECONTRACTCHANGE")) {
        	info.put("ShowContract", "1");
        } else if("DATAFREEDOMRECHARGEOPEN".equals(bpmTempletId) || "DATAFREEDOMRECHARGECANCEL".equals(bpmTempletId) || "DATAFREEDOMRECHARGECHANGE".equals(bpmTempletId)) {
            info.put("ShowGfff", "1");
            getFlowFreeInfo(subscribeData, info);
        }
        if(bpmTempletId.equals("ETAPMARKETINGENTERING")||bpmTempletId.equals("ETAPMARKETINGEXCITATION")||bpmTempletId.equals("EDIRECTLINEDATACHANGE")){
        	IDataset pattrs=new DatasetList();
            IData info1 = new DataMap();
        	getEopAttrToList(ibsysid,pattrs,info1);
        	setPattrs(pattrs);
        	info1.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        	if(info.getString("URGENCY_LEVEL")!=null){
        		info1.put("URGENCY_LEVEL", info.getString("URGENCY_LEVEL"));
        	}
        	info.putAll(info1);
        }
    	if(("EOMSSplitflowOpen".equals(bpmTempletId) ||"EOMSSplitflowChange".equals(bpmTempletId)
    			||"eomsUnhangProess".equals(bpmTempletId)	
    			)&& "applyConfirm".equals(nodeId)){ //如果是专线挂起申请客户经理审批节点
        	info.put("ShowHang", "1");
        	getHangInfo(param, info);
        }
        if(bpmTempletId.equals("EDIRECTLINECHANGEFEE")){
        	info.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        	IDataset pattrs=new DatasetList();
        	pattrs = getdataLineList(ibsysid);
        	setPattrs(pattrs);
        }

        if("ACCEPTANCEPERIOD".equals(condbpmTempletId) || "EDIRECTLINEOPENPBOSS".equals(condbpmTempletId)) {
            // 取计费方式
            IData inparam = new DataMap();
            inparam.put("IBSYSID", ibsysid);
            inparam.put("NODE_ID", "apply");
            //inparam.put("ATTR_CODE", "ACCEPTTANCE_PERIOD");
            IDataset chargeModes = CSViewCall.call(this, "SS.WorkformOtherSVC.qryByIbsysidNodeId", inparam);
            if(IDataUtil.isNotEmpty(chargeModes)) {
                for(int i=0;i<chargeModes.size();i++){
                    IData chargeMode = chargeModes.getData(i);
                    if("ACCEPTTANCE_PERIOD".equals(chargeMode.getString("ATTR_CODE"))) {
                        info.put("ACCEPTTANCE_PERIOD", StaticUtil.getStaticValue("ACCEPTANCEDATE", chargeMode.getString("ATTR_VALUE")));
                        break;
                    }
                }
                
            }
        }

        //加方法是请做一下判断，不然有的流程可能报错
        if("1".equals(info.getString("ShowContract"))) {
            // 取合同信息
            IData contractParam = new DataMap();
            contractParam.put("IBSYSID", ibsysid);
            IDataset contractInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoByIbsysidAttrtype", contractParam);
            if (IDataUtil.isNotEmpty(contractInfo)) {
                for (int i = 0; i < contractInfo.size(); i++) {
                    String key = contractInfo.getData(i).getString("ATTR_CODE");
                    String value = contractInfo.getData(i).getString("ATTR_VALUE");
                    info.put(key, value);
                }
            }
            //判断是否新用户
            IData proInput = new DataMap();
            proInput.put("IBSYSID", ibsysid);
            IDataset productInfos = CSViewCall.call(this, "SS.WorkformProductSVC.qryProductByIbsysid", proInput);
            if(IDataUtil.isNotEmpty(productInfos)) {
                IData productInfo = productInfos.getData(0);
                String serialNumber = productInfo.getString("SERIAL_NUMBER");
                if(serialNumber == null) {
                    info.put("IS_NEW_GRP_USER", "true");
                } else {
                    info.put("IS_NEW_GRP_USER", "false");
                }
            }
        }
        info.put("IBSYSID", subscribeData.getString("IBSYSID"));

        setInfo(info);
        getStaffInfo();
        // 查询附件
        IData input = new DataMap();
        input.put("IBSYSID", subscribeData.getString("IBSYSID"));
        IDataset filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryContractAttach", input);
        setAttachInfos(filesets);
    }

    private void queryCommonAttr(IData subscribeData, IData info) throws Exception {
        IData input = new DataMap();
        input.put("IBSYSID", subscribeData.getString("IBSYSID"));
        input.put("NODE_ID", "apply");
        input.put("RECORD_NUM", "0");
        IDataset attrDatas = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", input);
        if(IDataUtil.isEmpty(attrDatas)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "本工单[" + subscribeData.getString("IBSYSID") + "]资料表TF_B_EOP_ATTR没有数据！");
        }

        for (int i = 0; i < attrDatas.size(); i++) {
            IData attrData = attrDatas.getData(i);
            info.put(attrData.getString("ATTR_CODE"), attrData.getString("ATTR_VALUE"));
        }
        
        info.put("TITLE", subscribeData.getString("RSRV_STR4"));
        info.put("URGENCY_LEVEL", subscribeData.getString("RSRV_STR3"));

        String busiCode = subscribeData.getString("RSRV_STR2");
        if(StringUtils.isBlank(busiCode)){
            busiCode = subscribeData.getString("BPM_TEMPLET_ID");
        }
        info.put("TEMPLET_ID", busiCode);
        IData inparam = new DataMap();
        inparam.put("BUSI_CODE", busiCode);
        IDataset templetIDList = CSViewCall.call(this, "SS.BusiFlowReleSVC.getOperTypeByTempletId", inparam);
        if(IDataUtil.isNotEmpty(templetIDList)) {
            info.put("BUSI_NAME", templetIDList.first().getString("BUSI_NAME"));
        }

    }

    private IDataset getdataLineList(String ibsysid) throws Exception{
    	IData param  =  new DataMap();
    	param.put("IBSYSID", ibsysid);
    	IDataset productSubs = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
    	IDataset infos=  new DatasetList();
    	for(int i = 0;i < productSubs.size();i++){
    		IData productSub = productSubs.getData(i);
    		String reCordeNum = productSub.getString("RECORD_NUM");
    		param.put("RECORD_NUM", reCordeNum);
    		param.put("NODE_ID", "apply");
    		IDataset atts = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", param);
    		IData attr = new DataMap();
    		for (int j = 0; j < atts.size(); j++) {
	             String key = atts.getData(j).getString("ATTR_CODE");
	             String value = atts.getData(j).getString("ATTR_VALUE");
	             attr.put(key, value);
	        }
			infos.add(attr);
    	}
    	return infos;
    }

    private void getStaffInfo() throws Exception {
        String staff = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staff);
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        setAuditInfo(staffInfo);
    }

    public void queryInfosByIbsysid(String ibsysid) throws Exception {
        //IData param = getData();
        //String ibsysid = param.getString("IBSYSID");
        IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        String groupId = "";
        if (IDataUtil.isNotEmpty(workformData)) {
            groupId = workformData.getString("GROUP_ID");
        } else {
            this.setAjax("error_message", "根据工单号未查到对应工单, 请核实");
            return;
        }
        if (StringUtils.isNotBlank(groupId)) {
            IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            group.put("IBSYSID", ibsysid);
            setGroupInfo(group);

            String custMgrId = group.getString("CUST_MANAGER_ID");
            if(StringUtils.isNotEmpty(custMgrId)) {
                IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
                setCustMgrInfo(managerInfo);
            }
        }
        String busiCode = workformData.getString("BUSI_CODE");
        String bpmTempletId = workformData.getString("BPM_TEMPLET_ID");
        // 查询产品属性
        if ("ADDCREDITREDLIST".equals(bpmTempletId)) {
            getPattrCredit(ibsysid);
        } else if("ACCEPTANCEPERIODCHGAUDIT".equals(bpmTempletId)) {
            getUserLineDataAndBiling(ibsysid, workformData);
        }else{
            getProductInfos(busiCode, ibsysid);
            
        }
        // 查询验收期

    }

    private void getUserLineDataAndBiling(String ibsysid, IData workformData) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        IDataset productSubList = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
        IDataset pattrs = new DatasetList();
        if(IDataUtil.isNotEmpty(productSubList)) {
            for(int i=0;i<productSubList.size();i++){
                IData productSub = productSubList.getData(i);
                String userId = productSub.getString("USER_ID");
                IData input = new DataMap();
                input.put("USER_ID", userId);
                IDataset userDataLines = CSViewCall.call(this, "SS.QcsGrpIntfSVC.getProductInfoForPboss", input);
                if(IDataUtil.isNotEmpty(userDataLines)) {
                    IData userDataLine = userDataLines.first();
                    IData pattr = new DataMap();
                    IDataset esopParam = pageutil.getList("TD_B_EWE_CONFIG", "PARAMVALUE", "PARAMNAME", "CONFIGNAME", "LINEPARAM_CRM_ESOP");
                    Iterator<String> itr = userDataLine.keySet().iterator();
                    while (itr.hasNext()) {
                        String attrCode = itr.next();
                        for (int j = 0; j < esopParam.size(); j++) {
                            String paramValue = esopParam.getData(j).getString("PARAMVALUE");
                            if(attrCode.equals(paramValue)) {
                                pattr.put(esopParam.getData(j).getString("PARAMNAME"), userDataLine.getString(attrCode));
                                break;
                            }
                        }
                    }
                    pattr.put("PRODUCT_ID", productSub.getString("PRODUCT_ID"));
                    pattr.put("PRODUCT_NAME", productSub.getString("PRODUCT_NAME"));
                    pattr.put("IBSYSID", ibsysid);
                    pattrs.add(pattr);
                }
            }
            setPattrs(pattrs);

            IData bilingInfo = new DataMap();
            bilingInfo.put("ACCEPT_DATE", workformData.getString("ACCEPT_TIME"));
            String startDate = "";
            String endDate = "";
            String changEndDate = "";
            String mebUserId = productSubList.first().getString("USER_ID");
            IData inparam = new DataMap();
            inparam.put("USER_ID", mebUserId);
            inparam.put("DISCNT_CODE", EcConstants.ZERO_DISCNT_CODE);
            //inparam.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
            IDataset userDiscntDatas = CSViewCall.call(this, "SS.UserDiscntInfoQrySVC.queryUserAllDisnctByCode", inparam);
            if(IDataUtil.isNotEmpty(userDiscntDatas)) {
                IData userDiscntData = userDiscntDatas.first();
                endDate = userDiscntData.getString("END_DATE");
                startDate = userDiscntData.getString("START_DATE");
                bilingInfo.put("END_DATE", endDate);
                String accpValue = "";
                IData attrParam = new DataMap();
                attrParam.put("IBSYSID", ibsysid);
                attrParam.put("NODE_ID", "apply");
                IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewInfoByIbsysidAndNodeId", attrParam);
                if(IDataUtil.isNotEmpty(attrs)) {
                    for (int i = 0; i < attrs.size(); i++) {
                        IData attr = attrs.getData(i);
                        String code = attr.getString("ATTR_CODE");
                        String value = attr.getString("ATTR_VALUE");
                        if("NEW_ACCEPTTANCE_PERIOD".equals(code)) {
                            accpValue = value;
                            bilingInfo.put("NEW_ACCEPTTANCE_PERIOD", pageutil.getStaticValue("EOP_BILLING_DELAY", value));
                            break;
                        }
                    }

                }
                if("0".equals(accpValue)) {
                    changEndDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
                } else if("1".equals(accpValue)) {
                    changEndDate = SysDateMgr.suffixDate(SysDateMgr.addMonths(endDate, Integer.valueOf(accpValue)), 0);
                }
                bilingInfo.put("CHANG_END_DATE", changEndDate);
            }
            setBilingInfo(bilingInfo);

        }
    }

    private void getPattrCredit(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        IDataset allSn = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrByIbsysid", param);
        if (DataUtils.isNotEmpty(allSn)) {
            IDataset productAttrs = new DatasetList();
            for (int i = 0; i < allSn.size(); i++) {
                IData data = allSn.getData(i);
                String attrCode = data.getString("ATTR_CODE");
                if ("SERIAL_NUMBER".equals(attrCode)) {
                    IData inparam = new DataMap();
                    inparam.put("SERIAL_NUMBER", data.getString("ATTR_VALUE"));
                    IDataset lineInfos = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.qryLineInfoByUserId", inparam);
                    if (DataUtils.isNotEmpty(lineInfos)) {
                        IData lineInfo = lineInfos.first();
                        lineInfo.put("IBSYSID", ibsysid);
                        lineInfo.put("PRODUCTNO", lineInfo.getString("PRODUCT_NO"));
                        lineInfo.put("BIZSECURITYLV", lineInfo.getString("BIZ_SECURITY_LV"));
                        lineInfo.put("TRADENAME", lineInfo.getString("RSRV_STR5"));
                        productAttrs.add(lineInfo);
                    }
                }
            }
            setPattrs(productAttrs);
        }
    }

    private void getProductInfos(String busiCode, String ibsysid) throws Exception {
        IData input = new DataMap();
        IDataset productAttrs = new DatasetList();
        input.put("IBSYSID", ibsysid);
        input.put("NODE_ID", "apply");
        IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.getNewLineInfoList", input);
        if(attrs != null && attrs.size() > 0) {
            for (Object obj : attrs) {
                IData data = (IData) obj;
                IData productAttr = new DataMap();
                Iterator<String> it = data.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = data.getString(key);
                    if(key.startsWith("pattr_")) {
                        key = key.substring(6);
                    }
                    productAttr.put(key, value);
                }
                productAttr.put("PRODUCT_ID", busiCode);
                productAttr.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", busiCode));
                productAttrs.add(productAttr);
            }
        }
        setPattrs(productAttrs);
    }

    public void qryByIbsysidProductNo(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String productNo = param.getString("PRODUCTNO");
        String nodeId = param.getString("NODE_ID");
        String bpmTempletId = param.getString("BPM_TEMPLET_ID");
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("ATTR_VALUE", productNo);
        data.put("NODE_ID", nodeId);
        data.put("ATTR_CODE", "PRODUCTNO");

        IData productInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryByIbsysidProductNoNodeId", data);
        productInfo.put("PRODUCT_ID", param.getString("PRODUCT_ID"));
        productInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", param.getString("PRODUCT_ID")));
        productInfo.put("BPM_TEMPLET_ID", bpmTempletId);
        //转换路由保护方式
        productInfo.put("ROUTEMODE", StaticUtil.getStaticValue("ROUTEMODE", productInfo.getString("ROUTEMODE")));
        setProductInfo(productInfo);
    }

    //取流量自由充产品数据
    private void getFlowFreeInfo(IData subscribeData, IData info) throws Exception {
        String ibsysid = subscribeData.getString("IBSYSID");
        String bpmTempletId = subscribeData.getString("BPM_TEMPLET_ID");
        IData productData = WorkfromViewCall.qryEopProductByIbsysId(this, ibsysid, "0");
        String tradeId = productData.getString("TRADE_ID");
        String userId = productData.getString("USER_ID", "");
        String getType = "";
        if("DATAFREEDOMRECHARGECANCEL".equals(bpmTempletId)) {
            getType = "2";
        } else if("DATAFREEDOMRECHARGECHANGE".equals(bpmTempletId)) {
            getType = "1";
        } else if("DATAFREEDOMRECHARGEOPEN".equals(bpmTempletId)) {
            getType = "0";
        }
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("GET_TYPE", getType);
        param.put("USER_ID", userId);
        IDataset productInfos = CSViewCall.call(this, "SS.GrpCenpayGfffEsopSvc.getGrpGfffInfo", param);
        if(DataUtils.isNotEmpty(productInfos)) {
            info.putAll(productInfos.first());
        }
    }
    //getHangInfo
    private void getHangInfo(IData eweInfo, IData info) throws Exception {
    	IData param = new DataMap();
    	param.put("BUSIFORM_NODE_ID", eweInfo.getString("BUSIFORM_NODE_ID",""));
    	IData eweNodeInfo = new DataMap();
    	eweNodeInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryByBusiformNodeId", param);
    	if(IDataUtil.isEmpty(eweNodeInfo)){
    		eweNodeInfo = CSViewCall.callone(this, "SS.EweNodeTraSVC.qryEweNodeTraByBusiformNodeId", param);
    	}
    	param.put("BUSIFORM_NODE_ID", eweNodeInfo.getString("PRE_BUSIFORM_NODE_ID")); 
    	IDataset eweAsynInfos = CSViewCall.call(this, "SS.EweAsynSVC.qryInfosByBusiformNodeId", param);
    	IData work = new DataMap();
    	if(IDataUtil.isNotEmpty(eweAsynInfos)){
    		for(int i = 0;i < eweAsynInfos.size();i++){
    			IData eweAsynInfo = eweAsynInfos.getData(i);
    			work.put(eweAsynInfo.getString("ATTR_CODE"), eweAsynInfo.getString("ATTR_VALUE"));
    		}
    	}
    	
    	info.putAll(work);
    }

    @Override
    public void submit(IRequestCycle cycle) throws Exception {
        IData submitData = new DataMap(getData().getString("SUBMIT_PARAM"));
        IData commonData = submitData.getData("COMMON_DATA");
        if("ACCEPTANCEPERIODCHGAUDIT".equals(commonData.getString("BPM_TEMPLET_ID", ""))) {
            String endData = submitData.getString("END_DATE");
            String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
            int compareToResult = SysDateMgr.compareTo(endData, sysDate);
            if(compareToResult <= 0) {
                //1.审核过期先删除当前代办
                IData inparam = new DataMap();
                inparam.put("INFO_TYPE", EcEsopConstants.TASK_TYPE_CODE_WORKINFO);
                inparam.put("INFO_SIGN", commonData.getString("BUSIFORM_NODE_ID"));
                inparam.put("INFO_STATUS", "9");
                CSViewCall.call(this, "SS.WorkTaskMgrSVC.updWorkTaskInfo", inparam);

                //2.插入节点AUTO_TIME,推动流程
                IData atuoparam = new DataMap();
                atuoparam.put("BUSIFORM_NODE_ID", commonData.getString("BUSIFORM_NODE_ID"));
                atuoparam.put("AUTO_TIME", sysDate);
                CSViewCall.call(this, "SS.EweNodeQrySVC.updEweNodeAutoTimeByPk", inparam);

                IData result = new DataMap();
                result.put("INVALID_TAG", "true");
                setAjax(result);
            } else {
                super.submit(cycle);
            }
        } else {
            super.submit(cycle);
        }
    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        super.buildOtherSvcParam(param);
        buildAuditSvcParam(param);
        IData commonData = param.getData("COMMON_DATA");
        String nodeId = commonData.getString("NODE_ID");
    }

    private void buildAuditSvcParam(IData param) throws Exception {
    	IData commonData = param.getData("COMMON_DATA");
    	String bpmtemtId = commonData.getString("BPM_TEMPLET_ID","");
    	String nodeId = commonData.getString("NODE_ID");
    	if(("EOMSSplitflowOpen".equals(bpmtemtId) ||"EOMSSplitflowChange".equals(bpmtemtId)||"eomsUnhangProess".equals(bpmtemtId))&& "applyConfirm".equals(nodeId)){ //如果是专线挂起申请客户经理审批节点
    		IDataset attrInput =  new DatasetList();
    		IData inputAttr = new DataMap();

        	String biaRange="";
    	    String cityA="";
    	    String cityZ="";
    	    String buildingsection="";
    		
    		IData params = new DataMap();
    		String busiformNodeId = commonData.getString("BUSIFORM_NODE_ID");
    		String busiformId = commonData.getString("BUSIFORM_ID");
    		params.put("BUSIFORM_ID", busiformId);
    		IData preInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryBySubBusiformId", params);
    		IData preInfoOld=new DataMap(preInfo.toString());
    		if("eomsUnhangProess".equals(bpmtemtId)){//子子流程获取需要两次
        		params.put("BUSIFORM_ID", preInfo.getString("BUSIFORM_ID"));
    			preInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryBySubBusiformId", params);
    		}
    		params.put("IBSYSID", commonData.getString("IBSYSID"));
    		params.put("RECORD_NUM", preInfo.getString("RELE_VALUE"));
    		IData emostate = CSViewCall.callone(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndRecordNum", params);
//    		String productNo = emostate.getString("PRODUCT_NO");
    		/*params.put("BUSIFORM_NODE_ID", busiformNodeId);
    	    IData preInfo = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweByBusiFormNodeId", params);
    	    String productNo = "" ;
    	    if(IDataUtil.isNotEmpty(preInfo)){
    	    	String subIbsysId = preInfo.getString("SUB_BI_SN");
    	    	IData attrNode = new DataMap();
    	    	attrNode.put("SUB_IBSYSID", subIbsysId);
    	    	attrNode.put("GROUP_SEQ", "0");
    	    	IDataset attrs = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrBySubIbsysidAndGroupseq", attrNode);
    	    	for(int i = 0;i<attrs.size();i++){
    	    		IData attr = attrs.getData(i);
    	    		if("serialNo".equals(attr.getString("ATTR_CODE"))){
    	    			attrInput.add(attr);
    	    		}
    	    		if("ProductNo".equals(attr.getString("ATTR_CODE"))){
    	    			productNo = attr.getString("ATTR_VALUE");
    	    			attrInput.add(attr);
    	    		}
    	    	}
    	    }*/
    	  /*  IData input =  new DataMap();
    	    input.put("IBSYSID", commonData.getString("IBSYSID"));
    	    input.put("PRODUCT_NO", productNo);
    	    IData emos = CSViewCall.callone(this,"SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo",input);
    		if(IDataUtil.isEmpty(emos)){
    			CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据ESOP工单编号"+commonData.getString("IBSYSID")+"查询无资料，不能办理该业务！");
    		}*/
    		String recordNum = preInfo.getString("RELE_VALUE");
    		
    		IData inputAttrIn = new DataMap();
		    inputAttrIn.put("IBSYSID", commonData.getString("IBSYSID"));
		    inputAttrIn.put("NODE_ID", "eomsProess");
		    IDataset eomsDat = CSViewCall.call(this,"SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", inputAttrIn);
			if(eomsDat != null && eomsDat.size() > 0){
				for(int j= 0;j<eomsDat.size();j++){
					IData emossub = eomsDat.getData(j);
					if("BIZRANGE".equals(emossub.getString("ATTR_CODE",""))){
						biaRange=emossub.getString("ATTR_VALUE","");//业务范围
					}
					if(recordNum.equals(emossub.getString("RECORD_NUM",""))&&"CITYA".equals(emossub.getString("ATTR_CODE",""))){
						cityA=emossub.getString("ATTR_VALUE","");//业务范围
					}
					if(recordNum.equals(emossub.getString("RECORD_NUM",""))&&"CITYZ".equals(emossub.getString("ATTR_CODE",""))){
						cityZ=emossub.getString("ATTR_VALUE","");//业务范围
					}
					if("BUILDINGSECTION".equals(emossub.getString("ATTR_CODE",""))){
						buildingsection=emossub.getString("ATTR_VALUE","");//
					}
				}
			}
    	    inputAttr.put("taskName", "");
			if("省内跨地市".equals(biaRange)){
	    	    inputAttr.put("cityName", cityA);
			}else{
	    	    inputAttr.put("cityName", buildingsection);
			}
			if("eomsUnhangProess".equals(bpmtemtId)){//子子流程获取需要两次
    			if("1".equals(preInfoOld.getString("RELE_VALUE"))){
    	    	    inputAttr.put("cityName", cityA);
    			}else if("2".equals(preInfoOld.getString("RELE_VALUE"))){
    	    	    inputAttr.put("cityName", cityZ);
    			}else{
    	    	    inputAttr.put("cityName", cityA);
    			}
    		}
    	    String staffId = "";
    		IDataset otherList =  param.getDataset("OTHER_LIST");
    	    for(int i = 0;i<otherList.size();i++){
	    		IData attr = otherList.getData(i);
	    		if("AUDIT_STAFF_ID".equals(attr.getString("ATTR_CODE"))){
	    			staffId = attr.getString("ATTR_VALUE");
	    			inputAttr.put("agreePerson", staffId);
	    		}
	    		if("AUDIT_RESULT".equals(attr.getString("ATTR_CODE"))){
	    			//1、查询地州
	    			String agree = attr.getString("ATTR_VALUE");
	    			String agreeResult =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
	    			    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
	    			    	{ "AUDIT_TEXT",agree}); //查询审核意见中文
	    			inputAttr.put("agreeResult", agreeResult);
	    		}
	    		if("AUDIT_TEXT".equals(attr.getString("ATTR_CODE"))){
	    			inputAttr.put("agreeContent", attr.getString("ATTR_VALUE"));
	    		}
	    	}
    	    IData staff =  new DataMap();
    	    staff.put("STAFF_ID", staffId);
    	    IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", staff);
    	    inputAttr.put("agreePersonContactPhone", staffInfo.getString("SERIAL_NUMBER",""));
    	    inputAttr.put("serialNo", emostate.getString("SERIALNO",""));
    	    inputAttr.put("ProductNo", emostate.getString("PRODUCT_NO",""));
    	    Iterator<String> itr = inputAttr.keySet().iterator();
    		while(itr.hasNext())
    		{
    			String key = itr.next();
    			String value = inputAttr.getString(key);
    			IData dAttr = new DataMap();
    			dAttr.put("ATTR_CODE", key);
    			dAttr.put("ATTR_VALUE", value);
    			String name =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
    			    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
    			    	{ "APPLY_HANG_CODE",key}); //查询对应名字
    			dAttr.put("ATTR_NAME", name);
    			dAttr.put("RECORD_NUM", recordNum);
    			attrInput.add(dAttr);
    		}
    		param.put("CUSTOM_ATTR_LIST", attrInput);
    	}
    }

}
