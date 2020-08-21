
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DealAdcMasComm extends CSBizBean
{
    private static transient Logger logger = Logger.getLogger(DealAdcMasComm.class);
    
    /*
     * @description 根据报文类型分解平台或网关发起的订购业务
     * @author liaolc
     * @date 2014-07-08
     */
    public IData dealAdcMasCommData(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData rspResult = new DataMap();
        MemParamData dealMemParam = new MemParamData();

        // 2-报文类型
        String busiSign = data.getString("BUSI_SIGN", "");
        if (StringUtils.isEmpty(busiSign))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_331);
        }

        // 3-反向接口处理
        if (IntfField.SubTransCode.SubXXTGrpMemBiz.value.equals(busiSign) || IntfField.SubTransCode.SubXXTGrpMemBiz.value.equals(busiSign + "_1_0"))
        { //校讯通,反向接口BIPXXT05_TX100005_1_0
            rspResult = dealMemParam.subAdcGrpMemBiz(data);
        }

        else if (IntfField.SubTransCode.IAGWGrpMemBiz.value.equals(busiSign) || IntfField.SubTransCode.IAGWGrpMemBiz.value.equals(busiSign + "_1_0"))
        { // 行业网关2.0 =>2.9. 行业应用黑白名单同步业务,反向接口BIP4B248_T4101025_1_0
            rspResult = dealMemParam.IAGWGrpMemBiz(data);

        }

        else if (IntfField.SubTransCode.SubXHKGrpMemBiz.value.equals(busiSign) || IntfField.SubTransCode.SubXHKGrpMemBiz.value.equals(busiSign + "_1_0"))
        { // 学护卡
            rspResult = dealMemParam.subXfkGrpMemBiz(data);

        }
        else if (IntfField.SubTransCode.SubTXLGrpMemBiz.value.equals(busiSign) || IntfField.SubTransCode.SubTXLGrpMemBiz.value.equals(busiSign + "_1_0"))
        { // 集团通讯录
            rspResult = dealMemParam.subTxlGrpMemBiz(data);
        }

        // 4- 返回结果
        return rspResult;
    }

    /************************* 成员参数处理开始 **************************/
    /*
     * @description 成员个性化参数
     * @author liaolc
     * @date 2014/09/23
     */
    public static IDataset xfkFamNumSynAttr(IData data) throws Exception
    {
    	String mainSn = data.getString("MOB_NUM");
    	String oprCode = data.getString("OPR_CODE");
    	String famSn = data.getString("FAMNUM");
    	String modifyTag = "0";// 新增,
        if (StringUtils.isBlank(mainSn))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_01);
        }
        if (StringUtils.isBlank(famSn))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_01);
        }
        if (!"06".equals(oprCode) && !"07".equals(oprCode))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_02);
        }

        if("06".equals(oprCode))
        {//变更
        	modifyTag = "2";
	    }
	    else
	    {//删除
	    	modifyTag = "1";
	    }
        IData userMebInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userMebInfo))
        {// 网外号码
        	CSAppException.apperr(BatException.CRM_BAT_79);
        }
        String mebUserId = userMebInfo.getString("USER_ID");
        String relationTypeCode="D1";

        IDataset bbInfos = RelaBBInfoQry.qryRelaBBInfoByUserIdBRelaTypeCode(mebUserId, relationTypeCode);
        if (IDataUtil.isEmpty(bbInfos))
        {
        	 CSAppException.apperr(GrpException.CRM_GRP_839,mainSn);
		}
        String grpUserId = bbInfos.getData(0).getString("USER_ID_A");

        // 处理结果集
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", mainSn);
        param.put("PRODUCT_ID","10005744");
        param.put("MODIFY_TAG",modifyTag);
        param.put("FAMNUM",famSn);
        param.put("OPEN_CODE",oprCode);
        param.put("USER_ID",grpUserId);

        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("SS.XfkFamNumSynSVC.crtTrade", param);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    public static IData dealXxtCommDataFromUIP(IDataset paramList, IData data) throws Exception
    {
        IData param = new DataMap();
        IData studentsData = new DataMap();
        IDataset students = new DatasetList();
        String userIdA = "";
        String serialNumberA = "";
        String serialNumberB = "";
        String elementId = "";
        IDataset outsn = new DatasetList();
        IDataset stdKey = new DatasetList();
        IData xxtInfo = new DataMap();
        String opertype = "";

        IDataset xxtInfos = RelaXxtInfoQry.qryMemInfoBySNForUIPDestroy(paramList.getData(0).getString("SERIAL_NUMBER_A"),
                paramList.getData(0).getString("USER_ID_A"));
        IDataset infos = new DatasetList();
        // 对入参进行分组
        for (int i = 0; i < paramList.size(); i++)
        {
            IData info = paramList.getData(i);

            userIdA = IDataUtil.getMandaData(info, "USER_ID_A");
            serialNumberA = IDataUtil.getMandaData(info, "SERIAL_NUMBER_A");
            serialNumberB = IDataUtil.getMandaData(info, "SERIAL_NUMBER_B");
            elementId = IDataUtil.getMandaData(info, "ELEMENT_ID");

            if (!outsn.contains(serialNumberB)) {
                outsn.add(serialNumberB);
            }

            IDataset snBxxts = DataHelper.filter(xxtInfos, "SERIAL_NUMBER_B=" + serialNumberB + "," + "ELEMENT_ID=" + elementId);
            if (snBxxts.size() > 0)
            {
                stdKey.add(snBxxts.getData(0).getString("RSRV_STR1"));
            }

            IData discntInfo = DiscntInfoQry.getDiscntInfoByCode2(elementId);

            if (discntInfo != null) {
                if (!"".equals(discntInfo.getString("RSRV_STR3", ""))) {
                    IData relaDis = new DataMap();

                    relaDis.put("USER_ID_A", userIdA);
                    relaDis.put("SERIAL_NUMBER_A", serialNumberA);
                    relaDis.put("SERIAL_NUMBER_B", serialNumberB);
                    relaDis.put("ELEMENT_ID", discntInfo.getString("RSRV_STR3", ""));

                    infos.add(relaDis);
                }
            }

            infos.add(info);
        }

        // 获取计费号码订购数据
        IDataset productInfo = UserProductInfoQry.queryMainProduct(xxtInfos.getData(0).getString("EC_USER_ID"));

        IDataset distinctinfo = filterByEqualsCol(xxtInfos, "SERIAL_NUMBER_A", "SERIAL_NUMBER_B", "ELEMENT_ID", infos, "SERIAL_NUMBER_A", "SERIAL_NUMBER_B", "ELEMENT_ID");
        // 如果退订的件数等于订购的信息条数，为全退订
        if (distinctinfo.size() == xxtInfos.size())
        {
            opertype = TRADE_MODIFY_TAG.DEL.getValue();

            param.put("ALL_ALL_CANCEL", "true");
        }
        else
        {
            for (int i = 0; i < outsn.size(); i++)
            {
                IData stdParam = new DataMap();
                IDataset paramInfo = new DatasetList();
                serialNumberB = (String) outsn.get(i);

                IDataset snBinfos = DataHelper.filter(infos, "SERIAL_NUMBER_B=" + serialNumberB);
                IDataset snBxxts = DataHelper.filter(xxtInfos, "SERIAL_NUMBER_B=" + serialNumberB);

                distinctinfo = filterByEqualsCol(snBxxts, "SERIAL_NUMBER_A", "SERIAL_NUMBER_B", "ELEMENT_ID", infos, "SERIAL_NUMBER_A", "SERIAL_NUMBER_B", "ELEMENT_ID");
                IDataset elements = new DatasetList();

                if (distinctinfo.size() == snBxxts.size())
                {
                    opertype = TRADE_MODIFY_TAG.DEL.getValue();
                    param.put("BIZ_CTRL_TYPE", BizCtrlType.DestoryMember);

                    for (int j = 0; j < snBxxts.size(); j++)
                    {
                        IData elementParam = new DataMap();
                        xxtInfo = snBxxts.getData(j);

                        elementParam.put("STUD_NAME", xxtInfo.getString("NAME"));
                        elementParam.put("STUD_KEY", xxtInfo.getString("RSRV_STR1"));
                        elementParam.put("ELEMENT_ID", xxtInfo.getString("ELEMENT_ID"));
                        elementParam.put("tag", "1");

                        elements.add(elementParam);
                    }
                }
                else
                {
                    opertype = TRADE_MODIFY_TAG.MODI.getValue();
                    param.put("BIZ_CTRL_TYPE", BizCtrlType.ChangeMemberDis);

                    for (int j = 0; j < snBxxts.size(); j++)
                    {
                        IData elementParam = new DataMap();
                        xxtInfo = snBxxts.getData(j);

                        elementParam.put("STUD_NAME", xxtInfo.getString("NAME"));
                        elementParam.put("STUD_KEY", xxtInfo.getString("RSRV_STR1"));
                        elementParam.put("ELEMENT_ID", xxtInfo.getString("ELEMENT_ID"));
                        if (stdKey.contains(xxtInfo.getString("RSRV_STR1")))
                        {
                            elementParam.put("tag", "1");
                        }
                        else
                        {
                            elementParam.put("tag", "");
                        }

                        elements.add(elementParam);
                    }
                }

                stdParam.put("NOTIN_OUT_SN0", serialNumberB);
                stdParam.put("NOTIN_OPER_TYPE0", opertype);
                stdParam.put("NOTIN_STU_PARAM_LIST0", elements);
                stdParam.put("NOTIN_ctag0", "on");
                studentsData.put("PRODUCT_ID", productInfo.getData(0).getString("PRODUCT_ID"));
                studentsData.put("PRODUCT_PARAM", stdParam);
                paramInfo.addAll(IDataUtil.iData2iDataset(stdParam, "ATTR_CODE", "ATTR_VALUE"));
                studentsData.put("PRODUCT_ID", productInfo.getData(0).getString("PRODUCT_ID"));
                studentsData.put("PRODUCT_PARAM", paramInfo);
                students.add(studentsData);
            }
        }

        param.put("PRODUCT_PARAM_INFO", students);
        param.put("SERIAL_NUMBER", serialNumberA);
        param.put("PRODUCT_ID", productInfo.getData(0).getString("PRODUCT_ID")); // 集团产品id
        param.put("USER_ID", xxtInfos.getData(0).getString("EC_USER_ID"));
        param.put("CREATE_STAFF_ID", data.getString("CREATE_STAFF_ID"));
        param.put("CREATE_DEPART_ID", data.getString("CREATE_DEPART_ID"));
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        param.put("CREATE_CITY_CODE", data.getString("CREATE_CITY_CODE"));

        return param;
    }

    /**
     * 根据传入的2个col，比较两个IDataset对应col相同的IData 返回oriList中符合条件的数据
     *
     * @param oriList
     * @param col
     * @param compareList
     * @param compareCol
     * @return
     * @throws Exception
     */
    public static IDataset filterByEqualsCol(IDataset oriList,
            String col1, String col2, String col3, IDataset compareList,
            String compareCol1, String compareCol2, String compareCol3) throws Exception
    {
        IDataset rtList = new DatasetList();
        for (int i = 0, size = oriList.size(); i < size; i++)
        {
            IData oriData = oriList.getData(i);
            String value1 = oriData.getString(col1);
            String value2 = oriData.getString(col2);
            String value3 = oriData.getString(col3);
            if (StringUtils.isBlank(value1) || StringUtils.isBlank(value2) || StringUtils.isBlank(value3))
            {
                continue;
            }
            for (int j = 0, size2 = compareList.size(); j < size2; j++)
            {
                IData compareData = compareList.getData(j);
                String compareValue1 = compareData.getString(compareCol1);
                String compareValue2 = compareData.getString(compareCol2);
                String compareValue3 = compareData.getString(compareCol3);
                if (value1.equals(compareValue1) && value2.equals(compareValue2) && value3.equals(compareValue3))
                {
                    rtList.add(oriData);
                }
            }
        }

        return rtList;
    }

    public static IData splitInParamUserSerialNumA(IData data) throws Exception
    {
        IData result = new DataMap();

        IDataset userIdAList = data.getDataset("USER_ID_A");
        IDataset serialNumAList = data.getDataset("SERIAL_NUMBER_A");
        IDataset serialNumBList = data.getDataset("SERIAL_NUMBER_B");
        IDataset elementIdList = data.getDataset("ELEMENT_ID");

        if (userIdAList == null || userIdAList.size() == 1)
        {
            String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");
            IDataset infos = new DatasetList();
            infos.add(data);
            result.put(serialNumberA, infos);
        }
        else if (userIdAList.size() > 1)
        {
            for (int i = 0; i < userIdAList.size(); i++)
            {
                IData info = new DataMap();
                info.put("USER_ID_A", userIdAList.get(i));
                info.put("SERIAL_NUMBER_A", serialNumAList.get(i));
                info.put("SERIAL_NUMBER_B", serialNumBList.get(i));
                info.put("ELEMENT_ID", elementIdList.get(i));

                IDataset infos = null;
                String key = String.valueOf(serialNumAList.get(i)) + "_" + String.valueOf(userIdAList.get(i));
                if (IDataUtil.isEmpty(result.getDataset(key)))
                {
                    infos = new DatasetList();
                    infos.add(info);
                    result.put(key, infos);
                }
                else
                {
                    infos = result.getDataset(key);
                    infos.add(info);
                }
            }
        }
        return result;
    }
}
