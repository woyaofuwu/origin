
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.AttrTrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.OperCodeTrans;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatIntf extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	System.out.println("BuildPlatIntf orderParam"+param.toString());
    	if(StringUtils.isNotBlank(param.getString("DEAL_COND",""))){
    		param.putAll(new DataMap(param.getString("DEAL_COND")));
        }
    	System.out.println("BuildPlatIntf deal orderParam"+param.toString());
        PlatReqData req = (PlatReqData) brd;
        OperCodeTrans.operCodeTrans(param);
        IDataset attrParams = param.getDataset("ATTR_PARAM");
        List<PlatSvcData> platSvcDatas = new ArrayList<PlatSvcData>();
        List<String> allPlatCancels = new ArrayList<String>();
        if (IDataUtil.isEmpty(attrParams))
        {
            AttrTrans.trans(param);
            AttrTrans.transExtendInfo(param);
        }
        if ("03".equals(param.getString("OPER_CODE"))){
			if ("78".equals(param.getString("BIZ_TYPE_CODE"))){
				param.put("OPER_CODE", PlatConstants.OPER_ORDER);
				param.put("RSRV_STR5", "03");
			}
		}
        
        // 如果是全退订
        if (PlatConstants.OPER_CANCEL_ALL.equals(param.getString("OPER_CODE")))
        {
            String orgDomain = param.getString("ORIGDOMAIN", param.getString("ORG_DOMAIN", ""));
            // 如果是DSMP和MUSC的全退订，需要将整个平台的全部退订，其它的只退订传入的BIZ_TYPE_CODE的
            if (orgDomain.equals("DSMP") || orgDomain.equals("MUSC"))
            {
                allPlatCancels.add(orgDomain);
            }
            else
            {
                String bizTypeCode = param.getString("BIZ_TYPE_CODE");
                allPlatCancels.add(bizTypeCode);
            }
        }
        // 如果是SP全退订，需要根据SPCode退订
        else if (PlatConstants.OPER_SP_CANCEL_ALL.equals(param.getString("OPER_CODE")))
        {
            String spCode = param.getString("SP_CODE", "");
            allPlatCancels.add("SP:" + spCode);
        }
        else
        {
        	
        	//如果是DSMP的，从手机客户端过来的OPR_SOURCE为69，OPR_SOURCE改为08，因为DSMP平台不识别OPR_SOURCE为69
        	if(StringUtils.isNotEmpty(param.getString("BIZ_TYPE_CODE"))
        			&& "_03_04_05_07_08_09_13_56_57_".indexOf(param.getString("BIZ_TYPE_CODE"))>0
        			&& StringUtils.equals(param.getString("OPR_SOURCE"), "69"))
        	{
        		param.put("OPR_SOURCE", "08");
        	}
        	
        	//和校园与和教育业务共用一套局数据，需依据入口渠道进行biz_code进行转换
            if ("80".equals(param.getString("BIZ_TYPE_CODE"))){
                UcaData uca = brd.getUca();
        		String opr_source = param.getString("OPR_SOURCE");
        		String biz_code = param.getString("BIZ_CODE");
        		param.put("RSRV_STR8", biz_code);
            	IDataset sourceData = ParamInfoQry.getCommparaByCode("CSM", "8002", "CHECKSOURCE", uca.getUser().getEparchyCode());
        		//判断当前渠道是否需要转换
        		boolean flag = false;
        		if (null != sourceData){
        			if (StringUtils.isNotEmpty(opr_source)){
    					IData source = sourceData.getData(0);
    					if (null != source && null != source.getString("PARA_CODE1")){
    						String str = source.getString("PARA_CODE1");
    						String[] ss = str.split(",");
    						for (String string : ss) {
    							if (opr_source.equals(string)){
    								flag = true;
    								break;
    							}
    						}
        				}
        			}
        		}
        		//如需转换，则获取当前biz_code对应的新的BIZ_CODE记录
        		String service_id = param.getString("SERVICE_ID",param.getString("ELEMENT_ID"));
        		if (flag){
        			IDataset bizData = ParamInfoQry.getCommparaByCode("CSM", "8002", biz_code, uca.getUser().getEparchyCode());
        			if (null != bizData && bizData.size() > 0){
        				biz_code = bizData.getData(0).getString("PARA_CODE1");
        				service_id = bizData.getData(0).getString("PARA_CODE2");
        			}
        		}
        		//重新设置BIZ_CODE
        		param.put("BIZ_CODE", biz_code);
        		param.put("SERVICE_ID", service_id);
        		param.put("ELEMENT_ID", service_id);
            }
        	
            PlatSvcData psd = new PlatSvcData(param);
            
            //add by liaosw 手机阅读支持书券充值修改 2015-06-15
            if ("60".equals(psd.getOfficeData().getBizTypeCode()))
            {
                if (PlatConstants.OPER_ADD_MONEY.equals(psd.getOperCode()))
                {
                    psd.setModifyTag(BofConst.MODIFY_TAG_ADD);  //这里设置成0，否则后续在执行规则时会报错
                }
            }
            
            
            if ("SS.PlatSyncRegSVC.tradeReg".equals(req.getXTransCode()))
            {
                req.setSync(true);
            }
            platSvcDatas.add(psd);
        }

        req.setPlatSvcDatas(platSvcDatas);
        req.setAllPlatCancels(allPlatCancels);

    }

    // 一级boss过来的checkBefore不校验；保证用户状态的错误码符合一级boss规定
    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        if (!"6".equals(CSBizBean.getVisit().getInModeCode()))
        {
            super.checkBefore(input, reqData);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatReqData();
    }

}
