
package com.asiainfo.veris.crm.order.soa.person.busi.procuratorateinf;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.procuratorateinf.QrySvcForAccountQry;

public class QrySvcForAccountBean extends CSBizBean
{

    /**
     * 接口数据添加, 入参检查
     * 
     * @param data
     * @throws Exception
     */
    private void checkParam(IData data) throws Exception
    {
        List<String> arrayList = new ArrayList<String>();
        arrayList.add("USER_ID");
        // 一下修改为不必传的
        // arrayList.add("BIZ_TYPE_CODE");
        // arrayList.add("SP_CODE");
        // arrayList.add("BIZ_CODE");
        // arrayList.add("END_DATE");
        // arrayList.add("OPER_CODE");
        arrayList.add("BIZ_STATE_CODE");
        for (int i = 0; i < arrayList.size(); i++)
        {
            IDataUtil.chkParam(data, arrayList.get(i));
        }
    }

    /**
     * 订购关系查询 --- 接口方法
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getOrderRelation(IData data) throws Exception
    {
        IDataset retdata = new DatasetList();
        checkParam(data);
        IData user = UcaInfoQry.qryUserInfoByUserId(data.getString("USER_ID"));
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您要找的用户不存在或者[USER_ID]不正确!");
        }
        String crmSerialNumber = user.getString("SERIAL_NUMBER", "");
        String bizState = data.getString("BIZ_STATE_CODE");
        if ("1".equals(bizState))// 查询正常
        {
            IDataset result = QrySvcForAccountQry.selOrderRelationIng(data);
            retdata = this.getReturnData(result, crmSerialNumber);
        }
        else if ("2".equals(bizState))
        { // 查询退订或者预定的
            IDataset result = QrySvcForAccountQry.selOrderRelationPre(data);
            retdata = this.getReturnData(result, crmSerialNumber);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "[BIZ_STATE_CODE]的值不正确");
        }
        return retdata;
    }

    /**
     * 封装数据 -- 数据量不大
     * 
     * @param dataset
     * @param crmSerialNumber
     * @return
     * @throws Exception
     */
    public IDataset getReturnData(IDataset dataset, String crmSerialNumber) throws Exception
    {
        for (int i = 0; i < dataset.size(); i++)
        {
            // dataset.getData(i).put("SP_CODE", dataset.getData(i).getString("SPCODE", ""));
            // dataset.getData(i).put("BIZ_CODE", dataset.getData(i).getString("BIZCODE", ""));
            // Q
            IDataset daes = SpInfoQry.queryBizServiceInfosBySpBizCode(dataset.getData(i).getString("BIZ_CODE", ""), dataset.getData(i).getString("SP_CODE", ""));
            if (IDataUtil.isEmpty(daes))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "请配置[BIZCODE]对应的平台服务基层数据!");
            }
            IData das = daes.getData(0);
            dataset.getData(i).put("BIZ_NAME", das.getString("BIZ_NAME", ""));
            dataset.getData(i).put("BILL_TYPE", das.getString("BILL_TYPE", ""));
            dataset.getData(i).put("PRICE", das.getString("PRICE", "0"));

            dataset.getData(i).put("CRM_SERIAL_NUMBER", crmSerialNumber);
        }
        return dataset;
    }

    /**
     * 1.2 查询无线音乐会员级别 --- 接口方法
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getServiceAttr(IData data) throws Exception
    {
        IDataset result = new DatasetList();
        IDataUtil.chkParam(data, "USER_ID");
        IDataUtil.chkParam(data, "SERVICE_ID");
        result = QrySvcForAccountQry.selServiceAttr(data);
        return result;
    }

}
