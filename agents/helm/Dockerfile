FROM alpine:3.7

RUN apk add jq
RUN cd /tmp ; \
    wget https://storage.googleapis.com/kubernetes-helm/helm-v2.9.1-linux-amd64.tar.gz -O /tmp/helm.tar.gz ; \
    tar xf helm.tar.gz ; \
    mv linux-amd64/helm /usr/local/bin/

# Create aws user
RUN adduser -S -u 10000 jenkins
USER jenkins
