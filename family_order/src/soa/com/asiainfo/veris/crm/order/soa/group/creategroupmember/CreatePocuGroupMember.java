
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreatePocuGroupMember extends CreateGroupMember
{
    
    private static final Logger logger = Logger.getLogger(CreatePocuGroupMember.class);
    
    private IData paramData = new DataMap();
    private String strApplyTypeA = "";
    private String strApplyTypeB = "";
    
    /**
     * @author yanwu
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }
    
    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

    }

    /**
     * 生成登记信息
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        // start add by wangyf6 at 2013-8-15
        //checkRegBeforeData();
        // end add by wangyf6 at 2013-8-15
        
        //@author yanwu begin
        paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            strApplyTypeA = paramData.getString("APPLY_TYPE_A");
            strApplyTypeB = paramData.getString("APPLY_TYPE_B");
        }
        //@author yanwu end 
    }
    
    /**
     * @author yanwu
     * @date 2015-07-30
     * @其它台帐处理
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
    }
    
    /**
     * 不选中优惠也可以提交，但是需要有如下套餐param_attr = '9091' for REQ201308090009
     * 
     * @throws Exception
     * @author wangyf6
     */
    private void checkRegBeforeData() throws Exception
    {
        IDataset disDatasest = reqData.cd.getDiscnt();
        boolean addFlag = false;
        if (disDatasest != null && disDatasest.size() > 0)
        {
            for (int j = 0; j < disDatasest.size(); j++)
            {
                IData eleData = disDatasest.getData(j);
                if (eleData != null && eleData.size() > 0)
                {
                    //String discntCode = eleData.getString("ELEMENT_ID", "");
                    String typeCode = eleData.getString("ELEMENT_TYPE_CODE", "");
                    String state = eleData.getString("MODIFY_TAG");
                    if ("D".equals(typeCode) && TRADE_MODIFY_TAG.Add.getValue().equals(state))
                    {
                        addFlag = true;
                        break;
                    }
                }
            }
        }

        // //true选择优惠时不进行拦截判断,false为不选择优惠时则进行拦截判断用户是否有相应的优惠
        if (!addFlag)
        {
            String userId = reqData.getUca().getUserId();// 用户标识

            String paramAttr = "9091";
            String paramCode = "1";
            String eparchyCode = "0898";
            IDataset resultData = UserDiscntInfoQry.getM2MUserDisntCommparaByUserId(userId, paramAttr, paramCode, eparchyCode);
            if (IDataUtil.isEmpty(resultData))
            {
                // common.error("589010", "您未订购相应的数据流量套餐，不可办理M2M产品成员新增!");
                CSAppException.apperr(GrpException.CRM_GRP_679);
            }
        }
    }
    
    /**
     *@author yanwu
     *@date 2015-08-01 
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        if( logger.isDebugEnabled() ){
            logger.debug("-----------------------yanwu---------------------2222" + map.toString());
        }
        String strBatid = map.getString("BATCH_ID");
        if( !"".equals(strBatid) ){
            IData pam = map.getData("ALL_PARAM");
            if( IDataUtil.isNotEmpty(pam) ){
                strApplyTypeA = pam.getString("APPLY_TYPE_A");
                strApplyTypeB = pam.getString("APPLY_TYPE_B");
            }
            if( logger.isDebugEnabled() ){
                logger.debug("-----------------------yanwu--------------------------3333" + strApplyTypeA + strApplyTypeB);
            }
        } 
    }
        
    /**
     *@author yanwu
     *@date 2015-09-15 
     */
    @Override
    public void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);
        
        map.put("RSRV_STR4", strApplyTypeA); 
        map.put("RSRV_STR5", strApplyTypeB); 
    }
        
}
