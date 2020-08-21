
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IDataset;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.sequence.AbstractSequence;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

abstract class SeqBase extends AbstractSequence
{
    public SeqBase(String seqName)
    {
        super(seqName);
    }

    public SeqBase(String seqName, int fetchSize)
    {
        super(seqName, fetchSize);
    }

    /**
     * 补足位数, 取初始序列,不足num位前面补str
     * 
     * @param seqid
     *            ,num,str
     * @return @
     * @throws Exception
     */
    protected final String fillupFigure(String instr, int num, String str) throws Exception
    {
        StringBuilder strbuf = new StringBuilder();
        int len = instr.length();
        if (len < num)
        {
            for (int i = 0; i < (num - len); i++)
            {
                strbuf.append(str);
            }
        }
        else if (len > num) // 该逻辑按原函数逻辑处理
        {
            instr = instr.substring(len - num);
        }
        strbuf.append(instr);
        return strbuf.toString();
    }

    /**
     * 获取地域编码后两位
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    protected final String getAreaCodeAfterTwo(String eparchyCode) throws Exception
    {
        return eparchyCode.substring(2);
    }

    /**
     * 业务侧可自定义序列拼接方式
     * 
     * @throws Exception
     */
    public String getNextval(String connName) throws Exception
    {
        return "";
    }

    /**
     * 获取地域编码序号，不足两位前面补9
     * 
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    protected final String getOrderno() throws Exception
    {
        String eparchyCode = CSBizBean.getUserEparchyCode();

        IDataset dataset = UAreaInfoQry.qryAreaByPk(eparchyCode, null, null);

        // 初始值
        String iv_orderno = "";

        if (dataset.size() > 0)
        {
            iv_orderno = dataset.getData(0).getString("ORDER_NO", "");
            iv_orderno = fillupFigure(iv_orderno, 2, "9");
        }

        return iv_orderno;
    }

    // 对于携号的处理,需要传入epachyCode
    protected final String getOrdernoByEpachy(String eparchyCode) throws Exception
    {
        IDataset dataset = UAreaInfoQry.qryAreaByPk(eparchyCode, null, null);

        // 初始值
        String iv_orderno = "";

        if (dataset.size() > 0)
        {
            iv_orderno = dataset.getData(0).getString("ORDER_NO", "");
            iv_orderno = fillupFigure(iv_orderno, 2, "9");
        }

        return iv_orderno;
    }

    /**
     * 取4位系统时间，9yMMdd 集团业务的将yy改为9y
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_9yMM() throws Exception
    {
        String tmp = getSysDate_yyMM();

        StringBuilder strbuf = new StringBuilder();
        strbuf.append("9");
        strbuf.append(tmp.substring(1));

        return strbuf.toString();
    }

    /**
     * 取6位系统时间，9yMMdd 集团业务的将yy改为9y
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_9yMMdd() throws Exception
    {
        String tmp = getSysDate_yyMMdd();

        StringBuilder strbuf = new StringBuilder();
        strbuf.append("9");
        strbuf.append(tmp.substring(1));

        return strbuf.toString();
    }

    /**
     * 取9位系统时间，yy9yyMMdd
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yy9yyMMdd() throws Exception
    {
        String datestr = getSysDate_yyyyMMdd();

        StringBuilder strbuf = new StringBuilder();

        strbuf.append(datestr.substring(0, 2));
        strbuf.append("9");
        strbuf.append(datestr.substring(3));

        return strbuf.toString();
    }

    /**
     * 取4位系统时间，yyMMdd
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yyMM() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyMM");

        return tmp;
    }

    /**
     * 取6位系统时间，yyMMdd
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yyMMdd() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyMMdd");

        return tmp;
    }
    protected final String getSysDate_yyMMddHHmmss() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyMMddHHmmss");

        return tmp;
    }

    /**
     * 取 系统时间，yyMMddhh24miss
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yyMMddhh24miss() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyMMddHHmmss");

        return tmp;
    }

    /**
     * 取8位系统时间
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yyyyMMdd() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyyyMMdd");

        return tmp;
    }

    /**
     * 取系统时间，yyyyMMddhh24miss
     * 
     * @param dateStr
     * @return @
     * @throws Exception
     */
    protected final String getSysDate_yyyyMMddhh24miss() throws Exception
    {
        String tmp = SysDateMgr.getSysDate("yyyyMMddhh24mmss");

        return tmp;
    }

    protected final String getJourConnName(String epachyCode) throws Exception
    {
        if (StringUtils.isBlank(epachyCode))
        {
            epachyCode = BizRoute.getRouteId();
        }
        String connName = DBRouteCfg.getRoute(DBRouteCfg.getGroup(CSBizBean.getVisit().getSubSysCode()), Route.getJourDb(epachyCode));

        return connName;
    }
}
