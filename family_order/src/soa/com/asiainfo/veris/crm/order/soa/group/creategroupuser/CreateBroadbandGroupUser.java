
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateBroadbandGroupUser extends CreateGroupUser
{
    /**
     * 构造函数
     */
    public CreateBroadbandGroupUser()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        regOtherInfoTrade();

    }

    protected void actTradeUser() throws Exception
    {
        UserTradeData userTradeData = reqData.getUca().getUser();
        // 用户
        if (userTradeData != null)
        {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);
            userTradeData.setUserPasswd("123456");
            
            if (IDataUtil.isNotEmpty(productParams))
            {
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_DETMANAGER_INFO"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETMANAGER_PHONE"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_DETADDRESS"));
                
                //chenhh6新增  修改新界面入参
                String str = productParams.getString("DEPART_ID");
                if (str !=  null && !"".equals(str)) {
                	String departIds[] = str.split(" ");
                    if (departIds.length == 1) {
                    	String departCode = departIds[0];
                    	IDataset departList = BBossAttrQry.qryBBossDepartCode(departCode);
                    	if (departList.size() > 0) {
                    		IData depaerData = departList.getData(0);
                    		str = depaerData.getString("DEPART_ID") +" "+ depaerData.getString("DEPART_NAME")
                    				+ " " + depaerData.getString("DEPART_CODE");
    					}
    				}
				}
                
                userTradeData.setRsrvStr1(str);
            }
        }
        super.actTradeUser();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    public void regOtherInfoTrade() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();
        IData data = null;
        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        if (null != attrInternet && attrInternet.size() > 0)
        {
            for (int i = 0; i < attrInternet.size(); i++)
            {
                data = new DataMap();
                IData internet = attrInternet.getData(i);

                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "N002");
                data.put("STATE", "ADD");
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());

                data.put("RSRV_VALUE", internet.getString("pam_NOTIN_OPER_TAG"));
                // 宽带数目
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_NUM"));
                // 月租费
                //data.put("RSRV_STR2", internet.getString("pam_NOTIN_MONTHLY_FEE"));
                // 安装调测费
                data.put("RSRV_STR3", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                // 一次性通信服务费
                data.put("RSRV_STR4", internet.getString("pam_NOTIN_ONE_COST"));

                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_4"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_4"),"4");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_8"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_8"),"8");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_10"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_10"),"10");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_12"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_12"),"12");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_20"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_20"),"20");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_50"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_50"),"50");
                    
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_100"),"100");
                    
                }     
                
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ20"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ20"),"CZ20");
                } 
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ50"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ50"),"CZ50");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ100"),"CZ100");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZ200"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZ200"),"CZ200");
                }
                
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC20"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JC20"),"JC20");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC50"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JC50"),"JC50");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JC100"),"JC100");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JC200"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JC200"),"JC200");
                }
                
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY50"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JY50"),"JY50");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JY100"),"JY100");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY200"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JY200"),"JY200");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_JY500"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_JY500"),"JY500");
                }
                
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ100"),"ZZ100");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ200"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ200"),"ZZ200");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ500"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ500"),"ZZ500");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ1000"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_ZZ1000"),"ZZ1000");
                }
                
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX20"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX20"),"CZSX20");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX50"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_CZSX50"),"CZSX50");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX100"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX100"),"SWSX100");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX200"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX200"),"SWSX200");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX500"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_SWSX500"),"SWSX500");
                }
                if(StringUtils.isNotEmpty(internet.getString("pam_NOTIN_MONTHLY_FEE_QYSW1000"))){
                    this.addBroadBandFee(dataset,internet.getString("pam_NOTIN_MONTHLY_FEE_QYSW1000"),"QYSW1000");
                }
                
                dataset.add(data);
            }

            addTradeOther(dataset);
        }
    }

    /**
     * 
     * @param otherSet
     * @param rsrvValue
     * @param broadCode
     * @throws Exception
     */
    private void addBroadBandFee(IDataset otherSet,String rsrv2Value,String broadCode) throws Exception{
        
            IData data = new DataMap();
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("RSRV_VALUE_CODE", "N003");
            data.put("STATE", "ADD");
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
    
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));

    }

}
