VERSION:=local

build b:
	docker build . -t docker.io/brianwolf/java-springboot:$(VERSION)

push p:
	docker push docker.io/brianwolf/java-springboot:$(VERSION)

run r:
	docker run -it --rm -p 8080:8080 docker.io/brianwolf/java-springboot:$(VERSION)