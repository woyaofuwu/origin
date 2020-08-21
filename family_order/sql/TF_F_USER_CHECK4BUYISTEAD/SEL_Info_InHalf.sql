select a.* from TF_F_USER_Check4BuyIstead a 
where a.serial_number = :SERIAL_NUMBER and a.check_state ='1' and a.in_date > sysdate - 1/48
order by a.in_date desc