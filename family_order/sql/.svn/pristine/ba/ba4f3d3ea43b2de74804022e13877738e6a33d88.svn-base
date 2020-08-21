SELECT discnt_code,inter_fee_idx,out_fee_idx,outgrp_fee_idx,farin_fee_idx,ingrp_fee_idx,normal_fee_idx,smp_fee_type,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_m_smpfee
 WHERE discnt_code = (SELECT discnt_code FROM tf_b_trade_discnt
                       WHERE trade_id = TO_NUMBER(:TRADE_ID)
                         AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)))