/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.valuecard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-6-6 修改历史 Revision 2014-6-6 上午11:10:32
 */
public abstract class CancelValueCard extends PersonBasePage
{

    public void addClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getValueCardInfo", data);

        IDataset set1 = results.getData(0).getDataset("TABLE1");
        IDataset set2 = results.getData(0).getDataset("TABLE2");

        String temp = data.getString("table2");
        IDataset tempSet = new DatasetList(temp);
        if (tempSet.size() > 0)
        {

            set2.addAll(tempSet);
        }
        setAjax(set2);

        this.setBasicInfos(set1);
        this.setSaleInfos(set2);

    }

    public void importClick(IRequestCycle cycle) throws java.lang.Exception
    {

        IData param = getData();

        param.put("STAFF_ID", param.getString("import_STAFF_ID", ""));
        param.put("CITY_CODE", param.getString("import_CITY_CODE", ""));
        param.put("CARD_LIST", param.getString("import_CARD_LIST", ""));

        param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.ValueCardMgrSVC.getResInfoForImport", param);

        setBasicInfos(results.getData(0).getDataset("TABLE1"));

    }

    public void importFile(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        String fileId = data.getString("FILE_FIELD1");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        File file = ImpExpUtil.getImpExpManager().getFileAction().download(fileId);

        String line;
        String rows = "", separator = ",";
        InputStream stream = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        while ((line = in.readLine()) != null)
        {
            rows += separator + line.trim();
        }
        if (!"".equals(rows))
        {
            rows = rows.substring(separator.length());
        }
        in.close();
        this.setAjax("TEXT_CONTENT", rows);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        // 查询区域信息
        IData svcData = new DataMap();

        svcData.put("AREA_FRAME", getTradeEparchyCode());

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        setCityList(cityList);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.CancelGiveValueCardRegSVC.tradeReg", data);

        this.setAjax(results);

    }

    public abstract void setBasicInfos(IDataset dataset);

    public abstract void setCityList(IDataset cityList);

    public abstract void setCond(IData cond);

    public abstract void setSaleInfos(IDataset dataset);

}
