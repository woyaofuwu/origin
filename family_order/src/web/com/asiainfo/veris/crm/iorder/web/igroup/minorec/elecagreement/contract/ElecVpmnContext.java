package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.contract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

import java.text.SimpleDateFormat;

public abstract class ElecVpmnContext extends ElecLineProtocolBase {

	private static final int DATE_LENGTH = 10;

	private final static String B_NAME = "中国移动通信集团海南有限公司";

	protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		String sysDate =SysDateMgr4Web.getSysDate("yyyy年MM月dd日");
		if("N".equals(pageData.getString("SUBMITTYPE"))){
			IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());

			protocolInfo.put("A_NAME", groupInfo.getString("CUST_NAME"));
			protocolInfo.put("B_NAME", B_NAME);
			protocolInfo.put("A_SIGN_DATE", sysDate);
			protocolInfo.put("B_SIGN_DATE", sysDate);
		}else{
			String date1 = protocolInfo.getString("A_SIGN_DATE");
			protocolInfo.put("A_SIGN_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(date1,SysDateMgr4Web.PATTERN_STAND),"yyyy年MM月dd日"));
			String date2 = protocolInfo.getString("B_SIGN_DATE");
			protocolInfo.put("B_SIGN_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(date2,SysDateMgr4Web.PATTERN_STAND),"yyyy年MM月dd日"));
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

		//协议状态
		archives.put("ARCHIVES_STATE", "0");//待审核

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

		//协议状态
		archives.put("ARCHIVES_STATE", "0");//待审核

		return archives;
	}

	protected void setBackValue(IData resultData, IData pageData) throws Exception {
		resultData.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME"));
		resultData.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
		if(!"U".equals(pageData.getString("SUBMITTYPE"))){
			//新增时,主体协议不包含产品参数，先置失效。
			IData input = new DataMap();
			input.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));
			input.put("VALID_TAG","1");
			CSViewCall.call(this,"SS.AgreementInfoSVC.updateElectronicRel",input);
		}

		ElecLineUtil.setBackValue(resultData,pageData);
	}


}
