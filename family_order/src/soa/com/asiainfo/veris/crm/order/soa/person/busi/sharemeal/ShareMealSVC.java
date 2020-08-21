
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ShareMealException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ShareMealSVC extends CSBizService
{
	
    // 校验方法
    public IDataset checkAddMebMaxNum(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        IDataset rds =bean.checkAddMebMaxNum(data);
        return rds;
    }
    
    // 校验方法
    public void check(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        bean.check(data);
    }

    // 新增成员时候的校验
    public IDataset checkAddMeb(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        return bean.checkAddMeb(data);
    }
    
    /**
     * 流量共享接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData manageShareMember(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        return bean.manageShareMember(data);
    }
    
    // 查询可以共享的优惠
    public IDataset queryFamilyDiscntList(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        return bean.queryShareDiscntList(data);
    }

    // 查询副卡本身
    public IDataset queryFamilyMeb(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        return bean.queryShareMeb(data);
    }

    // 查询共享关系所有副卡成员
    public IDataset queryFamilyMebList(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        return bean.queryShareMebList(data);
    }

    // 查询接口
    public IData queryUserMember(IData data) throws Exception
    {
        ShareMealBean bean = BeanManager.createBean(ShareMealBean.class);
        IData returnInfo = new DataMap();
        IDataset returnDiscnt = new DatasetList();
        IDataset returnMember = new DatasetList();
        // 校验
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(ShareMealException.CRM_SHARE_20);
        }
        bean.check(data);
        // 查询
        returnMember = bean.queryShareMebList(data);
        returnDiscnt = bean.queryShareDiscntList(data);
        returnInfo.put("DISCNT", returnDiscnt);
        returnInfo.put("MEMBER_NUM", returnMember);
        returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "成功！");
        return returnInfo;
    }
    /**************************************************************************
     * 多终端共享成员变更-移动商城<BR/>
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chgShareMealMemberShip(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	// 先对身份凭证进行鉴权
    	String identCode = input.getString("IDENT_CODE", "");
    	String businessCode = input.getString("BUSINESS_CODE", "");
    	String identCodeType = input.getString("IDENT_CODE_TYPE", "");
    	String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
    	String userType = input.getString("USER_TYPE", "");

    	IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
    	if (IDataUtil.isEmpty(idents))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_915);
    	}

    	if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_1103);
    	}

    	SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
    	
    	//参数转换
    	input.put("TYPE", "01".equals(input.getString("OPER_CODE")) ? "ADD" : "DEL");
    	input.put("SERIAL_NUMBER_A", input.getString("SHARE_NUMBER"));
    	
    	ShareMealBean bean = (ShareMealBean) BeanManager.createBean(ShareMealBean.class);
    	IData result = new DataMap();
    	try {
    		result = bean.manageShareMember(input);
		} catch (Exception e) {
			 //1.将接口数据转换为IBOSS需要的数据 由于这个类没有继承OrderService所以无法用filter的方式实现.
			String[] errorMessage = e.getMessage().split("●");
			result.put("X_RSPTYPE", "2");// add by ouyk
//			result.put("X_RSPCODE", "2998");// add by ouyk
            if(errorMessage[0].contains("主卡当前有4个副卡共享成员,不能再新增共享成员！")){
            	result.put("X_RESULTCODE", "3006");
            	result.put("X_RSPCODE","3006");
            	result.put("X_RESULTINFO", errorMessage[0]);
            	IDataset resultList = new DatasetList();
        		resultList.add(result);
                return resultList;
            }else if(errorMessage[0].contains("该用户已经添加到别的共享，不可以多次添加共享！")){
            	result.put("X_RESULTCODE", "2000");
            	result.put("X_RSPCODE","2000");
            	result.put("X_RESULTINFO", errorMessage[0]);
            	IDataset resultList = new DatasetList();
        		resultList.add(result);
                return resultList;
            }else if(errorMessage[0].contains("此成员关系到本账期末结束，不需要再次取消！")){
            	result.put("X_RESULTCODE", "2001");
            	result.put("X_RSPCODE","2001");
            	result.put("X_RESULTINFO", errorMessage[0]);
            	IDataset resultList = new DatasetList();
        		resultList.add(result);
                return resultList;
            }else{
            	//暂不处理
            	result.put("X_RSPCODE","2998");
            	result.put("X_RESULTCODE", "2998");
            	result.put("X_RESULTINFO", errorMessage[0]);
            	IDataset resultList = new DatasetList();
        		resultList.add(result);
                return resultList;
            }
		}
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		IDataset resultList = new DatasetList();
		resultList.add(result);
		
        return resultList;
    }
    /****************************************************************************************
     * 多终端共享成员查询-移动商城
     * @param input
     * @return
     * @throws Exception
     */
    public IData qryShareMealMember(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	// 先对身份凭证进行鉴权
    	String identCode = input.getString("IDENT_CODE", "");
    	String businessCode = input.getString("BUSINESS_CODE", "");
    	String identCodeType = input.getString("IDENT_CODE_TYPE", "");
    	String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
    	String userType = input.getString("USER_TYPE", "");

    	IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
    	if (IDataUtil.isEmpty(idents))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_915);
    	}

    	if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_1103);
    	}

    	SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
        //1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        String userID = userInfos.getData(0).getString("USER_ID");
        //2.将USER_ID设置到
        String existShareDiscnt = "1";
        try{
        	if(! userInfos.isEmpty())
        		existShareDiscnt = ShareInfoQry.queryDiscnt(userID).isEmpty() ? "1" : "0";
        }catch(Exception e){//此处不能抛出异常，IS_SHARE返回“0”即可
        }
        //3.查询用户关系信息
        IDataset shareMemberList = ShareInfoQry.queryMember(userID);
        IData result = new DataMap();
        result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		result.put("IS_SHARE", existShareDiscnt);
        //4.将接口数据转换为IBOSS需要的数据
		result.put("SUB_CARD_REC", transData(shareMemberList));
		
        return result;
    }
    /***************************************************************************
	 * 数据格式转换：提取IBOSS需要的参数<BR/>
	 * @param srcList
	 * @return
	 * @throws Exception
	 */
	private IDataset transData(IDataset srcList)throws Exception{
		if(srcList == null)
			return srcList;
		//日期格式化模板
    	DateFormat loadFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
    	DateFormat formatFormat = new SimpleDateFormat("yyyyMMddHHmmSS");
    	
    	IDataset result = new DatasetList();
    	IData memberItem;
    	for(int i = 0; i < srcList.size(); i++){
    		memberItem = new DataMap();
    		memberItem.put("SHARE_ID_TYPE", "01");
    		memberItem.put("SHARE_ID", srcList.getData(i).getString("SERIAL_NUMBER"));
    		
    		String date = srcList.getData(i).getString("START_DATE");
    		if(date.length() == 10)
    			date = date + SysDateMgr.START_DATE_FOREVER;
    		memberItem.put("START_DATE", formatFormat.format(loadFormat.parse(date)));

    		date = srcList.getData(i).getString("END_DATE");
    		if(date.length() == 10)
    			date = date + SysDateMgr.END_DATE;
    		memberItem.put("END_DATE", formatFormat.format(loadFormat.parse(date)));
    		
    		result.add(memberItem);
    	}
    	return result;
	}
}
