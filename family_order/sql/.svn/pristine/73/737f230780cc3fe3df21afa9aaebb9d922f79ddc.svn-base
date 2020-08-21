DELETE FROM tf_f_user_discnt a 
WHERE user_id =to_number(:USER_ID)
 AND partition_id=MOD(to_number(:USER_ID),10000)
 AND (RELATION_TYPE_CODE IS NULL OR  RELATION_TYPE_CODE NOT IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F'))
 AND discnt_code = :DISCNT_CODE
 AND START_DATE  =to_date(:START_DATE,'YYYY-MM-DD HH24:MI:SS')