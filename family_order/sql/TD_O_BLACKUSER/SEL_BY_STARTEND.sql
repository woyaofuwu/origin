--IS_CACHE=Y
SELECT pspt_type_code,pspt_id,black_user_class_code,mob_phonecode,bank_acct_no,join_cause,to_char(start_date,'yyyy-mm-dd') start_date,to_char(end_date,'yyyy-mm-dd') end_date,to_char(update_time,'yyyy-mm-dd') update_time,update_staff_id,update_depart_id,remark ,0 x_tag
  FROM td_o_blackuser
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND 
       ((TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') BETWEEN start_date AND end_date)
   OR  (TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') BETWEEN start_date AND end_date)
   OR  (TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') < start_date AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')>end_date))