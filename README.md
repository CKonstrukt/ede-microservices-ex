# Chomp! - Enterprise Development Experience exercise
[YouTube video](https://youtu.be/xda1ZilwEWY)\
[Chomp! (Frontend, Vercel)](https://ede-microservices-ex.vercel.app/)\
[API Gateway (Okteto Cloud)](https://api-gateway-ckonstrukt.cloud.okteto.net)

## Thema
De applicatie draait rond het bijhouden van recepten. Hiervoor zijn de databases user, recipe en ingredient nodig. Het spreekt wel voor zich wat hierin wordt opgeslagen.
Ik heb voor elke van deze een microservice draaien. Jammer dat ik het zo gedaan heb omdat ik bij recipe een `userId` en `ingredientId` moest bijhouden wat voor inconsistencies en zware dependencies tussen services
zou kunnen zorgen. Na wat research blijkt dit niet echt het doel ervan te zijn.

## Microservices
- **user-service**: De user-service is verantwoordelijk voor de users. Via de API gateway kan er een request gemaakt worden om een user aan te maken. Deze moet geen authenticated request zijn. Er zijn ook endpoints
voor het opvragen van een user via zijn `id` of `email`. Deze worden gebruikt door de recipe-service. Ten slotte is er nog een delete endpoint voor een user te deleten, dit was nodig voor testing purposes.
- **ingredient-service**: De ingredient-service heeft volledige CRUD functionaliteit. Enkel de `/recipe` endpoint is toegangbaar via de gateway omdat een user niets anders moet kunnen doen.
- **recipe-service**: Dit is de bulk van de applicatie. Hier is ook weer volledige CRUD functionaliteit. Er is ook nog een ander endpoint `/recipe/me` waarmee de gebruiker zijn eigen recipes zal terug krijgen.
In een entry van de recipe table wordt een `userId` bijgehouden, deze is weldegelijk de `id` van de user. Om hieraan te kunnen (herriner dat user een andere microservice is) moet er een request gemaakt worden
naar die service, maar daarvoor is natuurlijk een identifier nodig om te weten voor welke user er gezocht moet worden. Als een user een request maakt naar de gateway voor `recipe/me` moet deze authenticated zijn.
Van een authenticated request kan er de email uit de `Authorization` header gehaald worden. Dit gebeurt adhv een pre-filter. Na het extracten wordt het request geforward met een nieuwe header `X-User-Email` die de
email van de user bevat. Dan wordt er een request gemaakt naar `/api/user/email/{email}` om de user op te halen en de `id` ervan op te slaan. De email wordt niet opgeslagen omdat dit potentieel zou kunnen veranderen.

## Schema
Hieronder het schema van de applicatie's backend. Zoals te zien is er maar 1 endpoint niet protected. Deze is de `POST /user` om een user te kunnen aanmaken. Er wordt gebruik gemaakt van 2 MongoDB en 1 Postgres databases.

![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/3b92b8d4-cc19-4856-a462-a2a0fac12ac1)

## Testing van endpoints
Sommige endpoints worden lokaal getest omdat deze niet beschikbaar zijn via Okteto Cloud (zie chapter over Nodeport).

### User
#### POST => /user
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/55a1a545-bb0f-481e-953f-c923de3ef674)

#### GET => /user
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/4293aed4-542f-46fd-96e4-1652fd529d0b)

#### GET => /user/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/ef31db8e-9411-44e5-b489-e4eda585e509)

#### GET => /user/email/{email}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/179efe43-39a6-4834-a03d-06f30b5f7b77)

#### DELETE => /user/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/8c0359f4-c6ca-4398-98f4-3dca3de57349)

### Ingredient
#### POST => /ingredient
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/6c11d2dd-6e57-4a1b-b26f-9a49afcebe99)

#### GET => /ingredient
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/fae1c888-524a-4ae8-a9d8-b615e1592089)

#### GET => /ingredient/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/43347a79-ab1d-4399-b3a4-44ce8e15d0ca)

#### PUT => /ingredient/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/9ac0b503-320c-4d1c-a4ca-69ba114c6105)

#### DELETE => /ingredient/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/dedcedd1-d507-48e9-81ef-55537590efd0)

### Recipe
#### POST => /recipe
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/a158e233-61f8-45db-acc7-89c8c430e451)

#### GET => /recipe/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/01aeeb84-bd05-4397-a0a1-78b60f9f5511)

#### GET => /recipe/me
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/f6e0d1d9-657e-4268-bf3d-7386445363bf)

#### PUT => /recipe/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/0bb32d00-2e5c-4593-b894-ac643a4d4fed)

#### DELETE => /recipe/{id}
![image](https://github.com/CKonstrukt/ede-microservices-ex/assets/91123727/21081f52-c55c-4da0-86ce-8480199edf9b)

## Uitbreidingen
### 2.1 Maak een front-end voor je applicatie die niet gehost is op Okteto. (+15%)
Ik heb ervoor gekozen een front-end te maken in NextJS 14. Dit omdat ik dit nodig zal hebben voor project 4.0. NextJS 14 ondersteund SSR (Server Side Rendering) maar met het gebruik van mijn keuze voor authentication module
,[authjs](https://authjs.dev/), bleken de voordelen hiervan al snel in het water te liggen. Ik had moeite om aan de bearer token te geraken server side, wat SSR voor de meeste components onmogelijk maakte. Uitendelijk
is het nog wel goed gekomen. Het eindresultaat had natuurlijk beter gekunt door recipes af te schermen die niet tot de ingelogde user behoren en validation beter te handelen. Ook de backend had beter gekunt, momenteel
kan iedereen iedereen's recipes aanpassen. De front-end is gehost op Vercel.

### 2.2 Zet de deployment docker-compose.yml om naar Kubernetes Manifest .yml-files (+5%)
Ik heb de docker-compose omgezet naar Kubernetes manifest files adhv [Kompose](https://kompose.io/), een tool van Kubernetes dat dit automatisch doet. Vervolgens zat ik even vast met hoe ik dit op Okteto moest krijgen.
Na een tijdje vond ik een [resource van Okteto](https://www.okteto.com/docs/guides/tutorials/getting-started-with-okteto/#step-3-create-kubernetes-manifests-to-deploy-the-hello-world-application-on-okteto) die dit uitlegde.
Het bleek dat de manifests gewoon in een `k8s` folder moesten zitten. Ik had ook nog een probleem met het deployment van de `postgres-service` maar hier ontbrak gewoon een environment variable 
([github issue](https://github.com/docker-library/postgres/issues/263)). De `docker-compose` staat momenteel onder een andere naam (`compose-for-good-measure.yml`) zodat Okteto deze niet neemt ipv de Kubernetes manifests.
Deze blijft erin staan omdat dit makkelijker is te bekijken dan alle k8s manifests.

### 2.2.2 Gebruik ClusterIP & Nodeport op een logische manier (+5%)
Na het deployment van Kubernetes op Okteto merkte ik dat de api-gateway niet meer exposed was voor external traffic. Na nog eens de project requirements te bekijken zag ik Nodeport staan en dit deet me wel een belletje
rinkelen. Na een klein beetje research wist ik hoe ik de api-gateway service manifest naar een Nodeport service moest omzetten. Momenteel is op Okteto enkel de gateway toegangbaar. Ik heb niets met ClusterIP gedaan.
