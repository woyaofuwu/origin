--IS_CACHE=Y
SELECT to_char(id) id,class class1,name,description,to_char(definition) definition,to_char(swimlane) swimlane,assignmentrequired,stateauthentication,stable,to_char(delegation) delegation,to_char(correspondingjoin) correspondingjoin,to_char(correspondingfork) correspondingfork,endcode 
  FROM td_bpm_node
 WHERE id=TO_NUMBER(:ID)