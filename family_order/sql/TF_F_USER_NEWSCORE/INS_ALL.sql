INSERT INTO tf_f_user_newscore(user_id,partition_id,year_id,acyc_id,id_type,score_type_code,score,rsrv_str1,rsrv_str2,rsrv_num1,rsrv_num2,rsrv_date1,rsrv_date2)
 VALUES(TO_NUMBER(:USER_ID),MOD(TO_NUMBER(:USER_ID),10000),:YEAR_ID,:ACYC_ID,:ID_TYPE,:SCORE_TYPE_CODE,TO_NUMBER(:SCORE),:RSRV_STR1,:RSRV_STR2,TO_NUMBER(:RSRV_NUM1),TO_NUMBER(:RSRV_NUM2),TO_DATE(:RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS'))