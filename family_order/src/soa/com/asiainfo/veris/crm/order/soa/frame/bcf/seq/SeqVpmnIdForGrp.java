
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SeqVpmnIdForGrp extends SeqBase
{
    public SeqVpmnIdForGrp()
    {
        super("seq_vpmn_id", 100);
    }

    /**
     * VPMN集团标识,前2位为orderno,后6位自动生成
     */
    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        if(ProvinceUtil.isProvince(ProvinceUtil.QHAI)){
        	strbuf.append(CSBizBean.getUserEparchyCode().substring(1,4));
        	strbuf.append(fillupFigure(nextval, 8, "0").substring(1,8));
        }else{
        	strbuf.append(getOrderno()); // 获取地域编码序号，不足两位前面补9
	        strbuf.append(fillupFigure(nextval, 6, "0")); // 取初始序列,不足6位前面补 0
        }

        return strbuf.toString();
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
