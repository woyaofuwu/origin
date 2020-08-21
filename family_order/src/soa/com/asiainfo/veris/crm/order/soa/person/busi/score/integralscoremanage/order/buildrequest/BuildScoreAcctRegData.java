
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata.ModifyScoreAcctRegData;

public class BuildScoreAcctRegData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ModifyScoreAcctRegData reqData = (ModifyScoreAcctRegData) brd;

        String PSPT_TYPE_CODE = param.getString("PSPT_TYPE_CODE");
        String PSPT_ID = param.getString("PSPT_ID");
        String EMAIL = param.getString("EMAIL");
        String CONTRACT_PHONE = param.getString("CONTRACT_PHONE");
        String NAME = param.getString("NAME");
        String ADDRESSE = param.getString("ADDRESSE");
        String START_DATE = param.getString("START_DATE");
        String END_DATE = param.getString("END_DATE");
        
        reqData.setPSPT_TYPE_CODE(PSPT_TYPE_CODE);
        reqData.setPSPT_ID(PSPT_ID);
        reqData.setEMAIL(EMAIL);
        reqData.setCONTRACT_PHONE(CONTRACT_PHONE);
        reqData.setNAME(NAME);
        reqData.setADDRESSE(ADDRESSE);
        reqData.setSTART_DATE(START_DATE);
        reqData.setEND_DATE(END_DATE);
        
        //接口入参
        String INTEGRAL_ACCOUNT_ID = param.getString("INTEGRAL_ACCOUNT_ID");
        String INTEGRAL_ACCOUNT_TYPE = param.getString("INTEGRAL_ACCOUNT_TYPE");
        String STATUS = param.getString("STATUS");
        String IN_TYPE = param.getString("inType");
        reqData.setINTEGRAL_ACCOUNT_ID(INTEGRAL_ACCOUNT_ID);
        reqData.setINTEGRAL_ACCOUNT_TYPE(INTEGRAL_ACCOUNT_TYPE);
        reqData.setSTATUS(STATUS);
        reqData.setIN_TYPE(IN_TYPE);

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ModifyScoreAcctRegData();
    }

}
