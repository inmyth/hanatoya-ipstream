v.100
- Make delete button to delete camExt
- "htttp" is not saved to db
- URL = protocol + host
- (bug) save creates a new camExt
- Progress bar
- revert niqdev/ipcam-view to 0.3.4
- added icons
- better layouts

v.101

- custom access endpoints
- export / import user settings
- added table custom

v.102
- removed unneeded libs

v.103
- added img cap for main fragment
- removed blob cap from db

v.104
- changed test from stream to img cap
- fixed bugs on edit form back (Event back listener activated in MainActivity instead of EditActivity)
- comestics fix on cgi rows

v.105
- removed debug line

v.106
- added  TestActivity to test the new stream

v.107
- added Evidence stream (Evidence stream needs cookie for auth.)
-- Modified Form to support Evidence. If Evidence is selected, some other fields are hidden.
-- Added repeating thread to call the image every 500ms.

v.108
- added node to db and UI for Evidence
- added switch layout between Evidence and other cams

v.109
- added landscape layout for stream
-- moved stream fragment to stream activity
- added jp strings
- changed title
- node now accepts text

v.110
- changed the stream activity mechanism. moved presenter to fragment#onCreate

TODOS:
- img cap
- bug on com.android.support.design:24.2.0+ (FAB wrong position), need to revert to 24.1.1
- build UI for Evidence stream
-- registration
-- stream
-- control

NOTE:
- when adding new cam
-- add string resource, add in initAPI in CamExt