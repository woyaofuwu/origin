package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.contract;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;



public abstract class ElecFusionComAccessorySec extends ElecLineProtocolBase {

	protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		inintalCommField();
		if("N".equals(pageData.getString("SUBMITTYPE"))){

		}
	}

	protected  void inintalCommField() throws Exception {
		IDataset numberTypes = new DatasetList();
		IData fixedTel = new DataMap();
		fixedTel.put("textField","固话");
		fixedTel.put("valueField","0");
		IData cellPhone = new DataMap();
		cellPhone.put("textField","手机");
		cellPhone.put("valueField","1");
		numberTypes.add(fixedTel);
		numberTypes.add(cellPhone);
		getInfo().put("NUMBER_TYPES",numberTypes);
	}

	protected IData buildFDFInfo(IData pageData) throws Exception{
		IData contextData = buildAccessoryInfo(pageData);
		return contextData;
	}

	protected IData buildAccessoryInfo(IData pageData){
		IDataset archiveslist = new DatasetList();
		IData tableInfos = new DataMap();
		archiveslist.add(tableInfos);

		IData lineInfo = new DataMap();

		IDataset lineInfos = new DatasetList(pageData.getString("LINE_INFOS"));
		if(IDataUtil.isEmpty(lineInfos)){
			for (int i = 0; i < 10; i++) {
				lineInfos.add(new DataMap());
			}
		}
		IDataset otherTable = new DatasetList();
		IData remark = new DataMap();
		remark.put("VALUE","注：该表可根据实际需求自行添加行数");
		remark.put("COLSPAN","2");

		IData aName = new DataMap();
		aName.put("VALUE","甲方（盖章）："+pageData.getString("A_NAME"));
		IData bName = new DataMap();
		bName.put("VALUE","乙方（盖章）：中国移动通信集团海南有限公司");
		IData aHead = new DataMap();
		aHead.put("VALUE","经办人（签名）：");
		IData bHead = new DataMap();
		bHead.put("VALUE","经办人（签名）：");
		IData aDate = new DataMap();
		aDate.put("VALUE","日期："+pageData.getString("A_SIGN_DATE"));
		IData bDate = new DataMap();
		bDate.put("VALUE","日期："+pageData.getString("B_SIGN_DATE"));

		otherTable.add(remark);
		for (int i = 0; i < 4; i++) {
			otherTable.add(new DataMap());
		}

		otherTable.add(aName);
		otherTable.add(bName);
		otherTable.add(aHead);
		otherTable.add(bHead);
		otherTable.add(aDate);
		otherTable.add(bDate);

		lineInfo.put("OTHER_TABLE",otherTable);
		lineInfo.put("LINE_INFOS",lineInfos);

		tableInfos.put("LINE_INFOS", lineInfo);

		IData datas = new DataMap();
		datas.put("DATAS", archiveslist);
		return datas;
	}

	public IData saveProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveAccessContextInfo(pageData);
		archivesInfo.put("A_NAME", "123");
		archivesInfo.put("B_NAME", "123");
		archivesInfo.put("A_HEADER", "123");
		archivesInfo.put("B_HEADER", "123");
		archivesInfo.put("CONTRACT_CODE", "1");

		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = buildArchivesAttrs(pageData);
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

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveAccessContextInfo(pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = buildArchivesAttrs(pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		IDataset products = new DatasetList();
		products.add(pageData.getString("PRODUCT_ID"));
		archives.put("PRODUCTS", products);

		return archives;
	}

	protected void setBackValue(IData resultData, IData pageData) throws Exception {
		resultData.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME"));
		ElecLineUtil.setBackValue(resultData,pageData);
	}

	private IDataset buildArchivesAttrs(IData pageData){
		IData archivesAttrs = new DataMap();
		IDataset archivesAttrsSet = new DatasetList();
		archivesAttrsSet.add(archivesAttrs);
		IDataset lineInfos = new DatasetList(pageData.getString("LINE_INFOS"));
		archivesAttrs.put("LINE_INFOS", lineInfos);

		archivesAttrs.put("A_NAME",pageData.getString("A_NAME"));
		archivesAttrs.put("B_NAME",pageData.getString("B_NAME"));
		archivesAttrs.put("A_HEADER",pageData.getString("A_HEADER"));
		archivesAttrs.put("B_HEADER",pageData.getString("B_HEADER"));
		archivesAttrs.put("A_SIGN_DATE",pageData.getString("A_SIGN_DATE"));
		archivesAttrs.put("B_SIGN_DATE",pageData.getString("B_SIGN_DATE"));
		archivesAttrs.put("AGREEMENT_DEF_ID",pageData.getString("AGREEMENT_DEF_ID"));

		return archivesAttrsSet;
	}

}
