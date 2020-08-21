
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.service.BizRoute;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqRealId extends SeqBase
{
    public SeqRealId()
    {
        super("seq_real_id", 100);
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
        strbuf.append(getOrderno()); // 获取地域编码序号，不足两位前面补9

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.QHAI))
        {
            strbuf.append(getSysDate_yyMM()); // 取4位系统时间，yyMM
        }
        else
        {
            String routId = BizRoute.getRouteId();

            if (routId.equals(Route.CONN_CRM_CG))
            {
                strbuf.append(getSysDate_9yMM()); // 取4位系统时间，9yMM
            }
            else
            {
                strbuf.append(getSysDate_yyMM()); // 取4位系统时间，yyMM
            }
        }

        // 海南特殊要求，只需要6位
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
        {
            strbuf = new StringBuilder();
        }

        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String s, String s1) throws Exception
    {
        String nextval = nextval(s);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }
}
