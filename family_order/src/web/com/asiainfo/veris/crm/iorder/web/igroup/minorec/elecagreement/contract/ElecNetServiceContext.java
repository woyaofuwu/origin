package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.contract;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.tapestry.IRequestCycle;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;

public abstract class ElecNetServiceContext extends ElecLineProtocolBase {

	protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		if("N".equals(pageData.getString("SUBMITTYPE"))){
			IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());
			IData managerInfo = protocolInfo.getData("CUST_MRG_INFO",new DataMap());

			String custName = pageData.getString("CUST_NAME");
			if(StringUtils.isNotBlank(custName)){
				custName = URLDecoder.decode(pageData.getString("CUST_NAME"), "utf-8");
			}

			String sysDate =SysDateMgr4Web.getSysDate("yyyy年MM月dd日");
			protocolInfo.put("A_NAME", groupInfo.getString("CUST_NAME",custName));
			protocolInfo.put("B_NAME", "中国移动通信集团海南有限公司");
			protocolInfo.put("A_SIGN_DATE", sysDate);
			protocolInfo.put("B_SIGN_DATE", sysDate);

			protocolInfo.put("PRODUCT_ID", pageData.getString("PRODUCT_ID","-1"));
			accessoryInfo.put("AGREEMENT_DEF_ID","ElecNetServiceContext");
			accessoryInfo.put("GROUP_ID",groupInfo.getString("GROUP_ID"));
			accessoryInfo.put("CUST_ID",groupInfo.getString("CUST_ID",pageData.getString("CUST_ID")));
			
			if(StringUtils.isBlank(protocolInfo.getString("AGREEMENT_ID"))){
			    //合同编码
				IData ret = CSViewCall.callone(this,"SS.AgreementInfoSVC.getAgreementId",new DataMap());
			    /*ServiceResponse response = BizServiceFactory.call("CC.groupcontract.IGroupContractQuerySV.queryNewContractId", new DataMap());
		        IData ret = response.getBody();*/

		        if (IDataUtil.isEmpty(ret))
		        {
		            CSViewException.apperr(CrmCommException.CRM_COMM_103, "合同编码获取失败，请检查合同编码是否用尽！");
		        }
		        
		        protocolInfo.put("AGREEMENT_ID", ret.getString("AGREEMENT_ID"));
			}
		}
    }

    protected IData buildFDFInfo(IData pageData) throws Exception{
        IData contextData = buildProtocolInfo(pageData);
        return contextData;
    }

	protected IData buildProtocolInfo(IData pageData){
		IData archivesInfo = new DataMap();
		IDataset archiveslist = new DatasetList();

		archivesInfo.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_NAME",pageData.getString("A_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_NAME_2",pageData.getString("A_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("A_SIGN_DATE",pageData.getString("A_SIGN_DATE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("B_SIGN_DATE",pageData.getString("B_SIGN_DATE",ElecLineUtil.DUFUALT_VALUE));

		ElecLineUtil.addMapKeyToList(archivesInfo, archiveslist);

		IData datas = new DataMap();
		datas.put("DATAS", archiveslist);
		return datas;
	}



	public IData saveProtocolInfo(IData pageData) throws Exception {
        // TODO Auto-generated method stub
        IData archives = new DataMap();

		SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sf2 = new SimpleDateFormat(SysDateMgr4Web.PATTERN_STAND_YYYYMMDD);
		pageData.put("A_SIGN_DATE",sf2.format(sf.parse(pageData.getString("A_SIGN_DATE"))));
		pageData.put("B_SIGN_DATE",sf2.format(sf.parse(pageData.getString("B_SIGN_DATE"))));

        ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveProtocolInfo(pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		IDataset products = new DatasetList();
		products.add(pageData.getString("PRODUCT_ID"));
		archives.put("PRODUCTS", products);

        return archives;
    }


	public IData updateProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();
		archives.put("ARCHIVES_ID", pageData.getString("ARCHIVES_ID"));

		SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sf2 = new SimpleDateFormat(SysDateMgr4Web.PATTERN_STAND_YYYYMMDD);
		pageData.put("A_SIGN_DATE",sf2.format(sf.parse(pageData.getString("A_SIGN_DATE"))));
		pageData.put("B_SIGN_DATE",sf2.format(sf.parse(pageData.getString("B_SIGN_DATE"))));

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveProtocolInfo(pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		IDataset products = new DatasetList();
		products.add(pageData.getString("PRODUCT_ID"));
		archives.put("PRODUCTS", products);

		return archives;
	}

	protected void setBackValue(IData resultData, IData pageData) throws Exception {
		resultData.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME"));
		resultData.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
		ElecLineUtil.setBackValue(resultData,pageData);
	}
	
	//更新集团资料表
	public void callCustomer(IRequestCycle cycle) throws Exception{
	    String custId = getData().getString("CUST_ID");
	    if(StringUtils.isBlank(custId)){
	        CSViewException.apperr(CrmCommException.CRM_COMM_103,"未获取到集团CUST_ID!");
	    }
	    IData data = new DataMap();
	    data.put("CUST_ID", custId);
	    data.put("REMOVE_TAG", "0");
	    ServiceResponse response = BizServiceFactory.call("CC.group.IGroupOperateSV.updateGroupByCustId", data);
	    IData ret = response.getBody();
	    String backGroupId = "";
	    if(DataUtils.isNotEmpty(ret)){
	        IDataset backList = ret.getDataset("OUTDATA");
	        if(DataUtils.isNotEmpty(backList)){
	            backGroupId = backList.first().getString("GROUP_ID");
	        }
	    }
	    
	    if(StringUtils.isBlank(backGroupId)){
	        CSViewException.apperr(CrmCommException.CRM_COMM_103,"生成集团失败,接口返回值为："+ret.toString());
	    }
	    //CSViewCall.call(this, "CC.group.IGroupOperateSV.updateGroupByCustId", data);
		data.put("GROUP_ID",backGroupId);
	    setAjax(data);
	}

}
