SELECT row_num,to_char(batch_id) batch_id,mphonecode,TO_CHAR(user_id) user_id,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,TO_CHAR(olcom_finish_time,'YYYY-MM-DD HH24:MI:SS') olcom_finish_time,TO_CHAR(boss_finish_time,'YYYY-MM-DD HH24:MI:SS') boss_finish_time,param_code,param_hlr_value,param_boss_value,olcom_serv_code||'|'||param_code olcom_serv_code
FROM(SELECT batch_id,mphonecode,user_id,accept_date,olcom_finish_time,boss_finish_time,param_code,param_hlr_value,param_boss_value,olcom_serv_code,ROWNUM row_num
FROM(SELECT batch_id,mphonecode,user_id,accept_date,olcom_finish_time,boss_finish_time,b.param_code,param_hlr_value,param_boss_value,olcom_serv_code
FROM tf_b_batolcomcomp a,tf_b_batolcomcomp_var b
WHERE a.olcom_work_id=b.olcom_work_id
AND accept_date BETWEEN TO_DATE(:START_DATE,'YYYYMMDD') AND TO_DATE(:END_DATE,'YYYYMMDD')+1
AND a.differ_flag='1'
AND b.differ_flag='1'
AND eparchy_code=:TRADE_EPARCHY_CODE AND 1=1 ) c
WHERE ROWNUM <= :X_SELCOUNT*:X_CHANNEL_NUM) b WHERE row_num > (:X_CHANNEL_NUM-1)*:X_SELCOUNT