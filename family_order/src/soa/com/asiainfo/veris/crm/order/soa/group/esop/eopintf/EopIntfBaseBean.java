package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class EopIntfBaseBean
{
    public static final String SUCCESS_CODE            = "0";          //处理成功
    public static final String SUCCESS_INFO            = "success";    //成功时返回信息
    public static final String FAIL_CODE               = "-1";         //处理失败
    
    public static final String NGBOSS_PAGE_SUBMIT      = "01";         //NGBOSS页面提交
    public static final String BBOSS_COMEDOWN          = "02";         //BBOSS业务落地信息
    public static final String MESOP_PAGE_SUBMIT       = "03";         //mesop页面提交
    
    public static final String creditparse      = "creditparse";        //信控暂停停机
    public static final String creditcontiue    = "creditcontiue";      //信控暂停开机
    public static final String creditcanle      = "creditcanle";        //信控拆机
    
    //根据订单编号没有查到订单资料
    public static final String[] NOT_EXIST_ORDER_FLOWID = new String[]{"0000001", "没有业务订单!订单编号为:"};
    
    //根据业务流水号没有查到订单资料
    public static final String[] NOT_EXIST_ORDER_IBSYSID = new String[]{"0000002", "没有业务订单!业务流水号为:"};
    
    protected String getMandaData(IData input, String key) throws Exception
    {
        String value = input.getString(key);
        if(StringUtils.isBlank(value))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "711000:接口参数检查，输入参数[" + key + "]不存在！");
        }
        return value;
    }
}
