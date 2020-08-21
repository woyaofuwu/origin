
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * tf_f_user_infochange 逻辑处理，此次融合了原来UserInfoChgMgr.cpp中的 AddUserInfoChg，AddUserInfoChg2，ModifyUserInfoChg逻辑，重新规划
 * 
 * @author J2EE
 */
public class UserInfoChgAction implements ITradeFinishAction
{

    /**
     * 生成新的infochange信息
     * 
     * @param userId
     * @auth liuke
     */
    private void createNewInfoChange(String userId, IData mainTrade) throws Exception
    {
        /********************** 资料准备 开始 *************************/
    	String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
    	if (StringUtils.equals("5432", tradeTypeCode)||StringUtils.equals("5433", tradeTypeCode)
    			||StringUtils.equals("5434", tradeTypeCode)||StringUtils.equals("5435", tradeTypeCode)
    			||StringUtils.equals("5436", tradeTypeCode)||StringUtils.equals("5437", tradeTypeCode)
    			||StringUtils.equals("5438", tradeTypeCode)||StringUtils.equals("5439", tradeTypeCode))
        {
    		return ;
        }
        // 不能取缓存的，取数据库的
        IData userInfo = UcaInfoQry.qryUserInfoByUserIdFromDB(userId, null);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_189, userId);
        }
        
        // 产品信息
        IDataset userProductInfos = UserProductInfoQry.queryUserMainProduct(userId);
        if (IDataUtil.isEmpty(userProductInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }
      
        String res_type_code = "1";// 普通业务默认查sim卡

        // 用户信息
        String userStartDate = userInfo.getString("OPEN_DATE");// 用户信息开始时间

        // 如果存在号码资源的新增，则用户的开户时间取使用号码的开始时间，
        // 主要针对 复机、改号这些场景
        IDataset serialNumTradeInfos = TradeResInfoQry.getTradeRes(mainTrade.getString("TRADE_ID"), "0", "0");
        if (IDataUtil.isNotEmpty(serialNumTradeInfos))
        {
            for (int i = 0, size = serialNumTradeInfos.size(); i < size; i++)
            {
                if (StringUtils.equals(userId, serialNumTradeInfos.getData(i).getString("USER_ID")))
                {
                    // 取用户表中最新的号码开始时间，这样可以支持正向和返销
                    IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userId, "0");
                    if (IDataUtil.isNotEmpty(userResInfos))
                    {
                        userStartDate = userResInfos.getData(0).getString("START_DATE");
                        break;
                    }
                }
            }
        }

        // 复机
        /*
         * if (StringUtils.equals("310", tradeTypeCode) || StringUtils.equals("7302", tradeTypeCode)) { userStartDate =
         * mainTrade.getString("EXEC_TIME");// 用户信息开始时间 }else if (StringUtils.equals("143", tradeTypeCode))//改号 {
         * userStartDate = mainTrade.getString("EXEC_TIME");// 改号的开始时间取工单的执行时间 }
         */

        String userEndDate = SysDateMgr.END_DATE_FOREVER;// 用户信息开始时间

        // 集团BBOSS业务取serialNumber、res_type_code特殊处理
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        if (StringUtils.equals("4690", tradeTypeCode) || StringUtils.equals("4691", tradeTypeCode) || StringUtils.equals("4692", tradeTypeCode) || StringUtils.equals("4693", tradeTypeCode))
        {
            String productId = userProductInfos.getData(0).getString("PRODUCT_ID", "");//获取主产品
            // 如果RSRV_STR1配的是SERIAL_NUMBER,异动表的SERIAL_NUMBER用集团的全网集团编码填充
            IDataset attrbizinfos = AttrBizInfoQry.getBizAttrByDynamic("1", "B", "PRO", productId, null);
            if (IDataUtil.isNotEmpty(attrbizinfos) && "SERIAL_NUMBER".equals(attrbizinfos.getData(0).getString("RSRV_STR1")))
            {

                IData custInfo = UcaInfoQry.qryGrpInfoByCustId(mainTrade.getString("CUST_ID"));

                if (IDataUtil.isNotEmpty(custInfo))
                {
                    serialNumber = custInfo.getString("MP_GROUP_CUST_CODE", serialNumber);
                }
            }
            res_type_code = "G";
        }
        if(StringUtils.equals("9601", tradeTypeCode)){
        	//家庭固话业务（其中 user_id=手机号码user_id  ,serail_number=固话号码）
        	String fixNumber=mainTrade.getString("RSRV_STR1");
        	serialNumber=fixNumber;
        }

        // 资源信息
        String imsi = "0";
        String resStartDate = "";// 资源开始时间
        String resEndDate = SysDateMgr.END_DATE_FOREVER;// 资源结束时间
        IDataset userResInfos = UserResInfoQry.queryUserResByUserIdResType(userId, res_type_code);// 查sim卡
        if (IDataUtil.isNotEmpty(userResInfos))// 虚拟用户是没有该资料的
        {
            imsi = userResInfos.getData(0).getString("IMSI", "0");
            resStartDate = userResInfos.getData(0).getString("START_DATE");
            resEndDate = userResInfos.getData(0).getString("END_DATE");
        }

        /********************** 资料准备 结束 *************************/
        IData infoChangeCommData = new DataMap();
        infoChangeCommData.put("USER_ID", userId);
        infoChangeCommData.put("SERIAL_NUMBER", serialNumber);
        infoChangeCommData.put("PARTITION_ID", userId.substring(userId.length() - 4));
        infoChangeCommData.put("IMSI", imsi);
        infoChangeCommData.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
        infoChangeCommData.put("RELATION_TRADE_ID", mainTrade.getString("TRADE_ID"));
        infoChangeCommData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        infoChangeCommData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
        infoChangeCommData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
        infoChangeCommData.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE"));

        String sysTime = SysDateMgr.getSysTime();
        // String sysTime = mainTrade.getString("ACCEPT_DATE",SysDateMgr.getSysTime());

        this.deleteValidInfoChange(userId);// 先删除还未生效的记录

        // 110 和 601 都是存在预约产品变更的情况，
        // 这里其实最好不要写死业务类型，可以分析一下 去掉业务类型
        if ((StringUtils.equals("110", tradeTypeCode) || StringUtils.equals("601", tradeTypeCode)) && userProductInfos.size() > 1)// 下周期产品变更特殊处理
        {
            for (int i = 0; i < userProductInfos.size(); i++)// 当前认为只有产品才有可能出现多条的情况
            {
                IData productData = userProductInfos.getData(i);
                String prodStartDate = productData.getString("START_DATE");// 产品开始时间
                if (SysDateMgr.getTimeDiff(sysTime, prodStartDate, SysDateMgr.PATTERN_STAND) > 0)// 开始时间大于系统时间这条才需要处理
                {
                    this.endValidInfoChange(userId, prodStartDate);// 终止当前正在生效的记录,结束时间为新的开始时间

                    String prodEndDate = productData.getString("END_DATE");// 产品结束时间
                    String endDate = this.getEndMinDate(userEndDate, resEndDate, prodEndDate);// 取用户、资源、产品最小的结束时间

                    IData infoChangeData = new DataMap();
                    infoChangeData.putAll(infoChangeCommData);
                    infoChangeData.put("START_DATE", prodStartDate);// 产品的开始时间
                    infoChangeData.put("END_DATE", endDate);// 结束时间还是计算
                    infoChangeData.put("PRODUCT_ID", productData.getString("PRODUCT_ID"));
                    infoChangeData.put("BRAND_CODE", productData.getString("BRAND_CODE"));
                    infoChangeData.put("INST_ID", SeqMgr.getInstId());
                    Dao.insert("TF_F_USER_INFOCHANGE", infoChangeData);// 新增记录
                }
            }
        }
        else
        {
            // 1、删除还未生效的数据 start>syadate
            // 2、终止开始时间小于当前 且end_date>sysdate
            // 3、重新计算
            IDataset newDataset = new DatasetList();
            for (int i = 0; i < userProductInfos.size(); i++)
            {
                IData productData = userProductInfos.getData(i);
                String prodStartDate = productData.getString("START_DATE");// 产品开始时间
                String prodEndDate = productData.getString("END_DATE");// 产品结束时间
                String startDate = this.getStartMaxDate(userStartDate, resStartDate, prodStartDate);// 取用户、资源、产品最大的开始时间
                String endDate = this.getEndMinDate(userEndDate, resEndDate, prodEndDate);// 取用户、资源、产品最小的结束时间
                if (SysDateMgr.getTimeDiff(sysTime, prodStartDate, SysDateMgr.PATTERN_STAND) <= 0)// 产品开始时间小于等于系统时间的认为是已经生效的，
                {
                    this.endValidInfoChange(userId, startDate);// 终止当前正在生效的记录，拿这个开始时间终止
                }

                IData infoChangeData = new DataMap();
                infoChangeData.putAll(infoChangeCommData);
                infoChangeData.put("START_DATE", startDate);
                infoChangeData.put("END_DATE", endDate);
                infoChangeData.put("PRODUCT_ID", productData.getString("PRODUCT_ID"));
                infoChangeData.put("BRAND_CODE", productData.getString("BRAND_CODE"));
                infoChangeData.put("INST_ID", SeqMgr.getInstId());
                newDataset.add(infoChangeData);
            }
            if (newDataset.size() == 0)
            {
                CSAppException.apperr(TradeException.CRM_TRADE_95, "没有最新的infochange信息！");
            }
            else
            {
                Dao.insert("TF_F_USER_INFOCHANGE", newDataset);// 最后新增 记录
            }
        }

        // 数据校验，防止因为异常导致出现数据错误
        IDataset userNowValidInfos = UserInfoQry.getUserInfoChgByUserIdCurvalid(userId);
        if (userNowValidInfos.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1060);// 该业务受理之后用户不存在有效的infochange信息！
        }
        else if (userNowValidInfos.size() > 1)
        {
            // 报错，同一时间存在多条有效的infochange信息。
            CSAppException.apperr(CrmUserException.CRM_USER_1061);// 该业务受理之后用户同一时间存在多条有效的infochange信息！
        }
    }

    /**
     * 处理用户重要信息
     * 
     * @param mainTrade
     * @throws Exception
     * @auth liuke
     * @date 2013-10-29 PM
     */
    private void dealUserInfoChange(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String intfId = mainTrade.getString("INTF_ID");
        String cancelTag = mainTrade.getString("CANCEL_TAG");
        // String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");//业务类型
        IData userIdData = new DataMap();// 收集本订单涉及到的user_id
        IData cancelAddUserIdData = new DataMap();// 收集返销时订单涉及到的新增user_id
        IData destoreUserIdData = new DataMap();// 收集返销时订单涉及到的新增user_id

        if (StringUtils.isNotBlank(intfId))
        {
            // 首先检查本次工单中是否存在 用户、产品、资源信息的变化
            boolean existsUser = StringUtils.indexOf(intfId, TradeTableEnum.TRADE_USER.getValue() + ",") > -1 ? true : false;
            boolean existsProduct = StringUtils.indexOf(intfId, TradeTableEnum.TRADE_PRODUCT.getValue() + ",") > -1 ? true : false;
            boolean existRes = StringUtils.indexOf(intfId, TradeTableEnum.TRADE_RES.getValue() + ",") > -1 ? true : false;

            if (existsUser)
            {
                IDataset tradeUserInfos = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
                for (int i = 0; i < tradeUserInfos.size(); i++)
                {
                    String modifyTag = tradeUserInfos.getData(i).getString("MODIFY_TAG");

                    if (StringUtils.equals(BofConst.MODIFY_TAG_UPD, modifyTag))
                    {
                        continue;// 修改用户资料的不要处理
                    }

                    String userId = tradeUserInfos.getData(i).getString("USER_ID");
                    userIdData.put(userId, userId);

                    // 开户返销
                    if (StringUtils.equals("2", cancelTag) && StringUtils.equals("0", modifyTag))
                    {
                        cancelAddUserIdData.put(userId, userId);// 记录返销时新增的userId，后面直接删除infochange信息
                        existsProduct = false;// 没有必要去查询后面的了，提高效率
                        existRes = false;// 没有必要去查询后面的了，提高效率
                    }// 销户
                    else if (StringUtils.equals("0", cancelTag) && StringUtils.equals("1", modifyTag))
                    {
                        destoreUserIdData.put(userId, userId);// 记录正向销户的用户，后面直接删除infochange信息
                        existsProduct = false;// 没有必要去查询后面的了，提高效率
                        existRes = false;// 没有必要去查询后面的了，提高效率
                    }
                }
            }

            if (existsProduct)// 产品
            {
                IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);// MAIN_TAG ADD
                for (int i = 0; i < tradeProductInfos.size(); i++)
                {
                    String mainTag = tradeProductInfos.getData(i).getString("MAIN_TAG", "");// 只关心主产品
                    if (StringUtils.equals("1", mainTag))
                    {
                        String userId = tradeProductInfos.getData(i).getString("USER_ID");
                        userIdData.put(userId, userId);
                    }
                }
            }

            if (existRes)// 资源
            {
                IDataset tradeResInfos = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
                for (int i = 0; i < tradeResInfos.size(); i++)
                {
                    String resTypeCode = tradeResInfos.getData(i).getString("RES_TYPE_CODE", "");// 只关心IMSI信息
                    String modifyTag = tradeResInfos.getData(i).getString("MODIFY_TAG");// 只关心IMSI信息
                    // 关心sim卡变化 以及新增号码的情况(主要针对改号这种场景)
                    if (StringUtils.equals("1", resTypeCode) || (StringUtils.equals("0", resTypeCode) && StringUtils.equals("0", modifyTag)))
                    {
                        String userId = tradeResInfos.getData(i).getString("USER_ID");
                        userIdData.put(userId, userId);
                    }
                }
            }
        }
        else
        // 如果intf_id字段没值的话，则直接查询对应订单信息，相当于一个容错处理
        {
            IDataset tradeUserInfos = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
            for (int i = 0; i < tradeUserInfos.size(); i++)
            {
                String modifyTag = tradeUserInfos.getData(i).getString("MODIFY_TAG");
                if (StringUtils.equals(BofConst.MODIFY_TAG_UPD, modifyTag))
                {
                    continue;// 修改用户资料的不要处理
                }

                String userId = tradeUserInfos.getData(i).getString("USER_ID");
                userIdData.put(userId, userId);

                if (StringUtils.equals("2", cancelTag) && StringUtils.equals("0", modifyTag))
                {
                    cancelAddUserIdData.put(userId, userId);// 记录返销时新增的userId，后面直接删除infochange信息
                }
                else if (StringUtils.equals("0", cancelTag) && StringUtils.equals("1", modifyTag))
                {
                    destoreUserIdData.put(userId, userId);// 记录正向销户的用户，后面直接删除infochange信息
                }
            }

            // 产品
            IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
            for (int i = 0; i < tradeProductInfos.size(); i++)
            {
                String mainTag = tradeProductInfos.getData(i).getString("MAIN_TAG", "");
                if (StringUtils.equals("1", mainTag))
                {
                    String userId = tradeProductInfos.getData(i).getString("USER_ID");
                    userIdData.put(userId, userId);
                }
            }

            // 资源
            IDataset tradeResInfos = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
            for (int i = 0; i < tradeResInfos.size(); i++)
            {
                String resTypeCode = tradeResInfos.getData(i).getString("RES_TYPE_CODE", "");// 只关心IMSI信息
                String modifyTag = tradeResInfos.getData(i).getString("MODIFY_TAG");// 只关心IMSI信息
                if (StringUtils.equals("1", resTypeCode) || (StringUtils.equals("0", resTypeCode) && StringUtils.equals("0", modifyTag)))
                {
                    String userId = tradeResInfos.getData(i).getString("USER_ID");
                    userIdData.put(userId, userId);
                }
            }
        }

        // 处理销户用户的infochange
        if (!destoreUserIdData.isEmpty())
        {
            Set<String> keyset = destoreUserIdData.keySet();
            Iterator iterrator = keyset.iterator();
            while (iterrator.hasNext())
            {
                String userId = (String) iterrator.next();
                this.endValidInfoChange(userId, SysDateMgr.getSysTime());// 处理用户infochange信息

                userIdData.remove(userId);// 删除掉该userId
            }
        }

        // 返销时处理新增用户的infochange
        if (!cancelAddUserIdData.isEmpty())// 处理新增用户返销时的信息
        {
            Set<String> keyset = cancelAddUserIdData.keySet();
            Iterator iterrator = keyset.iterator();
            while (iterrator.hasNext())
            {
                String userId = (String) iterrator.next();
                this.endValidInfoChange(userId, SysDateMgr.getSysTime());// 老系统是终止资料非删除

                userIdData.remove(userId);// 删除掉该userId
            }
        }

        // 存在需要重新计算用户重要信息的用户
        if (!userIdData.isEmpty())
        {
            Set<String> keyset = userIdData.keySet();
            Iterator iterrator = keyset.iterator();
            while (iterrator.hasNext())
            {
                String userId = (String) iterrator.next();
                this.createNewInfoChange(userId, mainTrade);// 处理用户infochange信息
            }
        }
    }

    // 删除还未生效的记录
    private void deleteValidInfoChange(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM TF_F_USER_INFOCHANGE T WHERE T.USER_ID=:USER_ID AND T.PARTITION_ID=MOD(:USER_ID,10000) AND START_DATE>SYSDATE");
        Dao.executeUpdate(sb, param);
    }

    // 终止原始有效记录
    private void endValidInfoChange(String userId, String endDate) throws Exception
    {
        // 终止有效记录
        IData inparam = new DataMap();
        inparam.put("END_DATE", endDate);// SysDateMgr.getSysTime()
        inparam.put("USER_ID", userId);
        Dao.executeUpdateByCodeCode("TF_F_USER_INFOCHANGE", "UPD_DEL_USERINFO", inparam);

        // 再删除start_date>=end_date的错误数据
        Dao.executeUpdateByCodeCode("TF_F_USER_INFOCHANGE", "DEL_CHANGEINFO", inparam);
    }

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        if("49".equals(tradeTypeCode)||"276".equals(tradeTypeCode)||"149".equals(tradeTypeCode)){ 
            return;
        }
        
        if("9601".equals(tradeTypeCode)){
        	String userId=mainTrade.getString("USER_ID");
        	this.createNewInfoChange(userId, mainTrade);
        }
        
        this.dealUserInfoChange(mainTrade);
    }

    // 比较用户、产品、资源三个结束日期中最小的一个
    private String getEndMinDate(String userEndDateStr, String resEndDateStr, String prodEndDateStr) throws Exception
    {
        String pattern = SysDateMgr.PATTERN_STAND;
        long userDateTime = SysDateMgr.string2Date(userEndDateStr, pattern).getTime();
        long prodDateTime = SysDateMgr.string2Date(prodEndDateStr, pattern).getTime();
        long resDateTime = SysDateMgr.string2Date(resEndDateStr, pattern).getTime();

        long[] endDate = new long[]
        { userDateTime, prodDateTime, resDateTime };
        Arrays.sort(endDate);// 升序排序
        long minTime = endDate[0];// 取最小的一个
        if (minTime == userDateTime)
        {
            return userEndDateStr;
        }
        else if (minTime == prodDateTime)
        {
            return prodEndDateStr;
        }
        else
        {
            return resEndDateStr;
        }
    }

    // 比较用户、资源、产品三个开始日期中最大的一个
    private String getStartMaxDate(String userStartDateStr, String resStartDateStr, String prodStartDateStr) throws Exception
    {
        String pattern = SysDateMgr.PATTERN_STAND;
        long userDateTime = SysDateMgr.string2Date(userStartDateStr, pattern).getTime();
        long prodDateTime = SysDateMgr.string2Date(prodStartDateStr, pattern).getTime();
        long resDateTime = -1L;
        if (!StringUtils.equals("", resStartDateStr))
        {
            resDateTime = SysDateMgr.string2Date(resStartDateStr, pattern).getTime();
        }

        long[] startDate = new long[]
        { userDateTime, prodDateTime, resDateTime };
        Arrays.sort(startDate);// 升序排序
        long maxTime = startDate[2];// 取最大的一个

        if (maxTime == userDateTime)
        {
            return userStartDateStr;
        }
        else if (maxTime == prodDateTime)
        {
            return prodStartDateStr;
        }
        else
        {
            return resStartDateStr;
        }
    }

}
