# Your Shiny ✨ New FCS Endpoint ✨

## ❗ NOTE BEFORE USE

Please check the generated source files for any occurrence of `TODO`, `FIXME` and `NOTE` and adjust accordingly.

The generated endpoint sources should work out-of-the-box but do not really implement any translation layer for a search engine or perform an actual search. All search requests to CQL will just respond with two static results.

## Folder Structure

### `./` - Project root

* [`Dockerfile`](Dockerfile)  
  Multi-stage Maven build and slim Jetty runtime image.
* [`docker-compose.yml`](docker-compose.yml)
* [`pom.xml`](pom.xml)  
  Java dependencies for use with Maven.

### `src/main/java/` - Java Source Files

* [`${package}.${classnameConstants}`](src/main/java/${packageInPathFormat}/${classnameConstants}.java)  
  Constants for accessing FCS request parameters and output generation. Can be used to store own constants.
* [`${package}.${classnameEndpointSearchEngine}`](src/main/java/${packageInPathFormat}/${classnameEndpointSearchEngine}.java)  
  The glue between the FCS and our own search engine. It is the actual implementation that handles SRU/FCS explain and search requests. Here, we load and initialize our FCS endpoint.
  It will perform searches with our own search engine (here only with static results), and wrap results into the appropriate output (`${package}.${classnameSRUSearchResultSet}`). 
* [`${package}.${classnameSRUSearchResultSet}`](src/main/java/${packageInPathFormat}/${classnameSRUSearchResultSet}.java)  
  FCS Data View output generation. Writes minimal, basic HITS Data View. Here custom output can be generated from the result wrapper `${package}.searcher.MyResults`.
* [`${package}.searcher.MyResults`](src/main/java/${packageInPathFormat}/searcher/MyResults.java)  
  Lightweight wrapper around own results that allows access to results counts and result items per index.
* [`${package}.query.CQLtoMYQUERYConverter`](src/main/java/${packageInPathFormat}/query/CQLtoMYQUERYConverter.java)  
  Query converion from simple CQL to demo `MYQUERY` as example for own query adapters.

### `src/main/resources/` - Resources, General Configurations

Only the [`log4j2.xml`](src/main/resources/log4j2.xml) is important in case of changing logging settings.

### `src/main/webapp/WEB-INF/` - SRU/FCS Configurations

Here are the endpoint configuration:

* [`endpoint-description.xml`](src/main/webapp/WEB-INF/endpoint-description.xml)  
  FCS Endpoint Description, like resources, capabilities etc.
* [`jetty-env.xml`](src/main/webapp/WEB-INF/jetty-env.xml)  
  Jetty environment variable settings.
* [`sru-server-config.xml`](src/main/webapp/WEB-INF/sru-server-config.xml)  
  SRU Endpoint Settings.
* [`web.xml`](src/main/webapp/WEB-INF/web.xml)  
  Java Servlet configuration, SRU/FCS endpoint settings.

## 🔨 Build

Build [`fcs.war`](target/fcs.war) file for webapp deployment:

```bash
mvn [clean] package
```

## ⚙️ Configuration

Some endpoint/resource configurations are being set using environment variables. See [`jetty-env.xml`](src/main/webapp/WEB-INF/jetty-env.xml) for details. You can set default values there.
For production use, you can set values in the .env file that is then loaded with the `docker-compose.yml` configuration.

## 🏃 Run with Docker

The archetype includes both a [`Dockerfile`](Dockerfile) and a [`docker-compose.yml`](docker-compose.yml) configuration.
The `Dockerfile` can be used to build a simple Jetty image to run the FCS endpoint. It still needs to be configured with port-mappings, environment variables etc. The `docker-compose.yml` file bundles all those runtime configurations to allow easier deployment. You still need to create an `.env` file or set the environment variables if you use the generated code as is.

Using docker:

```bash
# build the image and label it "fcs-endpoint"
docker build -t fcs-endpoint .

# run the image in the foreground (to see logs and interact with it) with environment variables from .env file
docker run --rm -it --name fcs-endpoint -p 8081:8080 --env-file .env fcs-endpoint

# or run in background with automatic restart
docker run -d --restart=unless-stopped --name fcs-endpoint -p 8081:8080 --env-file .env fcs-endpoint
```

