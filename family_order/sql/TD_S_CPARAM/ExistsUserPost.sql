SELECT COUNT(*) recordcount
  FROM TF_F_POSTINFO
 WHERE id=TO_NUMBER(:USER_ID)
   AND post_content like '%'||:POST_CONTENT||'%'
   AND (post_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
   AND id_type='1'