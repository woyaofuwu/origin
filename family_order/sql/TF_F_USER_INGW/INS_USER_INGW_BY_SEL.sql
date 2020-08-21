INSERT INTO tf_f_user_ingw(partition_id,user_id,serial_number,regist_date,rsrv_str1,rsrv_str2,rsrv_num3,rsrv_num4,rsrv_dat5,rsrv_dat6)
SELECT partition_id,user_id,serial_number,SYSDATE,rsrv_str1,rsrv_str2,rsrv_num3,rsrv_num4,rsrv_dat5,rsrv_dat6
FROM tf_f_user_ingw_inc WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND user_id=TO_NUMBER(:USER_ID)