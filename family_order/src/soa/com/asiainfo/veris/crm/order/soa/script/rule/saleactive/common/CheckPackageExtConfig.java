package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.PackageElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.TroopMemberInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.WideChangeUserCheckBean;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.SaleActiveBean;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;


/**
 * 
 3.0.出账标记才能办理 rsrv_str11 3.1.小于年份才能办理 rsrv_str12 3.2.大于年份才能办理 rsrv_str13 3.3.客户级别才能办理 rsrv_str14 rsrv_str20
 * 3.4.需要优惠才能办理 rsrv_str15 rsrv_str21 3.5.满足品牌才能办理 rsrv_str16 rsrv_str22 3.6.实名制才能办理 rsrv_str18 3.7.网龄区间判断 rsrv_str19
 * 3.8.个人宽带装机工单是否完工才能办理 tag_set1[3] 3.9.需要宽带服务才能办理 rsrv_str24 3.10.需要在对应目标客户群才能办理 rsrv_str21 --3.11 存在某优惠不能办理该营销包
 * ---param_attr= '1549'
 * 
 * @author Mr.Z
 */
public class CheckPackageExtConfig extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 3024917760355623778L;

    private static Logger logger = Logger.getLogger(CheckPackageExtConfig.class);

    private boolean filterUserDiscntByDate(IDataset userDiscntDataset, String discntCode, String filterType) throws Exception
    {
        for (int index = 0, size = userDiscntDataset.size(); index < size; index++)
        {
            IData userDiscnt = userDiscntDataset.getData(index);
            String userDiscntCode ="|" + userDiscnt.getString("DISCNT_CODE") + "|";
            if ("1".equals(filterType))
            {
                String discntStartDate = userDiscnt.getString("START_DATE");
                String sysDate = SysDateMgr.getSysTime();
                if (discntCode.indexOf(userDiscntCode)>=0 && discntStartDate.compareTo(sysDate) < 0)
                {
                    return true;
                }
            }

            if ("2".equals(filterType))
            {
                String discntEndDate = userDiscnt.getString("END_DATE");
                String firstDayNextMoth = SysDateMgr.getFirstDayOfNextMonth();

                if (discntCode.indexOf(userDiscntCode)>=0 && discntEndDate.compareTo(firstDayNextMoth) > 0)
                {
                    return true;
                }
            }

            if ("3".equals(filterType))
            {
                if (discntCode.indexOf(userDiscntCode)>=0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getExsitsSaleActive(IDataset userSaleActives, String packageId) throws Exception
    {
        for (int i = 0, size = userSaleActives.size(); i < size; i++)
        {
            IData userSaleActive = userSaleActives.getData(i);
            if (packageId.equals(userSaleActive.getString("PACKAGE_ID")))
            {
                return true;
            }

        }
        return false;
    }

    //@Override
    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPackageExtConfig() >>>>>>>>>>>>>>>>>>"+databus);
        }

        String userId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");
        String packageId = databus.getString("PACKAGE_ID");
        
//        IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE"));
//        IDataset packageExtInfos = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        IData packageExtData = databus.getData("PM_OFFER_EXT");//packageExtInfos.getData(0);
//        IData productInfo = ProductInfoQry.queryAllProductInfo(productId);
        
        IData productInfo = databus.getData("PM_CATALOG");
        String productTagSet = productInfo.getString("TAG_SET");//SaleActiveBreUtil.getProductTagSet(productId);
        String actionType = databus.getString("ACTION_TYPE");

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>用户不归属于目标客户群
        String condFactor1 = packageExtData.getString("COND_FACTOR1");
        //判断宽带畅想服务特权活动
        if("66000310".equals(productId)){
            //是否宽带用户查询
            String serialNumber = databus.getString("SERIAL_NUMBER");
            String kdSerialNumber = serialNumber;
            if (!serialNumber.startsWith("KD")) {
                kdSerialNumber = "KD_" + serialNumber;
            }
            IDataset isWideNetUserIData = WidenetInfoQry.getUserWidenetInfoBySerialNumber(kdSerialNumber);
                if(IDataUtil.isEmpty(isWideNetUserIData)){
                
                	  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200515, "尚未办理移动宽带，申请家庭宽带成功安装后即可参与。");	
                }
                else{
                	String isFttp = isWideNetUserIData.getData(0).getString("RSRV_STR2");
                	if(!"3".equals(isFttp)){
                		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2020515, "非FTTH网络，该设备地址暂不支持提速。");
                	}
                	String userid = isWideNetUserIData.getData(0).getString("USER_ID");
                	IDataset isWideNetFowrd = UserProductInfoQry.queryWidenetFoward(userid);
                	   if(IDataUtil.isNotEmpty(isWideNetFowrd)){
                           
                     	  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200516, "您有预约生效的宽带产品变更，请取消宽带产品变更再办理提速活动!");	
                     }
                	
                }
        }
        
        if(StringUtils.isNotBlank(condFactor1) && !databus.getBoolean("IS_TROOP_MEMBER")
                && productTagSet.length()>8 && "1".equals(productTagSet.substring(7, 8)))
        {
        	 boolean isTargetMember = false;
             IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdUserId(condFactor1, userId);
             if (IDataUtil.isNotEmpty(troopMemberSet))
             {
                 isTargetMember = true;
             }
             if (!isTargetMember)
             {
                 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402400, "用户不归属于目标客户群， 不能办理该业务！");
             }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>只有集团成员或VIP客户才能办理该业务
        String condFactor2 = packageExtData.getString("COND_FACTOR2");
        if (StringUtils.isNotBlank(condFactor2) && "1".equals(condFactor2))
        {
            boolean isGropMember = false;
            if (IDataUtil.isNotEmpty(BreQry.getGropMemberInfoByUserId(userId)))
            {
                isGropMember = true;
            }

            String vipClassId = databus.getString("VIP_CLASS_ID");
            boolean isVipCust = "|1|2|3|4|".indexOf(vipClassId) > -1 ? true : false;

            if (!isGropMember && !isVipCust)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402401, "只有集团成员或VIP客户才能办理该业务！");
            }
        }
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>只有集团关键人才能办理该业务
        
        //包互斥校验，用户办理过活动产品包，不能办理当前活动包！
        String limit_610 = packageExtData.getString("LIMIT_610");
        if (StringUtils.isNotBlank(limit_610))
        {
        	IDataset userSaleActives = SaleActiveInfoQry.getUserSaleActiveInfoByPRODUCTIDPACKAGEID(databus.getString("USER_ID"),productId,packageId);
        	if (IDataUtil.isNotEmpty(userSaleActives))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200512, limit_610);
            }
        }
        
        //用户当前在活动包协议期内, 不能办理该活动包!
        String limit_61 = packageExtData.getString("LIMIT_61");
        if (StringUtils.isNotBlank(limit_61))
        {
        	IDataset userSaleActives = SaleActiveInfoQry.getVaildSaleActiveByPackageId(databus.getString("USER_ID"),productId,packageId);
        	if (IDataUtil.isNotEmpty(userSaleActives))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200513, limit_61);
            }
        }
        
        //用户存在指定生效的平台服务, 不能办理该活动包!
        String limit_611 = packageExtData.getString("LIMIT_611");
        if (StringUtils.isNotBlank(limit_611))
        {
        	IDataset platSvcInfos = Dao.qryByCodeParser("TF_F_USER_PLATSVC", "SEL_VAILDBIZTYPE_BY_USERID", databus);
        	for(int i=0;i<platSvcInfos.size();i++){
        		IData platSvcInfo = platSvcInfos.getData(i);
        		String serviceId = platSvcInfo.getString("SERVICE_ID");
        		if(limit_611.contains(serviceId)){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200513, "用户已存在有效的"+serviceId+"平台服务!");
        			break;
        		}
        	}
        }
        
        String MOBILE_HOME = packageExtData.getString("MOBILE_HOME");
        if(StringUtils.isNotBlank(MOBILE_HOME)){
        	int monthBegin = Integer.parseInt("12");
            int openMoths = SysDateMgr.monthInterval(SysDateMgr.getSysTime(), databus.getString("OPEN_DATE"));
            //全球通标识  1、全球通银卡 2、全球通金卡  3、全球通白金卡 4、全球通钻石卡（非终身）5、全球通终身钻石卡 6、全球通客户（体验）7、其他非全球通用户
            IData param = new DataMap();
        	param.put("SERIAL_NUMBER", databus.getString("SERIAL_NUMBER"));
        	IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", param);
        	String UserGSMState = "7";
        	if(DataUtils.isNotEmpty(roamTag))
        	{
        		UserGSMState = roamTag.getData(0).getString("USER_CLASS","7");
        	}
        	
        	IDataset userSaleActives = SaleActiveInfoQry.getVaildSaleActiveByPackageId(databus.getString("USER_ID"),null,null);
        	int n=0;
        	for(int i=0;i<userSaleActives.size();i++){
        		String packageId2 = userSaleActives.getData(i).getString("PACKAGE_ID");
        		if(MOBILE_HOME.contains(packageId2)){
        			n++;
        		}
        	}
        	if(openMoths <= monthBegin){
        		if(n>0){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 202005141, "网龄小于或等于一年的用户最多办理1部！");
        		}
        	}else{
        		if(n>2&&!("2".equals(UserGSMState)||"3".equals(UserGSMState)||"4".equals(UserGSMState)||"5".equals(UserGSMState))){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 202005141, "网龄超过一年且不是全球通金卡及以上的用户最多办理3部！");
        		}
        	}
        	
        }
        
        String rsrvStr17 = packageExtData.getString("RSRV_STR17");
        if (StringUtils.isNotBlank(rsrvStr17))
        {
            boolean isGroupSpecUser = false;
            IDataset groupMemberSet = BreQry.getGropMemberInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(groupMemberSet))
            {
                for (int index = 0, size = groupMemberSet.size(); index < size; index++)
                {
                    IData groupData = groupMemberSet.getData(index);
                    String memberKind = groupData.getString("MEMBER_KIND");
                    if (rsrvStr17.indexOf(memberKind) > -1)
                    {
                        isGroupSpecUser = true;
                        break;
                    }
                }
            }

            if (!isGroupSpecUser)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402402, "只有集团关键人才能办理该业务！");
            }
        }

        IDataset specProductDataset = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "530", "0", productId, databus.getString("EPARCHY_CODE"));
        if ("".equals(actionType) && IDataUtil.isNotEmpty(specProductDataset) && "530".equals(databus.getString("VIP_TYPE_ID")))
        {
            return true;
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>出账标记才能办理

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR11")))
        {
            String rsrvStr11 = packageExtData.getString("RSRV_STR11");
            String acctTag = databus.getString("ACCT_TAG", "");
            if (rsrvStr11.indexOf(acctTag) < 0)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402403, "正常出账用户不能办理！");
            }
            else
            {
                if (rsrvStr11.startsWith("!"))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402403, "正常出账用户不能办理！");
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>小于年份的条件
        // TODO 修改RSRV_STR12的配置，全部修改为YYYY-MM-DD的格式

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR12")))
        {
            String rsrvStr12 = packageExtData.getString("RSRV_STR12");
            String opendate = databus.getString("OPEN_DATE", "");
            if (opendate.compareTo(rsrvStr12) >= 0)
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402404, "开户在[" + rsrvStr12 + "]年后的用户不能办理该业务！！");
                }
            }
            else
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>大于年份的条件

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR13")))
        {
            String rsrvStr13 = packageExtData.getString("RSRV_STR13");
            String opendate = databus.getString("OPEN_DATE", "");
            if (opendate.compareTo(rsrvStr13) <= 0)
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402405, "开户在[" + rsrvStr13 + "]年前的用户不能办理该业务！！");
                }
            }
            else
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>客户级别的条件

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR14")))
        {
            IDataset custVipset = databus.getDataset("TF_F_CUST_VIP");
            String custVipClass = "XXXX";
            if (IDataUtil.isNotEmpty(custVipset))
            {
                IData custVip = custVipset.getData(0);
                custVipClass = custVip.getString("VIP_TYPE_CODE", "-1") + "#" + custVip.getString("VIP_CLASS_ID", "-1");
            }
            String rsrvStr14 = packageExtData.getString("RSRV_STR14");
            if (rsrvStr14.indexOf(custVipClass) < 0)
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402406, "非VIP" + packageExtData.getString("RSRV_STR20", "") + "客户不能办理该业务！");
                }
            }
            else
            {
            	//获取commpara配置，查看是否是大客户积分的营销活动产品
            	IDataset Commpara523 = CommparaInfoQry.getCommparaCode1("CSM", "523", productId, databus.getString("EPARCHY_CODE"));
            	if((IDataUtil.isNotEmpty(Commpara523)))//如果是
            	{
            		//判断星级是否是5、6、7，如果是则不通过
            		String userCreditClass = SaleActiveUtil.queryUserCreditClass(databus.getString("USER_ID"));
            		if("5".equals(userCreditClass) || "6".equals(userCreditClass) || "7".equals(userCreditClass))
            		{
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402406, "存在用户星级匹配的包，优先星级！");
            		}
            	}
            	
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }
        }

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>他判断条件

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR18")))
        {
            String rsrvStr18 = packageExtData.getString("RSRV_STR18");

            if ("1".equals(rsrvStr18.substring(0, 1)))// 实名制的条件
            {
                IDataset customerSet = databus.getDataset("TF_F_CUSTOMER");
                IData customer = customerSet.getData(0);

                if (!"1".equals(customer.getString("IS_REAL_NAME")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402407, "非实名制客户不能办理该业务！");
                }
                else
                {
                    if ("1".equals(packageExtData.getString("RSRV_STR10")))
                    {
                        return true;
                    }
                }
            }
            else if (rsrvStr18.length() > 1 && "1".equals(rsrvStr18.substring(1, 2)))// 网厅开户条件
            {
                IData userRes0 = new DataMap();

                IDataset userRes = databus.getDataset("TF_F_USER_RES");
                if (IDataUtil.isNotEmpty(userRes))
                {
                    for (int index = 0, size = userRes.size(); index < size; index++)
                    {
                        IData userResData = userRes.getData(index);
                        if ("0".equals(userResData.getString("RES_TYPE_CODE")))
                        {
                            userRes0 = userResData;
                            break;
                        }
                    }
                }

                if (!"1".equals(userRes0.getString("RSRV_TAG2")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402408, "非网厅开户客户不能办理该业务！");
                }
                else
                {
                    if ("1".equals(packageExtData.getString("RSRV_STR10")))
                    {
                        return true;
                    }
                }
            }
            else if (rsrvStr18.length() > 2 && "1".equals(rsrvStr18.substring(2, 3)))// 亲亲网组建条件判断
            {
                // ----1、判断亲亲网组建次数是否超标---------
                IDataset qqwCommparaSet = CommparaInfoQry.getCommparaByParaAttr("CSM", "1527", databus.getString("EPARCHY_CODE"));
                if (IDataUtil.isNotEmpty(qqwCommparaSet))
                {
                    String paramDate = qqwCommparaSet.getData(0).getString("PARA_CODE1");
                    String createNum = qqwCommparaSet.getData(0).getString("PARA_CODE2", "0");
                    String memberNum = qqwCommparaSet.getData(0).getString("PARA_CODE3", "0");

                    IDataset allRelationDataset = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userId, "45", "1");
                    int relationNumber = 0;
                    if (IDataUtil.isNotEmpty(allRelationDataset))
                    {
                        for (int index = 0, size = allRelationDataset.size(); index < size; index++)
                        {
                            String startDate = allRelationDataset.getData(index).getString("START_DATE");
                            if (startDate.compareTo(paramDate) > 0)
                            {
                                relationNumber++;
                            }
                        }
                    }

                    if (relationNumber > Integer.parseInt(createNum))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1402409, "该号码组建亲亲网超过限制次数！");
                    }

                    // ----2、判断亲亲网成员是否达标--------------
                    int memberNumber = CParamQry.getRelationMemberNumber(userId, "45");
                    if (memberNumber < Integer.parseInt(memberNum))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024010, "该号码组建的亲亲网成员数量不足！");
                    }
                }
            }
            else if (rsrvStr18.length() > 3 && "1".equals(rsrvStr18.substring(3, 4)))// 亲亲网主号才能办理
            {
                IDataset relationSet = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "45", "1");
                if (relationSet.isEmpty())
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024011, "亲亲网主号才能办理该包！");
                }
            }
            else if (rsrvStr18.length() > 4 && "1".equals(rsrvStr18.substring(4, 5)))// 统一付费主号才能办理
            {
                IDataset relationSet = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "56", "1");
                if (IDataUtil.isEmpty(relationSet))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024012, "统一付费主号才能办理该包！");
                }
            }
            else if (rsrvStr18.length() > 5 && "1".equals(rsrvStr18.substring(5, 6)))// 判断积分是否足够扣减
            {
                // TODO 取消该逻辑，这里不需要判断。
            }
            else if (rsrvStr18.length() > 6 && "1".equals(rsrvStr18.substring(6, 7)))// 判断用户是否过相应的营销包
            {
                IDataset userSaleActiveDataset = databus.getDataset("TF_F_USER_SALE_ACTIVE");
                IDataset limit1573Set = CommparaInfoQry.getCommparaByParaAttr("CSM", "1753", databus.getString("EPARCHY_CODE"));

                String salePackageId = databus.getString("PACKAGE_ID");

                if (IDataUtil.isNotEmpty(limit1573Set))
                {
                    boolean isExists = false;
                    for (int index = 0, size = limit1573Set.size(); index < size; index++)
                    {
                        IData limit1573Data = limit1573Set.getData(index);

                        String userPackageId = limit1573Data.getString("PARA_CODE1");
                        String limitPackageId = limit1573Data.getString("PARA_CODE2");

                        if (salePackageId.equals(userPackageId))
                        {
                            if (IDataUtil.isEmpty(userSaleActiveDataset))
                                break;
                            isExists = getExsitsSaleActive(userSaleActiveDataset, limitPackageId);
                            if (isExists)
                                break;
                        }
                    }

                    if (!isExists)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024013, "用户未办理相应的营销包！");
                    }
                }
            }
            else if (rsrvStr18.length() > 9 && "1".equals(rsrvStr18.substring(8, 9)))// 统一付费副号不能办理
            {
                IDataset relationSet = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "56", "2");
                if (IDataUtil.isNotEmpty(relationSet))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200429, "统一付费副号不能能办理该包！");
                }
            }
        }

        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR15")))// 用户没有XX优惠，不能办理该业务！
        {
            String salePackageId = databus.getString("PACKAGE_ID");
            IDataset paramSet = CommparaInfoQry.getCommparaCode1("CSM", "1754", salePackageId, databus.getString("EPARCHY_CODE"));

            IDataset userDiscntDataset = databus.getDataset("TF_F_USER_DISCNT");
            String rsrvStr15 = packageExtData.getString("RSRV_STR15", " |");
            String rsrvStrArr[] = rsrvStr15.split("\\|");

            boolean isExists = false;
            if (IDataUtil.isNotEmpty(paramSet))
            {
                String paraCode1 = paramSet.getData(0).getString("PARA_CODE1");
                isExists = filterUserDiscntByDate(userDiscntDataset, rsrvStr15, paraCode1);
            }
            else
            {
                isExists = filterUserDiscntByDate(userDiscntDataset, rsrvStr15, "3");
            }

            if (!isExists)
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024014, "没有办理[" + packageExtData.getString("RSRV_STR15") + "]资费的客户不能办理该业务！");
                }
            }
            else
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }
        }

        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR16")))// 用户非XX品牌，不能办理该业务
        {
            String rsrvStr16 = packageExtData.getString("RSRV_STR16");
            String userBrandCode = databus.getString("BRAND_CODE");
            if (rsrvStr16.indexOf(userBrandCode) < 0)
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024015, "客户品牌不能办理该业务！");
                }
            }
            else
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }

        }
        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR19")))// 网龄区间判断
        {
            String rsrvStr19 = packageExtData.getString("RSRV_STR19");
            String monthBetwenArray[] = rsrvStr19.split("\\|");

            int monthBegin = Integer.parseInt(monthBetwenArray[0]);
            int monthEnd = Integer.parseInt(monthBetwenArray[1]);

            int openMoths = SysDateMgr.monthInterval(SysDateMgr.getSysTime(), databus.getString("OPEN_DATE"));

            if (monthEnd != -1 && (openMoths > monthBegin && openMoths <= monthEnd))
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }

            }
            else if (monthEnd == -1 && openMoths > monthBegin)
            {
                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    return true;
                }
            }
            else
            {
                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024016, "用户网龄不在区间['" + rsrvStr19 + "']月！");
                }
            }
        }
        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR20")) && packageExtData.getString("RSRV_STR20").startsWith("CREDIT_CLASS|"))
        {
        	String userCreditClass = SaleActiveUtil.queryUserCreditClass(databus.getString("USER_ID"));

            if (packageExtData.getString("RSRV_STR20").indexOf(userCreditClass)>-1)
            {
            	 if ("1".equals(packageExtData.getString("RSRV_STR10")))
                 {
                     return true;
                 }
            }
            else
            {
            	 if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                 {
                     BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20047, "用户信用度星级不满足办理条件，不能继续办理！");
                 }
            }
        }
        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR21")))// 需要在对应目标客户群才能办理
        {
            String rsrvStr21 = packageExtData.getString("RSRV_STR21");
            IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdUserId(rsrvStr21, userId);
            if (IDataUtil.isEmpty(troopMemberSet))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024017, "用户不在目标客户群内，不能办理此营销包！");
            }
            else
            {
                IDataset paramSet = CommparaInfoQry.getCommparaByCode1("CSM", "1549", databus.getString("PACKAGE_ID"), databus.getString("EPARCHY_CODE"));
                if (IDataUtil.isNotEmpty(paramSet))
                {
                    boolean existsDiscnts = false;
                    for (int index = 0, size = paramSet.size(); index < size; index++)
                    {
                        String discntCode = paramSet.getData(index).getString("PARAM_CODE");

                        IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
                        for (int j = 0, s = userDiscnts.size(); j < s; j++)
                        {
                            IData discntData = userDiscnts.getData(j);
                            if (discntCode.equals(discntData.getString("DISCNT_CODE")))
                            {
                                existsDiscnts = true;
                                break;
                            }
                        }

                        if (existsDiscnts)
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024018, "客户存在的数据流量套餐不能办理该活动包！");
                        }
                        else
                        {
                            return true;
                        }
                    }
                }
            }
        }
        else if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR22")))// 需要互联网电视服务的条件
        {
        	String tagset1 = packageExtData.getString("TAG_SET1", "");//SaleActiveUtil.getPackageExtTagSet1(packageId, packageExtInfos);
            if (tagset1.length() > 4)
            {
                if ("1".equals(tagset1.substring(4, 5)) || "2".equals(tagset1.substring(4, 5)))
                {
                    IDataset userWidenetBookActives = SaleActiveInfoQry.queryWidenetActivesByIds(userId, productId, packageId);
                    if (IDataUtil.isEmpty(userWidenetBookActives))
                    {
                        IDataset userPlatSvcDataset = databus.getDataset("TF_F_USER_PLATSVC");
                        boolean hasPlatSvc = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "51");
                        
                    	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
                    	boolean isIPTV = true;
                    	boolean hasIPTV = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "86");
                    	IDataset commparaInfos = CommparaInfoQry.getCommparaBy1to4("CSM","9514","SUPPORT_IPTV",productId,packageId,null,null,"0898");
                    	if(hasIPTV && IDataUtil.isNotEmpty(commparaInfos))
                    	{
                    		isIPTV = false;
                    	}
    					//System.out.println("--------isIPTV----------"+isIPTV);

                    	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
                        
                        if (!hasPlatSvc)
                        {
                            if (!"1".equals(packageExtData.getString("RSRV_STR10")) && isIPTV )
                            {
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "没有办理互联网电视业务的客户不能办理该活动包！");
                            }
                        }
                    }
                }
            }
        }

        String tagset1 = packageExtData.getString("TAG_SET1", "");

        if (tagset1.length() > 2)// 需要宽带服务的条件
        {
            String serialNumber = databus.getString("SERIAL_NUMBER");

            if ("1".equals(tagset1.substring(2, 3)))//正式的包
            {
                boolean isBookSaleActive = BreQry.isBookSaleActive(serialNumber, productId, packageId);//判断book表中是否存在正式包对应的预受理包
                if (!isBookSaleActive)//如果不存在预受理的包，则判断是否为宽带完工用户
                {
                    boolean isWidenetUser = BreQry.isWideNetUser(serialNumber);//判断是否为宽带完工用户
                    
                    IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "6822", productId, packageId, "1", null, "0898");
                    if(IDataUtil.isNotEmpty(ds))
                    {
                    	isWidenetUser = true;
                    }
                    /**
                     * 宽带过户
                     * 增加判断如果是宽带过户的用户，则跳过校验
                     * */
                    IData inParam=new DataMap();
                    inParam.put("SERIAL_NUMBER", serialNumber);
                    inParam.put("IN_DATE", SysDateMgr.getSysDateYYYYMMDD());
                    IDataset tampList=WideChangeUserCheckBean.qryWideChangeUserTempInfo(inParam);
                    if(tampList!=null && tampList.size()>0)
                    {
                    	return true;//isWidenetUser = true;
                    }
                    if (!isWidenetUser)//如果既不存在预受理的包，也不是宽带完工用户
                    {
                    	//宽带融合开户同时办理不需要做此校验
                    	if (!"1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","")))
                        {
                    		//用户存在宽带开户未完工单也允许办理【68900331 和目尝鲜活动】
                    		if ("68900331".equals(packageId)) {
                    			if (IDataUtil.isEmpty(TradeInfoQry.getMainTradeBySN("KD_"+serialNumber,"600"))) 
                    			{
                    				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024019, "该用户不存在未完工的宽带开户工单！");
                    			}
							}else {
								BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024019, "该用户不是个人宽带装机完工用户！");
							}
                    		
                        }
                    }
                    else//如果不存在预受理的包，但是宽带完工用户，继续判断宽带的条件
                    {
                        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR24")))
                        {
                            boolean isFinishUserSvc = BreQry.isFinishUserSvc(serialNumber, packageExtData.getString("RSRV_STR24"));
                            if (!isFinishUserSvc)
                            {
                            	//---------------------------宽带优化新增校验，针对宽带产品变更中的营销活动办理-------------------------------
                            	String wideUserSelectedServiceIds="";
                                //如果是产品变更功能和营销活动同时办理，不需要进行此校验，
                                if ("1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","")))
                                {
                                	//宽带用户当前选中的服务
                                    wideUserSelectedServiceIds = databus.getString("WIDE_USER_SELECTED_SERVICEIDS","");
                                }
                                
                                //移机的时候做产品变更，同时做预约营销活动，移机完工时，需要从台账子表查询服务
                                String wideMoveSvc = "";
                                if(!isFinishUserSvc){
                                	IDataset widenetMoveInfo = TradeInfoQry.getMainTradeBySN(serialNumber, "606");
                                	if(widenetMoveInfo!=null&&widenetMoveInfo.size()>0){
                                		String tradeId = widenetMoveInfo.getData(0).getString("TRADE_ID");
                                		IDataset widenetProductInfo = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
                                		if(widenetProductInfo!=null&&widenetProductInfo.size()>0){
                                			for(int i=0;i<widenetProductInfo.size();i++){
                                    			if(!"1".equals(widenetProductInfo.getData(i).getString("MODIFY_TAG"))){
                                    				wideMoveSvc = wideMoveSvc + "|" + widenetProductInfo.getData(i).getString("SERVICE_ID");
                                    			}
                                			}
                                			if(!"".equals(wideMoveSvc)) wideMoveSvc = wideMoveSvc + "|";
                                		}
                                	}
                                }
                                if (StringUtils.isNotBlank(wideUserSelectedServiceIds))
                                {
                                    //判断用户当前选订的服务是否符合条件
                                	isFinishUserSvc =  BreQry.isNoOrderUserSvc(wideUserSelectedServiceIds, packageExtData.getString("RSRV_STR24"));
                                }
                                if (!isFinishUserSvc && StringUtils.isNotBlank(wideMoveSvc))
                                {
                                	isFinishUserSvc =  BreQry.isNoOrderUserSvc(wideMoveSvc, packageExtData.getString("RSRV_STR24"));
                                }
                                if (!isFinishUserSvc)
                                {
                                	if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                                    {
                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024020, "没有办理[" + packageExtData.getString("RSRV_STR24") + "]服务的客户不能办理该活动包！");
                                    }
                                }else
                                {
                                	if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                    {
                                        return true;
                                    }
                                }
                                //-----------------------end-----------------------
                            }
                            else
                            {
                                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            if ("2".equals(tagset1.substring(2, 3)))
            {
                boolean isWideUserCreateSaleActive = false;
                
                //如果是开户营销活动不需要进行此校验
                if ("1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","")))
                {
                    isWideUserCreateSaleActive = true;
                }
                
                //宽带用户当前选中的服务
                String wideUserSelectedServiceIds = databus.getString("WIDE_USER_SELECTED_SERVICEIDS","");
                
                boolean isNoFinishTrade = BreQry.isNoFinishTrade(serialNumber);
				
				IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "6822", productId, packageId, "1", null, "0898");
                if(IDataUtil.isNotEmpty(ds))
                {
                	isNoFinishTrade = true;
                }
                
                //宽带开户同时办理营销活动不需要报错
                if (!isNoFinishTrade && !isWideUserCreateSaleActive)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024021, "该用户不是个人宽带装机未完工用户！");
                }
                else
                {
                    if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR24")))
                    {
                        boolean isNoFinishUserSvc = BreQry.isNoFinishUserSvc(serialNumber, packageExtData.getString("RSRV_STR24"));
                        
                        if (!isNoFinishUserSvc)
                        {
                            if (StringUtils.isNotBlank(wideUserSelectedServiceIds))
                            {
                                //判断用户当前选订的服务是否符合条件
                                isNoFinishUserSvc =  BreQry.isNoOrderUserSvc(wideUserSelectedServiceIds, packageExtData.getString("RSRV_STR24"));
                            }
                            
                            if (!isNoFinishUserSvc){
                            	isNoFinishUserSvc = BreQry.isFinishUserSvc(serialNumber, packageExtData.getString("RSRV_STR24"));
                            }
                            
                            if (!isNoFinishUserSvc)
                            {
                                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024022, "没有办理[" + packageExtData.getString("RSRV_STR24") + "]服务的客户不能办理该活动包！");
                                }
                            }
                            else
                            {
                                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            if ("1".equals(packageExtData.getString("RSRV_STR10")))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            if ("3".equals(tagset1.substring(2, 3)))//正式的包
            {
                boolean isBookSaleActive = BreQry.isBookSaleActive(serialNumber, productId, packageId);//判断book表中是否存在正式包对应的预受理包
                if (!isBookSaleActive)//如果不存在预受理的包，则判断是否为宽带完工用户
                {                    
                    boolean isWidenetUser = false;
                    String imsserialNumber = "";
                    
                    IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "6822", productId, packageId, "1", null, "0898");
                    if(IDataUtil.isNotEmpty(ds))
                    {
                    	isWidenetUser = true;
                    }
                    
                    IData useuca = UcaInfoQry.qryUserInfoBySn(serialNumber);
                    
                    IDataset id2 = RelaUUInfoQry.qryRelationUUAllForKDMem(useuca.getString("USER_ID"), "MS", null);
                    if(IDataUtil.isNotEmpty(id2))
                    {
                    	imsserialNumber = id2.getData(0).getString("SERIAL_NUMBER_B","");
                    	isWidenetUser = true;
                    }
                    
                    if (!isWidenetUser)//如果既不存在预受理的包，也不是宽带完工用户
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024019, "该用户不是家庭IMS固话开户完工用户！");
                    }
                    else//如果不存在预受理的包，但是宽带完工用户，继续判断宽带的条件
                    {
                        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR24")))
                        {
                            boolean isFinishUserSvc = BreQry.isIMSFinishUserSvc(imsserialNumber, packageExtData.getString("RSRV_STR24"));
                            if (!isFinishUserSvc)
                            {
                            	//---------------------------宽带优化新增校验，针对宽带产品变更中的营销活动办理-------------------------------
                            	String wideUserSelectedServiceIds="";
                                //如果是产品变更功能和营销活动同时办理，不需要进行此校验，
                                if ("1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","")))
                                {
                                	//宽带用户当前选中的服务
                                    wideUserSelectedServiceIds = databus.getString("WIDE_USER_SELECTED_SERVICEIDS","");
                                }

                                if (StringUtils.isNotBlank(wideUserSelectedServiceIds))
                                {
                                    //判断用户当前选订的服务是否符合条件
                                	isFinishUserSvc =  BreQry.isNoOrderUserSvc(wideUserSelectedServiceIds, packageExtData.getString("RSRV_STR24"));
                                }
                                if (!isFinishUserSvc)
                                {
                                	if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                                    {
                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024020, "没有办理[" + packageExtData.getString("RSRV_STR24") + "]服务的客户不能办理该活动包！");
                                    }
                                }else
                                {
                                	if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                    {
                                        return true;
                                    }
                                }
                                //-----------------------end-----------------------
                            }
                            else
                            {
                                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            if ("4".equals(tagset1.substring(2, 3)))
            {
                boolean isWideUserCreateSaleActive = false;
                
                //如果是开户营销活动不需要进行此校验
                if ("1".equals(databus.getString("WIDE_USER_CREATE_SALE_ACTIVE","")))
                {
                    isWideUserCreateSaleActive = true;
                }
                
                //宽带用户当前选中的服务
                String wideUserSelectedServiceIds = databus.getString("WIDE_USER_SELECTED_SERVICEIDS","");
     
                boolean isNoFinishTrade = false;
                String imsserialNumber = "";
				
				IDataset ds = CommparaInfoQry.getCommparaInfoByCode5("CSM", "6822", productId, packageId, "1", null, "0898");
                if(IDataUtil.isNotEmpty(ds))
                {
                	isNoFinishTrade = true;
                }
                
                IData useuca = UcaInfoQry.qryUserInfoBySn(serialNumber);
                IDataset id2 = TradeInfoQry.getExistUser("MS", useuca.getString("USER_ID"), "1");
                if(IDataUtil.isNotEmpty(id2))
                {
                	imsserialNumber = id2.getData(0).getString("SERIAL_NUMBER","");
                	isNoFinishTrade = true;
                }
                
                //宽带开户同时办理营销活动不需要报错
                if (!isNoFinishTrade && !isWideUserCreateSaleActive)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024021, "该用户不是家庭IMS固话开户未完工用户！");
                }
                else
                {
                    if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR24")))
                    {
                        boolean isNoFinishUserSvc = BreQry.isNoIMSFinishUserSvc(imsserialNumber, packageExtData.getString("RSRV_STR24"));
                        
                        if (!isNoFinishUserSvc)
                        {
                            if (StringUtils.isNotBlank(wideUserSelectedServiceIds))
                            {
                                //判断用户当前选订的服务是否符合条件
                                isNoFinishUserSvc =  BreQry.isNoOrderUserSvc(wideUserSelectedServiceIds, packageExtData.getString("RSRV_STR24"));
                            }
                            
                            if (!isNoFinishUserSvc){
                            	isNoFinishUserSvc = BreQry.isIMSFinishUserSvc(imsserialNumber, packageExtData.getString("RSRV_STR24"));
                            }
                            
                            if (!isNoFinishUserSvc)
                            {
                                if (!"1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024022, "没有办理[" + packageExtData.getString("RSRV_STR24") + "]服务的客户不能办理该活动包！");
                                }
                            }
                            else
                            {
                                if ("1".equals(packageExtData.getString("RSRV_STR10")))
                                {
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            if ("1".equals(packageExtData.getString("RSRV_STR10")))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        if (tagset1.length() > 4)// 互联网电视
        {
            if ("1".equals(tagset1.substring(4, 5)))
            {
                IDataset userWidenetBookActives = SaleActiveInfoQry.queryWidenetActivesByIds(userId, productId, packageId);
                if (IDataUtil.isEmpty(userWidenetBookActives))
                {
                    //IDataset userPlatSvcDataset = databus.getDataset("TF_F_USER_PLATSVC");
                    //boolean hasPlatSvc = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "51");

                    //if (!hasPlatSvc)
                    //{
                    //    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "该用户不是个人互联网电视装机完工用户！");
                    //}
                	
                	//不再采用读databus中的TF_F_USER_PLATSVC，由于databus包含tf_b_trade_platsvc的数据，导致判断不准
                	IDataset userPlatSvcDataset = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");
                	
                	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
                	boolean isIPTV = true;
                	IDataset userPlatSvcDataset1 = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");
                	IDataset commparaInfos = CommparaInfoQry.getCommparaBy1to4("CSM","9514","SUPPORT_IPTV",productId,packageId,null,null,"0898");
                	if(IDataUtil.isNotEmpty(userPlatSvcDataset1) && IDataUtil.isNotEmpty(commparaInfos))
                	{
                		isIPTV = false;
                	}
					//System.out.println("--------isIPTV----------"+isIPTV);
                	//REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求

                	if (IDataUtil.isEmpty(userPlatSvcDataset))
                    {
                		if(isIPTV){
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "该用户不是个人互联网电视装机完工用户！");
                		}
                    }
                }
                
                /**
                 * REQ201607050007 关于移动电视尝鲜活动的需求
                 * 正式包）
                 * chenxy3 20160719
                 * */
                String rsrvStr24 = packageExtData.getString("RSRV_STR24", ""); 
                if(!"".equals(rsrvStr24)){ 
                	String serialNumber = databus.getString("SERIAL_NUMBER"); 
                	boolean isFinishUserSvc = BreQry.isFinishUserSvc(serialNumber, rsrvStr24); 
    				if(!isFinishUserSvc){
    					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "没有办理[" + rsrvStr24 + "]服务的客户不能办理该活动包！");
    				} 
                } 
            }
            else if ("2".equals(tagset1.substring(4, 5)))
            {
            	//REQ201604280010 关于拆分宽带1+（魔百和）的需求；取消了以前依赖3800工单，改为了依赖600宽带开户或4800魔百和预开户+tf_b_trade_other有TOPSETBOX
                //IDataset tradeInfoSet = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userId, "0");
            	IDataset tradeInfoSet = TradeOtherInfoQry.getTradeInfosByTradeTypeAndOther(databus.getString("SERIAL_NUMBER"));
                if (IDataUtil.isEmpty(tradeInfoSet))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "该用户不是个人互联网电视装机未完工用户！");
                }
                 
                /**
                 * REQ201607050007 关于移动电视尝鲜活动的需求
                 * 正式包）
                 * chenxy3 20160719
                 * */
                String rsrvStr24 = packageExtData.getString("RSRV_STR24", "");
                String serialNumber = databus.getString("SERIAL_NUMBER"); 
                if(!"".equals(rsrvStr24)){
                	boolean isNoFinishTrade = BreQry.isNoFinishUserSvc(serialNumber,rsrvStr24);
    				if(!isNoFinishTrade){
    					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "没有办理[" + rsrvStr24 + "]服务的客户不能办理该活动包！");
    				} 
                } 
            }
            else if ("3".equals(tagset1.substring(4, 5)))
            {
                IDataset tradeInfoSet = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userId, "0");
                if (IDataUtil.isEmpty(tradeInfoSet))
                {
                    IDataset userPlatSvcDataset = databus.getDataset("TF_F_USER_PLATSVC");
                    boolean hasPlatSvc = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "51");
                    if (hasPlatSvc)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "该用户已办理互联网电视装机！");
                    }
                }
                else
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "该用户已办理互联网电视装机！");
                }
            }
        }

        if (StringUtils.isNotBlank(packageExtData.getString("RSRV_STR10")))
        {
            if ("1".equals(packageExtData.getString("RSRV_STR10")))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024023, "入网年限或者VIP等级或用户信用度星级不满足，业务不能继续！");
            }
        }
        //实时结余
        IDataset needCheck = CommparaInfoQry.getCommparaByCode1("CSM", "9111", databus.getString("PACKAGE_ID"), databus.getString("EPARCHY_CODE"));
        if(IDataUtil.isNotEmpty(needCheck)){
           String minMoney = needCheck.getData(0).getString("PARA_CODE2","0");
           Double minMoneyNew = Double.parseDouble(minMoney) / 100;
      	   String acctBalance = "0";
    	   IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
    	   acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
    	   
           //实时结余加上信用度,海南用户需求
           double nCreditValue = 0;
           if("1".equals(needCheck.getData(0).getString("PARA_CODE3","0"))){
        	   IDataset ids = AcctCall.getUserCreditInfos("0", userId);
        	   if (IDataUtil.isNotEmpty(ids)) {
        		   IData idCreditInfo = ids.getData(0);
        		   nCreditValue = idCreditInfo.getDouble("CREDIT_VALUE");
        	   }
           }
    	   Double acctBalancenew = (Double.parseDouble(acctBalance)+ nCreditValue)/ 100;
    	    if (acctBalancenew  < minMoneyNew )
            {
    	    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 11222015, "您当前话费余额不足"+minMoneyNew.toString()+"元，当前话费余额为："+acctBalancenew.toString()+"元。");
            }
        }
        //往月欠费
        IDataset needCheck2 = CommparaInfoQry.getCommparaByCode1("CSM", "9112", databus.getString("PACKAGE_ID"), databus.getString("EPARCHY_CODE"));
        if(IDataUtil.isNotEmpty(needCheck2)){
           String minMoney = "0";
      	   String lastOweFee = "0";
    	   IData oweFeeData = AcctCall.getOweFeeByUserId(userId);
    	   lastOweFee = oweFeeData.getString("LAST_OWE_FEE", "0");
    	   
    	   double nCreditValue = 0;
           if("1".equals(needCheck.getData(0).getString("PARA_CODE3","0"))){
        	   IDataset ids = AcctCall.getUserCreditInfos("0", userId);
        	   if (IDataUtil.isNotEmpty(ids)) {
        		   IData idCreditInfo = ids.getData(0);
        		   nCreditValue = idCreditInfo.getDouble("CREDIT_VALUE");
        	   }
           }
    	   
    	   Double lastOweFeeNew = (Double.parseDouble(lastOweFee) + nCreditValue)/ 100;
    	    if ((Double.parseDouble(lastOweFee) + nCreditValue)  > Double.parseDouble(minMoney) )
            {
    	    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 11332015, "客户有往月欠费"+lastOweFeeNew.toString()+"元，不能办理该业务。");
            }
        }
        
      //预转账校验及不扣减欠费
