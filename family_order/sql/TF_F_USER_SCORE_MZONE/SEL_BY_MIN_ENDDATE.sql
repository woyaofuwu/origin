SELECT to_char(user_id) user_id,to_char(score_value) score_value,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,prevaluec1,prevaluec2,prevaluec3,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevaluen3) prevaluen3,to_char(prevalued1,'yyyy-mm-dd hh24:mi:ss') prevalued1,to_char(prevalued2,'yyyy-mm-dd hh24:mi:ss') prevalued2,to_char(prevalued3,'yyyy-mm-dd hh24:mi:ss') prevalued3 
  FROM tf_f_user_score_mzone
  WHERE user_id=TO_NUMBER(:USER_ID) AND end_date IN 
(select MIN(end_date) from TF_F_USER_SCORE_MZONE where user_id=TO_NUMBER(:USER_ID) and SYSDATE BETWEEN
start_date+0 and end_date+0)