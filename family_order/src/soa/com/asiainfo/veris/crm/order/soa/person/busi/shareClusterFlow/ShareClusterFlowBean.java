
package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.exception.ShareClusterFlowException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow.ShareClusterFlowQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class ShareClusterFlowBean extends CSBizBean
{

    /**
     * 查询时校验
     * 
     * @param data
     * @throws Excepion
     */
    public IDataset check(IData data) throws Exception
    {
        String mainSn = data.getString("SERIAL_NUMBER");
        String userId = data.getString("USER_ID", "");
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        if (StringUtils.isBlank(userId))
        {
            userId = mainUser.getString("USER_ID");
            data.put("USER_ID", userId);

        }
        
        IDataset productInfo = UserProductInfoQry.queryMainProduct(userId);
        IDataset returns=new DatasetList();
        IData rs=new DataMap();
        rs.put("IS_EXIST", 0);
        rs.put("IS_STOP", 0);
        rs.put("IS_END", 0);
        String tag = data.getString("MEMBER_CANCEL", "0");
        if (StringUtils.isNotBlank(tag) && "0".equals(tag))
        {
        	String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
     				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
     				 new String[]{ "CSM", "276", "STOP_TYPE" });
        	if(urltime.indexOf(","+mainUser.getString("USER_STATE_CODESET")+",")>=0){
        		//CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_31);
        		rs.put("IS_STOP", 1);
        	}
            // 查询是不是存在共享关系
            IDataset returnData = ShareClusterFlowQry.queryMemberRela(userId, "02");
            if (IDataUtil.isNotEmpty(returnData))
            {
                CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_1);
            }
            // 查询可以共享的资费，配置在参数表 sunxin 只有主卡操作使用
            IDataset discntMainInfo = ShareClusterFlowQry.queryDiscnt(userId);
            if (IDataUtil.isEmpty(discntMainInfo))
            {
                CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_2);
            }
            IDataset  mainRela= ShareClusterFlowQry.queryMemberRela(userId, "01");
            if(mainRela!=null&&mainRela.size()>0){
            	rs.put("IS_EXIST", 1);
            	if(mainRela.getData(0).getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth())){
            		rs.put("IS_END", 1);
            	}
            }
            
            //验证主卡是否办理JTA套餐
