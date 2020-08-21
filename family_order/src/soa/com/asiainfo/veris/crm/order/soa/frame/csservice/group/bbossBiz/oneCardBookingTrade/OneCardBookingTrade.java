
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardBookingTrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class OneCardBookingTrade extends CSBizBean
{

    /**
     * 一卡通预约订购
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset ecBizInfoReq(IData inParam) throws Exception
    {

        IData param = new DataMap();

        // 订单来源：0-省BBOSS上传; 1-EC上传; 2-BBOSS受理
        param.put("OPR_SOURCE", IDataUtil.getMandaData(inParam, "OPR_SOURCE"));
        param.put("GROUP_ID", IDataUtil.getMandaData(inParam, "GROUP_ID"));
        param.put("OPERATE_ID1", IDataUtil.getMandaData(inParam, "OPERATE_ID1"));
        param.put("ITEM_FIELD_CODE", IDataUtil.getMandaData(inParam, "ITEM_FIELD_CODE"));
        param.put("PREVALUEC1", inParam.getString("PREVALUEC1", ""));
        param.put("PROVINCE_NO", inParam.getString("PROVINCE_NO", ""));
        param.put("OPER_CODE", inParam.getString("OPER_CODE", ""));
        param.put("PARA_CODE2", IDataUtil.getMandaData(inParam, "PARA_CODE2"));
        param.put("GCVALUE1", inParam.getString("GCVALUE1", ""));
        param.put("GCVALUE2", inParam.getString("GCVALUE2", ""));
        param.put("GCVALUE3", inParam.getString("GCVALUE3", ""));
        param.put("MODIFY_TAG", IDataUtil.getMandaData(inParam, "MODIFY_TAG"));
        param.put("A_DISCNT", IDataUtil.getMandaData(inParam, "A_DISCNT"));
        param.put("A_DISCNT_NAME", inParam.getString("A_DISCNT_NAME", ""));
        param.put("PARA_CODE5", inParam.getString("PARA_CODE5", ""));
        param.put("PARA_CODE6", inParam.getString("PARA_CODE6", ""));
        param.put("PARA_CODE7", inParam.getString("PARA_CODE7", ""));
        param.put("ACT_TAG", inParam.getString("ACT_TAG", ""));
        param.put("OPERATE_ID2", IDataUtil.getMandaData(inParam, "OPERATE_ID2"));
        param.put("PARA_CODE3", inParam.getString("PARA_CODE3", ""));
        param.put("PREVALUED1", inParam.getString("PREVALUED1", ""));
        param.put("ITEM_FIELD_NAME", IDataUtil.getMandaData(inParam, "ITEM_FIELD_NAME"));
        param.put("RES_INFO1", inParam.getString("RES_INFO1", ""));
        param.put("RES_INFO2", inParam.getString("RES_INFO2", ""));
        param.put("RES_INFO3", inParam.getString("RES_INFO3", ""));
        param.put("RES_INFO4", inParam.getString("RES_INFO4", ""));
        param.put("RES_INFO5", inParam.getString("RES_INFO5", ""));
        param.put("RES_INFO6", inParam.getString("RES_INFO6", ""));
        param.put("OPER_FLAG", IDataUtil.getMandaData(inParam, "OPER_FLAG"));
        param.put("B_DISCNT", IDataUtil.getMandaData(inParam, "B_DISCNT"));
        param.put("B_DISCNT_NAME", inParam.getString("B_DISCNT_NAME", ""));
        param.put("PARA5", inParam.getString("PARA5", ""));
        param.put("PARA6", inParam.getString("PARA6", ""));
        param.put("PARA7", inParam.getString("PARA7", ""));
        param.put("RSRV_STR7", inParam.getString("RSRV_STR7", ""));
        param.put("RSRV_STR2", inParam.getString("RSRV_STR2", ""));
        param.put("OPER_INFO", IDataUtil.getMandaData(inParam, "OPER_INFO"));
        param.put("RSRV_STR3", inParam.getString("RSRV_STR3", ""));
        param.put("RSRV_STR4", inParam.getString("RSRV_STR4", ""));
        param.put("RSRV_STR5", inParam.getString("RSRV_STR5", ""));
        param.put("RSRV_STR6", inParam.getString("RSRV_STR6", ""));
        param.put("PARA_CODE4", IDataUtil.getMandaData(inParam, "PARA_CODE4"));
        param.put("STATUS", inParam.getString("STATUS", ""));
        param.put("OPERATE_TIME", IDataUtil.getMandaData(inParam, "OPERATE_TIME"));
        int count = Dao.executeUpdateByCodeCode("TF_F_QDGROUP", "INS_ONECARD_BOOKING", param, Route.CONN_CRM_CG);
        if (count == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_20);
        }

        return IDataUtil.idToIds(inParam);
    }

}
