SELECT RES_TYPE_CODE,RES_CODE,IMSI,KI,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') END_DATE
FROM tf_f_user_res 
WHERE 1 = 1
AND user_id = TO_NUMBER(:USER_ID)