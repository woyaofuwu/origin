UPDATE td_s_tag
   SET tag_char=:TAG_CHAR 
 WHERE tag_code=:TAG_CODE
   AND subsys_code=:SUBSYS_CODE
   AND use_tag=:USE_TAG