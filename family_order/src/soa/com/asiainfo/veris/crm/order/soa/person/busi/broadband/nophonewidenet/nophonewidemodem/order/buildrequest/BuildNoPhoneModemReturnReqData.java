package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.buildrequest;
  
import com.ailk.common.data.IData; 
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemApplyReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemChangeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemReturnReqData;

/**
 * FTTH光猫退还
 * @author Administrator
 *
 */
public class BuildNoPhoneModemReturnReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	NoPhoneModemReturnReqData fttRd = (NoPhoneModemReturnReqData)brd;
    	fttRd.setInstId(param.getString("INST_ID"));
    	fttRd.setModermId(param.getString("MODERM_ID",""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneModemReturnReqData();
    }

}
