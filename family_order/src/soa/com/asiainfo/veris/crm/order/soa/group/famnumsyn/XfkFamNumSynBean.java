
package com.asiainfo.veris.crm.order.soa.group.famnumsyn;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class XfkFamNumSynBean extends MemberBean
{
    protected XfkFamNumSynReqData reqData = null;

    protected String modifyTag = "0";

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.regSetAttr();

        // 成员号码统付
        //this.actTradefanNumPayRelation();
        //this.regTradeRelation();
    }



    protected BaseReqData getReqData() throws Exception
    {
        return new XfkFamNumSynReqData();
    }



    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (XfkFamNumSynReqData) getBaseReqData();
    }

    protected void makInit(IData map) throws Exception
    {
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setOperCode(map.getString("OPEN_CODE"));
        reqData.setFumNub(map.getString("FAMNUM"));
    }

    protected void makUca(IData map) throws Exception
    {
    	makUcaForMebNormal(map);

    }


    /**
     * 学护卡特殊处理亲情号码
     *
     * @throws Exception
     */
    public void regSetAttr() throws Exception
    {
    	String fanNum = reqData.getFumNub();
    	String oprCode = reqData.getOperCode();
    	String mainSn = reqData.getUca().getSerialNumber();
    	String mebUserId = reqData.getUca().getUserId();
    	String instType = "D";
        IDataset userAttrDataset = UserAttrInfoQry.getUserAttrbyUserIdInsttype(mebUserId,instType);
        if (IDataUtil.isEmpty(userAttrDataset))
        {
        	 CSAppException.apperr(GrpException.CRM_GRP_839,mainSn);
		}
        IDataset dataset = new DatasetList();
        IData map = null;
        if ("06".equals(oprCode))
        {
        	IDataset dbUserAttrDataset = DataHelper.filter(userAttrDataset, "ATTR_VALUE="+fanNum);
        	if (dbUserAttrDataset.size()>0)
        	{
        		 CSAppException.apperr(GrpException.CRM_GRP_841,fanNum,mainSn);
			}
            for (int j = 0, jSize = userAttrDataset.size(); j < jSize; j++)
            {
            	map = userAttrDataset.getData(j);
            	if ("-1".equals(map.getString("ATTR_VALUE")))
            	{
                    IData addMapData = new DataMap();
                    addMapData.putAll(map);
                    addMapData.put("INST_ID", SeqMgr.getInstId());
                    addMapData.put("ATTR_VALUE", fanNum);
                    addMapData.put("START_DATE", getAcceptTime());
                    addMapData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    dataset.add(addMapData);

                    map.put("END_DATE", getAcceptTime());
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    dataset.add(map);
                    break;
				}

            }
		}
        if ("07".equals(oprCode))
        {
            for (int j = 0, jSize = userAttrDataset.size(); j < jSize; j++)
            {
            	map = userAttrDataset.getData(j);
            	if (fanNum.equals(map.getString("ATTR_VALUE")))
            	{
                    map.put("ATTR_VALUE", "-1");
                    map.put("START_DATE", getAcceptTime());
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(map);
                    break;
            	}
            }

        }
       super.addTradeAttr(dataset);

    }

    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
        map.put("INST_TYPE", map.getString("INST_TYPE", ""));
        map.put("RELA_INST_ID", map.getString("RELA_INST_ID", ""));
        map.put("INST_ID", map.getString("INST_ID", ""));
        map.put("ATTR_CODE", map.getString("ATTR_CODE", ""));
        map.put("ATTR_VALUE", map.getString("ATTR_VALUE", ""));
        map.put("START_DATE", map.getString("START_DATE", getAcceptTime()));// 起始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 终止时间
        // 状态属性：0-增加，1-删除，2-变更
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", ""));
        map.put("REMARK", map.getString("REMARK", "")); // 备注
        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "")); // 预留数值1
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "")); // 预留数值2
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "")); // 预留数值3
        map.put("RSRV_NUM4", map.getString("RSRV_NUM4", "")); // 预留数值4
        map.put("RSRV_NUM5", map.getString("RSRV_NUM5", "")); // 预留数值5
        map.put("RSRV_STR1", map.getString("RSRV_STR1", "")); // 预留字段1
        map.put("RSRV_STR2", map.getString("RSRV_STR2", "")); // 预留字段2
        map.put("RSRV_STR3", map.getString("RSRV_STR3", "")); // 预留字段3
        map.put("RSRV_STR4", map.getString("RSRV_STR4", "")); // 预留字段4
        map.put("RSRV_STR5", map.getString("RSRV_STR5", "")); // 预留字段5
        map.put("RSRV_DATE1", map.getString("RSRV_DATE1", "")); // 预留日期1
        map.put("RSRV_DATE2", map.getString("RSRV_DATE2", "")); // 预留日期2
        map.put("RSRV_DATE3", map.getString("RSRV_DATE3", "")); // 预留日期3
        map.put("RSRV_TAG1", map.getString("RSRV_TAG1", "")); // 预留标志1
        map.put("RSRV_TAG2", map.getString("RSRV_TAG2", "")); // 预留标志2
        map.put("RSRV_TAG3", map.getString("RSRV_TAG3", "")); // 预留标志3
    }

    /**
     * 网外号码付费关系
     *
     * @throws Exception
     */
    protected void actTradefanNumPayRelation() throws Exception
    {
        String fanNum = reqData.getFumNub();
        String oprCode = reqData.getOperCode();
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();

        if ("06".equals(oprCode))
        {
            IData data = new DataMap();
            data.put("ACCT_ID", reqData.getGrpUca().getAcctId());
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("PAYITEM_CODE", "-1"); // 付费帐目编码
            data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
            data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
            data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
            data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
            data.put("DEFAULT_TAG", "1"); // 默认标志
            data.put("LIMIT_TYPE", "1"); // 限定方式：0-不限定，1-金额，2-比例
            data.put("LIMIT", "0"); // 限定值
            data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
            data.put("INST_ID", SeqMgr.getInstId());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
            data.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
            data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());

            super.addTradePayrelation(data);
        }
