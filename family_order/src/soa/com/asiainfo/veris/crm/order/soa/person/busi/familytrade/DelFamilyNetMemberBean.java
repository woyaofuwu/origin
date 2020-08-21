
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class DelFamilyNetMemberBean extends CSBizBean
{

    /**
     * 业务受理前的校验
     * 
     * @param inData
     * @throws Exception
     */
    public void checkInData(IData inData) throws Exception
    {
        if (IDataUtil.isEmpty(inData))
        {
            // 输入数据为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_706);
        }

        String serialNumber = inData.getString("SERIAL_NUMBER");
        String serialNumberB = inData.getString("SERIAL_NUMBER_B");
        String xTag = inData.getString("X_TAG");// 操作类型
        String effectTag = inData.getString("EFFECT_TAG");// 生效标识 0:下月生效 1:立即生效

        if (StringUtils.isBlank(serialNumber))
        {
            // 办理业务号码SERIAL_NUMBER为空"
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }

        if (StringUtils.isBlank(serialNumberB))
        {
            // SERIAL_NUMBER_B为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_72);
        }

        if (StringUtils.isBlank(xTag))
        {
            // X_TAG为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_71);
        }

        if (StringUtils.isBlank(effectTag))
        {
            // 生效标志EFFECT_TAG不能为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_705);
        }

        String[] serialNumArray = serialNumberB.split(",");
        String[] xTagArray = xTag.split(",");
        String[] effectTagArray = effectTag.split(",");

        int len1 = serialNumArray.length;
        int len2 = xTagArray.length;
        int len3 = effectTagArray.length;
        if (len1 != len2 || len1 != len3 || len2 != len3)
        {
            // 成员长度与增删标识长度或生效标识长度不一致
            CSAppException.apperr(FamilyException.CRM_FAMILY_704);
        }

        for (int i = 0; i < len1; i++)
        {
            String tag = xTagArray[i];
            if (!"0".equals(tag) && !"1".equals(tag))
            {
                // X_TAG值不能被识别（0－开通、1－取消）
                CSAppException.apperr(FamilyException.CRM_FAMILY_703);
            }
        }

        for (int i = 0; i < len1; i++)
        {
            String tag = effectTagArray[i];
            if (!"0".equals(tag) && !"1".equals(tag))
            {
                // EFFECT_TAG值不能被识别（0－下月生效、1－立即生效）;
                CSAppException.apperr(FamilyException.CRM_FAMILY_702);
            }
        }

        for (int i = 0; i < len1; i++)
        {
            for (int j = i + 1; j < len1; j++)
            {
                if (serialNumArray[i].equals(serialNumArray[j]))
                {
                    // SERIAL_NUMBER_B不能存在两个相同的值
                    CSAppException.apperr(FamilyException.CRM_FAMILY_701);
                }
            }
        }

        // 查询用户信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            // SERIAL_NUMBER无效
            CSAppException.apperr(FamilyException.CRM_FAMILY_78);
        }

        // 查询UU关系
        String userIdB = userInfo.getString("USER_ID", "");
        IDataset relations = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userIdB, "45", null);
        if (IDataUtil.isEmpty(relations) || relations.size() != 1)
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }

        IData relat = relations.getData(0);
        String userIdA = relat.getString("USER_ID_A");
        String roleIdB = relat.getString("ROLE_CODE_B", "");

        // 如果是副卡，则只能删除自己
        if (StringUtils.equals(roleIdB, "2"))
        {
            if (len1 != 1 || !serialNumber.equals(serialNumArray[0]) || !"1".equals(xTagArray[0]))
            {
                // 副卡只能删除自己
                CSAppException.apperr(FamilyException.CRM_FAMILY_709);
            }
        }

        // 如果是主卡，则能删除所有副卡但不能被删除
        if (StringUtils.equals(roleIdB, "1"))
        {
            for (int i = 0; i < len1; i++)
            {
                String serialNumB = serialNumArray[i];
                if (StringUtils.equals(serialNumber, serialNumB))
                {
                    // 主卡不能被删除
                    CSAppException.apperr(FamilyException.CRM_FAMILY_708);
                }
            }
        }

        inData.put("USER_ID_A", userIdA);
        inData.put("SERIAL_NUMBER_B", serialNumArray);
        inData.put("X_TAG", xTagArray);
        inData.put("EFFECT_TAG", effectTagArray);
        
        //@add yanwu
        inData.put("USER_ID", userIdB);
        inData.put("ROLE_CODE_B", roleIdB);	//1:主号，2：副号
        inData.put("MEB_SIZE", len1);		//删除成员个数
        
    }

    /**
     * 亲亲网成员批量删除校验接口 @add yanwu
     * @param input
     * @return IData
     * @throws Exception
     */
    public IData familyNetMemBatCheck(IData input) throws Exception
    {
    	IData backInfo = new DataMap();
    	backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "业务校验成功");
        backInfo.put("X_RECORDNUM", "1");
    	// 业务受理前的校验
        checkInData(input);
        int len = input.getInt("MEB_SIZE");
        String userId = input.getString("USER_ID");
        String userIdA = input.getString("USER_ID_A");
        String roleCodeB = input.getString("ROLE_CODE_B");
        //IDataset userDiscntList = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);

		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);
		
        if ( ArrayUtil.isNotEmpty(userDiscntList) )
        {
        	DiscntTradeData userDiscnt = userDiscntList.get(0);
            String discntCode = userDiscnt.getDiscntCode();
        	if( !"3403".equals(discntCode) && !"3404".equals(discntCode) ){
        		IDataset uuRelas = RelaUUInfoQry.qryRelaByUserIdAThisMonth(userIdA, "45");
                Integer uuSizeDel = uuRelas.size() + len;	//本次删除和本月删除之和不超过9个成员
        		if( uuSizeDel > 9 ){
        			if( "1".equals(roleCodeB) ){
        				backInfo.put("X_RESULTCODE", "140744");
        		        backInfo.put("X_RESULTINFO", "尊敬的客户，亲亲网每月最多可删除1批成员（9个）。您删除亲亲网成员数已超出上限，本次操作失败。请您下月再操作！");
        			}else if( "2".equals(roleCodeB) ){
        				backInfo.put("X_RESULTCODE", "140745");
        		        backInfo.put("X_RESULTINFO", "尊敬的客户，亲亲网本月内最多可退出9个亲亲网成员。您所在亲亲网退出成员数已超出上限，本次操作失败，请您下月再操作！");
        			}
            	}
        	}
        }
        return backInfo;
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }
    
    /**
     * 亲亲网批量删除成员接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData familyNetMemBatDeal(IData input) throws Exception
    {
        // 业务受理前的校验
        checkInData(input);

        String userIdA = input.getString("USER_ID_A");// 虚拟用户ID
        String[] serialNumberBArray = (String[]) input.get("SERIAL_NUMBER_B");// 待操作的成员
        String[] xTagArray = (String[]) input.get("X_TAG");// 0:开通 1:取消 目前只支持删除
        //String[] effectTagArray = (String[]) input.get("EFFECT_TAG");// 生效标识 0:下月生效 1:立即生效

        IDataset memberList = new DatasetList();
        IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);

        for (int i = 0, len = serialNumberBArray.length; i < len; i++)
        {
            String xTag = xTagArray[i];
            String serialNumberB = serialNumberBArray[i];
            //String effectTag = effectTagArray[i];

            boolean isMeb = false;
            for (int j = 0, length = mebList.size(); j < length; j++)
            {
                IData meb = mebList.getData(j);
                String mebSnB = meb.getString("SERIAL_NUMBER_B");
                if (StringUtils.equals(serialNumberB, mebSnB))
                {
                    isMeb = true;
                    break;
                }
            }
            if (!isMeb)
            {
                // 该号码不是您的家庭成员号码,不能被删除
                CSAppException.apperr(FamilyException.CRM_FAMILY_707);
            }

            String effectNow = "YES";// 是否立即生效标识 NO：否 YES：是
            /*if (StringUtils.equals(effectTag, "0"))
            {
                effectNow = "NO";
            }
            else
            {
                effectNow = "YES";
            }*/

            // 目前只支持删除
            if ("1".equals(xTag))
            {
                IData meb = new DataMap();
                meb.put("EFFECT_NOW", effectNow);
                meb.put("SERIAL_NUMBER_B", serialNumberB);
                memberList.add(meb);
            }
        }

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("IS_INTERFACE", "1");// 是接口
        param.put("MEB_LIST", memberList);
        IDataset rtDataset = CSAppCall.call("SS.DelFamilyNetMemberRegSVC.tradeReg", param);

        return rtDataset.getData(0);
    }

}
