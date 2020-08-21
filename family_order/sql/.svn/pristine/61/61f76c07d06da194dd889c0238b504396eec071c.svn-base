SELECT  serial_number ,to_char(user_id) user_id, partition_id, para_code2, para_code3, para_code4, para_code5, para_code6, para_code7,
         para_code8, para_code9, para_code10, para_code11, para_code12, para_code13, para_code14, para_code15, para_code16,
         para_code17, para_code18, para_code19, para_code20, para_code21, para_code22, para_code23, para_code24, para_code25, 
         para_code26, para_code27, para_code28, para_code29, para_code30, to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,para_code31,para_code32,para_code33,para_code34,para_code35,para_code36,para_code37,
para_code38,para_code39,para_code40,para_code41,para_code42,para_code43,para_code44,para_code45,para_code46,
para_code47,para_code48,para_code49,para_code50,para_code51,para_code52,para_code53,para_code54,para_code55,
para_code56,para_code57,para_code58,para_code59,para_code60,
         decode(para_code1, '01','该用户是全球通目标用户，'||'前3月月平均消费为'
                                  ||NVL(to_char(to_number(para_code2)/100),'大于60')||'元，'||'推荐办理'
                                  ||decode(para_code3,'1','商务全球通68包168元套餐',
                                                      '2','商务全球通98包268元套餐',
                                                      '3','新全球通包干套餐','全球通套餐'),/*常德全球通推荐*/
                            '02','该用户所属区号为：'||NVL(para_code2,'')||'，,ARPU为：'||NVL(para_code3,'')||'分，'||'MOU为：'||
                                  NVL(para_code4,'')||'分钟，'||'本地通话时长为：'||NVL(para_code5,'')||'分钟，'||'本地主叫通话时长为：'||
                                  NVL(para_code6,'')||'分钟，'||'本地被叫通话时长为：'||NVL(para_code7,'')||'分钟，'||'长途通话时长为：'||
                                  NVL(para_code8,'')||'分钟，'||'非漫游长途计费时长为：'||NVL(para_code9,'')||'分钟，'||'漫游长途计费时长为：'||
                                  NVL(para_code10,'')||'分钟，'||'点对点短信条数：'||NVL(para_code11,'')||'条，'||
                                  decode(para_code12,'0','不支持',
                                                      '1','支持','不知是否支持')||'GPRS，'||
                                  decode(para_code13,'0','不支持',
                                                      '1','支持','不知是否支持')||'彩信，'||
                                  decode(para_code14,'0','不支持',
                                                      '1','支持','不知是否支持')||'WAP。',
                            '03','您当前使用的是'||NVL(para_code40,'')||'品牌，近3个月平均月消费'||NVL(to_number(para_code29),'')||'元，如果使用'||NVL(para_code41,'')||'品牌'||NVL(para_code38,'')||'套餐，您每月的消费将降低到'||NVL(to_number(para_code35),'')||'元左右，每月能节约'||NVL(to_number(para_code36),'')||'元左右，同时还可以参加积分兑奖活动。',
                            '04','您当前使用的是'||NVL(para_code40,'')||'品牌，近3个月平均月消费'||NVL(round((to_number(para_code17)+to_number(para_code18)+to_number(para_code19))/3,2),'')||'元，根据对您的通话信息统计，由于'||NVL(para_code20,'')||'原因，建议您使用'||NVL(para_code41,'')||'品牌'||NVL(para_code38,'')||'套餐.',
                            '') para_code1 --default
  FROM  tf_f_user_wholeinfo
 WHERE  user_id=:USER_ID