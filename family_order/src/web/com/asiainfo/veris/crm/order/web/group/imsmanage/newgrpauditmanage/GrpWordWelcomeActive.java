
package com.asiainfo.veris.crm.order.web.group.imsmanage.newgrpauditmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpWordWelcomeActive extends GroupBasePage
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
        //String grpName = grpInfo.getString("CUST_NAME");
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
            
            IData userInfo = userInfos.getData(0);
            String vpnNo = userVpnData.getString("VPN_NO");
            
            //融合总机的集团产品铃音文件查询
            IData inputParam = new DataMap();
            String userIdA = userInfo.getString("USER_ID");//集团融合总机的集团产品user_id
            if(StringUtils.isNotBlank(userIdA)){
                inputParam.put("USER_ID", userIdA);
            }
            inputParam.put("TRADE_TAG", "4");
            
            IDataOutput output = CSViewCall.callPage(this, "SS.GrpVoiceWelcomeSVC.queryGrpVoiceWelcomeFiles",
                    inputParam, getPagination("LogNav"));
            
            IDataset infos = output.getData();
            if(IDataUtil.isNotEmpty(infos)){
                for(int i=0;i < infos.size(); i++){
                    IData info = infos.getData(i);
                    info.put("VPN_NO", vpnNo);
                }
            }
            setInfos(infos);
            setLogCount(output.getDataCount());
            
            setInfo(userInfo);
        }
        
        setCondition(getData("cond",true));
    }

    /**
     * 融合总机的集团产品铃音文件查询
     * @param cycle
     * @throws Exception
     */
    public void queryGrpVoiceWelcomeFiles(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData inputParam = new DataMap();
        String userIdA = data.getString("GRP_USER_ID");
        if(StringUtils.isNotBlank(userIdA)){
            inputParam.put("USER_ID", userIdA);
        }
        inputParam.put("TRADE_TAG", "4");
        
        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userIdA);
        String vpnNo = "";
        if (IDataUtil.isNotEmpty(userVpnData))
        {
            vpnNo = userVpnData.getString("VPN_NO");
        }
        
        IDataOutput output = CSViewCall.callPage(this, "SS.GrpVoiceWelcomeSVC.queryGrpVoiceWelcomeFiles", 
                inputParam, getPagination("LogNav"));
        
        IDataset infos = output.getData();
        if(IDataUtil.isNotEmpty(infos)){
            for(int i=0;i < infos.size(); i++){
                IData info = infos.getData(i);
                info.put("VPN_NO", vpnNo);
            }
        }
        
        setInfos(infos);
        setLogCount(output.getDataCount());
        setCondition(data);
    
    }
    
    /**
     * 上传欢迎词
     * @param cycle
     * @throws Exception
     */
    public void sendGrpWordMessage(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        
        String fileName = cond.getString("FILE_NAME");
        int len = fileName.lastIndexOf(".");
        if(len <= -1){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到文件的后缀名!请确认!");
        }
        
        String fileSuffix = fileName.substring(len + 1, fileName.length());
        if(!StringUtils.equals("wav", fileSuffix.toLowerCase())){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "文件后缀名不是wav!请确认!");
        }
        
        IData temp = new DataMap();
        temp.put("KIND_ID", "CTX1A018_T1000001_0_0");
        temp.put("BG_NAME", cond.getString("VPN_NO"));
        temp.put("WORDSDES", cond.getString("WORDS_DES"));
        temp.put("WORDSID", cond.getString("VPN_NO") + "-" + cond.getString("FILE_ID") + "." + fileSuffix);
        
        String fileId = cond.getString("FILE_ID");
        if(StringUtils.isNotBlank(fileId)){
            IData param = new DataMap();
            param.put("FILE_ID", fileId);
            IDataset fileInfos = CSViewCall.call(this, "CS.MFileInfoQrySVC.qryFileInfoListByFileID", param);
            if(IDataUtil.isEmpty(fileInfos)){
                CSViewException.apperr(GrpException.CRM_GRP_642);
            }
            String fileKind = fileInfos.getData(0).getString("FILE_KIND","");
            if ("".equals(fileKind) || !StringUtils.equals("9", fileKind)){
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "文件ID:" + fileId + "还未处理完,请稍后再提交!");
            }
        }
        
        IDataset result = CSViewCall.call(this, "SS.GrpVoiceWelcomeSVC.sendGrpWordMessage", temp);
        // 调一级BOSS服务上传欢迎词
        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_39);
        }
        else if (!"0".equals(result.getData(0).getString("X_RESULTCODE")))
        {
            CSViewException.apperr(BizException.CRM_BIZ_5, result.getData(0).getString("X_RESULTINFO"));
        }
        else
        {
            String userId = cond.getString("USER_ID");
            IData inParam = new DataMap();
            inParam.put("USER_ID", userId);
            inParam.put("FILE_ID", fileId);
            
            //修改状态 
            CSViewCall.call(this, "SS.GrpVoiceWelcomeSVC.saveCheckWordWelcome", inParam);
            
            IData errresult = new DataMap();
            errresult.put("RESULT_CODE", "true");
            errresult.put("X_MESSAGE", "审核欢迎词业务成功!");
            setAjax(errresult);
        }
    }
    
    /**
     * 激活融合总机欢迎词
     * @param cycle
     * @throws Exception
     */
    public void activeGrpWordMessage(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IData temp = new DataMap();
        
        String fileName = cond.getString("FILE_NAME");
        int len = fileName.lastIndexOf(".");
        if(len <= -1){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到文件的后缀名!请确认!");
        }
        
        String fileSuffix = fileName.substring(len + 1, fileName.length());
        if(!StringUtils.equals("wav", fileSuffix.toLowerCase())){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "文件后缀名不是wav!请确认!");
        }
        
        temp.put("KIND_ID", "CTX1A019_T1000001_0_0");
        temp.put("BG_NAME", cond.getString("VPN_NO"));
        temp.put("WORDSID", cond.getString("VPN_NO") + "-" + cond.getString("FILE_ID") + "." + fileSuffix);
        temp.put("CHECK_STATUS", "1");
        
        String userId = cond.getString("USER_ID","");
        String fileId = cond.getString("FILE_ID","");
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("FILE_ID", fileId);
        param.put("TRADE_TAG", "4");
        
        IDataset fileInfos = CSViewCall.call(this, "SS.GrpVoiceWelcomeSVC.queryImsGrpFileByFileId", param);
        if(IDataUtil.isEmpty(fileInfos)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "未查询到USER_ID:" + 
                    userId + "文件ID:" + fileId + "文件记录,请核实后再激活!");
        }
        
        String rsrvTag1 = fileInfos.getData(0).getString("RSRV_TAG1","");
        if ("".equals(rsrvTag1) || !StringUtils.equals("9", rsrvTag1)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "文件ID:" + fileId + "还未审核处理,请先审核后再激活!");
        }
        
        IDataset result = CSViewCall.call(this, "SS.GrpVoiceWelcomeSVC.activeGrpWordMessage", temp);
        // 调一级BOSS服务上传欢迎词
        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_39);
        }
        else if (!"0".equals(result.getData(0).getString("X_RESULTCODE")))
        {
            CSViewException.apperr(BizException.CRM_BIZ_5, result.getData(0).getString("X_RESULTINFO"));
        }
        else
        {
            IData errresult = new DataMap();
            errresult.put("RESULT_CODE", "true");
            errresult.put("X_MESSAGE", "激活欢迎词业务成功!");
            setAjax(errresult);
        }
    }
    
    /**
     * 初始化
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
    }

    public abstract void setInfo(IData info);
    
    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset dataset);
    
    public abstract void setLogCount(long logCount);
}
