
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.bizcommon.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.ailk.bizcommon.util.Clone;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeBroadbandUserElement extends ChangeUserElement
{

    public ChangeBroadbandUserElement()
    {

    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegVispDataOther();
    }

    public IData getTradeUserExtendData() throws Exception
    {

        IData userExtenData = super.getTradeUserExtendData();

        // 存产品产品信息到user表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);
        if (IDataUtil.isNotEmpty(productParams))
        {
            userExtenData.put("RSRV_STR7", productParams.getString("NOTIN_DETMANAGER_INFO"));
            userExtenData.put("RSRV_STR8", productParams.getString("NOTIN_DETMANAGER_PHONE"));
            userExtenData.put("RSRV_STR9", productParams.getString("NOTIN_DETADDRESS"));
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        return userExtenData;

    }

    public void infoRegVispDataOther() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();

        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "N002");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        for (int j = 0; j < attrInternet.size(); j++)
        {
            IData internet = attrInternet.getData(j);
            String numberCode = internet.getString("pam_NOTIN_OPER_TAG");
            String flag = internet.getString("tag", "");

            if(IDataUtil.isNotEmpty(userOther)) {
                for (int i = 0; i < userOther.size(); i++)
                {
                    IData vispUser = userOther.getData(i);
                    String lineNumberCode = vispUser.getString("RSRV_VALUE");
                    if (numberCode.equals(lineNumberCode))
                    {
                        /**
                        if ("1".equals(flag))
                        {
                            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            vispUser.put("UPDATE_TIME", getAcceptTime());
                            vispUser.put("END_DATE", getAcceptTime());

                            dataset.add(vispUser);
                        }*/
                        
                        if ("2".equals(flag))
                        {
                            IData newVispUser  = (IData) Clone.deepClone(vispUser);
                            
                            //对原来的数据删除，并截止掉
                            vispUser.put("UPDATE_TIME", getAcceptTime());
                            vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            dataset.add(vispUser);
                            
                            //新增一条新的数据
                            newVispUser.put("RSRV_VALUE", String.valueOf(internet.getString("pam_NOTIN_OPER_TAG")));
                            // 宽带数目
                            newVispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_NUM"));
                            // 月租费
                            //newVispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE"));
                            // 安装调测费
                            newVispUser.put("RSRV_STR3", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                            // 一次性通信服务费
                            newVispUser.put("RSRV_STR4", internet.getString("pam_NOTIN_ONE_COST"));
                            
                            newVispUser.put("INST_ID", SeqMgr.getInstId());                        
                            newVispUser.put("START_DATE", getAcceptTime());
                            newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
                            newVispUser.put("UPDATE_TIME", getAcceptTime());
                            newVispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            
                            IData broadData = new DataMap();
                            broadData.put("USER_ID", reqData.getUca().getUser().getUserId());
                            broadData.put("RSRV_VALUE_CODE", "N003");
                            IDataset userBroadInfo = TradeOtherInfoQry.queryUserOtherInfoByUserId(broadData);
                            if (IDataUtil.isNotEmpty(userBroadInfo)){
                                for (int k = 0; k < userBroadInfo.size(); k++)
                                {
                                    IData userBroad = userBroadInfo.getData(k);
                                    String rsrvValue = userBroad.getString("RSRV_VALUE","");
                                    if("M4".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_4"));
                                    } else if("M8".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_8"));
                                    } else if("M10".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_10"));
                                    } else if("M12".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_12"));
                                    } else if("M20".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_20"));
                                    } else if("M50".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_50"));
                                    } else if("M100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_100"));
                                    } else if("CZM20".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZ20"));
                                    } else if("CZM50".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZ50"));
                                    } else if("CZM100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZ100"));
                                    } else if("CZM200".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZ200"));
                                    } else if("JCM20".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JC20"));
                                    } else if("JCM50".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JC50"));
                                    } else if("JCM100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JC100"));
                                    } else if("JCM200".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JC200"));
                                    } else if("JYM50".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JY50"));
                                    } else if("JYM100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JY100"));
                                    } else if("JYM200".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JY200"));
                                    } else if("JYM500".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_JY500"));
                                    } else if("ZZM100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ100"));
                                    } else if("ZZM200".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ200"));
                                    } else if("ZZM500".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ500"));
                                    } else if("ZZM1000".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ1000"));
                                    } else if("CZSXM20".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX20"));
                                    } else if("CZSXM50".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX50"));
                                    } else if("SWSXM100".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX100"));
                                    } else if("SWSXM200".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX200"));
                                    } else if("SWSXM500".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX500"));
                                    } else if("QYSWM1000".equals(rsrvValue)){
                                        userBroad.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE_QYSW1000"));
                                    }
                                    userBroad.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                    userBroad.put("UPDATE_TIME", getAcceptTime());
                                    dataset.add(userBroad);
                                }
                            }
                            
                            dataset.add(newVispUser);
                            
                            //优化：历史数据只有4M、8M、10M、12、20、50、100M宽带月租输入项，如果集团产品资料变更时想填写新增的宽带月租项，则修改不了
                            this.specDealTradeOtherData(dataset, internet);
                        }
                    }
                }
            }

            /**
            if ("0".equals(flag))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                vispUsers.put("RSRV_VALUE_CODE", "N002");
                vispUsers.put("UPDATE_TIME", getAcceptTime());

                vispUsers.put("RSRV_VALUE", String.valueOf(internet.getString("pam_NOTIN_OPER_TAG")));
                // 宽带数目
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_NUM"));
                // 月租费
                vispUsers.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE"));
                // 安装调测费
                vispUsers.put("RSRV_STR3", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                // 一次性通信服务费
                vispUsers.put("RSRV_STR4", internet.getString("pam_NOTIN_ONE_COST"));

                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());

                vispUsers.put("PROCESS_TAG", ""); // 处理标志map.getString("TRADE_TAG", "")
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }
            */
            
        }
        
        addTradeOther(dataset);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));
    }
    /**
     * REQ201805290016关于小微宽带资费办理优化的需求
     * 针对原先办理只选择了商务宽带产品资费的用户，不需要注销可以直接选择增加小微宽带资费信息
     * 原来没有的带宽就要新增了
     * @param otherDs
     * @throws Exception
     * @author chenzg
     * @date 2018-6-11
     */
    private void specDealTradeOtherData(IDataset otherDs, IData internet) throws Exception{
    	if(IDataUtil.isNotEmpty(otherDs)){
    		IDataset dstmp = DataHelper.filter(otherDs, "RSRV_VALUE_CODE=N003");
    		if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_4")) 
    				&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M4"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_4"),"4");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_8"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M8"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_8"),"8");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_10"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M10"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_10"),"10");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_12"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M12"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_12"),"12");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_20"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M20"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_20"),"20");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_50"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M50"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_50"),"50");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=M100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_100"),"100");
            }     
            
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ20"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZM20"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ20"),"CZ20");
            } 
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ50"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZM50"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ50"),"CZ50");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZM100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ100"),"CZ100");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ200"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZM200"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ200"),"CZ200");
            }
            
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC20"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JCM20"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JC20"),"JC20");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC50"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JCM50"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JC50"),"JC50");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JCM100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JC100"),"JC100");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC200"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JCM200"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JC200"),"JC200");
            }
            
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY50"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JYM50"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JY50"),"JY50");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JYM100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JY100"),"JY100");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY200"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JYM200"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JY200"),"JY200");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY500"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=JYM500"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_JY500"),"JY500");
            }
            
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=ZZM100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ100"),"ZZ100");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ200"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=ZZM200"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ200"),"ZZ200");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ500"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=ZZM500"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ500"),"ZZ500");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ1000"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=ZZM1000"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ1000"),"ZZ1000");
            }
            
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX20"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZSXM20"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX20"),"CZSX20");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX50"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=CZSXM50"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX50"),"CZSX50");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX100"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=SWSXM100"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX100"),"SWSX100");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX200"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=SWSXM200"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX200"),"SWSX200");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX500"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=SWSXM500"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX500"),"SWSX500");
            }
            if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_QYSW1000"))
            		&& IDataUtil.isEmpty(DataHelper.filter(dstmp, "RSRV_VALUE=QYSWM1000"))){
                this.addBroadBandFee(otherDs,internet.getString("pam_NOTIN_MONTHLY_FEE_QYSW1000"),"QYSW1000");
            }
            
    	}
    }
    

    /**
     * 生成宽带包月费台帐
     * @param otherSet
     * @param rsrv2Value
     * @param broadCode
     * @throws Exception
     * @author chenzg
     * @date 2018-6-11
     */
    private void addBroadBandFee(IDataset otherSet,String rsrv2Value,String broadCode) throws Exception{
        IData data = new DataMap();
        data.put("INST_ID", SeqMgr.getInstId());  
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("RSRV_VALUE_CODE", "N003");
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        
        if("4".equals(broadCode)){
            data.put("RSRV_VALUE", "M4");//4M宽带
        } else if("8".equals(broadCode)){
            data.put("RSRV_VALUE", "M8");//8M宽带
        } else if("10".equals(broadCode)){
            data.put("RSRV_VALUE", "M10");//10M宽带
        } else if("12".equals(broadCode)){
            data.put("RSRV_VALUE", "M12");//12M宽带
        } else if("20".equals(broadCode)){
            data.put("RSRV_VALUE", "M20");//20M宽带
        } else if("50".equals(broadCode)){
            data.put("RSRV_VALUE", "M50"); //50M宽带
        } else if("100".equals(broadCode)){
            data.put("RSRV_VALUE", "M100");//100M宽带
        } else if("CZ20".equals(broadCode)){
            data.put("RSRV_VALUE", "CZM20");//超值版20M(上行10M)宽带月租
        } else if("CZ50".equals(broadCode)){
            data.put("RSRV_VALUE", "CZM50");//超值版50M(上行10M)宽带月租
        } else if("CZ100".equals(broadCode)){
            data.put("RSRV_VALUE", "CZM100");//超值版100M(上行10M)宽带月租
        } else if("CZ200".equals(broadCode)){
            data.put("RSRV_VALUE", "CZM200");//超值版200M(上行10M)宽带月租
        } else if("JC20".equals(broadCode)){
            data.put("RSRV_VALUE", "JCM20");//基础版20M(上行20M)宽带月租
        } else if("JC50".equals(broadCode)){
            data.put("RSRV_VALUE", "JCM50");//基础版50M(上行20M)宽带月租
        } else if("JC100".equals(broadCode)){
            data.put("RSRV_VALUE", "JCM100");//基础版100M(上行20M)宽带月租
        } else if("JC200".equals(broadCode)){
            data.put("RSRV_VALUE", "JCM200");//基础版200M(上行20M)宽带月租
        } else if("JY50".equals(broadCode)){
            data.put("RSRV_VALUE", "JYM50");//精英版50M(上行50M)宽带月租
        } else if("JY100".equals(broadCode)){
            data.put("RSRV_VALUE", "JYM100");//精英版100M(上行50M)宽带月租
        } else if("JY200".equals(broadCode)){
            data.put("RSRV_VALUE", "JYM200");//精英版200M(上行50M)宽带月租
        } else if("JY500".equals(broadCode)){
            data.put("RSRV_VALUE", "JYM500");//精英版500M(上行50M)宽带月租
        } else if("ZZ100".equals(broadCode)){
            data.put("RSRV_VALUE", "ZZM100");//至尊版100M(上行100M)宽带月租
        } else if("ZZ200".equals(broadCode)){
            data.put("RSRV_VALUE", "ZZM200");//至尊版200M(上行100M)宽带月租
        } else if("ZZ500".equals(broadCode)){
            data.put("RSRV_VALUE", "ZZM500");//至尊版500M(上行100M)宽带月租 
        } else if("ZZ1000".equals(broadCode)){
            data.put("RSRV_VALUE", "ZZM1000");//至尊版1000M(上行100M)宽带月租
        } else if("CZSX20".equals(broadCode)){
            data.put("RSRV_VALUE", "CZSXM20");//超值版(下行带宽20M,上行带宽20M)月租
        } else if("CZSX50".equals(broadCode)){
            data.put("RSRV_VALUE", "CZSXM50");//超值版(下行带宽50M,上行带宽50M)月租
        } else if("SWSX100".equals(broadCode)){
            data.put("RSRV_VALUE", "SWSXM100");//商务版(下行带宽100M,上行带宽50M)月租
        } else if("SWSX200".equals(broadCode)){
            data.put("RSRV_VALUE", "SWSXM200");//商务版(下行带宽200M,上行带宽100M)月租
        } else if("SWSX500".equals(broadCode)){
            data.put("RSRV_VALUE", "SWSXM500");//商务版(下行带宽500M,上行带宽250M)月租
        } else if("QYSW1000".equals(broadCode)){
            data.put("RSRV_VALUE", "QYSWM1000");//企业宽带商务版1000M资费
        }
        
        data.put("RSRV_STR2", rsrv2Value);
        otherSet.add(data);
    }
}
