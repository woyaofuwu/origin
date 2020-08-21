
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class FamilyShortCodeBusiBean extends CSBizBean
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

        String xTag = inData.getString("X_TAG");// 操作类型:0-新增；1-删除
        String serialNumber = inData.getString("SERIAL_NUMBER");// 业务办理号码，可为主号码，也可为副号码
        String serialNumberBs = inData.getString("SERIAL_NUMBER_B");// 副号
        String shortCodes = inData.getString("SHORT_CODE");// 短号

        if (StringUtils.isBlank(serialNumber))
        {
            // 办理业务号码SERIAL_NUMBER为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }

        if (StringUtils.isBlank(serialNumberBs))
        {
            // SERIAL_NUMBER_B为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_72);
        }

        if (StringUtils.isBlank(xTag))
        {
            // X_TAG为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_71);
        }

        if (StringUtils.isBlank(shortCodes))
        {
            // SHORT_CODE不能为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_720);
        }
        IDataset dataset = new DatasetList();
		dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
		if(IDataUtil.isEmpty(dataset)){
	        Pattern p = Pattern.compile("52\\d|53\\d");
	        Matcher m = p.matcher(shortCodes);
	        boolean b = m.matches();
	        if (!b)
	        {
	            // 短号非法,短号必须为【520-539】
	            CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCodes);
	        }	
		}else{
	        Pattern p = Pattern.compile("52\\d(,52\\d)*");
	        Matcher m = p.matcher(shortCodes);
	        boolean b = m.matches();
	        if (!b)
	        {
	            // 短号非法,短号必须为【520-529】
	            CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCodes);
	        }	
		}

        
    }

    /**
     * 亲亲网短号批量修改接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData familyShortCodeBatDeal(IData input) throws Exception
    {
        // 业务受理前的校验
        checkInData(input);

        String xTag = input.getString("X_TAG");// 操作类型:0-新增；1-删除
        String serialNumber = input.getString("SERIAL_NUMBER");// 业务办理号码，可为主号码，也可为副号码
        String serialNumberBs = input.getString("SERIAL_NUMBER_B");// 副号
        String shortCodes = input.getString("SHORT_CODE");// 短号

        FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
        IDataset uuList = bean.getAllMebByMainSn(input);

        String[] serialNumBArray = serialNumberBs.split(",");
        String[] xTagArray = xTag.split(",");
        String[] shortCodesArray = shortCodes.split(",");

        int len1 = serialNumBArray.length;
        int len2 = xTagArray.length;
        int len3 = shortCodesArray.length;
        if (len1 != len2 || len1 != len3 || len2 != len3)
        {
            // 传入参数SERIAL_NUMBER_B的长度与X_TAG或SHORT_CODE的长度不致
            CSAppException.apperr(FamilyException.CRM_FAMILY_721);
        }

        IDataset memberList = new DatasetList();

        for (int i = 0; i < len1; i++)
        {
            String tag = xTagArray[i];
            String serialNumberB = serialNumBArray[i];
            String shortCode = shortCodesArray[i];
            if (!"2".equals(tag))
            { // !"0".equals(tag) && !"1".equals(tag) 对比老系统后考虑目前只支持修改，新增、删除有问题
                // X_TAG值不能被识别
                CSAppException.apperr(FamilyException.CRM_FAMILY_703);
            }

            IData member = new DataMap();

            if ("0".equals(tag))
            {
                member.put("X_TAG", tag);
                member.put("SERIAL_NUMBER_B", serialNumberB);
                member.put("SHORT_CODE_B", shortCode);
            }
            else
            {
            	String strShorCodes = "";
            	boolean IsSame = false;
                for (int j = 0, size = uuList.size(); j < size; j++)
                {
                    IData uu = uuList.getData(j);
                    String tempSnB = uu.getString("SERIAL_NUMBER_B");
                    String tempShortCode = uu.getString("SHORT_CODE", "");

                    if( StringUtils.isBlank(strShorCodes) ){
                    	strShorCodes = tempShortCode;
                    }else{
                    	strShorCodes += "、" + tempShortCode;
                    }
                    
                    if( tempShortCode.equals(shortCode) ){
                    	IsSame = true;
                    	//break;
                    }
                    
                    if (StringUtils.equals(tempSnB, serialNumberB))
                    {
                        member.put("X_TAG", tag);
                        member.put("SERIAL_NUMBER_B", serialNumberB);
                        member.put("SHORT_CODE_B", shortCode);// 新的短号
                        // member.put("OLD_SHORT_CODE", tempShortCode);//原来的短号
                        //break;
                    }
                }

                if( IsSame ){
                	//尊敬的客户，您重新设置的短号与现有短号重复，请重新设置。短号使用范围为：520至529，
                	//当前已被使用短号：***、***、***，请您选择未被使用短号设置。
                	/*String info = "尊敬的客户，您重新设置的短号与现有短号重复，请重新设置。短号使用范围为：520至529，";
                		   info = info + "当前已被使用短号：" + strShorCodes + "，请您选择未被使用短号设置。";
                    IData data = new DataMap();
            		data.put("X_RESULT_CODE", "-1");
            		data.put("X_RESULTINFO", info);*/
            		// 尊敬的客户，您重新设置的短号与现有短号重复，请重新设置。短号使用范围为：520至529，当前已被使用短号：%s，请您选择未被使用短号设置。
                    CSAppException.apperr(FamilyException.CRM_FAMILY_746, strShorCodes);
            		
                }
                
                /*
                 * if ("1".equals(xTag) && IDataUtil.isEmpty(member)) { //成员号码不存在短号，不能删除
                 * CSAppException.apperr(FamilyException.CRM_FAMILY_732); }
                 */

                if ("2".equals(xTag) && IDataUtil.isEmpty(member))
                {
                    // 成员号码不存在短号，不能修改
                    CSAppException.apperr(FamilyException.CRM_FAMILY_733);
                }
            }

            memberList.add(member);
        }

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        // param.put("IS_INTERFACE", "1");//是接口
        param.put("MEB_LIST", memberList);
        IDataset rtDataset = CSAppCall.call("SS.FamilyShortCodeBusiRegSVC.tradeReg", param);

        return rtDataset.getData(0);
    }

}
