SELECT     b.user_id, c.product_id,b.serial_number,b.text_ecgn_zh,b.text_ecgn_en,b.serv_code,b.service_id FROM tf_f_user_svc a,tf_f_user_grp_platsvc b,tf_f_user_product c
WHERE 1=1
AND b.group_id=:GROUP_ID
AND  a.user_id=b.user_id
AND a.partition_id=b.partition_id
AND  a.partition_id=c.partition_id
AND c.user_id=a.user_id
AND c.product_id  IN ('9107','9127','9101','9105','9104','9103','9102','9106','9991')
AND c.end_date>SYSDATE
AND a.end_date>SYSDATE
AND b.end_date>SYSDATE
AND b.rsrv_str1='1'  
AND a.service_id=b.service_id