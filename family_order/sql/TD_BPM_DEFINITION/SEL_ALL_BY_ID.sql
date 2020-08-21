--IS_CACHE=Y
SELECT to_char(id) id,name,displayname,description,to_char(definition) definition,version_,to_char(startstate) startstate 
  FROM td_bpm_definition
 WHERE name=:NAME