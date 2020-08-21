
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.service.BizRoute;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqOrderId extends SeqBase
{
    public SeqOrderId()
    {
        super("seq_order_id", 100);
    }

    @Override
    public String getNextval(String connName) throws Exception
    {
        connName = getJourConnName(null);
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        strbuf.append(getOrderno()); // 获取地域编码序号，不足两位前面补9

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.QHAI))
        {
            strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd
        }
        else
        {
            String routId = BizRoute.getRouteId();

            if (routId.equals(Route.CONN_CRM_CG))
            {
                strbuf.append(getSysDate_9yMMdd()); // 取6位系统时间，9yMMdd
            }
            else
            {
                strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd
            }
        }

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();

        return nextval;
    }

    public String getNextval(String connName, String epachyCode) throws Exception
    {
        connName = getJourConnName(epachyCode);
        String nextval = nextval(Route.getJourDb(connName));

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();

        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.QHAI))
        {
            strbuf.append(getOrdernoByEpachy(epachyCode)); // 获取地域编码序号，不足两位前面补9
            strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd
        }
        else
        {
            String routId = BizRoute.getRouteId();

            if (routId.equals(Route.CONN_CRM_CG))
            {
                strbuf.append(getOrderno());
                strbuf.append(getSysDate_9yMMdd()); // 取6位系统时间，9yMMdd
            }
            else
            {
                strbuf.append(getOrdernoByEpachy(epachyCode)); // 获取地域编码序号，不足两位前面补9
                strbuf.append(getSysDate_yyMMdd()); // 取6位系统时间，yyMMdd
            }
        }

        strbuf.append(fillupFigure(nextval, 8, "0")); // 取初始序列,不足8位前面补 0
        nextval = strbuf.toString();
        return nextval;
    }
}
