package com.asiainfo.veris.crm.order.soa.group.returnratio;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ReturnRationBean extends GroupBean {
    protected ReturnRationReqData reqData = null;

    @Override
    public void actTradeSub() throws Exception {
        super.actTradeSub();

        String operValue = reqData.getOperation();
        // 操作标识
        if (operValue != null && "0".equals(operValue)) {// 新增
            this.infoRegDataOtherAdd();

        } else if (operValue != null && "2".equals(operValue)) { // 修改
            this.infoRegDataOtherModify();

        } else if (operValue != null && "1".equals(operValue)) { // 删除
            this.infoRegDataOtherDel();
        }

    }

    /**
     * 新增操作日志
     * 
     * @param params
     * @param tradeInfo
     * @throws Exception
     */
    private void createTradeUserOtherLog(IData params, IData tradeInfo) throws Exception {
        IData npriInfo = new DataMap();

        String user_id = reqData.getUca().getUserId();
        IData resultInfo = UserInfoQry.getGrpUserInfoByUserId(user_id, "0", "0898");
        if (IDataUtil.isEmpty(resultInfo)) {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String serialNumber = resultInfo.getString("SERIAL_NUMBER");

        String operValue = "";
        if (IDataUtil.isNotEmpty(tradeInfo)) {
            operValue = tradeInfo.getString("MODIFY_FLAG", "");
        }

        npriInfo.putAll(tradeInfo);

        String groupId = reqData.getGroupId();
        npriInfo.put("GROUP_ID", groupId);
        npriInfo.put("SERIAL_NUMBER", serialNumber);
        npriInfo.put("AREA_CODE", CSBizBean.getVisit().getCityCode());
        npriInfo.put("AREA_NAME", CSBizBean.getVisit().getCityName());
        npriInfo.put("OPER_ID", SeqMgr.getOperIdCode());

        // 添加日志的标识
        if (operValue != null && "0".equals(operValue)) {// 新增
            npriInfo.put("RSRV_TAG1", "0");
        } else if (operValue != null && "2".equals(operValue)) { // 修改
            npriInfo.put("RSRV_TAG1", "2");
        } else if (operValue != null && "1".equals(operValue)) { // 删除
            npriInfo.put("RSRV_TAG1", "1");
        }

        Dao.insert("TF_F_NPRI_OTHER_LOG", npriInfo);

    }

    @Override
    protected BaseReqData getReqData() throws Exception {
        return new ReturnRationReqData();
    }

    /**
     * 处理other表
     * 
     * @throws Exception
     */
    private void infoRegDataOtherAdd() throws Exception {
        float total_price = 0;
        String returnRation = reqData.getReturnRation();
        String startActiveDate = reqData.getStartActiveDate();
        String endActiveDate = reqData.getEndActiveDate();
        String activeCode = reqData.getActiveCode();
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();

        // 校验
        this.checkValidate(userId, returnRation);

        IDataset tradeInfos = new DatasetList();
        IData tradeInfo = new DataMap();
        boolean addFlag = false;
        IData addInfo = new DataMap();
        IData delInfo = new DataMap();

        IData params = new DataMap();
        params.put("USER_ID", userId);

        // 判断用户是否已经有两条返回比例记录
        int numCnt = ReturnRationQry.queryOtherCounts(userId);

        if (numCnt >= 2) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户" + userId + "的返还比例已经有两条，不可再新增!如果需要修改,请通过查询修改来做修改!");
        } else if (numCnt == 1) {// 做新增操作
            IDataset otherInfos = this.queryOtherNPRI(userId);
            if (IDataUtil.isEmpty(otherInfos)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据该" + userId + "未获取到用户的返还比例,请确认!");
            }

            IData otherInfo = otherInfos.getData(0);
            String rsrvStr3 = otherInfo.getString("RSRV_STR3", "");
            String rsrvStr4 = otherInfo.getString("RSRV_STR4", "");

            if (StringUtils.isBlank(rsrvStr3) || StringUtils.isBlank(rsrvStr4)) {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据该" +
                // userId + "获取用户的活动生效月份或活动结束月份为空1!");
                addFlag = true;
            }

            if (addFlag) {
                if (this.checkIsNpriProduct(productId)) {

                    addInfo = (IData) Clone.deepClone(otherInfo);
                    delInfo = (IData) Clone.deepClone(otherInfo);

                    // 删除原来的返还比例
                    delInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    delInfo.put("END_DATE", SysDateMgr.getSysTime());
                    delInfo.put("REMARK", "集团产品返还话费营销活动-修改");
                    delInfo.put("MODIFY_FLAG", "2");
                    tradeInfos.add(delInfo);

                    // 新增一条返还比例
                    addInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    addInfo.put("START_DATE", SysDateMgr.getSysTime());
                    addInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                    addInfo.put("RSRV_VALUE_CODE", "NPRI");
                    addInfo.put("RSRV_STR2", returnRation); // 返回比例
                    addInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                    addInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                    addInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                    addInfo.put("INST_ID", SeqMgr.getInstId());
                    addInfo.put("REMARK", "集团产品返还话费营销活动-修改");
                    addInfo.put("MODIFY_FLAG", "2");
                    tradeInfos.add(addInfo);
                } else {
                    if ("7011".equals(productId) || "7012".equals(productId)) {
                        // 1、 7011,7012专线,专线费用由成员单独计费，因此集团用户增加一条总费用为0的数据
                        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
                        IDataset directLineList = RelaUUInfoQry.getAllMebByUSERIDA(userId, relationTypeCode);
                        if (IDataUtil.isEmpty(directLineList)) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团用户user_id=" + userId + "不存在有效的专线成员，不能参加返回活动！");
                        }

                        // 2、判断专线成员是否有资费
                        boolean feelTag = true;
                        for (int i = 0; i < directLineList.size(); i++) {
                            IDataset discntDataset = UserDiscntInfoQry.getAllDiscntInfo(directLineList.getData(i).getString("USER_ID_B"));
                            if (IDataUtil.isNotEmpty(discntDataset)) {
                                feelTag = false;
                                break;
                            }
                        }
                        if (feelTag) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线成员不存在有效的资费，不能参加返回活动！");
                        }
                    } else {
                        IDataset result = this.queryOther(userId);
                        if (IDataUtil.isEmpty(result)) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "不存在N001类型");
                        }
                        for (int i = 0; i < result.size(); i++) {
                            IData otherData = result.getData(i);
                            total_price = total_price + Float.valueOf(otherData.getString("RSRV_STR3"));
                        }
                    }

                    addInfo = (IData) Clone.deepClone(otherInfo);
                    delInfo = (IData) Clone.deepClone(otherInfo);

                    // 删除原来的返还比例
                    delInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    delInfo.put("END_DATE", SysDateMgr.getSysTime());
                    delInfo.put("REMARK", "集团产品返还话费营销活动-修改");
                    delInfo.put("MODIFY_FLAG", "2");
                    tradeInfos.add(delInfo);

                    // 新增一条返还比例
                    addInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    addInfo.put("START_DATE", SysDateMgr.getSysTime());
                    addInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                    addInfo.put("RSRV_VALUE_CODE", "NPRI");
                    addInfo.put("RSRV_VALUE", "N001");
                    addInfo.put("RSRV_STR1", total_price); // 专线总费用
                    addInfo.put("RSRV_STR2", returnRation); // 返回比例
                    addInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                    addInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                    addInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                    addInfo.put("INST_ID", SeqMgr.getInstId());
                    addInfo.put("REMARK", "集团产品返还话费营销活动-修改");
                    addInfo.put("MODIFY_FLAG", "2");
                    tradeInfos.add(addInfo);
                }
            } else {
                // 校验时间
                this.checkCompareDate(startActiveDate, endActiveDate, rsrvStr3, rsrvStr4, "0");

                if (this.checkIsNpriProduct(productId)) {

                    tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    tradeInfo.put("USER_ID", userId);
                    tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                    tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
                    tradeInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                    tradeInfo.put("RSRV_VALUE_CODE", "NPRI");
                    tradeInfo.put("RSRV_STR2", returnRation); // 返回比例
                    tradeInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                    tradeInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                    tradeInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                    tradeInfo.put("INST_ID", SeqMgr.getInstId());
                    tradeInfo.put("REMARK", "集团产品返还话费营销活动-增加");
                    tradeInfo.put("MODIFY_FLAG", "0");
                    tradeInfos.add(tradeInfo);
                } else {// 含有N001类型的集团专线的返还比例新增
                    if ("7011".equals(productId) || "7012".equals(productId)) {
                        // 1、 7011,7012专线,专线费用由成员单独计费，因此集团用户增加一条总费用为0的数据
                        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
                        IDataset directLineList = RelaUUInfoQry.getAllMebByUSERIDA(userId, relationTypeCode);
                        if (IDataUtil.isEmpty(directLineList)) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团用户user_id=" + userId + "不存在有效的专线成员，不能参加返回活动！");
                        }

                        // 2、判断专线成员是否有资费
                        boolean feelTag = true;
                        for (int i = 0; i < directLineList.size(); i++) {
                            IDataset discntDataset = UserDiscntInfoQry.getAllDiscntInfo(directLineList.getData(i).getString("USER_ID_B"));
                            if (IDataUtil.isNotEmpty(discntDataset)) {
                                feelTag = false;
                                break;
                            }
                        }
                        if (feelTag) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线成员不存在有效的资费，不能参加返回活动！");
                        }
                    } else {
                        IDataset result = this.queryOther(userId);
                        if (IDataUtil.isEmpty(result)) {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "不存在N001类型");
                        }

                        for (int i = 0; i < result.size(); i++) {
                            IData otherData = result.getData(i);
                            total_price = total_price + Float.valueOf(otherData.getString("RSRV_STR3"));
                        }
                    }

                    tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    tradeInfo.put("USER_ID", userId);
                    tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                    tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
                    tradeInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                    tradeInfo.put("RSRV_VALUE_CODE", "NPRI");
                    tradeInfo.put("RSRV_VALUE", "N001");
                    tradeInfo.put("RSRV_STR1", total_price); // 专线总费用
                    tradeInfo.put("RSRV_STR2", returnRation); // 返回比例
                    tradeInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                    tradeInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                    tradeInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                    tradeInfo.put("INST_ID", SeqMgr.getInstId());
                    tradeInfo.put("REMARK", "集团产品返还话费营销活动-增加");
                    tradeInfo.put("MODIFY_FLAG", "0");
                    tradeInfos.add(tradeInfo);
                }
            }

        } else if (numCnt == 0) {// 没有返还比例时，直接做新增，不用判断返还比例记录的时间是否重叠

            if (this.checkIsNpriProduct(productId)) {
                tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                tradeInfo.put("USER_ID", userId);
                tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
                tradeInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                tradeInfo.put("RSRV_VALUE_CODE", "NPRI");
                tradeInfo.put("RSRV_STR2", returnRation); // 返回比例
                tradeInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                tradeInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                tradeInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                tradeInfo.put("INST_ID", SeqMgr.getInstId());
                tradeInfo.put("REMARK", "集团产品返还话费营销活动-增加");
                tradeInfo.put("MODIFY_FLAG", "0");
                tradeInfos.add(tradeInfo);
            } else {// 含有N001类型的集团专线的返还比例新增
                if ("7011".equals(productId) || "7012".equals(productId)) {
                    // 1、 7011,7012专线,专线费用由成员单独计费，因此集团用户增加一条总费用为0的数据
                    String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
                    IDataset directLineList = RelaUUInfoQry.getAllMebByUSERIDA(userId, relationTypeCode);
                    if (IDataUtil.isEmpty(directLineList)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团用户user_id=" + userId + "不存在有效的专线成员，不能参加返回活动！");
                    }

                    // 2、判断专线成员是否有资费
                    boolean feelTag = true;
                    for (int i = 0; i < directLineList.size(); i++) {
                        IDataset discntDataset = UserDiscntInfoQry.getAllDiscntInfo(directLineList.getData(i).getString("USER_ID_B"));
                        if (IDataUtil.isNotEmpty(discntDataset)) {
                            feelTag = false;
                            break;
                        }
                    }
                    if (feelTag) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线成员不存在有效的资费，不能参加返回活动！");
                    }
                } else {
                    IDataset result = this.queryOther(userId);
                    if (IDataUtil.isEmpty(result)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "不存在N001类型");
                    }

                    for (int i = 0; i < result.size(); i++) {
                        IData otherData = result.getData(i);
                        total_price = total_price + Float.valueOf(otherData.getString("RSRV_STR3"));
                    }
                }

                tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                tradeInfo.put("USER_ID", userId);
                tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
                tradeInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                tradeInfo.put("RSRV_VALUE_CODE", "NPRI");
                tradeInfo.put("RSRV_VALUE", "N001");
                tradeInfo.put("RSRV_STR1", total_price); // 专线总费用
                tradeInfo.put("RSRV_STR2", returnRation); // 返回比例
                tradeInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
                tradeInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
                tradeInfo.put("RSRV_STR5", activeCode);// 活动审批编号
                tradeInfo.put("INST_ID", SeqMgr.getInstId());
                tradeInfo.put("REMARK", "集团产品返还话费营销活动-增加");
                tradeInfo.put("MODIFY_FLAG", "0");
                tradeInfos.add(tradeInfo);
            }
        }

        super.addTradeOther(tradeInfos);

        if (addFlag) {
            this.createTradeUserOtherLog(params, delInfo);
            this.createTradeUserOtherLog(params, addInfo);
        } else {
            this.createTradeUserOtherLog(params, tradeInfo);
        }
    }

    /**
     * 处理other表,修改返还比例
     * 
     * @throws Exception
     */
    private void infoRegDataOtherModify() throws Exception {
        float total_price = 0;
        String returnRation = reqData.getEreturnRation();
        String startActiveDate = reqData.getEstartActiveDate();
        String endActiveDate = reqData.getEendActiveDate();
        String activeCode = reqData.getEactiveApproveCode();
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();

        String euserId = reqData.getEuserId();
        String instId = reqData.getInstId();

        // 校验
        this.checkValidate(euserId, returnRation);

        IDataset tradeInfo = new DatasetList();
        IData addInfo = new DataMap();
        IData delInfo = new DataMap();

        IData params = new DataMap();
        params.put("USER_ID", euserId);
        params.put("INST_ID", instId);

        if (this.checkIsNpriProduct(productId)) {
            IDataset modifyDataset = this.queryOtherNpriByUserInstID(params);
            IDataset notModifyDataset = this.queryOtherNpriNotUserInstID(params);

            if (IDataUtil.isEmpty(modifyDataset)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到" + euserId + "用户的返还比例,请确认!");
            }

            if (IDataUtil.isNotEmpty(notModifyDataset)) {
                IData notModifyData = notModifyDataset.getData(0);
                String rsrvStr3 = notModifyData.getString("RSRV_STR3", "");
                String rsrvStr4 = notModifyData.getString("RSRV_STR4", "");

                if (StringUtils.isBlank(rsrvStr3) || StringUtils.isBlank(rsrvStr4)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据该" + userId + "获取用户的活动生效月份或活动结束月份为空,请确认2!");
                }

                // 校验时间
                this.checkCompareDate(startActiveDate, endActiveDate, rsrvStr3, rsrvStr4, "2");
            }

            addInfo = (IData) Clone.deepClone(modifyDataset.getData(0));
            delInfo = (IData) Clone.deepClone(modifyDataset.getData(0));

            // 删除原来的返还比例
            delInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            delInfo.put("END_DATE", SysDateMgr.getSysTime());
            delInfo.put("REMARK", "集团产品返还话费营销活动-修改");
            delInfo.put("MODIFY_FLAG", "2");
            tradeInfo.add(delInfo);

            // 新增一条返还比例
            addInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            addInfo.put("START_DATE", SysDateMgr.getSysTime());
            addInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            addInfo.put("RSRV_VALUE_CODE", "NPRI");
            addInfo.put("RSRV_STR2", returnRation); // 返回比例
            addInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
            addInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
            addInfo.put("RSRV_STR5", activeCode);// 活动审批编号
            addInfo.put("INST_ID", SeqMgr.getInstId());
            addInfo.put("REMARK", "集团产品返还话费营销活动-修改");
            addInfo.put("MODIFY_FLAG", "2");
            tradeInfo.add(addInfo);

        } else {

            if ("7011".equals(productId) || "7012".equals(productId)) {
                // 1、 7011,7012专线,专线费用由成员单独计费，因此集团用户增加一条总费用为0的数据
                String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
                IDataset directLineList = RelaUUInfoQry.getAllMebByUSERIDA(userId, relationTypeCode);
                if (IDataUtil.isEmpty(directLineList)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团用户user_id=" + userId + "不存在有效的专线成员，不能参加返回活动！");
                }

                // 2、判断专线成员是否有资费
                boolean feelTag = true;
                for (int i = 0; i < directLineList.size(); i++) {
                    IDataset discntDataset = UserDiscntInfoQry.getAllDiscntInfo(directLineList.getData(i).getString("USER_ID_B"));
                    if (IDataUtil.isNotEmpty(discntDataset)) {
                        feelTag = false;
                        break;
                    }
                }
                if (feelTag) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "专线成员不存在有效的资费，不能参加返回活动！");
                }
            } else {
                IDataset result = this.queryOther(euserId);
                if (IDataUtil.isEmpty(result)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "不存在N001类型");
                }
                for (int i = 0; i < result.size(); i++) {
                    IData otherData = result.getData(i);
                    total_price = total_price + Float.valueOf(otherData.getString("RSRV_STR3"));
                }
            }

            IDataset modifyDataset = this.queryOtherNpriByUserInstID(params);
            IDataset notModifyDataset = this.queryOtherNpriNotUserInstID(params);

            if (IDataUtil.isEmpty(modifyDataset)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到" + euserId + "用户的返还比例,请确认!");
            }

            if (IDataUtil.isNotEmpty(notModifyDataset)) {
                IData notModifyData = notModifyDataset.getData(0);
                String rsrvStr3 = notModifyData.getString("RSRV_STR3", "");
                String rsrvStr4 = notModifyData.getString("RSRV_STR4", "");

                if (StringUtils.isBlank(rsrvStr3) || StringUtils.isBlank(rsrvStr4)) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据该" + userId + "获取用户的活动生效月份或活动结束月份为空,请确认3!");
                }

                // 校验时间
                this.checkCompareDate(startActiveDate, endActiveDate, rsrvStr3, rsrvStr4, "2");

            }

            addInfo = (IData) Clone.deepClone(modifyDataset.getData(0));
            delInfo = (IData) Clone.deepClone(modifyDataset.getData(0));

            // 删除原来的返还比例
            delInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            delInfo.put("END_DATE", SysDateMgr.getSysTime());
            delInfo.put("REMARK", "集团产品返还话费营销活动-修改");
            delInfo.put("MODIFY_FLAG", "2");
            tradeInfo.add(delInfo);

            // 新增一条返还比例
            addInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            addInfo.put("START_DATE", SysDateMgr.getSysTime());
            addInfo.put("END_DATE", SysDateMgr.getTheLastTime());
            addInfo.put("RSRV_VALUE_CODE", "NPRI");
            addInfo.put("RSRV_VALUE", "N001");
            addInfo.put("RSRV_STR1", total_price); // 专线总费用
            addInfo.put("RSRV_STR2", returnRation); // 返回比例
            addInfo.put("RSRV_STR3", startActiveDate);// 活动生效月份
            addInfo.put("RSRV_STR4", endActiveDate);// 活动结束月份
            addInfo.put("RSRV_STR5", activeCode);// 活动审批编号
            addInfo.put("INST_ID", SeqMgr.getInstId());
            addInfo.put("REMARK", "集团产品返还话费营销活动-修改");
            addInfo.put("MODIFY_FLAG", "2");
            tradeInfo.add(addInfo);

        }

        super.addTradeOther(tradeInfo);

        this.createTradeUserOtherLog(params, delInfo);
        this.createTradeUserOtherLog(params, addInfo);
    }

    /**
     * 
     * @throws Exception
     */
    private void infoRegDataOtherDel() throws Exception {
        IData delInfo = new DataMap();
        String euserId = reqData.getEuserId();
        String instId = reqData.getInstId();

        IData params = new DataMap();
        params.put("USER_ID", euserId);
        params.put("INST_ID", instId);

        IDataset modifyDataset = this.queryOtherNpriByUserInstID(params);

        if (IDataUtil.isNotEmpty(modifyDataset)) {
            delInfo = modifyDataset.getData(0);
            if (IDataUtil.isNotEmpty(delInfo)) {
                // 删除原来的返还比例
                delInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                delInfo.put("END_DATE", SysDateMgr.getSysTime());
                delInfo.put("REMARK", "集团产品返还话费营销活动-删除");
                delInfo.put("MODIFY_FLAG", "1");
            }
        }

        super.addTradeOther(delInfo);

        this.createTradeUserOtherLog(params, delInfo);
    }

    /**
     * 校验值
     * 
     * @param userId
     * @param returnRation
     * @throws Exception
     */
    private void checkValidate(String userId, String returnRation) throws Exception {

        if (StringUtils.isEmpty(userId)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请先选择集团产品");
        }
        int val = 0;
        try {
            val = Integer.parseInt(returnRation);
        }
        catch (Exception e) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "返回比例必须为整数!!");
        }
        if (val > 100 || val < 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "返回比例必须在1~100之间!!");
        }
        String staffId = CSBizBean.getVisit().getStaffId();
        if (staffId != null || !staffId.equals("")) {
            if (!staffId.toLowerCase().contains("hnsj")) {
                if (val > 50) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "只有总局才能设置返回比例超过50%");
                }
            }
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "STAFFID不能为空");
        }
    }

    /**
     * 校验活动月份是否重叠
     * 
     * @param startActiveDate
     * @param endActiveDate
     * @param rsrvStr3
     * @param rsrvStr4
     * @param modify
     * @throws Exception
     */
    private void checkCompareDate(String startActiveDate, String endActiveDate, String rsrvStr3, String rsrvStr4, String modify) throws Exception {

        StringBuffer sbuffer = new StringBuffer();
        if (modify != null && "2".equals(modify)) {
            sbuffer.append("修改的返还录入比例的活动月份与另一条返回比例的活动月份重叠,不允许修改!用户另一条的活动生效月份:");
        } else if (modify != null && "0".equals(modify)) {
            sbuffer.append("新增返还录入比例的活动月份与原来的活动月份重叠,不允许办理!用户原来活动生效月份:");
        }
        sbuffer.append(rsrvStr3);
        sbuffer.append(",活动结束月份:");
        sbuffer.append(rsrvStr4);

        if (startActiveDate.compareTo(rsrvStr3) <= 0 && endActiveDate.compareTo(rsrvStr3) >= 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, sbuffer.toString());

        } else if (startActiveDate.compareTo(rsrvStr3) >= 0 && endActiveDate.compareTo(rsrvStr4) <= 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, sbuffer.toString());

        } else if (startActiveDate.compareTo(rsrvStr3) >= 0 && startActiveDate.compareTo(rsrvStr4) <= 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, sbuffer.toString());

        } else if (startActiveDate.compareTo(rsrvStr3) >= 0 && startActiveDate.compareTo(rsrvStr4) <= 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, sbuffer.toString());

        }

    }

    @Override
    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (ReturnRationReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);
        reqData.setReturnRation(map.getString("RETURN_RATION"));
        reqData.setStartActiveDate(map.getString("START_ACTIVE_DATE"));
        reqData.setEndActiveDate(map.getString("END_ACTIVE_DATE"));
        reqData.setActiveCode(map.getString("ACTIVE_APPROVE_CODE"));
        reqData.setRemark(map.getString("REMARK"));
        reqData.setGroupId(map.getString("GROUP_ID"));

        reqData.setOperation(map.getString("OPERATION"));
        reqData.setEuserId(map.getString("EUSER_ID"));
        reqData.setInstId(map.getString("INST_ID"));
        reqData.setEactiveApproveCode(map.getString("EACTIVE_APPROVE_CODE"));
        reqData.setEreturnRation(map.getString("ERETURN_RATION"));
        reqData.setEstartActiveDate(map.getString("ESTART_ACTIVE_DATE"));
        reqData.setEendActiveDate(map.getString("EEND_ACTIVE_DATE"));
    }

    @Override
    protected final void makUca(IData map) throws Exception {
        makUcaForGrpNormal(map);
    }

    /**
     * 返回比例录入分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryGroupProductInfo(IData param, Pagination pagination) throws Exception {
        IDataset infos = ReturnRationQry.queryGroupProductInfo(param, pagination);
        return infos;
    }

    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private IDataset queryOther(String userId) throws Exception {
        IDataset otherindos = ReturnRationQry.queryOther(userId);
        return otherindos;
    }

    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private IDataset queryOtherNPRI(String userId) throws Exception {
        IDataset otherindos = ReturnRationQry.queryOtherNPRI(userId);
        return otherindos;
    }

    public IDataset queryProduct(IData data) throws Exception {
        IDataset infos = ReturnRationQry.queryProduct(data);
        return infos;
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        return "8902";
    }

    /**
     * 返回比例录入other表信息的分页查询
     * 
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryRatioOtherInfo(IData param, Pagination pagination) throws Exception {
        IDataset infos = ReturnRationQry.queryRatioOtherInfo(param, pagination);
        return infos;
    }

    /**
     * 根据userId和InstId查询返还比例信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    private IDataset queryOtherNpriByUserInstID(IData params) throws Exception {
        IDataset otherindos = ReturnRationQry.queryOtherNpriByUserInstID(params);
        return otherindos;
    }

    /**
     * 根据userId和InstId查询返还比例信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    private IDataset queryOtherNpriNotUserInstID(IData params) throws Exception {
        IDataset otherindos = ReturnRationQry.queryOtherNpriNotUserInstID(params);
        return otherindos;
    }

    /**
     * 校验产品ID是否是NPRI类型的产品
     * 
     * @param productId
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-1-17
     */
    private boolean checkIsNpriProduct(String productId) throws Exception {
        boolean retFlag = false;
        IDataset comds = CommparaInfoQry.getCommpara("CSM", "3317", productId, "0898");
        if (IDataUtil.isNotEmpty(comds)) {
            IData com = comds.getData(0);
            String paraCode3 = com.getString("PARA_CODE3", "");
            if ("NPRI".equals(paraCode3)) {
                retFlag = true;
            }
        }
        return retFlag;
    }

}
