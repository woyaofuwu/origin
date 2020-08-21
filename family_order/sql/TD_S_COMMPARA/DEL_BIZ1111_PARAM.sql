UPDATE td_s_commpara
   SET end_date = TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 WHERE rowid = :ROWID