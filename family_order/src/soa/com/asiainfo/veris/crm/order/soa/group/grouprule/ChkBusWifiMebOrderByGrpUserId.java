
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChkBusWifiMebOrderByGrpUserId extends BreBase implements IBREScript
{

    /**
     * 201412291501规则
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ChkBusWifiMebOrderByGrpUserId.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkBusWifiMebOrderByGrpUserId()  >>>>>>>>>>>>>>>>>>");

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userIdA = databus.getString("USER_ID");//集团用户的USER_ID
        boolean isBusWifiCheck =false;
        if(StringUtils.isNotEmpty(userIdA)){
            String subTransCode = databus.getString("X_SUBTRANS_CODE","");
            int batchCount =0;
            if(StringUtils.isNotEmpty(subTransCode) && StringUtils.equals(subTransCode, "GrpBat")){
                String operateId = databus.getString("BATCH_ID");//批量流水号
                IDataset batchCountValue = BatDealInfoQry.qryBatchCountByOperateId(operateId, null);
                batchCount = batchCountValue.getData(0).getInt("BATCH_COUNT");
            }else{
                batchCount = 1;
            }
            
            IDataset discntInfos = UserDiscntInfoQry.qryUserDiscntByUserId(userIdA, null);
            if (IDataUtil.isNotEmpty(discntInfos))
            {
                for (int i = 0, size = discntInfos.size(); i < size; i++)
                {
                    IData discntInfo = discntInfos.getData(i);
                    String relationTypeCode = discntInfo.getString("RELATION_TYPE_CODE", "");
                    String discntCode = discntInfo.getString("DISCNT_CODE", "");
                    if("T5".equals(relationTypeCode)){
                        if(("73601".equals(discntCode)||"73602".equals(discntCode)||"73603".equals(discntCode)||"73604".equals(discntCode)
                                ||"73605".equals(discntCode))||"73606".equals(discntCode)||"73607".equals(discntCode)||"73608".equals(discntCode)
                                ||"73609".equals(discntCode)||"73610".equals(discntCode)||"73611".equals(discntCode)||"73612".equals(discntCode)
                                ||"73613".equals(discntCode)||"73614".equals(discntCode)||"73615".equals(discntCode)||"73616".equals(discntCode)
                                ||"73617".equals(discntCode)||"73618".equals(discntCode)||"73619".equals(discntCode)||"73620".equals(discntCode)){
                            isBusWifiCheck = true;
                        }
                    }
                }
            }
            if(isBusWifiCheck){
                
                IDataset results = UserAttrInfoQry.getUserAttrByUserId(userIdA,"7361");
                if(IDataUtil.isEmpty(results) && results.size() == 0){
                    err = "获取不到集团产品设置的流量份数。";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
                
                if (IDataUtil.isNotEmpty(results) && results.size() != 1)
                {
                    err = "获取不到集团产品设置的流量份数。";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
                
                IData attrInfos = results.getData(0);
                String maxFlowCounts = attrInfos.getString("ATTR_VALUE");
                
                int maxUsers = 0;
                
                try{
                    maxUsers = Integer.parseInt(maxFlowCounts);
                } catch (Exception e){
                    err = "格式化流量份数失败,请确认集团产品设置的流量份数是否是整数!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return false;
                }
                
                
                IDataset gfspRela = RelaUUInfoQry.getRelaCoutByPK(userIdA, "T5");//T5:流量自由充(全量统付)
                int gfspAllNum = gfspRela.getData(0).getInt("RECORDCOUNT");
                if(StringUtils.isNotEmpty(subTransCode) && StringUtils.equals(subTransCode, "GrpBat")){
                    err = "此集团最大用户数为["+maxUsers+"]，批量加入的数量已超过最大用户数！";
                }else{
                    err = "此集团用户数已达到最大用户数["+maxUsers+"]，不能再新增成员了！";
                }
                if (gfspAllNum + batchCount > maxUsers)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return false;
                }
                
            }
           
            
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ChkBusWifiMebOrderByGrpUserId() <<<<<<<<<<<<<<<<<<<");

        return true;
    }

}
