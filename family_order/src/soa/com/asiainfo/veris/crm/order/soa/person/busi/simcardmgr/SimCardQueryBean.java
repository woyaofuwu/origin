
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class SimCardQueryBean extends CSBizBean
{

    /**
     * 根据sim卡号，查询老sim卡信息
     * 
     * @return IData
     * @exception
     */
    public static IData getSimCardInfo(String simCardNo, IData input) throws Exception
    {
        // 查询旧SIM卡信息
        String netTypeCode = input.getString("NET_TYPE_CODE");
        IDataset oldSimCardInfoSet = ResCall.getSimCardInfo("0", simCardNo, "", "1", netTypeCode);
        if (IDataUtil.isEmpty(oldSimCardInfoSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户资源数据为空！");
        }
        IData oldSimCard = oldSimCardInfoSet.getData(0);
        String emptyCardId = oldSimCard.getString("EMPTY_CARD_ID", "");
        // 如果SIM卡表中EMPTY_CARD_ID字段不为空，表明该卡由白卡写成，到白卡表中取卡类型
        if (!("07".equals(netTypeCode) || "18".equals(netTypeCode)) && StringUtils.isNotEmpty(emptyCardId))
        {
            IDataset oldEmptyCardInfo = ResCall.getEmptycardInfo(emptyCardId, "", "USE");//修复低级错误查白卡传的智能卡号
            if (IDataUtil.isNotEmpty(oldEmptyCardInfo))
            {
                oldSimCard.put("RES_TYPE_CODE", oldEmptyCardInfo.getData(0).getString("RES_TYPE_CODE"));
                oldSimCard.put("EMPTY_CARD_ID", oldEmptyCardInfo.getData(0).getString("EMPTY_CARD_ID", ""));
            }
        }
        // 记录日志信息
        if (!"18".equals(netTypeCode))
        {
            insertLog("142", input.getString("SERIAL_NUMBER"), "0", "Q");
        }

        return oldSimCard;
    }

    private static void insertLog(String trade_type_code, String serial_number, String state, String oper_type) throws Exception
    {
        IData data = new DataMap();
        if (StringUtils.isEmpty(serial_number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "插入补换卡日志错误，手机号码不得为空！");
        }
        data.put("OPER_STAFF_ID", getVisit().getStaffId());
        data.put("TRADE_TYPE_CODE", trade_type_code);
        data.put("SERIAL_NUMBER", serial_number);
        data.put("OPER_TYPE", oper_type);
        data.put("EPARCHY_CODE", getTradeEparchyCode());
        data.put("OPER_TIME", SysDateMgr.getSysTime());
        /** wangyz7 REQ201309300008 star */
        data.put("RSRV_STR1", getVisit().getLoginIP());
        /** wangyz7 REQ201309300008 end */
        if (state != null)
        {
            data.put("STATE", state);
        }
        Dao.insert("TL_B_ABNORMAL_OPER", data);
    }

}
