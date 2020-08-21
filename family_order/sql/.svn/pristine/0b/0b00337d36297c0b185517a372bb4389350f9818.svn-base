Select * From Tf_f_Bank_Recvfee 
	Where substr(trade_time,0,8) <=:ENDDATE  and   substr(trade_time,0,8) >=:STARTDATE
   And cancel_tag = 1
order by trade_time