SELECT min(bill_id) bill_id,
        min(integrate_item_code) integrate_item_code,
        integrate_item,
        to_char(sum(fee+b_discnt+a_discnt)) fee,
        to_char(sum(adjust_fee)) adjust_fee,
        to_char(sum(b_discnt)) b_discnt,
        to_char(sum(a_discnt)) a_discnt,
        to_char(sum(balance+b_discnt+a_discnt)) balance,
        to_char(sum(nbalance)) nbalance,
        to_char(sum(late_balance)) late_balance,
        to_char(sum(nlate_balance)) nlate_balance,
        NULL late_flag,
        NULL latecal_date,
        NULL pay_tag,
        NULL npay_tag,
        to_char(sum(imp_fee)) imp_fee,
        min(recv_acyc_id) recv_acyc_id,
        min(mconsign_id) mconsign_id,
        min(acct_id) acct_id,
        min(eparchy_code) eparchy_code,
        min(acyc_id) acyc_id
   FROM tf_a_subconsigninfolog
  WHERE   acct_id=TO_NUMBER(:ACCT_ID)
 AND  recv_acyc_id=:RECV_ACYC_ID AND bill_id=TO_NUMBER(:BILL_ID) group by integrate_item order by integrate_item_code