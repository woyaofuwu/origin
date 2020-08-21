
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetbuyouttelequ;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BuyoutTelEquQry;

public class CttQryBuyoutTelEquSVC extends CSBizService
{
    public IDataset qryBuyoutTelEqu(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryBuyoutTelEqu(input.getString("DEPART_ID"), input.getString("CHNL_ID"), input.getString("START_REG_DATE"), input.getString("END_REG_DATE"), this.getPagination());
    }

    public IDataset qryBuyoutTelEqu2(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryBuyoutTelEqu2(input.getString("CHNL_ID"), input.getString("START_REG_DATE"), input.getString("END_REG_DATE"), this.getPagination());
    }

    public IDataset qryChl(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryChl(input.getString("DEPART_ID"), input.getString("CHNL_CODE"), input.getString("CHNL_NAME"), this.getPagination());
    }

    public IDataset qryChl2(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryChl2(input.getString("CHNL_CODE"), input.getString("CHNL_NAME"), this.getPagination());
    }

    public IDataset qryDepart(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryDepart(input.getString("DEPART_ID"));
    }

    public IDataset qryDeparts(IData input) throws Exception
    {
        return BuyoutTelEquQry.qryDeparts();
    }
}
