
package com.asiainfo.veris.crm.order.soa.person.busi.sharemeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.exception.ShareMealException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class ShareMealBean extends CSBizBean
{
			
	 public IDataset checkAddMebMaxNum(IData data) throws Exception
	    {
		    
	        String mainSn = data.getString("SERIAL_NUMBER");	        	        
	        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
	        if (IDataUtil.isEmpty(mainUser))
	        {
	            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
	        }
	        
	        IDataset productInfo = UserProductInfoQry.queryMainProduct(mainUser.getString("USER_ID"));	        
	        String productId = productInfo.getData(0).getString("PRODUCT_ID");
	        //查询配置表，看是否有指定该产品配置多少个上限共享号码的记录
			IDataset commparalist= CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "8332", "PRODUCT_ID",productId );
			
			IDataset rds = new DatasetList();
			IData map = new DataMap();
			if(IDataUtil.isNotEmpty(commparalist)){
				String num = commparalist.getData(0).getString("PARA_CODE2","").trim();
				if(!StringUtils.isNumeric(num)){
		            CSAppException.apperr(CrmUserException.CRM_USER_783, "参数表8332,PRODUCT_ID="+productId+",的PARA_CODE2须是数字!");
				}else{
					map.put("AddMebMaxNum", Integer.parseInt(num));
					rds.add(map);
				}
			}else{				
				commparalist= CommparaInfoQry.getCommparaAllColByParser("CSM", "8332", "DEFAULT","ZZZZ" );
				if(IDataUtil.isNotEmpty(commparalist)){
					String num = commparalist.getData(0).getString("PARA_CODE1","").trim();
					if(!StringUtils.isNumeric(num)){
			            CSAppException.apperr(CrmUserException.CRM_USER_783, "参数表8332,DEFAULT的PARA_CODE1须是数字!");
					}else{
						map.put("AddMebMaxNum", Integer.parseInt(num));
						rds.add(map);
					}
				}else{
		            CSAppException.apperr(CrmUserException.CRM_USER_783, "参数表8332,必须有DEFAULT配置!");
				}
			}
	        return rds;
	    }

    /**
     * 查询时校验
     * 
     * @param data
     * @throws Excepion
     */
    public void check(IData data) throws Exception
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

        String tag = data.getString("MEMBER_CANCEL", "0");
        if (StringUtils.isNotBlank(tag) && "0".equals(tag))
        {

            // 查询是不是存在共享关系
            IDataset returnData = ShareInfoQry.queryMemberRela(userId, "02");
            if (IDataUtil.isNotEmpty(returnData))
            {
                CSAppException.apperr(ShareMealException.CRM_SHARE_1);
            }
            // 查询可以共享的资费，配置在参数表 sunxin 只有主卡操作使用
            IDataset discntMainInfo = ShareInfoQry.queryDiscnt(userId);
            if (IDataUtil.isEmpty(discntMainInfo))
            {
                CSAppException.apperr(ShareMealException.CRM_SHARE_2);
            }
        }
        if (StringUtils.isNotBlank(tag) && "1".equals(tag))
        {
            IDataset MemberData = ShareInfoQry.queryMemberRela(userId, "01");
            if (IDataUtil.isNotEmpty(MemberData))
                CSAppException.apperr(ShareMealException.CRM_SHARE_14);
        }
        String brandCode = productInfo.getData(0).getString("BRAND_CODE");
        if ("G005".equals(brandCode))
            CSAppException.apperr(ShareMealException.CRM_SHARE_4);
        if (!"0".equals(mainUser.getString("ACCT_TAG")))
            CSAppException.apperr(ShareMealException.CRM_SHARE_5);
        
        IData commpara = new DataMap();
        commpara.put("SUBSYS_CODE", "CSM");
        commpara.put("PARAM_ATTR", "5544");
        commpara.put("PARAM_CODE", "SHARE");
        commpara.put("PARA_CODE1", productInfo.getData(0).getString("PRODUCT_ID"));
        IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
        if(IDataUtil.isNotEmpty(commparaDs))
        {
        	IData map = new DataMap();
        	map.put("USER_ID", userId);
        	map.put("PARAM_ATTR", "5545");
        	map.put("PARAM_CODE", "SHARE");
        	IDataset dscnt = UserDiscntInfoQry.queryUserAllDiscntsByUserIdPCC(map);
        	if (IDataUtil.isNotEmpty(dscnt))
        	{
                CSAppException.apperr(ShareMealException.CRM_SHARE_25);
        	}
        }
        
        //验证主卡是否办理JTZ套餐
