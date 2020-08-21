
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SaleActiveStop extends GroupBasePage
{

    public abstract void setInfo(IData info);

    public abstract void setActives(IDataset actives);

    public abstract void setServs(IDataset servs);

    public abstract void setDiscnts(IDataset discnts);

    /**
     * 根据集团服务号码查询营销活动
     * 
     * @param cycle
     * @throws Exception
     */
    public void getSaleActiveInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 查询集团用户信息
        String serial_number = data.getString("cond_GROUP_SERIAL_NUMBER");
        IData grpUserData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serial_number);
        // 根据用户id查询营销活动信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserData.getString("USER_ID"));
        inparam.put("PROCESS_TAG", "0");
        IDataset saleActives = CSViewCall.call(this, "CS.UserSaleActiveInfoQrySVC.getSaleActiveByUserIdProcessTag", inparam);
        setActives(saleActives);
        setInfo(data);
    }

    /**
     * 查询营销活动的优惠
     * 
     * @param cycle
     * @throws Exception
     */
    public void querySaleParams(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = new DataMap();
        inparam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        inparam.put("CAMPN_ID", data.getString("CAMPN_ID"));
        inparam.put("USER_ID", data.getString("USER_ID"));

        // 查询营销活动服务
        IDataset servs = CSViewCall.call(this, "CS.UserSaleActiveInfoQrySVC.querySaleServs", inparam);
        setServs(servs);

        // 查询营销活动优惠
        IDataset discnts = CSViewCall.call(this, "CS.UserSaleActiveInfoQrySVC.querySaleDiscnts", inparam);
        setDiscnts(discnts);
    }

    /**
     * 营销活动终止提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String discntStr = data.getString("DISCNT_STR");
        String activeStr = data.getString("SALEACTIVE_STR");

        IData inparam = new DataMap();
        IDataset discnts = string2Dataset(discntStr);
        IData saleActive = string2Data(activeStr);
        inparam.put("SALE_DISCNT", discnts);
        inparam.put("SALE_ACTIVE", saleActive);
        inparam.put("USER_ID", data.getString("USER_ID"));
        inparam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        IDataset retDataset = CSViewCall.call(this, "SS.SaleActiveSVC.crtStopTrade", inparam);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 将字符串转换成Dataset
     * 
     * @param datasetStr格式
     *            ：key1=value1,key2=value2……@key1=value3,key2=value4……
     * @return
     * @throws Exception
     */
    public IDataset string2Dataset(String datasetStr) throws Exception
    {
        IDataset dataset = new DatasetList();
        if (StringUtils.isEmpty(datasetStr))
        {
            return dataset;
        }

        String[] dataStrs = datasetStr.split("@");

        for (int i = 0, len = dataStrs.length; i < len; i++)
        {
            IData data = new DataMap();

            String[] keyValues = dataStrs[i].split(",");

            for (int j = 0, size = keyValues.length; j < size; j++)
            {
                String[] keyValue = keyValues[j].split("=");

                data.put(keyValue[0], keyValue[1]);
            }
            dataset.add(data);
        }

        return dataset;
    }

    /**
     * 将字符串转换成Data
     * 
     * @param dataStr格式
     *            ：key1=value1,key2=value2……
     * @return
     * @throws Exception
     */
    public IData string2Data(String dataStr) throws Exception
    {
        IData data = new DataMap();
        if (StringUtils.isEmpty(dataStr))
        {
            return data;
        }

        String[] keyValues = dataStr.split(",");

        for (int i = 0, len = keyValues.length; i < len; i++)
        {
            String[] keyValue = keyValues[i].split("=");

            data.put(keyValue[0], keyValue[1]);
        }
        return data;
    }
}