//        else if ("07".equals(oprCode)) {
//            IDataset payRelaList = PayRelaInfoQry.getMemberPayRelaxc(reqData.getUca().getUserId(), reqData.getGrpUca().getAcctId(), "-1");
//            if(IDataUtil.isNotEmpty(payRelaList))
//            {
//                PayRelationTradeData payRelationTD = new PayRelationTradeData(payRelaList.getData(0));
//                payRelationTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
//                payRelationTD.setRemark("删除学护卡付费关系");
//                payRelationTD.setEndCycleId(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
//
//                super.addTradePayrelation(payRelationTD);
//            }
//        }
    }

    public void regTradeRelation() throws Exception
    {
        String fanNum = reqData.getFumNub();
        String oprCode = reqData.getOperCode();
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();

        IDataset dataset = new DatasetList();
        IData map = null;
        if ("06".equals(oprCode))
        {
            map = new DataMap();
            RelationTradeData uuTD = new RelationTradeData();
            IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", fanNum);
            map.put("USER_ID_A", userInfos.getData(0).getData("USER_ID")); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
            map.put("SERIAL_NUMBER_A", fanNum); // A服务号码
            map.put("USER_ID_B", mebUserId); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
            map.put("SERIAL_NUMBER_B", mainSn); // B服务号码
            map.put("RELATION_TYPE_CODE", "56");
            map.put("ROLE_TYPE_CODE", "0");
            map.put("ROLE_CODE_A", "0");
            map.put("ROLE_CODE_B", "2");
            map.put("ORDERNO", "0");
            //map.put("START_DATE", reqData.getAcceptTime());
            map.put("START_DATE", getAcceptTime());
            map.put("END_DATE", SysDateMgr.getTheLastTime());
            map.put("INST_ID", SeqMgr.getInstId());
            map.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);

            map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
            map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
            map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值

            dataset.add(map);

        }
       super.addTradeRelation(dataset);

    }

    /**
     *
     */
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", map.getString("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber())); // A服务号码
        map.put("USER_ID_B", map.getString("USER_ID_B", reqData.getUca().getUserId())); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", map.getString("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber())); // B服务号码

        map.put("RELATION_TYPE_CODE", map.getString("MODIFY_TAG", "56"));
        map.put("ROLE_TYPE_CODE", map.getString("MODIFY_TAG", "0"));
        map.put("ROLE_CODE_A", map.getString("MODIFY_TAG", "0"));
        map.put("ROLE_CODE_B", map.getString("MODIFY_TAG", "2"));
        map.put("ORDERNO", map.getString("MODIFY_TAG", "0"));
        /*map.put("START_DATE", map.getString("MODIFY_TAG", reqData.getAcceptTime()));
        map.put("END_DATE", map.getString("MODIFY_TAG", SysDateMgr.getTheLastTime()));*/
        map.put("START_DATE", getAcceptTime());
        map.put("END_DATE", SysDateMgr.getTheLastTime());
        map.put("INST_ID", map.getString("MODIFY_TAG", SeqMgr.getInstId()));

        map.put("MODIFY_TAG", map.getString("MODIFY_TAG"));

        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "3648";
    }

}
