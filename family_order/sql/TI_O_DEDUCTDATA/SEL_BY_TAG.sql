SELECT serial_number,TO_CHAR(charge_id) charge_id,TO_CHAR(user_id) user_id FROM ti_o_deductdata 
 WHERE trim(channel_id)=:CHANNEL_ID AND deduct_tag='0' AND deal_tag='0' AND ROWNUM<2