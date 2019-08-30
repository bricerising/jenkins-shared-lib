FROM alpine:3.7

RUN apk add jq
RUN cd /tmp ; \
    wget https://storage.googleapis.com/kubernetes-helm/helm-v2.14.1-linux-amd64.tar.gz -O /tmp/helm.tar.gz ; \
    tar xf helm.tar.gz ; \
    mv linux-amd64/helm /usr/local/bin/

RUN cd /tmp ; \
    apk update && \
    apk add curl && \
    curl -LO https://storage.googleapis.com/kubernetes-release/release/`curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt`/bin/linux/amd64/kubectl && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/kubectl

# Create aws user
RUN adduser -S -u 10000 jenkins
USER jenkins
