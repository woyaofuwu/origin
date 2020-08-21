
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;

public final class BatDestroyOneKey extends CSBizBean
{
    private final static Logger logger = Logger.getLogger(BatDestroyOneKey.class);

    /**
     * 将JSON串转换成WADE的串
     * 
     * @param strCoding
     * @return
     * @throws Exception
     */
    public static String codingtoWadeString(String strCoding) throws Exception
    {

        if (strCoding.indexOf("[") == 0)
        {
            IDataset set = new DatasetList(strCoding);
            return set.toString();
        }
        else if (strCoding.indexOf("{") == 0)
        {
            IData data = new DataMap(strCoding);

            return data.toString();
        }

        return strCoding;
    }

    /**
     * 一键注销成员 只注销成员(将成员注销插入批量表) 成功时返回成员批量任务编码
     * 
     * @param batData
     * @return
     * @throws Exception
     */
    public static IDataset destroyMemberOneKeyByProc(IData batData) throws Exception
    {
        IDataset returnSet = new DatasetList();
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>集团成员一键注销开始>>>>>>>>>>>>>>>>>");
        }
        String codingStr = batData.getString("CODING_STR", "");
        if (codingStr.equals(""))
        {
            CSAppException.apperr(BatException.CRM_BAT_2);
        }
        codingStr = codingtoWadeString(codingStr);

        IData codingData = new DataMap(codingStr);
        String productId = codingData.getString("PRODUCT_ID");
        IDataset compProduct = ProductCompInfoQry.getCompProductInfoByID(productId);
        String relationTypeCode = compProduct.getData(0).getString("RELATION_TYPE_CODE", "");
        if (relationTypeCode.equals(""))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_15, productId);
        }
        String eparchyCode = codingData.getString("USER_EPARCHY_CODE", "");
        if ("".equals(eparchyCode))
        {
            eparchyCode = CSBizBean.getTradeEparchyCode();
        }
        // 存储过程参数
        String[] paramName =
        { "v_User_Id", "v_Relation_Type_Code", "v_Staff_Id", "v_Depart_Id", "v_City_Code", "v_Eparchy_Code", "v_MemConding", "v_TASK_NAME", "v_Result_Code", "v_Result_Info" };

        IData paramValue = new DataMap();
        paramValue.put("v_User_Id", codingData.getString("USER_ID"));
        paramValue.put("v_Relation_Type_Code", relationTypeCode);
        paramValue.put("v_Staff_Id", getVisit().getStaffId());
        paramValue.put("v_Depart_Id", getVisit().getDepartId());
        paramValue.put("v_City_Code", getVisit().getCityCode());
        paramValue.put("v_Eparchy_Code", eparchyCode);
        paramValue.put("v_MemConding", codingStr);
        paramValue.put("v_TASK_NAME", batData.getString("BATCH_TASK_NAME", ""));

        // 得到proc名称
        String procName = "P_CMS_DESTROY_GRPMEB_AUTO";// 存储过程名称

        Dao.callProc(procName, paramName, paramValue);

        // 是否成功
        String resultCode = paramValue.getString("v_Result_Code");
        String resultInfo = paramValue.getString("v_Result_Info");

        if ("-1".equals(resultCode))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("集团一键注销成员，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
            }

            CSAppException.apperr(BatException.CRM_BAT_63, resultCode, resultInfo);
        }
        else
        {
            IData returnData = new DataMap();
            String hintMessage = resultInfo + "\n 本次操作插入批量明细数据：" + resultCode;
            returnData.put("hint_message", hintMessage);
            returnSet.add(returnData);
        }

        return returnSet;
    }

    /**
     * 一键注销成员 同时注销集团(将成员注销插入批量表 集团注销插入批量表) 成功时返回成员任务编码以及集团任务编码
     * 
     * @param batData
     * @return
     * @throws Exception
     */
    public static IDataset destroyUserMemberOneKeyByProc(IData batData) throws Exception
    {
        IDataset returnSet = new DatasetList();
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>集团成员一键注销开始>>>>>>>>>>>>>>>>>");
        }
        String codingStr = batData.getString("CODING_STR", "");
        if (codingStr.equals(""))
        {
            CSAppException.apperr(BatException.CRM_BAT_2);
        }
        codingStr = codingtoWadeString(codingStr);

        IData codingData = new DataMap(codingStr);
        String productId = codingData.getString("PRODUCT_ID");
        IDataset compProduct = ProductCompInfoQry.getCompProductInfoByID(productId);
        String relationTypeCode = compProduct.getData(0).getString("RELATION_TYPE_CODE", "");
        if (relationTypeCode.equals(""))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_15, productId);
        }
        String eparchyCode = codingData.getString("USER_EPARCHY_CODE", "");
        if ("".equals(eparchyCode))
        {
            eparchyCode = CSBizBean.getTradeEparchyCode();
        }
        // 存储过程参数
        String[] paramName =
        { "v_User_Id", "v_Serial_Number", "v_Relation_Type_Code", "v_Staff_Id", "v_Depart_Id", "v_City_Code", "v_Eparchy_Code", "v_MemConding", "v_TASK_NAME", "v_Result_Code", "v_Result_Info" };

        IData paramValue = new DataMap();
        paramValue.put("v_User_Id", codingData.getString("USER_ID"));
        paramValue.put("v_Serial_Number", codingData.getString("GRP_SERIAL_NUMBER"));// 集团服务号码
        paramValue.put("v_Relation_Type_Code", relationTypeCode);
        paramValue.put("v_Staff_Id", getVisit().getStaffId());
        paramValue.put("v_Depart_Id", getVisit().getDepartId());
        paramValue.put("v_City_Code", getVisit().getCityCode());
        paramValue.put("v_Eparchy_Code", eparchyCode);
        paramValue.put("v_MemConding", codingStr);
        paramValue.put("v_TASK_NAME", batData.getString("BATCH_TASK_NAME", ""));

        // 得到proc名称
        String procName = "P_CMS_DESTROY_GRPUSERMEB_AUTO";// 存储过程名称

        Dao.callProc(procName, paramName, paramValue);

        // 是否成功
        String resultCode = paramValue.getString("v_Result_Code");
        String resultInfo = paramValue.getString("v_Result_Info");

        if ("-1".equals(resultCode))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("集团一键注销成员，OUT_RESULT_CODE=[" + resultCode + "] OUT_RESULT_INFO=[" + resultInfo + "]");
            }

            CSAppException.apperr(BatException.CRM_BAT_63, resultCode, resultInfo);
        }
        else
        {
            IData returnData = new DataMap();
            String hintMessage = resultInfo + "\n 本次操作插入批量明细数据：" + resultCode;
            returnData.put("hint_message", hintMessage);
            returnSet.add(returnData);
        }

        return returnSet;
    }
}
