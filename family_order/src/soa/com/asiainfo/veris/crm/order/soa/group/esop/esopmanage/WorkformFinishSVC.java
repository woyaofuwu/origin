package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;

public class WorkformFinishSVC extends GroupOrderService
{
	private static final long serialVersionUID = -3923226370322319051L;
	
	public IDataset finish(IData inparam) throws Exception
	{
		String ibsysid = inparam.getString("IBSYSID", "");
		String state = inparam.getString("STATE", "");
		IDataset dataset = new DatasetList();
		//流程订单表
		dealWorkformSubscribe(ibsysid, state);
		
		//节点工单信息表
		dealWorkformNode(ibsysid);
		
		//定单产品信息
		dealWorkformProduct(ibsysid);
		
		//流程暂存表
		dealWorkformPageSave(ibsysid);
		
		//流程业务控制表
		dealWorkformOther(ibsysid);
		
		//流程业务资费表()
		dealWorkformDis(ibsysid);
		
		//流程业务服务表
		dealWorkformSvc(ibsysid);
		
		//流程业务数据表
		dealWorkformAttr(ibsysid);
		
		//流程附件表
		dealWorkformAttach(ibsysid);
		
		//EOMS SI工单基本信息表
		dealWorkformEoms(ibsysid);
		
		//EomsState
		dealWorkformEomsState(ibsysid);
		
		//关联流程信息变动表
		dealWorkformModiTrace(ibsysid);
		
		//快速办理产品受理数据表TF_B_EOP_QUICKORDER_COND
		dealWorkformQuickorderCond(ibsysid);
		
		//快速办理产品页面数据TF_B_EOP_QUICKORDER_DATA
		dealWorkformQuickorderData(ibsysid);
		
		//快速办理产品成员页面数据表TF_B_EOP_QUICKORDER_MEB
		dealWorkformQuickorderMeb(ibsysid);
		
		return dataset;
	}
	
