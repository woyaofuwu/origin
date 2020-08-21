package com.asiainfo.veris.crm.iorder.web.person.smartaddvalue;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/4/8 20:31
 */
public abstract class DredgeSmartNetworkNew extends PersonBasePage {
    /**
     * 页面初始化方法
     *
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData initParam = new DataMap();
        initParam.put("authType", data.getString("authType", "00"));
        initParam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", "870"));
        IDataset firstTimes = StaticUtil.getStaticList("SMART_FIRST_TYPE_TIME");
        IDataset firstMonths = StaticUtil.getStaticList("SMART_FIRST_TYPE_MONTH");
        IDataset secondTimes = StaticUtil.getStaticList("SMART_SECOND_TYPE_TIME");
        IDataset secondMonths = StaticUtil.getStaticList("SMART_SECOND_TYPE_MONTH");
        IDataset thirdTimes = StaticUtil.getStaticList("SMART_THIRD_TYPE_TIME");
        IDataset thirdMonths = StaticUtil.getStaticList("SMART_THIRD_TYPE_MONTH");
        DataHelper.sort(thirdTimes,"DATA_NAME",IDataset.TYPE_STRING);
        DataHelper.sort(thirdMonths,"DATA_NAME",IDataset.TYPE_STRING);

        initParam.put("SMART_FIRST_TYPE_TIME",checkFuncDataPriv(firstTimes));
        initParam.put("SMART_FIRST_TYPE_MONTH",checkFuncDataPriv(firstMonths));
        initParam.put("SMART_SECOND_TYPE_TIME",checkFuncDataPriv(secondTimes));
        initParam.put("SMART_SECOND_TYPE_MONTH",checkFuncDataPriv(secondMonths));
        initParam.put("SMART_THIRD_TYPE_TIME",checkFuncDataPriv(thirdTimes));
        initParam.put("SMART_THIRD_TYPE_MONTH",checkFuncDataPriv(thirdMonths));
        setInfo(initParam);
    }

    /**
     * 校验权限
     * @param discnts
     * @return
     * @throws Exception
     */
    private IDataset checkFuncDataPriv(IDataset discnts) throws Exception {
        IDataset discntsAfterPriv = new DatasetList();
        for (int i = 0; i < discnts.size(); i++) {
            String discntCode=discnts.getData(i).getString("DATA_ID");
            String staffId = getVisit().getStaffId();
            boolean isDistPriv = StaffPrivUtil.isDistPriv(staffId,discntCode);
            if(isDistPriv){
                discntsAfterPriv.add(discnts.get(i));
            }
        }
        return discntsAfterPriv;
    }
    /**
     * 业务提交
     *
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData authData = getData("AUTH", true);
        data.putAll(authData);
        IData otherData = getData("otherinfo", true);
        data.putAll(otherData);

        String selectType = data.getString("SELECT_TYPE");
        if(StringUtils.isBlank(selectType)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "智能组网开通类别选择异常!");
        }
        data.put("FIRST_TYPE",data.getString("FIRST_TYPE_"+selectType.trim()));
        data.put("SECOND_TYPE",data.getString("SECOND_TYPE_"+selectType.trim()));
        data.put("THIRD_TYPE",data.getString("THIRD_TYPE_"+selectType.trim()));
        IDataset dataset = CSViewCall.call(this, "SS.DredgeSmartNetworkRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author lizj
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkIntfSVC.checkFeeBeforeSubmit", data);
        this.setAjax(result);
    }

    /**
     * 提交前宽带校验
     * @param cycle
     * @throws Exception
     * @author
     */
    public void checkWideBeforeSubmit(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkIntfSVC.checkQualificate", data);
        this.setAjax(result);
    }

    /**
     * 获取设备
     * @param cycle
     * @throws Exception
     * @author
     */
    public void getDevice(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.DredgeSmartNetworkSVC.getDevice", data);
        this.setAjax(result);
    }
    /**
     * WIFI调测包校验
     * @param cycle
     * @throws Exception
     * @author
     */
    public void checkUserClass(IRequestCycle cycle) throws Exception{
        IData data = getData();
        String serialNumber = data.getString("SERIAL_NUMBER");
        if(StringUtils.isBlank(serialNumber)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "请输入手机号!");
        }
        String typeCode = data.getString("TYPE_CODE");
        //全球通等级匹配wifi服务包
        IData param=new DataMap();
        param.put("SERIAL_NUMBER",serialNumber);
        IDataset dataset = CSViewCall.call(this, "SS.UserInfoQrySVC.queryUserClassBySN", param);
        String userClass="";
        if(IDataUtil.isNotEmpty(dataset)){
            userClass=dataset.first().getString("USER_CLASS");
        }
        IDataset alls = StaticUtil.getStaticList("SMART_THIRD_TYPE_ALL");
        IDataset halfs = StaticUtil.getStaticList("SMART_THIRD_TYPE_HALF");
        IDataset zeros = StaticUtil.getStaticList("SMART_THIRD_TYPE_ZERO");
        if("1".equals(userClass)){
            for (int i = 0; i < zeros.size(); i++) {
                if(typeCode.equals(zeros.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通金卡及以上客户办理!");
                }
            }
            for (int i = 0; i < alls.size(); i++) {
                if(typeCode.equals(alls.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "全球通银卡及以上用户不能办理原价包!");
                }
            }
        }else if("2".equals(userClass)||"3".equals(userClass)||"4".equals(userClass)){
            for (int i = 0; i < halfs.size(); i++) {
                if(typeCode.equals(halfs.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通银卡客户办理!");
                }
            }
            for (int i = 0; i < alls.size(); i++) {
                if(typeCode.equals(alls.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "全球通银卡及以上用户不能办理原价包!");
                }
            }
        }else{
            for (int i = 0; i < zeros.size(); i++) {
                if(typeCode.equals(zeros.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通金卡及以上客户办理!");
                }
            }
            for (int i = 0; i < halfs.size(); i++) {
                if(typeCode.equals(halfs.getData(i).getString("DATA_ID"))){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "仅限全球通银卡客户办理!");
                }
            }
        }
    }
    public abstract void setAuthType(String authType);

    public abstract void setTradeTypeCode(String tradeTypeCode);

    public abstract void setInfo(IData info);
}
