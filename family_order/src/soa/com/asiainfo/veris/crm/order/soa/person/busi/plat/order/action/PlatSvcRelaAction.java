
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend.PlatSvcTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

// 废弃该类 用tradeaction
public class PlatSvcRelaAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();
        IDataset relaLimitDatas = PlatInfoQry.queryRelaLimitPlatSvcs(pstd.getElementId(), psd.getOperCode(), psd.getOfficeData().getBizTypeCode());
        if (relaLimitDatas != null && relaLimitDatas.size() > 0)
        {
            int size = relaLimitDatas.size();
            for (int i = 0; i < size; i++)
            {
                IData relaConfig = relaLimitDatas.getData(i);
                String relaState = relaConfig.getString("SVC_STATE", "");
                String limitType = relaConfig.getString("LIMIT_TYPE");
                String flag = relaConfig.getString("RSRV_STR1", ""); // TRUE时，不管是PACKAGE_ID为多少，都连带退订，如果非TURE，则PACKAGE_ID为50000000才退订
                String pfTag = relaConfig.getString("RSRV_STR2", "0"); // 是否发指令，此处可能需要修改配置表TD_B_PLATSVC_LIMIT，表中原配置为01，发指令
                String infoCode = relaConfig.getString("RSRV_STR3", ""); // 需要匹配属性名
                String infoValue = relaConfig.getString("RSRV_STR4", "");// 需要匹配属性值
                String attrCode = relaConfig.getString("RSRV_STR5", ""); // 属性名 连带开服务或者平台服务，需要加的属性名
                String attrValue = relaConfig.getString("RSRV_STR6", ""); // 属性值 连带开服务或者平台服务，需要加的属性值

                if (!"".equals(infoCode) && !"".equals(infoValue))
                {
                    List<AttrTradeData> attrTradeDatas = dealPmtd.getAttrTradeDatas();
                    if (attrTradeDatas == null || attrTradeDatas.size() < 0)
                    {
                        continue;
                    }
                    else
                    {
                        int attrSize = attrTradeDatas.size();
                        boolean isFind = false;
                        for (int j = 0; j < attrSize; j++)
                        {
                            // 针对特级无线音乐会员需要绑定彩铃
                            AttrTradeData attr = attrTradeDatas.get(j);
                            if (attr.getAttrCode().equals(infoCode) && attr.getAttrValue().equals(infoValue) && (BofConst.MODIFY_TAG_ADD.equals(attr.getModifyTag())))
                            {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind)
                        {
                            continue;
                        }
                    }
                }

                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(relaConfig.getString("SVC_TYPE")))
                {
                    // 连带处理普通服务
                    if ("3".equals(limitType))
                    {

                        // 连带开
                        String modifyTag = "";

                        if (uca.getUserSvcBySvcId(relaConfig.getString("SERVICE_ID_L")).size() <= 0)
                        {
                            modifyTag = BofConst.MODIFY_TAG_ADD;
                            SvcTradeData std = new SvcTradeData();
                            String instId = SeqMgr.getInstId();
                            std.setInstId(instId);
                            std.setElementId(relaConfig.getString("SERVICE_ID_L"));
                            std.setModifyTag(modifyTag);
                            if (BofConst.MODIFY_TAG_ADD.equals(pstd.getModifyTag()))
                            {
                                std.setStartDate(pstd.getStartDate());
                            }
                            else
                            {
                                std.setStartDate(btd.getRD().getAcceptTime());
                            }
                            std.setEndDate(pstd.getEndDate());
                            std.setProductId(PlatConstants.PRODUCT_ID);
                            std.setPackageId(PlatConstants.PACKAGE_ID);
                            std.setUserId(pstd.getUserId());
                            std.setMainTag("0");
                            std.setUserIdA("-1");
                            btd.add(uca.getSerialNumber(), std);

                            // 如果配置了属性，则新增属性
                            if (StringUtils.isNotBlank(attrValue) && StringUtils.isNotBlank(attrCode))
                            {
                                String[] attrCodeArray = attrCode.split(":");
                                String[] attrValueArray = attrValue.split(":");

                                if (attrCodeArray.length != attrValueArray.length)
                                {
                                    CSAppException.apperr(PlatException.CRM_PLAT_74, "属性名和属性值配置有问题，请配置TD_S_COMPARA表的RSRV_STR5(属性名)" + "RSRV_STR6(属性值)，多个属性请用:分割");
                                }

                                for (int j = 0; j < attrCodeArray.length; j++)
                                {
                                    AttrTradeData attrTrade = new AttrTradeData();
                                    attrTrade.setAttrCode(attrCodeArray[j]);
                                    attrTrade.setAttrValue(attrValueArray[j]);
                                    attrTrade.setElementId(relaConfig.getString("SERVICE_ID_L"));
                                    attrTrade.setEndDate(pstd.getEndDate());
                                    attrTrade.setInstId(SeqMgr.getInstId());
                                    attrTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                    attrTrade.setRelaInstId(instId);
                                    attrTrade.setStartDate(pstd.getStartDate());
                                    attrTrade.setUserId(pstd.getUserId());
                                    attrTrade.setInstType(BofConst.ELEMENT_TYPE_CODE_SVC);
                                    btd.add(uca.getSerialNumber(), attrTrade);
                                }

                            }
                        }
                    }
                    else if ("4".equals(limitType))
                    {
                        // 连带关
                        List<SvcTradeData> stds = uca.getUserSvcBySvcId(relaConfig.getString("SERVICE_ID_L"));

                        if (stds.size() > 0)
                        {
                            SvcTradeData std = stds.get(0);
                            if (("TRUE".equals(flag) && "50000000".equals(std.getPackageId())) || (!"TRUE".equals(flag)))
                            {
                                if (std.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
                                {
                                    SvcTradeData newStd = std.clone();
                                    newStd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    newStd.setEndDate(pstd.getOperTime());// 立即终止
                                    btd.add(uca.getSerialNumber(), newStd);

                                }
                            }
                        }
                    }
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(relaConfig.getString("SVC_TYPE")))
                {
                    // 连带处理平台服务
                    if ("3".equals(limitType))
                    {
                        // 如果局数据不存在，则不连带开
                        try
                        {
                            PlatOfficeData.getInstance(relaConfig.getString("SERVICE_ID_L"));
                        }
                        catch (Exception e)
                        {
                            continue;
                        }

                        // 如果是批量处理默认开通，不处理
                        if ("SS.PlatBatchIBossRegIntfSVC.tradeReg".equals(CSBizBean.getVisit().getXTransCode()))
                        {
                            continue;
                        }

                        List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relaConfig.getString("SERVICE_ID_L"));
                        if (userPlatSvcs == null || userPlatSvcs.size() <= 0)
                        {
                            PlatSvcTradeData newPstd = new PlatSvcTradeData();
                            // 订购服务
                            newPstd.setElementId(relaConfig.getString("SERVICE_ID_L"));
                            newPstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            newPstd.setUserId(uca.getUserId());
                            newPstd.setBizStateCode(PlatConstants.STATE_OK);
                            newPstd.setProductId(pstd.getProductId());
                            newPstd.setPackageId(pstd.getPackageId());
                            newPstd.setStartDate(pstd.getStartDate());
                            newPstd.setEndDate(pstd.getEndDate());
                            String instId = SeqMgr.getInstId();
                            newPstd.setInstId(instId);
                            newPstd.setActiveTag(pstd.getActiveTag());// 主被动标记
                            newPstd.setOperTime(pstd.getOperTime());
                            newPstd.setPkgSeq(pstd.getPkgSeq());// 批次号，批量业务时传值
                            newPstd.setUdsum(pstd.getUdsum());// 批次数量
                            newPstd.setIntfTradeId(pstd.getIntfTradeId());
                            newPstd.setOperCode(PlatConstants.OPER_ORDER);
                            newPstd.setOprSource(pstd.getOprSource());
                            newPstd.setIsNeedPf(pfTag);
                            PlatSvcTrade.dealFirstTime(newPstd, uca); // 处理首次订购时间，连带开的可能首次订购时间为空
                            btd.add(uca.getSerialNumber(), newPstd);

                            // 如果配置了属性，则新增属性
                            // 如果配置了属性，则新增属性
                            if (StringUtils.isNotBlank(attrValue) && StringUtils.isNotBlank(attrCode))
                            {
                                String[] attrCodeArray = attrCode.split(":");
                                String[] attrValueArray = attrValue.split(":");

                                if (attrCodeArray.length != attrValueArray.length)
                                {
                                    CSAppException.apperr(PlatException.CRM_PLAT_74, "属性名和属性值配置有问题，请配置TD_S_COMPARA表的RSRV_STR5(属性名)" + "RSRV_STR6(属性值)，多个属性请用:分割");
                                }

                                for (int j = 0; j < attrCodeArray.length; j++)
                                {
                                    AttrTradeData attrTrade = new AttrTradeData();
                                    attrTrade.setAttrCode(attrCodeArray[j]);
                                    attrTrade.setAttrValue(attrValueArray[j]);
                                    attrTrade.setElementId(relaConfig.getString("SERVICE_ID_L"));
                                    attrTrade.setEndDate(pstd.getEndDate());
                                    attrTrade.setInstId(SeqMgr.getInstId());
                                    attrTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                    attrTrade.setRelaInstId(instId);
                                    attrTrade.setStartDate(pstd.getStartDate());
                                    attrTrade.setUserId(pstd.getUserId());
                                    attrTrade.setInstType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
                                    btd.add(uca.getSerialNumber(), attrTrade);
                                }
                            }
                        }
                        else
                        {
                            // 如果本来就存在，但是是暂停状态，则恢复
                            PlatSvcTradeData userPlatSvc = userPlatSvcs.get(0);
                            if ("N".equals(userPlatSvc.getBizStateCode()))
                            {
                                userPlatSvc.setOperCode(PlatConstants.OPER_RESTORE);
                                userPlatSvc.setStartDate(pstd.getStartDate());
                                userPlatSvc.setEndDate(pstd.getEndDate());
                                userPlatSvc.setBizStateCode(PlatConstants.STATE_OK);
                                userPlatSvc.setOperTime(btd.getRD().getAcceptTime());
                                userPlatSvc.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                userPlatSvc.setOprSource(pstd.getOprSource());
                                userPlatSvc.setIsNeedPf(pfTag);
                                btd.add(uca.getSerialNumber(), userPlatSvc);
                            }
                        }
                    }
                    else if ("4".equals(limitType))
                    {
                        List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relaConfig.getString("SERVICE_ID_L"));
                        if (userPlatSvcs != null && userPlatSvcs.size() > 0)
                        {
                            PlatSvcTradeData userPlatSvc = userPlatSvcs.get(0).clone();
                            if (BofConst.MODIFY_TAG_USER.equals(userPlatSvc.getModifyTag()))
                            {
                                PlatSvcTradeData newPstd = userPlatSvc.clone();
                                // 订购服务
                                newPstd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                newPstd.setBizStateCode(PlatConstants.STATE_CANCEL);
                                newPstd.setEndDate(pstd.getOperTime());
                                newPstd.setActiveTag(pstd.getActiveTag());// 主被动标记
                                newPstd.setOperTime(pstd.getOperTime());
                                newPstd.setPkgSeq(pstd.getPkgSeq());// 批次号，批量业务时传值
                                newPstd.setUdsum(pstd.getUdsum());// 批次数量
                                newPstd.setIntfTradeId(pstd.getIntfTradeId());
                                newPstd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                                newPstd.setOprSource(pstd.getOprSource());
                                newPstd.setIsNeedPf(pfTag);
                                btd.add(uca.getSerialNumber(), newPstd);
                            }
                        }
                    }
                }
            }
        }
    }

}
