package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChkWlwMebDiscntChange extends BreBase implements IBREScript
{
	
private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(ChkWlwMebDiscntChange.class);
    
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ChkWlwMebDiscntChange()  >>>>>>>>>>>>>>>>>>");
		
		String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
		String userId = databus.getString("SELECTED_USER_ID");
		String err = "";
		String errCode = ErrorMgrUtil.getErrorCode(databus);
		if (StringUtils.isBlank(userElementsStr))
            return false;
        IDataset userElements = new DatasetList(userElementsStr);
        for(int i=0; i<userElements.size(); i++)
        {
        	IData element = userElements.getData(i);
        	// 物联网成员退订资费 如果 资费已经是月底结束就不允许在退订
        	if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")) && "1".equals(element.getString("MODIFY_TAG")))
            {
        		String discntCode = element.getString("ELEMENT_ID");
        		String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
        		IDataset discntInfos = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(userId, discntCode);
        		if(IDataUtil.isEmpty(discntInfos))
        		{
        			return false;
        		}
        		String endDate = discntInfos.getData(0).getString("END_DATE");
        		if(endDate.equals(SysDateMgr.getLastDateThisMonth()))
        		{
        			err = "退订的资费["+discntName+"]已经是月底结束，不允许在进行退订!";

                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        		}
        		
            }
        }
        
        
        //进行某些具有限制的产品的规则校验
        IDataset configinfos = CommparaInfoQry.getCommByParaAttr("CSM", "4546", "ZZZZ");
		if(IDataUtil.isEmpty(configinfos)){
			BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 888888,"数据库未配置 ChkWlwMebDiscntChange 类的规则的数据！请配置后重试！");
			return true;
		}
		
		int count=0;
		if(IDataUtil.isNotEmpty(userElements)){
			for(Object object : configinfos ){
				IData data=(IData) object;//一条
				String errormsg=data.getString("PARA_CODE25");//字段25，放该配置的报错信息。
				String limitoffer=getOfferCode(data.getString("PARAM_CODE"));//已转本地discnt_code
				int limitnum=Integer.parseInt(data.getString("PARA_CODE2"));//字段2，放的是限制产品数量
				String[] limitPkg = new String[30];//互斥产品包数组（集团）
				for(int i=1;i<25;i++){//字符串字段只有25个
					if(i==2) continue;
					String col="PARA_CODE";
					limitPkg[i]=data.getString(col+i);
				}
				//此次限制的产品+依赖的包下产品，不能超过2个元素
				if(limitoffer!=null&& !limitoffer.equals("")){
					outer:for(int i=0;i<userElements.size();i++){
						String modify_tag=userElements.getData(i).getString("MODIFY_TAG");//操作类型
						String discnt_code=userElements.getData(i).getString("ELEMENT_ID");//操作优惠编码
						//取新增和存量。退订和修改均不操作
						if("EXIST".equals(modify_tag)||"0".equals(modify_tag)){
							//本产品
							if(limitoffer.equals(discnt_code)){
								count++;
								continue;
							}
							//依赖产品
							for(int a=0;a<limitPkg.length;a++){
								if(limitPkg[a]!=null&& !"".equals(limitPkg[a])){
									String[] loaclcodes=getPkgOfferCode(limitPkg[a]);//本地优惠
									if(contains(discnt_code,loaclcodes)){
										count++;
										continue outer;
									}
								}
							}
						}
					}
				}
				if(count>limitnum){
					BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 888888 ,errormsg);
					return false;
				}
				count=0;//一条配置循环完成，初始化
			}
		}
		
        
        
        
        return true;
	}
	
	
	/**
	 * 取本地优惠数组
	 * @param Ipkg 集团包ID
	 * @return 
	 * @throws Exception
	 */
	public String[] getPkgOfferCode(String Ipkg) throws Exception{
		IDataset pkginfos = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", Ipkg, "ZZZZ", null);
		if(!pkginfos.isEmpty()){
			String[] result =new String[pkginfos.size()];
			for (int i = 0; i < pkginfos.size(); i++) {
				result[i]=pkginfos.getData(i).getString("PARAM_CODE");
			}
			return result;
		}
		return null;
	}
	/**
	 * 获取本地offer code
	 * @param Iproduct
	 * @return
	 * @throws Exception
	 */
	public String getOfferCode(String Iproduct) throws Exception{
		IDataset productinfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9013", Iproduct, "ZZZZ", null);
		if(DataUtils.isNotEmpty( productinfos)){
			return productinfos.first().getString("PARAM_CODE");
		}else{
			return "";//确保不空指针
		}
		
	}
	/**
	 * 判断有没有在这个数组里面
	 * @param offercode
	 * @param relcode
	 * @return true在 false不在
	 */
	public boolean contains(String offercode,String[] relcode){
		for(int i=0;i<relcode.length;i++){
			if(relcode[i].equals(offercode)){
				return true;
			}
		}
		return false;
	}

}
