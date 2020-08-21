
package com.asiainfo.veris.crm.order.soa.frame.bcf.seq;

import org.apache.commons.lang.StringUtils;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SeqSmsTemplateId extends SeqBase
{

    public SeqSmsTemplateId()
    {
        super("seq_sms_template_id", 100);
    }

    // 生成template_id，由前缀（预定义在_td_s_static表中--type_id=‘BMC_TEMPLATE_ID_KEY’--前缀为data_name对应记录）
    @Override
    public String getNextval(String connName) throws Exception
    {

        String nextval = nextval(connName);
        if (nextval == null)
        {
            return "";
        }

        StringBuilder strbuf = new StringBuilder();
        // 此处获取的获取的前缀目前写死，数据库表中暂时只对应该一个前缀，具体逻辑待数据库完善处理
        String templateIdPre = StaticUtil.getStaticValue("BMC_TEMPLATE_ID_KEY", "CRM_SMS_GRP_VPCN");

        if (StringUtils.isEmpty(templateIdPre))
        {// 如果无法从静态表中获取temlate_id前缀，
            CSAppException.apperr(BizException.CRM_BIZ_9, connName);// 异常：数据库中template_id前缀没有预定义
        }

        strbuf.append(templateIdPre);
        strbuf.append("_");// 生成序列的形式为 templateIdPre_sequence;

        strbuf.append(fillupFigure(nextval, 6, "0"));// 序列不足6位，前面补0，形如： 000021
        nextval = strbuf.toString();
        return nextval;
    }

    @Override
    public String getNextval(String connName, String s1) throws Exception
    {
        return getNextval(connName);
    }

}
