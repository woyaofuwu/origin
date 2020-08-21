package com.asiainfo.veris.crm.order.soa.group.esop.eopdatamgr;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EopParamTransQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformEomsBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

/**
 * order
 * 为eoms交互提供数据抽取服务
 *
 * @author ckh
 * @date 2018/2/27.
 */
public class ExtractEomsDataForEweBean
{
    public IDataset extractData(IData param) throws Exception
    {
        // 1- 首先查TF_B_EOP_EOMS表
        String subIbsysId = param.getString("SUB_IBSYSID");
        String operType = param.getString("OPER_TYPE");
        IData eomsInfo = WorkformEomsBean.queryBySubIbsysIdAndOperTypeDesc(subIbsysId, operType);
        if (DataUtils.isEmpty(eomsInfo))
        {
            return new DatasetList();
        }
        // 1.1- 设置返回结构第一层数据
        IDataset retData = makeMainData(eomsInfo);

        // 2- 查下转换配置表，看是否需要转换，如果需要转换，针对TF_B_EOP_ATTR表中的数据进行转换
        IDataset transData = new DatasetList();
        IData configInfo = EweConfigQry.queryEomsTranConfig("SHEETTYPE", eomsInfo.getString("SHEETTYPE"),
                eomsInfo.getString("SERVICETYPE"), operType);
        if (DataUtils.isNotEmpty(configInfo))
        {
            transData = getTransData(configInfo.getString("PARAMNAME"));
        }

        // 3- 查询TF_B_EOP_PRODUCT表，看有多少订单
        IDataset eopProductInfos = WorkformProductSubBean.qryProductByIbsysid(param.getString("IBSYSID"));
        if (DataUtils.isEmpty(eopProductInfos))
        {
            return new DatasetList();
        }

        // 4- 开始处理TF_B_EOP_ATTR表数据
        IDataset recordInfos = new DatasetList();
        IDataset commonList = new DatasetList();
        // 4.1- 需要先将集团信息拼入
//        commonList = dealGroupInfo(param.getString("IBSYSID"));

        // 4.1- 多条工单，可能有公共数据，公共数据的RECORD_NUM=0
        commonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");

     // 4.2- 查询工单数据
        IDataset recordDataset = WorkformAttrBean.qryRecordNumBySubIbsysid(subIbsysId);
        if (DataUtils.isNotEmpty(recordDataset)) {
        	for (int j = 0; j < recordDataset.size(); j++) {
				String recordNum =  recordDataset.getData(j).getString("RECORD_NUM");
				
				//4.2.1- 查询工单数据
				IDataset productAttrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,eomsInfo.getString("GROUP_SEQ"),recordNum);
				
				if (DataUtils.isEmpty(productAttrInfos))
		        {
		            productAttrInfos = new DatasetList();
		        }
		        productAttrInfos.addAll(commonList);

		        // 4.2.2- 处理工单数据结构
		        IData recordInfo = dealStructure(productAttrInfos, transData);
		        recordInfos.add(recordInfo);
			}
		}

        IData opDetail = new DataMap();
        opDetail.put("opDetail", recordInfos);
        IData in13 = new DataMap();
        in13.put("ns1:in13", opDetail);
        retData.add(in13);

