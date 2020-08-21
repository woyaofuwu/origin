package com.asiainfo.veris.crm.order.soa.group.esop.transfer;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeTraQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInfoQry;

public class StaffTransferSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public IDataset qryStaffNameForName(IData param) throws Exception{
		IDataset staffInfo=StaffTransferBean.qryStaffNameForName(param.getString("StaffName",""));
		return staffInfo;

	}
	public IDataset qryStaffNameForId(IData param) throws Exception{
	    IDataset staffInfo=StaffTransferBean.qryStaffNameForId(param.getString("StaffId",""));
	    return staffInfo;
	    
	}
	public IDataset updStaffTransferForinfoList(IData param) throws Exception{
	    //临时员工工单转派  TYPE_ID=0
		String newStaff=param.getString("newStaffId");
		String remark=param.getString("remark");
		String typeId= "0";
		IDataset infoList=new DatasetList(param.getString("instList","[]"));
		System.out.println("updStaffTransferForinfoList"+param);
		Date date= new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update_time =simpleDateFormat.format(date);
		String batchID=SeqMgr.getSubIbsysId();
		
		IDataset staffTransferLogList= new DatasetList();

		
		if(IDataUtil.isNotEmpty(infoList)&&infoList.size()>0){
			for (int i=0,size=infoList.size();i<size;i++){
				IData infoData=infoList.getData(i);
				infoData.put("RECE_OBJ", newStaff);
				IData staffTransferLog = new DataMap();
				staffTransferLog.put("BATCHID",batchID);
				staffTransferLog.put("TRANSFER_TABLES", "TF_F_INFO_INSTANCE");
				staffTransferLog.put("TRANSFER_COLUMN1", "INST_ID");
				staffTransferLog.put("TRANSFER_PRIMARYKEY1", infoData.getString("INST_ID"));
				staffTransferLog.put("IBSYSID", SeqMgr.getSubIbsysId());
				staffTransferLog.put("UPDATE_COLUMNKEY1", "RECE_OBJ");
				staffTransferLog.put("UPDATE_OLDCOLUMNVAL1", infoData.getString("oldStaffId"));
				staffTransferLog.put("UPDATE_NEWCOLUMNVAL1", newStaff);
				staffTransferLog.put("ACCEPT_DATE", update_time);
				staffTransferLog.put("UPDATE_DATE", update_time);
				staffTransferLog.put("REMARK", remark);
				staffTransferLog.put("IBSYS_ID", infoData.getString("IBSYS_ID"));
				staffTransferLog.put("GROUP_ID", infoData.getString("GROUP_ID"));
				staffTransferLog.put("NODE_NAME", infoData.getString("NODE_NAME"));
				staffTransferLog.put("PRODUCT_NAME", infoData.getString("PRODUCT_NAME"));
				staffTransferLog.put("CUST_NAME", infoData.getString("CUST_NAME"));
				staffTransferLog.put("INFO_TOPIC", infoData.getString("INFO_TOPIC"));
				staffTransferLog.put("TYPE_ID", typeId);
				staffTransferLogList.add(staffTransferLog);
	        }
			
			int[] infoListFlag=OpTaskInfoQry.updStaffForInfo(infoList);
			
			for(int j:infoListFlag){

				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量更新TF_F_INFO_INSTANCE时异常，已自动停止");
				}
			}
		}
		
		
		if(IDataUtil.isNotEmpty(staffTransferLogList)&&staffTransferLogList.size()>0){
			int[] staffTransFlag=StaffTransferBean.insertStaffTransfer(staffTransferLogList);
			for(int j:staffTransFlag){
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量新增TF_B_EOP_STAFFTRANSFERLOG时异常，已自动停止");
				}
			}
		}

		return null;
	}
	public IDataset updStaffTransfer(IData param) throws Exception{
	    //离职员工工单转派 TYPE_ID=1
		String oldStaffId=param.getString("oldStaffId");
		String newStaff=param.getString("newStaffId");
		String remark=param.getString("remark");
		String typeId="1";

		
		IDataset eweNodeList= EweNodeQry.selStaffForEweNode(oldStaffId);
		IDataset staffTransferLogList= new DatasetList();
		Date date= new Date();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String update_time =simpleDateFormat.format(date);
		String batchID=SeqMgr.getSubIbsysId();

		if(IDataUtil.isNotEmpty(eweNodeList)&&eweNodeList.size()>0){
			for (int i=0,size=eweNodeList.size();i<size;i++){
				IData infoData=eweNodeList.getData(i);
				infoData.put("DEAL_NEW_STAFF_ID", newStaff);
				IData staffTransferLog = new DataMap();
				staffTransferLog.put("BATCHID",batchID);
				staffTransferLog.put("TRANSFER_TABLES", "TF_B_EWE_NODE");
				staffTransferLog.put("TRANSFER_COLUMN1", "BUSIFORM_NODE_ID");
				staffTransferLog.put("TRANSFER_PRIMARYKEY1", infoData.getString("BUSIFORM_NODE_ID"));
				staffTransferLog.put("IBSYSID", SeqMgr.getSubIbsysId());
				staffTransferLog.put("UPDATE_COLUMNKEY1", "DEAL_STAFF_ID");
				staffTransferLog.put("UPDATE_OLDCOLUMNVAL1", oldStaffId);
				staffTransferLog.put("UPDATE_NEWCOLUMNVAL1", newStaff);
				staffTransferLog.put("ACCEPT_DATE", update_time);
				staffTransferLog.put("UPDATE_DATE", update_time);
				staffTransferLog.put("REMARK", remark);
				staffTransferLog.put("TYPE_ID", typeId);
				staffTransferLogList.add(staffTransferLog);
	        }
			
			int[] eweListFlag=EweNodeQry.updStaffForEweNode(eweNodeList);
			for(int j:eweListFlag){
				System.out.println("StaffTransferSVC-updStaffTransfer-eweListFlag:"+j);
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量更新TF_B_EWE_NODE时异常，已自动停止");
				}
			}
			System.out.print(eweListFlag);
			
		}
		
		
		IDataset eweTraNodeList= EweNodeTraQry.selStaffForEweNodeTra(oldStaffId);
		if(IDataUtil.isNotEmpty(eweTraNodeList)&&eweTraNodeList.size()>0){
			for (int i=0,size=eweTraNodeList.size();i<size;i++){
				IData infoData=eweTraNodeList.getData(i);
				infoData.put("DEAL_NEW_STAFF_ID", newStaff);
				IData staffTransferLog = new DataMap();
				staffTransferLog.put("BATCHID",batchID);
				staffTransferLog.put("TRANSFER_TABLES", "TF_B_EWE_NODE_TRA");
				staffTransferLog.put("TRANSFER_COLUMN1", "BUSIFORM_NODE_ID");
				staffTransferLog.put("TRANSFER_PRIMARYKEY1", infoData.getString("BUSIFORM_NODE_ID"));
				staffTransferLog.put("IBSYSID", SeqMgr.getSubIbsysId());
				staffTransferLog.put("UPDATE_COLUMNKEY1", "DEAL_STAFF_ID");
				staffTransferLog.put("UPDATE_OLDCOLUMNVAL1", oldStaffId);
				staffTransferLog.put("UPDATE_NEWCOLUMNVAL1", newStaff);
				staffTransferLog.put("ACCEPT_DATE", update_time);
				staffTransferLog.put("UPDATE_DATE", update_time);
				staffTransferLog.put("REMARK", remark);
				staffTransferLog.put("TYPE_ID", typeId);
				staffTransferLogList.add(staffTransferLog);
	        }
			
			int[] eweTraListFlag=EweNodeTraQry.updStaffForEweNodeTra(eweTraNodeList);
			for(int j:eweTraListFlag){
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量更新TF_B_EWE_NODE_TRA时异常，已自动停止");
				}
			}
			
		}
		
		IDataset infoList=OpTaskInfoQry.selStaffForInfo("1", oldStaffId);

		if(IDataUtil.isNotEmpty(infoList)&&infoList.size()>0){
			for (int i=0,size=infoList.size();i<size;i++){
				IData infoData=infoList.getData(i);
				infoData.put("RECE_OBJ", newStaff);
				IData staffTransferLog = new DataMap();
				staffTransferLog.put("BATCHID",batchID);
				staffTransferLog.put("TRANSFER_TABLES", "TF_F_INFO_INSTANCE");
				staffTransferLog.put("TRANSFER_COLUMN1", "INST_ID");
				staffTransferLog.put("TRANSFER_PRIMARYKEY1", infoData.getString("INST_ID"));
				staffTransferLog.put("IBSYSID", SeqMgr.getSubIbsysId());
				staffTransferLog.put("UPDATE_COLUMNKEY1", "RECE_OBJ");
				staffTransferLog.put("UPDATE_OLDCOLUMNVAL1", oldStaffId);
				staffTransferLog.put("UPDATE_NEWCOLUMNVAL1", newStaff);
				staffTransferLog.put("ACCEPT_DATE", update_time);
				staffTransferLog.put("UPDATE_DATE", update_time);
				staffTransferLog.put("REMARK", remark);
				staffTransferLog.put("TYPE_ID", typeId);
				staffTransferLogList.add(staffTransferLog);
	        }
			
			int[] infoListFlag=OpTaskInfoQry.updStaffForInfo(infoList);
			
			for(int j:infoListFlag){
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量更新TF_F_INFO_INSTANCE时异常，已自动停止");
				}
			}
		}
		
		
		if(IDataUtil.isNotEmpty(staffTransferLogList)&&staffTransferLogList.size()>0){
			int[] staffTransFlag=StaffTransferBean.insertStaffTransfer(staffTransferLogList);
			for(int j:staffTransFlag){
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量新增TF_B_EOP_STAFFTRANSFERLOG时异常，已自动停止");
				}
			}
		}

		
		
		return null;
	}

}
