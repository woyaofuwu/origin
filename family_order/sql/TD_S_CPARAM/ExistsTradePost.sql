SELECT COUNT(*) recordcount
  FROM TF_B_TRADE_POST
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND post_content like '%'||:POST_CONTENT||'%'
   AND (post_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
   AND id_type='1'