package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 **电子档案统计
 * @author xieqj
 * @Date 2019年12月23日
 */
public abstract class ElecAgreementReportForm extends EopBasePage {

	/**
	 * 入网协议对应的产品id （不存在实质的关联产品）
	 */
	private static final String NET_SERVICE_PRODUCT_ID = "-1";
	 
	 /**
     * 电子协议报表查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryElecAgreementList(IRequestCycle cycle) throws Exception {

        IData queryCondition = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.AgreementInfoSVC.elecAgreementSumReportForm", queryCondition, this.getPagination("eleNav"));
        IDataset resultDataset = output.getData();
        
        for (int i = 0, len = resultDataset.size(); i < len; i++) {
        	IData elecAgreement = resultDataset.getData(i);
        	String productId = elecAgreement.getString("PRODUCT_ID");
        	if (!NET_SERVICE_PRODUCT_ID.equals(productId))
        	{
        		String productName =  StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "ELECPRODUCT", productId});
//        		String productName = StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", "DATA_ID", "DATA_NAME", productId);
        		elecAgreement.put("PRODUCT_ID", productName);
			} else {
				elecAgreement.put("PRODUCT_ID", "");
			}
		}

        setCondition(queryCondition);
         
        setCount(output.getDataCount());
        setInfos(resultDataset);
    }
    
    public abstract void setCondition(IData cond) throws Exception;

    public abstract void setInfos(IDataset info) throws Exception;

    public abstract void setCount(long infosCount) throws Exception;
}