	private void dealWorkformEoms(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformEomss = WorkformEomsBean.qryEomsByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformEomss))
		{
			WorkformEomsBean.delEomsByIbsysid(ibsysid);
			WorkformEomsHBean.insertWorkformEoms(workformEomss);
		}
	}
	
	private void dealWorkformEomsState(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformEomsStates = WorkformEomsStateBean.qryEomsStateByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformEomsStates))
		{
			WorkformEomsStateBean.delEomsByIbsysid(ibsysid);
			WorkformEomsStateHBean.insertWorkformEoms(workformEomsStates);
		}
	}
	
	private void dealWorkformAttach(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformAttachs = WorkformAttachBean.qryAttachByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformAttachs))
		{
			WorkformAttachBean.delAttachByIbsysid(ibsysid);
			WorkformAttachHBean.insertWorkformAttachH(workformAttachs);
		}
	}
	
	private void dealWorkformAttr(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformAttrs = WorkformAttrBean.qryAttrByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformAttrs))
		{
			WorkformAttrBean.delAttrByIbsysid(ibsysid);
			WorkformAttrHBean.insertWorkformAttrH(workformAttrs);
		}
	}
	
	private void dealWorkformSvc(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformSvcs = WorkformSvcBean.qrySvcByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformSvcs))
		{
			WorkformSvcBean.delSvcByIbsysid(ibsysid);
			WorkformSvcHBean.insertWorkformSvcH(workformSvcs);
		}
	}
	
	private void dealWorkformDis(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformDiss = WorkformDisBean.qryDisByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformDiss))
		{
			WorkformDisBean.delDisByIbsysid(ibsysid);
			WorkformDisHBean.insertWorkformDisH(workformDiss);
		}
	}
	
	private void dealWorkformOther(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformOthers = WorkformOtherBean.qryOtherByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformOthers))
		{
			WorkformOtherBean.delOtherByIbsysid(ibsysid);
			WorkformOtherHBean.insertWorkformOtherH(workformOthers);
		}
	}
	
	private void dealWorkformPageSave(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformPageSaves = WorkformPageSaveBean.qryPageSaveByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformPageSaves))
		{
			WorkformPageSaveBean.delPageSaveByIbsysid(ibsysid);
			WorkformPageSaveHBean.insertWorkformPageSaveH(workformPageSaves);
		}
	}
	
	private void dealWorkformProduct(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformProducts = WorkformProductBean.qryProductByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformProducts))
		{
			WorkformProductBean.delProductByIbsysid(ibsysid);
			WorkformProductHBean.insertWorkformProductH(workformProducts);
		}
		
		//查询PRODUCT_SUB数据
		IDataset workformProductSubs = WorkformProductSubBean.qryProductByIbsysid(ibsysid);
		if (DataUtils.isNotEmpty(workformProductSubs)) {
			WorkformProductSubBean.delProductByIbsysid(ibsysid);
			WorkformProductSubHBean.insertWorkformProductH(workformProductSubs);
		}
		
		//查询PRODUCT_EXT数据
		IDataset workformProductExts = WorkformProductExtBean.qryProductByIbsysid(ibsysid);
        if (DataUtils.isNotEmpty(workformProductExts)) {
            WorkformProductExtBean.delProductByIbsysid(ibsysid);
            WorkformProductExtHBean.insertWorkformProductH(workformProductExts);
        }
	}
	
	private void dealWorkformNode(String ibsysid) throws Exception
	{
		//查询数据
		IDataset workformNodes = WorkformNodeBean.qryWorkformNodeByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformNodes))
		{
			WorkformNodeBean.delWorkformNodeByIbsysid(ibsysid);
			WorkformNodeHBean.insertWorkformNodeH(workformNodes);
		}
	}
	
	private void dealWorkformSubscribe(String ibsysid, String state) throws Exception
	{
		//查询数据
		IDataset workformSubscribes = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
		if(DataUtils.isNotEmpty(workformSubscribes))
		{
			WorkformSubscribeBean.delWorkformSubscribeByIbsysid(ibsysid);
			IData workformSubscribe = workformSubscribes.first();
			if(StringUtils.isNotEmpty(state))
			{
				workformSubscribe.put("DEAL_STATE", state);
			}
			WorkformSubscribeHBean.insertWorkformSubscribeH(workformSubscribe);
		}
	}
	
	private void dealWorkformModiTrace(String mainIbsysid) throws Exception
	{
		IDataset  workformModiTraceInfos = WorkformModiTraceBean.qryModiTraceInfos(mainIbsysid);
		if(DataUtils.isNotEmpty(workformModiTraceInfos))
		{
			WorkformModiTraceBean.delWorkformModiTrace(mainIbsysid);
			WorkformModiTraceHBean.insertWorkformTrace(workformModiTraceInfos);
		}
	}
	
	private void dealWorkformQuickorderCond(String ibsysid) throws Exception
    {
        IDataset  quickorderCondInfos = QuickOrderCondBean.qryQuickorderCondInfos(ibsysid);
        if(DataUtils.isNotEmpty(quickorderCondInfos))
        {
            QuickOrderCondBean.delQuickorderCond(ibsysid);
            QuickOrderCondHBean.insertWorkformQuickOrderCondH(quickorderCondInfos);
        }
    }
	
	private void dealWorkformQuickorderData(String ibsysid) throws Exception
    {
        IDataset  quickorderDataInfos = QuickOrderDataBean.qryWorkformQuickOrderDataInfos(ibsysid);
        if(DataUtils.isNotEmpty(quickorderDataInfos))
        {
            QuickOrderDataBean.delWorkformQuickOrderData(ibsysid);
            QuickOrderDataHBean.insertWorkformQuickOrderDataH(quickorderDataInfos);
        }
    }
	
	private void dealWorkformQuickorderMeb(String ibsysid) throws Exception
    {
        IDataset  quickorderMebInfos = QuickOrderMebBean.qryWorkformQuickOrderMebInfos(ibsysid);
        if(DataUtils.isNotEmpty(quickorderMebInfos))
        {
            QuickOrderMebBean.delWorkformQuickOrderMeb(ibsysid);
            QuickOrderMebHBean.insertWorkformQuickOrderMebH(quickorderMebInfos);
        }
    }
}
