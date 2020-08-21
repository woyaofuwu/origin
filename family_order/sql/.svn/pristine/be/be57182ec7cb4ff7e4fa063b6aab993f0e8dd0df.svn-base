Update /*+index (a PK_TF_F_RELATION_UU)*/ tf_f_relation_uu a
Set end_date=(
       Select end_date  From tf_b_trade_relation b
       Where b.trade_id=:TRADE_ID
       And b.id_a=a.user_id_a
       And b.id_b=a.user_id_b )
Where Exists(Select 1 From tf_b_trade_relation b
       Where b.trade_id=:TRADE_ID
       And b.id_a=a.user_id_a
       And b.id_b=a.user_id_b )
And a.end_date>Sysdate