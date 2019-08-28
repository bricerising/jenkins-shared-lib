package com.bricerising.tools.deploy

import com.tools.Tool

public class HelmDeployTool implements Tool {
    
    private String namespace
    private String name

    HelmDeployTool(String name, String namespace) {
        this.namespace = namespace
        this.name = name
    }

    public void execute(steps, String opts) {
        steps.sh """
            set +e

            kubectl create namespace ${this.namespace}
            cat <<EoF | kubectl apply -f -
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: ${this.namespace}
---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: tiller
  namespace: ${this.namespace}
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: ${this.namespace}
EoF
        """
        steps.sh """
            helm init --upgrade \
                --service-account tiller \
                --tiller-namespace $(this.namespace}
        """
        steps.sh """
            helm upgrade --install \
                --tiller-namespace ${this.namespace} \
                --namespace ${namespace} \
                ${opts} \
                ${this.name}
        """
    }

}
