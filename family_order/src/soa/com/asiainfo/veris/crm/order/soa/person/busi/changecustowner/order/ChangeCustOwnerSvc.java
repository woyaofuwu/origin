package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FastAuthApproveQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.FTTHModermApplyBean;

public class ChangeCustOwnerSvc extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    
    /**
     * 核对宽带过户
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkChangeWidenetUser(IData input)throws Exception{
    	IData result=new DataMap();
    	
    	String serialNumber = input.getString("SERIAL_NUMBER");

    	IData param = new DataMap();
        // 查询手机号码的宽带用户信息
        IDataset userInfo = WidenetInfoQry.getUserInfo(serialNumber);
        if (userInfo == null || userInfo.size() == 0)
        {
            return result;
        }
        
        String userId = userInfo.getData(0).getString("USER_ID");
        // 用户宽带资料
        IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(dataset))
        {
        	return result;
        }
        param.put("USER_ID_WIDE", userId);
        // 判断宽带状态: --- 可以通过配置 td_s_svcstate_trade_limit 表, 只配置1,5, 4种业务类型8条记录
        // 外加一个规则: --- WideChangeUserCheckBean.java
        // boolean statusFlag = userQuery.queryWideNetOpenStatus(pd, userInfo);
        // if (statusFlag) {
        // common.error("该宽带用户为非开通状态，不能进行宽带帐号变更！");
        // }
        // 判断是否有未完工的工单-- j2ee默认就有判断
        // 查询主账号下所有子账号
        IDataset allAcct = RelaUUInfoQry.getAllSubAcct(userId, "77");
        if (allAcct != null && allAcct.size() > 0)
        	// 调用成功，记录兑换日志
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能进行宽带过户：号码属于平行帐号关系，不能变更帐号");
        	
        
        IDataset allAcctF = RelaUUInfoQry.getAllSubAcct(userId, "78");
        if (allAcctF != null && allAcctF.size() > 0)
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能进行宽带过户：此号码属于家庭帐号关系，不能变更帐号");
        
            
        // 查询生效的优惠
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
        if (discntInfo == null || discntInfo.size() <= 0)
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能进行宽带过户：该用户在首月免费期中，不能变更帐号");
        
        // 判断是不是存在特殊优惠
        else
        {
            for (int i = 0; i < discntInfo.size(); i++)
            {
                String disCode = ((IData) discntInfo.get(i)).getString("DISCNT_CODE");

                IDataset exit = FastAuthApproveQry.queryCommByAttrCode("CSM", "640", disCode, CSBizBean.getTradeEparchyCode());
                if (exit != null && exit.size() > 0)
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能进行宽带过户：号码的优惠不允许办理变更帐号业务！");
            }
        }

        //获取用户在本月是否办理了一次宽带过户
        String curMonth=SysDateMgr.getCurMonth();
        boolean isChangeOwner=TradeUserInfoQry.queryWideNetUserIsChangeOwner(userId, curMonth);
        if(isChangeOwner){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能进行宽带过户：本月已经变更一次帐号，不能再次办理!");
        }
        
        
    	return result;
    }
    
    
    /**
     * 核对取消宽带
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkCancelWidenetUser(IData param)throws Exception{
    	IData result=new DataMap();
    	
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	
    	IData params = new DataMap();
    	params.put("SERIAL_NUMBER", "KD_" + serialNumber);
    	IDataset dt = Dao.qryByCode("TF_B_TRADE_WIDENETHN", "SEL_WIDEUSER_BY_SN_KD", params);
        if (dt != null && dt.size() > 0)
        {
            StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("用户在本月已办理宽带开户，开户当月不能办理手机号码宽带拆机！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
        }
        
        
        
        
        IDataset userInfo = WidenetInfoQry.getUserInfo(serialNumber);
        if (userInfo == null || userInfo.size() == 0)
        {
            return result;
        }
        String userId = userInfo.getData(0).getString("USER_ID");
        

        
        //判断用户是否有光猫，有的的情况下，需先执行宽带拆机处理后方可办理手机过户。
        FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset users=bean.getUserModermInfo(param);
        if(users!=null && users.size()>0){
        	CSAppException.appError("61319", "该宽带已经绑定光猫的，请先执行宽带拆机处理后再办理手机过户");
        }
        
        String roleCodeB = "";
        String relationTypeCode = "";
        
        // 平行账号查询
        IDataset relationInfos = RelaUUInfoQry.isMasterAccount(userId, "77");
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            roleCodeB = relationInfos.getData(0).getString("ROLE_CODE_B");
            relationTypeCode = relationInfos.getData(0).getString("RELATION_TYPE_CODE");
        }
        else
        {
            // 家庭账号查询
            relationInfos = RelaUUInfoQry.isMasterAccount(userId, "78");
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                roleCodeB = relationInfos.getData(0).getString("ROLE_CODE_B");
                relationTypeCode = relationInfos.getData(0).getString("RELATION_TYPE_CODE");
            }
            else
            {
                return result;
            }
        }

        if ("1".equals(roleCodeB) && "78".equals(relationTypeCode))
        {
            relationInfos = RelaUUInfoQry.getUserUU(userId, "2", relationTypeCode);
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                String serialNumberB = relationInfos.getData(0).getString("SERIAL_NUMBER_B");

                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户的子账号[" + serialNumberB + "]没有拆机,不能办理拆机业务!");
            }

        }
    	
    	return result;
    }

}
