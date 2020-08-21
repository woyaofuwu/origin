package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

public class TpOrderRelSeq extends SeqBase{
    /**
     * 序列长度
     */
    private static final int LENTH = 6;

    /**
     * 缓存数量
     */
    private static final int FETCH_SIZE = 100;

    public TpOrderRelSeq()
    {
        super("SEQ_TPREL_ID", FETCH_SIZE);
    }

    public String getNextval(String connName) throws Exception{
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(fillupFigure(nextval, LENTH, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    @Override
    public String getNextval(String connName, String s1) throws Exception {
        return getNextval(connName);
    }
}


