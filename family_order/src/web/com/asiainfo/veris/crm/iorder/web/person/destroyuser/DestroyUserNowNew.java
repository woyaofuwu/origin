package com.asiainfo.veris.crm.iorder.web.person.destroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class DestroyUserNowNew extends PersonBasePage {

    public abstract void setBusiInfo(IData custInfo);

    public abstract void setDestroyInfo(IData destroyInfo);

    public abstract void setInfo(IData info);

    public abstract void setReasonItem(IData reasonItem);

    /**
     * 查询用户积分
     *
     * @param cycle
     * @throws Exception
     */
    public void checkScore(IRequestCycle cycle) throws Exception {
        IDataset screDataset = new DatasetList();
        IData info = getData();
        if (StringUtils.isEmpty(info.getString("SERIAL_NUMBER", ""))) {
            info.put("SERIAL_NUMBER", info.getString("AUTH_SERIAL_NUMBER"));
        }
        info.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        screDataset = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserScore", info);
        String score = "0";
        if (IDataUtil.isNotEmpty(screDataset)) {
            score = screDataset.getData(0).getString("SCORE");
        }
        setAjax("SCORE", score);
    }


    /**判断是否为海洋通 船东用户  船东不允许销户
     * @param cycle
     * @throws Exception
     * xuzh5 2018-7-2 14:52:49
     */
    public void checkUserInfo(IRequestCycle cycle) throws Exception
    {
        IData info = getData();
        IData param=new DataMap();
        String eparchyCode = getTradeEparchyCode();
        String userFlag="0";
        param.put("USER_ID", info.getString("USER_ID",""));
        param.put("RSRV_VALUE_CODE", "HYT");
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset userInfos = CSViewCall.call(this,"CS.UserOtherInfoQrySVC.getUserOtherByUseridRsrvcode", param);
        if(IDataUtil.isNotEmpty(userInfos)){
            if("1".equals(userInfos.getData(0).getString("RSRV_STR2")))
                userFlag="1";//船东状态
        }
        IData res=new DataMap();
        res.put("FLAG", userFlag);
        setAjax(res);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param: @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-5 下午07:59:29 Modification History: Date Author Version
     * Description ---------------------------------------------------------*
     * 2014-8-5 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IData pgData = this.getData();
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE", "192");
        String authType = pgData.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setInfo(info);
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", ""))) {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.DestroyUserNowRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 认证结束之后，设置用户相关信息
     *
     * @param cycle
     * @throws Exception
     */
    public void setPageCustInfo(IRequestCycle cycle) throws Exception {
        IData pagedata = getData();
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER", "0"));
        inParam.put("USER_ID", pagedata.getString("USER_ID", "0"));
        inParam.put(Route.ROUTE_EPARCHY_CODE, pagedata.getString("EPARCHY_CODE", ""));
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", inParam);
        setBusiInfo(busiInfos.getData(0));
        // 设置公共值
        IData destroyData = new DataMap();
        destroyData.put("REMARK", "");
        destroyData.put("REMOVE_REASON", "");
        setDestroyInfo(destroyData);

        IData ImsInfo = null;
        try {
            //查询用户固话信息
            ImsInfo = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", inParam);
        } catch (Exception e) {
        }
        if (IDataUtil.isNotEmpty(ImsInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "用户存在IMS固话，请先办理IMS固话拆机！");
        }
        //REQ201807230029一机多宽业务需求1.0版本
        IDataset tfRelations = null;
        try {
            //查询手机用户统付关系
        	tfRelations = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryTFRelation", inParam);
        } catch (Exception e) {
        }
        if (IDataUtil.isNotEmpty(tfRelations)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "用户存在统付的无手机宽带，不能办理立即销户！");
        }
        //REQ201807230029一机多宽业务需求1.0版本
    }

}
