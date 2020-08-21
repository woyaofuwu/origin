SELECT a.discnt_code,inter_fee_idx,out_fee_idx,outgrp_fee_idx,farin_fee_idx,ingrp_fee_idx,normal_fee_idx,smp_fee_type,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_m_smpfee a,tf_b_trade_vpmnclosegrp b
 WHERE a.discnt_code=b.discnt_code AND b.modify_tag='0' AND b.trade_id=to_number(:TRADE_ID) AND b.accept_month=to_number(substr(:TRADE_ID,5,2))