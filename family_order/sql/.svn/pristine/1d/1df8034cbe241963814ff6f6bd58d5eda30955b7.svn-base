SELECT fee_mode,'营业费用' fee_mode_name,fee_type_code,b.feeitem_name fee_type,'0' adjustfee,TO_CHAR(a.fee/100) fee,'1' MUST_TAG
FROM tf_b_tradefee_sub a,td_b_feeitem b
WHERE a.fee_type_code=b.feeitem_code
AND trade_id =:TRADE_ID   
AND fee_mode='0'