//            IDataset discntSet=UserDiscntInfoQry.getAllDiscntByUser(userId, "270");
//            if(discntSet!=null&&discntSet.size()>0){
//      		   CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_33, mainSn);
//      	   }
            
        }
        if (StringUtils.isNotBlank(tag) && "1".equals(tag))
        {
            IDataset MemberData = ShareClusterFlowQry.queryMemberRela(userId, "01");
            if (IDataUtil.isNotEmpty(MemberData))
                CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_14);
        }
        String brandCode = productInfo.getData(0).getString("BRAND_CODE");
        if ("G005".equals(brandCode))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_4);
        if (!"0".equals(mainUser.getString("ACCT_TAG")))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_5);
        returns.add(rs);
        return returns;
    }

    /**
     * 添加成员时校验成员信息
     * 
     * @param data
     * @throws Excepion
     */
    public void checkAddMeb(IData data) throws Exception
    {
        String mainSn = data.getString("SERIAL_NUMBER");
        String sn = data.getString("SERIAL_NUMBER_B");

        // 新增的成员号码不能和主卡号码一致,请确认！
        if (mainSn.equals(sn))
        {
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_6);
        }
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        String userId = mainUser.getString("USER_ID");
        IData mainAcct = UcaInfoQry.qryAcctInfoByUserId(userId);
        // 查询成员信息
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }
        String userIdB = user.getString("USER_ID");
        IData Acct = UcaInfoQry.qryAcctInfoByUserId(userIdB);
        // 判断地州 海南暂时不需要 sunxin
        if (!mainUser.getString("EPARCHY_CODE").equals(user.getString("EPARCHY_CODE")))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_8);
        // 判断账期
        String mainAcctId = mainAcct.getString("ACCT_ID");
        String AcctId = Acct.getString("ACCT_ID");
        IDataset mainAcctDay = UserAcctDayInfoQry.getAccountAcctDay(mainAcctId);
        IDataset AcctDay = UserAcctDayInfoQry.getAccountAcctDay(AcctId);
        if (IDataUtil.isNotEmpty(mainAcctDay) && IDataUtil.isNotEmpty(AcctDay))
        {
            if (!mainAcctDay.getData(0).getString("ACCT_DAY").equals(AcctDay.getData(0).getString("ACCT_DAY")))
                CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_7);
        }
        // 判断成员是不是已经存在共享关系
        IDataset returnDataMen = ShareClusterFlowQry.queryMemberRela(userIdB, "02");
        if (IDataUtil.isNotEmpty(returnDataMen))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_9);

        IDataset returnDataMain = ShareClusterFlowQry.queryMemberRela(userIdB, "01");
        if (IDataUtil.isNotEmpty(returnDataMain))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_9);
        // 判断是否未激活
        if (!"0".equals(user.getString("ACCT_TAG")))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_5);
        // 判断是不是已经存在4个成员
        IDataset returnDataMenber = ShareClusterFlowQry.queryMember(userId);
        if (returnDataMenber.size() >= 4)
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_21);

    }
    /**
     * 副卡申请加入家庭群组时校验主卡信息
     * 
     * @param data
     * @throws Excepion
     */
    public IDataset checkAddMain(IData data) throws Exception
    {
        String sn = data.getString("SERIAL_NUMBER");//副卡号码
        String mainSn = data.getString("SERIAL_NUMBER_B");//主卡号码

        // 新增的成员号码不能和主卡号码一致,请确认！
        if (mainSn.equals(sn))
        {
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_6);
        }
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
				 new String[]{ "CSM", "276", "STOP_TYPE" });
        if(urltime.indexOf(","+mainUser.getString("USER_STATE_CODESET")+",")>=0){
        	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_31);
        }
        String userId = mainUser.getString("USER_ID");
        // 查询成员信息
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }
        String userIdB = user.getString("USER_ID");
        // 判断地州 
        if (!mainUser.getString("EPARCHY_CODE").equals(user.getString("EPARCHY_CODE")))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_8);
        // 判断主卡是不是开通了群组共享
        IDataset MemberData = ShareClusterFlowQry.queryMemberRela(userId, "01");
        if (MemberData==null||MemberData.size()==0)
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_22);
        //判断主卡是不是已经取消了群组共享
        if(MemberData.getData(0).getString("END_DATE").equals(SysDateMgr.getLastDateThisMonth()))
        	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_32);
        // 判断主卡是不是已经存在4个成员
        IDataset returnDataMenber = ShareClusterFlowQry.queryMember(userId);
        if (returnDataMenber.size() >= 4)
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_21);
        IDataset discntMainInfo = ShareClusterFlowQry.queryDiscnt(userId);
        if(discntMainInfo==null||discntMainInfo.size()==0)
        	CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_3);
        // 判断成员是不是已经存在共享关系
        IDataset returnDataMen = ShareClusterFlowQry.queryMemberRela(userIdB, "02");
        if (IDataUtil.isNotEmpty(returnDataMen))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_9);

        IDataset returnDataMain = ShareClusterFlowQry.queryMemberRela(userIdB, "01");
        if (IDataUtil.isNotEmpty(returnDataMain))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_9);
        return discntMainInfo;
    }


    // 查询共享优惠
    public IDataset queryShareDiscntList(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        // 查询用户下所有可以共享4g资费
        String tag = data.getString("MEMBER_CANCEL", "0");
        if (StringUtils.isNotBlank(tag) && "1".equals(tag))
        {
        	//根据副卡userId获取shareid
        	IDataset memberRela=ShareClusterFlowQry.queryMemberRela(userId,"02");
        	if(memberRela!=null&&memberRela.size()!=0){
        		String shareId=memberRela.getData(0).getString("SHARE_ID");
        		//根据shareid获取主卡userId
        		IDataset shareRela=ShareClusterFlowQry.queryRelaByShareIdAndRoleCode(shareId,"01");
        		if(shareRela!=null&&shareRela.size()!=0){
        			userId=shareRela.getData(0).getString("USER_ID_B");
        		}else{
        			userId="";
        		}
        	}else{
        		userId="";
        	}
        	
        }
        IDataset discntInfo = ShareClusterFlowQry.queryDiscnt(userId);
        if (!userId.equals("")&&IDataUtil.isEmpty(discntInfo))
            CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_3);

        return discntInfo;
    }

    // 副卡取消查询本身
    public IDataset queryShareMeb(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String sn = data.getString("SERIAL_NUMBER");
        // 需要判断是否可以取消 即结束时间为用户本账期最后一天
        IDataset mebList = ShareClusterFlowQry.queryMemberRela(userId, "02");
        // 一次线程设置一个多账期的环境变量，因所有号码账期必须一致，所以只需要取主卡的即可
        //UcaDataFactory.getNormalUca(sn);
        String date = SysDateMgr.getLastDateThisMonth();
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int i = 0; i < mebList.size(); i++)
            {
                IData map = mebList.getData(i);
                String endDate = map.getString("END_DATE");
                if (date.equals(endDate))
                    CSAppException.apperr(ShareClusterFlowException.CRM_SHARE_12);
	          else
	              map.put("DEAL_TAG", "0");
            }
        }
        return mebList;

    }

    // 查询副卡
    public IDataset queryShareMebList(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String sn = data.getString("SERIAL_NUMBER");
        // 需要判断是否可以取消 即结束时间为用户本账期最后一天
        String tag = data.getString("MEMBER_CANCEL", "0");
        IDataset mebList = null;
        if (StringUtils.isNotBlank(tag) && "0".equals(tag))
        {
        	mebList = ShareClusterFlowQry.queryMember(userId);
        }else{
        	mebList = ShareClusterFlowQry.queryMemberRela(userId,"02");
        }
        
        // 一次线程设置一个多账期的环境变量，因所有号码账期必须一致，所以只需要取主卡的即可
        //UcaDataFactory.getNormalUca(sn);
        String date = SysDateMgr.getLastDateThisMonth();
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int i = 0; i < mebList.size(); i++)
            {
                IData map = mebList.getData(i);
                String endDate = map.getString("END_DATE");
                if (date.equals(endDate))
                    map.put("DEAL_TAG", "1");// 传递到前台，不能取消
                else
                    map.put("DEAL_TAG", "0");

            }
        }
        return mebList;

    }
}
