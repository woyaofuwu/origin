
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHModemLoseReqData;

/**
 * FTTH光猫退还
 * @author Administrator
 *
 */
public class BuildFTTHModemLoseReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	FTTHModemLoseReqData fttRd = (FTTHModemLoseReqData)brd;
    	fttRd.setInstId(param.getString("INST_ID"));
    	fttRd.setModermId(param.getString("MODERM_ID",""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FTTHModemLoseReqData();
    }

}
