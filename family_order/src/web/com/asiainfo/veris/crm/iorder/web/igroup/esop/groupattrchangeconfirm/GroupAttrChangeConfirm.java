package com.asiainfo.veris.crm.iorder.web.igroup.esop.groupattrchangeconfirm;


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GroupAttrChangeConfirm extends EopBasePage {

	

	public abstract void setGroupInfo(IData groupInfo) throws Exception;

	public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;
	
	public abstract void setCheckGroupInfo(IData groupInfo) throws Exception;

	public abstract void setCheckCustInfo(IData custMgrInfo) throws Exception;
	
	public abstract void setCustInfo(IData custInfo) throws Exception;
	
	public abstract void setFileInfo(IData files) throws Exception;

	public abstract void setFileInfos(IDataset files) throws Exception;

	public abstract void setInfo(IData info) throws Exception;
	
	public abstract void setEosInfo(IData info) throws Exception;

	public abstract void setOffer(IData offer) throws Exception;
	
	public abstract void setOffers(IDataset offers) throws Exception;

	public abstract void setInfos(IDataset dataset);
	
	IDataset attrList = new DatasetList();
    
	IData groupAInfo = new DataMap();
	
	IData groupBInfo = new DataMap();
	
	public void initPage(IRequestCycle cycle) throws Exception {
		IData data = getData();
		
		IData eosInfo = new DataMap();
		
		IData ibsys = new DataMap();
		// ibsys.put("IBSYSID", data.getString("IBSYSID"));
		ibsys.put("IBSYSID", data.getString("IBSYSID"));// 测试
		ibsys.put("NODE_ID", "apply");
		IDataset result = CSViewCall.call(this,
				"SS.WorkformAttrSVC.qryMaxEopAttrByIbsysidNodeid", ibsys);
		IDataset offers = new DatasetList();

		IDataset fileInfos = new DatasetList();
		IData fileInfo = new DataMap();

		if (DataUtils.isNotEmpty(result)) {


				for (int i = 0; i < result.size(); i++) {
					IData res = result.getData(i);
					if("rowData".equals(res.getString("ATTR_CODE")))
					{
						IDataset dataset = new DatasetList(res.getString("ATTR_VALUE"));
						offers.addAll(dataset);
					}
                    
					if ("GRP_SERIAL_NUMBER_B".equals(res.getString("ATTR_CODE"))) {
						
						queryCustGroupByGroupId(res.getString("ATTR_VALUE"));
					}
					if ("ATTR_FILE".equals(res.getString("ATTR_CODE"))) {
						String file = res.getString("ATTR_VALUE");
						String[] files = file.split("\\|");
						fileInfo.put("FILE_NAME", files[0]);
						fileInfo.put("FILE_ID", files[1]);
						fileInfos.add(fileInfo);

					}
					if ("BUSI_FILE".equals(res.getString("ATTR_CODE"))) {
						IDataset datas = new DatasetList(
								res.getString("ATTR_VALUE"));
						fileInfos.addAll(datas);
					}

				}
				
			}
			this.setFileInfos(fileInfos);

			IDataset resu = CSViewCall.call(this,
					"SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid",
					ibsys);
			String groupIdA = resu.getData(0).getString("GROUP_ID");// 测试写死后面会从流程获取
			queryGroupByGroupId(groupIdA);

			IData commData = new DataMap();
			 commData.put("IBSYSID", data.getString("IBSYSID"));
			 commData.put("NODE_ID", data.getString("NODE_ID"));
			 commData.put("BUSIFORM_NODE_ID", data.getString("BUSIFORM_NODE_ID"));
			 commData.put("BPM_TEMPLET_ID", data.getString("BPM_TEMPLET_ID"));
			 commData.put("IN_MODE_CODE", "0");
			 commData.put("DEAL_STATE", "0");
			 commData.put("BUSI_CODE", data.getString("BUSI_CODE"));
			 commData.put("BUSI_TYPE", data.getString("BUSI_TYPE"));
			 commData.put("GROUP_ID", data.getString("GROUP_ID"));
			 commData.put("CUST_NAME", resu.first().getString("CUST_NAME"));
			 
			 eosInfo.put("EOS_COMMON_DATA", commData);
             //eosInfo.put("NODE_ID", data.getString("NODE_ID"));
             setEosInfo(eosInfo);
			 
			this.setOffers(offers);

	    }
		
	
	
	
	
	
	public void submitChange(IRequestCycle cycle) throws Exception {
		IData data = getData();

		IData submitData = new DataMap();
		
		IData submitCommData = new DataMap(data.getString("COMMON_DATA"));
		
		IData nodeInfo = new DataMap();
		nodeInfo.put("NODE_ID", submitCommData.getString("NODE_ID",""));
		// 受理集团信息
		//IData custData = groupAInfo.getData("GROUPA");
		 IData busiSpecRele = new DataMap();

		 

//		commData.put("IBSYSID", "201901236041");
//		commData.put("NODE_ID", "approveI");
//		commData.put("BUSIFORM_NODE_ID", "2019012200589052");
//		commData.put("BPM_TEMPLET_ID", "GROUPATTRCHANGE");
//		commData.put("IN_MODE_CODE", "0");
//		commData.put("DEAL_STATE", "0");
//		commData.put("BUSI_CODE", "1221");
//		commData.put("BUSI_TYPE", "P");
//		commData.put("GROUP_ID", custData.getString("GROUP_ID"));

		 busiSpecRele.put("BPM_TEMPLET_ID", submitCommData.getString("BPM_TEMPLET_ID",""));
		 busiSpecRele.put("BUSI_TYPE", "P");
		 busiSpecRele.put("BUSI_CODE", submitCommData.getString("BUSI_CODE",""));
		 submitData.put("NODE_TEMPLETE", nodeInfo);
		 submitData.put("BUSI_SPEC_RELE", busiSpecRele);
		submitData.put("COMMON_DATA", submitCommData);
		//submitData.put("CUST_DATA", custData);
		submitData.put("EOMS_ATTR_LIST", sendRowData(data));

		IData otherInfo = new DataMap();
		otherInfo.put("ATTR_CODE", "EOS_CHECK_GRP_ATTRS");
		otherInfo.put("ATTR_VALUE", data.getString("EOS_CHECK_GRP_ATTRS"));
		otherInfo.put("ATTR_NAME", "审核结果");
		otherInfo.put("RECORD_NUM", "0");
		IDataset otherList = new DatasetList();
		otherList.add(otherInfo);
		submitData.put("OTHER_LIST", otherList);

		IData submitParam = ScrDataTrans.buildWorkformSvcParam(submitData);
		IDataset result = CSViewCall.call(this,
				"SS.WorkformRegisterSVC.register", submitParam);
		this.setAjax(result.first());
	}
	private IDataset sendRowData(IData rowData)throws Exception
	{

		 IData checkAttr = new DataMap();
		 

		 checkAttr.put("ATTR_CODE", "CONFIRM_GRP_ATTRS");
		 checkAttr.put("ATTR_VALUE",rowData.getString("CONFIRM_GRP_ATTRS"));
		 checkAttr.put("ATTR_NAME", "是否过户完毕");
		 checkAttr.put("RECORD_NUM", "0");

		 checkAttr.put("ATTR_CODE", "BUSI_CONFIRM_FILE");
		 checkAttr.put("ATTR_VALUE",rowData.getString("BUSI_CONFIRM_FILE"));
		 checkAttr.put("ATTR_NAME", "客户验收单扫描件");
		 checkAttr.put("RECORD_NUM", "0");

		 
		 attrList.add(checkAttr);	
		
		 
		
		
		return attrList;
	}
	
	
	
	/**
	 * 查询集团客户信息
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryGroupByGroupId(String grouIdA) throws Exception {
		//查询集团客户信息
		
		IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, grouIdA);
		groupAInfo.put("GROUPA", groupInfo);
		this.setInfo(groupInfo);
		this.setAjax(groupInfo);
		if(IDataUtil.isNotEmpty(groupInfo)){
			//查询客户经理信息
			String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
			if (StringUtils.isNotEmpty(custMgrId)) {
				IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
				this.setCustInfo(managerInfo);
			
			}

		}
	}
	
	
	
	
	  /**
     * 查询集团客户信息
     * @param cycle
     * @throws Exception
     */
    public void queryCustGroupByGroupId(String groupIdB) throws Exception
    {
        
       
        IData group = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, groupIdB);
        //String custId = group.getString("CUST_ID");
        group = group.getData("GRP_CUST_INFO");
        setGroupInfo(group);
        
        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }


	

}
