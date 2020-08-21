
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.nfcpinfo;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.asiainfo.veris.crm.order.web.person.sundryquery.score.TransferPoint;

/**
 * @CREATED by gongp@2013-9-20
 */
public abstract class NFCPInfoQuery extends PersonBasePage
{	

    public IDataset getDatasetFromData(IData data, String[] arr) throws Exception
    {
        IDataset dataset = new DatasetList();
        IDataset[] datas = new DatasetList[arr.length];

        if (data.get(arr[0]) instanceof String)
        {
        	
            for (int i = 0; i < arr.length; i++)
            {
                datas[i].add(data.get(arr[i]));
            }
            
        }
        else
        {	
        	
            for (int i = 0; i < arr.length; i++)
            {
            
                datas[i] = data.getDataset(arr[i]);
              
            }
        }

        if (datas[0] != null && datas[0].size() > 0)
        {

            for (int i = 0; i < datas[0].size(); i++)
            {

                IData temp = new DataMap();
                for (int j = 0; j < arr.length; j++)
                {

                    temp.put(arr[j], datas[j].get(i));
                }
                dataset.add(temp);
            }
        }
        
        return dataset;
    }

    public void init() throws Exception
    {

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData custInfo = new DataMap();
        IData userInfo = new DataMap();
        IData params = new DataMap();
        IData params2 = new DataMap();
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        params.put("REMOVE_TAG", "0");
        IDataset userInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfoBySerialNumber", params);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos.getData(0);
            params2.put("CUST_ID", userInfo.getString("CUST_ID", ""));
            params2.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        }
        IDataset custInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryCustInfo", params2);
        if (IDataUtil.isNotEmpty(custInfos))
        {
            custInfo = custInfos.getData(0);
        }
        IData resultData = CSViewCall.call(this, "SS.QryPlatsvcSVC.qryNFCPInfo", userInfo).getData(0);

        if ("0".equals(resultData.getString("X_RSPTYPE")) && "0000".equals(resultData.getString("X_RSPCODE")))
        {

            String[] arr = new String[]
            { "ORDER_SEQ", "APP_NAME", "DOWNLOAD_TIME", "APP_SUPPLY", "APP_TYPE", "OTHER_INFO" };
           
            this.setInfos(getDatasetFromData(resultData, arr));
            this.setCustInfo(custInfo);
            //this.setAjax(resultData);
            IData result = new DataMap();
            
            result.put("X_RSPCODE", resultData.getString("X_RSPCODE"));
            result.put("X_RSPTYPE", resultData.getString("X_RSPTYPE"));
           // result.put("X_RSPDESC", resultData.getString("X_RSPDESC"));
            
            this.setAjax(result);
        }
        else
        {
            this.setCustInfo(custInfo);
            this.setAjax(resultData);
            IData result = new DataMap();
            result.put("X_RSPCODE", resultData.getString("X_RSPCODE"));
            result.put("X_RSPTYPE", resultData.getString("X_RSPTYPE"));
            result.put("X_RSPDESC", resultData.getString("X_RSPDESC"));
            
            this.setAjax(result);
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);

}
