package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;

import org.apache.log4j.Logger;

/**
 * 多媒体 桌面电话成员完工之后，如果有 融合v网成员受理 的 子流程，需要将驱动该子流程；
 *
 * */
public class WorkformMebDriveChildSVC extends CSBizService
{

	public void execute(IData data) throws Exception
	{
        String ibsysid = data.getString("BI_SN","");
        String busiformId =data.getString("BUSIFORM_ID");
        
        IDataset eweReleInfo = WorkformReleBean.qryBySubBusiformId(busiformId);
        if (DataUtils.isEmpty(eweReleInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID" + busiformId + "查询TF_B_EWE_RELE为空");
        }

        String mebRecordNum = eweReleInfo.getData(0).getString("RELE_VALUE", "");
        
        //查询成员数据
        IData mebInfos =WorkformProductExtBean.qryProductByrecodeNum(ibsysid, mebRecordNum);
        
        String mebSerialNumber = mebInfos.getString("SERIAL_NUMBER");
        String mebProductId = mebInfos.getString("PRODUCT_ID");

        if( !"222201".equals(mebProductId))  // 非 多媒体桌面电话子流程 ，不需要驱动
        {
            return ;
        }

        // 根据 ibsysid && PRODUCT_ID 查询  product表  ，获取产品 信息 ；
        IData   productInfo =WorkformProductExtBean.qryProductByProductId(ibsysid, "22000020", mebSerialNumber);
        
        if (IDataUtil.isNotEmpty(productInfo))
        {
            String subBusiformId = productInfo.getString("RSRV_STR2");

            // 拆分子流程时，将 recodeNum 存入  rele 表
            IDataset releInfos = WorkformReleBean.qryBySubBusiformId(subBusiformId);
            if(IDataUtil.isNotEmpty(releInfos))
            {
                IDataset eweNodeList = EweNodeQry.qryEweNodeByBusiformIdState(subBusiformId,"W");

                if(IDataUtil.isNotEmpty(eweNodeList))
                {
                    IData eweNode = eweNodeList.first();
                    String busiformNodeId = eweNode.getString("BUSIFORM_NODE_ID");

                    //受理融合v网，需要校验是否 受理了多媒体桌面电话 ， 没有受理，则 改为等待
                    IDataset subscribeInfo = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
                    String groupId = subscribeInfo.first().getString("GROUP_ID");
                    IData ucaInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                    IData  userInfo = UcaInfoQry.qryUserInfoBySn(mebSerialNumber);
                    
                    boolean  IMSFlag =true;
                    if (IDataUtil.isNotEmpty(userInfo))
                    {
        				String netTypeCode =userInfo.getString("NET_TYPE_CODE","");
        				if ("05".equals(netTypeCode))   //固话号码
        				{
        					String userId = userInfo.getString("USER_ID","");
        					
        					IData inparams = new DataMap();
        					String custId = ucaInfo.getString("CUST_ID", "");

        					// 查用户关系表，判断是否有uu关系
                            inparams.put("USER_ID_B", userId);
                            inparams.put("CUST_ID", custId);
                            inparams.put("RELATION_TYPE_CODE", "S1");
                            IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(inparams);
        		             
                            if (DataUtils.isEmpty(idsUU))
                            {//没有开通多媒体桌面电话
        						IMSFlag =false;
        					}
        				}
					}
                    else {
                    	IMSFlag =false;
					}
                    
                    if(!IMSFlag)
                    {
                        return;
                    }
                    else
                    {
                        WorkformStepBean.updByBusiformNodeIdStepId(busiformNodeId,"10000013","0","before");

                        return ;
                    }
                }
            }
        }
	}
}