//        IDataset discntSet=UserDiscntInfoQry.getAllDiscntByUser(userId, "270");
//        if(discntSet!=null&&discntSet.size()>0){
//  		   CSAppException.apperr(ShareMealException.CRM_SHARE_22, mainSn);
//  	    }
    }

    /**
     * 添加成员时校验成员信息
     * 
     * @param data
     * @throws Excepion
     */
    public IDataset checkAddMeb(IData data) throws Exception
    {
    	//主号码
    	IDataset rds = new DatasetList();
        String mainSn = data.getString("SERIAL_NUMBER");
        //副号码
        String sn = data.getString("SERIAL_NUMBER_B");

        // 新增的成员号码不能和主卡号码一致,请确认！
        if (mainSn.equals(sn))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "新增成员号码不能与主卡号码一致！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(ShareMealException.CRM_SHARE_6);
        }
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);
        if (IDataUtil.isEmpty(mainUser))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号码"+mainSn+"用户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        String userId = mainUser.getString("USER_ID");
        IData mainAcct = UcaInfoQry.qryAcctInfoByUserId(userId);
        // 查询成员信息
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号"+sn+"用户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }
        String userIdB = user.getString("USER_ID");
        IData Acct = UcaInfoQry.qryAcctInfoByUserId(userIdB);
        // 判断地州 海南暂时不需要 sunxin
        if (!mainUser.getString("EPARCHY_CODE").equals(user.getString("EPARCHY_CODE")))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "成员归属地州和主卡地州不一致！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_8);
        }
            
        // 判断账期
        String mainAcctId = mainAcct.getString("ACCT_ID");
        String AcctId = Acct.getString("ACCT_ID");
        IDataset mainAcctDay = UserAcctDayInfoQry.getAccountAcctDay(mainAcctId);
        IDataset AcctDay = UserAcctDayInfoQry.getAccountAcctDay(AcctId);
        if (IDataUtil.isNotEmpty(mainAcctDay) && IDataUtil.isNotEmpty(AcctDay))
        {
            if (!mainAcctDay.getData(0).getString("ACCT_DAY").equals(AcctDay.getData(0).getString("ACCT_DAY")))
            {
            	data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "该号码帐期与主卡不一致，不允许添加成员,请先办理帐期变更业务。");
            	rds.add(data);
            	return rds;
            	//CSAppException.apperr(ShareMealException.CRM_SHARE_7);
            }
                
        }
        // 判断成员是不是已经存在共享关系
        IDataset returnDataMen = ShareInfoQry.queryMemberRela(userIdB, "02");
        if (IDataUtil.isNotEmpty(returnDataMen))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该用户已经添加到别的共享，不可以多次添加共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_9);
        }
            

        IDataset returnDataMain = ShareInfoQry.queryMemberRela(userIdB, "01");
        if (IDataUtil.isNotEmpty(returnDataMain))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该用户已经添加到别的共享，不可以多次添加共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_9);
        }
            
        // 判断是否未激活
        if (!"0".equals(user.getString("ACCT_TAG")))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "未激活用户不能参与多终端共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_5);
        }
            
        // 判断是不是已经存在4个成员
        IDataset returnDataMenber = ShareInfoQry.queryMember(userId);
        if (returnDataMenber.size() >= 4)
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "主卡当前有4个副卡共享成员,不能再新增共享成员！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_21);
        }
        
        //98元档可增加1张副卡、158元档可增加2张副卡，新入网或携入30天内的客户，可作为“多业务共享”副卡
        //IData userProduct = UcaInfoQry.qryMainProdInfoByUserId(userId);
        boolean tipFlag = true;
        IDataset ds = UserProductInfoQry.queryUserMainProduct(userId);
        if(IDataUtil.isNotEmpty(ds))
        {
        	for(int i=0;i<ds.size();i++)
        	{
        		IData userProduct = ds.getData(i);
        		IData commpara = new DataMap();
                commpara.put("SUBSYS_CODE", "CSM");
                commpara.put("PARAM_ATTR", "5544");
                commpara.put("PARAM_CODE", "SHARE");
                commpara.put("PARA_CODE1", userProduct.getString("PRODUCT_ID"));
                IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                if(IDataUtil.isNotEmpty(commparaDs))
                {
                	IData map = commparaDs.getData(0);
            		boolean isDependOpenDate = false;
                    String openDate = user.getString("OPEN_DATE");
                    String limitCount = map.getString("PARA_CODE2");
                    String limitCode = map.getString("PARA_CODE3");
                    String sysDate = SysDateMgr.getSysTime();
                    String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(limitCode));

                    if (tempDate.compareTo(sysDate) > 0)
                        isDependOpenDate = true;

                    if (!isDependOpenDate)
                    {
                    	data.put("WARM_TIPS", "-1");
                    	data.put("WARM_INFO", "新入网或携入"+Integer.parseInt(limitCode)+"天内的客户，才可作为“多业务共享”副卡！");
                    	rds.add(data);
                    	return rds;
                    	//CSAppException.apperr(ShareMealException.CRM_SHARE_23,Integer.parseInt(limitCode));                 	
                    }
                    
                    if(returnDataMenber.size() >= Integer.parseInt(limitCount)){
                    	data.put("WARM_TIPS", "-1");
                    	data.put("WARM_INFO", "主卡当前有"+returnDataMenber.size()+"个副卡共享成员,不能再新增共享成员！");
                    	rds.add(data);
                    	return rds;
                    	//CSAppException.apperr(ShareMealException.CRM_SHARE_24,returnDataMenber.size());
                    }
                    
                    IData param = new DataMap();
                    param.put("USER_ID", userIdB);
                    param.put("PARAM_ATTR", "5545");
                    param.put("PARAM_CODE", "SHARE");
                	IDataset dscnt = UserDiscntInfoQry.queryUserAllDiscntsByUserIdPCC(param);
                	if (IDataUtil.isNotEmpty(dscnt))
                	{
                		for(int j=0;j<dscnt.size();j++)
                		{
                			if(SysDateMgr.compareTo(dscnt.getData(j).getString("END_DATE"), userProduct.getString("START_DATE")) > 0)
                        	{
                				data.put("WARM_TIPS", "-1");
                            	data.put("WARM_INFO", "号码当前有流量冲浪包、4G流量套餐等，不满足办理条件！");
                            	rds.add(data);
                            	return rds;
                				//CSAppException.apperr(ShareMealException.CRM_SHARE_25);
                        	}
                		} 
                	}
                	
                	tipFlag = false;
                }
        	}
        }
        
        /*IData dparam = new DataMap();
        dparam.put("USER_ID", userId);
    	IDataset discntp = UserDiscntInfoQry.queryUserAllDiscntsByUserIdShareMeal(dparam);
    	if(IDataUtil.isNotEmpty(discntp))
    	{
    		if(IDataUtil.isEmpty(UserDiscntInfoQry.queryUserAllDiscntsByUserIdShareMealT(dparam)))
        	{
        		data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "主卡存在预约下月生效的不可共享套餐，不能办理共享业务，请先取消预约的套餐。");
            	rds.add(data);
            	return rds;
        		//CSAppException.apperr(ShareMealException.CRM_SHARE_26);
        	}
    		
    	}*/
    
        if(tipFlag)
        {
        	data.put("WARM_TIPS", "1");
        }


        //QR-20150120-02 通过网厅办理多终端流量共享问题,修改页面添加时间@yanwu
        String disendDate = "";
        //IDataset mainList = ShareInfoQry.queryMemberRela(userId, "01");
        //if ( IDataUtil.isEmpty(mainList) ){
        	disendDate = SysDateMgr.END_DATE_FOREVER;
        //}
        //else{
        //   disendDate = mainList.getData(0).getString("END_DATE");
        //}
    	String dissysdate = "";
        // 先查询用户下预约生效的可以共享的4g资费，如果没有再查已经生效的可以共享的4g资费。
        IDataset discntInfo = null;
        discntInfo = ShareInfoQry.queryDiscntsNEW(userId);
        if(IDataUtil.isNotEmpty(discntInfo) && null != discntInfo && discntInfo.size() > 0){
        	dissysdate = discntInfo.getData(0).getString("DISCNT_START_DATE");
        }else{
        	dissysdate = SysDateMgr.getSysTime();
        }
        data.put("START_DATE", dissysdate);
        data.put("END_DATE", disendDate);
        rds.add(data);
    	return rds;
    }

    /**
     * 校验入参
     * 
     * @param inData
     * @throws Exception
     */
    public void checkInData(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER", "");
        String serialNumberA = inData.getString("SERIAL_NUMBER_A", "");
        String type = inData.getString("TYPE", "");// 操作类型： "ADD" :新增 "DEL" :取消
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(ShareMealException.CRM_SHARE_15);
        }
        if (StringUtils.isBlank(serialNumberA))
        {
            CSAppException.apperr(ShareMealException.CRM_SHARE_16);
        }
        if (StringUtils.isBlank(type))
        {
            CSAppException.apperr(ShareMealException.CRM_SHARE_17);
        }
        if (serialNumber.equals(serialNumberA))
            CSAppException.apperr(ShareMealException.CRM_SHARE_18);
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
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String serialNumberA = data.getString("SERIAL_NUMBER_A", "");
        String type = data.getString("TYPE", "");// "ADD" :新增 "DEL" :取消

        // 校验入参
        checkInData(data);

        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_78, serialNumber);// 830009:SERIAL_NUMBER无效
        }
        
        // 新增
        if (StringUtils.equals(type, "ADD"))
        {
	        //校验副号
	        data.put("SERIAL_NUMBER_B", serialNumberA);
	        IDataset rds = checkAddMeb(data);
	        if(IDataUtil.isNotEmpty(rds))
	        {
	        	IData mm = rds.getData(0);
	        	if("-1".equals(mm.getString("WARM_TIPS", "")))
	        	{
	        		CSAppException.appError("201801152100", mm.getString("WARM_INFO", ""));
	        	}
	        }
        }
        
        // 校验服务号码是否有效
        IData userA = UcaInfoQry.qryUserInfoBySn(serialNumberA);
        if (IDataUtil.isEmpty(userA))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_78, serialNumberA);// 830009:SERIAL_NUMBER无效
        }
        data.put("USER_ID", user.getString("USER_ID"));
        data.put("MEMBER_CANCEL", "0");
        this.check(data);

        // 新增
        if (StringUtils.equals(type, "ADD"))
        {
            IData map = new DataMap();
            map.put("SERIAL_NUMBER", serialNumber);
            map.put("SERIAL_NUMBER_B", serialNumberA);
            // 新增校验处理
            this.checkAddMeb(map);

            IData params = new DataMap();
            IDataset mebs = new DatasetList();

            // 新增的号码
            map.put("tag", "0");
            map.put("SERIAL_NUMBER", serialNumberA);
            mebs.add(map);

            // 调用共享流量业务服务
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("MEB_LIST", mebs);
            params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            IDataset rtDataset = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", params);

            // 返回信息
            IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "您新增的流量共享号码" + serialNumberA + "已成功。");
            backInfo.put("X_RECORDNUM", "1");

            return backInfo;
        }

        // 删除
        if (StringUtils.equals(type, "DEL"))
        {

            String userIdA = userA.getString("USER_ID");
            IDataset mebList = ShareInfoQry.queryMemberRela(userIdA, "02");
            if (IDataUtil.isEmpty(mebList))
                CSAppException.apperr(ShareMealException.CRM_SHARE_19);
            //添加重复取消校验
            IData cancelData = new DataMap();
            cancelData.put("USER_ID",userIdA);
            cancelData.put("SERIAL_NUMBER", serialNumberA);
            this.queryShareMeb(cancelData);
            String instId = mebList.getData(0).getString("INST_ID");
            IData params = new DataMap();
            IDataset mebs = new DatasetList();

            // 删除的号码
            IData delMeb = new DataMap();
            delMeb.put("SERIAL_NUMBER_B", serialNumberA);
            delMeb.put("tag", "1");
            delMeb.put("INST_ID", instId);

            mebs.add(delMeb);

            // 调用业务服务
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("MEB_LIST", mebs);
            params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            IDataset rtDataset = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", params);

            // 返回信息
            IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "您已成功将共享号码" + serialNumberA + "删除。");
            backInfo.put("X_RECORDNUM", "1");
            backInfo.put("SERIAL_NUMBER_B", serialNumberA);

            return backInfo;
        }

        return null;
    }

    // 查询共享优惠
    public IDataset queryShareDiscntList(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        // 查询用户下所有可以共享4g资费
        IDataset discntInfo = ShareInfoQry.queryDiscnts(userId);
        if (IDataUtil.isEmpty(discntInfo))
            CSAppException.apperr(ShareMealException.CRM_SHARE_3);

        return discntInfo;
    }

    // 副卡取消查询本身
    public IDataset queryShareMeb(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String sn = data.getString("SERIAL_NUMBER");
        // 需要判断是否可以取消 即结束时间为用户本账期最后一天
        IDataset mebList = ShareInfoQry.queryMemberRela(userId, "02");
        // 一次线程设置一个多账期的环境变量，因所有号码账期必须一致，所以只需要取主卡的即可
        UcaDataFactory.getNormalUca(sn);
        String date = SysDateMgr.getLastDateThisMonth();
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int i = 0; i < mebList.size(); i++)
            {
                IData map = mebList.getData(i);
                String endDate = map.getString("END_DATE");
                if (date.equals(endDate))
                    CSAppException.apperr(ShareMealException.CRM_SHARE_12);
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
        IDataset mebList = ShareInfoQry.queryMember(userId);
        // 一次线程设置一个多账期的环境变量，因所有号码账期必须一致，所以只需要取主卡的即可
        UcaDataFactory.getNormalUca(sn);
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
                {
                	if("1".equals(map.getString("RSRV_TAG1","")))
                	{
                		map.put("DEAL_TAG", "1");// 传递到前台，不能取消
                	}else{
                		map.put("DEAL_TAG", "0");
                	}	
                }
            }
        }
        return mebList;

    }
    
    
    public IDataset checkAddMebIntf(IData data) throws Exception
    {
    	//主号码
    	IDataset rds = new DatasetList();
        String mainSn = data.getString("SERIAL_NUMBER");
        //副号码
        String sn = data.getString("SERIAL_NUMBER_B");

        // 新增的成员号码不能和主卡号码一致,请确认！
        if (mainSn.equals(sn))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "新增成员号码不能与主卡号码一致！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(ShareMealException.CRM_SHARE_6);
        }
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainSn);

        if (IDataUtil.isEmpty(mainUser))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号码"+mainSn+"用户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        String userId = mainUser.getString("USER_ID");
        String custId = mainUser.getString("CUST_ID");
        IData mainAcct = UcaInfoQry.qryAcctInfoByUserId(userId);
        
        IData mainCustomer = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(mainCustomer))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号码"+mainSn+"客户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        
        
        // 查询成员信息
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号"+sn+"用户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }
        String userIdB = user.getString("USER_ID");
        String custIdB = user.getString("CUST_ID");
        IData Acct = UcaInfoQry.qryAcctInfoByUserId(userIdB);
        // 判断地州 海南暂时不需要 sunxin
        if (!mainUser.getString("EPARCHY_CODE").equals(user.getString("EPARCHY_CODE")))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "成员归属地州和主卡地州不一致！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_8);
        }
        
        IData userCustomer = UcaInfoQry.qryCustomerInfoByCustId(custIdB);
        if (IDataUtil.isEmpty(userCustomer))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该服务号码"+sn+"客户信息不存在！");
        	rds.add(data);
        	return rds;
            //CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        
        if(!mainCustomer.getString("CUST_NAME","").equals(userCustomer.getString("CUST_NAME","")) || !mainCustomer.getString("PSPT_ID","").equals(userCustomer.getString("PSPT_ID","")))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该副号"+sn+"的姓名或身份证号码的实名信息和主号不一致。");
        	rds.add(data);
        	return rds;
        }
            
        // 判断账期
        String mainAcctId = mainAcct.getString("ACCT_ID");
        String AcctId = Acct.getString("ACCT_ID");
        IDataset mainAcctDay = UserAcctDayInfoQry.getAccountAcctDay(mainAcctId);
        IDataset AcctDay = UserAcctDayInfoQry.getAccountAcctDay(AcctId);
        if (IDataUtil.isNotEmpty(mainAcctDay) && IDataUtil.isNotEmpty(AcctDay))
        {
            if (!mainAcctDay.getData(0).getString("ACCT_DAY").equals(AcctDay.getData(0).getString("ACCT_DAY")))
            {
            	data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "该号码帐期与主卡不一致，不允许添加成员,请先办理帐期变更业务。");
            	rds.add(data);
            	return rds;
            	//CSAppException.apperr(ShareMealException.CRM_SHARE_7);
            }
                
        }
        // 判断成员是不是已经存在共享关系
        IDataset returnDataMen = ShareInfoQry.queryMemberRela(userIdB, "02");
        if (IDataUtil.isNotEmpty(returnDataMen))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该用户已经添加到别的共享，不可以多次添加共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_9);
        }
            

        IDataset returnDataMain = ShareInfoQry.queryMemberRela(userIdB, "01");
        if (IDataUtil.isNotEmpty(returnDataMain))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "该用户已经添加到别的共享，不可以多次添加共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_9);
        }
            
        // 判断是否未激活
        if (!"0".equals(user.getString("ACCT_TAG")))
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "未激活用户不能参与多终端共享！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_5);
        }
            
        // 判断是不是已经存在4个成员
        IDataset returnDataMenber = ShareInfoQry.queryMember(userId);
        if (returnDataMenber.size() >= 3)
        {
        	data.put("WARM_TIPS", "-1");
        	data.put("WARM_INFO", "主卡当前有3个副卡共享成员,不能再新增共享成员！");
        	rds.add(data);
        	return rds;
        	//CSAppException.apperr(ShareMealException.CRM_SHARE_21);
        }
        
        
        
        //98元档可增加1张副卡、158元档可增加2张副卡，新入网或携入30天内的客户，可作为“多业务共享”副卡
        //IData userProduct = UcaInfoQry.qryMainProdInfoByUserId(userId);
        boolean tipFlag = true;
		IData commpara = new DataMap();
        commpara.put("SUBSYS_CODE", "CSM");
        commpara.put("PARAM_ATTR", "5544");
        commpara.put("PARAM_CODE", "SHARE");
        commpara.put("PARA_CODE1", data.getString("NEW_PRODUCT_ID"));
        IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
        if(IDataUtil.isNotEmpty(commparaDs))
        {
        	IData map = commparaDs.getData(0);
    		boolean isDependOpenDate = false;
            String openDate = user.getString("OPEN_DATE");
            String limitCount = map.getString("PARA_CODE2");
            String limitCode = map.getString("PARA_CODE3");
            String sysDate = SysDateMgr.getSysTime();
            String tempDate = SysDateMgr.addDays(openDate, Integer.parseInt(limitCode));

            if (tempDate.compareTo(sysDate) > 0)
                isDependOpenDate = true;

            if (!isDependOpenDate)
            {
            	data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "新入网或携入"+Integer.parseInt(limitCode)+"天内的客户，才可作为“多业务共享”副卡！");
            	rds.add(data);
            	return rds;
            	//CSAppException.apperr(ShareMealException.CRM_SHARE_23,Integer.parseInt(limitCode));                 	
            }
            
            if(returnDataMenber.size() >= Integer.parseInt(limitCount)){
            	data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "主卡当前有"+returnDataMenber.size()+"个副卡共享成员,不能再新增共享成员！");
            	rds.add(data);
            	return rds;
            	//CSAppException.apperr(ShareMealException.CRM_SHARE_24,returnDataMenber.size());
            }
            
            IData param = new DataMap();
            param.put("USER_ID", userIdB);
            param.put("PARAM_ATTR", "5545");
            param.put("PARAM_CODE", "SHARE");
        	IDataset dscnt = UserDiscntInfoQry.queryUserAllDiscntsByUserIdPCC(param);
        	if (IDataUtil.isNotEmpty(dscnt))
        	{
        		for(int j=0;j<dscnt.size();j++)
        		{
        			if(SysDateMgr.compareTo(dscnt.getData(j).getString("END_DATE"), data.getString("START_DATE")) > 0)
                	{
        				data.put("WARM_TIPS", "-1");
                    	data.put("WARM_INFO", "号码当前有流量冲浪包、4G流量套餐等，不满足办理条件！");
                    	rds.add(data);
                    	return rds;
        				//CSAppException.apperr(ShareMealException.CRM_SHARE_25);
                	}
        		} 
        	}
        	
        	tipFlag = false;
        }
        /*IData dparam = new DataMap();
        dparam.put("USER_ID", userId);
        IDataset discntp = UserDiscntInfoQry.queryUserAllDiscntsByUserIdShareMeal(dparam);
    	if(IDataUtil.isNotEmpty(discntp))
    	{
    		if(IDataUtil.isEmpty(UserDiscntInfoQry.queryUserAllDiscntsByUserIdShareMealT(dparam)))
        	{
    			data.put("WARM_TIPS", "-1");
            	data.put("WARM_INFO", "主卡存在预约下月生效的不可共享套餐，不能办理共享业务，请先取消预约的套餐。");
            	rds.add(data);
            	return rds;
        		//CSAppException.apperr(ShareMealException.CRM_SHARE_26);
        	}
    		
    	}     */
        
        if(tipFlag)
        {
        	data.put("WARM_TIPS", "1");
        }


        //QR-20150120-02 通过网厅办理多终端流量共享问题,修改页面添加时间@yanwu
        String disendDate = "";
        //IDataset mainList = ShareInfoQry.queryMemberRela(userId, "01");
        //if ( IDataUtil.isEmpty(mainList) ){
        	disendDate = SysDateMgr.END_DATE_FOREVER;
        //}
        //else{
        //   disendDate = mainList.getData(0).getString("END_DATE");
        //}
    	String dissysdate = "";
        // 先查询用户下预约生效的可以共享的4g资费，如果没有再查已经生效的可以共享的4g资费。
        IDataset discntInfo = null;
        discntInfo = ShareInfoQry.queryDiscntsNEW(userId);
        if(IDataUtil.isNotEmpty(discntInfo) && null != discntInfo && discntInfo.size() > 0){
        	dissysdate = discntInfo.getData(0).getString("DISCNT_START_DATE");
        }else{
        	dissysdate = SysDateMgr.getSysTime();
        }
        data.put("START_DATE", dissysdate);
        data.put("END_DATE", disendDate);
        rds.add(data);
    	return rds;
    }

}
