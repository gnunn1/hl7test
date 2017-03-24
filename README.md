# FIS 2.0 HL7/MLLP External Access on Openshift

This is an example of exposing HL7/MLLP from Openshift to external consumers using FIS 2.0. In Openshift, routers do not support MLLP traffic since it is TCP based rather then HTTP. It would be possible to tunnel MLLP using TLS pass-through but it's pretty rare to see healthcare systems using MLLP with TLS.

This project contains of a very simple camel route that accepts HL7 traffic and logs it. Two service files are provided:

* ```hl7test-external-nodeport.yml```. This exposes the service using a nodeport and can be used with Minishift to expose MLLP pods to the outside world.
* ```hl7test-external-loadbalancer.yml```. This creates a loadbalancer service when running in cloud environments, I've only tested it with AWS.

To create the desired service, just use the ```oc create -f hl7test-external-xxxx.yml``` command, obviously deploy the example first though.

### Building

The example can be built with

    mvn clean install

### Running the example in OpenShift

It is assumed that:
- OpenShift platform is already running, if not you can find details how to [Install OpenShift at your site](https://docs.openshift.com/container-platform/3.3/install_config/index.html).
- Your system is configured for Fabric8 Maven Workflow, if not you can find a [Get Started Guide](https://access.redhat.com/documentation/en/red-hat-jboss-middleware-for-openshift/3/single/red-hat-jboss-fuse-integration-services-20-for-openshift/)

The example can be built and run on OpenShift using a single goal:

    mvn fabric8:deploy

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the OpenShift [web console](https://docs.openshift.com/container-platform/3.3/getting_started/developers_console.html#developers-console-video) to manage the running pods, and view logs and much more.

### Testing the example

To test the example, you can use the HapiTestPanel which is available here http://hl7api.sourceforge.net/hapi-testpanel/.

For the Nodeport example, it exposes a port on 31450. You can use the ```minishift openshift svc list``` command to see the IP address to use to access the port.

On AWS, an ELB will be created automatically. I mapped that ELB to a DNS record in Route 53 and then used that to access the service on port 8888.

### Troubleshooting AWS

If you are using the reference architecture in AWS, you will run into a problem where creating the loadbalancer service doesn't work. If you do an ```oc describe svc hl7test-external``` you will see the following error:
```
Error creating load balancer (will retry): Failed to create load balancer for service hl7test/hl7test-external: Multiple tagged security groups found for instance i-04cf63acf18d31850; ensure only the k8s security group is tagged
```

The Openshift AWS reference architecture creates two security groups for the Openshift Infra nodes and both groups are tagged with the ```KubernetesCluster``` attribute. There is a Kubernetes open issue talking about the problem here (https://github.com/kubernetes/kubernetes/issues/26787).

You can workaround it by removing the KubernetesCluster tag from the ```ose_infra_node_sg``` security group.
