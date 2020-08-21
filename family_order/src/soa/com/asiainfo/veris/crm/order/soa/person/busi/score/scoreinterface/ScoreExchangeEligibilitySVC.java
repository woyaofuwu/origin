
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ScoreExchangeEligibilitySVC extends CSBizService
{

    public static IDataset queryUserDiscnt(IData input) throws Exception
    {
        IDataset returnDs = new DatasetList();
        String ids = input.getString("DISCNT_CODES");
        if (ids.contains(","))
        {
            String[] idArray = ids.split(",");
            for (String id : idArray)
            {
                IDataset svcData = UserDiscntInfoQry.getAllDiscntByUser(input.getString("USER_ID"), id);
                if (IDataUtil.isNotEmpty(svcData))
                {
                    returnDs.add(svcData.getData(0));
                }
            }
        }
        else
        {
            IDataset svcData = UserDiscntInfoQry.getAllDiscntByUser(input.getString("USER_ID"), ids);
            return svcData;
        }
        return returnDs;
    }

    public static IDataset queryUserSvc(IData input) throws Exception
    {
        IDataset returnDs = new DatasetList();
        String ids = input.getString("SERVICE_IDS");
        if (ids.contains(","))
        {
            String[] idArray = ids.split(",");
            for (String id : idArray)
            {
                IDataset svcData = UserSvcInfoQry.getSvcUserId(input.getString("USER_ID"), id);
                if (IDataUtil.isNotEmpty(svcData))
                {
                    returnDs.add(svcData.getData(0));
                }
            }
        }
        else
        {
            IDataset svcData = UserSvcInfoQry.getSvcUserId(input.getString("USER_ID"), ids);
            return svcData;
        }
        return returnDs;
    }

    /**
     * 订购资格查询
     * 
     * @author huangsl
     * @param data
     * @return IData
     * @throws Exception
     */
    public IData queryExchangeEligibility(IData data) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(data, "ITEM_ID");
        IDataUtil.chkParam(data, "ITEM_COUNT");
        IDataUtil.chkParam(data, "B_MOBILE");
        IDataUtil.chkParam(data, "BUSI_TYPE");
        if (!data.getString("BUSI_TYPE", "").equals("01"))
        {
            // 当前仅支持流量加油包订购资格查询!
            CSAppException.apperr(CrmUserException.CRM_USER_1162);
        }
        data.put("SERIAL_NUMBER", data.getString("B_MOBILE", ""));
        String serialNumber = data.getString("B_MOBILE", "");
        String strItemID = data.getString("ITEM_ID");
        int iItemCount = data.getInt("ITEM_COUNT");

        // 获取用户信息
        IData res = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(res))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = res.getString("USER_ID");
        data.put("USER_ID", res.getString("USER_ID", ""));

        IData resultData = new DataMap();
        resultData.put("ORG_ID", data.getString("ORG_ID"));
        resultData.put("B_MOBILE", data.getString("B_MOBILE", ""));
        resultData.put("MAX_COUNT", "0");
        resultData.put("CURRENT_COUNT", "0");

        // 获取当前item信息
        IData exchangeMobilType = new DataMap();
        IDataset exchangeMobilTypes = CommparaInfoQry.getCommpara("CSM", "4502", strItemID, this.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(exchangeMobilTypes))
        {
            exchangeMobilType = exchangeMobilTypes.getData(0);
        }
        else
        {
            resultData.put("STATUS", "04");
            resultData.put("DESC", "获取兑换的移动自有产品无数据!");
            return resultData;
        }
        String mobilType = exchangeMobilType.getString("PARA_CODE1", "");
        String mobilValue = exchangeMobilType.getString("PARA_CODE2", "");

        if ("99".equals(mobilType))
        {
            // 兑换GPRS叠加包的重复次数最大值
            IData countMap = queryGprsTotalTimes(userId);

            int discntCount = (Integer) countMap.get("CURRENT_COUNT");
            int moileTime = (Integer) countMap.get("MAX_COUNT");

            resultData.put("MAX_COUNT", String.valueOf(moileTime));
            resultData.put("CURRENT_COUNT", String.valueOf(discntCount));

            if (discntCount + iItemCount > moileTime)
            {
                resultData.put("STATUS", "01");
                resultData.put("DESC", "超过订购数量的上限，不可订购");
                return resultData;
            }
        }

        // 检查当前元素依赖的服务&优惠
        // 获取优惠生效时间
        String strStartDate, strEndDate;
        if ("99".equals(mobilType))
        {
            strStartDate = SysDateMgr.getSysDate();
            strEndDate = SysDateMgr.getLastDateThisMonth() + SysDateMgr.END_DATE;
        }
        else
        {
            strStartDate = SysDateMgr.getFirstDayOfNextMonth();
            strEndDate = SysDateMgr.getNextMonthLastDate() + SysDateMgr.END_DATE;
        }

        StringBuilder allDependSvc = new StringBuilder();// limit_tag为2对应的依赖
        int iAllDependSvc = 0;
        StringBuilder allDependDisc = new StringBuilder();
        int iAllDependDisc = 0;
        StringBuilder partDependDisc = new StringBuilder();// limit_tag为1对应的依赖
        StringBuilder partDependSvc = new StringBuilder();
        StringBuilder part2DependDisc = new StringBuilder();// limit_tag为6对应的依赖
        StringBuilder part2DependSvc = new StringBuilder();
        // 查询依赖
       // IDataset elementLimits = ElemLimitInfoQry.getElementInfoByElementId(mobilValue, "D", this.getTradeEparchyCode());
		//查询TD_B_ELEMENT_LIMIT表，改为调用产商品接口 duhj add by duhj
        IDataset elementLimits = UpcCall.qryOfferRelOfferByOfferIdWithRelTypeFilter(mobilValue, BofConst.ELEMENT_TYPE_CODE_DISCNT, "1,2,6");
        if(IDataUtil.isNotEmpty(elementLimits)){//数据转换
        	for(int i=0;i<elementLimits.size();i++){
        		IData elementLimit=elementLimits.getData(i);
        		elementLimit.put("LIMIT_TAG", elementLimit.getString("REL_TYPE"));
        		elementLimit.put("ELEMENT_TYPE_CODE_B", elementLimit.getString("OFFER_TYPE_B"));
        		elementLimit.put("ELEMENT_ID_B", elementLimit.getString("OFFER_CODE_B"));

        	}
        	
            int elemLimitSize = elementLimits.size();
            for (int i = 0; i < elemLimitSize; ++i)
            {
                IData eleRecord = elementLimits.getData(i);
                if (eleRecord.getString("LIMIT_TAG", "").equals("2"))
                {// 全部依赖
                    if (eleRecord.getString("ELEMENT_TYPE_CODE_B").equals("S"))
                    {
                        if (0 == allDependSvc.length())
                        {
                            allDependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            allDependSvc.append(",");
                            allDependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        ++iAllDependSvc;
                    }
                    else
                    {
                        if (0 == allDependDisc.length())
                        {
                            allDependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            allDependDisc.append(",");
                            allDependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        ++iAllDependDisc;
                    }
                }
                else if (eleRecord.getString("LIMIT_TAG", "").equals("1"))
                {// 部分依赖
                    if (eleRecord.getString("ELEMENT_TYPE_CODE_B").equals("S"))
                    {
                        if (0 == partDependSvc.length())
                        {
                            partDependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            partDependSvc.append(",");
                            partDependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                    }
                    else
                    {
                        if (0 == partDependDisc.length())
                        {
                            partDependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            partDependDisc.append(",");
                            partDependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                    }
                }
                else if (eleRecord.getString("LIMIT_TAG", "").equals("6"))
                {// 类似部分依赖
                    if (eleRecord.getString("ELEMENT_TYPE_CODE_B").equals("S"))
                    {
                        if (0 == part2DependSvc.length())
                        {
                            part2DependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            part2DependSvc.append(",");
                            part2DependSvc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                    }
                    else
                    {
                        if (0 == part2DependDisc.length())
                        {
                            part2DependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                        else
                        {
                            part2DependDisc.append(",");
                            part2DependDisc.append(eleRecord.getString("ELEMENT_ID_B", ""));
                        }
                    }
                }
            }
        	
        	
        	
        }
		


        // 检查部分依赖服务&优惠2
        IDataset userOrderLst;
        data.put("START_DATE", strStartDate);
        if (part2DependSvc.length() > 0)
        {
            data.put("SERVICE_IDS", new String(part2DependSvc));
            userOrderLst = queryUserSvc(data);
            if (IDataUtil.isEmpty(userOrderLst))
            {// 部分依赖的服务全部没有开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }
        if (part2DependDisc.length() > 0)
        {
            data.put("DISCNT_CODES", new String(part2DependDisc));
            userOrderLst = queryUserDiscnt(data);
            if (IDataUtil.isEmpty(userOrderLst))
            {// 部分依赖的优惠全部没有开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }

        // 检查全部依赖服务&优惠
        data.put("END_DATE", strEndDate);
        if (allDependSvc.length() > 0)
        {
            data.put("SERVICE_IDS", new String(allDependSvc));
            userOrderLst = queryUserSvc(data);
            if (userOrderLst.size() != iAllDependSvc)
            {// 服务没有全部开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }
        if (allDependDisc.length() > 0)
        {
            data.put("DISCNT_CODES", new String(allDependDisc));
            userOrderLst = queryUserDiscnt(data);
            if (userOrderLst.size() != iAllDependDisc)
            {// 优惠没有全部开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }

        // 检查部分依赖服务&优惠
        if (partDependSvc.length() > 0)
        {
            data.put("SERVICE_IDS", new String(partDependSvc));
            userOrderLst = queryUserSvc(data);
            if (IDataUtil.isEmpty(userOrderLst))
            {// 部分依赖的服务全部没有开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }
        if (partDependDisc.length() > 0)
        {
            data.put("DISCNT_CODES", new String(partDependDisc));
            userOrderLst = queryUserDiscnt(data);
            if (IDataUtil.isEmpty(userOrderLst))
            {// 部分依赖的优惠全部没有开通
                resultData.put("STATUS", "02");
                resultData.put("DESC", "依赖关系不存在,不可订购");
                return resultData;
            }
        }

        resultData.put("STATUS", "00");
        resultData.put("DESC", "可订购");

        return resultData;
    }

    /**
     * 如果没有配置兑换GPRS总数默认通过
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public IData queryGprsTotalTimes(String userId) throws Exception
    {
        IData retMap = new DataMap();
        int discntCount = 0; // 用户针对优惠叠加包订购的次数;
        int toatlLimit = 0;
        // 获取总数配置
        IDataset commparaList = CommparaInfoQry.getCommParas("CSM", "4512", "9999", "99", this.getUserEparchyCode());

        if (IDataUtil.isNotEmpty(commparaList))
        {
            toatlLimit = commparaList.getData(0).getInt("PARA_CODE3");
            retMap.put("MAX_COUNT", toatlLimit);
        }
        else
        {
            // 参数表COMMPARA-4012流量加油包最大次数没有配置
            CSAppException.apperr(CrmUserException.CRM_USER_1145);
        }

        IDataset userDiscntList = UserDiscntInfoQry.getCountGprs(userId); // 根据用户和叠加包的优惠编码查询用户订购了多少条记录
        if (IDataUtil.isNotEmpty(userDiscntList))
        {
            discntCount += ((IData) userDiscntList.get(0)).getInt("DONE_TOTAL");
        }

        // 判断台账表中是否存在该优惠且未完工的情况,只考虑新增的情况，一般的取消都是取消到月底
       // IDataset tradeDiscntList = UserDiscntInfoQry.getCountGprsDoning(userId, "0");
        IDataset tradeDiscntList = TradeDiscntInfoQry.getCountGprsDoning(userId, "0");

        if (tradeDiscntList != null && tradeDiscntList.size() > 0)
        {
            discntCount += ((IData) tradeDiscntList.get(0)).getInt("DONING_TOTAL");
        }
        retMap.put("CURRENT_COUNT", discntCount);
        return retMap;
    }

    @Override
    public void setTrans(IData input) throws Exception
    {
        input.put("SERIAL_NUMBER", input.getString("B_MOBILE", ""));
    }
}
