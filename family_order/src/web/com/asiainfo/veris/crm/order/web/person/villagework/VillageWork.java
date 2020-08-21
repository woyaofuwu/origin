
package com.asiainfo.veris.crm.order.web.person.villagework;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.UserPccException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户动态策略计费控制信息导入
 */
public abstract class VillageWork extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(VillageWork.class);

    public static final String msgSuc = "S";

    public static final String msgFail = "F";

    public static final String msgKey = "MSG";

    public static final String msgTypeKey = "MSG_TYPE";

    public static final String msgNameKey = "NAME";

    public void checkInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();

        IDataset infos = CSViewCall.call(this, "SS.VillageWorkSVC.checkInfos", pageData);

    }

    public void checkList(IData data) throws Exception
    {
        IDataset infos = CSViewCall.call(this, "SS.VillageWorkSVC.checkInfos", data);

        IDataset otherList = CSViewCall.call(this, "SS.VillageWorkSVC.queryNumInfos", data);

        IDataset serialLists = new DatasetList(data.getString("serialData"));

        otherList.addAll(serialLists);
        for (int i = 0; i < serialLists.size(); i++)
        {
            String serA = serialLists.getData(i).getString("SERIAL_NUMBER");
            int flag = 0;

            String sn = data.getString("AUTH_SERIAL_NUMBER");

            for (int k = 0; k < otherList.size(); k++)
            {

                String serB = otherList.getData(k).getString("SERIAL_NUMBER");
                // 删除不做判断
                if (!"1".equals(serialLists.getData(i).getString("tag")))
                {

                    if (sn.equals(serA))
                    {
                        CSViewException.apperr(UserPccException.CRM_UserPccInfo_09, serA);
                    }

                    if (serA.equals(serB))
                    {
                        flag++;
                        if (flag > 1)
                        {
                            CSViewException.apperr(UserPccException.CRM_UserPccInfo_06, serA);
                        }

                    }
                }
            }
        }

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pageData = getData();
        IData userInfo = new DataMap(pageData.getString("USER_INFO"));
        pageData.putAll(userInfo);

        // 交易用户服务是否开通
        if (!"10000773".equals(userInfo.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(UserPccException.CRM_UserPccInfo_02);
        }

        IDataset infos = CSViewCall.call(this, "SS.VillageWorkSVC.queryNumInfos", pageData);

        setEditList(infos);

        setSum(infos.size());

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     *             zhuyu 2014-4-14
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 校验号码
        checkList(data);

        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.VillageWorkRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setEditList(IDataset editList);

    public abstract void setSum(int info);

}
