SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_f_user_discnt
 WHERE discnt_code in (:DISCNT_CODES)
 AND user_id = :USER_ID
 and to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS') between start_date and end_date
 and to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS') between start_date and end_date