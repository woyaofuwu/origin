Update /*+index (a)*/ Tf_f_User_Discnt a
Set end_date=(
       Select end_date  From tf_b_trade_discnt b
       Where b.trade_id=:TRADE_ID
       And b.Id=a.user_id
       And b.discnt_code=a.discnt_code),
       update_time=sysdate
Where Exists(Select 1 From tf_b_trade_discnt b
       Where b.trade_id=:TRADE_ID
       And b.Id=a.user_id
       And b.discnt_code=a.discnt_code)
And a.end_date>Sysdate