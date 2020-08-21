
package com.asiainfo.veris.crm.order.web.person.interboss.mobileoperation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.RemoteCrossRegServiceException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CrossRegService extends PersonBasePage
{

    private String codeTransfer(String type, String value, String flag)
    {
        String ret = "";
        if (flag.equals("1"))
        { // 从一级BOSS转为CRM代码
            if (type.equals("1"))
            {// 积分类型
                if ("0".equals(value))
                    ret = "G001";
                if ("1".equals(value))
                    ret = "G010";
            }
            else
            {// 客户级别
                if ("0".equals(value))
                    ret = "0";
                if ("1".equals(value))
                    ret = "3";
                if ("2".equals(value))
                    ret = "2";
                if ("3".equals(value))
                    ret = "1";
            }
        }
        else
        {
            if (type.equals("1"))
            {// 积分类型
                if ("G001".equals(value))
                    ret = "0";
                if ("G010".equals(value))
                    ret = "1";
            }
            else
            {// 客户级别
                if ("0".equals(value))
                    ret = "0";
                if ("3".equals(value))
                    ret = "1";
                if ("2".equals(value))
                    ret = "2";
                if ("1".equals(value))
                    ret = "3";
            }
        }
        return ret;
    }

    /**
     * 查询客户资料
     * 
     * @param cycle
     * @throws Exception
     */
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        IDataset dataset = CSViewCall.call(this, "SS.RemoteCrossRegServiceSVC.getCustInfo", param);
        setCondition(getData("cond"));
        /*
         * LanuchUtil logutil = new LanuchUtil(); data.put("EPARCHY_NAME",
         * logutil.getEparchyName(data.getString("EPARCHY_NAME")));
         */
        refresh(dataset.getData(0));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.put("cond_ROUTETYPE", "01");
        setCondition(cond);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData idata = getData();

        if (!"1".equals(idata.getString("DISABLED_FLAG")))
        {
            CSViewException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_2);
        }

        idata.put("SERIAL_NUMBER", idata.getString("cond_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.RemoteCrossRegServiceSVC.updateInfo", idata);
        /*
         * IData data = dataset.getData(0); if (!"0".equals(data.getString("X_RSPTYPE")) ||
         * !"0000".equals(data.getString("X_RSPCODE"))){ if ("2998".equals(data.getString("X_RSPCODE"))){
         * data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC")); }
         * CSViewException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_3,data.getString("X_RSPDESC")); }
         */
        setAjax(dataset);
    }

    public void refresh(IData data) throws Exception
    {
        IData info = new DataMap();

        info.put("SCORE_VALUE", data.getString("ABLE_SCORE") == null ? "" : data.getString("ABLE_SCORE"));
        info.put("CLASS_LEVEL", data.getString("CLASS_LEVEL") == null ? "" : codeTransfer("0", data.getString("CLASS_LEVEL"), "1"));// 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
        // 不要转就好了
        info.put("ALL_CON_SCORE", data.getString("ALL_CON_SCORE", ""));
        info.put("LEVEL_DATE", data.getString("LEVEL_DATE", ""));
        info.put("JOIN_DATE", data.getString("JOIN_DATE") == null ? "" : data.getString("JOIN_DATE"));
        /*
         * RESULT:00－成功 01－无此手机号码 02－手机号码与用户姓名不符 03－手机号码与证件号码不符 04－证件号码错误 05－客服密码错误
         */
        if ("0".equals(data.getString("X_RSPTYPE")) && "0000".equals(data.getString("X_RSPCODE")) && "0000".equals(data.getString("RESULT")))
        {
            info.put("DISABLED_FLAG", "1");
            info.putAll(data);
            setCust(info);
        }
        else
        {
            info.put("DISABLED_FLAG", "0");
            String errorInfo = "";
            if ("0000".equals(data.getString("X_RSPCODE")))
            {
                errorInfo = "接口文档中必须返回字段RESULT无内容！00－成功";
                if ("01".equals(data.getString("RESULT")))
                    errorInfo = "无此手机号码";
                if ("02".equals(data.getString("RESULT")))
                    errorInfo = "手机号码与用户姓名不符";
                if ("03".equals(data.getString("RESULT")))
                    errorInfo = "手机号码与证件号码不符";
                if ("04".equals(data.getString("RESULT")))
                    errorInfo = "证件号码错误";
                if ("05".equals(data.getString("RESULT")))
                    errorInfo = "客服密码错误";
            }
            if ("2998".equals(data.getString("X_RSPCODE")))
            {
                data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC"));
            }
            CSViewException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_1, data.getString("X_RSPDESC") + "<br>" + errorInfo);
            setCust(null);
        }

    }

    public abstract void setCondition(IData condition);

    public abstract void setCust(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
