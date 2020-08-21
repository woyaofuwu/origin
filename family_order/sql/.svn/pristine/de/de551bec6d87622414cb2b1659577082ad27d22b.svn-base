select imsi from 
(select imsi from tf_r_simcard_idle where sim_card_no = :SIM_CARD_NO
union 
select imsi from tf_r_simcard_use where sim_card_no = :SIM_CARD_NO)
where rownum < 2