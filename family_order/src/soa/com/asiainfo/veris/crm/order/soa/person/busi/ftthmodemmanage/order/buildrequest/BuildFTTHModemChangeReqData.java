
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHModemChangeReqData;

/**
 * FTTH光猫退还
 * @author Administrator
 *
 */
public class BuildFTTHModemChangeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	FTTHModemChangeReqData fttRd = (FTTHModemChangeReqData) brd;
    	fttRd.setInstId(param.getString("INST_ID"));
    	fttRd.setOldModermId(param.getString("MODERM_ID",""));
    	fttRd.setNewModermId(param.getString("NEW_MODERM_ID"));
    	fttRd.setNewModermType(param.getString("NEW_MODERM_TYPE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FTTHModemChangeReqData();
    }

}
