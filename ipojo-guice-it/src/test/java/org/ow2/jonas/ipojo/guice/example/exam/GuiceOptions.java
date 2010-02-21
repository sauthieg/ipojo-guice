package org.ow2.jonas.ipojo.guice.example.exam;

import org.ops4j.pax.exam.options.MavenArtifactProvisionOption;

public class GuiceOptions {

    public static MavenArtifactProvisionOption ipojoBundle() {
        MavenArtifactProvisionOption option = new MavenArtifactProvisionOption();
        option.groupId("org.apache.felix")
              .artifactId("org.apache.felix.ipojo")
              .version("1.4.0");
        return option;
    }

    public static MavenArtifactProvisionOption guiceBundle() {
        MavenArtifactProvisionOption option = new MavenArtifactProvisionOption();
        option.groupId("com.google.inject")
              .artifactId("guice")
              .version("2.0");
        return option;
    }

    public static MavenArtifactProvisionOption aopAllianceBundle() {
        MavenArtifactProvisionOption option = new MavenArtifactProvisionOption();
        option.groupId("org.apache.servicemix.bundles")
              .artifactId("org.apache.servicemix.bundles.aopalliance")
              .version("1.0_3");
        return option;
    }
}
