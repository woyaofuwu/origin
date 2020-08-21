package com.asiainfo.veris.crm.iorder.web.igroup.esop.acceptanceperiodchange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class AcceptanceperiodApply extends EopBasePage{
	
	private IDataset productAttrs;
	
	public abstract void setInAttr(IData inAttr);
	
	public abstract void setPattrs(IDataset pattrs);
	
	public abstract void setInfo(IData info);
	
	public abstract void setOtherInfo(IData otherInfo);
	
	public abstract void setAccetanceperiodInfos(IDataset accetanceperiodInfos);
	
	public abstract void setProductInfo(IData productInfo);
	
	public void initPage(IRequestCycle cycle) throws Exception{
		super.initPage(cycle);
		IData param = getData();
		String ibsysid = param.getString("IBSYSID");
		/*IData inAttr = new DataMap();
		inAttr.put("FLOW_ID", "ADUIT_7011" + param.getString("PRODUCT_ID")); // POINT_ONE
		inAttr.put("NODE_ID", param.getString("DEAL_TYPE")"ADUIT"); // POINT_TWO
		setInAttr(inAttr);*/
    }
	
	public void queryInfosByIbsysid(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String ibsysid = param.getString("IBSYSID");
		IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
		String groupId = "";
		if (IDataUtil.isNotEmpty(workformData)) 
        {
			groupId = workformData.getString("GROUP_ID");
		}else{
			this.setAjax("RESULT", "0");
			this.setAjax("error_message", "根据工单号或专线实例号未查到对应工单, 请核实");
			return;
		}
		if(StringUtils.isNotBlank(groupId)){
			IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
			group.put("IBSYSID", ibsysid);
	        setGroupInfo(group);
		}
		String busiCode = workformData.getString("BUSI_CODE");
		//查询产品属性
		getProductInfos(busiCode,ibsysid);
		//查询验收期
		IDataset otherInfos = WorkfromViewCall.qryOtherInfoByIbsysidAndAttrCode(this, ibsysid,"ACCEPTTANCE_PERIOD");
		IData otherInfo = otherInfos.first();
		if(DataUtils.isNotEmpty(otherInfo)){
			String attrValue = otherInfo.getString("ATTR_VALUE");
			String acceptPrd = StaticUtil.getStaticValue("ACCEPTTANCE_PERIOD", attrValue);
			otherInfo.put("ACCEPTTANCE_PERIOD", acceptPrd);
			setOtherInfo(otherInfo);
		}
		//查询验收期变更历史信息
		qryAccetanceperiodInfo(ibsysid);
	}
	
	private void getProductInfos(String busiCode,String ibsysid) throws Exception{
		IData info = new DataMap();
		if(StringUtils.isNotBlank(busiCode)){
			info.put("PRODUCT_ID", busiCode);
			if("7010".equals(busiCode)){
				info.put("PRODUCT_NAME", "VOIP专线（专网专线）");
			}else if("7011".equals(busiCode)){
				info.put("PRODUCT_NAME", "互联网专线（专网专线）");
			}else if("7012".equals(busiCode)){
				info.put("PRODUCT_NAME", "数据专线（专网专线）");
			}
		}
		
		setInfo(info);
		
		
		productAttrs = WorkfromViewCall.qryDataLineInfoByIbsysid(this, ibsysid);
		if(productAttrs != null && productAttrs.size()>0){
			for (Object obj : productAttrs) {
				IData data = (IData)obj;
				data.putAll(info);
			}
		}
		setPattrs(productAttrs);
	}
	
	private void qryAccetanceperiodInfo(String ibsysid) throws Exception{
		IDataset accetanceperiodInfos = null;
		if(StringUtils.isNotBlank(ibsysid)){
			IData data = new DataMap();
			data.put("IBSYSID", ibsysid);
			accetanceperiodInfos = CSViewCall.call(this,"SS.WorkformModiTraceSVC.qryAccetanceperiodChangeByIbsysid",data);
		}
		if(DataUtils.isNotEmpty(accetanceperiodInfos)){
			for(int i=0; i<accetanceperiodInfos.size(); i++){
				IData accetanceperiodInfo = accetanceperiodInfos.getData(i);
				accetanceperiodInfo.put("OLD_ACCEPTTANCE_PERIOD", 
						StaticUtil.getStaticValue("ACCEPTTANCE_PERIOD", accetanceperiodInfo.getString("ATTR_MAIN_VALUE")));
				accetanceperiodInfo.put("NEW_ACCEPTTANCE_PERIOD", 
						StaticUtil.getStaticValue("ACCEPTTANCE_PERIOD", accetanceperiodInfo.getString("ATTR_VALUE")));
			}
			setAccetanceperiodInfos(accetanceperiodInfos);
		}
	}
	
	public void qryByIbsysidProductNo(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String ibsysid = param.getString("IBSYSID");
		String productNo = param.getString("PRODUCTNO");
		IData data = new DataMap();
		data.put("IBSYSID", ibsysid);
		data.put("PRODUCTNO", productNo);
		IData productInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryByIbsysidProductNo", data);
		setProductInfo(productInfo);
	}
	
}
