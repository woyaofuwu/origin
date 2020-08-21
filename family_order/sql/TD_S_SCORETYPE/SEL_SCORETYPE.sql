--IS_CACHE=Y
select SCORE_TYPE_CODE, SCORE_TYPE_CODE||' | '||SCORE_TYPE_NAME SCORE_TYPE_NAME
   from TD_S_SCORETYPE
   where score_type_code in('0','1','2','3','4','5','6','7','8','9')
   order by score_type_code