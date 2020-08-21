package com.asiainfo.veris.crm.order.soa.group.esop.modifylinedistinct;

import com.ailk.bizcommon.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.ailk.bizcommon.util.Clone;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ModifyLineDistinctBean extends GroupBean {

    private String changeTag = "";

    @Override
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();
    }

    @Override
    public void actTradeSub() throws Exception {

        super.actTradeSub();

        actTradeDiscnt();

    }

    protected void actTradeDiscnt() throws Exception {
        String userId = reqData.getUca().getUserId();
        IDataset userDiscntList = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        IDataset modifyUserDiscnts = new DatasetList();
        IDataset modifyUserAttrs = new DatasetList();
        //SeqMgr.getInstId() EcConstants.ZERO_DISCNT_CODE
        if(IDataUtil.isNotEmpty(userDiscntList)) {
            for (int i = 0; i < userDiscntList.size(); i++) {
                IData userDiscnt = userDiscntList.getData(i);

                IData newUserDiscnt = (IData) Clone.deepClone(userDiscnt);
                //先结束以前的资费
                userDiscnt.put("END_DATE", SysDateMgr.getLastMonthLastDate());//截止到上月底
                userDiscnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                modifyUserDiscnts.add(userDiscnt);
                //再将结束的新增
                String newInstId = SeqMgr.getInstId();
                if(EcConstants.ZERO_DISCNT_CODE.toString().equals(newUserDiscnt.getString("DISCNT_CODE"))) {
                    if("0".equals(changeTag)) {
                        newUserDiscnt.put("END_DATE", SysDateMgr.getLastMonthLastDate());//截止到上月底
                    } else if("1".equals(changeTag)) {
                        newUserDiscnt.put("END_DATE", SysDateMgr.suffixDate(SysDateMgr.getAddMonthsLastDay(0,SysDateMgr.addMonths(newUserDiscnt.getString("END_DATE"), Integer.valueOf(changeTag))), 1));
//                        newUserDiscnt.put("END_DATE", SysDateMgr.suffixDate(SysDateMgr.addMonths(newUserDiscnt.getString("END_DATE"), Integer.valueOf(changeTag)), 1));
                    } else {
                        CSAppException.apperr(GrpException.CRM_GRP_713, "未知延期方式！");
                    }
                }
                String instId = newUserDiscnt.getString("INST_ID");
                IDataset userAttrs = UserAttrInfoQry.getUserAttrByInstID(userId, instId);
                if(IDataUtil.isNotEmpty(userAttrs)) {
                    for (int j = 0; j < userAttrs.size(); j++) {
                        IData userAttr = userAttrs.getData(j);
                        IData newUserAttr = (IData) Clone.deepClone(userAttr);
                        //先结束以前的资费属性
                        userAttr.put("END_DATE", SysDateMgr.getLastMonthLastDate());//截止到上月底
                        userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        modifyUserAttrs.add(userAttr);
                        //再将结束的新增
                        newUserAttr.put("RELA_INST_ID", newInstId);
                        newUserAttr.put("INST_ID", SeqMgr.getInstId());
                        newUserAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        modifyUserAttrs.add(newUserAttr);
                    }
                }
                newUserDiscnt.put("INST_ID", newInstId);
                newUserDiscnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                modifyUserDiscnts.add(newUserDiscnt);
            }
        }
        addTradeDiscnt(modifyUserDiscnts);
        addTradeAttr(modifyUserAttrs);
    }

    @Override
    public IData getProductInfoByElement(IDataset productInfoCaches, IData elementData) throws Exception {
        IDataset userProductInfo = UserProductInfoQry.queryMainProduct(reqData.getUca().getUserId());
        IData userProduct = new DataMap();
        if(IDataUtil.isNotEmpty(userProductInfo)) {
            userProduct = userProductInfo.first();
        }
        return userProduct;
    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
        changeTag = map.getString("CHANGE_TAG");
    }

    @Override
    protected void makUca(IData map) throws Exception {
        makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        return "3190";
    }

}
