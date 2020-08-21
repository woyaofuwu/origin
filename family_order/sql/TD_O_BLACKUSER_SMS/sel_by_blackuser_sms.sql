select black_type, init_serinum, to_char(start_scout_time,'yyyy-mm-dd hh24:mi:ss') start_scout_time , to_char(end_scout_time,'yyyy-mm-dd hh24:mi:ss') end_scout_time, called_serinum,decode(out_reason,'1','关键字违规','2','流量超标违规','3','发送号码为连续号段','4','被叫均为外省用户','未知定义') out_reason, out_num, msgid,  to_char(start_time,'yyyy-mm-dd hh24:mi:ss')start_time,  to_char(end_time,'yyyy-mm-dd hh24:mi:ss')end_time, eparchy_code, msg_content, remark,  to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time, upate_staff, update_depart, rsrv_str1, rsrv_str2, rsrv_str3 ,0 x_tag
from td_o_blackuser_sms
where  
(:INIT_SERINUM IS NULL OR (:INIT_SERINUM IS NOT NULL AND init_serinum=:INIT_SERINUM))
and (:CALLED_SERINUM IS NULL OR (:CALLED_SERINUM IS NOT NULL AND called_serinum=:CALLED_SERINUM))
and (:EPARCHY_CODE is null or (:EPARCHY_CODE is not null and eparchy_code =to_char(:EPARCHY_CODE)))
and (:START_SCOUT_TIME is null or(:START_SCOUT_TIME is not null and  start_time>to_date(:START_SCOUT_TIME,'yyyy-mm-dd hh24:mi:ss')))
and (:END_SCOUT_TIME is null or(:END_SCOUT_TIME  is not null and  end_time<to_date(:END_SCOUT_TIME,'yyyy-mm-dd hh24:mi:ss')))