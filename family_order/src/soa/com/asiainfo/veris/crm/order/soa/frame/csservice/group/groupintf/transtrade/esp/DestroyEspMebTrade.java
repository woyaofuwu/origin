
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;


public class DestroyEspMebTrade extends DestroyGroupMember
{
	 protected DestroyEspMebReqData reqData = null;

    /**
     * @description 注销BB关系(该类重写基类方法，基类注销的表为UU表，而BBOSS侧注销的表为BB表)
     * @throws Exception
     */
    protected void actTradeRelationUU() throws Exception
    {
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset relaList = RelaBBInfoQry.qryBB(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null);

        if (IDataUtil.isEmpty(relaList))
        {
            return;
        }

        IData relaData = relaList.getData(0);
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        relaData.put("END_DATE", getAcceptTime());

        super.addTradeRelationBb(relaData);
    } 
    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        if(!"1".equals(reqData.getInterType())){//正向退订，省内退订
            this.SysOrderInfoForEsp();	
            return;
         }
        // 1- 继承基类处理
        super.actTradeSub();
        
        actTradeRelationUU();
        
       
    }
    /**
     * 同步给ESP平台信息
     * @throws Exception
     */
    public void SysOrderInfoForEsp() throws Exception
    {
    	IData synInfo=new DataMap();
    	synInfo.put("SYS_ID", SeqMgr.getEspSynId());
    	synInfo.put("SYN_TAG", "Z");//代表省BOSS正向同步给ESP
    	synInfo.put("FLAG", "0");
    	synInfo.put("SYNC_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());    	
    	String grpProductId=reqData.getGrpUca().getProductId();
    	String productNumber= GrpCommonBean.productToMerch(grpProductId, 0);
    	String grpUserId=reqData.getGrpUca().getUserId();
    	String mebNumber=reqData.getUca().getSerialNumber();
    	IData input=new DataMap();
    	input.put("RSRV_VALUE_CODE", "GROUP_ID");
    	input.put("USER_ID", grpUserId);
    	input.put("RSRV_STR6", grpProductId);
    	IDataset otherInfos=EspMebCommonBean.getUserOtherInfoForEsp(input);
    	if(IDataUtil.isEmpty(otherInfos)){
    		return;		
    	}
    	String productOrderId=otherInfos.getData(0).getString("RSRV_STR4","");
    	String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(grpProductId);
    	IDataset relaList = RelaBBInfoQry.qryBB(grpUserId, reqData.getUca().getUserId(), relationTypeCode, null);
    	if(IDataUtil.isEmpty(relaList)){
    		return;
    	}
    	synInfo.put("PRODUCT_ORDER_ID", productOrderId);
    	synInfo.put("PRODUCT_NUMBER", productNumber);
    	synInfo.put("MEMBER_NUMBER", mebNumber);
    	synInfo.put("ACTION", "0");
    	synInfo.put("MEMBER_TYPE_ID", relaList.getData(0).getString("RSRV_STR1",""));
    	synInfo.put("EFF_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	Dao.insert("TD_B_ESP_MEMB_SYNINFO", synInfo, Route.CONN_CRM_CEN);
   
    	
    }

    /**
    * 初始化
     */
     @Override
     protected void initReqData() throws Exception
     {
         super.initReqData();
         reqData = (DestroyEspMebReqData) getBaseReqData();
     }
     @Override
     protected BaseReqData getReqData() throws Exception
     {
         return new DestroyEspMebReqData();
     }
    /**
     * 将前台传过来的数据保存在reqData里面
     * @param map
     * @throws Exception
     */
     public void makEspReqData(IData map) throws Exception
     {
         
         reqData.setInterType(map.getString("INTER_TYPE"));
     }

    /**
     * 给rd赋值
     */
     @Override
     protected void makReqData(IData map) throws Exception
     {
         super.makReqData(map);
         makEspReqData(map);
     }
}
