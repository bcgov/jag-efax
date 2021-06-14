This folder is a local maven repo for custom, proprietary jars not available online (https://repo1.maven.org/maven2/)
 
To install a custom jar to this local repo:

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file ^
    -Dfile=/path/to/jar/customjar.jar ^
    -DgroupId=ca.bc.gov.ag ^
	-DartifactId=customjar ^
    -Dversion=0.0.1 ^
	-Dpackaging=jar ^
    -DlocalRepositoryPath=/path/to/this/repo/nexus
