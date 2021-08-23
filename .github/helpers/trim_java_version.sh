  
#!/bin/bash

# returns the java project version present in the POM without '-SNAPSHOT'

TRIMMED_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }' | sed -r 's/-SNAPSHOT//g')

echo $TRIMMED_VERSION
