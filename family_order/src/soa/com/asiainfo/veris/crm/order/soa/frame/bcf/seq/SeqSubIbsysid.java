/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.service.BizRoute;

/**
 * @author admin
 */
public class SeqSubIbsysid extends SeqBase
{

    public SeqSubIbsysid()
    {
        super("SEQ_GRPBIZ_SUBID", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        String routId = BizRoute.getRouteId();

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getSysDate_yyMMddHHmmss()); // 取6位系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String epachyCode) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        String routId = BizRoute.getRouteId();

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getSysDate_yyMMddHHmmss()); // 取6位系统时间，yyMMdd
        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }
}
