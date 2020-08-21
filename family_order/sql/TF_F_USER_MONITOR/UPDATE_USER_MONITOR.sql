UPDATE tf_f_user_monitor
   SET cust_name=:CUST_NAME,sex=:SEX,birthday=TO_DATE(:BIRTHDAY, 'YYYY-MM-DD HH24:MI:SS'),school_name=:SCHOOL_NAME,grad_school=:GRAD_SCHOOL,classname=:CLASSNAME,main_duty=:MAIN_DUTY,interest=:INTEREST,serial_number1=:SERIAL_NUMBER1,serial_number2=:SERIAL_NUMBER2,active_flag=:ACTIVE_FLAG,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_num3=TO_NUMBER(:RSRV_NUM3),rsrv_num4=TO_NUMBER(:RSRV_NUM4),rsrv_dat5=TO_DATE(:RSRV_DAT5, 'YYYY-MM-DD HH24:MI:SS'),rsrv_dat6=TO_DATE(:RSRV_DAT6, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   and end_date > sysdate