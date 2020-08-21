select b.User_Id,
       b.net_type_code,
       b.Product_Id,
       b.Brand_Code,
       b.Serial_Number,
       b.Imsi,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
       b.INST_ID
  from TF_F_USER_INFOCHANGE b
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > sysdate
   AND end_date > start_date