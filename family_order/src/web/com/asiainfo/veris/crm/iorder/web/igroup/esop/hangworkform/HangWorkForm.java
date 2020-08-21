package com.asiainfo.veris.crm.iorder.web.igroup.esop.hangworkform;

import java.util.Iterator;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class HangWorkForm extends CSBasePage
{
	private static final String AGREEUNHANGWORKSHEET = "agreeUnhangWorkSheet";
	 /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }
    /**
     * @Description: 校讯通异网号码互查, 根据原来的 ChenTest.java文件写的
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void layoutWorkForm(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        String ibSysId = condData.getString("cond_IBSYSID");
        String groupId = condData.getString("cond_GROUPID");
        String productNo = condData.getString("cond_PRODUCTNO");
        String serialNo = condData.getString("cond_SERIALNO");
   
     //   String queryType =  condData.getString("HANG_NODE");
        
        // 获取所有数据
 		IData param = new DataMap();
 		param.put("IBSYSID", ibSysId);
 		param.put("GROUP_ID", groupId);
 		param.put("PRODUCT_NO", productNo);
 		param.put("SERIALNO", serialNo);
 	/*	if("1".equals(queryType)){
 			param.put("BUSI_STATE", "B");//暂定挂起是B
 		}else{*/
 			param.put("BUSI_STATE", "Q");//可解挂是Q
 		/*}*/
 		
 		//查询TF_B_EOP_EOMS_STATE
        IDataset eomsDatas = CSViewCall.call(this, "SS.WorkformEomsDetailSVC.qryEmosDetailInfoByIdNo", param);
 		if(IDataUtil.isEmpty(eomsDatas)){
 			param.put("BUSI_STATE", "W");//
 			eomsDatas = CSViewCall.call(this, "SS.WorkformEomsDetailSVC.qryEmosDetailInfoByIdNo", param);
 		}
        //过滤数据
 		IDataset groupInfo = queryFromInfo(eomsDatas,ibSysId);
 		setInfos(groupInfo);
        setCondition(condData);
        setInfoCount(groupInfo.size());

        if (IDataUtil.isEmpty(groupInfo))
        {
            IData ajax = new DataMap();
            ajax.put("X_RESULTCODE", "0");
            ajax.put("X_RESULTINFO", "没有符合条件的查询结果,请核对你要查询的内容！");
            setAjax(ajax);
        }
        else
        {
            setHintInfo("查询成功！");
        }
    }
  
    /**
     * 前台提交数据
     * @param cycle
     * @throws Exception
     */
	public void createWorkFormInfo(IRequestCycle cycle) throws Exception {
		IData condData = getData();
		IData oattrs = new DataMap();
		String ibSysId = condData.getString("cond_IBSYSID");
	//	String flag = condData.getString("HANG_NODE");//1：挂起；2：解挂
		String productNos = condData.getString("SN");
		String[] productNo = productNos.split("\\|");
	
		for(int i= 0;i<productNo.length;i++){
			String productNoStr=productNo[i];
			String[] productNoStrs = productNoStr.split("_");

			String productNoSimple = productNoStrs[0];
			// 获取所有数据
			IData param = new DataMap();
			param.put("IBSYSID", ibSysId);
			param.put("PRODUCT_NO",productNoSimple);
			/*if("1".equals(flag)){
				param.put("BUSI_STATE","C");
				oattrs.put("applyReason", condData.getString("CONTENT"));//申请原因
				oattrs.put("days", condData.getString("HANG_DAT"));
			}else if("2".equals(flag)){*/
				
			/*}*/

			//CSViewCall.call(this, "SS.WorkformEomsDetailSVC.updateDetailInfo", param);
			IData eomsDatas = CSViewCall.callone(this, "SS.WorkformEomsStateSVC.qryEomsStateByIbsysidAndProductNo", param);

			oattrs.put("serialNo",eomsDatas.getString("SERIALNO"));
			oattrs.put("productNo",productNoSimple);
			oattrs.put("agreePerson", condData.getString("LINK_NAME"));//申请人
			oattrs.put("agreePersonContactPhone", condData.getString("LINK_PHONE_CODE"));//申请人联系电话
			oattrs.put("agreeType", "00");//解挂类型
			oattrs.put("agreeResult", condData.getString("CONTENT"));
			if("1".equals(productNoStrs[1])||"2".equals(productNoStrs[1])){
				oattrs.put("cityName", productNoStrs[2]);//
			}else{
				oattrs.put("cityName", productNoStrs[2]);//
			}
			oattrs.put("taskName", "");//
			
			
			String recordNum = eomsDatas.getString("RECORD_NUM");
			IData params = new DataMap();
			params.put("RECORD_NUM", recordNum);
			params.put("IBSYSID", ibSysId);
			
	        IData eweData = CSViewCall.callone(this,"SS.EweNodeQrySVC.qryEweByibsysIdRecordnum", params);//查询是否有子流程
	        if(IDataUtil.isEmpty(eweData)){
	        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单无子流程，无法挂起！");
	        }
	        String bpmTempletId = eweData.getString("BPM_TEMPLET_ID");

	        IData eweInfo  =  new DataMap();
	        String busiformId = eweData.getString("SUB_BUSIFORM_ID");
	        eweInfo.put("BUSIFORM_ID", busiformId);
	        eweInfo.put("STATE", "0");
	        IData eweNode =  CSViewCall.callone(this,"SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", eweInfo);//查询节点
	        if(IDataUtil.isEmpty(eweNode)){
	        	IDataset releDatas = CSViewCall.call(this, "SS.WorkformReleSVC.qryWorkFormReleByBusiformId", eweInfo);//
		        if(IDataUtil.isNotEmpty(releDatas)){
		        	for(Object releDataObject:releDatas){
		        		IData releData=(IData)releDataObject;
		        		if(releData.getString("RELE_VALUE","").equals(productNoStrs[1])){
		        			eweInfo.put("BUSIFORM_ID", releData.getString("SUB_BUSIFORM_ID"));
			    	        eweNode = CSViewCall.callone(this, "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", eweInfo);
			    	        if(IDataUtil.isNotEmpty(eweNode)){
			    	        	if(!"waitUnhangWorkSheet".equals(eweNode.getString("NODE_ID")) || !"replyhangWorkSheet".equals(eweNode.getString("PRE_NODE_ID"))){
				    	        	continue;
				    	        }else{
				    	        	bpmTempletId="eomsUnhangProess";
				    	        	busiformId=releData.getString("SUB_BUSIFORM_ID");
				    	        	break;
				    	        }
			    	        }
		        		}
		        	}
		        }
	        }
	        if(IDataUtil.isEmpty(eweNode)){
	        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单TF_B_EWE_NODE表无数据！");
	        }
	        String nodeId = eweNode.getString("NODE_ID");
	        String busiFormNodeId =  eweNode.getString("BUSIFORM_NODE_ID");
	        String productId =  eweData.getString("BUSI_CODE");
			IData data = new DataMap();
			data.put("IBSYSID", ibSysId);
			data.put("ATTR_DATA", oattrs);
			data.put("RECORD_NUM", eomsDatas.getString("RECORD_NUM"));
			data.put("OPER_TYPE", AGREEUNHANGWORKSHEET);
			data.put("NODE_ID",nodeId);
			data.put("BUSIFORM_ID",busiformId);
			data.put("BUSIFORM_NODE_ID",busiFormNodeId);
			data.put("DEAL_EPARCHY_CODE",getVisit().getLoginEparchyCode());
			data.put("PARENT_BPM_TEMPLET_ID",bpmTempletId);
			data.put("PARENT_BUSI_CODE",productId);
			data.put("STAFF_ID",getVisit().getStaffId());
			data.put("PRODUCT_NO",productNoSimple);
			IData sb = CSViewCall.callone(this, "SS.WorkformEomsSVC.saveUnHangWorkSheet", data); // 保存EOMS数据
			IData input = new DataMap();
			input.put("SUB_BI_SN", sb.getString("SUB_IBSYSID"));
			input.put("BI_SN", ibSysId);
			input.put("BUSIFORM_ID", busiformId);
			input.put("BPM_TEMPLET_ID", bpmTempletId);
			//input.put("RECORD_NUM", recordNum);
			CSViewCall.call(this, "SS.EweAgreeUnHangWorkSVC.agreeUnHangWorkSheet", input); // 保存EOMS数据
			data.put("IBSYSID", ibSysId);
        	data.put("BUSIFORM_ID", busiformId);
        	data.put("NODE_ID", nodeId);
        	CSViewCall.call(this, "SS.EweAsynSVC.saveAsynInfo", data); // 驱动流程
			
			

			
		}
			
		
	}
	
	/**
	 * 拼接前台展示数据
	 * @param eomsDatas
	 * @param ibSysId
	 * @return
	 * @throws Exception
	 */
    public IDataset queryFromInfo(IDataset eomsDatas,String ibSysId) throws Exception {
		IDataset groupInfo = new DatasetList();
		for(int i = 0;i<eomsDatas.size();i++){
			IData emos = eomsDatas.getData(i);
			ibSysId = emos.getString("IBSYSID","");
			IData emosDetail = new DataMap();
			IData params = new DataMap();
			String recordNum = emos.getString("RECORD_NUM");
			params.put("RECORD_NUM", recordNum);
			params.put("IBSYSID", ibSysId);
			
	        IDataset eweData = CSViewCall.call(this, "SS.EweNodeQrySVC.qryEweByibsysIdRecordnum", params);//查询是否有子流程
	        if(IDataUtil.isEmpty(eweData)){
	        	continue;
	        }
	       /* params.put("ATTR_CODE", "applyType");
	        params.put("BUSIFORM_ID", eweData.first().getString("BUSIFORM_ID"));
	        params.put("NODE_ID", eweData.first().getString("replyhangWorkSheet"));
	        IDataset applyTypes = CSViewCall.call(this, "SS.EweAsynSVC.queryByBusiformNode", params);//查询是否是客户类原因
	        if(IDataUtil.isEmpty(applyTypes)){
	        	continue; //测试注释掉
	        }else{
	        	String applyType = applyTypes.first().getString("ATTR_VALUE");
	        	if(!"00".equals(applyType))
	        	continue;
	        }*/
	        String cityA="";
			String cityZ="";
			String buildingsection="";
	        IData input = new DataMap();
	        input.put("IBSYSID", ibSysId);
	        input.put("NODE_ID", "eomsProess");
	        IDataset eomsDat = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", input);
			if(eomsDat != null && eomsDat.size() > 0){
				for(int j= 0;j<eomsDat.size();j++){
					IData emossub = eomsDat.getData(j);
					if("PRODUCTNAME".equals(emossub.getString("ATTR_CODE",""))){
						emosDetail.put("PRODUCT_NAME", emossub.getString("ATTR_VALUE",""));//产品名称
					}
					if("PRODUCTNO".equals(emossub.getString("ATTR_CODE","")) && emossub.getString("RECORD_NUM").equals(recordNum)){
						emosDetail.put("PRODUCT_NO", emossub.getString("ATTR_VALUE",""));//业务标识
					}
					if("GROUP_ID_INPUT".equals(emossub.getString("ATTR_CODE",""))){
						emosDetail.put("CUSTOM_NO", emossub.getString("ATTR_VALUE",""));//集团客户编号
					}
					if("CUSTOMNAME".equals(emossub.getString("ATTR_CODE",""))){
						emosDetail.put("CUSTOM_NAME", emossub.getString("ATTR_VALUE",""));//集团客户名称
					}
					if("TRADENAME".equals(emossub.getString("ATTR_CODE","")) && emossub.getString("RECORD_NUM").equals(recordNum)){
						emosDetail.put("TRADE_NAME", emossub.getString("ATTR_VALUE",""));//专线名称
					}
					if("BIZRANGE".equals(emossub.getString("ATTR_CODE",""))){
						emosDetail.put("BIZ_RANGE", emossub.getString("ATTR_VALUE",""));//业务范围
					}
					if(recordNum.equals(emossub.getString("RECORD_NUM",""))&&"CITYA".equals(emossub.getString("ATTR_CODE",""))){
						cityA=emossub.getString("ATTR_VALUE","");//
					}
					if(recordNum.equals(emossub.getString("RECORD_NUM",""))&&"CITYZ".equals(emossub.getString("ATTR_CODE",""))){
						cityZ=emossub.getString("ATTR_VALUE","");//
					}
					
					if("BUILDINGSECTION".equals(emossub.getString("ATTR_CODE",""))){
						buildingsection=emossub.getString("ATTR_VALUE","");//
					}
				}
			}
			emosDetail.put("SERIALNO", emos.getString("SERIALNO",""));
	        String busiformId = eweData.first().getString("SUB_BUSIFORM_ID");
	        params.put("BUSIFORM_ID", busiformId);
	        params.put("STATE", "0");
	        IDataset applyTypes = CSViewCall.call(this, "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", params);//查询是否是客户类原因
	        if(IDataUtil.isEmpty(applyTypes)){
		        IDataset releDatas = CSViewCall.call(this, "SS.WorkformReleSVC.qryWorkFormReleByBusiformId", params);//
		        if(IDataUtil.isNotEmpty(releDatas)){
		        	for(Object releDataObject:releDatas){
		        		IData releData=(IData)releDataObject;
		    	        params.put("BUSIFORM_ID", releData.getString("SUB_BUSIFORM_ID"));
		    	        applyTypes = CSViewCall.call(this, "SS.EweNodeQrySVC.qryEweNodeByBusiformIdState", params);
		    	        emosDetail=new DataMap(emosDetail.toString());
		    	        emosDetail.put("BIZRANGECITY", releData.getString("RELE_VALUE"));
		    	        if("1".equals(releData.getString("RELE_VALUE"))){
			    	        emosDetail.put("CITYNAME", cityA);
		    	        }else if("2".equals(releData.getString("RELE_VALUE"))){
			    	        emosDetail.put("CITYNAME", cityZ);
		    	        }else{
				    	  emosDetail.put("CITYNAME", buildingsection);
		    	        }
		    	        queryFromInfoToadd(ibSysId,applyTypes,emosDetail,groupInfo);
		        	}
		        }else{
		        	continue; //测试注释掉
		        }

	        	
	        }else{
	        	if("省内跨地市".equals(emosDetail.getString("BIZ_RANGE"))){
	        		emosDetail.put("BIZRANGECITY", "0");
			    	emosDetail.put("CITYNAME", cityA);
	        	}else{
	        		emosDetail.put("BIZRANGECITY", "0");
			    	emosDetail.put("CITYNAME", buildingsection);
	        	}
	        	queryFromInfoToadd(ibSysId,applyTypes,emosDetail,groupInfo);
	        }
	        
		}
		
		return groupInfo;
	}
    private void queryFromInfoToadd(String ibSysId,IDataset applyTypes,IData emosDetail,IDataset groupInfo) throws Exception{
    	if(!"waitUnhangWorkSheet".equals(applyTypes.first().getString("NODE_ID")) || !"replyhangWorkSheet".equals(applyTypes.first().getString("PRE_NODE_ID")))
        	return;
			IData data = new DataMap();
			data.put("IBSYSID", ibSysId);
			IData subscrib = CSViewCall.callone(this, "SS.WorkformSubscribeSVC.qryWorkformSubscribeByIbsysid", data);
			String bpmTemplteId = subscrib.getString("BPM_TEMPLET_ID");
			IData busi = new DataMap();
			busi.put("BPM_TEMPLET_ID", bpmTemplteId);
			busi.put("VALID_TAG", "0");
			IData busiName = CSViewCall.callone(this, "SS.QryFlowNodeDescSVC.qryFlowDescByTempletId", busi);
			emosDetail.put("BUSI_NAME", busiName.getString("TEMPLET_NAME",""));//业务类型
			groupInfo.add(emosDetail);
		}
		
   
    
    public abstract void setCondition(IData condition);
    public abstract void setHintInfo(String hintInfo);
    public abstract void setInfoCount(long infoCount);
    public abstract void setInfos(IDataset infos);
}
