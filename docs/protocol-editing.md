# Protokol for redigering

Dette dokument indeholder en beskrivelse af protokollen for kommunikationen mellem 
Alfresco og SBSIP ved redigering af dokumenter.

## Protokol

1.  Brugeren trykker på "Redigér kladde" i SB-NemSag, hvorefter der bliver sendt et POST request til Alfresco med JSON 
    indeholdende ID’et på den pågældende kladde samt ID’et på sagen:
    ```
    POST /alfresco/s/template/draft/edit
    
    {
      "urls": {
        "redirect": "<url til redirect>",
        "filcheckud": "<url til checkud>",
        "filcheckind": "<url til checkind>",
        "fortrydcheckud": "<url til fortryd checkud>",
        "kladdedata": "<url til kladdedata>",
        "part": "",
        "sag": "<url til sag>",
        "sagsfelter": "<url til sagsfeltindhold>"
      }, 
      "token": {
            "refreshToken": "eyJhbGciOiJSUzI1NiIs...nXM4Yd5hg",
            "token": "eyJhbGciOiJSUzI1NiIsInR5.....EnwYwsJnoB5tz18neg",
            "exp": 1558515632,
            "refreshurl": "https://<auth-server>/auth/realms/sbsip/protocol/openid-connect/token",
            "refreshuser": "<username>",
            "refreshpwd": "<password>"
        }
    }
    ```
2.  Alfresco henter den pågældende kladde fra SBSYS og gemmer den i en mappe ved at lave et GET 
    request til filcheckud URL’en, som blev leveret i ovenstående JSON.
3.  Derefter svarer Alfresco tilbage på requestet, som SB-NemSag foretog under punkt 1) med 
    (e.g.) følgende JSON:
    ```
    {
     "preUploadId": "workspace://SpacesStore/2acc91c4-ef7b-4e20-8778-22951555031a",
      "preUploadFilename": "74b37e74-f549-496b-98aa-b3d6522c2bac",
      "url": "https://<alfresco-server>/share/page/site/swsdp/onlyoffice-edit?nodeRef=workspace://SpacesStore/2acc91c4-ef7b-4e20-8778-22951555031a"
    }
    ```
    (eller en tilsvarende URL for LibreOffice Online)
4.  SB-NemSag åbner en iframe i browseren med ovenstående URL, hvorefter indholdet af kladden vises og 
    kan redigeres.
5.  Når brugeren er færdig med at redigere gøres følgende trykker han/hun "Gem" (i enten OnlyOffice 
    eller LOOL) og trykker efterfølgende op en "Upload til SBSYS"-knap.
6.  SB-NemSag sender følgende request til Alfresco:
    ```
    POST /alfresco/s/template/upload
    
    {
      "preUploadId": "workspace://SpacesStore/2acc91c4-ef7b-4e20-8778-22951555031a",
      "token": {
            "refreshToken": "eyJhbGciOiJSUzI1NiIs...nXM4Yd5hg",
            "token": "eyJhbGciOiJSUzI1NiIsInR5.....EnwYwsJnoB5tz18neg",
            "exp": 1558515632,
            "refreshurl": "https://<auth-server>/auth/realms/sbsip/protocol/openid-connect/token",
            "refreshuser": "<username>",
            "refreshpwd": "<password>"
        }
    }
    ```
7.  Der laves fra Alfresco et POST (multipart/form-data) request til filcheckind URL’en, som blev 
    leveret i ovenstående JSON. Requestet skal indeholde følgende form-felter:
    ```
    json: {}
    files: (binary)
    ```
8.  Efter upload - og som svar på 6) - sender ALF følgende respons til SB-NemSag:
    ```
    {
      "msg": "success"
    }
    ```
    Såfremt bruger ikke har gemt kladden i OnlyOffice først (og dermed ikke opdateret det pågældende 
    dokument i Alfresco fra version 1.0 til 1.1), så svarer ALF i stedet med en HTTP statuskode 403 
    og flg. JSON:
    ```
    {
      "msg": "Document not yet saved",
      "uploadStatus": 1
    }    
    ```
