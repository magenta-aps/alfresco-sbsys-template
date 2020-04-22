# alfresco-sbsys-template

Project for handeling document templates to be used within SBSIP/SBSYS. A number of document templates 
are stored in Alfresco and SBSYS can (via REST) access a list of these templates. The SBSYS user 
can select a template and send this back to Alfresco along with an SBSYS case id. Alfresco then 
retreives case metadata from SBSYS and merges these data into the template. Finally, the 
filled out template is send back to SBSYS.

_NOTE: This is a proof-of-concept/pilot project, i.e. no test are written and proper error 
handling is (with respect to the HTTP communication between Alfresco and SBSYS) is missing 
to some extend in the code base_.

## Infrastructure

The solution contains the following components:

1.  An SBSYS backend (maintained by Miracle).
1.  An SBSIP application working as a frontend (called SB-NemSag) and also working as a proxy 
    layer to the SBSYS backend (maintained by Convergens).
1.  An Alfresco application with all the usual supporting components such as a database and SolR 
    (maintained by Magenta).
1.  A Spring Boot HTTP/2 "requester" - see details [elsewhere](spring-boot/README.md) in these 
    docs (maintained by Magenta).
1.  An OnlyOffice server for online editing of DOCX files (maintained by Magenta).
1.  A LibreOffice Online server for online editing og ODT files (maintained by Magenta).

From an Alfresco view point this means that all communication to SBSYS happens via the SBSIP 
proxy.

### More documentation

More documentation about the Alfresco application (including info about development) and 
the Spring Boot application can be found here:
*  Documentation about the [Alfresco application](alfresco/README.md).
*  Documentation about the [Spring Boot application](spring-boot/README.md).

## Workflows 

There are three overall workflows as described below.

### Creating a new draft 
The overall workflow for creating a draft is as follows:

1.  A number of draft templates (DOCX or ODT) are stored in Alfresco.
1.  SBSIP can (via REST) access a list of these templates and display the list to the user.
1.  The SBSYS user can select a template and send this back to Alfresco along with an SBSYS case id for 
    the current case that (s)he is working on.
1.  Alfresco then retreives case metadata from SBSYS and merges these data into the template.
1.  Finally, the metadata enriched template is shown to the user in an online editor 
    (OnlyOffice or LibreOffice Online) where the user can finish editing of the document.
1.  The user then saves the edited draft by clicking a "Save" button.
1.  The draft is then uploaded from Alfresco to SBSYS.
1.  The draft is deleted from Alfresco. 

See further details about the protocol [here](docs/protocol-creating.md) (in danish).

### Viewing an existing draft

The following happens when a user chooses to view an existing draft:

1.  The user clicks the "View" button in SB-NemSag.
1.  SBSIP sends information about which draft to view to Alfresco.
1.  Alfresco downloads the draft from SBSYS.
1.  Alfresco sends an URL for viewing back to SB-NemSag.
1.  The user views the draft and when done, the window (iframe) for viewing is closed 
    and a request for deleting the draft is sent to Alfresco.
1.  Alfresco deletes the draft.

See further details about the protocol [here](docs/protocol-viewing.md) (in danish).

### Editing an existing draft

The following happens when a user chooses to edit an existing draft:

1.  The user clicks the "Edit" button in SB-NemSag.
1.  SBSIP sends information about which draft to edit to Alfresco.
1.  Alfresco downloads the draft from SBSYS.
1.  Alfresco sends an URL for online editing back to SB-NemSag.
1.  The user edits and saves the draft and when done, the window (iframe) for editing is closed 
    and a request for uploading the draft to SBSYS is sent to Alfresco.
1.  Alfresco uploades the document to SBSYS.
1.  Alfresco deletes the draft.

See further details about the protocol [here](docs/protocol-editing.md) (in danish).
