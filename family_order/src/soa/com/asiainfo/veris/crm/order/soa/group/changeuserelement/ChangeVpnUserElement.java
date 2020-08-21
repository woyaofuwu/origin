
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

/**
 * @description 智能VPMN集团产品变更Bean类
 * @author yish
 */
public class ChangeVpnUserElement extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(ChangeVpnUserElement.class);

    private String grpPayDiscnt = "";// 获取统一付费优惠，入user表扩展字段5和10

    public ChangeVpnUserElement()
    {

    }

    /**
     * @description 业务执行前处理
     * @author yish
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        IData productParam = getParamData();
        if (IDataUtil.isNotEmpty(productParam))
        {
            grpPayDiscnt = productParam.getString("GRP_PAY_DISCNT", ""); // 获取统一付费优惠
        }
        // add by lixiuyu@20100518 注销漫游短号服务801需要重新发送800服务指令
        infoRegDataSVC();

    }

    /**
     * @description 子类执行的动作
     * @author yish
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // USER_VPN表
        infoRegDataVPMNVpn();
        
        //start add by wangyf6 at 20191111
        infoRegOtherData();
        //end add by wangyf6 at 20191111
    }

    /**
     * @description 处理台帐VPN子表的数据-用户
     * @author yish
     * @date 2013-10-15
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行ChangeVpnUserElement类 getParamData() 得到产品页面传过来的参数：paramData=" + paramData + "<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    /**
     * 集团产品变更漫游短号服务(801)，登记服务子台帐，走服务开通
     * 
     * @param Datas
     * @author lixiuyu@20100512
     * @throws Exception
     */
    public void infoRegDataSVC() throws Exception
    {
        IDataset svcs = reqData.cd.getSvc();
        String userId = reqData.getUca().getUserId();
        if (IDataUtil.isNotEmpty(svcs))
        {
            for (int i = 0; i < svcs.size(); i++)
            {
                IData svc = svcs.getData(i);
                String modify = svc.getString("MODIFY_TAG");
                String svcId = svc.getString("ELEMENT_ID", "");
                if ((TRADE_MODIFY_TAG.Add.getValue().equals(modify) || TRADE_MODIFY_TAG.DEL.getValue().equals(modify)) && ("801".equals(svcId) || "808".equals(svcId)))
                {
                    IDataset tmpSvc = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "800");
                    if (IDataUtil.isEmpty(tmpSvc))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_684);
                    }

                    IData tmpSvcData = (IData) tmpSvc.get(0);
                    tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));

                    svcs.add(tmpSvcData);
                    break;
                }
            }
        }
    }

    /**
     * 修改用户资料
     * 
     * @throws Exception
     */
    public IData getTradeUserExtendData() throws Exception
    {
        IData userData = super.getTradeUserExtendData();
        if (!grpPayDiscnt.equals(reqData.getUca().getUser().getRsrvStr5()))
        {
            userData.put("RSRV_STR5", grpPayDiscnt); // 获取统一付费优惠
            userData.put("RSRV_STR10", grpPayDiscnt);// 获取统一付费优惠
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        return userData;
    }

    /**
     * @description 处理台帐VPN子表的数据-用户
     * @author yish
     * @date 2013-10-15
     * @throws Exception
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入ChangeVpnUserElement类 infoRegDataVPMN()>>>>>>>>>>>>>>>>>>");

        // 获取VPMN页面个性化参数
        IData productParam = getParamData();
        // 获取集团用户VPN个性信息
        String userIdA = reqData.getUca().getUserId();
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_421);
        }
        else if (IDataUtil.isNotEmpty(productParam))
        {

            IData vpnData = userVpnList.getData(0);
            IData oldVpnData = new DataMap(vpnData);
            // CALL_NET_TYPE
            dealVpnParam(vpnData, oldVpnData, "CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam)); // 呼叫网络类型
            dealVpnParam(vpnData, oldVpnData, "CALL_AREA_TYPE", productParam.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型

            // FUNC_TLAGS
            String funcTlags = "1100000000000000000000000000000000000000";
            String vpnTageSet = productParam.getString("NOTIN_VPN_TAG_SET", "");
            if ("".equals(vpnTageSet))
            {
                vpnTageSet = "[]";// 为null  new datasetlist的时候会把null放进去 后面就会空指针
            }
            IDataset vpnTagSet = new DatasetList(vpnTageSet);
            if (IDataUtil.isNotEmpty(vpnTagSet))
            {
                funcTlags = VpnUnit.comFlagField(funcTlags, vpnTagSet);
            }
            String strCallNetType = vpnData.getString("CALL_NET_TYPE");
            String strCallAreaType = vpnData.getString("CALL_AREA_TYPE");
            String strFuncFlagsTem = "";
            if (strCallAreaType != "" && strCallNetType != "")
            {
                for (int i = 0; i < 4; i++)
                {
                    if (strCallNetType.substring(i, i + 1).equals("1"))
                    {
                        strFuncFlagsTem += strCallAreaType;
                        strFuncFlagsTem += strCallAreaType;
                    }
                    else
                    {
                        strFuncFlagsTem += "11";
                    }
                    funcTlags = strFuncFlagsTem + funcTlags.substring(8);
                }
            }

            // FUNC_TLAGS
            dealVpnParam(vpnData, oldVpnData, "FUNC_TLAGS", funcTlags);
            dealVpnParam(vpnData, oldVpnData, "MAX_CLOSE_NUM", productParam.getString("MAX_CLOSE_NUM", ""));// 最大闭合用户群数
            dealVpnParam(vpnData, oldVpnData, "MAX_NUM_CLOSE", productParam.getString("MAX_NUM_CLOSE", ""));// 单个闭合用户群能包含的最大用户数
            dealVpnParam(vpnData, oldVpnData, "PERSON_MAXCLOSE", productParam.getString("PERSON_MAXCLOSE", ""));// 单个用户最大可加入的闭合群数

            // PERSON_MAXCLOSE
            dealVpnParam(vpnData, oldVpnData, "MAX_INNER_NUM", productParam.getString("MAX_INNER_NUM", ""));// 最大网内号码总数
            dealVpnParam(vpnData, oldVpnData, "MAX_OUTNUM", productParam.getString("MAX_OUTNUM", ""));// 最大网外号码总数
            dealVpnParam(vpnData, oldVpnData, "MAX_USERS", productParam.getString("MAX_USERS", ""));// 集团最大用户数-海南固定为10
            dealVpnParam(vpnData, oldVpnData, "MAX_TELPHONIST_NUM", productParam.getString("MAX_TELPHONIST_NUM", ""));// 话务员最大数
            // MAX_TELPHONIST_NUM

            // 6、HAIN有特殊处理MAX_LINKMAN_NUM=rsrvStr5(但CB里没找到这个控件)
            dealVpnParam(vpnData, oldVpnData, "MAX_LINKMAN_NUM", productParam.getString("MAX_LINKMAN_NUM", ""));
            dealVpnParam(vpnData, oldVpnData, "WORK_TYPE_CODE", productParam.getString("WORK_TYPE_CODE", ""));// VPMN集团类型
            dealVpnParam(vpnData, oldVpnData, "VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", ""));// 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
            dealVpnParam(vpnData, oldVpnData, "OVER_FEE_TAG", productParam.getString("OVER_FEE_TAG", ""));// 呼叫超出限额处理标记

            dealVpnParam(vpnData, oldVpnData, "SINWORD_TYPE_CODE", productParam.getString("SINWORD_TYPE_CODE", ""));// 语种选择
            dealVpnParam(vpnData, oldVpnData, "CUST_MANAGER", productParam.getString("CUST_MANAGER", ""));
            dealVpnParam(vpnData, oldVpnData, "VPN_BUNDLE_CODE", productParam.getString("VPN_BUNDLE_CODE", ""));
            dealVpnParam(vpnData, oldVpnData, "MANAGER_NO", productParam.getString("MANAGER_NO", ""));//
            dealVpnParam(vpnData, oldVpnData, "RSRV_STR5", productParam.getString("VPN_CLASS", ""));
            vpnData.put("RSRV_STR1", ""); // 集团优惠类型(CB中未找到控件)
            boolean isModi = false; // 是否进行了修改
            isModi = vpnData.getBoolean("isModi", false);
            if (isModi)
            {
                vpnData.remove("isModi");
                vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); // 状态属性：ADD-增加，DEL-删除，MODI-变更
                IDataset dataset = new DatasetList();
                dataset.add(vpnData);
                addTradeVpn(dataset);
            }

            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChangeVpnUserElement类 infoRegDataVPMN()  插入vpn表数据：" + vpnData + "<<<<<<<<<<<<<<<<<<<");
        }
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        IData param = getParamData();
        if (IDataUtil.isNotEmpty(param))
        {
            data.put("RSRV_STR5", param.getString("GRP_PAY_DISCNT", "")); // 获取统一付费优惠
            data.put("RSRV_STR6", param.getString("WORK_TYPE_CODE", "")); // 行业类型-集团V网类别
            data.put("RSRV_STR7", param.getString("CUST_MANAGER", ""));
            data.put("RSRV_STR10", param.getString("VPN_GRP_ATTR", ""));// 集团属性
        }
    }

    /**
     * 对比参数值是否修改
     * 
     * @param vpnData
     * @param paramCode
     * @param paramValue
     * @throws Exception
     */
    protected void dealVpnParam(IData vpnData, IData oldVpnData, String paramCode, String paramValue) throws Exception
    {
        if (!paramValue.equals(oldVpnData.getString(paramCode, "")))
        {
            vpnData.put(paramCode, paramValue);
            vpnData.put("isModi", true);
        }
    }
    
    
    /**
     * 处理台帐Other子表的数据
     * for REQ201910230023
     * @throws Exception
     */
    public void infoRegOtherData() throws Exception 
    {
    	IDataset lineDataset = new DatasetList();
        String newGrpClipType = ""; //GRP_CLIP_TYPE 呼叫来显方式
        String newGrpUserClipType = ""; //GRP_USER_CLIP_TYPE 选择号显方式
        String newGrpUserMod = ""; //GRP_USER_MOD 成员修改号显方式
        boolean isContains800Svc = false;
        
        // 获取VPMN页面个性化参数
        IData productParam = getParamData();
        String grpUserId = reqData.getUca().getUserId();
        if (IDataUtil.isNotEmpty(productParam))
        {
            newGrpClipType = productParam.getString("GRP_CLIP_TYPE", "");
            newGrpUserClipType = productParam.getString("GRP_USER_CLIP_TYPE", "");
            newGrpUserMod = productParam.getString("GRP_USER_MOD", "");
        }
        
        IDataset svcs = reqData.cd.getSvc();
        if (IDataUtil.isNotEmpty(svcs))
        {
        	isContains800Svc = true;
            for (int i = 0; i < svcs.size(); i++)
            {
                IData svc = svcs.getData(i);
                String svcId = svc.getString("ELEMENT_ID", "");
                if ("800".equals(svcId))
                {
                    isContains800Svc = false;
                    break;
                }
            }
        }
        else 
        {
        	isContains800Svc = true;
        }
        
        IDataset otherInfoSet = UserOtherInfoQry.getUserOtherInfoByAll(grpUserId, "VPMN_GRPCLIP");
        if(IDataUtil.isEmpty(otherInfoSet))//没有，则做新增操作
        { 
        	if(StringUtils.isNotBlank(newGrpClipType))
        	{
        		IData data = new DataMap();
                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "VPMN_GRPCLIP");
                data.put("RSRV_VALUE", "");
                
                data.put("RSRV_STR1", newGrpClipType); //GRP_CLIP_TYPE 呼叫来显方式
                if("1".equals(newGrpClipType))
                {
                    data.put("RSRV_STR2", newGrpUserClipType);//GRP_USER_CLIP_TYPE 选择号显方式
                    data.put("RSRV_STR3", newGrpUserMod);//GRP_USER_MOD 成员修改号显方式
                } 
                else if("0".equals(newGrpClipType)) //如果是该值是0时，需要把这两个字段置空
                {
                    data.put("RSRV_STR2", "");//GRP_USER_CLIP_TYPE 选择号显方式
                    data.put("RSRV_STR3", "");//GRP_USER_MOD 成员修改号显方式
                }
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("INST_ID", SeqMgr.getInstId());
                lineDataset.add(data);
                super.addTradeOther(lineDataset);
                
                if(isContains800Svc)//如果没有800,则插一条svcid=800 modify=2的数据
                {
                	IDataset tmpSvc = UserSvcInfoQry.qryUserSvcByUserSvcId(grpUserId, "800");
                    if (IDataUtil.isEmpty(tmpSvc))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_684);
                    }

                    IData tmpSvcData = (IData) tmpSvc.get(0);
                    tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));

                    super.addTradeSvc(tmpSvcData);
                }
        	}
        }
        else 
        {
        	IData otherInfo = otherInfoSet.getData(0);
            IData oldOtherInfo = new DataMap(otherInfo);
            dealVpnParam(otherInfo, oldOtherInfo, "RSRV_STR1", productParam.getString("GRP_CLIP_TYPE", ""));
            dealVpnParam(otherInfo, oldOtherInfo, "RSRV_STR2", productParam.getString("GRP_USER_CLIP_TYPE", ""));
            dealVpnParam(otherInfo, oldOtherInfo, "RSRV_STR3", productParam.getString("GRP_USER_MOD", ""));
            
            boolean isModi = false; // 是否进行了修改
            isModi = otherInfo.getBoolean("isModi", false);
            if (isModi)
            {
            	otherInfo.remove("isModi");
            	if(StringUtils.isNotBlank(newGrpClipType))
            	{
            		otherInfo.put("RSRV_STR1", newGrpClipType); //GRP_CLIP_TYPE 呼叫来显方式
            		if("1".equals(newGrpClipType))
            		{
                        otherInfo.put("RSRV_STR2", newGrpUserClipType);//GRP_USER_CLIP_TYPE 选择号显方式
                        otherInfo.put("RSRV_STR3", newGrpUserMod);//GRP_USER_MOD 成员修改号显方式
                    } 
            		else if("0".equals(newGrpClipType))//如果是该值是0时，需要把这两个字段置空
            		{
                        otherInfo.put("RSRV_STR2", "");//GRP_USER_CLIP_TYPE 选择号显方式
                        otherInfo.put("RSRV_STR3", "");//GRP_USER_MOD 成员修改号显方式
                    }
            		otherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); 
            		
            		IDataset dataset = new DatasetList();
                    dataset.add(otherInfo);
                    addTradeOther(dataset);
                    
                    if(isContains800Svc)//如果没有800,则插一条svcid=800 modify=2的数据
                    {
                    	IDataset tmpSvc = UserSvcInfoQry.qryUserSvcByUserSvcId(grpUserId, "800");
                        if (IDataUtil.isEmpty(tmpSvc))
                        {
                            CSAppException.apperr(GrpException.CRM_GRP_684);
                        }
                        IData tmpSvcData = (IData) tmpSvc.get(0);
                        tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));

                        super.addTradeSvc(tmpSvcData);
                        
                    }
            	}
            }
        }
    }
}
