all: downloadDependencies start

downloadDependencies:
	cd ServerParts && das dc

build:
	cd Features && das make

run: clean
	cd ServerParts && java -jar server.jar

runDebug: clean
	cd ServerParts && java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar server.jar

copyFeatures:
	mkdir -p ServerParts/features && cp Features/project-distribution/*.zip ServerParts/features

clean:
	rm -rf ServerParts/corefeatures/*/
	rm -rf ServerParts/features/*/

start: build copyFeatures run

startDebug: build copyFeatures runDebug
