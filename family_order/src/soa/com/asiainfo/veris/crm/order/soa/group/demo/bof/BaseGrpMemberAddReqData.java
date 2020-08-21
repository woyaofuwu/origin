/**
 * @Title: BaseGrpMemberAddReqData.java
 * @Package com.ailk.groupservice.demo.bof
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date Feb 21, 2014 2:45:10 PM
 * @version V1.0
 */

package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 项目名称：yn_groupserv 类名称：BaseGrpMemberAddReqData 类描述： 创建人：penghaibo 创建时间：Feb 21, 2014 2:45:10 PM 修改人：penghaibo 修改时间：Feb
 * 21, 2014 2:45:10 PM 修改备注：
 * 
 * @version
 */
public class BaseGrpMemberAddReqData extends BaseReqData
{

    protected boolean effectNow = false;// 是否立即生效

    protected String trade_type_code;

    protected String order_type_code;

    public String getTrade_type_code()
    {
        return trade_type_code;
    }

    public void setTrade_type_code(String trade_type_code)
    {
        this.trade_type_code = trade_type_code;
    }

    public String getOrder_type_code()
    {
        return order_type_code;
    }

    public void setOrder_type_code(String order_type_code)
    {
        this.order_type_code = order_type_code;
    }

    public boolean isEffectNow()
    {
        return effectNow;
    }

    public void setEffectNow(boolean effectNow)
    {
        this.effectNow = effectNow;
    }
}
