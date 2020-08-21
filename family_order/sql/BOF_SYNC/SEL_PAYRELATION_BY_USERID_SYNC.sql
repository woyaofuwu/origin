select b.Inst_id,
       b.User_Id,
       b.Payitem_Code,
       b.Acct_Priority,
       nvl(b.Addup_Method,0),
       b.Addup_Months,
       b.Limit_Type,
       b.Limit,
       b.Complement_Tag,
       b.Acct_Id,
       b.Bind_Type,
       b.User_Priority,
       b.Default_Tag,
       b.Act_Tag,
       b.Start_Cycle_Id,
       b.End_Cycle_Id,
       TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')
  from TF_A_PAYRELATION b
 WHERE b.USER_ID = :USER_ID
 and END_CYCLE_ID > START_CYCLE_ID
 and END_CYCLE_ID >TO_CHAR(SYSDATE, 'YYYYMMDD')