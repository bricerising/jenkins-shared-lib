import com.bricerising.stages.Stage
import com.bricerising.stages.CheckoutStage
import com.bricerising.tools.Tool
import com.bricerising.tools.build.NpmBuildTool
import com.bricerising.tools.build.DockerBuildTool
import com.bricerising.tools.auth.DockerhubAuthTool
import com.bricerising.tools.publish.DockerPublishTool
import com.bricerising.tools.deploy.HelmDeployTool

def call(String appName, boolean mongo = false, String registryUrl = '') {
  CheckoutStage checkoutStage = new CheckoutStage(scm)
  Tool npmBuildTool = new NpmBuildTool()
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
    image: bricerisingslalom/docker:18.05.0-ce
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
  - name: helm
    image: bricerisingslalom/helm:v2.9.1
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
              unitTest.add(npmBuildTool)
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
              buildStage.add(new DockerBuildTool("docker.io/bricerisingslalom/${appName}", npmBuildTool.getPackageVersion(), '-f docker/Dockerfile .'))
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
              publishStage.add(new DockerhubAuthTool(registryUrl))
              publishStage.add(new DockerPublishTool("docker.io/bricerisingslalom/${appName}", npmBuildTool.getPackageVersion()))
              withCredentials([
                  usernamePassword(credentialsId: 'DOCKERHUB_CREDENTIALS', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USER')
              ]) {
                publishStage.execute(steps)
              }
            }
          }
        }
      }
      stage('Deploy') {
        steps {
          container('helm') {
            script {
              TreeMap scmVars = checkoutStage.getScmVars()
              String tillerNamespace = "${appName}-${scmVars.GIT_BRANCH}".toLowerCase().replaceAll('/','-')
              Stage deployStage = new Stage()
              if(mongo) {
                deployStage.add(new HelmDeployTool(
                  "mongo",
                  tillerNamespace,
                  "--set usePassword=false stable/mongodb"
                ))
              }
              deployStage.add(new HelmDeployTool(
                tillerNamespace,
                tillerNamespace,
                "--set catalog-service.deployment.tag=${npmBuildTool.getPackageVersion()} ./chart"
              ))
              deployStage.execute(steps)
            }
          }
        }
      }
    }
  }
}
