
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class SeqPlatOfficeDataImportId extends SeqBase
{

    public SeqPlatOfficeDataImportId()
    {
        super("SEQ_SPBURE_IMPORT_ID", 2000);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getSysDate_yyMMdd()); // 系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }

    @Override
    public String getNextval(String connName, String arg1) throws Exception
    {
        return getNextval(connName);
    }

}
