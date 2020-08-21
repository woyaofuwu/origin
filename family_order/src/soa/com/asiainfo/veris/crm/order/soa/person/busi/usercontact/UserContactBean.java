
package com.asiainfo.veris.crm.order.soa.person.busi.usercontact;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.usercontact.UserContactQry;

public class UserContactBean extends CSBizBean
{

    /**
     * 用户接触开始、结束接口
     * 
     * @param pd
     * @param data
     *            必备参数IDITEMRANGE,CONTTYPE,SESSIONID,IDTYPE
     * @return X_RESULTCODE,X_RESULTINFO
     * @throws Exception
     * @author huanghui
     */
    public IData userContactBean(IData data) throws Exception
    {
        IData result = new DataMap();

        // 必备参数校验，检查参数中必填参数是否完整，不完整则返回失败
        ArrayList list = new ArrayList();
        list.add("IDITEMRANGE"); // 标识号码
        list.add("CONTTYPE"); // 接触开始、结束 0:接触开始；1：接触结束
        list.add("SESSIONID"); // SessionID
        list.add("IDTYPE");
        for (int i = 0; i < list.size(); i++)
        {
            if ("".equals(data.getString(list.get(i).toString())) || null == data.getString(list.get(i).toString()))
            {
                // result.put("X_RESULTCODE", "700001");
                // result.put("X_RESULTINFO", "参数中缺少必填参数: " + list.get(i).toString());
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001", "参数中缺少必填参数: " + list.get(i).toString());
                return result;
            }
        }

        // SEL_BY_SN需要三个参数VSERIAL_NUMBER、VREMOVE_TAG（0）、VNET_TYPE_CODE（00）,用于查询用户信息
        data.put("SERIAL_NUMBER", data.get("IDITEMRANGE"));
        data.put("REMOVE_TAG", "0");
        data.put("NET_TYPE_CODE", "00");
        IDataset resUser = UserContactQry.getUserInfo(data);
        // 查询出用户信息
        if (null == resUser || resUser.size() < 1)
        {
            // result.put("X_RESULTCODE", "700002");
            // result.put("X_RESULTINFO", "用户信息不存在");
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700002", "用户信息不存在");
            return result;
        }

        // 不能为空的参数ACCEPT_MONTH、CUST_CONTACT_ID、EPARCHY_CODE、CITY_CODE
        // CONTACT_MODE、IN_MODE_CODE、CHANNEL_ID、SUB_CHANNEL_ID、START_TIME、FINISH_TIME
        // CONTACT_STATE
        IData resUserFinal = (IData) resUser.get(0); // TF_F_USER 数据
        String acceptMonth = SysDateMgr.getCurMonth();
        // CUST_CONTACT_ID 不知道是什么数据？？？？
        // String custContactID = resUserFinal.getString("USECUST_ID");
        String custID = resUserFinal.getString("CUST_ID");
        String userID = resUserFinal.getString("USER_ID");
        String productID = resUserFinal.getString("PRODUCT_ID");
        String eparchyCode = resUserFinal.getString("EPARCHY_CODE");
        String cityCode = resUserFinal.getString("CITY_CODE");
        String custContactID = UserContactQry.getSeqUserContact();
        // String custContactID = "11111111111";
        data.put("CUST_CONTACT_ID", custContactID);
        data.put("ACCEPT_MONTH", acceptMonth);
        data.put("CUST_ID", custID);
        data.put("USER_ID", userID);
        // data.put("PARTITION_ID", userID.substring(userID.length() - 4));//添加分区
        data.put("PRODUCT_ID", productID);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("CITY_CODE", cityCode);
        data.put("SERIAL_NUMBER", data.get("IDITEMRANGE"));
        data.put("RSRV_STR1", data.getString("SESSIONID")); // 把sesionID插入标识位
        data.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "L")); // 不是必填参数，但是是必填字段
        // 查询出custPerson信息
        IData resCustPerson = UcaInfoQry.qryPerInfoByCustId(custID);
        String custName = resCustPerson.getString("CUST_NAME");
        data.put("CUST_NAME", custName);
        data.put("CONTACT_MODE", "P"); // CONTACT_MODE 业务类型写死为P
        data.put("CONTACT_STATE", "0");
        // 本地表中的CHANELID,SUB_CHANNEL_ID应该是接口中传入的TRADE_DEPART_ID
        data.put("CHANNEL_ID", data.getString("TRADE_DEPART_ID", "1")); // 取不到则取默认值
        data.put("SUB_CHANNEL_ID", data.getString("TRADE_DEPART_ID", "1"));
        // IN_MODE_CODE 从接口取
        String nowDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        String beginTime = (String) data.get("BEGINTIME");
        String endTime = (String) data.get("ENDTIME");
        String contType = (String) data.get("CONTTYPE");
        if ("0".equals(contType))
        {
            if (null == data.get("BEGINTIME") || "".equals(data.get("BEGINTIME")))
            {
                data.put("START_TIME", nowDate);
                data.put("FINISH_TIME", nowDate);
            }
            else
            {
                String beginTimeFinal = DateFormatUtils.format(DateUtils.parseDate(beginTime, new String[]
                { "yyyyMMddHHmmss" }), "yyyy-MM-dd HH:mm:ss");
                data.put("START_TIME", beginTimeFinal);
                data.put("FINISH_TIME", beginTimeFinal);
            }
            boolean isAlreadyInsert = UserContactQry.saveLog(data); // 保存日子
            if (isAlreadyInsert)
            {
                // result.put("X_RESULTCODE", "00");
                // result.put("X_RESULTINFO", "WAP用户开始接触日志插入成功");
                // CSAppException.apperr(CrmCommException.CRM_COMM_13,"700005", "WAP用户开始接触日志插入成功");
                return result;
            }
            else
            {
                // result.put("X_RESULTCODE", "700006");
                // result.put("X_RESULTINFO", "WAP用户开始接触日志插入失败");
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "700006", "WAP用户开始接触日志插入失败");
                return result;
            }
        }
        if ("1".equals(contType))
        {
            IDataset logUserContact = UserContactQry.getUserContactLog(data);
            // 如果参数表中未传入登出时间则用当前时间替代，如果有登出时间则按登出时间记录
            if (null == data.get("ENDTIME") || "".equals(data.get("ENDTIME")))
            {
                data.put("FINISH_TIME", nowDate);
                data.put("START_TIME", nowDate);
            }
            else
            {
                String endTimeFinal = DateFormatUtils.format(DateUtils.parseDate(endTime, new String[]
                { "yyyyMMddHHmmss" }), "yyyy-MM-dd HH:mm:ss");
                data.put("FINISH_TIME", endTimeFinal);
                data.put("START_TIME", endTimeFinal);
            }

            // 如果日志表中没有登入记录
            if (null == logUserContact || logUserContact.size() < 1)
            {
                boolean isAlreadyInsert1 = UserContactQry.saveLog(data);
                if (isAlreadyInsert1)
                {
                    // result.put("X_RESULTCODE", "00");
                    // result.put("X_RESULTINFO", "WAP用户结束接触日志插入成功");
                    return result;
                }
                else
                {
                    // result.put("X_RESULTCODE", "700007");
                    // result.put("X_RESULTINFO", "WAP用户结束接触日志插入失败");
                    CSAppException.apperr(CrmCommException.CRM_COMM_13, "700007", "WWAP用户结束接触日志插入失败");
                    return result;
                }
            }
            // 如果日志表中存在登入记录，根据号码和sessionID更新登出时间？？？
            else
            {
                UserContactQry.updateUserContactLog(data);
                // result.put("X_RESULTCODE", "00");
                // result.put("X_RESULTINFO", "WAP用户结束接触日志更新成功");
                return result;
            }
        }
        // result.put("X_RESULTCODE", "700008");
        // result.put("X_RESULTINFO", "日志插入操作失败，请检查参数是否正确");
        CSAppException.apperr(CrmCommException.CRM_COMM_13, "700008", "日志插入操作失败，请检查参数是否正确");
        return result;
    }

}
