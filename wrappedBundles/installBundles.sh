# mule-core wrapped bundles
mvn install:install-file -Dfile=uuid-3.4.0-osgi.jar -DgroupId=com.github.stephenc.eaio-uuid -DartifactId=uuid -Dversion=3.4.0-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-beanutils-1.8.0-osgi.jar -DgroupId=commons-beanutils -DartifactId=commons-beanutils -Dversion=1.8.0-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-collections-3.2.1-osgi.jar -DgroupId=commons-collections -DartifactId=commons-collections -Dversion=3.2.1-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-cli-1.2-osgi.jar -DgroupId=commons-cli -DartifactId=commons-cli -Dversion=1.2-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-io-2.4-osgi.jar -DgroupId=commons-io -DartifactId=commons-io -Dversion=2.4-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-lang-2.4-osgi.jar -DgroupId=commons-lang -DartifactId=commons-lang -Dversion=2.4-osgi -Dpackaging=jar
mvn install:install-file -Dfile=commons-pool-1.6-osgi.jar -DgroupId=commons-pool -DartifactId=commons-pool -Dversion=1.6-osgi -Dpackaging=jar
mvn install:install-file -Dfile=jgrapht-jdk1.5-0.7.3-osgi.jar -DgroupId=org.jgrapht -DartifactId=jgrapht-jdk1.5 -Dversion=0.7.3-osgi -Dpackaging=jar
mvn install:install-file -Dfile=mule-mvel2-2.1.9-MULE-005-osgi.jar -DgroupId=org.mule.mvel -DartifactId=mule-mvel2 -Dversion=2.1.9-MULE-005-osgi -Dpackaging=jar
mvn install:install-file -Dfile=asm-commons-3.1-osgi.jar -DgroupId=asm -DartifactId=asm-commons -Dversion=3.1-osgi -Dpackaging=jar
mvn install:install-file -Dfile=asm-3.1-osgi.jar -DgroupId=asm -DartifactId=asm -Dversion=3.1-osgi -Dpackaging=jar

# mule-common
mvn install:install-file -Dfile=antlr-runtime-3.5-osgi.jar -DgroupId=org.antlr -DartifactId=antlr-runtime -Dversion=3.5-osgi -Dpackaging=jar

# mule-module-spring-config
mvn install:install-file -Dfile=dom4j-1.6.1-osgi.jar -DgroupId=dom4j -DartifactId=dom4j -Dversion=1.6.1-osgi -Dpackaging=jar

# tests
mvn install:install-file -Dfile=objenesis-1.4.jar -DgroupId=org.objenesis -DartifactId=objenesis -Dversion=1.4 -Dpackaging=jar


