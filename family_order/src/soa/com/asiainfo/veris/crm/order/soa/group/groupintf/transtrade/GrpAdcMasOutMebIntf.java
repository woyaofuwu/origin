
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class GrpAdcMasOutMebIntf
{
    /**
     *
	 * 省行业网关业务黑白名单及销户信息跨省同步,一级BOSS接口
	 *
	 */

    private static final long serialVersionUID = 1L;

    /**
     *
     * 黑白名单成员状态同步:号码归属省销户时，业务受理省退出名单
     * @author
     * @date 2014-12-10
     * @param pd
     * @param data
     * @return
     * @throws Throwable
     */
    public static IDataset destroyOutMeb(IData data) throws Exception
    {
//        String serialNumber = data.getString("MEMBERNUMBER");
        IData param = resolveParamFromIBOSS(data);
        String serialNumber = IDataUtil.getMandaData(param,"MOB_NUM");
        String orderResult = param.getString("BIZ_ORDER_RESULT", "");
        if ("0000".equals(orderResult)) {
            return new DatasetList();
        }

        IDataset bwInfos = UserBlackWhiteInfoQry.getBlackWhiteInfoBySn(serialNumber, Route.CONN_CRM_CG);
        for(int i=0;i<bwInfos.size();i++){
            IData bwInfo = bwInfos.getData(i);
            String ecUserId = bwInfo.getString("EC_USER_ID");

            IData grpPlatSvcInfo = UserGrpPlatSvcInfoQry.getGrpPlatSvcByBizInCode(bwInfo.getString("BIZ_IN_CODE"),ecUserId);
            IData grpProductInfo = UserProductInfoQry.queryProductByUserIdFromDB(ecUserId, Route.CONN_CRM_CG).getData(0);

            IData svcParam = new DataMap();
            svcParam.put("PRODUCT_ID", grpProductInfo.getString("PRODUCT_ID"));
            svcParam.put("MEB_USER_ID", "-1");    // 成员USER_ID
            svcParam.put("SERIAL_NUMBER", serialNumber);  // 成员用户号码
            svcParam.put("USER_ID", ecUserId);

            svcParam.put("BIZ_CTRL_TYPE", BizCtrlType.DestoryMember);
            svcParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

            // 拼服务参数数据
            IData serviceInfo = new DataMap();
            serviceInfo.put("PRODUCT_ID", grpProductInfo.getString("PRODUCT_ID"));
            serviceInfo.put("SERVICE_ID", grpPlatSvcInfo.getString("SERVICE_ID"));
            serviceInfo.put("OPER_TYPE", "02"); // 退出黑白名单
            serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "1"); // 删除

            svcParam.put("SERVICE_INFOS", new DatasetList(serviceInfo));

            // 网外号码处理
            CSAppCall.call("SS.MgrBlackWhiteOutSVC.crtTrade", svcParam);
        }

        IDataset ret = new DatasetList();
        return ret;
    }

    private static IData resolveParamFromIBOSS(IData data) throws Exception{
        IData params = new DataMap();
        String paramnameen = IDataUtil.getMandaData(data, "PARAMNAMEEN");
        String paramvalue = IDataUtil.getMandaData(data, "PARAMVALUE");

        String[] paramnameens = paramnameen.split(",");
        String[] paramvalues = paramvalue.split(",");

        for(int i = 0; i < paramnameens.length; i++) {
            params.put(paramnameens[i], paramvalues[i]);
        }

        return params;
    }


    /*
     * @description 处理成员黑白名单同步信息（号码归属省）
     * @author
     * @date 2014-12-08
     */
    public static IDataset synMebBlackWhiteOut(IData data) throws Exception
    {
        IData dataparam = resolveParamFromIBOSS(data);
        String mobileNumber = IDataUtil.getMandaData(dataparam,"MOB_NUM");

        String backResult = "2999";      // 2999:其他错误
        if(!RouteInfoQry.isChinaMobileNumber(mobileNumber)){
            backResult = "2023";         //2023：非移动品牌号码
        }else{
            IDataset userinfos = UserInfoQry.queryAllUserInfoBySn(mobileNumber);
            if (IDataUtil.isEmpty(userinfos)){
                backResult = "4005";     //4005:号码不存在
            } else {
                IData userinfo = userinfos.getData(0);
                String removeTag = userinfo.getString("REMOVE_TAG");
                if("24".contains(removeTag)){
                    backResult = "2007";   //2007：用户销户
                }else if("013".contains(removeTag)){
                    backResult = "0000";
                }
            }
        }

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", mobileNumber);
        param.put("GROUP_ID",   IDataUtil.getMandaData(data,"ECID"));
        param.put("SERV_CODE",   IDataUtil.getMandaData(data,"SERVCODE"));
        param.put("BIZ_CODE",   IDataUtil.getMandaData(data,"BIZCODE"));
        param.put("PROVINCE_CODE",  IDataUtil.getMandaData(data,"COMPANYID"));
        param.put("SYNC_TYPE",  "02");
        //IDataset list = BlackWhiteOutInfoQry.getBlackWhiteOutInfo(param);

        String operCode = IDataUtil.getMandaData(data,"OPERCODE");
        if("02".equals(operCode)){
            param.put("END_DATE", SysDateMgr.getSysTime());
        } else {
            param.put("START_DATE",  SysDateMgr.getSysTime());
            param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        }

        param.put("OPER_STATE",operCode);
        param.put("BK_RESULT",  backResult);
        param.put("STATUS_CODE",  "01");
        param.put("USER_TYPE_CODE",  IDataUtil.getMandaData(data,"TYPE"));
        param.put("UPDATE_STAFF_ID", "IBOSS");
        param.put("UPDATE_DEPART_ID", "IBOSS");
        // TODO TF_F_BLACKWHITE_OUT 建主键
        Dao.save("TF_F_BLACKWHITE_OUT", param, Route.CONN_CRM_CEN);

        IDataset ret = new DatasetList();
        return ret;
    }

}
