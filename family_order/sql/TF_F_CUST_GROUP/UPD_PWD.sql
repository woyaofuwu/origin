UPDATE tf_f_cust_group
   SET unify_pay_code=:UNIFY_PAY_CODE
 WHERE group_id=:GROUP_ID
   AND remove_tag=:REMOVE_TAG