SELECT integrate_item_code,
       '' integrate_item,
       sum(fee) fee,
       sum(balance) balance,
       SUM(a_discnt) a_discnt,
       SUM(b_discnt) b_discnt,
       SUM(adjust_before) adjust_before,
       SUM(adjust_after) adjust_after,
       SUM(late_fee) late_fee,
       SUM(late_balance) late_balance,
       user_id,
       acyc_id,
       pay_tag
  FROM ts_a_bill_rc
 WHERE user_id = TO_NUMBER(:USER_ID) AND acyc_id = :ACYC_ID
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
 group by integrate_item_code, user_id, acyc_id,pay_tag