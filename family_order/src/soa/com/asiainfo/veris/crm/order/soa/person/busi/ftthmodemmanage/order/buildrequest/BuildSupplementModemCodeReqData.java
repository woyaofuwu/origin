
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.SupplementModemCodeReqData;

/**
 * FTTH光猫补录
 * @author Administrator
 *
 */
public class BuildSupplementModemCodeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	SupplementModemCodeReqData fttRd = (SupplementModemCodeReqData)brd;
    	fttRd.setInstId(param.getString("INST_ID"));
    	fttRd.setModemId(param.getString("MODEM_ID",""));
    	fttRd.setModemType(param.getString("MODEM_TYPE",""));
    	fttRd.setSupplementType(param.getString("SUPPLEMENT_TYPE",""));
    	fttRd.setOperType(param.getString("OPER_TYPE",""));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SupplementModemCodeReqData();
    }

}
