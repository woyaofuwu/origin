package com.asiainfo.veris.crm.order.soa.group.esop.busicheck;


import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubscribeViewInfoBean;

public class BusiCheckVoiceSVC extends CSBizService{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 根据条件查询
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset queryWorkformInfos(IData inparams) throws Exception
    {
    	String groupId =  inparams.getString("GROUP_ID");
    	String ibsysId = inparams.getString("IBSYSID");
    	String cityCode = inparams.getString("CITY_CODE");
        return BusiCheckVoiceBean.queryWorkformInfos(groupId,ibsysId,cityCode);
    }

	//3、获取最新的订单属性信息(封装到IData)
	public static IDataset queryBusiDetailInfo(IData data) throws Exception{
		IDataset results =  new DatasetList();
		//如果该订单有复核通过的订单
		IDataset reviewWorkformInfos = BusiCheckVoiceBean.queryBusiDetailInfo(data);
		if(reviewWorkformInfos.size()>0){
			data = reviewWorkformInfos.getData(0);
		}
		IData result = getTimerTaskWorkformAttrInfo(data);
		results.add(result);
		return results;
	}
	
	//2、获取订单属性信息(封装到IData)
		public static IData getTimerTaskWorkformAttrInfo(IData data) throws Exception{
			String sub_ibsysid =  data.getString("SUB_IBSYSID");
			//业务属性
			IDataset productAttrInfos = SubscribeViewInfoBean.qrEweAttributesByGroupSeq(sub_ibsysid,null);	
			IData pattr = new DataMap();
			for(int i=0,size=productAttrInfos.size();i<size;i++){
				pattr.put(productAttrInfos.getData(i).getString("ATTR_CODE"), productAttrInfos.getData(i).getString("ATTR_VALUE"));
				
			}
			return pattr;
		}
	
	public static IDataset queryCheckRecordInfos(IData data) throws Exception{
		
		IDataset productAttrInfos = BusiCheckVoiceBean.queryCheckRecordInfos(data);	
		
		return productAttrInfos;
	}
	
	public static void addCheckRecordInfo(IData data) throws Exception{
		IData param  = new DataMap();
		param.put("CHECK_ID",System.currentTimeMillis()+"");
		param.put("IBSYSID", data.get("IBSYSID"));
		param.put("CHECK_TYPE", data.get("CHECK_TYPE"));
		param.put("BUSI_TYPE", data.get("BUSI_TYPE"));
		param.put("CHECK_STAFF_ID", getVisit().getStaffId());
		param.put("CHECK_TIME",DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		param.put("INSERT_TIME",DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		param.put("CHECK_RESULT", data.get("CHECK_RESULT"));
		param.put("REMARK", data.get("REMARK"));
		Dao.insert("TF_B_EOP_BUSI_CHECK_RECORD", param,Route.getJourDb(Route.CONN_CRM_CG));// 新增记录
		
	}
	
	public static void updateCheckRecordInfo(IData data) throws Exception{
		
		Dao.executeUpdateByCodeCode("TF_B_EOP_BUSI_CHECK_RECORD","UPDATE_CHECK_RECORD", data,Route.getJourDb(Route.CONN_CRM_CG));// 更新记录
		
	}
	public static IDataset queryAddInfoByIbsysid(IData data) throws Exception{
		return BusiCheckVoiceBean.queryAddInfoByIbsysid(data);
	}
	public static void submitAddInfos(IData data) throws Exception{
		data.put("INST_ID",SeqMgr.getInstId(getVisit().getLoginEparchyCode()));
		Dao.insert("TD_B_EOP_VOICELINE_ADDINFO", data,Route.CONN_CRM_CEN);// 新增记录
	}
	
	public static IDataset queryVoicelineAnnualInfo(IData data) throws Exception{
		return BusiCheckVoiceBean.queryVoicelineAnnualInfo(data);
	}
	
	public static void submitAnnualReviewInfo(IData data) throws Exception{
		data.put("INST_ID",SeqMgr.getInstId(getVisit().getLoginEparchyCode()));
		Dao.insert("TD_B_EOP_VOICELINE_ANNUAL", data,Route.CONN_CRM_CEN);// 新增记录
	}
	//5、获取所有审核通过的最新的属性列表_语音专线
	public static IDataset getWorkformNewAttrList(IData data) throws Exception{
		
			IDataset newAttrList = BusiCheckVoiceBean.queryWorkformInfos_VoiceSpec(data);
			for(int i=0,size=newAttrList.size();i<size;i++){
				newAttrList.getData(i).putAll(getTimerTaskWorkformNewAttrInfo(newAttrList.getData(i)));
			}
			return newAttrList;

	}
	
	//3、获取最新的订单属性信息(封装到IData)
	public static IData getTimerTaskWorkformNewAttrInfo(IData data) throws Exception{
		//如果该订单有复核通过的订单
		IDataset reviewWorkformInfos = BusiCheckVoiceBean.queryBusiDetailInfo(data);
		if(reviewWorkformInfos.size()>0){
			data = reviewWorkformInfos.getData(0);
		}
		return getTimerTaskWorkformAttrInfo(data);
	}

}
