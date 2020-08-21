package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.contract;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * 
 * @ClassName ContractAndProdQuery
 * @Description 集团合同组件 
 * @author zhouchao5
 * @date 2017年2月18日 下午12:50:06 
 *
 */
public abstract class ContractAndProdQuery extends BizTempComponent
{
    public abstract void setContractInfos(IDataset dataset);

    public abstract void setProductInfos(IDataset dataset);

    public abstract String getCustId();

    public abstract String getContractId();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        // 逻辑
        String flag = cycle.getRequestContext().getParameter("flag");
        if(!StringUtils.isEmpty(flag)&&flag.equals("back")){
            String custId = cycle.getRequestContext().getParameter("custId");
            String contractId = cycle.getRequestContext().getParameter("contractId");
            if (custId != null && contractId == null)
            {
                queryConctractInfo(custId);
            }
        }else{
            // 添加js
            boolean isAjax = isAjaxServiceReuqest(cycle);
            if (isAjax)
            {
                includeScript(writer, "scripts/iorder/icsserv/component/enterprise/contract/ContractAndProdQuery.js", false, false);
            }
            else
            {
                getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/contract/ContractAndProdQuery.js", false, false);
                
            }
        }
    }

    /**使用custId查询合同信息
     * @param custId
     * @throws Exception
     */
    public void queryConctractInfo(String custId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        IDataset allContractInfo =  CSViewCall.call(this,"CS.CustContractInfoQrySVC.qryContractByCustIdForGrp", data);

        boolean hasContract = false;
        if (DataUtils.isNotEmpty(allContractInfo))
        {
            for (int i = 0, size = allContractInfo.size(); i < size; i++)
            {
                IData contractData = allContractInfo.getData(i);
                String contractId = contractData.getString("CONTRACT_ID");
                if (StringUtils.isNotEmpty(contractId))
                {
                	IDataset productInfos = queryProductInfo(custId, contractId);
                	if(productInfos != null && productInfos.size() > 0){
                		StringBuffer offerIds = new StringBuffer();
                		for(int j=0;j<productInfos.size();j++){
                			if(j == 0){
                				offerIds.append(productInfos.getData(j).getString("PRODUCT_ID"));
                			}else{
                				offerIds.append(","+productInfos.getData(j).getString("PRODUCT_ID"));
                			}
                		}
                		contractData.put("OFFER_IDS", offerIds);
                        contractData.put("SEARCH_OFFER_IDS", offerIds.toString());
                	}
                    contractData.put("QUERY_PRODUCT_INFO", productInfos);
                }
            }
            hasContract = true;
        }
        this.setContractInfos(allContractInfo);

        IData ajaxData = new DataMap();
        ajaxData.put("HAS_CONTRACT", hasContract);
        ajaxData.put("CHOOSE_CONTRACT_LIST",allContractInfo);
        this.getPage().setAjax(ajaxData);

    }
    
 

    /**使用custId,contractId查询合同信息
     * @param custId
     * @param contractId
     * @return
     * @throws Exception
     */
    public IDataset queryProductInfo(String custId, String contractId) throws Exception
    {
        IData data = new DataMap();
        data.put("CUST_ID", custId);
        data.put("CONTRACT_ID", contractId);
        IDataset allProductInfo = CSViewCall.call(this,"CS.CustContractProductInfoQrySVC.qryContractProductByContIdForGrp", data);
        if(allProductInfo == null){
        	allProductInfo = new DatasetList();
        }
        this.setProductInfos(allProductInfo);
        return allProductInfo;
    }
}
