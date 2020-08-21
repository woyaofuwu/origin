package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * @program: hain_order
 * @description:
 * @author: zhangchengzhi
 * @create: 2018-10-10 17:04
 **/

public class ChangeRecepHallContinueMemberTrade extends ChangeRecepHallBaseMemberTrade{
	
	// XXX 处理基类取不到业务类型
    @Override
    public String setTradeTypeCode() throws Exception
    {
        // 1- 继承基类处理
        //super.setTradeTypeCode();

       return "2355";
    }

    protected void setTradeEcrecepMeb(IData map) throws Exception
    {
        map.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.MODI.getValue());
        map.put("STATUS", "A");
        // 成员暂停与恢复，更改UU关系表与服务状态表状态
        modifyStateForRelaTabs(mebOperCode);
    }
    /*
     * @description 成员暂停与恢复，修改相关表状态
     * @author xunyl
     * @date 2013-09-10
     */
    protected void modifyStateForRelaTabs(String memOpType) throws Exception
    {
        // 1- 修改BB表状态
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IData mebRelaUUInfo = RelaUUInfoQry.getRelationUUByPk(grpUserId, memUserId, relationTypeCode, null);
        mebRelaUUInfo.put("RSRV_STR5", "A");
        this.addTradeRelation(mebRelaUUInfo);

        // 2- 修改服务状态表(删除一条，新增一条)
        IDataset newProductSvcStateInfoList = new DatasetList();
        IDataset productSvcInfoList = UserSvcInfoQry.getUserProductSvc(memUserId, grpUserId, null);
        if (null == productSvcInfoList || productSvcInfoList.size() < 0)
        {
            return;
        }
        for (int i = 0; i < productSvcInfoList.size(); i++)
        {
            IData productSvcInfo = productSvcInfoList.getData(i);
            String svcId = productSvcInfo.getString("SERVICE_ID");
            IDataset productSvcStateInfoList = UserSvcStateInfoQry.getUserLastStateByUserSvc(memUserId, svcId);
            if (!productSvcStateInfoList.isEmpty())
            {
                IData productSvcStateInfo = productSvcStateInfoList.getData(0);
                productSvcStateInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
                productSvcStateInfo.put("END_DATE", getAcceptTime());
                newProductSvcStateInfoList.add(productSvcStateInfo);
            }
            IData newProductSvcStateInfo = (IData) Clone.deepClone(productSvcInfo);
            newProductSvcStateInfo.put("START_DATE", getAcceptTime());
            newProductSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            newProductSvcStateInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
            newProductSvcStateInfo.put("INST_ID", SeqMgr.getInstId());
            newProductSvcStateInfo.put("STATE_CODE", "0");
            newProductSvcStateInfoList.add(newProductSvcStateInfo);
            this.addTradeSvcstate(newProductSvcStateInfoList);
        }
    }

    protected String getServOpType(String memOpType) throws Exception
    {
        return "05";
    }
    protected void setTradeEcrecepProcedure(IData map) throws Exception
    {
        map.put("SVC_STATUS", "A");
    }
}

