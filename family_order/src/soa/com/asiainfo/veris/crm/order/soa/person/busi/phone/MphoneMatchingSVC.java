
package com.asiainfo.veris.crm.order.soa.person.busi.phone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaMMInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class MphoneMatchingSVC extends CSBizService
{

    public IData alterMphone(IData data) throws Exception
    {
        IData iResult = new DataMap();

        int tag = Dao.executeUpdateByCodeCode("TF_F_RELATION_MM", "UPD_SERIAL_NUMBER_B", data);
        iResult.put("TAG", tag);

        return iResult;
    }

    public IDataset checkMphone(IData data) throws Exception
    {

        IData result = queryMphoneUsed(data);
        boolean tag = result.getBoolean("tag");

        // hebing
        String serialNumberB = data.getString("SERIAL_NUMBER");
        if (tag)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "原匹配号码" + serialNumberB + ",已使用，不能修改！");
        }
        long serialNumberC = Long.parseLong(data.getString("SERIAL_NUMBER_C"));

        // IData mphoneData = CSViewCall.callone(this, "SS.MphoneMatchingSVC.getTagInfoIData", inparam);
        IData mphoneData = getTagInfoIData();

        long serialNumberS = 0;
        long serialNumberE = 0;
        if (!mphoneData.isEmpty())
        {
            String mphone = mphoneData.getString("TAG_INFO", "");
            serialNumberS = Long.parseLong(mphone.substring(3, 14));
            serialNumberE = Long.parseLong(mphone.substring(15, 26));
        }
        if (serialNumberC > serialNumberE || serialNumberC < serialNumberS)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码匹配校验,该号码不在本次活动范围内，请重新选号！");
        }

        data.put("SERIAL_NUMBER_B", data.getString("SERIAL_NUMBER_C", ""));
        // IDataset dataset = CSViewCall.call(this, "SS.MphoneMatchingSVC.queryMphoneBySerialNumberB", inparam);
        IDataset dataset = queryMphoneBySerialNumberB(data);

        if (dataset != null && dataset.size() > 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码匹配校验,该号码已被其他用户占用！"); // 修改资源接口
        }
        else
        {
            dataset.clear();
            data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER_C", ""));
            // dataset = CSViewCall.call(this, "SS.MphoneMatchingSVC.queryMphoneIdle", data);
            dataset = queryMphoneIdle(data);

            if (dataset.isEmpty())
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码匹配校验,该号码不可用！");
            }
        }

        // dataset = CSViewCall.call(this, "SS.MphoneMatchingSVC.queryMphoneMatch", inparam);
        dataset = queryMphoneMatch(data);

        return dataset;
    }

    public IData getTagInfoIData() throws Exception
    {
        IDataset dataList = new DatasetList();

        dataList = TagInfoQry.getTagInfo(getVisit().getStaffEparchyCode(), "SALE_ACTIVE_DISCNT_ENDDATE", "0");

        return dataList.size() > 0 ? dataList.getData(0) : new DataMap();
    }

    public IData mphoneSubmit(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset importDatas = new DatasetList(data.getString("ImportList"));
        // IDataset dataset = data.getDataset("importDataset");

        int iResult = 0;
        for (int i = 0; i < importDatas.size(); i++)
        {
            try
            {
                IData param = importDatas.getData(i);
                // String serialNumber = data.getString("SERIAL_NUMBER_A");
                // String custName = data.getString("SERIAL_NUMBER_A");
                // String brand = data.getString("BRAND");
                // String viptag = data.getString("VIP_TAG");
                // String yearValue = data.getString("YEAR_VALUE");
                // String avgArpu = data.getString("AVG_ARPU");
                // String custValue = data.getString("CUST_VALUE");
                int j = Dao.executeUpdateByCodeCode("TF_F_RELATION_MM", "INS_ALL", param);

                iResult += j;
            }
            catch (Exception e)
            {
                if (e.getMessage().indexOf("ORA-00001") != -1)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "数据入库异常,插入了重复数据");
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "数据入库异常," + e.getMessage());
                }
            }
        }

        if (iResult != importDatas.size())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码匹配，数据导入执行异常！");
        }
        IData result = new DataMap();
        result.put("sucSize", iResult);
        return result;
    }

    public IData queryMphoneBySerialNumberA(IData data) throws Exception
    {

        // 号码校验
        IData userinfo = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER_A"));
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到记录！");
        }

        IDataset dataset = RelaMMInfoQry.getInfoBySnA(data.getString("SERIAL_NUMBER_A"));

        IData returndata = new DataMap();
        if (DataSetUtils.isBlank(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码匹配,查询不到匹配记录！");
        }
        else
        {

            String serialNumberB = dataset.getData(0).getString("SERIAL_NUMBER_B", "");
            IData inparam = new DataMap();
            inparam.put("SERIAL_NUMBER", serialNumberB);
            // IData result = CSViewCall.callone(this, "SS.MphoneMatchingSVC.queryMphoneUsed", inparam);
            IData result = queryMphoneUsed(inparam);
            boolean tag = result.getBoolean("tag");

            if (tag)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "原匹配号码" + serialNumberB + ",已使用，不能修改！");
            }
            returndata.putAll(dataset.getData(0));
            // this.setCondition(data);
        }

        return returndata;
    }

    public IDataset queryMphoneBySerialNumberB(IData data) throws Exception
    {
        IDataset iResult = new DatasetList();
        iResult = RelaMMInfoQry.getInfoBySnB(data.getString("SERIAL_NUMBER_B"));
        return iResult;
    }

    public IDataset queryMphoneIdle(IData data) throws Exception
    {
        // 修改code_code为资源接口 01 !@# SEL_BY_MATCHMPHONE
        return ResCall.qryIdlePhone(data.getString("SERIAL_NUMBER"));
    }

    public IDataset queryMphoneMatch(IData data) throws Exception
    {
        IDataset iResult = new DatasetList();

        IData mphoneData = getTagInfoIData();
        String serialNumberS = "";
        String serialNumberE = "";

        if (IDataUtil.isNotEmpty(mphoneData))
        {

            String mphone = mphoneData.getString("TAG_INFO", "");
            serialNumberS = mphone.substring(3, 14);
            serialNumberE = mphone.substring(15, 26);
        }
        // data.put("SERIAL_NUMBER_S", serialNumberS);
        // data.put("SERIAL_NUMBER_E", serialNumberE);

        // 修改code_code为资源接口 02 !@#
        // iResult = dao.queryListByCodeCode("TF_R_MPHONECODE_IDLE", "SEL_BY_MPHONEMATCH", data,pd.getPagination());
        iResult = ResCall.qryMatchingPhone(serialNumberS, serialNumberE);

        if (DataSetUtils.isNotBlank(iResult))
        { // 过滤与TF_F_RELATION_MM中SERIAL_NUMBER_B相同的数据
            /*
             * IDataset ids = RelaMMInfoQry.getAllSnB(); if(DataSetUtils.isNotBlank(ids)){ for(int i=0, s=
             * iResult.size();i<s;i++){ String serialNumber = iResult.get(i, "SERIAL_NUMBER").toString(); for(int j=0,
             * l= ids.size();j<l;j++){ String serialNumberB = ids.get(i, "SERIAL_NUMBER_B").toString();
             * if(serialNumber.equals(serialNumberB)){ iResult.remove(i); } } } }
             */
            for (int i = 0; i < iResult.size(); i++)
            {
                String serialNumber = iResult.get(i, "SERIAL_NUMBER").toString();
                IDataset ids = RelaMMInfoQry.getInfoBySnB(serialNumber);

                if (DataSetUtils.isNotBlank(ids))
                {
                    iResult.remove(i);
                }
            }

        }

        if (iResult.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到记录！");
        }

        return iResult;
    }

    public IData queryMphoneUsed(IData data) throws Exception
    {
        boolean tag = false;
        String serialNumber = data.getString("SERIAL_NUMBER");
        IData userList = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userList))
        {
            IDataset outDataset = TradeInfoQry.getTradeInfoBySn(serialNumber);

            if (DataSetUtils.isBlank(outDataset))
            {
                tag = false;
            }
            else
            {
                tag = true;// 存在未完工工单也可以说明号码已经被使用了
            }
        }
        else
        {
            tag = true;
        }

        IData result = new DataMap();
        result.put("tag", tag);

        return result;
    }

}
