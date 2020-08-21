
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeEspMemberTrade extends ChangeMemElement
{
	protected static final Logger log = Logger.getLogger(ChangeEspMemberTrade.class);
    /**
     * 查询被删除的是否能找到台账信息
     * @param delParamCode
     * @param paramInfoList
     * @return
     * @throws Exception
     */
    protected static boolean isExistAddAttrInfo(String delParamCode, IDataset paramInfoList) throws Exception
    {
        boolean isExistAddAttr = false;

        IData paramInfo = new DataMap();
        for (int i = 0; i < paramInfoList.size(); i++)
        {
            paramInfo = paramInfoList.getData(i);
            String paramCode = paramInfo.getString("ATTR_CODE");
            String state = paramInfo.getString("STATE");
            if (delParamCode.equals(paramCode) && GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(state))
            {
                isExistAddAttr = true;
                break;
            }
        }

        return isExistAddAttr;
    }

    protected ChangeEspMebReqData reqData = null;

    protected String mebOperCode = "";

    protected String productOfferId = "";

    /**
     *  登记产品参数，一般来讲，如果是参数变更的情况，应该对应生成两条记录，一条删除，两外一条新增
     */
    @Override
    public void actTradePrdParam() throws Exception
    {
        // 获取产品参数
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset productParams = reqData.cd.getProductParamList(baseMemProduct);
        if (IDataUtil.isEmpty(productParams))
        {
            return;
        }

        // 获取instId,转资料表用,转资料时，会根据instId查找到之前的记录做相应操作
        String USER_ID = reqData.getUca().getUserId();
        String PRODUCT_ID = baseMemProduct;
        IData productInfo = UserProductInfoQry.getUserProductBykey(USER_ID, PRODUCT_ID, reqData.getGrpUca().getUserId(), null);
        if (IDataUtil.isEmpty(productInfo))
        {
            return;
        }
        String relaInstId = productInfo.getString("INST_ID");

        // 参数集
        IDataset paramSet = new DatasetList();

        // 循环参数入表（一般对某个参数修改时，前台会产生两条记录，一条删除记录，一条新增记录，目的是给BBOSS侧发报文用）
        for (int i = 0; i < productParams.size(); i++)
        {
            IData productParam = productParams.getData(i);
            String paramCode = productParam.getString("ATTR_CODE");
 
            String paramValue = productParam.getString("ATTR_VALUE");
            String paramName = productParam.getString("ATTR_NAME");
            if (GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_DEL.getValue().equals(productParam.getString("STATE")))
            {// 属性删除
                String modifyTag = TRADE_MODIFY_TAG.DEL.getValue();
                IDataset userAttrInfoList = UserAttrInfoQry.getUserAttr(reqData.getUca().getUserId(), "P", paramCode, null);
                if (IDataUtil.isNotEmpty(userAttrInfoList))
                {
                    IData userAttrInfo = userAttrInfoList.getData(0);
                    String startDate = userAttrInfo.getString("START_DATE");
                    String instId = userAttrInfo.getString("INST_ID");
                    String userAttrValue = userAttrInfo.getString("ATTR_VALUE");
                    String endDate = getAcceptTime();
                    addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, userAttrValue, startDate, endDate, paramSet, "0");
                }

                // /如果该当被删除的参数在在参数列表中找不到对应的状态为新增的记录，则需要新增一条值为""的参数台帐信息
                if (!isExistAddAttrInfo(productParam.getString("ATTR_CODE"), productParams))
                {
                    String startDate = getAcceptTime();
                    String endDate = SysDateMgr.getTheLastTime();
                    String instId = SeqMgr.getInstId();
                    addAttrTradeInfo("F", instId, relaInstId, paramCode, paramName, "", startDate, endDate, paramSet, "0");
                }
            }
            else if (GroupBaseConst.MEB_ATTR_STATUS_DESC.ATTR_ADD.getValue().equals(productParam.getString("STATE")))
            {// 属性新增
                String modifyTag = TRADE_MODIFY_TAG.Add.getValue();
                String startDate = getAcceptTime();
                String endDate = SysDateMgr.getTheLastTime();
                String instId = SeqMgr.getInstId();
                addAttrTradeInfo(modifyTag, instId, relaInstId, paramCode, paramName, paramValue, startDate, endDate, paramSet, "0");
            }

        }
        if (!paramSet.isEmpty())
        {
            this.addTradeAttr(paramSet);
        }
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
        // 1- 继承基类处理
        super.actTradeSub();

        this.dealMebAction(reqData.getEspProductInfo());
//       // 登记other表
//       infoRegDataOther();
//
//       infoRegDataSpecial();

    }

    /**
     * 新增参数数据(modifyTag为F时表示该台账不转资料)
     * @param modifyTag
     * @param instId
     * @param relaInstId
     * @param paramCode
     * @param paramName
     * @param paramValue
     * @param startDate
     * @param endDate
     * @param paramSet
     * @param is_need_pf
     * @throws Exception
     */
    protected void addAttrTradeInfo(String modifyTag, String instId, String relaInstId, String paramCode, String paramName, String paramValue, String startDate, String endDate, IDataset paramSet, String is_need_pf) throws Exception
    {
        IData newParam = new DataMap();
        newParam.put("MODIFY_TAG", modifyTag);
        newParam.put("INST_TYPE", "P");
        newParam.put("RELA_INST_ID", relaInstId);
        newParam.put("INST_ID", instId);
        newParam.put("ATTR_CODE", paramCode);// 属性编号
        newParam.put("RSRV_STR3", paramName);// 属性名称
        newParam.put("ATTR_VALUE", paramValue);// 属性值
        newParam.put("START_DATE", startDate);// 开始时间默认为系统当前时间
        newParam.put("END_DATE", endDate);// 结束时间默认为2050年
        newParam.put("IS_NEED_PF", is_need_pf);
        paramSet.add(newParam);
    }


    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeEspMebReqData();
    }


    /**
     * 处理操作类型
     * 
     * @throws Exception
     */
    public void dealMebAction(IData productInfo) throws Exception
    {
        mebOperCode = productInfo.getString("MEB_OPER_CODE");
        if (mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue())||mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
          {
              // 成员暂停与恢复，更改BB关系表与服务状态表状态
              modifyStateForRelaTabs(mebOperCode);
          }else if(mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY.getValue())){
        	  modifyBBRelaTabs(mebOperCode);
          }
