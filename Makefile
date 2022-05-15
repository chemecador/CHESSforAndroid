VERSION := $(shell git describe --tags --always --abbrev=0)

build:
	.\gradlew server:build

clean: 
	.\gradlew server:clean

docker:
	docker build .

run:
	.\gradlew server:run

