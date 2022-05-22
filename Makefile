VERSION := $(shell git describe --tags --always --abbrev=8)

build:
	.\gradlew server:build

clean: 
	.\gradlew server:clean

run:
	.\gradlew server:run

docker: build
	docker build . -t chessserver:$(VERSION)

docker-release: build
	docker buildx build . -f build.Dockerfile \
		--platform linux/arm64 \
		--tag ghcr.io/chemecador/chessserver:$(VERSION) \
		--tag ghcr.io/chemecador/chessserver:latest \
		--push
