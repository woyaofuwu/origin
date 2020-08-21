
package com.asiainfo.veris.crm.order.web.group.returnratio;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryReturnRatio extends GroupBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        IData condData = getData("cond", true);

        String staffId = getVisit().getStaffId();

        // 查询区域信息
        IData svcData = new DataMap();

        if (staffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
        }

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        condData.put("CITY_CODE", getVisit().getCityCode());

        // 设置返回值
        setMessage("请输入查询条件");
        setCityList(cityList);
        setCondition(condData);
    }

    /**
     * 查询返回比例信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryRatioList(IRequestCycle cycle) throws Exception
    {
        IDataset dataList = new DatasetList();
        IData condData = getData("cond", true);

        IData data = new DataMap();
        data.put("GROUP_ID", condData.getString("GROUP_ID"));
        data.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        data.put("START_STAFF_ID", condData.getString("START_STAFF_ID"));
        data.put("END_STAFF_ID", condData.getString("END_STAFF_ID"));
        data.put("START_DATE", condData.getString("START_DATE"));
        data.put("END_DATE", condData.getString("END_DATE"));
        data.put("CITY_CODE", condData.getString("CITY_CODE"));

        Pagination pg = getPagination("ratioNavBar");
        IDataOutput outPut = CSViewCall.callPage(this, "SS.ReturnRationSVC.queryGroupProductInfo", data, getPagination("ratioNavBar"));

        dataList = outPut.getData();
        long ratioCount = outPut.getDataCount();

        // if (IDataUtil.isNotEmpty(dataList))
        // {
        setRatioList(dataList);
        setRatioCount(ratioCount);
        // setMessage("查询成功");
        // }else
        // {
        // setMessage("没有符合条件的查询结果~~！");
        // }

        String loginStaffId = getVisit().getStaffId(); // 系统登录工号

        // 查询区域信息
        IData svcData = new DataMap();

        if (loginStaffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
        }

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        condData.put("CITY_CODE", getVisit().getCityCode());

        setCityList(cityList);
        setCondition(condData);

    }

    public abstract void setCityList(IDataset cityList);

    public abstract void setCondition(IData condition);

    public abstract void setMessage(String message);

    public abstract void setRatioCount(long ratioCount);

    public abstract void setRatioList(IDataset ratioList);
}
