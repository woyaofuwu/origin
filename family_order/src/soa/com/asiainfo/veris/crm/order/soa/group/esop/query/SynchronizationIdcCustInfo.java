package com.asiainfo.veris.crm.order.soa.group.esop.query;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SynchronizationIdcCustInfo  extends CSBizService{
private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SynchronizationIdcCustInfo.class);

	public IDataset synchronizationCustInfo(IData param) throws Exception
	{
		IDataset returnList=new DatasetList();
		String ibsysid = IDataUtil.chkParam(param, "IBSYSID");	
		IDataset subscribeData = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		if (DataUtils.isEmpty(subscribeData)) {
			return returnList;
		}
		IData inparam=new DataMap();
		inparam.put("IBSYSID", ibsysid);
		inparam.put("NODE_ID", "apply");
//		IDataset custInfos=new DatasetList();
		IData custInfo = new DataMap();
		IDataset attrList=WorkformAttrBean.getNewInfoByIbsysidAndNodeId(inparam);
		boolean sendIdcFlag=false;
		if (DataUtils.isNotEmpty(attrList)) {
			for (int i = 0; i < attrList.size(); i++) {
				IData attrData=attrList.getData(i);
				if(DataUtils.isNotEmpty(attrData)){
					int index=attrData.getString("ATTR_CODE","").indexOf("CUST_");
					if(index>=0){
						if("CUST_MODIFYTAG".equals(attrData.getString("ATTR_CODE",""))&&"2".equals(attrData.getString("ATTR_VALUE",""))){
							sendIdcFlag=true;
						}else{
							custInfo.put(attrData.getString("ATTR_CODE","").substring(index+5) ,attrData.getString("ATTR_VALUE",""));
						}
						
					}
				}
			}
		}
		if(sendIdcFlag){
			String groupID = subscribeData.first().getString("GROUP_ID","");
			IData grpQryData = new DataMap();
	        grpQryData.put("GROUP_ID", groupID);
	        IDataset groupInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", grpQryData);
	        if (DataUtils.isEmpty(groupInfos)&&DataUtils.isEmpty(groupInfos.first()))
	        {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过GROUP_ID:"+groupID+"找不到集团资料！");
	        }
	        IData groupInfo=groupInfos.first();
			IData custInfoIn = new DataMap();
			custInfoIn.put("CUST_ID",groupInfo.getString("CUST_ID"));
			custInfoIn.put("GROUP_ID", groupID);
			
			custInfoIn.put("CUSTTYPE", custInfo.getString("unitNature", ""));//单位性质
			custInfoIn.put("ORGCLASS", custInfo.getString("unitType", ""));//单位所属分类
			custInfoIn.put("ORGADMINLEVEL", custInfo.getString("unitLevel", ""));//单位行政级别
			custInfoIn.put("CONT_NAME", custInfo.getString("contactsName", ""));//联系人姓名(客户侧)
			custInfoIn.put("FIXEDPHONE", custInfo.getString("contactsNumber", ""));//联系人固定电话（客户侧）
			custInfoIn.put("PHONE", custInfo.getString("contactsPhoneNumber", ""));//联系人移动电话（客户侧）
			custInfoIn.put("EMAIL", custInfo.getString("contactsEmail", ""));//联系人Email地址（客户侧）
			custInfoIn.put("BUSNUMBER", custInfo.getString("manageLicenceNum", ""));//经营许可证编号
			custInfoIn.put("ICTYPE", custInfo.getString("PsptType", ""));//联系人证件类型（客户侧）
			custInfoIn.put("ICNUMBER", custInfo.getString("PsptNum", ""));//联系人证件号码（客户侧）
			


	        IDataset returnInfos = CSAppCall.call("CC.group.IGroupOperateSV.synIDCInfo", custInfoIn);
	        System.out.println("CC.group.IGroupOperateSV.synIDCInfo returnInfos:"+returnInfos);
	        if (DataUtils.isEmpty(returnInfos)&&DataUtils.isEmpty(returnInfos.first()))
	        {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用接口 CC.group.IGroupOperateSV.synIDCInfo 返回信息为空!");
	        }
	        IData returnInfo=returnInfos.first();
	        if(DataUtils.isEmpty(returnInfo.getDataset("DATAS"))
	        		||DataUtils.isEmpty(returnInfo.getDataset("DATAS").first())
	        		||!"true".equals(returnInfo.getDataset("DATAS").first().getString("SUCCESS"))){
	        	logger.info("调用接口 CC.group.IGroupOperateSV.synIDCInfo 返回异常!返回内容:returnInfos:"+returnInfos);
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用接口 CC.group.IGroupOperateSV.synIDCInfo 返回异常!");
	        }
	        else{
	        	returnList.add(returnInfo.getDataset("DATAS").first());
	        }
	        
		}
		return returnList;
	}


}
