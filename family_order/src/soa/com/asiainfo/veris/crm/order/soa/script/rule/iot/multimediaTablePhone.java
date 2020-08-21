package com.asiainfo.veris.crm.order.soa.script.rule.iot;

import com.ailk.bre.tools.BreFactory;
import com.ailk.bre.tools.BreTipsHelp;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 存在已办理预约生效套餐，不允许再变更资费
 * */
public class multimediaTablePhone extends BreBase implements IBREScript{
	private static Logger logger = Logger.getLogger(multimediaTablePhone.class);

	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) {
			logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<进入multimediaTablePhone()>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		DateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置日期格式
		String nowTime = currentTime.format(new Date());   //获取当前时间
		IDataset discntList = databus.getDataset("TF_B_TRADE_ATTR"); //获取台账资费信息
		logger.debug("进入discntList:"+discntList);
		IDataset userDiscntList = databus.getDataset("TF_F_USER_ATTR_AFTER");//获取用户台账资费信息
		logger.debug("进入userDiscntList:"+userDiscntList);
		IDataset tradeDiscntList = databus.getDataset("TF_B_TRADE_DISCNT");   //获取优惠表
		String errorID = "";
		String errorInfo = "";
		for(int p = 0; p < tradeDiscntList.size(); p++) {
			IData tradeDiscnt = tradeDiscntList.getData(p);
			String tradeModifg = tradeDiscnt.getString("MODIFY_TAG");
			String tradeInstId = tradeDiscnt.getString("INST_ID");
			if ("2".equals(tradeModifg)) {
				for (int i = 0; i < discntList.size(); i++) {
					logger.debug("进入for循环");
					IData discntRecord = discntList.getData(i);
					String modifyTag = discntRecord.getString("MODIFY_TAG");
					String discntCode = discntRecord.getString("ATTR_CODE");
					String relaInstId = discntRecord.getString("RELA_INST_ID");
					if (tradeInstId.equals(relaInstId)) {
						if("0".equals(modifyTag)) {
							logger.debug("进入第一个if语句");
							logger.debug("获取discntCode："+discntCode);
							logger.debug("获取userID："+relaInstId);
							for (int j = 0; j < userDiscntList.size(); j++) {
								logger.debug("进入第二个for循环");
								IData usercntRecord = userDiscntList.getData(j);
								String discntCodeUser = usercntRecord.getString("ATTR_CODE");
								String userRelaInstId = usercntRecord.getString("RELA_INST_ID");
								String elementID = usercntRecord.getString("ELEMENT_ID");
								String userStartTime = usercntRecord.getString("START_DATE");
								String userEndTime = usercntRecord.getString("END_DATE");
								logger.debug("获取discntCodeUser："+discntCodeUser);
								logger.debug("获取userIDUser："+userRelaInstId);
								logger.debug("获取userStartTime："+userStartTime);
								logger.debug("获取userEndTime："+userEndTime);
								if(relaInstId.equals(userRelaInstId) && discntCode.equals(discntCodeUser)) {
									logger.debug("进入第二个if语句");
									try {
										Date time = currentTime.parse(nowTime);
										Date timeTwo = currentTime.parse(userStartTime);
										Date timeThree = currentTime.parse(userEndTime);
										errorID = elementID;
										logger.debug("获取当前时间："+time);
										logger.debug("获取开始时间："+timeTwo);
										logger.debug("获取结束时间："+timeThree);
										if (time.getTime() < timeTwo.getTime() && time.getTime() < timeThree.getTime()) {
											logger.debug("进入第三个if语句");
											errorInfo = "操作异常！当前用户已经成功预约办理"+"("+errorID +")"+"下月生效的资费，多次错变更会导致数据异常";
											BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20190923, errorInfo);
											break;
										}
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
}
