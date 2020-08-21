
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;
 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
public class BatGrpMdmMemCreateTrans implements ITrans
{    
    private static Logger logger = Logger.getLogger(BatGrpMdmMemCreateTrans.class);
    
    @Override
    public void transRequestData(IData batData) throws Exception
    {
    	System.out.println("guonj_test_BatGrpMdmMemCreateTrans_batData1="+batData);
    	//集团工作手机二次确认短信修改，短信回复后，再次调用接口时，不需要再次进行该参数的转换
    	String isConfirm = batData.getString("IS_CONFIRM","");
        String batchOperType = batData.getString("BATCH_OPER_TYPE","");
    	if("BATADDMDMMEMBER".equals(batchOperType) && "true".equals(isConfirm))
            return ;
        // 校验请求参数
        checkRequestDataSub(batData);
        System.out.println("guonj_test_BatGrpMdmMemCreateTrans_batData2="+batData);
        
        // 构建服务请求数据
        builderSvcData(batData);
        System.out.println("guonj_test_BatGrpMdmMemCreateTrans_batData3="+batData);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
        svcData.put("REMARK", batData.getString("REMARK", condData.getString("PRODUCT_ID") + "批量成员新增"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE"));
        svcData.put("PAGE_SELECTED_TC", condData.getString("PAGE_SELECTED_TC", "false"));		//add by chenzg@20180315 界面上选了"下发二次确认短信"选项
        
        String sms_flag = batData.getString("SMS_FLAG");
        sms_flag = "1";
        svcData.put("IF_SMS", (StringUtils.isBlank(sms_flag) || StringUtils.equals("0", sms_flag)) ? false : true);
        logger.info("guonj_test_BatGrpMdmMemCreateTrans-svcData"+svcData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataUtil.chkParam(condData, "MEM_ROLE_B");
        IDataUtil.chkParam(condData, "PRODUCT_ID");
        IDataUtil.chkParam(condData, "ELEMENT_INFO");
        IDataUtil.chkParam(condData, "USER_ID");
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        //String grpUserId = condData.getString("USER_ID", "-1");     //集团用户ID
        IDataset userInfoData = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        
        if (IDataUtil.isEmpty(userInfoData)){
            //无效用户
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumber);
        } 
            
        IDataset eleInfos = condData.getDataset("ELEMENT_INFO", new DatasetList());
        if(eleInfos==null || eleInfos.size()<=0){
        	return ;
        }
        for(int i=0;i<eleInfos.size();i++) {
            IData eleInfo = eleInfos.getData(i);
            if(IDataUtil.isEmpty(eleInfo)){
                continue ;
            }
            //String elementId = eleInfo.getString("ELEMENT_ID", "");
            String modifyTag = eleInfo.getString("MODIFY_TAG", "");
            String eleTyeCode = eleInfo.getString("ELEMENT_TYPE_CODE", "");
            IDataset attrDs = eleInfo.getDataset("ATTR_PARAM");
            System.out.println("guonj_test_BatGrpMdmMemCreateTrans_checkRequestDataSub_eleTyeCode="+eleTyeCode+"; attrDs="+attrDs);
            if( "D".equals(eleTyeCode) && ("0".equals(modifyTag)||"2".equals(modifyTag)) && IDataUtil.isNotEmpty(attrDs) ){
				/*判断工号是否有套餐折扣权限*/
				String tradeStaffId = CSBizBean.getVisit().getStaffId();
				
				for(int iattr=0;iattr<attrDs.size();iattr++){
                    IData attrData = attrDs.getData(iattr);
                    String attrCode = attrData.getString("ATTR_CODE", "");
                    //打折率属性编码
                    if("214485".equals(attrCode)){
                        int attrVal = attrData.getInt("ATTR_VALUE");
                        if(attrVal>100 || attrVal<0){
        					CSAppException.apperr(CrmCommException.CRM_COMM_103, "您填写折扣不正确，请确认!");
        				}
                        //
        				if(attrVal != 100 && !StaffPrivUtil.isPriv(tradeStaffId, "PRIV_MDM_DISCOUNT", "1")){
        					//修改了折扣且没有权限
        					CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有MDM手机管控套餐折扣权限，不能办理打折，请确认!");
        				}
                    }
                }
                
			}
             
        }
     
    }
}