Using docker-compose:

```bash
# build
docker-compose build
# run
docker-compose up [-d]
```

## 👨‍🔬 (Test) Run

Uses Jetty 10. See [`pom.xml`](pom.xml) --> plugin `jetty-maven-plugin`.

```bash
mvn [package] jetty:run-war
```

NOTE: `jetty:run-war` uses built war file in [`target/`](target/) folder.

### Test Requests

The search request for _something_ in CQL/BASIC-Search:

```bash
curl '127.0.0.1:8080?operation=searchRetrieve&queryType=cql&query=something&x-indent-response=1'
# or port 8081 if run with docker
```

should respond with:

<details>
<summary>Response</summary>

```xml
<?xml version='1.0' encoding='utf-8'?>
<sruResponse:searchRetrieveResponse xmlns:sruResponse="http://docs.oasis-open.org/ns/search-ws/sruResponse">
 <sruResponse:version>2.0</sruResponse:version>
 <sruResponse:numberOfRecords>1</sruResponse:numberOfRecords>
 <sruResponse:records>
  <sruResponse:record>
   <sruResponse:recordSchema>http://clarin.eu/fcs/resource</sruResponse:recordSchema>
   <sruResponse:recordXMLEscaping>xml</sruResponse:recordXMLEscaping>
   <sruResponse:recordData>
    <fcs:Resource xmlns:fcs="http://clarin.eu/fcs/resource" pid="FIXME:DEFAULT_RESOURCE_PID">
     <fcs:ResourceFragment>
      <fcs:DataView type="application/x-clarin-fcs-hits+xml">
       <hits:Result xmlns:hits="http://clarin.eu/fcs/dataview/hits">
        <hits:Hit>abc</hits:Hit>
       </hits:Result>
      </fcs:DataView>
     </fcs:ResourceFragment>
    </fcs:Resource>
   </sruResponse:recordData>
   <sruResponse:recordPosition>1</sruResponse:recordPosition>
  </sruResponse:record>
  <sruResponse:record>
   <sruResponse:recordSchema>http://clarin.eu/fcs/resource</sruResponse:recordSchema>
   <sruResponse:recordXMLEscaping>xml</sruResponse:recordXMLEscaping>
   <sruResponse:recordData>
    <fcs:Resource xmlns:fcs="http://clarin.eu/fcs/resource" pid="FIXME:DEFAULT_RESOURCE_PID">
     <fcs:ResourceFragment>
      <fcs:DataView type="application/x-clarin-fcs-hits+xml">
       <hits:Result xmlns:hits="http://clarin.eu/fcs/dataview/hits">
        <hits:Hit>def</hits:Hit>
       </hits:Result>
      </fcs:DataView>
     </fcs:ResourceFragment>
    </fcs:Resource>
   </sruResponse:recordData>
   <sruResponse:recordPosition>2</sruResponse:recordPosition>
  </sruResponse:record>
 </sruResponse:records>
 <sruResponse:echoedSearchRetrieveRequest>
  <sruResponse:version>2.0</sruResponse:version>
  <sruResponse:query>something</sruResponse:query>
  <sruResponse:xQuery xmlns="http://docs.oasis-open.org/ns/search-ws/xcql">
   <searchClause>
    <index>cql.serverChoice</index>
    <relation>
     <value>=</value>
    </relation>
    <term>something</term>
   </searchClause>
  </sruResponse:xQuery>
  <sruResponse:startRecord>1</sruResponse:startRecord>
 </sruResponse:echoedSearchRetrieveRequest>
</sruResponse:searchRetrieveResponse>
```

</details>

### 🐞 Debug with VSCode

Add default debug setting `Attach by Process ID`, then start the jetty server with the following command, and start debugging in VSCode while it waits to attach.

```bash
# export configuration values, see section #Configuration
MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -agentlib:jdwp=transport=dt_socket,server=y,address=5005" mvn jetty:run-war
```
