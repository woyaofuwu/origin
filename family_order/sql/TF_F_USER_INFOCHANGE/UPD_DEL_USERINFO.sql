UPDATE /*+ index(t IDX_TF_F_USER_INFOCHG_UID) */ tf_f_user_infochange t
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-1/24/3600
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')