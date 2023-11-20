# CLARIN SRU/FCS Endpoint Archetype

The easiest way to use this archetype is by cloning this git repository, installing the archetype in your local maven repository and then generating your project. But you can also configure your maven to use custom remote repositories if you need to generate projects in the future again.

## (a) Install into Local Maven Repository

```bash
# optionally run before
#git clone https://github.com/clarin-eric/fcs-endpoint-archetype.git
#cd fcs-endpoint-archetype

mvn clean install "archetype:update-local-catalog"
```

## (b) Using the remote CLARIN Maven Repository/Nexus

With a custom repository, you need to update your local maven settings (can be found at `~/.m2/settings.xml`) to include the CLARIN Nexus for archetype discovery and download.

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  ...
  <profiles>
    <profile>
      ...
      <repositories>
        <repository>
          <id>CLARIN-Snapshot</id>
          <url>https://nexus.clarin.eu/content/repositories/clarin-snapshot</url>
        </repository>
        <repository>
          <id>CLARIN</id>
          <url>https://nexus.clarin.eu/content/repositories/Clarin</url>
        </repository>
      </repositories>
      ...
    </profile>
  </profiles>
  ...
</settings>
```

See:
* https://maven.apache.org/settings.html#repositories
* https://maven.apache.org/archetype/maven-archetype-plugin/generate-mojo.html#archetypeCatalog

## Create a new FCS Endpoint Project

Now that your Maven can find the archetype definition, you can start the project generation:

```bash
mvn archetype:generate \
    -DarchetypeGroupId=eu.clarin.sru.fcs \
    -DarchetypeArtifactId=fcs-endpoint-archetype \
    -DarchetypeVersion=1.6.0 \
    -DgroupId=[ id.group.fcs ] \
    -DartifactId=[ my-cool-endpoint ] \
    -Dversion=[ 1.0-SNAPSHOT ] \
    -DinstitutionName=[ My Institution ]
```

The values for `artifactIdCamelCase` and `package` are used internally and should be kept as is. The Parameter `classnamePrefix` will be used as prefix for the generated class names: `classnameEndpointSearchEngine`, `classnameSRUSearchResultSet` and `classnameConstants`.

Note that depending on the [`<version>` in `pom.xml`](pom.xml) and how you installed the Maven repository you should use `-DarchetypeVersion=1.6.0` or `-DarchetypeVersion=1.6.0-SNAPSHOT`!

---

## Update Archetype and Test

Install into [local repository](#install-into-local-maven-repository), and then test:

```bash
mvn archetype:generate \
    -DarchetypeGroupId=eu.clarin.sru.fcs \
    -DarchetypeArtifactId=fcs-endpoint-archetype \
    -DarchetypeVersion=1.6.0-SNAPSHOT \
    -DarchetypeCatalog=local \
    ...
```

### Install to Remote Repository (_only CLARIN developers_)

Check that the [`pom.xml`](pom.xml) contains the `<distributionManagement>` section, and that the matching repository id is configured in the global `~/.m2/settings.xml` file as `<servers><server>`. Then upload your artifact by running:

```bash
mvn deploy
```
