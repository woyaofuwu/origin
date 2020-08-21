
package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class CommonVerifySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据证件类型、客户姓名 校验证件实名制个数是否达到最大值
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset CheckRealNameLimit(IData inParam) throws Exception
    {
        IData retData = new DataMap();
        String psptId = inParam.getString("PSPT_ID");
        String custName = inParam.getString("CUST_NAME");

        int rCount = 0;
        int rLimit = 5;
        String psptTyeCode = inParam.getString("PSPT_TYPE_CODE");
    	String brandCode = inParam.getString("BRAND_CODE");
    	IDataset otherInfo = UserOtherInfoQry.getOtherInfoByCodeUserId(inParam.getString("USER_ID"),"HYYYKBATCHOPEN");//行业应用卡判断
    	String userType = "";
    	if(("D".equals(psptTyeCode)||"E".equals(psptTyeCode)||"G".equals(psptTyeCode)
	    	||"L".equals(psptTyeCode)||"M".equals(psptTyeCode)) 
	    	 &&("G001".equals(brandCode)||"G002".equals(brandCode)||"G010".equals(brandCode)
	    			||"PWLW".equals(brandCode) ||"IMSG".equals(brandCode) || IDataUtil.isNotEmpty(otherInfo))){
    		
    		if("PWLW".equals(brandCode) || "IMSG".equals(brandCode) || IDataUtil.isNotEmpty(otherInfo)){
    			userType = "1";//物联网卡（含IMS、行业应用卡）
    		}else{
    			userType = "0";//移动电话
    		}
    		
    		rCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, userType);
            rLimit = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, userType);
    	}else {
    		rCount = UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);
    		rLimit = UserInfoQry.getRealNameUserLimitByPspt(custName, psptId);
    	}
        //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
        rCount += UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, null);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
        //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
        retData.put("EXISTS_COUNT", rCount);// 已经存在的个数
        retData.put("LIMIT_COUNT", rLimit);// 限制的个数
        retData.put("RESULT", rLimit - rCount);// 比较接口

        IDataset retSet = new DatasetList();
        retSet.add(retData);
        return retSet;//
    }
	/**
	 * 使用人证件号码数限制校验
	 * @param input
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 */
    public IDataset checkRealNameLimitByUsePspt(IData input) throws Exception
    {
        IDataset ajaxDataset = new DatasetList();
        String custName = input.getString("CUST_NAME").trim();
        String psptId = input.getString("PSPT_ID").trim();
        String serialNumber = input.getString("SERIAL_NUMBER","");
        
        if (!"".equals(custName) && !"".equals(psptId))
        {
        	IData ajaxData = new DataMap();
            //使用人默认使用次数(读取配置个数)
            int rLimit = UserInfoQry.getGroupUsePersonDefaultCount(custName, psptId);//限制个数
            
            //证件号码使用次数
            //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
            //int psptCount=UserInfoQry.getRealNameUserCountByUsePspt3(psptId);
            int psptCount=UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, null);
            int usePsptCount = psptCount;
            psptCount += UserInfoQry.getRealNameUserCountByPspt2(custName, psptId);	// 判断一证五号个数以登记该证件为户主和使用人合并计算           
            //add by zhangxing3 for REQ201906130010关于本省一证五号优化需求
            if(psptCount < rLimit){
            	//小于规定数量
            	if(usePsptCount == 0){
            		//没有被使用过(验证通过)
                    ajaxData.put("MSG", "OK");
                    ajaxData.put("CODE", "0");
            	}else{
            		/**
            		 * 当使用人证件号码满足过户条件时，同时判断使用人姓名也要与原来的一致，如果原登记的证件有多个名称，
            	     * 只需要和其中一个一致就行
            		 */
            		//getRealNameUserCountByUsePspt() 这个方法原来就存在的
            		int useCount= UserInfoQry.getRealNameUserCountByUsePspt(custName, psptId, serialNumber);// 获取使用人证件号码已实名制开户的数量
            		if(useCount > 0){
            			//验证通过
                        ajaxData.put("MSG", "OK");
                        ajaxData.put("CODE", "0");
            		}else{
            			//验证未通过
                        ajaxData.put("MSG", "使用人证件号码【" + psptId + "】与原来使用的不一致，请更换其它证件！");
                        ajaxData.put("CODE", "1");
            		}
            	}
            }else{
            	//大于规定数量
            	//证件号码使用已超过规定次数(5次)
            	//验证未通过
                ajaxData.put("MSG", "使用人证件号码【" + psptId + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                ajaxData.put("CODE", "1");
            }
            ajaxDataset.add(ajaxData);
        }
        return ajaxDataset;
    }
    
}
