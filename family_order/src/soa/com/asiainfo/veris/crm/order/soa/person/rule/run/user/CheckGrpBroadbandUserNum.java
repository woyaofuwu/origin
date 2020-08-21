
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 集团商务宽带成员数目判断
 * 
 * @author 
 * @date 2014-05-23
 */
public class CheckGrpBroadbandUserNum extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 集团商务宽带成员数目判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

    	String errorMsg = "";
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
        	String userId = userInfo.getString("USER_ID");
            IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo))
            {
            	if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                {
            		/* add by zhangxing3 for REQ201707060017关于商务宽带产品编码欠费停机不能新增宽带条数的需求开发
            		 * 新增规则：商务宽带产品编码欠费停机状态时，限制产品编码不能新增宽带条数，即拦截开户。
            		 */
            		IDataset userSvcStateInfos = UserSvcStateInfoQry.getUserSvcStateByUserID(userId,"734101","5",Route.CONN_CRM_CG);
            		 if (IDataUtil.isNotEmpty(userSvcStateInfos))
            		 {
             			errorMsg = "集团商务宽带产品状态为欠费停机，不能新增宽带!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201504071617111", errorMsg);
            			return true;
            		 }
            		 
            		 
            		IData inparme = new DataMap();
            	    inparme.put("USER_ID", userId);
            	    inparme.put("RSRV_VALUE_CODE", "N002");
            		IDataset userAttrInfo = CSAppCall.call("CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);
            		if(IDataUtil.isEmpty(userAttrInfo)){
            			errorMsg = "获取集团商务宽带产品设置的宽带数目为空!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201504071617000", errorMsg);
            			return true;
            		}
            		
            		//设置的宽带数目
            		int otherNum = 0;            		
            		 // 判断OTHER表中有没有数据
                    if (IDataUtil.isNotEmpty(userAttrInfo))
                    {
                        for (int i = 0; i < userAttrInfo.size(); i++)
                        {
                            IData userAttrData = (IData) userAttrInfo.get(i);
                            String num = userAttrData.getString("RSRV_STR1","0");
                            if(StringUtils.isNotBlank(num)){
                            	otherNum += Integer.parseInt(num);
                            }
                        }
                    }
                    
            		IData inData = new DataMap();
            		inData.put("USER_ID_A", userId);
        	        inData.put("RELATION_TYPE_CODE", "47");
            		//商务商务宽带的成员数目
        	        int memNum = 0;
            		IDataset infoResult= CSAppCall.call("CS.RelaUUInfoQrySVC.qryCountKDMemForAllCrm", inData);
            		if(IDataUtil.isNotEmpty(infoResult)){
            			String counts = infoResult.getData(0).getString("RECORDCOUNT","0");
            			memNum = Integer.parseInt(counts);
            		}
            		
            		if(memNum >= otherNum){
            			errorMsg = "该集团商务宽带下需要创建的成员宽带数目已经超过设置的宽带数目!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201504071617000", errorMsg);
            			return true;
            		}
                }
            }
        }

        return false;
    }

}