        return retData;
    }
    
    
    public IDataset extractSubData(IData param) throws Exception
    {
        // 1- 首先查TF_B_EOP_EOMS表
        String subIbsysId = param.getString("SUB_IBSYSID");
        String operType = param.getString("OPER_TYPE");
        String recordNum = param.getString("RECORD_NUM","");
        String ibsysid = param.getString("IBSYSID","");
        IDataset eomsInfos = WorkformEomsBean.qryEomsByIbsysIdOperType(ibsysid, recordNum,operType);
        if (DataUtils.isEmpty(eomsInfos))
        {
            return new DatasetList();
        }
        // 1.1- 设置返回结构第一层数据
        IData eomsInfo = eomsInfos.first();
        IDataset retData = makeMainData(eomsInfo);

        // 2- 查下转换配置表，看是否需要转换，如果需要转换，针对TF_B_EOP_ATTR表中的数据进行转换
        IDataset transData = new DatasetList();
        IData configInfo = EweConfigQry.queryEomsTranConfig("SHEETTYPE", eomsInfo.getString("SHEETTYPE"),
                eomsInfo.getString("SERVICETYPE"), operType);
        if (DataUtils.isNotEmpty(configInfo))
        {
            transData = getTransData(configInfo.getString("PARAMNAME"));
        }

        // 3- 查询TF_B_EOP_PRODUCT表，看有多少订单
        IDataset eopProductInfos = WorkformProductSubBean.qryProductByIbsysid(param.getString("IBSYSID"));
        if (DataUtils.isEmpty(eopProductInfos))
        {
            return new DatasetList();
        }

        // 4- 开始处理TF_B_EOP_ATTR表数据
        IDataset recordInfos = new DatasetList();
        IDataset commonList = new DatasetList();

        // 4.1- 多条工单，可能有公共数据，公共数据的RECORD_NUM=0
        commonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNum(subIbsysId, "0");

        // 4.2- 查询工单数据
        IDataset productAttrInfos = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,
                eomsInfo.getString("GROUP_SEQ"),
                eomsInfo.getString("RECORD_NUM"));
        if (DataUtils.isEmpty(productAttrInfos))
        {
            productAttrInfos = new DatasetList();
        }
        productAttrInfos.addAll(commonList);

        // 4.3- 处理工单数据结构
        IData recordInfo = dealStructure(productAttrInfos, transData);
        recordInfos.add(recordInfo);

        IData opDetail = new DataMap();
        opDetail.put("opDetail", recordInfos);
        IData in13 = new DataMap();
        in13.put("ns1:in13", opDetail);
        retData.add(in13);

        return retData;
    }

    private IDataset dealGroupInfo(String ibsysid) throws Exception
    {
        // 1- 查TF_B_EOP_SUBSCRIBE表数据
        IDataset eopSubscriberInfos = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if (DataUtils.isEmpty(eopSubscriberInfos) || StringUtils.isEmpty(eopSubscriberInfos.first().getString("GROUP_ID")))
        {
            return new DatasetList();
        }

        // 2- 调用客户管理接口
        IData inparam = new DataMap();
        inparam.put("GROUP_ID", eopSubscriberInfos.first().getString("GROUP_ID"));
        IDataset groupInfos = new DatasetList();//GrpInfoQry.qryCustGroupByGroupId(inparam);
        if (DataUtils.isEmpty(groupInfos))
        {
            return new DatasetList();
        }

        // 3- 拼装客户信息数据
        IDataset retDataset = new DatasetList();
        retDataset.add(makeSubNodeData("CUST_LINK_NAME", groupInfos.first().getString("RSRV_STR21")));
        retDataset.add(makeSubNodeData("CUST_LINK_PHONE", groupInfos.first().getString("RSRV_STR20")));
        retDataset.add(makeSubNodeData("CUST_EMAIL", groupInfos.first().getString("CUST_EMAIL")));
        retDataset.add(makeSubNodeData("CUST_TEC_LINK_NAME", groupInfos.first().getString("GROUP_MGR_CUST_NAME")));
        retDataset.add(makeSubNodeData("CUST_TEC_LINK_PHONE", groupInfos.first().getString("CUST_TEC_LINK_PHONE")));
        retDataset.add(makeSubNodeData("CUST_CLASS_ID", groupInfos.first().getString("CLASS_ID")));
        retDataset.add(makeSubNodeData("CUST_CITY_CODE", groupInfos.first().getString("CITY_CODE")));
        retDataset.add(makeSubNodeData("VIP_MANAGER_CODE", groupInfos.first().getString("CUST_MANAGER_ID")));
        retDataset.add(makeSubNodeData("CUST_EPARCHY_CODE", groupInfos.first().getString("EPARCHY_CODE")));
        retDataset.add(makeSubNodeData("CUST_SERV_GRADE", "".equals(groupInfos.first().getString("RSRV_NUM1","")) ?  "4" : groupInfos.first().getString("RSRV_NUM1")));

        return retDataset;
    }

    private IData makeSubNodeData(String paramName, String paramValue)
    {
        IData ret = new DataMap();
        ret.put("ATTR_VALUE", paramValue);
        ret.put("ATTR_CODE", paramName);
        return ret;
    }

    private IData dealStructure(IDataset productAttrInfos, IDataset transData) throws Exception
    {
        IDataset attrInfos = new DatasetList();
        IData recordInfo = new DataMap();
        for (int i = 0; i < productAttrInfos.size(); i++)
        {
            IData attrInfo = new DataMap();
            IData fieldInfo = new DataMap();
            IData productAttrInfo = productAttrInfos.getData(i);
            //由于前台暂时没有给转换value数据，对value数据进行转换，无奈之举╮(╯▽╰)╭
            //从配置变里面取数据，进行转换
            String configkeyValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "EsopParamToCrmParamKey", productAttrInfo.getString("ATTR_CODE")});
            if(StringUtils.isNotEmpty(configkeyValue))
            {
                String value = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ configkeyValue, productAttrInfo.getString("ATTR_VALUE")});
                if (StringUtils.isNotEmpty(value))
                {
                    productAttrInfo.put("ATTR_VALUE", value);
                }
            }
            if (DataUtils.isNotEmpty(transData))
            {
               IData transInfo =  doTransData(productAttrInfo,transData);
               
               if (DataUtils.isNotEmpty(transInfo)) {
            	   attrInfo.put("fieldChName", transInfo.getString("ATTR_NAME"));
                   attrInfo.put("fieldContent", productAttrInfo.getString("ATTR_VALUE",""));
                   attrInfo.put("fieldEnName", transInfo.getString("ATTR_CODE"));
                   fieldInfo.put("fieldInfo", attrInfo);
                   attrInfos.add(fieldInfo);
               }
            }
            
            recordInfo.put("recordInfo", attrInfos);
        }
        return recordInfo;
    }

    private IData doTransData(IData productAttrInfo, IDataset transDatas)
    {
    	IData transInfo = new DataMap();
        for (int i = 0; i < transDatas.size(); i++)
        {
            IData transData = transDatas.getData(i);
            String transName = transData.getString("PARAM_CODE","");
            if (!transName.equals(productAttrInfo.getString("ATTR_CODE")))
            {
                continue;
            }
            transInfo.put("ATTR_NAME", transData.getString("CHG_PARAM_DESC"));
            transInfo.put("ATTR_CODE", transData.getString("CHG_PARAM_CODE"));
            break;
        }
        
        return transInfo;
    }

    private IDataset getTransData(String eopOperType) throws Exception
    {
        IDataset paramTransList = EopParamTransQry.queryParamTransByOperType(eopOperType);
        if (DataUtils.isEmpty(paramTransList))
        {
            paramTransList = new DatasetList();
        }
        return paramTransList;
    }

    public IDataset makeMainData(IData eomsData) throws Exception
    {
        IDataset retData = new DatasetList();
        IData in0 = new DataMap();
        IData in1 = new DataMap();
        IData in2 = new DataMap();
        IData in3 = new DataMap();
        IData in4 = new DataMap();
        IData in5 = new DataMap();
        IData in6 = new DataMap();
        IData in7 = new DataMap();
        IData in8 = new DataMap();
        IData in9 = new DataMap();
        IData in10 = new DataMap();
        IData in11 = new DataMap();
        IData in12 = new DataMap();
        in0.put("ns1:in0", eomsData.getString("SHEETTYPE",""));
        in1.put("ns1:in1", eomsData.getString("SERVICETYPE",""));
        in2.put("ns1:in2", eomsData.getString("SERIALNO",""));
        in3.put("ns1:in3", "SX_EOMS");
        in4.put("ns1:in4", "HI_ESOP15");
        in5.put("ns1:in5", "");
        in6.put("ns1:in6", SysDateMgr.getSysTime());
        //附件连接需要调用服务查取
        IData param = new DataMap();
        param.put("SUB_IBSYSID", eomsData.getString("SUB_IBSYSID",""));
        IDataset attrList =  CSAppCall.call("SS.WorkformAttachSVC.qryAttachInfo", param);
        if (IDataUtil.isEmpty(attrList))
        {
            in7.put("ns1:in7", new DataMap());
        }
        else
        {
            in7.put("ns1:in7", attrList.getData(0));
        }
        in8.put("ns1:in8", eomsData.getString("OPPERSON",""));
        in9.put("ns1:in9", eomsData.getString("OPCORP",""));
        in10.put("ns1:in10", eomsData.getString("OPDEPART",""));
        in11.put("ns1:in11", eomsData.getString("OPCONTACT",""));
        in12.put("ns1:in12", eomsData.getString("OPTIME",""));
        retData.add(in0);
        retData.add(in1);
        retData.add(in2);
        retData.add(in3);
        retData.add(in4);
        retData.add(in5);
        retData.add(in6);
        retData.add(in7);
        retData.add(in8);
        retData.add(in9);
        retData.add(in10);
        retData.add(in11);
        retData.add(in12);

        return retData;
    }

   /**
    *  重派数据报文拼装
    * @param param
    * @return
    * @throws Exception
    */
	public IDataset renewEomsDataForEwe(IData param) throws Exception {
		
		IDataset emosInfos = new DatasetList(param.getString("EMOS_INFO"));
		if (DataUtils.isEmpty(emosInfos)) {
			return new DatasetList();
		}
		
		 // 1- 首先查TF_B_EOP_EOMS表
        String subIbsysId = param.getString("SUB_IBSYSID");
        String operType = param.getString("OPER_TYPE");
        IData eomsInfo = WorkformEomsBean.queryBySubIbsysIdAndOperTypeDesc(subIbsysId, "renewWorkSheet");
        if (DataUtils.isEmpty(eomsInfo))
        {
            return new DatasetList();
        }
        // 1.1- 设置返回结构第一层数据
        IDataset retData = makeMainData(eomsInfo);

        // 2- 查下转换配置表，看是否需要转换，如果需要转换，针对TF_B_EOP_ATTR表中的数据进行转换
        IDataset transData = new DatasetList();
        IData configInfo = EweConfigQry.queryEomsTranConfig("SHEETTYPE", eomsInfo.getString("SHEETTYPE"),
                eomsInfo.getString("SERVICETYPE"), operType);
        if (DataUtils.isNotEmpty(configInfo))
        {
            transData = getTransData(configInfo.getString("PARAMNAME"));
        }
        
        // 3- 查询TF_B_EOP_PRODUCT表，看有多少订单
        IDataset eopProductInfos = WorkformProductSubBean.qryProductByIbsysid(param.getString("IBSYSID"));
        if (DataUtils.isEmpty(eopProductInfos))
        {
            return new DatasetList();
        }

        // 4- 开始处理TF_B_EOP_ATTR表数据
        IDataset recordInfos = new DatasetList();
        IDataset commonList = new DatasetList();
        // 4.1- 需要先将集团信息拼入
        commonList = WorkformAttrBean.qryAttrBySubIbsysidAndRecordNumGroupSeq(subIbsysId,eomsInfo.getString("GROUP_SEQ"), "0");


        // 4.2- 拼工单数据
        
    	for (int j = 0; j < emosInfos.size(); j++) {
			String recordNum =  emosInfos.getData(j).getString("RECORD_NUM");
			
			//4.2.1- 查询工单数据
			IDataset productAttrInfos = new DatasetList(emosInfos.getData(j).getString("ATTR_INFOS"));
			
			if (DataUtils.isEmpty(productAttrInfos))
	        {
	            productAttrInfos = new DatasetList();
	        }
	        productAttrInfos.addAll(commonList);

	        // 4.2.2- 处理工单数据结构
	        IData recordInfo = dealStructure(productAttrInfos, transData);
	        recordInfos.add(recordInfo);
		}

        IData opDetail = new DataMap();
        opDetail.put("opDetail", recordInfos);
        IData in13 = new DataMap();
        in13.put("ns1:in13", opDetail);
        retData.add(in13);

        return retData;
		
	}
}
