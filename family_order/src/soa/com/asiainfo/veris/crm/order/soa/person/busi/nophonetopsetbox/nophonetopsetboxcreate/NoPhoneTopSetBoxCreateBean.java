
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate;

 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * 无手机宽带魔百和
 * @author Administrator
 *
 */
public class NoPhoneTopSetBoxCreateBean extends CSBizBean
{
	
	/**
	 * 获取用户是否存在已完工（未完工）的宽带信息。
	 * */
	public static IDataset getUserWilenInfos(IData param) throws Exception
    { 
	    
        IDataset dataInfos = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_WILEN_BY_SN_PRODID", param);
        
        if (IDataUtil.isNotEmpty(dataInfos))
        {
            return dataInfos;
        }
        else
        {
            dataInfos = TradeInfoQry.getTradeInfoBySn(param.getString("SERIAL_NUMBER"));
            
            if (IDataUtil.isNotEmpty(dataInfos))
            {
                if (param.getString("PRODUCT_ID").equals(dataInfos.getData(0).getString("PRODUCT_ID")))
                {
                    return dataInfos;
                }
            }
        }
        
        return null;
    }  
}
