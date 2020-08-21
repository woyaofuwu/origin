SELECT COUNT(1) recordcount
FROM
(select moffice_id from tf_r_mphonecode_use where serial_number=:SERIAL_NUMBER)a,
(select moffice_id from tf_r_simcard_idle where sim_card_no=:SIM_CARD_NO)b
 where substr(a.moffice_id,1,2)=substr(b.moffice_id,1,2)