//          else if(mebOperCode.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY_PARAM.getValue())){
//        	  actTradePrdParam();
//          }
    }


   

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeEspMebReqData) getBaseReqData();
    }

    /**
     * 将前台传递过来的BBOSS数据放入RD中
     * @param map
     * @throws Exception
     */
    public void makBBossReqData(IData map) throws Exception
    {
    	 reqData.setEspProductInfo(map.getData("PRODUCT_INFO"));
    	 reqData.setMebType(map.getString("MEB_TYPE"));

    }

    /**
     * 給RD賦值
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makBBossReqData(map);
    }

    /**
     * 成员暂停与恢复，修改相关表状态
     * @param memOpType
     * @throws Exception
     */
    protected void modifyStateForRelaTabs(String memOpType) throws Exception
    {
        // 1- 修改BB表状态
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IDataset mebRelaBBInfoList = RelaBBInfoQry.getBBByUserIdAB(grpUserId, memUserId, "1", relationTypeCode);
        if (IDataUtil.isEmpty(mebRelaBBInfoList))
        {
            return;
        }
        IData mebRelaBBInfo = mebRelaBBInfoList.getData(0);
        if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue()))
        {
            mebRelaBBInfo.put("RSRV_STR5", "N");//暂停成员
        }
        else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
        {
            mebRelaBBInfo.put("RSRV_STR5", "A");//恢复成员
        }
        mebRelaBBInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        this.addTradeRelationBb(mebRelaBBInfo);

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
                productSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                productSvcStateInfo.put("END_DATE", getAcceptTime());
                newProductSvcStateInfoList.add(productSvcStateInfo);
            }
            IData newProductSvcStateInfo = (IData) Clone.deepClone(productSvcInfo);
            newProductSvcStateInfo.put("START_DATE", getAcceptTime());
            newProductSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            newProductSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newProductSvcStateInfo.put("INST_ID", SeqMgr.getInstId());
            if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_PASTE.getValue()))
            {
                newProductSvcStateInfo.put("STATE_CODE", "n");
            }
            else if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_CONTINUE.getValue()))
            {
                newProductSvcStateInfo.put("STATE_CODE", "0");
            }
            newProductSvcStateInfoList.add(newProductSvcStateInfo);
            this.addTradeSvcstate(newProductSvcStateInfoList);
        }
    }
    protected void modifyBBRelaTabs(String memOpType) throws Exception
    {
    	  // 1- 修改BB表状态
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IDataset mebRelaBBInfoList = RelaBBInfoQry.getBBByUserIdAB(grpUserId, memUserId, "1", relationTypeCode);
        if (IDataUtil.isEmpty(mebRelaBBInfoList))
        {
            return;
        }
        IData mebRelaBBInfo = mebRelaBBInfoList.getData(0);
        if (memOpType.equals(GroupBaseConst.BBOSS_MEB_STATUS.MEB_MODIFY.getValue()))
        {
            mebRelaBBInfo.put("RSRV_STR1", memOpType);
        }
        mebRelaBBInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        this.addTradeRelationBb(mebRelaBBInfo);

    }



}
