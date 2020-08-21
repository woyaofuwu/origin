INSERT INTO tf_f_user_grpmbmp_info(partition_id,user_id,biz_code,pre_charge,max_item_pre_day,max_item_pre_mon,is_text_ecgn,default_ecgn_lang,text_ecgn_en,text_ecgn_zh,start_date,end_date,update_time,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3)
SELECT
a.partition_id,TO_NUMBER(:USER_ID),a.biz_code,a.pre_charge,a.max_item_pre_day,a.max_item_pre_mon,a.is_text_ecgn,a.default_ecgn_lang,a.text_ecgn_en,a.text_ecgn_zh,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),sysdate,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3
from tf_f_user_grpmbmp_info a
WHERE a.user_id=TO_NUMBER(:USER_ID)
  AND a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
  AND a.end_date=TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS')