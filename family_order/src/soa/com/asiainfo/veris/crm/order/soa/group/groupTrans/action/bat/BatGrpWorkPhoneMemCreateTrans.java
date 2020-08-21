
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupBizNoRuleCheckUtil;

public class BatGrpWorkPhoneMemCreateTrans implements ITrans
{    
    private static Logger logger = Logger.getLogger(BatGrpWorkPhoneMemCreateTrans.class);
    
    @Override
    public void transRequestData(IData batData) throws Exception
    {
    	//集团工作手机二次确认短信修改，短信回复后，再次调用接口时，不需要再次进行该参数的转换
    	String isConfirm = batData.getString("IS_CONFIRM","");
        String batchOperType = batData.getString("BATCH_OPER_TYPE","");
    	if("BATADDWPGRPMEMBER".equals(batchOperType) && "true".equals(isConfirm))
            return ;
        // 校验请求参数
        checkRequestDataSub(batData);
        
        // 构建服务请求数据
        builderSvcData(batData);
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

    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataUtil.chkParam(condData, "MEM_ROLE_B");
        IDataUtil.chkParam(condData, "PRODUCT_ID");
        IDataUtil.chkParam(condData, "ELEMENT_INFO");
        IDataUtil.chkParam(condData, "USER_ID");
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        String grpUserId = condData.getString("USER_ID", "-1");     //集团用户ID
        IDataset userInfoData = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        
        if (IDataUtil.isEmpty(userInfoData)){
            //无效用户
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumber);
        }else{
            String userId = userInfoData.getData(0).getString("USER_ID","");
            
            IDataset eleInfos = condData.getDataset("ELEMENT_INFO", new DatasetList());
            if(eleInfos!=null && eleInfos.size()>0){
                String tipsEleIds = "";
                String tipsDiscntCodes = "";
                String tipsPrivs = "";
                for(int i=0;i<eleInfos.size();i++){
                    IData eleInfo = eleInfos.getData(i);
                    String elementId = eleInfo.getString("ELEMENT_ID","");
                    //--------------判断用户办理是否存在打折套餐所依赖的套餐-----------------
                    //选择了整体打折的套餐不做校验
                    IDataset commFilter =  CommparaInfoQry.getCommparaInfoBy5("CSM","6013","GPWP_FILTER",elementId,"0898",null);
                    if(IDataUtil.isNotEmpty(commFilter)){
                        continue;
                    }
                    IDataset ds =  CommparaInfoQry.getCommparaInfoBy5("CSM","6013","GPWP",elementId,"0898",null);
                    if(!IDataUtil.isEmpty(ds)){
                        String discntCode = ds.getData(0).getString("PARA_CODE2", "");      //打折套餐依赖的用户优惠
                        int sum = UserDiscntInfoQry.getDiscntByMUIdToCommpara(userId, elementId, grpUserId);
                        if(sum == 0){
                            tipsEleIds += tipsEleIds.length()>0 ? ","+elementId : elementId;
                            tipsDiscntCodes += tipsDiscntCodes.length()>0 ? ","+discntCode : discntCode;
                        }
                    }
                    
                    /*add by chenzg@20161024判断是否有套餐(10010201 工作手机省内语音包,10010202 工作手机省内流量包)的折扣权限*/
                    String checkInfo = GroupBizNoRuleCheckUtil.checkGPWPDiscntAttrPriv(eleInfo);
                    if(StringUtils.isNotBlank(checkInfo)){
                        tipsPrivs += tipsPrivs.length()>0 ? "," + checkInfo : checkInfo;
                    }
                    
                }
                
                if(tipsEleIds.length()>0){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "【" + serialNumber +"】号码订购打折套餐【" + tipsEleIds + "】无对应套餐【" + tipsDiscntCodes + "】");
                }
                if(tipsPrivs.length()>0){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, tipsPrivs);
                }
            }
            
        }

    }

}
