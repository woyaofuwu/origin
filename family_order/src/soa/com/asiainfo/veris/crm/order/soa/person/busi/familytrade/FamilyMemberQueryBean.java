
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class FamilyMemberQueryBean extends CSBizBean
{

    /**
     * 查询前的校验
     * 
     * @param inData
     * @throws Exception
     */
    public void checkInData(IData inData) throws Exception
    {
        if (IDataUtil.isEmpty(inData))
        {
            // 输入数据为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_706);
        }

        String serialNumber = inData.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
            // SERIAL_NUMBER为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }
    }

    /**
     * tag:HNYD-REQ-20110811-007 传入dataset，列名返回这个列所有行拼成的String,并加上序号,最后一个不加句号
     * 
     * @create_date Aug 16, 2011
     * @author wengdq 引入时间：2014-08-22 引入人：zhouwu
     */
    public String fromDatasetToString(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData element = dataset.getData(i);
            buffer.append(element.getString(columnName));
            buffer.append("\", \"");
        }
        return buffer.length() > 0 ? buffer.replace(buffer.length() - 4, buffer.length(), "").toString() : "";
    }

    /**
     * 传入dataset，列名返回这个列所有行拼成的String,并加上序号
     * 
     * @create_date Aug 10, 2009
     * @author heyq
     */
    public String fromDatasetToStringSeq(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData element = dataset.getData(i);
            buffer.append(i + 1);
            buffer.append("、");
            buffer.append(element.getString(columnName));
            buffer.append(",");
        }
        return buffer.length() > 0 ? buffer.replace(buffer.length() - 1, buffer.length(), "。").toString() : "";
    }

    /**
     * 取出主号码
     * 
     * @create_date Oct 10, 2009
     * @author heyq 引入时间：2014-08-22 引入人：zhouwu
     */
    public String getMainSerialNumber(IDataset allMemInfo)
    {
        String serialNumber = "";
        for (Iterator iter = allMemInfo.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();
            if ("1".equals(element.getString("ROLE_CODE_B")))
            {
                serialNumber = element.getString("SERIAL_NUMBER_B");
                break;
            }
        }
        return serialNumber;
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }

    /**
     * 亲亲网成员查询接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryFamilyMeb(IData input) throws Exception
    {
        // 查询前的校验
        checkInData(input);

        String serialNumber = input.getString("SERIAL_NUMBER");

        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // SERIAL_NUMBER无效
            CSAppException.apperr(FamilyException.CRM_FAMILY_78);
        }

        String userId = user.getString("USER_ID");
        //IDataset result = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);
		List<DiscntTradeData> result = uca.getUserDiscntsByDiscntCodeArray(discntArrays);  
        if (ArrayUtil.isEmpty(result))
        {
            // 您还未开通任何亲亲网套餐,不能办理该业务！
            CSAppException.apperr(FamilyException.CRM_FAMILY_533);
        }

        String userIdA = result.get(0).getUserIdA();
        IDataset mebList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");

        if (IDataUtil.isEmpty(mebList))
        {
            // 查询不到成员的亲亲关系
            CSAppException.apperr(FamilyException.CRM_FAMILY_85);
        }

        IDataset returnList = new DatasetList();

        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData memInfo = mebList.getData(i);
            String memUserId = memInfo.getString("USER_ID_B");
            String roleCodeB = memInfo.getString("ROLE_CODE_B");
            IData mebUser = UcaInfoQry.qryUserMainProdInfoByUserId(memUserId);
            if ( IDataUtil.isNotEmpty(mebUser) ) {
            	String RM = mebUser.getString("REMOVE_TAG", "");
            	if( !"0".equals(RM) ){ continue; }
            }else{ continue; }
            
            IDataset appDiscntList = UserDiscntInfoQry.queryUserDiscntByParamattr(memUserId, "1009");
            memInfo.put("DISCNT_NAME_A", "");// 当前叠加包
            memInfo.put("DISCNT_NAME_B", "");// 预约叠加包
            if (IDataUtil.isNotEmpty(appDiscntList))
            {
                IData appDiscnt = appDiscntList.getData(0);
                String appDiscntName = appDiscnt.getString("DISCNT_NAME");
                memInfo.put("DISCNT_NAME_A", appDiscntName);
                memInfo.put("DISCNT_NAME_B", appDiscntName);
            }

            IDataset userDiscnts = UserDiscntInfoQry.qryUserNormalDiscntByIdCodeFromDB(memUserId, "45", "");
            String discntCode = userDiscnts.getData(0).getString("DISCNT_CODE", "");

            String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
            // memInfo.put("DISCNT_NAME", discntName);//优惠名称

            //IData mebUser = UcaInfoQry.qryUserMainProdInfoByUserId(memUserId);
            if (IDataUtil.isNotEmpty(mebUser))
            {
        		String campusCard = "0";// 是否校园卡，默认0（不是），1（是）
                String productId = mebUser.getString("PRODUCT_ID");

                IDataset dataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "697", roleCodeB, this.getUserEparchyCode());
                if (IDataUtil.isNotEmpty(dataset))
                {
                    for (int j = 0, length = dataset.size(); j < length; j++)
                    {
                        IData each = dataset.getData(j);
                        String paraCode1 = each.getString("PARA_CODE1", "");
                        if (StringUtils.equals(productId, paraCode1))
                        {
                            campusCard = "1";
                            break;
                        }
                    }
                }

                memInfo.put("CAMPUS_CARD", campusCard);
            }
            else
            {
            	continue;
                //CSAppException.apperr(CrmUserException.CRM_USER_117, memInfo.getString("SERIAL_NUMBER_B"));
            }

            memInfo.put("SERIAL_NUMBER_C", memInfo.getString("SERIAL_NUMBER_B"));
            memInfo.put("TYPE", memInfo.getString("ROLE_CODE_B"));

            String serialNumberB = memInfo.getString("SERIAL_NUMBER_B");

            IData data = new DataMap();
            data.put("SERIAL_NUMBER_B", serialNumberB);
            data.put("SERIAL_NUMBER_C", serialNumberB);
            data.put("TYPE", roleCodeB);
            data.put("START_DATE", memInfo.getString("START_DATE"));
            data.put("END_DATE", memInfo.getString("END_DATE"));
            data.put("DISCNT_CODE", discntCode);
            data.put("DISCNT_NAME", discntName);
            data.put("DISCNT_NAME_A", memInfo.getString("DISCNT_NAME_A"));
            data.put("DISCNT_NAME_B", memInfo.getString("DISCNT_NAME_B"));
            String strSC = memInfo.getString("SHORT_CODE");
            /*if( "".equals(strSC) || strSC == null ){
            	strSC = memInfo.getString("RSRV_STR2");
            }*/
            
            data.put("SHORT_CODE", strSC);
            data.put("CAMPUS_CARD", memInfo.getString("CAMPUS_CARD"));

            returnList.add(data);
        }

        /*
         * String serialNumberBs = fromDatasetToStringSeq(mebList,"SERIAL_NUMBER_B"); String serialNumberCs =
         * fromDatasetToString(mebList,"SERIAL_NUMBER_B"); String type = fromDatasetToString(mebList,"ROLE_CODE_B");
         * String startDate = fromDatasetToString(mebList,"START_DATE"); String endDate =
         * fromDatasetToString(mebList,"END_DATE"); String discntName = fromDatasetToString(mebList,"DISCNT_NAME");
         * String discntNameA = fromDatasetToString(mebList,"DISCNT_NAME_A"); String discntNameB =
         * fromDatasetToString(mebList,"DISCNT_NAME_B"); String shortCode = fromDatasetToString(mebList,"SHORT_CODE");
         * String campusCard = fromDatasetToString(mebList,"CAMPUS_CARD"); IData backInfo = new DataMap();
         * backInfo.put("X_RESULTCODE", "0"); backInfo.put("X_RESULTINFO", "家庭成员号码查询成功"); backInfo.put("X_RECORDNUM",
         * mebList.size()); backInfo.put("SERIAL_NUMBER_B", serialNumberBs); backInfo.put("SERIAL_NUMBER",
         * getMainSerialNumber(mebList)); backInfo.put("SERIAL_NUMBER_C", serialNumberCs); backInfo.put("TYPE", type);
         * backInfo.put("START_DATE", startDate); backInfo.put("END_DATE", endDate); backInfo.put("DISCNT_NAME",
         * discntName); backInfo.put("DISCNT_NAME_A", discntNameA); backInfo.put("DISCNT_NAME_B", discntNameB);
         * backInfo.put("SHORT_CODE", shortCode); backInfo.put("CAMPUS_CARD", campusCard);
         */

        return returnList;
    }

	/**
	 * @Description：查询是否指定时间加入亲亲网
	 * @param:@param input
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午03:24:53
	 */
	public String queryJoinFamily(IData input) throws Exception {
		String result = "0";
		
		
		String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        IDataset res = RelaUUInfoQry.queryJoinFamily(user.getString("USER_ID"), "45", input.getString("START_DATE"));
        if (IDataUtil.isNotEmpty(res))
        {
        	result = "1";
        }
		
		return result;
		
	}

	/**
	 * @Description：查询是否指定时间增加亲亲网成员
	 * @param:@param input
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午03:25:00
	 */
	public String queryAddFamily(IData input) throws Exception {
		String result = "0";
		String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        IDataset res = RelaUUInfoQry.queryJoinFamily(user.getString("USER_ID"), "45", null);
        if (IDataUtil.isNotEmpty(res) && "1".equals( res.first().getString("ROLE_CODE_B")))
        {
        	IData data = res.first();
    		String userIdA = data.getString("USER_ID_A");
    		IDataset dataset = RelaUUInfoQry.queryAddFamily(userIdA,"45",input.getString("START_DATE"));
    		if(IDataUtil.isNotEmpty(dataset)){
    			result = "1";
    		}
        }
		return result;
	}

	/**
	 * @Description：BUS201805290029 端午划龙舟3.0开发需求 
	 * @param:@param input
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午03:25:07
	 */
	public IDataset queryFamilyMemList(IData input) throws Exception {
		IDataset dataset = null ;
		String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        IDataset res = RelaUUInfoQry.queryJoinFamily(user.getString("USER_ID"), "45", null);
        if (IDataUtil.isNotEmpty(res))
        {
        	IData data = res.first();
    		String userIdA = data.getString("USER_ID_A");
    		dataset = RelaUUInfoQry.queryFamilyMemList(userIdA,"45");
        }
		
		return potingFamilyMemList(dataset);
		
	}
	private IDataset potingFamilyMemList(IDataset dataset) {
		IDataset resultList = new DatasetList();
		for (int i = 0; i < dataset.size(); i++){
			IData ele = dataset.getData(i);
			IData numberInfo = new DataMap();
			numberInfo.put("SERIAL_NUMBER", ele.getString("SERIAL_NUMBER_B"));
			numberInfo.put("ROLE_CODE_B",ele.getString("ROLE_CODE_B"));
			numberInfo.put("START_DATE",ele.getString("START_DATE"));
			numberInfo.put("END_DATE",ele.getString("END_DATE"));
			resultList.add(numberInfo);
		}	
		return resultList;
	}
}
