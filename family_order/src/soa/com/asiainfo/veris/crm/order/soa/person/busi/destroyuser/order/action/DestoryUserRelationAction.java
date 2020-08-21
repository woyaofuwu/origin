package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class DestoryUserRelationAction implements ITradeAction
{
	/**
	 * 销户时结束虚拟用户已经虚拟用户下的UU关系
	 */
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		// TODO Auto-generated method stub
		String sn = btd.getRD().getUca().getSerialNumber();
		String userId = btd.getRD().getUca().getUserId();
		//配置需要处理掉的个人的关系类型 格式  比如28|75|45
		String relaTypeStrs = StaticUtil.getStaticValue("DESTORY_RELA_TYPE", "0");
		if(StringUtils.isBlank(relaTypeStrs))
		{
			return;
		}
		IDataset userRelas = this.getUserAllRelations(userId);
		if(IDataUtil.isNotEmpty(userRelas))
		{
			for(int i=0,size = userRelas.size();i<size;i++)
			{
				IData temp = userRelas.getData(i);
				String roleCodeB = temp.getString("ROLE_CODE_B");
				if(StringUtils.equals("1", roleCodeB))
				{
					String userIdA = temp.getString("USER_ID_A");
					String relaType = temp.getString("RELATION_TYPE_CODE");
					if(StringUtils.indexOf(relaTypeStrs, relaType) != -1)
					{
						//结束虚拟用户
						IData userA = UcaInfoQry.qryUserInfoByUserId(userIdA,null);
						if(IDataUtil.isEmpty(userA) || !StringUtils.equals("0", userA.getString("REMOVE_TAG")))
						{
							continue;
						}
						UserTradeData userATd = new UserTradeData(userA);
						userATd.setRemoveTag("2");
						userATd.setDestroyTime(btd.getRD().getAcceptTime());
						userATd.setModifyTag(BofConst.MODIFY_TAG_DEL);
						btd.add(sn, userATd);
						//结束虚拟用户下的所有关系
						IDataset userIdARelas = RelaUUInfoQry.getAllValidRelaByUserIDA(userIdA);
						if(IDataUtil.isNotEmpty(userIdARelas))
						{
							for(int j=0,sizej = userIdARelas.size();j<sizej;j++)
							{
								IData tempRela = userIdARelas.getData(j);
								RelationTradeData relatd = new RelationTradeData(tempRela);
								relatd.setEndDate(SysDateMgr.getLastDateThisMonth());
								relatd.setModifyTag(BofConst.MODIFY_TAG_DEL);
								btd.add(sn, relatd);
							}
						}
					}
				}
			}
		}
	}
	
	private IDataset getUserAllRelations(String userIdB) throws Exception
	{
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT * FROM TF_F_RELATION_UU WHERE USER_ID_B=:USER_ID_B AND ROLE_CODE_B ='1'");
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		return Dao.qryBySql(sql, param);
	}
}
