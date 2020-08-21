
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;

public class SeqSaleGoodsId extends SeqBase
{
    public SeqSaleGoodsId()
    {
        super("SEQ_SALE_GOODS_ID", 100);
    }

    /**
     * 业务流水号
     */
    @Override
    public String getNextval(String connName) throws Exception
    {
        String nextval = nextval(connName);

        if (nextval == null)
        {
            return "";
        }
        
        if (ProvinceUtil.isProvince(ProvinceUtil.TJIN)) 
        {
        	nextval = "55" + fillupFigure(nextval, 6, "0");	//取初始序列,不足6位前面补 0
		}

        return nextval;
    }

    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }
}
