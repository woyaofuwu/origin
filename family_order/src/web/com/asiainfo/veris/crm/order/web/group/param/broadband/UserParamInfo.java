
package com.asiainfo.veris.crm.order.web.group.param.broadband;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.view.IBizCommon;
import com.ailk.bizcommon.priv.StaffPrivUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查页面上需要显示的商务宽带参数信息
        IDataset broadbandInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "BROADBAND", "ZZZZ");
        
        parainfo.put("BROADBAND_INFO", broadbandInfo);
        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");

        //获取权限
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GRP_BROADBAND_FEE_PRV");
        parainfo.put("HAS_FEE_PRIV", bool);
        
        String userId = data.getString("USER_ID");
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);
            parainfo.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
            parainfo.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
            parainfo.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
            parainfo.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));

        }

        // 调用后台服务查保存的商务宽带参数信息
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "N002");

        IDataset userAttrInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 判断OTHER表中有没有数据
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData userAttrData = (IData) userAttrInfo.get(i);
                IData userData = new DataMap();

                userData.put("pam_NOTIN_OPER_TAG", userAttrData.get("RSRV_VALUE"));
                userData.put("pam_NOTIN_NUM", userAttrData.get("RSRV_STR1"));
                //userData.put("pam_NOTIN_MONTHLY_FEE", userAttrData.get("RSRV_STR2"));
                                
                // 安装调试费、一次性费用START_DATE不在当月显示为0
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date nowDate = new Date();
                String startDate4 = userAttrData.getString("START_DATE");
                int nowYear = nowDate.getYear();
                int nowMonth = nowDate.getMonth();
                int startYear = sdf.parse(startDate4).getYear();
                int startMonth = sdf.parse(startDate4).getMonth();
                
                if (nowMonth == startMonth && nowYear == startYear){
                    userData.put("pam_NOTIN_INSTALLATION_COST", userAttrData.get("RSRV_STR3"));//安装调测费
                    userData.put("pam_NOTIN_ONE_COST", userAttrData.get("RSRV_STR4")); //一次性通信服务费
                    
                }else{
                    userData.put("pam_NOTIN_INSTALLATION_COST", "0");
                    userData.put("pam_NOTIN_ONE_COST", "0");
                }
                
                IData broadData = new DataMap();
                broadData.put("USER_ID", userId);
                broadData.put("RSRV_VALUE_CODE", "N003");
                IDataset userBroadInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", broadData);
                
                if (IDataUtil.isNotEmpty(userBroadInfo)){
                    for (int j = 0; j < userBroadInfo.size(); j++)
                    {
                        IData userBroadData = (IData) userBroadInfo.get(j);
                        String broadCode = userBroadData.getString("RSRV_VALUE","");
                        String rsrvStr2 = userBroadData.getString("RSRV_STR2","");
                        
                        if("M4".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_4", rsrvStr2);
                        } else if("M8".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_8", rsrvStr2);
                        } else if("M10".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_10", rsrvStr2);
                        } else if("M12".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_12", rsrvStr2);
                        } else if("M20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_20", rsrvStr2);
                        } else if("M50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_50", rsrvStr2);
                        } else if("M100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_100", rsrvStr2);
                        } else if("CZM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ20", rsrvStr2);
                        } else if("CZM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ50", rsrvStr2);
                        } else if("CZM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ100", rsrvStr2);
                        } else if("CZM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZ200", rsrvStr2);
                        } else if("JCM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC20", rsrvStr2);
                        } else if("JCM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC50", rsrvStr2);
                        } else if("JCM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC100", rsrvStr2);
                        } else if("JCM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JC200", rsrvStr2);
                        } else if("JYM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY50", rsrvStr2);
                        } else if("JYM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY100", rsrvStr2);
                        } else if("JYM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY200", rsrvStr2);
                        } else if("JYM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_JY500", rsrvStr2);
                        } else if("ZZM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ100", rsrvStr2);
                        } else if("ZZM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ200", rsrvStr2);
                        } else if("ZZM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ500", rsrvStr2);
                        } else if("ZZM1000".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_ZZ1000", rsrvStr2);
                        } else if("CZSXM20".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZSX20", rsrvStr2);
                        } else if("CZSXM50".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_CZSX50", rsrvStr2);
                        } else if("SWSXM100".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX100", rsrvStr2);
                        } else if("SWSXM200".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX200", rsrvStr2);
                        } else if("SWSXM500".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_SWSX500", rsrvStr2);
                        } else if("QYSWM1000".equals(broadCode)){
                            userData.put("pam_NOTIN_MONTHLY_FEE_QYSW1000", rsrvStr2);
                        }
                    }
                }
                
                dataset.add(userData);
            }
            parainfo.put("VISP_INFO", dataset);
            parainfo.put("NOTIN_AttrInternet", dataset);
            parainfo.put("NOTIN_OLD_AttrInternet", dataset);

        }
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查页面上需要显示的商务宽带参数信息
        IDataset broadbandInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "BROADBAND", "ZZZZ");

        //获取权限
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GRP_BROADBAND_FEE_PRV");
        parainfo.put("HAS_FEE_PRIV", bool);
        
        parainfo.put("BROADBAND_INFO", broadbandInfo);
        parainfo.put("NOTIN_METHOD_NAME", "CrtUs");
        
        return result;
    }
}
