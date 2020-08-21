package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ScrData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;

public class WorkformRegisterSVC extends GroupOrderService
{
	private static final long serialVersionUID = -3923226370322319051L;
	
	private ScrData srcData = new ScrData();
	
	private IData eosCom = new DataMap();
	
	private String REG_SVC = "SS.WorkformRegSVC.execute";
	
	private String DRIVE_SVC = "SS.WorkformDriveSVC.execute";
	
	public IDataset register(IData inparam) throws Exception
	{
		//IDataset dataset  = new DatasetList();
		//esop数据生成前校验
        checkEosDataBefore(inparam);
        
        //esop数据处理
        saveEosData();
        
        //esop数据生成后处理
        checkEosDataAfter(inparam);
        
        //发送esop数据给外系统
        IDataset dataset = sendEosMessage(inparam);
        //IData data = new DataMap("{\"IBSYSID\":\"1234567890\",\"ASSIGN_FLAG\":\"false\"}");
//        IData data = new DataMap(inparam);
//        data.put("IBSYSID", eosCom.getString("IBSYSID"));
//        data.put("ASSIGN_FLAG", "false");
//        dataset.add(data);
        
        return dataset;
	}
	
	private void checkEosDataBefore(IData inparam) throws Exception
	{
		if(StringUtils.isNotEmpty(inparam.getString("EOSAttr", "")))
		{
			srcData = new ScrData(inparam.getString("EOSAttr", ""));
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务数据获取失败！");
		}
		
		if(StringUtils.isNotEmpty(inparam.getString("EOSCom", "")))
		{
			eosCom = new DataMap(inparam.getString("EOSCom", ""));
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务公共数据获取失败！");
		}
		
		//  专线建设成员计费号码重复校验
		String ibsysid = eosCom.getString("IBSYSID", "");
		String bpmTempletID = eosCom.getString("BPM_TEMPLET_ID");
		if (StringUtils.isNotEmpty(ibsysid)) {
			IDataset eopSubscribeDataset = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
			if (IDataUtil.isEmpty(eopSubscribeDataset)) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID"+ibsysid+"查询TF_B_EOP_SUBSCRIBE为空");
			}
			
			String grpSn = eopSubscribeDataset.first().getString("RSRV_STR6"); 
			if ("EDIRECTLINEOPENNEW".equals(bpmTempletID)) {
				IDataset workformProductInfos = srcData.getWorkformProduct();
				
				if(DataUtils.isEmpty(workformProductInfos))
				{
					return;
				}
				
				String mebSn = "";
				for(int i = 0 ; i < workformProductInfos.size() ; i ++)
				{
					IData workformProductInfo = workformProductInfos.getData(i);
					String serialNumber = workformProductInfo.getString("SERIAL_NUMBER");
					String recordNum = workformProductInfo.getString("RECORD_NUM");
					if("0".equals(recordNum))
		            {
		                continue;
		            }
					
					IDataset product = WorkformProductBean.queryProInfoBySn(serialNumber);
					if (IDataUtil.isNotEmpty(product)) {
						int grpNum = grpSn.length();
						int num = 0;
						IDataset dataset = WorkformProductBean.queryMaxSerialMumberByGrpSn(grpSn, grpNum);
						String maxMemSubSn = dataset.getData(0).getString("MAXSN");
						
						int no = Integer.parseInt(recordNum);
						
						String maxMemNo = maxMemSubSn.substring(grpNum, maxMemSubSn.length());
		    	        if(StringUtils.isNotEmpty(maxMemNo)){
		    	        	num = Integer.parseInt(maxMemNo);
		    	            num = num + no;
		    	        }else{
		    	        	num = no ;
		    	        }
		    	        
		    	        if (num < 1000)
		                {
		    	        	mebSn = grpSn + num;
		                }
		                if (num < 100)
		                {
		                	mebSn = grpSn + "0" + num;
		                }
		                if (num < 10)
		                {
		                	mebSn = grpSn + "00" + num;
		                }
						
						workformProductInfo.put("SERIAL_NUMBER", mebSn);
					}
					
					//设置公共参数
					setCommonInfos(workformProductInfo);
				}
			}
		}
	}
	
	private void saveEosData() throws Exception
	{
		String ibsysid = eosCom.getString("IBSYSID", "");
		boolean flag = false;
		if(StringUtils.isEmpty(ibsysid))
		{
			eosCom.put("SVC_NAME", REG_SVC);
			String ibsysId = SeqMgr.getIbsysId();
			String subIbsysId = SeqMgr.getSubIbsysId();
			eosCom.put("IBSYSID", ibsysId);
			eosCom.put("SUB_IBSYSID", subIbsysId);
			eosCom.put("DEAL_STATE", "0");
			flag = true;
		}
		else
		{
			eosCom.put("SVC_NAME", DRIVE_SVC);
			String subIbsysId = SeqMgr.getSubIbsysId();
			eosCom.put("SUB_IBSYSID", subIbsysId);
		}
		
		if(flag)
		{
			//存储流程订单表
			saveWorkformSubscribe();
		}
		//存储节点工单信息表
		saveWorkformNode();
		
		//存储定单产品信息表
		saveWorkformProduct();
		
		//存储定单产品信息子表
		saveWorkformProductSub();
		
		//存储流程业务信息表
		saveWorkformAttr();
		
		//存储流程业务资费表
		saveWorkformDis();
		
		//存储流程业务服务表
		saveWorkformSvc();
		
		//存储流程业务控制表
		saveWorkformOther();
		
		//存储流程附件表
		saveWorkformAttach();
		
		//存储EOMS SI工单基本信息表
		saveWorkformEoms();
		
		//快速办理产品受理数据表(海南政企一单清/快速办理新增表)
		saveWorkformQuickOrderCond();

        //快速办理产品受理数据表(海南政企一单清/快速办理新增表)
		saveWorkformQuickOrderMeb();

        //快速办理产品受理数据表(海南政企一单清/快速办理新增表)
		saveWorkformQuickOrderData();

        //快速办理产品受理数据表(海南政企一单清/快速办理新增表)   存储成员 产品 表
        saveWorkformProductExt();
	}
	
	private void saveWorkformEoms() throws Exception
	{
		IData workformEoms = srcData.getWorkformEoms();
		if(DataUtils.isNotEmpty(workformEoms))
		{
			workformEoms.put("IBSYSID", eosCom.getString("IBSYSID", ""));
			workformEoms.put("SUB_IBSYSID", eosCom.getString("SUB_IBSYSID", ""));
			workformEoms.put("MONTH", SysDateMgr.getCurMonth());
			workformEoms.put("BPM_TEMPLET_ID", eosCom.getString("BPM_TEMPLET_ID", ""));
			workformEoms.put("NODE_ID", eosCom.getString("NODE_ID", ""));
			workformEoms.put("PRODUCT_ID", eosCom.getString("PRODUCT_ID", ""));
			workformEoms.put("PRODUCT_NAME", eosCom.getString("OFFER_NAME", ""));
			//设置公共参数
			setCommonInfos(workformEoms);
			
			WorkformEomsBean.insertWorkformEoms(workformEoms);
		}
	}

	private void saveWorkformAttach() throws Exception
	{
		IDataset workformAttachs = srcData.getWorkformAttach();
		String nodeId = eosCom.getString("NODE_ID", "");
		String ibsysid = eosCom.getString("IBSYSID", "");
		String subIbsysid = eosCom.getString("SUB_IBSYSID", "");
		
		if(DataUtils.isEmpty(workformAttachs))
		{
			return;
		}
		
		for(int i = 0 ; i < workformAttachs.size() ; i ++)
		{
			IData workformAttach = workformAttachs.getData(i);
			workformAttach.put("SUB_IBSYSID", subIbsysid);
			workformAttach.put("IBSYSID", ibsysid);
			workformAttach.put("SEQ", SeqMgr.getAttrSeq());
			workformAttach.put("NODE_ID", nodeId);
			workformAttach.put("VALID_TAG", EcEsopConstants.STATE_VALID);
			workformAttach.put("ATTACH_CITY_CODE", getVisit().getCityCode());
			workformAttach.put("ATTACH_EPARCHY_CODE", getVisit().getLoginEparchyCode());
			workformAttach.put("ATTACH_DEPART_ID", getVisit().getDepartId());
			workformAttach.put("ATTACH_DEPART_NAME", getVisit().getDepartName());
			workformAttach.put("ATTACH_STAFF_ID", getVisit().getStaffId());
			workformAttach.put("ATTACH_STAFF_NAME", getVisit().getStaffName());
			workformAttach.put("ATTACH_STAFF_PHONE", getVisit().getSerialNumber());
			//设置公共参数
			setCommonInfos(workformAttach);
		}
		
		WorkformAttachBean.insertWorkformAttach(workformAttachs);
	}
	
	private void saveWorkformOther() throws Exception
	{
		IDataset workformOthers = srcData.getWorkformOther();
		String nodeId = eosCom.getString("NODE_ID", "");
		String ibsysid = eosCom.getString("IBSYSID", "");
		String subIbsysid = eosCom.getString("SUB_IBSYSID", "");
		
		if(DataUtils.isEmpty(workformOthers))
		{
			return;
		}
		
		for(int i = 0 ; i < workformOthers.size() ; i ++)
		{
			IData workformOther = workformOthers.getData(i);
			workformOther.put("SUB_IBSYSID", subIbsysid);
			workformOther.put("IBSYSID", ibsysid);
			workformOther.put("SEQ", SeqMgr.getAttrSeq());
			workformOther.put("GROUP_SEQ", 0);
			workformOther.put("NODE_ID", nodeId);
			//设置公共参数
			setCommonInfos(workformOther);
		}
		
		WorkformOtherBean.insertWorkformOther(workformOthers);
	}
	
	private void saveWorkformSvc() throws Exception
	{
		IDataset workformSvcs = srcData.getWorkformSvc();
		String nodeId = eosCom.getString("NODE_ID", "");
		String ibsysid = eosCom.getString("IBSYSID", "");
		String subIbsysid = eosCom.getString("SUB_IBSYSID", "");
		
		if(DataUtils.isEmpty(workformSvcs))
		{
			return;
		}
		
		for(int i = 0 ; i < workformSvcs.size() ; i ++)
		{
			IData workformSvc = workformSvcs.getData(i);
			workformSvc.put("SUB_IBSYSID", subIbsysid);
			workformSvc.put("IBSYSID", ibsysid);
			workformSvc.put("SEQ", SeqMgr.getAttrSeq());
			workformSvc.put("NODE_ID", nodeId);
			//设置公共参数
			setCommonInfos(workformSvc);
		}
		
		WorkformSvcBean.insertWorkformSvc(workformSvcs);
	}
	
	private void saveWorkformDis() throws Exception
	{
		IDataset workformDiss = srcData.getWorkformDis();
		String nodeId = eosCom.getString("NODE_ID", "");
		String ibsysid = eosCom.getString("IBSYSID", "");
		String subIbsysid = eosCom.getString("SUB_IBSYSID", "");
		
		if(DataUtils.isEmpty(workformDiss))
		{
			return;
		}
		
		for(int i = 0 ; i < workformDiss.size() ; i ++)
		{
			IData workformDis = workformDiss.getData(i);
			workformDis.put("SUB_IBSYSID", subIbsysid);
			workformDis.put("IBSYSID", ibsysid);
			workformDis.put("SEQ", SeqMgr.getAttrSeq());
			workformDis.put("NODE_ID", nodeId);
			//设置公共参数
			setCommonInfos(workformDis);
		}
		
		WorkformDisBean.insertWorkformDis(workformDiss);
	}
	
	private void saveWorkformAttr() throws Exception
	{
		IDataset workformAttrs = srcData.getWorkformAttr();
		String nodeId = eosCom.getString("NODE_ID", "");
		String ibsysid = eosCom.getString("IBSYSID", "");
		String subIbsysid = eosCom.getString("SUB_IBSYSID", "");
		
		if(DataUtils.isEmpty(workformAttrs))
		{
			return;
		}
		
		for(int i = 0 ; i < workformAttrs.size() ; i ++)
		{
			IData workformAttr = workformAttrs.getData(i);
			workformAttr.put("SUB_IBSYSID", subIbsysid);
			workformAttr.put("IBSYSID", ibsysid);
			workformAttr.put("SEQ", SeqMgr.getAttrSeq());
            workformAttr.put("GROUP_SEQ", "0");
            workformAttr.put("NODE_ID", nodeId);
            
            String attrCode = workformAttr.getString("ATTR_CODE");
            String attrValue = workformAttr.getString("ATTR_VALUE");
            if ("serviceLevel".equals(attrCode)) {
            	if (StringUtils.isNotEmpty(attrValue)) {
            		String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ attrCode, attrValue});
            		if (StringUtils.isNotEmpty(value)) {
            			workformAttr.put("ATTR_VALUE", value);
					}else {
						workformAttr.put("ATTR_VALUE", attrValue);
					}
				}else {
					workformAttr.put("ATTR_VALUE", "标");//服务等级为空时，直接置为标准级
				}
				
			}
            
            
			//设置公共参数
			setCommonInfos(workformAttr);
		}
		
		WorkformAttrBean.insertWorkformAttr(workformAttrs);
	}
	
	private void saveWorkformProduct() throws Exception
	{
		IDataset workformProductInfos = srcData.getWorkformProduct();
		String ibsysid = eosCom.getString("IBSYSID", "");
		if(DataUtils.isEmpty(workformProductInfos))
		{
			return;
		}
		
		IDataset productInfos = WorkformProductBean.qryProductByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(productInfos))
		{
			WorkformProductBean.delProductByIbsysid(ibsysid);
		}
		
		for(int i = 0 ; i < workformProductInfos.size() ; i ++)
		{
			IData workformProductInfo = workformProductInfos.getData(i);
			workformProductInfo.put("IBSYSID", ibsysid);
			workformProductInfo.put("VALID_TAG", EcEsopConstants.STATE_VALID);
			//设置公共参数
			setCommonInfos(workformProductInfo);
			
		}
		WorkformProductBean.insertWorkformProduct(workformProductInfos);
	}
	
	private void saveWorkformProductSub() throws Exception
	{
		IDataset workformProductSubInfos = srcData.getWorkformProductSub();
		String ibsysid = eosCom.getString("IBSYSID", "");
		if(DataUtils.isEmpty(workformProductSubInfos))
		{
			return;
		}
		
		IDataset productSubInfos = WorkformProductBean.qryProductSubByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(productSubInfos))
		{
			WorkformProductBean.delProductSubByIbsysid(ibsysid);
		}
		
		for(int i = 0 ; i < workformProductSubInfos.size() ; i ++)
		{
			IData workformProductInfo = workformProductSubInfos.getData(i);
			workformProductInfo.put("IBSYSID", ibsysid);
			workformProductInfo.put("VALID_TAG", EcEsopConstants.STATE_VALID);
			//设置公共参数
			setCommonInfos(workformProductInfo);
			
		}
		WorkformProductBean.insertWorkformProductSub(workformProductSubInfos);
	}
	
	private void saveWorkformNode() throws Exception
	{
		IData workformNode = srcData.getWorkformNode();
		if(DataUtils.isNotEmpty(workformNode))
		{
			workformNode.put("IBSYSID", eosCom.getString("IBSYSID", ""));
			workformNode.put("SUB_IBSYSID", eosCom.getString("SUB_IBSYSID", ""));
			workformNode.put("NODE_ID", eosCom.getString("NODE_ID", ""));
			workformNode.put("PRODUCT_ID", eosCom.getString("BUSI_CODE", ""));
			workformNode.put("WORKFORM_TYPE", eosCom.getString("WORKFORM_TYPE", ""));
			workformNode.put("DEAL_STATE", eosCom.getString("DEAL_STATE", ""));
			//设置公共参数
			setCommonInfos(workformNode);

			WorkformNodeBean.insertWorkformNode(workformNode);
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务工单节点数据获取失败！");
		}
	}
	
	private void saveWorkformSubscribe() throws Exception
	{
		IData workformSubscribe = srcData.getWorkformSubscribe();
		if(DataUtils.isNotEmpty(workformSubscribe))
		{
			workformSubscribe.put("IBSYSID", eosCom.getString("IBSYSID", ""));
			workformSubscribe.put("BPM_TEMPLET_ID", eosCom.getString("BPM_TEMPLET_ID", ""));
			workformSubscribe.put("IN_MODE_CODE", eosCom.getString("IN_MODE_CODE", ""));
			workformSubscribe.put("DEAL_STATE", eosCom.getString("DEAL_STATE", ""));
			workformSubscribe.put("BUSI_CODE", eosCom.getString("BUSI_CODE", ""));
			workformSubscribe.put("BUSI_TYPE", eosCom.getString("BUSI_TYPE", EcEsopConstants.DEFAULT_BUSI_TYPE));
			//设置公共参数
			setCommonInfos(workformSubscribe);
			
			WorkformSubscribeBean.insertWorkformSubscribe(workformSubscribe);
		}
		else
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务工单数据获取失败！");
		}
	}
	
	/**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     */
	private void saveWorkformQuickOrderCond() throws Exception
	{
	    IDataset workformQuickOrderCondList = srcData.getWorkformQuickOrderCond();

        
        if(DataUtils.isEmpty(workformQuickOrderCondList))
        {
            return ;
        }

        String nodeId = eosCom.getString("NODE_ID", "");
        String ibsysid = eosCom.getString("IBSYSID", "");
        String subIbsysid = eosCom.getString("SUB_IBSYSID", "");

        IDataset workformQuickOrderList = new DatasetList();
        for(int i = 0; i < workformQuickOrderCondList.size(); i++)
        {
            IData workformQuickOrderCond = workformQuickOrderCondList.getData(i);
            workformQuickOrderCond.put("SUB_IBSYSID", subIbsysid);
            workformQuickOrderCond.put("IBSYSID", ibsysid);
            workformQuickOrderCond.put("NODE_ID", nodeId);
            
            //设置公共参数
            setCommonInfos(workformQuickOrderCond);

            workformQuickOrderList.add(workformQuickOrderCond);
        }
        QuickOrderCondBean.insertWorkformQuickOrderCond(workformQuickOrderList);
	}

	private void saveWorkformQuickOrderMeb() throws Exception
	{
	    IDataset workformQuickOrderMebList = srcData.getWorkformQuickOrderMeb();
        String nodeId = eosCom.getString("NODE_ID", "");
        String ibsysid = eosCom.getString("IBSYSID", "");
        String subIbsysid = eosCom.getString("SUB_IBSYSID", "");

        if(DataUtils.isEmpty(workformQuickOrderMebList))
        {
            return ;
        }
        IDataset workformQuickOrderList = new DatasetList();
        for(int i = 0; i < workformQuickOrderMebList.size(); i++)
        {
            IData workformQuickOrderMeb = workformQuickOrderMebList.getData(i);
            workformQuickOrderMeb.put("SUB_IBSYSID", subIbsysid);
            workformQuickOrderMeb.put("IBSYSID", ibsysid);
            workformQuickOrderMeb.put("NODE_ID", nodeId);

            //设置公共参数
            setCommonInfos(workformQuickOrderMeb);
            workformQuickOrderList.add(workformQuickOrderMeb);
        }
        QuickOrderMebBean.insertWorkformQuickOrderMeb(workformQuickOrderList);
	}

	private void saveWorkformQuickOrderData() throws Exception
	{
	    IDataset workformQuickOrderDataList = srcData.getWorkformQuickOrderData();
        String nodeId = eosCom.getString("NODE_ID", "");
        String ibsysid = eosCom.getString("IBSYSID", "");
        String subIbsysid = eosCom.getString("SUB_IBSYSID", "");

        if(DataUtils.isEmpty(workformQuickOrderDataList))
        {
            return ;
        }

        IDataset workformQuickOrderList = new DatasetList();
        for(int i = 0; i < workformQuickOrderDataList.size(); i++)
        {
            IData workformQuickOrderData = workformQuickOrderDataList.getData(i);
            workformQuickOrderData.put("SUB_IBSYSID", subIbsysid);
            workformQuickOrderData.put("IBSYSID", ibsysid);
            workformQuickOrderData.put("NODE_ID", nodeId);

            //设置公共参数
            setCommonInfos(workformQuickOrderData);
            workformQuickOrderList.add(workformQuickOrderData);
        }
        QuickOrderDataBean.insertWorkformQuickOrderData(workformQuickOrderList);
	}



	private void saveWorkformProductExt() throws Exception
	{
	    IDataset workformProductExtDataList = srcData.getWorkformProductExt();

        if(DataUtils.isEmpty(workformProductExtDataList))
        {
            return ;
        }

        String nodeId = eosCom.getString("NODE_ID", "");
        String ibsysid = eosCom.getString("IBSYSID", "");
        String subIbsysid = eosCom.getString("SUB_IBSYSID", "");

        IDataset productExtInfos = WorkformProductBean.qryProductExtByIbsysid(ibsysid);
        if(DataUtils.isNotEmpty(productExtInfos))
        {
            WorkformProductBean.delProductExtByIbsysid(ibsysid);
        }

        IDataset workformProductExt = new DatasetList();
        for(int i = 0; i < workformProductExtDataList.size(); i++)
        {
            IData workformProductExtData = workformProductExtDataList.getData(i);
            workformProductExtData.put("SUB_IBSYSID", subIbsysid);
            workformProductExtData.put("IBSYSID", ibsysid);
            workformProductExtData.put("NODE_ID", nodeId);

            //设置公共参数
            setCommonInfos(workformProductExtData);
            workformProductExt.add(workformProductExtData);
        }
        WorkformProductBean.insertWorkformProductExt(workformProductExt);
	}



	private void checkEosDataAfter(IData inparam) throws Exception
	{
		
	}
	
	private IDataset sendEosMessage(IData inparam) throws Exception
	{
		IData data = getCommonInfos();
        String bpmTempletId = eosCom.getString("BPM_TEMPLET_ID");
        IDataset otherInfos = srcData.getWorkformOther();
        //避免提交流程不返回指派URL
        /*if("EDIRECTLINEOPENPBOSS".equals(bpmTempletId) || "MANUALSTOP".equals(bpmTempletId)) {
            otherInfos = eosCom.getDataset("OTHER_INFO");
        } else {
            otherInfos = srcData.getWorkformOther();
        }*/
		IDataset dataset = SendEosUtil.sendEos(data, eosCom, otherInfos);
		if(DataUtils.isNotEmpty(dataset))
		{
			dataset.first().putAll(eosCom);
		}
		return dataset;
	}
	
	private IData getCommonInfos() throws Exception
    {
        IData params = new DataMap();
        params.put("ACCEPT_DATE", SysDateMgr.getSysTime());       //受理时间
        params.put("TRADE_EPARCHY_CODE", getVisit().getLoginEparchyCode());   //受理地州
        params.put("TRADE_STAFF_ID", getVisit().getStaffId());              //受理员工
        params.put("TRADE_DEPART_ID", getVisit().getDepartId());             //受理部门
        params.put("TRADE_CITY_CODE", getVisit().getCityCode());              //受理业务区
        params.put("TRADE_DEPART_NAME", getVisit().getDepartName());
        params.put("TRADE_SERIAL_NUMBER", getVisit().getSerialNumber());
        return params;
    }
	
	private void setCommonInfos(IData params) throws Exception
    {
		String groupId = params.getString("GROUP_ID","");
		String eparchCode = getVisit().getLoginEparchyCode();
		if(StringUtils.isNotEmpty(groupId))
		{
			IData custInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
			if(DataUtils.isNotEmpty(custInfo))
			{
				eparchCode = custInfo.getString("EPARCHY_CODE", "");
			}
		}
		if(StringUtils.isNotEmpty(eparchCode))
		{
			eparchCode = getVisit().getLoginEparchyCode();
		}
		
		params.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		params.put("ACCEPT_TIME", SysDateMgr.getSysTime());
		params.put("UPDATE_TIME", SysDateMgr.getSysTime());
		params.put("INSERT_TIME", SysDateMgr.getSysTime());
		params.put("CITY_CODE", getVisit().getCityCode());
		params.put("EPARCHY_CODE", eparchCode);
		params.put("DEPART_ID", getVisit().getDepartId());
		params.put("DEPART_NAME", getVisit().getDepartName());
		params.put("STAFF_ID", getVisit().getStaffId());
		params.put("STAFF_NAME", getVisit().getStaffName());
		params.put("STAFF_PHONE", getVisit().getSerialNumber());
    }
}
