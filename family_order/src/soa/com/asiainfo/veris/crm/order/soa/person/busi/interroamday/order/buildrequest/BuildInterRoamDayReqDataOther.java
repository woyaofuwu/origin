
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.InterRoamDayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata.InterRoamDayRequestData;

public class BuildInterRoamDayReqDataOther extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        InterRoamDayRequestData reqData = (InterRoamDayRequestData) brd;
        List<ProductModuleData> discntDatas = new ArrayList<ProductModuleData>();
        IDataUtil.chkParam(param, "ELEMENT_ID");
        IDataUtil.chkParam(param, "ELEMENT_TYPE_CODE");
        IDataUtil.chkParam(param, "MODIFY_TAG");

        String element_id = param.getString("ELEMENT_ID");
        String element_type_code = param.getString("ELEMENT_TYPE_CODE", "D");
        String modify_tag = param.getString("MODIFY_TAG");
        String package_id = PersonConst.INTER_ROAM_DAY_PACKAGE_ID;

//        IDataset ids = UserDiscntInfoQry.getUserDiscntByUserAndElementId(brd.getUca().getUserId(), package_id, element_id, element_type_code);
        IDataset ids =new  DatasetList();
		//查询包下所有优惠
		IDataset  upackageEleInfos=UPackageElementInfoQry.getElementInfoByGroupId(package_id);
	       if(DataSetUtils.isNotBlank(upackageEleInfos)){
	    	   for(int k=0; k<upackageEleInfos.size();k++){
		    	     String  prarmdiscode=upackageEleInfos.getData(k).getString("DISCNT_CODE");
		    	     if(element_id.equals(prarmdiscode)){   	 
		    	    	 ids.add(upackageEleInfos.getData(k));
		    	     }
		      }
	       }
        
        
        
        InterRoamDayBean bean = (InterRoamDayBean) BeanManager.createBean(InterRoamDayBean.class);
        param.put("PACKAGE_ID", package_id);

        if ("0".equals(modify_tag))
        { // 订购

            if (DataSetUtils.isNotBlank(ids))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_349, "300002", "用户已订购该国漫数据流量套餐，不允许重复订购！");
                return;
            }
            //在订购880001时，验证是否已经订购服务22，如果没有订购就报错
            String inModeCode=param.getString("IN_MODE_CODE_C");
            String discntCode6=StaticUtil.getStaticValue("INTER_ROAM_DAY_TRADE_6", "6");	//获取默认的优惠
            if(inModeCode!=null&&inModeCode.equals("6")&&element_id.equals(discntCode6)){	//如果是从IBOSS过来的订购
            	IDataset sevSet=BofQuery.getUserSvc(brd.getUca().getUserId(), "22", Route.CONN_CRM_CG);
                if(!(sevSet!=null&&sevSet.size()>0)){
                	CSAppException.apperr(CrmCommException.CRM_COMM_349, "300002", "用户未开通GPRS服务，不能订购此套餐！");
                }
            }

            IDataset elems = PkgElemInfoQry.getServElementByPk(package_id, element_type_code, element_id);

            if (elems.size() > 0)
            {
                DiscntData discntData = new DiscntData(elems.getData(0));
                discntData.setModifyTag("0");
                discntData.setProductId(brd.getUca().getProductId());
                discntData.setPackageId(package_id);
                discntData.setElementId(element_id);
                // 计算优惠开始结束时间
                IData seDate = bean.geneNewDiscntDate(param);
                String start_date = seDate.getString("START_DATE");
                String end_date = seDate.getString("END_DATE");
                discntData.setStartDate(start_date);
                discntData.setEndDate(end_date);

                discntDatas.add(discntData);
                reqData.setDiscntDatas(discntDatas);

            }
        }
        else
        {

            if (DataSetUtils.isNotBlank(ids))
            {
                String endDate = ids.getData(0).getString("END_DATE", "");
                String sysDate = SysDateMgr.getSysDateYYYYMMDD();

                if (sysDate.equals(SysDateMgr.getDateForYYYYMMDD(endDate)))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_536, "300003", param.getString("SERIAL_NUMBER") + "该类优惠已退订！");
                }
                DiscntData discntData = new DiscntData(ids.getData(0));
                discntData.setModifyTag("1");
                discntData.setProductId(brd.getUca().getProductId());
                discntData.setPackageId(package_id);
                discntData.setElementId(element_id);

                // 计算优惠结束时间
                IData seDate = bean.geneDelDiscntDate(param);
                String end_date = seDate.getString("END_DATE");
                discntData.setEndDate(end_date);

                discntDatas.add(discntData);
                reqData.setDiscntDatas(discntDatas);

            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_349, "300004", "未订购该类优惠或已退订！");
            }

        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new InterRoamDayRequestData();
    }

}
