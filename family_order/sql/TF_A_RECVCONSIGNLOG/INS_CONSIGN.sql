INSERT INTO tf_a_recvconsignlog(consign_id,vip_id,acyc_id,bcyc_id,acct_id,aspay_fee,act_tag,charge_id,trade_eparchy_code,trade_city_code,trade_depart_id,trade_staff_id,in_date,deal_tag,recv_acyc_id)
 VALUES(TO_NUMBER(:CONSIGN_ID),TO_NUMBER(:VIP_ID),:ACYC_ID,:BCYC_ID,TO_NUMBER(:ACCT_ID),TO_NUMBER(:ASPAY_FEE),:ACT_TAG,TO_NUMBER(:CHARGE_ID),:TRADE_EPARCHY_CODE,:TRADE_CITY_CODE,:TRADE_DEPART_ID,:TRADE_STAFF_ID,sysdate,:DEAL_TAG,:RECV_ACYC_ID)