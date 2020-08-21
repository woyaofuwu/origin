
package com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order.requestdata.ModifySpeInfoReqData;

/**
 * 特殊资料变更TRADE类
 * 
 * @author liutt
 */
public class ModifySpeTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        modifyUserResTrade(btd);// 修改用户资源资料
        modifyUserSvcStateTrade(btd);// 修改用户服务状态
        // 修改用户重新信息 配置infochangeAction解决
        modifyUserProductTrade(btd);// 修改用户重新信息
        modifyUserTrade(btd);// 修改用户信息
        modifyCustomerTrade(btd);//
        modifyAccountTrade(btd);//
        modifyMainTrade(btd);
    }

    /**
     * 账户信息修改
     * 
     * @param btd
     * @throws Exception
     */
    private void modifyAccountTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果业务区有修改，则修改客户资料表中的业务区
        if (StringUtils.isNotBlank(reqData.getCityCode()))
        {
            AccountTradeData accountTrade = reqData.getUca().getAccount();
            accountTrade.setCityCode(reqData.getCityCode());
            accountTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(reqData.getUca().getSerialNumber(), accountTrade);
        }
    }

    /**
     * 客户信息修改
     * 
     * @param btd
     * @throws Exception
     */
    private void modifyCustomerTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果业务区有修改，则修改客户资料表中的业务区
        if (StringUtils.isNotBlank(reqData.getCityCode()))
        {
            CustomerTradeData customerTrade = reqData.getUca().getCustomer();
            customerTrade.setCityCode(reqData.getCityCode());
            customerTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(reqData.getUca().getSerialNumber(), customerTrade);

            CustPersonTradeData custpersonTrade = reqData.getUca().getCustPerson();
            custpersonTrade.setCityCode(reqData.getCityCode());
            custpersonTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(reqData.getUca().getSerialNumber(), custpersonTrade);

            // 修改tf_f_cust_vip
            StringBuilder sql = new StringBuilder(200);
            sql.append("UPDATE tf_f_cust_vip ");
            sql.append("set city_code =:city_code ");
            sql.append("where  user_id =:user_id ");

            IData param = new DataMap();
            param.put("city_code", reqData.getCityCode());
            param.put("user_id", reqData.getUca().getUserId());
            Dao.executeUpdate(sql, param);
        }
    }

    public void modifyMainTrade(BusiTradeData btd) throws Exception
    {

        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        btd.getMainTradeData().setRsrvStr1(reqData.getBrandCode());
        btd.getMainTradeData().setRsrvStr2(reqData.getUserStateCode());
        btd.getMainTradeData().setRsrvStr3(reqData.getSimCardNo());
        btd.getMainTradeData().setRsrvStr4(reqData.getImsi());
        btd.getMainTradeData().setRsrvStr5(reqData.getCityCode());
        btd.getMainTradeData().setRsrvStr6(reqData.getUserTypeCode());
        btd.getMainTradeData().setRsrvStr7(reqData.getDevelopStaffId());
        btd.getMainTradeData().setRsrvStr9(reqData.getRemark());
    }

    /**
     * 用户产品信息修改
     * 
     * @param btd
     * @throws Exception
     */
    private void modifyUserProductTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果用户品牌有改动则修改用户产品表
        if (StringUtils.isNotBlank(reqData.getBrandCode()))
        {
            ProductTradeData oldProductTrade = reqData.getUca().getUserMainProduct();

            ProductTradeData newProductTrade = oldProductTrade.clone();

            newProductTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);// 修改
            newProductTrade.setInstId(SeqMgr.getInstId());// 新inst_id
            newProductTrade.setStartDate(reqData.getAcceptTime());
            newProductTrade.setBrandCode(reqData.getBrandCode());
            newProductTrade.setRemark(reqData.getRemark());
            btd.add(reqData.getUca().getSerialNumber(), newProductTrade);

            // 删除原来的
            oldProductTrade.setEndDate(SysDateMgr.getLastSecond(reqData.getAcceptTime()));
            oldProductTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);// 删除
            btd.add(reqData.getUca().getSerialNumber(), oldProductTrade);
        }
    }

    /**
     * 修改用户资源信息
     * 
     * @param btd
     */
    private void modifyUserResTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果sim卡或者imsi有改动则修改用户资源表
        if (StringUtils.isNotBlank(reqData.getSimCardNo()) || StringUtils.isNotBlank(reqData.getImsi()))
        {
            // 查询用户有效的simcard信息
            IDataset resDataInfos = UserResInfoQry.queryUserResByUserIdResType(reqData.getUca().getUserId(), "1");
            if (IDataUtil.isNotEmpty(resDataInfos))
            {
                IData resData = resDataInfos.getData(0);

                // 新增新的
                ResTradeData newResTradeData = new ResTradeData(resData);
                newResTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);// 修改
                newResTradeData.setInstId(SeqMgr.getInstId());// 新inst_id
                newResTradeData.setStartDate(reqData.getAcceptTime());
                if (StringUtils.isNotBlank(reqData.getSimCardNo()))
                {
                    newResTradeData.setResCode(reqData.getSimCardNo());// 新simcard号
                }
                if (StringUtils.isNotBlank(reqData.getImsi()))
                {
                    newResTradeData.setImsi(reqData.getImsi());// 新Imei号
                }
                newResTradeData.setRemark(reqData.getRemark());
                btd.add(reqData.getUca().getSerialNumber(), newResTradeData);

                // 删除原来的
                ResTradeData delResTradeData = new ResTradeData(resData);
                delResTradeData.setEndDate(SysDateMgr.getLastSecond(reqData.getAcceptTime()));
                delResTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);// 删除
                btd.add(reqData.getUca().getSerialNumber(), delResTradeData);
            }
        }
    }

    /**
     * 修改用户服务状态
     * 
     * @param btd
     */
    private void modifyUserSvcStateTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果用户服务状态有改动则修改用户服务状态表
        if (StringUtils.isNotBlank(reqData.getUserStateCode()))
        {
            SvcStateTradeData oldStateTradeData = reqData.getUca().getUserSvcsStateByServiceId("0");

            SvcStateTradeData newStateTradeData = oldStateTradeData.clone();
            newStateTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);// 修改
            newStateTradeData.setInstId(SeqMgr.getInstId());// 新inst_id
            newStateTradeData.setStartDate(reqData.getAcceptTime());
            newStateTradeData.setStateCode(reqData.getUserStateCode());
            newStateTradeData.setRemark(reqData.getRemark());
            btd.add(reqData.getUca().getSerialNumber(), newStateTradeData);

            // 删除原来的
            oldStateTradeData.setEndDate(SysDateMgr.getLastSecond(reqData.getAcceptTime()));
            oldStateTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);// 删除
            btd.add(reqData.getUca().getSerialNumber(), oldStateTradeData);
        }
    }

    /**
     * 用户信息修改
     * 
     * @param btd
     * @throws Exception
     */
    private void modifyUserTrade(BusiTradeData btd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) btd.getRD();
        // 如果用户品牌有改动则修改用户产品表
        if (StringUtils.isNotBlank(reqData.getUserStateCode()) || StringUtils.isNotBlank(reqData.getUserTypeCode()) || StringUtils.isNotBlank(reqData.getDevelopStaffId()) || StringUtils.isNotBlank(reqData.getCityCode()))
        {
            UserTradeData userTrade = reqData.getUca().getUser();

            // 服务状态
            if (StringUtils.isNotBlank(reqData.getUserStateCode()))
            {
                userTrade.setUserStateCodeset(reqData.getUserStateCode());
            }
            // 用户类型
            if (StringUtils.isNotBlank(reqData.getUserTypeCode()))
            {
                userTrade.setUserTypeCode(reqData.getUserTypeCode());
            }
            // 发展员工
            if (StringUtils.isNotBlank(reqData.getDevelopStaffId()))
            {
                userTrade.setDevelopStaffId(reqData.getDevelopStaffId());
            }
            // 归属业务区
            if (StringUtils.isNotBlank(reqData.getCityCode()))
            {
                userTrade.setCityCode(reqData.getCityCode());
            }
            userTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
            userTrade.setRemark(reqData.getRemark());
            btd.add(reqData.getUca().getSerialNumber(), userTrade);
        }
    }

}
