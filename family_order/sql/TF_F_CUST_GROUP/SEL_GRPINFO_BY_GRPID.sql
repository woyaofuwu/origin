SELECT u.partition_id,u.user_id,p.group_id,u.serial_number,p.audit_state,to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') uopen_date, 
       p.cust_name,u.open_date ,t.product_id
FROM   tf_f_user u,tf_f_cust_group p ,tf_f_user_product  t
WHERE  u.cust_id=p.cust_id 
        AND t.user_id=u.user_id
        AND t.partition_id=mod(u.user_id,10000)
       AND p.group_id= :GROUP_ID
       AND u.remove_tag='0' 