//        IDataset productTradeFeeInfos = ProductFeeInfoQry.getTransFeeByPPSql(databus);
        IDataset productTradeFeeInfos = SaleActiveUtil.getSaleAtiveFeeList(productId, packageId);
        
        if(IDataUtil.isNotEmpty(productTradeFeeInfos)){
        	IData productTradeFeeData = productTradeFeeInfos.getData(0);
	        if (StringUtils.isNotBlank(productTradeFeeData.getString("RSRV_STR5")))
	        {
	            if ("1".equals(productTradeFeeData.getString("RSRV_STR5")))
	            {
	            	IData checkdata = new DataMap();
	            	try {
	            		checkdata.put("TRADE_ID", "");
	            		checkdata.put("SERIAL_NUMBER", databus.getString("SERIAL_NUMBER"));
	            		checkdata.put("TRADE_FEE", productTradeFeeData.getString("FEE"));
	            		checkdata.put("CHANNEL_ID", "15000");
	                    checkdata.put("OUT_DEPOSIT_CODE", productTradeFeeData.getString("OUT_DEPOSIT_CODE"));
	                    checkdata.put("PAYMENT_ID", productTradeFeeData.getString("FEE_TYPE_CODE"));
	                    checkdata.put("DEPOSIT_CODE", "");
	                    checkdata.put("IS_SPE_FLAG_X", "1");//不扣减标识
	                    checkdata.put("PRE_CHECK", productTradeFeeData.getString("RSRV_STR5"));
	                    
//	                    IDataset saleDepositInfos = SaleDepositInfoQry.querySaleDepositByPackageId(packageId);
//	                    IDataset saleDepositInfos = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
	                    IDataset saleDepositInfos = PackageElementsCache.getElementsByElementTypeCode(packageId, BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
	                    if(IDataUtil.isNotEmpty(saleDepositInfos)){
	                    	checkdata.put("ACTION_CODE", saleDepositInfos.getData(0).getString("A_DISCNT_CODE", ""));
	                    }else{
	                    	checkdata.put("ACTION_CODE", "");
	                    }
	                    checkdata.put("START_DATE", SysDateMgr.getSysTime());

	            		IDataset result = AcctCall.sameTransCheckFee(checkdata);
					} catch (Exception e) {
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20161109, e.toString());
					}
	            	
	            }
	        }      
        }
        
        
        
        //2019海南移动存费送费活动-新增接口、参数
        IDataset commParaInfo9119 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9119", databus.getString("PACKAGE_ID"), databus.getString("PRODUCT_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9119)){
        	IData ParaInfo = commParaInfo9119.getData(0);
        	String timeLimited = ParaInfo.getString("PARA_CODE2");
        	String paymentid = ParaInfo.getString("PARA_CODE5");
        	String depositCode = ParaInfo.getString("PARA_CODE6");
        	String fee = ParaInfo.getString("PARA_CODE4","0");
        	String content = ParaInfo.getString("PARA_CODE3");
        	
        	IData params=new DataMap(); 
        	params.put("USER_ID", userId);
        	params.put("TIME_LIMITED", timeLimited);
        	params.put("PAYMENT_ID", paymentid);
        	params.put("DEPOSIT_CODE", depositCode);
        	IData resultData = AcctCall.qryRecentRecvFee(params);
        	if(IDataUtil.isNotEmpty(resultData)){
        		String payFee = resultData.getString("PAY_FEE","0");
        		if(new BigDecimal(payFee).compareTo(new BigDecimal(fee))<0){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199119, content);
        		}
        	}
        	        	
        }

		        
        /*
         *  REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305
         *  非全球通用户,才能办理“全球通银卡合约一年”活动
         *  不在表TF_F_USER_INFO_CLASS的用户，即非全球通用户
         */
        //获取commpara配置，查看是否是“约定套餐一年成为全球通客户”营销活动产品
    	IDataset commpara305 = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "305", "GSM_SILVER_CONTRACT_PRODUCT", productId, packageId, databus.getString("EPARCHY_CODE"));
    	if((IDataUtil.isNotEmpty(commpara305)) && commpara305.size()>0)//如果是
    	{
            String changeFlag = commpara305.first().getString("PARA_CODE5");
            if("1".equals(changeFlag)){
                IData inData = new DataMap();
                inData.put("USER_ID", userId);
                IDataset dataset = UserClassInfoQry.queryUserClass(inData);
                if(IDataUtil.isNotEmpty(dataset) && dataset.size()>0){//如果是全球通客户
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 190305, "非全球通用户才能办理该活动！");
                }
            }

    	}
        //End REQ201902260042新增“约定套餐一年成为全球通客户”的规则  by mengqx 20190305

        
        //REQ201902280005+【紧急需求】关于开发宽带保有专用的“60元报停专项款”和“60元话费礼包”的需求--wangsc10-20190313
        IDataset commParaInfo6600 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6600", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo6600)){
        	CustomerTradeData customerTradeData = UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER")).getCustomer();
        	if(customerTradeData != null){
            	String psptTypeCode = customerTradeData.getPsptTypeCode();
            	String psptId = customerTradeData.getPsptId();
            	String psptIdsub = "";
            	if(psptId != null){
            		psptIdsub = psptId.substring(0, 2);
            	}
            	String KD_USERID = "";
    			IData userInfo=UcaInfoQry.qryUserInfoBySn("KD_"+databus.getString("SERIAL_NUMBER"));//取到宽带USER_ID
    			if(IDataUtil.isNotEmpty(userInfo)){
    				KD_USERID = userInfo.getString("USER_ID");
            	}
            	
            	IDataset widenetInfoQry = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+databus.getString("SERIAL_NUMBER"));//存有有效宽带
            	IDataset userSvcStateInfoQry = UserSvcStateInfoQry.getUserSvcStateByUserID(KD_USERID, "2010", "1",Route.CONN_CRM_CG);//查询宽带报停状态
            	String kdzt = "Y";
            	if(IDataUtil.isNotEmpty(userSvcStateInfoQry)){
            		kdzt = "N";//宽带处于报停状态
            	}
            	String rhtc = "N";
				//查找配置：主套餐是融合套餐（具体
				IDataset mixProducts = CommparaInfoQry.getCommPkInfo("CSM", "1312", "MixProducts", "0898");
				if(mixProducts!=null && mixProducts.size()>0)
				{
					//查询用户主套餐ProductId
					String productIdZTC =UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER")).getProductId();
					//判断主套餐是否是融合套餐
					for (int i = 0; i < mixProducts.size(); i++) 
					{
						IData mixProduct = mixProducts.getData(i);
						String code1 = mixProduct.getString("PARA_CODE1", "");
						if(productIdZTC.equals(code1))//主套餐是融合套餐
						{
							rhtc = "Y";
						}
					}
				}
            	//除了“60元报停专项款”仅限宽带已完工，同时宽带处于宽带报停（局方停机不算）状态的外省身份证（证件类型是0或1，证件号非46打头）办理，同时是融合套餐,其他全部拦截
            	if(IDataUtil.isNotEmpty(widenetInfoQry) && kdzt.equals("N") && (psptTypeCode.equals("0") || psptTypeCode.equals("1")) && !psptIdsub.equals("46") && rhtc.equals("Y")){
            		
            	}else{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190318, "非目标客户，不符合办理'60元报停专项款'营销活动的条件!");
            	}
        	}
        }
        
        IDataset commParaInfo6601 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6601", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo6601)){
        	CustomerTradeData customerTradeData = UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER")).getCustomer();
        	if(customerTradeData != null){
            	
            	//存有有效宽带
            	IDataset widenetInfoQry = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+databus.getString("SERIAL_NUMBER"));
            	IDataset saleActivelist = SaleActiveInfoQry.getUserSaleActiveInfoByPRODUCTIDPACKAGEID(userId,productId,packageId);
            	String yjbl = "N";
				if(saleActivelist != null && saleActivelist.size() > 0){
					for (int i = 0; i < saleActivelist.size(); i++) {
						yjbl = "Y";;
					}
				}
            	//“60元话费礼包”仅限宽带已完工，不作客户限制，但每个客户只能办理一次。
            	if(IDataUtil.isNotEmpty(widenetInfoQry) && yjbl.equals("N")){
            		
            	}else{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190317, "非目标客户，不符合办理'60元话费礼包'营销活动的条件!");
            	}
        	}
        }
      
        
        //配置在9117的活动为指定身份证号的用户才能办理 add by tanzheng @20190314
        IDataset commParaInfo9117 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9117", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9117)){
        	UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
        	String psptId = ucaData.getCustomer().getPsptId();
        	String content = commParaInfo9117.first().getString("PARA_CODE3");
        	//以|分割，前边是productId，后边是packageId
        	String productActual = commParaInfo9117.first().getString("PARA_CODE2");
        	if(StringUtils.isBlank(content)){
        		content =  "没有资格办理该活动";
        	}
        	SaleActiveBean bean  = BeanManager.createBean(SaleActiveBean.class);
        	String[] array = productActual.split("\\|");
        	
        	IData result = bean.checkPsptValideForActive(psptId,array[0], array[1]);
        	if(result != null){
        		String state = result.getString("STATE");
        		if(!"0".equals(state)){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199117, "用户的证件下已经有号码办理过该活动");
        		}
        		
        	}else{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199117,content);
        	}
        }
        
        
        //不是宽带用户，才可以办理该活动add by tanzheng @20190314
        IDataset commParaInfo9219 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9219", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9219)){
        	String serialNumber = databus.getString("SERIAL_NUMBER");
        	boolean isWidenetUser = BreQry.isWideNetUser(serialNumber);//判断是否为宽带完工用户
        	IData commData = commParaInfo9219.first();
        	String content = commData.getString("PARA_CODE3");
        	//如果是宽带用户
        	if(isWidenetUser){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199219,content);
        	}else{
        		if(!serialNumber.startsWith("KD_")){
        			serialNumber = "KD_"+serialNumber ;
        		}
        		IDataset dataset = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);
        		String nowDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        		
        		long limitDay = commData.getLong("PARA_CODE2");
        		for(Object temp : dataset){
        			IData data = (IData)temp;
        			String openDate = data.getString("OPEN_DATE");
        			String destroyDate = data.getString("DESTROY_TIME");
        			
        			long openDiffTime = SysDateMgr.getTimeDiff(openDate, nowDate, SysDateMgr.PATTERN_STAND);
        			long openDiffDay = openDiffTime/(24*60*60*1000);
        			
        			long destroyDiffTime = SysDateMgr.getTimeDiff(destroyDate, nowDate, SysDateMgr.PATTERN_STAND);
        			long destroyDiffDay = destroyDiffTime/(24*60*60*1000);
        			//如果开户时间或者销户时间在90天内则不能办理
        			if( openDiffDay < limitDay || destroyDiffDay<limitDay){
        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201992191,content);
        				logger.error(data.getString("SERIAL_NUMBER")+"指定天数内开户或者销户");
        			}
        		}
        		
        		
        		
        		
        	}
        }
        
        
        
        //不是亲亲用户，才可以办理该活动add by tanzheng @20190314
        IDataset commParaInfo9220 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9220", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9220)){
        	IDataset allRelationDataset = RelaUUInfoQry.check_byuserida_idbzm(userId, "45",null,null);
        	IData commData = commParaInfo9220.first();
        	String content = commData.getString("PARA_CODE3");
        	if(IDataUtil.isEmpty(allRelationDataset)){
        		IDataset invalidRelation = RelaUUInfoQry.qryRelationUUAllForExpire(userId,"45");
        		String nowDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        		long limitDay = commData.getLong("PARA_CODE2");
        		for(Object temp : invalidRelation){
        			IData data = (IData)temp;
        			String starteDate = data.getString("START_DATE");
        			String endDate = data.getString("START_DATE");
        			
        			long startDiffTime = SysDateMgr.getTimeDiff(starteDate, nowDate, SysDateMgr.PATTERN_STAND);
        			long endDiffTime = SysDateMgr.getTimeDiff(endDate, nowDate, SysDateMgr.PATTERN_STAND);
        			
        			long stratDiffDay = startDiffTime/(24*60*60*1000);
        			long endDiffDay = endDiffTime/(24*60*60*1000);
        			//如果办理时间或者截至时间在90天内则不能办理
        			if( stratDiffDay < limitDay || endDiffDay < limitDay){
        				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199220,content);
        				logger.error(data.getString("SERIAL_NUMBER_B")+"指定天数内开通或取消亲亲网");
        			}
        			
        		}
        	}else{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201992201,content);
        	}
        }
        
        
        
        //没有办理过咪咕视频会员，才可以办理该活动add by tanzheng @20190314
        IDataset commParaInfo9221 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9221", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9221)){
        	IData commData = commParaInfo9221.first();
        	
        	long limitDay = commData.getLong("PARA_CODE2");
    		String content = commData.getString("PARA_CODE3");
    		String serviceIds = commData.getString("PARA_CODE20");
    		String[] serviceIdArray = serviceIds.split("\\|");
    		
    		for(String serviceId : serviceIdArray){
    			IDataset userPlatSvcDataset = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userId, serviceId);
                if(IDataUtil.isEmpty(userPlatSvcDataset)){
                	IDataset invalidPlatSvc = UserPlatSvcInfoQry.queryUserInValidPlatSvc(userId, serviceId);
                	for(Object temp : invalidPlatSvc){
                		
                		IData platSvcData = (IData)temp;
                		
                		String starteDate = platSvcData.getString("START_DATE");
                		String endDate = platSvcData.getString("END_DATE");
                		
                		String nowDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
                		
                		
                		long stratDiffTime = SysDateMgr.getTimeDiff(starteDate, nowDate, SysDateMgr.PATTERN_STAND);
                		long startDiffDay = stratDiffTime/(24*60*60*1000);

                		long endDiffTime = SysDateMgr.getTimeDiff(endDate, nowDate, SysDateMgr.PATTERN_STAND);
                		long endDiffDay = endDiffTime/(24*60*60*1000);
                		//如果办理时间或者截至时间在90天内则不能办理
                		if( startDiffDay < limitDay || endDiffDay < limitDay){
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199221,content);
                			logger.error(userId+"用户在指定天数内开通或取消指定平台服务"+serviceId);
                			break;
                		}
                	}
                }else{
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201992211,content);
                	logger.error(userId+"用户存在指定平台服务"+serviceId);
                	break;
                }
    		}
        }
        //没有办理过咪咕视频会员，才可以办理该活动add by tanzheng @20190314
        IDataset commParaInfo9224 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9224", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
        if(IDataUtil.isNotEmpty(commParaInfo9224)){
        	IData commData = commParaInfo9224.first();
        	
        	long limitDay = commData.getLong("PARA_CODE2");
    		String content = commData.getString("PARA_CODE3");
    		String serviceIds = commData.getString("PARA_CODE20");
    		String[] serviceIdArray = serviceIds.split("\\|");
    		
    		for(String serviceId : serviceIdArray){
    			IDataset userSvcDataset = UserSvcInfoQry.getSvcUserId(userId, serviceId);
                if(IDataUtil.isEmpty(userSvcDataset)){
                	IDataset invalidSvc = UserSvcInfoQry.getExpireSvcUserId(userId, serviceId);
                	for(Object temp : invalidSvc){
                		
                		IData platSvcData = (IData)temp;
                		
                		String starteDate = platSvcData.getString("START_DATE");
                		String endDate = platSvcData.getString("END_DATE");
                		
                		String nowDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
                		
                		
                		long stratDiffTime = SysDateMgr.getTimeDiff(starteDate, nowDate, SysDateMgr.PATTERN_STAND);
                		long startDiffDay = stratDiffTime/(24*60*60*1000);

                		long endDiffTime = SysDateMgr.getTimeDiff(endDate, nowDate, SysDateMgr.PATTERN_STAND);
                		long endDiffDay = endDiffTime/(24*60*60*1000);
                		//如果办理时间或者截至时间在90天内则不能办理
                		if( startDiffDay < limitDay || endDiffDay < limitDay){
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20199221,content);
                			logger.error(userId+"用户在指定天数内开通或取消指定服务"+serviceId);
                			break;
                		}
                	}
                }else{
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201992211,content);
                	logger.error(userId+"用户存在指定服务"+serviceId);
                	break;
                }
    		}
        }
        
        //REQ202003050012 关于开发融合套餐增加魔百和业务优惠体验权益的需求 
	    IDataset commParaInfo2324 = CommparaInfoQry.getCommparaInfoByParacode4AndAttr("CSM", "2324", productId, "0898");
        if(IDataUtil.isNotEmpty(commParaInfo2324)){
        	IData proInfo = UcaInfoQry.qryMainProdInfoByUserId(databus.getString("USER_ID"));
    		if (IDataUtil.isNotEmpty(proInfo))
    		{
    			IDataset commparaInfos2324=CommparaInfoQry.getCommparaInfos("CSM", "2324",proInfo.getString("PRODUCT_ID"));
    	    	if (IDataUtil.isEmpty(commparaInfos2324))
    	        {
    	    		 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200504, "主产品不在魔百和权益活动最新套餐范围！");
    	        }
    		}
        	
        	if(databus.getString("PACKAGE_ID").equals("84076652")){
        		boolean flg =false;
        		int num = 0;//判断用户办理了几个魔百和
        		IDataset userPlatSvcDataset = databus.getDataset("TF_F_USER_PLATSVC");
                boolean hasPlatSvc = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "51");
             	boolean hasIPTV = BreQry.hasBizeTypeCodePlatSvc(userPlatSvcDataset, "86");
             	if(hasPlatSvc){
             		 IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");//biz_type_code=51为互联网电视类的平台服务
             		 if(SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysTime(),platSvcInfos.first().getString("START_DATE"))>2){
             			flg = true;
             		 }
             		 num = platSvcInfos.size();
             	}else if(hasIPTV){
             		 IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");//biz_type_code=86为互联网电视类的平台服务
             		 if(SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysTime(),platSvcInfos.first().getString("START_DATE"))>2){
             			flg = true;
            		 }
             		 num = platSvcInfos.size();
             	}
            	if(num>1){
            		 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200404, "用户已经有2条或2条以上魔百和业务，则不能办理魔百和体验基础包！");
            	}
            	if(flg){
            		 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20200403, "已经办理原价基础包的用户，办理后2个月内且当前原价基础包仍然生效才可办理！");
            	}
        	}
        	
        }
        
        //REQ201907090034 购机直降（全网统一营销）活动开发需求--添加规则判断 
        IDataset commParaInfo6699 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6699", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo6699)){
        	String opendate = databus.getString("OPEN_DATE");
        	Date now = new Date();
        	String nowDate=SysDateMgr.date2String(now,SysDateMgr.PATTERN_STAND);
        	int monthSum=SysDateMgr.monthInterval(opendate,nowDate);
        	
        	IData inData = new DataMap();
    		inData.put("USER_ID", userId);
    		inData.put("VAILD_DATE", SysDateMgr.getSysTime());//当前时间
        	IDataset dataset = UserClassInfoQry.queryUserClassByVaildDate(inData);
        	boolean flg = false;//判断是否全球通标识用户
            if(IDataUtil.isNotEmpty(dataset) && dataset.size()!=0) {
            	String userClassAll = commParaInfo6699.first().getString("PARA_CODE5");
            	String userClass = dataset.first().getString("USER_CLASS"); 
            	if(StringCompareString(userClass,userClassAll))
            	{
            		flg=true;
            	}
            }
        	
        	String paraCode2 = commParaInfo6699.first().getString("PARA_CODE2");
        	String content = commParaInfo6699.first().getString("PARA_CODE3");
        	int paraCode4 = Integer.parseInt(commParaInfo6699.first().getString("PARA_CODE4","12"));//网龄
        	if("0".equals(paraCode2)){
        		if(monthSum>=paraCode4||flg){
            		
            	}else{
            		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907011,content);
                	logger.error(userId+content);
            	}
        	}else{
        		if(monthSum<paraCode4&&!flg){
        			
        		}else{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907011,content);
                	logger.error(userId+content);
        		}
        	}
        	
        }

        // 宽带+移动电视 业务办理有礼活动 --start
        IDataset commParaInfo1208 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1208", productId, packageId);

        if (IDataUtil.isNotEmpty(commParaInfo1208)){
            // 获取宽带开户限制的开关
            String limitParaCode3 = commParaInfo1208.getData(0).getString("PARA_CODE3");
            // 获取宽带拆机限制的开关
            String limitParaCode4 = commParaInfo1208.getData(0).getString("PARA_CODE4");
            // 获取时间参数，默认为2020年2月20日
            String limitParaCode5 = commParaInfo1208.getData(0).getString("PARA_CODE5");

            if (null == limitParaCode5 || "".equals(limitParaCode5)){
                limitParaCode5 = "2020-02-20 00:00:00";
            }
            String flag = "0";
            // 获取用户当前必须优惠编码
            IDataset userDiscntsDataset = databus.getDataset("TF_F_USER_DISCNT");
            logger.debug("cncccn---->userDiscntsDataset"+userDiscntsDataset);
            // 配置允许用户办理的必选套餐优惠 D
            logger.debug("cncccn---->can"+productId+"--"+packageId);
            IDataset commParaInfo1206 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "1206", productId, packageId,"0898");
            logger.debug("cncccn---->commParaInfo1206"+commParaInfo1206);
            if (IDataUtil.isNotEmpty(commParaInfo1206) && IDataUtil.isNotEmpty(userDiscntsDataset)){// 遍历配置表中的全部主套餐编码，判断当前用户是否命中。
                for (int i =0; i<commParaInfo1206.size(); i++ ){
                    String discontCode = commParaInfo1206.getData(i).getString("PARA_CODE1");
                    logger.debug("cncccn---->discontCode"+discontCode);
                    for (int j = 0; j < userDiscntsDataset.size(); j++)
                    {
                        IData discntData = userDiscntsDataset.getData(j);
                        if (discontCode.equals(discntData.getString("DISCNT_CODE")))
                        {
                            flag = "1";
                            break;
                        }
                    }
                }
            }

            // 获取用户营销活动编码
            IDataset userSaleActiveDataset = databus.getDataset("TF_F_USER_SALE_ACTIVE");
            logger.debug("cncccn---->userSaleActiveDataset"+userSaleActiveDataset);

            // 配置允许用户办理的营销包编码 K
            IDataset commParaInfo1207 = CommparaInfoQry.getCommparaInfoByCode2("CSM", "1207", productId, packageId,"0898");
            logger.debug("cncccn---->commParaInfo1207"+commParaInfo1207);

            if (IDataUtil.isNotEmpty(commParaInfo1207) && IDataUtil.isNotEmpty(userSaleActiveDataset)){// 遍历配置表中的全部营销活动编码，判断当前用户是否命中。
                for (int i=0; i<commParaInfo1207.size(); i++){
                    String packageCode = commParaInfo1207.getData(i).getString("PARA_CODE1");
                    boolean hasSaleActive = getExsitsSaleActive(userSaleActiveDataset,packageCode);
                    if (hasSaleActive){
                        // 校验通过
                        flag = "1";
                        break;
                    }
                }
            }
            logger.debug("cncccn---->flag"+flag);
            if (flag.equals("0")){
                if (IDataUtil.isEmpty(commParaInfo1207) && IDataUtil.isEmpty(commParaInfo1206)){// 没有配置在这两个规则里面的活动包，不拦截
                    ;
                }else {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200220", "用户不属于融合套餐、宽带1+、宽带包年、度假系列套餐！");
                }
            }

            if (limitParaCode3.equals("1")){// 开关打开
                // 自2020年2月20号开通宽带的用户（台账记录的accept_date 为准）
                boolean isNewKDuser = false;

                // 查询用户是否为某个时间起开户的宽带用户
                isNewKDuser = BreQry.qryKDUserForTrade(databus.getString("SERIAL_NUMBER"),"600",limitParaCode5);
                if (!isNewKDuser){
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200220", "用户不属于2020年2月20日后办理的宽带用户！");
                }
            }

            if (limitParaCode4.equals("1")){// 开关打开
                // 自2020年2月20号前3个月没有办理过宽带拆机:605
                boolean hasOrder = BreQry.qryKDTradeforThreeMonth(databus.getString("SERIAL_NUMBER"),"605");
                logger.debug("cncccn---->hasOrder"+hasOrder);
                if (hasOrder){
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200220", "用户3个月内有办理过宽带拆机，不能办理该活动！");
                }
                // 宽带拆机预约用户不能办理改活动
                boolean isRemoval = BreQry.queryRemoval(databus.getString("SERIAL_NUMBER"));
                if (isRemoval){
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200220", "预约宽带拆机用户，不能办理该活动！");
                }

            }

        }

        // 宽带+移动电视 业务办理有礼活动 --end

        //BUS202001200014 泛渠道营销活动系统优化需求 --start
        // 通过 1300 规则过滤出需要进行拦截的营销活动产品 即：根据 productId 判断是否为需要校验的营销产品
        IDataset commParaInfo1300 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1300", productId, packageId);
        logger.debug("111cncccn---->commParaInfo1300"+commParaInfo1300);
        if (IDataUtil.isNotEmpty(commParaInfo1300)){

            String standVlaue = commParaInfo1300.first().getString("PARA_CODE2");
            if (null == standVlaue || "".equals(standVlaue)){
                // 设置默认的ARPU阈值
                standVlaue = "98";
            }
            logger.debug("111cncccn---->standVlaue"+standVlaue);

            // 获取当前用户近三月月均ARPU值
            IDataset avgFeeInfo = MvelMiscQry.getUserAvgPayFee(userId, "3");
            logger.debug("111cncccn---->avgFeeInfo"+avgFeeInfo);


            if (IDataUtil.isNotEmpty(avgFeeInfo)) {
                String avgFee = avgFeeInfo.first().getString("PARAM_CODE","0");
                BigDecimal standValue = new BigDecimal(standVlaue);

                IDataset commParaInfo1302 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1302", productId, packageId);
                IDataset commParaInfo1205 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1205", productId, packageId);

                if (new BigDecimal(avgFee).compareTo(standValue)<0){// ARPU 值小于98
                    // 走原来的1302规则
                    logger.debug("111cncccn---->1302"+productId+packageId);

                    if(IDataUtil.isNotEmpty(commParaInfo1302)){
                        String minfee = commParaInfo1302.first().getString("PARA_CODE2");
                        String maxfee = commParaInfo1302.first().getString("PARA_CODE4");

                        BigDecimal minfee2 = new BigDecimal(minfee);
                        BigDecimal maxfee2 = new BigDecimal(maxfee);

                        if(new BigDecimal(avgFee).compareTo(minfee2)<0||new BigDecimal(avgFee).compareTo(maxfee2)>0){
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1001】用户暂不满足该营销活动的办理条件！");
                        }
                    }
                    // 此时，需要把1205规则里面的包全部拦截
                    if (IDataUtil.isNotEmpty(commParaInfo1205)){
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【2001】用户暂不满足该营销活动的办理条件！");
                    }

                }else {// ARPU 值大于等于98
                    // 走新的 1205 规则
                    logger.debug("111cncccn---->1205"+productId+packageId);

                    if(IDataUtil.isNotEmpty(commParaInfo1205)){

                        String minfee = commParaInfo1205.first().getString("PARA_CODE2");
                        BigDecimal minfee2 = new BigDecimal(minfee);

                        // 1、查询用户主产品资料表当天是否有过记录更新。即：当天是否有办理过主产品变更 （立即变更/预约变更）
                        boolean hasChangeMainProduct = BreQry.qryUserProductTrade(databus.getString("USER_ID"));
                        logger.debug("ccccccccc-------hasChangeMainProduct"+hasChangeMainProduct);

                        if (!hasChangeMainProduct){// 用户办理活动当天没有升档主套餐操作，进行拦截
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1003】用户套餐暂不满足该营销活动的办理条件!");
                        }else {
                            // 2、获取用户当天的主产品资料信息。只取最近更新的2条信息
                            IDataset userProductInfo = BreQry.qryUserProductInfo(databus.getString("USER_ID"));
                            logger.debug("cccccccccc----------userProductInfo"+userProductInfo);
                            if (IDataUtil.isNotEmpty(userProductInfo) && userProductInfo.size()==2){

                                String afterChanegProductId = userProductInfo.getData(0).getString("PRODUCT_ID");
                                String beforeChangeProductId = userProductInfo.getData(1).getString("PRODUCT_ID");

                                logger.debug("cccccccccc----------afterChanegProductId"+afterChanegProductId);
                                logger.debug("cccccccccc----------beforeChangeProductId"+beforeChangeProductId);

                                // 3、根据上述两个编码，在套餐价格表中查找对应的价格，并判断价格差
                                IDataset before = CommparaInfoQry.getCommparaAllColByParser("CSM", "1301", beforeChangeProductId, "0898");
                                IDataset after = CommparaInfoQry.getCommparaAllColByParser("CSM", "1301", afterChanegProductId, "0898");

                                if (IDataUtil.isNotEmpty(before) && IDataUtil.isNotEmpty(after)){ // 必须确保找到数据
                                    IDataset userDiscntsDataset = BreQry.qryUserDiscntInfo(databus.getString("USER_ID"));
                                    String beforePrice = null;
                                    logger.debug("hhhhhh----------before.size"+before.size());
                                    if (before.size() == 1){
                                        beforePrice =  before.first().getString("PARA_CODE3");
                                    }else {// 根据产品编码找到多条记录，那么当前用户套餐为自选组合套餐
                                        // 根据用户的优惠资料表，并结合主套餐编码再进行二次查找
                                        if (IDataUtil.isNotEmpty(userDiscntsDataset)){
                                            int total = 0;
                                            for (int i=0; i<userDiscntsDataset.size(); i++){
                                                String discnt = userDiscntsDataset.getData(i).getString("DISCNT_CODE");
                                                logger.debug("hhhhhh----------discnt"+discnt);
                                                for (int j=0; j<before.size(); j++){
                                                    logger.debug("hhhhhh----------before.getData(j)"+before.getData(j));
                                                    if (discnt.equals(before.getData(j).getString("PARA_CODE1"))){
                                                        String temp =  before.getData(j).getString("PARA_CODE3");
                                                        total = total + new BigDecimal(temp).intValue();
                                                    }
                                                }
                                            }
                                            beforePrice = total +"";
                                            logger.debug("111cncccn---->beforePrice"+beforePrice);
                                        }else {
                                            ;// 获取用户优惠资料表数据异常
                                        }
                                    }
                                    logger.debug("222cncccn---->beforePrice"+beforePrice);

                                    String afterPrice = null;
                                    if (after.size() == 1){
                                        afterPrice =  after.first().getString("PARA_CODE3");
                                    }else {// 根据产品编码找到多条记录，那么当前用户套餐为自选组合套餐
                                    }
                                    logger.debug("222cncccn---->afterPrice"+afterPrice);

                                    if (null == beforePrice || null == afterPrice){
                                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【100-"+userDiscntsDataset.size()+"-"+beforePrice+"-"+afterPrice+"】用户套餐暂不满足该营销活动的办理条件！");
                                    }else {
                                        BigDecimal beforePriceInt = new BigDecimal(beforePrice);
                                        BigDecimal afterPriceInt = new BigDecimal(afterPrice);
                                        logger.debug("111cncccn---->beforePriceInt"+beforePriceInt);
                                        logger.debug("111cncccn---->afterPriceInt"+afterPriceInt);

                                        if (afterPriceInt.intValue() - beforePriceInt.intValue() >0){// 主套餐价格升档，允许办理
                                            // 用户办理当天已有升档操作，再进一步的确定之前套餐的档次；和办理之后的套餐档次
                                            String beforeLevel = "0";
                                            if (0<= beforePriceInt.intValue() && beforePriceInt.intValue()<98){// 小于98档次
                                                beforeLevel = "0";
                                            }
                                            if (98<= beforePriceInt.intValue() && beforePriceInt.intValue()<128){// 98档次
                                                beforeLevel = "1";
                                            }
                                            if (128<=beforePriceInt.intValue() && beforePriceInt.intValue()<158){//128档次
                                                beforeLevel = "2";
                                            }
                                            if (158<=beforePriceInt.intValue() && beforePriceInt.intValue()<198){//158档次
                                                beforeLevel = "3";
                                            }
                                            if (198<=beforePriceInt.intValue() && beforePriceInt.intValue()<288){//198档次
                                                beforeLevel = "4";
                                            }
                                            if (288<=beforePriceInt.intValue() && beforePriceInt.intValue()<388){//288档次
                                                beforeLevel = "5";
                                            }
                                            if (388<=beforePriceInt.intValue() && beforePriceInt.intValue()<588){//388档次
                                                beforeLevel = "6";
                                            }
                                            if (588<=beforePriceInt.intValue()){//588档次
                                                beforeLevel = "7";
                                            }

                                            String afterLevel = "0";
                                            if (0<= afterPriceInt.intValue() && afterPriceInt.intValue()<=98){// 小于98档次
                                                afterLevel = "0";
                                            }
                                            if (98<= afterPriceInt.intValue() && afterPriceInt.intValue()<128){// 98档次
                                                afterLevel = "1";
                                            }
                                            if (128<=afterPriceInt.intValue() && afterPriceInt.intValue()<158){//128档次
                                                afterLevel = "2";
                                            }
                                            if (158<=afterPriceInt.intValue() && afterPriceInt.intValue()<198){//158档次
                                                afterLevel = "3";
                                            }
                                            if (198<=afterPriceInt.intValue() && afterPriceInt.intValue()<288){//198档次
                                                afterLevel = "4";
                                            }
                                            if (288<=afterPriceInt.intValue() && afterPriceInt.intValue()<388){//288档次
                                                afterLevel = "5";
                                            }
                                            if (388<=afterPriceInt.intValue() && afterPriceInt.intValue()<588){//388档次
                                                afterLevel = "6";
                                            }
                                            if (588<=afterPriceInt.intValue()){//588档次
                                                afterLevel = "7";
                                            }

                                            // 在 1205 规则中查找出小于等beforeLevel的营销包和大于afterLevel的营销包
                                            IDataset brePackages = CommparaInfoQry.qryUserSalePackage(productId, packageId,beforeLevel,afterLevel);

                                            if (IDataUtil.isNotEmpty(brePackages)){
                                                logger.debug("111cncccn---->brePackages"+brePackages);
                                                String level = brePackages.first().getString("PARA_CODE5");
                                                if (level.compareTo(beforeLevel)>0){// 档次高于办理之前的套餐档次，拦截不给办理
                                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1004】"+brePackages.first().getString("PARA_CODE6"));
                                                }else {// 档次低于等于之前办理的档次，拦截不给办理
                                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1005】用户暂不满足该营销活动的办理条件！");
                                                }
                                            }

                                        }else {// 没有升档，不允许办理
                                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1006】用户套餐暂不满足该营销活动的办理条件。");
                                        }
                                    }

                                }else {// 找不到价格表的情况处理
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1007】用户套餐暂不满足该营销活动的办理条件！");
                                }
                            }else {// 用户主套餐资料表异常。
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191205", "【1008】用户暂不满足该营销活动的办理条件！");
                            }
                        }
                    }
                    // 此时需要把1302规则里面的包都拦截
                    if (IDataUtil.isNotEmpty(commParaInfo1302)){
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191204", "【2002】用户暂不满足该营销活动的办理条件！");
                    }
                }
            }
        }

        //REQ201911220023 泛渠道拓展合作商家系统配置需求 --start
        IDataset commParaInfo1204 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1204", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo1204)){
        	 IDataset avgFeeInfo = MvelMiscQry.getUserAvgPayFee(userId, "3"); // 近三月月均
             if (IDataUtil.isNotEmpty(avgFeeInfo)) {
                 String avgFee = avgFeeInfo.first().getString("PARAM_CODE");
                 String minfee = commParaInfo1204.first().getString("PARA_CODE2");
                 String maxfee = commParaInfo1204.first().getString("PARA_CODE4");

                 BigDecimal minfee2 = new BigDecimal(minfee);
                 BigDecimal maxfee2 = new BigDecimal(maxfee);

                 if(new BigDecimal(avgFee).compareTo(minfee2)<0||new BigDecimal(avgFee).compareTo(maxfee2)>0){
                	 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20191204", "用户暂不满足该营销活动的办理条件");
                 }


             }

        }
        //REQ201911220023 泛渠道拓展合作商家系统配置需求 --end
        IDataset commParaInfo6700 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "6700", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo6700)){
        	
        	String paraCode4 = commParaInfo6700.first().getString("PARA_CODE4");//开通亲亲网业务的30天内校验开关
        	if("Y".equals(paraCode4)){
        		IDataset allRelaUUInfo = RelaUUInfoQry.qryAllRelaUUByUidB2(userId, "45",Route.CONN_CRM_CG);
        		if(allRelaUUInfo.size()>1){
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907030,"主号码首次开通亲亲网业务的30天内才能成功办理活动！");
        		}else{
        			IDataset result = RelaUUInfoQry.getUserRelationByUserIdBRe(userId, "45");
        	        if (IDataUtil.isNotEmpty(result))
        	        {
        	        	if(!"1".equals(result.first().getString("ROLE_CODE_B"))){
        	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907030,"主号码首次开通亲亲网业务的30天内才能成功办理活动！");
        	        	}else{
        	        		String startDate = result.first().getString("START_DATE");
            	        	String end = SysDateMgr.addDays(startDate,30);
            	        	Date now = new Date();
            	        	String sysdate = SysDateMgr.date2String(now, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            	        	if(sysdate.compareTo(end)>0){
            	        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907030,"主号码首次开通亲亲网业务的30天内才能成功办理活动！");
            	        	}
        	        	}
        	        }else{
        	        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201907030,"主号码首次开通亲亲网业务的30天内才能成功办理活动！");
        	        }
        		}
        		
        	}
        	
        }
        
