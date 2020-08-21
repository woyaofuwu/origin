select  a.user_id,a.cust_id,a.serial_number,n.acct_id from tf_f_cust_group p 
left join tf_f_user a on p.cust_id=a.cust_id
left join tf_f_user_product t  on a.user_id=t.user_id
left join TF_A_PAYRELATION n on a.user_id=n.user_id  
where  t.product_id='1116011'
and p.mp_group_cust_code=:PRODUCT_ORDER_ID