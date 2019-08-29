import com.bricerising.stages.Stage
import com.bricerising.stages.CheckoutStage
import com.bricerising.tools.Tool
import com.bricerising.tools.build.NpmBuildTool
import com.bricerising.tools.build.DockerBuildTool
import com.bricerising.tools.auth.DockerhubAuthTool
import com.bricerising.tools.publish.DockerPublishTool

CheckoutStage checkoutStage = new CheckoutStage(scm)

def call(String appName, String version, String registryUrl = '') {
  pipeline {
    agent {
      kubernetes {
        defaultContainer 'jnlp'
        yaml """
apiVersion: v1
kind: Pod
metadata:
  name: "express-slave-${UUID.randomUUID().toString()}"
  labels:
    type: express-slave
spec:
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
  containers:
  - name: docker
    image: docker:18.05.0-ce
    command:
    - cat
    tty: true
    voumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
    resources:
      limits:
        memory: 128M
        cpu: .5
    securityContext:
      fsGroup: 10000
      runAsUser: 10000
  - name: helm
    image: alpine/helm:2.14.1
    command:
    - cat
    tty: true
    resources:
      limits:
        memory: 512M
        cpu: .5
    securityContext:
      fsGroup: 10000
      runAsUser: 10000
  - name: node
    image: node:8-alpine
    command:
    - cat
    tty: true
    resources:
      limits:
        memory: 512M
        cpu: .5
    securityContext:
      fsGroup: 10000
      runAsUser: 10000
          """
      }
    }
    stages {
      stage('Checkout') {
        container('jnlp') {
          checkoutStage.execute(steps)
        }
      }
      stage('Build') {
        container('node') {
          Stage buildStage = new Stage()
          buildStage.add(new NpmBuildTool())
          buildStage.add(new DockerBuildTool(appName, version, '-f docker/Dockerfile .'))
          buildStage.execute(steps)
        }
      }
      stage('Publish') {
        container('docker') {
          Stage publishStage = new Stage()
          publishStage.add(new DockerhubAuthTool(registryUrl))
          publishStage.add(new DockerPublishTool(appName, version))
          publishStage.execute(steps)
        }
      }
    }
  }
}
