
package com.asiainfo.veris.crm.order.web.group.imsmanage.groupauditmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpAuditWelcomeVoice extends GroupBasePage
{

    /**
     * 按条件查询批量信息
     * 
     * @description
     * @author xiaozp
     * @date Jun 11, 2009
     * @version 1.0.0
     * @param cycle
     * @throws Exception
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData grpInfo = super.queryGroupCustInfo(cycle);
        if (IDataUtil.isEmpty(grpInfo))
        {
            return;
        }
        String cust_manager_id = grpInfo.getString("CUST_MANAGER_ID", "");
        String grpName = grpInfo.getString("CUST_NAME");
        String groupId = grpInfo.getString("GROUP_ID");
        if (StringUtils.isBlank(cust_manager_id))
        {
            CSViewException.apperr(GrpException.CRM_GRP_501);
        }
        else if (!cust_manager_id.equals(getVisit().getStaffId()))
        {
            CSViewException.apperr(GrpException.CRM_GRP_500);
        }
        else
        {
            IData temp = new DataMap();
            temp.put("CUST_ID", grpInfo.getString("CUST_ID"));
            temp.put("PRODUCT_ID", "6130");// 融合总机
            IDataset userInfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, grpInfo.getString("CUST_ID"), "6130", false);
            ;
            if (IDataUtil.isEmpty(userInfos))
            {
                CSViewException.apperr(GrpException.CRM_GRP_499, groupId);
            }

            temp.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
            temp.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

            // 查询用户VPN信息
            IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userInfos.getData(0).getString("USER_ID"));

            if (IDataUtil.isEmpty(userVpnData))
            {
                CSViewException.apperr(GrpException.CRM_GRP_498, groupId);
            }

            if (!"2".equals(userVpnData.getString("VPN_USER_CODE")))
            {
                CSViewException.apperr(GrpException.CRM_GRP_497);
            }

            temp.put("KIND_ID", "CTX1A016_T1000001_0_0");
            temp.put("BG_NAME", userVpnData.getString("VPN_NO"));
             
            IDataset result = null;
            try{
            	result = CSViewCall.call(this, "SS.GrpDealWelcomeVoiceSVC.QryGrpAuditWelcomeVoice", temp);
            }catch(Exception e){
            	CSViewException.apperr(IBossException.CRM_IBOSS_6);
            }

            // 调一级BOSS服务获取欢迎词信息 result
            if (IDataUtil.isEmpty(result))
            {
                // 未找到对应的集团
                CSViewException.apperr(PlatException.CRM_PLAT_39);
            }
            else if (!"0".equals(result.getData(0).getString("X_RESULTCODE")))
            {
                CSViewException.apperr(BizException.CRM_BIZ_5, result.getData(0).getString("X_RESULTINFO"));
            }
            else
            {
                IDataset wordIds = IDataUtil.getDatasetSpecl("WORDSID", result.getData(0));
                IDataset words = IDataUtil.getDatasetSpecl("WORDSDES", result.getData(0));
                IDataset upAddress = IDataUtil.getDatasetSpecl("UPADDRESS", result.getData(0));
                IDataset infos = new DatasetList();
                for (int i = 0; i < wordIds.size(); i++)
                {
                    IData info = new DataMap();
                    info.put("VPN_NO", userVpnData.getString("VPN_NO"));
                    info.put("GROUP_ID", groupId);
                    info.put("GROUP_NAME", grpName);
                    info.put("WORDSID", wordIds.get(i));
                    info.put("WORDS", words.get(i));
                    info.put("UPADDRESS", upAddress.get(i));
                    info.put("INFO", info.toString());
                    infos.add(info);
                }
                setInfos(infos);
            }
        }

        IData cond = new DataMap();
        cond.put("cond_GROUP_ID", groupId);
        setCondition(cond);
    }

    /**
     * 初始化
     * 
     * @author xiaosp 2009-8-25
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset dataset);
}