//      BUS201912190011关于开发首开亲亲网1元享活动的需求规则开发
//      1、用户在至今3个月内，只唯一办理过一次主号码亲亲网，且办理在30天内。
        if(IDataUtil.isNotEmpty(commParaInfo6700)){// 继续沿用上面的6700

          String paraCode5 = commParaInfo6700.first().getString("PARA_CODE5");// 校验开关
          if("1".equals(paraCode5)){// 有效，需要校验
              // 查询距离现在3个月内有开通办理亲亲网主号的记录
              IDataset mainNumberForThreeMonth = RelaUUInfoQry.qryMainNumberForThreeMonth(userId, "45",Route.CONN_CRM_CG);
              if (IDataUtil.isNotEmpty(mainNumberForThreeMonth)){
                  if (mainNumberForThreeMonth.size()>1){// 3个月内有多次开通多个主号记录
                      BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201912230,"3个月内主号码开通过多次亲亲网业务，不能办理该活动！");
                  }else {// 3个月内只有一条主号码开通亲亲网业务且当前依旧有效
                      String endDate = mainNumberForThreeMonth.first().getString("END_DATE");
                      Date now = new Date();
                      String sysdate = SysDateMgr.date2String(now, SysDateMgr.PATTERN_STAND);

                      if (sysdate.compareTo(endDate)>=0){// 当前时间超过有效时间
                          BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201912231,"用户亲亲网主号业务已取消，不符合本次活动的办理条件！");
                      }else {
                          String startDate = mainNumberForThreeMonth.first().getString("START_DATE");
                          String end = SysDateMgr.addDays(startDate,30);

                          if(sysdate.compareTo(end)>=0){// 当前时间超过开通时间后的30天
                              BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201912232,"主号码开通亲亲网业务30天内才能办理该活动！");
                          }
                      }
                  }
              }else {// 3个月内没有开通办理主号码的亲亲网业务
                  BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201912233,"客户未开通亲亲网主号业务，需先开通并符合条件才可办理本活动！");
              }

          }
       }
      
        logger.debug("客户群校验开始"+System.currentTimeMillis());
        //不是目标客户群不能办理活动@tanzheng 20190813
        IData param2256 = new DataMap();
        param2256.put("SUBSYS_CODE", "CSM");
        param2256.put("PARAM_ATTR", "2256");
        param2256.put("PARAM_CODE", "SALE_ACTIVE_LIMIT");
        param2256.put("PARA_CODE2", productId);
        param2256.put("PARA_CODE3", packageId);
        IDataset commParaInfo2256 = CommparaInfoQry.getCommparaInfoByPara(param2256);//("CSM", "2256", productId, packageId);
        if(IDataUtil.isNotEmpty(commParaInfo2256)){
        	UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
        	String troopId = commParaInfo2256.first().getString("PARA_CODE1");
        	String psptId = ucaData.getCustomer().getPsptId();
        	IDataset result = TroopMemberInfoQry.queryCountByTroopIdAndPspt(troopId, psptId);
			if (IDataUtil.isNotEmpty(result) && result.size()>0) {
				param2256.remove("PARA_CODE2");
				param2256.remove("PARA_CODE3");
				IDataset commParaInfo2256All = CommparaInfoQry.getCommparaInfoByPara(param2256);//("CSM", "2256", productId, packageId);
				IDataset custInfoPspt = CustomerInfoQry.getCustIdByPspt(psptId);
				
				boolean loopFlag = true ;
				if (IDataUtil.isNotEmpty(custInfoPspt))
		        {
		        	IData param = new DataMap();
		        	//循环cust
		            for (int k = 0; k < custInfoPspt.size()&&loopFlag; k++)
		            {
		            	String custId = custInfoPspt.getData(k).getString("CUST_ID");
		                // 查在线表
		                IDataset userInfos = UserInfoQry.getUserInfoByCusts(custId);
		                if (userInfos.size() > 0)
		                {
		                	//循环user
		                	 for (int p = 0; p < userInfos.size()&&loopFlag; p++){
		                		 
		                		 param.put("USER_ID", ((IData)userInfos.get(p)).getString("USER_ID"));
		                		 
		                		 for(int q = 0; q < commParaInfo2256All.size()&&loopFlag; q++){
		                			 
		                			 param.put("PRODUCT_ID", commParaInfo2256All.getData(q).getString("PARA_CODE2"));

		                			 param.put("PACKAGE_ID", commParaInfo2256All.getData(q).getString("PARA_CODE3"));
		                			 IDataset userDiscnts = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_VALIDSALEACTIVE_PRODUCTID", param);
		                			 if(IDataUtil.isNotEmpty(userDiscnts)&&userDiscnts.size()>0){
		                				 loopFlag = false ;
		                				 BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190813,"该用户证件下已有其他号码办理该类活动");
		                	             logger.error("USER_ID:"+((IData)userInfos.get(p)).getString("USER_ID")
		                	            		 +">>PRODUCT_ID:"+commParaInfo2256All.getData(q).getString("PARA_CODE2")
		                	            		 +">>PACKAGE_ID:"+commParaInfo2256All.getData(q).getString("PARA_CODE3"));
		                				 
		                			 }
		                		 }
		                		 
					          }
		                }
		                
		            }
		        }
				
				
				
				
			}else{
				BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190813,"不是目标客户群不可以办理");
            	logger.error("user_id:"+userId+">>troop_id:"+troopId+">>pspt_id:"+psptId);
			}
        }
        logger.debug("客户群校验结束"+System.currentTimeMillis());
        
        
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPackageExtConfig() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
    
    public static  boolean StringCompareString (String str,String allStr){
		if(StringUtils.isBlank(str)||StringUtils.isBlank(allStr)){
			return false;
		}
		
		boolean flag = false;
		for(String s:allStr.split("\\|")){
			 if(str.equals(s)){
				 flag = true;
				 break;
			 }
		 }
		
		return flag;
	}
   
}
