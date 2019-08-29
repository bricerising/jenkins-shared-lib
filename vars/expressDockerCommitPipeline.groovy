import com.bricerising.stages.Stage
import com.bricerising.stages.CheckoutStage
import com.bricerising.tools.Tool
import com.bricerising.tools.build.NpmBuildTool
import com.bricerising.tools.build.DockerBuildTool
import com.bricerising.tools.auth.DockerhubAuthTool
import com.bricerising.tools.publish.DockerPublishTool

CheckoutStage checkoutStage = new CheckoutStage(scm)

def call(String appName, String version, String registryUrl = '') {
  podTemplate(label: "express-slave-${UUID.randomUUID().toString()}",
    containers: [
      containerTemplate(
        name: 'docker',
        image: 'docker:18.05.0-ce',
        ttyEnabled: true,
        command: 'cat'
      ),
      containerTemplate(
        name: 'node',
        image: 'node:8-alpine',
        ttyEnabled: true,
        command: 'cat'
      )
      containerTemplate(
        name: 'helm',
        image: 'alpine/helm:2.14.1',
        ttyEnabled: true,
        command: 'cat'
      )
    ],
    volumes: [
      hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')
    ]
  ) {
    node('express-slave') {
      container('jnlp') {
        stage('Checkout') {
          checkoutStage.execute(steps)
        }
      }
      container('node') {
        stage('Build') {
          Stage buildStage = new Stage()
          buildStage.add(new NpmBuildTool())
          buildStage.add(new DockerBuildTool(appName, version, '-f docker/Dockerfile .'))
          buildStage.execute(steps)
        }
      }
      container('docker') {
        stage('Publish') {
          Stage publishStage = new Stage()
          publishStage.add(new DockerhubAuthTool(registryUrl))
          publishStage.add(new DockerPublishTool(appName, version))
          publishStage.execute(steps)
        }
      }
    }
  }
}
