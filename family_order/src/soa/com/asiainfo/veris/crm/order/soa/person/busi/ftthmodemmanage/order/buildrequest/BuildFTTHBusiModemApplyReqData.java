
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHBusiModemApplyReqData;

/**
 * FTTH商务光猫申领
 * @author Administrator
 *
 */
public class BuildFTTHBusiModemApplyReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	FTTHBusiModemApplyReqData ftth=(FTTHBusiModemApplyReqData)brd;  
//    	ftth.setKDNumber(param.getString("KD_NUMBER",""));
//    	ftth.setKDUserId(param.getString("KD_USERID",""));
    	
    	IDataset sets = new DatasetList(param.getString("FTTH_DATASET"));
    	ftth.setMemberList(sets);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FTTHBusiModemApplyReqData();
    }

}
