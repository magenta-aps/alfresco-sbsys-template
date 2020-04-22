# Protokol for visning

Dette dokument indeholder en beskrivelse af protokollen for kommunikationen mellem 
Alfresco og SBSIP ved visning af dokumenter.

## Protokol

1.  Brugeren trykker på "Vis kladde" i SB-NemSag, hvorefter der bliver sendt et POST request 
    til Alfresco med JSON indeholdende ID’et på den pågældende kladde:
    
    ```
    POST /alfresco/s/template/draft/preview
    
    {
      "urls": {
        "fildownload": "https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde/1234/readonly/<version>"
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
2.  Alfresco henter den pågældende kladde fra SBSYS og gemmer den i en mappe ved at lave et GET request til (fx)
    ```
    GET https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde/1234/readonly/<version>
    ```
3.  Derefter svarer Alfresco tilbage på requestet, som SB-NemSag foretog under punkt 1) med (e.g.) følgende JSON:
    ```
    {
      "preUploadId":"workspace://SpacesStore/5c0a33d6-54f0-494c-baa1-cacd568553be",
      "url": "https://<alfresco-server>/share/page/iframe-preview?nodeRef=workspace://SpacesStore/5c0a33d6-54f0-494c-baa1-cacd568553be"
    }
    ```
4.  SB-NemSag åbner en ny iframe/fane i browseren med ovenstående URL, hvorefter indholdet af kladden vises.
5.  Brugeren lukker iframen/visningsfanen og SB-NemSag sender efterfølgende et HTTP DELETE request til følgende URL 
    for at slette det pågældende dokument i Alfresco:
    ```
    DELETE /alfresco/s/template/draft/delete/<UUID>
    ```
    hvor <UUID> er UUID-delen af "preUploadId" fra JSON jf. punkt 3). Dvs. UUID = 5c0a33d6-54f0-494c-baa1-cacd568553be 
    i dette eksempel.
