package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import java.util.Iterator;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userotherinfo.UserOtherInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;

public abstract class IdcDataApplyParam extends BizTempComponent {
    public abstract void setInfo(IData info);
    public abstract void setAttrInfo(IData attrInfo);
    public abstract void setAttrInfoList(IDataset attrInfoList);
    public abstract void setLineIdcInfo(IData lineIdcInfo);
    public abstract void setGrpExtInfo(IData grpExtInfo);

    public abstract void setRowIndex(int rowIndex);

    public abstract String getOperCode();

    public abstract String getTempletId();
    public abstract String getProductID();


    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/applyparam/IdcDataApplyParam.js";
    	IData data = getPage().getData();
        if (isAjax) {
            includeScript(writer, jsFile, false, false);
        } else {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
    	IData lineIdcInfo=new DataMap();

        if(!"selectRelIbsysidForIdc".equals(data.getString("listener"))){//非选择勘察单时
        	IData param=new DataMap();
        	param.put("GROUP_ID",data.getString("GROUP_ID"));
        	param.put("EXTEND_TAG","idcstate");
            IDataset grpExtInfos = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.selectGroupExtendForTag", param);
            if(IDataUtil.isNotEmpty(grpExtInfos)&&IDataUtil.isNotEmpty(grpExtInfos.getData(0))){
            	setGrpExtInfo(grpExtInfos.getData(0));
            }
        	if ("21".equals(getOperCode())||"23".equals(getOperCode())) {
            	initUserAttrList();
            }
        	String bpmTempletID=data.getString("TEMPLET_ID");
        	if(!"EDIRECTLINECHECKIDC".equals(bpmTempletID)&&!"EDIRECTLINECANCELIDC".equals(bpmTempletID)){
            	selSubscribePool(data,lineIdcInfo);
        	}
        }
        selSubscribePooltoFist(data,lineIdcInfo);
        setLineIdcInfo(lineIdcInfo);
        selCustInfo(data);

    }
    /*public void getFilesLists() throws Exception {
    	  // 查询附件
    	IData data = getPage().getData();
        IData input = new DataMap();
        input.put("IBSYSID", data.getString("IBSYSID"));
        IDataset  filesets = CSViewCall.call(this, "SS.WorkformAttachSVC.qryContractAttach", input);

        setReasonList(filesets);
    }*/
    protected void selSubscribePool(IData data,IData lineIdcInfo)throws Exception{
    	String bpmTempletID=data.getString("TEMPLET_ID");
        /*if (data.getString("BPM_TEMPLET_ID").equals("EDIRECTLINEOPENIDC")) {
        	bpmTempletID=''
        }*/
        IDataset outList = new DatasetList();

    	if (data.getString("TEMPLET_ID").equals("EDIRECTLINEOPENIDC")
    			||data.getString("TEMPLET_ID").equals("EDIRECTLINEPREEMPTIONIDC")) {//勘察
    		bpmTempletID="EDIRECTLINECHECKIDC";
    		selSubscribePooltoList(data,bpmTempletID,outList,"勘察单");
    	}
    	
    	if (data.getString("TEMPLET_ID").equals("EDIRECTLINEOPENIDC")) {//预占
    		bpmTempletID="EDIRECTLINEPREEMPTIONIDC";
    		selSubscribePooltoList(data,bpmTempletID,outList,"预占单");
    	}
    	if (data.getString("TEMPLET_ID").equals("EDIRECTLINECHANGEIDC")) {//变更勘察
    		bpmTempletID="EDIRECTLINECHANGECHECKIDC";
    		IData params = new DataMap();
    		params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            data.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
    		selSubscribePooltoList(data,bpmTempletID,outList,"变更勘察单");
    	}
        

        lineIdcInfo.put("IDCLINELIST", outList);
    }
    private  void selSubscribePooltoList(IData data,String bpmTempletID,IDataset outList,String str)throws Exception{
    	IData param = new DataMap();
        param.put("BPM_TEMPLET_ID", bpmTempletID);
        param.put("PRODUCT_ID", data.getString("OFFER_CODE"));
        param.put("GROUP_ID", data.getString("GROUP_ID"));
        IDataset infos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryIbsysid", param);


        for (int i = 0; i < infos.size(); i++) {
            IData temp = infos.getData(i);
            String ibsysid = temp.getString("IBSYSID");
            IData tempParam = new DataMap();

            tempParam.put("REL_IBSYSID", ibsysid);
            tempParam.put("STATE", "F");
            // 查询此勘察单总共有多少条
            IDataset confInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qryLineNo", tempParam);
            if (IDataUtil.isNotEmpty(confInfos)&&IDataUtil.isNotEmpty(confInfos.getData(0))) {
            	IData confInfo=confInfos.getData(0);
            	if(!"".equals(data.getString("GRP_SERIAL_NUMBER",""))&&!confInfo.getString("POOL_VALUE","").equals(data.getString("GRP_SERIAL_NUMBER"))){
            		continue;
            	}
                IData outData = new DataMap();
                outData.put("IDCLINELIST_TEXT","订单号："+ ibsysid+",产品编码："+confInfo.getString("POOL_VALUE","")+",工单类型："+str);
                outData.put("IDCLINELIST_VAL", confInfo.getString("REL_IBSYSID",""));
                outList.add(outData);
            }
        }
    }
    protected void initUserAttrList()throws Exception{
    	IData data = getPage().getData();
    	if(!"".equals(data.getString("EC_USER_ID",""))){
    		String productId = data.getString("OFFER_CODE","");
    		IDataset dataLines = new DatasetList();
    		IData params = new DataMap();
    		params.put("USER_ID", data.getString("EC_USER_ID"));
            params.put("REMOVE_TAG", "0");
            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getGrpUserInfoByUserIdForGrp", params);
            params.clear();
            params.put("GRP_USER_ID", data.getString("EC_USER_ID"));
            params.put("GRP_SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            params.put("PRODUCT_ID", productId);
//            setUserInfo(params);
//			if ("7041".equals(productId)) {
				IData param = new DataMap();
				param.put("USER_ID", data.getString("EC_USER_ID"));
				// param.put("PRODUCT_ID", productId);VIOP不传产品id直接查
				IDataset attrDatas = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.queryUserAllAttrs", param);
				IData idcTradeParam = new DataMap();
				idcTradeParam.put("CONFIG_NAME", "IDC_TRADEPARAMCHANGE");
				idcTradeParam.put("STATUS", "0");
				IDataset idcTradeParamChange = CSViewCall.call(this, "SS.EweConfigQrySVC.qryEweConfigByConfigName", idcTradeParam);
				if(IDataUtil.isNotEmpty(attrDatas)) {
					IData attrInfo= new DataMap();
					attrInfo.put("IDC_USER_ID",data.getString("EC_USER_ID"));
		            IDataset attrInfoList=new DatasetList();
					for (int i = 0; i < attrDatas.size(); i++) {
						IData attrData=attrDatas.getData(i);
						if("".equals(attrData.getString("RSRV_NUM1", ""))){
							attrInfo.put(attrData.getString("ATTR_CODE", ""), attrData.getString("ATTR_VALUE", ""));
						}else{
							String recordNum=attrData.getString("RSRV_NUM1", "");
		                    if(recordNum.matches("^[0-9]*$")){
		                    	int recordNumInt=Integer.valueOf(recordNum);
		                    	/*int num =recordNumInt-1;
		                		if(attrInfoList.size()<recordNumInt){
		                			for(int size=attrInfoList.size();size<recordNumInt;size++){
		                				attrInfoList.add(new DataMap());
		                			}
		                		}*/
		                		int num=-1;
		                    	for (int j=0,size=attrInfoList.size();j<size;j++){
		                    		IData attrInfoJ= attrInfoList.getData(j);
		                    		if(recordNum.equals(attrInfoJ.getString("RSRV_NUM1"))){
		                    			num=j;break;
		                    		}
		                    		
		                    	}
		                    	if(num!=-1){
		                    		attrInfoList.getData(num).put(attrData.getString("ATTR_CODE"), attrData.getString("ATTR_VALUE", ""));
			                		attrInfoList.getData(num).put("RSRV_NUM1",recordNum);
		                    	}else{
		                    		num=attrInfoList.size();
		                    		attrInfoList.add(new DataMap());
		                    		attrInfoList.getData(num).put(attrData.getString("ATTR_CODE"), attrData.getString("ATTR_VALUE", ""));
			                		attrInfoList.getData(num).put("RSRV_NUM1",recordNum);
		                    	}
		                		
		                    }
						}
						String attrCode=attrData.getString("ATTR_CODE", "");
						for(int j = 0; j < idcTradeParamChange.size(); j++){
							IData idcTradeParamChangeData=idcTradeParamChange.getData(j);
							if(attrCode.equals(idcTradeParamChangeData.getString("PARAMNAME"))){
								attrInfo.put(idcTradeParamChangeData.getString("PARAMVALUE"), attrData.getString("ATTR_VALUE", ""));
							}
						}
						
					}
					
					if(!"".equals(attrInfo.getString("IDC_ProductType", ""))){
						attrInfo.put("IDC_Price",attrInfo.getString(attrInfo.getString("IDC_ProductType", ""), ""));
					}
					if(!"".equals(attrInfo.getString("7040011", ""))){
						attrInfo.put("IDC_Discount",attrInfo.getString("7040011", ""));//折扣
					}
					
					setAttrInfo(attrInfo);
					setAttrInfoList(attrInfoList);
				}
				
	            IDataset attrInfoList=new DatasetList();
				IData idcOtherparam = new DataMap();
				idcOtherparam.put("USER_ID", data.getString("EC_USER_ID"));
				idcOtherparam.put("RSRV_VALUE_CODE", "IDCLINE");
				IDataset idcOther = CSViewCall.call(this, "CS.UserOtherQrySVC.getUserOtherByUserRsrvValueCodeForGrp", idcOtherparam);

				for (int i = 0; i < idcOther.size(); i++) {
					IData idcOtherData=idcOther.getData(i);
					
					String recordNum=idcOtherData.getString("RSRV_VALUE", "");
                    if(recordNum.matches("^[0-9]*$")){
                    	int recordNumInt=Integer.valueOf(recordNum);
                    	/*int num =recordNumInt-1;
                		if(attrInfoList.size()<recordNumInt){
                			for(int size=attrInfoList.size();size<recordNumInt;size++){
                				attrInfoList.add(new DataMap());
                			}
                		}*/
                		int num=-1;
                    	for (int j=0,size=attrInfoList.size();j<size;j++){
                    		IData attrInfoJ= attrInfoList.getData(j);
                    		if(recordNum.equals(attrInfoJ.getString("RSRV_NUM1"))){
                    			num=j;break;
                    		}
                    		
                    	}
                    	if(num!=-1){
                    	}else{
                    		num=attrInfoList.size();
                    		attrInfoList.add(new DataMap());
                    	}
                		attrInfoList.getData(num).put("IDC_7041_PortNumber", idcOtherData.getString("RSRV_STR1", ""));
                		attrInfoList.getData(num).put("IDC_7041_IpAddress", idcOtherData.getString("RSRV_STR2", ""));
                		attrInfoList.getData(num).put("IDC_7041_EquipmentName", idcOtherData.getString("RSRV_STR3", ""));
                		attrInfoList.getData(num).put("IDC_7041_TakeEffectType", idcOtherData.getString("RSRV_STR4", ""));
                		attrInfoList.getData(num).put("IDC_7041_TakeEffectTime", idcOtherData.getString("RSRV_STR5", ""));
                		attrInfoList.getData(num).put("IDC_7041_FreeTime1", idcOtherData.getString("RSRV_STR6", ""));
                		attrInfoList.getData(num).put("IDC_7041_FreeTime2", idcOtherData.getString("RSRV_STR7", ""));
                		attrInfoList.getData(num).put("IDC_7041_FreeTime3", idcOtherData.getString("RSRV_STR8", ""));

                		attrInfoList.getData(num).put("RSRV_NUM1",recordNum);

                		
                    }
				}
				setAttrInfoList(attrInfoList);
//			}
    	}
    }


    private  void selSubscribePooltoFist(IData data,String bpmTempletID,IDataset outList)throws Exception{
    	IData tempParam = new DataMap();
        tempParam.put("POOL_NAME", "ORDER_OrderNumFlag");
        tempParam.put("STATE", "F");
//        tempParam.put("POOL_CODE", data.getString("OFFER_CODE"));

        // 查询此勘察单总共有多少条
        IDataset subscribePoolInfos = CSViewCall.call(this, "SS.ConfCrmQrySVC.qrySubscribePool", tempParam);
        for (int i = 0; i < subscribePoolInfos.size(); i++) {
            IData subscribePoolInfo = subscribePoolInfos.getData(i);
            if(subscribePoolInfo.getString("POOL_VALUE", "").equals(subscribePoolInfo.getString("REL_IBSYSID", ""))){
            	IData tempParam1 = new DataMap();
            	tempParam1.put("IBSYSID", subscribePoolInfo.getString("REL_IBSYSID", ""));
                IDataset subscribeInfos = CSViewCall.call(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", tempParam1);
                if(IDataUtil.isNotEmpty(subscribeInfos)&&IDataUtil.isNotEmpty(subscribeInfos.getData(0))
                		&&data.getString("GROUP_ID").equals(subscribeInfos.getData(0).getString("GROUP_ID", ""))
                		&&bpmTempletID.equals(subscribeInfos.getData(0).getString("BPM_TEMPLET_ID", ""))){
                	subscribeInfos.getData(0).getString("RSRV_STR4", "");
                	IData outData = new DataMap();
                    outData.put("IDCFIRSTIBSYSIDLIST_TEXT","订单号："+ subscribePoolInfo.getString("REL_IBSYSID", "")+",主题："+subscribeInfos.getData(0).getString("RSRV_STR4", ""));
                    outData.put("IDCFIRSTIBSYSIDLIST_VAL", subscribePoolInfo.getString("REL_IBSYSID", ""));
                    outList.add(outData);
                }
                
            }

        }
    }
    protected void selCustInfo(IData data)throws Exception{
    	
    	IData tempParam=new DataMap();
    	tempParam.put("EXTEND_TAG", "IDCSTATE");
    	tempParam.put("GROUP_ID", data.getString("GROUP_ID"));
        IDataset custInfos = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.selectGroupExtendForTag", tempParam);
        if(IDataUtil.isNotEmpty(custInfos)){
        	IData custInfo=custInfos.first();
        	IData custInfoOut=new DataMap();
        	custInfoOut.put("CUST_unitNature", custInfo.getString("RSRV_STR1", ""));//单位性质
        	custInfoOut.put("CUST_unitType", custInfo.getString("RSRV_STR2", ""));//单位所属分类
        	custInfoOut.put("CUST_unitLevel", custInfo.getString("RSRV_STR3", ""));//单位行政级别
        	custInfoOut.put("CUST_contactsName", custInfo.getString("RSRV_STR4", ""));//联系人姓名(客户侧)
        	custInfoOut.put("CUST_contactsNumber", custInfo.getString("RSRV_STR5", ""));//联系人固定电话（客户侧）
        	custInfoOut.put("CUST_contactsPhoneNumber", custInfo.getString("RSRV_STR6", ""));//联系人移动电话（客户侧）
        	custInfoOut.put("CUST_contactsEmail", custInfo.getString("RSRV_STR7", ""));//联系人Email地址（客户侧）
        	custInfoOut.put("CUST_manageLicenceNum", custInfo.getString("RSRV_STR8", ""));//经营许可证编号
        	custInfoOut.put("CUST_PsptType", custInfo.getString("RSRV_STR9", ""));//联系人证件类型（客户侧）
        	custInfoOut.put("CUST_PsptNum", custInfo.getString("RSRV_STR10", ""));//联系人证件号码（客户侧）

        	setGrpExtInfo(custInfoOut);
        }
    }

    protected void selSubscribePooltoFist(IData data,IData lineIdcInfo)throws Exception{
    	String bpmTempletID=data.getString("TEMPLET_ID");
        /*if (data.getString("BPM_TEMPLET_ID").equals("EDIRECTLINEOPENIDC")) {
        	bpmTempletID=''
        }*/
        IDataset outList = new DatasetList();

		selSubscribePooltoFist(data,bpmTempletID,outList);
    	

        

        lineIdcInfo.put("IDCFIRSTIBSYSIDLIST", outList);
    }
}
