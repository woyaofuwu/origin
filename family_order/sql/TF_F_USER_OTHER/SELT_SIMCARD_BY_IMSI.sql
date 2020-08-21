select sim_card_no from 
(select sim_card_no from tf_r_simcard_idle where imsi = :IMSI
union 
select sim_card_no from tf_r_simcard_use where imsi = :IMSI)
where rownum < 2