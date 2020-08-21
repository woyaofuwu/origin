package com.asiainfo.veris.crm.order.soa.group.minorec.queryContract;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class ContractOfferChaListSVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    /**
     * 查询EOMS_BUSI_STATE状态编码
     * 
     * @param
     * @return
     * @throws Exception
     */
    public IData queryContractOfferChaList(IData param) throws Exception {

        IData agreementElementData = new DataMap();
        String offerCode = param.getString("OFFER_CODE");
        IDataset ContractList = new DatasetList();
        if ("8000".equals(offerCode))
        {
            ContractList = ContractOfferChaListBean.queryContractOfferChaListVw(param);
        }
        else
        {
            ContractList = ContractOfferChaListBean.queryContractOfferChaList(param);
        }

        if ("7341".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 宽带产品获取协议的产品属性等
            agreementElementData = queryOfferChaWideList(ContractList);
        }

        if ("380300".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 云wifi获取协议的产品属性等
            agreementElementData = queryOfferChaWififiList(ContractList);
        }

        if ("921015".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 云酒馆获取协议的产品属性等
        	agreementElementData = queryOfferChaTavernList(ContractList);
        }

        if ("380700".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 和商务TV云酒馆获取协议的产品属性等
            agreementElementData = queryOfferChaSumBusinessTVList(ContractList);
        }

        if ("2222".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 多媒体桌面电话 获取协议的产品属性等
            agreementElementData = queryOfferChaIMS(ContractList);
        }

        if ("8001".equals(offerCode) && IDataUtil.isNotEmpty(ContractList))
        {
            // 融合v网  获取协议的产品属性等
            // agreementElementData = queryOfferChaVpmn(ContractList);
        }

        return agreementElementData;
    }

    public IData queryOfferChaWideList(IDataset ContractList) throws Exception {

        IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IDataset mebOfferList = new DatasetList();
        IData ecOfferInfo = new DataMap();
        IDataset offerChaSpecs = new DatasetList();
        if (IDataUtil.isNotEmpty(ContractList))
        {
            for (int i = 0;i<ContractList.size();i++)
            {
                IData contractInfo = ContractList.getData(i);
                String attrCode = contractInfo.getString("ATTR_CODE");

                IData offerChaSpecsInfo = new DataMap();
                if ("EP_NUM".equals(attrCode))
                {
                    offerChaSpecsInfo.put("ATTR_CODE", "NOTIN_NUM");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("EP_TEST_FEE".equals(attrCode))
                {
                    offerChaSpecsInfo.put("ATTR_CODE", "NOTIN_INSTALLATION_COST");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("EP_WITH".equals(attrCode))
                {
                    offerChaSpecsInfo.put("ATTR_CODE", "EP_WITH");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("EP_WITH_FEE".equals(attrCode))
                {
                    offerChaSpecsInfo.put("ATTR_CODE", "EP_WITH_FEE");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
            }
        }
        ecOfferInfo.put("OFFER_ID", "110000007341");
        ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
        ecOfferList.add(ecOfferInfo);
        if (IDataUtil.isNotEmpty(ecOfferList)) {
            agreementElementData.put("EC_OFFER", ecOfferList);
        }
        if (IDataUtil.isNotEmpty(mebOfferList)) {
            agreementElementData.put("MEB_OFFER", mebOfferList);
        }

        return agreementElementData;

    }

    public IData queryOfferChaWififiList(IDataset ContractList) throws Exception {

        IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IDataset mebOfferList = new DatasetList();
        IData ecOfferInfo = new DataMap();
        IDataset offerChaSpecs = new DatasetList();
        if (IDataUtil.isNotEmpty(ContractList)) {
        	String epWifiYes = null;
            for (Object object : ContractList) {
                IData offerChaSpecsInfo = new DataMap();
                IData contractInfo = (IData) object;
                // 电子协议定义的code
                if ("EP_WIFI_FEE".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "38030000101");// x 标准资费(元/台/月)
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                if ("EP_WIFI_YES".equals(contractInfo.getString("ATTR_CODE"))) {
                	//电子协议中是否订购了云WiFi
                	 epWifiYes = contractInfo.getString("ATTR_VALUE");
                }
            }
            
			if (!"1".equals(epWifiYes)) {
				return agreementElementData;
			}
        }
        if (IDataUtil.isNotEmpty(offerChaSpecs)) {
            ecOfferInfo.put("OFFER_ID", "130038030011");
            ecOfferInfo.put("GROUP_ID", "3803001");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
            ecOfferList.add(ecOfferInfo);
        }

        if (IDataUtil.isNotEmpty(ecOfferList)) {
            agreementElementData.put("EC_OFFER", ecOfferList);
        }
        if (IDataUtil.isNotEmpty(mebOfferList)) {
            agreementElementData.put("MEB_OFFER", mebOfferList);
        }

        return agreementElementData;
    }

    /**
     * @Title:queryOfferChaTavernList
     * @Description:从电子协议中获取云酒馆产品参数值与资费产品参数值，回填到页面的数据集
     * @param @param contractList 电子协议所有内容
     * @param @return
     * @return IData
     * @throws
     */
    private IData queryOfferChaTavernList(IDataset contractList) {
    	IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IDataset mebOfferList = new DatasetList();
        IData ecOfferInfo1 = new DataMap();
        IDataset offerChaSpecs1 = new DatasetList();
        IData ecOfferInfo2 = new DataMap();
        IDataset offerChaSpecs2 = new DatasetList();
        IData ecOfferInfo3 = new DataMap();
        IDataset offerChaSpecs3 = new DatasetList();
        IData ecOfferInfo4 = new DataMap();
        IDataset offerChaSpecs4 = new DatasetList();
        IData ecOfferInfo5 = new DataMap();
        IDataset offerChaSpecs5 = new DatasetList();
        String cwMonthfee = null;//月功能费
        if (IDataUtil.isNotEmpty(contractList)) {
            for (Object object : contractList) {
                IData offerChaSpecsInfo = new DataMap();
                IData contractInfo = (IData) object;
                //电子协议定义的code
                if ("CW_ONEFEE".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "921001500101");//酒店实施费
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs1.add(offerChaSpecsInfo);
                }
                else if ("CW_TFEE".equals(contractInfo.getString("ATTR_CODE"))) {
                	offerChaSpecsInfo.put("ATTR_CODE", "921001500201");//接口功能费
                	offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                	offerChaSpecs2.add(offerChaSpecsInfo);
                }
                else if ("CW_INF_NUM".equals(contractInfo.getString("ATTR_CODE"))) {
                	offerChaSpecsInfo.put("ATTR_CODE", "921001500202");//接口个数
                	offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                	offerChaSpecs2.add(offerChaSpecsInfo);
                }
                else if ("CW_LUXURY".equals(contractInfo.getString("ATTR_CODE"))) {
                	offerChaSpecsInfo.put("ATTR_CODE", "921001500501");//豪华版月功能费用
                	offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                	offerChaSpecs3.add(offerChaSpecsInfo);
                }
                else if ("CW_ELIT".equals(contractInfo.getString("ATTR_CODE"))) {
                	offerChaSpecsInfo.put("ATTR_CODE", "921001500401");//精英版月功能费用
                	offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                	offerChaSpecs4.add(offerChaSpecsInfo);
                }
                else if ("CW_STANDARD".equals(contractInfo.getString("ATTR_CODE"))) {
                	offerChaSpecsInfo.put("ATTR_CODE", "921001500301");//标准版月功能费用
                	offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                	offerChaSpecs5.add(offerChaSpecsInfo);
                }
                else if ("CW_MONTHFEE".equals(contractInfo.getString("ATTR_CODE"))) {
                	cwMonthfee = contractInfo.getString("ATTR_VALUE");
				}
             }
        }
		if (IDataUtil.isNotEmpty(offerChaSpecs1)) {
			ecOfferInfo1.put("OFFER_ID", "130392101501");
			ecOfferInfo1.put("GROUP_ID", "9210151");
			ecOfferInfo1.put("OFFER_CHA_SPECS", offerChaSpecs1);
			ecOfferList.add(ecOfferInfo1);
		}
		if (IDataUtil.isNotEmpty(offerChaSpecs2)) {
			ecOfferInfo2.put("OFFER_ID", "130392101502");
			ecOfferInfo2.put("GROUP_ID", "9210151");
			ecOfferInfo2.put("OFFER_CHA_SPECS", offerChaSpecs2);
			ecOfferList.add(ecOfferInfo2);
		}
		if (IDataUtil.isNotEmpty(offerChaSpecs3)) {
			//设置豪华版月功能费
			offerChaSpecs3.first().put("ATTR_VALUE", cwMonthfee);
			ecOfferInfo3.put("OFFER_ID", "130392101505");
			ecOfferInfo3.put("GROUP_ID", "9210151");
			ecOfferInfo3.put("OFFER_CHA_SPECS", offerChaSpecs3);
			ecOfferList.add(ecOfferInfo3);
		}
		if (IDataUtil.isNotEmpty(offerChaSpecs4)) {
			//设置精英版月功能费
			offerChaSpecs4.first().put("ATTR_VALUE", cwMonthfee);
			ecOfferInfo4.put("OFFER_ID", "130392101504");
			ecOfferInfo4.put("GROUP_ID", "9210151");
			ecOfferInfo4.put("OFFER_CHA_SPECS", offerChaSpecs4);
			ecOfferList.add(ecOfferInfo4);
		}
		if (IDataUtil.isNotEmpty(offerChaSpecs5)) {
			//设置标准版月功能费
			offerChaSpecs5.first().put("ATTR_VALUE", cwMonthfee);
			ecOfferInfo5.put("OFFER_ID", "130392101503");
			ecOfferInfo5.put("GROUP_ID", "9210151");
			ecOfferInfo5.put("OFFER_CHA_SPECS", offerChaSpecs5);
			ecOfferList.add(ecOfferInfo5);
		}
        
        if (IDataUtil.isNotEmpty(ecOfferList)) {
        	IData ecOfferInfo = new DataMap();
        	IDataset offerChaSpecs = new DatasetList();
        	IData offerChaSpecsInfo = new DataMap();
			offerChaSpecsInfo.put("ATTR_CODE", "ESP_GROUP_VERSION");
        	offerChaSpecsInfo.put("ATTR_VALUE", "云服务版");
        	offerChaSpecs.add(offerChaSpecsInfo);
        	ecOfferInfo.put("OFFER_ID", "110000921015");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
            ecOfferList.add(ecOfferInfo);
            
            agreementElementData.put("EC_OFFER", ecOfferList);
        }
        if (IDataUtil.isNotEmpty(mebOfferList)) {
            agreementElementData.put("MEB_OFFER", mebOfferList);
        }

        return agreementElementData;
	}

    /**
     * 和商务TV 从电子协议获取参数值，回填到页面
     * 
     * @param
     * @return
     */
    private IData queryOfferChaSumBusinessTVList(IDataset contractList) {
        IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IDataset mebOfferList = new DatasetList();
        IData ecOfferInfo = new DataMap();
        IData ecOfferInfo2 = new DataMap();
        IData ecOfferInfo3 = new DataMap();
        IDataset offerChaSpecs = new DatasetList();
        IDataset offerChaSpecs2 = new DatasetList();
        IDataset offerChaSpecs3 = new DatasetList();
        //转换contractList信息
        IData transferContractInfo = transferContractInfo(contractList);
        if (IDataUtil.isNotEmpty(contractList)) {
            for (Object object : contractList) {
                IData offerChaSpecsInfo = new DataMap();
                IData contractInfo = (IData) object;
                // 电子协议定义的code
                /**
                 * 豪华定制包 130038070012 视听内容包 130038070011 通用免费包 130038070013
                 */
                if ("38070000101".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "38070000101");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                } else if ("38070000102".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "38070000102");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                } else if ("38070000201".equals(contractInfo.getString("ATTR_CODE"))) {
                    //判断是否勾选豪华定制包
                    String tvPkgFlag = transferContractInfo.getString("TV_PKG_YES");
                    if(StringUtils.isNotBlank(tvPkgFlag) && "1".equals(tvPkgFlag)) {
                        offerChaSpecsInfo.put("ATTR_CODE", "38070000201");
                        offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                        offerChaSpecs2.add(offerChaSpecsInfo);
                    }
                } else if ("38070000202".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "38070000202");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs2.add(offerChaSpecsInfo);
                } else if ("38070000301".equals(contractInfo.getString("ATTR_CODE"))) {
                    offerChaSpecsInfo.put("ATTR_CODE", "38070000301");
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs3.add(offerChaSpecsInfo);
                }

            }
        }
        if (IDataUtil.isNotEmpty(offerChaSpecs)) {
            ecOfferInfo.put("OFFER_ID", "130038070011");
            ecOfferInfo.put("GROUP_ID", "9210151");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
            ecOfferList.add(ecOfferInfo);
        }
        if (IDataUtil.isNotEmpty(offerChaSpecs2)) {
            ecOfferInfo2.put("OFFER_ID", "130038070012");
            ecOfferInfo2.put("GROUP_ID", "9210151");
            ecOfferInfo2.put("OFFER_CHA_SPECS", offerChaSpecs2);
            ecOfferList.add(ecOfferInfo2);
        }
        if (IDataUtil.isNotEmpty(offerChaSpecs3)) {
            ecOfferInfo3.put("OFFER_ID", "130038070013");
            ecOfferInfo3.put("GROUP_ID", "9210151");
            ecOfferInfo3.put("OFFER_CHA_SPECS", offerChaSpecs3);
            ecOfferList.add(ecOfferInfo3);
        }
        if (IDataUtil.isNotEmpty(ecOfferList)) {
            agreementElementData.put("EC_OFFER", ecOfferList);
        }
        if (IDataUtil.isNotEmpty(mebOfferList)) {
            agreementElementData.put("MEB_OFFER", mebOfferList);
        }
        return agreementElementData;
    }

    /**
     * 多媒体桌面电视
     * 从电子协议获取参数值，回填到页面
     * @param ContractList
     * @return
     */
    private IData queryOfferChaIMS(IDataset ContractList)
    {
        IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IDataset mebOfferList = new DatasetList();

        IDataset offerChaSpecs = new DatasetList();
        IDataset offerChaSpecs2 = new DatasetList();
        IDataset offerChaSpecs3 = new DatasetList();

        if (IDataUtil.isNotEmpty(ContractList))
        {
            for (int i =0 ; i<ContractList.size() ; i++)
            {

                IData contractInfo = ContractList.getData(i);

                /**
                 *  来电显示   22220101
                 *  多媒体彩铃 10122824
                 *  自定义资费 800109   // offerCode
                 *       月租费  20000002 、通信费 20000000 、 打折 20000001
                 */
                if ("CC_WDS_FEE".equals(contractInfo.getString("ATTR_CODE")))
                {
                    IData offerChaSpecsInfo = new DataMap();
                    offerChaSpecsInfo.put("ATTR_CODE", "20000002");   // 月租费
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("CC_PHONE_FEE".equals(contractInfo.getString("ATTR_CODE")))
                {
                    IData offerChaSpecsInfo = new DataMap();
                    offerChaSpecsInfo.put("ATTR_CODE", "20000000");   // 通信费
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("CC_PHONE_OUTFEE".equals(contractInfo.getString("ATTR_CODE")))
                {
                    IData offerChaSpecsInfo = new DataMap();
                    offerChaSpecsInfo.put("ATTR_CODE", "20000001");     // 打折
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs.add(offerChaSpecsInfo);
                }
                else if ("CC_COMSHOW".equals(contractInfo.getString("ATTR_CODE")) || "CC_COMSHOW_FEE".equals(contractInfo.getString("ATTR_CODE")))  // 协议中 勾选 来电显示
                {
                    IData offerChaSpecsInfo = new DataMap();
                    offerChaSpecsInfo.put("ATTR_CODE", "22220101");   // 来电显示
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs2.add(offerChaSpecsInfo);
                }
                else if ("CC_CRBT".equals(contractInfo.getString("ATTR_CODE"))|| "CC_CRBT_FEE".equals(contractInfo.getString("ATTR_CODE")))  // 协议中 勾选 多媒体彩铃
                {
                    IData offerChaSpecsInfo = new DataMap();
                    offerChaSpecsInfo.put("ATTR_CODE", "10122824");   // 多媒体彩铃
                    offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                    offerChaSpecs3.add(offerChaSpecsInfo);
                }
            }
        }

        if (IDataUtil.isNotEmpty(offerChaSpecs))
        {
            IData ecOfferInfo = new DataMap();
            ecOfferInfo.put("OFFER_ID", "130000800109");
            ecOfferInfo.put("GROUP_ID", "33001652");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
            ecOfferList.add(ecOfferInfo);
        }

        if (IDataUtil.isNotEmpty(offerChaSpecs2))   // 来电显示
        {
            IData ecOfferInfo = new DataMap();
            ecOfferInfo.put("OFFER_ID", "130022220101");
            ecOfferInfo.put("GROUP_ID", "10122802");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs2);
            mebOfferList.add(ecOfferInfo);
        }

        if (IDataUtil.isNotEmpty(offerChaSpecs3))   //  多媒体彩铃
        {
            IData ecOfferInfo = new DataMap();
            ecOfferInfo.put("OFFER_ID", "120010122824");
            ecOfferInfo.put("GROUP_ID", "10122801");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs3);
            mebOfferList.add(ecOfferInfo);
        }

        if (IDataUtil.isNotEmpty(ecOfferList))
        {
            agreementElementData.put("EC_OFFER", ecOfferList);
        }

        if (IDataUtil.isNotEmpty(mebOfferList))
        {
            agreementElementData.put("MEB_OFFER", mebOfferList);
        }

        return agreementElementData;
    }

    /**
     *  融合v网
     * 从电子协议获取参数值，回填到页面
     * @param ContractList
     * @return
     */
    private IData queryOfferChaVpmn(IDataset ContractList)
    {
        IData agreementElementData = new DataMap();
        IDataset ecOfferList = new DatasetList();
        IData ecOfferInfo = new DataMap();
        IDataset offerChaSpecs = new DatasetList();

        IData transferContractInfo = transferContractInfo(ContractList);

        if (IDataUtil.isNotEmpty(ContractList))
        {
            for (int i =0 ; i<ContractList.size() ; i++)
            {
                IData contractInfo = ContractList.getData(i);

                /**
                 *
                 */
                if ("CC_VNET".equals(contractInfo.getString("ATTR_CODE")))  // 协议中 勾选 融合v网
                {
                    //判断是否 录入 融合V网 月租费
                    String ccVnetFee = transferContractInfo.getString("CC_VNET_FEE");
                    if(StringUtils.isNotBlank(ccVnetFee) && "1".equals(contractInfo.getString("ATTR_VALUE")))
                    {
                        IData offerChaSpecsInfo = new DataMap();
                        offerChaSpecsInfo.put("ATTR_CODE", "10122824");   // 多媒体彩铃
                        offerChaSpecsInfo.put("ATTR_VALUE", contractInfo.getString("ATTR_VALUE"));
                        offerChaSpecs.add(offerChaSpecsInfo);
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(offerChaSpecs)) {
            ecOfferInfo.put("OFFER_ID", "130038070011");
            ecOfferInfo.put("GROUP_ID", "9210151");
            ecOfferInfo.put("OFFER_CHA_SPECS", offerChaSpecs);
            ecOfferList.add(ecOfferInfo);
        }

        if (IDataUtil.isNotEmpty(ecOfferList))
        {
            agreementElementData.put("EC_OFFER", ecOfferList);
        }

        return agreementElementData;
    }
    
    /**
     * 
    * @Title: transferContractInfo 
    * @Description: 转换ATTR表查询结果集
    * @param contractList
    * @return IData
    * @author zhangzg
    * @date 2019年11月7日下午2:57:51
     */
    private static IData transferContractInfo(IDataset contractList) {
        IData contractcData = new DataMap();
        for(Object obj : contractList) {
            IData data = (IData)obj;
            contractcData.put(data.getString("ATTR_CODE"), data.getString("ATTR_VALUE"));
        }
        return contractcData;
    }
}
