
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class FamilyTradeBean extends CSBizBean
{

    public static String getDiscntCodeByBrandCode(String brandCode, String productId) throws Exception
    {
        String discntCode = null;
        if ("G010".equals(brandCode))
        {// 动感地带
            discntCode = "866";
        }
        else if ("G002".equals(brandCode) && "10001254".equals(productId))
        {// 神州行轻松卡产品
            discntCode = "1255";
        }
        else if ("G002".equals(brandCode) && "10001264".equals(productId))
        {// 神州行幸福卡产品
            discntCode = "1266";
        }
        else
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_42);
        }
        return discntCode;
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
        IDataset memberList = new DatasetList(data.getString("MEB_LIST", "[]"));

        // 新增的亲情号码不能和主卡号码一致,请确认！
        if (mainSn.equals(sn))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_56);
        }

        IData mainUser = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainSn);
        }
        String proudctId = mainUser.getString("PRODUCT_ID");
        String brandCode = mainUser.getString("BRAND_CODE");

        // 副号相同号码校验：1.新增时页面相同号码校验（不包括删除的）
        // 2.与已存在的成员比较，如果相同则判断是否是删除成员，如果删除成员列表为空或者已存在的成员在删除列表里则报错
        IDataset delMebs = new DatasetList();
        for (int i = 0, length = memberList.size(); i < length; i++)
        {
            IData member = memberList.getData(i);
            String memberSn = member.getString("SERIAL_NUMBER_B");
            String tag = member.getString("tag");
            if (StringUtils.equals(sn, memberSn) && !StringUtils.equals(tag, "1"))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_57, sn);
            }
            if (StringUtils.equals(tag, "1"))
            {
                delMebs.add(memberSn);
            }
        }
        IData param = new DataMap();
        String mUserId = mainUser.getString("USER_ID");
        param.put("USER_ID", mUserId);
        param.put("IS_ADD", true);
        IDataset mebList = queryFamilyMebList(param);
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int j = 0, size = mebList.size(); j < size; j++)
            {
                IData meb = mebList.getData(j);
                String mebSn = meb.getString("SERIAL_NUMBER_B");
                if (StringUtils.equals(sn, mebSn))
                {
                    if (IDataUtil.isEmpty(delMebs) || !delMebs.contains(mebSn))
                    {
                        CSAppException.apperr(FamilyException.CRM_FAMILY_57, sn);
                    }
                }
            }
        }

        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(user))
        {
            // 外网号码
        }
        else
        {
            String userId = user.getString("USER_ID");

            // 账期限制
            IDataset userAcctDays = UcaInfoQry.qryUserAcctDaysByUserId(userId);
            String acctDay = "";
            String nextAcctDay = "";
            if (IDataUtil.isNotEmpty(userAcctDays))
            {
                int size = userAcctDays.size();
                IData userAcctDay = userAcctDays.getData(0);
                acctDay = userAcctDay.getString("ACCT_DAY");
                if (size > 1)
                {
                    IData userNextAcctDay = userAcctDays.getData(1);
                    nextAcctDay = userNextAcctDay.getString("ACCT_DAY");
                }
            }

            if (StringUtils.isNotBlank(nextAcctDay) && !StringUtils.equals(acctDay, nextAcctDay))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_46, sn);
            }
            if (!"1".equals(acctDay))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_47, sn);
            }

            // 特殊号码校验，调帐管接口
            IDataset result = AcctCall.checkSpecialNumber(sn);
            if (IDataUtil.isNotEmpty(result))
            {
                String xTag = result.getData(0).getString("X_TAG", "");
                if (StringUtils.equals(xTag, "1"))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_501, sn);
                }
            }
        }

        // 非法号码判断
        if (!StringUtils.isNumeric(sn))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_48, sn);
        }

        String[] fbPhones = getForbidPhoneCode(brandCode).split(",");
        if (fbPhones == null)
        {
            fbPhones = new String[]
            {};
        }

        IData npInfo = getUserNpInfoAll(sn);// 获取携转信息
        boolean isNp = false;// 是否携转到移动

        for (int i = 0, size = fbPhones.length; i < size; i++)
        {
            if (sn.startsWith(fbPhones[i]))// 外网携入到移动的用户也可以办理
            {
                // 非携转到移动
                if (IDataUtil.isEmpty(npInfo) || !npInfo.getString("PORT_IN_NETID").substring(0, 3).equals("002"))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_49, sn);
                }
                // 携转到移动
                else
                {
                    isNp = true;
                }
            }
        }

        if ("G002".equals(brandCode))// 神州行号码新增亲情号码，需判断移动号码是否已携出
        {
            if (isNpUser(sn))// 归属移动
            {
                if (IDataUtil.isNotEmpty(npInfo) && !npInfo.getString("PORT_IN_NETID").substring(0, 3).equals("002"))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_51, sn);
                }
            }
        }

        if ("10001264".equals(proudctId))// 神州行幸福卡可以新增中国移动号码或携入至移动的号码
        {
            if (!isNpUser(sn))// 不归属移动
            {
                if (!isNp)
                {// 没有携入至移动
                    CSAppException.apperr(FamilyException.CRM_FAMILY_52, sn);
                }
            }
        }
        
        IDataset userFmyDisCnt = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode2(mUserId);
        if (null == userFmyDisCnt || userFmyDisCnt.size() == 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_55);
        }
    }

    /**
     * 办理该业务前，动感地带的必须是已经办理了866亲情号码套餐，神州行轻松卡用户必须是已经办理了1255轻松卡之亲情号码套餐
     * 
     * @date 2014-05-20 16:12:21
     * @author zhouwu
     */
    public void checkFmyDisCnt(String userId) throws Exception
    {
        IData userMainProd = UcaInfoQry.qryUserMainProdInfoByUserId(userId);

        if (IDataUtil.isEmpty(userMainProd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }

        String brandCode = userMainProd.getString("BRAND_CODE");
        String productId = userMainProd.getString("PRODUCT_ID");

        String discntCode = getDiscntCodeByBrandCode(brandCode, productId);

        // 老系统的sql_ref为：SEL_USER_BY_PK，新系统使用下面的查询方法，sql语句跟老系统是一致的
        IDataset userFmyDisCnt = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, discntCode);
        if (null == userFmyDisCnt || userFmyDisCnt.size() == 0)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_55);
        }
    }

    /**
     * 校验入参
     * 
     * @param inData
     * @throws Exception
     */
    public IData checkInData(IData inData) throws Exception
    {
    	IData backInfo = new DataMap();
        
        String serialNumber = inData.getString("SERIAL_NUMBER", "");// 手机号
        String xTag = inData.getString("X_TAG", "");// 操作类型：新增0,修改1,查询3
        if (StringUtils.isBlank(serialNumber))
        {
        	backInfo.put("X_RESULTCODE", "830001");
            backInfo.put("X_RESULTINFO", "SERIAL_NUMBER为空");
            //CSAppException.apperr(FamilyException.CRM_FAMILY_70);// 830001:SERIAL_NUMBER为空
        }
        if (StringUtils.isBlank(xTag))
        {
        	backInfo.put("X_RESULTCODE", "830002");
            backInfo.put("X_RESULTINFO", "X_TAG为空");
            //CSAppException.apperr(FamilyException.CRM_FAMILY_71);// 830002:X_TAG为空
        }
        if ("3".equals(xTag))
        {// 查询

        }
        else if ("0".equals(xTag))
        {// 新增
            String serialNumberB = inData.getString("SERIAL_NUMBER_B", "");// 新增副号
            if (StringUtils.isBlank(serialNumberB))
            {
            	backInfo.put("X_RESULTCODE", "830003");
                backInfo.put("X_RESULTINFO", "SERIAL_NUMBER_B为空");
                //CSAppException.apperr(FamilyException.CRM_FAMILY_72);// 830003:SERIAL_NUMBER_B为空
            }
            else if (serialNumberB.trim().length() < 11 || serialNumberB.length() > 12)
            {
            	backInfo.put("X_RESULTCODE", "830004");
                backInfo.put("X_RESULTINFO", "SERIAL_NUMBER_B长度不对，长度必须是11或12");
                //CSAppException.apperr(FamilyException.CRM_FAMILY_73);// 830004:SERIAL_NUMBER_B长度不对，长度必须是11或12
            }
        }
        else if ("1".equals(xTag))
        {// 修改
            String serialNumberB = inData.getString("SERIAL_NUMBER_B", "");// 原副号
            String sequenNum = inData.getString("SEQUEN_NUM", "");// 序列号
            String serialNumberC = inData.getString("SERIAL_NUMBER_C", "");// 新副号
            if (StringUtils.isBlank(serialNumberB))
            {// 老号码
                if (StringUtils.isBlank(sequenNum))
                {
                	backInfo.put("X_RESULTCODE", "830005");
                    backInfo.put("X_RESULTINFO", "SERIAL_NUMBER_B和SEQUEN_NUM为空");
                    //CSAppException.apperr(FamilyException.CRM_FAMILY_74);// 830005:SERIAL_NUMBER_B和SEQUEN_NUM为空
                }
            }
            if (StringUtils.isBlank(serialNumberC))
            {// 新号码
            	backInfo.put("X_RESULTCODE", "830006");
                backInfo.put("X_RESULTINFO", "SERIAL_NUMBER_C为空");
                //CSAppException.apperr(FamilyException.CRM_FAMILY_75);// 830006:SERIAL_NUMBER_C为空
            }
            else if (serialNumberC.trim().length() < 11 || serialNumberC.trim().length() > 12)
            {
            	backInfo.put("X_RESULTCODE", "830007");
                backInfo.put("X_RESULTINFO", "SERIAL_NUMBER_C长度不对，长度必须是11或12");
                //CSAppException.apperr(FamilyException.CRM_FAMILY_76);// 830007:SERIAL_NUMBER_C长度不对，长度必须是11或12
            }
        }
        else
        {
        	backInfo.put("X_RESULTCODE", "830008");
            backInfo.put("X_RESULTINFO", "操作类型不能被识别（新增0,修改1,查询3）");
            //CSAppException.apperr(FamilyException.CRM_FAMILY_77);// 830008:操作类型不能被识别（新增0,修改1,查询3）
        }
        return backInfo;
    }

    /**
     * 亲情业务接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData createFamilyTrade(IData data) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String xTag = data.getString("X_TAG", "");

        // 校验入参
        IData checkDate = checkInData(data);
        if( IDataUtil.isNotEmpty(checkDate) ){
        	return checkDate;
        }
        
        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
        	IData backInfo = new DataMap();
        	backInfo.put("X_RESULTCODE", "830009");
            backInfo.put("X_RESULTINFO", "SERIAL_NUMBER无效");
            return backInfo;
            //CSAppException.apperr(FamilyException.CRM_FAMILY_78);// 830009:SERIAL_NUMBER无效
        }

        // 校验是否办理了亲情优惠
        String userId = user.getString("USER_ID");
        try {
        	checkFmyDisCnt(userId);
		} catch (Exception e) {
			IData resultData = new DataMap();
			String[] errorMessage = e.getMessage().split("●");
			if(errorMessage[0].contains("您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务")){
				
	    		resultData.put("X_RESULTCODE", "0");
            	resultData.put("X_RESULTCODE", "830030");
            	resultData.put("X_RSPCODE","830030");
            	resultData.put("X_RESULTINFO", "您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务！");
            	resultData.putAll(resultData);
            	return resultData;
            }
			resultData.put("X_RESULTCODE", "0");
        	resultData.put("X_RESULTCODE", "830031");
        	resultData.put("X_RSPCODE","830031");
        	resultData.put("X_RESULTINFO", errorMessage[0]);
        	resultData.putAll(resultData);
        	return resultData;
		}
        

        // 查询所有副卡
        IDataset mebList = queryAllUnionPayMembers(userId);// 此处会有个问题：每次查询出来的序列号可能会变化 已解决

        // 查询
        if (StringUtils.equals(xTag, "3"))
        {
            String serialNumberB = "";
            if (IDataUtil.isEmpty(mebList))
            {
            	IData backInfo = new DataMap();
            	backInfo.put("X_RESULTCODE", "830012");
                backInfo.put("X_RESULTINFO", "您还未设置亲情号码!");
                return backInfo;
                //CSAppException.apperr(FamilyException.CRM_FAMILY_79);// 830012:您还未设置亲情号码!
            }
            else
            {
                serialNumberB = fromDatasetToStringSeq(mebList, "SERIAL_NUMBER_B");
            }
            int modifyTimes = getCurMonthModifyTimes(userId);
            IData redata = new DataMap();
            redata.put("MODI_NUM", modifyTimes);
            redata.put("SERIAL_NUMBER_B", serialNumberB);
            redata.put("X_RESULTCODE", "0");
            redata.put("X_RESULTINFO", "ok!");
            redata.put("X_RECORDNUM", "1");
            return redata;
        }

        // 新增
        if (StringUtils.equals(xTag, "0"))
        {
            String serialNumberB = data.getString("SERIAL_NUMBER_B", "");
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData meb = mebList.getData(i);
                String mebSn = meb.getString("SERIAL_NUMBER_B");
                if (StringUtils.equals(serialNumberB, mebSn))
                {
                	IData backInfo = new DataMap();
                	backInfo.put("X_RESULTCODE", "830013");
                    backInfo.put("X_RESULTINFO", String.format("[%s]已经是您的亲情号码，无需重复设置", serialNumberB));
                    return backInfo;
                    //CSAppException.apperr(FamilyException.CRM_FAMILY_80, serialNumberB);// 830013:"serialNumberB"已经是您的亲情号码,无需重复设置
                }
            }

            IData params = new DataMap();
            IDataset mebs = new DatasetList();

            // 新增的号码
            IData meb = new DataMap();
            meb.put("SERIAL_NUMBER_B", serialNumberB);
            meb.put("tag", "0");
            mebs.add(meb);

            // 调用亲情业务服务
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("MEB_LIST", mebs);
    		
            IData callResult = new DataMap();
    		IData resultData = new DataMap();
    		resultData.put("X_RESULTCODE", "0");
            try {
            	callResult = CSAppCall.call("SS.FamilyTrade.tradeReg", params).getData(0);
    		} catch (Exception e) {
    			String[] errorMessage = e.getMessage().split("●");
    			if(errorMessage[0].contains("新增的亲情号码不能和主卡号码一致,请确认")){
    				resultData.put("X_RESULTCODE", "830020");
                	resultData.put("X_RSPCODE","830020");
                	resultData.put("X_RESULTINFO", "新增的亲情号码不能和主卡号码一致,请确认！");
                	resultData.putAll(resultData);
    			}else if(errorMessage[0].contains("新增的副号码不能和主号码一致，请确认")){
                	resultData.put("X_RESULTCODE", "830017");
                	resultData.put("X_RSPCODE","830017");
                	resultData.put("X_RESULTINFO", "新增的副号码不能和主号码一致，请确认！");
                	resultData.putAll(resultData);
            		return resultData;
                }else if(errorMessage[0].contains("用户信息不存在")){
                	resultData.put("X_RESULTCODE", "830018");
                	resultData.put("X_RSPCODE","830018");
                	resultData.put("X_RESULTINFO", String.format("该服务号码[%s]用户信息不存在！", serialNumber));
                	resultData.putAll(resultData);
            		return resultData;
                }else if(errorMessage[0].contains("已经在成员列表")){
                	resultData.put("X_RESULTCODE", "830019");
                	resultData.put("X_RSPCODE","830019");
                	resultData.put("X_RESULTINFO", String.format("号码[%s]已经在成员列表!", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("请在预约账期生效后来办理该业务")){
                	resultData.put("X_RESULTCODE", "830021");
                	resultData.put("X_RSPCODE","830021");
                	resultData.put("X_RESULTINFO", String.format("用户[%s]存在预约账期,请在预约账期生效后来办理该业务!", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("不是自然月用户，请确认后在加入")){
                	resultData.put("X_RESULTCODE", "830022");
                	resultData.put("X_RSPCODE","830022");
                	resultData.put("X_RESULTINFO", String.format("用户[%s]不是自然月用户，请确认后在加入。", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为特殊号码，不享受套餐的优惠资费，请重新设置")){
                	resultData.put("X_RESULTCODE", "830023");
                	resultData.put("X_RSPCODE","830023");
                	resultData.put("X_RESULTINFO", String.format("号码[%s]为特殊号码，不享受套餐的优惠资费，请重新设置", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E001)")){
                	resultData.put("X_RESULTCODE", "830024");
                	resultData.put("X_RSPCODE","830024");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E001)！", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E002)")){
                	resultData.put("X_RESULTCODE", "830025");
                	resultData.put("X_RSPCODE","830025");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E002)！", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E003)")){
                	resultData.put("X_RESULTCODE", "830026");
                	resultData.put("X_RSPCODE","830026");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E003)！", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E004)")){
                	resultData.put("X_RESULTCODE", "830027");
                	resultData.put("X_RSPCODE","830027");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E004)！", serialNumberB));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("您还未申请亲情号码套餐")){
                	resultData.put("X_RESULTCODE", "830028");
                	resultData.put("X_RSPCODE","830028");
                	resultData.put("X_RESULTINFO", "您还未申请亲情号码套餐！");
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务")){
                	resultData.put("X_RESULTCODE", "830029");
                	resultData.put("X_RSPCODE","830029");
                	resultData.put("X_RESULTINFO", "您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务！");
                	resultData.putAll(resultData);
                	return resultData;
                }else {
                	throw e;
                }
    		}
            resultData.putAll(callResult);
    		//resultList.add(resultData);
    		return resultData;

            // 返回信息
            /*IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "您设置的亲情号码" + serialNumberB + "已成功。");
            backInfo.put("X_RECORDNUM", "1");

            return backInfo;*/
        }

        // 修改
        if (StringUtils.equals(xTag, "1"))
        {
            int curCount = mebList.size();
            IData commPara = getCommpara(userId);
            int mebCountLim = commPara.getInt("FMY_SUB_COUNT_LIM", 0);
            if (curCount < mebCountLim)
            {
//                CSAppException.apperr(FamilyException.CRM_FAMILY_81, mebCountLim);
            }

            String serialNumberB = data.getString("SERIAL_NUMBER_B");// 原副号
            String serialNumberC = data.getString("SERIAL_NUMBER_C");// 新副号
            int sequenNum = data.getInt("SEQUEN_NUM");// 原副号序列号
            IData mebData = null;

            // 如果serialNumberB为空，则以sequenNum为准
            if (StringUtils.isBlank(serialNumberB))
            {
                for (int i = 0, size = mebList.size(); i < size; i++)
                {
                    mebData = mebList.getData(i);
                    if (i + 1 == sequenNum)
                    {
                        serialNumberB = mebData.getString("SERIAL_NUMBER_B");
                        break;
                    }
                }
            }

            boolean isOldMeb = false;// 是否原副号 默认false
            String instId = "";// 原副号的实例ID
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                mebData = mebList.getData(i);
                String tempMebSn = mebData.getString("SERIAL_NUMBER_B");
                if (StringUtils.equals(serialNumberB, tempMebSn))
                {
                    instId = mebData.getString("INST_ID");
                    isOldMeb = true;
                }
                if (StringUtils.equals(serialNumberC, tempMebSn))
                {
                	IData backInfo = new DataMap();
                	backInfo.put("X_RESULTCODE", "830015");
                    backInfo.put("X_RESULTINFO", String.format("号码[%s]已经是您的亲情号码，无需进行修改。", serialNumberC));
                    return backInfo;
                    //CSAppException.apperr(FamilyException.CRM_FAMILY_82, serialNumberC);// 830015:号码"serialNumberC"已经是您的亲情号码，无需进行修改。
                }
            }

            if (!isOldMeb)
            {
            	IData backInfo = new DataMap();
            	backInfo.put("X_RESULTCODE", "830016");
                backInfo.put("X_RESULTINFO", String.format("对不起！号码[%s]不是原来设置的亲情号码。", serialNumberB));
                return backInfo;
                //CSAppException.apperr(FamilyException.CRM_FAMILY_83, mebCountLim);// 830016:对不起！号码"serialNumberB"不是原来设置的亲情号码。
            }

            IData params = new DataMap();
            IDataset mebs = new DatasetList();

            // 删除的号码
            IData delMeb = new DataMap();
            delMeb.put("SERIAL_NUMBER_B", serialNumberB);
            delMeb.put("tag", "1");
            delMeb.put("INST_ID", instId);

            mebs.add(delMeb);

            // 新增的号码
            IData addMeb = new DataMap();
            addMeb.put("SERIAL_NUMBER_B", serialNumberC);
            addMeb.put("tag", "0");
            mebs.add(addMeb);

            // 调用亲情业务服务
            params.put("SERIAL_NUMBER", serialNumber);
            params.put("MEB_LIST", mebs);
            
            IData callResult = new DataMap();
    		IData resultData = new DataMap();
    		resultData.put("X_RESULTCODE", "0");
            try {
            	callResult = CSAppCall.call("SS.FamilyTrade.tradeReg", params).getData(0);
    		} catch (Exception e) {
    			String[] errorMessage = e.getMessage().split("●");
    			if(errorMessage[0].contains("新增的亲情号码不能和主卡号码一致,请确认")){
    				resultData.put("X_RESULTCODE", "830020");
                	resultData.put("X_RSPCODE","830020");
                	resultData.put("X_RESULTINFO", "新增的亲情号码不能和主卡号码一致,请确认！");
                	resultData.putAll(resultData);
    			}else if(errorMessage[0].contains("新增的副号码不能和主号码一致，请确认")){
                	resultData.put("X_RESULTCODE", "830017");
                	resultData.put("X_RSPCODE","830017");
                	resultData.put("X_RESULTINFO", "新增的副号码不能和主号码一致，请确认！");
                	resultData.putAll(resultData);
            		return resultData;
                }else if(errorMessage[0].contains("用户信息不存在")){
                	resultData.put("X_RESULTCODE", "830018");
                	resultData.put("X_RSPCODE","830018");
                	resultData.put("X_RESULTINFO", String.format("该服务号码[%s]用户信息不存在！", serialNumber));
                	resultData.putAll(resultData);
            		return resultData;
                }else if(errorMessage[0].contains("已经在成员列表")){
                	resultData.put("X_RESULTCODE", "830019");
                	resultData.put("X_RSPCODE","830019");
                	resultData.put("X_RESULTINFO", String.format("号码[%s]已经在成员列表!", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("请在预约账期生效后来办理该业务")){
                	resultData.put("X_RESULTCODE", "830021");
                	resultData.put("X_RSPCODE","830021");
                	resultData.put("X_RESULTINFO", String.format("用户[%s]存在预约账期,请在预约账期生效后来办理该业务!", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("不是自然月用户，请确认后在加入")){
                	resultData.put("X_RESULTCODE", "830022");
                	resultData.put("X_RSPCODE","830022");
                	resultData.put("X_RESULTINFO", String.format("用户[%s]不是自然月用户，请确认后在加入。", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为特殊号码，不享受套餐的优惠资费，请重新设置")){
                	resultData.put("X_RESULTCODE", "830023");
                	resultData.put("X_RSPCODE","830023");
                	resultData.put("X_RESULTINFO", String.format("号码[%s]为特殊号码，不享受套餐的优惠资费，请重新设置", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E001)")){
                	resultData.put("X_RESULTCODE", "830024");
                	resultData.put("X_RSPCODE","830024");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E001)！", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E002)")){
                	resultData.put("X_RESULTCODE", "830025");
                	resultData.put("X_RSPCODE","830025");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E002)！", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E003)")){
                	resultData.put("X_RESULTCODE", "830026");
                	resultData.put("X_RSPCODE","830026");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E003)！", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("为非法号码(E004)")){
                	resultData.put("X_RESULTCODE", "830027");
                	resultData.put("X_RSPCODE","830027");
                	resultData.put("X_RESULTINFO", String.format("对不起！你所设置亲情号码[%s]为非法号码(E004)！", serialNumberC));
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("您还未申请亲情号码套餐")){
                	resultData.put("X_RESULTCODE", "830028");
                	resultData.put("X_RSPCODE","830028");
                	resultData.put("X_RESULTINFO", "您还未申请亲情号码套餐！");
                	resultData.putAll(resultData);
                	return resultData;
                }else if(errorMessage[0].contains("您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务")){
                	resultData.put("X_RESULTCODE", "830029");
                	resultData.put("X_RSPCODE","830029");
                	resultData.put("X_RESULTINFO", "您不是动感地带、神州行轻松卡、神州行幸福卡用户，不能办理该业务！");
                	resultData.putAll(resultData);
                	return resultData;
                }else {
                	throw e;
                }
    		}
            resultData.putAll(callResult);
    		return resultData;
            
            // 返回信息
            /*IData backInfo = new DataMap();
            backInfo.put("X_RESULTCODE", "0");
            backInfo.put("X_RESULTINFO", "您已成功将亲情号码" + serialNumberB + "修改为" + serialNumberC + "。");
            backInfo.put("X_RECORDNUM", "1");
            backInfo.put("SERIAL_NUMBER_B", serialNumberB);

            return backInfo;*/
        }

        return null;
    }

    /**
     * 传入dataset，列名返回这个列所有行拼成的String,并加上序号
     * 
     * @param dataset
     * @param columnName
     * @return
     * @throws Exception
     */
    public String fromDatasetToStringSeq(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder sb = new StringBuilder(1024);
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData element = dataset.getData(i);
            sb.append(i + 1);
            sb.append("、");
            sb.append(element.getString(columnName));
            sb.append(",");
        }
        return sb.length() > 0 ? sb.replace(sb.length() - 1, sb.length(), "。").toString() : "";
    }

    /**
     * 获取通用参数配置
     * 
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private IData getCommpara(String userId) throws Exception
    {
        IData userMainProd = UcaInfoQry.qryUserMainProdInfoByUserId(userId);

        if (IDataUtil.isEmpty(userMainProd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }

        String brandCode = userMainProd.getString("BRAND_CODE");
        String productId = userMainProd.getString("PRODUCT_ID");
        String eparchyCode = userMainProd.getString("EPARCHY_CODE");

        IData rtParam = new DataMap();
        String paramCode = "FAMILYCNLIMIT_" + brandCode;
        int subCountLim = 0;
        int modiTimesLim = 0;
        IDataset result = CommparaInfoQry.getCommparaInfoBy5("CSM", "1010", paramCode, productId, eparchyCode, null);
        if (IDataUtil.isEmpty(result))
        {
            result = CommparaInfoQry.getCommparaAllCol("CSM", "1010", paramCode, eparchyCode);
        }

        if (IDataUtil.isNotEmpty(result))
        {
            IData commpara = result.getData(0);
            subCountLim = commpara.getInt("PARA_CODE2");
            modiTimesLim = commpara.getInt("PARA_CODE3");
        }

        rtParam.put("FMY_SUB_COUNT_LIM", subCountLim);
        rtParam.put("FMY_MODIFY_TIMES_LIM", modiTimesLim);

        return rtParam;
    }

    /**
     * 获取用户当月已经修改的次数
     * 
     * @param userId
     * @param productId
     * @return
     * @throws Exception
     */
    public int getCurMonthModifyTimes(String userId) throws Exception
    {
        int modiTimes = 0;

        // 查询用户主产品信息
        IData userMainProd = UcaInfoQry.qryUserMainProdInfoByUserId(userId);

        if (IDataUtil.isEmpty(userMainProd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }
        String productId = userMainProd.getString("PRODUCT_ID");

        // 查询other表信息获取当月修改次数
        IDataset userOther = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "1399");
        if (null != userOther && userOther.size() > 0)
        {
            for (int i = 0, size = userOther.size(); i < size; i++)
            {
                IData uo = userOther.getData(i);
                String rsrvValue = uo.getString("RSRV_VALUE", "");
                String rsrvValueCode = uo.getString("RSRV_VALUE_CODE", "");
                String rsrvStr1 = uo.getString("RSRV_STR1", "");
                if (productId.equals(rsrvValue) && "1399".equals(rsrvValueCode) && "HAIN".equals(rsrvStr1))
                {
                    modiTimes = uo.getInt("RSRV_STR2", 0);
                    break;
                }
            }
        }

        return modiTimes;
    }

    private String getForbidPhoneCode(String brandCode) throws Exception
    {
        String typeId = "FAMILY_LIMIT";
        IDataset result = StaticUtil.getStaticList(typeId);
        if ("G002".equals(brandCode))
        {
            typeId = "FAMILY_LIMIT_OTHER";
            result.addAll(StaticUtil.getStaticList(typeId));
        }
        else if ("G010".equals(brandCode))
        {
            typeId = "FAMILY_LIMIT_G010";
            result.addAll(StaticUtil.getStaticList(typeId));
        }

        return IDataUtil.fromDatasetToString(result, "DATA_ID");
    }

    /**
     * 查询用户当前携转信息（从全国信息库）
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-05-21 10:15:11
     */
    public IData getUserNpInfoAll(String serialNumber) throws Exception
    {
        IDataset out = TradeNpQry.getUserNpInfoAll(serialNumber);

        if (null != out && out.size() > 0)
            return out.getData(0);
        else
            return null;
    }

    /**
     * 判断用户是否归属移动
     * 
     * @param pd
     * @param serialNumber
     * @return true-归属移动用户,false-非归属移动用户
     * @throws Exception
     * @author zhouwu
     */
    public boolean isNpUser(String serialNumber) throws Exception
    {
        IData npRes = MsisdnInfoQry.getMsisonBySerialnumber(serialNumber, null);
        if (IDataUtil.isNotEmpty(npRes))
        {
            if (npRes.getString("ASP").equals("1"))
                return true;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * 获取亲情成员 以start_date排序
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IDataset queryAllUnionPayMembers(String userId) throws Exception
    {
        IDataset relaUUList = null;

        checkFmyDisCnt(userId);

        IDataset tmp = UserDiscntInfoQry.queryUserDiscntByUserIdRelation(userId,"75");
        if (IDataUtil.isNotEmpty(tmp))
        {
            String userIdA = tmp.getData(0).getString("USER_ID_A");
            relaUUList = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "75", "2");
        }

        if (null == relaUUList)
        {
            relaUUList = new DatasetList();
        }

        return relaUUList;
    }

    public IDataset queryFamilyMebList(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        boolean isAdd = data.getBoolean("IS_ADD", false);

        if (!isAdd)
            checkFmyDisCnt(userId);

        IDataset relaUUList = null;

        IDataset tmp = UserDiscntInfoQry.queryUserDiscntByUserIdRelation(userId,"75");
        if (IDataUtil.isNotEmpty(tmp))
        {
            String userIdA = tmp.getData(0).getString("USER_ID_A");
            relaUUList = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "75", "2");
        }
        if (null == relaUUList)
        {
            relaUUList = new DatasetList();
        }
        return relaUUList;
    }
}
