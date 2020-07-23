APPLICATION_BASEDIR = .
DOCKER_BASEDIR = $(APPLICATION_BASEDIR)/src/main/docker
DOCKER_IMAGE_VERSION = 1.0
E2E_BASEDIR = $(APPLICATION_BASEDIR)/src/e2e
BASE_URL = 192.168.99.100:8080

TAURUS_COMMAND = bzt \
	-o settings.artifacts-dir=$(E2E_BASEDIR)/logs \
	-o settings.env.BASE_URL=$(BASE_URL) \
	$(E2E_BASEDIR)/hello-test.yml

.PHONY: e2e gradle

build: docker compose

gradle:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./gradlew build"

gradle-clean:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./gradlew clean"

dev:
	bash -c	"cd $(APPLICATION_BASEDIR) && ./gradlew quarkusDev"

docker: gradle
	docker build -f $(DOCKER_BASEDIR)/Dockerfile.jvm -t quarkus/resilience4j-sandbox-jvm:$(DOCKER_IMAGE_VERSION) $(APPLICATION_BASEDIR)

compose:
	docker-compose -f $(DOCKER_BASEDIR)/resilience4j-sandbox.yml up -d --force --build; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml up -d --force

clean: gradle-clean
	docker-compose -f $(DOCKER_BASEDIR)/resilience4j-sandbox.yml down --remove-orphans; \
	docker-compose -f $(DOCKER_BASEDIR)/monitoring.yml down --remove-orphans; \
	rm -rf e2e/logs

e2e:
	$(TAURUS_COMMAND)

restart: clean default
