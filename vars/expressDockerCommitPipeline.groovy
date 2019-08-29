import com.bricerising.stages.Stage
import com.bricerising.stages.CheckoutStage
import com.bricerising.tools.Tool
import com.bricerising.tools.build.NpmBuildTool
import com.bricerising.tools.build.DockerBuildTool
import com.bricerising.tools.auth.DockerhubAuthTool
import com.bricerising.tools.publish.DockerPublishTool

def call(String appName, String version, String registryUrl = '') {
  CheckoutStage checkoutStage = new CheckoutStage(scm)
  String podLabel = "express-slave-${UUID.randomUUID().toString()}"
  String podYaml = """
apiVersion: v1
kind: Pod
metadata:
  name: ${podLabel}
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
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
    resources:
      limits:
        memory: 128M
        cpu: .5
    securityContext:
      fsGroup: 10000
      runAsUser: 10000
  - name: node
    image: bricerisingslalom/node:8-alpine
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
  pipeline {
    agent {
      kubernetes {
        label podLabel
        defaultContainer 'jnlp'
        yaml podYaml
        inheritFrom 'default'
      }
    }
    stages {
      stage('Checkout') {
        steps {
          container('jnlp') {
            script {
              checkoutStage.execute(steps)
            }
          }
        }
      }
      stage('Unit Test') {
        steps {
          container('node') {
            script {
              Stage unitTest = new Stage()
              unitTest.add(new NpmBuildTool())
              unitTest.execute(steps)
            }
          }
        }
      }
      stage('Build') {
        steps {
          container('docker') {
            script {
              Stage buildStage = new Stage()
              buildStage.add(new DockerBuildTool("docker.io/bricerisingslalom/${appName}", version, '-f docker/Dockerfile .'))
              buildStage.execute(steps)

            }
          }
        }
      }
      stage('Publish') {
        steps {
          container('docker') {
            script {
              Stage publishStage = new Stage()
              publishStage.add(new DockerPublishTool("docker.io/bricerisingslalom/${appName}", version))
              publishStage.execute(steps)
            }
          }
        }
      }
    }
  }
}
