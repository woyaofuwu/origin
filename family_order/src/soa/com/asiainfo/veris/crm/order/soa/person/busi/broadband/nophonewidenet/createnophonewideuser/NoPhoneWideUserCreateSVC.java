
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class NoPhoneWideUserCreateSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     *  根据宽带类型查询宽带产品
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData getWidenetProductInfoByWideType(IData input) throws Exception
    {
        IData resultData = new DataMap();

        String wideProductType = input.getString("wideProductType");

        IDataset wideModemStyleDataset = StaticUtil.getStaticList("WIDE_MODEM_STYLE");

        //如果营业员没有  光猫自备模式权限，则过滤掉此模式
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_MODEM_STYLE_3"))
        {
            if (IDataUtil.isNotEmpty(wideModemStyleDataset))
            {
                for (int i = 0; i < wideModemStyleDataset.size(); i++)
                {
                    if ("3".equals(wideModemStyleDataset.getData(i).getString("DATA_ID")))
                    {
                        wideModemStyleDataset.remove(i);
                        break;
                    }
                }
            }
        }
        //如果营业员没有“光猫赠送模式权限”，则过滤掉此模式
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"))
        {
            if (IDataUtil.isNotEmpty(wideModemStyleDataset))
            {
                for (int j = 0; j < wideModemStyleDataset.size(); j++)
                {
                    if ("2".equals(wideModemStyleDataset.getData(j).getString("DATA_ID")))
                    {
                        wideModemStyleDataset.remove(j);
                        break;
                    }
                }
            }
        }

        resultData.put("WIDE_MODEM_STYLE", wideModemStyleDataset);

        String productMode = "";

        if ("2".equals(wideProductType))
        {
            // adsl
            productMode = "18";
        }
        else if ("3".equals(wideProductType))
        {
            // FTTH
            productMode = "21";
        }
        else if ("5".equals(wideProductType))
        {
            // 铁通FTTH
            productMode = "23";
        }
        else  if ("6".equals(wideProductType))
        {
            //铁通FTTB
            productMode = "24";
        }
        else
        {
            //移动FTTB
            productMode = "22";
        }

        IDataset productList = ProductInfoQry.getWidenetProductInfo(productMode, CSBizBean.getTradeEparchyCode());

        //如果是两城两宽，根据入参中NET_WIDE过滤产品
        String netWide = input.getString("NET_WIDE").replace("M","");
        String wideTag = input.getString("TWO_CITY_WIDENET_FLAG");

        if("1".equals(wideTag))
        {
            if(IDataUtil.isNotEmpty(productList))
            {
                for (int i = 0; i < productList.size(); i++)
                {
                    String productId = productList.getData(i).getString("PRODUCT_ID");

                    //过滤非两城两宽产品
					IDataset isTwoCityWidenet = CommparaInfoQry.getCommpara("CSM", "6801", productId, CSBizBean.getVisit().getStaffEparchyCode());
					
					if(IDataUtil.isEmpty(isTwoCityWidenet) && isTwoCityWidenet.size() <= 0)
					{
					    productList.remove(i);
					    i = i - 1;
					    continue;
					}

                    IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");

                    String wideRate = null;
                    if (IDataUtil.isNotEmpty(forceElements))
                    {
                        IDataset rate_ds = null;
                        IData forceElement = null;

                        for (int j = 0; j < forceElements.size(); j++)
                        {
                            forceElement = forceElements.getData(j);

                            if ("S".equals(forceElement.getString("OFFER_TYPE")))
                            {
                                //根据产品下的服务ID查询宽带速率
                                rate_ds = CommparaInfoQry.getCommpara("CSM", "4000", forceElement.getString("OFFER_CODE"), "0898");

                                if (IDataUtil.isNotEmpty(rate_ds))
                                {
                                    break;
                                }
                            }
                        }
                        //根据预受理已选速率过滤产品
                        if (IDataUtil.isNotEmpty(rate_ds))
                        {
                            String wideRateString = rate_ds.getData(0).getString("PARA_CODE1","");
                            productList.getData(i).put("WIDE_RATE", wideRateString);
                            wideRate = String.valueOf(Integer.valueOf(wideRateString)/1024);
                        }
                    }
                    if(!netWide.equals(wideRate))
                    {
                        productList.remove(i);
                        i = i - 1;
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < productList.size(); i++)
            {
                String productId = productList.getData(i).getString("PRODUCT_ID");

                //过滤两城两宽产品
				IDataset isTwoCityWidenet = CommparaInfoQry.getCommpara("CSM", "6801", productId, CSBizBean.getVisit().getStaffEparchyCode());
				
				if(IDataUtil.isNotEmpty(isTwoCityWidenet) && isTwoCityWidenet.size() > 0)
				{
				    productList.remove(i);
				    i = i - 1;
				}
            }
        }
        resultData.put("PRODUCT_LSIT", productList);
        return resultData;
    }


    /**
     *  获得无手机开户证件类型列表
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryPsptTypeList(IData input) throws Exception
    {
        IData rtnData=new DataMap();

        IDataset allPsptTypeList =  StaticUtil.getStaticList("TD_S_PASSPORTTYPE2");

        IDataset psptTypeList = new DatasetList();

        //获得配置参数中前台可展示的证件类型
        IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "198", "PSPT_TYPE_LIST", "0898");

        if (IDataUtil.isNotEmpty(paras))
        {
            String psptIds = paras.getData(0).getString("PARA_CODE1");

            if (StringUtils.isNotBlank(psptIds))
            {
                for (int i = 0; i < allPsptTypeList.size(); i++)
                {
                    if (psptIds.indexOf(allPsptTypeList.getData(i).getString("DATA_ID")) > -1)
                    {
                        psptTypeList.add(allPsptTypeList.getData(i));
                    }
                }
            }
            else
            {
                psptTypeList = allPsptTypeList;
            }
        }
        else
        {
            psptTypeList = allPsptTypeList;
        }

        rtnData.put("PSPT_TYPE_LIST", psptTypeList);

        return rtnData;
    }


    /**
     * 开户数限制校验
     *
     * @author yuyj3
     * @param input
     * @throws Exception
     */
    public IDataset checkRealNameLimitByPspt(IData input) throws Exception
    {
        IDataset ajaxDataset = new DatasetList();
        String psptId = input.getString("PSPT_ID").trim();

        if (StringUtils.isNotBlank(psptId))
        {
            //校验标记位
            boolean flag = false;

            //证件号码前缀校验配置参数
            IDataset prefixParas = CommparaInfoQry.getCommparaAllCol("CSM", "198", "PSPT_ID_PREFIX", "0898");

            IData ajaxData = new DataMap();

            //如果没有则不进行校验
            if (IDataUtil.isNotEmpty(prefixParas))
            {
                String psptIdPrefix = prefixParas.getData(0).getString("PARA_CODE1");

                if (StringUtils.isNotBlank(psptIdPrefix))
                {
                    if (psptId.startsWith(psptIdPrefix))
                    {
                        ajaxData.put("MSG", "无手机宽带开户证件号码不能以["+psptIdPrefix +"]开头，请更换其它证件！");
                        ajaxData.put("CODE", "1");

                        flag = true;
                    }
                }
            }

            if (!flag)
            {
                IDataset noPhoneTradeInfos = TradeInfoQry.queryTradeInfoByCodeAndRsrv1("680", psptId);

                if (IDataUtil.isNotEmpty(noPhoneTradeInfos))
                {
                    ajaxData.put("MSG", "该证件号码有一笔未完工的无手机宽带开户订单，请更换其它证件！");
                    ajaxData.put("CODE", "1");

                    flag = true;
                }
            }

            if (!flag)
            {
                int rCount = UserInfoQry.getRealNameUserCountByNoPhoneWidenet(psptId);

                //默认只能开一个
                int rLimit = 1;

                //获得配置参数一个证件号码可开户的个数
                IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "198", "PSPT_ID_CREATE_USER_NUM", "0898");

                if (IDataUtil.isNotEmpty(paras))
                {
                    rLimit = Integer.valueOf(paras.getData(0).getString("PARA_CODE1"));
                }


                ajaxData.put("rCount", rCount);
                ajaxData.put("rLimit", rLimit);
                if (rCount < rLimit)
                {
                    ajaxData.put("MSG", "OK");
                    ajaxData.put("CODE", "0");
                }
                else
                {
                    ajaxData.put("MSG", "证件号码【" + psptId + "】实名制无手机宽带开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                    ajaxData.put("CODE", "1");
                }
            }

            ajaxDataset.add(ajaxData);

        }
        return ajaxDataset;
    }

    /**
     *  获得光猫费用
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData getModemDeposit(IData input) throws Exception
    {
        IData rtnData=new DataMap();

        int modemDepositFee = 0;

        //2.获取押金金额commpara表param_attr=6131
        IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", "2", "0898");

        String deposit = paras.getData(0).getString("PARA_CODE1");

        modemDepositFee = Integer.parseInt(deposit);


        rtnData.put("MODEM_DEPOSIT", modemDepositFee);

        return rtnData;
    }



    /**
     * 校验录入的宽带账号是否可用
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkWidenetAcctId(IData input) throws Exception
    {
        IData inParam = new DataMap();

        String widenetAcctId = input.getString("WIDENET_ACCT_ID");

        String oldWidenetAcctId = input.getString("OLD_WIDENET_ACCT_ID");

        inParam.put("ACCOUNT_ID", widenetAcctId);

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);

        IDataset wideAcctInfos = bean.queryWideNetAccoutInfo(inParam);

        if (IDataUtil.isNotEmpty(wideAcctInfos))
        {
            if (wideAcctInfos.size() > 1)
            {
                CSAppException.appError("-1", "该宽带账号信息配置错误，存在多条记录，请联系管理员！");
            }

            String state = wideAcctInfos.getData(0).getString("STATE");

            //0 为空闲可用状态
            if (!"0".equals(state))
            {
                CSAppException.appError("-1", "该宽带账号状态不可用["+StaticUtil.getStaticValue("WIDE_ACCONT_STATE", state)+"]，请录入其它宽带账号！");
            }

            //新录入的宽带账号能查询到有效的用户信息则报错
            IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + widenetAcctId);

            if (IDataUtil.isNotEmpty(userInfo))
            {
                CSAppException.appError("-1", "该宽带账号已经被使用，请录入其它宽带账号！");
            }

            //全部校验通过后将新账号修改为预占
            bean.updateWideNetAccoutState(widenetAcctId,"1",SysDateMgr.getSysTime(),null,null,null);

            if (StringUtils.isNotBlank(oldWidenetAcctId))
            {
                //如果存在老账号则重新将老账号改为空闲
                bean.updateWideNetAccoutState(oldWidenetAcctId,"0","",null,null,null);
            }
        }
        else
        {
            CSAppException.appError("-1", "该宽带账号信息不存在，请重新录入！");
        }

        return inParam;
    }


    /**
     * 随机获得有效可用的宽带账号
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData getValidWideNetAccountId(IData input) throws Exception
    {
        IData resultData = new DataMap();

        String oldWidenetAcctId = input.getString("OLD_WIDENET_ACCT_ID");

        String validWidenetAcctId = "";

        //标记是否已找到可使用的有效的宽带号码
        boolean flag = false;

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);

        //可能存在这种场景，宽带号码表里面标记为0 可用的宽带号码，但是这个号码在宽带用户资料表里面却是有效的，导致下面的逻辑进入无限循环。所以控制最多循环100次，则抛异常。
        int i = 0;

        do
        {
            //查询空闲可用的宽带账号
            IDataset wideAcctInfos = bean.getValidWideNetAccount();

            i++;

            if (IDataUtil.isNotEmpty(wideAcctInfos))
            {
                resultData = wideAcctInfos.getData(0);

                String widenetAcctId = resultData.getString("ACCOUNT_ID");

                //新录入的宽带账号能查询到有效的用户信息则报错
                IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + widenetAcctId);

                if (IDataUtil.isEmpty(userInfo))
                {
                    validWidenetAcctId = widenetAcctId;

                    flag =true;
                }

                if (i > 100)
                {
                    CSAppException.appError("-1", "已经没有可用的宽带账号资源，请联系管理员！");
                }
            }
            else
            {
                CSAppException.appError("-1", "已经没有可用的宽带账号资源，请联系管理员！");
            }

        }while(!flag);


        if (StringUtils.isNotBlank(validWidenetAcctId))
        {
            //全部校验将新账号修改为预占
            bean.updateWideNetAccoutState(validWidenetAcctId,"1",SysDateMgr.getSysTime(),null,null,null);
        }

        if (StringUtils.isNotBlank(oldWidenetAcctId))
        {
            //如果存在老账号则重新将老账号改为空闲
            bean.updateWideNetAccoutState(oldWidenetAcctId,"0","",null,null,null);
        }


        return resultData;
    }

    /**
     * 释放宽带账号
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData releaseWideNetAcct(IData input) throws Exception
    {
        IData resultData = new DataMap();

        String oldWidenetAcctId = input.getString("OLD_WIDENET_ACCT_ID");

        if (StringUtils.isNotBlank(oldWidenetAcctId))
        {
            NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);

            IData inParam = new DataMap();

            inParam.put("ACCOUNT_ID", oldWidenetAcctId);
            inParam.put("STATE", "1");

            IDataset widenetAccoutInfos = bean.queryWideNetAccoutInfo(inParam);

            //只将状态为1 预占的 释放，  因为业务提交成功后，会刷新页面可能也会调用到此方法
            if (IDataUtil.isNotEmpty(widenetAccoutInfos))
            {
              //如果存在老账号则重新将老账号改为空闲
                bean.updateWideNetAccoutState(oldWidenetAcctId,"0","",null,null,null);
            }
        }

        return resultData;
    }


    /**
     * 提交前费用校验
     * @param param
     * @throws Exception
     * @author zhangyc5
     */
    public IData checkFeeBeforeSubmit(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String modemDeposit = param.getString("MODEM_DEPOSIT","0");
    	String topSetBoxDeposit = param.getString("TOPSETBOX_DEPOSIT","0");
    	String saleActiveFee = param.getString("SALE_ACTIVE_FEE","0");
    	String topSetBoxSaleActiveFee = param.getString("TOPSETBOX_SALE_ACTIVE_FEE","0");
    	String serialNumber = param.getString("SERIAL_NUMBER","");

        String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);

        int allTotalTransFee = Integer.parseInt(modemDeposit)*100 + Integer.parseInt(topSetBoxDeposit)*100 + Integer.parseInt(saleActiveFee) + Integer.parseInt(topSetBoxSaleActiveFee);

        if(Integer.parseInt(leftFee)< allTotalTransFee )
        {
            CSAppException.appError("61314", "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[光猫押金金额：" + Double.parseDouble(modemDeposit)+"元，魔百和押金金额："
            			+ Double.parseDouble(topSetBoxDeposit) + "元,宽带营销活动费用金额：" + Double.parseDouble(saleActiveFee)/100+"元,"
            			+ "魔百和营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee)/100 + "元]");
        }

    	result.put("X_RESULTCODE", "0");

    	return result;
    }

    /**
     * 获取魔百和信息
     * @param param
     * @throws Exception
     * @author zhengkai5
     */
    public IDataset loadTopSetBoxInfo(IData param) throws Exception {
    	IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "680");
    	return topSetBoxProducts;
    }

    /**
     * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * @param input
     * @throws Exception
     * @author zhengkai5
     */
    public IData queryTopSetBoxDiscntPackagesByPID(IData input) throws Exception
    {
        IData retData = new DataMap();
        String topSetBoxProductId = input.getString("TOP_SET_BOX_PRODUCT_ID");

        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID");

        String serialNumber = input.getString("serialNumber");

        //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
        String houNiaoTag = input.getString("HOU_NIAO_TAG","false");
        //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求

        
        //光猫押金
        String modemDeposit = input.getString("MODEM_DEPOSIT","");

        if (StringUtils.isBlank(modemDeposit))
        {
            modemDeposit = "0";
        }

        if (StringUtils.isNotBlank(topSetBoxProductId))
        {
            //获取费用信息
            String topSetBoxDeposit = "20000";

            IDataset topSetBoxDepositDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");

            if(IDataUtil.isNotEmpty(topSetBoxDepositDatas))
            {
                topSetBoxDeposit = topSetBoxDepositDatas.getData(0).getString("PARA_CODE1","20000");
            }

            //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求
            if ("true".equals(houNiaoTag))
            {
            	topSetBoxDeposit = "10000";
            }
            //add by zhangxing3 for BUS201907300031新增度假宽带季度半年套餐开发需求

            //如果选择了魔百和营销活动，则不需要缴纳魔百和押金
            if (StringUtils.isNotBlank(topSetBoxSaleActiveId))
            {
                topSetBoxDeposit = "0";
            }

            //用户余额校验，校验用户余额是否够交纳各类押金     中间不需要校验，提交的时候一次性校验
//            retData = userBalanceCheck(serialNumber, Integer.valueOf(topSetBoxDeposit), Integer.valueOf(modemDeposit)*100);

            retData.put("resultCode", "0");
            
            //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", topSetBoxProductId, "0898");
            if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
            {
            	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
            		if(PARA_CODE2.equals("IPTV")){
            			topSetBoxDeposit = "10000";
            		}
            	}
            }
            //end

            //魔百和押金
            retData.put("TOP_SET_BOX_DEPOSIT", topSetBoxDeposit);

            IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(topSetBoxProductId);

            // 基础服务包
            retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));

            // 可选服务包
            retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P"));

            IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "0");


            topSetBoxSaleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList);
            //start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
            retData.put("resultIPTVCode", "0");
            retData.put("resultIPTVInfo", "");
            IDataset isIPTV=CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", topSetBoxProductId, "0898");
            if(IDataUtil.isNotEmpty(isIPTV))
            {
    			String wideProductType = input.getString("WIDE_PRODUCT_TYPE","");//宽带类型
    			String wideProductId = input.getString("WIDE_PRODUCT_ID","");//宽带产品档次

    			if(null != wideProductType && !"".equals(wideProductType)){
    				if (StringUtils.equals("1", wideProductType) || StringUtils.equals("6", wideProductType))
                    {
    					retData.put("resultIPTVCode", "-1");
    					retData.put("resultIPTVInfo", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
                    }
    			}else{
    				retData.put("resultIPTVCode", "-1");
					retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品类型！");
    			}
    			if(null != wideProductId && !"".equals(wideProductId)){
    				
    				IDataset ftthkddc=CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", wideProductId, "0898");//查询宽带FTTH档次是不是50M以下
                    if(IDataUtil.isNotEmpty(ftthkddc)){
                    	retData.put("resultIPTVCode", "-1");
    					retData.put("resultIPTVInfo", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
                    }
    			}else{
    				retData.put("resultIPTVCode", "-1");
					retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品！");
    			}
    			retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
            	if(retData.getString("resultIPTVCode").equals("-1")){
            		 retData.put("B_P", new DataMap());
                     retData.put("O_P", new DataMap()); 
                     retData.put("TOP_SET_BOX_DEPOSIT", "0");
            	}
            }
            //end-wangsc10-20190326

            //魔百和营销活动
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", topSetBoxSaleActiveList);
        }
        else
        {
            retData.put("B_P", new DataMap());
            retData.put("O_P", new DataMap());
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
        }

        return retData;
    }

    /**
     * 营销活动依赖校验
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSaleActiveDependence(IData input) throws Exception
    {
        IData resultData = new DataMap();

        String resultCode = "0";

        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID","");

        IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");

        if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos))
        {
            IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();

            //所依赖的宽带1+营销活动虚拟ID
            String paraCode25 = topSetBoxCommparaInfo.getString("PARA_CODE25");

            //不为空，则依赖于宽带营销活动
            if (StringUtils.isNotBlank(paraCode25))
            {
                resultCode = "-1";
//                CSAppException.appError("-1", "魔百和营销活动依赖于当前取消的宽带1+营销活动，魔百和营销活动将同时被取消！");
            }
        }

        resultData.put("RESULT_CODE", resultCode);

        return resultData;
    }

    /**
     * 营销活动费用校验
     * 宽带开户营销活动及魔百和营销活动暂时只支持转账类的预存营销活动，如果配置的不是转账类的预存营销，则报错
     * 营销活动转入转出存折走费用配置表TD_B_PRODUCT_TRADEFEE IN_DEPOSIT_CODE,OUT_DEPOSIT_CODE
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCheckSaleActiveFee(IData param) throws Exception{
    	IData result = new DataMap();
    	int totalFee = 0 ;

    	String activeFlag = param.getString("ACTIVE_FLAG") ; //活动标记，1：宽带营销活动，2：魔百和营销活动
    	String productId = param.getString("PRODUCT_ID","");
    	String packageId = param.getString("PACKAGE_ID","");

    	//查询营销包下面所有默认必选元素费用
    	IDataset businessFee = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId);

        if (IDataUtil.isEmpty(businessFee))
        {
          //如果该营销活动未配置费用，则直接返回成功
            result.put("SALE_ACTIVE_FEE", "0");
            result.put("X_RESULTCODE", "0");
            return result;
        }

        for(int j = 0 ; j < businessFee.size() ; j++)
        {
             IData feeData = businessFee.getData(j);

            String payMode = feeData.getString("PAY_MODE");
            String feeMode = feeData.getString("FEE_MODE");
            String fee = feeData.getString("FEE");

            if(fee != null && !"".equals(fee) && Integer.parseInt(fee) >0)
            {
                if(!"1".equals(payMode))
                {
                    //付费模式非转账报错
                    String errorMsg = "";
                    if("1".equals(activeFlag))
                    {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽带营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]营销活动";
                    }
                    else
                    {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户魔百和营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode)+ "]营销活动";
                    }
                    CSAppException.appError("61312", errorMsg);
                }

                if(!"2".equals(feeMode))
                {
                    //费用类型非预存费报错
                    String errorMsg = "";
                    if("1".equals(activeFlag))
                    {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户营销活动费用类型暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
                    }
                    else
                    {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户魔百和营销活动暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" :"营业费") + "]类型";
                    }
                    CSAppException.appError("61313", errorMsg);
                }
            }

            totalFee += Integer.parseInt(fee);
        }

    	result.put("SALE_ACTIVE_FEE", totalFee);
    	result.put("X_RESULTCODE", "0");

    	return result;
    }

    public String getPayModeName(String payMode)
    {
    	String payModeName = "" ;
    	if(payMode == null || "".equals(payMode))
    	{
    		payModeName = "未知类型";
    		return payModeName;
    	}
    	if("0".equals(payMode))
    	{
    		payModeName = "现金";
    	}
    	else if("1".equals(payMode))
    	{
    		payModeName = "转账";
    	}
    	else if("2".equals(payMode))
    	{
    		payModeName = "可选现金、转账";
    	}
    	else if("3".equals(payMode))
    	{
    		payModeName = "清退";
    	}
    	else if("4".equals(payMode))
    	{
    		payModeName = "分期付款";
    	}
    	return payModeName;
    }

    /**
     * 校验用户选中的是否是包年优惠
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSelectedDiscnts(IData input) throws Exception
    {
        IData resultData = new DataMap();

        //用户已选择优惠ID
        String discntIds = input.getString("DISCNT_IDS");

        //默认是校验通过
        String resultCode = "0";

        String discntCode = "";

        if (StringUtils.isNotBlank(discntIds))
        {
            //查询出参数中配置的宽带包年优惠
            IDataset discntDataset = CommparaInfoQry.getCommNetInfo("CSM", "532", "600");

            if (IDataUtil.isNotEmpty(discntDataset))
            {
                IData discntInfo = null;

                for (int i = 0; i < discntDataset.size(); i++)
                {
                    discntInfo = discntDataset.getData(i);

                    //如果用户订购了宽带包年资费
                    if (discntIds.contains(discntInfo.getString("PARA_CODE1")))
                    {
                        resultCode = "-1";
                        discntCode = discntInfo.getString("PARA_CODE1");

                        break;
                    }
                }
            }

           //查询出参数中配置的VIP体验套餐
            IDataset discntDataset2 = CommparaInfoQry.getCommNetInfo("CSM", "532", "699");

            if (IDataUtil.isNotEmpty(discntDataset2))
            {
                IData discntInfo = null;

                for (int i = 0; i < discntDataset2.size(); i++)
                {
                    discntInfo = discntDataset2.getData(i);

                    //如果用户订购了VIP体验套餐
                    if (discntIds.contains(discntInfo.getString("PARA_CODE1")))
                    {
                        resultCode = "-2";
                        discntCode = discntInfo.getString("PARA_CODE1");

                        break;
                    }
                }
            }
        }

        resultData.put("resultCode", resultCode);
        resultData.put("DISCNT_CODE", discntCode);

        IDataset saleActiveAttrCommparaInfos = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600",null, input.getString("PACKAGE_ID",""),null, "0898");
        if(IDataUtil.isNotEmpty(saleActiveAttrCommparaInfos))
        {
        	resultData.put("FLAG", "1");
        }else
        {
        	resultData.put("FLAG", "0");
        }

        return resultData;
    }

    public IData gettopsetboxfee(IData input) throws Exception
    {
    	IData result = new DataMap();
    	IDataset results = CommparaInfoQry.getCommpara("CSM", "210","TOP_SET_BOX_FEE", "0898");
    	if(!IDataUtil.isEmpty(results))
    	{
    		result.put("PARA_CODE2", results.first().getString("PARA_CODE2","0")) ;
    	}
    	else
    	{
    		result.put("PARA_CODE2", "0");
    	}
    	return result;
    }


    public IDataset getCreateWideUserStyle(IData input) throws Exception
    {
        //宽带开户方式权限控制
        IDataset mergeWideUserStyleList = StaticUtil.getStaticList("HGS_WIDE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HGS_WIDE"))
        {
            if (IDataUtil.isNotEmpty(mergeWideUserStyleList))
            {
                for (int k = 0; k < mergeWideUserStyleList.size(); k++)
                {
                    if ("1".equals(mergeWideUserStyleList.getData(k).getString("DATA_ID")))
                    {
                        mergeWideUserStyleList.remove(k);
                        break;
                    }
                }
            }
        }
        return mergeWideUserStyleList;
    }
    public IData getProductRateByProductId(IData input) throws Exception
    {
    	IData resultData = new DataMap();
    	String productId = input.getString("NEW_PRODUCT_ID","");
        String new_rate = WideNetUtil.getWidenetProductRate(productId);
        resultData.put("NEW_RATE", new_rate);  
        return resultData;
    } 
    // 两城两宽校验北京移动号码
    public IDataset checkBJWidenetSn(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("BJ_SERIAL_NUMBER"));
        IDataset result = CSAppCall.call("SS.WidenetPreAcceptSVC.loadInfo", param);

        if (IDataUtil.isNotEmpty(result)) {
            if ("2".equals(result.first().getString("ACCEPT_TAG")))
                CSAppException.appError("-1","该北京移动号码已开户！");
        } else {
            CSAppException.appError("-1","该北京移动号码没有进行预受理！");
        }
        return result;
    }
    
    /**REQ201809300006
	  * 获取手工输入权限
    * @author wuck3
    */
   public IDataset initInputRight(IData input) throws Exception
   {
   	IData result = new DataMap();
   	IDataset dataset = new DatasetList();
   	String staffId = getVisit().getStaffId();
   	//手工输入权限
   	if(StaffPrivUtil.isFuncDataPriv(staffId, "HIGH_PRIV")){
   		result.put("INPUT_PERMISSION", "1");
       }else{
       	result.put("INPUT_PERMISSION", "0");
       }
   	// 工号是否属于自办营业厅
       IDataset zbyytSet = CParamQry.getZBYYT(getVisit().getStaffId());
   	if (IDataUtil.isNotEmpty(zbyytSet)){
          int recordcount = zbyytSet.getData(0).getInt("RECORDCOUNT", 0);
          if (recordcount != 0){	// 自办营业厅
       	  result.put("INPUT_PERMISSION", "1");
           }
   	}
   	
   	//无手机宽带开户是否需要sim卡 ，现在局方不要，免得后面需要，做个开关
    boolean simTag = BizEnv.getEnvBoolean("crm.nophoneWide.simSwitch", false); 
    if(simTag){
        result.put("INPUT_SIM_TAG", "Y");
    }else {
        result.put("INPUT_SIM_TAG", "N");
    }
    
   	dataset.add(result);
   	
   	return dataset;
   }


   public IDataset checkSerialNumber(IData input) throws Exception {
       IDataset serialNumberInfos = CSAppCall.call("SS.CreatePersonUserSVC.checkSerialNumber", input);
       if (IDataUtil.isNotEmpty(serialNumberInfos)) {
           CSAppCall.call("SS.NoPhoneWideUserCreateSVC.checkWidenetAcctIdAvailable", input);
       }
       return serialNumberInfos;
   }


    /**
     * 校验录入的宽带服务号码是否可用
     * @param input [WIDENET_ACCT_ID] 必传且不能为空
     * @return IData
     * @author ApeJungle
     */
    public IData checkWidenetAcctIdAvailable(IData input) throws Exception {
        String selectedWidenetAcctId = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String preOccupiedWidenetAcctId = input.getString("OLD_SERIAL_NUMBER", "");

        IData param = new DataMap();
        param.put("SELECTED_WIDENET_ACCTID", selectedWidenetAcctId);
        param.put("PRE_OCCUPIED_WIDENET_ACCTID", preOccupiedWidenetAcctId);

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        return bean.checkWidenetAcctIdAvailable(param);
    }

    /**
     * 页面关闭时，释放选占的号码资源
     * @param input
     * @return
     * @throws Exception
     */
    public IData releaseSelOccupiedSn(IData input) throws Exception {
        String selOccupiedWidenetAcctId = input.getString("WIDENET_ACCT_ID", "");

        IData param = new DataMap();
        param.put("SEL_OCCUPIED_WIDENET_ACCTID", selOccupiedWidenetAcctId);

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        return bean.releaseSelOccupiedSn(param);
    }
}
