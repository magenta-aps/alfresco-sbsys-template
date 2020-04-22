# Protokol for oprettelse af kladde

Dette dokument indeholder en beskrivelse af protokollen for kommunikationen mellem 
Alfresco og SBSIP ved oprettelse af nye kladder.

## Protokol

1.  SBSYS fortager et HTTP GET request til ALF for at rekvirere en liste af tilgængelige skabeloner. 
    En skabelon defineres som et Word-dokument eller et LibreOffice-dokument med et antal 
    pre-definerede felter, som kan udfyldes på et senere tidspunkt. Følgende kald foretages:
    ```
    GET /alfresco/s/template
    
    [
      {
          "filename": "navn-på-skabelon.docx",
          "id": "f3bb5d08-9fd1-46da-a94a-97f20f1ef208"
      },
      {
          "filename": "navn-på-skabelon2.docx",
          "id": "bb3b5d08-9fd1-46da-a94a-97f20f1ef999"
      },
       ...
    ]
    ```
2.  SBSYS laver et HTTP POST request til ALF indeholdende ID’et på den skabelon, som brugeren har valgt.
    ```
    POST /alfresco/s/template/merge
    
    {
        "id": <uuid på template jf. kaldet fra 1)>
        "kladde": {
            "SagID": "12344",
            "Navn": "testtitel",
            "SagsPart": {
                "PartId": 8,
                "PartType": "Person" 
            }
        },
        "urls": {
            "opretkladde": "https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde",
            "part": "https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/part/person?id=1",
            "sag": "https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/12344",
            "redirect": "https://<sbsip-server>/p-sag/v1/#/sager/12344/kladder/{kladdeId}"
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
3.  ALF laver kald til SBSYS for at hente metadata om den pågældende sag.
    ```
    GET https://<sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/12344
    
    {
      "SagsTitel": "MagentaTest",
      "Personer": [
        {
          "FoedeDato": "1900-04-01T00:00:00+02:00",
          "Initialer": "TTM",
          "Titel": "",
          "Uddannelse": "",
          "Stilling": "",
          "Ansaettelsessted": "",
          "Koen": "Mand",
          "CprNummer": "010490-9989",
          "Adresse": {
            "Id": 53,
            "Adresse1": "Testgrusgraven 3,3.tv",
            "PostNummer": 3400,
            "PostDistrikt": "Hiller\u00f8d",
            "ErBeskyttet": false,
            "WorkTelefonNummer": "",
            "PrivateEmailAdresse": "torre@hotmail.com",
            "PrivateMobilNummer": "",
            "PrivateTelefonNummer": "10100101010"
          },
          "CivilstandId": 5,
          "MorCPR": "010862-9996",
          "FarCPR": "010858-9995",
          "TilmeldtDigitalPost": true,
          "Uuid": "5b2b06ad-67f9-4ea6-b623-c5b48b4ffd01",
          "Id": 4,
          "Navn": "Torre Test Mosebryggersen"
        },
        {
          "FoedeDato": "1972-04-01T00:00:00+02:00",
          "Koen": "Mand",
          "CprNummer": "010772-9995",
          "Adresse": {
            "Id": 55,
            "Adresse1": "Testervej18",
            "PostNummer": 6200,
            "PostDistrikt": "Aabenraa",
            "ErBeskyttet": false,
            "WorkTelefonNummer": "",
            "PrivateEmailAdresse": "",
            "PrivateMobilNummer": "",
            "PrivateTelefonNummer": "456767"
          },
          "CivilstandId": 5,
          "MorCPR": "251248-9996",
          "FarCPR": "000000-0000",
          "TilmeldtDigitalPost": true,
          "Uuid": "f42261df-a6e2-40f0-9920-03c210b914a7",
          "Id": 6,
          "Navn": "Max Test Berggren"
        },
        {
          "Koen": "Kvinde",
          "CprNummer": "310154-1966",
          "Adresse": {
            "Id": 334,
            "Adresse1": "Testgrusgraven 3,3.th",
            "Adresse2": "Dyrup",
            "PostNummer": 5250,
            "PostDistrikt": "Odense SV",
            "ErBeskyttet": false
          },
          "CivilstandId": 4,
          "TilmeldtDigitalPost": false,
          "Uuid": "51e82b3b-207f-4f73-99b9-83111fbd3cd2",
          "Id": 46,
          "Navn": "Lonni Test Lauridsen"
        }
      ],
      "Firmaer": [],
      "PrimaryPart": {
        "PartId": 4,
        "PartType": "Person",
        "CPRnummer": "010490-9989",
        "Navn": "Torre Test Mosebryggersen"
      },
      "Id": 979,
      "SagIdentity": "a2252d45-89eb-451e-9079-c7c63203d7af",
      "Opstaaet": "2019-11-18T09:24:48.507+01:00",
      "Nummer": "32.03.12-G01-36-19",
      "Fagomraade": {
        "Uuid": "b168ec95-d6e4-480a-8c82-30c1cab68209",
        "Id": 1,
        "Navn": "Standard"
      },
      "BevaringId": 20,
      "SenesteStatusAendringKommentar": "Sagsstatus \u00e6ndret til Opst\u00e5et",
      "SenesteStatusAendring": "2019-11-18T09:24:48.397+01:00",
      "Oprettet": "2019-11-18T09:24:48.507+01:00",
      "SenestAendret": "2020-01-23T13:44:43.197+01:00",
      "ErBeskyttet": true,
      "ErBesluttet": false,
      "BeslutningHarDeadline": false,
      "SagsNummerId": 979,
      "Ansaettelsessted": {
        "Uuid": "c2822c97-8e48-4cc7-8e0f-474318c8192a",
        "Id": 5,
        "Navn": "L\u00f8n"
      },
      "Behandler": {
        "LogonId": "testbruger01",
        "Uuid": "c713c192-939f-463a-8f71-dc92d699e64e",
        "Id": 2,
        "Navn": "Test Bruger 01"
      },
      "SagsStatus": {
        "Id": 6,
        "Navn": "Opst\u00e5et",
        "Orden": 1,
        "SagsTilstand": "Aktiv",
        "RequireComments": false,
        "SagsStatusKommentar": "Sagsstatus \u00e6ndret til Opst\u00e5et"
      },
      "ArkivAfklaringStatusId": 1,
      "OprettetAf": {
        "LogonId": "testbruger01",
        "Uuid": "c713c192-939f-463a-8f71-dc92d699e64e",
        "Id": 2,
        "Navn": "Test Bruger 01"
      },
      "SenestAendretAf": {
        "LogonId": "testbruger01",
        "Uuid": "c713c192-939f-463a-8f71-dc92d699e64e",
        "Id": 2,
        "Navn": "Test Bruger 01"
      },
      "StyringsreolHylde": {
        "Id": 3,
        "Navn": "Ny ans\u00f8gning"
      },
      "SecuritySetId": 76883,
      "SagSkabelon": {
        "Uuid": "ecc02786-56e2-4b56-ae11-ff6e2dce0626",
        "Id": 11,
        "Navn": "Ans\u00f8gningssag"
      },
      "SagsNummer": {
        "EmneplanNummer": {
          "Id": 13877,
          "EmneplanID": 90,
          "Nummer": "32.03.12",
          "Navn": "Personligt till\u00e6g og helbredstill\u00e6g",
          "Beskrivelse": "\r\n",
          "Niveau": 2,
          "Oprettet": "2009-05-01T00:00:00+02:00",
          "ErUdgaaet": false,
          "AfloserNumre": []
        },
        "Facet": {
          "ID": 770,
          "FacetTypeID": 1337,
          "Nummer": "G01",
          "Navn": "Generelle sager",
          "Beskrivelse": "G01 anvendes til almindeligt forekommende sager, fx:</p>\n<p> \n - afg\u00f8relser efter faste regler, principper eller instrukser - \n - beslutninger ud fra faste regler, principper eller instrukser - \n - generelle henvendelser - \n - generel vejledning - \n - generel r\u00e5dgivning - \n </p>\n<p>G01 bruges til at skelne generelle sager fra de specielle, dvs. sager hvor facetten bruges til s\u00f8gning efter bestemte sagstyper, ledelsesinformation eller s\u00f8gning p\u00e5 tv\u00e6rs af emner.\r\n",
          "BevaringID": 20,
          "Oprettet": "1988-01-01T00:00:00+01:00",
          "Rettet": "2011-11-01T00:00:00+01:00"
        },
        "Aarstal": 2019,
        "SekvensNummer": 36
      }
    }
    ```
4.  ALF fletter sagens metadata ind i den ovenfor valgte skabelon.
5.  ALF returnerer en HTTP status 200 med JSON responset
    ```
    {
      "preUploadId": "workspace://SpacesStore/2acc91c4-ef7b-4e20-8778-22951555031a",
      "preUploadFilename": "74b37e74-f549-496b-98aa-b3d6522c2bac",
      "url": "https://<alfresco-server>/share/page/site/swsdp/onlyoffice-edit?nodeRef=workspace://SpacesStore/2acc91c4-ef7b-4e20-8778-22951555031a"
    }
    ```
6.  URL’en fra 5) åbnes i en iframe i SB-NemSag, og brugeren redigerer den med sagsdata flettede 
    kladde færdig. Når redigeringen er færdig, trykker brugeren på en "Upload til SBSYS"-knap, som 
    findes på samme side som der, hvor iframen vises. Trykket på knappen sender følgende request til ALF:
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
7.  Der skal eksperimenteres med denne del af protokollen, idet det skal undersøges, om vi kan omgå 
    den asynkrone kommunikation, der foregår mellem Alfresco og OnlyOffice, men planen er, at ALF 
    foretager et multipart/form-data request til SBSYS serveren, hvor dokumentet uploades:
    ```
    POST <sbsip-server>/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde    
    ```
    Formen skal indeholde følgende felter:
    ```
    json: {"SagID": …, "Navn":..., "Emne":..., "Beskrivelse":.., "SagsPart":???}
    files: <selve filen som binære data>
    ```
8.  Kladden slettes i ALF.
9.  Efter upload - og som svar på 6) - sender ALF følgende respons til SB-NemSag:
    ```
    {
      "msg": "success"
    }
    ```
    Såfremt brugeren ikke har gemt kladden i OnlyOffice først (og dermed ikke opdateret 
    det pågældende dokument i Alfresco fra version 1.0 til 1.1), så svarer ALF i stedet 
    med en HTTP statuskode 403 og flg. JSON:
    ```
    {
      "msg": "Document not yet saved",
      "uploadStatus": 1
    }    
    ```
