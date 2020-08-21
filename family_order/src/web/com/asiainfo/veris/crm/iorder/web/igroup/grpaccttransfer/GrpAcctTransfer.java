
package com.asiainfo.veris.crm.iorder.web.igroup.grpaccttransfer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.group.payplanedit.PayPlanEditView;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attrbiz.AttrBizInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpAcctTransfer extends GroupBasePage
{
	
	/**
	 * 没用到
	 * 根据group_id查询集团基本信息 默认传入为cond_GROUP_ID
	 * @param cycle
	 * @throws Throwable
	 */
	public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
	{
		setGroupInfo(queryGroupCustInfo(cycle));
		setCondition(getData("cond", true));
	}
	
	/**
	 * 查询集团产品相关信息
	 * @param cycle
	 * @throws Throwable
	 */
	public void queryProductInfo(IRequestCycle cycle) throws Throwable
	{
		String custid = getData().getString("CUST_ID", "");
		
        IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custid, false);
       
        setCustInfo(result);
		
	}
	
	/**
	 * 查询集团账户信息
	 * @param cycle
	 * @throws Throwable
	 */
	public void queryAcctInfo(IRequestCycle cycle) throws Throwable
	{
		String user_id = getData().getString("USER_ID");
		setInfo(UCAInfoIntfViewUtil.qryGrpDefAcctInfoByUserId(this, user_id, false));
	}
	
	/**
	 * 作用：ajax方式查
	 */
	public void getProductInfoByAjax(IRequestCycle cycle) throws Throwable
	{
		
		String productid = getData().getString("PRODUCT_ID", "");
		String userid = getData().getString("USER_ID", "");

		//判断该产品是否支持变更付费计划操作
		IDataset productCtrl = AttrBizInfoIntf.qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(this, productid, "P", "ModUs");
		if(IDataUtil.isEmpty(productCtrl))
		{
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "该产品暂不支持付费计划变更操作");
		}
		
		// 集团付费计划定制
		IData resultData = PayPlanEditView.renderPayPlanEditInfo(this, productid, userid);
		
		if (IDataUtil.isEmpty(resultData))
            return;

        IDataset payPanSrc = resultData.getDataset("PAYPLAN_SRC");
        IDataset payPanList = resultData.getDataset("PAYPLAN_LIST");
        setPayPlans(payPanSrc);
        setPayPlanList(payPanList);

		setCondition(getData());
		
		//界面互联网化,优化界面,多展示一个集团账目列表的数量以及详细信息,本来挺简单的界面,强行复杂一波,我真的是日了狗了,好烦
		IDataset grpItems = new DatasetList();
		                                             
		IDataset payItems = CommParaInfoIntfViewUtil.qryPayItemsParamByGrpProductId(this, productid); // 可选择的列表
		if (IDataUtil.isNotEmpty(payItems))
        {
            // 付费账目描述
            for (int i = 0; i < payItems.size(); i++)
            {
                IData item = payItems.getData(i);

                String itemDesc = item.getString("NOTE_ITEM", "") + "(" + item.getString("PARA_CODE1", "") + ")";
                item.put("ITEM_DESC", itemDesc);
                grpItems.add(item);
            }
        }
		setGrpItems(grpItems);
	}
	
	
	public abstract void setGrpItems(IDataset grpItems);
	
    public abstract void setPayPlanList(IDataset payPlanList);

    public abstract void setPayPlans(IDataset payPlans);
	
	public void submitChange(IRequestCycle cycle) throws Exception
	{
		String payplanString = getData().getString("PAYPLAN_INFOS", "[]");
        IDataset payPlan = new DatasetList(payplanString);
        IData svcData = new DataMap();
        svcData.put("USER_ID", getData().getString("USER_ID", ""));
        svcData.put("PRODUCT_ID", getData().getString("PRODUCT_ID", ""));
        svcData.put("REMARK", getData().getString("param_REMARK", ""));
        svcData.put(Route.USER_EPARCHY_CODE, getData().getString("GRP_USER_EPARCHYCODE"));
        svcData.put("PLAN_INFO", payPlan);
        IDataset result = CSViewCall.call(this, "SS.ModifyUserDataSVC.crtTrade", svcData);
        this.setAjax(result);
	}
	
	public abstract void setCustInfo(IData custInfo);
    
	public abstract void setAcctInfo(IData acctInfo);// 账户信息

	public abstract void setCompProductInfo(IData compProductInfo);// 组合产品信息

	public abstract void setCondition(IData condition);

	public abstract void setGroupInfo(IData groupInfo);

	public abstract void setInfo(IData info);

	public abstract void setPaydata(IData paydata); // 付费信息

	public abstract void setProductInfos(IDataset productInfo); // 产品信息
	
	public abstract IData getInfo();
	
	
	
	
	
	
	

	/**
	 * 作用：页面的初始化
	 * 
	 * @author luojh 2009-07-29
	 * @param cycle
	 * @throws Throwable
	 */
	public void initial(IRequestCycle cycle) throws Throwable
	{
		IData data = getData();
		String cust_id = data.getString("CUST_ID","");
		if(StringUtils.isBlank(cust_id)){
			return;
		}

		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		IDataset dataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getGrpProductByGrpCustIdProID", param);
		
		if(IDataUtil.isNotEmpty(dataset)){
			for(int i=0;i<dataset.size();i++){
				IData productInfo = dataset.getData(i);
				IData inparam = new DataMap();
				inparam.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
				IDataset productTypeDataset = CSViewCall.call(this, "CS.GroupCatalogsQrySVC.getProductTypesByProductId", inparam);
				if(IDataUtil.isEmpty(productTypeDataset)){
					dataset.remove(i);
					i--;
				}
			}
		}

		setProductInfos(dataset);

	}

}

