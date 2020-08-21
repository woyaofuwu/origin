WITH wt_user_discnt AS (SELECT user_id_a,discnt_code,start_date,end_date FROM tf_f_user_discnt WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000) AND user_id_a >0 AND spec_tag='2' AND relation_type_code IS NOT NULL AND SYSDATE<end_date+0),wt_user_grpmbmpsub AS (SELECT ec_user_id,biz_code,start_date,end_Date FROM tf_f_user_grpmbmp_sub WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=MOD(TO_NUMBER(:USER_ID),10000))
SELECT serial_number,c.product_id,c.product_name,TO_CHAR(d.discnt_code) SERV_CODE,discnt_name SERV_NAME,TO_CHAR(a.start_date,'YYYY-MM-DD HH24:MI:SS') start_date,TO_CHAR(a.end_date,'YYYY-MM-DD HH24:MI:SS') end_date,'0' DATA_TYPE
FROM  tf_f_user_infochange a,wt_user_discnt b,td_b_product c,td_b_discnt d
WHERE a.user_id=b.user_id_a AND a.partition_id=MOD(b.user_id_a,10000) AND SYSDATE<a.end_date+0
AND a.product_id=c.product_id AND SYSDATE BETWEEN c.start_Date AND c.end_date
AND b.discnt_code=d.discnt_code AND SYSDATE BETWEEN d.start_date AND d.end_date
UNION ALL SELECT serial_number,c.product_id,c.product_name,b.biz_code,'',TO_CHAR(a.start_date,'YYYY-MM-DD HH24:MI:SS') start_date,TO_CHAR(a.end_date,'YYYY-MM-DD HH24:MI:SS') end_date,'1'
FROM tf_f_user_infochange a,wt_user_grpmbmpsub b,td_b_product c
WHERE a.user_id=b.ec_user_id AND a.partition_id=MOD(b.ec_user_id,10000) AND SYSDATE<a.end_date+0
AND a.product_id=c.product_id AND SYSDATE BETWEEN c.start_Date AND c.end_date