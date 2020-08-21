package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;


import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.querygroupinfo.GrpLineInfoQryBean;

public class OwnFeeQrySVC extends CSBizService{
	private static final long serialVersionUID = 1L;
	
	public void saveEopInfoAndDrive(IData data) throws Exception
	{
		String ibsysid = data.getString("IBSYSID","");
		String recordNum = data.getString("RECORD_NUM");
		String busiformNodeId = data.getString("BUSIFORM_NODE_ID","");
		
		//查询tf_b_eop_product_sub 
		IData productSubInfo = WorkformProductSubBean.qryProductByPk(ibsysid, recordNum);
		if (IDataUtil.isEmpty(productSubInfo)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID"+ibsysid+",RECORD_NUM"+recordNum+"获取tf_b_eop_product_sub数据失败！");
		}
		IData info = new DataMap();
		info.put("USER_ID", productSubInfo.getString("USER_ID"));
		info.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
		info.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
		
		IDataset oweFeeData = CSAppCall.call("CS.UserOwenInfoQrySVC.getOweFeeByUserId", info);
		
        double relaFee = Double.valueOf(oweFeeData.first().getString("REAL_FEE"));


        if (relaFee < 0)
        {
        	data.put("IS_OWNFEE", "1");//0- 表示无欠费，1-表示欠费 
        	data.put("EXEC_TIME", SysDateMgr.startDateOffset(SysDateMgr.getSysDate(), "7", "1")+ SysDateMgr.START_DATE_FOREVER);
        }else {
        	data.put("IS_OWNFEE", "0");//0- 表示无欠费，1-表示欠费 
        	data.put("EXEC_TIME", SysDateMgr.getSysTime());
		}

    	saveEopOtherData(data,recordNum);
    	
//    	EweNodeQry.updEweNodeByPk(busiformNodeId,data.getString("EXEC_TIME"));
    	
	}
	
	/**
     * 保存other表信息
     */
    public static void saveEopOtherData(IData data,String recordNum) throws Exception
    {
    	IDataset otherList = new DatasetList();
    //	int seq = 0;
    	
//    	IDataset otherInfos = WorkformOtherBean.getOtherSeq(data.getString("IBSYSID",""));
//    	if (DataUtils.isNotEmpty(otherInfos)) {
//    		String seqString = otherInfos.first().getString("MAX(A.SEQ)");
//    		if (StringUtils.isNotEmpty(seqString)) {
//    			seq =  Integer.valueOf(seqString);
//			}
//		}
    //	String ibsysid = data.getString("IBSYSID","");
    //	IDataset eomsinfo = WorkformEomsBean.getEomsDatasetByIbsysidRecordNum(ibsysid, recordNum);
    	
        IData otherData = new DataMap();
        otherData.put("SUB_IBSYSID",SeqMgr.getSubIbsysId());
        otherData.put("IBSYSID",data.getString("IBSYSID",""));
        otherData.put("SEQ",SeqMgr.getAttrSeq());
        otherData.put("GROUP_SEQ","0");
        otherData.put("NODE_ID",data.getString("NODE_ID",""));
        otherData.put("ACCEPT_MONTH",SysDateMgr.getCurMonth());
        otherData.put("ATTR_CODE","IS_OWNFEE");
        otherData.put("ATTR_NAME","是否欠费");
        otherData.put("ATTR_VALUE",data.getString("IS_OWNFEE",""));
        otherData.put("PARENT_ATTR_CODE","");
        otherData.put("RECORD_NUM",recordNum);
        otherData.put("UPDATE_TIME",SysDateMgr.getSysTime());
        otherData.put("RSRV_STR1","");
        otherData.put("RSRV_STR2","");
        otherList.add(otherData);
        
		WorkformOtherBean.insertWorkformOther(otherList);
    	
    }
    
    public void saveVoipEopInfoAndDrive(IData data) throws Exception
  	{
  		String ibsysid = data.getString("IBSYSID","");
  		
  		//查询tf_b_eop_product 
  		IDataset productInfo = WorkformProductBean.qryProductByIbsysid(ibsysid);
  		if (IDataUtil.isEmpty(productInfo)) {
  			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID"+ibsysid+",获取tf_b_eop_product数据失败！");
  		}
  		
  		String userId = productInfo.first().getString("USER_ID","");
  		IDataset userDatalineInfos = GrpLineInfoQryBean.queryLineByUserId(userId);
  		if (IDataUtil.isEmpty(userDatalineInfos)) {
  			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID"+userId+",获取TF_F_USER_DATALINE数据失败！");
		}
  		
  		IDataset productSubInfo = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
  		if (IDataUtil.isEmpty(productSubInfo)) {
  			CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID"+ibsysid+",获取tf_b_eop_product_sub数据失败！");
  		}
  		
  		int linecount = userDatalineInfos.size();//集团专线总条数
  		int linesize = productSubInfo.size();//拆机条数
  		boolean islastLine = true;  //判断是否最后一条专线
  		
  		if (linecount == linesize) {
  			islastLine = true;
		}else{
			if (linecount > 1) {
	  			islastLine = false;
			}
		}
  		
  		
  		if (islastLine) {
  			IData info = new DataMap();
  	  		info.put("USER_ID", productSubInfo.first().getString("USER_ID"));
  	  		info.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
  			info.put(Route.USER_EPARCHY_CODE, getVisit().getLoginEparchyCode());
  			
  	  		IDataset oweFeeData = CSAppCall.call("CS.UserOwenInfoQrySVC.getOweFeeByUserId", info);
  	  		
  	        double relaFee = Double.valueOf(oweFeeData.first().getString("REAL_FEE"));


			if (relaFee < 0)
			{
			  	data.put("IS_OWNFEE", "1");//0- 表示无欠费，1-表示欠费 
			    data.put("EXEC_TIME", SysDateMgr.startDateOffset(SysDateMgr.getSysDate(), "7", "1")+ SysDateMgr.START_DATE_FOREVER);
			}else {
			  	data.put("IS_OWNFEE", "0");//0- 表示无欠费，1-表示欠费 
			    data.put("EXEC_TIME", SysDateMgr.getSysTime());
			}
			
			saveEopOtherData(data,"0");
		}
  		
  	}
}