package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class CheckSchoolInfoSyn  extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {

        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
        
        String custId=databus.getString("CUST_ID");
        
        if (StringUtils.isBlank(userElementsStr))
            return false;
        IDataset selectElements = new DatasetList(userElementsStr);
        if (IDataUtil.isEmpty(selectElements))
            return false;
        
        int size = selectElements.size();
        
        IData custInfo= UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isEmpty(custInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, GrpException.CRM_GRP_190.toString(), GrpException.CRM_GRP_190.getValue());
            return false;
        }
       
        String groupId=custInfo.getString("GROUP_ID");
        String custName=custInfo.getString("CUST_NAME","");
        
        
        for (int i = 0; i < size; i++)
        {
            IData element = selectElements.getData(i);
            
           
            String spCode="";
            boolean flag =false;
            String sysTimeString = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                String attrparamstr = element.getString("ATTR_PARAM");
                if (!StringUtils.isBlank(attrparamstr))
                {
                    IDataset attrparams = new DatasetList(attrparamstr);
                    
                    IData attrParamData = attrparams.getData(1);

                    IData serparam = attrParamData.getData("PLATSVC");
                    
                    spCode = serparam.getString("pam_SP_CODE");
                    
                    flag=true;
                    
                }
            }
            
            if(flag)
            {
                IData ibossParam= new DataMap();
                ibossParam.put("PKG_SEQ", ""); //交易包流水号
                
                String seqId=sysTimeString+"001";
                ibossParam.put("OPR_SEQ", seqId); //本次操作的流水号
                ibossParam.put("EC_ID", groupId); //EC客户编码
                ibossParam.put("EC_NAME", custName);//EC客户名称
                ibossParam.put("SP_CODE", spCode);//代理商编码
                ibossParam.put("OPR_CODE", "01"); //本业务信息的操作编码: 01－增加,02－终止,03－暂停,04－恢复,05－变更
                ibossParam.put("OPR_SEQ", seqId);
                ibossParam.put("KIND_ID", "BIPXXT01_TX000001_0_0");
               // IDataset callResult = IBossCall.dealInvokeUrl("BIPXXT01_TX000001_0_0", "IBOSS7", ibossParam);
                
//                if (IDataUtil.isEmpty(callResult))
//                {
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, IBossException.CRM_IBOSS_6.toString(), IBossException.CRM_IBOSS_6.getValue());
//                    return true;
//                }
//                
//                String errCode= callResult.getData(0).getString("X_RSPCODE", "");
//                String errCode2= callResult.getData(0).getString("X_RESULTCODE", "");
//                if (!"0000".equals(errCode))
//                {
//                    String errInfo = "校讯通平台校验失败："+callResult.getData(0).getString("X_RSPDESC", "");
//                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode,errInfo);
//                    return true;
//                }
           }
        }
        return false;
    }
   

}

