SELECT to_char(bill_id) bill_id,
       to_char(acct_id) acct_id,
       to_char(user_id) user_id,
       partition_id,
       acyc_id,
       integrate_item_code,
       to_char(fee) fee,
       to_char(balance) balance,
       to_char(print_fee) print_fee,
       to_char(b_discnt) b_discnt,
       to_char(a_discnt) a_discnt,
       to_char(adjust_before) adjust_before,
       to_char(adjust_after) adjust_after,
       to_char(late_fee) late_fee,
       to_char(late_balance) late_balance,
       to_char(latecal_date, 'yyyy-mm-dd hh24:mi:ss') latecal_date,
       canpay_tag,
       pay_tag,
       bill_pay_tag,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_depart_id,
       update_staff_id
  FROM TS_A_BILL
 WHERE (ACCT_ID, PARTITION_ID, user_id, ACYC_ID) in
       (SELECT ACCT_ID, MOD(ACCT_ID, 10000), a.USER_ID, :ACYC_ID ACYC_ID
          FROM TF_F_USER_SPECIALEPAY a
         where a.user_id IN (SELECT user_id
                               FROM tf_a_payrelation
                              WHERE acct_id = :ACCT_ID
                                AND start_acyc_id <= :ACYC_ID
                                AND :ACYC_ID <= end_acyc_id
                                AND default_tag = '1')
           AND a.acct_id <> :ACCT_ID
           and a.start_acyc_id <= :ACYC_ID
           and a.end_acyc_id >= :ACYC_ID)
union all
SELECT to_char(bill_id) bill_id,
       to_char(acct_id) acct_id,
       to_char(user_id) user_id,
       partition_id,
       acyc_id,
       integrate_item_code,
       to_char(fee) fee,
       to_char(balance) balance,
       to_char(print_fee) print_fee,
       to_char(b_discnt) b_discnt,
       to_char(a_discnt) a_discnt,
       to_char(adjust_before) adjust_before,
       to_char(adjust_after) adjust_after,
       to_char(late_fee) late_fee,
       to_char(late_balance) late_balance,
       to_char(latecal_date, 'yyyy-mm-dd hh24:mi:ss') latecal_date,
       canpay_tag,
       pay_tag,
       bill_pay_tag,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_depart_id,
       update_staff_id
  FROM TS_AH_BILL
 WHERE (ACCT_ID, PARTITION_ID, user_id, ACYC_ID) in
       (SELECT ACCT_ID, MOD(ACCT_ID, 10000), a.USER_ID, :ACYC_ID ACYC_ID
          FROM TF_F_USER_SPECIALEPAY a
         where a.user_id IN (SELECT user_id
                               FROM tf_a_payrelation
                              WHERE acct_id = :ACCT_ID
                                AND start_acyc_id <= :ACYC_ID
                                AND :ACYC_ID <= end_acyc_id
                                AND default_tag = '1')
           AND a.acct_id <> :ACCT_ID
           and a.start_acyc_id <= :ACYC_ID
           and a.end_acyc_id >= :ACYC_ID)