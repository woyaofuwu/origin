
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqContractId extends SeqBase
{

    public SeqContractId()
    {
        super("seq_contract_id", 20);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        nextval = fillupFigure(nextval, 6, "0");

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        return null;
    }

}
