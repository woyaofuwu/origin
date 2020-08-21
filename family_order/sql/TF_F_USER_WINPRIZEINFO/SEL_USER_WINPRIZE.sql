SELECT serial_number,info, to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
	to_char( end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
	to_char(win_prize_date,'yyyy-mm-dd hh24:mi:ss') win_prize_date 
  FROM TF_F_USER_WINPRIZEINFO
 WHERE SERIAL_NUMBER = :SERIAL_NUMBER