
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.DealPcrfTacticsReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.PcrfTacticData;

public class BuildDealPcrfTactics extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	System.out.println("DealPcrfTacticsReqData  ----param："+param);
    	DealPcrfTacticsReqData prd = (DealPcrfTacticsReqData) brd;
    	String strPcrfReq= param.getString("X_BATPCRFREQ_STR");
    	IDataset batchPcrfReqs = new DatasetList(strPcrfReq);
    	List<PcrfTacticData> pcrfList = new ArrayList<PcrfTacticData>();
    	if (batchPcrfReqs != null && !batchPcrfReqs.isEmpty()) 
    	{
		    for (int i = 0; i < batchPcrfReqs.size(); i++) {
		    	IData batchPcrfReq = batchPcrfReqs.getData(i);
		    	PcrfTacticData pcrfData = new PcrfTacticData();
		    	pcrfData.setInstId(batchPcrfReq.getString("INST_ID"));
		    	pcrfData.setRelaInstId(batchPcrfReq.getString("RELA_INST_ID"));
		    	pcrfData.setServiceCode(batchPcrfReq.getString("SERVICE_CODE"));
		    	pcrfData.setBillingType(batchPcrfReq.getString("BILLING_TYPE"));
		    	pcrfData.setUsageState(batchPcrfReq.getString("USAGE_STATE"));
		    	pcrfData.setStartDate(batchPcrfReq.getString("START_DATE"));
		    	pcrfData.setEndDate(batchPcrfReq.getString("END_DATE"));
		    	pcrfData.setModifyTag(batchPcrfReq.getString("MODIFY_TAG"));
		    	pcrfList.add(pcrfData);
		    }  
		    prd.setPcrfList(pcrfList);
    	}
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new DealPcrfTacticsReqData();
    }

}
