update tf_f_user set OPEN_DATE =  trunc ( OPEN_DATE )where  
user_id= (select user_id from TF_B_TRADE_NP where trade_id=:TRADE_ID)