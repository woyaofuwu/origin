package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.buildrequest;
 
import com.ailk.common.data.IData; 
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemApplyReqData;
public class BuildNoPhoneModemApplyReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
    	NoPhoneModemApplyReqData fttRd = (NoPhoneModemApplyReqData) brd;
    	fttRd.setRemark(param.getString("REMARK"));
    	fttRd.setApply_type(param.getString("APPLY_TYPE",""));
    	fttRd.setDeposit(param.getString("DEPOSIT",""));
    	fttRd.setModermId(param.getString("MODERMID",""));
    	fttRd.setModermType(param.getString("MODERMTYPE",""));
    	fttRd.setReturnDate(param.getString("RETURN_DATE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneModemApplyReqData();
    }

}
