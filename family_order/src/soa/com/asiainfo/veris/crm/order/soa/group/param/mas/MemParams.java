
package com.asiainfo.veris.crm.order.soa.group.param.mas;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;

public class MemParams
{

    /**
     * 2014-03-28 用户取平集团参数
     * 
     * @param userIdA
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static IData getUserAPlatSvcParam(String userIdA, String serviceId) throws Exception
    {
        IData platsvcparam = new DataMap();

        IDataset tempLists = AttrBizInfoQry.getBizAttrByDynamic(serviceId, "S", "Obver", serviceId, null);

        if (IDataUtil.isEmpty(tempLists))
        {
            platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userIdA, serviceId);// 取平集团用户参数
        }
        else
        {
            for (int i = 0; i != tempLists.size(); ++i)
            {
                String service_id = tempLists.getData(i).getString("ATTR_VALUE");
                platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userIdA, service_id);
                // 取平集团用户参数
                if (!platsvcparam.isEmpty())
                {
                    break;
                }
            }
        }
        return platsvcparam;
    }

    public IDataset getServiceParam(IData params) throws Exception
    {
        IData platsvcParam = this.getServicePlatsvcParam(params);

        String newTag = platsvcParam.getString("pam_MODIFY_TAG");
        IData checkmap = new DataMap();
        if ("0".equals(newTag))
        {
            checkmap.put("PARAM_VERIFY_SUCC", "false");// 当是新增服务时，参数自然没有
        }
        else
        {
            checkmap.put("PARAM_VERIFY_SUCC", "true");// 当是修改服务时，参数自然是已有，故下一步按钮是要放过的
        }

        IDataset paramDataset = new DatasetList();
        IData serparam = new DataMap();
        serparam.put("PLATSVC", platsvcParam);
        serparam.put("ID", params.getString("SERVICE_ID", ""));
        paramDataset.add(0, checkmap);
        paramDataset.add(1, serparam);
        return paramDataset;
    }

    /**
     * 获取服务个性化参数显示信息
     * 
     * @author zouli
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public IData getServicePlatsvcParam(IData params) throws Exception
    {
        String userId = params.getString("USER_ID", "");
        String userIdA = params.getString("USER_ID_A", "");
        String serviceId = params.getString("SERVICE_ID", "");

        IData memplatsvc = new DataMap();
        // 取GRP_MEB_PLATSVC平台服务表已经存在的参数
        IDataset mebPlatsvcset = UserGrpMebPlatSvcInfoQry.getMemPlatSvc(userId, userIdA, serviceId);

        // 取黑白名单已经存在的参数
        IDataset blackwhiteset = UserBlackWhiteInfoQry.getBlackWhitedatauserIdUserIdaSvcid(userId, userIdA, serviceId);

        // 取集团用户GRP_Platsvc数据
        IData platsvcparam = getUserAPlatSvcParam(userIdA, serviceId);

        if (IDataUtil.isNotEmpty(mebPlatsvcset) && IDataUtil.isNotEmpty(blackwhiteset))// 这个if真分支表示成员已存在，即是修改
        {
            IData blackwhite = blackwhiteset.getData(0);
            memplatsvc.put("MODIFY_TAG", "2");// 标识修改
            memplatsvc.put("BIZ_CODE", blackwhite.getString("BIZ_CODE", ""));// 业务代码
            memplatsvc.put("BIZ_NAME", blackwhite.getString("BIZ_NAME", ""));// 业务名称
            memplatsvc.put("BIZ_IN_CODE", blackwhite.getString("BIZ_IN_CODE", ""));// 业务接入号
            memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("EC_BASE_IN_CODE_A"));// 业务接入号属性
            memplatsvc.put("BIZ_ATTR", platsvcparam.getString("BIZ_ATTR", ""));// 业务属性
            memplatsvc.put("EXPECT_TIME", blackwhite.getString("EXPECT_TIME", ""));// 用户期望生效时间
            String strSynState = blackwhite.getString("PLAT_SYNC_STATE", "");// 当前服务状态 ;
            String openState = blackwhite.getString("OPER_STATE");

            if (StringUtils.isBlank(strSynState)) // 将来要报异常，表示未得到更新结果
            {
                strSynState = "1";
            }
            memplatsvc.put("PLAT_SYNC_STATE", strSynState);// 用户服务状态

            if ("04".equals(openState))// "OPER_STATE":"04" 表示暂停
            {
                memplatsvc.put("PLAT_SYNC_STATE", "P");
            }

            memplatsvc.put("SERVICE_ID", serviceId);
            memplatsvc.put("OPER_STATE", openState);
            memplatsvc.put("GRP_PLAT_SYNC_STATE", platsvcparam.getString("PLAT_SYNC_STATE", ""));
        }
        else
        {
            // 此if分支表明是新增成员服务参数，因为没找到成员平台，黑白名单信息
            memplatsvc.put("MODIFY_TAG", "0");// 标识新增
            memplatsvc.put("BIZ_CODE", platsvcparam.getString("BIZ_CODE"));// 业务代码
            memplatsvc.put("BIZ_NAME", platsvcparam.getString("BIZ_NAME"));// 业务名称
            memplatsvc.put("BIZ_IN_CODE", platsvcparam.getString("BIZ_IN_CODE"));// 业务接入号
            memplatsvc.put("BIZ_IN_CODE_A", platsvcparam.getString("EC_BASE_IN_CODE_A"));// 业务接入号属性
            memplatsvc.put("BIZ_ATTR", platsvcparam.getString("BIZ_ATTR"));// 业务属性
            memplatsvc.put("EXPECT_TIME", SysDateMgr.getSysTime());// 用户期望生效时间
            memplatsvc.put("PLAT_SYNC_STATE", "1");// 用户服务状态
            memplatsvc.put("SERVICE_ID", serviceId);
            memplatsvc.put("OPER_STATE", "01");
            memplatsvc.put("GRP_PLAT_SYNC_STATE", platsvcparam.getString("PLAT_SYNC_STATE", ""));

        }
        memplatsvc = IDataUtil.replaceIDataKeyAddPrefix(memplatsvc, "pam_");
        return memplatsvc;
    }

}
