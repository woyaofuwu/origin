select t.black_type, t.init_serinum, to_char(t.start_scout_time,'yyyy-mm-dd hh24:mi:ss') start_scout_time , to_char(t.end_scout_time,'yyyy-mm-dd hh24:mi:ss') end_scout_time, t.called_serinum, t.out_reason, t.out_num, t.msgid,  to_char(t.start_time,'yyyy-mm-dd hh24:mi:ss') start_time,  to_char(t.end_time,'yyyy-mm-dd hh24:mi:ss')end_time, t.eparchy_code, t.msg_content, t.remark,  to_char(t.update_time,'yyyy-mm-dd hh24:mi:ss') update_time, t.upate_staff, t.update_depart, t.rsrv_str1, t.rsrv_str2, t.rsrv_str3 ,0 x_tag
from td_oh_blackuser_sms t ,tf_f_user b
 WHERE b.user_id=:USER_ID AND t.init_serinum=b.serial_number
AND b.remove_tag='0' AND t.start_time>b.open_date
AND t.end_time>SYSDATE