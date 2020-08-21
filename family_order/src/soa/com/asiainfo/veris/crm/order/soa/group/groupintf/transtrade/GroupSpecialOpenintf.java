
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;

public class GroupSpecialOpenintf
{

    /**
     * 集团业务特殊开机办理接口
     * @param data
     * @return
     * @throws Exception
     */ 
    public static IData groupSpecialOpen(IData data) throws Exception
    {
        IData result = new DataMap();
        data = dealInparam(data);
        String services = data.getString("sercheck");
        if (StringUtils.isBlank(services))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有选择要恢复的服务或该集团下的服务都是正常状态,不用办理恢复");
        }

        // 以前的param中还有一个PARA_CODE4 =TIME 调用的服务中没用到，
        String eparchy_code = data.getString("TRADE_EPARCHY_CODE");
        IDataset commParams = CommparaInfoQry.getCommparaInfoBy5("CSM", "1111", "TYPECODE", "csGroupSpecialOpen", eparchy_code, null);
        if (IDataUtil.isNotEmpty(commParams))
        {
            for (int i = 0; i < commParams.size(); i++)
            {
                IData commParam = commParams.getData(i);
                if ("TIME".equals(commParam.getString("PARA_CODE4")))
                {
                    int max_time = Integer.parseInt(commParam.getString("PARA_CODE5"));
                    int normal_time = Integer.parseInt(data.getString("NORMAL_TIME"));
                    if (normal_time > max_time)
                    {
                        String resultinfo = "最大时长只能为:" + max_time + "(小时),请重新修改后提交";
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, resultinfo);
                    }
                    break;
                }
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取系统配置最大恢复时长失败");
        }

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("sercheck", data.getString("sercheck"));
        svcData.put("GROUP_ID", data.getString("GROUP_ID"));
        svcData.put("USER_ID", data.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcData.put("NORMAL_TIME", data.getString("NORMAL_TIME"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

        // 调用服务
        IDataset dataset = CSAppCall.call("SS.GroupSpecialOpenSVC.crtTrade", svcData);

        //[{"ORDER_ID":"1115090206932445","DB_SOURCE":"0898","TRADE_ID":"1115090241446846"}]
        if(IDataUtil.isNotEmpty(dataset) && !(null==dataset.getData(0).getString("TRADE_ID"))){
            // 设置返回数据        
            result.put("X_RESULTINFO", "SUCCESS");
            result.put("X_RESULTCODE", "0");
            result.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));
            return result;
        }else{
            // 设置返回数据        
            result.put("X_RESULTINFO", "FAILD");
            result.put("X_RESULTCODE", "2998");
            result.put("TRADE_ID", null);
            return result;
        }
        

    }
    
    /**
     * 根据集团用户编码USER_ID查询已订购服务信息
     * 查出非正常状态的服务SERVICE_ID(可办理特殊开机的服务)
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IData dealInparam(IData inparam) throws Exception
    {
        String userid = IDataUtil.getMandaData(inparam, "USER_ID");

        IDataset userStates = UserGrpPlatSvcInfoQry.getUserAttrByUserIda(userid);// 查询用户当前订购的服务
        String ids = "";
        //查出非正常状态的服务SERVICE_ID(可办理特殊开机的服务)
        if (IDataUtil.isNotEmpty(userStates))
        {
            for (int i = 0; i < userStates.size(); i++)
            {
                IData data = userStates.getData(i);
                if(!data.getString("BIZ_STATE_CODE").equals("A")){
                    ids += "||"+data.getString("SERVICE_ID");
                    inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                }
            }
        }
        //过滤掉不可办理特殊开机的服务
        String[] serviceIds = inparam.getString("SERVICE_ID","").split(",");
        String sercheck = "";
        if(serviceIds.length>0)
        {
            for (int i = 0; i < serviceIds.length; i++)
            {
                if(ids.contains(serviceIds[i]))
                {
                    sercheck += serviceIds[i]+",";
                }
            }
        }
        if(sercheck.length()>0)sercheck = sercheck.substring(0, sercheck.length() - 1);
        inparam.put("sercheck", sercheck);
        return inparam;
    }
}
